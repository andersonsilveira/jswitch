package br.com.org.jswitch.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import br.com.org.jswitch.model.JDK;

public class MenuContextExecutor {
	private static final String SLASHDOT = "\\";
	private static final String QUOTE = "\"";
	//private static final String DELETE_JAVA_HOME = "reg delete \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v JAVA_HOME /f";
	private static final String RESTORE_JAVA_HOME = "setx JAVA_HOME \"{1}\" /m";
	
	private static final String REGISTRY = 
			 "cd /d %ProgramFiles%\n"
			+ "if not exist {0}.bat (\n"
			+ "echo "+RESTORE_JAVA_HOME+">>{0}.bat\n"
			+ "echo echo off>>{0}.bat\n"
			+ "echo echo Setting JAVA_HOME>>{0}.bat\n"
			+ "echo set JAVA_HOME={1}>>{0}.bat\n"
			+ "echo echo setting PATH>>{0}.bat\n"
			+ "echo set PATH=%%JAVA_HOME%%\\bin;%%PATH%%>>{0}.bat\n"
			+ "echo echo Display java version>>{0}.bat\n"
			+ "echo java -version>>{0}.bat)\n"
			+ "reg add HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\{0}\\command  /d \"cmd /k "+ SLASHDOT+ QUOTE+ "%ProgramFiles%\\{0}"	+ SLASHDOT+ QUOTE+ QUOTE+ " /f\n"
			+ "reg add HKEY_CLASSES_ROOT\\Folder\\shell\\{0}\\command /d \"cmd /k "+ SLASHDOT + QUOTE + SLASHDOT + QUOTE + "%ProgramFiles%\\{0}"+SLASHDOT + QUOTE + " "+SLASHDOT+ QUOTE
			+ "%%1" +SLASHDOT+ QUOTE+SLASHDOT+ QUOTE + QUOTE+" /f\n";
	
	
	public void execute(List<JDK> jdks) {
		File file = new File("command.bat");
		try {
		FileWriter fileWriter = new FileWriter(file);
		for (JDK jdk : jdks) {
			file.createNewFile();
			fileWriter.write(MessageFormat.format(REGISTRY, jdk.getName(),
					jdk.getPath()));
		}
		fileWriter.flush();
		fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C",
					"start", "cmd.exe", "/K", "command.bat");
			Process process = processBuilder.start();
			getDiretoryOfJavaInstalations(process);
			process.getErrorStream().close();
			process.getOutputStream().close();
			int exitValue = process.waitFor();
			System.out.println(exitValue);
			if (exitValue != 0) {
				JOptionPane.showMessageDialog(null,
						"Erro na instalação, verifique as permissões de UAC!",
						"JSwitch", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
		}
		file.delete();
	}

	private List<String> getDiretoryOfJavaInstalations(Process prs) {
		List<String> directories = new ArrayList<String>();
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = prs.getInputStream();
			byte[] b = new byte[1024];
			int size = 0;
			baos = new ByteArrayOutputStream();
			while ((size = is.read(b)) != -1) {
				baos.write(b, 0, size);
			}
			System.out.println(new String(baos.toByteArray()));
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
}
