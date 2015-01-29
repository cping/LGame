package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;

public class Pattern {
	final RegExp regExp;

	private Pattern (String regExp) {
		this.regExp = RegExp.compile(regExp);
	}

	public static Pattern compile (String regExp) {
		return new Pattern(regExp);
	}

	public Matcher matcher (CharSequence input) {
		return new Matcher(this, input);
	}
}
