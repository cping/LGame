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

/**
 * 字符处理用工具类(此类被StringUtils继承,直接使用StringUtils也可以)
 */
public class CharUtils {

	public static final char MIN_HIGH_SURROGATE = '\uD800';

	public static final char MAX_HIGH_SURROGATE = '\uDBFF';

	public static final char MIN_LOW_SURROGATE = '\uDC00';

	public static final char MAX_LOW_SURROGATE = '\uDFFF';

	public static final char MIN_SURROGATE = MIN_HIGH_SURROGATE;

	public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;

	static final private class HexChars {

		static final char[] TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };

	}

	public static byte[] toSBytes(int... bytes) {
		final int size = bytes.length;
		final byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			result[i] = getUNByteToSByte(bytes[i]);
		}
		return result;
	}

	public static int[] toUNBytes(byte... bytes) {
		final int size = bytes.length;
		final int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = getSByteToUNByte(bytes[i]);
		}
		return result;
	}

	public static byte ToSByte(int b) {
		return getUNByteToSByte(b);
	}

	public static byte getUNByteToSByte(int b) {
		byte result = 0;
		if (b > 127) {
			result = (byte) (b - 256);
		} else if (b > 255) {
			b = 255;
		} else {
			result = (byte) b;
		}
		return result;
	}

	public static int toSByte(byte b) {
		return getUNByteToSByte(b);
	}

	public static int getSByteToUNByte(byte b) {
		int result = 0;
		if (b < 0) {
			result = (b + 256);
		} else {
			result = b;
		}
		return result;
	}

	public static int toUNByte(byte b) {
		return getSByteToUNByte(b);
	}

	public static char toChar(byte b) {
		return (char) (b & 0xFF);
	}

	public static long getBytesToLong(final byte[] bytes) {
		return getBytesToLong(bytes, 0, bytes.length);
	}

	public static long getBytesToLong(final byte[] x, final int offset, final int n) {
		switch (n) {
		case 1:
			return x[offset] & 0xFFL;
		case 2:
			return (x[offset + 1] & 0xFFL) | ((x[offset] & 0xFFL) << 8);
		case 3:
			return (x[offset + 2] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8) | ((x[offset] & 0xFFL) << 16);
		case 4:
			return (x[offset + 3] & 0xFFL) | ((x[offset + 2] & 0xFFL) << 8) | ((x[offset + 1] & 0xFFL) << 16)
					| ((x[offset] & 0xFFL) << 24);
		case 5:
			return (x[offset + 4] & 0xFFL) | ((x[offset + 3] & 0xFFL) << 8) | ((x[offset + 2] & 0xFFL) << 16)
					| ((x[offset + 1] & 0xFFL) << 24) | ((x[offset] & 0xFFL) << 32);
		case 6:
			return (x[offset + 5] & 0xFFL) | ((x[offset + 4] & 0xFFL) << 8) | ((x[offset + 3] & 0xFFL) << 16)
					| ((x[offset + 2] & 0xFFL) << 24) | ((x[offset + 1] & 0xFFL) << 32) | ((x[offset] & 0xFFL) << 40);
		case 7:
			return (x[offset + 6] & 0xFFL) | ((x[offset + 5] & 0xFFL) << 8) | ((x[offset + 4] & 0xFFL) << 16)
					| ((x[offset + 3] & 0xFFL) << 24) | ((x[offset + 2] & 0xFFL) << 32)
					| ((x[offset + 1] & 0xFFL) << 40) | ((x[offset] & 0xFFL) << 48);
		case 8:
			return (x[offset + 7] & 0xFFL) | ((x[offset + 6] & 0xFFL) << 8) | ((x[offset + 5] & 0xFFL) << 16)
					| ((x[offset + 4] & 0xFFL) << 24) | ((x[offset + 3] & 0xFFL) << 32)
					| ((x[offset + 2] & 0xFFL) << 40) | ((x[offset + 1] & 0xFFL) << 48) | ((x[offset] & 0xFFL) << 56);
		default:
			throw new LSysException("No bytes specified");
		}
	}

	public static long fromHexToLong(String hexStr) {
		return getBytesToLong(fromHex(hexStr));
	}

	public static byte[] fromHex(String hexStr) {
		if (StringUtils.isEmpty(hexStr)) {
			return new byte[] {};
		}
		byte[] bytes = new byte[hexStr.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			int char1 = hexStr.charAt(i * 2);
			char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
			int char2 = hexStr.charAt(i * 2 + 1);
			char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
			if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15) {
				throw new LSysException("Invalid hex number: " + hexStr);
			}
			bytes[i] = (byte) ((char1 << 4) + char2);
		}
		return bytes;
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
			StrBuilder sbr = new StrBuilder(hexChars.length);
			final char tag = '0';
			boolean flag = false;
			for (char ch : hexChars) {
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

	public static int toInt(char c) {
		char[] chars = HexChars.TABLE;
		for (int i = 0; i < 10; i++) {
			if (c == chars[i]) {
				return i;
			}
		}
		return c;
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
			barr[i] = (byte) (carr[i] <= 0xFF ? carr[i] : 0x3F);
		}
		return barr;
	}

	public static byte[] toAsciiByteArray(CharSequence charSequence) {
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			char c = charSequence.charAt(i);
			barr[i] = (byte) (c <= 0xFF ? c : 0x3F);
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
		return isAsciiLetterDiait(letter);
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
		return isLowercaseAlpha(c) || isUppercaseAlpha(c);
	}

	public static boolean isLowercaseAlpha(int c) {
		return (c >= 'a') && (c <= 'z');
	}

	public static boolean isUppercaseAlpha(int c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean isAlphabetUpper(char letter) {
		return isUppercaseAlpha(letter);
	}

	public static boolean isAlphabetLower(char letter) {
		return isLowercaseAlpha(letter);
	}

	public static boolean isAlphabet(char letter) {
		return isAsciiLetter(letter);
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

	public static boolean isHighSurrogate(char ch) {
		return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
	}

	public static boolean isLowSurrogate(char ch) {
		return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
	}

	public static boolean isSurrogate(char ch) {
		return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
	}

	public static boolean isSurrogatePair(char high, char low) {
		return isHighSurrogate(high) && isLowSurrogate(low);
	}

	public static boolean isInherited(char c) {
		return c == '~';
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

	public static char toUpper(int c) {
		if (isLowercaseAlpha(c)) {
			c -= 0x20;
		}
		return (char) c;
	}

	public static char toLower(int c) {
		if (isUppercaseAlpha(c)) {
			c += 0x20;
		}
		return (char) c;
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
