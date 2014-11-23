package br.com.org.jswitch.control.linux;

final class Command {

	static final String INSTALL_ENV = "cd $HOME\n"
			+ "echo JAVA_HOME={0} >> .bashrc\n"
			+ "echo PATH=\\$JAVA_HOME/bin:\\$PATH export PATH JAVA_HOME >> .bashrc\n"
			+ "echo CLASSPATH=\\$JAVA_HOME/lib/tools.jar >> .bashrc\n" 
			+ "echo CLASSPATH=.:\\$CLASSPATH >> .bashrc\n"
			+ "echo export PATH JAVA_HOME CLASSPATH >> .bashrc\n";
}
