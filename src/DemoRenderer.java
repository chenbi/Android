

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.swing.JPopupMenu;
import static javax.media.opengl.GL.*;

public class DemoRenderer extends AbstractSimpleRenderer {
    private GLU glu = new GLU();  // global glu object
    final private boolean THREE_SPACE = false;
    private float panelWidth,  panelHeight;
    private float a = THREE_SPACE ? 5 : 50;
    private float b = THREE_SPACE ? 5 : 50;

    public DemoRenderer(GLAutoDrawable drawable) {
        super(drawable);
    }

    public void init(final GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(0, 0, 0, 0);  // Black background
        gl.glEnable(GL_DEPTH_TEST);   // Enables Z-buffer (correct drawing order)
        gl.glEnable(GL_CULL_FACE);    // Draw only front of objects (ignore backside)
        gl.glShadeModel(GL_SMOOTH);   // Enable smooth shading
    }

    /** Called by the drawable to initiate OpenGL rendering by the client.
     * After all GLEventListeners have been notified of a display event, the
     * drawable will swap its buffers if necessary.
     * @param drawable The GLAutoDrawable object.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        //clear image- and depth buffer to get a clean canvas
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        //set up viewing transform (camera)
        //TODO: REPLACE CODE HERE
        if (THREE_SPACE) {
            // set camera
            // equivalent to: glTranslatef(0, 0, -10.0f);
            glu.gluLookAt(0, 0, 10.0,
                    0, 0, 0,
                    0, 1, 0);

            gl.glRotatef(45, 0, 1, 0);
        } else {
            gl.glTranslated(panelWidth / 2, panelHeight / 2, 0.0);
        }

        // TODO: INSERT GEOMETRIC TRANSFORMATION CODE HERE
        // none yet


        //Render object(s)
        // TODO: REPLACE CODE HERE
        if (THREE_SPACE) {
            gl.glBegin(GL_QUADS);
            gl.glColor3f(1, 1, 0); // yellow square
            gl.glVertex3f(-a, -b, -20.0f);
            gl.glVertex3f(a, -b, -20.0f);
            gl.glVertex3f(a, b, -20.0f);
            gl.glVertex3f(-a, b, -20.0f);
            gl.glEnd();
        } else {
            gl.glBegin(GL_QUADS);
            gl.glColor3f(1, 0, 0); // red square
            gl.glVertex2f(-a, -b);
            gl.glVertex2f(a, -b);
            gl.glVertex2f(a, b);
            gl.glVertex2f(-a, b);
            gl.glEnd();

            //draw axes
            gl.glColor3f(1, 1, 1);
            gl.glBegin(GL_LINES);
            gl.glVertex2f(-panelWidth / 2, 0);
            gl.glVertex2f(panelWidth / 2, 0);
            gl.glVertex2f(0, -panelHeight / 2);
            gl.glVertex2f(0, panelHeight / 2);
            gl.glEnd();
        }
    }

    /**
     * Called by the drawable during the first repaint after the component has
     * been resized. The client can update the viewport and view volume of the
     * window appropriately, for example by a call to
     * GL.glViewport(int, int, int, int); note that for convenience the component
     * has already called GL.glViewport(int, int, int, int)(x, y, width, height)
     * when this method is called, so the client may not have to do anything in
     * this method.
     * @param drawable The GLAutoDrawable object.
     * @param x The X Coordinate of the viewport rectangle.
     * @param y The Y coordinate of the viewport rectanble.
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        panelWidth = width;
        panelHeight = height;

        // Set projection matrix
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        if (THREE_SPACE) {
            // Set up a perspective camera at point (0,0,0), looking in negative Z-direction
            // Angle of view is 90 degrees. Near and far clipping planes are 10 and 1000
            glu.gluPerspective(90.0, 1.0, 10, 1000);
        } else {
            // Set up a orthogonal projection.
            gl.glOrtho(0, width, 0, height, 0, 1);
        }
        // set up geometric transformations
        gl.glMatrixMode(GL_MODELVIEW);
        // Set up viewport
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void keyboard(char key) {
        switch (key) {
            //TODO: REPLACE CODE HERE
            case 'a':
                //increase value of a
                a++;
                if (a > panelWidth / 4) {
                    a = panelWidth / 4;
                }
                break;
            case 'A':
                //decrease value of a
                a--;
                if (a < -panelWidth / 4) {
                    a = -panelWidth / 4;
                }
                break;
            case 'b':
                //increase value of b
                b++;
                if (b > panelWidth / 4) {
                    b = panelWidth / 4;
                }
                break;
            case 'B':
                //decrease value of b
                b--;
                if (b < -panelWidth / 4) {
                    b = -panelWidth / 4;
                }
                break;
            case 'q':
                System.exit(0); //Quit the program
                break;
        }
    }

    /** define and return a popup menu */
    public JPopupMenu getPopupMenu() {
        final JPopupMenu menu = new JPopupMenu("options");
        addMenuItem(menu, "Increase a", 'a');
        addMenuItem(menu, "Decrease A", 'A');
        addMenuItem(menu, "Increase b", 'b');
        addMenuItem(menu, "Decrease B", 'B');
        addMenuItem(menu, "Quit", 'q');
        return menu;
    }
    
    /** define and return an object for handling mouse events */
    @Override
    public MouseAdapter getMouseAdapter() {

        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  System.err.print("Clicked "); print(e); }

            @Override
            public void mouseDragged(MouseEvent e) {  System.err.print("Dragged "); print(e); }

            @Override
            public void mousePressed(MouseEvent e) {  System.err.print("Pressed "); print(e); }

            @Override
            public void mouseReleased(MouseEvent e) { System.err.print("Released "); print(e); }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    while (rotation++ < 0) {
                        keyboard('a');
                    }
                } else if (rotation > 0) {
                    while (rotation-- > 0) {
                        keyboard('A');
                    }
                }
            }

            private void print(MouseEvent e) {
                int x = e.getX(), y = e.getY(), button = e.getButton();
                System.err.println("button " + button + " at (" + x + "," + y + ")");
            }
        };
    }
}