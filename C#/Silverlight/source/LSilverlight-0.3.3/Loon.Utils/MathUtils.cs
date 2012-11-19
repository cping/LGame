using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Loon.Core;

namespace Loon.Utils
{
    public class MathUtils
    {

        public const float PI_OVER2 = 1.5708f;

        public const float PI_OVER4 = 0.785398f;

        private const int BIG_ENOUGH_INT = 16 * 1024;

        private const double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;

        private const double CEIL = 0.9999999d;

        static private readonly double BIG_ENOUGH_CEIL = BitConverter.Int64BitsToDouble(BitConverter.DoubleToInt64Bits(BIG_ENOUGH_INT + 1) - 1);

        private const double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

        private const int ATAN2_BITS = 7;

        private const int ATAN2_BITS2 = ATAN2_BITS << 1;

        private const int ATAN2_MASK = ~(-1 << ATAN2_BITS2);

        private const int ATAN2_COUNT = ATAN2_MASK + 1;

        static private readonly int ATAN2_DIM = (int)Math.Sqrt(ATAN2_COUNT);

        static private readonly float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

        static private readonly float[] atan2 = new float[ATAN2_COUNT];

        public const float PI = 3.1415927f;

        public const float TWO_PI = 6.28319f;

        private const int SIN_BITS = 13;

        private const int SIN_MASK = ~(-1 << SIN_BITS);

        private const int SIN_COUNT = SIN_MASK + 1;

        private const float radFull = PI * 2;

        private const float degFull = 360;

        private const float radToIndex = SIN_COUNT / radFull;

        private const float degToIndex = SIN_COUNT / degFull;

        public const float RAD_TO_DEG = 180.0f / PI;

        public const float DEG_TO_RAD = PI / 180.0f;

        public static readonly float[] sin = new float[SIN_COUNT];

        public static readonly float[] cos = new float[SIN_COUNT];

        public const int ZERO_FIXED = 0;

        public const int ONE_FIXED = 1 << 16;

        public static readonly int ONE_HALF_FIXED = FromFloat(0.5f);

        public const double EPSILON = 2.220446049250313E-16d;

        public static readonly int EPSILON_FIXED = FromFloat(0.002f);

        public const int PI_FIXED = 205887;

        public const int PI_OVER_2_FIXED = PI_FIXED / 2;

        public const int E_FIXED = 178145;

        public const int HALF_FIXED = 2 << 15;

        public static double Distance(Point p1, Point p2)
        {
            int dx = p1.X - p2.X;
            int dy = p1.Y - p2.Y;

            return Math.Sqrt(dx * dx + dy * dy);
        }

        public static Rectangle Sum(Rectangle r1, Rectangle r2)
        {
            return new Rectangle(r1.X + r2.X, r1.Y + r2.Y, r1.Width + r2.Width, r1.Height + r2.Height);
        }

        public static Point Sum(Point p1, Point p2)
        {
            return new Point(p1.X + p2.X, p1.Y + p2.Y);
        }

        public static Point Difference(Point p1, Point p2)
        {
            return new Point(p1.X - p2.X, p1.Y - p2.Y);
        }

        public static Loon.Core.Geom.RectBox Sum(Loon.Core.Geom.RectBox d1, Loon.Core.Geom.RectBox d2)
        {
            return new Loon.Core.Geom.RectBox(0, 0, d1.width + d2.width, d1.height + d2.height);
        }

        public static Rectangle Translate(Rectangle r, int x, int y)
        {
            return new Rectangle(r.X + x, r.Y + y, r.Width, r.Height);
        }

        public static Point Translate(Point p, int x, int y)
        {
            return new Point(p.X + x, p.Y + y);
        }

        public static Vector2 Translate(Vector2 v, int x, int y)
        {
            return new Vector2(v.X + x, v.Y + y);
        }

        public static Rectangle ConstructRectangle(Point p, Loon.Core.Geom.RectBox d)
        {
            return new Rectangle(p.X, p.Y, d.width, d.height);
        }

        public static Rectangle Scale(Rectangle rect, double scale)
        {
            return new Rectangle((int)(rect.X * scale), (int)(rect.Y * scale),
                (int)(rect.Width * scale),
                (int)(rect.Height * scale));
        }

        public static Point Scale(Point p, double scale)
        {
            return new Point((int)(p.X * scale), (int)(p.Y * scale));
        }

        public static Vector2 Scale(Vector2 v, double scale)
        {
            return new Vector2((float)(v.X * scale), (float)(v.Y * scale));
        }

        public static int ToInt(int x)
        {
            return x >> 16;
        }

        public static double ToDouble(int x)
        {
            return (double)x / ONE_FIXED;
        }

        public static float ToFloat(int x)
        {
            return (float)x / ONE_FIXED;
        }

        public static int FromInt(int x)
        {
            return x << 16;
        }

        public static int FromFloat(float x)
        {
            return (int)(x * ONE_FIXED);
        }

        public static int FromDouble(double x)
        {
            return (int)(x * ONE_FIXED);
        }

        public static int Mul(int x, int y)
        {
            long z = (long)x * (long)y;
            return ((int)(z >> 16));
        }

        public static int Mid(int i, int min, int max)
        {
            return MathUtils.Max(i, MathUtils.Min(min, max));
        }

        public static int Div(int x, int y)
        {
            long z = (((long)x) << 32);
            return (int)((z / y) >> 16);
        }

        public static double Sqrt(double n)
        {
            return Math.Sqrt(n);
        }

        public static int Sqrt(int n)
        {
            int s = (n + 65536) >> 1;
            for (int i = 0; i < 8; i++)
            {
                s = (s + Div(n, s)) >> 1;
            }
            return s;
        }

        public static double Round(double n)
        {
            return Math.Round(n);
        }

        public static int Round(int n)
        {
            if (n > 0)
            {
                if ((n & 0x8000) != 0)
                {
                    return (((n + 0x10000) >> 16) << 16);
                }
                else
                {
                    return (((n) >> 16) << 16);
                }
            }
            else
            {
                int k;
                n = -n;
                if ((n & 0x8000) != 0)
                {
                    k = (((n + 0x10000) >> 16) << 16);
                }
                else
                {
                    k = (((n) >> 16) << 16);
                }
                return -k;
            }
        }

        public static bool Equal(int a, int b)
        {
            if (a > b)
                return a - b <= EPSILON_FIXED;
            else
                return b - a <= EPSILON_FIXED;
        }

        internal const int SK1 = 498;

        internal const int SK2 = 10882;

        public static int Sin(int f)
        {
            int sign = 1;
            if ((f > PI_OVER_2_FIXED) && (f <= PI_FIXED))
            {
                f = PI_FIXED - f;
            }
            else if ((f > PI_FIXED) && (f <= (PI_FIXED + PI_OVER_2_FIXED)))
            {
                f = f - PI_FIXED;
                sign = -1;
            }
            else if (f > (PI_FIXED + PI_OVER_2_FIXED))
            {
                f = (PI_FIXED << 1) - f;
                sign = -1;
            }
            int sqr = Mul(f, f);
            int result = SK1;
            result = Mul(result, sqr);
            result -= SK2;
            result = Mul(result, sqr);
            result += ONE_FIXED;
            result = Mul(result, f);
            return sign * result;
        }

        internal const int CK1 = 2328;

        internal const int CK2 = 32551;

        private static double ReduceSinAngle(double radians)
        {
            radians %= System.Math.PI * 2.0d;
            if (Math.Abs(radians) > System.Math.PI)
            {
                radians = radians - (System.Math.PI * 2.0d);
            }
            if (Math.Abs(radians) > System.Math.PI / 2)
            {
                radians = System.Math.PI - radians;
            }
            return radians;
        }

        public static double Sin(double radians)
        {
            radians = ReduceSinAngle(radians);
            if (Math.Abs(radians) <= System.Math.PI / 4)
            {
                return System.Math.Sin(radians);
            }
            else
            {
                return System.Math.Cos(System.Math.PI / 2 - radians);
            }
        }

        public static double Cos(double radians)
        {
            return Sin(radians + System.Math.PI / 2);
        }

        public static int Cos(int f)
        {
            int sign = 1;
            if ((f > PI_OVER_2_FIXED) && (f <= PI_FIXED))
            {
                f = PI_FIXED - f;
                sign = -1;
            }
            else if ((f > PI_OVER_2_FIXED) && (f <= (PI_FIXED + PI_OVER_2_FIXED)))
            {
                f = f - PI_FIXED;
                sign = -1;
            }
            else if (f > (PI_FIXED + PI_OVER_2_FIXED))
            {
                f = (PI_FIXED << 1) - f;
            }
            int sqr = Mul(f, f);
            int result = CK1;
            result = Mul(result, sqr);
            result -= CK2;
            result = Mul(result, sqr);
            result += ONE_FIXED;
            return result * sign;
        }

        internal const int TK1 = 13323;

        internal const int TK2 = 20810;

        public static int Tan(int f)
        {
            int sqr = Mul(f, f);
            int result = TK1;
            result = Mul(result, sqr);
            result += TK2;
            result = Mul(result, sqr);
            result += ONE_FIXED;
            result = Mul(result, f);
            return result;
        }

        public static int Atan(int f)
        {
            int sqr = Mul(f, f);
            int result = 1365;
            result = Mul(result, sqr);
            result -= 5579;
            result = Mul(result, sqr);
            result += 11805;
            result = Mul(result, sqr);
            result -= 21646;
            result = Mul(result, sqr);
            result += 65527;
            result = Mul(result, f);
            return result;
        }

        internal const int AS1 = -1228;

        internal const int AS2 = 4866;

        internal const int AS3 = 13901;

        internal const int AS4 = 102939;

        public static int Asin(int f)
        {
            int fRoot = Sqrt(ONE_FIXED - f);
            int result = AS1;
            result = Mul(result, f);
            result += AS2;
            result = Mul(result, f);
            result -= AS3;
            result = Mul(result, f);
            result += AS4;
            result = PI_OVER_2_FIXED - (Mul(fRoot, result));
            return result;
        }

        public static int Acos(int f)
        {
            int fRoot = Sqrt(ONE_FIXED - f);
            int result = AS1;
            result = Mul(result, f);
            result += AS2;
            result = Mul(result, f);
            result -= AS3;
            result = Mul(result, f);
            result += AS4;
            result = Mul(fRoot, result);
            return result;
        }

        static internal int[] log2arr = { 26573, 14624, 7719, 3973, 2017, 1016, 510, 256,
				128, 64, 32, 16, 8, 4, 2, 1, 0, 0, 0 };

        static internal int[] lnscale = { 0, 45426, 90852, 136278, 181704, 227130, 272557,
				317983, 363409, 408835, 454261, 499687, 545113, 590539, 635965,
				681391, 726817 };

        public static int Ln(int x)
        {
            int shift = 0;
            while (x > 1 << 17)
            {
                shift++;
                x >>= 1;
            }
            int g = 0;
            int d = HALF_FIXED;
            for (int i = 1; i < 16; i++)
            {
                if (x > (ONE_FIXED + d))
                {
                    x = Div(x, (ONE_FIXED + d));
                    g += log2arr[i - 1];
                }
                d >>= 1;
            }
            return g + lnscale[shift];
        }

        static public float Tan(float angle)
        {
            return (float)System.Math.Tan(angle);
        }

        static public float Asin(float value_ren)
        {
            return (float)System.Math.Asin(value_ren);
        }

        static public float Acos(float value_ren)
        {
            return (float)System.Math.Acos(value_ren);
        }

        static public float Atan(float value_ren)
        {
            return (float)System.Math.Atan(value_ren);
        }

        static public float Mag(float a, float b)
        {
            return (float)Math.Sqrt(a * a + b * b);
        }

        static public float Mag(float a, float b, float c)
        {
            return (float)Math.Sqrt(a * a + b * b + c * c);
        }

        static public float Dist(float x1, float y1, float x2, float y2)
        {
            return Sqrt(Sq(x2 - x1) + Sq(y2 - y1));
        }

        static public float Dist(float x1, float y1, float z1, float x2,
                float y2, float z2)
        {
            return Sqrt(Sq(x2 - x1) + Sq(y2 - y1) + Sq(z2 - z1));
        }

        static public double Abs(double n)
        {
            return Math.Abs(n);
        }

        static public float Abs(float n)
        {
            return (n < 0) ? -n : n;
        }

        static public int Abs(int n)
        {
            return (n < 0) ? -n : n;
        }

        static public float Sq(float a)
        {
            return a * a;
        }

        static public float Sqrt(float a)
        {
            return (float)Math.Sqrt(a);
        }

        static public float Log(float a)
        {
            return (float)Math.Log(a);
        }

        static public float Exp(float a)
        {
            return (float)Math.Exp(a);
        }

        static public float Pow(float a, float b)
        {
            return (float)Math.Pow(a, b);
        }

        static public int Max(int a, int b)
        {
            return (a > b) ? a : b;
        }

        static public float Max(float a, float b)
        {
            return (a > b) ? a : b;
        }

        static public long Max(long a, long b)
        {
            return (a > b) ? a : b;
        }

        static public int Max(int a, int b, int c)
        {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }

        static public float Max(float a, float b, float c)
        {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }

        static public float Min(float a, float b)
        {
            return (a <= b) ? a : b;
        }

        public static int Min(int a, int b)
        {
            return (a <= b) ? a : b;
        }

        static public float Norm(float value_ren, float start, float stop)
        {
            return (value_ren - start) / (stop - start);
        }

        static public float Map(float value_ren, float istart, float istop,
                float ostart, float ostop)
        {
            return ostart + (ostop - ostart)
                    * ((value_ren - istart) / (istop - istart));
        }

        static public float Degrees(float radians)
        {
            return radians * MathUtils.RAD_TO_DEG;
        }

        static public float Radians(float degrees)
        {
            return degrees * MathUtils.DEG_TO_RAD;
        }

        public static float Sin(float rad)
        {
            return sin[(int)(rad * radToIndex) & SIN_MASK];
        }

        public static float Cos(float rad)
        {
            return cos[(int)(rad * radToIndex) & SIN_MASK];
        }

        public static float SinDeg(float deg)
        {
            return sin[(int)(deg * degToIndex) & SIN_MASK];
        }

        public static float CosDeg(float deg)
        {
            return cos[(int)(deg * degToIndex) & SIN_MASK];
        }

        public static double Atan2(double y, double x)
        {
            if (y == 0.0D && x == 0.0D)
            {
                return System.Math.Atan2(0.0D, 1.0D);
            }
            else
            {
                return System.Math.Atan2(y, x);
            }
        }

        public static float Atan2(float y, float x)
        {
            float add, mul;
            if (x < 0)
            {
                if (y < 0)
                {
                    y = -y;
                    mul = 1;
                }
                else
                    mul = -1;
                x = -x;
                add = -3.141592653f;
            }
            else
            {
                if (y < 0)
                {
                    y = -y;
                    mul = -1;
                }
                else
                    mul = 1;
                add = 0;
            }
            float invDiv = 1 / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);
            int xi = (int)(x * invDiv);
            int yi = (int)(y * invDiv);
            return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
        }

        public static float RadToDeg(float rad)
        {
            return RAD_TO_DEG * rad;
        }

        public static int BringToBounds(int minValue, int maxValue, int v)
        {
            return Math.Max(minValue, Math.Min(maxValue, v));
        }

        public static float BringToBounds(float minValue, float maxValue, float v)
        {
            return Math.Max(minValue, Math.Min(maxValue, v));
        }

        public static int Random(int range)
        {
            return LSystem.random.Next(range + 1);
        }

        public static int Random(int start, int end)
        {
            return start + LSystem.random.Next(end - start + 1);
        }

        public static bool RandomBoolean()
        {
            return (LSystem.random.Next(0, 1) == 1 ? true : false);
        }

        public static float Random()
        {
            return (float)LSystem.random.NextDouble();
        }

        public static float Random(float range)
        {
            return (float)LSystem.random.NextDouble() * range;
        }

        public static float Random(float start, float end)
        {
            return start + (float)LSystem.random.NextDouble() * (end - start);
        }

        public static int Floor(float x)
        {
            return (int)(x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
        }

        public static int FloorPositive(float x)
        {
            return (int)x;
        }

        public static int Ceil(float x)
        {
            return (int)(x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
        }

        public static int CeilPositive(float x)
        {
            return (int)(x + CEIL);
        }

        public static int Round(float x)
        {
            return (int)(x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
        }

        public static int RoundPositive(float x)
        {
            return (int)(x + 0.5f);
        }

        public static float Barycentric(float value1, float value2, float value3,
                float amount1, float amount2)
        {
            return value1 + (value2 - value1) * amount1 + (value3 - value1)
                    * amount2;
        }

        public static float CatmullRom(float value1, float value2, float value3,
                float value4, float amount)
        {
            double amountSquared = amount * amount;
            double amountCubed = amountSquared * amount;
            return (float)(0.5d * (2.0d * value2 + (value3 - value1) * amount
                    + (2.0d * value1 - 5.0d * value2 + 4.0d * value3 - value4)
                    * amountSquared + (3.0d * value2 - value1 - 3.0d * value3 + value4)
                    * amountCubed));
        }

        public static float Clamp(float value_ren, float min, float max)
        {
            value_ren = (value_ren > max) ? max : value_ren;
            value_ren = (value_ren < min) ? min : value_ren;
            return value_ren;
        }

        public static float Distance(float value1, float value2)
        {
            return Math.Abs(value1 - value2);
        }

        public static float Hermite(float value1, float tangent1, float value2,
                float tangent2, float amount)
        {
            double v1 = value1, v2 = value2, t1 = tangent1, t2 = tangent2, s = amount, result;
            double sCubed = s * s * s;
            double sSquared = s * s;

            if (amount == 0f)
            {
                result = value1;
            }
            else if (amount == 1f)
            {
                result = value2;
            }
            else
            {
                result = (2 * v1 - 2 * v2 + t2 + t1) * sCubed
                        + (3 * v2 - 3 * v1 - 2 * t1 - t2) * sSquared + t1 * s + v1;
            }
            return (float)result;
        }

        public static float Lerp(float value1, float value2, float amount)
        {
            return value1 + (value2 - value1) * amount;
        }

        public static float SmoothStep(float value1, float value2, float amount)
        {
            float result = Clamp(amount, 0f, 1f);
            result = Hermite(value1, 0f, value2, 0f, result);
            return result;
        }

        public static float ToDegrees(float radians)
        {
            return (float)(radians * 57.295779513082320876798154814105d);
        }

        public static float ToRadians(float degrees)
        {
            return (float)(degrees * 0.017453292519943295769236907684886d);
        }

        public static float WrapAngle(float angle)
        {
            angle = (float)System.Math.IEEERemainder((double)angle, 6.2831854820251465d);
            if (angle <= -3.141593f)
            {
                angle += 6.283185f;
                return angle;
            }
            if (angle > 3.141593f)
            {
                angle -= 6.283185f;
            }
            return angle;
        }

        static MathUtils()
        {
            for (int i = 0; i < SIN_COUNT; i++)
            {
                float a = (i + 0.5f) / SIN_COUNT * radFull;
                sin[i] = (float)System.Math.Sin(a);
                cos[i] = (float)System.Math.Cos(a);
            }
            for (int i = 0; i < 360; i += 90)
            {
                sin[(int)(i * degToIndex) & SIN_MASK] = (float)System.Math
                        .Sin(i * DEG_TO_RAD);
                cos[(int)(i * degToIndex) & SIN_MASK] = (float)System.Math
                        .Cos(i * DEG_TO_RAD);
            }
            for (int i = 0; i < ATAN2_DIM; i++)
            {
                for (int j = 0; j < ATAN2_DIM; j++)
                {
                    float x0 = (float)i / ATAN2_DIM;
                    float y0 = (float)j / ATAN2_DIM;
                    atan2[j * ATAN2_DIM + i] = (float)System.Math.Atan2(y0, x0);
                }
            }
        }
    }


}