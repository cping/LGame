package loon.utils.reflect;

import java.lang.annotation.Inherited;

import loon.gwtref.client.ReflectionCache;
import loon.gwtref.client.Type;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ClassReflection {

	static public Class forName(String name) throws ReflectionException {
		try {
			return ReflectionCache.forName(name).getClassOfType();
		} catch (ClassNotFoundException e) {
			throw new ReflectionException("Class not found: " + name);
		}
	}

	static public String getSimpleName(Class c) {
		return c.getSimpleName();
	}

	static public Class[] getInterfaces(Class c) {
		return ReflectionCache.getType(c).getInterfaces();
	}

	static public boolean isInstance(Class c, Object obj) {
		return obj != null && isAssignableFrom(c, obj.getClass());
	}

	static public boolean isAssignableFrom(Class c1, Class c2) {
		Type c1Type = ReflectionCache.getType(c1);
		Type c2Type = ReflectionCache.getType(c2);
		return c1Type.isAssignableFrom(c2Type);
	}

	static public boolean isMemberClass(Class c) {
		return ReflectionCache.getType(c).isMemberClass();
	}

	static public boolean isStaticClass(Class c) {
		return ReflectionCache.getType(c).isStatic();
	}

	static public boolean isArray(Class c) {
		return ReflectionCache.getType(c).isArray();
	}

	static public <T> T newInstance(Class<T> c) throws ReflectionException {
		try {
			return (T) ReflectionCache.getType(c).newInstance();
		} catch (NoSuchMethodException e) {
			throw new ReflectionException(
					"Could not use default constructor of " + c.getName(), e);
		}
	}

	static public Constructor[] getConstructors(Class c) {
		loon.gwtref.client.Constructor[] constructors = ReflectionCache
				.getType(c).getConstructors();
		Constructor[] result = new Constructor[constructors.length];
		for (int i = 0, j = constructors.length; i < j; i++) {
			result[i] = new Constructor(constructors[i]);
		}
		return result;
	}

	static public Constructor getConstructor(Class c, Class... parameterTypes)
			throws ReflectionException {
		try {
			return new Constructor(ReflectionCache.getType(c).getConstructor(
					parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting constructor for class: "
							+ c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Constructor not found for class: "
					+ c.getName(), e);
		}
	}

	static public Constructor getDeclaredConstructor(Class c,
			Class... parameterTypes) throws ReflectionException {
		try {
			return new Constructor(ReflectionCache.getType(c)
					.getDeclaredConstructor(parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting constructor for class: "
							+ c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Constructor not found for class: "
					+ c.getName(), e);
		}
	}

	static public Method[] getMethods(Class c) {
		loon.gwtref.client.Method[] methods = ReflectionCache.getType(c)
				.getMethods();
		Method[] result = new Method[methods.length];
		for (int i = 0, j = methods.length; i < j; i++) {
			result[i] = new Method(methods[i]);
		}
		return result;
	}

	static public Method getMethod(Class c, String name,
			Class... parameterTypes) throws ReflectionException {
		try {
			return new Method(ReflectionCache.getType(c).getMethod(name,
					parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting method: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Method not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public Method[] getDeclaredMethods(Class c) {
		loon.gwtref.client.Method[] methods = ReflectionCache.getType(c)
				.getDeclaredMethods();
		Method[] result = new Method[methods.length];
		for (int i = 0, j = methods.length; i < j; i++) {
			result[i] = new Method(methods[i]);
		}
		return result;
	}

	static public Method getDeclaredMethod(Class c, String name,
			Class... parameterTypes) throws ReflectionException {
		try {
			return new Method(ReflectionCache.getType(c).getMethod(name,
					parameterTypes));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting method: " + name
							+ ", for class: " + c.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Method not found: " + name
					+ ", for class: " + c.getName(), e);
		}
	}

	static public Field[] getFields(Class c) {
		loon.gwtref.client.Field[] fields = ReflectionCache.getType(c)
				.getFields();
		Field[] result = new Field[fields.length];
		for (int i = 0, j = fields.length; i < j; i++) {
			result[i] = new Field(fields[i]);
		}
		return result;
	}

	static public Field getField(Class c, String name)
			throws ReflectionException {
		try {
			return new Field(ReflectionCache.getType(c).getField(name));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting field: " + name
							+ ", for class: " + c.getName(), e);
		}
	}

	static public Field[] getDeclaredFields(Class c) {
		loon.gwtref.client.Field[] fields = ReflectionCache.getType(c)
				.getDeclaredFields();
		Field[] result = new Field[fields.length];
		for (int i = 0, j = fields.length; i < j; i++) {
			result[i] = new Field(fields[i]);
		}
		return result;
	}

	static public Field getDeclaredField(Class c, String name)
			throws ReflectionException {
		try {
			return new Field(ReflectionCache.getType(c).getField(name));
		} catch (SecurityException e) {
			throw new ReflectionException(
					"Security violation while getting field: " + name
							+ ", for class: " + c.getName(), e);
		}
	}

	static public boolean isAnnotationPresent(Class c,
			Class<? extends java.lang.annotation.Annotation> annotationType) {
		Annotation[] annotations = getAnnotations(c);
		for (Annotation annotation : annotations) {
			if (annotation.getAnnotationType().equals(annotationType))
				return true;
		}
		return false;
	}

	static public Annotation[] getAnnotations(Class c) {
		Type declType = ReflectionCache.getType(c);
		java.lang.annotation.Annotation[] annotations = declType
				.getDeclaredAnnotations();
		Annotation[] result = new Annotation[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			result[i] = new Annotation(annotations[i]);
		}
		Type superType = declType.getSuperclass();
		java.lang.annotation.Annotation[] superAnnotations;
		while (!superType.getClassOfType().equals(Object.class)) {
			superAnnotations = superType.getDeclaredAnnotations();
			for (int i = 0; i < superAnnotations.length; i++) {
				Type annotationType = ReflectionCache
						.getType(superAnnotations[i].annotationType());
				if (annotationType.getDeclaredAnnotation(Inherited.class) != null) {
					boolean duplicate = false;
					for (Annotation annotation : result) {
						if (annotation.getAnnotationType().equals(
								annotationType)) {
							duplicate = true;
							break;
						}
					}
					if (!duplicate) {
						Annotation[] copy = new Annotation[result.length + 1];
						for (int j = 0; j < result.length; j++) {
							copy[j] = result[j];
						}
						copy[result.length] = new Annotation(
								superAnnotations[i]);
						result = copy;
					}
				}
			}
			superType = superType.getSuperclass();
		}

		return result;
	}

	static public Annotation getAnnotation(Class c,
			Class<? extends java.lang.annotation.Annotation> annotationType) {
		Annotation[] annotations = getAnnotations(c);
		for (Annotation annotation : annotations) {
			if (annotation.getAnnotationType().equals(annotationType))
				return annotation;
		}
		return null;
	}

	static public Annotation[] getDeclaredAnnotations(Class c) {
		java.lang.annotation.Annotation[] annotations = ReflectionCache
				.getType(c).getDeclaredAnnotations();
		Annotation[] result = new Annotation[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			result[i] = new Annotation(annotations[i]);
		}
		return result;
	}

	static public Annotation getDeclaredAnnotation(Class c,
			Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation annotation = ReflectionCache.getType(c)
				.getDeclaredAnnotation(annotationType);
		if (annotation != null)
			return new Annotation(annotation);
		return null;
	}
}
