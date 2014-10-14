package br.com.org.jswitch;

import br.com.org.jswitch.control.OperationSystemManager;

public class ChangeJDKMain {

	public static void main(String[] args) throws Exception {
		OperationSystemManager operationSystemManager = new OperationSystemManager();
		operationSystemManager.changeJDKOnFileConfig(args[0]);
	}

}
