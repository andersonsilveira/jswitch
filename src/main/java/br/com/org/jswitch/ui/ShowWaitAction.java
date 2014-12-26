package br.com.org.jswitch.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.control.JSwitchManager;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
class ShowWaitAction {
    private static final ResourceBundle bundle = MessagesHelp.getBundle();

    private static final String SEARCHING_JDK = bundle.getString("info.searching.jdk");

    private static final String JDK_NOT_FOUND = bundle.getString("error.jdk.not.found");

    private static final String LOAD_ERROR = bundle.getString("error.jdk.load");

    protected static final long SLEEP_TIME = 1 * 1000;

    private Component componentParent;

    private List<JDK> loadJDKInstalled = new ArrayList<JDK>();

    public List<JDK> getLoadJDKInstalled() {
	return loadJDKInstalled;
    }

    private String name;

    private JDialog dialog;

    private RESOURCE resource;

    public ShowWaitAction(String name, Component componentParent, RESOURCE resource) {
	this.componentParent = componentParent;
	this.name = name;
	this.resource = resource;
    }

    public void executeLoader(final JSwitchManager manager) {
	SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {

	    @Override
	    protected Void doInBackground() throws Exception {

		try {
		    resource.setManager(manager);
		    loadJDKInstalled = resource.execute();
		} catch (LoadDefaultJDKException e) {
		    JOptionPane.showMessageDialog(null, LOAD_ERROR, "JSwitch", JOptionPane.ERROR_MESSAGE);
		} catch (DefautJDKInstalledNotFoundException e) {
		    JOptionPane.showMessageDialog(null, JDK_NOT_FOUND, "JSwitch", JOptionPane.WARNING_MESSAGE);
		}
		Thread.sleep(SLEEP_TIME);
		return null;
	    }
	};

	Window win = SwingUtilities.getWindowAncestor(componentParent);

	dialog = new JDialog(win, name, ModalityType.APPLICATION_MODAL);
	dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	dialog.setUndecorated(true);

	mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("state")) {
		    if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
			dialog.dispose();
		    }
		}
	    }
	});
	mySwingWorker.execute();

	JProgressBar progressBar = new JProgressBar();
	progressBar.setIndeterminate(true);
	JPanel panel = new JPanel(new BorderLayout());
	panel.add(progressBar, BorderLayout.CENTER);
	panel.add(new JLabel(SEARCHING_JDK), BorderLayout.PAGE_START);
	dialog.add(panel);
	dialog.pack();
	dialog.setLocationRelativeTo(win);
	dialog.setVisible(true);
    }

    public enum RESOURCE {
	CONFIGURED(1), INSTALLED_ON_SYSTEM(2);

	private int type;

	private RESOURCE(int type) {
	    this.type = type;
	}

	private JSwitchManager manager;

	public void setManager(JSwitchManager manager) {
	    this.manager = manager;
	}

	public List<JDK> execute() throws Exception {
	    switch (type) {
	    case 1:
		return manager.loadJDKConfigured();
	    case 2:
		return manager.loadJDKInstalledOnSystem();
	    }
	    return null;
	}
    }

}