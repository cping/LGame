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

import java.util.Iterator;

import loon.LSysException;
import loon.LSystem;
import loon.utils.ObjectMap.Keys;

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
	public static String format(String format, Object... o) {
		boolean fempty = isEmpty(format);
		if (fempty) {
			return LSystem.EMPTY;
		}
		if (!fempty && CollectionUtils.isEmpty(o)) {
			return format;
		}
		StrBuilder b = new StrBuilder();
		int p = 0;
		for (;;) {
			int i = format.indexOf(LSystem.DELIM_START, p);
			if (i == -1) {
				break;
			}
			int idx = format.indexOf(LSystem.DELIM_END, i + 1);
			if (idx == -1) {
				break;
			}
			if (p != i) {
				b.append(format.substring(p, i));
			}
			String nstr = format.substring(i + 1, idx);
			try {
				int n = Integer.parseInt(nstr);
				if (n >= 0 && n < o.length) {
					b.append(o[n]);
				} else {
					b.append(LSystem.DELIM_START).append(nstr).append(LSystem.DELIM_END);
				}
			} catch (Exception e) {
				b.append(LSystem.DELIM_START).append(nstr).append(LSystem.DELIM_END);
			}
			p = idx + 1;
		}
		b.append(format.substring(p));

		return b.toString();
	}

	/**
	 * 过滤指定字符串中的键值对为ObjectMap中数据
	 * 
	 * @sample ObjectMap<String, String> test = new ObjectMap<String, String>();
	 *         test.put("key1", "abc"); test.put("key2", "efg");
	 *         System.out.println(StringUtils.formatVars("key1 is ${key1},key2 is
	 *         ${key2}", test));
	 * 
	 * @param <K>
	 * @param <V>
	 * @param format
	 * @param flag
	 * @param params
	 * @return
	 */
	public static <K, V> String formatVars(String format, String flag, ObjectMap<K, V> params) {
		String context = format;
		if (params != null) {
			Keys<K> keys = params.keys();
			for (Iterator<K> it = keys.iterator(); it.hasNext();) {
				K key = it.next();
				if (key != null) {
					context = replace(context, flag + LSystem.DELIM_START + key + LSystem.DELIM_END,
							HelperUtils.toStr(params.get(key)));
				}
			}
		}
		return context;
	}

	/**
	 * 过滤指定字符串中的键值对为ObjectMap中数据,过滤标志对象以$开头
	 * 
	 * @param <K>
	 * @param <V>
	 * @param format
	 * @param params
	 * @return
	 */
	public static <K, V> String formatVars(String format, ObjectMap<K, V> params) {
		return formatVars(format, "$", params);
	}

	public static String format(float v) {
		StrBuilder result = new StrBuilder();
		if (v < 0f) {
			result.append(LSystem.DASHED);
			v = -v;
		}
		result.append((int) v);
		v -= (int) v;
		result.append(LSystem.DOT);
		for (int i = 0; i < 7 && (i <= 0 || v != 0f); i++) {
			v *= 10f;
			result.append((int) v);
			v -= (int) v;
		}
		return result.toString();
	}

	/**
	 * 将指定字符串为指定长度字符串
	 * 
	 * @param ch
	 * @param count
	 * @return
	 */
	public static String cpy(char ch, int count) {
		StrBuilder sbr = new StrBuilder(count);
		for (int i = 0; i < count; i++) {
			sbr.append(ch);
		}
		return sbr.toString();
	}

	/**
	 * 清空指定字串
	 * 
	 * @param ch
	 * @return
	 */
	public static String clean(String ch, String c) {
		return replace(ch, c, LSystem.EMPTY);
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

	private final static String[] BOOL_POOL_TRUE = { "true", "yes", "ok", "on" };

	private final static String[] BOOL_POOL_FALSE = { "false", "no", "fake", "off" };

	private final static String SELECTION_BOUND = "'\"|/\\<>()[]{}-";

	/**
	 * 判定是否存在范围符号
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isSelectionBound(String o) {
		return isNotEmpty(o) && SELECTION_BOUND.indexOf(o) != -1;
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
		return contains(str, BOOL_POOL_TRUE) || contains(str, BOOL_POOL_FALSE);
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
		if (contains(str, BOOL_POOL_TRUE)) {
			return true;
		} else if (contains(str, BOOL_POOL_FALSE)) {
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
	 * @param ignoreWhitespaces 如果此项为true,则无视所有不显示的占位符,即StringUtils.equals("abc\n",
	 *                          "abc",true)
	 *                          这样含有换行符之类不显示字符的字符串在比较时此标记为true时将等值,为false时不等值,默认为false
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
		while (off >= 0 && s.charAt(off) <= LSystem.SPACE) {
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
		while (off < s.length() && s.charAt(off) <= LSystem.SPACE) {
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
	public static boolean startsWith(final CharSequence s, final CharSequence sub) {
		return (s != null) && (sub != null) && s.toString().startsWith(sub.toString());
	}

	/**
	 * 判定指定字符串是否包含指定开头
	 * 
	 * @param n
	 * @param tag
	 * @return
	 */
	public static boolean startsWith(CharSequence n, char tag) {
		return (n != null) && n.charAt(0) == tag;
	}

	/**
	 * 检查是否以指定字符序列开头
	 * 
	 * @param str
	 * @param prefix
	 * @param isIgnore
	 * @return
	 */
	public static boolean startWith(CharSequence str, CharSequence prefix, boolean isIgnore) {
		if (null == str || null == prefix) {
			return null == str && null == prefix;
		}
		if (isIgnore) {
			return str.toString().toLowerCase().startsWith(prefix.toString().toLowerCase());
		} else {
			return str.toString().startsWith(prefix.toString());
		}
	}

	/**
	 * 检查指定字符序列开头中是否包含如下字符序列
	 * 
	 * @param str
	 * @param prefixes
	 * @return
	 */
	public static boolean startsAnyWith(CharSequence str, CharSequence... prefixes) {
		if (isNullOrEmpty(str) || CollectionUtils.isEmpty(prefixes)) {
			return false;
		}
		for (CharSequence suffix : prefixes) {
			if (startWith(str, suffix, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判定指定字符串是否包含指定结尾
	 * 
	 * @param s
	 * @param sub
	 * @return
	 */
	public static boolean endsWith(final CharSequence s, final CharSequence sub) {
		return (s != null) && (sub != null) && s.toString().endsWith(sub.toString());
	}

	/**
	 * 判定指定字符串是否包含指定结尾
	 * 
	 * @param n
	 * @param tag
	 * @return
	 */
	public static boolean endsWith(CharSequence n, char tag) {
		return (n != null) && n.length() > 0 && n.charAt(n.length() - 1) == tag;
	}

	/**
	 * 联合指定对象并输出为字符串
	 * 
	 * @param flag
	 * @param o
	 * @return
	 */
	public static String join(CharSequence flag, Object... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, float... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, int... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, long... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, boolean... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, CharSequence... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	public static String join(char flag, Object... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
		int size = o.length;
		for (int i = 0; i < size; i++) {
			sbr.append(HelperUtils.toStr(o[i]));
			if (i < size - 1) {
				sbr.append(flag);
			}
		}
		return sbr.toString();
	}

	/**
	 * 拼接指定对象数组为String
	 * 
	 * @param o
	 * @return
	 */
	public static String concat(final Object... o) {
		if (CollectionUtils.isEmpty(o)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder(o.length);
		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof Integer) {
				sbr.append((Integer) o[i]);
			} else {
				sbr.append(o[i]);
			}
		}
		return sbr.toString();
	}

	/**
	 * 拼接指定字符数组
	 * 
	 * @param o
	 * @return
	 */
	public final static char[] concat(final char[]... o) {
		if (CollectionUtils.isEmpty(o)) {
			return new char[] {};
		}
		int size = 0;
		for (final char[] e : o) {
			size += e.length;
		}
		final char[] c = new char[size];
		int ci = 0;
		for (final char[] e : o) {
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
	public static boolean isEnglishAndNumeric(final CharSequence cs) {
		if (isEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		int amount = 0;
		for (int j = 0; j < size; j++) {
			int letter = cs.charAt(j);
			if (isEnglishAndNumeric(letter) || letter == LSystem.SPACE) {
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
		return filter(message, chars, LSystem.EMPTY);
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
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
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
	 * 过滤指定字符串中的开始与结束标记后返回新字符串结果
	 * 
	 * @param cs
	 * @param startFlag
	 * @param endFlag
	 * @return
	 */
	public static String filterStartEnd(String cs, char startFlag, char endFlag) {
		if (isEmpty(cs)) {
			return LSystem.EMPTY;
		}
		String result = cs;
		int start = cs.indexOf(startFlag);
		int end = cs.lastIndexOf(endFlag);
		if (start != -1 && end != -1 && end > start) {
			result = cs.substring(start + 1, end).trim();
		} else if (start != -1 && end == -1) {
			result = cs.substring(start + 1).trim();
		} else if (end != -1 && start == -1) {
			result = cs.substring(0, end).trim();
		}
		return result;
	}

	/**
	 * 过滤字符序列中所有不显示的占位符
	 * 
	 * @param s
	 * @return
	 */
	public static String spaceFilter(CharSequence s) {
		if (size(s) <= 0) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
		final int size = s.length();
		for (int i = 0; i < size; i++) {
			char c = s.charAt(i);
			if (!isWhitespace(c)) {
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
	public static String[] split(String str, char flag) {
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
			int indexStart = idx + 1;
			idx = str.indexOf(flag, idx + 1);
			if (idx == -1) {
				strings[i] = str.substring(indexStart).trim();
			} else {
				strings[i] = str.substring(indexStart, idx).trim();
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
	public static String[] split(String str, char... flags) {
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
		if (isNullOrEmpty(str) || isNullOrEmpty(separator)) {
			return new String[] {};
		}
		int sepLength = separator.length();
		if (sepLength == 0) {
			return new String[] { str };
		}
		if (separator.length() == 1) {
			return split(str, separator.charAt(0));
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
				tokens.add(str.substring(start, p).trim());
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
		StrBuilder sbr = new StrBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == LSystem.DOUBLE_QUOTES) {
				i++;
				while (i < str.length()) {
					if (str.charAt(i) == LSystem.DOUBLE_QUOTES && str.charAt(i + 1) == LSystem.DOUBLE_QUOTES) {
						sbr.append(LSystem.DOUBLE_QUOTES);
						i = i + 2;
					}
					if (str.charAt(i) == LSystem.DOUBLE_QUOTES) {
						break;
					} else {
						sbr.append(str.charAt(i));
						i++;
					}
				}
				i++;
			}

			if (str.charAt(i) != LSystem.COMMA) {
				sbr.append(str.charAt(i));
			} else {
				tempString = sbr.toString().trim();
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
	 * 给指定序列加上引号
	 * 
	 * @param cs
	 * @return
	 */
	public static String quote(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return "\"\"";
		}
		return "\"" + cs + "\"";
	}

	/**
	 * 给指定序列删去引号
	 * 
	 * @param cs
	 * @return
	 */
	public static String dequote(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return LSystem.EMPTY;
		}
		String ch = cs.toString();
		if (ch.length() < 2) {
			return ch;
		} else if (ch.toString().startsWith("\"") && ch.endsWith("\"")) {
			return ch.substring(1, ch.length() - 1);
		} else {
			return ch;
		}
	}

	/**
	 * 以指定标记分割过滤字符序列并返回一个数据集合
	 * 
	 * @param chars 要过滤的字符序列
	 * @param flag  以何种标记过滤分段
	 * @return
	 */
	public static TArray<CharSequence> splitArray(final CharSequence chars, final char flag) {
		return splitArray(chars, flag, new TArray<CharSequence>());
	}

	/**
	 * 以指定标记分割过滤字符序列并返回一个数据集合
	 * 
	 * @param chars  要过滤的字符序列
	 * @param flag   以何种标记过滤分段
	 * @param result 返回结果用的集合对象
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
			StrBuilder buf = new StrBuilder(string2.length);
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
	public static String replacesTrim(String message, String... oldStrings) {
		return replaces(message, LSystem.EMPTY, oldStrings);
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
			StrBuilder buf = new StrBuilder(line2.length);
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
	public static String replaceIgnoreCase(String line, String oldString, String newString, int[] count) {
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
			StrBuilder buf = new StrBuilder(line2.length);
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
			StrBuilder buf = new StrBuilder(line2.length);
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
	 * @param cs
	 * @return
	 */
	public static boolean isChinaLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			final char c = cs.charAt(i);
			if (isWhitespace(c) || isChinese(c)) {
				count++;
			}
		}
		return count >= size;
	}

	/**
	 * 检查一组字符串是否完全由日文组成
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isJapanLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			final char c = cs.charAt(i);
			if (isWhitespace(c) || isJapanese(c)) {
				count++;
			}
		}
		return count >= size;
	}

	/**
	 * 检查一组字符串是否完全由韩文组成
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isKoreanLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			final char c = cs.charAt(i);
			if (isWhitespace(c) || isKorean(c)) {
				count++;
			}
		}
		return count >= size;
	}

	/**
	 * 判断字符串中是否包含中文
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean containChinaLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		for (int i = 0; i < size; i++) {
			if (isChinese(cs.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串中是否包含日文
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean containJapaneseLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		for (int i = 0; i < size; i++) {
			if (isJapanese(cs.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串中是否包含韩文
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean containKoreanLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		for (int i = 0; i < size; i++) {
			if (isKorean(cs.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串中是否包含中日韩文字符串
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean containCJKLanguage(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int size = cs.length();
		for (int i = 0; i < size; i++) {
			if (isCJK(cs.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回字符序列中出现的中文字符数量
	 * 
	 * @param cs
	 * @return
	 */
	public static int getChineseCount(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < cs.length(); i++) {
			char ch = cs.charAt(i);
			if (isChinese(ch)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 返回字符序列中出现的日文字符数量
	 * 
	 * @param cs
	 * @return
	 */
	public static int getJapaneseCount(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < cs.length(); i++) {
			char ch = cs.charAt(i);
			if (isJapanese(ch)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 返回字符序列中出现的韩文字符数量
	 * 
	 * @param cs
	 * @return
	 */
	public static int getKoreanCount(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < cs.length(); i++) {
			char ch = cs.charAt(i);
			if (isKorean(ch)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 判断字符串是否全部是空格
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isBlankAll(CharSequence v) {
		for (int i = 0; i < v.length(); i++) {
			if (v.charAt(i) != LSystem.SPACE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为null或者不显示的占位符
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(CharSequence v) {
		return isNullOrWhitespace(v);
	}

	/**
	 * 判断CharSequence是否非空(和isNotEmpty与isEmpty分开函数名,防止串类型,这个没有trim操作,也就是" "不算空)
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isNullOrEmpty(CharSequence v) {
		return v == null || v.length() == 0;
	}

	public static boolean isNullOrWhitespace(CharSequence v) {
		if (v == null) {
			return true;
		}
		for (int i = 0; i < v.length(); i++) {
			if (!isWhitespace(v.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断String是否非空
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isNotEmpty(CharSequence v) {
		return !isEmpty(v);
	}

	/**
	 * 判断是否为null
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(CharSequence... v) {
		return v == null || v.length == 0;
	}

	/**
	 * 大写第一个字符
	 * 
	 * @param mes
	 * @return
	 */
	public static String toUpperCaseFirst(String mes) {
		if (isEmpty(mes)) {
			return LSystem.EMPTY;
		}
		if (mes.length() < 2) {
			return mes.substring(0).toUpperCase();
		}
		return mes.substring(0, 1).toUpperCase() + mes.substring(1, mes.length());
	}

	/**
	 * 小写第一个字符
	 * 
	 * @param mes
	 * @return
	 */
	public static String toLowerCaseFirst(String mes) {
		if (isEmpty(mes)) {
			return LSystem.EMPTY;
		}
		if (mes.length() < 2) {
			return mes.substring(0).toLowerCase();
		}
		return mes.substring(0, 1).toLowerCase() + mes.substring(1, mes.length());
	}

	public static String capitalize(String text) {
		return toUpperCaseFirst(text);
	}

	public static String uncapitalize(String text) {
		return toLowerCaseFirst(text);
	}

	/**
	 * 大写字符序列的首字母,其余全部小写
	 * 
	 * @param cs
	 * @return
	 */
	public static String toUpperFirstOtherAllLower(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return LSystem.EMPTY;
		}
		final int len = cs.length();
		StrBuilder sbr = new StrBuilder(len).append(toUpper(cs.charAt(0)));
		for (int i = 1; i < len; i++) {
			sbr.append(toLower(cs.charAt(i)));
		}
		return sbr.toString();
	}

	/**
	 * 小写字符序列的首字母,其余全部大写
	 * 
	 * @param cs
	 * @return
	 */
	public static String toLowerFirstOtherAllUpper(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return LSystem.EMPTY;
		}
		final int len = cs.length();
		StrBuilder sbr = new StrBuilder(len).append(toLower(cs.charAt(0)));
		for (int i = 1; i < len; i++) {
			sbr.append(toUpper(cs.charAt(i)));
		}
		return sbr.toString();
	}

	/**
	 * 转换字符串为字符串数组
	 * 
	 * @param mes
	 * @return
	 */
	public static String[] toList(final String mes) {
		return toList(mes, null);
	}

	/**
	 * 转换字符串为字符串数组
	 * 
	 * @param mes
	 * @param flag
	 * @return
	 */
	public static String[] toList(final String mes, final String flag) {
		String result = trim(mes);
		if (result.startsWith("{") && result.endsWith("}") || result.startsWith("[") && result.endsWith("]")
				|| result.startsWith("(") && result.endsWith(")")) {
			result = result.substring(1, result.length() - 1);
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1).trim();
		}
		String sep = flag;
		if (sep == null) {
			if (result.indexOf(':') >= 0) {
				sep = ":";
			} else {
				sep = ",";
			}
		}
		return split(result, sep);
	}

	public static IntArray toIntArray(final String mes, final String flag) {
		if (isNotEmpty(mes)) {
			String[] list = split(mes, flag);
			IntArray result = new IntArray(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(Integer.parseInt(list[i]));
			}
			return result;
		}
		return null;
	}

	public static FloatArray toFloatArray(final String mes, final String flag) {
		if (isNotEmpty(mes)) {
			String[] list = split(mes, flag);
			FloatArray result = new FloatArray(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(Float.parseFloat(list[i]));
			}
			return result;
		}
		return null;
	}

	public static LongArray toLongArray(final String mes, final String flag) {
		if (isNotEmpty(mes)) {
			String[] list = split(mes, flag);
			LongArray result = new LongArray(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(Long.parseLong(list[i]));
			}
			return result;
		}
		return null;
	}

	public static BoolArray toBoolArray(final String mes, final String flag) {
		if (isNotEmpty(mes)) {
			String[] list = split(mes, flag);
			BoolArray result = new BoolArray(list.length);
			for (int i = 0; i < list.length; i++) {
				result.add(toBoolean(list[i]));
			}
			return result;
		}
		return null;
	}

	public static String toTimeCN(long ms) {
		return toTime(ms, true);
	}

	public static String toTimeEN(long ms) {
		return toTime(ms, false);
	}

	public static String toTime(long ms, boolean cn) {

		final long ss = LSystem.SECOND;
		final long mi = LSystem.MINUTE;
		final long hh = LSystem.HOUR;
		final long dd = LSystem.DAY;

		final long day = ms / dd;
		final long hour = (ms - day * dd) / hh;
		final long minute = (ms - day * dd - hour * hh) / mi;
		final long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		final long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

		StrBuilder sbr = new StrBuilder();
		if (cn) {
			if (day > 0) {
				sbr.append(day).append(" 天,");
			}
			if (hour > 0) {
				sbr.append(hour).append(" 小时,");
			}
			if (minute > 0) {
				sbr.append(minute).append(" 分钟,");
			}
			if (second > 0) {
				sbr.append(second).append(" 秒,");
			}
			if (milliSecond > 0) {
				sbr.append(milliSecond).append(" 毫秒,");
			}
		} else {
			if (day > 0) {
				sbr.append(day).append(" day,");
			}
			if (hour > 0) {
				sbr.append(hour).append(" hour,");
			}
			if (minute > 0) {
				sbr.append(minute).append(" minute,");
			}
			if (second > 0) {
				sbr.append(second).append(" second,");
			}
			if (milliSecond > 0) {
				sbr.append(milliSecond).append(" millisecond,");
			}
		}
		if (sbr.length() > 0) {
			sbr = sbr.deleteCharAt(sbr.length() - 1);
		}
		return sbr.toString();
	}

	public static String toTime(float secondstime) {
		return toTime(secondstime, 2);
	}

	public static String toTime(float secondstime, int numDigits) {
		final int seconds = (int) (secondstime % 60);
		final int minutes = (int) ((secondstime / 60) % 60);
		final int hours = (int) ((secondstime / 3600) % 24);
		return new String(MathUtils.addZeros(hours, numDigits) + ":" + MathUtils.addZeros(minutes, numDigits) + ":"
				+ MathUtils.addZeros(seconds, numDigits));
	}

	/**
	 * 检查指定字符串中是否存在中文字符。
	 * 
	 * @param checkStr 指定需要检查的字符串。
	 * @return 逻辑值（True Or False）。
	 */
	public static boolean hasChinese(CharSequence v) {
		return getChineseCount(v) > 0;
	}

	/**
	 * 检测最后一个字符是否为数字
	 * 
	 * @param v
	 * @return
	 */
	public static boolean hasNumberAtEnd(CharSequence v) {
		int letterChecking = v.length();
		do {
			letterChecking--;
		} while (letterChecking > -1 && isDigit(v.charAt(letterChecking)));
		if (letterChecking == v.length() - 1 && !isDigit(v.charAt(letterChecking))) {
			return false;
		}
		return true;
	}

	/**
	 * 检测最后一个字符是否为英文
	 * 
	 * @param v
	 * @return
	 */
	public static boolean hasEnglishAtEnd(CharSequence v) {
		int letterChecking = v.length();
		do {
			letterChecking--;
		} while (letterChecking > -1 && isAsciiLetter(v.charAt(letterChecking)));
		if (letterChecking == v.length() - 1 && !isAsciiLetter(v.charAt(letterChecking))) {
			return false;
		}
		return true;
	}

	/**
	 * /** 检查是否为纯字母
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isAlphabet(CharSequence v) {
		if (v == null || v.length() == 0) {
			return false;
		}
		int size = v.length();
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (isAlphabet(v.charAt(i))) {
				count++;
			} else {
				break;
			}
		}
		return count >= size;
	}

	public static boolean isAlphabetLower(CharSequence v) {
		return isAllLowercaseAlpha(v);
	}

	public static boolean isAlphabetUpper(CharSequence v) {
		return isAllUppercaseAlpha(v);
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
	 * 检查字符串数值是否为相对位置
	 * 
	 * @param mes
	 * @return
	 */
	public static boolean isRelative(String mes) {
		if (isEmpty(mes) || mes.length() < 2) {
			return false;
		}
		if (!mes.substring(0, 1).equals("~")) {
			return false;
		}
		return MathUtils.isNumber(mes.substring(1));
	}

	/**
	 * 变更数字字符串格式为指定分隔符货币格式(比如100000000用分隔符","分割后就是100,000,000)
	 * 
	 * @param v
	 * @return
	 */
	public static String changFormatToMoney(String v) {
		return changFormatToMoney(v, ",", false);
	}

	/**
	 * 变更数字字符串格式为指定分隔符货币格式
	 * 
	 * @param v
	 * @param split
	 * @param tag
	 * @return
	 */
	public static String changFormatToMoney(String v, String split, boolean tag) {
		if (!MathUtils.isNan(v)) {
			return v;
		}
		int count = 0;
		String sbr = LSystem.EMPTY;
		if (tag) {
			if (v.charAt(0) >= 48 && v.charAt(0) <= 57) {
				v = "+" + v;
			}
		}
		for (int i = v.length() - 1; i > -1; i--) {
			sbr = v.charAt(i) + sbr;
			int charCode = v.charAt(i);
			if (charCode >= 48 && charCode <= 57) {
				if (i > 0) {
					charCode = v.charAt(i - 1);
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

	public static boolean isAllFullAlphaChar(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		final int length = cs.length();
		for (int i = 0; i < length; i++) {
			final char letter = cs.charAt(i);
			if (!isWhitespace(letter) && !isFullChar(letter))
				return false;
		}
		return true;
	}

	public static boolean isAllHalfAlphaChar(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		final int length = cs.length();
		for (int i = 0; i < length; i++) {
			final char letter = cs.charAt(i);
			if (!isWhitespace(letter) && !isHalfChar(letter))
				return false;
		}
		return true;
	}

	public static String updateWhitespaceChar(CharSequence cs, char dst) {
		if (isNullOrEmpty(cs)) {
			return LSystem.EMPTY;
		}
		final int length = cs.length();
		final StrBuilder sbr = new StrBuilder(length);
		for (int i = 0; i < length; i++) {
			final char letter = cs.charAt(i);
			if (isWhitespace(letter)) {
				sbr.append(dst);
			} else {
				sbr.append(letter);
			}
		}
		return sbr.toString();
	}

	/**
	 * 检查是否为字母与数字混合
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isAlphabetNumeric(CharSequence v) {
		if (v == null || v.length() == 0)
			return true;
		for (int i = 0; i < v.length(); i++) {
			char letter = v.charAt(i);
			if (!isWhitespace(letter) && !isAlphaOrDigit(letter))
				return false;
		}
		return true;
	}

	/**
	 * 检查指定字符序列是否全部大写
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isAllUppercaseAlpha(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int length = cs.length();
		for (int i = 0; i < length; i++) {
			char letter = cs.charAt(i);
			if (!isWhitespace(letter) && !(isUppercaseAlpha(letter)))
				return false;
		}
		return true;
	}

	/**
	 * 检查指定字符序列是否全部小写
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isAllLowercaseAlpha(CharSequence cs) {
		if (isNullOrEmpty(cs)) {
			return false;
		}
		int length = cs.length();
		for (int i = 0; i < length; i++) {
			char letter = cs.charAt(i);
			if (!isWhitespace(letter) && !(isLowercaseAlpha(letter)))
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
			StrBuilder buffer = new StrBuilder(line2.length);
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
		if (isEmpty(escaped)) {
			return LSystem.EMPTY;
		}
		int length = escaped.length();
		int i = 0;
		StrBuilder sbr = new StrBuilder(escaped.length() / 2);

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
	public static String escape(CharSequence raw) {
		if (isEmpty(raw)) {
			return LSystem.EMPTY;
		}
		int length = raw.length();
		int i = 0;
		StrBuilder sbr = new StrBuilder(raw.length() / 2);

		while (i < length) {
			char c = raw.charAt(i++);

			if (CharUtils.isLetterOrDigit(c) || CharUtils.isEscapeExempt(c)) {
				sbr.append(c);
			} else {
				int i1 = raw.charAt(i - 1);
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
	 * @param chars   原始字符序列
	 * @param padChar 字符序列长度不够时补位用字符
	 * @param len     字符序列长度补位生效的要求长度
	 * @return
	 */
	public static CharSequence padFront(final CharSequence chars, final char padChar, final int len) {
		final int padCount = len - chars.length();
		if (padCount <= 0) {
			return chars;
		} else {
			final StrBuilder sbr = new StrBuilder();

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
			final StrBuilder sbr = new StrBuilder(chars);
			for (int i = padCount - 1; i >= 0; i--) {
				sbr.append(padChar);
			}
			return sbr.toString();
		}
	}

	private static boolean unificationAllow(char ch) {
		return !isWhitespace(ch) && !isZeroWidthChar(ch);
	}

	public static String merge(String[] messages) {
		if (isEmpty(messages)) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
		for (String mes : messages) {
			if (mes != null) {
				sbr.append(mes.trim());
			}
		}
		return sbr.toString().trim();
	}

	public static String merge(CharSequence[] messages) {
		if (messages == null || messages.length == 0) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder();
		for (CharSequence mes : messages) {
			if (mes != null) {
				sbr.append(mes);
			}
		}
		return sbr.toString().trim();
	}

	public static boolean isMatch(CharSequence message, CharSequence flag) {
		if (isEmpty(message, flag)) {
			return false;
		}
		final int len = message.length();
		for (int i = 0; i < len; i++) {
			char ch = message.charAt(i);
			for (int j = 0; j < flag.length(); j++) {
				if (ch == flag.charAt(j)) {
					return true;
				}
			}
		}
		return false;
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
		return unificationStrings(tempChars, mes, limit, false);
	}

	public static String unificationStrings(CharArray tempChars, String mes, CharSequence limit, boolean sorted) {
		if (isEmpty(mes)) {
			return LSystem.EMPTY;
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
			return LSystem.EMPTY;
		} else {
			return sorted ? tempChars.sort().getString() : tempChars.getString();
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
		return unificationCharSequence(tempChars, messages, limit, false);
	}

	public static String unificationCharSequence(CharArray tempChars, CharSequence[] messages, CharSequence limit,
			boolean sorted) {
		if (messages == null || messages.length == 0) {
			return LSystem.EMPTY;
		}
		tempChars.clear();
		final boolean mode = (limit == null || limit.length() == 0);
		for (int idx = 0; idx < messages.length; idx++) {
			final CharSequence mes = messages[idx];
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
			return LSystem.EMPTY;
		} else {
			return sorted ? tempChars.sort().getString() : tempChars.getString();
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
		return unificationStrings(tempChars, messages, limit, false);
	}

	public static String unificationStrings(CharArray tempChars, String[] messages, CharSequence limit,
			boolean sorted) {
		if (isEmpty(messages)) {
			return LSystem.EMPTY;
		}
		tempChars.clear();
		final boolean mode = (limit == null || limit.length() == 0);
		for (int idx = 0; idx < messages.length; idx++) {
			final CharSequence mes = messages[idx];
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
			return LSystem.EMPTY;
		} else {
			return sorted ? tempChars.sort().getString() : tempChars.getString();
		}
	}

	public static String unificationChars(char[] messages) {
		return unificationChars(messages, null);
	}

	public static String unificationChars(CharArray tempChars, char[] messages) {
		return unificationChars(tempChars, messages, null);
	}

	/**
	 * 合并字符数组到CharArray字符集合中去(不包含limit中限定的字符)
	 * 
	 * @param messages
	 * @param limit
	 * @return
	 */
	public static String unificationChars(char[] messages, CharSequence limit) {
		return unificationChars(new CharArray(128), messages, null);
	}

	/**
	 * 合并字符数组到CharArray字符集合中去(不包含limit中限定的字符)
	 * 
	 * @param tempChars
	 * @param messages
	 * @param limit
	 * @return
	 */
	public static String unificationChars(CharArray tempChars, char[] messages, CharSequence limit) {
		return unificationChars(tempChars, messages, limit, false);
	}

	/**
	 * 合并字符数组到CharArray字符集合中去(不包含limit中限定的字符)
	 * 
	 * @param tempChars
	 * @param messages
	 * @param limit
	 * @param sorted
	 * @return
	 */
	public static String unificationChars(CharArray tempChars, char[] messages, CharSequence limit, boolean sorted) {
		if (messages == null || messages.length == 0) {
			return LSystem.EMPTY;
		}
		tempChars.clear();
		final boolean mode = (limit == null || limit.length() == 0);
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
			return LSystem.EMPTY;
		} else {
			return sorted ? tempChars.sort().getString() : tempChars.getString();
		}
	}

	public static CharArray unificationCharArray(char[] messages) {
		return unificationCharArray(messages, null);
	}

	public static CharArray unificationCharArray(CharArray tempChars, char[] messages) {
		return unificationCharArray(tempChars, messages, null);
	}

	public static CharArray unificationCharArray(char[] messages, CharSequence limit) {
		return unificationCharArray(new CharArray(128), messages, null);
	}

	public static CharArray unificationCharArray(CharArray tempChars, char[] messages, CharSequence limit) {
		return unificationCharArray(tempChars, messages, limit, false);
	}

	public static CharArray unificationCharArray(CharArray tempChars, char[] messages, CharSequence limit,
			boolean sorted) {
		if (messages == null || messages.length == 0) {
			return new CharArray();
		}
		tempChars.clear();
		final boolean mode = (limit == null || limit.length() == 0);
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
			return new CharArray();
		} else {
			return sorted ? tempChars.sort() : tempChars;
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
		if ((c instanceof StringBuffer) || (c instanceof StringBuilder) || (c instanceof StrBuilder)
				|| (c instanceof String)) {
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
	 * 转化字符串数组为Array集合
	 * 
	 * @param str
	 * @return
	 */
	public static TArray<String> getStringsToList(String... str) {
		if (str == null || str.length == 0) {
			return null;
		}
		int len = str.length;
		TArray<String> list = new TArray<String>(len);
		for (int i = 0; i < len; i++) {
			list.add(str[i]);
		}
		return list;
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
		} else if (c instanceof StrBuilder) {
			((StrBuilder) c).getChars(start, end, dest, destoff);
		} else {
			for (int i = start; i < end; i++) {
				dest[destoff++] = c.charAt(i);
			}
		}
	}

	/**
	 * 获得换行符数量
	 * 
	 * @param chars
	 * @return
	 */
	public static int getLineCount(final CharSequence chars) {
		if (isNullOrEmpty(chars)) {
			return 0;
		}
		int lines = 1;
		for (int i = 0; i < chars.length(); i++) {
			char ch = chars.charAt(i);
			if (isEol(ch)) {
				lines++;
			}
		}
		return lines;
	}

	/**
	 * 以指定字符过滤一组字符串，并返回其数组形式。
	 * 
	 * @param line
	 * @param delimiter
	 * @return
	 */
	public static TArray<String> getDelimiterStrings(String line, char delimiter) {
		TArray<String> tokens = new TArray<>();
		boolean precedingBackslash = false;
		boolean isToken = false;
		int startIndex = 0;
		int endIndex = 0;
		for (; endIndex < line.length();) {
			char c = line.charAt(endIndex);
			if (c != LSystem.SPACE && c != LSystem.TF && c != LSystem.PB) {
				isToken = true;
			}
			if ((c == delimiter) && !precedingBackslash) {
				if (isToken) {
					String token = line.substring(startIndex, endIndex);
					token = getStripUnWhitespace(token);
					tokens.add(token);
					isToken = !Character.isWhitespace(c);
				}
				startIndex = endIndex + 1;
			}
			precedingBackslash = c == LSystem.BACKSLASH ? !precedingBackslash : false;
			endIndex += 1;
		}
		if (isToken) {
			String token = line.substring(startIndex);
			token = getStripUnWhitespace(token);
			tokens.add(token);
		}
		return tokens;
	}

	/**
	 * 获得字符串过滤结果，省略一切空格与换行符号
	 * 
	 * @param str
	 * @return
	 */
	public static String getStripUnWhitespace(String str) {
		if (str == null) {
			return LSystem.EMPTY;
		}
		int start = 0;
		int end = str.length();
		while (start < end) {
			char c = str.charAt(start);
			if (c != LSystem.SPACE && c != LSystem.TF && c != LSystem.PB) {
				break;
			}
			start += 1;
		}
		while (end > start) {
			char c = str.charAt(end - 1);
			if ((c != LSystem.SPACE && c != LSystem.TF && c != LSystem.PB)
					|| (end - 2 >= start && str.charAt(end - 2) == LSystem.BACKSLASH)) {
				break;
			}
			end -= 1;
		}
		return str.substring(start, end);
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
	 * 包含常规符号
	 * 
	 * @return
	 */
	public static boolean containsRegularSymbols(CharSequence cs) {
		if (isEmpty(cs)) {
			return false;
		}
		for (int i = 0; i < cs.length(); i++) {
			char ch = cs.charAt(i);
			if (!isChinese(ch) && !isAlphaOrDigit(ch) && !isNumeric(ch) && !isSingle(ch) && !isReserved(ch)
					&& !isFullWidth(ch)) {
				return false;
			}
		}
		return true;
	}

	private static String getChunk(String s, int slength, int count) {
		StrBuilder sbr = new StrBuilder();
		char ch = s.charAt(count);
		sbr.append(ch);
		count++;
		if (isDigit(ch)) {
			while (count < slength) {
				ch = s.charAt(count);
				if (!isDigit(ch)) {
					break;
				}
				sbr.append(ch);
				count++;
			}
		} else {
			while (count < slength) {
				ch = s.charAt(count);
				if (isDigit(ch)) {
					break;
				}
				sbr.append(ch);
				count++;
			}
		}
		return sbr.toString();
	}

	/**
	 * 为字符串进行排序
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int checkCompareTo(String s1, String s2) {
		if (isNullOrEmpty(s1) || isNullOrEmpty(s2)) {
			return 0;
		}
		int m1 = 0;
		int m2 = 0;
		int s1Length = s1.length();
		int s2Length = s2.length();
		for (; m1 < s1Length && m2 < s2Length;) {
			String chunk1 = getChunk(s1, s1Length, m1);
			m1 += chunk1.length();
			String chunk2 = getChunk(s2, s2Length, m2);
			m2 += chunk2.length();
			int result = 0;
			if (isDigit(chunk1.charAt(0)) && isDigit(chunk2.charAt(0))) {
				int thisChunkLength = chunk1.length();
				result = thisChunkLength - chunk2.length();
				if (result == 0) {
					for (int i = 0; i < thisChunkLength; i++) {
						result = chunk1.charAt(i) - chunk2.charAt(i);
						if (result != 0) {
							return result;
						}
					}
				}
			} else {
				result = chunk1.compareTo(chunk2);
			}
			if (result != 0) {
				return result;
			}
		}
		return s1Length - s2Length;
	}

	/**
	 * 判定是否全角
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isFullWidth(char ch) {
		if (ch > 65280 && ch < 65375) {
			return true;
		}
		if ((ch >= 8216 && ch <= 8223) || (ch >= 12288 && ch <= 12543)) {
			return true;
		}
		if (isChinese(ch)) {
			return true;
		}
		return false;
	}

	/**
	 * 判定是否半角
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isHalfWidth(char ch) {
		return !isFullWidth(ch);
	}

	/**
	 * 统计指定集合中字符序列对象的总长度
	 * 
	 * @param chars 字符序列集合
	 * @return
	 */
	public static int countCharacters(final TArray<CharSequence> chars) {
		return countCharacters(chars, false);
	}

	/**
	 * 统计指定集合中字符序列对象的总长度
	 * 
	 * @param chars             字符序列集合
	 * @param ignoreWhitespaces 是否跳过仅占位而不显示的字符
	 * @return
	 */
	public static int countCharacters(final TArray<CharSequence> chars, final boolean ignoreWhitespaces) {
		int characters = 0;
		if (ignoreWhitespaces) {
			for (int i = chars.size - 1; i >= 0; i--) {
				final CharSequence text = chars.get(i);
				for (int j = text.length() - 1; j >= 0; j--) {
					final char character = text.charAt(j);
					if (!isWhitespace(character)) {
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
		final int h = s.indexOf(LSystem.LF);
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
			return LSystem.EMPTY;
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
		StrBuilder sbr = new StrBuilder();
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
	public static String reverse(final CharSequence v) {
		if (size(v) <= 0) {
			return LSystem.EMPTY;
		}
		final int size = v.length();
		final StrBuilder sbr = new StrBuilder(size);
		for (int i = size - 1; i >= 0; i--) {
			sbr.append(v.charAt(i));
		}
		return sbr.toString();
	}

	/**
	 * 返回指定字符序列长度(为null时返回-1)
	 * 
	 * @param v
	 * @return
	 */
	public static int size(final CharSequence v) {
		return v == null ? -1 : v.length();
	}

	/**
	 * 返回指定字符序列长度(为null时返回0)
	 * 
	 * @param v
	 * @return
	 */
	public static int length(final CharSequence v) {
		if (isNullOrEmpty(v)) {
			return 0;
		}
		return v.length();
	}

	/**
	 * 返回指定字符序列中指定索引对应的字符
	 * 
	 * @param v
	 * @param i
	 * @return
	 */
	public static char charAt(final CharSequence v, final int i) {
		return size(v) <= i ? 0 : v.charAt(i);
	}

	/**
	 * 格式化回车符，使字符串中只出现LSystem.LF
	 * 
	 * @param src
	 * @return
	 */
	public static String formatCRLF(CharSequence cs) {
		if (isEmpty(cs)) {
			return LSystem.EMPTY;
		}
		String src = cs.toString();
		int pos = src.indexOf(LSystem.CR);
		if (pos != -1) {
			int len = src.length();
			StrBuilder buffer = new StrBuilder();
			int lastPos = 0;
			while (pos != -1) {
				buffer.append(src, lastPos, pos);
				if (pos == len - 1 || src.charAt(pos + 1) != LSystem.LF) {
					buffer.append(LSystem.LF);
				}
				lastPos = pos + 1;
				if (lastPos >= len) {
					break;
				}
				pos = src.indexOf(LSystem.CR, lastPos);
			}
			if (lastPos < len) {
				buffer.append(src, lastPos, len);
			}
			src = buffer.toString();
		}
		return src;
	}

	/**
	 * 格式化字符序列为escape,并在原始\n位置插入指定分隔符
	 * 
	 * @param cs
	 * @param indent
	 * @return
	 */
	public static String formatEscape(CharSequence cs, CharSequence indent) {
		String text = cs.toString();
		if (text.indexOf(LSystem.LF) != -1) {
			if (text.length() == 1) {
				return quote("\\n");
			}
			StrBuilder sbr = new StrBuilder();
			sbr.append("|");
			String[] lines = split(text, LSystem.LF);
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				sbr.append(LSystem.LS + indent + line);
			}
			if (text.charAt(text.length() - 1) == LSystem.LF) {
				sbr.append(LSystem.LS + indent);
			}
			return sbr.toString();
		} else if (isNullOrWhitespace(text)) {
			return quote(text);
		} else {
			final String indicators = ":[]{},\"'|*&";
			boolean quoteIt = false;
			for (char c : indicators.toCharArray())
				if (text.indexOf(c) != -1) {
					quoteIt = true;
					break;
				}
			if (text.trim().length() != text.length()) {
				quoteIt = true;
			}
			if (MathUtils.isNumber(text)) {
				quoteIt = true;
			}
			if (quoteIt) {
				text = escape(text);
				text = quote(text);
			}
			return text;
		}
	}

	/**
	 * 返回指定对象的字符串信息
	 * 
	 * @param o
	 * @return
	 */
	public static String toString(final Object o) {
		return toString(o, null);
	}

	public static String toString(final Object o, final String def) {
		return o == null ? def : o.toString();
	}

	public static String[] toStrings(TArray<String> list) {
		if (list == null || list.size == 0) {
			return null;
		}
		final String[] result = new String[list.size];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public static String toHexString(final String cs) {
		try {
			return toHex(cs.getBytes(LSystem.ENCODING));
		} catch (Exception e) {
			return LSystem.EMPTY;
		}
	}

	public static String getString(final CharSequence... chs) {
		return new StrBuilder(chs).toString();
	}

	public static String getRandString() {
		return getRandString(32);
	}

	public static String getRandString(final int size) {
		StrBuilder str = new StrBuilder(size);
		char ch;
		for (int i = 0; i < size; i++) {
			ch = (char) MathUtils.floor(MathUtils.random() * 26 + 65);
			str.append(ch);
		}
		return str.toString();
	}

	public static CharSequence getRoot(final CharSequence cur, final CharSequence ch) {
		for (int i = 0; i < cur.length(); i++) {
			int idx = i + 1;
			if (cur.subSequence(i, idx).equals(ch)) {
				return cur.subSequence(0, i);
			}
		}
		return cur;
	}

	public static CharSequence getBranch(final CharSequence cur, final CharSequence ch) {
		for (int i = 0; i < cur.length(); i++) {
			int idx = i + 1;
			if (cur.subSequence(i, idx).equals(ch)) {
				return cur.subSequence(idx, idx + cur.length() - i - 1);
			}
		}
		return cur;
	}

	public static CharSequence getLastBranch(final CharSequence cur, final CharSequence ch) {
		int i = cur.length() - 1;
		while (i >= 0) {
			int idx = i + 1;
			if (cur.subSequence(i, idx).equals(ch)) {
				return cur.subSequence(idx, idx + cur.length() - i - 1);
			}
			i--;
		}
		return cur;
	}

	public int getSymbolLength(String str) {
		int length = str.length();
		int count = 0;
		char charCode = 0;
		for (int i = 0; i < length; i++) {
			charCode = str.charAt(i);
			if (charCode == 0x200d) {
				continue;
			}
			if (charCode >= 0xd800 && charCode <= 0xdbff) {
				charCode = str.charAt(i + 1);
				if (charCode >= 0xdc00 && charCode <= 0xdfff) {
					if (i + 2 >= length || str.charAt(i + 2) != 0x200d) {
						count++;
					}
					i++;
					continue;
				}
			}
			count++;
		}
		return count;
	}

	public String getSymbolAt(String str, int index) {
		int length = str.length();
		int len = 0;
		int count = 0;
		int start = 0;
		char charCode = 0;
		for (int i = 0; i < length; i++) {
			charCode = str.charAt(i);
			if (charCode == 0x200d) {
				len++;
				continue;
			}
			if (charCode >= 0xd800 && charCode <= 0xdbff) {
				len++;
				charCode = str.charAt(i + 1);
				if (charCode >= 0xdc00 && charCode <= 0xdfff) {
					len++;
					if (i + 2 >= length || str.charAt(i + 2) != 0x200d) {
						if (index == count) {
							return str.substring(start, len);
						}
						start += len;
						count++;
						len = 0;
					}
					i++;
					continue;
				}
			}
			if (index == count) {
				return String.valueOf(str.charAt(i));
			}
			start = i + 1;
			count++;
			len = 0;
		}
		return LSystem.EMPTY;
	}

	public static String byteArrayToString(final byte[] array) {
		return byteArrayToString(array, 0, array.length);
	}

	public static String byteArrayToString(final byte[] array, int off, int len) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < len; ++i) {
			final byte b = array[off + i];
			int digit = (b >> 4) & 0xf;
			builder.append((char) ((digit < 10) ? (digit + '0') : (digit - 10 + 'a')));
			digit = b & 0xf;
			builder.append((char) ((digit < 10) ? (digit + '0') : (digit - 10 + 'a')));
		}
		return builder.toString();
	}

}
