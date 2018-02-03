import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class loopSubdiv extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener {

	/* 'input_verts' and 'input_faces' store the vertex and face data of the input mesh respectively
	 * each element in 'input_verts' is a 3D points defining the vertex
	 * every three integers in 'input_faces' define the indexes of the three vertices that make a triangle
	 * there are in total input_faces.size()/3 triangles
	 */
	private static ArrayList<Point3f> input_verts = new ArrayList<Point3f> ();
	private static ArrayList<Integer> input_faces = new ArrayList<Integer> ();
	
	/* 'curr_verts' and 'curr_faces' store the subdivided mesh that will be displayed on the screen
	 * the elements stored in these arrays are the same with the input mesh
	 * 'curr_normals' stores the normal of each face and is necessary for shading calculation
	 * you don't have to compute the normals yourself: once you have curr_verts and curr_faces
	 * ready, you just need to call estimateFaceNormal to generate normal data
	 */
	private static ArrayList<Point3f> curr_verts = new ArrayList<Point3f> ();
	private static ArrayList<Integer> curr_faces = new ArrayList<Integer> ();
	private static ArrayList<Vector3f> curr_normals = new ArrayList<Vector3f> ();
	
	/* GL related variables */
	private final GLCanvas canvas;
	private static GL gl;
	private final GLU glu = new GLU();
	// initial window size
	private static int winW = 800, winH = 800;
	// toggle parameters
	private static boolean reset_mesh = false;
	private static boolean wireframe = false;
	private static boolean cullface = true;
	
	// transformation and mouse control parameters
	private static float xpos = 0, ypos = 0, zpos = 0;
	private static float xmin, ymin, zmin;
	private static float xmax, ymax, zmax;
	private static float centerx, centery, centerz;
	private static float roth = 0, rotv = 0;
	private static float znear, zfar;
	private static int mouseX, mouseY, mouseButton;
	private static float motionSpeed, rotateSpeed;

	/* load a simple .obj mesh from disk file
	 * note that each face *must* be a triangle and cannot be a quad or other types of polygon
	 */ 
	private static void loadMesh(String filename) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (IOException e) {
			System.out.println("Error reading from file " + filename);
			System.exit(0);
		}

		float x, y, z;
		int v1, v2, v3;
		String line;
		String[] tokens;
		try {
		while ((line = in.readLine()) != null) {
			if (line.length() == 0)
				continue;
			switch(line.charAt(0)) {
			case 'v':
				tokens = line.split("[ ]+");
				x = Float.valueOf(tokens[1]);
				y = Float.valueOf(tokens[2]);
				z = Float.valueOf(tokens[3]);
				input_verts.add(new Point3f(x, y, z));
				break;
			case 'f':
				tokens = line.split("[ ]+");
				/* when defining faces, .obj assumes the vertex index starts from 1
				 * so we should subtract 1 from each index 
				 */
				v1 = Integer.valueOf(tokens[1])-1;
				v2 = Integer.valueOf(tokens[2])-1;
				v3 = Integer.valueOf(tokens[3])-1;
				input_faces.add(v1);
				input_faces.add(v2);
				input_faces.add(v3);				
				break;
			default:
				continue;
			}
		}
		in.close();	
		} catch(IOException e) {
			// error reading file
		}

		System.out.println("Read " + input_verts.size() +
					   	" vertices and " + input_faces.size() + " faces.");

	}
	
	/* find the bounding box of all vertices */
	private static void computeBoundingBox() {
		xmax = xmin = input_verts.get(0).x;
		ymax = ymin = input_verts.get(0).y;
		zmax = zmin = input_verts.get(0).z;
		
		for (int i = 1; i < input_verts.size(); i ++) {
			xmax = Math.max(xmax, input_verts.get(i).x);
			xmin = Math.min(xmin, input_verts.get(i).x);
			ymax = Math.max(ymax, input_verts.get(i).y);
			ymin = Math.min(ymin, input_verts.get(i).y);
			zmax = Math.max(zmax, input_verts.get(i).z);
			zmin = Math.min(zmin, input_verts.get(i).z);			
		}
	}
	
	/* creates OpenGL window */
	public loopSubdiv() {
		super("Assignment 2 -- Loop Subdivision");
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		getContentPane().add(canvas);
		setSize(winW, winH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		canvas.requestFocus();
	}
	
	/* YOUR WORK HERE:
	 * this function computes one step of subdivision according to Loop's subdivision formula
	 * if reset_mesh is true, it restores the current mesh to the input mesh
	 * otherwise, the current mesh stored in 'curr_verts' and 'curr_faces' will be subdivided,
	 * and the new mesh will be stored back to the two variables for display.
	 * if you keep calling computeSubdiv, the mesh will be repeatedly subdivided, and you will
	 * see the result becomes progressively smoother.
	 */
	public static void computeSubdiv() {
		if (reset_mesh == true) {
			curr_verts = input_verts;
			curr_faces = input_faces;
			estimateFaceNormal();
			reset_mesh = false;
			return;
		}
		
		/* at this point, 'curr_verts' and 'curr_faces' should store the new, subdivded mesh */
		/* calls estimateFaceNormal to calculate shading normals */
		estimateFaceNormal();
	}
	
	public static void printUsage() {
		System.out.println("Usage: java loopSubdiv input.obj");
		System.exit(1);
	}

	public static void main(String[] args) {

		String inputFilename = null;
//		if (args.length < 1) {
//			printUsage();
//		}
		inputFilename = "combo.obj";

		loadMesh(inputFilename);
		computeBoundingBox();

		curr_verts = input_verts;
		curr_faces = input_faces;
		estimateFaceNormal();
		
		new loopSubdiv();
	}
	
	/* setup GL display parameters */
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();

		initViewParameters();
		gl.glClearColor(.3f, .3f, .3f, 1f);
		gl.glClearDepth(1.0f);

		float mat_specular[] = {0.9f, 0.9f, 0.9f, 1.0f};
		float mat_diffuse[] = {0.8f, 0.5f, 0.2f, 1.0f};
		float mat_shiny[] = {40.f};
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		gl.glCullFace(GL.GL_BACK);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glShadeModel(GL.GL_SMOOTH);		

		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, mat_specular, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, mat_shiny, 0);
		
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);		
	}
	
	/* mouse and keyboard callback functions */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		winW = width;
		winH = height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(30.f, (float)width/(float)height, znear, zfar);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	public void mousePressed(MouseEvent e) {	
		mouseX = e.getX();
		mouseY = e.getY();
		mouseButton = e.getButton();
		canvas.display();
	}
	
	public void mouseReleased(MouseEvent e) {
		mouseButton = MouseEvent.NOBUTTON;
		canvas.display();
	}	
	
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (mouseButton == MouseEvent.BUTTON3) {
			zpos -= (y - mouseY) * motionSpeed;
			mouseX = x;
			mouseY = y;
			canvas.display();
		} else if (mouseButton == MouseEvent.BUTTON2) {
			xpos -= (x - mouseX) * motionSpeed;
			ypos += (y - mouseY) * motionSpeed;
			mouseX = x;
			mouseY = y;
			canvas.display();
		} else if (mouseButton == MouseEvent.BUTTON1) {
			roth -= (x - mouseX) * rotateSpeed;
			rotv += (y - mouseY) * rotateSpeed;
			mouseX = x;
			mouseY = y;
			canvas.display();
		}
	}

	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break;		
		case 'r':
		case 'R':
			initViewParameters();
			break;
		case 's':
		case 'S':
			computeSubdiv();
			break;
		case 'e':
		case 'E':
			reset_mesh = true;
			computeSubdiv();
			break;
		case 'w':
		case 'W':
			wireframe = ! wireframe;
			break;
		case 'b':
		case 'B':
			cullface = !cullface;
			break;
		default:
			break;
		}
		canvas.display();
	}
	
	/* display the current subdivision mesh */
	public void display(GLAutoDrawable drawable) {
		// setup lighting
		float lightPos[] = {0.f, 0.f, 0.f, 1.0f};
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, wireframe ? GL.GL_LINE : GL.GL_FILL);	
		if (cullface)
			gl.glEnable(GL.GL_CULL_FACE);
		else
			gl.glDisable(GL.GL_CULL_FACE);		
		
		gl.glLoadIdentity();
		
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPos, 0);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		
		gl.glTranslatef(-xpos, -ypos, -zpos);
		gl.glTranslatef(centerx, centery, centerz);
		gl.glRotatef(360.f - roth, 0, 1.0f, 0);
		gl.glRotatef(rotv, 1.0f, 0, 0);
		gl.glTranslatef(-centerx, -centery, -centerz);	
		
		int i;
		gl.glBegin(GL.GL_TRIANGLES);
		for (i = 0; i < curr_faces.size(); i ++) {
			int vid = curr_faces.get(i);
			gl.glNormal3f(curr_normals.get(i).x, curr_normals.get(i).y, curr_normals.get(i).z);
			gl.glVertex3f(curr_verts.get(vid).x, curr_verts.get(vid).y, curr_verts.get(vid).z);
		}
		gl.glEnd();
		
	}
	
	/* computes optimal transformation parameters for OpenGL rendering.
	 * these parameters will place the object at the center of the screen,
	 * and zoom out just enough so that the entire object is visible in the window
	 */
	void initViewParameters()
	{
		roth = rotv = 0;

		float ball_r = (float) Math.sqrt((xmax-xmin)*(xmax-xmin)
							+ (ymax-ymin)*(ymax-ymin)
							+ (zmax-zmin)*(zmax-zmin)) * 0.707f;

		centerx = (xmax+xmin)/2.f;
		centery = (ymax+ymin)/2.f;
		centerz = (zmax+zmin)/2.f;
		xpos = centerx;
		ypos = centery;
		zpos = ball_r/(float) Math.sin(30.f*Math.PI/180.f)+centerz;

		znear = 0.02f;
		zfar  = zpos - centerz + 3.f * ball_r;

		motionSpeed = 0.002f * ball_r;
		rotateSpeed = 0.1f;

	}	
	
	/* estimate face normals */
	private static void estimateFaceNormal() {
		int i;
		curr_normals.clear();
		for (i = 0; i < curr_faces.size(); i ++) {
			curr_normals.add(new Vector3f());
		}
		
		Vector3f e1 = new Vector3f();
		Vector3f e2 = new Vector3f();
		for (i = 0; i < curr_faces.size()/3; i ++) {
			// get face
			int v1 = curr_faces.get(3*i+0);
			int v2 = curr_faces.get(3*i+1);
			int v3 = curr_faces.get(3*i+2);
			
			// compute normal
			e1.sub(curr_verts.get(v2), curr_verts.get(v1));
			e2.sub(curr_verts.get(v3), curr_verts.get(v1));
			curr_normals.get(i*3+0).cross(e1, e2);
			curr_normals.get(i*3+0).normalize();
			curr_normals.get(i*3+1).cross(e1, e2);
			curr_normals.get(i*3+1).normalize();
			curr_normals.get(i*3+2).cross(e1, e2);
			curr_normals.get(i*3+2).normalize();
		}
	}
	
	// these event functions are not used for this assignment
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void actionPerformed(ActionEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) {	}	
}
