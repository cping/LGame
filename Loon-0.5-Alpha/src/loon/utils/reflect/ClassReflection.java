package loon.utils.reflect;

import java.lang.reflect.Modifier;

public final class ClassReflection {

	static public Class<?> forName(String name) throws ReflectionException {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ReflectionException("Class not found: " + name, e);
		}
	}

	static public String getSimpleName(Class<?> c) {
		return c.getSimpleName();
	}

	static public boolean isInstance(Class<?> c, Object obj) {
		return c.isInstance(obj);
	}

	static public boolean isAssignableFrom(Class<?> c1, Class<?> c2) {
		return c1.isAssignableFrom(c2);
	}

	static public boolean isMemberClass(Class<?> c) {
		return c.isMemberClass();
	}

	static public boolean isStaticClass(Class<?> c) {
		return Modifier.isStatic(c.getModifiers());
	}

	static public <T> T newInstance(Class<T> c) throws ReflectionException {
		try {
			return c.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectionException(
					"Could not instantiate instance of class: " + c.getName(),
					e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(
					"Could not instantiate instance of class: " + c.getName(),
					e);
		}
	}

	static public Constructor[] getConstructors(Class<?> c) {
		java.lang.reflect.Constructor<?>[] constructors = c.getConstructors();
		Constructor[] result = new Constructor[constructors.length];
		for (int i = 0, j = constructors.length; i < j; i++) {
			result[i] = new Constructor(constructors[i]);
		}
		return result;
	}

	static public Constructor getConstructor(Class<?> c,
			Class<?>... parameterTypes) throws ReflectionException {
		try {
			return new Constructor(c.getConstructor(parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation occurred while getting constructor for class: '"
							+ c.getName() + "'.", e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Constructor not found for class: "
					+ c.getName(), e);
		}
	}

	static public Constructor getDeclaredConstructor(Class<?> c,
			Class<?>... parameterTypes) throws ReflectionException {
		try {
			return new Constructor(c.getDeclaredConstructor(parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting constructor for class: "
							+ c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Constructor not found for class: "
					+ c.getName(), e);
		}
	}

	static public Method[] getMethods(Class<?> c) {
		java.lang.reflect.Method[] methods = c.getMethods();
		Method[] result = new Method[methods.length];
		for (int i = 0, j = methods.length; i < j; i++) {
			result[i] = new Method(methods[i]);
		}
		return result;
	}

	static public Method getMethod(Class<?> c, String name,
			Class<?>... parameterTypes) throws ReflectionException {
		try {
			return new Method(c.getMethod(name, parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting method: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Method not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public Method[] getDeclaredMethods(Class<?> c) {
		java.lang.reflect.Method[] methods = c.getDeclaredMethods();
		Method[] result = new Method[methods.length];
		for (int i = 0, j = methods.length; i < j; i++) {
			result[i] = new Method(methods[i]);
		}
		return result;
	}

	static public Method getDeclaredMethod(Class<?> c, String name,
			Class<?>... parameterTypes) throws ReflectionException {
		try {
			return new Method(c.getDeclaredMethod(name, parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting method: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Method not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public Field[] getFields(Class<?> c) {
		java.lang.reflect.Field[] fields = c.getFields();
		Field[] result = new Field[fields.length];
		for (int i = 0, j = fields.length; i < j; i++) {
			result[i] = new Field(fields[i]);
		}
		return result;
	}

	static public Field getField(Class<?> c, String name)
			throws ReflectionException {
		try {
			return new Field(c.getField(name));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting field: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Field not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public Field[] getDeclaredFields(Class<?> c) {
		java.lang.reflect.Field[] fields = c.getDeclaredFields();
		Field[] result = new Field[fields.length];
		for (int i = 0, j = fields.length; i < j; i++) {
			result[i] = new Field(fields[i]);
		}
		return result;
	}

	static public Field getDeclaredField(Class<?> c, String name)
			throws ReflectionException {
		try {
			return new Field(c.getDeclaredField(name));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting field: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Field not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public boolean isAnnotationPresent(Class<?> c,
			Class<? extends java.lang.annotation.Annotation> annotationType) {
		return c.isAnnotationPresent(annotationType);
	}

	static public Annotation[] getDeclaredAnnotations(Class<?> c) {
		java.lang.annotation.Annotation[] annotations = c
				.getDeclaredAnnotations();
		Annotation[] result = new Annotation[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			result[i] = new Annotation(annotations[i]);
		}
		return result;
	}

	static public Annotation getDeclaredAnnotation(Class<?> c,
			Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation[] annotations = c
				.getDeclaredAnnotations();
		for (java.lang.annotation.Annotation annotation : annotations) {
			if (annotation.annotationType().equals(annotationType)) {
				return new Annotation(annotation);
			}
		}
		return null;
	}
}
