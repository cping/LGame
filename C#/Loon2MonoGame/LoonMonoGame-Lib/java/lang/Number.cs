namespace java.lang
{
    public abstract class Number
    {
        public byte ByteValue()
        {
            return (byte)IntValue();
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
