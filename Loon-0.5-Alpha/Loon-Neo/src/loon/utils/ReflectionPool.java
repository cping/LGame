package loon.utils;

import loon.utils.reflect.ClassReflection;
import loon.utils.reflect.Constructor;
import loon.utils.reflect.ReflectionException;

public class ReflectionPool<T> extends Pool<T> {
	private final Constructor constructor;

	public ReflectionPool(Class<T> type) {
		this(type, 16, Integer.MAX_VALUE);
	}

	public ReflectionPool(Class<T> type, int initialCapacity) {
		this(type, initialCapacity, Integer.MAX_VALUE);
	}

	public ReflectionPool(Class<T> type, int initialCapacity, int max) {
		super(initialCapacity, max);
		constructor = findConstructor(type);
		if (constructor == null)
			throw new RuntimeException(
					"Class cannot be created (missing no-arg constructor): "
							+ type.getName());
	}

	private Constructor findConstructor(Class<T> type) {
		try {
			return ClassReflection.getConstructor(type, (Class[]) null);
		} catch (Exception ex1) {
			try {
				Constructor constructor = ClassReflection
						.getDeclaredConstructor(type, (Class[]) null);
				constructor.setAccessible(true);
				return constructor;
			} catch (ReflectionException ex2) {
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected T newObject() {
		try {
			return (T) constructor.newInstance((Object[]) null);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to create new instance: "
					+ constructor.getDeclaringClass().getName(), ex);
		}
	}
}
