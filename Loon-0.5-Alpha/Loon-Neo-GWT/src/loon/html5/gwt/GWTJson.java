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
package loon.html5.gwt;

import loon.Json;
import loon.utils.json.JsonImpl;
import loon.utils.json.JsonParserException;
import loon.utils.json.JsonSink;
import loon.utils.json.JsonTypedArray;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

public class GWTJson extends JsonImpl implements Json {
	static final class HtmlArray extends JavaScriptObject implements Json.Array {
		protected HtmlArray() {
		}

		@Override
		public Json.Array add(int index, java.lang.Object value) {
			if (index > length()) {
				set0(index, wrapNative(value));
			} else {
				splice0(index, 0, wrapNative(value));
			}
			return this;
		}

		@Override
		public Json.Array add(java.lang.Object value) {
			push0(wrapNative(value));
			return this;
		}

		@Override
		public Json.Array getArray(int index) {
			return getArray(index, (Json.Array) null);
		}

		@Override
		public final Json.Array getArray(int index, Json.Array dflt) {
			return isValueArray(get0(index)) ? (Json.Array) unwrap0(get0(index))
					: dflt;
		}

		@Override
		public boolean getBoolean(int index) {
			return getBoolean(index, false);
		}

		@Override
		public final boolean getBoolean(int index, boolean dflt) {
			return isValueBoolean(get0(index)) ? unwrapBoolean0(get0(index))
					: dflt;
		}

		@Override
		public float getNumber(int index) {
			return (float) getDouble(index, 0);
		}

		@Override
		public float getNumber(int index, float dflt) {
			return (float) getDouble(index, dflt);
		}

		@Override
		public double getDouble(int index) {
			return getDouble(index, 0);
		}

		@Override
		public final double getDouble(int index, double dflt) {
			return isValueNumber(get0(index)) ? unwrapDouble0(get0(index))
					: dflt;
		}

		@Override
		public int getInt(int index) {
			return (int) getDouble(index, 0);
		}

		@Override
		public int getInt(int index, int dflt) {
			return (int) getDouble(index, dflt);
		}

		@Override
		public long getLong(int index) {
			return (long) getDouble(index, 0);
		}

		@Override
		public long getLong(int index, long dflt) {
			return (long) getDouble(index, dflt);
		}

		@Override
		public Object getObject(int index) {
			return getObject(index, null);
		}

		@Override
		public final Json.Object getObject(int index, Json.Object dflt) {
			return isValueObject(get0(index)) ? (Json.Object) unwrap0(get0(index))
					: dflt;
		}

		@Override
		public String getString(int index) {
			return getString(index, null);
		}

		@Override
		public final String getString(int index, String dflt) {
			return isValueString(get0(index)) ? (String) unwrap0(get0(index))
					: dflt;
		}

		@Override
		public final <T> TypedArray<T> getArray(int index, Class<T> arrayType) {
			return new JsonTypedArray<T>(getArray(index), arrayType);
		}

		@Override
		public boolean isArray(int index) {
			return isValueArray(get0(index));
		}

		@Override
		public boolean isBoolean(int index) {
			return isValueBoolean(get0(index));
		}

		@Override
		public native boolean isNull(int index) /*-{
			return this[index] == null;
		}-*/;

		@Override
		public boolean isNumber(int index) {
			return isValueNumber(get0(index));
		}

		@Override
		public boolean isObject(int index) {
			return isValueObject(get0(index));
		}

		@Override
		public boolean isString(int index) {
			return isValueString(get0(index));
		}

		@Override
		public final native int length() /*-{
			return this.length;
		}-*/;

		private final native java.lang.Object get0(int index) /*-{
			return @com.google.gwt.core.client.GWT::isProdMode()() ? this[index]
					: [ this[index] ];
		}-*/;

		@Override
		public Json.Array remove(int index) {
			remove0(index);
			return this;
		};

		private native void remove0(int index) /*-{
			// splice removes from the end if negative numbers are passed in
			index >= 0 && this.splice(index, 1);
		}-*/;

		@Override
		public Json.Array set(int index, java.lang.Object value) {
			set0(index, wrapNative(value));
			return this;
		}

		@Override
		public <T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink) {
			for (int i = 0; i < length(); i++) {
				java.lang.Object o = get0(i);
				if (o == null || isValueString(o))
					sink.value(unwrapString0(o));
				else if (isValueArray(o))
					sink.array((Json.Array) o);
				else if (isValueObject(o))
					sink.object((Json.Object) o);
				else if (isValueBoolean(o))
					sink.value(unwrapBoolean0(o));
				else if (isValueNumber(o))
					sink.value(unwrapDouble0(o));
				else
					throw new IllegalStateException(
							"Invalid value inside JSON array");
			}
			return sink;
		}

		private native void push0(java.lang.Object value) /*-{
			this.push(@com.google.gwt.core.client.GWT::isProdMode()() ? value
					: value[0]);
		}-*/;

		private native void set0(int index, java.lang.Object value) /*-{
			this[index] = @com.google.gwt.core.client.GWT::isProdMode()() ? value
					: value[0];
		}-*/;

		private native void splice0(int index, int count, java.lang.Object value) /*-{
			this.splice(index, count,
					@com.google.gwt.core.client.GWT::isProdMode()() ? value
							: value[0]);
		}-*/;
	}

	static final class HtmlObject extends JavaScriptObject implements
			Json.Object {
		protected HtmlObject() {
		}

		@Override
		public Array getArray(String key) {
			return getArray(key, (Json.Array) null);
		}

		@Override
		public final Array getArray(String key, Json.Array dflt) {
			return isValueArray(get0(key)) ? (Json.Array) unwrap0(get0(key))
					: dflt;
		}

		@Override
		public boolean getBoolean(String key) {
			return getBoolean(key, false);
		}

		@Override
		public final boolean getBoolean(String key, boolean dflt) {
			return isValueBoolean(get0(key)) ? unwrapBoolean0(get0(key)) : dflt;
		}

		@Override
		public float getNumber(String key) {
			return (float) getDouble(key, 0);
		}

		@Override
		public float getNumber(String key, float dflt) {
			return (float) getDouble(key, dflt);
		}

		@Override
		public double getDouble(String key) {
			return getDouble(key, 0);
		}

		@Override
		public final double getDouble(String key, double dflt) {
			return isValueNumber(get0(key)) ? unwrapDouble0(get0(key)) : dflt;
		}

		@Override
		public int getInt(String key) {
			return (int) getDouble(key, 0);
		}

		@Override
		public int getInt(String key, int dflt) {
			return (int) getDouble(key, dflt);
		}

		@Override
		public long getLong(String key) {
			return (long) getDouble(key, 0);
		}

		@Override
		public long getLong(String key, long dflt) {
			return (long) getDouble(key, dflt);
		}

		@Override
		public Object getObject(String key) {
			return getObject(key, null);
		}

		@Override
		public final Json.Object getObject(String key, Json.Object dflt) {
			return isValueObject(get0(key)) ? (Json.Object) unwrap0(get0(key))
					: dflt;
		}

		@Override
		public String getString(String key) {
			return getString(key, null);
		}

		@Override
		public final String getString(String key, String dflt) {
			return isValueString(get0(key)) ? (String) unwrap0(get0(key))
					: dflt;
		}

		@Override
		public <T> TypedArray<T> getArray(String key, Class<T> valueType,
				TypedArray<T> dflt) {
			Json.Array array = getArray(key);
			if (array == null)
				return dflt;
			return new JsonTypedArray<T>(array, valueType);
		}

		@Override
		public final <T> TypedArray<T> getArray(String key, Class<T> arrayType) {
			return new JsonTypedArray<T>(getArray(key), arrayType);
		}

		@Override
		public final native boolean containsKey(String key) /*-{
			return this.hasOwnProperty(key);
		}-*/;

		@Override
		public TypedArray<String> keys() {
			return new JsonTypedArray<String>(getNativeKeys(), String.class);
		}

		@Override
		public boolean isArray(String key) {
			return isValueArray(get0(key));
		}

		@Override
		public boolean isBoolean(String key) {
			return isValueBoolean(get0(key));
		}

		@Override
		public native boolean isNull(String key) /*-{
			return this[key] == null;
		}-*/;

		@Override
		public boolean isNumber(String key) {
			return isValueNumber(get0(key));
		}

		@Override
		public boolean isObject(String key) {
			return isValueObject(get0(key));
		}

		@Override
		public boolean isString(String key) {
			return isValueString(get0(key));
		}

		@Override
		public Json.Object put(String key, java.lang.Object value) {
			put0(key, wrapNative(value));
			return this;
		}

		@Override
		public Json.Object remove(String key) {
			remove0(key);
			return this;
		};

		private native void remove0(String key) /*-{
			delete this[key];
		}-*/;

		@Override
		public <T extends JsonSink<T>> JsonSink<T> write(JsonSink<T> sink) {
			TypedArray<String> list = keys();
			for (int i = 0; i < list.length(); i++) {
				String key = list.get(i);
				java.lang.Object o = get0(key);
				if (o == null || isValueString(o))
					sink.value(key, unwrapString0(o));
				else if (isValueArray(o))
					sink.array(key, (Json.Array) o);
				else if (isValueObject(o))
					sink.object(key, (Json.Object) o);
				else if (isValueBoolean(o))
					sink.value(key, getBoolean(key));
				else if (isValueNumber(o))
					sink.value(key, getDouble(key));
				else
					throw new IllegalStateException(
							"Invalid value inside JSON object");
			}
			return sink;
		}

		private native JavaScriptObject get0(String key) /*-{
			return @com.google.gwt.core.client.GWT::isProdMode()() ? this[key]
					: [ this[key] ];
		}-*/;

		private native Array getNativeKeys() /*-{
			if (Object.prototype.keys) {
				return this.keys();
			}
			var keys = [];
			for ( var key in this)
				if (this.hasOwnProperty(key)) {
					keys.push(key);
				}
			return keys;
		}-*/;

		private native void put0(String key, java.lang.Object value) /*-{
			this[key] = @com.google.gwt.core.client.GWT::isProdMode()() ? value
					: value[0];
		}-*/;

	}

	@Override
	public Array createArray() {
		return (Json.Array) JavaScriptObject.createArray().cast();
	}

	@Override
	public Object createObject() {
		return (Json.Object) JavaScriptObject.createObject().cast();
	}

	@Override
	public Object parse(String json) throws JsonParserException {
		try {
			JavaScriptObject jsonParse = jsonParse(json);
			if (!isValueObject(jsonParse))
				throw new JsonParserException(null,
						"Input JSON was not an object", -1, -1, -1);
			HtmlObject object = (HtmlObject) unwrap0(jsonParse);
			return object;
		} catch (JavaScriptException e) {
			throw new JsonParserException(e, "Failed to parse JSON", -1, -1, -1);
		}
	}

	@Override
	public Array parseArray(String json) throws JsonParserException {
		try {
			JavaScriptObject jsonParse = jsonParse(json);
			if (!isValueArray(jsonParse))
				throw new JsonParserException(null,
						"Input JSON was not an array", -1, -1, -1);
			HtmlArray array = (HtmlArray) unwrap0(jsonParse);
			return array;
		} catch (JavaScriptException e) {
			throw new JsonParserException(e, "Failed to parse JSON", -1, -1, -1);
		}
	}

	@Override
	public boolean isArray(java.lang.Object o) {
		return isObjectAnArray(o);
	}

	@Override
	public boolean isObject(java.lang.Object o) {
		return isObjectAnObject(o);
	}

	public static boolean isObjectAnArray(java.lang.Object o) {
		return o instanceof JavaScriptObject && isValueArray(wrap0(o));
	}

	public static boolean isObjectAnObject(java.lang.Object o) {
		return o instanceof JavaScriptObject && isValueObject(wrap0(o));
	}

	private static native String unwrapString0(java.lang.Object value) /*-{
		return value;
	}-*/;

	private static native boolean isValueArray(java.lang.Object value) /*-{
		return (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]) instanceof Array;
	}-*/;

	private static native boolean isValueBoolean(java.lang.Object value) /*-{
		return typeof (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]) == "boolean";
	}-*/;

	private static native boolean isValueNumber(java.lang.Object value) /*-{
		return typeof (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]) == "number";
	}-*/;

	private static native boolean isValueObject(java.lang.Object value) /*-{
		return (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]) != null
				&& typeof (@com.google.gwt.core.client.GWT::isProdMode()() ? value
						: value[0]) == "object"
				&& !((@com.google.gwt.core.client.GWT::isProdMode()() ? value
						: value[0]) instanceof Array);
	}-*/;

	private static native boolean isValueString(java.lang.Object value) /*-{
		return typeof (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]) == "string";
	}-*/;

	private static native JavaScriptObject jsonParse(String json) /*-{
		return @com.google.gwt.core.client.GWT::isProdMode()() ? JSON
				.parse(json) : [ JSON.parse(json) ];
	}-*/;

	private static native java.lang.Object unwrap0(java.lang.Object value) /*-{
		return (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]);
	}-*/;

	private static native boolean unwrapBoolean0(java.lang.Object value) /*-{
		return (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]);
	}-*/;

	private static native double unwrapDouble0(java.lang.Object value) /*-{
		return (@com.google.gwt.core.client.GWT::isProdMode()() ? value
				: value[0]);
	}-*/;

	private static native java.lang.Object wrap0(double value) /*-{
		return @com.google.gwt.core.client.GWT::isProdMode()() ? value
				: [ value ];
	}-*/;

	private static native java.lang.Object wrap0(boolean value) /*-{
		return @com.google.gwt.core.client.GWT::isProdMode()() ? value
				: [ value ];
	}-*/;

	private static native java.lang.Object wrap0(java.lang.Object value) /*-{
		return @com.google.gwt.core.client.GWT::isProdMode()() ? value
				: [ value ];
	}-*/;

	private static java.lang.Object wrapNative(java.lang.Object value) {
		if (value == null || value instanceof String) {
			return wrap0(value);
		}
		if (value instanceof JavaScriptObject) {
			return wrap0(value);
		}
		if (value instanceof Number) {
			return wrap0(((Number) value).doubleValue());
		}
		if (value instanceof Boolean) {
			return wrap0(((Boolean) value).booleanValue());
		}
		throw new IllegalArgumentException("Invalid JSON type");
	}

}
