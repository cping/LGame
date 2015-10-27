package loon.utils;

import com.google.gwt.corp.compatibility.Numbers;

public class NumberUtils {

	public static int floatToIntBits (float value) {
		return Numbers.floatToIntBits(value);
	}

	public static int floatToRawIntBits (float value) {
		return Numbers.floatToIntBits(value);
	}

	public static int floatToIntColor (float value) {
		return Numbers.floatToIntBits(value);
	}

	public static float intToFloatColor (int value) {
		return Numbers.intBitsToFloat(value & 0xfeffffff);
	}

	public static float intBitsToFloat (int value) {
		return Numbers.intBitsToFloat(value);
	}

}
