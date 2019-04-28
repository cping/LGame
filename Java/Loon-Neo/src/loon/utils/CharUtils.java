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

import loon.LSysException;
import loon.LSystem;

public class CharUtils {

	static final private class HexChars {

		static final char[] TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };

	}

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
			return LSystem.EMPTY;
		}
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = HexChars.TABLE[v >>> 4];
			hexChars[j * 2 + 1] = HexChars.TABLE[v & 0x0F];
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
		ob[0] = HexChars.TABLE[(ib >>> 4) & 0X0F];
		ob[1] = HexChars.TABLE[ib & 0X0F];
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
			buf[offset + --charPos] = HexChars.TABLE[((int) val) & mask];
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

	public static boolean isChinese(int c) {
		return c >= 0x4e00 && c <= 0x9fa5;
	}

	public static boolean isEnglishAndNumeric(int letter) {
		return isDigit(letter) || isAsciiLetter(letter);
	}

	public static boolean isSingle(int c) {
		return (':' == c || '：' == c) || (',' == c || '，' == c) || ('"' == c || '“' == c)
				|| ((0x0020 <= c) && (c <= 0x007E) && !((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))
						&& !('0' <= c) && (c <= '9'));
	}

	public static boolean isAsciiLetterDiait(int c) {
		return isDigitCharacter(c) || isAsciiLetter(c);
	}

	public static boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}

	public static boolean isHexDigit(int c) {
		return (c >= '0' && c <= '9') || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
	}

	public static boolean isDigitCharacter(int c) {
		return (c >= '0' && c <= '9') || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
	}

	public static boolean isWhitespace(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	public static boolean isAsciiLetter(int c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isLowercaseAlpha(int c) {
		return (c >= 'a') && (c <= 'z');
	}

	public static boolean isUppercaseAlpha(int c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean isAlpha(int c) {
		return isAsciiLetter(c);
	}

	public static boolean isAlphaOrDigit(int c) {
		return isDigit(c) || isAlpha(c);
	}

	public static boolean isWordChar(int c) {
		return isDigit(c) || isAlpha(c) || (c == '_');
	}

	public static boolean isPropertyNameChar(int c) {
		return isDigit(c) || isAlpha(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
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

	protected static boolean isReserved(int c) {
		return isGenericDelimiter(c) || isSubDelimiter(c);
	}

	protected static boolean isUnreserved(int c) {
		return isAlpha(c) || isDigit(c) || c == '-' || c == '.' || c == '_' || c == '~';
	}

	protected static boolean isPchar(int c) {
		return isUnreserved(c) || isSubDelimiter(c) || c == ':' || c == '@';
	}

	protected static boolean isLetterOrDigit(int ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
	}

	protected static boolean isEscapeExempt(int c) {
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
			throw new LSysException("Not a hex: " + c);
		}
	}

	public static char int2hex(int i) {
		return HexChars.TABLE[i];
	}

}
