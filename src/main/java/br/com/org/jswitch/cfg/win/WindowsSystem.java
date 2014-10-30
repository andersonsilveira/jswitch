package br.com.org.jswitch.cfg.win;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTextPane;

import org.apache.commons.io.FileUtils;

import br.com.org.jswitch.cfg.OperatingSystem;
import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.model.JDK;

/**
 * Class has a several method related to Windows System. 
 * 
 * @author Anderson
 *
 */
public class WindowsSystem extends OperatingSystem {
    
    private StringBuilder log = new StringBuilder();

    @Override
    public List<JDK> loadDefaultJDK() throws LoadDefaultJDKException, DefautJDKInstalledNotFoundException {
	List<JDK> jdks = null;
	try {
	    File file = new File("load.bat");
	    file.createNewFile();
	    FileWriter fileWriter = new FileWriter(file);
	    fileWriter.write(Command.FIND_JAVA_PATH);
	    fileWriter.flush();
	    fileWriter.close();
	    ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "load.bat");
	    List<String> javaInstalations = getDiretoryOfJavaInstalations(pb);
	    jdks = new ArrayList<JDK>();
	    for (String javaDirectory : javaInstalations) {
		javaDirectory = javaDirectory.trim();
		String[] pathSplited = javaDirectory.split("\\\\");
		StringBuilder path = new StringBuilder();
		String name = pathSplited[pathSplited.length - 3];
		for (String part : pathSplited) {
		    if (!part.equalsIgnoreCase("bin") && !part.equalsIgnoreCase("javac.exe")) {
			path.append(part).append("\\");
		    }
		}
		jdks.add(new JDK(name, path.substring(0, path.length() - 1).toString()));
	    }
	    file.delete();
	} catch (Exception e) {
	   throw new LoadDefaultJDKException();
	}
	
	if(jdks.isEmpty()){
	    throw new DefautJDKInstalledNotFoundException();
	}

	return jdks;
    }

    private List<String> getDiretoryOfJavaInstalations(ProcessBuilder pb) throws IOException {
	List<String> directories = new ArrayList<String>();
	InputStream is = null;
	ByteArrayOutputStream baos = null;
	try {
	    Process prs = pb.start();
	    is = prs.getInputStream();
	    byte[] b = new byte[1024];
	    int size = 0;
	    baos = new ByteArrayOutputStream();
	    while ((size = is.read(b)) != -1) {
		baos.write(b, 0, size);
	    }
	    String[] dirs = new String(baos.toByteArray()).split("\\r?\\n");
	    for (int i = 0; i < dirs.length; i++) {
		String dir = dirs[i];
		if (dir.contains("\\bin\\javac.exe")) {
		    directories.add(dir);
		}
	    }
	} catch (IOException e) {
	    throw e;
	} finally {
	    try {
		if (is != null)
		    is.close();
		if (baos != null)
		    baos.close();
	    } catch (Exception ex) {
	    }
	}
	return directories;
    }

    @Override
    public void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException, InstallationDirectoryFaultException, PermissionOperatingSystemExpection  {
    if(jdks!=null && !jdks.isEmpty()){
    	configureConfigFiles(jdks);
    	configureContextMenu(jdks);
    	registerBootstrp();
    	initSysTray();
    }else{
	jTextPane.setText("Erro durante a instalação, selecione pelo menos um diretório de instalação da JDK.\nPara isso use o botão \"Carregar\"");
	throw new InstallationDirectoryFaultException();

    }
    jTextPane.setText(log.toString());
    }

    @Override
    public void registerBootstrp() throws InstallationFailException, PermissionOperatingSystemExpection {
	File file = new File("command.bat");
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
	log.append("[ERRO] Erro na permissção de operação no sistema operacional");
	    throw e;
	} finally {
		file.delete();
	}
    }

    private void configureContextMenu(List<JDK> jdks) throws InstallationFailException,
	    PermissionOperatingSystemExpection {
	File file = createCommandForRegistry(jdks, "command.bat", Command.REGISTRY);
    	try {
    		processCommand();
    		
    	} catch (IOException e) {
    	    	log.append("[ERRO] Problemas na instalação do programa");
    	    	throw new InstallationFailException();
    	} catch (InterruptedException e) {
    	    log.append("[ERRO] Problemas na instalação do programa");
    	    throw new InstallationFailException();
    	} catch (PermissionOperatingSystemExpection e) {
    	log.append("[ERRO] Erro na permissção de operação no sistema operacional");
	    throw e;
	} finally {
    		file.delete();
    	}
    }
    
    private File createCommandForRegistry(List<JDK> jdks, String fileName, String command) {
	File file = new File(fileName);
	try {
	    FileWriter fileWriter = new FileWriter(file);
	    for (JDK jdk : jdks) {
		file.createNewFile();
		fileWriter.write(MessageFormat.format(command, BatchStringScapeUtils.escape(jdk.getName()), BatchStringScapeUtils.escape(jdk.getPath())));
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
	    copySysTrayToProgramFiles();
	    createFileConfig(jdks);
	} catch (Exception e1) {
	    log.append("[ERRO] Problemas ao criar arquivos de configuração\n");
	    throw new InstallationFailException();
	}
    }

    @Override
    public void change(String jdk) throws IOException {
	ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", MessageFormat.format(
		Command.RESTORE_JAVA_HOME, "", getPathnameOfJDK(jdk)));
	processBuilder.start();
    }

    private String getPathnameOfJDK(String jdk) {
	try {
	    return getPropertyValueOnConfigFile(jdk, getFileConfig());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public String getPropertyValueOnConfigFile(String jdk, File file) throws Exception {
	FileReader fileReader = new FileReader(file);
	BufferedReader br = new BufferedReader(fileReader);
	String line;
	String result = null;
	while ((line = br.readLine()) != null) {
	    String name = line.split("=")[0];
	    String value = line.split("=")[1];
	    if (name.equalsIgnoreCase(jdk)) {
		result = value.trim();
	    }
	}
	br.close();
	fileReader.close();
	return result;
    }

    @Override
    public List<String> getJDKInstalled() {

	Set<String> ret = new HashSet<String>();
	try {
	    FileReader fileReader = getFileReaderConfig();
	    BufferedReader br = new BufferedReader(fileReader);
	    String line;

	    while ((line = br.readLine()) != null) {
		if (!line.contains(PROPERTY_SELECTED_JDK)) {
		    ret.add(line.split("=")[0]);
		}
	    }
	    fileReader.close();
	} catch (Exception e) {
	    return new ArrayList<String>();
	}
	return new ArrayList<String>(ret);
    }

    private FileReader getFileReaderConfig() throws Exception, FileNotFoundException {
	File fileConfig = getFileConfig();
	FileReader fileReader = new FileReader(fileConfig);
	return fileReader;
    }

    private void createFileConfig(List<JDK> jdks) throws Exception {
	File file = getFileConfig();
	verify(file);
	log.append("[INFO] Arquivo "+file.getName()+" criado\n");
	File fileSelectedConfig = getFileSelectedConfig();
	log.append("[INFO] Arquivo "+fileSelectedConfig.getName()+" criado\n");
	FileUtils.write(fileSelectedConfig, PROPERTY_SELECTED_JDK + "=\r\n");
	FileWriter fileWriter = new FileWriter(file);
	for (JDK jdk : jdks) {
	    fileWriter.write(jdk.getName() + "=" + jdk.getPath() + "\r\n");
	}
	fileWriter.flush();
	fileWriter.close();
    }

    private void verify(File file) throws IOException {
	if (file.exists()) {
	    file.delete();
	} else {
	    file.createNewFile();
	}
    }

    private void initSysTray() throws InstallationFailException  {
	try {
	    String installationDir = getInstallationDir();
	    ProcessBuilder processBuilder = new ProcessBuilder("\"" + installationDir + "\\sysTray.exe\"");
	    processBuilder.start();
	    log.append("[INFO] Systray iniciado!\n");
	} catch (Exception e) {
	    log.append("[ERRO] Erro na inicialização do aplicativo\n");
	    throw new InstallationFailException();
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

    private void copySysTrayToProgramFiles() throws Exception {
	String installPathname = getInstallationDir();
	File dest = new File(installPathname + File.separator + "sysTray.exe");
	dest.createNewFile();
	copyFileUsingChannel(new File(Command.INSTALL_JSWITCH_EXE), dest);
    }

    @Override
    public String getInstallationDir() throws Exception {
	ProcessBuilder pFindProgramFiles = new ProcessBuilder("cmd", "/C", "echo %programFiles%");
	Process findProgramFiles = pFindProgramFiles.start();
	String pathname = outputProcess(findProgramFiles);
	String installPathname = pathname.trim() + File.separator + "JSwitch";
	File dir = new File(installPathname);
	if (!dir.exists()) {
	    dir.mkdir();
	}
	return installPathname;
    }


    private void copyFileUsingChannel(File source, File dest) throws IOException {
	FileChannel sourceChannel = null;
	FileChannel destChannel = null;
	try {
	    sourceChannel = new FileInputStream(source).getChannel();
	    destChannel = new FileOutputStream(dest).getChannel();
	    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	} finally {
	    sourceChannel.close();
	    destChannel.close();
	}
    }

    private String outputProcess(Process prs) throws IOException {
	InputStream is = null;
	ByteArrayOutputStream baos = null;
	String result = null;
	try {
	    is = prs.getInputStream();
	    byte[] b = new byte[1024];
	    int size = 0;
	    baos = new ByteArrayOutputStream();
	    while ((size = is.read(b)) != -1) {
		baos.write(b, 0, size);
	    }
	    result = new String(baos.toByteArray());
	    System.out.println(result);

	} catch (IOException e) {
	   throw e;
	} finally {
	    try {
		if (is != null)
		    is.close();
		if (baos != null)
		    baos.close();
	    } catch (Exception ex) {
	    }
	}
	return result;
    }

    @Override
    public String getInitialDirectoryChooser() {
	return "C:/";
    }

    @Override
    public void setPropertyValueOnFile(String string, String jdkname, File file) {
	try {
	    List<String> lines = FileUtils.readLines(file);
	    lines.set(0, string + "=" + jdkname);
	    FileUtils.writeLines(file, lines);

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
