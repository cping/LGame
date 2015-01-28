package loon.utils.collection;

import java.util.NoSuchElementException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OrderedMap<K, V> extends ObjectMap<K, V> {
	final TArray<K> keys;

	private Entries entries1, entries2;
	private Values values1, values2;
	private Keys keys1, keys2;

	public OrderedMap() {
		keys = new TArray();
	}

	public OrderedMap(int initialCapacity) {
		super(initialCapacity);
		keys = new TArray(capacity);
	}

	public OrderedMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		keys = new TArray(capacity);
	}

	public OrderedMap(ObjectMap<? extends K, ? extends V> map) {
		super(map);
		keys = new TArray(capacity);
	}

	public V put(K key, V value) {
		if (!containsKey(key))
			keys.add(key);
		return super.put(key, value);
	}

	public V remove(K key) {
		keys.removeValue(key, false);
		return super.remove(key);
	}

	public void clear(int maximumCapacity) {
		keys.clear();
		super.clear(maximumCapacity);
	}

	public void clear() {
		keys.clear();
		super.clear();
	}

	public TArray<K> orderedKeys() {
		return keys;
	}

	public Entries<K, V> iterator() {
		return entries();
	}

	public Entries<K, V> entries() {
		if (entries1 == null) {
			entries1 = new OrderedMapEntries(this);
			entries2 = new OrderedMapEntries(this);
		}
		if (!entries1.valid) {
			entries1.reset();
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.reset();
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	public Values<V> values() {
		if (values1 == null) {
			values1 = new OrderedMapValues(this);
			values2 = new OrderedMapValues(this);
		}
		if (!values1.valid) {
			values1.reset();
			values1.valid = true;
			values2.valid = false;
			return values1;
		}
		values2.reset();
		values2.valid = true;
		values1.valid = false;
		return values2;
	}

	public Keys<K> keys() {
		if (keys1 == null) {
			keys1 = new OrderedMapKeys(this);
			keys2 = new OrderedMapKeys(this);
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	public String toString() {
		if (size == 0)
			return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		TArray<K> keys = this.keys;
		for (int i = 0, n = keys.size; i < n; i++) {
			K key = keys.get(i);
			if (i > 0)
				buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(get(key));
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class OrderedMapEntries<K, V> extends Entries<K, V> {
		private TArray<K> keys;

		public OrderedMapEntries(OrderedMap<K, V> map) {
			super(map);
			keys = map.keys;
		}

		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public Entry next() {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new RuntimeException("#iterator() cannot be used nested.");
			entry.key = keys.get(nextIndex);
			entry.value = map.get(entry.key);
			nextIndex++;
			hasNext = nextIndex < map.size;
			return entry;
		}

		public void remove() {
			if (currentIndex < 0)
				throw new IllegalStateException(
						"next must be called before remove.");
			map.remove(entry.key);
		}
	}

	static public class OrderedMapKeys<K> extends Keys<K> {
		private TArray<K> keys;

		public OrderedMapKeys(OrderedMap<K, ?> map) {
			super(map);
			keys = map.keys;
		}

		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public K next() {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new RuntimeException("#iterator() cannot be used nested.");
			K key = keys.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < map.size;
			return key;
		}

		public void remove() {
			if (currentIndex < 0)
				throw new IllegalStateException(
						"next must be called before remove.");
			map.remove(keys.get(nextIndex - 1));
		}
	}

	static public class OrderedMapValues<V> extends Values<V> {
		private TArray keys;

		public OrderedMapValues(OrderedMap<?, V> map) {
			super(map);
			keys = map.keys;
		}

		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		public V next() {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw new RuntimeException("#iterator() cannot be used nested.");
			V value = (V) map.get(keys.get(nextIndex));
			nextIndex++;
			hasNext = nextIndex < map.size;
			return value;
		}

		public void remove() {
			if (currentIndex < 0)
				throw new IllegalStateException(
						"next must be called before remove.");
			map.remove(keys.get(nextIndex - 1));
		}
	}
}
