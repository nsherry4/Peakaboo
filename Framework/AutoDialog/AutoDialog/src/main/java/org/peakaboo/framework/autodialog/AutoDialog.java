package org.peakaboo.framework.autodialog;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AutoDialog {

	private AutoDialog() {
		
	}

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String sluggify(String s) {
		s = WHITESPACE.matcher(s).replaceAll("-");
		s = Normalizer.normalize(s, Form.NFD);
		s = NONLATIN.matcher(s).replaceAll("");
		s = s.toLowerCase();
		return s;
	}
}
