/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils;

import java.util.Locale;

import loon.LSysException;
import loon.LSystem;

public class Language {

	private static Language ROOT_LANG;

	@SuppressWarnings("deprecation")
	public static Locale newLocaleFromCode(String v) {
		if (StringUtils.isEmpty(v)) {
			throw new LSysException("'value' must be present");
		}
		String[] parameters = StringUtils.split(v, '_');
		if (parameters.length == 0) {
			parameters = StringUtils.split(v, LSystem.COMMA);
		}
		if (parameters.length == 0) {
			parameters = StringUtils.split(v, '-');
		}
		if (parameters.length == 0) {
			throw new LSysException("'value' must not be empty");
		}
		final Locale newLocale;
		switch (parameters.length) {
		case 1:
			newLocale = new Locale(parameters[0]);
			break;
		case 2:
			newLocale = new Locale(parameters[0], parameters[1]);
			break;
		case 3:
			newLocale = new Locale(parameters[0], parameters[1], parameters[2]);
			break;
		default:
			throw new LSysException("Unknown locale descriptor: " + v);
		}
		return newLocale;
	}

	private static boolean checkLocale(Language localeSrc, Locale localeDst) {
		return localeSrc.getLanguage().equals(localeDst.toString())
				|| localeSrc.getCountry().equals(localeDst.toString());
	}

	public static boolean isEastLanguage() {
		return isEastLocale(Locale.getDefault());
	}

	public static boolean isEastLocale(Locale locale) {
		return checkLocale(Language.getJP(), locale) || checkLocale(Language.getKR(), locale)
				|| checkLocale(Language.getZHCN(), locale) || checkLocale(Language.getZHTW(), locale)
				|| checkLocale(Language.getZHHK(), locale);
	}

	private String _language;

	private String _country;

	private String _variant;

	public Language(String language, String country, String variant) {
		this._language = language;
		this._country = country;
		this._variant = variant;
	}

	public Language(String language, String country) {
		this(language, country, "");
	}

	public Language(String language) {
		this(language, "", "");
	}

	public Language(Locale locale) {
		this(locale.getLanguage(), locale.getCountry(), locale.getVariant());
	}

	public Language() {
		this(Locale.getDefault());
	}

	public static final Language getDefault() {
		if (ROOT_LANG == null) {
			ROOT_LANG = new Language();
		}
		return ROOT_LANG;
	}

	public static final Language getEN() {
		return new Language("en", "");
	}

	public static final Language getJP() {
		return new Language("ja", "JP");
	}

	public static final Language getKR() {
		return new Language("ko", "KR");
	}

	public static final Language getZHCN() {
		return new Language("zh", "CN");
	}

	public static final Language getZHTW() {
		return new Language("zh", "TW");
	}

	public static final Language getZHHK() {
		return new Language("zh", "HK");
	}

	public String getLanguage() {
		return StringUtils.isEmpty(_language) ? " " : _language;
	}

	public String getCountry() {
		return StringUtils.isEmpty(_country) ? " " : _country;
	}

	public String getVariant() {
		return StringUtils.isEmpty(_variant) ? " " : _variant;
	}

	public void setVariant(String variant) {
		this._variant = variant;
	}

	public void setLanguage(String language) {
		this._language = language;
	}

	public void setCountry(String country) {
		this._country = country;
	}

	@Override
	public String toString() {
		return getLanguage() + "_" + getCountry() + "_" + getVariant();
	}

}
