package br.com.org.jswitch.ui;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
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
    private CheckboxMenuItemGroupListener menuItemListener;
    /* Added a "hidden dialog" */
    private JDialog hiddenDialog;

    public void show() throws Exception {
	
	

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
	ButtonGroup buttonGroup = new ButtonGroup();
	menuItemListener = new CheckboxMenuItemGroupListener(systemManager, icon);
	
	  ActionListener listener = new ActionListener () {
	        @Override
	        public void actionPerformed (ActionEvent ae) {
	            /* We want to make sure the hidden dialog goes away after selection */
	            hiddenDialog.setVisible(false);
	        }
	    };
	    
	for (final String jdk : jdkInstalled) {
	    JCheckBoxMenuItem checkboxMenuItem = new JCheckBoxMenuItem(jdk);
	    buttonGroup.add(checkboxMenuItem);
	    jpopup.add(checkboxMenuItem);
	    menuItemListener.add(checkboxMenuItem);
	    checkboxMenuItem.addActionListener(listener);

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
                    /* Place the hidden dialog at the same location */
                    hiddenDialog.setLocation(me.getX(), me.getY());
                    /* Now the popup menu's invoker is the hidden dialog */
                    jpopup.setInvoker(hiddenDialog);
                    hiddenDialog.setVisible(true);
                    jpopup.setVisible(true);
                    jpopup.setLocation((me.getX() - jpopup.getWidth()), (me.getY() - jpopup.getHeight()));
		
	    }
	   }

	});
	
	jpopup.add(closeItem);

	tray.add(icon);
	/* Initialize the hidden dialog as a headless, titleless dialog window */
	hiddenDialog = new JDialog ();
	hiddenDialog.setSize(10, 10);
	/* Add the window focus listener to the hidden dialog */
	hiddenDialog.addWindowFocusListener(new WindowFocusListener () {
	    @Override
	    public void windowLostFocus (WindowEvent we ) {
		hiddenDialog.setVisible(false);
	    }
	    @Override
	    public void windowGainedFocus (WindowEvent we) {}
	});
	
	icon.displayMessage("JSwitch", "Foi iniciado com sucesso!!", TrayIcon.MessageType.INFO);

	FileMonitor fileMonitor = FileMonitor.getInstance();
	File fileConfSelected = systemManager.getFileSelected();
	fileMonitor.addFileChangeListener(this, fileConfSelected, 1000);
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
