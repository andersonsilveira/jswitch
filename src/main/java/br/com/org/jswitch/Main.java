/**
 * 
 */
package br.com.org.jswitch;

import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.ui.JSwitchSystemTray;
import br.com.org.jswitch.ui.JSwitchUI;


/**
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
