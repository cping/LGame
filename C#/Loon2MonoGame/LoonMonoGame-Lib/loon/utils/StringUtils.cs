using java.lang;
using System;

namespace loon.utils
{
    public sealed class StringUtils : CharUtils
    {
		public static string Format(string format,params object[] o)
		{
			if (CollectionUtils.IsEmpty(o))
			{
				return "";
			}
			StrBuilder b = new StrBuilder();
			int p = 0;
			for (; ; )
			{
				int i = format.IndexOf('{', p);
				if (i == -1)
				{
					break;
				}
				int idx = format.IndexOf('}', i + 1);
				if (idx == -1)
				{
					break;
				}
				if (p != i)
				{
					b.Append(format.Substring(p, i));
				}
				string nstr = format.Substring(i + 1, idx);
				try
				{
					int n = Integer.ParseInt(nstr);
					if (n >= 0 && n < o.Length)
					{
						b.Append(o[n]);
					}
					else
					{
						b.Append('{').Append(nstr).Append('}');
					}
				}
				catch (java.lang.Exception)
				{
					b.Append('{').Append(nstr).Append('}');
				}
				p = idx + 1;
			}
			b.Append(format.Substring(p));
			return b.ToString();
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
				int IndexStart = idx + 1;
				idx = str.IndexOf(flag, idx + 1);
				if (idx == -1)
				{
					strings[i] = str.Substring(IndexStart).Trim();
				}
				else
				{
					strings[i] = str.Substring(IndexStart, idx).Trim();
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

		public static bool IsNullOrEmpty(string v)
		{
			return v == null || v.Length() == 0;
		}

		public static string Quote(string cs)
		{
			if (IsNullOrEmpty(cs))
			{
				return "\"\"";
			}
			return "\"" + cs + "\"";
		}
		public static string Escape(string raw)
		{
			if (IsEmpty(raw))
			{
				return "";
			}
			int length = raw.Length();
			int i = 0;
			StrBuilder sbr = new StrBuilder(raw.Length() / 2);

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
