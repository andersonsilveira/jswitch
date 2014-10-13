package br.com.org.jswitch.cfg.win;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.org.jswitch.cfg.SystemTrayConfig;

public class WindowsSystemTrayConfig implements SystemTrayConfig {

	@Override
	public List<String> getJDKInstalled() {
		String pathname = getInstalationDir();
		Set<String> ret = new HashSet<String>();
		try {
			FileReader fileReader = new FileReader(new File(pathname
					+ "config.cfg"));
			BufferedReader br = new BufferedReader(fileReader);
			String line;

			while ((line = br.readLine()) != null) {
				ret.add(line); // prints the characters one by one
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			return new ArrayList<String>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<String>(ret);
	}

	@Override
	public void change(String jdk) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C",
				MessageFormat.format(
						WindowsSystem.RESTORE_JAVA_HOME, "",
						getProgramFilesDir() + File.separator + "Java"
								+ File.separator + jdk));
		processBuilder.start();
	}

	public String getInstalationDir() {
		String pathname = getProgramFilesDir();
		return pathname + File.separator + "JSwitch" + File.separator;
	}

	private String getProgramFilesDir() {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/C",
				"echo %programFiles%");
		List<String> diretoryOfJavaInstalations = getDiretoryOfJavaInstalations(pb);
		String pathname = diretoryOfJavaInstalations.get(0);
		return pathname;
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
				directories.add(dir);
			}
		} catch (IOException e) {
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
