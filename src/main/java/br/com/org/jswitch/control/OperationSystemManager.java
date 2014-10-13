package br.com.org.jswitch.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.OperationSystem;
import br.com.org.jswitch.cfg.SystemTrayConfig;
import br.com.org.jswitch.cfg.win.WindowsSystem;
import br.com.org.jswitch.cfg.win.WindowsSystemTrayConfig;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public final class OperationSystemManager {

	
	Map<Platform, SystemTrayConfig> mapOfSysTray = new HashMap<Platform, SystemTrayConfig>();
	
	Map<Platform, OperationSystem> mapOfOperation = new HashMap<Platform, OperationSystem>();
	
	
	
	
	public OperationSystemManager() {
		mapOfSysTray.put(Platform.Windows, new WindowsSystemTrayConfig());
		mapOfOperation.put(Platform.Windows, new WindowsSystem());
	}
	
	public List<JDK> loadJDKInstalled(){
		return mapOfOperation.get(getPlatform()).loadDefaultJDK();
	}
	
	public boolean isAlreadyInstalled(){
		return !getSystemTrayConfig().getJDKInstalled().isEmpty();
	}
	
	public void install(List<JDK> jdks, JTextPane jTextPane) throws Exception{
		try {
			mapOfOperation.get(getPlatform()).install(jdks, jTextPane);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public JDK chooseDirectory(){
		return mapOfOperation.get(getPlatform()).chooseDirectoryOfJDKInstalation();
	}
	
	public SystemTrayConfig getSystemTrayConfig() {
		return mapOfSysTray.get(getPlatform());
		
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
