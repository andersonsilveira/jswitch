package br.com.org.jswitch.control;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.exception.ChangeJDKFailException;
import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.DirectoryChooseNotValidException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.linux.LinuxSystem;
import br.com.org.jswitch.control.win.WindowsSystem;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public final class JSwitchManager{

	
	
	Map<Platform, OperatingSystem> mapOfOperation = new HashMap<Platform, OperatingSystem>();
	
	
	
	
	public JSwitchManager() {
		mapOfOperation.put(Platform.Windows, new WindowsSystem());
		mapOfOperation.put(Platform.Unix, new LinuxSystem());
	}
	
	
	public List<JDK> loadJDKInstalledOnSystem() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException{
		return getPlatformSystem().loadDefaultJDKOnSystem();
	}
	
	public List<JDK> loadJDKConfigured() throws Exception{
		return getPlatformSystem().loadJDKConfigured();
	}
	
	public boolean isAlreadyInstalled() throws InstallationFailException{
		return !getJDKInstalled().isEmpty();
	}
	
	public void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException, InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
			getPlatformSystem().install(jdks, jTextPane);
	}
	
	public void update(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException, InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
		getPlatformSystem().update(jdks, jTextPane);
	}
	
	public JDK chooseDirectory() throws DirectoryChooseNotValidException{
		return getPlatformSystem().chooseDirectoryOfJDKInstalation();
	}
	
	

	public List<String> getJDKInstalled() throws InstallationFailException {
		return getPlatformSystem().getJDKInstalled();
	}

	public void change(String newjdkname) throws ChangeJDKFailException {
		getPlatformSystem().change(newjdkname);
		
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

	public void changeJDKOnSelectedFile(String newjdkname) throws Exception {
		File fileConfig = getFileSelected();
		getPlatformSystem().setPropertyValueOnFile(OperatingSystem.PROPERTY_SELECTED_JDK,newjdkname, fileConfig);
		
	}
	
	private OperatingSystem getPlatformSystem() {
		return mapOfOperation.get(getPlatform());
	}

	public void ajustNames(JDK newJDK,List<JDK> jdks) {
	   for (JDK jdk : jdks) {
	    if(jdk.getName().equalsIgnoreCase(newJDK.getName())){
		newJDK.setName(jdk.getName()+"(2)");
	    }
	}
	    
	}

	public void setCurrentJDKOnSystem() throws Exception {
	  JDK jdk = getCurrentJavaHomeVar();
	  changeJDKOnSelectedFile(jdk.getName());  
	}

	public JDK getCurrentJavaHomeVar() throws Exception {
		JDK jdk =  getPlatformSystem().getCurrentJDK();
		return jdk;
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
}
