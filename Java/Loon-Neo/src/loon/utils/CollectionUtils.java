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

import loon.LSysException;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.component.Actor;
import loon.component.LComponent;
import loon.geom.Vector2f;
import loon.opengl.VertexAttribute;

final public class CollectionUtils {

	final static public int INITIAL_CAPACITY = 16;

	private CollectionUtils() {
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
		final int size = data.length;
		final int[][] copy = new int[size][];
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
		final LComponent[] tempArr = new LComponent[newSize];
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
		final String[] tempArr = new String[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param start
	 * @param end
	 * @return
	 */
	public static String[] copyOf(String[] data, int start, int end) {
		final String[] tempArr = new String[end - start];
		for (int i = start, j = 0; i < end; i++, j++) {
			tempArr[j] = data[i];
		}
		return tempArr;
	}

	public static ISprite[] copyOf(ISprite[] data, int start, int end) {
		final ISprite[] tempArr = new ISprite[end - start];
		for (int i = start, j = 0; i < end; i++, j++) {
			tempArr[j] = data[i];
		}
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param data
	 * @param start
	 * @param end
	 * @return
	 */
	public static <T> T[] copyOf(T[] data, int start, int end) {
		@SuppressWarnings("unchecked")
		final T[] tempArr = (T[]) new Object[end - start];
		for (int i = start, j = 0; i < end; i++, j++) {
			tempArr[j] = data[i];
		}
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
		final ISprite[] tempArr = new ISprite[newSize];
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
		final Actor[] tempArr = new Actor[newSize];
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
	public static Vector2f[] copy(Vector2f[] data, int newSize) {
		final Vector2f[] tempArr = new Vector2f[newSize];
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
	public static VertexAttribute[] copy(VertexAttribute[] data, int newSize) {
		final VertexAttribute[] tempArr = new VertexAttribute[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	/**
	 * copy颜色数据
	 * 
	 * @param data
	 * @param newSize
	 * @return
	 */
	public static LColor[] copyOf(LColor[] data, int newSize) {
		final LColor tempArr[] = new LColor[newSize];
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
		final Object tempArr[] = new Object[newSize];
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
		final int tempArr[] = new int[newSize];
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
		final double tempArr[] = new double[newSize];
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
		final float tempArr[] = new float[newSize];
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
		final byte tempArr[] = new byte[newSize];
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
		final char tempArr[] = new char[newSize];
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
		final long tempArr[] = new long[newSize];
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
		final boolean tempArr[] = new boolean[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static void copyWithStride(int[] src, int srcPos, int dest[], int destPos, int length, int chunk,
			int stride) {
		int total = length;
		for (; total > 0;) {
			System.arraycopy(src, srcPos, dest, destPos, chunk);
			srcPos += stride;
			destPos += chunk;
			total -= chunk;
		}
	}

	public static int copyOverWrite(float[] array, int fromIndex, int toIndex) {
		final int length = array.length;
		if (fromIndex > toIndex || length <= fromIndex || length < toIndex)
			return length;
		System.arraycopy(array, toIndex, array, fromIndex, length - toIndex);
		return length - (toIndex - fromIndex);
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static byte[] concat(byte[] first, byte[] second) {
		final byte[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static int[] concat(int[] first, int[] second) {
		final int[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static long[] concat(long[] first, long[] second) {
		final long[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static float[] concat(float[] first, float[] second) {
		final float[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static double[] concat(double[] first, double[] second) {
		final double[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 连接两个数组并返回为一个新数组
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Object[] concat(Object[] first, Object[] second) {
		final Object[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static ISprite[] concat(ISprite[] first, ISprite[] second) {
		final ISprite[] result = copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static Object[] concatMany(Object[][] arrays) {
		int len = 0;
		for (int i = 0; i < arrays.length; i++) {
			len += arrays[i].length;
		}
		final Object[] result = new Object[len];
		int startIndex = 0;
		for (int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, result, startIndex, arrays[i].length);
			startIndex += arrays[i].length;
		}
		return result;
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
	 * class1) { if (data == null) { return ArrayReflection.newInstance(class1, 1);
	 * } else { return expand(data, size, flag); } }
	 * 
	 * public static Object cut(Object data, int size) { int j; if ((j =
	 * ArrayReflection.getLength(data)) == 1) { return
	 * ArrayReflection.newInstance(data.getClass().getComponentType(), 0); } int k;
	 * if ((k = j - size - 1) > 0) { System.arraycopy(data, size + 1, data, size,
	 * k); } j--; Object obj1 =
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
		final int size = objs.length;
		final String[] newArrays = new String[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static int[] expand(int[] objs, int size) {
		return expand(objs, size, true);
	}

	public static int[] expand(int[] objs, int i, boolean flag) {
		final int size = objs.length;
		final int[] newArrays = new int[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static byte[] expand(byte[] objs, int size) {
		return expand(objs, size, true);
	}

	public static byte[] expand(byte[] objs, int i, boolean flag) {
		final int size = objs.length;
		final byte[] newArrays = new byte[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static float[] expand(float[] objs, int size) {
		return expand(objs, size, true);
	}

	public static float[] expand(float[] objs, int i, boolean flag) {
		final int size = objs.length;
		final float[] newArrays = new float[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static boolean[] expand(boolean[] objs, int size) {
		return expand(objs, size, true);
	}

	public static boolean[] expand(boolean[] objs, int i, boolean flag) {
		final int size = objs.length;
		final boolean[] newArrays = new boolean[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static ISprite[] expand(ISprite[] objs, int size) {
		return expand(objs, size, true);
	}

	public static ISprite[] expand(ISprite[] objs, int i, boolean flag) {
		final int size = objs.length;
		final ISprite[] newArrays = new ISprite[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static boolean equals(int[] a1, int[] a2) {
		if (a1 == a2) {
			return true;
		}
		if (a1 == null || a2 == null) {
			return false;
		}

		int length = a1.length;
		if (a2.length != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(float[] a1, float[] a2) {
		if (a1 == a2) {
			return true;
		}
		if (a1 == null || a2 == null) {
			return false;
		}
		int length = a1.length;
		if (a2.length != length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
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
	 * 移除一个指定索引的对象数组中数据
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

	/**
	 * 把对象数组中的元素随机重新排序
	 * 
	 * @param arrays
	 * @return
	 */
	public static Object[] shuffle(Object[] arrays) {
		for (int i = arrays.length - 1; i > -1; i--) {
			int j = MathUtils.floor(MathUtils.random() * (i + 1));
			Object temp = arrays[i];
			arrays[i] = arrays[j];
			arrays[j] = temp;
		}
		return arrays;
	}

	/**
	 * 检查针对指定对象数组的取值范围是否安全
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param endIndex
	 * @param checkLimit
	 * @param checkThrow
	 * @return
	 */
	public static boolean safeRange(Object[] arrays, int startIndex, int endIndex, boolean checkLimit,
			boolean checkThrow) {
		int len = arrays.length;
		if (checkLimit) {
			if (startIndex < 0 || startIndex > len || endIndex >= len || startIndex >= endIndex
					|| startIndex + endIndex > len) {
				if (checkThrow) {
					throw new LSysException(StringUtils.format(
							"Range startIndex:{0} endIndex:{1} length:{2}, Values outside acceptable range.",
							startIndex, endIndex, len));
				}
				return false;
			} else {
				return true;
			}
		} else {
			if (startIndex < 0 || startIndex > len || endIndex >= len) {
				if (checkThrow) {
					throw new LSysException(StringUtils.format(
							"Range startIndex:{0} endIndex:{1} length:{2}, Values outside acceptable range.",
							startIndex, endIndex, len));
				}
				return false;
			} else {
				return true;
			}
		}

	}

	/**
	 * 检查针对指定对象数组的取值范围是否安全
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param endIndex
	 * @param checkThrow
	 * @return
	 */
	public static boolean safeRange(Object[] arrays, int startIndex, int endIndex, boolean checkThrow) {
		return safeRange(arrays, startIndex, endIndex, false, checkThrow);
	}

	/**
	 * 检查针对指定对象数组的取值范围是否安全
	 * 
	 * @param arrays
	 * @param index
	 * @return
	 */
	public static boolean safeRange(Object[] arrays, int index) {
		return safeRange(arrays, index, index, false, false);
	}

	/**
	 * 检查针对指定对象数组的取值范围是否安全
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static boolean safeRange(Object[] arrays, int startIndex, int endIndex) {
		return safeRange(arrays, startIndex, endIndex, false);
	}

	public static int[] sort(int[] arrays) {
		return sort(arrays, true);
	}

	/**
	 * 数字数组排序
	 * 
	 * @param arrays
	 * @return
	 */
	public static int[] sort(int[] arrays, boolean positive) {
		if (arrays == null) {
			return null;
		}
		final int size = arrays.length;
		int tmp = 0;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (positive && arrays[i] > arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				} else if (!positive && arrays[i] < arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				}
			}
		}
		return arrays;
	}

	/**
	 * 字符数组排序
	 * 
	 * @param arrays
	 * @return
	 */
	public static char[] sort(char[] arrays, boolean positive) {
		if (arrays == null) {
			return null;
		}
		final int size = arrays.length;
		char tmp = 0;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (positive && arrays[i] > arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				} else if (!positive && arrays[i] < arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				}
			}
		}
		return arrays;
	}

	public static char[] sort(char[] arrays) {
		return sort(arrays, true);
	}

	public static float[] sort(float[] arrays) {
		return sort(arrays, true);
	}

	/**
	 * 数字数组排序
	 * 
	 * @param arrays
	 * @return
	 */
	public static float[] sort(float[] arrays, boolean positive) {
		if (arrays == null) {
			return null;
		}
		final int size = arrays.length;
		float tmp = 0;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (positive && arrays[i] > arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				} else if (!positive && arrays[i] < arrays[j]) {
					tmp = arrays[i];
					arrays[i] = arrays[j];
					arrays[j] = tmp;
				}
			}
		}
		return arrays;
	}

	/**
	 * 判定指定对象数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定指定对象数组是否不为空
	 * 
	 * @param array
	 * @return
	 */
	public static <T> boolean isNotEmpty(T[] array) {
		return (array != null && array.length != 0);
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(ISprite[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(LComponent[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(char[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(double[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判定数组是否为空
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断两组数组是否相等后返回
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean[] isEquals(float[] array1, float[] array2) {
		return isEquals(array1, array2, null);
	}

	/**
	 * 判断两组数组是否相等后返回
	 * 
	 * @param array1
	 * @param array2
	 * @param result
	 * @return
	 */
	public static boolean[] isEquals(float[] array1, float[] array2, boolean[] result) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] == array2[i];
		}
		return result;
	}

	/**
	 * 判断两组数组是否相等后返回
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean[] isEquals(int[] array1, int[] array2) {
		return isEquals(array1, array2, null);
	}

	/**
	 * 判断两组数组是否相等后返回
	 * 
	 * @param array1
	 * @param array2
	 * @param result
	 * @return
	 */
	public static boolean[] isEquals(int[] array1, int[] array2, boolean[] result) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] == array2[i];
		}
		return result;
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(int[] array1, int v) {
		return isEquals(array1, null, v);
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param result
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(int[] array1, boolean[] result, int v) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] == v;
		}
		return result;
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(float[] array1, float v) {
		return isEquals(array1, null, v);
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param result
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(float[] array1, boolean[] result, float v) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] == v;
		}
		return result;
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(boolean[] array1, boolean v) {
		return isEquals(array1, null, v);
	}

	/**
	 * 判断指定数组是否与指定值一致后返回
	 * 
	 * @param array1
	 * @param result
	 * @param v
	 * @return
	 */
	public static boolean[] isEquals(boolean[] array1, boolean[] result, boolean v) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] == v;
		}
		return result;
	}

	/**
	 * 判断两组数组是否完全一致后返回
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean[] isAnd(boolean[] array1, boolean[] array2) {
		return isAnd(array1, array2, null);
	}

	/**
	 * 判断两组数组是否完全一致后返回
	 * 
	 * @param array1
	 * @param array2
	 * @param result
	 * @return
	 */
	public static boolean[] isAnd(boolean[] array1, boolean[] array2, boolean[] result) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] && array2[i];
		}
		return result;
	}

	/**
	 * 判断两组数据是否满足条件之一后返回
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean[] isOr(boolean[] array1, boolean[] array2) {
		return isOr(array1, array2, null);
	}

	/**
	 * 判断两组数据是否满足条件之一后返回
	 * 
	 * @param array1
	 * @param array2
	 * @param result
	 * @return
	 */
	public static boolean[] isOr(boolean[] array1, boolean[] array2, boolean[] result) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] || array2[i];
		}
		return result;
	}

	/**
	 * 将指定布尔数组数据全部区反值后返回
	 * 
	 * @param array
	 * @return
	 */
	public static boolean[] isNot(boolean[] array1) {
		return isNot(array1, null);
	}

	/**
	 * 将指定布尔数组数据全部区反值后返回
	 * 
	 * @param array
	 * @param result
	 * @return
	 */
	public static boolean[] isNot(boolean[] array1, boolean[] result) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = !array1[i];
		}
		return result;
	}

	/**
	 * 将指定整型数组数据全部区反值后返回
	 * 
	 * @param array1
	 * @param v
	 * @return
	 */
	public static boolean[] isNot(int[] array1, int v) {
		return isNot(array1, null, v);
	}

	/**
	 * 将指定整型数组数据全部区反值后返回
	 * 
	 * @param array1
	 * @param result
	 * @param v
	 * @return
	 */
	public static boolean[] isNot(int[] array1, boolean[] result, int v) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] != v;
		}
		return result;
	}

	/**
	 * 将指定整型数组数据全部区反值后返回
	 * 
	 * @param array1
	 * @param v
	 * @return
	 */
	public static boolean[] isNot(float[] array1, float v) {
		return isNot(array1, null, v);
	}

	/**
	 * 将指定整型数组数据全部区反值后返回
	 * 
	 * @param array1
	 * @param result
	 * @param v
	 * @return
	 */
	public static boolean[] isNot(float[] array1, boolean[] result, float v) {
		final int length = array1.length;
		if (result == null) {
			result = new boolean[length];
		}
		for (int i = 0; i < length; i++) {
			result[i] = array1[i] != v;
		}
		return result;
	}

	/**
	 * 设定对象数组中指定范围内数值为统一的data
	 * 
	 * @param arrays
	 * @param data
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static Object[] setAll(Object[] arrays, Object data, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (endIndex > arrays.length) {
			endIndex = arrays.length;
		}
		if (safeRange(arrays, startIndex, endIndex)) {
			for (int i = startIndex; i < endIndex; i++) {
				arrays[i] = data;
			}
		}
		return arrays;
	}

	/**
	 * 向指定对象数组的开头添加一个或者多个数据
	 * 
	 * @param arrays
	 * @param values
	 * @return
	 */
	public static Object[] unshift(Object[] arrays, Object... values) {
		int len = arrays.length;
		int dataLen = values.length;
		Object[] newItems = new Object[len + dataLen];
		for (int i = 0; i < values.length; i++) {
			newItems[i] = values[i];
		}
		System.arraycopy(arrays, 0, newItems, dataLen, len);
		arrays = newItems;
		return arrays;
	}

	/**
	 * 删除对象数组中指定索引位置的数据,并返回一个剪切好的新对象数组
	 * 
	 * @param arrays
	 * @param index
	 * @return
	 */
	public static Object[] shiftCut(Object[] arrays, int index) {
		if (arrays == null || arrays.length == 0) {
			return null;
		}
		int len = arrays.length;
		len--;
		System.arraycopy(arrays, index + 1, arrays, index, len - index);
		return cutObject(arrays, len - 1);
	}

	/**
	 * 删除对象数组中指定索引的数据
	 * 
	 * @param arrays
	 * @param index
	 * @return
	 */
	public static Object shift(Object[] arrays, int index) {
		if (arrays == null || arrays.length == 0) {
			return null;
		}
		Object item = arrays[index];
		int len = arrays.length;
		len--;
		System.arraycopy(arrays, index + 1, arrays, index, len - index);
		arrays[len] = null;
		return item;
	}

	/**
	 * 删除对象数组中的一个数据
	 * 
	 * @param arrays
	 * @return
	 */
	public static Object shift(Object[] arrays) {
		return shift(arrays, 0);
	}

	/**
	 * 删除对象数组初始索引后指定长度范围内的数据并返回一个新剪切后数组
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param size
	 * @param cutData
	 * @return
	 */
	public static Object[] removeCutObject(Object[] arrays, int startIndex, int size, boolean cutData) {
		if (safeRange(arrays, startIndex, size, false, true)) {
			int len = arrays.length;
			int count = size - startIndex + 1;
			for (int i = startIndex; i < size + 1; i++) {
				arrays[i] = null;
			}
			if (cutData) {
				Object[] items = new Object[len - count];
				for (int i = 0, j = 0; i < len; i++) {
					Object vo = arrays[i];
					if (vo != null) {
						items[j++] = vo;
					}
				}
				return items;
			}
			return arrays;
		}
		return null;
	}

	/**
	 * 删除对象数组指定范围内的数据并返回一个新剪切后数组
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param size
	 * @return
	 */
	public static Object[] removeCutObject(Object[] arrays, int startIndex, int size) {
		return removeCutObject(arrays, startIndex, size, true);
	}

	/**
	 * 删除指定索引后指定长度的数据
	 * 
	 * @param arrays
	 * @param index
	 * @param size
	 * @return
	 */
	public static Object[] splice(Object[] arrays, int index, int size) {
		return removeCutObject(arrays, index, index + size - 1, true);
	}

	/**
	 * 删除指定索引的数据
	 * 
	 * @param arrays
	 * @param index
	 * @return
	 */
	public static Object[] splice(Object[] arrays, int index) {
		return splice(arrays, index, 1);
	}

	/**
	 * 将对象数组中已有的指定数据插入对象数组最前方
	 * 
	 * @param arrays
	 * @param data
	 * @return
	 */
	public static Object[] sendToBack(Object[] arrays, Object data) {
		int currentIndex = indexOf(arrays, data);
		if (currentIndex != -1 && currentIndex > 0) {
			arrays = splice(arrays, currentIndex);
			arrays = unshift(arrays, data);
		}
		return arrays;
	}

	/**
	 * 返回对象数组中一个随机数据
	 * 
	 * @param arrays
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static Object getRandom(Object[] arrays, int startIndex, int length) {
		if (safeRange(arrays, startIndex, length, false, true)) {
			int randomIndex = startIndex + MathUtils.floor(MathUtils.random() * length);
			return arrays[randomIndex];
		}
		return null;
	}

	/**
	 * 返回一个有界限的hashCode,避免重复
	 * 
	 * @param hashCode
	 * @return
	 */
	public static int getLimitHash(int hashCode) {
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
	}

	/**
	 * 获得hashCode
	 * 
	 * @param key
	 * @return
	 */
	public static long getHashKey(int key) {
		int hash = getLimitHash(key);
		if (hash == 0) {
			hash = 1;
		}
		return ((long) key << 32) | (hash & 0xFFFFFFFFL);
	}

	/**
	 * 检查数组长度是否越界
	 * 
	 * @param arrayLength
	 * @param fromIndex
	 * @param toIndex
	 */
	public static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new LSysException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new LSysException("fromIndex < 0");
		}
		if (toIndex > arrayLength) {
			throw new LSysException("toIndex > arrayLength");
		}
	}

	/**
	 * 填充指定整型数组
	 * 
	 * @param arrays
	 * @param fromIndex
	 * @param toIndex
	 * @param val
	 */
	public static void fill(int[] arrays, int fromIndex, int toIndex, int val) {
		rangeCheck(arrays.length, fromIndex, toIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定整型数组
	 * 
	 * @param arrays
	 * @param fromIndex
	 * @param toIndex
	 * @param val
	 */
	public static void fill(char[] arrays, int fromIndex, int toIndex, char val) {
		rangeCheck(arrays.length, fromIndex, toIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定整型数组
	 * 
	 * @param arrays
	 * @param fromIndex
	 * @param toIndex
	 * @param val
	 */
	public static void fill(long[] arrays, int fromIndex, int toIndex, int val) {
		rangeCheck(arrays.length, fromIndex, toIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定整型数组
	 * 
	 * @param arrays
	 * @param val
	 */
	public static void fill(long[] arrays, long val) {
		for (int i = 0, len = arrays.length; i < len; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定對象数组
	 * 
	 * @param arrays
	 * @param val
	 */
	public static void fill(Object[] arrays, Object val) {
		for (int i = 0, len = arrays.length; i < len; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定整型数组
	 * 
	 * @param arrays
	 * @param val
	 */
	public static void fill(int[] arrays, int val) {
		for (int i = 0, len = arrays.length; i < len; i++) {
			arrays[i] = val;
		}
	}

	/**
	 * 填充指定对象数组
	 * 
	 * @param arrays
	 * @param fromIndex
	 * @param toIndex
	 * @param data
	 */
	public static void fill(Object[] arrays, int fromIndex, int toIndex, Object data) {
		rangeCheck(arrays.length, fromIndex, toIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			arrays[i] = data;
		}
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @see indexOf(int[], int, int)
	 */
	public static int indexOf(int[] arr, int v) {
		return indexOf(arr, v, 0);
	}

	/**
	 * 以off为初始索引,查看指定数组中是否包含v值
	 * 
	 * @param arr 数组
	 * @param v   值
	 * @param off 从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static int indexOf(int[] arr, int v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @param arr
	 * @param v
	 * @return 最后一个匹配元素的下标
	 */
	public static int lastIndexOf(int[] arr, int v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @see indexOf(char[], char, int)
	 */
	public static int indexOf(char[] arr, char v) {
		if (null != arr)
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @param arr 数组
	 * @param v   值
	 * @param off 从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static int indexOf(char[] arr, char v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @param arr
	 * @param v
	 * @return 第一个匹配元素的下标
	 */
	public static int lastIndexOf(char[] arr, char v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @see indexOf(long[], long, int)
	 */
	public static int indexOf(long[] arr, long v) {
		return indexOf(arr, v, 0);
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @param arr 数组
	 * @param v   值
	 * @param off 从那个下标开始搜索(包含)
	 * @return 第一个匹配元素的下标
	 */
	public static int indexOf(long[] arr, long v, int off) {
		if (null != arr)
			for (int i = off; i < arr.length; i++) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}

	/**
	 * 查看指定数组中是否包含v值
	 * 
	 * @param arr
	 * @param v
	 * @return 第一个匹配元素的下标
	 */
	public static int lastIndexOf(long[] arr, long v) {
		if (null != arr)
			for (int i = arr.length - 1; i >= 0; i--) {
				if (arr[i] == v)
					return i;
			}
		return -1;
	}
}
