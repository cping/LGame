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

	public final static StringKeyValue at(String name) {
		return new StringKeyValue(name);
	}

	private final int _capacity;

	private String _key;

	private String _value;

	private final Array<CharSequence> _flags;

	private StrBuilder _buffer;

	private boolean _dirty;

	private boolean _init_buffer;

	public StringKeyValue(String key) {
		this(128, key, null);
	}

	public StringKeyValue(int size, String key) {
		this(size, key, null);
	}

	public StringKeyValue(int size, String k, String v) {
		this._capacity = size;
		this._key = k;
		this._value = v;
		this._flags = new Array<CharSequence>();
	}

	private void initBuild() {
		if (!_init_buffer && _buffer == null) {
			_buffer = new StrBuilder(_capacity);
			_init_buffer = true;
		}
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String newKey) {
		this._key = newKey;
	}

	public int getCapacity() {
		return _capacity;
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

	public StringKeyValue addValue(char ch) {
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
		return addValue(LSystem.TAB);
	}

	public StringKeyValue space() {
		return addValue(LSystem.SPACE);
	}

	public StringKeyValue newLine() {
		return addValue(LSystem.CR).addValue(LSystem.LF);
	}

	public StringKeyValue pushBrace() {
		return addValue(LSystem.DELIM_START);
	}

	public StringKeyValue popBrace() {
		return addValue(LSystem.DELIM_END);
	}

	public StringKeyValue pushParen() {
		return addValue(LSystem.PAREN_START);
	}

	public StringKeyValue popParen() {
		return addValue(LSystem.PAREN_END);
	}

	public StringKeyValue pushBracket() {
		return addValue(LSystem.BRACKET_START);
	}

	public StringKeyValue popBracket() {
		return addValue(LSystem.BRACKET_END);
	}

	public StringKeyValue quot() {
		return addValue(LSystem.DOUBLE_QUOTES);
	}

	public StringKeyValue comma() {
		return addValue(LSystem.COMMA);
	}

	public StringKeyValue branch() {
		return addValue(LSystem.BRANCH);
	}

	public StringKeyValue colon() {
		return addValue(LSystem.COLON);
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
		sbr.append(LSystem.DELIM_START);
		for (int i = 0; i < size; i++) {
			sbr.append(values[i]);
			if (i < size - 1) {
				sbr.append(LSystem.COMMA);
			}
		}
		sbr.append(LSystem.DELIM_END);
		return kv(key, sbr.toString());
	}

	public StringKeyValue kv(CharSequence key, Object vl) {
		if (key == null && vl == null) {
			return this;
		}
		if (key != null && vl == null) {
			return addValue(key).addValue(LSystem.EQUAL).addValue(LSystem.UNKNOWN);
		} else if (key != null && vl != null) {
			return addValue(key).addValue(LSystem.EQUAL).addValue(vl.toString());
		}
		return this;
	}

	public StringKeyValue text(CharSequence mes) {
		return addValue(mes);
	}

	public StringKeyValue line(CharSequence mes) {
		return addValue(mes).newLine();
	}

	public TArray<CharSequence> getTags() {
		return new TArray<CharSequence>(_flags);
	}

	public CharSequence removeFirstTag() {
		return _flags.removeFirst();
	}

	public CharSequence removeLastTag() {
		return _flags.removeLast();
	}

	public StringKeyValue addTag(CharSequence tag) {
		_flags.add(tag);
		return this;
	}

	public StringKeyValue removeTag(CharSequence tag) {
		_flags.remove(tag);
		return this;
	}

	public StringKeyValue pushTag(CharSequence tag) {
		_flags.add(tag);
		return addValue("<" + tag + ">");
	}

	public StringKeyValue popTag(CharSequence tag) {
		CharSequence tmp = _flags.pop();
		return addValue("</" + ((tag == null || tag.length() == 0 || " ".equals(tag)) ? tmp : tag) + ">");
	}

	public StringKeyValue popTag() {
		if (_flags.size() > 0) {
			return addValue("</" + _flags.pop() + ">");
		}
		return this;
	}

	public StringKeyValue popTagAll() {
		for (; _flags.hashNext();) {
			addValue("</" + _flags.next() + ">");
		}
		_flags.clear();
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
			_value = _buffer.toString();
			_dirty = false;
		}
		return _value;
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

	public String toData() {
		return toData(null, null);
	}

	public String toData(String left, String right) {
		if (left == null && right == null) {
			return getKey() + getValue();
		}
		return getKey() + left + getValue() + right;
	}

	public String toJson() {
		return "{\"" + getKey() + "\":[{" + getValue() + "}]}";
	}

	public String toHtml() {
		return "<html>\n" + toData() + "\n</html>";
	}

	public String toXml() {
		String key = getKey();
		return "<" + key + ">\n" + getValue() + "\n</" + key + ">";
	}

	public String toToml() {
		return StringUtils.replace("[" + getKey() + "]\n" + getValue(), ",", "\n");
	}

	public String toFormatString(Object[] args) {
		return StringUtils.format(toData(), args);
	}

	@Override
	public String toString() {
		return toData("[", "]");
	}
}
