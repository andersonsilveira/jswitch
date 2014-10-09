package br.com.org.jswitch.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.DirectoryChooser;
import br.com.org.jswitch.cfg.JDKLoader;
import br.com.org.jswitch.cfg.JDKMenuContextCreator;
import br.com.org.jswitch.cfg.win.JDKWindowChooseDirectory;
import br.com.org.jswitch.cfg.win.JDKWindowsLoader;
import br.com.org.jswitch.cfg.win.JDKWindowsMenuContextCreator;
import br.com.org.jswitch.model.JDK;

/**
 * 
 * @author Anderson
 *
 */
public final class OperationSystemManager {

	
	Map<Platform, JDKLoader> mapOfLoader = new HashMap<Platform, JDKLoader>();
	Map<Platform, JDKMenuContextCreator> mapOfContextMenuExecuter = new HashMap<Platform, JDKMenuContextCreator>();
	Map<Platform, DirectoryChooser> mapOfDirectoryChooser = new HashMap<Platform, DirectoryChooser>();
	
	
	public OperationSystemManager() {
		mapOfLoader.put(Platform.Windows, new JDKWindowsLoader());
		mapOfContextMenuExecuter.put(Platform.Windows, new JDKWindowsMenuContextCreator());
		mapOfDirectoryChooser.put(Platform.Windows, new JDKWindowChooseDirectory());
	}
	
	public List<JDK> loadJDKInstalled(){
		return mapOfLoader.get(getPlatform()).load();
	}
	
	public void createMenuContext(List<JDK> jdks, JTextPane jTextPane){
		mapOfContextMenuExecuter.get(getPlatform()).execute(jdks, jTextPane);
	}
	
	public JDK chooseDirectory(){
		return mapOfDirectoryChooser.get(getPlatform()).choose();
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
