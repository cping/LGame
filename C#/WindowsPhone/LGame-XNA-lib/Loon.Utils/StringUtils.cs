using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using Loon.Java;

namespace Loon.Utils
{
    /// <summary>
    /// 因为C#与Java差异性的存在，具体函数对比Android版有所出入。
    /// </summary>
    public class StringUtils
    {

        private StringUtils()
        {
        }

        public static Comparison<string> CASE_INSENSITIVE_ORDER;

        /// <summary>
        /// 判定是否由纯粹的西方字符组成
        /// </summary>
        ///
        /// <param name="string"></param>
        /// <returns></returns>
        public static bool IsEnglishAndNumeric(string mes)
        {
            if (mes == null || mes.Length == 0)
            {
                return false;
            }
            char[] chars = mes.ToCharArray();
            int size = chars.Length;
            for (int j = 0; j < size; j++)
            {
                char letter = chars[j];
                if ((97 > letter || letter > 122) && (65 > letter || letter > 90)
                        && (48 > letter || letter > 57))
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// 判定是否为半角符号
        /// </summary>
        ///
        /// <param name="c"></param>
        /// <returns></returns>
        public static bool IsSingle(char c)
        {
            return (':' == c || '：' == c)
                    || (',' == c || '，' == c)
                    || ('"' == c || '“' == c)
                    || ((0x0020 <= c)
                            && (c <= 0x007E)
                            && !((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))
                            && !('0' <= c) && (c <= '9'));

        }

        /// <summary>
        /// 分解字符串
        /// </summary>
        ///
        /// <param name="string"></param>
        /// <param name="tag"></param>
        /// <returns></returns>
        public static string[] Split(string s, string tag)
        {
            StringTokenizer str = new StringTokenizer(s, tag);
            string[] result = new string[str.CountTokens()];
            int index = 0;
            for (; str.HasMoreTokens(); )
            {
                result[index++] = str.NextToken();
            }
            return result;
        }

        /// <summary>
        /// 过滤指定字符串
        /// </summary>
        ///
        /// <param name="string"></param>
        /// <param name="oldString"></param>
        /// <param name="newString"></param>
        /// <returns></returns>
        public static string Replace(string s, string oldString,
                string newString)
        {
            if (s == null)
                return null;
            if (newString == null)
                return s;
            int i = 0;
            if ((i = IndexOf(s, oldString, i)) >= 0)
            {
                char[] string2 = s.ToCharArray();
                char[] newString2 = newString.ToCharArray();
                int oLength = oldString.Length;
                StringBuilder buf = new StringBuilder(string2.Length);
                buf.Append(string2, 0, i).Append(newString2);
                i += oLength;
                int j;
                for (j = i; (i = IndexOf(s, oldString, i)) > 0; j = i)
                {
                    buf.Append(string2, j, i - j).Append(newString2);
                    i += oLength;
                }

                buf.Append(string2, j, string2.Length - j);
                return buf.ToString();
            }
            else
            {
                return s;
            }
        }


        /// <summary>
        /// 不匹配大小写的过滤指定字符串
        /// </summary>
        ///
        /// <param name="line"></param>
        /// <param name="oldString"></param>
        /// <param name="newString"></param>
        /// <returns></returns>
        public static string ReplaceIgnoreCase(string line, string oldString,
                string newString)
        {
            if (line == null)
            {
                return null;
            }
            string lcLine = line.ToLower();
            string lcOldString = oldString.ToLower();
            int i = 0;
            if ((i = IndexOf(lcLine, lcOldString, i)) >= 0)
            {
                char[] line2 = line.ToCharArray();
                char[] newString2 = newString.ToCharArray();
                int oLength = oldString.Length;
                StringBuilder buf = new StringBuilder(line2.Length);
                buf.Append(line2, 0, i).Append(newString2);
                i += oLength;
                int j;
                for (j = i; (i = IndexOf(lcLine, lcOldString, i)) > 0; j = i)
                {
                    buf.Append(line2, j, i - j).Append(newString2);
                    i += oLength;
                }

                buf.Append(line2, j, line2.Length - j);
                return buf.ToString();
            }
            else
            {
                return line;
            }
        }

        /// <summary>
        /// 不匹配大小写的过滤指定字符串
        /// </summary>
        ///
        /// <param name="line"></param>
        /// <param name="oldString"></param>
        /// <param name="newString"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public static string ReplaceIgnoreCase(string line, string oldString,
                string newString, int[] count)
        {
            if (line == null)
            {
                return null;
            }
            string lcLine = line.ToLower();
            string lcOldString = oldString.ToLower();
            int i = 0;
            if ((i = IndexOf(lcLine, lcOldString, i)) >= 0)
            {
                int counter = 1;
                char[] line2 = line.ToCharArray();
                char[] newString2 = newString.ToCharArray();
                int oLength = oldString.Length;
                StringBuilder buf = new StringBuilder(line2.Length);
                buf.Append(line2, 0, i).Append(newString2);
                i += oLength;
                int j;
                for (j = i; (i = IndexOf(lcLine, lcOldString, i)) > 0; j = i)
                {
                    counter++;
                    buf.Append(line2, j, i - j).Append(newString2);
                    i += oLength;
                }
                buf.Append(line2, j, line2.Length - j);
                count[0] = counter;
                return buf.ToString();
            }
            else
            {
                return line;
            }
        }

        /// <summary>
        /// 以指定条件过滤字符串
        /// </summary>
        ///
        /// <param name="line"></param>
        /// <param name="oldString"></param>
        /// <param name="newString"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public static string Replace(string line, string oldString,
                string newString, int[] count)
        {
            if (line == null)
                return null;
            int i = 0;
            if ((i = IndexOf(line, oldString, i)) >= 0)
            {
                int counter = 1;
                char[] line2 = line.ToCharArray();
                char[] newString2 = newString.ToCharArray();
                int oLength = oldString.Length;
                StringBuilder buf = new StringBuilder(line2.Length);
                buf.Append(line2, 0, i).Append(newString2);
                i += oLength;
                int j;
                for (j = i; (i = IndexOf(line, oldString, i)) > 0; j = i)
                {
                    counter++;
                    buf.Append(line2, j, i - j).Append(newString2);
                    i += oLength;
                }
                buf.Append(line2, j, line2.Length - j);
                count[0] = counter;
                return buf.ToString();
            }
            else
            {
                return line;
            }
        }

        /// <summary>
        /// 过滤\n标记
        /// </summary>
        ///
        /// <param name="text"></param>
        /// <returns></returns>
        public static string[] ParseString(string text)
        {
            int token, index, index2;
            token = index = index2 = 0;
            while ((index = text.IndexOf('\n', index)) != -1)
            {
                token++;
                index++;
            }
            token++;
            index = 0;

            string[] document = new string[token];
            for (int i = 0; i < token; i++)
            {
                index2 = text.IndexOf('\n', index);
                if (index2 == -1)
                {
                    index2 = text.Length;
                }
                document[i] = text.Substring(index, (index2) - (index));
                index = index2 + 1;
            }

            return document;
        }

        /// <summary>
        /// 检查一组字符串是否完全由中文组成
        /// </summary>
        ///
        /// <param name="str"></param>
        /// <returns></returns>
        public static bool IsChinaLanguage(string str)
        {
            char[] chars = str.ToCharArray();
            int[] ints = new int[2];
            bool isChinese = false;
            int length = chars.Length;
            byte[] bytes = null;
            for (int i = 0; i < length; i++)
            {
                bytes = GetBytes(("" + chars[i]));
                if (bytes.Length == 2)
                {
                    ints[0] = bytes[0] & 0xff;
                    ints[1] = bytes[1] & 0xff;
                    if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
                            && ints[1] <= 0xFE)
                    {
                        isChinese = true;
                    }
                }
                else
                {
                    return false;
                }
            }
            return isChinese;
        }

        public static bool IsChinese(char c)
        {
            return c >= 0x4e00 && c <= 0x9fa5;
        }

        /// <summary>
        /// 判断是否为null
        /// </summary>
        ///
        /// <param name="param"></param>
        /// <returns></returns>
        public static bool IsEmpty(string param)
        {
            return param == null || param.Length == 0 || param.Trim().Equals("");
        }

        /// <summary>
        /// 显示指定编码下的字符长度
        /// </summary>
        ///
        /// <param name="encoding"></param>
        /// <param name="str"></param>
        /// <returns></returns>
        public static int GetBytesLengthOfEncoding(string encoding, string str)
        {
            if (str == null || str.Length == 0)
                return 0;
            try
            {
                byte[] bytes = GetBytes(str, encoding);
                int length = bytes.Length;
                return length;
            }
            catch (Exception exception)
            {
                System.Console.Error.WriteLine(exception.Message);
            }
            return 0;
        }

        /// <summary>
        /// 转化指定字符串为指定编码格式
        /// </summary>
        ///
        /// <param name="context"></param>
        /// <param name="encoding"></param>
        /// <returns></returns>
        public static string GetSpecialString(string context, string e)
        {
            System.Text.Encoding encoding = System.Text.Encoding.GetEncoding(e);
            byte[] message = GetBytes(context);
            return encoding.GetString(message, 0, message.Length);
        }

        /// <summary>
        /// 检查指定字符串中是否存在中文字符。
        /// </summary>
        ///
        /// <param name="checkStr">指定需要检查的字符串。</param>
        /// <returns>逻辑值（True Or False）。</returns>
        public static bool HasChinese(string checkStr)
        {
            bool checkedStatus = false;
            bool isError = false;
            string spStr = " _-";
            int checkStrLength = checkStr.Length - 1;
            for (int i = 0; i <= checkStrLength; i++)
            {
                char ch = checkStr[i];
                if (ch < 126)
                {
                    ch = Char.ToUpper(ch);
                    if (((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9'))
                            && (spStr.IndexOf(ch) < 0))
                    {
                        isError = true;
                    }
                }
            }
            checkedStatus = !isError;
            return checkedStatus;
        }

        /// <summary>
        /// 检查是否为纯字母
        /// </summary>
        ///
        /// <param name="value"></param>
        /// <returns></returns>
        public static bool IsAlphabet(string value_ren)
        {
            if (value_ren == null || value_ren.Length == 0)
                return false;
            for (int i = 0; i < value_ren.Length; i++)
            {
                char c = Char.ToUpper(value_ren[i]);
                if ('A' <= c && c <= 'Z')
                    return true;
            }
            return false;
        }

        /// <summary>
        /// 检查是否为字母与数字混合
        /// </summary>
        ///
        /// <param name="value"></param>
        /// <returns></returns>
        public static bool IsAlphabetNumeric(string value_ren)
        {
            if (value_ren == null || value_ren.Trim().Length == 0)
                return true;
            for (int i = 0; i < value_ren.Length; i++)
            {
                char letter = value_ren[i];
                if (('a' > letter || letter > 'z')
                        && ('A' > letter || letter > 'Z')
                        && ('0' > letter || letter > '9'))
                    return false;
            }
            return true;
        }

        /// <summary>
        /// 过滤首字符
        /// </summary>
        ///
        /// <param name="str"></param>
        /// <param name="pattern"></param>
        /// <param name="replace"></param>
        /// <returns></returns>
        public static string ReplaceFirst(string str, string pattern,
                string replace)
        {
            int s = 0;
            int e = 0;
            StringBuilder result = new StringBuilder();

            if ((e = IndexOf(str, pattern, s)) >= 0)
            {
                result.Append(str.Substring(s, (e) - (s)));
                result.Append(replace);
                s = e + pattern.Length;
            }
            result.Append(str.Substring(s));
            return result.ToString();
        }

        /// <summary>
        /// 替换指定字符串
        /// </summary>
        ///
        /// <param name="line"></param>
        /// <param name="oldString"></param>
        /// <param name="newString"></param>
        /// <returns></returns>
        public static string ReplaceMatch(string line, string oldString,
                string newString)
        {
            int i = 0;
            int j = 0;
            if ((i = IndexOf(line, oldString, i)) >= 0)
            {
                char[] line2 = line.ToCharArray();
                char[] newString2 = newString.ToCharArray();
                int oLength = oldString.Length;
                StringBuilder buffer = new StringBuilder(line2.Length);
                buffer.Append(line2, 0, i).Append(newString2);
                i += oLength;
                for (j = i; (i = IndexOf(line, oldString, i)) > 0; j = i)
                {
                    buffer.Append(line2, j, i - j).Append(newString2);
                    i += oLength;
                }
                buffer.Append(line2, j, line2.Length - j);
                return buffer.ToString();
            }
            else
            {
                return line;
            }
        }

        /// <summary>
        /// 以" "充满指定字符串
        /// </summary>
        ///
        /// <param name="str"></param>
        /// <param name="length"></param>
        /// <returns></returns>
        public static string FillSpace(string str, int length)
        {
            int strLength = str.Length;
            if (strLength >= length)
            {
                return str;
            }
            StringBuilder spaceBuffer = new StringBuilder();
            for (int i = 0; i < (length - strLength); i++)
            {
                spaceBuffer.Append(" ");
            }
            return str + spaceBuffer.ToString();
        }

        /// <summary>
        /// 得到定字节长的字符串，位数不足右补空格
        /// </summary>
        ///
        /// <param name="str"></param>
        /// <param name="length"></param>
        /// <returns></returns>
        public static string FillSpaceByByte(string str, int length)
        {
            byte[] strbyte = GetBytes(str);
            int strLength = strbyte.Length;
            if (strLength >= length)
            {
                return str;
            }
            StringBuilder spaceBuffer = new StringBuilder();
            for (int i = 0; i < (length - strLength); i++)
            {
                spaceBuffer.Append(" ");
            }
            return string.Concat(str, spaceBuffer.ToString());
        }

        /// <summary>
        /// 返回指定字符串长度
        /// </summary>
        ///
        /// <param name="s"></param>
        /// <returns></returns>
        public static int Length(string s)
        {
            if (s == null)
            {
                return 0;
            }
            else
            {
                return GetBytes(s).Length;
            }
        }

        /// <summary>
        /// 获得特定字符总数
        /// </summary>
        ///
        /// <param name="str"></param>
        /// <param name="chr"></param>
        /// <returns></returns>
        public static int CharCount(string str, char chr)
        {
            int count = 0;
            if (str != null)
            {
                int length = str.Length;
                for (int i = 0; i < length; i++)
                {
                    if (str[i] == chr)
                    {
                        count++;
                    }
                }
                return count;
            }
            return count;
        }

        public static byte[] GetAsciiBytes(string data)
        {
            if (data == null)
            {
                throw new ArgumentException("Parameter may not be null");
            }
            try
            {
                return GetBytes(data);
            }
            catch
            {
                throw new Exception("LGame requires ASCII support");
            }
        }

        /// <summary>
        /// 大写字符串首字母
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public static string Capitalize(string name)
        {
            if (name.Length == 0)
            {
                return name;
            }
            string str = name.Substring(0, 1).ToUpper();
            string str2 = name.Substring(1);
            return (str + str2);
        }

        private static int CaseInsensitiveComparator(string s1, string s2)
        {
            int length = s1.Length;
            int num2 = s2.Length;
            int num3 = 0;
            for (int i = 0; (num3 < length) && (i < num2); i++)
            {
                char c = s1[num3];
                char ch2 = s2[i];
                if (c != ch2)
                {
                    c = char.ToUpper(c);
                    ch2 = char.ToUpper(ch2);
                    if (c != ch2)
                    {
                        c = char.ToLower(c);
                        ch2 = char.ToLower(ch2);
                        if (c != ch2)
                        {
                            return (c - ch2);
                        }
                    }
                }
                num3++;
            }
            return (length - num2);
        }

        public static byte[] GetBytes(string stingMessage)
        {
            return GetBytes(stingMessage, System.Text.Encoding.UTF8);
        }

        public static byte[] GetBytes(string stingMessage, System.Text.Encoding e)
        {
            return e.GetBytes(stingMessage);
        }

        public static byte[] GetBytes(string stingMessage, string name)
        {
            System.Text.Encoding e = System.Text.Encoding.GetEncoding(name);
            return GetBytes(stingMessage, e);
        }

        public static string GetString(byte[] buffer)
        {
            return GetString(buffer, System.Text.Encoding.UTF8);
        }

        public static string GetString(byte[] buffer, System.Text.Encoding e)
        {
            return e.GetString(buffer, 0, buffer.Length);
        }

        public static string GetString(byte[] buffer, string name)
        {
            System.Text.Encoding e = System.Text.Encoding.GetEncoding(name);
            return GetString(buffer, e);
        }

        public static string GetAsciiString(byte[] data, int offset, int length)
        {
            if (data == null)
            {
                throw new ArgumentException("Parameter may not be null");
            }
            try
            {
                return NewString(data);
            }
            catch (Exception)
            {
            }
            throw new RuntimeException("LGame requires ASCII support");
        }

        public static string GetAsciiString(byte[] data)
        {
            return GetAsciiString(data, 0, data.Length);
        }

        public static int IndexOf(string s1, string s2, int idx)
        {
            if (idx != 0)
            {
                if (idx == -1)
                {
                    idx = 0;
                }
                else if (idx >= s1.Length)
                {
                    idx = s1.Length - 1;
                }
            }
            return s1.IndexOf(s2, idx);
        }

        public static string GetFormat(string format)
        {
            string str = format;
            int length = -1;
            int num2 = 0;
            while ((length = str.IndexOf("%s")) >= 0)
            {
                str = string.Concat(new object[] { str.Substring(0, length), "{", num2++, "}", str.Substring(length + 2) });
            }
            return str;
        }

        public static bool Matches(string input, string regex)
        {
            Regex regex2 = new Regex(regex);
            return regex2.Match(input).Success;
        }

        public static string NewString(byte[] p)
        {
            string str = "";
            for (int i = 0; i < p.Length; i++)
            {
                str = str + ((char)p[i]);
            }
            return str;
        }

        public static string NewString(char[] p)
        {
            return new string(p);
        }

        public static string NewString(byte[] p, string p_2)
        {
            string str = "";
            for (int i = 0; i < p.Length; i++)
            {
                str = str + ((char)p[i]);
            }
            return str;
        }

        public static bool RegionMatches(string s, int thisStart, string str, int start, int length)
        {
            if (str == null)
            {
                throw new NullReferenceException();
            }
            if ((start < 0) || ((str.Length - start) < length))
            {
                return false;
            }
            if ((thisStart < 0) || ((s.Length - thisStart) < length))
            {
                return false;
            }
            if (length > 0)
            {
                int num = thisStart;
                int num2 = start;
                for (int i = 0; i < length; i++)
                {
                    if (s[num + i] != str[num2 + i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool RegionMatches(string thisString, bool ignoreCase, int thisStart, string str, int start, int length)
        {
            if (!ignoreCase)
            {
                return RegionMatches(thisString, thisStart, str, start, length);
            }
            if (str == null)
            {
                throw new NullReferenceException();
            }
            if ((thisStart < 0) || (length > (thisString.Length - thisStart)))
            {
                return false;
            }
            if ((start < 0) || (length > (str.Length - start)))
            {
                return false;
            }

            int num = thisStart + length;
            char[] chArray = str.ToCharArray();
            while (thisStart < num)
            {
                char ch;
                char ch2;
                if ((((ch = thisString.ToCharArray()[thisStart++]) != (ch2 = chArray[start++])) && (char.ToUpper(ch) != char.ToUpper(ch2))) && (char.ToLower(ch) != char.ToLower(ch2)))
                {
                    return false;
                }
            }
            return true;
        }

        public static bool StartsWith(string value, string prefix, int toffset)
        {
            return RegionMatches(value, toffset, prefix, 0, prefix.Length);
        }

        public static string Substring(string str, int start, int end)
        {
            return JavaRuntime.Substring(str, start, end);
        }

    }
}
