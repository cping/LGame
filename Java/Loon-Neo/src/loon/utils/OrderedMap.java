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

/**
 * key-value形式的数据集合,有序排列,作用近似于HashMap
 * 
 * @param <K>
 * @param <V>
 */
public class OrderedMap<K, V> extends ObjectMap<K, V> {

	protected final boolean ordered;

	protected int[] prevNext;

	protected int headIndex;

	protected Entry<K, V> headEntry;

	public OrderedMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		ordered = false;
	}

	public OrderedMap(int initialCapacity) {
		super(initialCapacity);
		ordered = false;
	}

	public OrderedMap() {
		ordered = false;
	}

	public OrderedMap(ObjectMap<? extends K, ? extends V> m) {
		super(m);
		ordered = false;
	}

	public OrderedMap(int initialCapacity, float loadFactor, boolean ordered) {
		super(initialCapacity, loadFactor);
		this.ordered = ordered;
	}

	OrderedMap(int initialCapacity, float loadFactor, boolean ordered, boolean withValues) {
		super(initialCapacity, loadFactor, withValues);
		this.ordered = ordered;
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		int i = positionOf(key);
		if (i == NO_INDEX) {
			return null;
		}
		updateIndex(i);
		return (V) (keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE);
	}

	public void clear() {
		super.clear();
		headIndex = NO_INDEX;
		headEntry = null;
	}

	@Override
	void resize(int newCapacity) {
		super.resize(newCapacity);
		if (prevNext != null) {
			prevNext = CollectionUtils.copyOf(prevNext, (threshold + 1) << 1);
		} else if (threshold > 0) {
			prevNext = new int[(threshold + 1) << 1];
		}
	}

	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return false;
	}

	@Override
	void init() {
		if (threshold > 0) {
			prevNext = new int[(threshold + 1) << 1];
		}
		headIndex = NO_INDEX;
		headEntry = null;
	}

	@Override
	void addBind(int i) {
		insertIndex(i);
		if (headEntry == null) {
			headEntry = new Entry<K, V>(headIndex, this);
		}
		if (removeEldestEntry(headEntry)) {
			removeKey(headEntry.key, headIndex);
		}
	}

	@Override
	void removeBind(int i) {
		removeIndex(i);
	}

	@SuppressWarnings("unchecked")
	void updateBind(int i) {
		updateIndex(i);
		if (headEntry != null && headIndex == i && keyIndexShift > 0)
			headEntry.value = (V) keyValueTable[(i << keyIndexShift) + 2];
	}

	@Override
	void relocateBind(int newIndex, int oldIndex) {
		if (size == 1) {
			prevNext[(newIndex << 1) + 2] = prevNext[(newIndex << 1) + 3] = newIndex;
		} else {
			int prev = prevNext[(oldIndex << 1) + 2];
			int next = prevNext[(oldIndex << 1) + 3];
			prevNext[(newIndex << 1) + 2] = prev;
			prevNext[(newIndex << 1) + 3] = next;
			prevNext[(prev << 1) + 3] = prevNext[(next << 1) + 2] = newIndex;
		}
		if (headIndex == oldIndex) {
			headIndex = newIndex;
			headEntry = null;
		}
	}

	final void insertIndex(int i) {
		if (headIndex == NO_INDEX) {
			prevNext[(i << 1) + 2] = prevNext[(i << 1) + 3] = headIndex = i;
		} else {
			int last = prevNext[(headIndex << 1) + 2];
			prevNext[(i << 1) + 2] = last;
			prevNext[(i << 1) + 3] = headIndex;
			prevNext[(headIndex << 1) + 2] = prevNext[(last << 1) + 3] = i;
		}
	}

	final void updateIndex(int i) {
		if (ordered) {
			removeIndex(i);
			insertIndex(i);
			modCount++;
		}
	}

	final void removeIndex(int i) {
		if (size == 0) {
			headIndex = NO_INDEX;
			headEntry = null;
		} else {
			int prev = prevNext[(i << 1) + 2];
			int next = prevNext[(i << 1) + 3];
			prevNext[(next << 1) + 2] = prev;
			prevNext[(prev << 1) + 3] = next;
			if (headIndex == i) {
				headIndex = next;
				headEntry = null;
			}
		}
	}

	@Override
	final int iterateFirst() {
		return headIndex;
	}

	@Override
	final int iterateNext(int i) {
		i = prevNext[(i << 1) + 3];
		return i == headIndex ? NO_INDEX : i;
	}

}
