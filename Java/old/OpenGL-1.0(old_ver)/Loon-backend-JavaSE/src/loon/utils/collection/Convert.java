/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.utils.collection;

import java.math.BigInteger;

public class Convert {

	public static Number OR(Number a, Number b) {
		if (a.longValue() > 0) {
			return a;
		}
		if (b.longValue() > 0) {
			return b;
		}
		return a;
	}

	public static int[] objectToPrim(Integer[] is) {
		int[] integers = new int[is.length];
		for (int i = 0; i < is.length; i++) {
			integers[i] = is[i].intValue();
		}
		return integers;
	}

	public static long[] objectToPrim(Long[] ls) {
		long[] longs = new long[ls.length];
		for (int i = 0; i < ls.length; i++) {
			longs[i] = ls[i].longValue();
		}
		return longs;
	}

	public static short[] objectToPrim(Short[] ss) {
		short[] shorts = new short[ss.length];
		for (int i = 0; i < ss.length; i++) {
			shorts[i] = ss[i].shortValue();
		}
		return shorts;
	}

	public static double[] objectToPrim(Double[] ds) {
		double[] doubles = new double[ds.length];
		for (int i = 0; i < ds.length; i++) {
			doubles[i] = ds[i].doubleValue();
		}
		return doubles;
	}

	public static float[] objectToPrim(Float[] fs) {
		float[] floats = new float[fs.length];
		for (int i = 0; i < fs.length; i++) {
			floats[i] = fs[i].floatValue();
		}
		return floats;
	}

	public static boolean[] objectToPrim(Boolean[] bs) {
		boolean[] booleans = new boolean[bs.length];
		for (int i = 0; i < bs.length; i++) {
			booleans[i] = bs[i].booleanValue();
		}
		return booleans;
	}

	public static char[] objectToPrim(Character[] cs) {
		char[] chars = new char[cs.length];
		for (int i = 0; i < cs.length; i++) {
			chars[i] = cs[i].charValue();
		}
		return chars;
	}

	public static byte[] objectToPrim(Byte[] bys) {
		byte[] bytes = new byte[bys.length];
		for (int i = 0; i < bys.length; i++) {
			bytes[i] = bys[i].byteValue();
		}
		return bytes;
	}

	public static long MOVE_LeftShift(Number v, int pos) {
		return (int) (v.intValue() << pos);
	}

	public static long MOVE_RightUShift(Number v, int pos) {
		if (pos == 0) {
			String bin = Long.toBinaryString(v.longValue());
			if (bin.length() > 31) {
				bin = bin.substring(bin.length() - 32, bin.length());
			} else {
				return (v.intValue());
			}
			return new BigInteger(bin, 2).longValue();
		}
		return (v.intValue() >>> pos);
	}

	public static long MOVE_RightShift(Number v, int pos) {
		if (pos == 0) {
			String bin = Long.toBinaryString(v.longValue());
			if (bin.length() > 31) {
				bin = bin.substring(bin.length() - 32, bin.length());
			} else {
				return (v.intValue());
			}
			return new BigInteger(bin, 2).longValue();
		}
		return (v.intValue() >> pos);
	}

	public static long get(Number v) {
		return v.intValue();
	}

}
