/**
 * 
 */
package br.com.org.jswitch;

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
					"O Systema operacional não esta com a variável JAVA_HOME configurada, por favor faça a configuração antes de iniciar o aplicativo",
					"JSwitch", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"O JSwicht ocorreu um erro durante a inicialização do aplicativo",
					"JSwitch", JOptionPane.ERROR_MESSAGE);
			
		}

		
	}

}
