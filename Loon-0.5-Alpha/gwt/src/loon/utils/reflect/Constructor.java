package loon.utils.reflect;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("rawtypes")
public final class Constructor {

	private final java.lang.reflect.Constructor constructor;

	Constructor(java.lang.reflect.Constructor constructor) {
		this.constructor = constructor;
	}

	public Class[] getParameterTypes() {
		return constructor.getParameterTypes();
	}

	public Class getDeclaringClass() {
		return constructor.getDeclaringClass();
	}

	public boolean isAccessible() {
		return constructor.isAccessible();
	}

	public void setAccessible(boolean accessible) {
		constructor.setAccessible(accessible);
	}

	public Object newInstance(Object... args) throws ReflectionException {
		try {
			return constructor.newInstance(args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(
					"Illegal argument(s) supplied to constructor for class: "
							+ getDeclaringClass().getName(), e);
		} catch (InstantiationException e) {
			throw new ReflectionException(
					"Could not instantiate instance of class: "
							+ getDeclaringClass().getName(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(
					"Could not instantiate instance of class: "
							+ getDeclaringClass().getName(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(
					"Exception occurred in constructor for class: "
							+ getDeclaringClass().getName(), e);
		}
	}

}
