/**
 * 
 */
package br.com.org.jswitch;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.ui.JSwitchSystemTray;
import br.com.org.jswitch.ui.JSwitchUI;
import br.com.org.jswitch.ui.JTrayIconTest;


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
				new JSwitchUI().showForInstall();				
			}else{
				new JTrayIconTest().show();				
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
