package br.com.org.jswitch.cfg.win;

import br.com.org.jswitch.cfg.DirectoryChooser;

public class JDKWindowChooseDirectory extends DirectoryChooser{

	@Override
	public String getInitialDirectory() {
		return "C:/";
	}

}
