/**
 * 
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.build.tools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class StringUtils {

	public final static Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	public final static Pattern VALID_PHONE_REGEX = Pattern
			.compile("^\\(\\d{3}\\)\\d{3}\\-\\d{4}$");

	private static final String whiteRange = "\\p{javaWhitespace}\\p{Zs}";
	private static final Pattern whiteStart = Pattern.compile("^[" + whiteRange
			+ "]+");
	private static final Pattern whiteEnd = Pattern.compile("[" + whiteRange
			+ "]+$");

	public static final String ASCII_CHARSET = "US-ASCII";

	public static final String ISO88591_CHARSET = "ISO-8859-1";

	private StringUtils() {
	}

	public static boolean isBoolean(String o) {
		String str = o.trim().toLowerCase();
		return str.equals("true") || str.equals("false") || str.equals("yes")
				|| str.equals("no") || str.equals("ok");
	}

	public static boolean toBoolean(String o) {
		String str = o.trim().toLowerCase();
		if (str.equals("true") || str.equals("yes") || str.equals("ok")) {
			return true;
		} else if (MathUtils.isNan(str)) {
			return Double.parseDouble(str) > 0;
		}
		return false;
	}

	public static String join(String flag, Object... o) {
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < o.length; i++) {
			sbr.append(o[i]);
			sbr.append(flag);
		}
		return sbr.toString();
	}

	public static String toWithCenteredLinedBreaks(String[] lines) {
		final StringBuilder sbr = new StringBuilder(
				"<html><body style='width: 100%'><div align=center>");
		boolean first = true;
		for (String line : lines) {
			if (!first) {
				sbr.append("<br>");
			}
			sbr.append("<p>").append(line).append("</p>");
			first = false;
		}
		sbr.append("</div></body></html>");
		return sbr.toString();
	}

	public static String ltrim(String text) {
		if (text == null) {
			return "";
		}
		Matcher mStart = whiteStart.matcher(text);
		return mStart.find() ? text.substring(mStart.end()) : text;
	}

	public static String rtrim(String text) {
		if (text == null) {
			return "";
		}
		Matcher mEnd = whiteEnd.matcher(text);
		if (mEnd.find()) {
			int matchStart = mEnd.start();
			return text.substring(0, matchStart);
		} else {
			return text;
		}
	}

	public static String trim(String text) {
		return (rtrim(ltrim(text.trim()))).trim();
	}

	public final static boolean startsWith(String n, char tag) {
		return n.charAt(0) == tag;
	}

	public final static boolean endsWith(String n, char tag) {
		return n.charAt(n.length() - 1) == tag;
	}

	public static boolean isNumber(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		int start = (chars[0] == '-') ? 1 : 0;
		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				int i = start + 2;
				if (i == sz) {
					return false;
				}
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9')
							&& (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--;
		int i = start;
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;
			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				if (hasExp) {
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				return false;
			}
			if (!allowSigns
					&& (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit && !hasExp;
			}
			return false;
		}
		return !allowSigns && foundDigit;
	}

	/**
	 * 拼接指定对象数组为String
	 * 
	 * @param res
	 * @return
	 */
	public static String concat(Object... res) {
		StringBuffer sbr = new StringBuffer(res.length);
		for (int i = 0; i < res.length; i++) {
			if (res[i] instanceof Integer) {
				sbr.append((Integer) res[i]);
			} else {
				sbr.append(res[i]);
			}
		}
		return sbr.toString();
	}

	/**
	 * 判定是否由纯粹的西方字符组成
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isEnglishAndNumeric(String string) {
		if (string == null || string.length() == 0) {
			return false;
		}
		char[] chars = string.toCharArray();
		int size = chars.length;
		for (int j = 0; j < size; j++) {
			char letter = chars[j];
			if ((97 > letter || letter > 122) && (65 > letter || letter > 90)
					&& (48 > letter || letter > 57)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判定是否为半角符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSingle(final char c) {
		return (':' == c || '：' == c)
				|| (',' == c || '，' == c)
				|| ('"' == c || '“' == c)
				|| ((0x0020 <= c)
						&& (c <= 0x007E)
						&& !((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))
						&& !('0' <= c) && (c <= '9'));

	}

	/**
	 * 分解字符串
	 * 
	 * @param string
	 * @param tag
	 * @return
	 */
	public static String[] split(final String string, final String tag) {
		StringTokenizer str = new StringTokenizer(string, tag);
		String[] result = new String[str.countTokens()];
		int index = 0;
		for (; str.hasMoreTokens();) {
			result[index++] = str.nextToken();
		}
		return result;
	}

	/**
	 * 过滤指定字符串
	 * 
	 * @param string
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static final String replace(String string, String oldString,
			String newString) {
		if (string == null)
			return null;
		if (newString == null)
			return string;
		int i = 0;
		if ((i = string.indexOf(oldString, i)) >= 0) {
			char string2[] = string.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(string2.length);
			buf.append(string2, 0, i).append(newString2);
			i += oLength;
			int j;
			for (j = i; (i = string.indexOf(oldString, i)) > 0; j = i) {
				buf.append(string2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(string2, j, string2.length - j);
			return buf.toString();
		} else {
			return string;
		}
	}

	/**
	 * 不匹配大小写的过滤指定字符串
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString) {
		if (line == null)
			return null;
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			char line2[] = line.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j;
			for (j = i; (i = lcLine.indexOf(lcOldString, i)) > 0; j = i) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(line2, j, line2.length - j);
			return buf.toString();
		} else {
			return line;
		}
	}

	/**
	 * 不匹配大小写的过滤指定字符串
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @param count
	 * @return
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString, int count[]) {
		if (line == null)
			return null;
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			int counter = 1;
			char line2[] = line.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j;
			for (j = i; (i = lcLine.indexOf(lcOldString, i)) > 0; j = i) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		} else {
			return line;
		}
	}

	/**
	 * 以指定条件过滤字符串
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @param count
	 * @return
	 */
	public static final String replace(String line, String oldString,
			String newString, int count[]) {
		if (line == null)
			return null;
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			int counter = 1;
			char line2[] = line.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j;
			for (j = i; (i = line.indexOf(oldString, i)) > 0; j = i) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		} else {
			return line;
		}
	}

	/**
	 * 检查一组字符串是否完全由中文组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChinaLanguage(char[] chars) {
		int[] ints = new int[2];
		boolean isChinese = false;
		int length = chars.length;
		byte[] bytes = null;
		for (int i = 0; i < length; i++) {
			bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 2) {
				ints[0] = bytes[0] & 0xff;
				ints[1] = bytes[1] & 0xff;
				if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
						&& ints[1] <= 0xFE) {
					isChinese = true;
				}
			} else {
				return false;
			}
		}
		return isChinese;
	}

	public static boolean isChinese(char c) {
		return c >= 0x4e00 && c <= 0x9fa5;
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(String param) {
		return param == null || param.length() == 0 || param.trim().equals("");
	}

	/**
	 * 检查指定字符串中是否存在中文字符。
	 * 
	 * @param checkStr
	 *            指定需要检查的字符串。
	 * @return 逻辑值（True Or False）。
	 */
	public static final boolean hasChinese(String checkStr) {
		boolean checkedStatus = false;
		boolean isError = false;
		String spStr = " _-";
		int checkStrLength = checkStr.length() - 1;
		for (int i = 0; i <= checkStrLength; i++) {
			char ch = checkStr.charAt(i);
			if (ch < '\176') {
				ch = Character.toUpperCase(ch);
				if (((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9'))
						&& (spStr.indexOf(ch) < 0)) {
					isError = true;
				}
			}
		}
		checkedStatus = !isError;
		return checkedStatus;
	}

	/**
	 * 检查是否为纯字母
	 * 
	 * @param value
	 * @return
	 */
	public final static boolean isAlphabet(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		int count = 0;
		for (int i = 0; i < value.length(); i++) {
			char c = Character.toUpperCase(value.charAt(i));
			if ('A' <= c && c <= 'Z') {
				count++;
			}
		}
		return count == value.length();
	}

	/**
	 * 检查是否为字母与数字混合
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isAlphabetNumeric(String value) {
		if (value == null || value.trim().length() == 0)
			return true;
		for (int i = 0; i < value.length(); i++) {
			char letter = value.charAt(i);
			if (('a' > letter || letter > 'z')
					&& ('A' > letter || letter > 'Z')
					&& ('0' > letter || letter > '9'))
				return false;
		}
		return true;
	}

	/**
	 * 替换指定字符串
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static String replaceMatch(String line, String oldString,
			String newString) {
		int i = 0;
		int j = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char line2[] = line.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buffer = new StringBuffer(line2.length);
			buffer.append(line2, 0, i).append(newString2);
			i += oLength;
			for (j = i; (i = line.indexOf(oldString, i)) > 0; j = i) {
				buffer.append(line2, j, i - j).append(newString2);
				i += oLength;
			}
			buffer.append(line2, j, line2.length - j);
			return buffer.toString();
		} else {
			return line;
		}
	}

	/**
	 * 获得特定字符总数
	 * 
	 * @param str
	 * @param chr
	 * @return
	 */
	public static int charCount(String str, char chr) {
		int count = 0;
		if (str != null) {
			int length = str.length();
			for (int i = 0; i < length; i++) {
				if (str.charAt(i) == chr) {
					count++;
				}
			}
			return count;
		}
		return count;
	}

	/**
	 * 批量转换字符串数组编码
	 * 
	 * @param s
	 * @return
	 */
	public static String[] getString(String[] strs, String sourceEncoding,
			String objectEncoding) {
		String[] ss = new String[strs.length];
		try {
			for (int i = 0; i < strs.length; i++) {
				byte[] aa = strs[i].getBytes(sourceEncoding);
				ss[i] = new String(aa, objectEncoding);
			}
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return ss;
	}

	public static byte[] getAsciiBytes(String data) {
		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}
		try {
			return data.getBytes(ASCII_CHARSET);
		} catch (UnsupportedEncodingException e) {
		}
		throw new RuntimeException("LGame requires ASCII support");
	}

	public static String getAsciiString(byte[] data, int offset, int length) {
		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}
		try {
			return new String(data, offset, length, ASCII_CHARSET);
		} catch (UnsupportedEncodingException e) {
		}
		throw new RuntimeException("LGame requires ASCII support");
	}

	public static String getAsciiString(byte[] data) {
		return getAsciiString(data, 0, data.length);
	}

	public static final boolean contains(String input, String pattern) {
		return contains(input, pattern, false);
	}

	public static final boolean contains(String input, String pattern,
			boolean ignoreCase) {
		final int n = pattern.length();
		int last = 0;
		for (int i = 0; i < n;) {
			char c = ' ';
			int j = i;
			for (; j < n; j++) {
				char c2 = pattern.charAt(j);
				if (c2 == ' ' || c2 == '+' || c2 == '*') {
					c = c2;
					break;
				}
			}
			int k = subset(pattern, i, j, input, last, ignoreCase);
			if (k < 0) {
				return false;
			}
			if (c == ' ' || c == '+') {
				last = 0;
			} else if (c == '*') {
				last = k + j - i;
			}
			i = j + 1;
		}
		return true;
	}

	public static boolean containsCharacters(String input, char[] chars) {
		char[] inputChars = input.toCharArray();
		Arrays.sort(inputChars);
		for (int i = 0; i < chars.length; i++) {
			if (Arrays.binarySearch(inputChars, chars[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	private static final int subset(String little, int littleStart,
			int littleStop, String big, int bigStart, boolean ignoreCase) {
		if (ignoreCase) {
			final int n = big.length() - (littleStop - littleStart) + 1;
			outerLoop: for (int i = bigStart; i < n; i++) {
				final int n2 = littleStop - littleStart;
				for (int j = 0; j < n2; j++) {
					char c1 = big.charAt(i + j);
					char c2 = little.charAt(littleStart + j);
					if (c1 != c2 && c1 != toOtherCase(c2)) {
						continue outerLoop;
					}
				}
				return i;
			}
			return -1;
		} else {
			final int n = big.length() - (littleStop - littleStart) + 1;
			outerLoop: for (int i = bigStart; i < n; i++) {
				final int n2 = littleStop - littleStart;
				for (int j = 0; j < n2; j++) {
					char c1 = big.charAt(i + j);
					char c2 = little.charAt(littleStart + j);
					if (c1 != c2) {
						continue outerLoop;
					}
				}
				return i;
			}
			return -1;
		}
	}

	public static final char toOtherCase(char c) {
		int i = c;
		final int A = 'A';
		final int Z = 'Z';
		final int a = 'a';
		final int z = 'z';
		final int SHIFT = a - A;
		if (i < A) {
			return c;
		} else if (i <= Z) {
			return (char) (i + SHIFT);
		} else if (i < a) {
			return c;
		} else if (i <= z) {
			return (char) (i - SHIFT);
		} else {
			return c;
		}
	}

	public static String[] splitNoCoalesce(String s, char delimiter) {
		return splitNoCoalesce(s, Character.toString(delimiter));
	}

	public static String[] splitNoCoalesce(String s, String delimiters) {
		StringTokenizer tokenizer = new StringTokenizer(s, delimiters, true);
		ArrayList<String> tokens = new ArrayList<String>();
		boolean gotDelimiter = true;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.length() == 1 && delimiters.indexOf(token) >= 0) {
				if (gotDelimiter) {
					tokens.add("");
				}
				gotDelimiter = true;
			} else {
				tokens.add(token);
				gotDelimiter = false;
			}
		}
		if (gotDelimiter && !tokens.isEmpty()) {
			tokens.add("");
		}
		return tokens.toArray(new String[0]);
	}

	public static boolean startsWithIgnoreCase(String s, String prefix) {
		final int pl = prefix.length();
		if (s.length() < pl) {
			return false;
		}
		for (int i = 0; i < pl; i++) {
			char sc = s.charAt(i);
			char pc = prefix.charAt(i);
			if (sc != pc) {
				sc = Character.toUpperCase(sc);
				pc = Character.toUpperCase(pc);
				if (sc != pc) {
					sc = Character.toLowerCase(sc);
					pc = Character.toLowerCase(pc);
					if (sc != pc) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static String truncate(final String string, final int maxLen) {
		if (string.length() <= maxLen) {
			return string;
		} else {
			return string.substring(0, maxLen);
		}
	}

	public static String removeDoubleSpaces(String s) {
		return s != null ? s.replaceAll("\\s+", " ") : null;
	}

	public static boolean isEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	public static boolean isPhone(String phoneStr) {
		Matcher matcher = VALID_PHONE_REGEX.matcher(phoneStr);
		return matcher.find();
	}
}
