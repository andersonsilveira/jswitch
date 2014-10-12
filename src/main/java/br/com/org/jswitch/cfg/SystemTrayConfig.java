package br.com.org.jswitch.cfg;

import java.io.IOException;
import java.util.List;

public interface SystemTrayConfig {

	public abstract List<String> getJDKInstalled();

	public abstract void change(String jdk) throws IOException;

}
