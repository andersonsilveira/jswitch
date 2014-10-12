package br.com.org.jswitch;

import br.com.org.jswitch.ui.JSwitchSystemTray;


public class SystemTrayMain {
	

	public static void main(String[] args) throws Exception {
		 Runnable r = new Runnable() {
	         public void run() {
	             try {
					new JSwitchSystemTray().execute();
					System.out.println("System Tray Created");
				} catch (Exception e) {
					e.printStackTrace();
				}
	         }
	     };

	     new Thread(r).start();
	
	}
	
	
}