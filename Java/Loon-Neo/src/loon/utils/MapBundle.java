package loon.utils;

public class MapBundle<T> implements Bundle<T> {

	private final ObjectMap<String, T> map;

	public MapBundle() {
		this.map = new ObjectMap<>(20);
	}

	@Override
	public void put(String key, T value) {
		map.put(key, value);
	}

	@Override
	public T get(String key) {
		return get(key, null);
	}

	@Override
	public T get(String key, T defaultValue) {
		T value = map.get(key);
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
		T value = map.remove(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
}
