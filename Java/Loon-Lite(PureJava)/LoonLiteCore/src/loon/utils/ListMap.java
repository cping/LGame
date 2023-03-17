package loon.utils;

import java.util.Iterator;

import loon.LRelease;
import loon.LSysException;

public class ListMap<K, V> implements Iterable<V>, IArray, LRelease {

	public final static class ListMapIterable<T> implements Iterable<T> {

		private final ListMap<?, T> array;
		private final boolean allowRemove;
		private ListMapIterator<T> iterator1, iterator2;

		public ListMapIterable(ListMap<?, T> array) {
			this(array, true);
		}

		public ListMapIterable(ListMap<?, T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator1 == null) {
				iterator1 = new ListMapIterator<>(array, allowRemove);
				iterator2 = new ListMapIterator<>(array, allowRemove);
			}
			if (!iterator1.valid) {
				iterator1.index = 0;
				iterator1.valid = true;
				iterator2.valid = false;
				return iterator1;
			}
			iterator2.index = 0;
			iterator2.valid = true;
			iterator1.valid = false;
			return iterator2;
		}
	}

	public final static class ListMapIterator<T> implements LIterator<T>, Iterable<T> {

		private final ListMap<?, T> array;
		private final boolean allowRemove;
		int index;
		boolean valid = true;

		public ListMapIterator(ListMap<?, T> array) {
			this(array, true);
		}

		public ListMapIterator(ListMap<?, T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new LSysException("iterator() cannot be used nested.");
			}
			return index < array.size;
		}

		@Override
		public T next() {
			if (index >= array.size) {
				return null;
			}
			if (!valid) {
				throw new LSysException("iterator() cannot be used nested.");
			}
			return array.values[index++];
		}

		@Override
		public void remove() {
			if (!allowRemove) {
				throw new LSysException("Remove not allowed.");
			}
			index--;
			array.removeIndex(index);
		}

		public void reset() {
			index = 0;
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
	}

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
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
	}

	public ListMap(ListMap<K, V> array) {
		this(array.ordered, array.size);
		size = array.size;
		System.arraycopy(array.keys, 0, this.keys, 0, size);
		System.arraycopy(array.values, 0, this.values, 0, size);
	}

	public void put(K key, V value) {
		if (size == this.keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		int index = indexOfKey(key);
		if (index == -1)
			index = size++;
		this.keys[index] = key;
		this.values[index] = value;
	}

	public void put(K key, V value, int index) {
		if (size == this.keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		int existingIndex = indexOfKey(key);
		if (existingIndex != -1)
			removeIndex(existingIndex);
		System.arraycopy(this.keys, index, this.keys, index + 1, size - index);
		System.arraycopy(this.values, index, this.values, index + 1, size - index);
		this.keys[index] = key;
		this.values[index] = value;
		size++;
	}

	public void putAll(ListMap<K, V> map) {
		putAll(map, 0, map.size);
	}

	public void putAll(ListMap<K, V> map, int offset, int length) {
		if (offset + length > map.size)
			throw new LSysException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
		int sizeNeeded = size + length - offset;
		if (sizeNeeded >= this.keys.length)
			resize(MathUtils.max(8, (int) (sizeNeeded * 1.75f)));
		System.arraycopy(map.keys, offset, this.keys, size, length);
		System.arraycopy(map.values, offset, this.values, size, length);
		size += length;
	}

	public V get(K key) {
		Object[] keys = this.keys;
		int i = size - 1;
		if (key == null) {
			for (; i >= 0; i--) {
				if (keys[i] == key)
					return this.values[i];
			}
		} else {
			for (; i >= 0; i--) {
				if (key.equals(keys[i]))
					return this.values[i];
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
					return this.keys[i];
				}
			}
		} else {
			for (; i >= 0; i--) {
				if (value.equals(values[i])) {
					return this.keys[i];
				}
			}
		}
		return null;
	}

	public K getKeyAt(int index) {
		if (index >= size) {
			throw new LSysException(String.valueOf(index));
		}
		return this.keys[index];
	}

	public V getValueAt(int index) {
		if (index >= size) {
			throw new LSysException(String.valueOf(index));
		}
		return this.values[index];
	}

	public K firstKey() {
		if (size == 0)
			throw new LSysException("Map is empty.");
		return this.keys[0];
	}

	public V firstValue() {
		if (size == 0)
			throw new LSysException("Map is empty.");
		return this.values[0];
	}

	public void setKey(int index, K key) {
		if (index >= size)
			throw new LSysException(String.valueOf(index));
		this.keys[index] = key;
	}

	public void setValue(int index, V value) {
		if (index >= size)
			throw new LSysException(String.valueOf(index));
		this.values[index] = value;
	}

	public V last() {
		return this.values[size < 1 ? 0 : size - 1];
	}

	public V first() {
		if (size == 0)
			throw new LSysException("ListMap is empty.");
		return this.values[0];
	}

	public V pop() {
		if (size == 0)
			throw new LSysException("ListMap is empty.");
		--size;
		V item = this.values[size];
		if (item != null) {
			removeValue(item, false);
		}
		return item;
	}

	public void insert(int index, K key, V value) {
		if (index > size)
			throw new LSysException(String.valueOf(index));
		if (size == this.keys.length)
			resize(MathUtils.max(8, (int) (size * 1.75f)));
		if (ordered) {
			System.arraycopy(this.keys, index, this.keys, index + 1, size - index);
			System.arraycopy(this.values, index, this.values, index + 1, size - index);
		} else {
			this.keys[size] = this.keys[index];
			this.values[size] = this.values[index];
		}
		size++;
		this.keys[index] = key;
		this.values[index] = value;
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

	public boolean containsValue(V value) {
		return containsValue(value, true);
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

	public ListMap<K, V> cpy() {
		return new ListMap<>(this);
	}

	public TArray<K> keysToArray() {
		final K[] keys = this.keys;
		final int len = size;
		TArray<K> list = new TArray<>(len);
		for (int i = 0, n = len; i < n; i++) {
			list.add(keys[i]);
		}
		return list;
	}

	public TArray<V> valuesToArray() {
		final V[] values = this.values;
		final int len = size;
		TArray<V> list = new TArray<>(len);
		for (int i = 0, n = len; i < n; i++) {
			list.add(values[i]);
		}
		return list;
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
					V value = this.values[i];
					removeIndex(i);
					return value;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (key.equals(keys[i])) {
					V value = this.values[i];
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
			throw new LSysException(String.valueOf(index));
		Object[] keys = this.keys;
		size--;
		if (ordered) {
			System.arraycopy(keys, index + 1, keys, index, size - index);
			System.arraycopy(this.values, index + 1, this.values, index, size - index);
		} else {
			keys[index] = keys[size];
			this.values[index] = this.values[size];
		}
		keys[size] = null;
		this.values[size] = null;
	}

	public K peekKey() {
		return this.keys[size - 1];
	}

	public V peekValue() {
		return this.values[size - 1];
	}

	@Override
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
		if (sizeNeeded >= this.keys.length)
			resize(MathUtils.max(8, sizeNeeded));
	}

	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		K[] newKeys = (K[]) new Object[newSize];
		System.arraycopy(this.keys, 0, newKeys, 0, MathUtils.min(this.keys.length, newKeys.length));
		this.keys = newKeys;
		V[] newValues = (V[]) new Object[newSize];
		System.arraycopy(this.values, 0, newValues, 0, MathUtils.min(this.values.length, newValues.length));
		this.values = newValues;
	}

	public void reverse() {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			K tempKey = this.keys[i];
			this.keys[i] = this.keys[ii];
			this.keys[ii] = tempKey;

			V tempValue = this.values[i];
			this.values[i] = this.values[ii];
			this.values[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			K tempKey = this.keys[i];
			this.keys[i] = this.keys[ii];
			this.keys[ii] = tempKey;

			V tempValue = this.values[i];
			this.values[i] = this.values[ii];
			this.values[ii] = tempValue;
		}
	}

	public void truncate(int newSize) {
		if (size <= newSize)
			return;
		for (int i = newSize; i < size; i++) {
			this.keys[i] = null;
			this.values[i] = null;
		}
		size = newSize;
	}

	private ListMapIterable<V> _iterable;

	@Override
	public Iterator<V> iterator() {
		if (_iterable == null) {
			_iterable = new ListMapIterable<>(this);
		}
		return _iterable.iterator();
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = size - 1; i > -1; i--) {
			hashCode = 31 * hashCode + (this.keys[i] == null ? 0 : this.keys[i].hashCode());
			hashCode = 31 * hashCode + (this.values[i] == null ? 0 : this.values[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0 || this.keys == null || this.values == null;
	}

	@Override
	public String toString() {
		if (size == 0)
			return "[]";
		K[] keys = this.keys;
		V[] values = this.values;
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		buffer.append(keys[0]);
		buffer.append('=');
		buffer.append(values[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(keys[i]);
			buffer.append('=');
			buffer.append(values[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public void close() {
		this.keys = null;
		this.values = null;
		this.size = 0;
	}

}
