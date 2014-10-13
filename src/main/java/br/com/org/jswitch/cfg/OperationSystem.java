package br.com.org.jswitch.cfg;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;

import br.com.org.jswitch.model.JDK;

public abstract class OperationSystem {

	public abstract List<JDK> loadDefaultJDK();
	
	public abstract void install(List<JDK> jdks, JTextPane jTextPane) throws Exception;
	
	public JDK chooseDirectoryOfJDKInstalation() {
		JFileChooser chooser = new JFileChooser(getInitialDirectoryChooser());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retorno = chooser.showOpenDialog(null);

		if (retorno == JFileChooser.APPROVE_OPTION) {
			File chosenDirectory = chooser.getSelectedFile();
			String path = chosenDirectory.getPath();
			String name = chosenDirectory.getName();
			return new JDK(name, path);
		}
		return null;

	}
	
	public abstract String getInitialDirectoryChooser();
}
