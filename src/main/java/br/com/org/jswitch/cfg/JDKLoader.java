package br.com.org.jswitch.cfg;

import java.util.List;

import br.com.org.jswitch.model.JDK;

public interface JDKLoader {

	public abstract List<JDK> load();

}