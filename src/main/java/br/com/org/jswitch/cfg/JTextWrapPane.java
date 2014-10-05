package br.com.org.jswitch.cfg;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

class JTextWrapPane extends JTextPane {

    boolean wrapState = true;
    JTextArea j = new JTextArea();

    JTextWrapPane() {
        super();
    }

    public JTextWrapPane(StyledDocument p_oSdLog) {
        super(p_oSdLog);
    }


    public boolean getScrollableTracksViewportWidth() {
        return wrapState;
    }


    public void setLineWrap(boolean wrap) {
        wrapState = wrap;
    }


    public boolean getLineWrap(boolean wrap) {
        return wrapState;
    }
} 