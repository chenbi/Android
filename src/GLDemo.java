

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.awt.Dimension;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class GLDemo {
    // try to run animation with 25 frames per second
    static public Animator animator = new FPSAnimator(25);
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // create a drawing panel
                GLJPanel panel = new GLJPanel();
                panel.setFocusable(true);
                panel.requestFocusInWindow();
                panel.setPreferredSize(new Dimension(500, 500));

                // construct a window frame around the drawing panel
                // and makes sure that the program quits if window is closed
                JFrame frame = new JFrame("opengl Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setVisible(true);

                // add a renderer to the panel
                GLEventListener renderer = new DemoRenderer(panel);
                panel.addGLEventListener(renderer);

                // add panel to animator and start calling its display
                // at the specified number of frames per second (see above).
                animator.add(panel);
                animator.setIgnoreExceptions(false);
                animator.setPrintExceptions(true);
                animator.start();
            }
        });
    }
}
