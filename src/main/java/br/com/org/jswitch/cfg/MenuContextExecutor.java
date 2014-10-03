package br.com.org.jswitch.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MenuContextExecutor {
	private static final String SLASHDOT = "\\";
	private static final String QUOTE = "\"";
	private static final String REGISTRY = "cd %ProgramFiles%\n"
			+ "if not exist jdk6.bat (\n"
			+ "echo echo off>>jdk6.bat\n"
			+ "echo echo Setting JAVA_HOME>>jdk6.bat\n"
			+ "echo set JAVA_HOME=C:\\Program Files\\Java\\jdk1.6.0_45>>jdk6.bat\n"
			+ "echo echo setting PATH>>jdk6.bat\n"
			+ "echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%>>jdk6.bat\n"
			+ "echo echo Display java version>>jdk6.bat\n"
			+ "echo java -version>>jdk6.bat)\n"
			+ "dir\n"
			+ "reg add HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\JDK6\\command  /d \"cmd /k "
			+ SLASHDOT
			+ QUOTE
			+ "%ProgramFiles%\\jdk6"
			+ QUOTE
			+ QUOTE
			+ "\n"
			+ "reg add HKEY_CLASSES_ROOT\\Folder\\shell\\JDK6\\command /d \"cmd /k "
			+ SLASHDOT + QUOTE + "%ProgramFiles%\\jdk6\\" + QUOTE + " " + QUOTE
			+ "%%1" + QUOTE + QUOTE;

	public void execute() {
		try {
			File file = new File("command.bat");
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(REGISTRY);
			fileWriter.flush();
			fileWriter.close();
			ProcessBuilder process = new ProcessBuilder("cmd", "/C","command.bat");
			getDiretoryOfJavaInstalations(process);
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
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
			System.out.println(new String(baos.toByteArray()));
			prs.getErrorStream().close();
			prs.getOutputStream().close();
			prs.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
