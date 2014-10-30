package br.com.org.jswitch.cfg.win;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * @author Anderson
 *
 */
public final class BatchStringScapeUtils {

    public static String escape(String comment) {
	Map<String, String> mapOfEscape = new HashMap<String, String>();
	mapOfEscape.put("\\(", "^(");
	mapOfEscape.put("\\)", "^)");
	mapOfEscape.put("\\[", "\\\\[");
	mapOfEscape.put("\\]", "\\\\]");

	for (Entry<String, String> escape : mapOfEscape.entrySet()) {
	    String replacement = escape.getValue();
	    String regex = escape.getKey();
	    comment = comment.replaceAll(regex, replacement);
	}
	return comment;
    }
    public static void main(String[] args) {
	String comment = "C:/Program Files(x86)/JSwitch[]";
	
	comment = escape(comment);
	System.out.println(comment);
    }
}
