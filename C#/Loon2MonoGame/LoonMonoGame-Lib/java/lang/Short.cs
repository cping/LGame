namespace java.lang
{
    public class Short
    {
        private readonly short value;

        public const short MIN_VALUE_JAVA = -32768;
        public const short MAX_VALUE_JAVA = 32767;
        public Short(short v)
        {
            value = v;
        }

        public short ShortValue()
        {
            return value;
        }

        public override bool Equals(object o)
        {
            if (o == null || !(o is Short)) return false;
            return ((Short)o).value == value;
        }

        public override int GetHashCode()
        {
            return (int)value;
        }

        public override string ToString()
        {
            return Short.ToString(value);
        }

        public static string ToString(short b)
        {
            return b.ToString();
        }

        public static Short ValueOf(short b)
        {
            return new Short(b);
        }
    }
}
