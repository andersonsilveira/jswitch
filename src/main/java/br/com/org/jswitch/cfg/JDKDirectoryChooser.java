package br.com.org.jswitch.cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import br.com.org.jswitch.model.JDK;

public class JDKDirectoryChooser {

	public List<JDK> choose() {
		List<JDK> result = new ArrayList<JDK>();
		JFileChooser chooser = new JFileChooser("C:/");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retorno = chooser.showOpenDialog(null);

		if (retorno == JFileChooser.APPROVE_OPTION) {
			File chosenDirectory = chooser.getSelectedFile();
			String path = chosenDirectory.getPath();
			String name = chosenDirectory.getName();
			result.add(new JDK(name, path));
		}
		return result;

	}

}
