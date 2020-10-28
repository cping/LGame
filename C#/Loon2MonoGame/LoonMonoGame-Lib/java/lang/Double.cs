namespace java.lang
{
    public class Double : Number
    {
        private readonly double value;

        public const double MIN_VALUE_JAVA = 4.9E-324;
        public const double MAX_VALUE_JAVA = 1.7976931348623157E308;
        public const double POSITIVE_INFINITY_JAVA = 1.0 / 0.0;
        public const double NEGATIVE_INFINITY_JAVA = -1.0 / 0.0;

        public const double NaN = 0d / 0d;
        // 2^512, 2^-512
        private const double POWER_512 = 1.3407807929942597E154;
        private const double POWER_MINUS_512 = 7.458340731200207E-155;
        // 2^256, 2^-256
        private const double POWER_256 = 1.157920892373162E77;
        private const double POWER_MINUS_256 = 8.636168555094445E-78;
        // 2^128, 2^-128
        private const double POWER_128 = 3.4028236692093846E38;
        private const double POWER_MINUS_128 = 2.9387358770557188E-39;
        // 2^64, 2^-64
        private const double POWER_64 = 18446744073709551616.0;
        private const double POWER_MINUS_64 = 5.421010862427522E-20;
        // 2^52, 2^-52
        private const double POWER_52 = 4503599627370496.0;
        private const double POWER_MINUS_52 = 2.220446049250313E-16;
        // 2^32, 2^-32
        private const double POWER_32 = 4294967296.0;
        private const double POWER_MINUS_32 = 2.3283064365386963E-10;
        // 2^31
        private const double POWER_31 = 2147483648.0;
        // 2^20, 2^-20
        private const double POWER_20 = 1048576.0;
        private const double POWER_MINUS_20 = 9.5367431640625E-7;
        // 2^16, 2^-16
        private const double POWER_16 = 65536.0;
        private const double POWER_MINUS_16 = 0.0000152587890625;
        // 2^8, 2^-8
        private const double POWER_8 = 256.0;
        private const double POWER_MINUS_8 = 0.00390625;
        // 2^4, 2^-4
        private const double POWER_4 = 16.0;
        private const double POWER_MINUS_4 = 0.0625;
        // 2^2, 2^-2
        private const double POWER_2 = 4.0;
        private const double POWER_MINUS_2 = 0.25;
        // 2^1, 2^-1
        private const double POWER_1 = 2.0;
        private const double POWER_MINUS_1 = 0.5;
        // 2^-1022 (smallest double non-denorm)
        private const double POWER_MINUS_1022 = 2.2250738585072014E-308;

        static class PowersTable
        {
            public static readonly double[] Powers = {
        POWER_512, POWER_256, POWER_128, POWER_64, POWER_32, POWER_16, POWER_8,
        POWER_4, POWER_2, POWER_1
    };

            public static readonly double[] InvPowers = {
        POWER_MINUS_512, POWER_MINUS_256, POWER_MINUS_128, POWER_MINUS_64,
        POWER_MINUS_32, POWER_MINUS_16, POWER_MINUS_8, POWER_MINUS_4, POWER_MINUS_2,
        POWER_MINUS_1
    };
        }
        public Double(double d)
        {
            value = d;
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
            if (o == null || !(o is Double)) return false;
            return ((Double)o).value == value;
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


        public static ulong DoubleToLongBits(double value)
        {
            if (IsNaN(value))
            {
                return 0x7ff8000000000000L;
            }

            bool negative = false;
            if (value == 0.0)
            {
                if (1.0 / value == NEGATIVE_INFINITY_JAVA)
                {
                    return 0x8000000000000000L;
                }
                else
                {
                    return 0x0L;
                }
            }
            if (value < 0.0)
            {
                negative = true;
                value = -value;
            }
            if (IsInfinite(value))
            {
                if (negative)
                {
                    return 0xfff0000000000000L;
                }
                else
                {
                    return 0x7ff0000000000000L;
                }
            }

            int exp = 0;

            if (value < 1.0)
            {
                int bit = 512;
                for (int i = 0; i < 10; i++, bit >>= 1)
                {
                    if (value < PowersTable.InvPowers[i] && exp - bit >= -1023)
                    {
                        value *= PowersTable.Powers[i];
                        exp -= bit;
                    }
                }

                if (value < 1.0 && exp - 1 >= -1023)
                {
                    value *= 2.0;
                    exp--;
                }
            }
            else if (value >= 2.0)
            {
                int bit = 512;
                for (int i = 0; i < 10; i++, bit >>= 1)
                {
                    if (value >= PowersTable.Powers[i])
                    {
                        value *= PowersTable.InvPowers[i];
                        exp += bit;
                    }
                }
            }

            if (exp > -1023)
            {
                value -= 1.0;
            }
            else
            {
                value *= 0.5;
            }

            long ihi = (long)(value * POWER_20);

            value -= ihi * POWER_MINUS_20;

            long ilo = (long)(value * POWER_52);

#pragma warning disable CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符
            ihi |= (exp + 1023) << 20;
#pragma warning restore CS0675 // 对进行了带符号扩展的操作数使用了按位或运算符

            if (negative)
            {
                ihi |= 0x80000000L;
            }

            return (ulong)((ihi << 32) | ilo);
        }

        public static double LongBitsToDouble(long bits)
        {
            long ihi = (long)(bits >> 32);
            long ilo = (long)(bits & 0xffffffffL);
            if (ihi < 0)
            {
                ihi += 0x100000000L;
            }
            if (ilo < 0)
            {
                ilo += 0x100000000L;
            }

            bool negative = (ihi & 0x80000000) != 0;
            int exp = (int)((ihi >> 20) & 0x7ff);
            ihi &= 0xfffff;
            double d;
            if (exp == 0x0)
            {
                d = (ihi * POWER_MINUS_20) + (ilo * POWER_MINUS_52);
                d *= POWER_MINUS_1022;
                return negative ? (d == 0.0 ? -0.0 : -d) : d;
            }
            else if (exp == 0x7ff)
            {
                if (ihi == 0 && ilo == 0)
                {
                    return negative ? Double.NEGATIVE_INFINITY_JAVA : Double.POSITIVE_INFINITY_JAVA;
                }
                else
                {
                    return Double.NaN;
                }
            }

            exp -= 1023;

            d = 1.0 + (ihi * POWER_MINUS_20) + (ilo * POWER_MINUS_52);
            if (exp > 0)
            {
                int bit = 512;
                for (int i = 0; i < 10; i++, bit >>= 1)
                {
                    if (exp >= bit)
                    {
                        d *= PowersTable.Powers[i];
                        exp -= bit;
                    }
                }
            }
            else if (exp < 0)
            {
                while (exp < 0)
                {
                    int bit = 512;
                    for (int i = 0; i < 10; i++, bit >>= 1)
                    {
                        if (exp <= -bit)
                        {
                            d *= PowersTable.InvPowers[i];
                            exp += bit;
                        }
                    }
                }
            }
            return negative ? -d : d;
        }


        public static double Max(double a, double b)
        {
            return Math.Max(a, b);
        }

        public static double Min(double a, double b)
        {
            return Math.Min(a, b);
        }

        public static double Sum(double a, double b)
        {
            return a + b;
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
            (System.Double.TryParse
                (
                    s,
                    System.Globalization.NumberStyles.Float,
                    System.Globalization.CultureInfo.InvariantCulture,
                    out result
                )
            )
            {
                return result;
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

        public static int Compare(double x, double y)
        {
            if (x < y)
            {
                return -1;
            }
            if (x > y)
            {
                return 1;
            }
            if (x == y)
            {
                return x == 0 ? Double.Compare(1 / x, 1 / y) : 0;
            }

            if (IsNaN(x))
            {
                if (IsNaN(y))
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                return -1;
            }
        }

        public override double DoubleValue()
        {
            return this.value;
        }

        public override float FloatValue()
        {
            return (float)this.value;
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
