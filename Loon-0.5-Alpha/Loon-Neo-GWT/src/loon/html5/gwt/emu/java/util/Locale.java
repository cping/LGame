/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.util;

import java.io.Serializable;

//import libcore.icu.ICU;

public final class Locale implements Cloneable, Serializable {

	private static final long serialVersionUID = 9149081749638150636L;

	/** Locale constant for en_CA. */
	public static final Locale CANADA = new Locale(true, "en", "CA");

	/** Locale constant for fr_CA. */
	public static final Locale CANADA_FRENCH = new Locale(true, "fr", "CA");

	/** Locale constant for zh_CN. */
	public static final Locale CHINA = new Locale(true, "zh", "CN");

	/** Locale constant for zh. */
	public static final Locale CHINESE = new Locale(true, "zh", "");

	/** Locale constant for en. */
	public static final Locale ENGLISH = new Locale(true, "en", "");

	/** Locale constant for fr_FR. */
	public static final Locale FRANCE = new Locale(true, "fr", "FR");

	/** Locale constant for fr. */
	public static final Locale FRENCH = new Locale(true, "fr", "");

	/** Locale constant for de. */
	public static final Locale GERMAN = new Locale(true, "de", "");

	/** Locale constant for de_DE. */
	public static final Locale GERMANY = new Locale(true, "de", "DE");

	/** Locale constant for it. */
	public static final Locale ITALIAN = new Locale(true, "it", "");

	/** Locale constant for it_IT. */
	public static final Locale ITALY = new Locale(true, "it", "IT");

	/** Locale constant for ja_JP. */
	public static final Locale JAPAN = new Locale(true, "ja", "JP");

	/** Locale constant for ja. */
	public static final Locale JAPANESE = new Locale(true, "ja", "");

	/** Locale constant for ko_KR. */
	public static final Locale KOREA = new Locale(true, "ko", "KR");

	/** Locale constant for ko. */
	public static final Locale KOREAN = new Locale(true, "ko", "");

	/** Locale constant for zh_CN. */
	public static final Locale PRC = new Locale(true, "zh", "CN");

	/** Locale constant for the root locale. The root locale has an empty language, country, and variant.
	 *
	 * @since 1.6 */
	public static final Locale ROOT = new Locale(true, "", "");

	/** Locale constant for zh_CN. */
	public static final Locale SIMPLIFIED_CHINESE = new Locale(true, "zh", "CN");

	/** Locale constant for zh_TW. */
	public static final Locale TAIWAN = new Locale(true, "zh", "TW");

	/** Locale constant for zh_TW. */
	public static final Locale TRADITIONAL_CHINESE = new Locale(true, "zh", "TW");

	/** Locale constant for en_GB. */
	public static final Locale UK = new Locale(true, "en", "GB");

	/** Locale constant for en_US. */
	public static final Locale US = new Locale(true, "en", "US");

// /**
// * The current default locale. It is temporarily assigned to US because we
// * need a default locale to lookup the real default locale.
// */
	private static Locale defaultLocale = US;

// static {
// String language = System.getProperty("user.language", "en");
// String region = System.getProperty("user.region", "US");
// String variant = System.getProperty("user.variant", "");
// defaultLocale = new Locale(language, region, variant);
// }

	private transient String countryCode;
	private transient String languageCode;
	private transient String variantCode;
	private transient String cachedToStringResult;

	/** There's a circular dependency between toLowerCase/toUpperCase and Locale.US. Work around this by avoiding these methods when
	 * constructing the built-in locales.
	 *
	 * @param unused required for this constructor to have a unique signature */
	private Locale (boolean unused, String lowerCaseLanguageCode, String upperCaseCountryCode) {
		this.languageCode = lowerCaseLanguageCode;
		this.countryCode = upperCaseCountryCode;
		this.variantCode = "";
	}

	/** Constructs a new {@code Locale} using the specified language. */
	public Locale (String language) {
		this(language, "", "");
	}

	/** Constructs a new {@code Locale} using the specified language and country codes. */
	public Locale (String language, String country) {
		this(language, country, "");
	}

	/** Constructs a new {@code Locale} using the specified language, country, and variant codes. */
	public Locale (String language, String country, String variant) {
		if (language == null || country == null || variant == null) {
			throw new NullPointerException("language=" + language + ",country=" + country + ",variant=" + variant);
		}
		if (language.isEmpty() && country.isEmpty()) {
			languageCode = "";
			countryCode = "";
			variantCode = variant;
			return;
		}

// languageCode = language.toLowerCase(Locale.US); // not supported by GWT
		languageCode = language.toLowerCase();
		// Map new language codes to the obsolete language
		// codes so the correct resource bundles will be used.
		if (languageCode.equals("he")) {
			languageCode = "iw";
		} else if (languageCode.equals("id")) {
			languageCode = "in";
		} else if (languageCode.equals("yi")) {
			languageCode = "ji";
		}

// countryCode = country.toUpperCase(Locale.US); // not supported by GWT
		countryCode = country.toUpperCase();

		// Work around for be compatible with RI
		variantCode = variant;
	}

// @Override public Object clone() {
// try {
// return super.clone();
// } catch (CloneNotSupportedException e) {
// throw new AssertionError(e);
// }
// }

	/** Returns true if {@code object} is a locale with the same language, country and variant. */
	@Override
	public boolean equals (Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof Locale) {
			Locale o = (Locale)object;
			return languageCode.equals(o.languageCode) && countryCode.equals(o.countryCode) && variantCode.equals(o.variantCode);
		}
		return false;
	}

// /**
// * Returns the system's installed locales. This array always includes {@code
// * Locale.US}, and usually several others. Most locale-sensitive classes
// * offer their own {@code getAvailableLocales} method, which should be
// * preferred over this general purpose method.
// *
// * @see java.text.BreakIterator#getAvailableLocales()
// * @see java.text.Collator#getAvailableLocales()
// * @see java.text.DateFormat#getAvailableLocales()
// * @see java.text.DateFormatSymbols#getAvailableLocales()
// * @see java.text.DecimalFormatSymbols#getAvailableLocales()
// * @see java.text.NumberFormat#getAvailableLocales()
// * @see java.util.Calendar#getAvailableLocales()
// */
// public static Locale[] getAvailableLocales() {
// return ICU.getAvailableLocales();
// }

	/** Returns the country code for this locale, or {@code ""} if this locale doesn't correspond to a specific country. */
	public String getCountry () {
		return countryCode;
	}

	/** Returns the user's preferred locale. This may have been overridden for this process with {@link #setDefault}.
	 *
	 * <p>
	 * Since the user's locale changes dynamically, avoid caching this value. Instead, use this method to look it up for each use. */
	public static Locale getDefault () {
		return defaultLocale;
	}

// /**
// * Equivalent to {@code getDisplayCountry(Locale.getDefault())}.
// */
// public final String getDisplayCountry() {
// return getDisplayCountry(getDefault());
// }
//
// /**
// * Returns the name of this locale's country, localized to {@code locale}.
// * Returns the empty string if this locale does not correspond to a specific
// * country.
// */
// public String getDisplayCountry(Locale locale) {
// if (countryCode.isEmpty()) {
// return "";
// }
// String result = ICU.getDisplayCountryNative(toString(), locale.toString());
// if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
// result = ICU.getDisplayCountryNative(toString(), Locale.getDefault().toString());
// }
// return result;
// }
//
// /**
// * Equivalent to {@code getDisplayLanguage(Locale.getDefault())}.
// */
// public final String getDisplayLanguage() {
// return getDisplayLanguage(getDefault());
// }
//
// /**
// * Returns the name of this locale's language, localized to {@code locale}.
// * If the language name is unknown, the language code is returned.
// */
// public String getDisplayLanguage(Locale locale) {
// if (languageCode.isEmpty()) {
// return "";
// }
//
// // http://b/8049507 --- frameworks/base should use fil_PH instead of tl_PH.
// // Until then, we're stuck covering their tracks, making it look like they're
// // using "fil" when they're not.
// String localeString = toString();
// if (languageCode.equals("tl")) {
// localeString = toNewString("fil", countryCode, variantCode);
// }
//
// String result = ICU.getDisplayLanguageNative(localeString, locale.toString());
// if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
// result = ICU.getDisplayLanguageNative(localeString, Locale.getDefault().toString());
// }
// return result;
// }
//
// /**
// * Equivalent to {@code getDisplayName(Locale.getDefault())}.
// */
// public final String getDisplayName() {
// return getDisplayName(getDefault());
// }
//
// /**
// * Returns this locale's language name, country name, and variant, localized
// * to {@code locale}. The exact output form depends on whether this locale
// * corresponds to a specific language, country and variant.
// *
// * <p>For example:
// * <ul>
// * <li>{@code new Locale("en").getDisplayName(Locale.US)} -> {@code English}
// * <li>{@code new Locale("en", "US").getDisplayName(Locale.US)} -> {@code English (United States)}
// * <li>{@code new Locale("en", "US", "POSIX").getDisplayName(Locale.US)} -> {@code English (United States,Computer)}
// * <li>{@code new Locale("en").getDisplayName(Locale.FRANCE)} -> {@code anglais}
// * <li>{@code new Locale("en", "US").getDisplayName(Locale.FRANCE)} -> {@code anglais (tats-Unis)}
// * <li>{@code new Locale("en", "US", "POSIX").getDisplayName(Locale.FRANCE)} -> {@code anglais (tats-Unis,informatique)}.
// * </ul>
// */
// public String getDisplayName(Locale locale) {
// int count = 0;
// StringBuilder buffer = new StringBuilder();
// if (!languageCode.isEmpty()) {
// String displayLanguage = getDisplayLanguage(locale);
// buffer.append(displayLanguage.isEmpty() ? languageCode : displayLanguage);
// ++count;
// }
// if (!countryCode.isEmpty()) {
// if (count == 1) {
// buffer.append(" (");
// }
// String displayCountry = getDisplayCountry(locale);
// buffer.append(displayCountry.isEmpty() ? countryCode : displayCountry);
// ++count;
// }
// if (!variantCode.isEmpty()) {
// if (count == 1) {
// buffer.append(" (");
// } else if (count == 2) {
// buffer.append(",");
// }
// String displayVariant = getDisplayVariant(locale);
// buffer.append(displayVariant.isEmpty() ? variantCode : displayVariant);
// ++count;
// }
// if (count > 1) {
// buffer.append(")");
// }
// return buffer.toString();
// }
//
// /**
// * Returns the full variant name in the default {@code Locale} for the variant code of
// * this {@code Locale}. If there is no matching variant name, the variant code is
// * returned.
// */
// public final String getDisplayVariant() {
// return getDisplayVariant(getDefault());
// }
//
// /**
// * Returns the full variant name in the specified {@code Locale} for the variant code
// * of this {@code Locale}. If there is no matching variant name, the variant code is
// * returned.
// */
// public String getDisplayVariant(Locale locale) {
// if (variantCode.length() == 0) {
// return variantCode;
// }
// String result = ICU.getDisplayVariantNative(toString(), locale.toString());
// if (result == null) { // TODO: do we need to do this, or does ICU do it for us?
// result = ICU.getDisplayVariantNative(toString(), Locale.getDefault().toString());
// }
// return result;
// }
//
// /**
// * Returns the three-letter ISO 3166 country code which corresponds to the country
// * code for this {@code Locale}.
// * @throws MissingResourceException if there's no 3-letter country code for this locale.
// */
// public String getISO3Country() {
// String code = ICU.getISO3CountryNative(toString());
// if (!countryCode.isEmpty() && code.isEmpty()) {
// throw new MissingResourceException("No 3-letter country code for locale: " + this, "FormatData_" + this, "ShortCountry");
// }
// return code;
// }
//
// /**
// * Returns the three-letter ISO 639-2/T language code which corresponds to the language
// * code for this {@code Locale}.
// * @throws MissingResourceException if there's no 3-letter language code for this locale.
// */
// public String getISO3Language() {
// String code = ICU.getISO3LanguageNative(toString());
// if (!languageCode.isEmpty() && code.isEmpty()) {
// throw new MissingResourceException("No 3-letter language code for locale: " + this, "FormatData_" + this, "ShortLanguage");
// }
// return code;
// }
//
// /**
// * Returns an array of strings containing all the two-letter ISO 3166 country codes that can be
// * used as the country code when constructing a {@code Locale}.
// */
// public static String[] getISOCountries() {
// return ICU.getISOCountries();
// }
//
// /**
// * Returns an array of strings containing all the two-letter ISO 639-1 language codes that can be
// * used as the language code when constructing a {@code Locale}.
// */
// public static String[] getISOLanguages() {
// return ICU.getISOLanguages();
// }

	/** Returns the language code for this {@code Locale} or the empty string if no language was set. */
	public String getLanguage () {
		return languageCode;
	}

	/** Returns the variant code for this {@code Locale} or an empty {@code String} if no variant was set. */
	public String getVariant () {
		return variantCode;
	}

	@Override
	public synchronized int hashCode () {
		return countryCode.hashCode() + languageCode.hashCode() + variantCode.hashCode();
	}

	/** Overrides the default locale. This does not affect system configuration, and attempts to override the system-provided
	 * default locale may themselves be overridden by actual changes to the system configuration. Code that calls this method is
	 * usually incorrect, and should be fixed by passing the appropriate locale to each locale-sensitive method that's called. */
	public synchronized static void setDefault (Locale locale) {
		if (locale == null) {
			throw new NullPointerException("locale == null");
		}
		defaultLocale = locale;
	}

	/** Returns the string representation of this {@code Locale}. It consists of the language code, country code and variant
	 * separated by underscores. If the language is missing the string begins with an underscore. If the country is missing there
	 * are 2 underscores between the language and the variant. The variant cannot stand alone without a language and/or country
	 * code: in this case this method would return the empty string.
	 *
	 * <p>
	 * Examples: "en", "en_US", "_US", "en__POSIX", "en_US_POSIX" */
	@Override
	public final String toString () {
		String result = cachedToStringResult;
		if (result == null) {
			result = cachedToStringResult = toNewString(languageCode, countryCode, variantCode);
		}
		return result;
	}

	private static String toNewString (String languageCode, String countryCode, String variantCode) {
		// The string form of a locale that only has a variant is the empty string.
		if (languageCode.length() == 0 && countryCode.length() == 0) {
			return "";
		}
		// Otherwise, the output format is "ll_cc_variant", where language and country are always
		// two letters, but the variant is an arbitrary length. A size of 11 characters has room
		// for "en_US_POSIX", the largest "common" value. (In practice, the string form is almost
		// always 5 characters: "ll_cc".)
		StringBuilder result = new StringBuilder(11);
		result.append(languageCode);
		if (countryCode.length() > 0 || variantCode.length() > 0) {
			result.append('_');
		}
		result.append(countryCode);
		if (variantCode.length() > 0) {
			result.append('_');
		}
		result.append(variantCode);
		return result.toString();
	}

// private static final ObjectStreamField[] serialPersistentFields = {
// new ObjectStreamField("country", String.class),
// new ObjectStreamField("hashcode", int.class),
// new ObjectStreamField("language", String.class),
// new ObjectStreamField("variant", String.class),
// };
//
// private void writeObject(ObjectOutputStream stream) throws IOException {
// ObjectOutputStream.PutField fields = stream.putFields();
// fields.put("country", countryCode);
// fields.put("hashcode", -1);
// fields.put("language", languageCode);
// fields.put("variant", variantCode);
// stream.writeFields();
// }
//
// private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
// ObjectInputStream.GetField fields = stream.readFields();
// countryCode = (String) fields.get("country", "");
// languageCode = (String) fields.get("language", "");
// variantCode = (String) fields.get("variant", "");
// }
}
