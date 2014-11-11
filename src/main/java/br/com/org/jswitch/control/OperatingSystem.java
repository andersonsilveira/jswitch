package br.com.org.jswitch.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;

import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.model.JDK;
import br.com.org.jswitch.ui.JTrayIconUI;
/**
 * Abstraction of operating system, to realize the specific O.S
 * is mandatory extends this class
 * @author Anderson
 *
 */
public abstract class OperatingSystem {
	
	public static final String SELECTED = ".selected";
	public static final String CONFIG_CFG = "config.cfg";
	public static final String PROPERTY_SELECTED_JDK = "selectedJDK";
	protected StringBuilder log = new StringBuilder();

	public abstract List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException;
	
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

	public abstract void registerBootstrp() throws InstallationFailException, PermissionOperatingSystemExpection;

	public abstract JDK getCurrentJDK() throws Exception;

	public void verifyIfConfigured(List<JDK> jdks) {
	    for (JDK jdk : jdks) {
	        if(!getJDKInstalled().isEmpty()){
	    	String name = getPathnameOrNameOfJDK(jdk.getPath());
	    	if(name !=null && !name.isEmpty()){
	    	    jdk.setName(name);
	    	    jdk.setInstalled(true);
	    	}
	        }
	    }
	}

	public String getPathnameOrNameOfJDK(String jdk) {
	    try {
	        return getPropertyValueOnConfigFile(jdk, getFileConfig());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public void initSysTray() throws InstallationFailException {
	    try {
	       /* String installationDir = getInstallationDir();
	        ProcessBuilder processBuilder = new ProcessBuilder("\"" + installationDir + "\\sysTray.exe\"");
	        processBuilder.start();*/
		new JTrayIconUI().show();
	        log.append("[INFO] Systray iniciado!\n");
	    } catch (Exception e) {
	        log.append("[ERRO] Erro na inicialização do aplicativo\n");
	        throw new InstallationFailException();
	    }
	}

	public List<JDK> loadJDKConfigured() throws Exception {
	    List<JDK> jdks = new ArrayList<JDK>();
	    File fileConfig = getFileConfig();
	    List<String> lines = FileUtils.readLines(fileConfig);
	    for (String line : lines) {
	        String[] split = line.split("=");
	        JDK jdk = new JDK(split[0], split[1]);
	        jdk.setInstalled(true);
		jdks.add(jdk);
	        
	    }
	    return jdks;
	}

	public abstract void update(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
		InstallationDirectoryFaultException, PermissionOperatingSystemExpection ;

	
}
