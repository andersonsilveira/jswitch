package br.com.org.jswitch.ui;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import br.com.org.jswitch.cfg.FileChangeListener;
import br.com.org.jswitch.cfg.FileMonitor;
import br.com.org.jswitch.cfg.OperatingSystem;
import br.com.org.jswitch.control.OperationSystemManager;

/**
 * 
 * @author Anderson
 *
 */
public class JSwitchSystemTray implements FileChangeListener {

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

	icon = new TrayIcon(image, "JSwitch", null);

	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

	final JPopupMenu jpopup = new JPopupMenu();

	icon.setImageAutoSize(true);
	List<String> jdkInstalled = systemManager.getJDKInstalled();
	menuItemGroup = new CheckboxMenuItemGroup(systemManager, icon);
	for (final String jdk : jdkInstalled) {
	    JCheckBoxMenuItem checkboxMenuItem = new JCheckBoxMenuItem(jdk);
	    jpopup.add(checkboxMenuItem);
	    menuItemGroup.add(checkboxMenuItem);

	}
	jpopup.addSeparator();
	ImageIcon iconClose = createIconClose();
	JMenuItem closeItem = new JMenuItem("Fechar", iconClose);
	closeItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});
	icon.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
		    jpopup.setInvoker(jpopup);
		    jpopup.setVisible(true);
		    jpopup.setLocation((e.getX() - jpopup.getWidth()), (e.getY() - jpopup.getHeight()));

		}
	    }
	});

	jpopup.add(closeItem);

	tray.add(icon);
	icon.displayMessage("Attention", "JSwitch foi iniciado com sucesso!!", TrayIcon.MessageType.INFO);

	FileMonitor fileMonitor = FileMonitor.getInstance();
	File fileConfSelected = systemManager.getFileSelected();
	fileMonitor.addFileChangeListener(this, fileConfSelected, 1000);
    }

    private ImageIcon createIconClose() {
	ImageIcon iconClose = new ImageIcon((getClass().getResource("/logout.png")));
	Image imageOrg = iconClose.getImage(); // transform it
	Image imageNew = imageOrg.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); 
	iconClose = new ImageIcon(imageNew);
	return iconClose;
    }

    @Override
    public void fileChanged(File file) {
	try {
	    String value = systemManager.getPropertyValueOnSelectedFile(OperatingSystem.PROPERTY_SELECTED_JDK, file);
	    systemManager.change(value);
	    menuItemGroup.selectItem(value);
	    icon.displayMessage("JSwitch", value + " foi selecionado!", TrayIcon.MessageType.INFO);
	} catch (Exception e) {
	    icon.displayMessage("Attention", " ocorreu um erro durante a configuração", TrayIcon.MessageType.ERROR);
	}

    }
}
