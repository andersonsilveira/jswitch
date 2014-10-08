package br.com.org.jswitch.cfg;

import java.util.List;

import javax.swing.JTextPane;

import br.com.org.jswitch.model.JDK;

public interface JDKMenuContextCreator {

	public abstract void execute(List<JDK> jdks, JTextPane jTextPane);

}