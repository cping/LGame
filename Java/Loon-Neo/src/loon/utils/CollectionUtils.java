/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1.1
 */
package loon.utils;

import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.component.Actor;
import loon.component.LComponent;
import loon.physics.PBody;
import loon.physics.PConvexPolygonShape;
import loon.physics.PJoint;
import loon.physics.PShape;
import loon.physics.PSolver;
import loon.physics.PSortableObject;

final public class CollectionUtils {

	final static public int INITIAL_CAPACITY = 12;

	protected CollectionUtils() {
		super();
	}

	/**
	 * 判定指定对象是否存在于指定对象数组中
	 * 
	 * @param arrays
	 * @param data
	 * @return
	 */
	public static int indexOf(Object[] arrays, Object data) {
		int len = arrays.length - 1;
		int count = 0;
		for (int i = len; i >= 0; i--) {
			Object o = arrays[i];
			if (o == data || (o != null && o.equals(data))) {
				return len - count;
			}
			count++;
		}
		return -1;
	}

	/**
	 * 获得指定2维数组的HashCode
	 * 
	 * @param arrays
	 * @return
	 */
	public static int hashCode(int[][] arrays) {
		if (arrays == null) {
			return 0;
		}
		int result = 1;
		int h = arrays.length;
		int w = arrays[0].length;
		int value = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				value = arrays[i][j];
				int elementHash = (value ^ (value >>> 32));
				result = 31 * result + elementHash;
			}
		}
		return result;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static int[][] copyOf(int[][] data) {
		int size = data.length;
		int[][] copy = new int[size][];
		for (int i = 0; i < size; i++) {
			int len = data[i].length;
			int[] res = new int[len];
			System.arraycopy(data[i], 0, res, 0, len);
			copy[i] = res;
		}
		return copy;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static LComponent[] copyOf(LComponent[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static LComponent[] copyOf(LComponent[] data, int newSize) {
		LComponent tempArr[] = new LComponent[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static String[] copyOf(String[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static String[] copyOf(String[] data, int newSize) {
		String tempArr[] = new String[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static ISprite[] copyOf(ISprite[] data, int newSize) {
		ISprite tempArr[] = new ISprite[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static Actor[] copyOf(Actor[] data, int newSize) {
		Actor tempArr[] = new Actor[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static Object[] copyOf(Object[] data, int newSize) {
		Object tempArr[] = new Object[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static int[] copyOf(int[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static int[] copyOf(int[] data, int newSize) {
		int tempArr[] = new int[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static double[] copyOf(double[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static double[] copyOf(double[] data, int newSize) {
		double tempArr[] = new double[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static float[] copyOf(float[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static float[] copyOf(float[] data, int newSize) {
		float tempArr[] = new float[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] copyOf(byte[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static byte[] copyOf(byte[] data, int newSize) {
		byte tempArr[] = new byte[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static char[] copyOf(char[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static char[] copyOf(char[] data, int newSize) {
		char tempArr[] = new char[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static long[] copyOf(long[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static long[] copyOf(long[] data, int newSize) {
		long tempArr[] = new long[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @return
	 */
	public static boolean[] copyOf(boolean[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static boolean[] copyOf(boolean[] data, int newSize) {
		boolean tempArr[] = new boolean[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	// --为了兼容GWT，尽量减少反射的使用，所以只好针对不同类分别处理了……--//
	/**
	 * 
	 * public static Object expand(Object data, int i, boolean flag) { int j =
	 * ArrayReflection.getLength(data); Object obj1 =
	 * ArrayReflection.newInstance(data.getClass().getComponentType(), j + i);
	 * System.arraycopy(data, 0, obj1, flag ? 0 : i, j); return obj1; }
	 * 
	 * public static Object expand(Object data, int size) { return expand(data,
	 * size, true); }
	 * 
	 * public static Object expand(Object data, int size, boolean flag, Class<?>
	 * class1) { if (data == null) { return ArrayReflection.newInstance(class1,
	 * 1); } else { return expand(data, size, flag); } }
	 * 
	 * public static Object cut(Object data, int size) { int j; if ((j =
	 * ArrayReflection.getLength(data)) == 1) { return
	 * ArrayReflection.newInstance(data.getClass().getComponentType(), 0); } int
	 * k; if ((k = j - size - 1) > 0) { System.arraycopy(data, size + 1, data,
	 * size, k); } j--; Object obj1 =
	 * ArrayReflection.newInstance(data.getClass().getComponentType(), j);
	 * System.arraycopy(data, 0, obj1, 0, j); return obj1; }
	 * 
	 * public static Object copyOf(Object src) { int srcLength =
	 * ArrayReflection.getLength(src); Class<?> srcComponentType =
	 * src.getClass().getComponentType(); Object dest =
	 * ArrayReflection.newInstance(srcComponentType, srcLength); if
	 * (srcComponentType.isArray()) { for (int i = 0; i <
	 * ArrayReflection.getLength(src); i++) { ArrayReflection.set(dest, i,
	 * copyOf(ArrayReflection.get(src, i))); } } else { System.arraycopy(src, 0,
	 * dest, 0, srcLength); } return dest; }
	 * 
	 * 
	 * public static Object[] copyOf(Object[] original, int newLength) { return
	 * copyOf(original, newLength, original.getClass()); }
	 * 
	 * public static Object[] copyOf(Object[] original, int newLength, Class<?>
	 * newType) { Object[] copy = (newType == Object[].class) ? new
	 * Object[newLength] : (Object[])
	 * ArrayReflection.newInstance(newType.getComponentType(), newLength);
	 * System.arraycopy(original, 0, copy, 0, MathUtils.min(original.length,
	 * newLength)); return copy; }
	 */

	public static String[] expand(String[] objs, int size) {
		return expand(objs, size, true);
	}

	public static String[] expand(String[] objs, int i, boolean flag) {
		int size = objs.length;
		String[] newArrays = new String[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static int[] expand(int[] objs, int size) {
		return expand(objs, size, true);
	}

	public static int[] expand(int[] objs, int i, boolean flag) {
		int size = objs.length;
		int[] newArrays = new int[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static float[] expand(float[] objs, int size) {
		return expand(objs, size, true);
	}

	public static float[] expand(float[] objs, int i, boolean flag) {
		int size = objs.length;
		float[] newArrays = new float[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static boolean[] expand(boolean[] objs, int size) {
		return expand(objs, size, true);
	}

	public static boolean[] expand(boolean[] objs, int i, boolean flag) {
		int size = objs.length;
		boolean[] newArrays = new boolean[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static ISprite[] expand(ISprite[] objs, int size) {
		return expand(objs, size, true);
	}

	public static ISprite[] expand(ISprite[] objs, int i, boolean flag) {
		int size = objs.length;
		ISprite[] newArrays = new ISprite[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static ISprite[] cut(ISprite[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new ISprite[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		ISprite[] newArrays = new ISprite[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	public static Object[] cutObject(Object[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new Object[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		Object[] newArrays = new Object[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	public static Actor[] expand(Actor[] objs, int i, boolean flag) {
		int size = objs.length;
		Actor[] newArrays = new Actor[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static Actor[] cut(Actor[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new Actor[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		Actor[] newArrays = new Actor[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	public static LComponent[] expand(LComponent[] objs, int size) {
		return expand(objs, size, true);
	}

	public static LComponent[] expand(LComponent[] objs, int i, boolean flag) {
		int size = objs.length;
		LComponent[] newArrays = new LComponent[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static LComponent[] cut(LComponent[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new LComponent[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		LComponent[] newArrays = new LComponent[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] data, int newSize) {
		PConvexPolygonShape tempArr[] = new PConvexPolygonShape[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] data) {
		return copyOf(data, data.length);
	}

	public static PBody[] copyOf(PBody[] data, int newSize) {
		PBody tempArr[] = new PBody[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PBody[] copyOf(PBody[] data) {
		return copyOf(data, data.length);
	}

	public static PJoint[] copyOf(PJoint[] data) {
		return copyOf(data, data.length);
	}

	public static PJoint[] copyOf(PJoint[] data, int newSize) {
		PJoint tempArr[] = new PJoint[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PSolver[] copyOf(PSolver[] data) {
		return copyOf(data, data.length);
	}

	public static PSolver[] copyOf(PSolver[] data, int newSize) {
		PSolver tempArr[] = new PSolver[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PShape[] copyOf(PShape[] data) {
		return copyOf(data, data.length);
	}

	public static PShape[] copyOf(PShape[] data, int newSize) {
		PShape tempArr[] = new PShape[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PSortableObject[] copyOf(PSortableObject[] data) {
		return copyOf(data, data.length);
	}

	public static PSortableObject[] copyOf(PSortableObject[] data, int newSize) {
		PSortableObject tempArr[] = new PSortableObject[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static LTexture[] copyOf(LTexture[] data, int newSize) {
		LTexture tempArr[] = new LTexture[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static LTexture[] copyOf(LTexture[] data) {
		return copyOf(data, data.length);
	}

	public static ArrayMap.Entry[] copyOf(ArrayMap.Entry[] data, int newSize) {
		ArrayMap.Entry tempArr[] = new ArrayMap.Entry[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static ArrayMap.Entry[] copyOf(ArrayMap.Entry[] data) {
		return copyOf(data, data.length);
	}

	public static IntHashMap.Entry[] copyOf(IntHashMap.Entry[] data, int newSize) {
		IntHashMap.Entry tempArr[] = new IntHashMap.Entry[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static IntHashMap.Entry[] copyOf(IntHashMap.Entry[] data) {
		return copyOf(data, data.length);
	}

	/**
	 * 反转数组自身
	 * 
	 * @param arrays
	 */
	public static <T> void reverse(T[] arrays) {
		for (int i = 0, size = arrays.length; i < size; i++) {
			int idx = i;
			int last = size - 1 - i;
			if (idx == last || idx > last) {
				break;
			}
			T data = arrays[idx];
			T swap = arrays[last];
			arrays[idx] = swap;
			arrays[last] = data;
		}
	}

	/**
	 * 移除一个对象数组中数据
	 * 
	 * @param arrays
	 * @param index
	 * @return
	 */
	public static Object spliceObjectOne(Object[] arrays, int index) {
		if (index >= arrays.length) {
			return null;
		}
		int len = arrays.length - 1;
		Object item = arrays[index];
		for (int i = index; i < len; i++) {
			arrays[i] = arrays[i + 1];
		}
		CollectionUtils.cutObject(arrays, len);
		return item;
	}

	/**
	 * 交换对象数组中两个对象的位置
	 * 
	 * @param arrays
	 * @param data1
	 * @param data2
	 */
	public static void swapObject(Object[] arrays, Object data1, Object data2) {
		if (data1 == data2) {
			return;
		}
		int index1 = indexOf(arrays, data1);
		int index2 = indexOf(arrays, data2);
		if (index1 < 0 || index2 < 0) {
			return;
		}
		arrays[index1] = data2;
		arrays[index2] = data1;
		return;
	}

	/**
	 * 替换对象数组中的指定对象
	 * 
	 * @param arrays
	 * @param oldChild
	 * @param newChild
	 * @return
	 */
	public static boolean replaceObject(Object[] arrays, Object oldChild, Object newChild) {
		int index1 = indexOf(arrays, oldChild);
		int index2 = indexOf(arrays, newChild);
		if (index1 != -1 && index2 == -1) {
			arrays[index1] = newChild;
			return true;
		} else {
			return false;
		}
	}
}
