package loon.utils;

import android.util.FloatMath;
import loon.core.LSystem;
import loon.core.geom.RectBox;

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
public class MathUtils {

	public static RectBox getBounds(float x, float y, float width,
			float height, float rotate, RectBox result) {
		int[] rect = getLimit(x, y, width, height, rotate);
		if (result == null) {
			result = new RectBox(rect[0], rect[1], rect[2], rect[3]);
		} else {
			result.setBounds(rect[0], rect[1], rect[2], rect[3]);
		}
		return result;
	}

	public static RectBox getBounds(float x, float y, float width,
			float height, float rotate) {
		return getBounds(x, y, width, height, rotate, null);
	}

	public static int[] getLimit(float x, float y, float width, float height,
			float rotate) {
		float rotation = MathUtils.toRadians(rotate);
		float angSin = MathUtils.sin(rotation);
		float angCos = MathUtils.cos(rotation);
		int newW = MathUtils.floor((width * MathUtils.abs(angCos))
				+ (height * MathUtils.abs(angSin)));
		int newH = MathUtils.floor((height * MathUtils.abs(angCos))
				+ (width * MathUtils.abs(angSin)));
		int centerX = (int) (x + (width / 2));
		int centerY = (int) (y + (height / 2));
		int newX = (centerX - (newW / 2));
		int newY = (centerY - (newH / 2));
		return new int[] { newX, newY, newW, newH };
	}

	final static private String[] zeros = { "", "0", "00", "000", "0000",
			"00000", "000000", "0000000", "00000000", "000000000", "0000000000" };

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
	 * @param numDigits
	 * @return
	 */
	public static String addZeros(String number, int numDigits) {
		int length = numDigits - number.length();
		if (length != 0) {
			number = zeros[length] + number;
		}
		return number;
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
		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				int i = start + 2;
				if (i == sz) {
					return false;
				}
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9')
							&& (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--;
		int i = start;
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
			if (!allowSigns
					&& (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				return foundDigit && !hasExp;
			}
			return false;
		}
		return !allowSigns && foundDigit;
	}

	public static final float PI_OVER2 = 1.5708f;

	public static final float PI_OVER4 = 0.785398f;

	static private final int BIG_ENOUGH_INT = 16 * 1024;

	static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;

	static private final double CEIL = 0.9999999;

	static private final double BIG_ENOUGH_CEIL = Double
			.longBitsToDouble(Double.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);

	static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

	static private final int ATAN2_BITS = 7;

	static private final int ATAN2_BITS2 = ATAN2_BITS << 1;

	static private final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);

	static private final int ATAN2_COUNT = ATAN2_MASK + 1;

	static private final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	static private final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

	static private final float[] atan2 = new float[ATAN2_COUNT];

	public static final float PI = 3.1415927f;

	public static final float TWO_PI = 6.28319f;

	static private final int SIN_BITS = 13;

	static private final int SIN_MASK = ~(-1 << SIN_BITS);

	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;

	static private final float degFull = 360;

	static private final float radToIndex = SIN_COUNT / radFull;

	static private final float degToIndex = SIN_COUNT / degFull;

	public static final float RAD_TO_DEG = 180.0f / PI;

	public static final float DEG_TO_RAD = PI / 180.0f;

	public static final float[] sin = new float[SIN_COUNT];

	public static final float[] cos = new float[SIN_COUNT];

	static {
		for (int i = 0; i < SIN_COUNT; i++) {
			float a = (i + 0.5f) / SIN_COUNT * radFull;
			sin[i] = FloatMath.sin(a);
			cos[i] = FloatMath.cos(a);
		}
		for (int i = 0; i < 360; i += 90) {
			sin[(int) (i * degToIndex) & SIN_MASK] = FloatMath.sin(i
					* DEG_TO_RAD);
			cos[(int) (i * degToIndex) & SIN_MASK] = FloatMath.cos(i
					* DEG_TO_RAD);
		}
	}

	public static final int ZERO_FIXED = 0;

	public static final int ONE_FIXED = 1 << 16;

	public static final int ONE_HALF_FIXED = fromFloat(0.5f);

	public static final double EPSILON = 2.220446049250313E-16d;

	public static final int EPSILON_FIXED = fromFloat(0.002f);

	public static final int PI_FIXED = 205887;

	public static final int PI_OVER_2_FIXED = PI_FIXED / 2;

	public static final int E_FIXED = 178145;

	public static final int HALF_FIXED = 2 << 15;

	public static boolean isZero(float value) {
		return Math.abs(value) <= 0.00000001;
	}

	public static int toInt(int x) {
		return x >> 16;
	}

	public static double toDouble(int x) {
		return (double) x / ONE_FIXED;
	}

	public static float toFloat(int x) {
		return (float) x / ONE_FIXED;
	}

	public static int fromInt(int x) {
		return x << 16;
	}

	public static int fromFloat(float x) {
		return (int) (x * ONE_FIXED);
	}

	public static int fromDouble(double x) {
		return (int) (x * ONE_FIXED);
	}

	public static int mul(int x, int y) {
		long z = (long) x * (long) y;
		return ((int) (z >> 16));
	}

	public static int mid(int i, int min, int max) {
		return MathUtils.max(i, MathUtils.min(min, max));
	}

	public static int div(int x, int y) {
		long z = (((long) x) << 32);
		return (int) ((z / y) >> 16);
	}

	public static double sqrt(double n) {
		return Math.round(n);
	}

	public static int sqrt(int n) {
		int s = (n + 65536) >> 1;
		for (int i = 0; i < 8; i++) {
			s = (s + div(n, s)) >> 1;
		}
		return s;
	}

	public static double round(double n) {
		return Math.round(n);
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

	public static boolean equal(int a, int b) {
		if (a > b)
			return a - b <= EPSILON_FIXED;
		else
			return b - a <= EPSILON_FIXED;
	}

	static final int SK1 = 498;

	static final int SK2 = 10882;

	public static int sin(int f) {
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

	static final int CK1 = 2328;

	static final int CK2 = 32551;

	private static double reduceSinAngle(double radians) {
		radians %= Math.PI * 2.0;
		if (Math.abs(radians) > Math.PI) {
			radians = radians - (Math.PI * 2.0);
		}
		if (Math.abs(radians) > Math.PI / 2) {
			radians = Math.PI - radians;
		}
		return radians;
	}

	public static double sin(double radians) {
		radians = reduceSinAngle(radians);
		if (Math.abs(radians) <= Math.PI / 4) {
			return Math.sin(radians);
		} else {
			return Math.cos(Math.PI / 2 - radians);
		}
	}

	public static double cos(double radians) {
		return sin(radians + Math.PI / 2);
	}

	public static int cos(int f) {
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

	static final int TK1 = 13323;

	static final int TK2 = 20810;

	public static int tan(int f) {
		int sqr = mul(f, f);
		int result = TK1;
		result = mul(result, sqr);
		result += TK2;
		result = mul(result, sqr);
		result += ONE_FIXED;
		result = mul(result, f);
		return result;
	}

	public static int atan(int f) {
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

	static final int AS1 = -1228;

	static final int AS2 = 4866;

	static final int AS3 = 13901;

	static final int AS4 = 102939;

	public static int asin(int f) {
		int fRoot = sqrt(ONE_FIXED - f);
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

	public static int acos(int f) {
		int fRoot = sqrt(ONE_FIXED - f);
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

	static int log2arr[] = { 26573, 14624, 7719, 3973, 2017, 1016, 510, 256,
			128, 64, 32, 16, 8, 4, 2, 1, 0, 0, 0 };

	static int lnscale[] = { 0, 45426, 90852, 136278, 181704, 227130, 272557,
			317983, 363409, 408835, 454261, 499687, 545113, 590539, 635965,
			681391, 726817 };

	public static int ln(int x) {
		int shift = 0;
		while (x > 1 << 17) {
			shift++;
			x >>= 1;
		}
		int g = 0;
		int d = HALF_FIXED;
		for (int i = 1; i < 16; i++) {
			if (x > (ONE_FIXED + d)) {
				x = div(x, (ONE_FIXED + d));
				g += log2arr[i - 1];
			}
			d >>= 1;
		}
		return g + lnscale[shift];
	}

	static public final float tan(float angle) {
		return (float) Math.tan(angle);
	}

	static public final float asin(float value) {
		return (float) Math.asin(value);
	}

	static public final float acos(float value) {
		return (float) Math.acos(value);
	}

	static public final float atan(float value) {
		return (float) Math.atan(value);
	}

	static public final float mag(float a, float b) {
		return FloatMath.sqrt(a * a + b * b);
	}

	static public final float mag(float a, float b, float c) {
		return FloatMath.sqrt(a * a + b * b + c * c);
	}

	static public final float dist(float x1, float y1, float x2, float y2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	static public final float dist(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
	}

	static public final double abs(double n) {
		return Math.abs(n);
	}

	static public final float abs(float n) {
		return (n < 0) ? -n : n;
	}

	static public final int abs(int n) {
		return (n < 0) ? -n : n;
	}

	static public final float sq(float a) {
		return a * a;
	}

	static public final float sqrt(float a) {
		return FloatMath.sqrt(a);
	}

	static public final float log(float a) {
		return (float) Math.log(a);
	}

	static public final float exp(float a) {
		return (float) Math.exp(a);
	}

	static public final float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}

	static public final int max(int a, int b) {
		return (a > b) ? a : b;
	}

	static public final float max(float a, float b) {
		return (a > b) ? a : b;
	}

	static public final long max(long a, long b) {
		return (a > b) ? a : b;
	}

	static public final int max(int a, int b, int c) {
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	static public final float max(float a, float b, float c) {
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	static public final int min(int a, int b, int c) {
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

	static public final float min(float a, float b, float c) {
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

	static public final float min(float a, float b) {
		return (a <= b) ? a : b;
	}

	public static int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	static public final float norm(float value, float start, float stop) {
		return (value - start) / (stop - start);
	}

	static public final float map(float value, float istart, float istop,
			float ostart, float ostop) {
		return ostart + (ostop - ostart)
				* ((value - istart) / (istop - istart));
	}

	static public final float degrees(float radians) {
		return radians * MathUtils.RAD_TO_DEG;
	}

	static public final float radians(float degrees) {
		return degrees * MathUtils.DEG_TO_RAD;
	}

	public static final float sin(float rad) {
		return sin[(int) (rad * radToIndex) & SIN_MASK];
	}

	public static final float cos(float rad) {
		return cos[(int) (rad * radToIndex) & SIN_MASK];
	}

	public static final float sinDeg(float deg) {
		return sin[(int) (deg * degToIndex) & SIN_MASK];
	}

	public static final float cosDeg(float deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}

	static {
		for (int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				float x0 = (float) i / ATAN2_DIM;
				float y0 = (float) j / ATAN2_DIM;
				atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
			}
		}
	}

	public static double atan2(double y, double x) {
		if (y == 0.0D && x == 0.0D) {
			return Math.atan2(0.0D, 1.0D);
		} else {
			return Math.atan2(y, x);
		}
	}

	public static final float atan2(float y, float x) {
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
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}

	public static final float radToDeg(final float rad) {
		return RAD_TO_DEG * rad;
	}

	public static final int bringToBounds(final int minValue,
			final int maxValue, final int v) {
		return Math.max(minValue, Math.min(maxValue, v));
	}

	public static final float bringToBounds(final float minValue,
			final float maxValue, final float v) {
		return Math.max(minValue, Math.min(maxValue, v));
	}

	public static final int nextInt(int range) {
		return range <= 0 ? 0 : LSystem.random.nextInt(range);
	}

	public static final int nextInt(int start, int end) {
		return end <= 0 ? 0 : start + LSystem.random.nextInt(end - start);
	}

	public static final int random(int range) {
		return LSystem.random.nextInt(range + 1);
	}

	public static final int random(int start, int end) {
		return start + LSystem.random.nextInt(end - start + 1);
	}

	public static final boolean randomBoolean() {
		return LSystem.random.nextBoolean();
	}

	public static final float random() {
		return LSystem.random.nextFloat();
	}

	public static final float random(float range) {
		return LSystem.random.nextFloat() * range;
	}

	public static final float random(float start, float end) {
		return start + LSystem.random.nextFloat() * (end - start);
	}

	public static int floor(float x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	public static int floorPositive(float x) {
		return (int) x;
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

	public static int roundPositive(float x) {
		return (int) (x + 0.5f);
	}

	public static float barycentric(float value1, float value2, float value3,
			float amount1, float amount2) {
		return value1 + (value2 - value1) * amount1 + (value3 - value1)
				* amount2;
	}

	public static float catmullRom(float value1, float value2, float value3,
			float value4, float amount) {
		double amountSquared = amount * amount;
		double amountCubed = amountSquared * amount;
		return (float) (0.5 * (2.0 * value2 + (value3 - value1) * amount
				+ (2.0 * value1 - 5.0 * value2 + 4.0 * value3 - value4)
				* amountSquared + (3.0 * value2 - value1 - 3.0 * value3 + value4)
				* amountCubed));
	}

	public static float clamp(float value, float min, float max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static float distance(float value1, float value2) {
		return Math.abs(value1 - value2);
	}

	public static float hermite(float value1, float tangent1, float value2,
			float tangent2, float amount) {
		double v1 = value1, v2 = value2, t1 = tangent1, t2 = tangent2, s = amount, result;
		double sCubed = s * s * s;
		double sSquared = s * s;

		if (amount == 0f) {
			result = value1;
		} else if (amount == 1f) {
			result = value2;
		} else {
			result = (2 * v1 - 2 * v2 + t2 + t1) * sCubed
					+ (3 * v2 - 3 * v1 - 2 * t1 - t2) * sSquared + t1 * s + v1;
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

	public static float toDegrees(float radians) {
		return (float) (radians * 57.295779513082320876798154814105);
	}

	public static float toRadians(float degrees) {
		return (float) (degrees * 0.017453292519943295769236907684886);
	}

	public static float wrapAngle(float angle) {
		angle = (float) Math.IEEEremainder(angle, 6.2831854820251465d);
		if (angle <= -3.141593f) {
			angle += 6.283185f;
			return angle;
		}
		if (angle > 3.141593f) {
			angle -= 6.283185f;
		}
		return angle;
	}
}
