package loon.build.tools;

import java.util.Locale;

public enum Language {

	SIMPLECN(Locale.SIMPLIFIED_CHINESE), TRADITIONALCN(
			Locale.TRADITIONAL_CHINESE), EN(Locale.ENGLISH), JP(Locale.JAPANESE), DEF();

	private Locale locale;

	Language(Locale locale) {
		this.locale = locale;
	}

	Language() {
		this(Locale.getDefault());
	}

	public Locale getLocale() {
		return locale;
	}
}