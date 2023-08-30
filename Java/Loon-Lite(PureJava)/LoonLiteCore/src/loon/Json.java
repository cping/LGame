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
package loon;

import java.util.Iterator;

import loon.utils.TArray;
import loon.utils.json.JsonParserException;
import loon.utils.json.JsonSink;

public interface Json {

	interface TypedArray<T> {

		int length();

		T get(int index);

		T get(int index, T dflt);

		Iterator<T> iterator();

		public static class Util {

			public static TypedArray<Boolean> create(Boolean... data) {
				return Util.<Boolean>toArray(data);
			}

			public static TypedArray<Integer> create(Integer... data) {
				return Util.<Integer>toArray(data);
			}

			public static TypedArray<Float> create(Float... data) {
				return Util.<Float>toArray(data);
			}

			public static TypedArray<Double> create(Double... data) {
				return Util.<Double>toArray(data);
			}

			public static TypedArray<String> create(String... data) {
				return Util.<String>toArray(data);
			}

			public static TypedArray<Object> create(Object... data) {
				return Util.<Object>toArray(data);
			}

			public static TypedArray<Array> create(Array... data) {
				return Util.<Array>toArray(data);
			}

			private static <T> TypedArray<T> toArray(final java.lang.Object[] data) {
				return new TypedArray<T>() {
					@Override
					public int length() {
						return data.length;
					}

					@Override
					public T get(int index) {
						@SuppressWarnings("unchecked")
						T value = (T) data[index];
						return value;
					}

					@Override
					public T get(int index, T dflt) {
						return (index < 0 || index >= data.length) ? dflt : get(index);
					}

					@Override
					public Iterator<T> iterator() {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						TArray<T> list = (TArray<T>) new TArray(data);
						return list.iterator();
					}
				};
			}
		}
	}

	interface Array {

		int length();

		boolean getBoolean(int index);

		boolean getBoolean(int index, boolean dflt);

		float getNumber(int index);

		float getNumber(int index, float dflt);

		double getDouble(int index);

		double getDouble(int index, double dflt);

		int getInt(int index);

		int getInt(int index, int dflt);

		long getLong(int index);

		long getLong(int index, long dflt);

		String getString(int index);

		String getString(int index, String dflt);

		Object getObject(int index);

		Object getObject(int index, Object dflt);

		Array getArray(int index);

		Array getArray(int index, Array dflt);

		boolean isArray(int index);

		boolean isBoolean(int index);

		boolean isNull(int index);

		boolean isNumber(int index);

		boolean isString(int index);

		boolean isObject(int index);

		Array add(java.lang.Object value);

		Array add(int index, java.lang.Object value);

		Array remove(int index);

		Array set(int index, java.lang.Object value);

		<T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink);
	}

	interface Object {

		boolean getBoolean(String key);

		boolean getBoolean(String key, boolean dflt);

		float getNumber(String key);

		float getNumber(String key, float dflt);

		double getDouble(String key);

		double getDouble(String key, double dflt);

		int getInt(String key);

		int getInt(String key, int dflt);

		long getLong(String key);

		long getLong(String key, long dflt);

		String getString(String key);

		String getString(String key, String dflt);

		Object getObject(String key);

		Object getObject(String key, Object dflt);

		Array getArray(String key);

		Array getArray(String key, Array dflt);

		boolean containsKey(String key);

		TypedArray<String> keys();

		boolean isArray(String key);

		boolean isBoolean(String key);

		boolean isNull(String key);

		boolean isNumber(String key);

		boolean isString(String key);

		boolean isObject(String key);

		Object put(String key, java.lang.Object value);

		Object remove(String key);

		<T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink);
	}

	Array createArray();

	Object createObject();

	boolean isArray(java.lang.Object o);

	boolean isObject(java.lang.Object o);

	Object parse(String json) throws JsonParserException;

	Array parseArray(String json) throws JsonParserException;
}
