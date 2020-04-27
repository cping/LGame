/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.utils;

import loon.LSystem;
import loon.ZIndex;
import loon.canvas.LColor;
import loon.utils.reply.Pair;
import loon.utils.reply.Triple;

/**
 * 辅助用类,实现了一些常见的数值转换功能,可以在Screen中直接调用
 *
 */
public class HelperUtils {

	public static <T1, T2> Pair<T1, T2> toPair(T1 a, T2 b) {
		return Pair.get(a, b);
	}

	public static <T1, T2, T3> Triple<T1, T2, T3> toTriple(T1 a, T2 b, T3 c) {
		return Triple.get(a, b, c);
	}

	public static <T> T getValue(T val, T defval) {
		return val == null ? defval : val;
	}

	public static boolean toOrder(ZIndex[] array) {
		if (array == null || array.length < 2) {
			return false;
		}
		final int len = array.length;
		int key = 0;
		ZIndex cur;
		for (int i = 1, j = 0; i < len; i++) {
			j = i;
			cur = array[j];
			key = array[j].getLayer();
			for (;--j > -1;) {
				if (array[j].getLayer() > key) {
					array[j + 1] = array[j];
				} else {
					break;
				}
			}
			array[j + 1] = cur;
		}
		return true;
	}
	
	public final static LColor toColor(int r, int g, int b, int a) {
		return new LColor(r, g, b, a);
	}

	public final static LColor toColor(int r, int g, int b) {
		return new LColor(r, g, b);
	}

	public final static LColor toColor(String c) {
		if (StringUtils.isEmpty(c)) {
			return new LColor();
		}
		return new LColor(c);
	}

	public final static double toDouble(Object o) {
		if (o == null) {
			return -1d;
		}
		if (o instanceof Short) {
			return ((Short) o).doubleValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).doubleValue();
		}
		if (o instanceof Long) {
			return ((Long) o).doubleValue();
		}
		if (o instanceof Float) {
			return ((Float) o).doubleValue();
		}
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		}
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).doubleValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).doubleValue();
			}
		}
		return -1d;
	}

	public final static float toFloat(Object o) {
		if (o == null) {
			return -1f;
		}
		if (o instanceof Short) {
			return ((Short) o).floatValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).floatValue();
		}
		if (o instanceof Long) {
			return ((Long) o).floatValue();
		}
		if (o instanceof Float) {
			return ((Float) o).floatValue();
		}
		if (o instanceof Double) {
			return ((Double) o).floatValue();
		}
		if (o instanceof Number) {
			return ((Number) o).floatValue();
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).floatValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).floatValue();
			}
		}
		return -1f;
	}

	public final static int toInt(Object o) {
		if (o == null) {
			return -1;
		}
		if (o instanceof Short) {
			return ((Short) o).intValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		}
		if (o instanceof Long) {
			return ((Long) o).intValue();
		}
		if (o instanceof Float) {
			return ((Float) o).intValue();
		}
		if (o instanceof Double) {
			return ((Double) o).intValue();
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).intValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).intValue();
			}
		}
		return -1;
	}

	public final static long toLong(Object o) {
		if (o == null) {
			return -1l;
		}
		if (o instanceof Short) {
			return ((Short) o).longValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).longValue();
		}
		if (o instanceof Long) {
			return ((Long) o).longValue();
		}
		if (o instanceof Float) {
			return ((Float) o).longValue();
		}
		if (o instanceof Double) {
			return ((Double) o).longValue();
		}
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).longValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).longValue();
			}
		}
		return -1l;
	}

	public final static String toStr(Object o) {
		if (o == null) {
			return "";
		}
		if (o instanceof Short) {
			return String.valueOf(((Short) o).shortValue());
		}
		if (o instanceof Integer) {
			return String.valueOf(((Integer) o).intValue());
		}
		if (o instanceof Long) {
			return String.valueOf(((Long) o).longValue());
		}
		if (o instanceof Float) {
			return String.valueOf(((Float) o).floatValue());
		}
		if (o instanceof Double) {
			return String.valueOf(((Double) o).doubleValue());
		}
		if (o instanceof Number) {
			return String.valueOf(((Number) o).floatValue());
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			return String.valueOf(vc);
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				if (v.indexOf('.') != -1) {
					return String.valueOf(Float.valueOf(v).floatValue());
				} else {
					return String.valueOf(Float.valueOf(v).intValue());
				}
			} else {
				return v;
			}
		}
		return LSystem.UNKOWN;
	}

}
