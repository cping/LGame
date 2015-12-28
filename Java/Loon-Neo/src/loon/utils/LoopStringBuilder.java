package loon.utils;

public class LoopStringBuilder {

	private char[] chars;
	private int pos;
	private int size;

	LoopStringBuilder(int size) {
		this.size = size;
		pos = 0;
		chars = new char[size];
	}

	public void add(char c) {
		chars[pos++] = c;
		if (pos >= size) {
			pos = 0;
		}
	}

	public String get() {
		int q = pos;
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sbr.append(chars[q++]);
			if (q >= size) {
				q = 0;
			}
		}
		return sbr.toString();
	}

}
