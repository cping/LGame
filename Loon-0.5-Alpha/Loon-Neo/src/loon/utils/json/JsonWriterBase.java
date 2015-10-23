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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import loon.Json;

class JsonWriterBase<I extends JsonSink<I>> implements JsonSink<I> {
	
	protected final Appendable appendable;
	private Stack<Boolean> states = new Stack<Boolean>();
	private boolean first = true;
	private boolean inObject;
	private boolean verboseFormat;

	JsonWriterBase(Appendable appendable) {
		this.appendable = appendable;
	}

	@SuppressWarnings("unchecked")
	private I castThis() {
		return (I) this;
	}

	public I useVerboseFormat(boolean verboseFormat) {
		this.verboseFormat = verboseFormat;
		return castThis();
	}

	@Override
	public I array(Collection<?> c) {
		return array(null, c);
	}

	@Override
	public I array(Json.Array c) {
		return array(null, c);
	}

	@Override
	public I array(String key, Collection<?> c) {
		if (key == null)
			array();
		else
			array(key);

		for (Object o : c) {
			value(o);
		}

		return end();
	}

	@Override
	public I array(String key, Json.Array c) {
		if (key == null)
			array();
		else
			array(key);

		c.write(this);

		return end();
	}

	@Override
	public I object(Map<?, ?> map) {
		return object(null, map);
	}

	@Override
	public I object(Json.Object map) {
		return object(null, map);
	}

	@Override
	public I object(String key, Map<?, ?> map) {
		if (key == null) {
			object();
		} else {
			object(key);
		}

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object o = entry.getValue();
			if (!(entry.getKey() instanceof String)) {
				throw new JsonWriterException("Invalid key type for map: "
						+ (entry.getKey() == null ? "null" : entry.getKey()
								.getClass()));
			}
			String k = (String) entry.getKey();
			value(k, o);
		}

		return end();
	}

	@Override
	public I object(String key, Json.Object obj) {
		if (key == null) {
			object();
		} else {
			object(key);
		}
		obj.write(this);
		return end();
	}

	@Override
	public I nul() {
		preValue();
		raw("null");
		return castThis();
	}

	@Override
	public I nul(String key) {
		preValue(key);
		raw("null");
		return castThis();
	}

	@Override
	public I value(Object o) {
		if (o == null) {
			return nul();
		} else if (o instanceof String) {
			return value((String) o);
		} else if (o instanceof Number) {
			return value(((Number) o));
		} else if (o instanceof Boolean) {
			return value((boolean) (Boolean) o);
		} else if (o instanceof Collection) {
			return array((Collection<?>) o);
		} else if (o instanceof Map) {
			return object((Map<?, ?>) o);
		} else if (JsonTypes.isArray(o)) {
			return array((Json.Array) o);
		} else if (JsonTypes.isObject(o)) {
			return object((Json.Object) o);
		} else {
			throw new JsonWriterException("Unable to handle type: "
					+ o.getClass());
		}
	}

	@Override
	public I value(String key, Object o) {
		if (o == null) {
			return nul(key);
		} else if (o instanceof String) {
			return value(key, (String) o);
		} else if (o instanceof Number) {
			return value(key, (Number) o);
		} else if (o instanceof Boolean) {
			return value(key, (boolean) (Boolean) o);
		} else if (o instanceof Collection) {
			return array(key, (Collection<?>) o);
		} else if (o instanceof Map) {
			return object(key, (Map<?, ?>) o);
		} else if (JsonTypes.isArray(o)) {
			return array(key, (Json.Array) o);
		} else if (JsonTypes.isObject(o)) {
			return object(key, (Json.Object) o);
		} else {
			throw new JsonWriterException("Unable to handle type: "
					+ o.getClass());
		}
	}

	@Override
	public I value(String s) {
		if (s == null)
			return nul();
		preValue();
		emitStringValue(s);
		return castThis();
	}

	@Override
	public I value(boolean b) {
		preValue();
		raw(Boolean.toString(b));
		return castThis();
	}

	@Override
	public I value(Number n) {
		preValue();
		if (n == null)
			raw("null");
		else
			raw(n.toString());
		return castThis();
	}

	@Override
	public I value(String key, String s) {
		if (s == null)
			return nul(key);
		preValue(key);
		emitStringValue(s);
		return castThis();
	}

	@Override
	public I value(String key, boolean b) {
		preValue(key);
		raw(Boolean.toString(b));
		return castThis();
	}

	@Override
	public I value(String key, Number n) {
		if (n == null)
			return nul(key);
		preValue(key);
		raw(n.toString());
		return castThis();
	}

	@Override
	public I array() {
		preValue();
		states.push(inObject);
		inObject = false;
		first = true;
		raw('[');
		if (verboseFormat)
			raw('\n');
		return castThis();
	}

	@Override
	public I object() {
		preValue();
		states.push(inObject);
		inObject = true;
		first = true;
		raw('{');
		if (verboseFormat)
			raw('\n');
		return castThis();
	}

	@Override
	public I array(String key) {
		preValue(key);
		states.push(inObject);
		inObject = false;
		first = true;
		raw('[');
		if (verboseFormat)
			raw('\n');
		return castThis();
	}

	@Override
	public I object(String key) {
		preValue(key);
		states.push(inObject);
		inObject = true;
		first = true;
		raw('{');
		if (verboseFormat)
			raw('\n');
		return castThis();
	}

	@Override
	public I end() {
		if (states.size() == 0)
			throw new JsonWriterException("Invalid call to end()");

		boolean wasInObject = inObject;

		first = false;
		inObject = states.pop();

		if (verboseFormat) {
			raw('\n');
			indent();
		}

		if (wasInObject) {
			raw('}');
		} else {
			raw(']');
		}

		return castThis();
	}

	protected void doneInternal() {
		if (states.size() > 0) {
			throw new JsonWriterException(
					"Unclosed JSON objects and/or arrays when closing writer");
		}
		if (first) {
			throw new JsonWriterException(
					"Nothing was written to the JSON writer");
		}
	}

	private void indent() {
		for (int level = 0; level < states.size(); ++level) {
			raw("  ");
		}
	}

	private void raw(String s) {
		try {
			appendable.append(s);
		} catch (IOException e) {
			throw new JsonWriterException(e);
		}
	}

	private void raw(char c) {
		try {
			appendable.append(c);
		} catch (IOException e) {
			throw new JsonWriterException(e);
		}
	}

	private void pre() {
		if (first) {
			first = false;
		} else {
			if (states.size() == 0) {
				throw new JsonWriterException(
						"Invalid call to emit a value in a finished JSON writer");
			}
			raw(',');
			if (verboseFormat) {
				raw('\n');
			}
		}
		if (verboseFormat) {
			indent();
		}
	}

	private void preValue() {
		if (inObject) {
			throw new JsonWriterException(
					"Invalid call to emit a keyless value while writing an object");
		}

		pre();
	}

	private void preValue(String key) {
		if (!inObject) {
			throw new JsonWriterException(
					"Invalid call to emit a key value while not writing an object");
		}

		pre();

		emitStringValue(key);
		raw(':');
	}

	private void emitStringValue(String s) {
		raw('"');
		char b = 0, c = 0;
		for (int i = 0; i < s.length(); i++) {
			b = c;
			c = s.charAt(i);

			switch (c) {
			case '\\':
			case '"':
				raw('\\');
				raw(c);
				break;
			case '/':
				if (b == '<') {
					raw('\\');
				}
				raw(c);
				break;
			case '\b':
				raw("\\b");
				break;
			case '\t':
				raw("\\t");
				break;
			case '\n':
				raw("\\n");
				break;
			case '\f':
				raw("\\f");
				break;
			case '\r':
				raw("\\r");
				break;
			default:
				if (shouldBeEscaped(c)) {
					String t = "000" + Integer.toHexString(c);
					raw("\\u" + t.substring(t.length() - "0000".length()));
				} else {
					raw(c);
				}
			}
		}

		raw('"');
	}

	private boolean shouldBeEscaped(char c) {
		return c < ' ' || (c >= '\u0080' && c < '\u00a0')
				|| (c >= '\u2000' && c < '\u2100');
	}

	static String escape(String s) {
		String json = new JsonStringWriter().value(s).write();
		return json.substring(1, json.length() - 1);
	}
}
