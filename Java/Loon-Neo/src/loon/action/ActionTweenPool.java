package loon.action;

import loon.utils.TArray;


abstract class ActionTweenPool<T> {

	public interface Callback<T> {
		public void onPool(T obj);
		public void onUnPool(T obj);
	}
	
	private final TArray<T> _objects;
	private final Callback<T> _callback;

	public ActionTweenPool(int initCapacity, Callback<T> _callback) {
		this._objects = new TArray<T>(initCapacity);
		this._callback = _callback;
	}

	protected abstract T create();
	
	public T get() {
		T obj = null;
		try {
			obj = _objects.isEmpty() ? create() : _objects.removeIndex(0);
		} catch (Exception e) {}
		if (obj == null) {
			obj = create();
		}
		if (_callback != null){
			_callback.onUnPool(obj);
		}
		return obj;
	}

	public void free(T obj) {
		if (obj == null) return;
		if (!_objects.contains(obj)) {
			if (_callback != null){
				_callback.onPool(obj);
			}
			_objects.add(obj);
		}
	}

	public void clear() {
		_objects.clear();
	}

	public int size() {
		return _objects.size;
	}

	public void resize(int minCapacity) {
		_objects.ensureCapacity(minCapacity);
	}

}