package br.com.org.jswitch.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 * 
 * @author Anderson
 *
 */
public class JPopupMenuExt extends JPopupMenu {
    /**
     * 
     */
    private static final long serialVersionUID = 3729624181749689623L;
    
    private boolean mouseStillOnMenu = false;

    private class CloseMouseListener extends MouseAdapter {
	/*@Override
	public void mouseExited(MouseEvent e) {
	    // MenuSelectionManager disarms the old one and arms the new item in
	    // one event of the EventDispatchingThread.
	    // So, after that event we can be sure MenuSelectionManager has
	    // completed his job and the new item (or none) is selected.
	    EventQueue.invokeLater(new Runnable() {
		@Override
		public void run() {
		    if (!isAnItemSelected()) {
			cancel();
		    }
		}
	    });
	}*/
	
	 @Override
	    public void mouseEntered(MouseEvent arg0) {
		mouseStillOnMenu  = true;

	    }

	    @Override
	    public void mouseExited(MouseEvent arg0) {
		mouseStillOnMenu  = false;
		new Thread(new Runnable() {

		    @Override
		    public void run() {

			try {
			    Thread.sleep(1000); // waits one second before
						// checking if mouse is still on
						// the menu
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			if (!isMouseStillOnMenu()) {
			    cancel();
			}

		    }

		    private boolean isMouseStillOnMenu() {
			return mouseStillOnMenu;
		    }

		}).start();

	    }
    }

    private MouseListener closeMouseListener = new CloseMouseListener();

   /* private boolean isAnItemSelected() {
	for (Component comp :this.getComponents()) {
	    if (comp instanceof JMenuItem) {
		JMenuItem item = (JMenuItem) comp;
		if (item.isArmed()) {
		    return true;
		}
	    }
	}

	return false;
    }*/
    
    
    @Override
    public JMenuItem add(JMenuItem menuItem) {
	menuItem.addMouseListener(closeMouseListener);
	return super.add(menuItem);
    }

    @Override
    public void remove(int pos) {
	if (pos < 0) {
	    throw new IllegalArgumentException("index less than zero.");
	}
	if (pos > getComponentCount() - 1) {
	    throw new IllegalArgumentException("index greater than the number of items.");
	}

	Component comp = getComponent(pos);
	if (comp instanceof JMenuItem) {
	    comp.removeMouseListener(closeMouseListener);
	}

	super.remove(pos);
    }

    private void cancel() {
	firePopupMenuCanceled();
	setVisible(false);
    }
}
