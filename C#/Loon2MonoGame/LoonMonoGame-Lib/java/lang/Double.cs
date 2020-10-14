namespace java.lang 
{
    public class Double 
    {
        private readonly double value;

        public const double MIN_VALUE_JAVA = 4.9E-324;
        public const double MAX_VALUE_JAVA = 1.7976931348623157E308;
        public const double POSITIVE_INFINITY_JAVA = 1.0 / 0.0;
        public const double NEGATIVE_INFINITY_JAVA = -1.0 / 0.0;

        public Double(double d) 
        {   
            value = d;
        }

        public double DoubleValue() 
        {   
            return value;
        }
            
        public bool IsNaN() 
        {   
            return IsNaN(value);
        }
            
        public bool IsInfinite() 
        {   
            return IsInfinite(value);
        }
            
        public override bool Equals(object o) 
        {   
            if (o==null || !(o is Double)) return false;
            return ((Double)o).value == value;
        }

        public override int GetHashCode()
        {   
            long l = System.BitConverter.DoubleToInt64Bits( value );       
            int a = (int) (l>>32);
            int b = (int) l;
            return  a ^ b;
        }

        public override string ToString() 
        {   
            return JavaSystem.Str(value);
        }

        public static bool IsNaN(double d) 
        {   
            return System.Double.IsNaN(d);
        }
            
        public static bool IsInfinite(double d) 
        {   
            return System.Double.IsInfinity(d);
        }    
        
        public static double ParseDouble(string s)
        {
            double result;
            if 
            (   System.Double.TryParse
                (
                    s, 
                    System.Globalization.NumberStyles.Float,
                    System.Globalization.CultureInfo.InvariantCulture, 
                    out result
                )
            )
            {   return result;
            }
            throw new NumberFormatException();
        }
            
        public static string ToString(double d)
        {   
            return JavaSystem.Str(d);
        }
            
        public static Double ValueOf(double d)
        {   
            return new Double(d);
        }
    }
}
