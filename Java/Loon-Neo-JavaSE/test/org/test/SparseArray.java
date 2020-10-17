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
package org.test;

import java.util.Iterator;

import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public final class SparseArray<T> {

	private float loader_factor = 0.85f;

	private boolean locked;
	private int size;
	private int capacity;
	private long[] keyHashes;
	private T[] values;

	public SparseArray() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public SparseArray(final int capacity) {
		this(MathUtils.nextPowerOfTwo(capacity), 0.85f);
	}

	public SparseArray(final int capacity, final float factor) {
		this.loader_factor = factor;
		this.resize(MathUtils.nextPowerOfTwo(capacity));
	}

	public SparseArray(final SparseArray<T> data, final float factor) {
		this.loader_factor = factor;
		int neededCapacity = MathUtils.nextPowerOfTwo(data.size);
		if ((float) data.size / neededCapacity > loader_factor) {
			neededCapacity *= 2;
		}
		this.size = data.size;
		this.keyHashes = data.keyHashes;
		this.values = data.values;
		resize(neededCapacity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SparseArray)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final SparseArray<T> other = (SparseArray<T>) obj;
		if (other.size != size) {
			return false;
		}
		int found = 0;
		for (int i = 0; found < size; ++i) {
			final long kh = keyHashes[i];
			if ((int) kh != 0) {
				final int j = other.find(kh);
				if (j < 0 || !values[i].equals(other.values[j])) {
					return false;
				}
				++found;
			}
		}
		return true;
	}

	public int[] keys() {
		final int[] keys = new int[size];
		int found = 0;
		for (int i = 0; found < size; ++i) {
			final long kh = keyHashes[i];
			if ((int) kh != 0) {
				keys[found++] = (int) (kh >> 32);
			}
		}
		return keys;
	}

	public Iterable<T> values() {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					int index = 0;
					int found = 0;

					@Override
					public boolean hasNext() {
						return found < size;
					}

					@Override
					public T next() {
						for (; index < capacity; ++index) {
							final T value = values[index];
							if (value != null) {
								++index;
								++found;
								return value;
							}
						}
						return null;
					}

					@Override
					public void remove() {
					}
				};
			}
		};
	}

	public int size() {
		return size;
	}

	public int capacity() {
		return capacity;
	}

	public boolean containsKey(final Object key) {
		if (key == null) {
			return false;
		}
		return containsKey(key.hashCode());
	}

	public boolean containsKey(final int key) {
		return find(getHashKey(key)) >= 0;
	}

	public T get(final Object key) {
		if (key == null) {
			return null;
		}
		return get(key.hashCode(), null);
	}

	public T get(final int key) {
		return get(key, null);
	}

	public T get(final int key, final T defaultValue) {
		final int index = find(getHashKey(key));
		if (index >= 0) {
			return values[index];
		}
		return defaultValue;
	}

	public void clear() {
		if (locked) {
			return;
		}
		for (int i = 0; i < capacity; ++i) {
			keyHashes[i] = 0;
			values[i] = null;
		}
		size = 0;
	}

	public void lockArray() {
		locked = true;
	}

	public void unlockArray() {
		locked = false;
	}

	public void put(final Object key, final T value) {
		if (key == null) {
			return;
		}
		put(key.hashCode(), value);
	}

	public void put(final int key, final T value) {
		if (locked) {
			return;
		}
		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		if ((float) size / capacity > loader_factor) {
			resize(capacity * 2);
		}
		put(getHashKey(key), value);
	}

	public boolean remove(final int key) {
		if (locked) {
			return false;
		}
		final int index = find(getHashKey(key));
		if (index < 0) {
			return false;
		}
		for (int i = 0; i < capacity; ++i) {
			final int curr = (index + i) & (capacity - 1);
			final int next = (index + i + 1) & (capacity - 1);

			final int h = (int) keyHashes[next];
			if (h == 0 || findIndex(h, next) == 0) {
				keyHashes[curr] = 0;
				values[curr] = null;
				--size;
				return true;
			}
			keyHashes[curr] = keyHashes[next];
			values[curr] = values[next];
		}
		return false;
	}

	private void put(long keyHash, T value) {
		final int startIndex = (int) keyHash & (capacity - 1);
		int probe = 0;
		for (int i = 0; i < capacity; ++i, ++probe) {
			final int index = (startIndex + i) & (capacity - 1);
			final long kh = keyHashes[index];
			final int h = (int) kh;
			if (h == 0) {
				keyHashes[index] = keyHash;
				values[index] = value;
				++size;
				return;
			}
			if (kh == keyHash) {
				values[index] = value;
				return;
			}
			final int d = findIndex(h, index);
			if (probe > d) {
				probe = d;
				long tempHK = keyHashes[index];
				T tempVal = values[index];
				keyHashes[index] = keyHash;
				values[index] = value;
				keyHash = tempHK;
				value = tempVal;
			}
		}
	}

	private int find(final long keyHash) {
		final int startIndex = (int) keyHash & (capacity - 1);
		for (int i = 0; i < capacity; ++i) {
			final int index = (startIndex + i) & (capacity - 1);
			final long kh = keyHashes[index];
			if (kh == keyHash) {
				return index;
			}
			final int h = (int) kh;
			if (h == 0) {
				return -1;
			}
			int d = findIndex(h, index);
			if (i > d) {
				return -1;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private void resize(int newCapacity) {
		if (newCapacity < size) {
			return;
		}

		final int oldSize = size;
		final long[] oldHashKeys = keyHashes;
		final T[] oldValues = values;

		size = 0;
		capacity = newCapacity;
		keyHashes = new long[newCapacity];
		values = (T[]) new Object[newCapacity];

		int found = 0;
		for (int i = 0; found < oldSize; ++i) {
			final long kh = oldHashKeys[i];
			if ((int) kh != 0) {
				put(kh, oldValues[i]);
				++found;
			}
		}
	}

	private int findIndex(int hash, int indexStored) {
		final int startIndex = hash & (capacity - 1);
		if (startIndex <= indexStored) {
			return indexStored - startIndex;
		}
		return indexStored + (capacity - startIndex);
	}

	private int getSmear(int hashCode) {
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
	}
	
	private long getHashKey(int key) {
		int hash = getSmear(key);
		if (hash == 0) {
			hash = 1;
		}
		return ((long) key << 32) | (hash & 0xFFFFFFFFL);
	}


}
