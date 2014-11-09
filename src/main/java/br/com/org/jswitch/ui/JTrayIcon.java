package br.com.org.jswitch.ui;

/*
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JTrayIcon extends TrayIcon {
    private JPopupMenu menu;
    private static JDialog dialog;
    static {
	dialog = new JDialog((Frame) null);
	dialog.setUndecorated(true);
	dialog.setAlwaysOnTop(true);
    }

    private static PopupMenuListener popupListener = new PopupMenuListener() {

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	    dialog.setVisible(false);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
	    dialog.setVisible(false);
	}
    };

    public JTrayIcon(Image image) {
	super(image);
	setMouseListener();
    }

    public JTrayIcon(Image image, String name, JPopupMenu menu2) {
	super(image, name);
	setJPopupMenu(menu2);
	setMouseListener();
    }

    private void setMouseListener() {
	addMouseListener(new MouseAdapter() {
	    /*
	     * public void mousePressed(MouseEvent e) { showJPopupMenu(e); }
	     */

	    @Override
	    public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
		    showJPopupMenu(me);
		}
	    }

	    /*
	     * public void mouseReleased(MouseEvent e) { showJPopupMenu(e); }
	     */
	});
    }

    protected void showJPopupMenu(MouseEvent e) {
	if (/*e.isPopupTrigger() &&*/ menu != null) {
	    Dimension size = menu.getPreferredSize();
	    showJPopupMenu(e.getX()-size.width, e.getY() - size.height);
	}
    }

    protected void showJPopupMenu(int x, int y) {
	dialog.setLocation(x, y);
	dialog.setVisible(true);
	menu.show(dialog.getContentPane(), 0, 0);
	// popup works only for focused windows
	dialog.toFront();
    }

    public JPopupMenu getJPopupMenu() {
	return menu;
    }

    public void setJPopupMenu(JPopupMenu menu) {
	if (this.menu != null) {
	    this.menu.removePopupMenuListener(popupListener);
	}
	this.menu = menu;
	menu.addPopupMenuListener(popupListener);
    }
}