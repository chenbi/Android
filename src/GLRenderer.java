import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;


/**
*
 */
public class GLRenderer implements GLEventListener 
{
    
    // the man
    oneMan theMan=null;
    // mousing
    float m_rotZ=0.0f;
    float m_rotX=0.0f;
    boolean m_isTalking=false;
    boolean m_isWalking=false;
    
    GLAutoDrawable this_drawable=null;
    
    public void init(GLAutoDrawable drawable) 
    {
        this_drawable=drawable;

        GL gl = drawable.getGL();

        // Enable VSync
        gl.setSwapInterval(1); 

        // set up lighting
        float ambient[] = {1.0f,1.0f,1.0f,1.0f };
        float diffuse[] = {1.0f,1.0f,1.0f,1.0f };
        float position[] = {1.0f,1.0f,1.0f,0.0f };

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient,0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse,0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position,0);

        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHTING);

        gl.glEnable(GL.GL_DEPTH_TEST);

        // Setup the drawing area and shading mode
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glShadeModel(GL.GL_SMOOTH);

        // set up the man
        theMan=new oneMan();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, 
                                 int width, int height) 
    {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) {  height = 1;}
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

    }

    public void display(GLAutoDrawable drawable) 
    {
        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        // Move the whole scene
        gl.glTranslatef(-0.0f, 0.0f, -16.0f);
        gl.glRotatef(-90.0f,1.0f,0.0f,0.0f);
        gl.glRotatef(180.0f,0.0f,0.0f,1.0f);
        
        gl.glRotatef(m_rotZ,0.0f,0.0f,1.0f);
        gl.glRotatef(m_rotX,1.0f,0.0f,0.0f);
        
        theMan.drawMe(gl);
        if(m_isWalking || m_isTalking)
            theMan.updateAngles(m_isWalking,m_isTalking);

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable,
            boolean modeChanged, boolean deviceChanged) {
    }

    public void setBasicRotation(float z,float x)
    {       
        m_rotZ=z;
        m_rotX=x;
    }

    public void  doWalk(boolean on)
    {
        m_isWalking=on;
    }
    public void  doTalk(boolean on)
    {
        m_isTalking=on;
    }
}
