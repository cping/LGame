/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils;

import loon.LSystem;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.LIterator;
import loon.utils.MathUtils;

/**
 * key-value形式的数据集合,无序排列,作用近似于HashMap,在大数据存储上性能比HashMap更好<br>
 * (所有Key统一建立有序索引,查表时更容易定位,百万以上数据读写能明显看出差别,百万以内差别不大,还略慢一些-_-)
 * 
 * @param <K>
 * @param <V>
 */
public class ObjectMap<K, V> implements Iterable<ObjectMap.Entry<K, V>>, IArray {

	private Values<V> values1, values2;

	public Values<V> values() {
		if (values1 == null) {
			values1 = new Values<V>(this);
			values2 = new Values<V>(this);
		}
		if (!values1._valid) {
			values1.reset();
			values1._valid = true;
			values2._valid = false;
			return values1;
		}
		values2.reset();
		values2._valid = true;
		values1._valid = false;
		return values2;
	}

	public static final class Values<V> implements Iterable<V>, LIterator<V> {

		public boolean _valid;
		boolean _simpleOrder;
		int _nextIndex;
		int _lastIndex;
		int _expectedModCount;
		ObjectMap<?, V> _map;

		Values(ObjectMap<?, V> map) {
			this._map = map;
		}

		public void reset() {
			this._simpleOrder = !(_map instanceof OrderedMap<?, ?>);
			this._nextIndex = _map.iterateFirst();
			this._lastIndex = NO_INDEX;
			this._expectedModCount = _map.modCount;
		}

		@Override
		public final boolean hasNext() {
			if (!_valid) {
				return false;
			}
			return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
		}

		@SuppressWarnings("unchecked")
		public final V next() {
			if (!_valid) {
				return null;
			}
			if (_map.modCount != _expectedModCount) {
				return null;
			}
			if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex) {
				return null;
			}
			_lastIndex = _nextIndex;
			if (_simpleOrder) {
				do {
					_nextIndex++;
				} while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
						&& _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
			} else {
				_nextIndex = _map.iterateNext(_nextIndex);
			}
			if (_lastIndex == NULL_INDEX) {
				return null;
			}
			return ((V) (_map.keyValueTable[(_lastIndex << 1) + 2]));
		}

		@Override
		public final void remove() {
			if (!_valid) {
				return;
			}
			if (_lastIndex == NO_INDEX) {
				return;
			}
			if (_map.modCount != _expectedModCount) {
				return;
			}
			_map.removeKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
					_lastIndex);
			_lastIndex = NO_INDEX;
			_expectedModCount = _map.modCount;
		}

		@Override
		public Values<V> iterator() {
			return this;
		}
	}

	private Keys<K> keys1, keys2;

	public Keys<K> keys() {
		if (keys1 == null) {
			keys1 = new Keys<K>(this);
			keys2 = new Keys<K>(this);
		}
		if (!keys1._valid) {
			keys1.reset();
			keys1._valid = true;
			keys2._valid = false;
			return keys1;
		}
		keys2.reset();
		keys2._valid = true;
		keys1._valid = false;
		return keys2;
	}

	public static final class Keys<K> implements Iterable<K>, LIterator<K> {

		public boolean _valid;
		boolean _simpleOrder;
		int _nextIndex;
		int _lastIndex;
		int _expectedModCount;
		ObjectMap<K, ?> _map;

		Keys(ObjectMap<K, ?> map) {
			this._map = map;
		}

		public void reset() {
			this._simpleOrder = !(_map instanceof OrderedMap<?, ?>);
			this._nextIndex = _map.iterateFirst();
			this._lastIndex = NO_INDEX;
			this._expectedModCount = _map.modCount;
		}

		@Override
		public final boolean hasNext() {
			if (!_valid) {
				return false;
			}
			return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
		}

		@SuppressWarnings("unchecked")
		public final K next() {
			if (!_valid) {
				return null;
			}
			if (_map.modCount != _expectedModCount) {
				return null;
			}
			if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex) {
				return null;
			}
			_lastIndex = _nextIndex;
			if (_simpleOrder) {
				do {
					_nextIndex++;
				} while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
						&& _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
			} else {
				_nextIndex = _map.iterateNext(_nextIndex);
			}

			return _lastIndex == NULL_INDEX ? null : (K) _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1];

		}

		@Override
		public final void remove() {
			if (!_valid) {
				return;
			}
			if (_lastIndex == NO_INDEX) {
				return;
			}
			if (_map.modCount != _expectedModCount) {
				return;
			}
			_map.removeKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
					_lastIndex);
			_lastIndex = NO_INDEX;
			_expectedModCount = _map.modCount;
		}

		@Override
		public Keys<K> iterator() {
			return this;
		}
	}

	private Entries<K, V> entries1, entries2;

	@Override
	public Entries<K, V> iterator() {
		return entries();
	}

	public Entries<K, V> entries() {
		if (entries1 == null) {
			entries1 = new Entries<K, V>(this);
			entries2 = new Entries<K, V>(this);
		}
		if (!entries1._valid) {
			entries1.reset();
			entries1._valid = true;
			entries2._valid = false;
			return entries1;
		}
		entries2.reset();
		entries2._valid = true;
		entries1._valid = false;
		return entries2;
	}

	public static class Entries<K, V> implements Iterable<Entry<K, V>>, LIterator<Entry<K, V>> {

		public boolean _valid;
		boolean _simpleOrder;
		int _nextIndex;
		int _lastIndex;
		int _expectedModCount;
		ObjectMap<K, V> _map;

		public Entries(ObjectMap<K, V> map) {
			this._map = map;
		}

		public void reset() {
			this._simpleOrder = !(_map instanceof OrderedMap<?, ?>);
			this._nextIndex = _map.iterateFirst();
			this._lastIndex = NO_INDEX;
			this._expectedModCount = _map.modCount;
		}

		@Override
		public final boolean hasNext() {
			if (!_valid) {
				return false;
			}
			return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
		}

		@Override
		public final Entry<K, V> next() {
			if (!_valid) {
				return null;
			}
			if (_map.modCount != _expectedModCount) {
				return null;
			}
			if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex) {
				return null;
			}
			_lastIndex = _nextIndex;
			if (_simpleOrder) {
				do {
					_nextIndex++;
				} while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
						&& _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
			} else {
				_nextIndex = _map.iterateNext(_nextIndex);
			}
			return new Entry<K, V>(_lastIndex, _map);
		}

		@Override
		public final void remove() {
			if (!_valid) {
				return;
			}
			if (_lastIndex == NO_INDEX) {
				return;
			}
			if (_map.modCount != _expectedModCount) {
				return;
			}
			_map.removeKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
					_lastIndex);
			_lastIndex = NO_INDEX;
			_expectedModCount = _map.modCount;
		}

		@Override
		public Entries<K, V> iterator() {
			return this;
		}
	}

	private static final int MAP_BITS = 0xC0000000;

	private static final int MAP_EMPTY = 0;

	private static final int MAP_NEXT = 0x40000000;

	private static final int MAP_OVERFLOW = 0x80000000;

	private static final int MAP_END = 0xC0000000;

	private static final int AVAILABLE_BITS = 0x3FFFFFFF;

	private static final Object EMPTY_OBJECT = new Object();

	protected static final Object FINAL_VALUE = new Object();

	public static class Entry<K, V> {
		final int index;
		public final K key;
		public V value;
		ObjectMap<K, V> map;

		@SuppressWarnings("unchecked")
		Entry(int index, ObjectMap<K, V> map) {
			this.map = map;
			this.index = index;
			this.key = index == NULL_INDEX ? null : (K) map.keyValueTable[(index << map.keyIndexShift) + 1];
			this.value = (V) (map.keyIndexShift == 0 ? FINAL_VALUE
					: map.keyValueTable[(index << map.keyIndexShift) + 2]);
		}

		public final K getKey() {
			return key;
		}

		@SuppressWarnings("unchecked")
		public final V getValue() {
			if (index == NULL_INDEX ? map.nullKeyPresent : map.keyValueTable[(index << 1) + 1] == key) {
				value = (V) map.keyValueTable[(index << 1) + 2];
			}
			return value;
		}

		public final V setValue(V newValue) {
			if (index == NULL_INDEX ? map.nullKeyPresent : map.keyValueTable[(index << 1) + 1] == key) {
				@SuppressWarnings("unchecked")
				V oldValue = (V) map.keyValueTable[(index << 1) + 2];
				map.keyValueTable[(index << 1) + 2] = value = newValue;
				return oldValue;
			}
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Entry<K, V> that = (Entry<K, V>) o;
			K key2 = that.getKey();
			if (key == key2 || (key != null && key.equals(key2))) {
				V value2 = that.getValue();
				return getValue() == value2 || (value != null && value.equals(value2));
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (getValue() == null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return key + "=" + getValue();
		}
	}

	private boolean nullKeyPresent;

	public int size = 0;

	protected Object[] keyValueTable;

	protected int keyIndexShift;

	protected int threshold;

	private int[] indexTable;

	private int firstUnusedIndex = 0;

	private int firstDeletedIndex = -1;

	private int capacity;

	private final float load_factor;

	protected int modCount;

	public ObjectMap() {
		this(true);
	}

	ObjectMap(boolean withValues) {
		this(CollectionUtils.INITIAL_CAPACITY, 0.85f, withValues);
	}

	public ObjectMap(int initialCapacity, float factor) {
		this(initialCapacity, 0.85f, true);
	}

	public ObjectMap(int initialCapacity) {
		this(initialCapacity, 0.85f, true);
	}

	ObjectMap(int initialCapacity, boolean withValues) {
		this(initialCapacity, 0.85f, withValues);
	}

	public ObjectMap(ObjectMap<? extends K, ? extends V> map) {
		this(MathUtils.max((int) (map.size() / 0.85f) + 1, CollectionUtils.INITIAL_CAPACITY), 0.85f);
		for (Entry<? extends K, ? extends V> e : map) {
			put(e.getKey(), e.getValue());
		}
	}

	ObjectMap(int initialCapacity, float factor, boolean withValues) {
		if (initialCapacity < 0) {
			throw LSystem.runThrow("initialCapacity must be >= 0: " + initialCapacity);
		}
		if (initialCapacity > 1 << 30) {
			throw LSystem.runThrow("initialCapacity is too large: " + initialCapacity);
		}
		this.capacity = MathUtils.nextPowerOfTwo(initialCapacity);
		if (factor <= 0) {
			throw LSystem.runThrow("loadFactor must be > 0: " + factor);
		}
		this.load_factor = MathUtils.min(factor, 1f);
		this.threshold = (int) (capacity * load_factor);
		if (threshold < 1) {
			throw LSystem.runThrow("illegal load factor: " + load_factor);
		}
		this.keyIndexShift = withValues ? 1 : 0;
		init();
	}

	void init() {
	}

	void resize(int newCapacity) {
		int newValueLen = (int) (newCapacity * load_factor);
		if (keyValueTable != null) {
			keyValueTable = CollectionUtils.copyOf(keyValueTable, (newValueLen << keyIndexShift) + 1);
		} else {
			keyValueTable = new Object[(newValueLen << keyIndexShift) + 1];
		}
		int[] newIndices = new int[newCapacity + newValueLen];
		if (indexTable != null) {
			int mask = AVAILABLE_BITS ^ (capacity - 1);
			int newMask = AVAILABLE_BITS ^ (newCapacity - 1);
			for (int i = capacity - 1; i >= 0; i--) {
				int j = indexTable[i];
				if ((j & MAP_BITS) == MAP_EMPTY) {
					continue;
				}
				if ((j & MAP_BITS) == MAP_NEXT) {
					int i2 = (i + 1) & (capacity - 1);
					int j2 = indexTable[i2];
					int arrayIndex1 = j & (capacity - 1);
					int arrayIndex2 = j2 & (capacity - 1);
					int newHashIndex1 = i | (j & (newMask ^ mask));
					int newHashIndex2 = i | (j2 & (newMask ^ mask));
					if (newHashIndex1 == newHashIndex2) {
						newIndices[newHashIndex1] = arrayIndex1 | (j & newMask) | MAP_NEXT;
						newIndices[(newHashIndex1 + 1) & (newCapacity - 1)] = arrayIndex2 | (j2 & newMask);
					} else {
						newIndices[newHashIndex1] = arrayIndex1 | (j & newMask) | MAP_END;
						newIndices[newHashIndex2] = arrayIndex2 | (j2 & newMask) | MAP_END;
					}
				} else {
					int next1i = -1, next1v = 0, next1n = 0;
					int next2i = -1, next2v = 0, next2n = 0;
					for (;;) {
						int arrayIndex = j & (capacity - 1);
						int newHashIndex = i | (j & (newMask ^ mask));
						if (newHashIndex == i) {
							if (next1i >= 0) {
								newIndices[next1i] = next1v | MAP_OVERFLOW;
								next1i = newCapacity + (next1v & (newCapacity - 1));
								next1n++;
							} else {
								next1i = newHashIndex;
							}
							next1v = arrayIndex | (j & newMask);
						} else if (newHashIndex == i + capacity) {
							if (next2i >= 0) {
								newIndices[next2i] = next2v | MAP_OVERFLOW;
								next2i = newCapacity + (next2v & (newCapacity - 1));
								next2n++;
							} else {
								next2i = newHashIndex;
							}
							next2v = arrayIndex | (j & newMask);
						} else {
							int newIndex = arrayIndex | (j & newMask);
							int oldIndex = newIndices[newHashIndex];
							if ((oldIndex & MAP_BITS) != MAP_EMPTY) {
								newIndices[newCapacity + arrayIndex] = oldIndex;
								newIndex |= MAP_OVERFLOW;
							} else {
								newIndex |= MAP_END;
							}
							newIndices[newHashIndex] = newIndex;
						}
						if ((j & MAP_BITS) == MAP_END) {
							break;
						}
						j = indexTable[capacity + arrayIndex];
					}
					if (next1i >= 0) {
						if (next1n == 1 && i != capacity - 1 && (next1v & (capacity - 1)) != 0
								&& newIndices[i + 1] == 0) {
							newIndices[i] ^= MAP_OVERFLOW ^ MAP_NEXT;
							newIndices[i + 1] = next1v;
						} else {
							newIndices[next1i] = next1v | MAP_END;
						}
					}
					if (next2i >= 0) {
						if (next2n == 1 && i != capacity - 1 && (next2v & (capacity - 1)) != 0
								&& newIndices[i + capacity + 1] == 0) {
							newIndices[i + capacity] ^= MAP_OVERFLOW ^ MAP_NEXT;
							newIndices[i + capacity + 1] = next2v;
						} else {
							newIndices[next2i] = next2v | MAP_END;
						}
					}
				}
			}
			for (int i = firstDeletedIndex; i >= 0; i = (newIndices[newCapacity + i] = indexTable[capacity + i])) {
				;
			}
		}
		capacity = newCapacity;
		threshold = newValueLen;
		indexTable = newIndices;
	}

	static final int NULL_INDEX = -1;

	static final int NO_INDEX = -2;

	final int positionOf(Object key) {
		if (key == null) {
			return nullKeyPresent ? NULL_INDEX : NO_INDEX;
		}
		if (indexTable == null) {
			return NO_INDEX;
		}
		int hc = CollectionUtils.getLimitHash(key.hashCode());
		int index = indexTable[hc & (capacity - 1)];
		int MAP = index & MAP_BITS;
		if (MAP == MAP_EMPTY) {
			return NO_INDEX;
		}
		int mask = AVAILABLE_BITS ^ (capacity - 1);
		for (;;) {
			int position = index & (capacity - 1);
			if ((index & mask) == (hc & mask)) {
				Object key1 = keyValueTable[(position << keyIndexShift) + 1];
				if (key == key1 || key.equals(key1)) {
					return position;
				}
			}
			if (MAP == MAP_END) {
				return NO_INDEX;
			} else if (MAP == MAP_OVERFLOW) {
				index = indexTable[capacity + position];
			} else if (MAP == MAP_NEXT) {
				index = indexTable[(hc + 1) & (capacity - 1)];
			} else {
				return NO_INDEX;
			}
			MAP = index & MAP_BITS;
		}
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		if (key == null) {
			return nullKeyPresent ? (V) keyValueTable[0] : null;
		}
		if (indexTable == null) {
			return null;
		}
		int hc = CollectionUtils.getLimitHash(key.hashCode());
		int index = indexTable[hc & (capacity - 1)];
		int MAP = index & MAP_BITS;
		if (MAP == MAP_EMPTY) {
			return null;

		}
		int mask = AVAILABLE_BITS ^ (capacity - 1);
		for (;;) {
			int position = index & (capacity - 1);
			if ((index & mask) == (hc & mask)) {
				Object key1 = keyValueTable[(position << 1) + 1];
				if (key == key1 || key.equals(key1)) {
					return (V) keyValueTable[(position << 1) + 2];
				}
			}
			if (MAP == MAP_END) {
				return null;
			} else if (MAP == MAP_OVERFLOW) {
				index = indexTable[capacity + position];
			} else if (MAP == MAP_NEXT) {
				index = indexTable[(hc + 1) & (capacity - 1)];
			} else {
				return null;
			}
			MAP = index & MAP_BITS;
		}
	}

	final boolean isEmpty(int i) {
		return i == NULL_INDEX ? !nullKeyPresent
				: firstDeletedIndex >= 0 && keyValueTable[(i << keyIndexShift) + 1] == null;
	}

	public V put(K key, V value) {
		if (key == null) {
			return null;
		}
		return put(key, value, true);
	}

	@SuppressWarnings("unchecked")
	final V put(K key, V value, boolean searchForExistingKey) {
		boolean callback = this instanceof OrderedMap;
		if (key == null) {
			Object oldValue;
			if (keyIndexShift > 0) {
				if (keyValueTable == null) {
					keyValueTable = new Object[(threshold << keyIndexShift) + 1];
				}
				oldValue = keyValueTable[0];
				keyValueTable[0] = value;
			} else
				oldValue = nullKeyPresent ? FINAL_VALUE : null;
			if (nullKeyPresent) {
				if (callback) {
					updateBind(NULL_INDEX);
				}
			} else {
				nullKeyPresent = true;
				size++;
				if (callback) {
					addBind(NULL_INDEX);
				}
			}
			return (V) oldValue;
		}
		int hc = CollectionUtils.getLimitHash(key.hashCode());
		int i = hc & (capacity - 1);
		int head;
		if (indexTable != null) {
			head = indexTable[i];
		} else {
			head = 0;
			indexTable = new int[capacity + threshold];
			if (keyValueTable == null) {
				keyValueTable = new Object[(threshold << keyIndexShift) + 1];
			}
		}
		int depth = 1;
		int mask = AVAILABLE_BITS ^ (capacity - 1);
		int MAP = head & MAP_BITS;
		if (MAP != MAP_EMPTY && searchForExistingKey) {
			int index = head;
			for (;;) {
				int cur = index & (capacity - 1);
				if ((index & mask) == (hc & mask)) {
					Object key1 = keyValueTable[(cur << keyIndexShift) + 1];
					if (key == key1 || key.equals(key1)) {
						Object oldValue;
						if (keyIndexShift > 0) {
							oldValue = keyValueTable[(cur << keyIndexShift) + 2];
							keyValueTable[(cur << keyIndexShift) + 2] = value;
						} else {
							oldValue = FINAL_VALUE;
						}
						if (callback) {
							updateBind(cur);
						}
						return (V) oldValue;
					}
				}
				depth++;
				if ((index & MAP_BITS) == MAP_END) {
					break;
				} else if ((index & MAP_BITS) == MAP_OVERFLOW) {
					index = indexTable[capacity + cur];
				} else if ((index & MAP_BITS) == MAP_NEXT) {
					index = indexTable[(i + 1) & (capacity - 1)];
				} else {
					break;
				}
			}
		}
		boolean defragment = depth > 2 && firstUnusedIndex + depth <= threshold;
		if (size >= threshold) {
			resize(capacity << 1);
			i = hc & (capacity - 1);
			mask = AVAILABLE_BITS ^ (capacity - 1);
			head = indexTable[i];
			MAP = head & MAP_BITS;
			defragment = false;
		}
		if (MAP == MAP_EMPTY && head != 0) {
			int i2 = (hc - 1) & (capacity - 1);
			int head2 = indexTable[i2];
			int j2 = head2 & (capacity - 1);
			indexTable[i2] = (head2 & AVAILABLE_BITS) | MAP_OVERFLOW;
			indexTable[capacity + j2] = head | MAP_END;
			head = 0;
		}
		int newIndex;
		if (firstDeletedIndex >= 0 && !defragment) {
			newIndex = firstDeletedIndex;
			firstDeletedIndex = indexTable[capacity + firstDeletedIndex];
			modCount++;
		} else {
			newIndex = firstUnusedIndex;
			firstUnusedIndex++;
		}
		if (defragment) {
			int j = head;
			head = (j & ~(capacity - 1)) | firstUnusedIndex;
			for (;;) {
				int k = j & (capacity - 1);
				Object tmp = keyValueTable[(k << keyIndexShift) + 1];
				keyValueTable[(firstUnusedIndex << keyIndexShift) + 1] = tmp;
				keyValueTable[(k << keyIndexShift) + 1] = null;
				if (keyIndexShift > 0) {
					tmp = keyValueTable[(k << keyIndexShift) + 2];
					keyValueTable[(firstUnusedIndex << keyIndexShift) + 2] = tmp;
					keyValueTable[(k << keyIndexShift) + 2] = null;
				}
				int _nextIndex, n;
				if ((j & MAP_BITS) == MAP_END) {
					_nextIndex = -1;
					n = 0;
				} else if ((j & MAP_BITS) == MAP_OVERFLOW) {
					_nextIndex = capacity + k;
					n = indexTable[_nextIndex];
				} else if ((j & MAP_BITS) == MAP_NEXT) {
					_nextIndex = (i + 1) & (capacity - 1);
					n = indexTable[_nextIndex] | MAP_END;
					indexTable[_nextIndex] = 0;
					head = (head & AVAILABLE_BITS) | MAP_OVERFLOW;
					MAP = MAP_OVERFLOW;
				} else {
					_nextIndex = -1;
					n = 0;
				}
				indexTable[capacity + k] = firstDeletedIndex;
				firstDeletedIndex = k;
				if (callback) {
					relocateBind(firstUnusedIndex, k);
				}
				firstUnusedIndex++;
				if (_nextIndex < 0) {
					break;
				}
				j = n;
				indexTable[capacity + firstUnusedIndex - 1] = (j & ~(capacity - 1)) | firstUnusedIndex;
			}
		}
		keyValueTable[(newIndex << keyIndexShift) + 1] = key;
		if (keyIndexShift > 0) {
			keyValueTable[(newIndex << keyIndexShift) + 2] = value;
		}
		if (MAP == MAP_EMPTY) {
			indexTable[i] = newIndex | (hc & mask) | MAP_END;
		} else if (MAP == MAP_END && newIndex != 0 && indexTable[(i + 1) & (capacity - 1)] == 0) {
			indexTable[i] = (head & AVAILABLE_BITS) | MAP_NEXT;
			indexTable[(i + 1) & (capacity - 1)] = newIndex | (hc & mask);
		} else if (MAP == MAP_NEXT) {
			int i2 = (i + 1) & (capacity - 1);
			int head2 = indexTable[i2];
			indexTable[i2] = 0;
			indexTable[capacity + (head & (capacity - 1))] = head2 | MAP_END;
			indexTable[capacity + newIndex] = (head & AVAILABLE_BITS) | MAP_OVERFLOW;
			indexTable[i] = newIndex | (hc & mask) | MAP_OVERFLOW;
		} else {
			indexTable[capacity + newIndex] = head;
			indexTable[i] = newIndex | (hc & mask) | MAP_OVERFLOW;
		}
		size++;
		modCount++;
		if (callback) {
			addBind(newIndex);
		}
		return null;
	}

	public V remove(Object key) {
		if (key == null) {
			return null;
		}
		V result = removeKey(key, NO_INDEX);
		return result == EMPTY_OBJECT ? null : result;
	}

	@SuppressWarnings("unchecked")
	final V removeKey(Object key, int index) {
		if (key == null) {
			if (nullKeyPresent) {
				nullKeyPresent = false;
				size--;
				if (this instanceof OrderedMap) {
					removeBind(NULL_INDEX);
				}
				if (keyIndexShift > 0) {
					V oldValue = (V) keyValueTable[0];
					keyValueTable[0] = null;
					return oldValue;
				} else {
					return (V) FINAL_VALUE;
				}
			} else {
				return (V) EMPTY_OBJECT;
			}
		}
		if (indexTable == null) {
			return (V) EMPTY_OBJECT;
		}
		int hc = CollectionUtils.getLimitHash(key.hashCode());
		int prev = -1;
		int curr = hc & (capacity - 1);
		int i = indexTable[curr];
		if ((i & MAP_BITS) == MAP_EMPTY) {
			return (V) EMPTY_OBJECT;
		}
		int mask = AVAILABLE_BITS ^ (capacity - 1);
		for (;;) {
			int j = i & (capacity - 1);
			int k = capacity + j;
			if ((hc & mask) == (i & mask)) {
				boolean found;
				if (index == NO_INDEX) {
					Object o = keyValueTable[(j << keyIndexShift) + 1];
					found = key == o || key.equals(o);
				} else {
					found = j == index;
				}
				if (found) {
					size--;
					if ((i & MAP_BITS) == MAP_END) {
						if (prev >= 0)
							indexTable[prev] |= MAP_END;
						else {
							indexTable[curr] = 0;
						}
					} else if ((i & MAP_BITS) == MAP_OVERFLOW) {
						indexTable[curr] = indexTable[k];
					} else if ((i & MAP_BITS) == MAP_NEXT) {
						int c2 = (curr + 1) & (capacity - 1);
						int i2 = indexTable[c2];
						indexTable[curr] = i2 | MAP_END;
						indexTable[c2] = 0;
					} else {
						indexTable[prev] |= MAP_END;
						indexTable[curr] = 0;
					}
					if (size == 0) {
						firstUnusedIndex = 0;
						firstDeletedIndex = -1;
					} else if (j == firstUnusedIndex - 1) {
						firstUnusedIndex = j;
					} else {
						indexTable[k] = firstDeletedIndex;
						firstDeletedIndex = j;
					}
					Object oldValue = index != NO_INDEX ? null
							: keyIndexShift == 0 ? FINAL_VALUE : keyValueTable[(j << keyIndexShift) + 2];
					keyValueTable[(j << keyIndexShift) + 1] = null;
					if (keyIndexShift > 0) {
						keyValueTable[(j << keyIndexShift) + 2] = null;
					}
					modCount++;
					if (this instanceof OrderedMap) {
						removeBind(j);
					}
					return (V) oldValue;
				}
			}
			prev = curr;
			if ((i & MAP_BITS) == MAP_END) {
				break;
			} else if ((i & MAP_BITS) == MAP_OVERFLOW) {
				curr = k;
			} else if ((i & MAP_BITS) == MAP_NEXT) {
				curr = (curr + 1) & (capacity - 1);
			} else {
				break;
			}
			i = indexTable[curr];
		}
		return (V) EMPTY_OBJECT;
	}

	@Override
	public void clear() {
		if (indexTable != null) {
			CollectionUtils.fill(indexTable, 0, capacity + firstUnusedIndex, 0);
		}
		if (keyValueTable != null) {
			CollectionUtils.fill(keyValueTable, 0, (firstUnusedIndex << keyIndexShift) + 1, null);
		}
		size = 0;
		firstUnusedIndex = 0;
		firstDeletedIndex = -1;
		modCount++;
		nullKeyPresent = false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsKey(Object key) {
		return positionOf(key) != NO_INDEX;
	}

	public void putAll(ObjectMap<? extends K, ? extends V> m) {
		int mSize = m.size();
		if (mSize == 0) {
			return;
		}
		if (mSize > threshold) {
			int newCapacity = capacity;
			int newThreshold;
			do {
				newCapacity <<= 1;
				newThreshold = (int) (newCapacity * load_factor);
			} while (newThreshold < mSize);
			resize(newCapacity);
		}
		if (m instanceof ObjectMap<?, ?>) {
			@SuppressWarnings("unchecked")
			ObjectMap<K, V> fm = (ObjectMap<K, V>) m;
			for (int i = fm.iterateFirst(); i != NO_INDEX; i = fm.iterateNext(i)) {
				@SuppressWarnings("unchecked")
				K key = (K) fm.keyValueTable[(i << fm.keyIndexShift) + 1];
				@SuppressWarnings("unchecked")
				V value = (V) (fm.keyIndexShift > 0 ? fm.keyValueTable[(i << fm.keyIndexShift) + 2] : FINAL_VALUE);
				put(key, value);
			}
		} else {
			for (Entry<? extends K, ? extends V> e : m) {
				put(e.getKey(), e.getValue());
			}
		}
	}

	public boolean containsValue(Object value) {
		if (keyValueTable == null || size == 0) {
			return false;
		}
		if (keyIndexShift == 0) {
			return size > 0 && value == FINAL_VALUE;
		}
		for (int i = NULL_INDEX; i < firstUnusedIndex; i++) {
			if (!isEmpty(i)) {
				Object o = keyValueTable[(i << keyIndexShift) + 2];
				if (o == value || o != null && o.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	int iterateFirst() {
		if (size == 0) {
			return NO_INDEX;
		}
		if (nullKeyPresent) {
			return NULL_INDEX;
		}
		int i = 0;
		while (isEmpty(i)) {
			i++;
		}
		return i;
	}

	int iterateNext(int i) {
		do {
			i++;
		} while (i < firstUnusedIndex && isEmpty(i));
		return i < firstUnusedIndex ? i : NO_INDEX;
	}

	int capacity() {
		return capacity;
	}

	float load_factor() {
		return load_factor;
	}

	void addBind(int i) {
	}

	void updateBind(int i) {
	}

	void removeBind(int i) {
	}

	void relocateBind(int newIndex, int oldIndex) {
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = NULL_INDEX; i < firstUnusedIndex; i++)
			if (!isEmpty(i)) {
				int hc = i == NULL_INDEX ? 0 : keyValueTable[(i << keyIndexShift) + 1].hashCode();
				Object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
				if (value != null) {
					hc ^= value.hashCode();
				}
				hashCode += hc;
			}
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ObjectMap)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		ObjectMap<K, V> m = (ObjectMap<K, V>) o;
		if (m.size() != size) {
			return false;
		}
		for (int i = NULL_INDEX; i < firstUnusedIndex; i++)
			if (!isEmpty(i)) {
				Object key = i == NULL_INDEX ? null : keyValueTable[(i << keyIndexShift) + 1];
				Object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
				if (value == null) {
					if (!(m.get(key) == null && m.containsKey(key))) {
						return false;
					}
				} else {
					Object value2 = m.get(key);
					if (value != value2 && !value.equals(value2)) {
						return false;
					}
				}
			}
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		boolean first = true;
		for (int i = iterateFirst(); i != NO_INDEX; i = iterateNext(i)) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			Object key = i == NULL_INDEX ? null : keyValueTable[(i << keyIndexShift) + 1];
			Object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
		}
		return sb.append(']').toString();
	}

}