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

public class StrMap extends ObjectMap<String, String> {
	
	public StrMap() {
		super();
	}
	
	public StrMap(ObjectMap<? extends String, ? extends String> map) {
		super(map);
	}

	public StrMap(StringKeyValue... values) {
		for (StringKeyValue v : values) {
			if (v != null) {
				put(v.getKey(), v.getValue());
			}
		}
	}

	public StrMap(Object... values) {
		for (int i = 0; i < values.length / 2; i++) {
			put((String) values[i * 2], String.valueOf(values[i * 2 + 1]));
		}
	}

	public boolean getBool(String name) {
		return StringUtils.isBoolean(get(name));
	}

	public int getInt(String name) {
		return getInt(name, 0);
	}

	public float getFloat(String name) {
		return getFloat(name, 0f);
	}

	public long getLong(String name) {
		return getLong(name, 0l);
	}

	public int getInt(String name, int def) {
		String v = get(name);
		if (!MathUtils.isNan(v)) {
			return def;
		}
		return containsKey(name) ? Integer.parseInt(v) : def;
	}

	public float getFloat(String name, float def) {
		String v = get(name);
		if (!MathUtils.isNan(v)) {
			return def;
		}
		return containsKey(name) ? Float.parseFloat(v) : def;
	}

	public long getLong(String name, long def) {
		String v = get(name);
		if (!MathUtils.isNan(v)) {
			return def;
		}
		return containsKey(name) ? Long.parseLong(v) : def;
	}

}
