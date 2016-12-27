package loon.utils;

public class ListMap<K, V> implements IArray {

	public K[] keys;
	public V[] values;
	public int size;
	public boolean ordered;

	public ListMap() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public ListMap(int capacity) {
		this(true, capacity);
	}

	@SuppressWarnings("unchecked")
	public ListMap(boolean ordered, int capacity) {
		this.ordered = ordered;
		keys = (K[]) new Object[capacity];
		values = (V[]) new Object[capacity];
	}

	public ListMap(ListMap<K, V> array) {
		this(array.ordered, array.size);
		size = array.size;
		System.arraycopy(array.keys, 0, keys, 0, size);
		System.arraycopy(array.values, 0, values, 0, size);
	}

	public void put(K key, V value) {
		if (size == keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		int index = indexOfKey(key);
		if (index == -1)
			index = size++;
		keys[index] = key;
		values[index] = value;
	}

	public void put(K key, V value, int index) {
		if (size == keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		int existingIndex = indexOfKey(key);
		if (existingIndex != -1)
			removeIndex(existingIndex);
		System.arraycopy(keys, index, keys, index + 1, size - index);
		System.arraycopy(values, index, values, index + 1, size - index);
		keys[index] = key;
		values[index] = value;
		size++;
	}

	public void putAll(ListMap<K, V> map) {
		putAll(map, 0, map.size);
	}

	public void putAll(ListMap<K, V> map, int offset, int length) {
		if (offset + length > map.size)
			throw new IllegalArgumentException(
					"offset + length must be <= size: " + offset + " + "
							+ length + " <= " + map.size);
		int sizeNeeded = size + length - offset;
		if (sizeNeeded >= keys.length)
			resize(MathUtils.max(8, (int) (sizeNeeded * 1.75f)));
		System.arraycopy(map.keys, offset, keys, size, length);
		System.arraycopy(map.values, offset, values, size, length);
		size += length;
	}

	public V get(K key) {
		Object[] keys = this.keys;
		int i = size - 1;
		if (key == null) {
			for (; i >= 0; i--) {
				if (keys[i] == key)
					return values[i];
			}
		} else {
			for (; i >= 0; i--) {
				if (key.equals(keys[i]))
					return values[i];
			}
		}
		return null;
	}

	public K getKey(V value, boolean identity) {
		Object[] values = this.values;
		int i = size - 1;
		if (identity || value == null) {
			for (; i >= 0; i--) {
				if (values[i] == value) {
					return keys[i];
				}
			}
		} else {
			for (; i >= 0; i--) {
				if (value.equals(values[i])) {
					return keys[i];
				}
			}
		}
		return null;
	}

	public K getKeyAt(int index) {
		if (index >= size) {
			throw new RuntimeException(String.valueOf(index));
		}
		return keys[index];
	}

	public V getValueAt(int index) {
		if (index >= size) {
			throw new RuntimeException(String.valueOf(index));
		}
		return values[index];
	}

	public K firstKey() {
		if (size == 0)
			throw new IllegalStateException("Map is empty.");
		return keys[0];
	}

	public V firstValue() {
		if (size == 0)
			throw new IllegalStateException("Map is empty.");
		return values[0];
	}

	public void setKey(int index, K key) {
		if (index >= size)
			throw new RuntimeException(String.valueOf(index));
		keys[index] = key;
	}

	public void setValue(int index, V value) {
		if (index >= size)
			throw new RuntimeException(String.valueOf(index));
		values[index] = value;
	}

	public void insert(int index, K key, V value) {
		if (index > size)
			throw new RuntimeException(String.valueOf(index));
		if (size == keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		if (ordered) {
			System.arraycopy(keys, index, keys, index + 1, size - index);
			System.arraycopy(values, index, values, index + 1, size - index);
		} else {
			keys[size] = keys[index];
			values[size] = values[index];
		}
		size++;
		keys[index] = key;
		values[index] = value;
	}

	public boolean containsKey(K key) {
		K[] keys = this.keys;
		int i = size - 1;
		if (key == null) {
			while (i >= 0)
				if (keys[i--] == key)
					return true;
		} else {
			while (i >= 0)
				if (key.equals(keys[i--]))
					return true;
		}
		return false;
	}

	public boolean containsValue(V value, boolean identity) {
		V[] values = this.values;
		int i = size - 1;
		if (identity || value == null) {
			while (i >= 0)
				if (values[i--] == value)
					return true;
		} else {
			while (i >= 0)
				if (value.equals(values[i--]))
					return true;
		}
		return false;
	}

	public int indexOfKey(K key) {
		Object[] keys = this.keys;
		if (key == null) {
			for (int i = 0, n = size; i < n; i++)
				if (keys[i] == key)
					return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (key.equals(keys[i]))
					return i;
		}
		return -1;
	}

	public int indexOfValue(V value, boolean identity) {
		Object[] values = this.values;
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++)
				if (values[i] == value)
					return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (value.equals(values[i]))
					return i;
		}
		return -1;
	}

	public V removeKey(K key) {
		Object[] keys = this.keys;
		if (key == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (keys[i] == key) {
					V value = values[i];
					removeIndex(i);
					return value;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (key.equals(keys[i])) {
					V value = values[i];
					removeIndex(i);
					return value;
				}
			}
		}
		return null;
	}

	public boolean removeValue(V value, boolean identity) {
		Object[] values = this.values;
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (values[i] == value) {
					removeIndex(i);
					return true;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (value.equals(values[i])) {
					removeIndex(i);
					return true;
				}
			}
		}
		return false;
	}

	public void removeIndex(int index) {
		if (index >= size)
			throw new RuntimeException(String.valueOf(index));
		Object[] keys = this.keys;
		size--;
		if (ordered) {
			System.arraycopy(keys, index + 1, keys, index, size - index);
			System.arraycopy(values, index + 1, values, index, size - index);
		} else {
			keys[index] = keys[size];
			values[index] = values[size];
		}
		keys[size] = null;
		values[size] = null;
	}

	public K peekKey() {
		return keys[size - 1];
	}

	public V peekValue() {
		return values[size - 1];
	}

	public void clear() {
		K[] keys = this.keys;
		V[] values = this.values;
		for (int i = 0, n = size; i < n; i++) {
			keys[i] = null;
			values[i] = null;
		}
		size = 0;
	}

	public void shrink() {
		resize(size);
	}

	public void ensureCapacity(int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= keys.length)
			resize(MathUtils.max(8, sizeNeeded));
	}

	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		K[] newKeys = (K[]) new Object[newSize];
		System.arraycopy(keys, 0, newKeys, 0,
				MathUtils.min(keys.length, newKeys.length));
		this.keys = newKeys;
		V[] newValues = (V[]) new Object[newSize];
		System.arraycopy(values, 0, newValues, 0,
				MathUtils.min(values.length, newValues.length));
		this.values = newValues;
	}

	public void reverse() {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	public void truncate(int newSize) {
		if (size <= newSize)
			return;
		for (int i = newSize; i < size; i++) {
			keys[i] = null;
			values[i] = null;
		}
		size = newSize;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0 || keys == null || values == null;
	}

	public String toString() {
		if (size == 0)
			return "{}";
		K[] keys = this.keys;
		V[] values = this.values;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		buffer.append(keys[0]);
		buffer.append('=');
		buffer.append(values[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(keys[i]);
			buffer.append('=');
			buffer.append(values[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

}
