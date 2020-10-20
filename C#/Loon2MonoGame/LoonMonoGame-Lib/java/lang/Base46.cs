namespace java.lang
{
    public class Base46
{
        private Base46()
        {
        }

        public static void EncodeUnsigned(StringBuilder sbr, int number)
        {
            bool hasMore;
            do
            {
                int digit = number % 46;
                number /= 46;
                hasMore = number > 0;
                digit = digit * 2 + (hasMore ? 1 : 0);
                sbr.Append(EncodeDigit(digit));
            } while (hasMore);
        }

        public static void Encode(StringBuilder sbr, int number)
        {
            EncodeUnsigned(sbr, Math.Abs(number) * 2 + (number >= 0 ? 0 : 1));
        }

        public static void EncodeUnsigned(StringBuilder sbr, long number)
        {
            bool hasMore;
            do
            {
                int digit = (int)(number % 46);
                number /= 46;
                hasMore = number > 0;
                digit = digit * 2 + (hasMore ? 1 : 0);
                sbr.Append(EncodeDigit(digit));
            } while (hasMore);
        }

        public static void Encode(StringBuilder sbr, long number)
        {
            EncodeUnsigned(sbr, (int)Math.Abs(number) * 2 + (number >= 0 ? 0 : 1));
        }

        public static int DecodeUnsigned(UnicodeUtils.CharFlow seq)
        {
            int number = 0;
            int pos = 1;
            bool hasMore;
            do
            {
                int digit = DecodeDigit(seq.characters[seq.pointer++]);
                hasMore = digit % 2 == 1;
                number += pos * (digit / 2);
                pos *= 46;
            } while (hasMore);
            return number;
        }

        public static int Decode(UnicodeUtils.CharFlow seq)
        {
            int number = DecodeUnsigned(seq);
            int result = number / 2;
            if (number % 2 != 0)
            {
                result = -result;
            }
            return result;
        }

        public static long DecodeUnsignedLong(UnicodeUtils.CharFlow seq)
        {
            long number = 0;
            long pos = 1;
            bool hasMore;
            do
            {
                int digit = DecodeDigit(seq.characters[seq.pointer++]);
                hasMore = digit % 2 == 1;
                number += pos * (digit / 2);
                pos *= 46;
            } while (hasMore);
            return number;
        }

        public static long DecodeLong(UnicodeUtils.CharFlow seq)
        {
            long number = DecodeUnsigned(seq);
            long result = number / 2;
            if (number % 2 != 0)
            {
                result = -result;
            }
            return result;
        }

        public static char EncodeDigit(int digit)
        {
            if (digit < 2)
            {
                return (char)(digit + ' ');
            }
            else if (digit < 59)
            {
                return (char)(digit + 1 + ' ');
            }
            else
            {
                return (char)(digit + 2 + ' ');
            }
        }

        public static int DecodeDigit(char c)
        {
            if (c < '"')
            {
                return c - ' ';
            }
            else if (c < '\\')
            {
                return c - ' ' - 1;
            }
            else
            {
                return c - ' ' - 2;
            }
        }
    }
}
