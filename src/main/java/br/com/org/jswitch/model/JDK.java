package br.com.org.jswitch.model;

public class JDK {

	private String path;
	
	private String name;

	public JDK(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
