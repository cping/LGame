package loon.utils;

public class MapBundle<T> implements Bundle<T> {

	protected final ObjectMap<String, T> _mapBundle;

	public MapBundle() {
		this._mapBundle = new ObjectMap<>(20);
	}

	@Override
	public void put(String key, T value) {
		_mapBundle.put(key, value);
	}

	@Override
	public T get(String key) {
		return get(key, null);
	}

	@Override
	public T get(String key, T defaultValue) {
		T value = _mapBundle.get(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public T remove(String key) {
		return remove(key, null);
	}

	@Override
	public T remove(String key, T defaultValue) {
		T value = _mapBundle.remove(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public int size() {
		return _mapBundle.size;
	}

	@Override
	public void clear() {
		_mapBundle.clear();
	}

	@Override
	public boolean isEmpty() {
		return _mapBundle.isEmpty();
	}
}
