package loon.utils.reflect;

import loon.gwtref.client.Parameter;

@SuppressWarnings("rawtypes")
public final class Constructor {

	private final loon.gwtref.client.Constructor constructor;

	Constructor(loon.gwtref.client.Constructor constructor) {
		this.constructor = constructor;
	}

	public Class[] getParameterTypes() {
		Parameter[] parameters = constructor.getParameters();
		Class[] parameterTypes = new Class[parameters.length];
		for (int i = 0, j = parameters.length; i < j; i++) {
			parameterTypes[i] = parameters[i].getClazz();
		}
		return parameterTypes;
	}

	public Class getDeclaringClass() {
		return constructor.getEnclosingType();
	}

	public boolean isAccessible() {
		return constructor.isPublic();
	}

	public void setAccessible(boolean accessible) {

	}

	public Object newInstance(Object... args) throws ReflectionException {
		try {
			return constructor.newInstance(args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(
					"Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(), e);
		}
	}

}
