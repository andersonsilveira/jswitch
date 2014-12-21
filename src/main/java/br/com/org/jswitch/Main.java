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
import br.com.org.jswitch.control.OperationSystemManager;
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
	
	private static final String ERROR_INIT = bundle.getString("error.during.init");//"O JSwicht ocorreu um erro durante a inicialização do aplicativo";
	private static final String JAVA_HOME_NOT_FOUND = bundle.getString("warn.java.home.not.found"); //"O Sistema operacional não esta com a variável JAVA_HOME configurada, por favor faça a configuração antes de iniciar o aplicativo";

	/**
	 * @param args
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		OperationSystemManager operationSystemManager = new OperationSystemManager();
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
