package loon.utils;

import loon.LRelease;
import loon.LSystem;

public class ArrayByteReader implements LRelease {

	private static final byte R = '\r';
	private static final byte N = '\n';

	private final ArrayByte in;

	public ArrayByteReader(ArrayByte stream) {
		in = stream;
	}

	@Override
	public void close() {
		in.close();
	}

	public ArrayByteReader reset() {
		in.reset();
		return this;
	}

	public void skip(long n) {
		if (in == null) {
			return;
		}
		in.skip(n);
	}

	public int read() {
		int c = -1;
		if (in == null) {
			return c;
		}
		return c = in.readByte();
	}

	public int read(byte[] buf) {
		int c = -1;
		if (in == null) {
			return c;
		}
		return c = in.read(buf);
	}

	public int read(byte[] buf, int offset, int length) {
		int c = -1;
		if (in == null) {
			return c;
		}
		return c = in.read(buf, offset, length);
	}

	public String readLine() {
		if (in == null) {
			return LSystem.EMPTY;
		}
		if (in.available() <= 0) {
			return null;
		}
		StrBuilder sbr = new StrBuilder();
		int c = -1;
		boolean keepReading = true;
		do {
			c = in.readByte();
			switch (c) {
			case N:
				keepReading = false;
				break;
			case R:
				continue;
			case -1:
				return null;
			default:
				sbr.append((char) c);
			}
			if (in.available() <= 0) {
				keepReading = false;
			}
		} while (keepReading);
		return sbr.toString();
	}
}
