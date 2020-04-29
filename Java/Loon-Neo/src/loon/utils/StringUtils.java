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
 * 字符串处理用工具类
 */
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
	public static String format(String format, Object... params) {
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

	/**
	 * 判定指定字符序列是否在指定范围内
	 * 
	 * @param cs
	 * @param minX
	 * @param maxX
	 * @return
	 */
	public static boolean isLimit(CharSequence cs, int minX, int maxX) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		return MathUtils.isLimit(cs.length(), minX, maxX);
	}

	/**
	 * 判断指定字符串内容是否为布尔值(不判定数字为布尔，并且只判定布尔值，不考虑值真假问题)
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isBoolean(String o) {
		if (isEmpty(o)) {
			return false;
		}
		String str = o.trim().toLowerCase();
		return str.equals("true") || str.equals("false") || str.equals("yes") || str.equals("no") || str.equals("ok");
	}

	/**
	 * 转换指定字符串内容为布尔值(判定数字为布尔)
	 * 
	 * @param o
	 * @return
	 */
	public static boolean toBoolean(String o) {
		if (isEmpty(o)) {
			return false;
		}
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

	/**
	 * 判断两个字符串是否等值
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(String a, String b) {
		return assertEqual(a, b);
	}

	/**
	 * 判定两组字符序列是否内容相等
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(final CharSequence a, final CharSequence b) {
		return equals(a, b, false);
	}

	/**
	 * 判定两组字符序列是否内容相等
	 * 
	 * @param a
	 * @param b
	 * @param ignoreWhitespaces
	 *            如果此项为true,则无视所有不显示的占位符,即StringUtils.equals("abc\n",
	 *            "abc",true)
	 *            这样含有换行符之类不显示字符的字符串在比较时此标记为true时将等值,为false时不等值,默认为false
	 * @return
	 */
	public static boolean equals(final CharSequence a, final CharSequence b, final boolean ignoreWhitespaces) {
		if (a == null) {
			return (b == null);
		} else if (b == null) {
			return false;
		}
		if (ignoreWhitespaces) {
			return equals(spaceFilter(a), spaceFilter(b), false);
		} else {
			final int size = a.length();
			if (b.length() != size) {
				return false;
			}
			for (int i = size - 1; i >= 0; i--) {
				if (a.charAt(i) != b.charAt(i)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 去除字符串中所有空白字符
	 * 
	 * @param text
	 * @return
	 */
	public static String trim(String text) {
		return (rtrim(ltrim(text.trim()))).trim();
	}

	/**
	 * 去除字符串右侧空白字符
	 * 
	 * @param s
	 * @return
	 */
	public static String rtrim(String s) {
		int off = s.length() - 1;
		while (off >= 0 && s.charAt(off) <= ' ') {
			off--;
		}
		return off < s.length() - 1 ? s.substring(0, off + 1) : s;
	}

	/**
	 * 去除字符串左侧空白字符
	 * 
	 * @param s
	 * @return
	 */
	public static String ltrim(String s) {
		int off = 0;
		while (off < s.length() && s.charAt(off) <= ' ') {
			off++;
		}
		return off > 0 ? s.substring(off) : s;
	}

	/**
	 * 判定指定字符串是否包含指定开头
	 * 
	 * @param s
	 * @param sub
	 * @return
	 */
	public static boolean startsWith(final String s, final String sub) {
		return (s != null) && (sub != null) && s.startsWith(sub);
	}

	/**
	 * 判定指定字符串是否包含指定开头
	 * 
	 * @param n
	 * @param tag
	 * @return
	 */
	public static boolean startsWith(String n, char tag) {
		return (n != null) && n.charAt(0) == tag;
	}

	/**
	 * 判定指定字符串是否包含指定结尾
	 * 
	 * @param s
	 * @param sub
	 * @return
	 */
	public static boolean endsWith(final String s, final String sub) {
		return (s != null) && (sub != null) && s.endsWith(sub);
	}

	/**
	 * 判定指定字符串是否包含指定结尾
	 * 
	 * @param n
	 * @param tag
	 * @return
	 */
	public static boolean endsWith(String n, char tag) {
		return (n != null) && n.charAt(n.length() - 1) == tag;
	}

	/**
	 * 联合指定对象并输出为字符串
	 * 
	 * @param flag
	 * @param o
	 * @return
	 */
	public static String join(Character flag, Object... o) {
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
	public static String join(Character flag, float[] o) {
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
	public static String join(Character flag, int[] o) {
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
	public static String concat(final Object... res) {
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
	 * 拼接指定字符数组
	 * 
	 * @param cs
	 * @return
	 */
	public final static char[] concat(final char[]... res) {
		int size = 0;
		for (final char[] e : res) {
			size += e.length;
		}
		final char[] c = new char[size];
		int ci = 0;
		for (final char[] e : res) {
			size = e.length;
			System.arraycopy(e, 0, c, ci, size);
			ci += size;
		}
		return c;
	}

	/**
	 * 判定是否由纯粹的西方字符组成
	 * 
	 * @param message
	 * @return
	 */
	public static boolean isEnglishAndNumeric(final String message) {
		if (isEmpty(message)) {
			return false;
		}
		int size = message.length();
		int amount = 0;
		for (int j = 0; j < size; j++) {
			int letter = message.charAt(j);
			if (isEnglishAndNumeric(letter) || letter == ' ') {
				amount++;
			}
		}
		return amount >= size;
	}

	/**
	 * 过滤指定字符为空
	 * 
	 * @param message
	 * @param chars
	 * @return
	 */
	public static String filter(CharSequence message, char... chars) {
		return filter(message, chars, "");
	}

	/**
	 * 过滤指定字符为新字符
	 * 
	 * @param message
	 * @param chars
	 * @param newTag
	 * @return
	 */
	public static String filter(CharSequence message, char[] chars, CharSequence newTag) {
		if (size(message) <= 0) {
			return "";
		}
		StringBuilder sbr = new StringBuilder();
		boolean addFlag;
		for (int i = 0; i < message.length(); i++) {
			addFlag = true;
			char ch = message.charAt(i);
			for (int j = 0; j < chars.length; j++) {
				if (chars[j] == ch) {
					addFlag = false;
					sbr.append(newTag);
					break;
				}
			}
			if (addFlag) {
				sbr.append(ch);
			}
		}
		return sbr.toString();
	}

	/**
	 * 过滤字符序列中所有不显示的占位符
	 * 
	 * @param s
	 * @return
	 */
	public static String spaceFilter(CharSequence s) {
		if (size(s) <= 0) {
			return "";
		}
		StringBuilder sbr = new StringBuilder();
		final int size = s.length();
		for (int i = 0; i < size; i++) {
			char c = s.charAt(i);
			if (!isSpace(c)) {
				sbr.append(c);
			}
		}
		return sbr.toString();
	}

	/**
	 * 以指定字符过滤切割字符串，并返回分割后的字符串数组
	 * 
	 * @param str
	 * @param flag
	 * @return
	 */
	public static String[] split(String str, Character flag) {
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
	public static String[] split(String str, char[] flags) {
		return split(str, flags, false);
	}

	/**
	 * 分解字符串(同时过滤多个符号)
	 * 
	 * @param str
	 * @param flags
	 * @return
	 */
	public static String[] split(String str, char[] flags, boolean newline) {
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
	public static String[] split(String str, String separator) {
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
	public static String[] splitCsv(String str) {
		TArray<String> stringList = new TArray<String>();
		String tempString;
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '"') {
				i++;
				while (i < str.length()) {
					if (str.charAt(i) == '"' && str.charAt(i + 1) == '"') {
						sbr.append('"');
						i = i + 2;
					}
					if (str.charAt(i) == '"') {
						break;
					} else {
						sbr.append(str.charAt(i));
						i++;
					}
				}
				i++;
			}

			if (str.charAt(i) != ',') {
				sbr.append(str.charAt(i));
			} else {
				tempString = sbr.toString();
				stringList.add(tempString);
				sbr.setLength(0);
			}
		}

		tempString = sbr.toString();
		stringList.add(tempString);
		sbr.setLength(0);
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
	 * @throws LSysException
	 */
	public static String[] splitSize(String str, int size) throws LSysException {
		if (isEmpty(str)) {
			return new String[] { str };
		}
		if (size <= 0) {
			throw new LSysException("The size parameter must be more than 0.");
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

	/**
	 * 以指定标记分割过滤字符序列并返回一个数据集合
	 * 
	 * @param chars
	 *            要过滤的字符序列
	 * @param flag
	 *            以何种标记过滤分段
	 * @return
	 */
	public static TArray<CharSequence> splitArray(final CharSequence chars, final char flag) {
		return splitArray(chars, flag, new TArray<CharSequence>());
	}

	/**
	 * 以指定标记分割过滤字符序列并返回一个数据集合
	 * 
	 * @param chars
	 *            要过滤的字符序列
	 * @param flag
	 *            以何种标记过滤分段
	 * @param result
	 *            返回结果用的集合对象
	 * @return
	 */
	public static <T extends TArray<CharSequence>> T splitArray(final CharSequence chars, final char flag,
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
	public static String replace(String message, String oldString, String newString) {
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
	 * @param newTag
	 * @param oldStrings
	 * @return
	 */
	public static String replacesTrim(String message, String newTag, String... oldStrings) {
		return replaces(message, "", oldStrings);
	}

	/**
	 * 过滤指定字符串为指定样式
	 * 
	 * @param message
	 * @param newTag
	 * @param oldStrings
	 * @return
	 */
	public static String replaces(String message, String newTag, String... oldStrings) {
		if (message == null) {
			return null;
		}
		for (int i = 0; i < oldStrings.length; i++) {
			message = replace(message, oldStrings[i], newTag);
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
	public static String replaceIgnoreCase(String line, String oldString, String newString) {
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
	public static String replaceIgnoreCase(String line, String oldString, String newString, int count[]) {
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
	public static String replace(String line, String oldString, String newString, int[] count) {
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
	public static boolean isChinaLanguage(String mes) {
		int size = mes.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (isChinese(mes.charAt(i))) {
				count++;
			}
		}
		return count >= size;
	}

	/**
	 * 判断字符串中是否包含中文
	 * 
	 * @param mes
	 * @return
	 */
	public static boolean containChinaLanguage(String mes) {
		int size = mes.length();
		for (int i = 0; i < size; i++) {
			if (isChinese(mes.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否全部是空格
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isBlankAll(CharSequence param) {
		for (int i = 0; i < param.length(); i++) {
			if (param.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(String param) {
		return param == null || param.length() == 0 || "".equals(param.trim());
	}

	/**
	 * 判断CharSequence是否非空(和isNotEmpty与isEmpty分开函数名,防止串类型)
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNullOrEmpty(CharSequence value) {
		return value == null || value.length() == 0;
	}

	/**
	 * 判断String是否非空
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isNotEmpty(String param) {
		return !isEmpty(param);
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(String... param) {
		return param == null || param.length == 0;
	}

	/**
	 * 大写第一个字符
	 * 
	 * @param mes
	 * @return
	 */
	public static String toUpperCaseFirst(String mes) {
		if (isEmpty(mes)) {
			return "";
		}
		if (mes.length() < 2) {
			return mes.substring(0).toUpperCase();
		}
		return mes.substring(0, 1).toUpperCase() + mes.substring(1, mes.length());
	}

	/**
	 * 检查指定字符串中是否存在中文字符。
	 * 
	 * @param checkStr
	 *            指定需要检查的字符串。
	 * @return 逻辑值（True Or False）。
	 */
	public static boolean hasChinese(String checkStr) {
		for (int i = 0; i < checkStr.length(); i++) {
			int ch = checkStr.charAt(i);
			if (isChinese(ch)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查是否为纯字母
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isAlphabet(String value) {
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
	 * 检查是否为纯字母大写
	 * 
	 * @param letter
	 * @return
	 */
	public static boolean isAlphabetUpper(char letter) {
		return ('A' <= letter && letter <= 'Z');
	}

	/**
	 * 检查是否为纯字母小写
	 * 
	 * @param letter
	 * @return
	 */
	public static boolean isAlphabetLower(char letter) {
		return ('a' <= letter && letter <= 'z');
	}

	/**
	 * 检查是否为纯字母
	 * 
	 * @param letter
	 * @return
	 */
	public static boolean isAlphabet(char letter) {
		return isAlphabetUpper(letter) || isAlphabetLower(letter);
	}

	/**
	 * 判断整个字符串是否完全有hex字符(0-f)组成
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isHex(CharSequence ch) {
		if (ch == null) {
			return false;
		}
		for (int i = 0; i < ch.length(); i++) {
			int c = ch.charAt(i);
			if (!isHexDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查是否为数字
	 * 
	 * @param letter
	 * @return
	 */
	public static boolean isNumeric(char letter) {
		return isDigitCharacter(letter);
	}

	/**
	 * 变更数字字符串格式为指定分隔符货币格式(比如100000000用分隔符","分割后就是100,000,000)
	 * 
	 * @param value
	 * @return
	 */
	public static String changFormatToMoney(String value) {
		return changFormatToMoney(value, ",", false);
	}

	/**
	 * 变更数字字符串格式为指定分隔符货币格式
	 * 
	 * @param value
	 * @param split
	 * @param tag
	 * @return
	 */
	public static String changFormatToMoney(String value, String split, boolean tag) {
		if (!MathUtils.isNan(value)) {
			return value;
		}
		int count = 0;
		String sbr = "";
		if (tag) {
			if (value.charAt(0) >= 48 && value.charAt(0) <= 57) {
				value = "+" + value;
			}
		}
		for (int i = value.length() - 1; i > -1; i--) {
			sbr = value.charAt(i) + sbr;
			int charCode = value.charAt(i);
			if (charCode >= 48 && charCode <= 57) {
				if (i > 0) {
					charCode = value.charAt(i - 1);
					if (charCode >= 48 && charCode <= 57) {
						count++;
						if (count == 3) {
							sbr = split + sbr;
							count = 0;
						}
					}
				}
			} else {
				count = 0;
			}
		}
		return sbr;
	}

	/**
	 * 检查是否为字母与数字混合
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isAlphabetNumeric(CharSequence value) {
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
	public static String replaceMatch(String line, String oldString, String newString) {
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
	 * 对指定字符串进行escape解码
	 * 
	 * @param escaped
	 * @return
	 */
	public static String unescape(String escaped) {
		int length = escaped.length();
		int i = 0;
		StringBuilder sbr = new StringBuilder(escaped.length() / 2);

		while (i < length) {
			char n = escaped.charAt(i++);
			if (n != '%') {
				sbr.append(n);
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
				sbr.append((char) code);
			}
		}

		return sbr.toString();
	}

	/**
	 * 对指定字符串进行escape编码
	 * 
	 * @param raw
	 * @return
	 */
	public static String escape(String raw) {
		int length = raw.length();
		int i = 0;
		StringBuilder sbr = new StringBuilder(raw.length() / 2);

		while (i < length) {
			char c = raw.charAt(i++);

			if (CharUtils.isLetterOrDigit(c) || CharUtils.isEscapeExempt(c)) {
				sbr.append(c);
			} else {
				int i1 = raw.codePointAt(i - 1);
				String escape = CharUtils.toHex(i1);

				sbr.append('%');

				if (escape.length() > 2) {
					sbr.append('u');
				}
				sbr.append(escape.toUpperCase());

			}
		}

		return sbr.toString();
	}

	/**
	 * 当字符序列长度不满足指定要求时，在字符串前(左侧)补位特定字符,字符序列增加为指定长度
	 * 
	 * @param chars
	 *            原始字符序列
	 * @param padChar
	 *            字符序列长度不够时补位用字符
	 * @param len
	 *            字符序列长度补位生效的要求长度
	 * @return
	 */
	public static CharSequence padFront(final CharSequence chars, final char padChar, final int len) {
		final int padCount = len - chars.length();
		if (padCount <= 0) {
			return chars;
		} else {
			final StringBuilder sbr = new StringBuilder();

			for (int i = padCount - 1; i >= 0; i--) {
				sbr.append(padChar);
			}
			sbr.append(chars);

			return sbr.toString();
		}
	}

	/**
	 * 当字符序列长度不满足指定要求时，在字符串前(右侧)补位特定字符,字符序列增加为指定长度
	 * 
	 * @param chars
	 * @param padChar
	 * @param len
	 * @return
	 */
	public static CharSequence padBack(final CharSequence chars, final char padChar, final int len) {
		final int padCount = len - chars.length();
		if (padCount <= 0) {
			return chars;
		} else {
			final StringBuilder sbr = new StringBuilder(chars);
			for (int i = padCount - 1; i >= 0; i--) {
				sbr.append(padChar);
			}
			return sbr.toString();
		}
	}

	private static boolean unificationAllow(char ch) {
		return ch != '\n' && ch != '\t' && ch != '\r' && ch != ' ';
	}

	public static String merge(String[] messages) {
		if (isEmpty(messages)) {
			return "";
		}
		StringBuilder sbr = new StringBuilder();
		for (String mes : messages) {
			if (mes != null) {
				sbr.append(mes.trim());
			}
		}
		return sbr.toString().trim();
	}

	public static String merge(CharSequence[] messages) {
		if (messages == null || messages.length == 0) {
			return "";
		}
		StringBuilder sbr = new StringBuilder();
		for (CharSequence mes : messages) {
			if (mes != null) {
				sbr.append(mes);
			}
		}
		return sbr.toString().trim();
	}

	public static String unificationStrings(String mes) {
		return unificationStrings(mes, null);
	}

	public static String unificationStrings(String mes, CharSequence limit) {
		return unificationStrings(new CharArray(128), mes, limit);
	}

	public static String unificationStrings(CharArray tempChars, String mes) {
		return unificationStrings(tempChars, mes, null);
	}

	public static String unificationStrings(CharArray tempChars, String mes, CharSequence limit) {
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

	public static String unificationCharSequence(CharSequence[] messages) {
		return unificationCharSequence(messages, null);
	}

	public static String unificationCharSequence(CharArray tempChars, CharSequence[] messages) {
		return unificationCharSequence(tempChars, messages, null);
	}

	public static String unificationCharSequence(CharSequence[] messages, CharSequence limit) {
		return unificationCharSequence(new CharArray(128), messages, limit);
	}

	public static String unificationCharSequence(CharArray tempChars, CharSequence[] messages, CharSequence limit) {
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

	public static String unificationStrings(String[] messages) {
		return unificationStrings(messages, null);
	}

	public static String unificationStrings(CharArray tempChars, String[] messages) {
		return unificationStrings(tempChars, messages, null);
	}

	public static String unificationStrings(String[] messages, CharSequence limit) {
		return unificationStrings(new CharArray(128), messages, limit);
	}

	public static String unificationStrings(CharArray tempChars, String[] messages, CharSequence limit) {
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

	public static String unificationChars(char[] messages) {
		return unificationChars(messages, null);
	}

	public static String unificationChars(CharArray tempChars, char[] messages) {
		return unificationChars(tempChars, messages, null);
	}

	/**
	 * 合并字符数组到CharArray字符集合中的去(不包含limit中限定的字符)
	 * 
	 * @param messages
	 * @param limit
	 * @return
	 */
	public static String unificationChars(char[] messages, CharSequence limit) {
		return unificationChars(new CharArray(128), messages, null);
	}

	/**
	 * 合并字符数组到CharArray字符集合中的去(不包含limit中限定的字符)
	 * 
	 * @param tempChars
	 * @param messages
	 * @param limit
	 * @return
	 */
	public static String unificationChars(CharArray tempChars, char[] messages, CharSequence limit) {
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

	/**
	 * 判定指定字符序列中指定字符是否存在
	 * 
	 * @param s
	 * @param ch
	 * @return
	 */
	public static int indexOf(CharSequence s, char ch) {
		return indexOf(s, ch, 0);
	}

	/**
	 * 判定指定字符序列中指定字符是否存在
	 * 
	 * @param c
	 * @param ch
	 * @param start
	 * @return
	 */
	public static int indexOf(CharSequence c, char ch, int start) {
		if (c instanceof String) {
			return ((String) c).indexOf(ch, start);
		}
		return indexOf(c, ch, start, c.length());
	}

	/**
	 * 检索指定字符序列集合中指定区间内指定字符是否存在并返回其索引位置
	 * 
	 * @param c
	 * @param ch
	 * @param start
	 * @param end
	 * @return
	 */
	public static int indexOf(CharSequence c, char ch, int start, int end) {
		if ((c instanceof StringBuffer) || (c instanceof StringBuilder) || (c instanceof String)) {
			final int INDEX_INCREMENT = 500;
			char[] temp = new char[INDEX_INCREMENT];

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

	/**
	 * 转换一个字符串集合为字符串数组
	 * 
	 * @param list
	 * @return
	 */
	public static String[] getListToStrings(TArray<String> list) {
		if (list == null || list.size == 0) {
			return null;
		}
		String[] result = new String[list.size];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	/**
	 * 转化字符序列数组到字符序列集合中去
	 * 
	 * @param chars
	 * @return
	 */
	public static TArray<CharSequence> getArrays(CharSequence[] chars) {
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

	/**
	 * 从指定字符序列中截取指定长度的字符串到指定字符数组中去
	 * 
	 * @param c
	 * @param start
	 * @param end
	 * @param dest
	 * @param destoff
	 */
	public static void getChars(CharSequence c, int start, int end, char[] dest, int destoff) {
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

	/**
	 * 返回指定字符序列中指定标记出现的次数
	 * 
	 * @param chars
	 * @param flag
	 * @return
	 */
	public static int countOccurrences(final CharSequence chars, final char flag) {
		int count = 0;
		int lastOccurrence = indexOf(chars, flag, 0);
		while (lastOccurrence != -1) {
			count++;
			lastOccurrence = indexOf(chars, flag, lastOccurrence + 1);
		}
		return count;
	}

	/**
	 * 判定指定字符串是否仅占位而不显示
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSpace(char c) {
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

	/**
	 * 统计指定集合中字符序列对象的总长度
	 * 
	 * @param chars
	 *            字符序列集合
	 * @return
	 */
	public static int countCharacters(final TArray<CharSequence> chars) {
		return countCharacters(chars, false);
	}

	/**
	 * 统计指定集合中字符序列对象的总长度
	 * 
	 * @param chars
	 *            字符序列集合
	 * @param ignoreWhitespaces
	 *            是否跳过仅占位而不显示的字符
	 * @return
	 */
	public static int countCharacters(final TArray<CharSequence> chars, final boolean ignoreWhitespaces) {
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

	/**
	 * 去掉指定字符串句尾的换行字符
	 * 
	 * @param s
	 * @return
	 */
	public static String notLineBreaks(String s) {
		final int h = s.indexOf('\n');
		if (h >= 0) {
			return s.substring(0, h);
		}
		return s;
	}

	/**
	 * 返回一个非空的集合
	 * 
	 * @param texts
	 * @return
	 */
	public static String notEmptyOne(String... texts) {
		TArray<String> list = notEmpty(texts);
		if (list.size > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 添加所有非空的字符串到集合
	 * 
	 * @param texts
	 * @return
	 */
	public static TArray<String> notEmpty(String... texts) {
		TArray<String> list = new TArray<String>(10);
		for (String text : texts) {
			if (!StringUtils.isEmpty(text)) {
				list.add(text);
			}
		}
		return list;
	}

	/**
	 * 转化指定字符串为'非null'字符串
	 * 
	 * @param mes
	 * @return
	 */
	public static CharSequence notNull(final CharSequence mes) {
		if (mes == null || mes.length() == 0) {
			return "";
		}
		return mes;
	}

	/**
	 * 去除特定字符串中指定的字符后内容
	 * 
	 * @param values
	 * @param str
	 * @param sum
	 * @return
	 */
	public static CharSequence notNull(CharSequence mes, char str, int sum) {
		if (mes == null || mes.length() == 0) {
			return notNull(mes);
		}
		int num = 0;
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < mes.length(); i++) {
			char ch = mes.charAt(i);
			if (ch == str) {
				num++;
			}
			sbr.append(ch);
			if (num == sum) {
				return sbr.toString();
			}
		}
		return mes;
	}

	/**
	 * 判断两组CharSequence是否相等
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean assertEqual(final CharSequence a, final CharSequence b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return a == null && b == null;
		} else {
			return a.equals(b);
		}
	}

	/**
	 * 查看指定CharSequence数值是否在指定CharSequence数组中
	 * 
	 * @param key
	 * @param texts
	 * @return
	 */
	public static boolean contains(CharSequence key, CharSequence... texts) {
		for (CharSequence text : texts) {
			if (key == null && text == null) {
				return true;
			}
			if (text == key || (text != null && text.equals(key))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 反转指定字符序列
	 * 
	 * @param v
	 * @return
	 */
	public final static String reverse(final CharSequence v) {
		if (size(v) <= 0) {
			return "";
		}
		final int size = v.length();
		final StringBuilder sbr = new StringBuilder(size);
		for (int i = size - 1; i >= 0; i--) {
			sbr.append(v.charAt(i));
		}
		return sbr.toString();
	}

	/**
	 * 返回指定字符序列长度
	 * 
	 * @param v
	 * @return
	 */
	public static int size(final CharSequence v) {
		return v == null ? -1 : v.length();
	}

	/**
	 * 返回指定字符序列中指定索引对应的字符
	 * 
	 * @param v
	 * @param i
	 * @return
	 */
	public final static char charAt(final CharSequence v, final int i) {
		return size(v) <= i ? 0 : v.charAt(i);
	}

	/**
	 * 返回指定对象的字符串信息
	 * 
	 * @param o
	 * @return
	 */
	public static String toString(final Object o) {
		return o == null ? null : o.toString();
	}

}
