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
import loon.Json.TypedArray;
import loon.utils.ObjectMap.Entry;
import loon.utils.OrderedMap;

class JsonObject implements Json.Object {

	private final OrderedMap<String, Object> _orderedMap;

	public JsonObject() {
		_orderedMap = new OrderedMap<>();
	}

	public final static JsonBuilder<JsonObject> at() {
		return builder();
	}

	public final static JsonBuilder<JsonObject> builder() {
		return new JsonBuilder<>(new JsonObject());
	}

	@Override
	public Json.Array getArray(String key) {
		return getArray(key, (Json.Array) null);
	}

	@Override
	public Json.Array getArray(String key, Json.Array def) {
		Object o = get(key);
		return (o instanceof Json.Array) ? (Json.Array) o : def;
	}

	@Override
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(String key, boolean def) {
		Object o = get(key);
		return o instanceof Boolean ? (Boolean) o : def;
	}

	@Override
	public double getDouble(String key) {
		return getDouble(key, 0);
	}

	@Override
	public double getDouble(String key, double def) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).doubleValue() : def;
	}

	@Override
	public float getNumber(String key) {
		return getNumber(key, 0);
	}

	@Override
	public float getNumber(String key, float def) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).floatValue() : def;
	}

	@Override
	public int getInt(String key) {
		return getInt(key, 0);
	}

	@Override
	public int getInt(String key, int def) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).intValue() : def;
	}

	@Override
	public long getLong(String key) {
		return getLong(key, 0L);
	}

	@Override
	public long getLong(String key, long def) {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).longValue() : def;
	}

	@Override
	public Json.Object getObject(String key) {
		return getObject(key, null);
	}

	@Override
	public Json.Object getObject(String key, Json.Object def) {
		Object o = get(key);
		return (o instanceof JsonObject) ? (JsonObject) o : def;
	}

	@Override
	public String getString(String key) {
		return getString(key, null);
	}

	@Override
	public String getString(String key, String def) {
		Object o = get(key);
		return (o instanceof String) ? (String) o : def;
	}

	@Override
	public boolean containsKey(String key) {
		return _orderedMap.containsKey(key);
	}

	@Override
	public boolean isArray(String key) {
		return get(key) instanceof Json.Array;
	}

	@Override
	public boolean isBoolean(String key) {
		return get(key) instanceof Boolean;
	}

	@Override
	public boolean isNull(String key) {
		return get(key) == null;
	}

	@Override
	public boolean isNumber(String key) {
		return get(key) instanceof Number;
	}

	@Override
	public boolean isString(String key) {
		return get(key) instanceof String;
	}

	@Override
	public boolean isObject(String key) {
		return get(key) instanceof Json.Object;
	}

	@Override
	public TypedArray<String> keys() {
		return new JsonStringTypedArray(_orderedMap.keys());
	}

	@Override
	public JsonObject put(String key, Object vl) {
		JsonImpl.checkJsonType(vl);
		_orderedMap.put(key, vl);
		return this;
	}

	@Override
	public JsonObject remove(String key) {
		_orderedMap.remove(key);
		return this;
	}

	@Override
	public String toString() {
		return _orderedMap.toString();
	}

	@Override
	public <T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink) {
		for (Entry<String, Object> entry : _orderedMap.entries()) {
			sink.value(entry.key, entry.value);
		}
		return sink;
	}

	Object get(String key) {
		return _orderedMap.get(key);
	}
}
