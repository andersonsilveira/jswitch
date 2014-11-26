package br.com.org.jswitch.control.linux;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;

import br.com.org.jswitch.cfg.SystemCommandExecutor;
import br.com.org.jswitch.cfg.exception.DefautJDKInstalledNotFoundException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.JavaHomeVariableSystemNotFoundException;
import br.com.org.jswitch.cfg.exception.LoadDefaultJDKException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.OperatingSystem;
import br.com.org.jswitch.model.JDK;

public class LinuxSystem extends OperatingSystem {

	@Override
	public List<JDK> loadDefaultJDKOnSystem() throws LoadDefaultJDKException,
			DefautJDKInstalledNotFoundException {
		String command = "find /usr/local/java/ -name javac";
		ArrayList<JDK> list = new ArrayList<JDK>();
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
			List<String> javaInstalations = getDiretoryOfJavaInstalations(pb);
			for (String javaDirectory : javaInstalations) {
				javaDirectory = javaDirectory.trim();
				String[] pathSplited = javaDirectory.split(File.separator);
				StringBuilder path = new StringBuilder();
				String name = pathSplited[pathSplited.length - 3];
				for (String part : pathSplited) {
					if (!part.equalsIgnoreCase("bin")
							&& !part.equalsIgnoreCase("javac")) {
						path.append(part).append(File.separator);
					}
				}
				list.add(new JDK(name, path.substring(0, path.length() - 1)
						.toString()));
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
	public void install(List<JDK> jdks, JTextPane jTextPane)
			throws InstallationFailException,
			InstallationDirectoryFaultException,
			PermissionOperatingSystemExpection {
		try {
			setEnv("Default");
			createFileConfig(jdks);
			jTextPane.setText(log.toString());
		} catch (Exception e) {

			e.printStackTrace();
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
		log.append("[SET ENV] configurando variable de ambinente no .bashrc\n"+envCommandBashrc+"\n");
		// execute the command
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				commands);
		try {
			int result = commandExecutor.executeCommand();
			// get the stdout and stderr from the command that was run
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the stdout and stderr
			log.append("The numeric result of the command was: "
					+ result+"\n");
			log.append("STDOUT: "+stdout+"\n");
			log.append("STDERR:"+stderr+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getInitialDirectoryChooser() {
		return File.separator;
	}

	@Override
	public void change(String newJDK) throws IOException {
		try {
			String changeCommand = "sed -i \"s/"
					+ "JAVA_HOME=.*/"
					+ "JAVA_HOME="
					+ BatchStringScapeUtils
							.escape(getPathnameOrNameOfJDK(newJDK))
					+ "/g\" $HOME/.jswitchrc";
			List<String> commands = new ArrayList<String>();
			commands.add("bash");
			commands.add("-c");
					
			commands.add(changeCommand);
			commands.add("source $HOME/.jswitchrc");

			// execute the command
			SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
					commands);
			int result = commandExecutor.executeCommand();

			// get the stdout and stderr from the command that was run
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the stdout and stderr
			System.out.println("The numeric result of the command was: "
					+ result);
			System.out.println("STDOUT:");
			System.out.println(stdout);
			System.out.println("STDERR:");
			System.out.println(stderr);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getInstallationDir() throws Exception {
		List<String> commands = new ArrayList<String>();
		commands.add("bash");
		commands.add("-c");
		commands.add("echo $USER");

		// execute the command
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				commands);
		int result = commandExecutor.executeCommand();
		if(result==1){
			throw new IllegalStateException();
		}
		StringBuilder user = commandExecutor.getStandardOutputFromCommand();
		String installPathname = File.separator + "home" + File.separator
				+ user.toString().trim() + File.separator + "JSwitch";
		File dir = new File(installPathname);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return installPathname;
	}

	@Override
	public void registerBootstrp() throws InstallationFailException,
			PermissionOperatingSystemExpection {
		// TODO not implemented yet

	}

	@Override
	public JDK getCurrentJDK() throws Exception {
		ProcessBuilder pFindProgramFiles = new ProcessBuilder("bash", "-c",
				"echo $JAVA_HOME");
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

	@Override
	public void update(List<JDK> jdks, JTextPane jTextPane)
			throws InstallationFailException,
			InstallationDirectoryFaultException,
			PermissionOperatingSystemExpection {
		// TODO Auto-generated method stub

	}

}
