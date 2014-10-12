package br.com.org.jswitch;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActiveTray {
  public static void main(String args[]) throws Exception{
    if (SystemTray.isSupported() == false) {
      System.err.println("No system tray available");
      return;
    }
    final SystemTray tray = SystemTray.getSystemTray();
    PropertyChangeListener propListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        TrayIcon oldTray[] = (TrayIcon[]) evt.getOldValue();
        TrayIcon newTray[] = (TrayIcon[]) evt.getNewValue();
        System.out.println(oldTray.length + " / " + newTray.length);
      }
    };
    tray.addPropertyChangeListener("trayIcons", propListener);
    Image image = Toolkit.getDefaultToolkit().getImage("src/main/resources/switch-icon.png");
    PopupMenu popup = new PopupMenu();
    MenuItem item = new MenuItem("Hello, World");
    final TrayIcon trayIcon = new TrayIcon(image, "Tip Text", popup);
    ActionListener menuActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        trayIcon.displayMessage("Good-bye", "Cruel World", TrayIcon.MessageType.WARNING);
      }
    };
    item.addActionListener(menuActionListener);
    popup.add(item);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tray.remove(trayIcon);
      }
    };
    trayIcon.addActionListener(actionListener);
    tray.add(trayIcon);
  }
}