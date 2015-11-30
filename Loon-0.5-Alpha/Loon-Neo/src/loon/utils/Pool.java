package loon.utils;

public abstract class Pool<T> {
	
	public final int max;

	public int peak;

	private final Array<T> freeObjects;
	
	public Pool () {
		this(Integer.MAX_VALUE);
	}
	
	public Pool (int max) {
		freeObjects = new Array<T>();
		this.max = max;
	}

	abstract protected T newObject ();

	public T obtain () {
		return freeObjects.size() == 0 ? newObject() : freeObjects.pop();
	}

	public void free (T object) {
		if (object == null) throw new RuntimeException("object cannot be null.");
		if (freeObjects.size() < max) {
			freeObjects.add(object);
			peak = MathUtils.max(peak, freeObjects.size());
		}
		if (object instanceof Poolable) ((Poolable)object).reset();
	}

	public void freeAll (Array<T> objects) {
		if (objects == null){
			throw new RuntimeException("object cannot be null.");
		}
		for (;objects.hashNext();) {
			T object = objects.next();
			if (object == null){
				continue;
			}
			if (freeObjects.size() < max) {
				freeObjects.add(object);
			}
			if (object instanceof Poolable){
				((Poolable)object).reset();
			}
		}
		objects.stopNext();
		peak = MathUtils.max(peak, freeObjects.size());
	}

	public void clear () {
		freeObjects.clear();
	}

	public int getFree () {
		return freeObjects.size();
	}

	static public interface Poolable {
		public void reset ();
	}
}
