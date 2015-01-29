package loon.gwtref.client;

class CachedTypeLookup {
	
	final Class<?> clazz;
	private Type type;

	CachedTypeLookup (Class<?> clazz) {
		this.clazz = clazz;
	}

	Type getType () {
		if (type == null && clazz != null) type = ReflectionCache.getType(clazz);
		return type;
	}

	@Override
	public String toString () {
		return String.valueOf(clazz);
	}
}
