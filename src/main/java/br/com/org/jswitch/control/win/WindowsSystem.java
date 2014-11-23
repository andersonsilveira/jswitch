package br.com.org.jswitch.control.win;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.OperatingSystem;
import br.com.org.jswitch.model.JDK;

/**
 * Class has a several method related to Windows System.
 * 
 * @author Anderson
 *
 */
public class WindowsSystem extends OperatingSystem {

	@Override
	public List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException,
			DefautJDKInstalledNotFoundException {
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
					if (!part.equalsIgnoreCase("bin")
							&& !part.equalsIgnoreCase("javac.exe")) {
						path.append(part).append("\\");
					}
				}
				jdks.add(new JDK(name, path.substring(0, path.length() - 1)
						.toString()));
			}
			file.delete();
		} catch (Exception e) {
			throw new LoadDefaultJDKException();
		}

		if (jdks.isEmpty()) {
			throw new DefautJDKInstalledNotFoundException();
		}

		return jdks;
	}

	@Override
	public void install(List<JDK> jdks, JTextPane jTextPane)
			throws InstallationFailException,
			InstallationDirectoryFaultException,
			PermissionOperatingSystemExpection {
		if (jdks != null && !jdks.isEmpty()) {
			configureConfigFiles(jdks);
			configureContextMenu(jdks);
			registerBootstrp();
		} else {
			jTextPane
					.setText("Erro durante a instalação, selecione pelo menos um diretório de instalação da JDK.\nPara isso use o botão \"Carregar\"");
			throw new InstallationDirectoryFaultException();

		}
		jTextPane.setText(log.toString());
	}

	@Override
	public void update(List<JDK> jdks, JTextPane jTextPane)
			throws InstallationFailException,
			InstallationDirectoryFaultException,
			PermissionOperatingSystemExpection {
		if (jdks != null && !jdks.isEmpty()) {
			configureConfigFiles(jdks);
			configureContextMenu(jdks);
		} else {
			jTextPane
					.setText("Erro durante a instalação, selecione pelo menos um diretório de instalação da JDK.\nPara isso use o botão \"Carregar\"");
			throw new InstallationDirectoryFaultException();

		}
		jTextPane.setText(log.toString());
	}

	@Override
	public void registerBootstrp() throws InstallationFailException,
			PermissionOperatingSystemExpection {
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

	private void configureContextMenu(List<JDK> jdks)
			throws InstallationFailException,
			PermissionOperatingSystemExpection {
		File file = createCommandForRegistry(jdks, "command.bat",
				Command.REGISTRY);
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

	private File createCommandForRegistry(List<JDK> jdks, String fileName,
			String command) {
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file);
			for (JDK jdk : jdks) {
				if (!jdk.getInstalled()) {
					file.createNewFile();
					fileWriter.write(MessageFormat.format(command,
							BatchStringScapeUtils.escape(jdk.getName()),
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

	private void configureConfigFiles(List<JDK> jdks)
			throws InstallationFailException {
		try {
			copySysTrayToProgramFiles();
			createFileConfig(jdks);
		} catch (Exception e1) {
			log.append("[ERRO] Problemas ao criar arquivos de configuração\n");
			throw new InstallationFailException();
		}
	}

	@Override
	public void change(String newJDK) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C",
				MessageFormat.format(Command.RESTORE_JAVA_HOME, "",
						getPathnameOrNameOfJDK(newJDK)));
		processBuilder.start();
	}

	private void processCommand() throws PermissionOperatingSystemExpection,
			IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C",
				"command.bat");
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
		ProcessBuilder pFindProgramFiles = new ProcessBuilder("cmd", "/C",
				"echo %programFiles%");
		Process findProgramFiles = pFindProgramFiles.start();
		String pathname = outputProcess(findProgramFiles);
		String installPathname = pathname.trim() + File.separator + "JSwitch";
		File dir = new File(installPathname);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return installPathname;
	}

	private void copyFileUsingChannel(File source, File dest)
			throws IOException {
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

	@Override
	public String getInitialDirectoryChooser() {
		return "C:/";
	}

	@Override
	public JDK getCurrentJDK() throws JavaHomeVariableSystemNotFoundException {
		ProcessBuilder pFindProgramFiles = new ProcessBuilder("cmd", "/C",
				"echo %JAVA_HOME%");
		Process findProgramFiles;
		try {
			findProgramFiles = pFindProgramFiles.start();
			String pathname = outputProcess(findProgramFiles);
			if (pathname == null || pathname.isEmpty()) {
				throw new JavaHomeVariableSystemNotFoundException();
			}
			String name = getPropertyValueOnConfigFile(pathname.trim(),
					getFileConfig());
			return new JDK(name, pathname.trim());
		} catch (IOException e) {
			throw new IllegalStateException();
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

}
