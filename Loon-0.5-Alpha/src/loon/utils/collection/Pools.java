package loon.utils.collection;


@SuppressWarnings({"rawtypes","unchecked"})
public class Pools {
	static private final ObjectMap<Class, Pool> typePools = new ObjectMap();

	static public <T> Pool<T> get(Class<T> type, int max) {
		Pool pool = typePools.get(type);
		if (pool == null) {
			pool = new ReflectionPool(type, 4, max);
			typePools.put(type, pool);
		}
		return pool;
	}

	static public <T> Pool<T> get(Class<T> type) {
		return get(type, 100);
	}

	static public <T> void set(Class<T> type, Pool<T> pool) {
		typePools.put(type, pool);
	}

	static public <T> T obtain(Class<T> type) {
		return get(type).obtain();
	}

	static public void free(Object object) {
		if (object == null)
			throw new IllegalArgumentException("Object cannot be null.");
		Pool pool = typePools.get(object.getClass());
		if (pool == null)
			return;
		pool.free(object);
	}

	static public void freeAll(TArray objects) {
		freeAll(objects, false);
	}

	static public void freeAll(TArray objects, boolean samePool) {
		if (objects == null)
			throw new IllegalArgumentException("Objects cannot be null.");
		Pool pool = null;
		for (int i = 0, n = objects.size; i < n; i++) {
			Object object = objects.get(i);
			if (object == null)
				continue;
			if (pool == null) {
				pool = typePools.get(object.getClass());
				if (pool == null){
					continue;
				}
			}
			pool.free(object);
			if (!samePool){
				pool = null;
			}
		}
	}

	private Pools() {
	}
}
