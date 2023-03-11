package loon.utils.reflect;

import loon.gwtref.client.ReflectionCache;

@SuppressWarnings("rawtypes")
public final class ArrayReflection {

	static public Object newInstance(Class c, int size) {
		return ReflectionCache.newArray(c, size);
	}

	static public int getLength(Object array) {
		return ReflectionCache.getType(array.getClass()).getArrayLength(array);
	}

	static public Object get(Object array, int index) {
		return ReflectionCache.getType(array.getClass()).getArrayElement(array, index);
	}

	static public void set(Object array, int index, Object value) {
		ReflectionCache.getType(array.getClass()).setArrayElement(array, index, value);
	}
}
