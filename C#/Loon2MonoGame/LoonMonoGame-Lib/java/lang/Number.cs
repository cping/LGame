namespace java.lang
{
    public abstract class Number
    {
        public sbyte ByteValue()
        {
            return (sbyte)IntValue();
        }

        public abstract double DoubleValue();

        public abstract float FloatValue();

        public abstract int IntValue();

        public abstract long LongValue();

        public short ShortValue()
        {
            return (short)IntValue();
        }
    }
}
