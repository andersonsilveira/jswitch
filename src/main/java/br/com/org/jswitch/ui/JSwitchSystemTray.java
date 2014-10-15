package br.com.org.jswitch.ui;

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
import java.util.List;

import br.com.org.jswitch.cfg.FileChangeListener;
import br.com.org.jswitch.cfg.FileMonitor;
import br.com.org.jswitch.cfg.OperationSystem;
import br.com.org.jswitch.control.OperationSystemManager;

public class JSwitchSystemTray implements FileChangeListener{

	
	private OperationSystemManager systemManager;
	private TrayIcon icon;
	private CheckboxMenuItemGroup menuItemGroup;

	public void execute() throws Exception {

		systemManager = new OperationSystemManager();
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/switch-icon.png"));

		PopupMenu menu = new PopupMenu();
		icon = new TrayIcon(image, "JSwitch", menu);
		icon.setImageAutoSize(true);
		List<String> jdkInstalled = systemManager.getJDKInstalled();
		menuItemGroup = new CheckboxMenuItemGroup(systemManager,icon);
		for (final String jdk : jdkInstalled) {
			CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(jdk);
			menu.add(checkboxMenuItem);
			menuItemGroup.add(checkboxMenuItem);
			
		}
		menu.addSeparator();
		MenuItem closeItem = new MenuItem("Fechar");
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
	}

	@Override
	public void fileChanged(File file) {
		try {
			String value = systemManager.getPropertyValueOnSelectedFile(OperationSystem.PROPERTY_SELECTED_JDK,file);
			systemManager.change(value);
			menuItemGroup.selectItem(value);
			icon.displayMessage("JSwitch", value +" foi selecionado!", 
					TrayIcon.MessageType.INFO);
		} catch (Exception e) {
			icon.displayMessage("Attention", " ocorreu um erro durante a configuração", 
					TrayIcon.MessageType.ERROR);
		}
		
	}
}
