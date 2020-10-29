using java.lang;
using System;

namespace loon.utils
{
    public sealed class StringUtils : CharUtils
    {
		public static byte[] GetSBytesToBytes(sbyte[] bytes)
        {
			return ToBytes(bytes);
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
    }
}
