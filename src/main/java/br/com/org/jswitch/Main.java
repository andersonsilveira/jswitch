/**
 * 
 */
package br.com.org.jswitch;

import javax.swing.JOptionPane;

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
				new JSwitchUI().montaTela();				
			}else{
				new JSwitchSystemTray().execute();				
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"O JSwicht ocorreu um erro durante a inicialização do aplicativo",
					"JSwitch", JOptionPane.ERROR_MESSAGE);
		}

		
	}

}
