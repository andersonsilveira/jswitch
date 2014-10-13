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
import java.util.List;

import br.com.org.jswitch.cfg.SystemTrayConfig;
import br.com.org.jswitch.control.OperationSystemManager;

public class JSwitchSystemTray {

	
	public void execute() throws Exception {

		final SystemTrayConfig systemTrayConfig = new OperationSystemManager().getSystemTrayConfig();
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/switch-icon.png"));

		PopupMenu menu = new PopupMenu();
		final TrayIcon icon = new TrayIcon(image, "JSwitch", menu);
		icon.setImageAutoSize(true);
		List<String> jdkInstalled = systemTrayConfig.getJDKInstalled();
		 CheckboxMenuItemGroup mig = new CheckboxMenuItemGroup(systemTrayConfig,icon);
		for (final String jdk : jdkInstalled) {
			CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(jdk);
			menu.add(checkboxMenuItem);
			mig.add(checkboxMenuItem);
			
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
	}
}
