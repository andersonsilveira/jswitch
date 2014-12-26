package br.com.org.jswitch.control;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.log4j.Logger;

import br.com.org.jswitch.cfg.exception.ChangeJDKFailException;
import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.DirectoryChooseNotValidException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.win.WindowsSystem;
import br.com.org.jswitch.model.JDK;

/**
 * Abstraction of operating system, to realize the specific O.S is mandatory
 * extends this class
 * 
 * @author Anderson
 *
 */
public abstract class OperatingSystem {

    Logger logger = Logger.getLogger(WindowsSystem.class);

    public static final String SELECTED = ".selected";
    public static final String CONFIG_CFG = "config.cfg";
    public static final String PROPERTY_SELECTED_JDK = "selectedJDK";
    protected static final String JSWITCH_JAR = "jswitch.jar";
    protected StringBuilder log = new StringBuilder();

    public abstract List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException,
	    DefautJDKInstalledNotFoundException;

    public abstract void install(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection;

    public JDK chooseDirectoryOfJDKInstalation() throws DirectoryChooseNotValidException {
	logger.info("chooser system selected");
	JFileChooser chooser = new JFileChooser(getInitialDirectoryChooser());
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	int retorno = chooser.showOpenDialog(null);

	if (retorno == JFileChooser.APPROVE_OPTION) {
	    logger.info("Directory selected");
	    File chosenDirectory = chooser.getSelectedFile();
	    String[] listDir = chosenDirectory.list(org.apache.commons.io.filefilter.DirectoryFileFilter.INSTANCE);
	    List<String> dirs = Arrays.asList(listDir);
	    if (dirs.contains("bin")) {
		logger.info("verifying if is a java directory");
		File dirBin = new File(chosenDirectory.getAbsolutePath() + File.separator + "bin");
		List<String> files = Arrays.asList(dirBin
			.list(new NameFileFilter(new String[] { "javac", "javac.exe" })));
		if (!files.isEmpty()) {
		    String path = chosenDirectory.getPath();
		    String name = chosenDirectory.getName();
		    logger.info("Is a java directory");
		    return new JDK(name, path);

		}

	    }
	}
	 logger.info("Is not a Java directory");
	throw new DirectoryChooseNotValidException();

    }

    public abstract String getInitialDirectoryChooser();

    public abstract void change(String newJDK) throws ChangeJDKFailException;

    public File getFileConfig() throws Exception {
	String pathname = getInstallationDirName();
	File fileConfig = new File(pathname + File.separator + CONFIG_CFG);
	return fileConfig;
    }

    public File getFileSelectedConfig() throws Exception {
	String pathname = getInstallationDirName();
	File fileSelected = new File(pathname + File.separator + SELECTED);
	return fileSelected;
    }

    public abstract String getInstallationDirName() throws Exception;

    public abstract void registerBootstrp() throws InstallationFailException, PermissionOperatingSystemExpection;

    public abstract JDK getCurrentJDK() throws Exception;


    public String getPathnameOrNameOfJDK(String jdk) {
	try {
	    return getPropertyValueOnConfigFile(jdk, getFileConfig());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public List<JDK> loadJDKConfigured() throws Exception {
	logger.info("loading JDK already configured");
	List<JDK> jdks = new ArrayList<JDK>();
	File fileConfig = getFileConfig();
	List<String> lines = FileUtils.readLines(fileConfig);
	for (String line : lines) {
	    logger.info("loading JDK "+line);
	    String[] split = line.split("=");
	    JDK jdk = new JDK(split[0], split[1]);
	    jdk.setInstalled(true);
	    jdks.add(jdk);

	}
	return jdks;
    }

    public abstract void update(List<JDK> jdks, JTextPane jTextPane) throws InstallationFailException,
	    InstallationDirectoryFaultException, PermissionOperatingSystemExpection;


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
	    } else if (value.equalsIgnoreCase(jdk)) {
		result = name.trim();
	    }
	}
	br.close();
	fileReader.close();
	return result;
    }

    public void setPropertyValueOnFile(String string, String jdkname, File file) {
	try {
	    List<String> lines = FileUtils.readLines(file);
	    lines.set(0, string + "=" + jdkname);
	    FileUtils.writeLines(file, lines);

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    protected void createFileConfig(List<JDK> jdks) throws Exception {
	logger.info("creating .selected and config.cfg");
	File file = getFileConfig();
	verify(file);
	log.append("[INFO] Arquivo " + file.getName() + " criado\n");
	File fileSelectedConfig = getFileSelectedConfig();
	verify(fileSelectedConfig);
	log.append("[INFO] Arquivo " + fileSelectedConfig.getName() + " criado\n");
	FileUtils.write(fileSelectedConfig, PROPERTY_SELECTED_JDK + "=\r\n");
	FileWriter fileWriter = new FileWriter(file);
	for (JDK jdk : jdks) {
	    fileWriter.write(jdk.getName() + "=" + jdk.getPath() + "\r\n");
	}
	fileWriter.flush();
	fileWriter.close();
	logger.info("end create .selected and config.cfg");
    }

    private void verify(File file) throws IOException {
	if (file.exists()) {
	    file.delete();
	} else {
	    file.createNewFile();
	}
    }

    public List<String> getJDKInstalled() throws InstallationFailException {

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
	    logger.error("error during get JDK installed",e);
	    return new ArrayList<String>();
	}
	return new ArrayList<String>(ret);
    }

    private FileReader getFileReaderConfig() throws Exception, FileNotFoundException {
	File fileConfig = getFileConfig();
	FileReader fileReader = new FileReader(fileConfig);
	return fileReader;
    }

    protected String outputProcess(Process prs) throws IOException {
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

    public void copyFileUsingChannel(File source, File dest) throws IOException {
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

}
