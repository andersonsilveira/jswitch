package br.com.org.jswitch.control.linux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;

import org.apache.log4j.Logger;

import br.com.org.jswitch.cfg.SystemCommandExecutor;
import br.com.org.jswitch.cfg.exception.ChangeJDKFailException;
import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.OperatingSystem;
import br.com.org.jswitch.model.JDK;

/**
 * This class is responsible to execute system commands for linux
 * 
 * @author anderson
 */
public class LinuxSystem extends OperatingSystem {

    Logger logger = Logger.getLogger(LinuxSystem.class);

    private static final String AREA_DE_TRABALHO = "Área de Trabalho";

    @Override
    public List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException {
	String fincCommand = "find /usr/local/java/ -name javac";
	ArrayList<JDK> list = new ArrayList<JDK>();
	try {
	    List<String> commands = new ArrayList<String>();
	    commands.add("bash");
	    commands.add("-c");
	    commands.add(fincCommand);
	    SystemCommandExecutor commandExecutor = executeCommand(commands);
	    StringBuilder outPut = commandExecutor.getStandardOutputFromCommand();
	    String[] dirs = outPut.toString().split("\\r?\\n");
	    for (String javaDirectory : dirs) {
		javaDirectory = javaDirectory.trim();
		String[] pathSplited = javaDirectory.split(File.separator);
		StringBuilder path = new StringBuilder();
		String name = pathSplited[pathSplited.length - 3];
		for (String part : pathSplited) {
		    if (!part.equalsIgnoreCase("bin") && !part.equalsIgnoreCase("javac")) {
			path.append(part).append(File.separator);
		    }
		}
		list.add(new JDK(name, path.substring(0, path.length() - 1).toString()));
	    }
	} catch (Exception e) {
	    throw new LoadDefaultJDKException();
	}

	if (list.isEmpty()) {
	    throw new DefautJDKInstalledNotFoundException();
	}

	return list;
    }

    @Override
    public void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
	logger.info("Starting JDK configuration on linux");
	try {
	    setEnv("Default");
	    configureFiles(jdks);
	    createShortcut();
	    copySysTrayToProgramFiles();
	    // registerBootstrp();
	} catch (Exception e) {
	    throw new InstallationFailException();
	}finally{
	    jTextPane.setText(log.toString());	    
	}
	logger.info("end of JDK configuration on linux");
    }

    public void configureFiles(List<JDK> jdks) throws Exception {
	createFileConfig(jdks);
    }

    private void copySysTrayToProgramFiles() throws Exception {
	String installPathname = getInstallationDirName();
	String pathnamejar = installPathname + File.separator + JSWITCH_JAR;
	File dest = new File(pathnamejar);
	dest.createNewFile();
	FileWriter fileWriter = new FileWriter(installPathname + File.separator + "run.sh");
	String javaHomeEnv = getJavaHomeEnv();
	fileWriter.write("\"" + javaHomeEnv.trim() + File.separator + "bin" + File.separator + "java\" -jar \""
		+ pathnamejar.trim() + "\"");
	fileWriter.flush();
	fileWriter.close();

	List<String> commands = new ArrayList<String>();
	commands.add("bash");
	commands.add("-c");
	commands.add("cd $HOME/JSwitch && chmod +x run.sh");

	executeCommand(commands);
	try {
	    copyFileUsingChannel(new File(Command.LIB_JSWITCH), dest);
	} catch (Exception e) {
	    throw new IllegalStateException();
	}
    }

    private void setEnv(String newJDK) {
	List<String> commands = new ArrayList<String>();
	commands.add("bash");
	commands.add("-c");
	String envCommandBashrc = MessageFormat.format(Command.INSTALL_ENV, newJDK);
	commands.add(envCommandBashrc);
	commands.add("source $HOME/.jswitchrc");
	commands.add("source $HOME/.bashrc");
	log.append("[SET ENV] configurando variável de ambinente no .bashrc\n" + envCommandBashrc + "\n");
	// execute the command
	SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	try {
	    int result = commandExecutor.executeCommand();
	    // get the stdout and stderr from the command that was run
	    StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
	    StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

	    // print the stdout and stderr
	    log.append("The numeric result of the command was: " + result + "\n");
	    log.append("STDOUT: " + stdout + "\n");
	    log.append("STDERR:" + stderr + "\n");
	} catch (IOException e) {
	    throw new IllegalStateException();
	} catch (InterruptedException e) {
	    throw new IllegalStateException();
	}

    }

    @Override
    public String getInitialDirectoryChooser() {
	return File.separator;
    }

    @Override
    public void change(String newJDK) throws ChangeJDKFailException {
	try {
	    logger.info("change jdk command will be executed");
	    String changeCommand = "sed -i \"s/" + "JAVA_HOME=.*/" + "JAVA_HOME="
		    + BatchStringScapeUtils.escape(getPathnameOrNameOfJDK(newJDK)) + "/g\" $HOME/.jswitchrc";
	    List<String> commands = new ArrayList<String>();
	    commands.add("bash");
	    commands.add("-c");
	    commands.add(changeCommand);
	    commands.add("source $HOME/.jswitchrc");

	    logger.info("execute the command");
	    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	    int result = commandExecutor.executeCommand();

	    logger.info("get the stdout and stderr from the command that was run");
	    StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
	    StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

	    // print the stdout and stderr
	    logger.info("The numeric result of the command was: " + result);
	    logger.info("STDOUT:" + stdout);
	    logger.error("STDERR:" + stderr);
	} catch (InterruptedException e) {
	    logger.error("error:", e);
	    throw new IllegalStateException();
	} catch (Exception e) {
	    logger.error("error:", e);
	    throw new IllegalStateException();
	}

    }

    @Override
    public String getInstallationDirName() throws Exception {
	StringBuilder user = getUserDir();
	String installPathname = user.toString().trim() + File.separator + "JSwitch";
	File dir = new File(installPathname);
	if (!dir.exists()) {
	    dir.mkdir();
	}
	return installPathname;
    }

    public StringBuilder getUserDir() throws IOException, InterruptedException {
	logger.info("get user dir on linux");
	List<String> commands = new ArrayList<String>();
	commands.add("bash");
	commands.add("-c");
	commands.add("echo $HOME");

	SystemCommandExecutor commandExecutor = executeCommand(commands);
	StringBuilder user = commandExecutor.getStandardOutputFromCommand();
	logger.info("user = "+user);
	return user;
    }

    public void createShortcut() throws InstallationFailException {
	try {
	    logger.info("Starting shortcut");
	    String imgemTargPath = createIcon();

	    String shortcut = "[Desktop Entry]\n" + "Encoding=UTF-8\n" + "Name=Shortcut JSwitch\n"
		    + "Comment=Launch JSwich\n" + "Exec=java -jar \"" + getInstallationDirName().toString()
		    + File.separator + "jswitch.jar\"\n" + "Icon=" + imgemTargPath + "\n" + "Type=Application\n"
		    + "Name[pt_BR]=JSwitch";

	    String userDir = getUserDir().toString().trim();
	    userDir = userDir + File.separator + "Desktop" + File.separator;
	    File desktopDir = new File(userDir);
	    String homeCmd = getUserDir().toString().trim() + File.separator + "Desktop";
	    String shortcutFileName = "jswitch.desktop";
	    if (!desktopDir.exists()) {
		userDir = getUserDir().toString().trim();
		homeCmd = userDir + File.separator + AREA_DE_TRABALHO;
	    }

	    FileWriter fileWriter = new FileWriter(homeCmd + File.separator + shortcutFileName);
	    fileWriter.write(shortcut);
	    logger.info(shortcut);
	    fileWriter.flush();
	    fileWriter.close();

	    List<String> commands = new ArrayList<String>();
	    commands.add("bash");
	    commands.add("-c");
	    commands.add("cd \"" + homeCmd + "\" && chmod +x " + shortcutFileName);
	    logger.info("executing command...");
	    executeCommand(commands);

	} catch (IOException e) {
	    logger.error("error", e);
	    throw new InstallationFailException();
	} catch (InterruptedException e) {
	    logger.error("error", e);
	    throw new InstallationFailException();
	} catch (Exception e) {
	    logger.error("error", e);
	    throw new InstallationFailException();
	}
	logger.info("end shortcut");

    }

    private String createIcon() throws Exception, FileNotFoundException, IOException, InstallationFailException {
	logger.info("create icon for shortcut");
	InputStream resource = getClass().getResourceAsStream("/switch-icon.png");
	String imgemTargPath = getInstallationDirName().toString() + File.separator + "switch-icon.png";
	File imageTarget = new File(imgemTargPath);
	FileOutputStream outputStream = new FileOutputStream(imageTarget);

	int read = 0;
	byte[] bytes = new byte[1024];
	while ((read = resource.read(bytes)) != -1) {
	    outputStream.write(bytes, 0, read);
	}
	try {
	    resource.close();
	    outputStream.close();
	} catch (IOException e) {
	    logger.error("error: ",e);
	    throw new InstallationFailException();
	}
	return imgemTargPath;
    }

    @Override
    public void registerBootstrp() throws InstallationFailException, PermissionOperatingSystemExpection {
	logger.info("starting register bootstrp");
	List<String> commands = new ArrayList<String>();
	commands.add("bash");
	commands.add("-c");
	commands.add(Command.BOOTSTP_linux);
	try {
	    // execute the command
	    logger.info("execute command "+Command.BOOTSTP_linux);
	    executeCommand(commands);
	} catch (IOException e) {
	    logger.error("error: ",e);
	    throw new IllegalStateException();
	} catch (InterruptedException e) {
	    logger.error("error: ",e);
	    throw new IllegalStateException();
	}
	logger.info("end of register bootstrp");

    }

    @Override
    public JDK getCurrentJDK() throws Exception {
	try {
	    String pathname = getJavaHomeEnv();
	    String name = getPropertyValueOnConfigFile(pathname.trim(), getFileConfig());
	    return new JDK(name, pathname.trim());
	} catch (IOException e) {
	    throw new IllegalStateException();
	} catch (Exception e) {
	    throw new IllegalStateException();
	}
    }

    private String getJavaHomeEnv() throws JavaHomeVariableSystemNotFoundException {
	String pathname = "";
	try {
	    List<String> commands = new ArrayList<String>();
	    commands.add("bash");
	    commands.add("-c");
	    commands.add("echo $JAVA_HOME");
	    SystemCommandExecutor commandExecutor = executeCommand(commands);
	    StringBuilder standardOutputFromCommand = commandExecutor.getStandardOutputFromCommand();
	    pathname = standardOutputFromCommand.toString();
	    if (pathname == null || pathname.isEmpty()) {
		throw new JavaHomeVariableSystemNotFoundException();
	    }
	} catch (InterruptedException e) {
	    throw new IllegalStateException();
	} catch (IOException e) {
	    throw new IllegalStateException();
	}
	return pathname;
    }

    private SystemCommandExecutor executeCommand(List<String> commands) throws IOException, InterruptedException {
	SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	int result = commandExecutor.executeCommand();
	if (result == 1) {
	    throw new IllegalStateException();
	}
	return commandExecutor;
    }

    @Override
    public void update(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
	try {
	    configureFiles(jdks);
	} catch (Exception e) {
	    log.append("[ERRO] Problemas ao criar arquivos de configuração\n");
	    throw new InstallationFailException();
	}finally{
	    jTextPane.setText(log.toString());	 
	}

    }

}
