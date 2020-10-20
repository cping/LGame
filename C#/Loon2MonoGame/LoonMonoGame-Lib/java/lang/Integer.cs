using System;
using System.Globalization;

namespace java.lang 
{ 
    public class Integer : Number
    {
        private readonly int value;

        public const int MIN_VALUE_JAVA = -2147483648;
        public const int MAX_VALUE_JAVA = 2147483647;

        public Integer(int v) 
        {   
            value = v;
        }

        public override int IntValue() 
        {   
            return value;
        }

        public override bool Equals(object o)
        {   
            if (o==null || !(o is Integer)) return false;
            return ((Integer)o).value == value;
        }

        public override int GetHashCode()
        {   
            return value;
        }

        public override string ToString()
        {   
            return Integer.ToString(value);
        }

        public static int ParseInt(string s)
        {
            if (System.Int32.TryParse(s, out int result)) { return result; }
            throw new NumberFormatException();
        }
        public static int ParseInt(string s,int radix)
        {
            return System.Convert.ToInt32(s,radix);
        }

        public static string ToString(int i)
        {   
            return i.ToString("d");
        }    
        
        public static string ToHexString(int i)
        {   
            return i.ToString("x");
        }
            
        public static Integer ValueOf(int i)
        {   
            return new Integer(i);
        }

        public override double DoubleValue()
        {
            return this.value;
        }

        public override float FloatValue()
        {
            return this.value;
        }

        public override long LongValue()
        {
            return this.value;
        }
  
    }
}
