package loon.utils.debugging;

import java.util.WeakHashMap;

import loon.core.LSystem;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LogFactory {

	final static private WeakHashMap<String, Object> lazyMap = new WeakHashMap<String, Object>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	public static Log getInstance(String app) {
		return getInstance(app, 0);
	}

	public static Log getInstance(String app, int type) {
		String key = app.toLowerCase();
		Object obj = lazyMap.get(key);
		if (obj == null) {
			lazyMap.put(key, obj = new Log(app, type));
		}
		return (Log) obj;
	}

	public static Log getInstance(Class<?> clazz) {
		String key = clazz.getName().toLowerCase();
		Object obj = lazyMap.get(key);
		if (obj == null) {
			lazyMap.put(key, obj = new Log(clazz));
		}
		return (Log) obj;
	}

	public static void clear() {
		lazyMap.clear();
	}

}
