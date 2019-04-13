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

import loon.LRelease;
import loon.LSystem;

/**
 * I18N多语言文件配置用类
 */
public class I18N implements LRelease {

	private static ObjectMap<String, I18N> _I18N_CACHE;

	public final static I18N bindLanguage(Language language, I18N i18n, String mapKey) {
		if (_I18N_CACHE == null) {
			_I18N_CACHE = new ObjectMap<String, I18N>(LSystem.DEFAULT_MAX_CACHE_SIZE);
		}
		I18N i18nOld = _I18N_CACHE.get(language.toString());
		if (i18nOld != null && i18nOld != i18n) {
			i18nOld.close();
		}
		if (!StringUtils.isEmpty(mapKey)) {
			i18n.loadConfig(mapKey);
		}
		_I18N_CACHE.put(language.toString(), i18n);
		return i18n;
	}

	public final static I18N bindLanguage(I18N i18n, String mapKey) {
		return bindLanguage(Language.getDefault(), i18n, mapKey);
	}

	public final static I18N bindLanguage(Language language, String path, String mapKey) {
		return bindLanguage(language, new I18N(path), mapKey);
	}

	public final static I18N bindCurrentLanguage(String path, String mapKey) {
		return bindLanguage(Language.getDefault(), new I18N(path), mapKey);
	}

	public final static I18N bindCurrentLanguage(I18N i18n, String mapKey) {
		return bindLanguage(Language.getDefault(), i18n, mapKey);
	}

	public final static I18N bindCurrentLanguage(I18N i18n) {
		return bindLanguage(Language.getDefault(), i18n, null);
	}

	public final static I18N bindCurrentLanguage(String path, I18N i18n) {
		return bindLanguage(Language.getDefault(), new I18N(path), null);
	}

	public final static I18N getLanguage(Language language, String mapKey) {
		if (_I18N_CACHE == null) {
			_I18N_CACHE = new ObjectMap<String, I18N>(LSystem.DEFAULT_MAX_CACHE_SIZE);
		}
		I18N i18n = _I18N_CACHE.get(language.toString());
		if (i18n != null && !StringUtils.isEmpty(mapKey)) {
			i18n.loadConfig(mapKey);
		}
		return i18n;
	}

	public final static I18N getLanguage(Language language) {
		return getLanguage(language, null);
	}

	public final static I18N getCurrentLanguage() {
		return getLanguage(Language.getDefault());
	}

	private ConfigReader _langConfig;

	private final String _filePath;
	
	private boolean _initConfig;

	public I18N(String path) {
		this._filePath = path;
	}

	public final ConfigReader loadConfig() {
		return loadConfig(null);
	}

	public final ConfigReader loadConfig(String mapKey) {
		if (!_initConfig) {
			_langConfig = new ConfigReader(_filePath);
			_initConfig = true;
		}
		if (!StringUtils.isEmpty(mapKey)) {
			_langConfig.loadMapKey(mapKey);
		}
		return _langConfig;
	}

	public final ConfigReader newConfig(String mapKey) {
		ConfigReader cfr = new ConfigReader(_filePath);
		if (!StringUtils.isEmpty(mapKey)) {
			cfr.loadMapKey(mapKey);
		}
		return cfr;
	}
	
	public String getText(String res) {
		return getText(res, "");
	}

	public String getText(String res, String value) {
		return getText(LSystem.getSystemAppName(), res, value);
	}

	public String getText(Object obj, String res, String value) {
		loadConfig();
		String result = null;
		if (obj == null) {
			result = _langConfig.getValue(res, value);
		}
		if (result == null) {
			String clazz = null;
			if (obj instanceof String) {
				clazz = (String) obj;
			} else if (obj instanceof Class) {
				clazz = ((Class<?>) obj).getName();
			} else {
				clazz = obj.getClass().getName();
			}
			result = _langConfig.getValue(clazz + "." + res);
			if (result == null) {
				result = _langConfig.getValue(res, value);
			}
		}
		if (result != null && result.indexOf("\\n") != -1) {
			result = StringUtils.replace(result, "\\n", "\n");
		}
		if (result != null && result.indexOf("\\r") != -1) {
			result = StringUtils.replace(result, "\\r", "\r");
		}
		return result;
	}

	@Override
	public void close() {
		if (_langConfig != null) {
			_langConfig.close();
		}
	}

}
