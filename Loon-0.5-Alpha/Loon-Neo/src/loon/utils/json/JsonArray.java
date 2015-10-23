/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import loon.Json;
import loon.Json.TypedArray;

class JsonArray implements Json.Array {

	private final ArrayList<Object> list;

	public JsonArray() {
		list = new ArrayList<Object>();
	}

	JsonArray(Collection<? extends Object> collection) {
		list = new ArrayList<Object>(collection);
	}

	static JsonArray from(Object... contents) {
		return new JsonArray(Arrays.asList(contents));
	}

	public static JsonBuilder<JsonArray> builder() {
		return new JsonBuilder<JsonArray>(new JsonArray());
	}

	public JsonArray add(java.lang.Object value) {
		JsonImpl.checkJsonType(value);
		list.add(value);
		return this;
	}

	public JsonArray add(int index, java.lang.Object value) {
		JsonImpl.checkJsonType(value);
		while (list.size() < index) {
			list.add(null);
		}
		list.add(index, value);
		return this;
	}

	public Json.Array getArray(int key) {
		return getArray(key, (Json.Array) null);
	}

	public Json.Array getArray(int key, Json.Array default_) {
		Object o = get(key);
		return (o instanceof Json.Array) ? (Json.Array) get(key) : default_;
	}

	@Override
	public <T> TypedArray<T> getArray(int index, Class<T> jsonType) {
		Json.Array array = getArray(index);
		return array == null ? null : new JsonTypedArray<T>(array, jsonType);
	}

	public boolean getBoolean(int key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(int key, boolean default_) {
		Object o = get(key);
		return o instanceof Boolean ? (Boolean) o : default_;
	}

	public double getDouble(int key) {
		return getDouble(key, 0);
	}

	public double getDouble(int key, double default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).doubleValue() : default_;
	}

	public float getNumber(int key) {
		return getNumber(key, 0);
	}

	public float getNumber(int key, float default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).floatValue() : default_;
	}

	public int getInt(int key) {
		return getInt(key, 0);
	}

	public int getInt(int key, int default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).intValue() : default_;
	}

	public long getLong(int key) {
		return getLong(key, 0);
	}

	public long getLong(int key, long default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).longValue() : default_;
	}

	public Json.Object getObject(int key) {
		return getObject(key, null);
	}

	public Json.Object getObject(int key, Json.Object default_) {
		Object o = get(key);
		return o instanceof Json.Object ? (Json.Object) get(key) : default_;
	}

	public String getString(int key) {
		return getString(key, null);
	}

	public String getString(int key, String default_) {
		Object o = get(key);
		return (o instanceof String) ? (String) o : default_;
	}

	public boolean isArray(int key) {
		return get(key) instanceof Json.Array;
	}

	public boolean isBoolean(int key) {
		return get(key) instanceof Boolean;
	}

	public boolean isNull(int key) {
		return get(key) == null;
	}

	public boolean isNumber(int key) {
		return get(key) instanceof Number;
	}

	public boolean isString(int key) {
		return get(key) instanceof String;
	}

	public boolean isObject(int key) {
		return get(key) instanceof Json.Object;
	}

	@Override
	public int length() {
		return list.size();
	}

	@Override
	public JsonArray remove(int index) {
		if (index < 0 || index >= list.size())
			return this;
		list.remove(index);
		return this;
	}

	@Override
	public JsonArray set(int index, java.lang.Object value) {
		JsonImpl.checkJsonType(value);
		while (list.size() <= index) {
			list.add(null);
		}
		list.set(index, value);
		return this;
	}

	@Override
	public String toString() {
		return list.toString();
	}

	@Override
	public <T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink) {
		for (int i = 0; i < list.size(); i++) {
			sink.value(list.get(i));
		}
		return sink;
	}

	Object get(int key) {
		return (key >= 0 && key < list.size()) ? list.get(key) : null;
	}

}
