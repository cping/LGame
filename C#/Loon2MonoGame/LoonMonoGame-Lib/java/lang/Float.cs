namespace java.lang
{
    public class Float
    {
        private readonly float value;

        public const float MIN_VALUE_JAVA = 1.17549435E-38f;
        public const float MAX_VALUE_JAVA = 3.4028235e+38f;
        public const float POSITIVE_INFINITY_JAVA = 1.0f / 0.0f;
        public const float NEGATIVE_INFINITY_JAVA = -1.0f / 0.0f;
        public const float NaN_JAVA = 0.0f / 0.0f;
        public Float(float f)
        {
            value = f;
        }

        public float FloatValue()
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
            if (o == null || !(o is Float)) return false;
            return ((Float)o).value == value;
        }

        public override int GetHashCode()
        {
            long l = System.BitConverter.DoubleToInt64Bits(value);
            int a = (int)(l >> 32);
            int b = (int)l;
            return a ^ b;
        }

        public override string ToString()
        {
            return JavaSystem.Str(value);
        }

        public static bool IsNaN(float d)
        {
            return System.Double.IsNaN(d);
        }

        public static bool IsInfinite(float d)
        {
            return System.Double.IsInfinity(d);
        }

        public static float ParseFloat (string s)
        {
            double result;
            if
            (System.Double.TryParse
                (
                    s,
                    System.Globalization.NumberStyles.Float,
                    System.Globalization.CultureInfo.InvariantCulture,
                    out result
                )
            )
            {
                return (float)result;
            }
            throw new NumberFormatException();
        }

        public static string ToString(float d)
        {
            return JavaSystem.Str(d);
        }

        public static Float ValueOf(float d)
        {
            return new Float(d);
        }
    }
}
