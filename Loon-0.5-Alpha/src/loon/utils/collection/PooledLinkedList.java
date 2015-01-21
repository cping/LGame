package loon.utils.collection;

public class PooledLinkedList<T> {
	static final class Item<T> {
		public T payload;
		public Item<T> next;
		public Item<T> prev;
	}

	private Item<T> head;
	private Item<T> tail;
	private Item<T> iter;
	private Item<T> curr;
	private int size = 0;

	private final Pool<Item<T>> pool;

	public PooledLinkedList(int maxPoolSize) {
		this.pool = new Pool<Item<T>>(16, maxPoolSize) {
			protected Item<T> newObject() {
				return new Item<T>();
			}
		};
	}

	public void add(T object) {
		Item<T> item = pool.obtain();
		item.payload = object;
		item.next = null;
		item.prev = null;

		if (head == null) {
			head = item;
			tail = item;
			size++;
			return;
		}

		item.prev = tail;
		tail.next = item;
		tail = item;
		size++;
	}

	public void iter() {
		iter = head;
	}

	public T next() {
		if (iter == null)
			return null;

		T payload = iter.payload;
		curr = iter;
		iter = iter.next;
		return payload;
	}

	public void remove() {
		if (curr == null)
			return;

		size--;
		pool.free(curr);

		Item<T> c = curr;
		Item<T> n = curr.next;
		Item<T> p = curr.prev;
		curr = null;

		if (size == 0) {
			head = null;
			tail = null;
			return;
		}

		if (c == head) {
			n.prev = null;
			head = n;
			return;
		}

		if (c == tail) {
			p.next = null;
			tail = p;
			return;
		}

		p.next = n;
		n.prev = p;
	}

	public void clear() {
		iter();
		while ((next()) != null){
			remove();
		}
	}
}
