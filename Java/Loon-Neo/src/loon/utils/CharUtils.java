package loon.utils;

public class CharUtils {

	public static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static char toChar(byte b) {
		return (char) (b & 0xFF);
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
		return isDigit(c) || isAlpha(c) || (c == '_') || (c == '.')
				|| (c == '[') || (c == ']');
	}

	public static boolean isAlpha(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	public static boolean isHexDigit(char c) {
		return (c >= '0' && c <= '9') || ((c >= 'a') && (c <= 'f'))
				|| ((c >= 'A') && (c <= 'F'));
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
		return isAlpha(c) || isDigit(c) || c == '-' || c == '.' || c == '_'
				|| c == '~';
	}

	protected static boolean isPchar(char c) {
		return isUnreserved(c) || isSubDelimiter(c) || c == ':' || c == '@';
	}

	protected static boolean isLetterOrDigit(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
				|| (ch >= '0' && ch <= '9');
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
			throw new IllegalArgumentException("Not a hex: " + c);
		}
	}

	public static char int2hex(int i) {
		return HEX_CHARS[i];
	}

}
