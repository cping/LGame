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
package loon.utils.parse;

import loon.BaseIO;
import loon.LSysException;
import loon.utils.ArrayByte;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.TArray;

/**
 * 工具类，用于解析python游戏的配置数据
 */
public final class ParserPythonData {

	protected class LoopStringBuilder {

		private char[] _chars;
		private int _pos;
		private int _size;

		protected LoopStringBuilder(int size) {
			this._size = size;
			this._pos = 0;
			_chars = new char[size];
		}

		public void add(char c) {
			_chars[_pos++] = c;
			if (_pos >= _size) {
				_pos = 0;
			}
		}

		public int getPos() {
			return this._pos;
		}

		public int getSize() {
			return this._size;
		}

		public String get() {
			int q = _pos;
			StrBuilder sbr = new StrBuilder();
			for (int i = 0; i < _size; i++) {
				sbr.append(_chars[q++]);
				if (q >= _size) {
					q = 0;
				}
			}
			return sbr.toString();
		}

	}

	private StrBuilder _buffer = new StrBuilder();

	private int _lineNo = 1, _pos = 0;

	public static Object parseString(String str) {
		return new ParserPythonData().parseAll(new ArrayByte(str.getBytes()));
	}

	public static Object parseFile(String file) {
		return new ParserPythonData().parseAll(BaseIO.loadArrayByte(file));
	}

	private Object parse(ArrayByte in) {
		char i = readA(in);
		if (i == '/') {
			char flag = xread(in);
			if (flag == '*') {
				skipUtil(in, "*/");
				i = readA(in);
			} else {
				pushBack(flag);
			}
		}
		if (i == (char) -1) {
			return null;
		}
		if (i == '{') {
			ObjectMap<Object, Object> m = new ObjectMap<Object, Object>();
			readMap(in, m, '}');
			return m;
		}
		if (i == '[') {
			TArray<Object> l = new TArray<Object>();
			readList(in, l, ']');
			return l;
		}
		if (i == '(') {
			TArray<Object> l = new TArray<Object>();
			readList(in, l, ')');
			return l;
		}
		if (i == '"') {
			String s = readString(in, '"');
			return s;
		}
		if (i == '\'') {
			String s = readString(in, '\'');
			return s;
		}
		return readNumber(in, i);
	}

	public Object parseAll(ArrayByte in) {
		Object o = parse(in);
		char i = readA(in);
		if (i == (char) -1) {
			in.close();
			return o;
		}
		in.close();
		return o;
	}

	private void pushBack(char c) {
		_buffer.append(c);
	}

	private void confirm(char i, char c) throws LSysException {
		if (i != c) {
			throw new LSysException("Expected to read " + c + " but " + i + "(" + ((int) i) + ") found" + at());
		}
	}

	private void confirm(ArrayByte in, char c) {
		char i = readA(in);
		confirm(i, c);
	}

	private char read(ArrayByte in) {
		char c = (char) in.read();
		if (c == '\n') {
			_lineNo++;
			_pos = 0;
		} else {
			_pos++;
		}
		return c;
	}

	private char readA(ArrayByte in) {
		char i = xread(in);
		for (;;) {
			while (i == '\n' || i == '\r' || i == ' ' || i == '\t') {
				i = xread(in);
			}
			if (i == '/') {
				char flag = xread(in);
				if (flag == '*') {
					skipUtil(in, "*/");
					i = xread(in);
				} else {
					pushBack(flag);
					return i;
				}
			} else {
				return i;
			}
		}
	}

	private Object readNumber(ArrayByte in, char first) {
		StrBuilder sbr = new StrBuilder();
		sbr.append(first);
		for (;;) {
			char i = xread(in);
			if (i == (char) -1 || i == ' ' || i == '\n' || i == '\r' || i == '\t' || i == ',' || i == '}' || i == ')'
					|| i == ']' || i == ':') {
				pushBack(i);
				break;
			}
			sbr.append(i);
		}
		return sbr.toString();
	}

	private void readList(ArrayByte in, TArray<Object> l, char end) {
		for (;;) {
			char i = readA(in);
			if (i == (char) -1) {
				throw new LSysException("Expected to read " + end + " but (char) -1 found" + at());
			}
			if (i == end) {
				return;
			}
			pushBack(i);
			Object e = parse(in);
			l.add(e);
			i = readA(in);
			if (i == end) {
				return;
			}
			confirm(i, ',');
		}
	}

	private void readMap(ArrayByte in, ObjectMap<Object, Object> m, char end) {
		for (;;) {
			char i = readA(in);
			if (i == (char) -1) {
				throw new LSysException("Expected to read " + end + " but (char) -1 found" + at());
			}
			if (i == end) {
				return;
			}
			pushBack(i);
			Object key = parse(in);
			confirm(in, ':');
			Object vl = parse(in);
			m.put(key, vl);
			i = readA(in);
			if (i == end) {
				return;
			}
			confirm(i, ',');
		}
	}

	private String readString(ArrayByte in, char end) {
		StrBuilder sbr = new StrBuilder();
		char i = xread(in);
		for (;;) {
			if (i == end) {
				char flag = xread(in);
				if (flag == end && (flag == '"' || flag == '\'')) {
					sbr.append(flag);
					i = xread(in);
					continue;
				} else {
					pushBack(flag);
					break;
				}
			}
			if (i == '\\') {
				i = xread(in);
			}
			if (i == (char) -1) {
				throw new LSysException("Expected to read " + end + " but (char) -1 found" + at());
			}
			sbr.append(i);
			i = xread(in);
		}
		return sbr.toString();

	}

	private char xread(ArrayByte in) {
		int len = _buffer.length();
		if (len > 0) {
			char i = _buffer.charAt(len - 1);
			_buffer.setLength(len - 1);
			return i;
		}
		return read(in);
	}

	private String at() {
		return " at line:" + _lineNo + " pos:" + _pos;
	}

	private void skipUtil(ArrayByte in, String end) {
		LoopStringBuilder loopBuilder = new LoopStringBuilder(end.length());
		for (;;) {
			char b;
			if ((b = xread(in)) == (char) -1) {
				return;
			}
			loopBuilder.add(b);
			if (loopBuilder.get().equals(end)) {
				break;
			}
		}
	}

}
