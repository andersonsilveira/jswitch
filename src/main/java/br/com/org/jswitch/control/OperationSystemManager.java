package br.com.org.jswitch.control;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.OperationSystem;
import br.com.org.jswitch.cfg.win.WindowsSystem;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public final class OperationSystemManager{

	
	
	Map<Platform, OperationSystem> mapOfOperation = new HashMap<Platform, OperationSystem>();
	
	
	
	
	public OperationSystemManager() {
		mapOfOperation.put(Platform.Windows, new WindowsSystem());
	}
	
	public List<JDK> loadJDKInstalled(){
		return getPlatformSystem().loadDefaultJDK();
	}
	
	public boolean isAlreadyInstalled(){
		return !getJDKInstalled().isEmpty();
	}
	
	public void install(List<JDK> jdks, JTextPane jTextPane) throws Exception{
		try {
			getPlatformSystem().install(jdks, jTextPane);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public JDK chooseDirectory(){
		return getPlatformSystem().chooseDirectoryOfJDKInstalation();
	}
	
	
	public enum Platform {
		Windows,
		Mac,
		Unix,
		Solaris,
		unsupported
	}
	
	private static Platform m_os = null;
 
	
	private static Platform getPlatform() {
		if(m_os == null) {
			String os = System.getProperty("os.name").toLowerCase();
	
			                             m_os = Platform.unsupported;
			if(os.indexOf("win")   >= 0) m_os = Platform.Windows;		// Windows
			if(os.indexOf("mac")   >= 0) m_os = Platform.Mac;			// Mac
			if(os.indexOf("nux")   >= 0) m_os = Platform.Unix;			// Linux
			if(os.indexOf("nix")   >= 0) m_os = Platform.Unix;			// Unix
			if(os.indexOf("sunos") >= 0) m_os = Platform.Solaris;		// Solaris
		}
		
		return m_os;
	}

	public List<String> getJDKInstalled() {
		return getPlatformSystem().getJDKInstalled();
	}

	public void change(String jdk) throws IOException {
		getPlatformSystem().change(jdk);
		
	}
	
	public File getFileConfig() throws Exception{
		return getPlatformSystem().getFileConfig();
	}



	public String getPropertyValueOnConfigFile(String selectedJdk) throws Exception {
		return getPlatformSystem().getPropertyValueOnConfigFile(selectedJdk, getFileConfig());
	}
	
	public String getPropertyValueOnSelectedFile(String selectedJdk,File file) throws Exception {
		return getPlatformSystem().getPropertyValueOnConfigFile(selectedJdk, file);
	}

	public File getFileSelected() throws Exception {
	    return getPlatformSystem().getFileSelectedConfig();
	}

	public void changeJDKOnSelectedFile(String jdkname) throws Exception {
		File fileConfig = getFileSelected();
		getPlatformSystem().setPropertyValueOnFile(OperationSystem.PROPERTY_SELECTED_JDK,jdkname, fileConfig);
		
	}
	
	private OperationSystem getPlatformSystem() {
		return mapOfOperation.get(getPlatform());
	}


	
}
