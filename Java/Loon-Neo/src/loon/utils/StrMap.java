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

import java.util.Comparator;
import java.util.Iterator;

public class StrMap extends ObjectMap<String, String> {

	public static StrMap of(Object... cs) {
		if (cs == null) {
			return new StrMap();
		}
		return new StrMap(cs);
	}

	private static class StrComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return StringUtils.checkCompareTo(o1, o2);
		}

	}

	private final static StrComparator STR_COMP = new StrComparator();

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

	public TArray<String> sortKeys() {
		final TArray<String> list = new TArray<String>(size);
		final Keys<String> names = keys();
		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String name = it.next();
			if (StringUtils.isNullOrEmpty(name)) {
				list.add(name);
			}
		}
		list.sort(STR_COMP);
		return list;
	}

	public TArray<String> sortValues() {
		final TArray<String> list = new TArray<String>(size);
		final Values<String> names = values();
		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String name = it.next();
			if (StringUtils.isNullOrEmpty(name)) {
				list.add(name);
			}
		}
		list.sort(STR_COMP);
		return list;
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

	public long getLong(String name, long def) {
		String v = get(name);
		if (!MathUtils.isNan(v)) {
			return def;
		}
		return containsKey(name) ? Long.parseLong(v) : def;
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

}
