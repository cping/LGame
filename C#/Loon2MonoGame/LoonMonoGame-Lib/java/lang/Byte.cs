namespace java.lang 
{ 
    public class Byte : Number
    {
        private readonly sbyte value;

        public const sbyte MIN_VALUE_JAVA = -128;
        public const sbyte MAX_VALUE_JAVA = 127;

        public Byte(sbyte v)
        {   
            value = v;
        }

        public sbyte ByteValue()
        {   
            return value;
        }

        public override bool Equals(object o)
        {   
            if (o == null || !(o is Byte)) return false;
            return ((Byte)o).value == value;
        }

        public override int GetHashCode()
        {   
            return (int) value;
        }

        public override string ToString()
        {   
            return Byte.ToString(value);
        }
    
        public static string ToString(sbyte b)
        {   
            return b.ToString();
        }

        public static Byte ValueOf(sbyte b)
        {   
            return new Byte(b);
        }

        public override double DoubleValue()
        {
            return value;
        }

        public override float FloatValue()
        {
            return value;
        }

        public override int IntValue()
        {
            return value;
        }

        public override long LongValue()
        {
            return value;
        }
    }
}
