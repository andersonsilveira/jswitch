package br.com.org.jswitch.cfg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.org.jswitch.model.JDK;

public class Loader {

	private static final String FIND_JAVA_PATH = "for %i in (javac.exe) do @echo.   %~$PATH:i";

	public List<JDK> load() {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/C", FIND_JAVA_PATH);
		List<String> javaInstalations = getDiretoryOfJavaInstalations(pb);
		List<JDK> jdks = new ArrayList<JDK>();
		for (String javaDirectory : javaInstalations) {
			javaDirectory = javaDirectory.trim();
			String[] pathSplited = javaDirectory.split("\\\\");
			StringBuilder path = new StringBuilder();
			String name = pathSplited[pathSplited.length-3];
			for (String part : pathSplited) {
				if(!part.equalsIgnoreCase("bin") && !part.equalsIgnoreCase("javac.exe")){
					path.append(part).append("\\");
				}
			}
			jdks.add(new JDK(name, path.toString()));
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
			directories.add(new String(baos.toByteArray()));
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
