package loon.utils;

import loon.Session;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

public class MapBundle<T> implements Bundle<T> {

	protected final ObjectMap<String, T> _mapBundle;

	public MapBundle() {
		this._mapBundle = new ObjectMap<String, T>();
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

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@SuppressWarnings("unchecked")
	public MapBundle<T> loadFrom(Session session) {
		if (session != null) {
			final int size = session.size();
			if (size == 0) {
				session.load();
			}
			for (int i = 0; i < size; i++) {
				ArrayMap map = session.getRecords(i);
				if (map != null) {
					for (int j = 0; j < map.size(); j++) {
						loon.utils.ArrayMap.Entry entry = map.getEntry(j);
						if (entry != null && entry.key != null && entry.value != null) {
							put(HelperUtils.toStr(entry.key), (T) entry.value);
						}
					}
				}
			}
		}
		return this;
	}

	public MapBundle<T> savaTo(Session session) {
		if (session != null) {
			Entries<String, T> entries = _mapBundle.entries();
			for (entries.iterator(); entries.hasNext();) {
				Entry<String, T> entry = entries.next();
				if (entry != null && entry.key != null && entry.value != null) {
					session.add(entry.key, HelperUtils.toStr(entry.value));
				}
			}
			session.save();
		}
		return this;
	}

	public MapBundle<T> savaTo(MapBundle<T> saved) {
		if (saved != null) {
			Entries<String, T> entries = _mapBundle.entries();
			for (entries.iterator(); entries.hasNext();) {
				Entry<String, T> entry = entries.next();
				if (entry != null && entry.key != null && entry.value != null) {
					saved.put(entry.key, entry.value);
				}
			}
		}
		return this;
	}

	public MapBundle<T> loadFrom(MapBundle<T> load) {
		if (load != null) {
			Entries<String, T> entries = load._mapBundle.entries();
			for (entries.iterator(); entries.hasNext();) {
				Entry<String, T> entry = entries.next();
				if (entry != null && entry.key != null && entry.value != null) {
					put(entry.key, entry.value);
				}
			}
		}
		return this;
	}

}
