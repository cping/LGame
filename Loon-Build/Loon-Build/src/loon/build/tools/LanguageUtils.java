package loon.build.tools;

import java.util.Locale;

public class LanguageUtils {

	public static boolean isEast() {
		return isEastLocale(Language.DEF.getLocale());
	}

	public static boolean isEastLocale(Locale locale) {
		return locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE)
				|| locale.equals(new Locale("zh", "HK"))
				|| locale.equals(Locale.TAIWAN) || locale.equals(Locale.JAPAN)
				|| locale.equals(Locale.JAPANESE)
				|| locale.equals(Locale.KOREA) || locale.equals(Locale.KOREAN);
	}

}
