package loon.utils;

import com.google.gwt.corp.compatibility.Numbers;

public class NumberUtils {

	public static int floatToIntBits(float value) {
		return Numbers.floatToIntBits(value);
	}

	public static int floatToRawIntBits(float value) {
		return Numbers.floatToIntBits(value);
	}

	public static int floatToIntColor(float value) {
		return Numbers.floatToIntBits(value);
	}

	public static float intToFloatColor(int value) {
		return Numbers.intBitsToFloat(value & 0xfeffffff);
	}

	public static float intBitsToFloat(int value) {
		return Numbers.intBitsToFloat(value);
	}

	public static double longBitsToDouble(long value) {
		return Numbers.longBitsToDouble(value);
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
