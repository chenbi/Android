/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Installs keyboard event handler, mouse event handler, and popumenu event handler,
 * and allows for simple popup menu creation and handling.
 * @author huub
 */
public abstract class AbstractSimpleRenderer implements GLEventListener {

    public AbstractSimpleRenderer(GLAutoDrawable drawable) {
        MouseAdapter mouseAdapter = getMouseAdapter();
        drawable.addMouseListener(mouseAdapter);
        drawable.addMouseMotionListener(mouseAdapter);
        drawable.addMouseWheelListener(mouseAdapter);
        drawable.addKeyListener(new KeyAdapterImpl());

        // install mouse event handling for popup menu
        final JPopupMenu menu = getPopupMenu();
        if (menu != null) {
            drawable.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
    }

    /**
     * Called by the drawable to initiate OpenGL rendering by the client.
     * After all GLEventListeners have been notified of a display event, the
     * drawable will swap its buffers if necessary.
     * @param gLDrawable The GLAutoDrawable object.
     */
    public abstract void display(GLAutoDrawable drawable);

    /**
     * Called by the drawable immediately after the OpenGL context is
     * initialized for the first time. Can be used to perform one-time OpenGL
     * initialization such as setup of lights and display lists.
     * @param gLDrawable The GLAutoDrawable object.
     */
    public abstract void init(final GLAutoDrawable drawable);

    /**
     * Called by the drawable during the first repaint after the component has
     * been resized. The client can update the viewport and view volume of the
     * window appropriately, for example by a call to
     * GL.glViewport(int, int, int, int); note that for convenience the component
     * has already called GL.glViewport(int, int, int, int)(x, y, width, height)
     * when this method is called, so the client may not have to do anything in
     * this method.
     * @param gLDrawable The GLAutoDrawable object.
     * @param x The X Coordinate of the viewport rectangle.
     * @param y The Y coordinate of the viewport rectanble.
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    public abstract void reshape(GLAutoDrawable drawable, int x, int y, int width, int height);

    /** Called when the display mode has been changed. CURRENTLY UNIMPLEMENTED IN JOGL ????
     * @param gLDrawable The GLAutoDrawable object.
     * @param modeChanged Indicates if the video mode has changed.
     * @param deviceChanged Indicates if the video device has changed.
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /** Called when a key is typed in the opengl panel. 
     *  Override this method to handle keyboard events.
     * @param key The key typed.
     */
    public void keyboard(char key) {
    }

    /** returns the class for handling mouse events.
     *  Override this method to handle mouse events.
     * @return mouse event handling class, in this implementation a class
     *         with empty implementations.
     * @see MouseAdapter
     */
    public MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
        };
    }

    public abstract JPopupMenu getPopupMenu();

    /** keyboard event handling class that forwards events to method keyboard() */
    private class KeyAdapterImpl extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            keyboard(e.getKeyChar());
        }
    }

    /** help method for adding an item to a popup menu. This method also installs
     *  an action listener that forwards mouse selection to an appropriate call
     *  of the keyboard method.
     * @param menu Menu to which a new item is to be added
     * @param s    text of new menu item
     * @param key  equivalent keyboard key, used as argument for keyboard method
     */
    protected void addMenuItem(JPopupMenu menu, String s, final char key) {
        JMenuItem item = new JMenuItem(s + " [" + key + "]");
        item.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboard(key);
            }
        });
        menu.add(item);
    }
}