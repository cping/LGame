package loon.utils;

/**
 * 存储单独value的线性数据集合,有序排列,不允许重复
 *
 * @param <E>
 */
public class OrderedSet<E> extends ObjectSet<E> {

	public OrderedSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, true);
	}

	public OrderedSet(int initialCapacity) {
		super(initialCapacity, 0.85f, true);
	}

	public OrderedSet() {
		super(CollectionUtils.INITIAL_CAPACITY, 0.85f, true);
	}

	public OrderedSet(OrderedSet<? extends E> c) {
		super(MathUtils.max((int) (c.size() / 0.85f) + 1, CollectionUtils.INITIAL_CAPACITY),
				0.85f, true);
		addAll(c);
	}
}
