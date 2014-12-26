package br.com.org.jswitch.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import br.com.org.jswitch.cfg.FileChangeListener;
import br.com.org.jswitch.cfg.FileMonitor;
import br.com.org.jswitch.control.OperatingSystem;
import br.com.org.jswitch.control.JSwitchManager;
import br.com.org.jswitch.model.JDK;
import br.com.org.jswitch.ui.JSwitchUI.MODE;

/**
 * This class build elements necessary to show System Tray at app
 * 
 * @author Anderson
 *
 */
public class JTrayIconUI implements FileChangeListener {

    	private static final ResourceBundle bundle = MessagesHelp.getBundle();
	
    	private static final String MENU_CLOSE = bundle.getString("menu.close");
    	private static final String MENU_CONFIG = bundle.getString("menu.config");
    	private static final String WAS_SELECTED = bundle.getString("warn.jdk.was.selected");
	private static final String J_SWITCH_WAS_INIT_SUCCESS = bundle.getString("info.init.success");
	private static final String J_SWITCH_WAS_UPDATED = bundle.getString("info.update.success");
	private static final String ERROR_DURING_SETTINGS = bundle.getString("error.config"); 
	private static final String ATENTION = bundle.getString("info.atention");
	private JSwitchManager systemManager;
	private TrayIcon icon;
	private JCheckBoxMenuItemGroupListener menuItemGroup;
	private JSwitchUI jSwitchUI;
	
	public JTrayIconUI() {
	    jSwitchUI = new JSwitchUI(this);
	}

	public void show() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					buildSystemTray();
					jSwitchUI.initTray();
					icon.displayMessage(ATENTION, J_SWITCH_WAS_INIT_SUCCESS, 
						TrayIcon.MessageType.INFO);
				} catch (Exception e) {
					System.out.println("Not using the System UI defeats the purpose...");
					e.printStackTrace();
				}
			}
		});

	}

	public void buildSystemTray() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException, AWTException, Exception,
			FileNotFoundException {
		systemManager = new JSwitchManager();
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/switch-icon.png"));

		JPopupMenu menu = new JPopupMenu();
		icon = new JTrayIcon(image, "JSwitch", menu);
		icon.setImageAutoSize(true);
		List<String> jdkInstalled = systemManager.getJDKInstalled();
		menuItemGroup = new JCheckBoxMenuItemGroupListener(systemManager,icon);
		ButtonGroup buttonGroup = new ButtonGroup();
		for (final String jdk : jdkInstalled) {
			JCheckBoxMenuItem checkboxMenuItem = new JCheckBoxMenuItem(jdk);
			menu.add(checkboxMenuItem);
			buttonGroup.add(checkboxMenuItem);
			menuItemGroup.add(checkboxMenuItem);
			
		}
		menu.addSeparator();
		JMenuItem addjdk = new JMenuItem(MENU_CONFIG,createIconClose("/settings.png"));
		menu.add(addjdk);
		
		addjdk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {  
					    jSwitchUI.show(MODE.UPDATE);
					} catch (Exception e) {
						System.out.println("Not using the System UI defeats the purpose...");
						e.printStackTrace();
					}
				}
			});
			}
		});
		JMenuItem closeItem = new JMenuItem(MENU_CLOSE);
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(closeItem);

		FileMonitor fileMonitor = FileMonitor.getInstance();
		File fileConfSelected = systemManager.getFileSelected();
		fileMonitor.addFileChangeListener(this, fileConfSelected, 1000);
		
	}

	public void addIconTray(JFrame jFrame, boolean wasUpdate) throws Exception {
	    SystemTray tray = SystemTray.getSystemTray();
	    buildSystemTray();
	    tray.add(icon);
	    if(jFrame!=null){
		jFrame.setVisible(false);
	    }
	    JDK currentJavaHomeVar = systemManager.getCurrentJavaHomeVar();
	    menuItemGroup.selectItem(currentJavaHomeVar.getName());
	    if(wasUpdate){
		icon.displayMessage(ATENTION, J_SWITCH_WAS_UPDATED, 
			TrayIcon.MessageType.INFO);
	    }
	}
	
	public void removeIconTray(JFrame jFrame){
	    SystemTray tray = SystemTray.getSystemTray();
	    if(icon!=null){
		tray.remove(icon);
		jFrame.setVisible(true);		
	    }
	}

	private ImageIcon createIconClose(String imageName) {
		ImageIcon iconClose = new ImageIcon((getClass().getResource(imageName)));
		Image imageOrg = iconClose.getImage(); // transform it
		Image imageNew = imageOrg.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
		iconClose = new ImageIcon(imageNew);
		return iconClose;
	}

	@Override
	public void fileChanged(File file) {
		try {
			String value = systemManager.getPropertyValueOnSelectedFile(
					OperatingSystem.PROPERTY_SELECTED_JDK, file);
			systemManager.change(value);
			//menuItemGroup.selectItem(value);
			icon.displayMessage("JSwitch",MessageFormat.format(WAS_SELECTED,value), TrayIcon.MessageType.INFO);
		} catch (Exception e) {
			icon.displayMessage(ATENTION, ERROR_DURING_SETTINGS,
					TrayIcon.MessageType.ERROR);
		}

	}

}
