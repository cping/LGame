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
package loon.utils;

import loon.LSystem;

public class CharUtils {
	
	public static final char[] HEX_CHARS = new char[] {  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static char toChar(byte b) {
		return (char) (b & 0xFF);
	}

	public static String toHex(int value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >>> 24);
		bytes[1] = (byte) ((value >> 16) & 0xff);
		bytes[2] = (byte) ((value >> 8) & 0xff);
		bytes[3] = (byte) (value & 0xff);
		return toHex(bytes, true);
	}

	public static String toHex(byte[] bytes) {
		return toHex(bytes, false);
	}

	public static String toHex(byte[] bytes, boolean removeZero) {
		if (bytes == null) {
			return "";
		}
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_CHARS[v >>> 4];
			hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
		}
		if (removeZero) {
			StringBuilder sbr = new StringBuilder(hexChars.length);
			final char tag = '0';
			boolean flag = false;
			for (int i = 0; i < hexChars.length; i++) {
				char ch = hexChars[i];
				if (ch != tag) {
					flag = true;
				}
				if (flag) {
					sbr.append(ch);
				}
			}
			return sbr.toString();
		} else {
			return new String(hexChars);
		}
	}

	public static String toHex(byte ib) {
		char[] ob = new char[2];
		ob[0] = HEX_CHARS[(ib >>> 4) & 0X0F];
		ob[1] = HEX_CHARS[ib & 0X0F];
		return new String(ob);
	}
	
	public static long b2iu(byte b) {
		return b < 0 ? b & 0x7F + 128 : b;
	}
	
	public static int toUnsignedLong(long val, int shift, char[] buf, int offset, int len) {
		int charPos = len;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			buf[offset + --charPos] = HEX_CHARS[((int) val) & mask];
			val >>>= shift;
		} while (val != 0 && charPos > 0);
		return charPos;
	}

	public static byte[] toSimpleByteArray(char[] carr) {
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) carr[i];
		}
		return barr;
	}

	public static byte[] toSimpleByteArray(CharSequence charSequence) {
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charSequence.charAt(i);
		}
		return barr;
	}

	public static char[] toSimpleCharArray(byte[] barr) {
		char[] carr = new char[barr.length];
		for (int i = 0; i < barr.length; i++) {
			carr[i] = (char) (barr[i] & 0xFF);
		}
		return carr;
	}

	public static int toAscii(char c) {
		if (c <= 0xFF) {
			return c;
		} else {
			return 0x3F;
		}
	}

	public static byte[] toAsciiByteArray(char[] carr) {
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) ((int) (carr[i] <= 0xFF ? carr[i] : 0x3F));
		}
		return barr;
	}

	public static byte[] toAsciiByteArray(CharSequence charSequence) {
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			char c = charSequence.charAt(i);
			barr[i] = (byte) ((int) (c <= 0xFF ? c : 0x3F));
		}
		return barr;
	}

	public static byte[] toRawByteArray(char[] carr) {
		byte[] barr = new byte[carr.length << 1];
		for (int i = 0, bpos = 0; i < carr.length; i++) {
			char c = carr[i];
			barr[bpos++] = (byte) ((c & 0xFF00) >> 8);
			barr[bpos++] = (byte) (c & 0x00FF);
		}
		return barr;
	}

	public static char[] toRawCharArray(byte[] barr) {
		int carrLen = barr.length >> 1;
		if (carrLen << 1 < barr.length) {
			carrLen++;
		}
		char[] carr = new char[carrLen];
		int i = 0, j = 0;
		while (i < barr.length) {
			char c = (char) (barr[i] << 8);
			i++;

			if (i != barr.length) {
				c += barr[i] & 0xFF;
				i++;
			}
			carr[j++] = c;
		}
		return carr;
	}

	public static boolean equalsOne(char c, char[] match) {
		for (char aMatch : match) {
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	public static int findFirstEqual(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match)) {
				return i;
			}
		}
		return -1;
	}

	public static int findFirstEqual(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] == match) {
				return i;
			}
		}
		return -1;
	}

	public static int findFirstDiff(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (!equalsOne(source[i], match)) {
				return i;
			}
		}
		return -1;
	}

	public static int findFirstDiff(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] != match) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isWhitespace(char c) {
		return c <= ' ';
	}

	public static boolean isLowercaseAlpha(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	public static boolean isUppercaseAlpha(char c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean isAlphaOrDigit(char c) {
		return isDigit(c) || isAlpha(c);
	}

	public static boolean isWordChar(char c) {
		return isDigit(c) || isAlpha(c) || (c == '_');
	}

	public static boolean isPropertyNameChar(char c) {
		return isDigit(c) || isAlpha(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
	}

	public static boolean isAlpha(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	public static boolean isHexDigit(char c) {
		return (c >= '0' && c <= '9') || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
	}

	public static boolean isGenericDelimiter(int c) {
		switch (c) {
		case ':':
		case '/':
		case '?':
		case '#':
		case '[':
		case ']':
		case '@':
			return true;
		default:
			return false;
		}
	}

	protected static boolean isSubDelimiter(int c) {
		switch (c) {
		case '!':
		case '$':
		case '&':
		case '\'':
		case '(':
		case ')':
		case '*':
		case '+':
		case ',':
		case ';':
		case '=':
			return true;
		default:
			return false;
		}
	}

	protected static boolean isReserved(char c) {
		return isGenericDelimiter(c) || isSubDelimiter(c);
	}

	protected static boolean isUnreserved(char c) {
		return isAlpha(c) || isDigit(c) || c == '-' || c == '.' || c == '_' || c == '~';
	}

	protected static boolean isPchar(char c) {
		return isUnreserved(c) || isSubDelimiter(c) || c == ':' || c == '@';
	}

	protected static boolean isLetterOrDigit(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
	}

	protected static boolean isEscapeExempt(char c) {
		switch (c) {
		case '*':
		case '@':
		case '-':
		case '_':
		case '+':
		case '.':
		case '/':
			return true;
		default:
			return false;
		}
	}

	public static char toUpperAscii(char c) {
		if (isLowercaseAlpha(c)) {
			c -= (char) 0x20;
		}
		return c;
	}

	public static char toLowerAscii(char c) {
		if (isUppercaseAlpha(c)) {
			c += (char) 0x20;
		}
		return c;
	}

	public static int hex2int(char c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return c - '0';
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
			return c - 55;
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
			return c - 87;
		default:
			throw LSystem.runThrow("Not a hex: " + c);
		}
	}

	public static char int2hex(int i) {
		return HEX_CHARS[i];
	}

}
