package br.com.org.jswitch.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
    private CheckboxMenuItemGroupListener menuItemListener;
    private static JPopupMenuExt jpopup;

    public void show() throws Exception {
	 SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run () {
	            try {
	                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                buildSystemTray();
	            } catch (Exception e) {
	                System.out.println("Not using the System UI defeats the purpose...");
	                e.printStackTrace();
	            }
	        }
	    });
	
    }

    private void buildSystemTray() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	    UnsupportedLookAndFeelException, AWTException, Exception, FileNotFoundException {
	systemManager = new OperationSystemManager();
	if (!SystemTray.isSupported()) {
	    System.out.println("SystemTray is not supported");
	    return;
	}

	SystemTray tray = SystemTray.getSystemTray();
	Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/switch-icon.png"));

	icon = new TrayIcon(image, "JSwitch", null);

	jpopup = new JPopupMenuExt();

	icon.setImageAutoSize(true);
	List<String> jdkInstalled = systemManager.getJDKInstalled();
	ButtonGroup buttonGroup = new ButtonGroup();
	menuItemListener = new CheckboxMenuItemGroupListener(systemManager, icon);
	
	    
	for (final String jdk : jdkInstalled) {
	    JCheckBoxMenuItem checkboxMenuItem = new JCheckBoxMenuItem(jdk);
	    buttonGroup.add(checkboxMenuItem);
	    jpopup.add(checkboxMenuItem);
	    menuItemListener.add(checkboxMenuItem);

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
	    public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
		    jpopup.setInvoker(jpopup);
		    jpopup.setVisible(true);
                    jpopup.setLocation((me.getX()-jpopup.getWidth()), (me.getY()-jpopup.getHeight()));  
	    }
	   }

	});
	
	jpopup.add(closeItem);
	tray.add(icon);
	
	icon.displayMessage("JSwitch", "Foi iniciado com sucesso!!", TrayIcon.MessageType.INFO);

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
	    String value = systemManager.getPropertyValueOnSelectedFile(OperatingSystem.PROPERTY_SELECTED_JDK, file);
	    systemManager.change(value);
	    menuItemListener.selectItem(value);
	    icon.displayMessage("JSwitch", value + " foi selecionado!", TrayIcon.MessageType.INFO);
	} catch (Exception e) {
	    icon.displayMessage("Atenção", " ocorreu um erro durante a configuração", TrayIcon.MessageType.ERROR);
	}

    }
    
}
