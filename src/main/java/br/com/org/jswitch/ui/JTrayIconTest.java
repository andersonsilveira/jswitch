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

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
public class JTrayIconTest implements FileChangeListener {

	private OperationSystemManager systemManager;
	private TrayIcon icon;
	//private CheckboxMenuItemGroupListener menuItemListener;
	//private static JPopupMenuExt jpopup;
	private JCheckBoxMenuItemGroupListener menuItemGroup;

	public void show() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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

		JPopupMenu menu = new JPopupMenu();
		icon = new JTrayIcon(image, "JSwitch", menu);
		icon.setImageAutoSize(true);
		List<String> jdkInstalled = systemManager.getJDKInstalled();
		menuItemGroup = new JCheckBoxMenuItemGroupListener(systemManager,icon);
		for (final String jdk : jdkInstalled) {
			JCheckBoxMenuItem checkboxMenuItem = new JCheckBoxMenuItem(jdk);
			menu.add(checkboxMenuItem);
			menuItemGroup.add(checkboxMenuItem);
			
		}
		menu.addSeparator();
		JMenuItem closeItem = new JMenuItem("Fechar",createIconClose());
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
		fileMonitor.addFileChangeListener(this, fileConfSelected, 1000);
		systemManager.setCurrentJDKOnSystem();
	}

	private ImageIcon createIconClose() {
		ImageIcon iconClose = new ImageIcon((getClass().getResource("/logout.png")));
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
			icon.displayMessage("Atenção", " ocorreu um erro durante a configuração",
					TrayIcon.MessageType.ERROR);
		}

	}

}
