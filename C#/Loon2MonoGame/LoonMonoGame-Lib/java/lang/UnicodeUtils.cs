namespace java.lang
{
   public class UnicodeUtils
{
        private UnicodeUtils()
        {
        }

        static char HexDigit(int value)
        {
            return value < 10 ? (char)('0' + value) : (char)('A' + value);
        }

        static int ValueOfHexDigit(char digit)
        {
            return digit <= '9' ? digit - '0' : digit - 'A' + 10;
        }
        public class CharFlow
        {
            public readonly char[] characters;
            public int pointer;

            public CharFlow(char[] characters)
            {
                this.characters = characters;
            }
        }
        public class Range
        {
            public readonly int start;
            public readonly int end;
            public readonly byte[] data;

            public Range(int start, int end, byte[] data)
            {
                this.start = start;
                this.end = end;
                this.data = data;
            }
        }

        public static string EncodeIntPairsDiff(int[] data)
        {
            StringBuilder sbr = new StringBuilder();
            Base46.EncodeUnsigned(sbr, data.Length / 2);
            int lastKey = 0;
            int lastValue = 0;
            for (int i = 0; i < data.Length; i += 2)
            {
                int key = data[i];
                int value = data[i + 1];
                Base46.Encode(sbr, key - lastKey);
                Base46.Encode(sbr, value - lastValue);
                lastKey = key;
                lastValue = value;
            }
            return sbr.ToString();
        }

        public static int[] DecodeIntPairsDiff(string text)
        {
            CharFlow flow = new CharFlow(text.ToCharArray());
            int sz = Base46.DecodeUnsigned(flow);
            int[] data = new int[sz * 2];
            int j = 0;
            int lastKey = 0;
            int lastValue = 0;
            for (int i = 0; i < sz; i++)
            {
                lastKey += Base46.Decode(flow);
                lastValue += Base46.Decode(flow);
                data[j++] = lastKey;
                data[j++] = lastValue;
            }
            return data;
        }

        public static string EncodeIntDiff(int[] data)
        {
            StringBuilder sbr = new StringBuilder();
            Base46.EncodeUnsigned(sbr, data.Length);
            int last = 0;
            for (int i = 0; i < data.Length; i++)
            {
                int v = data[i];
                Base46.Encode(sbr, v - last);
                last = v;
            }
            return sbr.ToString();
        }

        public static int[] DecodeIntDiff(string text)
        {
            CharFlow flow = new CharFlow(text.ToCharArray());
            int sz = Base46.DecodeUnsigned(flow);
            int[] data = new int[sz];
            int last = 0;
            for (int i = 0; i < sz; i++)
            {
                last += Base46.Decode(flow);
                data[i] = last;
            }
            return data;
        }

        public static char EncodeByte(byte b)
        {
            if (b < '\"' - ' ')
            {
                return (char)(b + ' ');
            }
            else if (b < '\\' - ' ' - 1)
            {
                return (char)(b + ' ' + 1);
            }
            else
            {
                return (char)(b + ' ' + 2);
            }
        }

        public static byte DecodeByte(char c)
        {
            if (c > '\\')
            {
                return (byte)(c - ' ' - 2);
            }
            else if (c > '"')
            {
                return (byte)(c - ' ' - 1);
            }
            else
            {
                return (byte)(c - ' ');
            }
        }

        public static string CompressRle(byte[] bytes)
        {
            StringBuilder sbr = new StringBuilder();
            for (int i = 0; i < bytes.Length;)
            {
                byte b = bytes[i];
                if (i < bytes.Length - 1 && b == bytes[i + 1])
                {
                    int count = 0;
                    while (count < 16384 && bytes[i + count] == b)
                    {
                        ++count;
                    }
                    i += count;
                    if (count < 80)
                    {
                        sbr.Append(UnicodeUtils.EncodeByte((byte)(b + 32)));
                        sbr.Append(UnicodeUtils.EncodeByte((byte)count));
                    }
                    else
                    {
                        sbr.Append(UnicodeUtils.EncodeByte((byte)64));
                        sbr.Append(UnicodeUtils.EncodeByte(b));
                        for (int j = 0; j < 3; ++j)
                        {
                            sbr.Append(UnicodeUtils.EncodeByte((byte)(count & 0x3F)));
                            count /= 0x40;
                        }
                    }
                }
                else
                {
                    sbr.Append(UnicodeUtils.EncodeByte(bytes[i++]));
                }
            }
            return sbr.ToString();
        }

        public static Range[] ExtractRle(string encoded)
        {
            Range[] ranges = new Range[16384];
            byte[] buffer = new byte[16384];
            int index = 0;
            int rangeIndex = 0;
            int codePoint = 0;
            for (int i = 0; i < encoded.Length; ++i)
            {
                byte b = DecodeByte(encoded.CharAt(i));
                int count;
                if (b == 64)
                {
                    b = DecodeByte(encoded.CharAt(++i));
                    count = 0;
                    int pos = 1;
                    for (int j = 0; j < 3; ++j)
                    {
                        byte digit = DecodeByte(encoded.CharAt(++i));
                        count |= pos * digit;
                        pos *= 0x40;
                    }
                }
                else if (b >= 32)
                {
                    b -= 32;
                    count = DecodeByte(encoded.CharAt(++i));
                }
                else
                {
                    count = 1;
                }
                if (b != 0 || count < 128)
                {
                    if (index + count >= buffer.Length)
                    {
                        ranges[rangeIndex++] = new Range(codePoint, codePoint + index, util.Arrays.CopyOf(buffer, index));
                        codePoint += index + count;
                        index = 0;
                    }
                    while (count-- > 0)
                    {
                        buffer[index++] = b;
                    }
                }
                else
                {
                    if (index > 0)
                    {
                        ranges[rangeIndex++] = new Range(codePoint, codePoint + index, util.Arrays.CopyOf(buffer, index));
                    }
                    codePoint += index + count;
                    index = 0;
                }
            }
            return util.Arrays.CopyOf(ranges, rangeIndex);
        }
    }
}
