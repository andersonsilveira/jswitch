package br.com.org.jswitch.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.model.JDK;
/**
 * 
 * @author Anderson
 *
 */
class ShowWaitAction {
	protected static final long SLEEP_TIME = 1 * 1000;

	private Component componentParent;

	private JTable table;

	private List<JDK> loadJDKInstalled;

	public List<JDK> getLoadJDKInstalled() {
		return loadJDKInstalled;
	}

	private String name;

	public ShowWaitAction(String name, Component componentParent, JTable table) {
		this.componentParent = componentParent;
		this.table = table;
		this.name = name;
	}

	public void executeLoader(final OperationSystemManager manager) {
		SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				try {
				    loadJDKInstalled = manager.loadJDKInstalled();
				} catch (LoadDefaultJDKException e) {
				    JOptionPane.showMessageDialog(null, "Falha durante a busca de diretórios padrão de instalação da JDK, tente carregar manualmente apartir do botão 'Carregar..'",
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				}catch (DefautJDKInstalledNotFoundException e){
				    JOptionPane.showMessageDialog(null, "Não foi encontrado nenhum dirétorio padrão de instalação, tente carregar manualmente apartir do botão 'Carregar..'",
					    "JSwitch", JOptionPane.WARNING_MESSAGE);
				}
				JDKTableModel tableModel = new JDKTableModel(loadJDKInstalled);
				table.setModel(tableModel);
				Thread.sleep(SLEEP_TIME);
				return null;
			}
		};

		Window win = SwingUtilities.getWindowAncestor(componentParent);
		final JDialog dialog = new JDialog(win, name,
				ModalityType.APPLICATION_MODAL);

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
		panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(win);
		dialog.setVisible(true);
	}

}