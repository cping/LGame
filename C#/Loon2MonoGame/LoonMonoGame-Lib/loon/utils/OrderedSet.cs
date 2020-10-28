namespace loon.utils
{
    public class OrderedSet<E> : ObjectSet<E>
    {

        public OrderedSet(int initialCapacity, float loadFactor) : base(initialCapacity, loadFactor, true)
        {

        }

        public OrderedSet(int initialCapacity) : base(initialCapacity, 0.85f, true)
        {

        }

        public OrderedSet() : base(CollectionUtils.INITIAL_CAPACITY, 0.85f, true)
        {
        }

        public OrderedSet(OrderedSet<E> c) : base(MathUtils.Max((int)(c.Size() / 0.85f) + 1, CollectionUtils.INITIAL_CAPACITY),
                    0.85f, true)
        {

            AddAll(c);
        }


    }
}
