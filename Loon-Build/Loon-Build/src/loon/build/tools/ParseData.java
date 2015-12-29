package loon.build.tools;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ParseData {

	static ArrayMap cache = new ArrayMap();

	static char EOF = (char) -1;

	public static Object parseAll(String s) throws Exception {
		Object o = cache.get(s);
		if (o == null) {
			o = new ParseData().parseAll(new StringReader(s));
			cache.put(s, o);
		}
		return o;
	}

	private StringBuffer buf = new StringBuffer();

	private int lineNo = 1, pos;

	void confirm(char i, char c) throws Exception {
		if (i != c) {
			throw new Exception("Expected to read " + c + " but " + i + "("
					+ ((int) i) + ") found" + at());
		}
	}

	void confirm(Reader in, char c) throws Exception {
		char i = readA(in);
		confirm(i, c);
	}

	Object parse(Reader in) throws Exception {
		char i = readA(in);
		if (i == '/') {
			char i2 = xread(in);
			if (i2 == '*') {
				skipUtil(in, "*/");
				i = readA(in);
			} else {
				pushBack(i2);
			}
		}
		if (i == EOF) {
			return null;
		}
		if (i == '{') {
			ArrayMap m = new ArrayMap();
			readMap(in, m, '}');
			return m;
		}
		if (i == '[') {
			ArrayList<Object> l = new ArrayList<Object>();
			readList(in, l, ']');
			return l;
		}
		if (i == '(') {
			ArrayList<Object> l = new ArrayList<Object>();
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
		return readDecimal(in, i);
	}

	public Object parseAll(Reader in) throws Exception {
		Object o = parse(in);
		char i = readA(in);
		if (i == EOF) {
			in.close();
			return o;
		}
		in.close();
		System.err.println("drop char after " + i);
		return o;
	}

	void pushBack(char c) {
		buf.append(c);
	}

	char read(Reader in) throws Exception {
		char c = (char) in.read();
		if (c == '\n') {
			lineNo++;
			pos = 0;
		} else {
			pos++;
		}
		return c;
	}

	char readA(Reader in) throws Exception {
		char i = xread(in);
		while (true) {
			while (i == '\n' || i == '\r' || i == ' ' || i == '\t') {
				i = xread(in);
			}
			// add comment
			if (i == '/') {
				char i2 = xread(in);
				if (i2 == '*') {
					skipUtil(in, "*/");
					i = xread(in);
				} else {
					pushBack(i2);
					return i;
				}
			} else {
				return i;
			}
		}
	}

	Object readDecimal(Reader in, char first) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(first);
		while (true) {
			char i = xread(in);
			if (i == EOF || i == ' ' || i == '\n' || i == '\r' || i == '\t'
					|| i == ',' || i == '}' || i == ')' || i == ']' || i == ':') {
				pushBack(i);
				break;
			}
			sb.append(i);
		}
		try {
			return new BigDecimal(sb.toString());
		} catch (NumberFormatException ex) {
			return sb.toString();
		}
	}

	void readList(Reader in, ArrayList<Object> l, char end) throws Exception {
		while (true) {
			char i = readA(in);
			if (i == EOF) {
				throw new Exception("Expected to read " + end
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

	void readMap(Reader in, ArrayMap m, char end) throws Exception {
		while (true) {
			char i = readA(in);
			if (i == EOF) {
				throw new Exception("Expected to read " + end
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

	String readString(Reader in, char end) throws Exception {
		StringBuffer sb = new StringBuffer();
		char i = xread(in);
		while (true) {
			if (i == end) {
				char i2 = xread(in);
				if (i2 == end && (i2 == '"' || i2 == '\'')) {
					sb.append(i2);
					i = xread(in);
					continue;
				} else {
					pushBack(i2);
					break;
				}
			}
			if (i == '\\') {
				i = xread(in);
			}
			if (i == EOF) {
				throw new Exception("Expected to read " + end
						+ " but EOF found" + at());
			}
			sb.append(i);
			i = xread(in);
		}
		return sb.toString();

	}

	char xread(Reader in) throws Exception {
		int len = buf.length();
		if (len > 0) {
			char i = buf.charAt(len - 1);
			buf.setLength(len - 1);
			return i;
		}
		return read(in);
	}

	public static class LoopStringBuffer {

		private int[] cs;
		private int p;
		private int size;

		LoopStringBuffer(int size) {
			this.size = size;
			p = 0;
			cs = new int[size];
		}

		void add(int c) {
			cs[p++] = (char) c;
			if (p >= size) {
				p = 0;
			}
		}

		public String get() {
			int q = p;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < size; i++) {
				sb.append((char) cs[q++]);
				if (q >= size) {
					q = 0;
				}
			}
			return sb.toString();
		}
	}

	String at() {
		return " at line:" + lineNo + " pos:" + pos;
	}

	private void skipUtil(Reader in, String end) throws Exception {
		LoopStringBuffer lsb = new LoopStringBuffer(end.length());
		while (true) {
			char b;
			if ((b = xread(in)) == EOF) {
				return;
			}
			lsb.add(b);
			if (lsb.get().equals(end)) {
				break;
			}
		}
	}
}
