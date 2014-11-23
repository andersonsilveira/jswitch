package br.com.org.jswitch.control.linux;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

final class BatchStringScapeUtils {

    public static String escape(String comment) {
	Map<String, String> mapOfEscape = new HashMap<String, String>();
	mapOfEscape.put("\\/", "\\\\/");

	for (Entry<String, String> escape : mapOfEscape.entrySet()) {
	    String replacement = escape.getValue();
	    String regex = escape.getKey();
	    comment = comment.replaceAll(regex, replacement);
	}
	return comment;
    }
    public static void main(String[] args) {
	String comment = "/usr/local/java/jdk1.8.0_25";
	
	comment = escape(comment);
	System.out.println(comment);
    }
}