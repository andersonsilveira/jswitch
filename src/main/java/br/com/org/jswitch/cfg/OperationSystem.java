package br.com.org.jswitch.cfg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.commons.io.filefilter.NameFileFilter;

import br.com.org.jswitch.model.JDK;

public abstract class OperationSystem {
	
	 public static final String CONFIG_CFG = "config.cfg";
	public static final String SELECTED_JDK = "selectedJDK";

	public abstract List<JDK> loadDefaultJDK();
	
	public abstract void install(List<JDK> jdks, JTextPane jTextPane) throws Exception;
	
	public JDK chooseDirectoryOfJDKInstalation() {
		JFileChooser chooser = new JFileChooser(getInitialDirectoryChooser());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retorno = chooser.showOpenDialog(null);

		if (retorno == JFileChooser.APPROVE_OPTION) {
			File chosenDirectory = chooser.getSelectedFile();
			String[] listDir = chosenDirectory.list(org.apache.commons.io.filefilter.DirectoryFileFilter.INSTANCE);
			List<String> dirs = Arrays.asList(listDir);
				if(dirs.contains("bin")){
					File dirBin = new File(chosenDirectory.getAbsolutePath()+File.separator+"bin");
					List<String> files = Arrays.asList(dirBin.list( new NameFileFilter("javac.exe") ));
					if(!files.isEmpty()){
						String path = chosenDirectory.getPath();
						String name = chosenDirectory.getName();
					return new JDK(name, path);
					
				}
					
			}
		}
		JOptionPane.showMessageDialog(null, "O diretorio escolhido não é um diretório válido!",
				"JSwitch", JOptionPane.ERROR_MESSAGE);
		return null;

	}
	
	public abstract String getInitialDirectoryChooser();

	public abstract List<String> getJDKInstalled();

	public abstract void change(String jdk) throws IOException;

	public abstract String getPropertyValueOnConfigFile(String jdk)
			throws Exception;
	public File getFileConfig() throws Exception {
		String pathname = getInstallationDir();
		File fileConfig = new File(pathname+File.separator+CONFIG_CFG);
		return fileConfig;
	}

	public abstract String getInstallationDir() throws Exception;

	public abstract void setPropertyValueOnConfiFile(String string, String property);

	
}
