/**
 * 
 */
package br.com.org.jswitch;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.control.JSwitchManager;
import br.com.org.jswitch.ui.JSwitchUI;
import br.com.org.jswitch.ui.JSwitchUI.MODE;
import br.com.org.jswitch.ui.JTrayIconUI;


/**
 * Main class to start application
 * 
 * @author Anderson
 *
 */
public class Main {

    	private static final Locale locale = Locale.getDefault();
    	private static final ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
	
	private static final String ERROR_INIT = bundle.getString("error.during.init");
	private static final String JAVA_HOME_NOT_FOUND = bundle.getString("warn.java.home.not.found"); 

	/**
	 * @param args
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		JSwitchManager operationSystemManager = new JSwitchManager();
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		try {
			if(!operationSystemManager.isAlreadyInstalled()){
				new JSwitchUI().show(MODE.INSTALL);				
			}else{
				new JTrayIconUI().show();				
			}
		} 
		catch (JavaHomeVariableSystemNotFoundException e) {
			JOptionPane.showMessageDialog(null,
					JAVA_HOME_NOT_FOUND,
					"JSwitch", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					ERROR_INIT,
					"JSwitch", JOptionPane.ERROR_MESSAGE);
			
		}

		
	}

}
