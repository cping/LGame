using java.lang;
using loon.utils;

namespace java.lang
{
    public static class StringExtensions
    {
        public static byte[] GetBytes(this string str)
        {
            return new JavaString(str).GetBytes();
        }
        public static byte[] GetBytes(this string str,string encoding)
        {
            return new JavaString(str).GetBytes(encoding);
        }

        public static char CharAt(this string str, int index)
        {
            return str[index];
        }

        public static int CompareTo(this string str, string other)
        {
            int l1 = str.Length;
            int l2 = other.Length;
            for (int i = 0; i < l1 && i < l2; i++)
            {
                int c1 = str[i];
                int c2 = other[i];
                if (c1 != c2) { return c1 - c2; }
            }
            return l1 - l2;
        }

        public static string Concat(this string str, string other)
        {
            if (str == null || other == null) { throw new NullPointerException(); }
            return string.Concat(str, other);
        }

        public static bool Contains(this string @this, object other)
        {
            return @this.IndexOf(other.ToString()) >= 0;
        }

        public static bool EndsWith(this string str, string other)
        {
            return str.EndsWith(other);
        }

        public static int IndexOf(this string str, string other)
        {
            return str.IndexOf(other);
        }

        public static int IndexOf(this string str, int c)
        {
            return str.IndexOf((char)c);
        }

        public static int IndexOf(this string str, string other, int from)
        {
            return str.IndexOf(other, from);
        }

        public static int IndexOf(this string str, int c, int from)
        {
            return str.IndexOf((char)c, from);
        }

        public static bool IsEmpty(this string str)
        {
            return str.Length <= 0;
        }

        public static string Join(object delim, object[] parts)
        {
            string d = JavaSystem.Str(delim);
            System.Text.StringBuilder b = new System.Text.StringBuilder();
            for (int i = 0; i < parts.Length; i++)
            {
                if (i > 0) { b.Append(d); }
                b.Append(JavaSystem.Str(parts[i]));
            }
            return b.ToString();
        }

        public static string ValueOf(bool b)
        {
            return JavaSystem.Str(b);
        }

        public static string ValueOf(char c)
        {
            return JavaSystem.Str(c);
        }

        public static string ValueOf(char[] a)
        {
            if (a == null) { throw new NullPointerException(); }
            return new string(a);
        }

        public static string ValueOf(char[] a, int offset, int count)
        {
            if (a == null) { throw new NullPointerException(); }
            if (offset < 0 || count < 0 || offset + count > a.Length)
            {
                throw new IndexOutOfBoundsException();
            }
            return new string(a, offset, count);
        }

        public static string ValueOf(double d)
        {
            return JavaSystem.Str(d);
        }

        public static string ValueOf(int i)
        {
            return JavaSystem.Str(i);
        }

        public static string ValueOf(object o)
        {
            return JavaSystem.Str(o);
        }

        public static int LastIndexOf(this string str, string other)
        {
            return str.LastIndexOf(other);
        }

        public static int LastIndexOf(this string str, int c)
        {
            return str.LastIndexOf((char)c);
        }

        public static int LastIndexOf(this string str, string other, int from)
        {
            return str.LastIndexOf(other, from);
        }

        public static int LastIndexOf(this string str, int c, int from)
        {
            return str.LastIndexOf((char)c, from);
        }

        public static int Length(this string str)
        {
            return str.Length;
        }

        public static string Replace(this string str, char oldchar, char newchar)
        {
            return str.Replace(oldchar, newchar);
        }

        public static string Replace(this string str, object oldstr, object newstr)
        {
            return str.Replace(oldstr.ToString(), newstr.ToString());
        }

        public static bool StartsWith(this string str, string other)
        {
            return str.StartsWith(other);
        }

        public static string[] Split(this string str, string delim, int limit = 0)
        {
            if (str.Length < 1)
            {
                return new string[] { "" };
            }

            if (delim == null || delim.Length < 1)
            {
                string[] l;
                if (limit < 0)
                {
                    l = new string[str.Length + 1];
                }
                else if (limit == 0)
                {
                    l = new string[str.Length];
                }
                else
                {
                    l = new string[Math.Min(str.Length + 1, limit)];
                }
                for (int i = 0; i < l.Length; i++)
                {
                    l[i] = i >= str.Length ? "" : (i < l.Length - 1 ? str.Substring(i, 1) : str.Substring(i));
                }
                return l;
            }

            // normal operation
            if (limit > 0)
            {
                return str.Split(new string[] { delim }, limit, System.StringSplitOptions.None);
            }
            else
            {
                string[] l = str.Split(new string[] { delim }, System.StringSplitOptions.None);
                if (limit == 0)
                {
                    int len = l.Length;
                    while (len > 1 && l[len - 1].Length < 1) len--;
                    if (len < l.Length) System.Array.Resize(ref l, len);
                }
                return l;
            }
        }

        public static string Substring(this string str, int beginIndex)
        {
            return str.Substring(beginIndex);
        }

        public static string Substring(this string str, int beginIndex, int endIndex)
        {
            return str.Substring(beginIndex, endIndex - beginIndex);
        }

        public static char[] ToCharArray(this string str)
        {
            return str.ToCharArray();
        }

        public static string Trim(this string str)
        {
            return str.Trim();
        }
        public static void GetChars(this string str, int srcBegin, int srcEnd, char[] dst, int dstBegin)
        {
            if (srcBegin < 0)
            {
                throw new StringIndexOutOfBoundsException(srcBegin);
            }
            if (srcEnd > str.Length)
            {
                throw new StringIndexOutOfBoundsException(srcEnd);
            }
            if (srcBegin > srcEnd)
            {
                throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
            }
            while (srcBegin < srcEnd)
            {
                dst[dstBegin++] = CharAt(str,srcBegin++);
            }
        }

        public static JavaString ToJavaString(this string str)
        {
            return new JavaString(str);
        }
    }
}
