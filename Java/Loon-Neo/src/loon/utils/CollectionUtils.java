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

import java.util.NoSuchElementException;

import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.action.sprite.node.LNNode;
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
	 * @param array
	 * @param obj
	 * @return
	 */
	public static int indexOf(Object[] array, Object obj) {
		for (int i = 0; i < array.length; ++i) {
			if (obj == array[i]) {
				return i;
			}
		}
		throw new NoSuchElementException("" + obj);
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
	 * @param obj
	 * @return
	 */
	public static int[][] copyOf(int[][] obj) {
		int size = obj.length;
		int[][] copy = new int[size][];
		for (int i = 0; i < size; i++) {
			int len = obj[i].length;
			int[] res = new int[len];
			System.arraycopy(obj[i], 0, res, 0, len);
			copy[i] = res;
		}
		return copy;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static String[] copyOf(String[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static String[] copyOf(String[] obj, int newSize) {
		String tempArr[] = new String[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static ISprite[] copyOf(ISprite[] obj, int newSize) {
		ISprite tempArr[] = new ISprite[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static Actor[] copyOf(Actor[] obj, int newSize) {
		Actor tempArr[] = new Actor[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static Object[] copyOf(Object[] obj, int newSize) {
		Object tempArr[] = new Object[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static int[] copyOf(int[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static int[] copyOf(int[] obj, int newSize) {
		int tempArr[] = new int[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static double[] copyOf(double[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static double[] copyOf(double[] obj, int newSize) {
		double tempArr[] = new double[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static float[] copyOf(float[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static float[] copyOf(float[] obj, int newSize) {
		float tempArr[] = new float[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] copyOf(byte[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static byte[] copyOf(byte[] obj, int newSize) {
		byte tempArr[] = new byte[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static char[] copyOf(char[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static char[] copyOf(char[] obj, int newSize) {
		char tempArr[] = new char[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static long[] copyOf(long[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static long[] copyOf(long[] obj, int newSize) {
		long tempArr[] = new long[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean[] copyOf(boolean[] obj) {
		return copyOf(obj, obj.length);
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param obj
	 * @param newSize
	 * @return
	 */
	public static boolean[] copyOf(boolean[] obj, int newSize) {
		boolean tempArr[] = new boolean[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	// --为了兼容GWT，尽量减少反射的使用，所以只好针对不同类分别处理了……--//
	/**
	 * 
	 public static Object expand(Object obj, int i, boolean flag) { int j =
	 * ArrayReflection.getLength(obj); Object obj1 =
	 * ArrayReflection.newInstance(obj.getClass().getComponentType(), j + i);
	 * System.arraycopy(obj, 0, obj1, flag ? 0 : i, j); return obj1; }
	 * 
	 * public static Object expand(Object obj, int size) { return expand(obj,
	 * size, true); }
	 * 
	 * public static Object expand(Object obj, int size, boolean flag, Class<?>
	 * class1) { if (obj == null) { return ArrayReflection.newInstance(class1,
	 * 1); } else { return expand(obj, size, flag); } }
	 * 
	 * public static Object cut(Object obj, int size) { int j; if ((j =
	 * ArrayReflection.getLength(obj)) == 1) { return
	 * ArrayReflection.newInstance(obj.getClass().getComponentType(), 0); } int
	 * k; if ((k = j - size - 1) > 0) { System.arraycopy(obj, size + 1, obj,
	 * size, k); } j--; Object obj1 =
	 * ArrayReflection.newInstance(obj.getClass().getComponentType(), j);
	 * System.arraycopy(obj, 0, obj1, 0, j); return obj1; }
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

	public static LNNode[] expand(LNNode[] objs, int i, boolean flag) {
		int size = objs.length;
		LNNode[] newArrays = new LNNode[size + i];
		System.arraycopy(objs, 0, newArrays, flag ? 0 : i, size);
		return newArrays;
	}

	public static LNNode[] cut(LNNode[] objs, int size) {
		int j;
		if ((j = objs.length) == 1) {
			return new LNNode[0];
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(objs, size + 1, objs, size, k);
		}
		j--;
		LNNode[] newArrays = new LNNode[j];
		System.arraycopy(objs, 0, newArrays, 0, j);
		return newArrays;
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] obj,
			int newSize) {
		PConvexPolygonShape tempArr[] = new PConvexPolygonShape[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PBody[] copyOf(PBody[] obj, int newSize) {
		PBody tempArr[] = new PBody[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static PBody[] copyOf(PBody[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PJoint[] copyOf(PJoint[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PJoint[] copyOf(PJoint[] obj, int newSize) {
		PJoint tempArr[] = new PJoint[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static PSolver[] copyOf(PSolver[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PSolver[] copyOf(PSolver[] obj, int newSize) {
		PSolver tempArr[] = new PSolver[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static PShape[] copyOf(PShape[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PShape[] copyOf(PShape[] obj, int newSize) {
		PShape tempArr[] = new PShape[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static PSortableObject[] copyOf(PSortableObject[] obj) {
		return copyOf(obj, obj.length);
	}

	public static PSortableObject[] copyOf(PSortableObject[] obj, int newSize) {
		PSortableObject tempArr[] = new PSortableObject[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static LTexture[] copyOf(LTexture[] obj, int newSize) {
		LTexture tempArr[] = new LTexture[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static LTexture[] copyOf(LTexture[] obj) {
		return copyOf(obj, obj.length);
	}

	public static ArrayMap.Entry[] copyOf(ArrayMap.Entry[] obj, int newSize) {
		ArrayMap.Entry tempArr[] = new ArrayMap.Entry[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static ArrayMap.Entry[] copyOf(ArrayMap.Entry[] obj) {
		return copyOf(obj, obj.length);
	}

	public static IntHashMap.Entry[] copyOf(IntHashMap.Entry[] obj, int newSize) {
		IntHashMap.Entry tempArr[] = new IntHashMap.Entry[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public static IntHashMap.Entry[] copyOf(IntHashMap.Entry[] obj) {
		return copyOf(obj, obj.length);
	}

}
