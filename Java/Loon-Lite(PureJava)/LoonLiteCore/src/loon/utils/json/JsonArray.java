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

import loon.Json;
import loon.LSystem;
import loon.utils.IArray;
import loon.utils.TArray;

class JsonArray implements Json.Array, IArray {

	private final TArray<Object> _json_array_context;

	public JsonArray() {
		_json_array_context = new TArray<>(LSystem.DEFAULT_MAX_CACHE_SIZE);
	}

	JsonArray(Object... collection) {
		_json_array_context = new TArray<>(collection);
	}

	public final static JsonBuilder<JsonArray> at() {
		return builder();
	}

	public final static JsonBuilder<JsonArray> builder() {
		return new JsonBuilder<>(new JsonArray());
	}

	@Override
	public JsonArray add(Object value) {
		JsonImpl.checkJsonType(value);
		_json_array_context.add(value);
		return this;
	}

	@Override
	public JsonArray add(int index, Object value) {
		JsonImpl.checkJsonType(value);
		while (_json_array_context.size < index) {
			_json_array_context.add(null);
		}
		_json_array_context.insert(index, value);
		return this;
	}

	@Override
	public Json.Array getArray(int key) {
		return getArray(key, (Json.Array) null);
	}

	@Override
	public Json.Array getArray(int key, Json.Array default_) {
		Object o = get(key);
		return (o instanceof Json.Array) ? (Json.Array) get(key) : default_;
	}

	@Override
	public boolean getBoolean(int key) {
		return getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(int key, boolean default_) {
		Object o = get(key);
		return o instanceof Boolean ? (Boolean) o : default_;
	}

	@Override
	public double getDouble(int key) {
		return getDouble(key, 0);
	}

	@Override
	public double getDouble(int key, double default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).doubleValue() : default_;
	}

	@Override
	public float getNumber(int key) {
		return getNumber(key, 0);
	}

	@Override
	public float getNumber(int key, float default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).floatValue() : default_;
	}

	@Override
	public int getInt(int key) {
		return getInt(key, 0);
	}

	@Override
	public int getInt(int key, int default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).intValue() : default_;
	}

	@Override
	public long getLong(int key) {
		return getLong(key, 0);
	}

	@Override
	public long getLong(int key, long default_) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).longValue() : default_;
	}

	@Override
	public Json.Object getObject(int key) {
		return getObject(key, null);
	}

	@Override
	public Json.Object getObject(int key, Json.Object default_) {
		Object o = get(key);
		return o instanceof Json.Object ? (Json.Object) get(key) : default_;
	}

	@Override
	public String getString(int key) {
		return getString(key, null);
	}

	@Override
	public String getString(int key, String default_) {
		Object o = get(key);
		return (o instanceof String) ? (String) o : default_;
	}

	@Override
	public boolean isArray(int key) {
		return get(key) instanceof Json.Array;
	}

	@Override
	public boolean isBoolean(int key) {
		return get(key) instanceof Boolean;
	}

	@Override
	public boolean isNull(int key) {
		return get(key) == null;
	}

	@Override
	public boolean isNumber(int key) {
		return get(key) instanceof Number;
	}

	@Override
	public boolean isString(int key) {
		return get(key) instanceof String;
	}

	@Override
	public boolean isObject(int key) {
		return get(key) instanceof Json.Object;
	}

	@Override
	public int length() {
		return _json_array_context.size;
	}

	@Override
	public JsonArray remove(int index) {
		if (index < 0 || index >= _json_array_context.size)
			return this;
		_json_array_context.remove(index);
		return this;
	}

	@Override
	public JsonArray set(int index, Object value) {
		JsonImpl.checkJsonType(value);
		while (_json_array_context.size <= index) {
			_json_array_context.add(null);
		}
		_json_array_context.set(index, value);
		return this;
	}

	@Override
	public String toString() {
		return _json_array_context.toString();
	}

	@Override
	public <T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink) {
		for (int i = 0; i < _json_array_context.size; i++) {
			sink.value(_json_array_context.get(i));
		}
		return sink;
	}

	Object get(int key) {
		return (key >= 0 && key < _json_array_context.size) ? _json_array_context.get(key) : null;
	}

	@Override
	public int size() {
		return length();
	}

	@Override
	public void clear() {
		if (_json_array_context != null) {
			_json_array_context.clear();
		}
	}

	@Override
	public boolean isEmpty() {
		return length() == 0;
	}

}
