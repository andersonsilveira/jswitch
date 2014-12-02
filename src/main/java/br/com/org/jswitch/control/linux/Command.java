package br.com.org.jswitch.control.linux;

final class Command {

	static final String INSTALL_ENV = "cd $HOME\n"
			+ "echo JAVA_HOME={0} >> .jswitchrc\n"
			+ "echo PATH=\\$JAVA_HOME/bin:\\$PATH export PATH JAVA_HOME >> .jswitchrc\n"
			+ "echo CLASSPATH=\\$JAVA_HOME/lib/tools.jar >> .jswitchrc\n" 
			+ "echo CLASSPATH=.:\\$CLASSPATH >> .jswitchrc\n"
			+ "echo export PATH JAVA_HOME CLASSPATH >> .jswitchrc\n"
			+ "echo . ~/.jswitchrc >> .bashrc";
	
	static final String LIB_JSWITCH = "lib/jswitch-dist.jar";

	static final String BOOTSTP = "cd $HOME\n"
			+ "echo . ~/JSwitch/run >> .profile";
}
