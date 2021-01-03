using java.lang;
using System;

namespace loon.utils
{
    public sealed class StringUtils : CharUtils
    {
		public static string Format(string format, params object[] o)
		{
			
			bool fempty = IsEmpty(format);
			if (fempty)
			{
				return LSystem.EMPTY;
			}
			if (!fempty && CollectionUtils.IsEmpty(o))
			{
				return format;
			}
			StrBuilder b = new StrBuilder();
			int p = 0;
			for (; ; )
			{
				int i = format.IndexOf(LSystem.DELIM_START, p);
				if (i == -1)
				{
					break;
				}
				int idx = format.IndexOf(LSystem.DELIM_END, i + 1);
				if (idx == -1)
				{
					break;
				}
				if (p != i)
				{
				
					b.Append(format.JavaSubstring(p, i));
				}
				string nstr = format.JavaSubstring(i + 1, idx);
				try
				{
					int n = int.Parse(nstr);
					if (n >= 0 && n < o.Length)
					{
						b.Append(o[n]);
					}
					else
					{
						b.Append(LSystem.DELIM_START).Append(nstr).Append(LSystem.DELIM_END);
					}
				}
				catch (System.Exception)
				{
					b.Append(LSystem.DELIM_START).Append(nstr).Append(LSystem.DELIM_END);
				}
				p = idx + 1;
			}
			b.Append(format.JavaSubstring(p));

			return b.ToString();
		}

		public static string Cpy(char ch, int count)
		{
			StrBuilder sbr = new StrBuilder(count);
			for (int i = 0; i < count; i++)
			{
				sbr.Append(ch);
			}
			return sbr.ToString();
		}

		public static bool IsLimit(string cs, int minX, int maxX)
		{
			if (IsNullOrEmpty(cs))
			{
				return false;
			}
			return MathUtils.IsLimit(cs.Length(), minX, maxX);
		}

		private static readonly string[] BOOL_POOL_TRUE = new string[] { "true", "yes", "ok", "on" };

		private static readonly string[] BOOL_POOL_FALSE = new string[] { "false", "no", "fake", "off" };

		/// <summary>
		/// 判断指定字符串内容是否为布尔值(不判定数字为布尔，并且只判定布尔值，不考虑值真假问题)
		/// </summary>
		/// <param name="o">
		/// @return </param>
		public static bool IsBoolean(string o)
		{
			if (IsEmpty(o))
			{
				return false;
			}
			string str = o.Trim().ToLower();
			return Contains(str, BOOL_POOL_TRUE) || Contains(str, BOOL_POOL_FALSE);
		}

		/// <summary>
		/// 转换指定字符串内容为布尔值(判定数字为布尔)
		/// </summary>
		/// <param name="o">
		/// @return </param>
		public static bool ToBoolean(string o)
		{
			if (IsEmpty(o))
			{
				return false;
			}
			string str = o.Trim().ToLower();
			if (Contains(str, BOOL_POOL_TRUE))
			{
				return true;
			}
			else if (Contains(str, BOOL_POOL_FALSE))
			{
				return false;
			}
			else if (MathUtils.IsNan(str))
			{
				return double.Parse(str) > 0;
			}
			return false;
		}

		public static bool AssertEqual(string a, string b)
		{
			if (a == b)
			{
				return true;
			}
			if (a == null || b == null)
			{
				return a == null && b == null;
			}
			else
			{
				return a.Equals(b);
			}
		}

		public static int Size(CharSequence v)
		{
			return v == null ? -1 : v.Length();
		}

		public static bool IsSpace(char c)
		{
			switch (c)
			{
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

		/// <summary>
		/// 过滤字符序列中所有不显示的占位符
		/// </summary>
		/// <param name="s">
		/// @return </param>
		public static string SpaceFilter(CharSequence s)
		{
			if (Size(s) <= 0)
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = s.Length();
			for (int i = 0; i < size; i++)
			{
				char c = s.CharAt(i);
				if (!IsSpace(c))
				{
					sbr.Append(c);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 判断两个字符串是否等值
		/// </summary>
		/// <param name="a"> </param>
		/// <param name="b">
		/// @return </param>
		public static bool Equals(string a, string b)
		{
			return AssertEqual(a, b);
		}

		/// <summary>
		/// 判定两组字符序列是否内容相等
		/// </summary>
		/// <param name="a"> </param>
		/// <param name="b">
		/// @return </param>
		public static bool Equals(CharSequence a, CharSequence b)
		{
			return Equals(a, b, false);
		}

		/// <summary>
		/// 判定两组字符序列是否内容相等
		/// </summary>
		/// <param name="a"> </param>
		/// <param name="b"> </param>
		/// <param name="ignoreWhitespaces"> 如果此项为true,则无视所有不显示的占位符,即StringUtils.equals("abc\n",
		///                          "abc",true)
		///                          这样含有换行符之类不显示字符的字符串在比较时此标记为true时将等值,为false时不等值,默认为false
		/// @return </param>
		public static bool Equals(CharSequence a, CharSequence b, bool ignoreWhitespaces)
		{
			if (a == null)
			{
				return (b == null);
			}
			else if (b == null)
			{
				return false;
			}
			if (ignoreWhitespaces)
			{
				return Equals(new JavaString(SpaceFilter(a)), new JavaString(SpaceFilter(b)), false);
			}
			else
			{
				int size = a.Length();
				if (b.Length() != size)
				{
					return false;
				}
				for (int i = size - 1; i >= 0; i--)
				{
					if (a.CharAt(i) != b.CharAt(i))
					{
						return false;
					}
				}
			}
			return true;
		}

		/// <summary>
		/// 去除字符串中所有空白字符
		/// </summary>
		/// <param name="text">
		/// @return </param>
		public static string Trim(string text)
		{
			return (Rtrim(Ltrim(text.Trim()))).Trim();
		}

		/// <summary>
		/// 去除字符串右侧空白字符
		/// </summary>
		/// <param name="s">
		/// @return </param>
		public static string Rtrim(string s)
		{
			int off = s.Length - 1;
			while (off >= 0 && s.CharAt(off) <= LSystem.SPACE)
			{
				off--;
			}
			return off < s.Length - 1 ? s.JavaSubstring(0, off + 1) : s;
		}

		/// <summary>
		/// 去除字符串左侧空白字符
		/// </summary>
		/// <param name="s">
		/// @return </param>
		public static string Ltrim(string s)
		{
			int off = 0;
			while (off < s.Length && s[off] <= LSystem.SPACE)
			{
				off++;
			}
			return off > 0 ? s.JavaSubstring(off) : s;
		}

		/// <summary>
		/// 判定指定字符串是否包含指定开头
		/// </summary>
		/// <param name="s"> </param>
		/// <param name="sub">
		/// @return </param>
		public static bool StartsWith(CharSequence s, CharSequence sub)
		{
			return (s != null) && (sub != null) && s.ToString().StartsWith(sub.ToString(), StringComparison.Ordinal);
		}

		public static bool StartsWith(string s, string sub)
		{
			return (s != null) && (sub != null) && s.StartsWith(sub, StringComparison.Ordinal);
		}
		/// <summary>
		/// 判定指定字符串是否包含指定开头
		/// </summary>
		/// <param name="n"> </param>
		/// <param name="tag">
		/// @return </param>
		public static bool StartsWith(CharSequence n, char tag)
		{
			return (n != null) && n.CharAt(0) == tag;
		}

		public static bool StartsWith(string n, char tag)
		{
			return (n != null) && n.CharAt(0) == tag;
		}

		/// <summary>
		/// 检查是否以指定字符序列开头
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="prefix"> </param>
		/// <param name="isIgnore">
		/// @return </param>
		public static bool StartWith(CharSequence str, CharSequence prefix, bool isIgnore)
		{
			if (null == str || null == prefix)
			{
				return null == str && null == prefix;
			}
			if (isIgnore)
			{
				return str.ToString().ToLower().StartsWith(prefix.ToString().ToLower());
			}
			else
			{
				return str.ToString().StartsWith(prefix.ToString(), StringComparison.Ordinal);
			}
		}
		public static bool StartWith(string str, string prefix, bool isIgnore)
		{
			if (null == str || null == prefix)
			{
				return null == str && null == prefix;
			}
			if (isIgnore)
			{
				return str.ToLower().StartsWith(prefix.ToLower());
			}
			else
			{
				return str.StartsWith(prefix, StringComparison.Ordinal);
			}
		}

		/// <summary>
		/// 检查指定字符序列开头中是否包含如下字符序列
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="prefixes">
		/// @return </param>
		public static bool StartsAnyWith(CharSequence str, params CharSequence[] prefixes)
		{
			if (IsNullOrEmpty(str) || CollectionUtils.IsEmpty(prefixes))
			{
				return false;
			}
			foreach (CharSequence suffix in prefixes)
			{
				if (StartWith(str, suffix, false))
				{
					return true;
				}
			}
			return false;
		}

		/// <summary>
		/// 判定指定字符串是否包含指定结尾
		/// </summary>
		/// <param name="s"> </param>
		/// <param name="sub">
		/// @return </param>
		public static bool EndsWith(CharSequence s, CharSequence sub)
		{
			return (s != null) && (sub != null) && s.ToString().EndsWith(sub.ToString(), StringComparison.Ordinal);
		}

		/// <summary>
		/// 判定指定字符串是否包含指定结尾
		/// </summary>
		/// <param name="n"> </param>
		/// <param name="tag">
		/// @return </param>
		public static bool EndsWith(CharSequence n, char tag)
		{
			return (n != null) && n.Length() > 0 && n.CharAt(n.Length() - 1) == tag;
		}

		public static bool EndsWith(string n, char tag)
		{
			return (n != null) && n.Length() > 0 && n.CharAt(n.Length() - 1) == tag;
		}
		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params float[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params int[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params long[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params bool[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 联合指定对象并输出为字符串
		/// </summary>
		/// <param name="flag"> </param>
		/// <param name="o">
		/// @return </param>
		public static string Join(char? flag, params CharSequence[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 拼接指定对象数组为string
		/// </summary>
		/// <param name="o">
		/// @return </param>
		public static string Concat(params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder(o.Length);
			for (int i = 0; i < o.Length; i++)
			{
				if (o[i] is int?)
				{
					sbr.Append((int?)o[i]);
				}
				else
				{
					sbr.Append(o[i]);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 拼接指定字符数组
		/// </summary>
		/// <param name="o">
		/// @return </param>
		public static char[] Concat(params char[][] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return new char[] { };
			}
			int size = 0;
			foreach (char[] e in o)
			{
				size += e.Length;
			}
			char[] c = new char[size];
			int ci = 0;
			foreach (char[] e in o)
			{
				size = e.Length;
				System.Array.Copy(e, 0, c, ci, size);
				ci += size;
			}
			return c;
		}

		public static bool IsHex(CharSequence ch)
		{
			if (ch == null)
			{
				return false;
			}
			for (int i = 0; i < ch.Length(); i++)
			{
				int c = ch.CharAt(i);
				if (!IsHexDigit(c))
				{
					return false;
				}
			}
			return true;
		}

		/// <summary>
		/// 判定是否由纯粹的西方字符组成
		/// </summary>
		/// <param name="message">
		/// @return </param>
		public static bool IsEnglishAndNumeric(string cs)
		{
			if (IsEmpty(cs))
			{
				return false;
			}
			int size = cs.Length();
			int amount = 0;
			for (int j = 0; j < size; j++)
			{
				int letter = cs.CharAt(j);
				if (IsEnglishAndNumeric(letter) || (char)letter == LSystem.SPACE)
				{
					amount++;
				}
			}
			return amount >= size;
		}

		public static bool IsEnglishAndNumeric(CharSequence cs)
		{
			return IsEnglishAndNumeric(cs.ToString());
		}

			/// <summary>
			/// 过滤指定字符为空
			/// </summary>
			/// <param name="message"> </param>
			/// <param name="chars">
			/// @return </param>
			public static string Filter(string message, params char[] chars)
		{
			return Filter(message, chars, LSystem.EMPTY);
		}

		/// <summary>
		/// 过滤指定字符为新字符
		/// </summary>
		/// <param name="message"> </param>
		/// <param name="chars"> </param>
		/// <param name="newTag">
		/// @return </param>
		public static string Filter(string message, char[] chars, string newTag)
		{
			if (Size(message) <= 0)
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			bool addFlag;
			for (int i = 0; i < message.Length(); i++)
			{
				addFlag = true;
				char ch = message.CharAt(i);
				for (int j = 0; j < chars.Length; j++)
				{
					if (chars[j] == ch)
					{
						addFlag = false;
						sbr.Append(newTag);
						break;
					}
				}
				if (addFlag)
				{
					sbr.Append(ch);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 过滤字符序列中所有不显示的占位符
		/// </summary>
		/// <param name="s">
		/// @return </param>
		public static string SpaceFilter(string s)
		{
			if (Size(s) <= 0)
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			int size = s.Length();
			for (int i = 0; i < size; i++)
			{
				char c = s.CharAt(i);
				if (!IsSpace(c))
				{
					sbr.Append(c);
				}
			}
			return sbr.ToString();
		}

		/// <summary>
		/// 以指定字符过滤切割字符串，并返回分割后的字符串数组
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="flag">
		/// @return </param>
		public static string[] Split(string str, char? flag)
		{
			if (IsEmpty(str))
			{
				return new string[] { str };
			}
			int count = 0;
			int size = str.Length();
			for (int index = 0; index < size; index++)
			{
				if (flag == str.CharAt(index))
				{
					count++;
				}
			}
			if (str.CharAt(size - 1) != flag.Value)
			{
				count++;
			}
			if (count == 0)
			{
				return new string[] { str };
			}
			int idx = -1;
			string[] strings = new string[count];
			for (int i = 0, len = strings.Length; i < len; i++)
			{
				int indexStart = idx + 1;
				idx = str.IndexOf(flag.Value, idx + 1);
				if (idx == -1)
				{
					strings[i] = str.JavaSubstring(indexStart).Trim();
				}
				else
				{
					strings[i] = str.JavaSubstring(indexStart, idx).Trim();
				}
			}
			return strings;
		}

		/// <summary>
		/// 分解字符串(同时过滤多个符号)
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="flags">
		/// @return </param>
		public static string[] Split(string str, params char[] flags)
		{
			return Split(str, flags, false);
		}

		/// <summary>
		/// 分解字符串(同时过滤多个符号)
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="flags">
		/// @return </param>
		public static string[] Split(string str, char[] flags, bool newline)
		{
			if ((flags.Length == 0) || (str.Length == 0))
			{
				return new string[0];
			}
			char[] chars = str.ToCharArray();
			int maxparts = chars.Length + 1;
			int[] start = new int[maxparts];
			int[] end = new int[maxparts];
			int count = 0;
			start[0] = 0;
			int s = 0, e;
			if (CharUtils.EqualsOne(chars[0], flags))
			{
				end[0] = 0;
				count++;
				s = CharUtils.FindFirstDiff(chars, 1, flags);
				if (s == -1)
				{
					return new string[] { "", "" };
				}
				start[1] = s;
			}
			for (; ; )
			{
				e = CharUtils.FindFirstEqual(chars, s, flags);
				if (e == -1)
				{
					end[count] = chars.Length;
					break;
				}
				end[count] = e;
				count++;
				s = CharUtils.FindFirstDiff(chars, e, flags);
				if (s == -1)
				{
					start[count] = end[count] = chars.Length;
					break;
				}
				start[count] = s;
			}
			count++;
			string[] result = null;
			if (newline)
			{
				count *= 2;
				result = new string[count];
				for (int i = 0, j = 0; i < count; j++, i += 2)
				{
					result[i] = str.JavaSubstring(start[j], end[j]).Trim();
					result[i + 1] = LSystem.LS;
				}
			}
			else
			{
				result = new string[count];
				for (int i = 0; i < count; i++)
				{
					result[i] = str.JavaSubstring(start[i], end[i]).Trim();
				}
			}
			return result;
		}

		/// <summary>
		/// 以指定字符串来分解字符串
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="separator">
		/// @return </param>
		public static string[] Split(string str, string separator)
		{
			if (IsNullOrEmpty(str) || IsNullOrEmpty(separator))
			{
				return new string[] { };
			}
			int sepLength = separator.Length;
			if (sepLength == 0)
			{
				return new string[] { str };
			}
			if (separator.Length == 1)
			{
				return Split(str, separator[0]);
			}
			TArray<string> tokens = new TArray<string>();
			int length = str.Length;
			int start = 0;
			do
			{
				int p = str.IndexOf(separator, start, StringComparison.Ordinal);
				if (p == -1)
				{
					p = length;
				}
				if (p > start)
				{
					tokens.Add(str.JavaSubstring(start, p));
				}
				start = p + sepLength;
			} while (start < length);
			string[] result = new string[tokens.size];
			for (int i = 0; i < tokens.size; i++)
			{
				result[i] = tokens.Get(i);
			}
			return result;
		}

		/// <summary>
		/// 解析csv文件
		/// </summary>
		/// <param name="str">
		/// @return </param>
		public static string[] SplitCsv(string str)
		{
			TArray<string> stringList = new TArray<string>();
			string tempString;
			StrBuilder sbr = new StrBuilder();
			for (int i = 0; i < str.Length; i++)
			{
				if (str[i] == LSystem.DOUBLE_QUOTES)
				{
					i++;
					while (i < str.Length)
					{
						if (str[i] == LSystem.DOUBLE_QUOTES && str[i + 1] == LSystem.DOUBLE_QUOTES)
						{
							sbr.Append(LSystem.DOUBLE_QUOTES);
							i = i + 2;
						}
						if (str[i] == LSystem.DOUBLE_QUOTES)
						{
							break;
						}
						else
						{
							sbr.Append(str[i]);
							i++;
						}
					}
					i++;
				}

				if (str[i] != ',')
				{
					sbr.Append(str[i]);
				}
				else
				{
					tempString = sbr.ToString();
					stringList.Add(tempString);
					sbr.SetLength(0);
				}
			}

			tempString = sbr.ToString();
			stringList.Add(tempString);
			sbr.SetLength(0);
			string[] stockArr = new string[stringList.size];
			stockArr = stringList.ToArray(stockArr);
			return stockArr;
		}

		/// <summary>
		/// 以指定大小过滤字符串，并返回切割后的数组
		/// </summary>
		/// <param name="str"> </param>
		/// <param name="size">
		/// @return </param>
		/// <exception cref="LSysException"> </exception>
		public static string[] SplitSize(string str, int size)
		{
			if (IsEmpty(str))
			{
				return new string[] { str };
			}
			if (size <= 0)
			{
				throw new LSysException("The size parameter must be more than 0.");
			}
			int num = str.Length / size;
			int mod = str.Length % size;
			string[] ret = mod > 0 ? new string[num + 1] : new string[num];
			for (int i = 0; i < num; i++)
			{
				ret[i] = str.JavaSubstring(i * size, (i + 1) * size).Trim();
			}
			if (mod > 0)
			{
				ret[num] = str.JavaSubstring(num * size).Trim();
			}
			return ret;
		}

		/// <summary>
		/// 给指定序列加上引号
		/// </summary>
		/// <param name="cs">
		/// @return </param>
		public static string Quote(string cs)
		{
			if (IsNullOrEmpty(cs))
			{
				return "\"\"";
			}
			return "\"" + cs + "\"";
		}

		/// <summary>
		/// 给指定序列删去引号
		/// </summary>
		/// <param name="cs">
		/// @return </param>
		public static string Dequote(string cs)
		{
			if (IsNullOrEmpty(cs))
			{
				return LSystem.EMPTY;
			}
			string ch = cs.ToString();
			if (ch.Length < 2)
			{
				return ch;
			}
			else if (ch.ToString().StartsWith("\"", StringComparison.Ordinal) && ch.EndsWith("\"", StringComparison.Ordinal))
			{
				return ch.JavaSubstring(1, (ch.Length - 1));
			}
			else
			{
				return ch;
			}
		}

		public static bool IsHex(string ch)
		{
			if (ch == null)
			{
				return false;
			}
			for (int i = 0; i < ch.Length(); i++)
			{
				int c = ch.CharAt(i);
				if (!IsHexDigit(c))
				{
					return false;
				}
			}
			return true;
		}

		public static string[] Split(string str, char flag)
		{
			if (IsEmpty(str))
			{
				return new string[] { str };
			}
			int count = 0;
			int size = str.Length();
			for (int index = 0; index < size; index++)
			{
				if (flag == str.CharAt(index))
				{
					count++;
				}
			}
			if (str.CharAt(size - 1) != flag)
			{
				count++;
			}
			if (count == 0)
			{
				return new string[] { str };
			}
			int idx = -1;
			string[] strings = new string[count];
			for (int i = 0, len = strings.Length; i < len; i++)
			{
				int indexStart = idx + 1;
				idx = str.IndexOf(flag, idx + 1);
				if (idx == -1)
				{
					strings[i] = str.JavaSubstring(indexStart).Trim();
				}
				else
				{
					strings[i] = str.JavaSubstring(indexStart, idx).Trim();
				}
			}
			return strings;
		}
		public static string Replace(string message, string oldString, string newString)
		{
			if (message == null)
				return null;
			if (newString == null)
				return message;
			int i = 0;
			if ((i = message.IndexOf(oldString, i)) >= 0)
			{
				char[] string2 = message.ToCharArray();
				char[] newString2 = newString.ToCharArray();
				int oLength = oldString.Length();
				StrBuilder buf = new StrBuilder(string2.Length);
				buf.Append(string2, 0, i).Append(newString2);
				i += oLength;
				int j;
				for (j = i; (i = message.IndexOf(oldString, i)) > 0; j = i)
				{
					buf.Append(string2, j, i - j).Append(newString2);
					i += oLength;
				}
				buf.Append(string2, j, string2.Length - j);
				return buf.ToString();
			}
			else
			{
				return message;
			}
		}

		public static int CharCount(string str, char chr)
		{
			int count = 0;
			if (str != null)
			{
				int length = str.Length();
				for (int i = 0; i < length; i++)
				{
					if (str.CharAt(i) == chr)
					{
						count++;
					}
				}
				return count;
			}
			return count;
		}

		public static string Unescape(string escaped)
		{
			if (IsEmpty(escaped))
			{
				return LSystem.EMPTY;
			}
			int length = escaped.Length;
			int i = 0;
			StrBuilder sbr = new StrBuilder(escaped.Length / 2);

			while (i < length)
			{
				char n = escaped.CharAt(i++);
				if (n != '%')
				{
					sbr.Append(n);
				}
				else
				{
					n = escaped.CharAt(i++);
					int code;

					if (n == 'u')
					{
						string slice = escaped.JavaSubstring(i, i + 4);
						code = Integer.ValueOf(slice, 16);
						i += 4;
					}
					else
					{
						string slice = escaped.JavaSubstring(i - 1, ++i);
						code = Integer.ValueOf(slice, 16);
					}
					sbr.Append((char)code);
				}
			}

			return sbr.ToString();
		}

		public static string Escape(string raw)
		{
			if (IsEmpty(raw))
			{
				return LSystem.EMPTY;
			}
			int length = raw.Length;
			int i = 0;
			StrBuilder sbr = new StrBuilder(raw.Length / 2);

			while (i < length)
			{
				char c = raw.CharAt(i++);

				if (CharUtils.IsLetterOrDigit(c) || CharUtils.IsEscapeExempt(c))
				{
					sbr.Append(c);
				}
				else
				{
					int i1 = raw.CharAt(i - 1);
					string escape = CharUtils.ToHex(i1);

					sbr.Append('%');

					if (escape.Length > 2)
					{
						sbr.Append('u');
					}
					sbr.Append(escape.ToUpper());

				}
			}

			return sbr.ToString();
		}

		public static string PadFront( string chars, char padChar,  int len)
		{
			 int padCount = len - chars.Length;
			if (padCount <= 0)
			{
				return chars;
			}
			else
			{
			    StrBuilder sbr = new StrBuilder();

				for (int i = padCount - 1; i >= 0; i--)
				{
					sbr.Append(padChar);
				}
				sbr.Append(chars);

				return sbr.ToString();
			}
		}

	
		public static string PadBack( string chars, char padChar, int len)
		{
			int padCount = len - chars.Length;
			if (padCount <= 0)
			{
				return chars;
			}
			else
			{
				StrBuilder sbr = new StrBuilder(chars);
				for (int i = padCount - 1; i >= 0; i--)
				{
					sbr.Append(padChar);
				}
				return sbr.ToString();
			}
		}

		private static bool UnificationAllow(char ch)
		{
			return !IsSpace(ch);
		}

		public static string Merge(string[] messages)
		{
			if (IsEmpty(messages))
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			foreach (string mes in messages)
			{
				if (mes != null)
				{
					sbr.Append(mes.Trim());
				}
			}
			return sbr.ToString().Trim();
		}

		public static string Merge(CharSequence[] messages)
		{
			if (messages == null || messages.Length == 0)
			{
				return LSystem.EMPTY;
			}
			StrBuilder sbr = new StrBuilder();
			foreach (CharSequence mes in messages)
			{
				if (mes != null)
				{
					sbr.Append(mes);
				}
			}
			return sbr.ToString().Trim();
		}

		public static string UnificationStrings(string mes)
		{
			return UnificationStrings(mes, null);
		}

		public static string UnificationStrings(string mes, string limit)
		{
			return UnificationStrings(new CharArray(128), mes, limit);
		}

		public static string UnificationStrings(CharArray tempChars, string mes)
		{
			return UnificationStrings(tempChars, mes, null);
		}

		public static string UnificationStrings(CharArray tempChars, string mes, string limit)
		{
			if (IsEmpty(mes))
			{
				return LSystem.EMPTY;
			}
			tempChars.Clear();
			if (limit == null || limit.Length == 0)
			{
				for (int i = 0, size = mes.Length; i < size; i++)
				{
					char ch = mes.CharAt(i);
					if (UnificationAllow(ch) && !tempChars.Contains(ch))
					{
						tempChars.Add(ch);
					}
				}
			}
			else
			{
				bool running;
				for (int i = 0, size = mes.Length; i < size; i++)
				{
					running = true;
					char ch = mes.CharAt(i);
					for (int j = 0; j < limit.Length; j++)
					{
						if (limit.CharAt(j) == ch)
						{
							running = false;
							break;
						}
					}
					if (running && UnificationAllow(ch) && !tempChars.Contains(ch))
					{
						tempChars.Add(ch);
					}
				}
			}
			if (tempChars.length == 0)
			{
				return LSystem.EMPTY;
			}
			else
			{
				return tempChars.Sort().GetString().Trim();
			}
		}

		public static string UnificationCharSequence(params CharSequence[] messages)
		{
			return UnificationCharSequence(messages, null);
		}

		public static string UnificationCharSequence(CharArray tempChars, CharSequence[] messages)
		{
			return UnificationCharSequence(tempChars, messages, null);
		}

		public static string UnificationCharSequence(CharSequence[] messages, CharSequence limit)
		{
			return UnificationCharSequence(new CharArray(128), messages, limit);
		}

		public static string UnificationCharSequence(CharArray tempChars, CharSequence[] messages, CharSequence limit)
		{
			if (messages == null || messages.Length == 0)
			{
				return LSystem.EMPTY;
			}
			tempChars.Clear();
			bool mode = (limit == null || limit.Length() == 0);
			foreach (CharSequence mes in messages)
			{
				if (mes == null)
				{
					continue;
				}
				if (mode)
				{
					for (int i = 0, size = mes.Length(); i < size; i++)
					{
						char ch = mes.CharAt(i);
						if (UnificationAllow(ch) && !tempChars.Contains(ch))
						{
							tempChars.Add(ch);
						}
					}
				}
				else
				{
					bool running;
					for (int i = 0, size = mes.Length(); i < size; i++)
					{
						running = true;
						char ch = mes.CharAt(i);
						for (int j = 0; j < limit.Length(); j++)
						{
							if (limit.CharAt(j) == ch)
							{
								running = false;
								break;
							}
						}
						if (running && UnificationAllow(ch) && !tempChars.Contains(ch))
						{
							tempChars.Add(ch);
						}
					}
				}
			}
			if (tempChars.length == 0)
			{
				return LSystem.EMPTY;
			}
			else
			{
				return tempChars.Sort().GetString().Trim();
			}
		}

		public static string UnificationStrings(string[] messages)
		{
			return UnificationStrings(messages, null);
		}

		public static string UnificationStrings(CharArray tempChars, string[] messages)
		{
			return UnificationStrings(tempChars, messages, null);
		}

		public static string UnificationStrings(string[] messages, string limit)
		{
			return UnificationStrings(new CharArray(128), messages, limit);
		}

		public static string UnificationStrings(CharArray tempChars, string[] messages, string limit)
		{
			if (IsEmpty(messages))
			{
				return LSystem.EMPTY;
			}
			tempChars.Clear();
			bool mode = (limit == null || limit.Length == 0);
			foreach (string mes in messages)
			{
				if (mes == null)
				{
					continue;
				}
				if (mode)
				{
					for (int i = 0, size = mes.Length; i < size; i++)
					{
						char ch = mes.CharAt(i);
						if (UnificationAllow(ch) && !tempChars.Contains(ch))
						{
							tempChars.Add(ch);
						}
					}
				}
				else
				{
					bool running;
					for (int i = 0, size = mes.Length; i < size; i++)
					{
						running = true;
						char ch = mes.CharAt(i);
						for (int j = 0; j < limit.Length; j++)
						{
							if (limit.CharAt(j) == ch)
							{
								running = false;
								break;
							}
						}
						if (running && UnificationAllow(ch) && !tempChars.Contains(ch))
						{
							tempChars.Add(ch);
						}
					}
				}
			}
			if (tempChars.length == 0)
			{
				return LSystem.EMPTY;
			}
			else
			{
				return tempChars.Sort().GetString().Trim();
			}
		}

		public static string UnificationChars(char[] messages)
		{
			return UnificationChars(messages, null);
		}

		public static string UnificationChars(CharArray tempChars, char[] messages)
		{
			return UnificationChars(tempChars, messages, null);
		}

		public static string UnificationChars(char[] messages, string limit)
		{
			return UnificationChars(new CharArray(128), messages, null);
		}

	
		public static string UnificationChars(CharArray tempChars, char[] messages, string limit)
		{
			if (messages == null || messages.Length == 0)
			{
				return LSystem.EMPTY;
			}
			tempChars.Clear();
			bool mode = (limit == null || limit.Length == 0);
			if (mode)
			{
				for (int i = 0, size = messages.Length; i < size; i++)
				{
					char ch = messages[i];
					if (UnificationAllow(ch) && !tempChars.Contains(ch))
					{
						tempChars.Add(ch);
					}
				}
			}
			else
			{
				bool running;
				for (int i = 0, size = messages.Length; i < size; i++)
				{
					running = true;
					char ch = messages[i];
					for (int j = 0; j < limit.Length(); j++)
					{
						if (limit.CharAt(j) == ch)
						{
							running = false;
							break;
						}
					}
					if (running && UnificationAllow(ch) && !tempChars.Contains(ch))
					{
						tempChars.Add(ch);
					}
				}
			}

			if (tempChars.length == 0)
			{
				return LSystem.EMPTY;
			}
			else
			{
				return tempChars.Sort().GetString().Trim();
			}
		}


		public static string[] GetListToStrings(TArray<string> list)
		{
			if (list == null || list.size == 0)
			{
				return null;
			}
			string[] result = new string[list.size];
			for (int i = 0; i < result.Length; i++)
			{
				result[i] = list.Get(i);
			}
			return result;
		}

	
		public static TArray<string> GetStringsToList(params string[] str)
		{
			if (str == null || str.Length == 0)
			{
				return null;
			}
			int len = str.Length;
			TArray<string> list = new TArray<string>(len);
			for (int i = 0; i < len; i++)
			{
				list.Add(str[i]);
			}
			return list;
		}

	
		public static TArray<CharSequence> GetArrays(CharSequence[] chars)
		{
			if (chars == null)
			{
				return new TArray<CharSequence>(0);
			}
			int size = chars.Length;
			TArray<CharSequence> arrays = new TArray<CharSequence>();
			for (int i = 0; i < size; i++)
			{
				arrays.Add(chars[i]);
			}
			return arrays;
		}


		public static void GetChars(CharSequence c, int start, int end, char[] dest, int destoff)
		{
			if (c is JavaString) {
				((JavaString)c).GetChars(start, end, dest, destoff);
			} else if (c is StringBuffer) {
				((StringBuffer)c).GetChars(start, end, dest, destoff);
			} else if (c is StringBuilder) {
				((StrBuilder)c).GetChars(start, end, dest, destoff);
			} else if (c is StrBuilder) {
				((StrBuilder)c).GetChars(start, end, dest, destoff);
			} else
			{
				for (int i = start; i < end; i++)
				{
					dest[destoff++] = c.CharAt(i);
				}
			}
		}

		public static int IndexOf(CharSequence s, char ch)
		{
			return IndexOf(s, ch, 0);
		}


		public static int IndexOf(CharSequence c, char ch, int start)
		{
			if (c is JavaString s) {
				return s.ToString().IndexOf(ch, start);
			}
			return IndexOf(c, ch, start, c.Length());
		}


		public static int IndexOf(CharSequence c, char ch, int start, int end)
		{
			if ((c is StringBuffer) || (c is StringBuilder) || (c is StrBuilder)
				|| (c is JavaString)) {
				int INDEX_INCREMENT = 500;
				char[] temp = new char[INDEX_INCREMENT];

				while (start < end)
				{
					int segend = start + INDEX_INCREMENT;
					if (segend > end)
						segend = end;

					GetChars(c, start, segend, temp, 0);

					int count = segend - start;
					for (int i = 0; i < count; i++)
					{
						if (temp[i] == ch)
						{
							return i + start;
						}
					}

					start = segend;
				}
				return -1;
			}

			for (int i = start; i < end; i++)
			{
				if (c.CharAt(i) == ch)
				{
					return i;
				}
			}

			return -1;
		}

		public static int CountOccurrences(CharSequence chars, char? flag)
		{
			int count = 0;
			int lastOccurrence = IndexOf(chars, flag.Value, 0);
			while (lastOccurrence != -1)
			{
				count++;
				lastOccurrence = IndexOf(chars, flag.Value, lastOccurrence + 1);
			}
			return count;
		}

		public static bool IsEmpty(string v)
        {
            return v == null || v.Length == 0 || "".Equals(v.Trim());
        }
        public static bool IsEmpty(params CharSequence[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }
        public static bool IsEmpty(params string[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }

        public static bool IsEmpty(params char[] v)
        {
            return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
        }


		public static string Join(char flag,params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params float[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params int[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params long[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params bool[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag,params CharSequence[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static string Join(char flag, params string[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder sbr = new StrBuilder();
			int size = o.Length;
			for (int i = 0; i < size; i++)
			{
				sbr.Append(o[i]);
				if (i < size - 1)
				{
					sbr.Append(flag);
				}
			}
			return sbr.ToString();
		}

		public static bool IsNullOrEmpty(CharSequence v)
		{
			return v == null || v.Length() == 0;
		}

		public static bool IsNullOrEmpty(string v)
		{
			return v == null || v.Length() == 0;
		}

		public static int Size(string v)
		{
			return v == null ? -1 : v.Length();
		}

		public static int Length(string v)
		{
			if (IsNullOrEmpty(v))
			{
				return 0;
			}
			return v.Length();
		}

		public static char CharAt(string v, int i)
		{
			return Size(v) <= i ? (char)0 : v.CharAt(i);
		}
		public static bool Contains(CharSequence key,params CharSequence[] texts)
		{
			foreach (CharSequence text in texts)
			{
				if (key == null && text == null)
				{
					return true;
				}
				if (text == key || (text != null && string.ReferenceEquals(text?.ToString(),key?.ToString())))
				{
					return true;
				}
			}
			return false;
		}

		public static bool Contains(string key, params string[] texts)
		{
			foreach (string text in texts)
			{
				if (key == null && text == null)
				{
					return true;
				}
				if (text == key || (text != null && string.ReferenceEquals(text, key)))
				{
					return true;
				}
			}
			return false;
		}

		public static string FormatCRLF(string cs)
		{
			if (IsEmpty(cs))
			{
				return LSystem.EMPTY;
			}
			string src = cs.ToString();
			int pos = src.IndexOf("\r", StringComparison.Ordinal);
			if (pos != -1)
			{
				int len = src.Length;
				StrBuilder buffer = new StrBuilder();
				int lastPos = 0;
				while (pos != -1)
				{
					buffer.Append(src, lastPos, pos);
					if (pos == len - 1 || src[pos + 1] != '\n')
					{
						buffer.Append('\n');
					}
					lastPos = pos + 1;
					if (lastPos >= len)
					{
						break;
					}
					pos = src.IndexOf("\r", lastPos, StringComparison.Ordinal);
				}
				if (lastPos < len)
				{
					buffer.Append(src, lastPos, len);
				}
				src = buffer.ToString();
			}
			return src;
		}

		public static string FormatEscape(string cs, string indent)
		{
			
			string text = cs.ToString();
			if (text.IndexOf('\n') != -1)
			{
				if (text.Length == 1)
				{
					return Quote("\\n");
				}
				StrBuilder sbr = new StrBuilder();
				sbr.Append("|");
				string[] lines = Split(text, '\n');
				for (int i = 0; i < lines.Length; i++)
				{
					string line = lines[i];
					sbr.Append("\n" + indent + line);
				}
				if (text[text.Length - 1] == '\n')
				{
					sbr.Append("\n" + indent);
				}
				return sbr.ToString();
			}
			else if ("".Equals(text))
			{
				return Quote(text);
			}
			else
			{
				const string indicators = ":[]{},\"'|*&";
				bool quoteIt = false;
				foreach (char c in indicators.ToCharArray())
				{
					if (text.IndexOf(c) != -1)
					{
						quoteIt = true;
						break;
					}
				}
				if (text.Trim().Length != text.Length)
				{
					quoteIt = true;
				}
				if (MathUtils.IsNumber(text))
				{
					quoteIt = true;
				}
				if (quoteIt)
				{
					text = Escape(text);
					text = Quote(text);
				}
				return text;
			}
		}

		public static string ToString(object o)
		{
			return ToString(o, null);
		}

		public static string ToString(object o, string def)
		{
			return o == null ? def : o.ToString();
		}

		public static string GetRandString()
		{
			return GetRandString(32);
		}

		public static string GetRandString(int size)
		{
			StrBuilder str = new StrBuilder(size);
			char ch;
			for (int i = 0; i < size; i++)
			{
				ch = Convert.ToChar(Convert.ToInt32(MathUtils.Floor(MathUtils.Random() * 26 + 65)));
				str.Append(ch);
			}
			return str.ToString();
		}
	}
}
