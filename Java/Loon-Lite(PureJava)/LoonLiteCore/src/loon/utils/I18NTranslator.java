/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.LRelease;
import loon.font.ITranslator;

/**
 * 一个简单的ITranslator接口实现,用于实现注入Font的setTranslator,以执行与Font绑定的多语言显示
 */
public class I18NTranslator implements ITranslator, LRelease {

	private final ConfigReader _lang;

	private boolean _allowTrans;

	public I18NTranslator(String file, String mapKey) {
		this._lang = new I18N(file).newConfig(mapKey);
	}

	public I18NTranslator(I18N lang, String mapKey) {
		this._lang = lang.newConfig(mapKey);
	}

	public I18NTranslator(ConfigReader lang) {
		this._lang = lang;
	}

	public I18NTranslator setAllow(boolean a) {
		this._allowTrans = a;
		return this;
	}

	@Override
	public boolean isAllow() {
		return _allowTrans;
	}

	@Override
	public String toTanslation(String original, String def) {
		if (_lang != null) {
			return _lang.getValue(original, def);
		}
		return def == null ? original : def;
	}

	public ConfigReader getConfig() {
		return this._lang;
	}

	@Override
	public void close() {
		if (_lang != null) {
			_lang.close();
			_allowTrans = false;
		}

	}
}
