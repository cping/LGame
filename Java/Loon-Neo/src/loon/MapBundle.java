package loon;

import loon.utils.ObjectMap;

public class MapBundle implements Bundle<Object> {

	private final ObjectMap<String, Object> map;

	public MapBundle() {
		this.map = new ObjectMap<>(20);
	}

	@Override
	public void put(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object get(String key) {
		return get(key, null);
	}

	@Override
	public Object get(String key, Object defaultValue) {
		Object value = map.get(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public Object remove(String key) {
		return remove(key, null);
	}

	@Override
	public Object remove(String key, Object defaultValue) {
		Object value = map.remove(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
}
