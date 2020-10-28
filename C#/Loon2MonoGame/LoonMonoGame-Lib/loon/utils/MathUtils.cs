using java.lang;
using loon.geom;

namespace loon.utils
{
    public partial class MathUtils
    {

        public readonly static System.Random random = new System.Random();

        public const float FLOAT_ROUNDING_ERROR = 0.000001f;

        public const int ZERO_FIXED = 0;

        public const int ONE_FIXED = 1 << 16;

        public const float EPSILON = 0.001f;

        public const float NaN = 0.0f / 0.0f;

        public const int PI_FIXED = 205887;

        public const int PI_OVER_2_FIXED = PI_FIXED / 2;

        public const int E_FIXED = 178145;

        public const int HALF_FIXED = 2 << 15;

        private readonly static string[] ZEROS = { "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000",
            "000000000", "0000000000" };

        private readonly static int[] SHIFT = { 0, 1144, 2289, 3435, 4583, 5734, 6888, 8047, 9210, 10380, 11556, 12739, 13930,
            15130, 16340, 17560, 18792, 20036, 21294, 22566, 23853, 25157, 26478, 27818, 29179, 30560, 31964, 33392,
            34846, 36327, 37837, 39378, 40951, 42560, 44205, 45889, 47615, 49385, 51202, 53070, 54991, 56970, 59009,
            61113, 63287, 65536 };

        public const float PI_OVER2 = 1.5708f;

        public const float PI_OVER4 = 0.785398f;

        public const float PHI = 0.618f;

        private const float CEIL = 0.9999999f;

        private const int BIG_ENOUGH_INT = 16384;

        private const float BIG_ENOUGH_CEIL = 16384.998f;

        private const float BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

        private const float BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;

        private const int ATAN2_BITS = 7;

        private const int ATAN2_BITS2 = ATAN2_BITS << 1;

        private const int ATAN2_MASK = ~(-1 << ATAN2_BITS2);

        private const int ATAN2_COUNT = ATAN2_MASK + 1;

        public const int ATAN2_DIM = 128;

        private const float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

        public const float PI = 3.1415927f;

        public const float TWO_PI = 6.28319f;

        public const float SQRT2 = 1.4142135f;

        private const int SIN_BITS = 13;

        private const int SIN_MASK = ~(-1 << SIN_BITS);

        private const int SIN_COUNT = SIN_MASK + 1;

        private const float RAD_FULL = PI * 2;

        private const float DEG_FULL = 360;

        private const float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;

        private const float DEG_TO_INDEX = SIN_COUNT / DEG_FULL;

        public const float RAD_TO_DEG = 180.0f / PI;

        public const float DEG_TO_RAD = PI / 180.0f;

        static class SinCosImpl
        {

            public readonly static float[] SIN_LIST = new float[SIN_COUNT];

            public readonly static float[] COS_LIST = new float[SIN_COUNT];

            static SinCosImpl()
            {
                for (int i = 0; i < SIN_COUNT; i++)
                {
                    float a = (i + 0.5f) / SIN_COUNT * RAD_FULL;
                    SIN_LIST[i] = (float)System.Math.Sin(a);
                    COS_LIST[i] = (float)System.Math.Cos(a);
                }
            }
        }

        static class Atan2Impl
        {

            public readonly static float[] TABLE = new float[ATAN2_COUNT];

            static Atan2Impl()
            {
                for (int i = 0; i < ATAN2_DIM; i++)
                {
                    for (int j = 0; j < ATAN2_DIM; j++)
                    {
                        float x0 = (float)i / ATAN2_DIM;
                        float y0 = (float)j / ATAN2_DIM;
                        TABLE[j * ATAN2_DIM + i] = (float)System.Math.Atan2(y0, x0);
                    }
                }
            }
        }

        private MathUtils() { }
        static MathUtils()
        {

        }

        public static int Ifloor(float v)
        {
            int iv = (int)v;
            return (v >= 0f || iv == v || iv == Integer.MIN_VALUE_JAVA) ? iv : (iv - 1);
        }

        public static int Iceil(float v)
        {
            int iv = (int)v;
            return (v <= 0f || iv == v || iv == Integer.MAX_VALUE_JAVA) ? iv : (iv + 1);
        }

        public static RectBox GetBounds(float x, float y, float width, float height, float rotate, RectBox result)
        {
            if (rotate == 0)
            {
                if (result == null)
                {
                    result = new RectBox(x, y, width, height);
                }
                else
                {
                    result.SetBounds(x, y, width, height);
                }
                return result;
            }
            int[] rect = GetLimit(x, y, width, height, rotate);
            if (result == null)
            {
                result = new RectBox(rect[0], rect[1], rect[2], rect[3]);
            }
            else
            {
                result.SetBounds(rect[0], rect[1], rect[2], rect[3]);
            }
            return result;
        }

        public static RectBox GetBounds(float x, float y, float width, float height, float rotate)
        {
            return GetBounds(x, y, width, height, rotate, null);
        }

        public static bool IsZero(float value, float tolerance)
        {
            return MathUtils.Abs(value) <= tolerance;
        }
        public static float Log(float a)
        {
            return (float)System.Math.Log(a);
        }

        public static float Pow(float a, float b)
        {
            return (float)System.Math.Pow(a, b);
        }

        public static bool IsEqual(float a, float b)
        {
            return MathUtils.Abs(a - b) <= FLOAT_ROUNDING_ERROR;
        }

        public static bool IsEqual(float a, float b, float tolerance)
        {
            return MathUtils.Abs(a - b) <= tolerance;
        }

        public static bool IsPowerOfTwo(int w, int h)
        {
            return (w > 0 && (w & (w - 1)) == 0 && h > 0 && (h & (h - 1)) == 0);
        }

        public static int PreviousPowerOfTwo(int value)
        {
            int power = (int)(Log(value) / Log(2));
            return (int)Pow(2, power);
        }

        public static int NextPowerOfTwo(int value)
        {
            if (value == 0)
                return 1;
            value--;
            value |= value >> 1;
            value |= value >> 2;
            value |= value >> 4;
            value |= value >> 8;
            value |= value >> 16;
            return value + 1;
        }

        public static int[] GetLimit(float x, float y, float width, float height, float rotate)
        {
            float rotation = MathUtils.ToRadians(rotate);
            float angSin = MathUtils.Sin(rotation);
            float angCos = MathUtils.Cos(rotation);
            int newW = MathUtils.Floor((width * MathUtils.Abs(angCos)) + (height * MathUtils.Abs(angSin)));
            int newH = MathUtils.Floor((height * MathUtils.Abs(angCos)) + (width * MathUtils.Abs(angSin)));
            int centerX = (int)(x + (width / 2));
            int centerY = (int)(y + (height / 2));
            int newX = (centerX - (newW / 2));
            int newY = (centerY - (newH / 2));
            return new int[] { newX, newY, newW, newH };
        }

        public static float Sin(float n, float angle, float arc, bool plus)
        {
            return plus ? n + MathUtils.Sin(angle) + arc : n - MathUtils.Sin(angle) * arc;
        }

        public static float Sin(float rad)
        {
            return SinCosImpl.SIN_LIST[(int)(rad * RAD_TO_INDEX) & SIN_MASK];
        }

        public static float SinDeg(float deg)
        {
            return SinCosImpl.SIN_LIST[(int)(deg * DEG_TO_INDEX) & SIN_MASK];
        }

        public static float Cos(float n, float angle, float arc, bool plus)
        {
            return plus ? n + MathUtils.Cos(angle) + arc : n - MathUtils.Cos(angle) * arc;
        }

        public static float Cos(float rad)
        {
            return SinCosImpl.COS_LIST[(int)(rad * RAD_TO_INDEX) & SIN_MASK];
        }

        public static float CosDeg(float deg)
        {
            return SinCosImpl.COS_LIST[(int)(deg * DEG_TO_INDEX) & SIN_MASK];
        }

        public static bool Between(float v, float min, float max)
        {
            return (v > min && v < max);
        }

        public static float ToDegrees(float radians)
        {
            return radians * RAD_TO_DEG;
        }

        public static float ToRadians(float degrees)
        {
            return degrees * DEG_TO_RAD;
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
            float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);
            int xi = (int)(x * invDiv);
            int yi = (int)(y * invDiv);
            return (Atan2Impl.TABLE[yi * ATAN2_DIM + xi] + add) * mul;
        }


        public static int Random(int range)
        {
            return random.Next(range + 1);
        }
        public static float Random()
        {
            return (float)random.NextDouble();
        }
        public static float Random(float range)
        {
            return (float)random.NextDouble() * range;
        }

        public static float Abs(float n)
        {
            return (n < 0) ? -n : n;
        }

        public static double Abs(double n)
        {
            return (n < 0) ? -n : n;
        }

        public static int Abs(int n)
        {
            return (n < 0) ? -n : n;
        }

        public static long Abs(long n)
        {
            return (n < 0) ? -n : n;
        }

        public static float Sq(float a)
        {
            return a * a;
        }

        public static float Sqrt(float a)
        {
            return (float)System.Math.Sqrt(a);
        }

        public static int SqrtInt(int n)
        {
            int s = (n + 65536) >> 1;
            for (int i = 0; i < 8; i++)
            {
                s = (s + Div(n, s)) >> 1;
            }
            return s;
        }

        public static int Div(int x, int y)
        {
            long z = (((long)x) << 32);
            return (int)((z / y) >> 16);
        }

        public static float Div(float x, float y)
        {
            long z = (((long)x) << 32);
            return (float)((z / (long)y) >> 16);
        }

        public static int Mul(int x, int y)
        {
            long z = (long)x * (long)y;
            return ((int)(z >> 16));
        }

        public static float Mul(float x, float y)
        {
            long z = (long)x * (long)y;
            return ((float)(z >> 16));
        }

        public static int MulDiv(int f1, int f2, int f3)
        {
            return (int)((long)f1 * f2 / f3);
        }

        public static long MulDiv(long f1, long f2, long f3)
        {
            return f1 * f2 / f3;
        }

        public static int Mid(int i, int min, int max)
        {
            return MathUtils.Max(i, MathUtils.Min(min, max));
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
                return a - b <= EPSILON;
            else
                return b - a <= EPSILON;
        }

        public static bool Equal(float a, float b)
        {
            if (a > b)
                return a - b <= EPSILON;
            else
                return b - a <= EPSILON;
        }

        public static int Sign(float x)
        {
            if (x > 0)
            {
                return 1;
            }
            else if (x < 0)
            {
                return -1;
            }
            return 0;
        }

        public static int RandomSign()
        {
            return (MathUtils.Random() > 0.5f) ? 1 : -1;
        }

        static int SK1 = 498;

        static int SK2 = 10882;

        public static int SinInt(int f)
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

        const int CK1 = 2328;

        const int CK2 = 32551;

        public static int CosInt(int f)
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

        const int TK1 = 13323;

        const int TK2 = 20810;

        public static int TanInt(int f)
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

        public static int AtanInt(int f)
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

        const int AS1 = -1228;

        const int AS2 = 4866;

        const int AS3 = 13901;

        const int AS4 = 102939;

        public static int AsinInt(int f)
        {
            int fRoot = SqrtInt(ONE_FIXED - f);
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

        public static int AcosInt(int f)
        {
            int fRoot = SqrtInt(ONE_FIXED - f);
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

        public static float Trunc(float x)
        {
            return x < 0f ? MathUtils.Ceil(x) : MathUtils.Floor(x);
        }

        public static float Tan(float angle)
        {
            return (float)System.Math.Tan(angle);
        }

        public static float Asin(float value)
        {
            return (float)System.Math.Asin(value);
        }

        public static float Acos(float value)
        {
            return (float)System.Math.Acos(value);
        }

        public static float Atan(float value)
        {
            return (float)System.Math.Atan(value);
        }

        public static float Mag(float a, float b)
        {
            return Sqrt(a * a + b * b);
        }

        public static float Mag(float a, float b, float c)
        {
            return Sqrt(a * a + b * b + c * c);
        }

        public static float Median(float a, float b, float c)
        {
            return (a <= b) ? ((b <= c) ? b : ((a < c) ? c : a)) : ((a <= c) ? a : ((b < c) ? c : b));
        }

        public static float Distance(float x1, float x2)
        {
            return Abs(x1 - x2);
        }

        public static float Distance(float x1, float y1, float x2, float y2)
        {
            return Dist(x1, y1, x2, y2);
        }

        public static float Dist(float x1, float y1)
        {
            return Abs(x1 - y1);
        }

        public static float Dist(float x1, float y1, float x2, float y2)
        {
            return Sqrt(Sq(x2 - x1) + Sq(y2 - y1));
        }

        public static float Dist(float x1, float y1, float z1, float x2, float y2, float z2)
        {
            return Sqrt(Sq(x2 - x1) + Sq(y2 - y1) + Sq(z2 - z1));
        }

        public static float DistSquared(float x1, float y1, float x2, float y2)
        {
            return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        }

        public static float DistRectPoint(float px, float py, float rx, float ry, float rw, float rh)
        {
            if (px >= rx && px <= rx + rw)
            {
                if (py >= ry && py <= ry + rh)
                {
                    return 0f;
                }
                if (py > ry)
                {
                    return py - (ry + rh);
                }
                return ry - py;
            }
            if (py >= ry && py <= ry + rh)
            {
                if (px > rx)
                {
                    return px - (rx + rw);
                }
                return rx - px;
            }
            if (px > rx)
            {
                if (py > ry)
                {
                    return Dist(px, py, rx + rw, ry + rh);
                }
                return Dist(px, py, rx + rw, ry);
            }
            if (py > ry)
            {
                return Dist(px, py, rx, ry + rh);
            }
            return Dist(px, py, rx, ry);
        }

        public static float DistRects(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2)
        {
            if (x1 < x2 + w2 && x2 < x1 + w1)
            {
                if (y1 < y2 + h2 && y2 < y1 + h1)
                {
                    return 0f;
                }
                if (y1 > y2)
                {
                    return y1 - (y2 + h2);
                }
                return y2 - (y1 + h1);
            }
            if (y1 < y2 + h2 && y2 < y1 + h1)
            {
                if (x1 > x2)
                {
                    return x1 - (x2 + w2);
                }
                return x2 - (x1 + w1);
            }
            if (x1 > x2)
            {
                if (y1 > y2)
                {
                    return Dist(x1, y1, (x2 + w2), (y2 + h2));
                }
                return Dist(x1, y1 + h1, x2 + w2, y2);
            }
            if (y1 > y2)
            {
                return Dist(x1 + w1, y1, x2, y2 + h2);
            }
            return Dist(x1 + w1, y1 + h1, x2, y2);
        }

        public static float Exp(float a)
        {
            return (float)System.Math.Exp(a);
        }

        public static int Max(int a, int b)
        {
            return (a > b) ? a : b;
        }

        public static float Max(float a, float b)
        {
            return (a > b) ? a : b;
        }

        public static long Max(long a, long b)
        {
            return (a > b) ? a : b;
        }

        public static int Max(int a, int b, int c)
        {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }

        public static float Max(float a, float b, float c)
        {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }

        public static int Max(int[] numbers)
        {
            int max = Integer.MIN_VALUE_JAVA;
            for (int i = 0; i < numbers.Length; i++)
            {
                if (numbers[i] > max)
                {
                    max = numbers[i];
                }
            }
            return max;
        }

        public static float Max(float[] numbers)
        {
            float max = Integer.MIN_VALUE_JAVA;
            for (int i = 0; i < numbers.Length; i++)
            {
                if (numbers[i] > max)
                {
                    max = numbers[i];
                }
            }
            return max;
        }

        public static int Min(int a, int b, int c)
        {
            return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
        }

        public static float Min(float a, float b, float c)
        {
            return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
        }

        public static float Min(float a, float b)
        {
            return (a <= b) ? a : b;
        }

        public static int Min(int a, int b)
        {
            return (a <= b) ? a : b;
        }

        public static int Min(int[] numbers)
        {
            int min = Integer.MAX_VALUE_JAVA;
            for (int i = 0; i < numbers.Length; i++)
            {
                if (numbers[i] < min)
                {
                    min = numbers[i];
                }
            }
            return min;
        }

        public static float Min(float[] numbers)
        {
            float min = Integer.MAX_VALUE_JAVA;
            for (int i = 0; i < numbers.Length; i++)
            {
                if (numbers[i] < min)
                {
                    min = numbers[i];
                }
            }
            return min;
        }

        public static float Mix(float x, float y, float m)
        {
            return x * (1 - m) + y * m;
        }

        public static int Mix(int x, int y, float m)
        {
            return MathUtils.Round(x * (1 - m) + y * m);
        }

        public static float Norm(float value, float start, float stop)
        {
            return (value - start) / (stop - start);
        }

        public static float Map(float value, float istart, float istop, float ostart, float ostop)
        {
            return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
        }

        public static bool NextBoolean()
        {
            return RandomBoolean();
        }

        public static int NextInt(int range)
        {
            return range <= 0 ? 0 : random.Next(range);
        }

        public static int NextInt(int start, int end)
        {
            return end <= 0 ? 0 : start + random.Next(end - start);
        }

        public static int Random(int start, int end)
        {
            return start + random.Next(end - start + 1);
        }
        public static bool RandomBoolean()
        {
            return (random.Next(0, 1) == 1 ? true : false);
        }

        public static float Random(float start, float end)
        {
            return start + (float)random.NextDouble() * (end - start);
        }

        public static float RandomFloor(float start, float end)
        {
            return MathUtils.Floor(Random(start, end));
        }

        public static int Floor(float x)
        {
            return (int)(x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
        }

        public static long Floor(double x)
        {
            return (long)(x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
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

        public static float Barycentric(float value1, float value2, float value3, float amount1, float amount2)
        {
            return value1 + (value2 - value1) * amount1 + (value3 - value1) * amount2;
        }

        public static float CatmullRom(float value1, float value2, float value3, float value4, float amount)
        {
            double amountSquared = amount * amount;
            double amountCubed = amountSquared * amount;
            return (float)(0.5 * (2.0 * value2 + (value3 - value1) * amount
                    + (2.0 * value1 - 5.0 * value2 + 4.0 * value3 - value4) * amountSquared
                    + (3.0 * value2 - value1 - 3.0 * value3 + value4) * amountCubed));
        }

        public static int Clamp(int value, int min, int max)
        {
            value = (value > max) ? max : value;
            value = (value < min) ? min : value;
            return value;
        }

        public static float Clamp(float value, float min, float max)
        {
            value = (value > max) ? max : value;
            value = (value < min) ? min : value;
            return value;
        }

        public static double Clamp(double value, double min, double max)
        {
            value = (value > max) ? max : value;
            value = (value < min) ? min : value;
            return value;
        }

        public static long Clamp(long value, long min, long max)
        {
            value = (value > max) ? max : value;
            value = (value < min) ? min : value;
            return value;
        }

        public static float Clamp(float v)
        {
            return v < 0f ? 0f : (v > 1f ? 1f : v);
        }

        public static float ClampAngle(float v)
        {
            float value = v % PI * 2;
            if (value < 0)
            {
                value += PI * 2;
            }
            return value;
        }

        public static Vector2f ClampInRect(XY v, float x, float y, float width, float height)
        {
            return ClampInRect(v, x, y, width, height, 0f);
        }

        public static Vector2f ClampInRect(XY v, float x, float y, float width, float height, float padding)
        {
            if (v == null)
            {
                return new Vector2f(0f, 0f);
            }
            Vector2f obj = new Vector2f();
            obj.x = Clamp(v.GetX(), x + padding, x + width - padding);
            obj.y = Clamp(v.GetY(), y + padding, y + height - padding);
            return obj;
        }

        public static float Hermite(float value1, float tangent1, float value2, float tangent2, float amount)
        {
            float v1 = value1, v2 = value2, t1 = tangent1, t2 = tangent2, s = amount, result;
            float sCubed = s * s * s;
            float sSquared = s * s;

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
                result = (2 * v1 - 2 * v2 + t2 + t1) * sCubed + (3 * v2 - 3 * v1 - 2 * t1 - t2) * sSquared + t1 * s + v1;
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

        public static float WrapAngle(float angle)
        {
            angle = (float)IEEEremainder((double)angle, 6.2831854820251465d);
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

        public static double Signum(double d)
        {
            return d > 0 ? 1 : d < -0 ? -1 : d;
        }

        public static float Signum(float d)
        {
            return d > 0 ? 1 : d < -0 ? -1 : d;
        }

        public static bool IsNan(float v)
        {
            return float.IsNaN(v);
        }

        public static bool IsNan(double v)
        {
            return double.IsNaN(v);
        }
        public static bool IsNan(string str)
        {
            if (StringUtils.IsEmpty(str))
            {
                return false;
            }
            char[] chars = str.ToCharArray();
            int sz = chars.Length;
            bool hasExp = false;
            bool hasDecPoint = false;
            bool allowSigns = false;
            bool foundDigit = false;
            int start = (chars[0] == '-') ? 1 : 0;
            int i;
            if (sz > start + 1)
            {
                if (chars[start] == '0' && chars[start + 1] == 'x')
                {
                    i = start + 2;
                    if (i == sz)
                    {
                        return false;
                    }
                    for (; i < chars.Length; i++)
                    {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
                                && (chars[i] < 'A' || chars[i] > 'F'))
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
            sz--;
            i = start;
            while (i < sz || (i < sz + 1 && allowSigns && !foundDigit))
            {
                if (chars[i] >= '0' && chars[i] <= '9')
                {
                    foundDigit = true;
                    allowSigns = false;
                }
                else if (chars[i] == '.')
                {
                    if (hasDecPoint || hasExp)
                    {
                        return false;
                    }
                    hasDecPoint = true;
                }
                else if (chars[i] == 'e' || chars[i] == 'E')
                {
                    if (hasExp)
                    {
                        return false;
                    }
                    if (!foundDigit)
                    {
                        return false;
                    }
                    hasExp = true;
                    allowSigns = true;
                }
                else if (chars[i] == '+' || chars[i] == '-')
                {
                    if (!allowSigns)
                    {
                        return false;
                    }
                    allowSigns = false;
                    foundDigit = false;
                }
                else
                {
                    return false;
                }
                i++;
            }
            if (i < chars.Length)
            {
                if (chars[i] >= '0' && chars[i] <= '9')
                {
                    return true;
                }
                if (chars[i] == 'e' || chars[i] == 'E')
                {
                    return false;
                }
                if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F'))
                {
                    return foundDigit;
                }
                if (chars[i] == 'l' || chars[i] == 'L')
                {
                    return foundDigit && !hasExp;
                }
                return false;
            }
            return !allowSigns && foundDigit;
        }
        public static bool IsNumber(string num)
        {
            if (StringUtils.IsEmpty(num))
            {
                return false;
            }
            return IsNan(num);
        }

        public static bool IsNumber(CharSequence num)
        {
            if (StringUtils.IsEmpty(num))
            {
                return false;
            }
            return IsNan(num.ToString());
        }

        public static double IEEEremainder(double f1, double f2)
        {
            double r = Abs(f1 % f2);
            if (IsNan(r) || r == f2 || r <= Abs(f2) / 2.0)
            {
                return r;
            }
            else
            {
                return Signum(f1) * (r - f2);
            }
        }

        public static double NormalizeLon(double lon)
        {
            while ((lon < -180d) || (lon > 180d))
            {
                lon = IEEEremainder(lon, 360d);
            }
            return lon;
        }

        public static int Sum(int[] values)
        {
            int sum = 0;
            for (int i = values.Length - 1; i >= 0; i--)
            {
                sum += values[i];
            }
            return sum;
        }

        public static void ArraySumInternal(int[] values)
        {
            int valueCount = values.Length;
            for (int i = 1; i < valueCount; i++)
            {
                values[i] = values[i - 1] + values[i];
            }
        }

        public static void ArraySumInternal(long[] values)
        {
            int valueCount = values.Length;
            for (int i = 1; i < valueCount; i++)
            {
                values[i] = values[i - 1] + values[i];
            }
        }

        public static void ArraySumInternal(long[] values, long factor)
        {
            values[0] = values[0] * factor;
            int valueCount = values.Length;
            for (int i = 1; i < valueCount; i++)
            {
                values[i] = values[i - 1] + values[i] * factor;
            }
        }

        public static void ArraySumInto(long[] values, long[] targetValues, long factor)
        {
            targetValues[0] = values[0] * factor;
            int valueCount = values.Length;
            for (int i = 1; i < valueCount; i++)
            {
                targetValues[i] = targetValues[i - 1] + values[i] * factor;
            }
        }

        public static float ArraySum(float[] values)
        {
            float sum = 0;
            int valueCount = values.Length;
            for (int i = 0; i < valueCount; i++)
            {
                sum += values[i];
            }
            return sum;
        }

        public static float ArrayAverage(float[] values)
        {
            return MathUtils.ArraySum(values) / values.Length;
        }

        public static float[] ScaleAroundCenter(float[] vertices, float scaleX, float scaleY,
                 float scaleCenterX, float scaleCenterY)
        {
            if (scaleX != 1 || scaleY != 1)
            {
                for (int i = vertices.Length - 2; i >= 0; i -= 2)
                {
                    vertices[i] = scaleCenterX + (vertices[i] - scaleCenterX) * scaleX;
                    vertices[i + 1] = scaleCenterY + (vertices[i + 1] - scaleCenterY) * scaleY;
                }
            }
            return vertices;
        }

        public static bool IsInBounds(int minValue, int maxValue, int val)
        {
            return val >= minValue && val <= maxValue;
        }

        public static bool IsInBounds(float minValue, float maxValue, float val)
        {
            return val >= minValue && val <= maxValue;
        }

        public static int Round(int div1, int div2)
        {
            int remainder = div1 % div2;
            if (MathUtils.Abs(remainder) * 2 <= MathUtils.Abs(div2))
            {
                return div1 / div2;
            }
            else if (div1 * div2 < 0)
            {
                return div1 / div2 - 1;
            }
            else
            {
                return div1 / div2 + 1;
            }
        }

        public static float Round(float div1, float div2)
        {
            float remainder = div1 % div2;
            if (MathUtils.Abs(remainder) * 2 <= MathUtils.Abs(div2))
            {
                return div1 / div2;
            }
            else if (div1 * div2 < 0)
            {
                return div1 / div2 - 1;
            }
            else
            {
                return div1 / div2 + 1;
            }
        }

        public static long Round(long div1, long div2)
        {
            long remainder = div1 % div2;
            if (MathUtils.Abs(remainder) * 2 <= MathUtils.Abs(div2))
            {
                return div1 / div2;
            }
            else if (div1 * div2 < 0)
            {
                return div1 / div2 - 1;
            }
            else
            {
                return div1 / div2 + 1;
            }
        }

        public static int ToShift(int angle)
        {
            if (angle <= 45)
            {
                return SHIFT[angle];
            }
            else if (angle >= 315)
            {
                return -SHIFT[360 - angle];
            }
            else if (angle >= 135 && angle <= 180)
            {
                return -SHIFT[180 - angle];
            }
            else if (angle >= 180 && angle <= 225)
            {
                return SHIFT[angle - 180];
            }
            else if (angle >= 45 && angle <= 90)
            {
                return SHIFT[90 - angle];
            }
            else if (angle >= 90 && angle <= 135)
            {
                return -SHIFT[angle - 90];
            }
            else if (angle >= 225 && angle <= 270)
            {
                return SHIFT[270 - angle];
            }
            else
            {
                return -SHIFT[angle - 270];
            }
        }

        public static float BezierAt(float a, float b, float c, float d, float t)
        {
            return (MathUtils.Pow(1 - t, 3) * a + 3 * t * (MathUtils.Pow(1 - t, 2)) * b
                    + 3 * MathUtils.Pow(t, 2) * (1 - t) * c + MathUtils.Pow(t, 3) * d);
        }

        public static int ParseUnsignedInt(string s)
        {
            return ParseUnsignedInt(s, 10);
        }

        public static int ParseUnsignedInt(string s, int radix)
        {
            if (s == null)
            {
                throw new LSysException("null");
            }
            int len = s.Length();
            if (len > 0)
            {
                char firstChar = s.CharAt(0);
                if (firstChar == '-')
                {
                    throw new LSysException("on unsigned string %s.");
                }
                else
                {
                    if (len <= 5 || (radix == 10 && len <= 9))
                    {
                        return Integer.ParseInt(s, radix);
                    }
                    else
                    {
                        long ell = Long.ParseLong(s, radix);
                        if (((ulong)ell & 0xffff_ffff_0000_0000L) == 0)
                        {
                            return (int)ell;
                        }
                        else
                        {
                            throw new LSysException("range of unsigned int.");
                        }
                    }
                }
            }
            else
            {
                throw new LSysException(s);
            }
        }

        public static int NumberOfTrailingZeros(long i)
        {
            int x, y;
            if (i == 0)
            {
                return 64;
            }
            int n = 63;
            y = (int)i;
            if (y != 0)
            {
                n = n - 32;
                x = y;
            }
            else
                x = (int)Abs(i >> 32);
            y = x << 16;
            if (y != 0)
            {
                n = n - 16;
                x = y;
            }
            y = x << 8;
            if (y != 0)
            {
                n = n - 8;
                x = y;
            }
            y = x << 4;
            if (y != 0)
            {
                n = n - 4;
                x = y;
            }
            y = x << 2;
            if (y != 0)
            {
                n = n - 2;
                x = y;
            }
            return n - (Abs((x << 1) >> 31));

        }

        public static float MaxAbs(float x, float y)
        {
            return MathUtils.Abs(x) >= MathUtils.Abs(y) ? x : y;
        }

        public static float MinAbs(float x, float y)
        {
            return MathUtils.Abs(x) <= MathUtils.Abs(y) ? x : y;
        }

        public static float LerpCut(float progress, float progressLowCut, float progressHighCut, float fromValue,
                float toValue)
        {
            progress = MathUtils.Clamp(progress, progressLowCut, progressHighCut);
            float a = (progress - progressLowCut) / (progressHighCut - progressLowCut);
            return MathUtils.Lerp(fromValue, toValue, a);
        }

        public static float Scale(float value, float maxValue, float maxScale)
        {
            return (maxScale / maxValue) * value;
        }

        public static float Scale(float value, float minValue, float maxValue, float min2, float max2)
        {
            return min2 + ((value - minValue) / (maxValue - minValue)) * (max2 - min2);
        }

        public static float ScaleClamp(float value, float minValue, float maxValue, float min2, float max2)
        {
            value = min2 + ((value - minValue) / (maxValue - minValue)) * (max2 - min2);
            if (max2 > min2)
            {
                value = value < max2 ? value : max2;
                return value > min2 ? value : min2;
            }
            value = value < min2 ? value : min2;
            return value > max2 ? value : max2;
        }

        public static float Percent(float value, float min, float max)
        {
            return Percent(value, min, max, 1f);
        }

        public static float Percent(float value, float min, float max, float upperMax)
        {
            if (max <= -1f)
            {
                max = min + 1f;
            }
            float percentage = (value - min) / (max - min);
            if (percentage > 1f)
            {
                if (upperMax != -1f)
                {
                    percentage = ((upperMax - value)) / (upperMax - max);
                    if (percentage < 0f)
                    {
                        percentage = 0f;
                    }
                }
                else
                {
                    percentage = 1f;
                }
            }
            else if (percentage < 0f)
            {
                percentage = 0f;
            }
            return percentage;
        }

        public static float Percent(float value, float percent)
        {
            return value * (percent * 0.01f);
        }

        public static int Percent(int value, int percent)
        {
            return (int)(value * (percent * 0.01f));
        }

        public static int Compare(int x, int y)
        {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }

        public static int Compare(float x, float y)
        {
            if (x < y)
            {
                return -1;
            }
            if (x > y)
            {
                return 1;
            }
            int thisBits = (int)NumberUtils.FloatToIntBits(x);
            int anotherBits = (int)NumberUtils.FloatToIntBits(y);
            return (thisBits == anotherBits ? 0 : (thisBits < anotherBits ? -1 : 1));
        }

        public static int LongOfZeros(long i)
        {
            if (i == 0)
            {
                return 64;
            }
            int n = 1;

            int x = (int)Abs(i >> 32);
            if (x == 0)
            {
                n += 32;
                x = (int)i;
            }
            if (Abs(x >> 16) == 0)
            {
                n += 16;
                x <<= 16;
            }
            if (Abs(x >> 24) == 0)
            {
                n += 8;
                x <<= 8;
            }
            if (Abs(x >> 28) == 0)
            {
                n += 4;
                x <<= 4;
            }
            if (Abs(x >> 30) == 0)
            {
                n += 2;
                x <<= 2;
            }
            n -= Abs(x >> 31);
            return n;
        }

        public static float ParseAngle(string angle, float value)
        {
            if (StringUtils.IsEmpty(angle))
            {
                return 0f;
            }
            angle = angle.ToLower();
            if ("deg".Equals(angle))
            {
                return MathUtils.DEG_TO_RAD * value;
            }
            else if ("grad".Equals(angle))
            {
                return MathUtils.PI / 200 * value;
            }
            else if ("rad".Equals(angle))
            {
                return value;
            }
            else if ("turn".Equals(angle))
            {
                return MathUtils.TWO_PI * value;
            }
            return value;
        }

        public static bool IsLimit(int value, int minX, int maxX)
        {
            return value >= minX && value <= maxX;
        }

        public static float FixRotation(float rotation)
        {
            float newAngle = 0f;
            if (rotation == -360f || rotation == 360f)
            {
                return newAngle;
            }
            newAngle = rotation;
            if (newAngle < 0f)
            {
                while (newAngle < -360f)
                {
                    newAngle += 360f;
                }
            }
            if (newAngle > 0f)
            {
                while (newAngle > 360f)
                {
                    newAngle -= 360f;
                }
            }
            return newAngle;
        }

        public static float FixRotationLimit(float rotation, float min, float max)
        {
            float result = rotation;
            if (rotation > max)
            {
                result = max;
            }
            else if (rotation < min)
            {
                result = min;
            }
            return FixRotation(result);
        }

        public static float FixAngle(float angle)
        {
            float newAngle = 0f;
            if (angle == -TWO_PI || angle == TWO_PI)
            {
                return newAngle;
            }
            newAngle = angle;
            if (newAngle < 0)
            {
                while (newAngle < 0)
                {
                    newAngle += TWO_PI;
                }
            }
            if (newAngle > TWO_PI)
            {
                while (newAngle > TWO_PI)
                {
                    newAngle -= TWO_PI;
                }
            }
            return newAngle;
        }

        public static float FixAngleLimit(float angle, float min, float max)
        {
            float result = angle;
            if (angle > max)
            {
                result = max;
            }
            else if (angle < min)
            {
                result = min;
            }
            return FixAngle(result);
        }

        public static float Adjust(float angle)
        {
            float newAngle = angle;
            while (newAngle < 0)
            {
                newAngle += RAD_FULL;
            }
            while (newAngle > RAD_FULL)
            {
                newAngle -= RAD_FULL;
            }
            return newAngle;
        }

        public static float GetNormalizedAngle(float angle)
        {
            while (angle < 0)
            {
                angle += MathUtils.RAD_FULL;
            }
            return angle % MathUtils.RAD_FULL;
        }

        public static bool InAngleRange(float angle, float startAngle, float endAngle)
        {
            float newAngle = Adjust(angle);
            float newStartAngle = Adjust(startAngle);
            float newEndAngle = Adjust(endAngle);
            if (newStartAngle > newEndAngle)
            {
                newEndAngle += RAD_FULL;
                if (newAngle < newStartAngle)
                {
                    newAngle += RAD_FULL;
                }
            }
            return newAngle >= newStartAngle && newAngle <= newEndAngle;
        }

        public static float AngleFrom(float x1, float y1, float x2, float y2)
        {
            float diffX = x2 - x1;
            float diffY = y2 - y1;
            return Atan2(diffY, diffX);
        }

        public static float Scroll(float scroll, float side)
        {
            float start = 0;
            float v = MathUtils.Abs(scroll) % side;
            if (v < 0)
            {
                start = -(side - v);
            }
            else if (v > 0)
            {
                start = -v;
            }
            return start;
        }


        public static float Inerations(float total, float start, float side)
        {
            float diff = total = start;
            float v = diff / side;
            return v + (diff % side > 0 ? 1f : 0f);
        }

        public static float Factorial(float v)
        {
            if (v == 0f)
            {
                return 1f;
            }
            float result = v;
            while (--v > 0)
            {
                result *= v;
            }
            return result;
        }

        public static float MaxAdd(float v, float amount, float max)
        {
            v += amount;
            if (v > max)
            {
                v = max;
            }
            return v;
        }

        public static float MinSub(float v, float amount, float min)
        {
            v -= amount;
            if (v < min)
            {
                v = min;
            }
            return v;
        }

        public static float WrapValue(float v, float amount, float max)
        {
            float diff = 0f;
            v = MathUtils.Abs(v);
            amount = MathUtils.Abs(amount);
            max = MathUtils.Abs(max);
            diff = (v + amount) % max;
            return diff;
        }


        public static bool IsSuccessful(float k, float p)
        {
            return MathUtils.Random(k) < p;
        }

        public static bool ChanceRoll(float chance)
        {
            if (chance <= 0f)
            {
                return false;
            }
            else if (chance >= 100f)
            {
                return true;
            }
            else
            {
                if (MathUtils.Random(100f) >= chance)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }
}






