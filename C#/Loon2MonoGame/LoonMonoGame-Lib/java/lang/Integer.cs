namespace java.lang 
{ 
    public class Integer
    {
        private readonly int value;

        public Integer(int v) 
        {   
            value = v;
        }

        public int intValue() 
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
            return Integer.toString(value);
        }

        public static int parseInt(string s)
        {
            int result;
            if (System.Int32.TryParse(s, out result)) { return result; }            
            throw new NumberFormatException();
        }

        public static string toString(int i)
        {   
            return i.ToString("d");
        }    
        
        public static string toHexString(int i)
        {   
            return i.ToString("x");
        }
            
        public static Integer valueOf(int i)
        {   
            return new Integer(i);
        }

        public const int MIN_005fVALUE_f = -2147483648;
        public const int MAX_005fVALUE_f =  2147483647;        
    }
}
