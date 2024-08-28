package loon.utils.reflect;

import loon.gwtref.client.Type;

@SuppressWarnings("rawtypes")
public final class Field {

	private final loon.gwtref.client.Field field;

	Field (loon.gwtref.client.Field field) {
		this.field = field;
	}

	public String getName () {
		return field.getName();
	}

	public Class getType () {
		return field.getType().getClassOfType();
	}

	public Class getDeclaringClass () {
		return field.getEnclosingType().getClassOfType();
	}

	public boolean isAccessible () {
		return field.isPublic();
	}

	public void setAccessible (boolean accessible) {
	}

	public boolean isDefaultAccess () {
		return !isPrivate() && !isProtected() && !isPublic();
	}

	public boolean isFinal () {
		return field.isFinal();
	}

	public boolean isPrivate () {
		return field.isPrivate();
	}

	public boolean isProtected () {
		return field.isProtected();
	}

	public boolean isPublic () {
		return field.isPublic();
	}

	public boolean isStatic () {
		return field.isStatic();
	}

	public boolean isTransient () {
		return field.isTransient();
	}

	public boolean isVolatile () {
		return field.isVolatile();
	}

	public boolean isSynthetic () {
		return field.isSynthetic();
	}

	public Class getElementType (int index) {
		Type elementType = field.getElementType(index);
		return elementType != null ? elementType.getClassOfType() : null;
	}

	public boolean isAnnotationPresent (Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
		for (java.lang.annotation.Annotation annotation : annotations) {
			if (annotation.annotationType().equals(annotationType)) {
				return true;
			}
		}
		return false;
	}

	public Annotation[] getDeclaredAnnotations () {
		java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
		Annotation[] result = new Annotation[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			result[i] = new Annotation(annotations[i]);
		}
		return result;
	}

	public Annotation getDeclaredAnnotation (Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
		for (java.lang.annotation.Annotation annotation : annotations) {
			if (annotation.annotationType().equals(annotationType)) {
				return new Annotation(annotation);
			}
		}
		return null;
	}

	public Object get (Object obj) throws ReflectionException {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Could not get " + getDeclaringClass() + "#" + getName() + ": " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Illegal access to field " + getName() + ": " + e.getMessage(), e);
		}
	}

	public void set (Object obj, Object value) throws ReflectionException {
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Could not set " + getDeclaringClass() + "#" + getName() + ": " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Illegal access to field " + getName() + ": " + e.getMessage(), e);
		}
	}
}
