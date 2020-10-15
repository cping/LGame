namespace java.lang
{
    public class Float : Number
    {
        private readonly float value;

        private const long POWER_31_INT = 2147483648L;

        public const float MIN_VALUE_JAVA = 1.17549435E-38f;
        public const float MAX_VALUE_JAVA = 3.4028235e+38f;
        public const float POSITIVE_INFINITY_JAVA = 1.0f / 0.0f;
        public const float NEGATIVE_INFINITY_JAVA = -1.0f / 0.0f;
        public const float NaN = 0.0f / 0.0f;
        public Float(float f)
        {
            value = f;
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

        public static uint FloatToIntBits(float value)
        {
   
            if (IsNaN(value))
            {
                return 0x7fc00000;
            }

            if (value == 0.0f)
            {
                if (1.0 / value == NEGATIVE_INFINITY_JAVA)
                {
                    return 0x80000000; 
                }
                else
                {
                    return 0x0;
                }
            }
            bool negative = false;
            if (value < 0.0)
            {
                negative = true;
                value = -value;
            }
            if (IsInfinite(value))
            {
                if (negative)
                {
                    return 0xff800000;
                }
                else
                {
                    return 0x7f800000;
                }
            }

            ulong l = Double.DoubleToLongBits((double)value);
            int exp = (int)(((l >> 52) & 0x7ff) - 1023);
            int mantissa = (int)((l & 0xfffffffffffffL) >> 29);

            if (exp <= -127)
            {
                mantissa = (0x800000 | mantissa) >> (-127 - exp + 1);
                exp = -127;
            }
     
            long bits = negative ? POWER_31_INT : 0x0L;
#pragma warning disable CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
            bits |= (exp + 127) << 23;
            bits |= mantissa;
#pragma warning restore CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符

            return (uint)bits;
        }
        public static float IntBitsToFloat(int bits)
        {
            bool negative = (bits & 0x80000000) != 0;
            int exp = (bits >> 23) & 0xff;
            bits &= 0x7fffff;

            if (exp == 0x0)
            {
                if (bits == 0)
                {
                    return negative ? -0.0f : 0.0f;
                }
            }
            else if (exp == 0xff)
            {
                if (bits == 0)
                {
                    return negative ? NEGATIVE_INFINITY_JAVA : POSITIVE_INFINITY_JAVA;
                }
                else
                {
                    return NaN;
                }
            }

            if (exp == 0)
            {
                exp = 1;
                while ((bits & 0x800000) == 0)
                {
                    bits <<= 1;
                    exp--;
                }
                bits &= 0x7fffff;
            }

    
            ulong bits64 = negative ? 0x8000000000000000L : 0x0L;
            bits64 |= ((ulong)(exp + 896)) << 52;
            bits64 |= ((ulong)bits) << 29;
            return (float)Double.LongBitsToDouble((long)bits64);
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

        public override double DoubleValue()
        {
            return (double)this.value;
        }

        public override float FloatValue()
        {
            return this.value;
        }

        public override int IntValue()
        {
            return (int)this.value;
        }

        public override long LongValue()
        {
            return (long)this.value;
        }
    }
}
