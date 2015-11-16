package loon.utils;

public final class NumberUtils {

	public static int floatToIntBits(float value) {
		return Float.floatToIntBits(value);
	}

	public static int floatToRawIntBits(float value) {
		return Float.floatToRawIntBits(value);
	}

	public static int floatToIntColor(float value) {
		return Float.floatToRawIntBits(value);
	}

	public static float intToFloatColor(int value) {
		return Float.intBitsToFloat(value & 0xfeffffff);
	}

	public static float intBitsToFloat(int value) {
		return Float.intBitsToFloat(value);
	}

	public static int compare(float f1, float f2) {
		if (f1 < f2) {
			return -1;
		}
		if (f1 > f2) {
			return 1;
		}
		int thisBits = floatToIntBits(f1);
		int anotherBits = floatToIntBits(f2);
		return (thisBits == anotherBits ? 0 : (thisBits < anotherBits ? -1 : 1));
	}

}
