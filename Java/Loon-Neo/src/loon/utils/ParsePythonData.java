package loon.utils;

import loon.BaseIO;
import loon.LSystem;

/**
 * 工具类，用来解析python游戏的配置数据 
 */
public class ParsePythonData {

	static ObjectMap<String, Object> CACHE_DATA;

	private StringBuffer buf = new StringBuffer();

	private int lineNo = 1, pos;
	
	final static char EOF = (char) -1;

	public synchronized static Object parseString(String str) throws Exception {
		if (CACHE_DATA == null) {
			CACHE_DATA = new ObjectMap<String, Object>();
		}
		Object o = CACHE_DATA.get(str);
		if (o == null) {
			o = new ParsePythonData().parseAll(new ArrayByte(str.getBytes()));
			CACHE_DATA.put(str, o);
		}
		return o;
	}

	public synchronized static Object parseFile(String file) throws Exception {
		if (CACHE_DATA == null) {
			CACHE_DATA = new ObjectMap<String, Object>();
		}
		Object o = CACHE_DATA.get(file);
		if (o == null) {
			o = new ParsePythonData().parseAll(BaseIO.loadArrayByte(file));
			CACHE_DATA.put(file, o);
		}
		return o;
	}


	private Object parse(ArrayByte in) throws Exception {
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
		if (i == EOF) {
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

	public Object parseAll(ArrayByte in) throws Exception {
		Object o = parse(in);
		char i = readA(in);
		if (i == EOF) {
			in.close();
			return o;
		}
		in.close();
		return o;
	}

	private void pushBack(char c) {
		buf.append(c);
	}

	private void confirm(char i, char c) throws Exception {
		if (i != c) {
			throw LSystem.runThrow("Expected to read " + c + " but " + i + "("
					+ ((int) i) + ") found" + at());
		}
	}

	private void confirm(ArrayByte in, char c) throws Exception {
		char i = readA(in);
		confirm(i, c);
	}

	private char read(ArrayByte in) throws Exception {
		char c = (char) in.read();
		if (c == '\n') {
			lineNo++;
			pos = 0;
		} else {
			pos++;
		}
		return c;
	}

	private char readA(ArrayByte in) throws Exception {
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

	private Object readNumber(ArrayByte in, char first) throws Exception {
		StringBuffer sbr = new StringBuffer();
		sbr.append(first);
		for (;;) {
			char i = xread(in);
			if (i == EOF || i == ' ' || i == '\n' || i == '\r' || i == '\t'
					|| i == ',' || i == '}' || i == ')' || i == ']' || i == ':') {
				pushBack(i);
				break;
			}
			sbr.append(i);
		}
		return sbr.toString();
	}

	private void readList(ArrayByte in, TArray<Object> l, char end)
			throws Exception {
		for (;;) {
			char i = readA(in);
			if (i == EOF) {
				throw LSystem.runThrow("Expected to read " + end
						+ " but EOF found" + at());
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

	private void readMap(ArrayByte in, ObjectMap<Object, Object> m, char end)
			throws Exception {
		for (;;) {
			char i = readA(in);
			if (i == EOF) {
				throw LSystem.runThrow("Expected to read " + end
						+ " but EOF found" + at());
			}
			if (i == end) {
				return;
			}
			pushBack(i);
			Object key = parse(in);
			confirm(in, ':');
			Object value = parse(in);
			m.put(key, value);
			i = readA(in);
			if (i == end) {
				return;
			}
			confirm(i, ',');
		}
	}

	private String readString(ArrayByte in, char end) throws Exception {
		StringBuffer sb = new StringBuffer();
		char i = xread(in);
		for (;;) {
			if (i == end) {
				char flag = xread(in);
				if (flag == end && (flag == '"' || flag == '\'')) {
					sb.append(flag);
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
			if (i == EOF) {
				throw LSystem.runThrow("Expected to read " + end
						+ " but EOF found" + at());
			}
			sb.append(i);
			i = xread(in);
		}
		return sb.toString();

	}

	private char xread(ArrayByte in) throws Exception {
		int len = buf.length();
		if (len > 0) {
			char i = buf.charAt(len - 1);
			buf.setLength(len - 1);
			return i;
		}
		return read(in);
	}

	private String at() {
		return " at line:" + lineNo + " pos:" + pos;
	}

	private void skipUtil(ArrayByte in, String end) throws Exception {
		LoopStringBuilder loopBuilder = new LoopStringBuilder(end.length());
		for (;;) {
			char b;
			if ((b = xread(in)) == EOF) {
				return;
			}
			loopBuilder.add(b);
			if (loopBuilder.get().equals(end)) {
				break;
			}
		}
	}

}
