using System;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core;

namespace Loon.Utils
{
    public class NumberUtils
    {

        private NumberUtils()
        {

        }

        public static RectBox GetBounds(float x, float y, float width,
                float height, float rotate, RectBox result)
        {
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

        public static RectBox GetBounds(float x, float y, float width,
                float height, float rotate)
        {
            return GetBounds(x, y, width, height, rotate, null);
        }

        public static int[] GetLimit(float x, float y, float width, float height,
                float rotate)
        {
            float rotation = MathUtils.ToRadians(rotate);
            float angSin = MathUtils.Sin(rotation);
            float angCos = MathUtils.Cos(rotation);
            int newW = MathUtils.Floor((width * MathUtils.Abs(angCos))
                    + (height * MathUtils.Abs(angSin)));
            int newH = MathUtils.Floor((height * MathUtils.Abs(angCos))
                    + (width * MathUtils.Abs(angSin)));
            int centerX = (int)(x + (width / 2));
            int centerY = (int)(y + (height / 2));
            int newX = (int)(centerX - (newW / 2));
            int newY = (int)(centerY - (newH / 2));
            return new int[] { newX, newY, newW, newH };
        }

        static private readonly string[] zeros = { "", "0", "00", "000", "0000",
				"00000", "000000", "0000000", "00000000", "000000000", "0000000000" };

        /// <summary>
        /// 为指定数值补足位数
        /// </summary>
        ///
        /// <param name="number"></param>
        /// <param name="numDigits"></param>
        /// <returns></returns>
        public static string AddZeros(long number, int numDigits)
        {
            return AddZeros(number.ToString(), numDigits);
        }

        /// <summary>
        /// 为指定数值补足位数
        /// </summary>
        ///
        /// <param name="number"></param>
        /// <param name="numDigits"></param>
        /// <returns></returns>
        public static string AddZeros(string number, int numDigits)
        {
            int length = numDigits - number.Length;
            if (length != 0)
            {
                number = zeros[length] + number;
            }
            return number;
        }

        private const string IsNumericRegEx = @"^\d+$";
        
        /// <summary>
        /// 判断是否为数字
        /// </summary>
        ///
        /// <param name="param"></param>
        /// <returns></returns>
        public static bool IsNan(string param)
        {
            if (param == null || "".Equals(param))
            {
                return false;
            }
            try
            {
                return System.Text.RegularExpressions.Regex.IsMatch(param, IsNumericRegEx,
                       System.Text.RegularExpressions.RegexOptions.Compiled);
            }
            catch
            {
                return false;
            }
        }

        /// <summary>
        /// 判断指定对象是否数字
        /// </summary>
        /// 
        /// <param name="o"></param>
        /// <returns></returns>
        public static bool IsNan(object o)
        {
            return o is int || o is double || o is decimal || o is float || o is long || o is short;
        }

        /// <summary>
        /// 检查一个数字是否为空
        /// </summary>
        ///
        /// <param name="val"></param>
        /// <returns></returns>
        public static bool IsEmpty(int val)
        {
            return (val == Int32.MinValue) ? true : 0 == val;
        }

        /// <summary>
        /// 检查一个字符串数字是否为空
        /// </summary>
        ///
        /// <param name="val"></param>
        /// <returns></returns>
        public static bool IsEmpty(string val)
        {
            return (val == null | "".Equals(val));
        }

        /// <summary>
        /// 单纯计算两个数值的百分比
        /// </summary>
        ///
        /// <param name="divisor"></param>
        /// <param name="dividend"></param>
        /// <returns></returns>
        public static float ToPercent(long divisor, long dividend)
        {
            if (divisor == 0 || dividend == 0)
            {
                return 0f;
            }
            float cf = divisor * 1f;
            float pf = dividend * 1f;

            return (MathUtils.Round(cf / pf * 10000f) * 1f) / 100f;
        }

        /// <summary>
        /// 获得100%进制剩余数值百分比。
        /// </summary>
        ///
        /// <param name="maxValue"></param>
        /// <param name="minusValue"></param>
        /// <returns></returns>
        public static float MinusPercent(float maxValue, float minusValue)
        {
            return 100 - ((minusValue / maxValue) * 100);
        }

        /// <summary>
        /// 获得100%进制数值百分比。
        /// </summary>
        ///
        /// <param name="maxValue"></param>
        /// <param name="minusValue"></param>
        /// <returns></returns>
        public static float Percent(float maxValue, float minValue)
        {
            return (minValue / maxValue) * 100;
        }
    }
}
