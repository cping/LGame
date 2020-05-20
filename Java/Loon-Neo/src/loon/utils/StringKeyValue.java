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

import loon.LSystem;

/**
 * 一个字符串键值(key-value)拼接工具,允许value动态输入
 * 
 * <pre>
 * StringKeyValue kev = new StringKeyValue("test");
 * kev.pushTag("code").pushTag("java").text("hello world").popTag().popTag();
 * </pre>
 */
public class StringKeyValue {

	private int capacity;

	private String key;

	private String value;

	private Array<CharSequence> flags;

	private StrBuilder _buffer;

	private boolean _dirty;

	private boolean _init_buffer;

	public StringKeyValue(String key) {
		this(128, key, null);
	}

	public StringKeyValue(int size, String key) {
		this(size, key, null);
	}

	public StringKeyValue(int size, String k, String val) {
		this.capacity = size;
		this.key = k;
		this.value = val;
		this.flags = new Array<CharSequence>();
	}

	private void initBuild() {
		if (!_init_buffer && _buffer == null) {
			_buffer = new StrBuilder(capacity);
			_init_buffer = true;
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String newKey) {
		this.key = newKey;
	}

	public int getCapacity() {
		return capacity;
	}

	public StringKeyValue addValue(boolean ch) {
		initBuild();
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue addValue(long ch) {
		initBuild();
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue addValue(int ch) {
		initBuild();
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue addValue(float ch) {
		initBuild();
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue addValue(CharSequence ch) {
		if (ch == null) {
			return this;
		}
		initBuild();
		_buffer.append(ch);
		_dirty = true;
		return this;
	}

	public StringKeyValue tab() {
		return addValue("	");
	}

	public StringKeyValue space() {
		return addValue(" ");
	}

	public StringKeyValue newLine() {
		return addValue(LSystem.LS);
	}

	public StringKeyValue pushBrace() {
		return addValue("{");
	}

	public StringKeyValue popBrace() {
		return addValue("}");
	}

	public StringKeyValue pushParen() {
		return addValue("(");
	}

	public StringKeyValue popParen() {
		return addValue(")");
	}

	public StringKeyValue pushBracket() {
		return addValue("[");
	}

	public StringKeyValue popBracket() {
		return addValue("]");
	}

	public StringKeyValue quot() {
		return addValue("\"");
	}

	public StringKeyValue comma() {
		return addValue(",");
	}

	public StringKeyValue scomma() {
		return addValue(" , ");
	}

	public StringKeyValue kv(CharSequence key, Object[] values) {
		if (key == null && values == null) {
			return this;
		}
		int size = values.length;
		StrBuilder sbr = new StrBuilder(size + 32);
		sbr.append('{');
		for (int i = 0; i < size; i++) {
			sbr.append(values[i]);
			if (i < size - 1) {
				sbr.append(',');
			}
		}
		sbr.append('}');
		return kv(key, sbr.toString());
	}

	public StringKeyValue kv(CharSequence key, Object value) {
		if (key == null && value == null) {
			return this;
		}
		if (key != null && value == null) {
			return addValue(key).addValue("=").addValue(LSystem.UNKNOWN);
		} else if (key != null && value != null) {
			return addValue(key).addValue("=").addValue(value.toString());
		}
		return this;
	}

	public StringKeyValue text(CharSequence mes) {
		return addValue(mes);
	}

	public TArray<CharSequence> getTags() {
		return new TArray<CharSequence>(flags);
	}

	public CharSequence removeFirstTag() {
		return flags.removeFirst();
	}

	public CharSequence removeLastTag() {
		return flags.removeLast();
	}

	public StringKeyValue addTag(CharSequence tag) {
		flags.add(tag);
		return this;
	}

	public StringKeyValue removeTag(CharSequence tag) {
		flags.remove(tag);
		return this;
	}

	public StringKeyValue pushTag(CharSequence tag) {
		flags.add(tag);
		return addValue("<" + tag + ">");
	}

	public StringKeyValue popTag(CharSequence tag) {
		CharSequence tmp = flags.pop();
		return addValue("</" + ((tag == null || tag.length() == 0 || " ".equals(tag)) ? tmp : tag) + ">");
	}

	public StringKeyValue popTag() {
		if (flags.size() > 0) {
			return addValue("</" + flags.pop() + ">");
		}
		return this;
	}

	public StringKeyValue popTagAll() {
		for (; flags.hashNext();) {
			addValue("</" + flags.next() + ">");
		}
		flags.clear();
		return this;
	}

	public StringKeyValue removeValue() {
		return removeValue(0, _buffer.length());
	}

	public StringKeyValue removeValue(int start, int end) {
		initBuild();
		_buffer.delete(start, end);
		_dirty = true;
		return this;
	}

	public String getValue() {
		if (_dirty && _buffer != null) {
			value = _buffer.toString();
			_dirty = false;
		}
		return value;
	}

	public StringKeyValue clear() {
		if (_buffer != null && _buffer.length() > 0) {
			_buffer.delete(0, _buffer.length());
			_dirty = true;
		}
		return this;
	}

	public int length() {
		return (_buffer != null && _buffer.length() > 0) ? _buffer.length() : 0;
	}

	public char charAt(int i) {
		return (_buffer != null && _buffer.length() < i) ? _buffer.charAt(i) : (char) -1;
	}

	@Override
	public String toString() {
		return getKey() + " [" + getValue() + "]";
	}
}
