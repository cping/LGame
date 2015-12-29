package loon.build.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


public class CollectionUtils {

	final static public int INITIAL_CAPACITY = 20;

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
	 * 扩充指定数组
	 * 
	 * @param obj
	 * @param i
	 * @param flag
	 * @return
	 */
	public static Object expand(Object obj, int i, boolean flag) {
		int j = Array.getLength(obj);
		Object obj1 = Array.newInstance(obj.getClass().getComponentType(), j
				+ i);
		System.arraycopy(obj, 0, obj1, flag ? 0 : i, j);
		return obj1;
	}

	/**
	 * 扩充指定数组
	 * 
	 * @param obj
	 * @param size
	 * @return
	 */
	public static Object expand(Object obj, int size) {
		return expand(obj, size, true);
	}

	/**
	 * 扩充指定数组
	 * 
	 * @param obj
	 * @param size
	 * @param flag
	 * @param class1
	 * @return
	 */
	public static Object expand(Object obj, int size, boolean flag,
			Class<?> class1) {
		if (obj == null) {
			return Array.newInstance(class1, 1);
		} else {
			return expand(obj, size, flag);
		}
	}

	/**
	 * 剪切出指定长度的数组
	 * 
	 * @param obj
	 * @param size
	 * @return
	 */
	public static Object cut(Object obj, int size) {
		int j;
		if ((j = Array.getLength(obj)) == 1) {
			return Array.newInstance(obj.getClass().getComponentType(), 0);
		}
		int k;
		if ((k = j - size - 1) > 0) {
			System.arraycopy(obj, size + 1, obj, size, k);
		}
		j--;
		Object obj1 = Array.newInstance(obj.getClass().getComponentType(), j);
		System.arraycopy(obj, 0, obj1, 0, j);
		return obj1;
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param src
	 * @return
	 */
	public static Object copyOf(Object src) {
		int srcLength = Array.getLength(src);
		Class<?> srcComponentType = src.getClass().getComponentType();
		Object dest = Array.newInstance(srcComponentType, srcLength);
		if (srcComponentType.isArray()) {
			for (int i = 0; i < Array.getLength(src); i++) {
				Array.set(dest, i, copyOf(Array.get(src, i)));
			}
		} else {
			System.arraycopy(src, 0, dest, 0, srcLength);
		}
		return dest;
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
	 * @param original
	 * @param newLength
	 * @return
	 */
	public static Object[] copyOf(Object[] original, int newLength) {
		return copyOf(original, newLength, original.getClass());
	}

	/**
	 * copy指定长度的数组数据
	 * 
	 * @param original
	 * @param newLength
	 * @param newType
	 * @return
	 */
	public static Object[] copyOf(Object[] original, int newLength,
			Class<?> newType) {
		Object[] copy = (newType == Object[].class) ? new Object[newLength]
				: (Object[]) Array.newInstance(newType.getComponentType(),
						newLength);
		System.arraycopy(original, 0, copy, 0,
				MathUtils.min(original.length, newLength));
		return copy;
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

	final static public Set<Object> createSet() {
		return createSet(INITIAL_CAPACITY);
	}

	final static public Set<Object> createSet(final int size) {
		return new HashSet<Object>(size);
	}

	final static public Set<Object> createSet(final Set<?> set) {
		return new HashSet<Object>(set);
	}

	final static public List<Object> createList() {
		return createList(INITIAL_CAPACITY);
	}

	final static public List<Object> createList(int size) {
		return size > 0 ? new ArrayList<Object>(size) : createList();
	}


	final static public Map<Object, Object> createMap() {
		return createMap(INITIAL_CAPACITY);
	}

	final static public Map<Object, Object> createMap(final int size) {
		return size > 0 ? new HashMap<Object, Object>(size)
				: new HashMap<Object, Object>();
	}

	final static public Collection<Object> createCollection() {
		return new ArrayList<Object>();
	}

	final static public Collection<Object> createCollection(int size) {
		return size > 0 ? new ArrayList<Object>(size) : createCollection();
	}

	final static public Collection<Object> createCollection(Object object) {
		Collection<Object> collection = createCollection();
		collection.add(object);
		return collection;
	}


	/**
	 * 检查指定Collection是否为空
	 * 
	 * @param collection
	 * @return
	 */
	final static public boolean isEmpty(Collection<Object> collection) {
		return collection == null || collection.size() == 0;
	}

	/**
	 * 检查指定Map是否为空
	 * 
	 * @param map
	 * @return
	 */
	final static public boolean isEmpty(Map<Object, Object> map) {
		return map == null || map.size() == 0;
	}

	/**
	 * 检查指定Collection中是否包含指定对象
	 * 
	 * @param collection
	 * @param item
	 * @return
	 */
	final static public boolean contains(Collection<Object> collection,
			Object item) {
		return collection != null && collection.contains(item);
	}

	/**
	 * 检查指定Map中是否包含指定键
	 * 
	 * @param collection
	 * @param item
	 * @return
	 */
	final static public boolean containsKey(Map<Object, Object> collection,
			Object item) {
		return collection != null && collection.containsKey(item);
	}

	/**
	 * 检查指定Map中是否包含指定值
	 * 
	 * @param collection
	 * @param item
	 * @return
	 */
	final static public boolean containsValue(Map<Object, Object> collection,
			Object item) {
		return collection != null && collection.containsValue(item);
	}

	/**
	 * 返回指定Collection的首元素
	 * 
	 * @param collection
	 * @return
	 */
	final static public Object first(Collection<Object> collection) {
		Object[] obj = collection.toArray();
		if (obj.length > 0) {
			return obj[0];
		} else {
			return null;
		}
	}

	final static public Set<Object> synchronizedSet() {
		return Collections
				.synchronizedSet(new HashSet<Object>(INITIAL_CAPACITY));
	}

	final static public List<Object> synchronizedList(final int size) {
		return Collections.synchronizedList(createList(size));
	}

	final static public List<Object> synchronizedList() {
		return synchronizedList(INITIAL_CAPACITY);
	}

	final static public List<Object> createList(Collection<Object> collection) {
		return new ArrayList<Object>(collection);
	}

	final static public Collection<Object> createCollection(Object[] objects) {
		Collection<Object> result = createCollection();
		for (int i = 0; i < objects.length; i++) {
			result.add(objects[i]);
		}
		return result;
	}

}
