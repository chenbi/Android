///*
// * nrobot.java
// *
// * by Jeff Schmitt
// * Towson University
// * October, 1997
// *
// * Using java and jogl-0.7
// * Techniques of Perspective viewing, lighting, material properties
// * Quadric spheres and cylinders
// * 
// * Based on a Robot program (Chapter 8).
// * from Interactive Computer Graphics by Ed Angel
// *
// */
//
//
//import jogl.*;
//import jogl.glu.*;
//import java.awt.*;
//import java.awt.event.*;
//
//public class nrobot extends Frame implements MouseListener {
//
//   JoglCanvas gl;
//
//   final float DELTA  =  0.5f;       // angle change each second
//
//   // Rules of the robot world
//   // 1.  The robot arm can only move by changing the
//   //     three independent angles (theta[])
//   // 2.  Three stepper motors are controlling the angles
//   // 3.  The motors are very strong and very precise but
//   //     they have a constant angular velocity of xx rpm
//   // 4.  each angle cannot change by more than (plus or minus) DELTA
//   //     in each time unit (one second)
//   // 5.  More than one angle can change each time unit.
//   // 6.  The base can rotate either way in an unlimited range
//   // 7.  There are (plus and minus) 120-degree hard limits on the
//   //     motion of each arm measured from its vertical position.
//   // 8.  At no time should any part of the arm assembly
//   //     go below the ground level (y=0 plane).  This is not enforced.
//   
//   // Let's start using constants so we can better
//   //interpret the constants (and change them)
//
//   final float LEFT_MOUSE       = 0;
//   final float MIDDLE_MOUSE     = 8;
//   final float RIGHT_MOUSE      = 4;
//
//   final float BASE_HEIGHT      = 2.0f;
//   final float BASE_RADIUS      = 1.0f;
//   final float LOWER_ARM_HEIGHT = 5.0f;
//   final float ARM_RADIUS  = 0.5f;
//   final float UPPER_ARM_HEIGHT = 5.0f;
//
//
//
//   // start of material definitions
//   float mat_specular[] = { 0.628281f, 0.555802f, 0.366065f, 1.0f };
//   float mat_ambient[] = { 0.24725f, 0.1995f, 0.0745f, 1.0f };
//   float mat_diffuse[] = { 0.75164f, 0.60648f, 0.22648f, 1.0f };
//   float mat_shininess[] = { 128.0f * 0.4f };
//
//   float mat_specular2[] = { 0.508273f, 0.508273f, 0.508373f };
//   float mat_ambient2[] = { 0.19225f, 0.19225f, 0.19225f };
//   float mat_diffuse2[] = { 0.20754f, 0.70754f, 0.30754f };
//   float mat_shininess2[] = { 128.0f * 0.6f };
//									
//
//   static float theta[] = {0.0f,0.0f,0.0f};
//   static int axis = 0;
//   float direction=DELTA;      // will change sign for direction of motion
//
//   Quadric  p;        //GLU quadric object 
//
//   // Define the parts of the robot
//   // Note use of push/pop to return modelview matrix
//   // to its state before functions were entered and use
//   // rotation, translation, and scaling to create instances
//   // of symbols (sphere and cylinder)
//
//   public void base() {
//      gl.pushMatrix();
//
//      //rotate cylinder to align with y axis
//      gl.rotate(-90.0, 1.0, 0.0, 0.0);
//
//      // cyliner aligned with z axis, render with
//      // 5 slices for base and 15 along length
//      SetMaterial( mat_specular2, mat_ambient2, mat_diffuse2,
//          mat_shininess2 );
//      p.cylinder(BASE_RADIUS, BASE_RADIUS, BASE_HEIGHT, 10, 5);
//      gl.translate(0.0f, BASE_HEIGHT, 0.0f);
//      // the end disk of the cylinder (not yet working)
//      p.disk(BASE_RADIUS, BASE_RADIUS, 10, 5);
//      gl.popMatrix();
//   }
//
//   public void ball(float radius) {
//      gl.pushMatrix();
//      SetMaterial( mat_specular, mat_ambient, mat_diffuse,
//          mat_shininess );
//      p.sphere(ARM_RADIUS, 10, 10);
//      gl.popMatrix();
//   }
//
//   public void arm(float height) {
//      gl.pushMatrix();
//      //rotate cylinder to align with y axis
//      gl.rotate(-90.0, 1.0, 0.0, 0.0);
//      SetMaterial( mat_specular, mat_ambient, mat_diffuse,
//          mat_shininess );
//      p.cylinder(ARM_RADIUS, ARM_RADIUS,
//          height, 10, 5);
//      gl.popMatrix();
//   }
//
//
//   public void mouseClicked(MouseEvent evt) {
//   }
//   public void mouseEntered(MouseEvent evt) {
//   }
//   public void mouseExited(MouseEvent evt) {
//   }
//   public void mousePressed(MouseEvent evt) {
//      //System.out.println( "MousePressed: "+
//      //    evt.getX()+":"+evt.getY()+"|" +
//      //    evt.getModifiers() +"|" + MouseEvent.BUTTON3_MASK );
//      if (evt.getModifiers() == LEFT_MOUSE) {
//        direction=DELTA;
//      } else if (evt.getModifiers() == RIGHT_MOUSE) {
//        direction=-DELTA;
//      } else if (evt.getModifiers() == MIDDLE_MOUSE) {
//        direction=0;
//      }
//   }
//   public void mouseReleased(MouseEvent evt) {
//   }
//   // left button increase joint angle, right button decreases it
//
//   public void xsetSize( int width, int height ) {
//      gl.viewport( 0, 0, width, height );
//      gl.matrixMode( GL.PROJECTION );
//      gl.loadIdentity();
//
//      // to prevent distortion use the smaller scale factor
//      if (width<height) {
//         gl.perspective( 65.0, (double) width / height, 1.0, 100.0 );
//      } else {
//         gl.perspective( 65.0, (double) height / width, 1.0, 100.0 );
//      }      
//      gl.matrixMode( GL.MODELVIEW );
//      gl.loadIdentity();
//      gl.lookAt(0.0, 15.0, -10.0,  // eye positiom
//                0.0,  5.0,   0.0,  // center position
//		0.0,  1.0,   0.0);  // up vector
//   }
//
//   public nrobot() {
//      super( "Robot -- Perspective View" );
//      /* try to instantiate an OpenGL widget */
//      gl = new JoglCanvas();
//      MenuBar mb = new MenuBar();
//      Menu m = new Menu( "Menu" );
//      setMenuBar( mb );
//      m.add( new MenuItem( "Base" ));
//      m.add( new MenuItem( "Lower arm" ));
//      m.add( new MenuItem( "Upper arm" ));
//      m.add( new MenuItem( "Exit" ));
//      mb.add(m );
//      m.addActionListener(new ActionListener() {
//         public void actionPerformed(ActionEvent ev) {
//	    String label = ev.getActionCommand();
//	    if (label.equals("Base")) {
//	       axis=0;
//	    } else if (label.equals("Lower arm")) {
//	       axis=1;
//	    } else if (label.equals("Upper arm")) {
//	       axis=2;
//	    } else if (label.equals("Exit")) {
//	       System.exit(0);
//            }
//         }
//      });
//
//      // set up the Frame
//      gl.setSize( 400, 400 );
//      add( "Center", gl );
//      setSize( 400, 400 );
//      setVisible( true );
//      System.out.println( "show" );
//      gl.addMouseListener( this );
//      gl.addKeyListener( new KeyAdapter() {
//	   public void keyPressed( KeyEvent e ) {
//              System.out.println( e );
//           }
//      });
//      try {
//        Thread.sleep( 1000 );
//      } catch( Exception e ){
//      }
//
//      gl.use();
//   }
//
//   void SetMaterial( float spec[], float amb[], float diff[], float shin[] ) {
//      gl.material( GL.FRONT, GL.SPECULAR, spec );
//      gl.material( GL.FRONT, GL.SHININESS, shin );
//      gl.material( GL.FRONT, GL.AMBIENT, amb );
//      gl.material( GL.FRONT, GL.DIFFUSE, diff );
//   }
//
//   public void initRobot() {
//      gl.enable( GL.DEPTH_TEST );
//     
//      p=new Quadric(); // allocate quadric object
//      p.quadricDrawStyle(GLU.FILL); // render it as solid
//      p.quadricNormals(GLU.SMOOTH);
//      lighting();
//
//   }
//
//   public void advance() {
//      if (axis==0) {
//         theta[axis]+=direction;      
//      } else if (Math.abs(theta[axis]+direction) <=120) {
//         theta[axis]+=direction;
//      }
//   }
//
//   public void draw() {
//
//      gl.clearColor( 0.0f, 0.0f, 0.0f, 0.0f );
//      gl.clear( GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT );
//      gl.enable( GL.DEPTH_TEST );
//
//      // the viewing matrix is already on the stack
//      gl.pushMatrix();
//
//      // Accumulate ModelView Matrix as we traverse tree */
//      gl.color(1.0f, 0.0f, 0.0f);
//      gl.rotate(theta[0], 0.0f, 1.0f, 0.0f);
//      base();
//      gl.translate(0.0f, BASE_HEIGHT + ARM_RADIUS, 0.0f);
//      ball(ARM_RADIUS);
//      gl.translate(0.0f, ARM_RADIUS, 0.0f);
//      gl.rotate(theta[1], 0.0f, 0.0f, 1.0f);
//      arm(LOWER_ARM_HEIGHT);
//      gl.translate(0.0f, UPPER_ARM_HEIGHT + ARM_RADIUS, 0.0f);
//      ball(ARM_RADIUS);
//      gl.translate(0.0f, ARM_RADIUS, 0.0f);
//      gl.rotate(theta[2], 0.0f, 0.0f, 1.0f);
//      arm(UPPER_ARM_HEIGHT);
//      gl.popMatrix();
//      gl.flush();
//      gl.swap();
//   }
//
//   void lighting() {
//      float position[] = { 0.0f, 100.0f, 0.0f};
//      gl.enable( GL.LIGHTING );
//      gl.enable( GL.LIGHT0 );
//      gl.enable(GL.NORMALIZE );
//      gl.enable(GL.FLAT);
//      gl.depthFunc( GL.LESS );
//      gl.polygonMode( GL.FRONT_AND_BACK, GL.FILL );
//
//      gl.light( GL.LIGHT0, GL.POSITION, position );
//      gl.light( GL.LIGHT0, GL.SPOT_CUTOFF, 80.0f );
//      gl.enable( GL.LIGHTING );
//   }		 
//
//   public void doRobot() {
//      initRobot();
//      xsetSize( getSize().width, getSize().height );
//      while( true ) {
//         gl.use();
//         draw();
//         advance();
//      }
//   }
//
//   public static void main(String args[]) {
//      nrobot r = new nrobot();
//      r. doRobot();
//   }
//}