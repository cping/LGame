package loon.utils;

import java.util.NoSuchElementException;

import loon.LSystem;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OrderedSet<T> extends ObjectSet<T> implements IArray {
	
	final TArray<T> items;
	OrderedSetIterator iterator1, iterator2;

	public OrderedSet() {
		items = new TArray();
	}

	public OrderedSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		items = new TArray(capacity);
	}

	public OrderedSet(int initialCapacity) {
		super(initialCapacity);
		items = new TArray(capacity);
	}

	public OrderedSet(OrderedSet set) {
		super(set);
		items = new TArray(capacity);
		items.addAll(set.items);
	}

	public boolean add(T key) {
		if (!contains(key))
			items.add(key);
		return super.add(key);
	}

	public boolean remove(T key) {
		items.removeValue(key, false);
		return super.remove(key);
	}

	public void clear(int maximumCapacity) {
		items.clear();
		super.clear(maximumCapacity);
	}

	public void clear() {
		items.clear();
		super.clear();
	}

	public TArray<T> orderedItems() {
		return items;
	}

	public OrderedSetIterator<T> iterator() {
		if (iterator1 == null) {
			iterator1 = new OrderedSetIterator(this);
			iterator2 = new OrderedSetIterator(this);
		}
		if (!iterator1.valid) {
			iterator1.reset();
			iterator1.valid = true;
			iterator2.valid = false;
			return iterator1;
		}
		iterator2.reset();
		iterator2.valid = true;
		iterator1.valid = false;
		return iterator2;
	}

	public String toString() {
		if (size == 0)
			return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		TArray<T> keys = this.items;
		for (int i = 0, n = keys.size; i < n; i++) {
			T key = keys.get(i);
			if (i > 0)
				buffer.append(", ");
			buffer.append(key);
		}
		buffer.append('}');
		return buffer.toString();
	}

	static public class OrderedSetIterator<T> extends ObjectSetIterator<T> {
		private TArray<T> items;

		public OrderedSetIterator(OrderedSet<T> set) {
			super(set);
			items = set.items;
		}

		public void reset() {
			nextIndex = 0;
			hasNext = set.size > 0;
		}

		public T next() {
			if (!hasNext)
				throw new NoSuchElementException();
			if (!valid)
				throw LSystem.runThrow("#iterator() cannot be used nested.");
			T key = items.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < set.size;
			return key;
		}

		public void remove() {
			if (nextIndex < 0)
				throw new IllegalStateException(
						"next must be called before remove.");
			nextIndex--;
			set.remove(items.get(nextIndex));
		}
	}
}
