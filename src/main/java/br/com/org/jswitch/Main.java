/**
 * 
 */
package br.com.org.jswitch;

import javax.swing.JOptionPane;

import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.ui.JSwitchSystemTray;
import br.com.org.jswitch.ui.JSwitchUI;


/**
 * Main class to start application
 * 
 * @author Anderson
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		OperationSystemManager operationSystemManager = new OperationSystemManager();
		try {
			if(!operationSystemManager.isAlreadyInstalled()){
				new JSwitchUI().show();				
			}else{
				new JSwitchSystemTray().show();				
			}
		} 
		catch (JavaHomeVariableSystemNotFoundException e) {
			JOptionPane.showMessageDialog(null,
					"O Systema operacional n�o esta com a vari�vel JAVA_HOME configurada, por favor fa�a a configura��o antes de iniciar o aplicativo",
					"JSwitch", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"O JSwicht ocorreu um erro durante a inicializa��o do aplicativo",
					"JSwitch", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		
	}

}
