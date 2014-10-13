package br.com.org.jswitch.cfg.win;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.OperationSystem;
import br.com.org.jswitch.model.JDK;

public class WindowsSystem extends OperationSystem {

    private static final String FIND_JAVA_PATH = "cd %programFiles% \n" + "dir javac.exe /B /S";

    private static final String SLASHDOT = "\\";
    private static final String QUOTE = "\"";
    public static final String RESTORE_JAVA_HOME = "setx JAVA_HOME \"{1}\" /m";

    public static final String CREATE_FILE_CONF="cd /d %ProgramFiles%\n"
    	    + "cd JSwitch\n" 
	    + "if exist config.cfg (\n"
	    + "type nul> text.txt)\n"
	    + "echo {0}>>config.cfg\n";
    
    private static final String REGISTRY = "cd /d %ProgramFiles%\n" 
	    + "if not exist JSwitch (\n"
	    + "mkdir JSwitch)\n"
	    + "cd JSwitch\n"
	    + "if not exist {0}.bat (\n" + "echo "
	    + RESTORE_JAVA_HOME
	    + ">>{0}.bat\n"
	    + "echo echo off>>{0}.bat\n"
	    + "echo echo Setting JAVA_HOME>>{0}.bat\n"
	    + "echo set JAVA_HOME={1}>>{0}.bat\n"
	    + "echo echo setting PATH>>{0}.bat\n"
	    + "echo set PATH=%%JAVA_HOME%%\\bin;%%PATH%%>>{0}.bat\n"
	    + "echo echo Display java version>>{0}.bat\n"
	    + "echo java -version>>{0}.bat)\n"
	    + "reg add HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\{0}\\command  /d \"cmd /k "
	    + SLASHDOT
	    + QUOTE
	    + "%ProgramFiles%\\JSwitch\\{0}"
	    + SLASHDOT
	    + QUOTE
	    + QUOTE
	    + " /f\n"
	    + "reg add HKEY_CLASSES_ROOT\\Folder\\shell\\{0}\\command /d \"cmd /k "
	    + SLASHDOT
	    + QUOTE
	    + SLASHDOT
	    + QUOTE
	    + "%ProgramFiles%\\JSwitch\\{0}"
	    + SLASHDOT
	    + QUOTE
	    + " "
	    + SLASHDOT
	    + QUOTE
	    + "%%1"
	    + SLASHDOT
	    + QUOTE
	    + SLASHDOT
	    + QUOTE
	    + QUOTE
	    + " /f\n"
	    + "reg add HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"JSwitchSysTray\" /d "
	    + QUOTE
	    + SLASHDOT
	    + QUOTE
	    + "%ProgramFiles%\\JSwitch\\sysTray.exe"
	    + SLASHDOT
	    + QUOTE
	    + QUOTE + " /f\n";

    @Override
    public List<JDK> loadDefaultJDK() {
	List<JDK> jdks = null;
	try {
	    File file = new File("load.bat");
	    file.createNewFile();
	    FileWriter fileWriter = new FileWriter(file);
	    fileWriter.write(FIND_JAVA_PATH);
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
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return jdks;
    }

    private List<String> getDiretoryOfJavaInstalations(ProcessBuilder pb) {
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
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
    public void install(List<JDK> jdks, JTextPane jTextPane) throws Exception {
	copySysTrayToProgramFiles();
	createFileConfig(jdks,jTextPane);
	File file = createCommandForRegistry(jdks, "command.bat", REGISTRY);
	try {
	    executeCommand(jTextPane);

	} catch (IOException e) {
	    throw e;
	} catch (InterruptedException e) {
	   throw e;
	} finally {
	    file.delete();
	}
	initSysTray(jTextPane);

    }

    private void createFileConfig(List<JDK> jdks, JTextPane jTextPane) throws IOException {
	String installationDir = getInstallationDir();
	File file = new File(installationDir+File.separator+"config.cfg");
	if(file.exists()){
	    file.delete();
	}else{
	    file.createNewFile();
	}
	FileWriter fileWriter = new FileWriter(file);
	for (JDK jdk : jdks) {
	    fileWriter.write(jdk.getName()+"\r\n");
	}
	fileWriter.flush();
	fileWriter.close();
    }

    private void initSysTray(JTextPane jTextPane) throws Exception {
	try {
	    String installationDir = getInstallationDir();
	    ProcessBuilder processBuilder = new ProcessBuilder("\"" + installationDir
		    + "\\sysTray.exe\"");
	    processBuilder.start();
	    String text = jTextPane.getText();
	    jTextPane.setText(text + "Starting systray!");
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null, "Erro durante a inicialização do aplicativo!",
		    "JSwitch", JOptionPane.ERROR_MESSAGE);
	    throw e;
	}
    }

    private void executeCommand(JTextPane jTextPane) throws IOException, InterruptedException {
	ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", "command.bat");
	Process process = processBuilder.start();
	String result = outputProcess(process);
	jTextPane.setText(result);
	process.getErrorStream().close();
	process.getOutputStream().close();
	int exitValue = process.waitFor();
	System.out.println(exitValue);
	if (exitValue != 0) {
	    JOptionPane.showMessageDialog(null,
		    "Erro na instalação, verifique as permissões de UAC!", "JSwitch",
		    JOptionPane.ERROR_MESSAGE);
	}
    }

    private void copySysTrayToProgramFiles() throws IOException {
	String installPathname = getInstallationDir();
	File dest = new File(installPathname + File.separator + "sysTray.exe");
	dest.createNewFile();
	copyFileUsingChannel(new File("setup.exe"), dest);
    }

    private String getInstallationDir() throws IOException {
	ProcessBuilder pFindProgramFiles = new ProcessBuilder("cmd", "/C", "echo %programFiles%");
	Process findProgramFiles = pFindProgramFiles.start();
	String pathname = outputProcess(findProgramFiles);
	String installPathname = pathname.trim() + File.separator + "JSwitch";
	File dir = new File(installPathname);
	if(!dir.exists()){
		dir.mkdir();
	}
	return installPathname;
    }

    private File createCommandForRegistry(List<JDK> jdks, String fileName, String command) {
	File file = new File(fileName);
	try {
	    FileWriter fileWriter = new FileWriter(file);
	    for (JDK jdk : jdks) {
		file.createNewFile();
		fileWriter.write(MessageFormat.format(command, jdk.getName(), jdk.getPath()));
	    }
	    fileWriter.flush();
	    fileWriter.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return file;
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
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

    private String outputProcess(Process prs) {
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
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

}
