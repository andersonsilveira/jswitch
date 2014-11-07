package br.com.org.jswitch.ui;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import br.com.org.jswitch.cfg.FileChangeListener;
import br.com.org.jswitch.cfg.FileMonitor;
import br.com.org.jswitch.cfg.OperatingSystem;
import br.com.org.jswitch.control.OperationSystemManager;

/**
 * This class build elements necessary to show System Tray at app
 * 
 * @author Anderson
 *
 */
public class JSwitchSystemTray implements FileChangeListener {

	private OperationSystemManager systemManager;
	private TrayIcon icon;
	//private CheckboxMenuItemGroupListener menuItemListener;
	//private static JPopupMenuExt jpopup;
	private CheckboxMenuItemGroupItemListener menuItemGroup;
	private PopupMenu menu;

	public void show() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					buildSystemTray();
				} catch (Exception e) {
					System.out.println("Not using the System UI defeats the purpose...");
					e.printStackTrace();
				}
			}
		});

	}

	private void buildSystemTray() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException, AWTException, Exception,
			FileNotFoundException {
		systemManager = new OperationSystemManager();
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/switch-icon.png"));

		menu = new PopupMenu();
		MenuItem addjdk = new MenuItem("Adicionar...");
		addjdk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
					    new JSwitchUI().showForUpdate();
					} catch (Exception e) {
						System.out.println("Not using the System UI defeats the purpose...");
						e.printStackTrace();
					}
				}
			});
			}
		});
		menu.add(addjdk);
		menu.addSeparator();
		icon = new TrayIcon(image, "JSwitch", menu);
		icon.setImageAutoSize(true);
		createJDKItemMenu();
		
		menu.addSeparator();
		MenuItem closeItem = new MenuItem("Sair");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(closeItem);

		tray.add(icon);
		icon.displayMessage("Attention", "JSwitch foi iniciado com sucesso!!", 
				TrayIcon.MessageType.INFO);
		
		FileMonitor fileMonitor = FileMonitor.getInstance();
		File fileConfSelected = systemManager.getFileSelected();
		File fileConfig = systemManager.getFileConfig();
		fileMonitor.addFileChangeListener(this, fileConfSelected, 1000);
		fileMonitor.addFileChangeListener(this, fileConfig, 1000);
		systemManager.setCurrentJDKOnSystem();
	}

	private void createJDKItemMenu() {
	    List<String> jdkInstalled = systemManager.getJDKInstalled();
	    menuItemGroup = new CheckboxMenuItemGroupItemListener(systemManager,icon);
	    int index = 2;
	    for (final String jdk : jdkInstalled) {
	    	CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(jdk);
		menu.insert(checkboxMenuItem, index);
	    	menuItemGroup.add(checkboxMenuItem);
	    	index++;
	    }
	}

	/*private ImageIcon createIconClose() {
		ImageIcon iconClose = new ImageIcon((getClass().getResource("/logout.png")));
		Image imageOrg = iconClose.getImage(); // transform it
		Image imageNew = imageOrg.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
		iconClose = new ImageIcon(imageNew);
		return iconClose;
	}*/

	@Override
	public void fileChanged(File file) {
	    if(OperatingSystem.SELECTED.equals(file.getName())){
		try {
			String value = systemManager.getPropertyValueOnSelectedFile(
					OperatingSystem.PROPERTY_SELECTED_JDK, file);
			systemManager.change(value);
			menuItemGroup.selectItem(value);
			icon.displayMessage("JSwitch", value + " foi selecionado!", TrayIcon.MessageType.INFO);
		} catch (Exception e) {
			icon.displayMessage("Aten��o", " ocorreu um erro durante a configura��o",
					TrayIcon.MessageType.ERROR);
		}
	    }
	    
	     if(OperatingSystem.CONFIG_CFG.equals(file.getName())){{
		 Set<CheckboxMenuItem> items = menuItemGroup.getItems();
		 for (CheckboxMenuItem checkboxMenuItem : items) {
		    menu.remove(checkboxMenuItem);
		 }
		 createJDKItemMenu();
		 
	     }

	}
	}

}
