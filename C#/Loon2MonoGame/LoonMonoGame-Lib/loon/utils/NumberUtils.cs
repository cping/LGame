using java.lang;

namespace loon.utils
{
    public class NumberUtils
    {
        public static uint FloatToIntBits(float value)
        {
            return Float.FloatToIntBits(value);
        }

        public static uint FloatToRawIntBits(float value)
        {
            return Float.FloatToRawIntBits(value);
        }

        public static uint FloatToIntColor(float value)
        {
            return Float.FloatToRawIntBits(value);
        }

        public static float IntToFloatColor(int value)
        {
            return Float.IntBitsToFloat((int)(System.Convert.ToInt64(value) & 0xfeffffff));
        }

        public static float IntBitsToFloat(int value)
        {
            return Float.IntBitsToFloat(value);
        }

        public static double LongBitsToDouble(long value)
        {
            return java.lang.Double.LongBitsToDouble(value);
        }

        public static int Compare(float f1, float f2)
        {
            if (f1 < f2)
            {
                return -1;
            }
            if (f1 > f2)
            {
                return 1;
            }
            uint thisBits = FloatToIntBits(f1);
            uint anotherBits = FloatToIntBits(f2);
            return (thisBits == anotherBits ? 0 : (thisBits < anotherBits ? -1 : 1));
        }

    }
}
