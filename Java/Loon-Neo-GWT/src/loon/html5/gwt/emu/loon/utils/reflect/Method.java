package loon.utils.reflect;

import loon.gwtref.client.Parameter;

@SuppressWarnings("rawtypes")
public final class Method {

	private final loon.gwtref.client.Method method;

	Method(loon.gwtref.client.Method method) {
		this.method = method;
	}

	public String getName() {
		return method.getName();
	}

	public Class getReturnType() {
		return method.getReturnType();
	}

	public Class[] getParameterTypes() {
		Parameter[] parameters = method.getParameters();
		Class[] parameterTypes = new Class[parameters.length];
		for (int i = 0, j = parameters.length; i < j; i++) {
			parameterTypes[i] = parameters[i].getClazz();
		}
		return parameterTypes;
	}

	public Class getDeclaringClass() {
		return method.getEnclosingType();
	}

	public boolean isAccessible() {
		return method.isPublic();
	}

	public void setAccessible(boolean accessible) {
	}

	public boolean isAbstract() {
		return method.isAbstract();
	}

	public boolean isDefaultAccess() {
		return !isPrivate() && !isProtected() && !isPublic();
	}

	public boolean isFinal() {
		return method.isFinal();
	}

	public boolean isPrivate() {
		return method.isPrivate();
	}

	public boolean isProtected() {
		return method.isProtected();
	}

	public boolean isPublic() {
		return method.isPublic();
	}

	public boolean isNative() {
		return method.isNative();
	}

	public boolean isStatic() {
		return method.isStatic();
	}

	public boolean isVarArgs() {
		return method.isVarArgs();
	}

	public Object invoke(Object obj, Object... args) throws ReflectionException {
		try {
			return method.invoke(obj, args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Illegal argument(s) supplied to method: " + getName(), e);
		}
	}

	public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
		if (annotations != null) {
			for (java.lang.annotation.Annotation annotation : annotations) {
				if (annotation.annotationType().equals(annotationType)) {
					return true;
				}
			}
		}
		return false;
	}

	public Annotation[] getDeclaredAnnotations() {
		java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
		Annotation[] result = new Annotation[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			result[i] = new Annotation(annotations[i]);
		}
		return result;
	}

	public Annotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
		java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
		for (java.lang.annotation.Annotation annotation : annotations) {
			if (annotation.annotationType().equals(annotationType)) {
				return new Annotation(annotation);
			}
		}
		return null;
	}

}
