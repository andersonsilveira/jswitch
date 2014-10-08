package br.com.org.jswitch.cfg;

import java.io.File;

import javax.swing.JFileChooser;

import br.com.org.jswitch.model.JDK;
/**
 * 
 * @author Anderson
 *
 */
public abstract class DirectoryChooser {

	public JDK choose() {
		JFileChooser chooser = new JFileChooser(getInitialDirectory());
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
	
	public abstract String getInitialDirectory();

}
