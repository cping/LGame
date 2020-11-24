using java.lang;
using System;

namespace loon.utils
{
    public class Base64Coder
    {

        private const int BASELENGTH = 255;

        private const int LOOKUPLENGTH = 64;

        private const int TWENTYFOURBITGROUP = 24;

        private const int EIGHTBIT = 8;

        private const int SIXTEENBIT = 16;

        private const int FOURBYTE = 4;

        private const int SIGN = -128;

        private const sbyte PAD = (sbyte)'=';

        private static sbyte[] BASE64_ALPHABET;

        private static sbyte[] LOOKUP_BASE64_ALPHABET;

        private Base64Coder()
        {

        }

        public static sbyte[] FromBinHexString(string s)
        {
            char[] chars = s.ToCharArray();
            sbyte[] bytes = new sbyte[chars.Length / 2 + chars.Length % 2];
            FromBinHexString(chars, 0, chars.Length, bytes);
            return bytes;
        }

        public static int FromBinHexString(char[] chars, int offset, int charLength, sbyte[] buffer)
        {
            int bufIndex = offset;
            for (int i = 0; i < charLength - 1; i += 2)
            {
                buffer[bufIndex] = (chars[i] > '9' ? (sbyte)(chars[i] - 'A' + 10) : (sbyte)(chars[i] - '0'));
                buffer[bufIndex] <<= 4;
                buffer[bufIndex] += chars[i + 1] > '9' ? (sbyte)(chars[i + 1] - 'A' + 10) : (sbyte)(chars[i + 1] - '0');
                bufIndex++;
            }
            if (charLength % 2 != 0)
                buffer[bufIndex++] = (sbyte)((chars[charLength - 1] > '9' ? (sbyte)(chars[charLength - 1] - 'A' + 10)
                        : (sbyte)(chars[charLength - 1] - '0')) << 4);

            return bufIndex - offset;
        }

        private static void Checking()
        {
            if (BASE64_ALPHABET == null)
            {
                BASE64_ALPHABET = new sbyte[BASELENGTH];
                for (int i = 0; i < BASELENGTH; i++)
                {
                    BASE64_ALPHABET[i] = -1;
                }
                for (int i = 'Z'; i >= 'A'; i--)
                {
                    BASE64_ALPHABET[i] = (sbyte)(i - 'A');
                }
                for (int i = 'z'; i >= 'a'; i--)
                {
                    BASE64_ALPHABET[i] = (sbyte)(i - 'a' + 26);
                }

                for (int i = '9'; i >= '0'; i--)
                {
                    BASE64_ALPHABET[i] = (sbyte)(i - '0' + 52);
                }

                BASE64_ALPHABET['+'] = 62;
                BASE64_ALPHABET['/'] = 63;
            }
            if (LOOKUP_BASE64_ALPHABET == null)
            {
                LOOKUP_BASE64_ALPHABET = new sbyte[LOOKUPLENGTH];
                for (int i = 0; i <= 25; i++)
                {
                    LOOKUP_BASE64_ALPHABET[i] = (sbyte)('A' + i);
                }

                for (int i = 26, j = 0; i <= 51; i++, j++)
                {
                    LOOKUP_BASE64_ALPHABET[i] = (sbyte)('a' + j);
                }

                for (int i = 52, j = 0; i <= 61; i++, j++)
                {
                    LOOKUP_BASE64_ALPHABET[i] = (sbyte)('0' + j);
                }
                LOOKUP_BASE64_ALPHABET[62] = (sbyte)'+';
                LOOKUP_BASE64_ALPHABET[63] = (sbyte)'/';
            }
        }

        public static bool IsBase64(string v)
        {
            return IsArrayByteBase64(v.GetBytes());
        }

        public static bool IsArrayByteBase64(sbyte[] bytes)
        {
            Checking();
            int length = bytes.Length;
            if (length == 0)
            {
                return true;
            }
            for (int i = 0; i < length; i++)
            {
                if (!Base64Coder.IsBase64(bytes[i]))
                {
                    return false;
                }
            }
            return true;
        }

        private static bool IsBase64(sbyte octect)
        {
            return octect == PAD || BASE64_ALPHABET[octect] != -1;
        }

        public static sbyte[] Encode(sbyte[] binaryData)
        {
            Checking();
            int lengthDataBits = binaryData.Length * EIGHTBIT;
            int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
            int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
            sbyte[] encodedData;

            if (fewerThan24bits != 0)
            {
                encodedData = new sbyte[(numberTriplets + 1) * 4];
            }
            else
            {
                encodedData = new sbyte[numberTriplets * 4];
            }

            sbyte k ;
            sbyte l ;
            sbyte b1 ;
            sbyte b2 ;
            sbyte b3 ;
            int encodedIndex ;
            int dataIndex ;
            int i ;
            for (i = 0; i < numberTriplets; i++)
            {

                dataIndex = i * 3;
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                b3 = binaryData[dataIndex + 2];

                l = (sbyte)(b2 & 0x0f);
                k = (sbyte)(b1 & 0x03);

                encodedIndex = i * 4;
                sbyte val1 = ((b1 & SIGN) == 0) ? (sbyte)(b1 >> 2) : (sbyte)((b1) >> 2 ^ 0xc0);

                sbyte val2 = ((b2 & SIGN) == 0) ? (sbyte)(b2 >> 4) : (sbyte)((b2) >> 4 ^ 0xf0);
                sbyte val3 = ((b3 & SIGN) == 0) ? (sbyte)(b3 >> 6) : (sbyte)((b3) >> 6 ^ 0xfc);

                encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
#pragma warning disable CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2 | (k << 4)];
                encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[(l << 2) | val3];
#pragma warning restore CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                encodedData[encodedIndex + 3] = LOOKUP_BASE64_ALPHABET[b3 & 0x3f];
            }

            dataIndex = i * 3;
            encodedIndex = i * 4;
            if (fewerThan24bits == EIGHTBIT)
            {
                b1 = binaryData[dataIndex];
                k = (sbyte)(b1 & 0x03);
                sbyte val1 = ((b1 & SIGN) == 0) ? (sbyte)(b1 >> 2) : (sbyte)((b1) >> 2 ^ 0xc0);
                encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
                encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[k << 4];
                encodedData[encodedIndex + 2] = PAD;
                encodedData[encodedIndex + 3] = PAD;
            }
            else if (fewerThan24bits == SIXTEENBIT)
            {
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                l = (sbyte)(b2 & 0x0f);
                k = (sbyte)(b1 & 0x03);

                sbyte val1 = ((b1 & SIGN) == 0) ? (sbyte)(b1 >> 2) : (sbyte)((b1) >> 2 ^ 0xc0);
                sbyte val2 = ((b2 & SIGN) == 0) ? (sbyte)(b2 >> 4) : (sbyte)((b2) >> 4 ^ 0xf0);

                encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
#pragma warning disable CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2 | (k << 4)];
                encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[l << 2];
#pragma warning restore CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                encodedData[encodedIndex + 3] = PAD;
            }
            return encodedData;
        }

        public static sbyte[] Decode(sbyte[] base64Data)
        {
            Checking();
            if (base64Data.Length == 0)
            {
                return new sbyte[0];
            }

            int numberQuadruple = base64Data.Length / FOURBYTE;
            sbyte[] decodedData = null;
            sbyte b1 , b2 , b3 , b4 , marker0 , marker1 ;

            int encodedIndex = 0;
            int dataIndex = 0;
            {
                int lastData = base64Data.Length;
                while (base64Data[lastData - 1] == PAD)
                {
                    if (--lastData == 0)
                    {
                        return new sbyte[0];
                    }
                }
                decodedData = new sbyte[lastData - numberQuadruple];
            }

            for (int i = 0; i < numberQuadruple; i++)
            {
                dataIndex = i * 4;
                marker0 = base64Data[dataIndex + 2];
                marker1 = base64Data[dataIndex + 3];

                b1 = BASE64_ALPHABET[base64Data[dataIndex]];
                b2 = BASE64_ALPHABET[base64Data[dataIndex + 1]];

                if (marker0 != PAD && marker1 != PAD)
                {
                    b3 = BASE64_ALPHABET[marker0];
                    b4 = BASE64_ALPHABET[marker1];

                    decodedData[0] = (sbyte)(b1 << 2 | b2 >> 4);
                    decodedData[0 + 1] = (sbyte)(((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
#pragma warning disable CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                    decodedData[0 + 2] = (sbyte)(b3 << 6 | b4);
#pragma warning restore CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
                }
                else if (marker0 == PAD)
                {
                    decodedData[0] = (sbyte)(b1 << 2 | b2 >> 4);
                }
                else if (marker1 == PAD)
                {
                    b3 = BASE64_ALPHABET[marker0];
                    decodedData[0] = (sbyte)(b1 << 2 | b2 >> 4);
                    decodedData[0 + 1] = (sbyte)(((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                }
                encodedIndex += 3;
            }
            return decodedData;
        }

        public static sbyte[] Decode(string data)
        {
            return DecodeBase64(data.ToCharArray());
        }

        public static sbyte[] DecodeBase64(char[] data)
        {
            Checking();

            int size = data.Length;
            int temp = size;

            for (int ix = 0; ix < data.Length; ix++)
            {
                if ((data[ix] > 255) || BASE64_ALPHABET[data[ix]] < 0)
                {
                    --temp;
                }
            }

            int len = (temp / 4) * 3;
            if ((temp % 4) == 3)
            {
                len += 2;
            }
            if ((temp % 4) == 2)
            {
                len += 1;
            }
            sbyte[] outs = new sbyte[len];

            int shift = 0;
            int accum = 0;
            int index = 0;

            for (int ix = 0; ix < size; ix++)
            {
                int value = (data[ix] > 255) ? -1 : BASE64_ALPHABET[data[ix]];

                if (value >= 0)
                {
                    accum <<= 6;
                    shift += 6;
                    accum |= value;
                    if (shift >= 8)
                    {
                        shift -= 8;
                        outs[index++] = (sbyte)((accum >> shift) & 0xff);
                    }
                }
            }

            if (index != outs.Length)
            {
                throw new LSysException("index != " + outs.Length);
            }

            return outs;
        }
    }
}
