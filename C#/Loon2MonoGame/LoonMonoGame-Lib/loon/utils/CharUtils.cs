using java.lang;

namespace loon.utils
{
    class CharUtils
    {

        public readonly static char MIN_HIGH_SURROGATE = '\uD800';

        public readonly static char MAX_HIGH_SURROGATE = '\uDBFF';

        public readonly static char MIN_LOW_SURROGATE = '\uDC00';

        public readonly static char MAX_LOW_SURROGATE = '\uDFFF';

        public readonly static char MIN_SURROGATE = MIN_HIGH_SURROGATE;

        public readonly static char MAX_SURROGATE = MAX_LOW_SURROGATE;

        static class HexChars
        {

            public readonly static char[] TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        }


        public static char ToChar(byte b)
        {
            return (char)(b & 0xFF);
        }

        public static long GetBytesToLong(byte[] bytes)
        {
            return GetBytesToLong(bytes, 0, bytes.Length);
        }

        public static long GetBytesToLong(byte[] x, int offset, int n)
        {
            switch (n)
            {
                case 1:
                    return x[offset] & 0xFFL;
                case 2:
                    return (x[offset + 1] & 0xFFL) | ((x[offset] & 0xFFL) << 8);
                case 3:
                    return (x[offset + 2] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8) | ((x[offset] & 0xFFL) << 16);
                case 4:
                    return (x[offset + 3] & 0xFFL) | ((x[offset + 2] & 0xFFL) << 8) | ((x[offset + 1] & 0xFFL) << 16)
                            | ((x[offset] & 0xFFL) << 24);
                case 5:
                    return (x[offset + 4] & 0xFFL) | ((x[offset + 3] & 0xFFL) << 8) | ((x[offset + 2] & 0xFFL) << 16)
                            | ((x[offset + 1] & 0xFFL) << 24) | ((x[offset] & 0xFFL) << 32);
                case 6:
                    return (x[offset + 5] & 0xFFL) | ((x[offset + 4] & 0xFFL) << 8) | ((x[offset + 3] & 0xFFL) << 16)
                            | ((x[offset + 2] & 0xFFL) << 24) | ((x[offset + 1] & 0xFFL) << 32) | ((x[offset] & 0xFFL) << 40);
                case 7:
                    return (x[offset + 6] & 0xFFL) | ((x[offset + 5] & 0xFFL) << 8) | ((x[offset + 4] & 0xFFL) << 16)
                            | ((x[offset + 3] & 0xFFL) << 24) | ((x[offset + 2] & 0xFFL) << 32)
                            | ((x[offset + 1] & 0xFFL) << 40) | ((x[offset] & 0xFFL) << 48);
                case 8:
                    return (x[offset + 7] & 0xFFL) | ((x[offset + 6] & 0xFFL) << 8) | ((x[offset + 5] & 0xFFL) << 16)
                            | ((x[offset + 4] & 0xFFL) << 24) | ((x[offset + 3] & 0xFFL) << 32)
                            | ((x[offset + 2] & 0xFFL) << 40) | ((x[offset + 1] & 0xFFL) << 48) | ((x[offset] & 0xFFL) << 56);
                default:
                    throw new LSysException("No bytes specified");
            }
        }

        public static long FromHexToLong(string hexStr)
        {
            return GetBytesToLong(FromHex(hexStr));
        }

        public static byte[] FromHex(string hexStr)
        {
            if (StringUtils.IsEmpty(hexStr))
            {
                return new byte[] { };
            }
            byte[] bytes = new byte[hexStr.Length() / 2];
            for (int i = 0; i < bytes.Length; i++)
            {
                int char1 = hexStr.CharAt(i * 2);
                char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
                int char2 = hexStr.CharAt(i * 2 + 1);
                char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
                if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15)
                {
                    throw new LSysException("Invalid hex number: " + hexStr);
                }
                bytes[i] = (byte)((char1 << 4) + char2);
            }
            return bytes;
        }

        public static string ToHex(int value)
        {
            byte[] bytes = new byte[4];
            bytes[0] = (byte)((value >> 24) & 0xff);
            bytes[1] = (byte)((value >> 16) & 0xff);
            bytes[2] = (byte)((value >> 8) & 0xff);
            bytes[3] = (byte)(value & 0xff);
            return ToHex(bytes, true);
        }

        public static string ToHex(byte[] bytes)
        {
            return ToHex(bytes, false);
        }

        public static string ToHex(byte[] bytes, bool removeZero)
        {
            if (bytes == null)
            {
                return LSystem.EMPTY;
            }
            char[] hexChars = new char[bytes.Length * 2];
            int v;
            for (int j = 0; j < bytes.Length; j++)
            {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = HexChars.TABLE[(int)((uint)v >> 4) & 0X0F];
                hexChars[j * 2 + 1] = HexChars.TABLE[v & 0x0F];
            }
            if (removeZero)
            {
                System.Text.StringBuilder sbr = new System.Text.StringBuilder(hexChars.Length);
                char tag = '0';
                bool flag = false;
                for (int i = 0; i < hexChars.Length; i++)
                {
                    char ch = hexChars[i];
                    if (ch != tag)
                    {
                        flag = true;
                    }
                    if (flag)
                    {
                        sbr.Append(ch);
                    }
                }
                return sbr.ToString();
            }
            else
            {
                return new string(hexChars);
            }
        }

        public static string ToHex(byte ib)
        {
            char[] ob = new char[2];
            ob[0] = HexChars.TABLE[(int)((uint)ib >> 4) & 0X0F];
            ob[1] = HexChars.TABLE[ib & 0X0F];
            return new string(ob);
        }

        public static int ToInt(char c)
        {
            char[] chars = HexChars.TABLE;
            for (int i = 0; i < 10; i++)
            {
                if (c == chars[i])
                {
                    return i;
                }
            }
            return c;
        }

        public static long B2iu(byte b)
        {
            return b < 0 ? b & 0x7F + 128 : b;
        }

        public static int ToUnsignedLong(long val, int shift, char[] buf, int offset, int len)
        {
            int charPos = len;
            int radix = 1 << shift;
            int mask = radix - 1;
            do
            {
                buf[offset + --charPos] = HexChars.TABLE[((int)val) & mask];
                val = (long)((uint)val >> shift);
            } while (val != 0 && charPos > 0);
            return charPos;
        }

        public static byte[] ToSimpleByteArray(char[] carr)
        {
            byte[] barr = new byte[carr.Length];
            for (int i = 0; i < carr.Length; i++)
            {
                barr[i] = (byte)carr[i];
            }
            return barr;
        }

        public static byte[] ToSimpleByteArray(CharSequence charSequence)
        {
            byte[] barr = new byte[charSequence.Length()];
            for (int i = 0; i < barr.Length; i++)
            {
                barr[i] = (byte)charSequence.CharAt(i);
            }
            return barr;
        }

        public static char[] ToSimpleCharArray(byte[] barr)
        {
            char[] carr = new char[barr.Length];
            for (int i = 0; i < barr.Length; i++)
            {
                carr[i] = (char)(barr[i] & 0xFF);
            }
            return carr;
        }

        public static int ToAscii(char c)
        {
            if (c <= 0xFF)
            {
                return c;
            }
            else
            {
                return 0x3F;
            }
        }

        public static byte[] ToAsciiByteArray(char[] carr)
        {
            byte[] barr = new byte[carr.Length];
            for (int i = 0; i < carr.Length; i++)
            {
                barr[i] = (byte)((int)(carr[i] <= 0xFF ? carr[i] : 0x3F));
            }
            return barr;
        }

        public static byte[] ToAsciiByteArray(CharSequence charSequence)
        {
            byte[] barr = new byte[charSequence.Length()];
            for (int i = 0; i < barr.Length; i++)
            {
                char c = charSequence.CharAt(i);
                barr[i] = (byte)((int)(c <= 0xFF ? c : 0x3F));
            }
            return barr;
        }

        public static byte[] ToRawByteArray(char[] carr)
        {
            byte[] barr = new byte[carr.Length << 1];
            for (int i = 0, bpos = 0; i < carr.Length; i++)
            {
                char c = carr[i];
                barr[bpos++] = (byte)((c & 0xFF00) >> 8);
                barr[bpos++] = (byte)(c & 0x00FF);
            }
            return barr;
        }

        public static char[] ToRawCharArray(byte[] barr)
        {
            int carrLen = barr.Length >> 1;
            if (carrLen << 1 < barr.Length)
            {
                carrLen++;
            }
            char[] carr = new char[carrLen];
            int i = 0, j = 0;
            while (i < barr.Length)
            {
                char c = (char)(barr[i] << 8);
                i++;

                if (i != barr.Length)
                {
                    c += (char)(barr[i] & 0xFF);
                    i++;
                }
                carr[j++] = c;
            }
            return carr;
        }

        public static bool EqualsOne(char c, char[] match)
        {
            foreach (char aMatch in match)
            {
                if (c == aMatch)
                {
                    return true;
                }
            }
            return false;
        }

        public static int FindFirstEqual(char[] source, int index, char[] match)
        {
            for (int i = index; i < source.Length; i++)
            {
                if (EqualsOne(source[i], match))
                {
                    return i;
                }
            }
            return -1;
        }

        public static int FindFirstEqual(char[] source, int index, char match)
        {
            for (int i = index; i < source.Length; i++)
            {
                if (source[i] == match)
                {
                    return i;
                }
            }
            return -1;
        }

        public static int FindFirstDiff(char[] source, int index, char[] match)
        {
            for (int i = index; i < source.Length; i++)
            {
                if (!EqualsOne(source[i], match))
                {
                    return i;
                }
            }
            return -1;
        }

        public static int FindFirstDiff(char[] source, int index, char match)
        {
            for (int i = index; i < source.Length; i++)
            {
                if (source[i] != match)
                {
                    return i;
                }
            }
            return -1;
        }

        public static bool IsChinese(int c)
        {
            return c >= 0x4e00 && c <= 0x9fa5;
        }

        public static bool IsEnglishAndNumeric(int letter)
        {
            return IsAsciiLetterDiait(letter);
        }

        public static bool IsSingle(int c)
        {
            return (':' == c || '：' == c) || (',' == c || '，' == c) || ('"' == c || '“' == c)
                    || ((0x0020 <= c) && (c <= 0x007E) && !((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')))
                            && !('0' <= c) && (c <= '9'));
        }

        public static bool IsAsciiLetterDiait(int c)
        {
            return IsDigitCharacter(c) || IsAsciiLetter(c);
        }

        public static bool IsDigit(int c)
        {
            return c >= '0' && c <= '9';
        }

        public static bool IsHexDigit(int c)
        {
            return (c >= '0' && c <= '9') || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
        }

        public static bool IsDigitCharacter(int c)
        {
            return (c >= '0' && c <= '9') || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
        }

        public static bool IsWhitespace(int c)
        {
            return c == ' ' || c == '\n' || c == '\r' || c == '\t';
        }

        public static bool IsAsciiLetter(int c)
        {
            return IsLowercaseAlpha(c) || IsUppercaseAlpha(c);
        }

        public static bool IsLowercaseAlpha(int c)
        {
            return (c >= 'a') && (c <= 'z');
        }

        public static bool IsUppercaseAlpha(int c)
        {
            return (c >= 'A') && (c <= 'Z');
        }

        public static bool IsAlphabetUpper(char letter)
        {
            return IsUppercaseAlpha(letter);
        }

        public static bool IsAlphabetLower(char letter)
        {
            return IsLowercaseAlpha(letter);
        }

        public static bool IsAlphabet(char letter)
        {
            return IsAsciiLetter(letter);
        }

        public static bool IsAlpha(int c)
        {
            return IsAsciiLetter(c);
        }

        public static bool IsAlphaOrDigit(int c)
        {
            return IsDigit(c) || IsAlpha(c);
        }

        public static bool IsWordChar(int c)
        {
            return IsDigit(c) || IsAlpha(c) || (c == '_');
        }

        public static bool IsPropertyNameChar(int c)
        {
            return IsDigit(c) || IsAlpha(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
        }

        public static bool IsGenericDelimiter(int c)
        {
            switch (c)
            {
                case ':':
                case '/':
                case '?':
                case '#':
                case '[':
                case ']':
                case '@':
                    return true;
                default:
                    return false;
            }
        }

        protected static bool IsSubDelimiter(int c)
        {
            switch (c)
            {
                case '!':
                case '$':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case ';':
                case '=':
                    return true;
                default:
                    return false;
            }
        }

        public static bool IsHighSurrogate(char ch)
        {
            return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
        }

        public static bool IsLowSurrogate(char ch)
        {
            return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
        }

        public static bool IsSurrogate(char ch)
        {
            return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
        }

        public static bool IsSurrogatePair(char high, char low)
        {
            return IsHighSurrogate(high) && IsLowSurrogate(low);
        }

        public static bool IsInherited(char c)
        {
            return c == '~';
        }

        protected static bool IsReserved(int c)
        {
            return IsGenericDelimiter(c) || IsSubDelimiter(c);
        }

        protected static bool IsUnreserved(int c)
        {
            return IsAlpha(c) || IsDigit(c) || c == '-' || c == '.' || c == '_' || c == '~';
        }

        protected static bool IsPchar(int c)
        {
            return IsUnreserved(c) || IsSubDelimiter(c) || c == ':' || c == '@';
        }

        protected static bool IsLetterOrDigit(int ch)
        {
            return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
        }

        protected static bool IsEscapeExempt(int c)
        {
            switch (c)
            {
                case '*':
                case '@':
                case '-':
                case '_':
                case '+':
                case '.':
                case '/':
                    return true;
                default:
                    return false;
            }
        }

        public static int ToUpperAscii(int c)
        {
            if (IsLowercaseAlpha(c))
            {
                c -= (char)0x20;
            }
            return c;
        }

        public static int ToLowerAscii(int c)
        {
            if (IsUppercaseAlpha(c))
            {
                c += (char)0x20;
            }
            return c;
        }

        public static int Hex2int(char c)
        {
            switch (c)
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return c - '0';
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    return c - 55;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    return c - 87;
                default:
                    throw new LSysException("Not a hex: " + c);
            }
        }

        public static char Int2hex(int i)
        {
            return HexChars.TABLE[i];
        }


    }
}
