package br.com.org.jswitch.control.win;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * Class has a several sys commands related to Windows System.
 * 
 * @author Anderson
 *
 */
public class WindowsSystem extends OperatingSystem {

    Logger logger = Logger.getLogger(WindowsSystem.class);
    
    @Override
    public List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException {
	List<JDK> jdks = null;
	try {
	    logger.info("Initing searching default jdk on system");
	    File file = new File("load.bat");
	    file.createNewFile();
	   
	    logger.info("'load.bat' was created to execute command");
	    FileWriter fileWriter = new FileWriter(file);
	    fileWriter.write(Command.FIND_JAVA_PATH);
	    fileWriter.flush();
	    fileWriter.close();
	    List<String> commands = new ArrayList<String>();
	    commands.add("cmd");
	    commands.add("/C");
	    commands.add("load.bat");
	   
	    logger.info("begin execute command...");
	    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	    commandExecutor.executeCommand();
	    StringBuilder standardOutputFromCommand = commandExecutor.getStandardOutputFromCommand();
	    
	    logger.info("end execute command...");
	    List<String> directories = getJavaDirectories(standardOutputFromCommand);
	   
	    jdks = new ArrayList<JDK>();
	    for (String javaDirectory : directories ) {
		
		logger.info("found directory :"+javaDirectory);
		javaDirectory = javaDirectory.trim();
		String[] pathSplited = javaDirectory.split("\\\\");
		StringBuilder path = new StringBuilder();
		String name = pathSplited[pathSplited.length - 3];
		
		logger.info("verifing if java directory");
		for (String part : pathSplited) {
		    if (!part.equalsIgnoreCase("bin") && !part.equalsIgnoreCase("javac.exe")) {
			path.append(part).append("\\");
		    }
		}
		logger.info("directory ok!");
		jdks.add(new JDK(name, path.substring(0, path.length() - 1).toString()));
	    }
	    file.delete();
	} catch (Exception e) {
	    logger.error("Problem during searching jdk direcotry",e);
	    throw new LoadDefaultJDKException();
	}

	if (jdks.isEmpty()) {
	    logger.info("Not found any directory deafult, please add manualy");
	    throw new DefautJDKInstalledNotFoundException();
	}
	logger.info("End searching jdk installed. The amount was "+jdks.size()+" directories");
	return jdks;
    }

    private List<String> getJavaDirectories(StringBuilder standardOutputFromCommand) {
	String[] dirs = standardOutputFromCommand.toString().split("\\r?\\n");
	List<String> directories = new ArrayList<String>();
	for (int i = 0; i < dirs.length; i++) {
	String dir = dirs[i];
	if (dir.contains(File.separator+"bin"+File.separator+"javac")) {
		directories.add(dir);
	}
	}
	return directories;
    }

    @Override
    public void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
	logger.info("starting of configuration of jdks");
	if (jdks != null && !jdks.isEmpty()) {
	    configureConfigFiles(jdks);
	    configureContextMenu(jdks);
	    registerBootstrp();
	    // createShortcut();
	} else {
	    logger.error("Problem during configuration, list of jdks is empty");
	    jTextPane
		    .setText(log.toString());
	    throw new InstallationDirectoryFaultException();

	}
	logger.info("end of configuration of jdks");
	jTextPane.setText(log.toString());
    }

    @Override
    public void update(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection {
	try {
	    if (jdks != null && !jdks.isEmpty()) {
		createFileConfig(jdks);
		configureContextMenu(jdks);
	    } else {
		jTextPane
			.setText(log.toString());
		throw new InstallationDirectoryFaultException();

	    }
	} catch (Exception e) {
	    jTextPane
		    .setText(log.toString());
	    throw new InstallationDirectoryFaultException();
	}
	jTextPane.setText(log.toString());
    }

    @Override
    public void registerBootstrp() throws InstallationFailException, PermissionOperatingSystemExpection {
	File file = new File("command.bat");
	logger.info("starting registry of bootstrp on windows");
	try {

	    FileWriter fileWriter = new FileWriter(file);
	    file.createNewFile();
	    fileWriter.write(Command.BOOTSTP);
	    fileWriter.flush();
	    fileWriter.close();
	    processCommand();
	} catch (IOException e) {
	    log.append("[ERRO] Problemas na instalação do programa");
	    throw new InstallationFailException();
	} catch (InterruptedException e) {
	    log.append("[ERRO] Problemas na instalação do programa");
	    throw new InstallationFailException();
	} catch (PermissionOperatingSystemExpection e) {
	    log.append("[ERRO] Erro na permissão de operação no sistema operacional");
	    throw e;
	} finally {
	    logger.info("deleting file command of bootstrp");
	    file.delete();
	}
	logger.info("end of registry bootstrp on windows");
    }

    private void configureContextMenu(List<JDK> jdks) throws InstallationFailException,
	    PermissionOperatingSystemExpection {
	logger.info("creating contextmenu");
	File file = createCommandForRegistry(jdks, "command.bat", Command.REGISTRY);
	try {
	    processCommand();

	} catch (IOException e) {
	    logger.error("problem durring creation of contextmenu",e);
	    log.append("[ERRO] Problemas na instalação do programa");
	    throw new InstallationFailException();
	} catch (InterruptedException e) {
	    logger.error("problem durring creation of contextmenu",e);
	    log.append("[ERRO] Problemas na instalação do programa");
	    throw new InstallationFailException();
	} catch (PermissionOperatingSystemExpection e) {
	    logger.error("problem durring creation of contextmenu",e);
	    log.append("[ERRO] Erro na permissão de operação no sistema operacional");
	    throw e;
	} finally {
	    logger.info("deleting file command of contextmenu");
	    file.delete();
	}
	logger.info("end creation of contextmenu");
    }

    private File createCommandForRegistry(List<JDK> jdks, String fileName, String command) {
	File file = new File(fileName);
	try {
	    FileWriter fileWriter = new FileWriter(file);
	    for (JDK jdk : jdks) {
		if (!jdk.getInstalled()) {
		    file.createNewFile();
		    fileWriter.write(MessageFormat.format(command, BatchStringScapeUtils.escape(jdk.getName()),
			    BatchStringScapeUtils.escape(jdk.getPath())));
		}
	    }
	    fileWriter.flush();
	    fileWriter.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return file;
    }

    private void configureConfigFiles(List<JDK> jdks) throws InstallationFailException {
	try {
	    createProgramFiles();
	    createFileConfig(jdks);
	} catch (Exception e1) {
	    log.append("[ERRO] Problemas ao criar arquivos de configuração\n");
	    throw new InstallationFailException();
	}
    }

    @Override
    public void change(String newJDK) throws ChangeJDKFailException {
	logger.info("jdk was changed by "+newJDK);
	ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", MessageFormat.format(Command.RESTORE_JAVA_HOME,
		"", getPathnameOrNameOfJDK(newJDK)));
	int exitValue = 0;
	try {
	    Process process = processBuilder.start();
	    exitValue = process.waitFor();
	} catch (InterruptedException e) {
	    logger.error("error during try change jdk ",e);
	    throw new ChangeJDKFailException();
	} catch (IOException e) {
	    logger.error("error during try change jdk ",e);
	    throw new ChangeJDKFailException();
	}
	if (exitValue != 0) {
	    logger.error("error during try change jdk ");
	    throw new ChangeJDKFailException();
	}

    }

    private void processCommand() throws PermissionOperatingSystemExpection, IOException, InterruptedException {
	ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", "command.bat");
	Process process = processBuilder.start();
	String result = outputProcess(process);
	log.append("[INFO] " + result + "\n");
	process.getErrorStream().close();
	process.getOutputStream().close();
	int exitValue = process.waitFor();
	System.out.println(exitValue);
	if (exitValue != 0) {
	    throw new PermissionOperatingSystemExpection();
	}
    }

    private void createProgramFiles() throws Exception {
	logger.info("starting creation of run.bat file on programFiles");
	String installPathname = getInstallationDirName();
	String pathnamejar = installPathname + File.separator + JSWITCH_JAR;
	File dest = new File(pathnamejar);
	dest.createNewFile();
	FileWriter fileWriter = new FileWriter(installPathname + File.separator + "run.bat");
	String javaHomeEnv = getJavaHomeEnv();
	fileWriter.write("@echo off\n");
	fileWriter.write("start \"\" \"" + javaHomeEnv.trim() + "\\bin\\javaw\" -jar \"" + pathnamejar.trim() + "\"");
	fileWriter.flush();
	fileWriter.close();
	logger.info("The run.bat was created");
	logger.info("copying jswitch.jar to programFiles ");
	copyFileUsingChannel(new File(Command.INSTALL_JSWITCH_EXE), dest);
	logger.info("end of copy and create file to programFiles");
    }

    @Override
    public String getInstallationDirName() throws Exception {
	logger.info("get directory installation");
	List<String> commands = new ArrayList<String>();
	commands.add("cmd");
	commands.add("/C");
	commands.add("echo %programFiles%");

	SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	if (commandExecutor.executeCommand() == 1) {
	    throw new IllegalStateException();
	}

	String pathname = commandExecutor.getStandardOutputFromCommand().toString();
	String installPathname = pathname.trim() + File.separator + "JSwitch";
	File dir = new File(installPathname);
	if (!dir.exists()) {
	    dir.mkdir();
	}
	logger.info("get directory installation is "+installPathname);
	return installPathname;
    }

    @Override
    public String getInitialDirectoryChooser() {
	return "C:/";
    }

    @Override
    public JDK getCurrentJDK() throws JavaHomeVariableSystemNotFoundException {
	try {
	    logger.info("get current JDK on system");
	    String pathname = getJavaHomeEnv();
	    String name = getPropertyValueOnConfigFile(pathname.trim(), getFileConfig());
	    return new JDK(name, pathname.trim());
	} catch (IOException e) {
	    logger.error("error on get current JDK ",e);
	    throw new IllegalStateException();
	} catch (Exception e) {
	    logger.error("error on get current JDK ",e);
	    throw new IllegalStateException();
	}
    }

    private String getJavaHomeEnv() throws Exception, JavaHomeVariableSystemNotFoundException {
	logger.info("get JAVA_HOME env on system");
	List<String> commands = new ArrayList<String>();
	commands.add("cmd");
	commands.add("/C");
	commands.add("echo %JAVA_HOME%");

	SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	if (commandExecutor.executeCommand() == 1) {
	    throw new IllegalStateException();
	}
	String pathname = commandExecutor.getStandardOutputFromCommand().toString();// outputProcess(findProgramFiles);
	if (pathname == null || pathname.isEmpty()) {
	    logger.error("JAVA_HOME not found");
	    throw new JavaHomeVariableSystemNotFoundException();
	}
	logger.info("JAVA_HOME = "+pathname);
	return pathname;
    }

    /*
     * private void createShortcut() throws InstallationFailException {
     * List<String> commands = new ArrayList<String>(); commands.add("cmd");
     * commands.add("/C"); commands.add(
     * "lib\\Shortcut.exe /f:\"%USERPROFILE%\\Desktop\\JSwitch.lnk\" /a:c /t:%programfiles%\\JSwitch\\run.bat"
     * );
     * 
     * // execute the command SystemCommandExecutor commandExecutor = new
     * SystemCommandExecutor( commands); try { int result =
     * commandExecutor.executeCommand(); if (result == 1) { //throw new
     * InstallationFailException(); } // get the stdout and stderr from the
     * command that was run StringBuilder stdout = commandExecutor
     * .getStandardOutputFromCommand(); StringBuilder stderr = commandExecutor
     * .getStandardErrorFromCommand();
     * 
     * // print the stdout and stderr
     * System.out.println("The numeric result of the command was: " + result);
     * System.out.println("STDOUT:"); System.out.println(stdout);
     * System.out.println("STDERR:"); System.out.println(stderr); } catch
     * (IOException e) { throw new IllegalStateException(); } catch
     * (InterruptedException e) { throw new IllegalStateException(); }
     * 
     * }
     */
}
