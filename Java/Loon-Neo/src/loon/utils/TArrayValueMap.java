package loon.utils;

public class TArrayValueMap<K, V> {

	private final ObjectMap<K, V> map;
	private final TArray<V> values;

	private final TArray<K> tmpKeyArray;

	public TArrayValueMap() {
		this(16);
	}

	public TArrayValueMap(int capacity) {
		map = new ObjectMap<>(capacity);
		values = new TArray<>(true, capacity);
		tmpKeyArray = new TArray<>(capacity);
	}

	public void put(K key, V value) {
		map.put(key, value);
		values.add(value);
	}

	public V get(K key) {
		return map.get(key);
	}

	public boolean contains(K key) {
		return map.containsKey(key);
	}

	public V getValueAt(int valueIndex) {
		return values.get(valueIndex);
	}

	public V remove(K key) {
		V value = map.remove(key);
		if (value != null) {
			values.removeValue(value, true);
		}
		return value;
	}

	public V removeByValue(V value) {
		K key = findKey(value);
		return remove(key);
	}

	public K findKey(V value) {
		for (ObjectMap.Entry<K, V> entry : map.entries()) {
			if (entry.value == value) {
				return entry.key;
			}
		}
		return null;
	}

	public void clear() {
		map.clear();
		values.clear();
	}

	public int size() {
		return map.size();
	}

	public TArray<V> getValues() {
		return values;
	}

	public TArray<K> getKeys() {
		TArray<K> result = tmpKeyArray;
		result.clear();
		for (K key : map.keys()) {
			result.add(key);
		}
		return result;
	}
}
