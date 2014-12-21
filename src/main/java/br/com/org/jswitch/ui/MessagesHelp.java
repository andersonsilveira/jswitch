package br.com.org.jswitch.ui;

import java.util.Locale;
import java.util.ResourceBundle;
/**
 * 
 * @author Anderson
 *
 */
public final class MessagesHelp {

  public static ResourceBundle getBundle(){
      Locale locale = Locale.getDefault();
      ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
      return bundle;
  }
}
