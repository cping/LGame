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

final public class StringUtils extends CharUtils {

	private StringUtils() {
	}

	/**
	 * 一个仿C#的String.format实现（因为GWT不支持String.format）
	 * 
	 * @param format
	 * @param params
	 * @return
	 */
	public static final String format(String format, Object... params) {
		StringBuilder b = new StringBuilder();
		int p = 0;
		for (;;) {
			int i = format.indexOf('{', p);
			if (i == -1) {
				break;
			}
			int idx = format.indexOf('}', i + 1);
			if (idx == -1) {
				break;
			}
			if (p != i) {
				b.append(format.substring(p, i));
			}
			String nstr = format.substring(i + 1, idx);
			try {
				int n = Integer.parseInt(nstr);
				if (n >= 0 && n < params.length) {
					b.append(params[n]);
				} else {
					b.append('{').append(nstr).append('}');
				}
			} catch (NumberFormatException e) {
				b.append('{').append(nstr).append('}');
			}
			p = idx + 1;
		}
		b.append(format.substring(p));

		return b.toString();
	}

	public static final boolean isBoolean(String o) {
		String str = o.trim().toLowerCase();
		return str.equals("true") || str.equals("false") || str.equals("yes") || str.equals("no") || str.equals("ok");
	}

	public static final boolean toBoolean(String o) {
		String str = o.trim().toLowerCase();
		if (str.equals("true") || str.equals("yes") || str.equals("ok")) {
			return true;
		} else if (str.equals("no") || str.equals("false")) {
			return false;
		} else if (MathUtils.isNan(str)) {
			return Double.parseDouble(str) > 0;
		}
		return false;
	}

	public static final boolean equals(String a, String b) {
		if (a == null || b == null) {
			return (a == b);
		} else {
			return a.equals(b);
		}
	}

	public static final String trim(String text) {
		return (rtrim(ltrim(text.trim()))).trim();
	}

	public static final String rtrim(String s) {
		int off = s.length() - 1;
		while (off >= 0 && s.charAt(off) <= ' ') {
			off--;
		}
		return off < s.length() - 1 ? s.substring(0, off + 1) : s;
	}

	public static final String ltrim(String s) {
		int off = 0;
		while (off < s.length() && s.charAt(off) <= ' ') {
			off++;
		}
		return off > 0 ? s.substring(off) : s;
	}

	public final static boolean startsWith(String n, char tag) {
		return n.charAt(0) == tag;
	}

	public final static boolean endsWith(String n, char tag) {
		return n.charAt(n.length() - 1) == tag;
	}

	/**
	 * 联合指定对象并输出为字符串
	 * 
	 * @param flag
	 * @param o
	 * @return
	 */
	public static final String join(Character flag, Object... o) {
		StringBuilder sbr = new StringBuilder();
		int size = o.length;
		for (int i = 0; i < size; i++) {
			sbr.append(o[i]);
			if (i < size - 1) {
				sbr.append(flag);
			}
		}
		return sbr.toString();
	}

	/**
	 * 联合指定对象并输出为字符串
	 * 
	 * @param flag
	 * @param o
	 * @return
	 */
	public static final String join(Character flag, float[] o) {
		StringBuilder sbr = new StringBuilder();
		int size = o.length;
		for (int i = 0; i < size; i++) {
			sbr.append(o[i]);
			if (i < size - 1) {
				sbr.append(flag);
			}
		}
		return sbr.toString();
	}

	/**
	 * 联合指定对象并输出为字符串
	 * 
	 * @param flag
	 * @param o
	 * @return
	 */
	public static final String join(Character flag, int[] o) {
		StringBuilder sbr = new StringBuilder();
		int size = o.length;
		for (int i = 0; i < size; i++) {
			sbr.append(o[i]);
			if (i < size - 1) {
				sbr.append(flag);
			}
		}
		return sbr.toString();
	}

	/**
	 * 拼接指定对象数组为String
	 * 
	 * @param res
	 * @return
	 */
	public static final String concat(Object... res) {
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
	 * @param message
	 * @return
	 */
	public static final boolean isEnglishAndNumeric(String message) {
		if (message == null || message.length() == 0) {
			return false;
		}
		int size = message.length();
		for (int j = 0; j < size; j++) {
			char letter = message.charAt(j);
			if (isEnglishAndNumeric(letter)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判定是否由纯粹的西方字符组成
	 * 
	 * @param letter
	 * @return
	 */
	public static final boolean isEnglishAndNumeric(char letter) {
		return (97 > letter || letter > 122) && (65 > letter || letter > 90) && (48 > letter || letter > 57);
	}

	/**
	 * 判定是否为半角符号
	 * 
	 * @param c
	 * @return
	 */
	public static final boolean isSingle(final char c) {
		return (':' == c || '：' == c) || (',' == c || '，' == c) || ('"' == c || '“' == c)
				|| ((0x0020 <= c) && (c <= 0x007E) && !((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))
						&& !('0' <= c) && (c <= '9'));

	}

	/**
	 * 以指定字符过滤切割字符串，并返回分割后的字符串数组
	 * 
	 * @param str
	 * @param flag
	 * @return
	 */
	public static final String[] split(String str, Character flag) {
		if (isEmpty(str)) {
			return new String[] { str };
		}
		int count = 0;
		int size = str.length();
		for (int index = 0; index < size; index++) {
			if (flag == str.charAt(index)) {
				count++;
			}
		}
		if (str.charAt(size - 1) != flag) {
			count++;
		}
		if (count == 0) {
			return new String[] { str };
		}
		int idx = -1;
		String[] strings = new String[count];
		for (int i = 0, len = strings.length; i < len; i++) {
			int IndexStart = idx + 1;
			idx = str.indexOf(flag, idx + 1);
			if (idx == -1) {
				strings[i] = str.substring(IndexStart).trim();
			} else {
				strings[i] = str.substring(IndexStart, idx).trim();
			}
		}
		return strings;
	}

	/**
	 * 分解字符串(同时过滤多个符号)
	 * 
	 * @param str
	 * @param flags
	 * @return
	 */
	public static final String[] split(String str, char[] flags) {
		return split(str, flags, false);
	}

	/**
	 * 分解字符串(同时过滤多个符号)
	 * 
	 * @param str
	 * @param flags
	 * @return
	 */
	public static final String[] split(String str, char[] flags, boolean newline) {
		if ((flags.length == 0) || (str.length() == 0)) {
			return new String[0];
		}
		char[] chars = str.toCharArray();
		int maxparts = chars.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];
		int count = 0;
		start[0] = 0;
		int s = 0, e;
		if (CharUtils.equalsOne(chars[0], flags)) {
			end[0] = 0;
			count++;
			s = CharUtils.findFirstDiff(chars, 1, flags);
			if (s == -1) {
				return new String[] { "", "" };
			}
			start[1] = s;
		}
		for (;;) {
			e = CharUtils.findFirstEqual(chars, s, flags);
			if (e == -1) {
				end[count] = chars.length;
				break;
			}
			end[count] = e;
			count++;
			s = CharUtils.findFirstDiff(chars, e, flags);
			if (s == -1) {
				start[count] = end[count] = chars.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = null;
		if (newline) {
			count *= 2;
			result = new String[count];
			for (int i = 0, j = 0; i < count; j++, i += 2) {
				result[i] = str.substring(start[j], end[j]).trim();
				result[i + 1] = LSystem.LS;
			}
		} else {
			result = new String[count];
			for (int i = 0; i < count; i++) {
				result[i] = str.substring(start[i], end[i]).trim();
			}
		}
		return result;
	}

	/**
	 * 以指定字符串来分解字符串
	 * 
	 * @param str
	 * @param separator
	 * @return
	 */
	public static final String[] split(String str, String separator) {
		int sepLength = separator.length();
		if (sepLength == 0) {
			return new String[] { str };
		}
		TArray<String> tokens = new TArray<String>();
		int length = str.length();
		int start = 0;
		do {
			int p = str.indexOf(separator, start);
			if (p == -1) {
				p = length;
			}
			if (p > start) {
				tokens.add(str.substring(start, p));
			}
			start = p + sepLength;
		} while (start < length);
		String[] result = new String[tokens.size];
		for (int i = 0; i < tokens.size; i++) {
			result[i] = tokens.get(i);
		}
		return result;
	}

	/**
	 * 解析csv文件
	 * 
	 * @param str
	 * @return
	 */
	public static final String[] splitCsv(String str) {
		TArray<String> stringList = new TArray<String>();
		String tempString;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '"') {
				i++;
				while (i < str.length()) {
					if (str.charAt(i) == '"' && str.charAt(i + 1) == '"') {
						sb.append('"');
						i = i + 2;
					}
					if (str.charAt(i) == '"') {
						break;
					} else {
						sb.append(str.charAt(i));
						i++;
					}
				}
				i++;
			}

			if (str.charAt(i) != ',') {
				sb.append(str.charAt(i));
			} else {
				tempString = sb.toString();
				stringList.add(tempString);
				sb.setLength(0);
			}
		}

		tempString = sb.toString();
		stringList.add(tempString);
		sb.setLength(0);
		String[] stockArr = new String[stringList.size];
		stockArr = stringList.toArray(stockArr);
		return stockArr;
	}

	/**
	 * 以指定大小过滤字符串，并返回切割后的数组
	 * 
	 * @param str
	 * @param size
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public static final String[] splitSize(String str, int size) throws NullPointerException, IllegalArgumentException {
		if (isEmpty(str)) {
			return new String[] { str };
		}
		if (size <= 0) {
			throw LSystem.runThrow("The size parameter must be more than 0.");
		}
		int num = str.length() / size;
		int mod = str.length() % size;
		String[] ret = mod > 0 ? new String[num + 1] : new String[num];
		for (int i = 0; i < num; i++) {
			ret[i] = str.substring(i * size, (i + 1) * size).trim();
		}
		if (mod > 0) {
			ret[num] = str.substring(num * size).trim();
		}
		return ret;
	}

	public static final TArray<CharSequence> splitArray(final CharSequence chars, final char flag) {
		return splitArray(chars, flag, new TArray<CharSequence>());
	}

	public static final <T extends TArray<CharSequence>> T splitArray(final CharSequence chars, final char flag,
			final T result) {
		final int partCount = countOccurrences(chars, flag) + 1;
		if (partCount == 0) {
			result.add(chars);
		} else {
			int from = 0;
			int to;
			for (int i = 0; i < (partCount - 1); i++) {
				to = indexOf(chars, flag, from);
				result.add(chars.subSequence(from, to));
				from = to + 1;
			}
			result.add(chars.subSequence(from, chars.length()));
		}
		return result;
	}

	/**
	 * 过滤指定字符串
	 * 
	 * @param message
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static final String replace(String message, String oldString, String newString) {
		if (message == null)
			return null;
		if (newString == null)
			return message;
		int i = 0;
		if ((i = message.indexOf(oldString, i)) >= 0) {
			char string2[] = message.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(string2.length);
			buf.append(string2, 0, i).append(newString2);
			i += oLength;
			int j;
			for (j = i; (i = message.indexOf(oldString, i)) > 0; j = i) {
				buf.append(string2, j, i - j).append(newString2);
				i += oLength;
			}
			buf.append(string2, j, string2.length - j);
			return buf.toString();
		} else {
			return message;
		}
	}

	/**
	 * 过滤指定字符串为空
	 * 
	 * @param message
	 * @param oldStrings
	 * @return
	 */
	public static final String replaceTrim(String message, String... oldStrings) {
		if (message == null)
			return null;
		String trim = "";
		for (int i = 0; i < oldStrings.length; i++) {
			message = replace(message, oldStrings[i], trim);
		}
		return message.trim();
	}

	/**
	 * 不匹配大小写的过滤指定字符串
	 * 
	 * @param line
	 * @param oldString
	 * @param newString
	 * @return
	 */
	public static final String replaceIgnoreCase(String line, String oldString, String newString) {
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
	public static final String replaceIgnoreCase(String line, String oldString, String newString, int count[]) {
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
	public static final String replace(String line, String oldString, String newString, int[] count) {
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
	public static final boolean isChinaLanguage(String mes) {
		int size = mes.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (isChinese(mes.charAt(i))) {
				count++;
			}
		}
		return count >= size;
	}

	public static final boolean containChinaLanguage(String mes) {
		int size = mes.length();
		for (int i = 0; i < size; i++) {
			if (isChinese(mes.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static final boolean isChinese(char c) {
		return c >= 0x4e00 && c <= 0x9fa5;
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static final boolean isEmpty(String param) {
		return param == null || param.length() == 0 || "".equals(param.trim());
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static final boolean isEmpty(String... param) {
		return param == null || param.length == 0;
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
				if (((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9')) && (spStr.indexOf(ch) < 0)) {
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
		int size = value.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (isAlphabet(value.charAt(i))) {
				count++;
			} else {
				break;
			}
		}
		return count >= size;
	}

	/**
	 * 检查是否为纯字母
	 * 
	 * @param letter
	 * @return
	 */
	public final static boolean isAlphabetUpper(char letter) {
		return ('A' <= letter && letter <= 'Z');
	}

	public final static boolean isAlphabetLower(char letter) {
		return ('a' <= letter && letter <= 'z');
	}

	public final static boolean isAlphabet(char letter) {
		return isAlphabetUpper(letter) || isAlphabetLower(letter);
	}

	/**
	 * 检查是否为数字
	 * 
	 * @param letter
	 * @return
	 */
	public static final boolean isNumeric(char letter) {
		return ('0' > letter || letter > '9');
	}

	/**
	 * 检查是否为字母与数字混合
	 * 
	 * @param value
	 * @return
	 */
	public static final boolean isAlphabetNumeric(CharSequence value) {
		if (value == null || value.length() == 0)
			return true;
		for (int i = 0; i < value.length(); i++) {
			char letter = value.charAt(i);
			if (('a' > letter || letter > 'z') && ('A' > letter || letter > 'Z') && ('0' > letter || letter > '9'))
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
	public static final String replaceMatch(String line, String oldString, String newString) {
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
	 * @see #indexOf(int[], int, int)
	 */
	public static final int indexOf(int[] arr, int v) {
		return indexOf(arr, v, 0);
	}

	/**
	 * @param arr
	 *            数组
	 * @param v
	 *            值
	 * @param off
	 *            从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static final int indexOf(int[] arr, int v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @param arr
	 * @param v
	 * @return 最后一个匹配元素的下标
	 */
	public static final int lastIndexOf(int[] arr, int v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @see #indexOf(char[], char, int)
	 */
	public static final int indexOf(char[] arr, char v) {
		if (null != arr)
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @param arr
	 *            数组
	 * @param v
	 *            值
	 * @param off
	 *            从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static final int indexOf(char[] arr, char v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @param arr
	 * @param v
	 * @return 第一个匹配元素的下标
	 */
	public static final int lastIndexOf(char[] arr, char v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @see #indexOf(long[], long, int)
	 */
	public static final int indexOf(long[] arr, long v) {
		return indexOf(arr, v, 0);
	}

	/**
	 * @param arr
	 *            数组
	 * @param v
	 *            值
	 * @param off
	 *            从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static final int indexOf(long[] arr, long v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * @param arr
	 * @param v
	 * @return 第一个匹配元素的下标
	 */
	public static final int lastIndexOf(long[] arr, long v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 获得特定字符总数
	 * 
	 * @param str
	 * @param chr
	 * @return
	 */
	public static final int charCount(String str, char chr) {
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

	public static final String unescape(String escaped) {
		int length = escaped.length();
		int i = 0;
		StringBuilder sb = new StringBuilder(escaped.length() / 2);

		while (i < length) {
			char n = escaped.charAt(i++);
			if (n != '%') {
				sb.append(n);
			} else {
				n = escaped.charAt(i++);
				int code;

				if (n == 'u') {
					String slice = escaped.substring(i, i + 4);
					code = Integer.valueOf(slice, 16);
					i += 4;
				} else {
					String slice = escaped.substring(i - 1, ++i);
					code = Integer.valueOf(slice, 16);
				}
				sb.append((char) code);
			}
		}

		return sb.toString();
	}

	public static final String escape(String raw) {
		int length = raw.length();
		int i = 0;
		StringBuilder sb = new StringBuilder(raw.length() / 2);

		while (i < length) {
			char c = raw.charAt(i++);

			if (CharUtils.isLetterOrDigit(c) || CharUtils.isEscapeExempt(c)) {
				sb.append(c);
			} else {
				int i1 = raw.codePointAt(i - 1);
				String escape = Integer.toHexString(i1);

				sb.append('%');

				if (escape.length() > 2) {
					sb.append('u');
				}
				sb.append(escape.toUpperCase());

			}
		}

		return sb.toString();
	}

	public static final CharSequence padFront(final CharSequence chars, final char padChar, final int len) {
		final int padCount = len - chars.length();
		if (padCount <= 0) {
			return chars;
		} else {
			final StringBuilder sb = new StringBuilder();

			for (int i = padCount - 1; i >= 0; i--) {
				sb.append(padChar);
			}
			sb.append(chars);

			return sb.toString();
		}
	}

	private final static boolean unificationAllow(char ch) {
		return ch != '\n' && ch != '\t' && ch != '\r' && ch != ' ';
	}

	public final static String merge(String[] messages) {
		StringBuilder sbr = new StringBuilder();
		for (String mes : messages) {
			if (mes != null) {
				sbr.append(mes.trim());
			}
		}
		return sbr.toString().trim();
	}

	public final static String merge(CharSequence[] messages) {
		StringBuilder sbr = new StringBuilder();
		for (CharSequence mes : messages) {
			if (mes != null) {
				sbr.append(mes);
			}
		}
		return sbr.toString().trim();
	}

	public final static String unificationStrings(String mes) {
		return unificationStrings(mes, null);
	}

	public final static String unificationStrings(String mes, CharSequence limit) {
		return unificationStrings(new CharArray(128), mes, limit);
	}

	public final static String unificationStrings(CharArray tempChars, String mes) {
		return unificationStrings(tempChars, mes, null);
	}

	public final static String unificationStrings(CharArray tempChars, String mes, CharSequence limit) {
		if (isEmpty(mes)) {
			return "";
		}
		tempChars.clear();
		if (limit == null || limit.length() == 0) {
			for (int i = 0, size = mes.length(); i < size; i++) {
				char ch = mes.charAt(i);
				if (unificationAllow(ch) && !tempChars.contains(ch)) {
					tempChars.add(ch);
				}
			}
		} else {
			boolean running;
			for (int i = 0, size = mes.length(); i < size; i++) {
				running = true;
				char ch = mes.charAt(i);
				for (int j = 0; j < limit.length(); j++) {
					if (limit.charAt(j) == ch) {
						running = false;
						break;
					}
				}
				if (running && unificationAllow(ch) && !tempChars.contains(ch)) {
					tempChars.add(ch);
				}
			}
		}
		if (tempChars.length == 0) {
			return "";
		} else {
			return tempChars.sort().getString().trim();
		}
	}

	public final static String unificationCharSequence(CharSequence[] messages) {
		return unificationCharSequence(messages, null);
	}

	public final static String unificationCharSequence(CharArray tempChars, CharSequence[] messages) {
		return unificationCharSequence(tempChars, messages, null);
	}

	public final static String unificationCharSequence(CharSequence[] messages, CharSequence limit) {
		return unificationCharSequence(new CharArray(128), messages, limit);
	}

	public final static String unificationCharSequence(CharArray tempChars, CharSequence[] messages,
			CharSequence limit) {
		if (messages == null || messages.length == 0) {
			return "";
		}
		tempChars.clear();
		boolean mode = (limit == null || limit.length() == 0);
		for (CharSequence mes : messages) {
			if (mes == null) {
				continue;
			}
			if (mode) {
				for (int i = 0, size = mes.length(); i < size; i++) {
					char ch = mes.charAt(i);
					if (unificationAllow(ch) && !tempChars.contains(ch)) {
						tempChars.add(ch);
					}
				}
			} else {
				boolean running;
				for (int i = 0, size = mes.length(); i < size; i++) {
					running = true;
					char ch = mes.charAt(i);
					for (int j = 0; j < limit.length(); j++) {
						if (limit.charAt(j) == ch) {
							running = false;
							break;
						}
					}
					if (running && unificationAllow(ch) && !tempChars.contains(ch)) {
						tempChars.add(ch);
					}
				}
			}
		}
		if (tempChars.length == 0) {
			return "";
		} else {
			return tempChars.sort().getString().trim();
		}
	}

	public final static String unificationStrings(String[] messages) {
		return unificationStrings(messages, null);
	}

	public final static String unificationStrings(CharArray tempChars, String[] messages) {
		return unificationStrings(tempChars, messages, null);
	}

	public final static String unificationStrings(String[] messages, CharSequence limit) {
		return unificationStrings(new CharArray(128), messages, limit);
	}

	public final static String unificationStrings(CharArray tempChars, String[] messages, CharSequence limit) {
		if (isEmpty(messages)) {
			return "";
		}
		tempChars.clear();
		boolean mode = (limit == null || limit.length() == 0);
		for (String mes : messages) {
			if (mes == null) {
				continue;
			}
			if (mode) {
				for (int i = 0, size = mes.length(); i < size; i++) {
					char ch = mes.charAt(i);
					if (unificationAllow(ch) && !tempChars.contains(ch)) {
						tempChars.add(ch);
					}
				}
			} else {
				boolean running;
				for (int i = 0, size = mes.length(); i < size; i++) {
					running = true;
					char ch = mes.charAt(i);
					for (int j = 0; j < limit.length(); j++) {
						if (limit.charAt(j) == ch) {
							running = false;
							break;
						}
					}
					if (running && unificationAllow(ch) && !tempChars.contains(ch)) {
						tempChars.add(ch);
					}
				}
			}
		}
		if (tempChars.length == 0) {
			return "";
		} else {
			return tempChars.sort().getString().trim();
		}
	}

	public final static String unificationChars(char[] messages) {
		return unificationChars(messages, null);
	}

	public final static String unificationChars(CharArray tempChars, char[] messages) {
		return unificationChars(tempChars, messages, null);
	}

	public final static String unificationChars(char[] messages, CharSequence limit) {
		return unificationChars(new CharArray(128), messages, null);
	}

	public final static String unificationChars(CharArray tempChars, char[] messages, CharSequence limit) {
		if (messages == null || messages.length == 0) {
			return "";
		}
		tempChars.clear();
		boolean mode = (limit == null || limit.length() == 0);
		if (mode) {
			for (int i = 0, size = messages.length; i < size; i++) {
				char ch = messages[i];
				if (unificationAllow(ch) && !tempChars.contains(ch)) {
					tempChars.add(ch);
				}
			}
		} else {
			boolean running;
			for (int i = 0, size = messages.length; i < size; i++) {
				running = true;
				char ch = messages[i];
				for (int j = 0; j < limit.length(); j++) {
					if (limit.charAt(j) == ch) {
						running = false;
						break;
					}
				}
				if (running && unificationAllow(ch) && !tempChars.contains(ch)) {
					tempChars.add(ch);
				}
			}
		}

		if (tempChars.length == 0) {
			return "";
		} else {
			return tempChars.sort().getString().trim();
		}
	}

	public static final int indexOf(CharSequence s, char ch) {
		return indexOf(s, ch, 0);
	}

	public static final int indexOf(CharSequence c, char ch, int start) {
		if (c instanceof String) {
			return ((String) c).indexOf(ch, start);
		}
		return indexOf(c, ch, start, c.length());
	}

	private static char[] obtain(int len) {
		return new char[len];
	}

	public static final int indexOf(CharSequence c, char ch, int start, int end) {
		if ((c instanceof StringBuffer) || (c instanceof StringBuilder) || (c instanceof String)) {
			final int INDEX_INCREMENT = 500;
			char[] temp = obtain(INDEX_INCREMENT);

			while (start < end) {
				int segend = start + INDEX_INCREMENT;
				if (segend > end)
					segend = end;

				getChars(c, start, segend, temp, 0);

				int count = segend - start;
				for (int i = 0; i < count; i++) {
					if (temp[i] == ch) {
						return i + start;
					}
				}

				start = segend;
			}
			return -1;
		}

		for (int i = start; i < end; i++) {
			if (c.charAt(i) == ch) {
				return i;
			}
		}

		return -1;
	}

	public static final String[] getListToStrings(TArray<String> list) {
		if (list == null || list.size == 0) {
			return null;
		}
		String[] result = new String[list.size];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public static final TArray<CharSequence> getArrays(CharSequence[] chars) {
		if (chars == null) {
			return new TArray<CharSequence>(0);
		}
		int size = chars.length;
		TArray<CharSequence> arrays = new TArray<CharSequence>();
		for (int i = 0; i < size; i++) {
			arrays.add(chars[i]);
		}
		return arrays;
	}

	public static final void getChars(CharSequence c, int start, int end, char[] dest, int destoff) {
		if (c instanceof String) {
			((String) c).getChars(start, end, dest, destoff);
		} else if (c instanceof StringBuffer) {
			((StringBuffer) c).getChars(start, end, dest, destoff);
		} else if (c instanceof StringBuilder) {
			((StringBuilder) c).getChars(start, end, dest, destoff);
		} else {
			for (int i = start; i < end; i++) {
				dest[destoff++] = c.charAt(i);
			}
		}
	}

	public static final int countOccurrences(final CharSequence chars, final char flag) {
		int count = 0;
		int lastOccurrence = indexOf(chars, flag, 0);
		while (lastOccurrence != -1) {
			count++;
			lastOccurrence = indexOf(chars, flag, lastOccurrence + 1);
		}
		return count;
	}

	public static final boolean isSpace(char c) {
		switch (c) {
		case ' ':
			return true;
		case '\n':
			return true;
		case '\t':
			return true;
		case '\f':
			return true;
		case '\r':
			return true;
		default:
			return false;
		}
	}

	public static final int countCharacters(final TArray<CharSequence> chars) {
		return countCharacters(chars, false);
	}

	public static final int countCharacters(final TArray<CharSequence> chars, final boolean ignoreWhitespaces) {
		int characters = 0;
		if (ignoreWhitespaces) {
			for (int i = chars.size - 1; i >= 0; i--) {
				final CharSequence text = chars.get(i);
				for (int j = text.length() - 1; j >= 0; j--) {
					final char character = text.charAt(j);
					if (!isSpace(character)) {
						characters++;
					}
				}
			}
		} else {
			for (int i = chars.size - 1; i >= 0; i--) {
				final CharSequence text = chars.get(i);
				characters += text.length();
			}
		}
		return characters;
	}

	public final static String notLineBreaks(String text) {
		final int h = text.indexOf('\n');
		if (h >= 0) {
			return text.substring(0, h);
		}
		return text;
	}

	public final static String notEmptyOne(String... texts) {
		TArray<String> list = notEmpty(texts);
		if (list.size > 0) {
			return list.get(0);
		}
		return null;
	}

	public final static TArray<String> notEmpty(String... texts) {
		TArray<String> list = new TArray<String>(10);
		for (String text : texts) {
			if (!StringUtils.isEmpty(text)) {
				list.add(text);
			}
		}
		return list;
	}

}
