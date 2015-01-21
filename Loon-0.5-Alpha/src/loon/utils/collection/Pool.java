package loon.utils.collection;


@SuppressWarnings({"rawtypes","unchecked"})
abstract public class Pool<T> {

	public final int max;

	public int peak;

	private final TArray<T> freeObjects;

	public Pool() {
		this(16, Integer.MAX_VALUE);
	}

	public Pool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public Pool(int initialCapacity, int max) {
		freeObjects = new TArray(false, initialCapacity);
		this.max = max;
	}

	abstract protected T newObject();

	public T obtain() {
		return freeObjects.size == 0 ? newObject() : freeObjects.pop();
	}

	public void free(T object) {
		if (object == null)
			throw new IllegalArgumentException("object cannot be null.");
		if (freeObjects.size < max) {
			freeObjects.add(object);
			peak = Math.max(peak, freeObjects.size);
		}
		if (object instanceof Poolable)
			((Poolable) object).reset();
	}

	public void freeAll(TArray<T> objects) {
		if (objects == null)
			throw new IllegalArgumentException("object cannot be null.");
		TArray<T> freeObjects = this.freeObjects;
		int max = this.max;
		for (int i = 0; i < objects.size; i++) {
			T object = objects.get(i);
			if (object == null)
				continue;
			if (freeObjects.size < max)
				freeObjects.add(object);
			if (object instanceof Poolable)
				((Poolable) object).reset();
		}
		peak = Math.max(peak, freeObjects.size);
	}

	public void clear() {
		freeObjects.clear();
	}

	public int getFree() {
		return freeObjects.size;
	}

	static public interface Poolable {

		public void reset();
	}
}
