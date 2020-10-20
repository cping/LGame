namespace java.lang
{
    public class Long : Number
    {
        public readonly static long MIN_VALUE_JAVA = -0x8000000000000000L;
        public readonly static long MAX_VALUE_JAVA = 0x7FFFFFFFFFFFFFFFL;
        public readonly static int SIZE = 64;
        private long value;

        public override double DoubleValue()
        {
            return (double)value;
        }

        public override float FloatValue()
        {
            return (float)value;
        }

        public override int IntValue()
        {
            return (int)value;
        }

        public override long LongValue()
        {
            return this.value;
        }

        public static long ParseLong(string s)
        {
            if (System.Int64.TryParse(s, out long result)) { return result; }
            throw new NumberFormatException();
        }

        public static long ParseLong(string s, int radix)
        {
            return System.Convert.ToInt64(s, radix);
        }

    }
}
