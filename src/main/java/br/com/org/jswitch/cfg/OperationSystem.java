package br.com.org.jswitch.cfg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.commons.io.filefilter.NameFileFilter;

import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.model.JDK;
/**
 * 
 * @author Anderson
 *
 */
public abstract class OperationSystem {
	
	 private static final String SELECTED = ".selected";
	public static final String CONFIG_CFG = "config.cfg";
	public static final String PROPERTY_SELECTED_JDK = "selectedJDK";

	public abstract List<JDK> loadDefaultJDK() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException;
	
	public abstract void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException, InstallationDirectoryFaultException, PermissionOperatingSystemExpection ;
	
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

	public abstract String getPropertyValueOnConfigFile(String jdk, File file)
			throws Exception;
	public File getFileConfig() throws Exception {
		String pathname = getInstallationDir();
		File fileConfig = new File(pathname+File.separator+CONFIG_CFG);
		return fileConfig;
	}
	
	public File getFileSelectedConfig() throws Exception {
		String pathname = getInstallationDir();
		File fileSelected = new File(pathname+File.separator+SELECTED);
		return fileSelected;
	}

	public abstract String getInstallationDir() throws Exception;

	public abstract void setPropertyValueOnFile(String string, String property, File file);

	
}
