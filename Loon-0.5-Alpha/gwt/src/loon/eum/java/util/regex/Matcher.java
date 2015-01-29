package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;

public class Matcher {
	private final RegExp regExp;
	private final String input;

	Matcher (Pattern pattern, CharSequence input) {
		this.regExp = pattern.regExp;
		this.input = String.valueOf(input);
	}

	public boolean matches () {
		return regExp.test(input);
	}
}
