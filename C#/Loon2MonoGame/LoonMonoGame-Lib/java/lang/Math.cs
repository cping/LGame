namespace java.lang
{
    public class Math
    {
        public const double E_f = System.Math.E;
        public const double PI_f = System.Math.PI;
        public const double E = System.Math.E;
        public const double PI = System.Math.PI;

        private const double PI_L = 1.2246467991473532e-16; // Long bits 0x3ca1a62633145c07L.    
        public static double Abs(double a)
        {
            return System.Math.Abs(a);
        }

        public static int Abs(int a)
        {
            return a < 0 ? -a : a;
        }

        public static double Acos(double a)
        {
            return System.Math.Acos(a);
        }

        public static double Asin(double a)
        {
            return System.Math.Asin(a);
        }

        public static double Atan(double a)
        {
            return System.Math.Atan(a);
        }

        public static double Atan2(double y, double x)
        {
            if (System.Double.IsNaN(x) || System.Double.IsNaN(y)) { return 0.0 / 0.0; }

            if (x == 1.0) { return System.Math.Atan(y); }
            if (x == -1.0)
            {
                double z = System.Math.Atan(System.Math.Abs(y));
                return y > 0 ? PI_f - (z - PI_L) : z - PI_L - PI_f;
            }

            if (y == Double.POSITIVE_INFINITY_JAVA)
            {
                if (x == Double.POSITIVE_INFINITY_JAVA) { return PI_f / 4; }
                else if (x == Double.NEGATIVE_INFINITY_JAVA) { return 3 * PI_f / 4; }
                else { return PI_f / 2; }
            }
            if (y == Double.NEGATIVE_INFINITY_JAVA)
            {
                if (x == Double.POSITIVE_INFINITY_JAVA) { return -PI_f / 4; }
                else if (x == Double.NEGATIVE_INFINITY_JAVA) { return -3 * PI_f / 4; }
                else { return -PI_f / 2; }
            }
            if (x == Double.POSITIVE_INFINITY_JAVA)
            {
                return (y == 0) ? y : ((y > 0) ? 0.0 : -0.0);
            }
            if (x == Double.NEGATIVE_INFINITY_JAVA)
            {
                return PI_f;
            }
            if (x == 0)
            {
                return y > 0 ? PI_f / 2.0 : (y < 0 ? -PI_f / 2.0 : 0);
            }

            return System.Math.Atan2(y, x);
        }

        public static double Ceil(double a)
        {
            return System.Math.Ceiling(a);
        }

        public static double Cos(double a)
        {
            return System.Math.Cos(a);
        }

        public static double Cosh(double a)
        {
            return System.Math.Cosh(a);
        }

        public static double Exp(double a)
        {
            return System.Math.Exp(a);
        }

        public static double Floor(double a)
        {
            return System.Math.Floor(a);
        }

        public static double Hypot(double x, double y)
        {
            return Sqrt(x * x + y * y);
        }

        public static double IEEEremainder(double f1, double f2)
        {
            return System.Math.IEEERemainder(f1, f2);
        }

        public static double Log(double a)
        {
            return System.Math.Log(a);
        }

        public static double Log10(double a)
        {
            return System.Math.Log10(a);
        }

        public static double Max(double a, double b)
        {
            return System.Math.Max(a, b);
        }

        public static double Min(double a, double b)
        {
            return System.Math.Min(a, b);
        }

        public static int Max(int a, int b)
        {
            return (a > b) ? a : b;
        }

        public static int Min(int a, int b)
        {
            return (a < b) ? a : b;
        }

        public static double Pow(double a, double b)
        {
            if (b == 0) { return 1.0; }
            return System.Math.Pow(a, b);
        }

        public static long Round(double x)
        {
            if (System.Double.IsNaN(x)) { return 0; }
            if (x >= 9.223372036854776E18) { return System.Int64.MaxValue; }
            if (x <= -9.223372036854776E18) { return System.Int64.MinValue; }

            if (x == 0.49999999999999994) { return 0; }

            if (x <= -4503599627370496.0 || 4503599627370496.0 <= x)
            {
                return (long)x;
            }

            return (long)System.Math.Floor(x + 0.5);
        }

        public static double Rint(double x)
        {
            if (x % 0.5 != 0)
            {
                return System.Math.Round(x);
            }
            else
            {
                return (System.Math.Floor(x) % 2 == 0) ? System.Math.Floor(x) : System.Math.Round(x);
            }
        }

        public static double Signum(double a)
        {
            if (System.Double.IsNaN(a)) { return 0.0 / 0.0; }
            return a == 0 ? a : System.Math.Sign(a);
        }

        public static double Sin(double a)
        {
            return System.Math.Sin(a);
        }

        public static double Sinh(double a)
        {
            return System.Math.Sinh(a);
        }

        public static double Sqrt(double a)
        {
            return System.Math.Sqrt(a);
        }

        public static double Tan(double a)
        {
            return System.Math.Tan(a);
        }

        public static double Tanh(double a)
        {
            return 1 - 2 / (Math.Exp(2 * a) + 1);
        }

        public static double ToDegrees(double angrad)
        {
            return angrad * 180.0 / System.Math.PI;
        }

        public static double ToRadians(double angdeg)
        {
            return angdeg * (System.Math.PI / 180.0);
        }

    }
}
