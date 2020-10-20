using System;
using System.Text;

namespace java.lang
{
    public class JavaString : CharSequence
    {
        private readonly string value;

        public JavaString(params char[] v)
        {
            this.value = new string(v);
        }

        public JavaString(string v)
        {
            this.value = v;
        }
        public JavaString(params byte[] v)
        {
            this.value = JavaSystem.GetEncoding().GetString(v);
        }

        public char CharAt(int index)
        {
            return StringExtensions.CharAt(this.value, index);
        }

        public int Length()
        {
            return value.Length();
        }

        public CharSequence SubSequence(int start, int end)
        {
            return new JavaString(StringExtensions.Substring(this.value, start, end));
        }

        public int IndexOf(string str)
        {
            return StringExtensions.IndexOf(this.value, str);
        }
        public int IndexOf(string str, int startIndex)
        {
            return StringExtensions.IndexOf(this.value, str, startIndex);
        }
        public int IndexOf(int codePoint)
        {
            return StringExtensions.IndexOf(this.value, codePoint);
        }

        public int IndexOf(int codePoint, int startIndex)
        {
            return StringExtensions.IndexOf(this.value, codePoint, startIndex);
        }
        public int LastIndexOf(int codePoint)
        {
            return StringExtensions.LastIndexOf(this.value, codePoint);
        }

        public int LastIndexOf(int codePoint, int startIndex)
        {
            return StringExtensions.LastIndexOf(this.value, codePoint, startIndex);
        }

        public int LastIndexOf(string str)
        {
            return StringExtensions.LastIndexOf(this.value, str);
        }
        public string[] Split(string regex)
        {
            return StringExtensions.Split(this.value, regex);
        }
        public string[] Split(string regex, int maxMatch)
        {
            return StringExtensions.Split(this.value, regex, maxMatch);
        }
        public bool StartsWith(string prefix)
        {
            return StringExtensions.StartsWith(this.value, prefix);
        }
        public bool StartsWith(string prefix, int toffset)
        {
            return toffset >= 0 && StringExtensions.Substring(this.value, toffset, prefix.Length).Equals(prefix);
        }
        public bool EndsWith(string prefix)
        {
            return StringExtensions.EndsWith(this.value, prefix);
        }
        public string Replace(char oldchar, char newchar)
        {
            return StringExtensions.Replace(this.value, oldchar, newchar);
        }
        public string Replace(object oldchar, object newchar)
        {
            return StringExtensions.Replace(this.value, oldchar, newchar);
        }
        public string Substring(int beginIndex)
        {
            return StringExtensions.Substring(this.value, beginIndex);
        }

        public string Substring(int beginIndex, int endIndex)
        {
            return StringExtensions.Substring(this.value, beginIndex, endIndex);
        }

        public bool IsEmpty()
        {
            return this.value == null || this.value.Length == 0;
        }
    }
}
