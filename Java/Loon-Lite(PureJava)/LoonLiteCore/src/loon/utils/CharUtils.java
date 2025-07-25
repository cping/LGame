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

	public enum CharType {
		CharWhitespace, CharAlphaNumeric, CharJapanese, CharKorean, CharChinese, CharUnknown
	}

	public static final char MIN_HIGH_SURROGATE = '\uD800';

	public static final char MAX_HIGH_SURROGATE = '\uDBFF';

	public static final char MIN_LOW_SURROGATE = '\uDC00';

	public static final char MAX_LOW_SURROGATE = '\uDFFF';

	public static final char MIN_SURROGATE = MIN_HIGH_SURROGATE;

	public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;

	static final private class HexChars {

		static final char[] TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };

		static final char[] ZERO_WIDTH_CHARS = new char[] { '\u200B', '\uFEFF', '\u200C', '\u200D' };
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

	public static byte toSByte(int b) {
		return getUNByteToSByte(b);
	}

	public static byte getUNByteToSByte(int b) {
		byte result = 0;
		if (b > 127) {
			result = (byte) (b - 256);
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
		final byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >>> 24);
		bytes[1] = (byte) ((value >> 16) & 0xff);
		bytes[2] = (byte) ((value >> 8) & 0xff);
		bytes[3] = (byte) (value & 0xff);
		return toHex(bytes, true);
	}

	public static String toHex(long value) {
		final byte[] bytes = new byte[8];
		final int v1 = (int) (value >>> 32);
		bytes[0] = (byte) (v1 >>> 24);
		bytes[1] = (byte) ((v1 >> 16) & 0xff);
		bytes[2] = (byte) ((v1 >> 8) & 0xff);
		bytes[3] = (byte) (v1 & 0xff);
		final int v2 = (int) (value & 0xffffffffL);
		bytes[4] = (byte) (v2 >>> 24);
		bytes[5] = (byte) ((v2 >> 16) & 0xff);
		bytes[6] = (byte) ((v2 >> 8) & 0xff);
		bytes[7] = (byte) (v2 & 0xff);
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

	public static boolean isJapanese(int c) {
		return isJapaneseHiragana(c) || isJapaneseKatakana(c);
	}

	public static boolean isJapaneseHiragana(int c) {
		return c >= 0x3040 && c <= 0x309f;
	}

	public static boolean isJapaneseKatakana(int c) {
		return c >= 0x30a0 && c <= 0x30ff;
	}

	public static boolean isKorean(int c) {
		return c >= 0x1100 && c <= 0x11ff;
	}

	public static boolean isChinese(int c) {
		return c >= 0x4e00 && c <= 0x9fa5;
	}

	public static boolean isCJK(int c) {
		return isChinese(c) || isJapanese(c) || isKorean(c);
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

	public static boolean isPunctuation(int c) {
		return (c >= 0x20 && c <= 0x2f) || (c >= 0x3A && c <= 0x40) || (c >= 0x5B && c <= 0x60)
				|| (c >= 0x7B && c <= 0x7F) || (c >= 0xff01 && c <= 0xff5e) || (c >= 0x2000 && c <= 0x2e44)
				|| (c >= 0x3000 && c <= 0x303f);
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
		return c == 0 || c == LSystem.SPACE || c == LSystem.LF || c == LSystem.CR || c == LSystem.TF || c == LSystem.PB;
	}

	public static boolean isEol(char c) {
		return c == LSystem.LF || c == LSystem.CR;
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

	public static boolean isZeroWidthChar(int ch) {
		final char[] chars = HexChars.ZERO_WIDTH_CHARS;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ch) {
				return true;
			}
		}
		return false;
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

	public static boolean isSymbol(char c) {
		return c != '_' && ((c >= '!' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`')
				|| (c >= '{' && c <= '~') || c == '\t' || c == ' ');
	}

	public static boolean isControl(char c) {
		return (c <= 0x001f) || (c >= 0x007f && c <= 0x009f);
	}

	public static boolean isLinebreak(char c) {
		return (c >= 0x000a && c <= 0x000d) || (c == 0x0085) || (c == 0x2028) || (c == 0x2029);
	}

	public static boolean isPunct(char c) {
		return (c >= ' ' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '^') || (c == '`')
				|| (c >= '{' && c <= '~') || (c >= 0x2000 && c <= 0x206f) || (c >= 0x3000 && c <= 0x303f);
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

	private static int lineBegin(CharSequence chars, int pos) {
		while (pos > 0 && !isEol(chars.charAt(pos - 1))) {
			pos--;
		}
		return pos;
	}

	private static int lineEnd(CharSequence chars, int cursor) {
		return lineEnd(chars, cursor, false);
	}

	private static int lineEnd(CharSequence chars, int cursor, boolean include) {
		while (cursor < chars.length() && !isEol(chars.charAt(cursor))) {
			cursor++;
		}
		if (include && cursor < chars.length()) {
			if (chars.charAt(cursor) == LSystem.CR && chars.charAt(cursor + 1) == LSystem.LF) {
				cursor += 2;
			} else {
				cursor++;
			}
		}

		return cursor;
	}

	public static CharType getCharType(char c) {
		if (isWhitespace(c)) {
			return CharType.CharWhitespace;
		} else if (isLetterOrDigit(c)) {
			return CharType.CharAlphaNumeric;
		} else if (isJapanese(c)) {
			return CharType.CharJapanese;
		} else if (isKorean(c)) {
			return CharType.CharKorean;
		} else if (isChinese(c)) {
			return CharType.CharChinese;
		} else {
			return CharType.CharUnknown;
		}
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

	public static int previousWord(CharSequence chars, int cursor) {
		if (chars == null || chars.length() == 0) {
			return 0;
		}
		cursor = MathUtils.min(cursor, chars.length());
		int begin;
		int i;
		int cr;
		int lf;
		lf = lineBegin(chars, cursor) - 1;
		if (lf > 0 && chars.charAt(lf) == LSystem.LF && chars.charAt(lf - 1) == LSystem.CR) {
			cr = lf - 1;
		} else {
			cr = lf;
		}

		if (cursor - 1 == lf) {
			return (cr > 0) ? cr : 0;
		}
		CharType ct = getCharType(chars.charAt(cursor - 1));
		begin = lf + 1;
		i = cursor;

		while (i > begin && getCharType(chars.charAt(i - 1)) == ct) {
			i--;
		}

		if (ct == CharType.CharWhitespace && i > begin) {
			ct = getCharType(chars.charAt(i - 1));
			while (i > begin && getCharType(chars.charAt(i - 1)) == ct) {
				i--;
			}
		}

		return i;
	}

	public static int nextWord(CharSequence chars, int cursor) {
		if (chars == null || chars.length() == 0) {
			return 0;
		}
		int i, lf, cr;
		cr = lineEnd(chars, cursor);
		if (cursor >= chars.length()) {
			return cursor;
		}
		if (cr < chars.length() && chars.charAt(cr) == LSystem.CR && cr + 1 < chars.length()
				&& chars.charAt(cr + 1) == LSystem.LF) {
			lf = cr + 1;
		} else {
			lf = cr;
		}
		if (cursor == cr || cursor == lf) {
			if (lf < chars.length()) {
				return lf + 1;
			}
			return cursor;
		}
		i = cursor;
		while (i < cr && isWhitespace(chars.charAt(i))) {
			i++;
		}
		if (i >= cr) {
			return i;
		}
		CharType codeType = getCharType(chars.charAt(i));
		while (i < cr && getCharType(chars.charAt(i)) == codeType) {
			i++;
		}
		return i;
	}

}
