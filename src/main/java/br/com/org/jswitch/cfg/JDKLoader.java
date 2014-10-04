package br.com.org.jswitch.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.org.jswitch.model.JDK;

public class JDKLoader {

	private static final String FIND_JAVA_PATH = "cd %programFiles% \n"
												+ "dir javac.exe /B /S";

	public List<JDK> load() {
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
				String name = pathSplited[pathSplited.length-3];
				for (String part : pathSplited) {
					if(!part.equalsIgnoreCase("bin") && !part.equalsIgnoreCase("javac.exe")){
						path.append(part).append("\\");
					}
				}
				jdks.add(new JDK(name, path.substring(0,path.length()-1).toString()));
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
				if(dir.contains("\\bin\\javac.exe")){
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
}
