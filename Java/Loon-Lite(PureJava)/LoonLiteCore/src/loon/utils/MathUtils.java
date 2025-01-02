/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package loon.utils;

import loon.LObject;
import loon.LSysException;
import loon.LSystem;
import loon.geom.RangeI;
import loon.geom.RectBox;
import loon.geom.SetXY;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.geom.XY;

public final class MathUtils {

	private MathUtils() {
	}

	public static final Random random = new Random();

	public static final float FLOAT_ROUNDING_ERROR = 0.000001f;

	public static final int ZERO_FIXED = 0;

	public static final int ONE_FIXED = 1 << 16;

	public static final float EPSILON = 0.001f;

	public static final float NaN = 0.0f / 0.0f;

	public static final int PI_FIXED = 205887;

	public static final int PI_OVER_2_FIXED = PI_FIXED / 2;

	public static final int E_FIXED = 178145;

	public static final int HALF_FIXED = 2 << 15;

	protected final static int TO_STRING_DECIMAL_PLACES = 3;

	private static final String[] ZEROS = { "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000",
			"000000000", "0000000000" };

	private static final int[] SHIFT = { 0, 1144, 2289, 3435, 4583, 5734, 6888, 8047, 9210, 10380, 11556, 12739, 13930,
			15130, 16340, 17560, 18792, 20036, 21294, 22566, 23853, 25157, 26478, 27818, 29179, 30560, 31964, 33392,
			34846, 36327, 37837, 39378, 40951, 42560, 44205, 45889, 47615, 49385, 51202, 53070, 54991, 56970, 59009,
			61113, 63287, 65536 };

	public static final float PI_OVER2 = 1.5708f;

	public static final float PI_OVER4 = 0.785398f;

	public static final float PHI = 0.618f;

	public static final float TAU = 6.28318548f;

	private static final float CEIL = 0.9999999f;

	private static final int BIG_ENOUGH_INT = 16384;

	private static final float BIG_ENOUGH_CEIL = 16384.998f;

	private static final float BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

	private static final float BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;

	private static final int ATAN2_BITS = 7;

	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;

	private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);

	private static final int ATAN2_COUNT = ATAN2_MASK + 1;

	public static final int ATAN2_DIM = 128;

	private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

	public static final float PI = 3.1415927f;

	public static final float TWO_PI = 6.28319f;

	public static final float HALF_PI = PI / 2f;

	public static final float SQRT2 = 1.4142135f;

	public static final float DEG_FULL = 360f;

	private static final int SIN_BITS = 13;

	private static final int SIN_MASK = ~(-1 << SIN_BITS);

	private static final int SIN_COUNT = SIN_MASK + 1;

	private static final float RAD_FULL = PI * 2;

	private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;

	private static final float DEG_TO_INDEX = SIN_COUNT / DEG_FULL;

	public static final float RAD_TO_DEG = 180.0f / PI;

	public static final float DEG_TO_RAD = PI / 180.0f;

	static final private class SinCos {

		static final float[] SIN_LIST = new float[SIN_COUNT];

		static final float[] COS_LIST = new float[SIN_COUNT];

		static {
			for (int i = 0; i < SIN_COUNT; i++) {
				float a = (i + 0.5f) / SIN_COUNT * RAD_FULL;
				SIN_LIST[i] = (float) Math.sin(a);
				COS_LIST[i] = (float) Math.cos(a);
			}
		}
	}

	static final private class Atan2 {

		static final float[] TABLE = new float[ATAN2_COUNT];

		static {
			for (int i = 0; i < ATAN2_DIM; i++) {
				for (int j = 0; j < ATAN2_DIM; j++) {
					float x0 = (float) i / ATAN2_DIM;
					float y0 = (float) j / ATAN2_DIM;
					TABLE[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
				}
			}
		}
	}

	public static RectBox getBounds(float x, float y, float width, float height, float rotate, RectBox result) {
		if (rotate == 0) {
			if (result == null) {
				result = new RectBox(x, y, width, height);
			} else {
				result.setBounds(x, y, width, height);
			}
			return result;
		}
		int[] rect = getLimit(x, y, width, height, rotate);
		if (result == null) {
			result = new RectBox(rect[0], rect[1], rect[2], rect[3]);
		} else {
			result.setBounds(rect[0], rect[1], rect[2], rect[3]);
		}
		return result;
	}

	public static RectBox getBounds(float x, float y, float width, float height, float rotate) {
		return getBounds(x, y, width, height, rotate, null);
	}

	public static boolean isZero(float value, float tolerance) {
		return MathUtils.abs(value) <= tolerance;
	}

	public static boolean isEqual(float a, float b) {
		return MathUtils.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}

	public static boolean isEqual(float a, float b, float tolerance) {
		return MathUtils.abs(a - b) <= tolerance;
	}

	public static boolean isOdd(int i) {
		return (i % 2 != 0);
	}

	public static boolean isPowerOfTwo(int w, int h) {
		return (w > 0 && (w & (w - 1)) == 0 && h > 0 && (h & (h - 1)) == 0);
	}

	public static boolean isPowerOfTwo(int n) {
		int i = 1;
		for (;;) {
			if (i > n) {
				return false;
			}
			if (i == n) {
				return true;
			}
			i = i * 2;
		}
	}

	public static int previousPowerOfTwo(int value) {
		final int power = (int) (log(value) / log(2));
		return (int) pow(2, power);
	}

	public static int precision(float v) {
		int e = 1;
		int p = 0;
		while (abs((round(v * e) / e) - v) > FLOAT_ROUNDING_ERROR) {
			e *= 10;
			p++;
		}
		return p;
	}

	public static int nextPowerOfTwo(int value) {
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

	public static int[] getLimit(float x, float y, float width, float height, float rotate) {
		final float angle = wrapCompare(rotate, 0, 360f);
		final float rotation = MathUtils.toRadians(angle);
		final float angSin = MathUtils.sin(rotation);
		final float angCos = MathUtils.cos(rotation);

		final int newW = MathUtils.floor((width * MathUtils.abs(angCos)) + (height * MathUtils.abs(angSin)));
		final int newH = MathUtils.floor((height * MathUtils.abs(angCos)) + (width * MathUtils.abs(angSin)));

		final int centerX = MathUtils.floor(x + (width / 2));
		final int centerY = MathUtils.floor(y + (height / 2));

		final int newX = (centerX - (newW / 2));
		final int newY = (centerY - (newH / 2));

		return new int[] { newX, newY, newW, newH };
	}

	public static int divTwoAbs(int v) {
		if (v % 2 != 0) {
			v += 1;
		}
		return abs(v);
	}

	/**
	 * 为指定数值补足位数
	 * 
	 * @param number
	 * @param numDigits
	 * @return
	 */
	public static String addZeros(long number, int numDigits) {
		return addZeros(String.valueOf(number), numDigits);
	}

	/**
	 * 为指定数值补足位数
	 * 
	 * @param number
	 * @param numDigit
	 * @return
	 */
	public static String addZeros(String number, int numDigit) {
		return addZeros(number, numDigit, false);
	}

	/**
	 * 为指定数值补足位数
	 * 
	 * @param number
	 * @param numDigits
	 * @return
	 */
	public static String addZeros(String number, int numDigits, boolean reverse) {
		int length = numDigits - number.length();
		if (length > -1) {
			if (length - 1 < ZEROS.length) {
				if (reverse) {
					number = number + ZEROS[length];
				} else {
					number = ZEROS[length] + number;
				}
			} else {
				StrBuilder sbr = new StrBuilder();
				for (int i = 0; i < length; i++) {
					sbr.append('0');
				}
				if (reverse) {
					number = number + sbr.toString();
				} else {
					number = sbr.toString() + number;
				}
			}
		}
		return number;
	}

	/**
	 * 返回数字的位数长度
	 * 
	 * @param num
	 * @return
	 */
	public static int getBitSize(int num) {
		int numBits = 0;
		if (num < 10l) {
			numBits = 1;
		} else if (num < 100l) {
			numBits = 2;
		} else if (num < 1000l) {
			numBits = 3;
		} else if (num < 10000l) {
			numBits = 4;
		} else if (num < 100000l) {
			numBits = 5;
		} else if (num < 1000000l) {
			numBits = 6;
		} else if (num < 10000000l) {
			numBits = 7;
		} else if (num < 100000000l) {
			numBits = 8;
		} else if (num < 1000000000l) {
			numBits = 9;
		} else {
			numBits = (String.valueOf(num).length() - 1);
		}
		return numBits;
	}

	/**
	 * 返回浮点数'.'后长度
	 * 
	 * @param num
	 * @return
	 */
	public static int getFloatDotBackSize(float num) {
		if (num < 0f) {
			num = -num;
		}
		if (num < 1f) {
			int numBits = 1;
			if (num >= 1f - 0.01f) {
				numBits = 1;
			} else if (num >= 1f - 0.001f) {
				numBits = 2;
			} else if (num >= 1f - 0.0001f) {
				numBits = 3;
			} else if (num >= 1f - 0.00001f) {
				numBits = 4;
			} else if (num >= 1f - 0.000001f) {
				numBits = 5;
			} else if (num >= 1f - 0.0000001f) {
				numBits = 6;
			} else if (num >= 1f - 0.00000001f) {
				numBits = 7;
			} else if (num >= 1f - 0.000000001f) {
				numBits = 8;
			} else if (num >= 1f - 0.0000000001f) {
				numBits = 9;
			} else {
				String v = String.valueOf(num);
				numBits = v.substring(v.indexOf('.'), v.length()).length() - 1;
			}
			return numBits;
		}
		return 0;
	}

	public static int getCircleSideCount(float radius, float maxLength) {
		float circumference = TWO_PI * radius;
		return (int) max(circumference / maxLength, 1f);
	}

	public static int getCircleArcSideCount(float radius, float angleDeg, float maxLength) {
		float circumference = TWO_PI * radius * (angleDeg / DEG_FULL);
		return (int) max(circumference / maxLength, 1f);
	}

	public static boolean isNan(float v) {
		return (v != v);
	}

	public static boolean isNan(double v) {
		return (v != v);
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param param
	 * @return
	 */
	public static boolean isNan(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		int start = (chars[0] == '-') ? 1 : 0;
		int i = 0;
		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				i = start + 2;
				if (i == sz) {
					return false;
				}
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--;
		i = start;
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;
			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				if (hasExp) {
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				return false;
			}
			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit && !hasExp;
			}
			return false;
		}
		return !allowSigns && foundDigit;
	}

	public static boolean isNumber(CharSequence num) {
		if (StringUtils.isEmpty(num)) {
			return false;
		}
		return isNan(num.toString());
	}

	public static boolean isZero(float value) {
		return MathUtils.abs(value) <= 0.00000001;
	}

	public static boolean isBeside(double x, double y) {
		return abs(x - y) == 1;
	}

	public static boolean isBeside(float x, float y) {
		return abs(x - y) == 1;
	}

	public static boolean isBeside(long x, long y) {
		return abs(x - y) == 1;
	}

	public static boolean isBeside(int x, int y) {
		return abs(x - y) == 1;
	}

	public static int mul(int x, int y) {
		long z = (long) x * (long) y;
		return ((int) (z >> 16));
	}

	public static float mul(float x, float y) {
		long z = (long) x * (long) y;
		return ((float) (z >> 16));
	}

	public static int mulDiv(int f1, int f2, int f3) {
		return (int) ((long) f1 * f2 / f3);
	}

	public static long mulDiv(long f1, long f2, long f3) {
		return f1 * f2 / f3;
	}

	public static int mid(int i, int min, int max) {
		return limit(i, min, max);
	}

	public static float mid(float i, float min, float max) {
		return limit(i, min, max);
	}

	public static int div(int x, int y) {
		long z = (((long) x) << 32);
		return (int) ((z / y) >> 16);
	}

	public static float div(float x, float y) {
		long z = (((long) x) << 32);
		return (float) ((z / (long) y) >> 16);
	}

	public static int round(int n) {
		if (n > 0) {
			if ((n & 0x8000) != 0) {
				return (((n + 0x10000) >> 16) << 16);
			} else {
				return (((n) >> 16) << 16);
			}
		} else {
			int k;
			n = -n;
			if ((n & 0x8000) != 0) {
				k = (((n + 0x10000) >> 16) << 16);
			} else {
				k = (((n) >> 16) << 16);
			}
			return -k;
		}
	}

	public static boolean equal(float[] a, float[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (!equal(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean equal(int[] a, int[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (!equal(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean equal(boolean[] a, boolean[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean equal(float[] points, float[] xpoints, float[] ypoints) {
		int count = 0;
		for (int i = 0; i < points.length; i++) {
			if (i % 2 == 0) {
				final float newX = xpoints[count];
				if (!equal(points[i], newX)) {
					return false;
				}
			} else {
				final float newY = ypoints[count];
				if (!equal(points[i], newY)) {
					return false;
				}
				count++;
			}
		}
		return true;
	}

	public static boolean equal(float[] points, int[] xpoints, int[] ypoints) {
		int count = 0;
		for (int i = 0; i < points.length; i++) {
			if (i % 2 == 0) {
				final float newX = xpoints[count];
				if (!equal(points[i], newX)) {
					return false;
				}
			} else {
				final float newY = ypoints[count];
				if (!equal(points[i], newY)) {
					return false;
				}
				count++;
			}
		}
		return true;
	}

	public static boolean equal(int a, int b) {
		return a == b;
	}

	public static boolean equal(double a, double b) {
		if (a > b)
			return a - b <= EPSILON;
		else
			return b - a <= EPSILON;
	}

	public static boolean equal(float a, float b) {
		if (a > b)
			return a - b <= EPSILON;
		else
			return b - a <= EPSILON;
	}

	public static int sign(float x) {
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		}
		return 0;
	}

	public static int randomSign() {
		return random.nextSignInt();
	}

	final static int SK1 = 498;

	final static int SK2 = 10882;

	public static int sinInt(int f) {
		int sign = 1;
		if ((f > PI_OVER_2_FIXED) && (f <= PI_FIXED)) {
			f = PI_FIXED - f;
		} else if ((f > PI_FIXED) && (f <= (PI_FIXED + PI_OVER_2_FIXED))) {
			f = f - PI_FIXED;
			sign = -1;
		} else if (f > (PI_FIXED + PI_OVER_2_FIXED)) {
			f = (PI_FIXED << 1) - f;
			sign = -1;
		}
		int sqr = mul(f, f);
		int result = SK1;
		result = mul(result, sqr);
		result -= SK2;
		result = mul(result, sqr);
		result += ONE_FIXED;
		result = mul(result, f);
		return sign * result;
	}

	final static int CK1 = 2328;

	final static int CK2 = 32551;

	public static int cosInt(int f) {
		int sign = 1;
		if ((f > PI_OVER_2_FIXED) && (f <= PI_FIXED)) {
			f = PI_FIXED - f;
			sign = -1;
		} else if ((f > PI_OVER_2_FIXED) && (f <= (PI_FIXED + PI_OVER_2_FIXED))) {
			f = f - PI_FIXED;
			sign = -1;
		} else if (f > (PI_FIXED + PI_OVER_2_FIXED)) {
			f = (PI_FIXED << 1) - f;
		}
		int sqr = mul(f, f);
		int result = CK1;
		result = mul(result, sqr);
		result -= CK2;
		result = mul(result, sqr);
		result += ONE_FIXED;
		return result * sign;
	}

	final static int TK1 = 13323;

	final static int TK2 = 20810;

	public static int tanInt(int f) {
		int sqr = mul(f, f);
		int result = TK1;
		result = mul(result, sqr);
		result += TK2;
		result = mul(result, sqr);
		result += ONE_FIXED;
		result = mul(result, f);
		return result;
	}

	public static int atanInt(int f) {
		int sqr = mul(f, f);
		int result = 1365;
		result = mul(result, sqr);
		result -= 5579;
		result = mul(result, sqr);
		result += 11805;
		result = mul(result, sqr);
		result -= 21646;
		result = mul(result, sqr);
		result += 65527;
		result = mul(result, f);
		return result;
	}

	final static int AS1 = -1228;

	final static int AS2 = 4866;

	final static int AS3 = 13901;

	final static int AS4 = 102939;

	public static int asinInt(int f) {
		int fRoot = sqrtInt(ONE_FIXED - f);
		int result = AS1;
		result = mul(result, f);
		result += AS2;
		result = mul(result, f);
		result -= AS3;
		result = mul(result, f);
		result += AS4;
		result = PI_OVER_2_FIXED - (mul(fRoot, result));
		return result;
	}

	public static int acosInt(int f) {
		int fRoot = sqrtInt(ONE_FIXED - f);
		int result = AS1;
		result = mul(result, f);
		result += AS2;
		result = mul(result, f);
		result -= AS3;
		result = mul(result, f);
		result += AS4;
		result = mul(fRoot, result);
		return result;
	}

	public static <T> float angleBetween(LObject<T> a1, LObject<T> b1, boolean degree) {
		if (a1 == null || b1 == null) {
			return 0f;
		}
		float dx = b1.getX() - a1.getX();
		float dy = b1.getY() - a1.getY();
		return angleFrom(dx, dy, degree);
	}

	public static <T> float degreesBetween(LObject<T> a1, LObject<T> b1) {
		return angleBetween(a1, b1, true);
	}

	public static <T> float radiansBetween(LObject<T> a1, LObject<T> b1) {
		return angleBetween(a1, b1, false);
	}

	public static <T> float angleBetweenPoint(LObject<T> o, XY pos, boolean degree) {
		float dx = pos.getX() - o.getX();
		float dy = pos.getY() - o.getY();
		return angleFrom(dx, dy, degree);
	}

	public static <T> float degreesBetweenPoint(LObject<T> o, XY pos) {
		return angleBetweenPoint(o, pos, true);
	}

	public static <T> float radiansBetweenPoint(LObject<T> o, XY pos) {
		return angleBetweenPoint(o, pos, false);
	}

	/**
	 * 转化极坐标系为笛卡尔坐标系
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param angle
	 * @return
	 */
	public static Vector2f getCartesianCoords(float x, float y, float radius, float angle) {
		return new Vector2f(radius * MathUtils.cos(angle * DEG_TO_RAD), radius * MathUtils.sin(angle * DEG_TO_RAD));
	}

	/**
	 * 转化笛卡尔坐标系为极坐标系
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Vector2f getPolarCoords(float x, float y) {
		return new Vector2f(MathUtils.sqrt((x * x) + (y * y)), degreesFrom(x, y));
	}

	public static float trunc(float x) {
		return x < 0f ? MathUtils.ceil(x) : MathUtils.floor(x);
	}

	public static float tan(float angle) {
		return (float) Math.tan(angle);
	}

	public static float asin(float value) {
		return (float) Math.asin(value);
	}

	public static float acos(float value) {
		return (float) Math.acos(value);
	}

	public static float atan(float value) {
		return (float) Math.atan(value);
	}

	public static float atan2(float y, float x) {
		float add, mul;
		if (x < 0) {
			if (y < 0) {
				y = -y;
				mul = 1;
			} else
				mul = -1;
			x = -x;
			add = -3.141592653f;
		} else {
			if (y < 0) {
				y = -y;
				mul = -1;
			} else
				mul = 1;
			add = 0;
		}
		float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);
		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		return (Atan2.TABLE[yi * ATAN2_DIM + xi] + add) * mul;
	}

	public static float abs(float n) {
		return (n < 0) ? -n : n;
	}

	public static double abs(double n) {
		return (n < 0) ? -n : n;
	}

	public static int abs(int n) {
		return (n < 0) ? -n : n;
	}

	public static long abs(long n) {
		return (n < 0) ? -n : n;
	}

	public static float mag(float a, float b) {
		return sqrt(a * a + b * b);
	}

	public static float mag(float a, float b, float c) {
		return sqrt(a * a + b * b + c * c);
	}

	public static float median(float a, float b, float c) {
		return (a <= b) ? ((b <= c) ? b : ((a < c) ? c : a)) : ((a <= c) ? a : ((b < c) ? c : b));
	}

	public static float distance(float x1, float x2) {
		return abs(x1 - x2);
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return dist(x1, y1, x2, y2);
	}

	public static float dist(float x1, float y1) {
		return abs(x1 - y1);
	}

	public static float dist(float x1, float y1, float x2, float y2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	public static float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
	}

	public static float distSquared(float x1, float y1, float x2, float y2) {
		return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
	}

	public static float distRectPoint(float px, float py, float rx, float ry, float rw, float rh) {
		if (px >= rx && px <= rx + rw) {
			if (py >= ry && py <= ry + rh) {
				return 0f;
			}
			if (py > ry) {
				return py - (ry + rh);
			}
			return ry - py;
		}
		if (py >= ry && py <= ry + rh) {
			if (px > rx) {
				return px - (rx + rw);
			}
			return rx - px;
		}
		if (px > rx) {
			if (py > ry) {
				return dist(px, py, rx + rw, ry + rh);
			}
			return dist(px, py, rx + rw, ry);
		}
		if (py > ry) {
			return dist(px, py, rx, ry + rh);
		}
		return dist(px, py, rx, ry);
	}

	public static float distRects(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
		if (x1 < x2 + w2 && x2 < x1 + w1) {
			if (y1 < y2 + h2 && y2 < y1 + h1) {
				return 0f;
			}
			if (y1 > y2) {
				return y1 - (y2 + h2);
			}
			return y2 - (y1 + h1);
		}
		if (y1 < y2 + h2 && y2 < y1 + h1) {
			if (x1 > x2) {
				return x1 - (x2 + w2);
			}
			return x2 - (x1 + w1);
		}
		if (x1 > x2) {
			if (y1 > y2) {
				return dist(x1, y1, (x2 + w2), (y2 + h2));
			}
			return dist(x1, y1 + h1, x2 + w2, y2);
		}
		if (y1 > y2) {
			return dist(x1 + w1, y1, x2, y2 + h2);
		}
		return dist(x1 + w1, y1 + h1, x2, y2);
	}

	public static float sq(float a) {
		return a * a;
	}

	public static float sqrt(float a) {
		return (float) Math.sqrt(a);
	}

	public static int sqrtInt(int n) {
		int s = (n + 65536) >> 1;
		for (int i = 0; i < 8; i++) {
			s = (s + div(n, s)) >> 1;
		}
		return s;
	}

	public static float log(float a) {
		return (float) Math.log(a);
	}

	public static float exp(float a) {
		return (float) Math.exp(a);
	}

	public static float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}

	public static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	public static float max(float a, float b) {
		return (a > b) ? a : b;
	}

	public static long max(long a, long b) {
		return (a > b) ? a : b;
	}

	public static int max(int a, int b, int c) {
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	public static float max(float a, float b, float c) {
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	public static int max(final int... numbers) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}

	public static float max(final float... numbers) {
		float max = Integer.MIN_VALUE;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}

	public static int min(int a, int b, int c) {
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

	public static float min(float a, float b, float c) {
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

	public static float min(float a, float b) {
		return (a <= b) ? a : b;
	}

	public static int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	public static int min(final int... numbers) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
			}
		}
		return min;
	}

	public static float min(final float... numbers) {
		float min = Integer.MAX_VALUE;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
			}
		}
		return min;
	}

	public static float mix(final float x, final float y, final float m) {
		return x * (1 - m) + y * m;
	}

	public static int mix(final int x, final int y, final float m) {
		return MathUtils.round(x * (1 - m) + y * m);
	}

	public static float norm(float value, float start, float stop) {
		return (value - start) / (stop - start);
	}

	public static float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

	public static float sin(float n, float angle, float arc, boolean plus) {
		return plus ? n + MathUtils.sin(angle) + arc : n - MathUtils.sin(angle) * arc;
	}

	public static float sin(float rad) {
		return SinCos.SIN_LIST[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
	}

	public static float sinDeg(float deg) {
		return SinCos.SIN_LIST[(int) (deg * DEG_TO_INDEX) & SIN_MASK];
	}

	public static float cos(float n, float angle, float arc, boolean plus) {
		return plus ? n + MathUtils.cos(angle) + arc : n - MathUtils.cos(angle) * arc;
	}

	public static float cos(float rad) {
		return SinCos.COS_LIST[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
	}

	public static float cosDeg(float deg) {
		return SinCos.COS_LIST[(int) (deg * DEG_TO_INDEX) & SIN_MASK];
	}

	public static float choose(float... choices) {
		if (choices == null) {
			return 0f;
		}
		return choices[MathUtils.ifloor(random() * choices.length)];
	}

	public static boolean between(float v, float min, float max) {
		return (v > min && v < max);
	}

	public static float distanceBetween(float x1, float y1, float x2, float y2) {
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		return MathUtils.sqrt(dx * dx + dy * dy);
	}

	public static float distanceBetweenPoints(XY a, XY b) {
		final float dx = a.getX() - b.getX();
		final float dy = a.getY() - b.getY();
		return MathUtils.sqrt(dx * dx + dy * dy);
	}

	public static float distanceBetweenPointsSquared(XY a, XY b) {
		final float dx = a.getX() - b.getX();
		final float dy = a.getY() - b.getY();
		return dx * dx + dy * dy;
	}

	public static float toDegrees(final float radians) {
		return radians * RAD_TO_DEG;
	}

	public static float toRadians(final float degrees) {
		return degrees * DEG_TO_RAD;
	}

	public static float translateX(float angle, float length) {
		return length * MathUtils.cosDeg(angle);
	}

	public static float translateY(float angle, float length) {
		return length * MathUtils.sinDeg(angle);
	}

	public static RangeI transformIndexToCoordinates(int index, int rows, int cols) {
		return transformIndexToCoordinates(index, rows, cols, true);
	}

	public static RangeI transformIndexToCoordinates(int index, int rows, int cols, boolean leftToRight) {
		if (leftToRight) {
			int row = index / cols;
			int col = index % cols;
			return new RangeI(col, row);
		} else {
			int col = index / rows;
			int row = index % rows;
			return new RangeI(col, row);
		}
	}

	public static int transformCoordinatesToIndex(int row, int col, int rows, int cols) {
		return transformCoordinatesToIndex(row, col, rows, cols, true);
	}

	public static int transformCoordinatesToIndex(int row, int col, int rows, int cols, boolean leftToRight) {
		if (leftToRight) {
			return row * cols + col;
		} else {
			return col * rows + row;
		}
	}

	public static int dip2px(float scale, float dpValue) {
		return (int) (dpValue * scale + 0.5f);
	}

	public static float oscilliate(float x, float min, float max, float period) {
		return max - (sin(x * 2f * PI / period) * ((max - min) / 2f) + ((max - min) / 2f));
	}

	public static float degToRad(float deg) {
		return deg * 360 / TWO_PI;
	}

	public static float safeAdd(float left, float right) {
		if (right > 0 ? left > Long.MAX_VALUE - right : left < Long.MIN_VALUE - right) {
			throw new LSysException("Integer overflow");
		}
		return left + right;
	}

	public static float safeSubtract(float left, float right) {
		if (right > 0 ? left < Long.MIN_VALUE + right : left > Long.MAX_VALUE + right) {
			throw new LSysException("Integer overflow");
		}
		return left - right;
	}

	public static float safeMultiply(float left, float right) {
		if (right > 0 ? left > Long.MAX_VALUE / right || left < Long.MIN_VALUE / right
				: (right < -1 ? left > Long.MIN_VALUE / right || left < Long.MAX_VALUE / right
						: right == -1 && left == Long.MIN_VALUE)) {
			throw new LSysException("Integer overflow");
		}
		return left * right;
	}

	public static float safeDivide(float left, float right) {
		if ((left == Float.MIN_VALUE) && (right == -1)) {
			throw new LSysException("Integer overflow");
		}
		return left / right;
	}

	public static float safeNegate(float a) {
		if (a == Long.MIN_VALUE) {
			throw new LSysException("Integer overflow");
		}
		return -a;
	}

	public static float safeAbs(float a) {
		if (a == Long.MIN_VALUE) {
			throw new LSysException("Integer overflow");
		}
		return abs(a);
	}

	public static int bringToBounds(final int minValue, final int maxValue, final int v) {
		return max(minValue, min(maxValue, v));
	}

	public static float bringToBounds(final float minValue, final float maxValue, final float v) {
		return max(minValue, min(maxValue, v));
	}

	public static float nearest(float x, float a, float b) {
		if (abs(a - x) < abs(b - x)) {
			return a;
		}
		return b;
	}

	public static boolean nextBoolean() {
		return randomBoolean();
	}

	public static int nextInt(int range) {
		return random.nextInt(range);
	}

	public static int nextInt(int start, int end) {
		return random.nextInt(start, end);
	}

	public static float nextFloat(float range) {
		return random.nextFloat(range);
	}

	public static float nextFloat(float start, float end) {
		return random.nextFloat(start, end);
	}

	public static long randomLong(long start, long end) {
		return random.nextLong(start, end);
	}

	public static CharSequence nextChars(CharSequence... chs) {
		return random.nextChars(chs);
	}

	public static int random(int range) {
		return random.nextInt(range);
	}

	public static int random(int start, int end) {
		return random.nextInt(start, end);
	}

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static float random() {
		return random.nextFloat();
	}

	public static float random(float range) {
		return random.nextFloat(range);
	}

	public static float random(float start, float end) {
		return random.nextFloat(start, end);
	}

	public static float randomFloor(float start, float end) {
		return MathUtils.floor(random(start, end));
	}

	public static int ifloor(float v) {
		return (int) (v + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static int floor(float x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static int floorInt(long x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static int floorInt(double x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static long floor(double x) {
		return (long) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static int floorPositive(float x) {
		return (int) x;
	}

	public static int iceil(float v) {
		return (int) (v + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
	}

	public static int ceil(float x) {
		return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
	}

	public static int ceilPositive(float x) {
		return (int) (x + CEIL);
	}

	public static int round(float x) {
		return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}

	public static int round(long x) {
		return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}

	public static int roundPositive(float x) {
		return (int) (x + 0.5f);
	}

	public static float barycentric(float value1, float value2, float value3, float amount1, float amount2) {
		return value1 + (value2 - value1) * amount1 + (value3 - value1) * amount2;
	}

	public static float catmullRom(float value1, float value2, float value3, float value4, float amount) {
		double amountSquared = amount * amount;
		double amountCubed = amountSquared * amount;
		return (float) (0.5 * (2.0 * value2 + (value3 - value1) * amount
				+ (2.0 * value1 - 5.0 * value2 + 4.0 * value3 - value4) * amountSquared
				+ (3.0 * value2 - value1 - 3.0 * value3 + value4) * amountCubed));
	}

	public static int clamp(int value, int min, int max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static float clamp(float value, float min, float max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static double clamp(double value, double min, double max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static long clamp(long value, long min, long max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static float clamp(final float v) {
		return v < 0f ? 0f : (v > 1f ? 1f : v);
	}

	public static float clampAngle(final float v) {
		float value = v % PI * 2;
		if (value < 0) {
			value += PI * 2;
		}
		return value;
	}

	public static Vector2f clampInRect(XY v, float x, float y, float width, float height) {
		return clampInRect(v, x, y, width, height, 0f);
	}

	public static Vector2f clampInRect(XY v, float x, float y, float width, float height, float padding) {
		if (v == null) {
			return Vector2f.ZERO();
		}
		Vector2f obj = new Vector2f();
		obj.x = clamp(v.getX(), x + padding, x + width - padding);
		obj.y = clamp(v.getY(), y + padding, y + height - padding);
		return obj;
	}

	public static float cameraLerp(float elapsed, float l) {
		return l * (elapsed / LSystem.DEFAULT_EASE_DELAY);
	}

	public static float coolLerp(float elapsed, float b, float t, float r) {
		return b + cameraLerp(elapsed, r) * (t - b);
	}

	public static float hermite(float value1, float tangent1, float value2, float tangent2, float amount) {
		float v1 = value1, v2 = value2, t1 = tangent1, t2 = tangent2, s = amount, result;
		float sCubed = s * s * s;
		float sSquared = s * s;

		if (amount == 0f) {
			result = value1;
		} else if (amount == 1f) {
			result = value2;
		} else {
			result = (2 * v1 - 2 * v2 + t2 + t1) * sCubed + (3 * v2 - 3 * v1 - 2 * t1 - t2) * sSquared + t1 * s + v1;
		}
		return (float) result;
	}

	public static float lerp(float value1, float value2, float amount) {
		return value1 + (value2 - value1) * amount;
	}

	public static float smoothStep(float value1, float value2, float amount) {
		float result = clamp(amount, 0f, 1f);
		result = hermite(value1, 0f, value2, 0f, result);
		return result;
	}

	public static float wave(float time) {
		return wave(1f, 1f, time);
	}

	public static float wave(float frequency, float amplitude, float time) {
		return amplitude / 2f * (1f - MathUtils.cos(time * frequency * MathUtils.TWO_PI));
	}

	public static float wrapAngle(float angle) {
		angle = (float) IEEEremainder((double) angle, 6.2831854820251465d);
		if (angle <= -3.141593f) {
			angle += 6.283185f;
			return angle;
		}
		if (angle > 3.141593f) {
			angle -= 6.283185f;
		}
		return angle;
	}

	public static double signum(double d) {
		return d > 0 ? 1 : d < -0 ? -1 : d;
	}

	public static float signum(float d) {
		return d > 0 ? 1 : d < -0 ? -1 : d;
	}

	public static double IEEEremainder(double f1, double f2) {
		double r = abs(f1 % f2);
		if (isNan(r) || r == f2 || r <= abs(f2) / 2.0) {
			return r;
		} else {
			return signum(f1) * (r - f2);
		}
	}

	public static double normalizeLon(double lon) {
		while ((lon < -180d) || (lon > 180d)) {
			lon = IEEEremainder(lon, 360d);
		}
		return lon;
	}

	public static int sum(final int[] values) {
		int sum = 0;
		for (int i = values.length - 1; i >= 0; i--) {
			sum += values[i];
		}
		return sum;
	}

	public static float snap(float src, float dst) {
		return round(src / dst) * dst;
	}

	public static Vector2f snap(XY src, float dst) {
		if (src == null) {
			return new Vector2f();
		}
		return new Vector2f(snap(src.getX(), dst), snap(src.getY(), dst));
	}

	public static Vector2f snap(XY src, XY dst) {
		if (src == null || dst == null) {
			return new Vector2f();
		}
		return new Vector2f(snap(src.getX(), dst.getX()), snap(src.getY(), dst.getY()));
	}

	public static float snapFloor(float src, float dst) {
		return floor(src / dst) * dst;
	}

	public static Vector2f snapFloor(XY src, float dst) {
		if (src == null) {
			return new Vector2f();
		}
		return new Vector2f(snapFloor(src.getX(), dst), snapFloor(src.getY(), dst));
	}

	public static Vector2f snapFloor(XY src, XY dst) {
		if (src == null || dst == null) {
			return new Vector2f();
		}
		return new Vector2f(snapFloor(src.getX(), dst.getX()), snapFloor(src.getY(), dst.getY()));
	}

	public static float snapCeil(float src, float dst) {
		return ceil(src / dst) * dst;
	}

	public static Vector2f snapCeil(XY src, float dst) {
		if (src == null) {
			return new Vector2f();
		}
		return new Vector2f(snapCeil(src.getX(), dst), snapCeil(src.getY(), dst));
	}

	public static Vector2f snapCeil(XY src, XY dst) {
		if (src == null || dst == null) {
			return new Vector2f();
		}
		return new Vector2f(snapCeil(src.getX(), dst.getX()), snapCeil(src.getY(), dst.getY()));
	}

	public static void arraySumInternal(final int[] values) {
		final int valueCount = values.length;
		for (int i = 1; i < valueCount; i++) {
			values[i] = values[i - 1] + values[i];
		}
	}

	public static void arraySumInternal(final long[] values) {
		final int valueCount = values.length;
		for (int i = 1; i < valueCount; i++) {
			values[i] = values[i - 1] + values[i];
		}
	}

	public static void arraySumInternal(final long[] values, final long factor) {
		values[0] = values[0] * factor;
		final int valueCount = values.length;
		for (int i = 1; i < valueCount; i++) {
			values[i] = values[i - 1] + values[i] * factor;
		}
	}

	public static void arraySumInto(final long[] values, final long[] targetValues, final long factor) {
		targetValues[0] = values[0] * factor;
		final int valueCount = values.length;
		for (int i = 1; i < valueCount; i++) {
			targetValues[i] = targetValues[i - 1] + values[i] * factor;
		}
	}

	public static float arraySum(final float[] values) {
		float sum = 0;
		final int valueCount = values.length;
		for (int i = 0; i < valueCount; i++) {
			sum += values[i];
		}
		return sum;
	}

	public static float arrayAverage(final float[] values) {
		return MathUtils.arraySum(values) / values.length;
	}

	public static float average(final float a, final float b) {
		return a + (b - a) * 0.5f;
	}

	public static float approach(float src, float dst, float amount) {
		if (src > dst) {
			return max(src - amount, dst);
		} else {
			return min(src + amount, dst);
		}
	}

	public static float approachIfLower(float src, float dst, float amount) {
		if (sign(src) != sign(dst) || abs(src) < abs(dst)) {
			return approach(src, dst, amount);
		} else {
			return src;
		}
	}

	public static Vector2f approach(Vector2f src, Vector2f dst, float amount) {
		if (src == null || dst == null) {
			return new Vector2f();
		}
		if (src == dst) {
			return dst;
		} else {
			Vector2f diff = dst.sub(src);
			if (diff.lengthSquared() <= amount * amount) {
				return dst;
			} else {
				return diff.nor().mulSelf(amount).addSelf(src);
			}
		}
	}

	public static float angle(XY vec) {
		if (vec == null) {
			return 0f;
		}
		return atan2(vec.getY(), vec.getX());
	}

	public static float angle(XY src, XY dst) {
		if (src == null || dst == null) {
			return 0f;
		}
		return atan2(dst.getY() - src.getY(), dst.getX() - src.getX());
	}

	public static Vector2f angleToVector(float angle) {
		return angleToVector(angle, 1f);
	}

	public static Vector2f angleToVector(float angle, float length) {
		return angleToVector(angle, length, null);
	}

	public static Vector2f angleToVector(float angle, float length, Vector2f result) {
		float newX = cos(angle) * length;
		float newY = sin(angle) * length;
		if (result == null) {
			return new Vector2f(newX, newY);
		}
		return result.set(newX, newY);
	}

	public static float angleLerp(float startAngle, float endAngle, float percent) {
		return startAngle + angleDiff(startAngle, endAngle) * percent;
	}

	public static float angleDiff(float radiansA, float radiansB) {
		return ((radiansB - radiansA - PI) % TAU + TAU) % TAU - PI;
	}

	public static float angleApproach(float val, float dst, float maxMove) {
		float diff = angleDiff(val, dst);
		if (abs(diff) < maxMove) {
			return dst;
		}
		return val + clamp(diff, -maxMove, maxMove);
	}

	public static float absAngleDiff(float radiansA, float radiansB) {
		return abs(angleDiff(radiansA, radiansB));
	}

	public static Vector2f rotateToward(Vector2f dir, Vector2f dst, float maxAngleDelta, float maxMagnitudeDelta) {
		float angle = dir.angle();
		float len = dir.length();
		if (maxAngleDelta > 0f) {
			angle = angleApproach(angle, dst.angle(), maxAngleDelta);
		}
		if (maxMagnitudeDelta > 0f) {
			len = approach(len, dst.length(), maxMagnitudeDelta);
		}
		return angleToVector(angle, len);
	}

	public static float[] scaleAroundCenter(final float[] vertices, final float scaleX, final float scaleY,
			final float scaleCenterX, final float scaleCenterY) {
		if (scaleX != 1 || scaleY != 1) {
			for (int i = vertices.length - 2; i >= 0; i -= 2) {
				vertices[i] = scaleCenterX + (vertices[i] - scaleCenterX) * scaleX;
				vertices[i + 1] = scaleCenterY + (vertices[i + 1] - scaleCenterY) * scaleY;
			}
		}
		return vertices;
	}

	public static boolean isInBounds(final int minValue, final int maxValue, final int val) {
		return val >= minValue && val <= maxValue;
	}

	public static boolean isInBounds(final float minValue, final float maxValue, final float val) {
		return val >= minValue && val <= maxValue;
	}

	public static Vector2f randomXY(Vector2f v, float scale) {
		float r = MathUtils.random() * MathUtils.TWO_PI;
		v.x = MathUtils.cos(r) * scale;
		v.y = MathUtils.sin(r) * scale;
		return v;
	}

	public static Vector3f randomXYZ(Vector3f v, float radius) {
		float r = MathUtils.random() * MathUtils.TWO_PI;
		float z = (MathUtils.random() * 2) - 1;
		float zScale = MathUtils.sqrt(1 - z * z) * radius;
		v.x = MathUtils.cos(r) * zScale;
		v.y = MathUtils.sin(r) * zScale;
		v.z = z * radius;
		return v;
	}

	public static SetXY rotate(XY src, SetXY dst, float angle) {
		final float x = src.getX();
		final float y = src.getY();

		dst.setX((x * MathUtils.cos(angle)) - (y * MathUtils.sin(angle)));
		dst.setY((x * MathUtils.sin(angle)) + (y * MathUtils.cos(angle)));

		return dst;
	}

	public static SetXY rotateTo(SetXY dst, float x, float y, float angle, float distance) {
		dst.setX(x + (distance * MathUtils.cos(angle)));
		dst.setY(y + (distance * MathUtils.sin(angle)));
		return dst;
	}

	public static SetXY rotateAround(XY src, SetXY dst, float x, float y, float angle) {
		final float cos = MathUtils.cos(angle);
		final float sin = MathUtils.sin(angle);

		final float tx = src.getX() - x;
		final float ty = src.getY() - y;

		dst.setX(tx * cos - ty * sin + x);
		dst.setY(tx * sin + ty * cos + y);

		return dst;
	}

	public static SetXY rotateAroundDistance(XY src, SetXY dst, float x, float y, float angle, float distance) {
		final float t = angle + MathUtils.atan2(src.getY() - y, src.getX() - x);

		dst.setX(x + (distance * MathUtils.cos(t)));
		dst.setY(y + (distance * MathUtils.sin(t)));

		return dst;
	}

	public static String toString(float value) {
		return toString(value, TO_STRING_DECIMAL_PLACES);
	}

	public static String toString(float value, boolean showTag) {
		return toString(value, TO_STRING_DECIMAL_PLACES, showTag);
	}

	public static String toString(float value, int decimalPlaces) {
		return toString(value, decimalPlaces, false);
	}

	public static String toString(float value, int decimalPlaces, boolean showTag) {
		if (isNan(value)) {
			return "NaN";
		}
		StrBuilder buf = new StrBuilder();
		if (value >= 0) {
			if (showTag) {
				buf.append("+");
			}
		} else {
			if (showTag) {
				buf.append("-");
			}
			value = -value;
		}
		int ivalue = (int) value;
		buf.append(ivalue);
		if (decimalPlaces > 0) {
			buf.append(".");
			for (int ii = 0; ii < decimalPlaces; ii++) {
				value = (value - ivalue) * 10;
				ivalue = (int) value;
				buf.append(ivalue);
			}
			// trim trailing zeros
			for (int ii = 0; ii < decimalPlaces - 1; ii++) {
				if (buf.charAt(buf.length() - 1) == '0') {
					buf.setLength(buf.length() - 1);
				}
			}
		}
		return buf.toString();
	}

	public static int round(int div1, int div2) {
		final int remainder = div1 % div2;
		if (MathUtils.abs(remainder) * 2 <= MathUtils.abs(div2)) {
			return div1 / div2;
		} else if (div1 * div2 < 0) {
			return div1 / div2 - 1;
		} else {
			return div1 / div2 + 1;
		}
	}

	public static float round(float div1, float div2) {
		final float remainder = div1 % div2;
		if (MathUtils.abs(remainder) * 2 <= MathUtils.abs(div2)) {
			return div1 / div2;
		} else if (div1 * div2 < 0) {
			return div1 / div2 - 1;
		} else {
			return div1 / div2 + 1;
		}
	}

	public static long round(long div1, long div2) {
		final long remainder = div1 % div2;
		if (MathUtils.abs(remainder) * 2 <= MathUtils.abs(div2)) {
			return div1 / div2;
		} else if (div1 * div2 < 0) {
			return div1 / div2 - 1;
		} else {
			return div1 / div2 + 1;
		}
	}

	public static float roundToNearest(float v, float n) {
		int p = max(precision(v), precision(n));
		float inv = 1f / n;
		return round(round(v, inv) / inv, p);
	}

	public static int toShift(int angle) {
		if (angle <= 45) {
			return SHIFT[angle];
		} else if (angle >= 315) {
			return -SHIFT[360 - angle];
		} else if (angle >= 135 && angle <= 180) {
			return -SHIFT[180 - angle];
		} else if (angle >= 180 && angle <= 225) {
			return SHIFT[angle - 180];
		} else if (angle >= 45 && angle <= 90) {
			return SHIFT[90 - angle];
		} else if (angle >= 90 && angle <= 135) {
			return -SHIFT[angle - 90];
		} else if (angle >= 225 && angle <= 270) {
			return SHIFT[270 - angle];
		} else {
			return -SHIFT[angle - 270];
		}
	}

	public static float bezierAt(float a, float b, float c, float d, float t) {
		return (MathUtils.pow(1 - t, 3) * a + 3 * t * (MathUtils.pow(1 - t, 2)) * b
				+ 3 * MathUtils.pow(t, 2) * (1 - t) * c + MathUtils.pow(t, 3) * d);
	}

	public static int parseUnsignedInt(String s) {
		return parseUnsignedInt(s, 10);
	}

	public static int parseUnsignedInt(String s, int radix) {
		if (s == null) {
			throw new LSysException("null");
		}
		int len = s.length();
		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar == '-') {
				throw new LSysException("on unsigned string %s.");
			} else {
				if (len <= 5 || (radix == 10 && len <= 9)) {
					return Integer.parseInt(s, radix);
				} else {
					long ell = Long.parseLong(s, radix);
					if ((ell & 0xffff_ffff_0000_0000L) == 0) {
						return (int) ell;
					} else {
						throw new LSysException("range of unsigned int.");
					}
				}
			}
		} else {
			throw new LSysException(s);
		}
	}

	public static int numberOfTrailingZeros(long i) {
		int x, y;
		if (i == 0) {
			return 64;
		}
		int n = 63;
		y = (int) i;
		if (y != 0) {
			n = n - 32;
			x = y;
		} else
			x = (int) (i >>> 32);
		y = x << 16;
		if (y != 0) {
			n = n - 16;
			x = y;
		}
		y = x << 8;
		if (y != 0) {
			n = n - 8;
			x = y;
		}
		y = x << 4;
		if (y != 0) {
			n = n - 4;
			x = y;
		}
		y = x << 2;
		if (y != 0) {
			n = n - 2;
			x = y;
		}
		return n - ((x << 1) >>> 31);

	}

	public static float maxAbs(float x, float y) {
		return MathUtils.abs(x) >= MathUtils.abs(y) ? x : y;
	}

	public static float minAbs(float x, float y) {
		return MathUtils.abs(x) <= MathUtils.abs(y) ? x : y;
	}

	public static float lerpCut(float progress, float progressLowCut, float progressHighCut, float fromValue,
			float toValue) {
		progress = MathUtils.clamp(progress, progressLowCut, progressHighCut);
		float a = (progress - progressLowCut) / (progressHighCut - progressLowCut);
		return MathUtils.lerp(fromValue, toValue, a);
	}

	public static float scale(float value, float maxValue, float maxScale) {
		return (maxScale / maxValue) * value;
	}

	public static float scale(float value, float minValue, float maxValue, float min2, float max2) {
		return min2 + ((value - minValue) / (maxValue - minValue)) * (max2 - min2);
	}

	public static float scaleClamp(float value, float minValue, float maxValue, float min2, float max2) {
		value = min2 + ((value - minValue) / (maxValue - minValue)) * (max2 - min2);
		if (max2 > min2) {
			value = value < max2 ? value : max2;
			return value > min2 ? value : min2;
		}
		value = value < min2 ? value : min2;
		return value > max2 ? value : max2;
	}

	public static float percent(float value, float min, float max) {
		return percent(value, min, max, 1f);
	}

	public static float percent(float value, float min, float max, float upperMax) {
		if (max <= -1f) {
			max = min + 1f;
		}
		float percentage = (value - min) / (max - min);
		if (percentage > 1f) {
			if (upperMax != -1f) {
				percentage = ((upperMax - value)) / (upperMax - max);
				if (percentage < 0f) {
					percentage = 0f;
				}
			} else {
				percentage = 1f;
			}
		} else if (percentage < 0f) {
			percentage = 0f;
		}
		return percentage;
	}

	public static float percent(float value, float percent) {
		return value * (percent * 0.01f);
	}

	public static int percent(int value, int percent) {
		return (int) (value * (percent * 0.01f));
	}

	public static int compare(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public static int compare(float x, float y) {
		if (x < y) {
			return -1;
		}
		if (x > y) {
			return 1;
		}
		int thisBits = NumberUtils.floatToIntBits(x);
		int anotherBits = NumberUtils.floatToIntBits(y);
		return (thisBits == anotherBits ? 0 : (thisBits < anotherBits ? -1 : 1));
	}

	public static boolean isCompare(float x, float y) {
		return isCompare(x, y, EPSILON);
	}

	public static boolean isCompare(XY p1, XY p2) {
		return isCompare(p1, p2, EPSILON);
	}

	public static boolean isCompare(float x, float y, float epsilon) {
		return MathUtils.abs(x - y) <= epsilon * MathUtils.max(1.0f, MathUtils.max(Math.abs(x), MathUtils.abs(y)));
	}

	public static boolean isCompare(XY p1, XY p2, float epsilon) {
		return isCompare(p1.getX(), p2.getX(), epsilon) && isCompare(p1.getY(), p2.getY(), epsilon);
	}

	public static int longOfZeros(long i) {
		if (i == 0) {
			return 64;
		}
		int n = 1;
		int x = (int) (i >>> 32);
		if (x == 0) {
			n += 32;
			x = (int) i;
		}
		if (x >>> 16 == 0) {
			n += 16;
			x <<= 16;
		}
		if (x >>> 24 == 0) {
			n += 8;
			x <<= 8;
		}
		if (x >>> 28 == 0) {
			n += 4;
			x <<= 4;
		}
		if (x >>> 30 == 0) {
			n += 2;
			x <<= 2;
		}
		n -= x >>> 31;
		return n;
	}

	public static float logBase(float b, float v) {
		return log(v) / log(b);
	}

	public static int limit(int i, int min, int max) {
		if (i < min) {
			return min;
		} else if (i > max) {
			return max;
		} else {
			return i;
		}
	}

	public static float limit(float i, float min, float max) {
		if (i < min) {
			return min;
		} else if (i > max) {
			return max;
		} else {
			return i;
		}
	}

	public static float parseAngle(String angle, float value) {
		if (StringUtils.isEmpty(angle)) {
			return 0f;
		}
		angle = angle.toLowerCase();
		if ("deg".equals(angle)) {
			return MathUtils.DEG_TO_RAD * value;
		} else if ("grad".equals(angle)) {
			return MathUtils.PI / 200 * value;
		} else if ("rad".equals(angle)) {
			return value;
		} else if ("turn".equals(angle)) {
			return MathUtils.TWO_PI * value;
		}
		return value;
	}

	public static boolean isLimit(int value, int minX, int maxX) {
		return value >= minX && value <= maxX;
	}

	public static float fixRotation(final float rotation) {
		float newAngle = 0f;
		if (rotation == -360f || rotation == 360f) {
			return newAngle;
		}
		newAngle = rotation;
		if (newAngle < 0f) {
			while (newAngle < -360f) {
				newAngle += 360f;
			}
		}
		if (newAngle > 0f) {
			while (newAngle > 360f) {
				newAngle -= 360f;
			}
		}
		return newAngle;
	}

	public static float fixRotationLimit(final float rotation, final float min, final float max) {
		float result = rotation;
		if (rotation > max) {
			result = max;
		} else if (rotation < min) {
			result = min;
		}
		return fixRotation(result);
	}

	public static float fixAngle(final float angle) {
		float newAngle = 0f;
		if (angle == -TWO_PI || angle == TWO_PI) {
			return newAngle;
		}
		newAngle = angle;
		if (newAngle < 0) {
			while (newAngle < 0) {
				newAngle += TWO_PI;
			}
		}
		if (newAngle > TWO_PI) {
			while (newAngle > TWO_PI) {
				newAngle -= TWO_PI;
			}
		}
		return newAngle;
	}

	public static float fixAngleLimit(final float angle, final float min, final float max) {
		float result = angle;
		if (angle > max) {
			result = max;
		} else if (angle < min) {
			result = min;
		}
		return fixAngle(result);
	}

	public static float adjust(final float angle) {
		float newAngle = angle;
		while (newAngle < 0) {
			newAngle += RAD_FULL;
		}
		while (newAngle > RAD_FULL) {
			newAngle -= RAD_FULL;
		}
		return newAngle;
	}

	public static float getNormalizedAngle(float angle) {
		while (angle < 0) {
			angle += MathUtils.RAD_FULL;
		}
		return angle % MathUtils.RAD_FULL;
	}

	public static int getGreatestCommonDivisor(int a, int b) {
		if (a < b) {
			int t = a;
			a = b;
			b = t;
		}
		for (; b > 0;) {
			int t = a % b;
			a = b;
			b = t;
		}
		return a;
	}

	public static boolean inAngleRange(final float angle, final float startAngle, final float endAngle) {
		float newAngle = adjust(angle);
		float newStartAngle = adjust(startAngle);
		float newEndAngle = adjust(endAngle);
		if (newStartAngle > newEndAngle) {
			newEndAngle += RAD_FULL;
			if (newAngle < newStartAngle) {
				newAngle += RAD_FULL;
			}
		}
		return newAngle >= newStartAngle && newAngle <= newEndAngle;
	}

	/**
	 * 转换坐标为angle
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static float angleFrom(float x1, float y1, float x2, float y2) {
		float diffX = x2 - x1;
		float diffY = y2 - y1;
		return atan2(diffY, diffX);
	}

	/**
	 * 转换坐标为angle
	 * 
	 * @param x
	 * @param y
	 * @param d
	 * @return
	 */
	public static float angleFrom(float x, float y, boolean d) {
		if (d) {
			return atan2(y, x) * RAD_TO_DEG;
		}
		return atan2(y, x);
	}

	public static float radiansFrom(float x, float y) {
		return angleFrom(x, y, false);
	}

	public static float degreesFrom(float x, float y) {
		return angleFrom(x, y, true);
	}

	/**
	 * 滚动指定参数值
	 * 
	 * @param scroll
	 * @param side
	 * @return
	 */
	public static float scroll(float scroll, float side) {
		float start = 0;
		final float v = MathUtils.abs(scroll) % side;
		if (v < 0) {
			start = -(side - v);
		} else if (v > 0) {
			start = -v;
		}
		return start;
	}

	/**
	 * 迭代下降指定数值
	 * 
	 * @param total
	 * @param start
	 * @param side
	 * @return
	 */
	public static float inerations(float start, float side) {
		final float diff = start;
		final float v = diff / side;
		return v + (diff % side > 0 ? 1f : 0f);
	}

	/**
	 * 计算指定数值的阶乘
	 * 
	 * @param v
	 * @return
	 */
	public static float factorial(float v) {
		if (v == 0f) {
			return 1f;
		}
		float result = v;
		while (--v > 0) {
			result *= v;
		}
		return result;
	}

	public static float fmodulo(float v1, float v2) {
		int p = max(precision(v1), precision(v2));
		int e = 1;
		for (int i = 0; i < p; i++) {
			e *= 10;
		}
		int i1 = round(v1 * e);
		int i2 = round(v2 * e);
		return round(i1 % i2 / e, p);
	}

	/**
	 * 让两值做加法,若大于第三值则返回第三值
	 * 
	 * @param v
	 * @param amount
	 * @param max
	 * @return
	 */
	public static float maxAdd(float v, float amount, float max) {
		v += amount;
		if (v > max) {
			v = max;
		}
		return v;
	}

	/**
	 * 让两值做减法,若小于第三值则返回第三值
	 * 
	 * @param v
	 * @param amount
	 * @param min
	 * @return
	 */
	public static float minSub(float v, float amount, float min) {
		v -= amount;
		if (v < min) {
			v = min;
		}
		return v;
	}

	/**
	 * 比较数值大小
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static float wrapCompare(float value, float min, float max) {
		float newValue = value;
		final float step = max - min;

		if (compare(newValue, max) >= 0) {
			for (; compare(newValue, max) >= 0;) {
				newValue -= step;
			}
		} else if (newValue < min) {
			for (; newValue < min;) {
				newValue += step;
			}
		}
		return newValue;
	}

	/**
	 * 返回一个数值增加指定变量后与指定值比较的余数
	 * 
	 * @param v
	 * @param amount
	 * @param max
	 * @return
	 */
	public static float wrapValue(float v, float amount, float max) {
		float diff = 0f;
		v = MathUtils.abs(v);
		amount = MathUtils.abs(amount);
		max = MathUtils.abs(max);
		diff = (v + amount) % max;
		return diff;
	}

	/**
	 * 判定数值是否在指定模糊查询值区间内
	 * 
	 * @param src
	 * @param dst
	 * @param vague
	 * @param found
	 * @param invalid
	 * @return
	 */
	public static float interval(final float src, final float dst, final float vague, final float found,
			final float invalid) {
		if ((src + vague == dst) || (src - vague) == dst) {
			return found;
		}
		final float result = (dst - src);
		if (result > 0) {
			return result <= vague ? found : invalid;
		} else if (result < 0) {
			return (-result) <= vague ? found : invalid;
		} else {
			return found;
		}
	}

	/**
	 * 判定数值是否在指定模糊查询值区间内
	 * 
	 * @param src
	 * @param dst
	 * @param vague
	 * @param found
	 * @param invalid
	 * @return
	 */
	public static int intervalInt(final int src, final int dst, final int vague, final int found, final int invalid) {
		if ((src + vague == dst) || (src - vague) == dst) {
			return found;
		}
		final int result = (dst - src);
		if (result > 0) {
			return result <= vague ? found : invalid;
		} else if (result < 0) {
			return (-result) <= vague ? found : invalid;
		} else {
			return found;
		}
	}

	/**
	 * 返回一个概率事件在100%范围是否被触发的布尔值
	 * 
	 * @param chance>0 && <100
	 * @return
	 */
	public static boolean chanceRoll(final float chance) {
		return chanceRoll(chance, 100f);
	}

	/**
	 * 返回一个概率事件是否被触发的布尔值
	 * 
	 * @param chance
	 * @param max
	 * @return
	 */
	public static boolean chanceRoll(final float chance, final float max) {
		if (chance <= 0f) {
			return false;
		} else if (chance >= max) {
			return true;
		} else if (MathUtils.random(max) >= chance) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 返回一个对象集合中的随机概率对象
	 * 
	 * @param <T>
	 * @param values
	 * @return
	 */
	public static <T> T chanceRollValues(final T[] values) {
		final int maxIndex = max(0, values.length - 1);
		final int rolledIndex = round(random() * maxIndex);
		return values[rolledIndex];
	}

	/**
	 * 返回一个值在指定概率范围内是否可能被触发
	 * 
	 * @param k
	 * @param p
	 * @return
	 */
	public static boolean isSuccess(final float k, final float p) {
		return chanceRoll(k, p);
	}

	/**
	 * 返回一个值在100%概率范围内是否可能被触发
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isSuccess(final float p) {
		return chanceRoll(p);
	}
}
