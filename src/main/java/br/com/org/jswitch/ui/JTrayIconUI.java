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
import java.util.List;

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
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.ui.JSwitchUI.MODE;

/**
 * This class build elements necessary to show System Tray at app
 * 
 * @author Anderson
 *
 */
public class JTrayIconUI implements FileChangeListener {

	private OperationSystemManager systemManager;
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
					icon.displayMessage("Atenção", "JSwitch foi iniciado com sucesso!!", 
						TrayIcon.MessageType.INFO);
					//systemManager.setCurrentJDKOnSystem();
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
		systemManager = new OperationSystemManager();
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
		JMenuItem addjdk = new JMenuItem("Configurar...",createIconClose("/settings.png"));
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
		JMenuItem closeItem = new JMenuItem("Sair");
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
	    systemManager.setCurrentJDKOnSystem();
	    if(wasUpdate){
		icon.displayMessage("Atenção", "JSwitch foi atualizado", 
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
			menuItemGroup.selectItem(value);
			icon.displayMessage("JSwitch", value + " foi selecionado!", TrayIcon.MessageType.INFO);
		} catch (Exception e) {
			icon.displayMessage("Atenção", " ocorreu um erro durante as configurações",
					TrayIcon.MessageType.ERROR);
		}

	}

}
