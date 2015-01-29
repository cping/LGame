package loon.gwtref.client;

import java.util.Arrays;

public class Method {
	private static final Parameter[] EMPTY_PARAMS = new Parameter[0];
	final String name;
	final CachedTypeLookup enclosingType;
	final CachedTypeLookup returnType;
	final boolean isAbstract;
	final boolean isFinal;
	final boolean isStatic;
	final boolean isNative;
	final boolean isDefaultAccess;
	final boolean isPrivate;
	final boolean isProtected;
	final boolean isPublic;
	final boolean isVarArgs;
	final boolean isMethod;
	final boolean isConstructor;
	final Parameter[] parameters;
	final int methodId;

	public Method (String name, Class<?> enclosingType, Class<?> returnType, Parameter[] parameters, boolean isAbstract,
		boolean isFinal, boolean isStatic, boolean isDefaultAccess, boolean isPrivate, boolean isProtected, boolean isPublic,
		boolean isNative, boolean isVarArgs, boolean isMethod, boolean isConstructor, int methodId) {
		this.name = name;
		this.enclosingType = new CachedTypeLookup(enclosingType);
		this.parameters = parameters != null ? parameters : EMPTY_PARAMS;
		this.returnType = new CachedTypeLookup(returnType);
		this.isAbstract = isAbstract;
		this.isFinal = isFinal;
		this.isStatic = isStatic;
		this.isNative = isNative;
		this.isDefaultAccess = isDefaultAccess;
		this.isPrivate = isPrivate;
		this.isProtected = isProtected;
		this.isPublic = isPublic;
		this.isVarArgs = isVarArgs;
		this.isMethod = isMethod;
		this.isConstructor = isConstructor;
		this.methodId = methodId;
	}

	public Class<? extends CachedTypeLookup> getEnclosingType () {
		return enclosingType.getClass();
	}

	public Class<? extends CachedTypeLookup> getReturnType () {
		return returnType.getClass();
	}

	public Parameter[] getParameters () {
		return parameters;
	}

	public String getName () {
		return name;
	}

	public boolean isAbstract () {
		return isAbstract;
	}

	public boolean isFinal () {
		return isFinal;
	}

	public boolean isDefaultAccess () {
		return isDefaultAccess;
	}

	public boolean isPrivate () {
		return isPrivate;
	}

	public boolean isProtected () {
		return isProtected;
	}

	public boolean isPublic () {
		return isPublic;
	}

	public boolean isNative () {
		return isNative;
	}

	public boolean isVarArgs () {
		return isVarArgs;
	}

	public boolean isStatic () {
		return isStatic;
	}

	public boolean isMethod () {
		return isMethod;
	}

	public boolean isConstructor () {
		return isConstructor;
	}

	public Object invoke (Object obj, Object... params) {
		if (parameters.length != (params != null ? params.length : 0)) throw new IllegalArgumentException("Parameter mismatch");

		return ReflectionCache.invoke(this, obj, params);
	}

	boolean match (String name, Class<?>... types) {
		return this.name.equals(name) && match(types);
	}

	boolean match (Class<?>... types) {
		if (types == null) return parameters.length == 0;
		if (types.length != parameters.length) return false;
		for (int i = 0; i < types.length; i++) {
			Type t1 = parameters[i].getType();
			Type t2 = ReflectionCache.getType(types[i]);
			if (t1 != t2 && !t1.isAssignableFrom(t2)) return false;
		}
		return true;
	}

	@Override
	public String toString () {
		return "Method [name=" + name + ", enclosingType=" + enclosingType + ", returnType=" + returnType + ", isAbstract="
			+ isAbstract + ", isFinal=" + isFinal + ", isStatic=" + isStatic + ", isNative=" + isNative + ", isDefaultAccess="
			+ isDefaultAccess + ", isPrivate=" + isPrivate + ", isProtected=" + isProtected + ", isPublic=" + isPublic
			+ ", isVarArgs=" + isVarArgs + ", isMethod=" + isMethod + ", isConstructor=" + isConstructor + ", parameters="
			+ Arrays.toString(parameters) + "]";
	}
}
