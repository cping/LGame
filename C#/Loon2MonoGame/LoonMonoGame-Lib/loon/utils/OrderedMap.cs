namespace loon.utils
{
    public class OrderedMap<K, V> : ObjectMap<K, V>
    {

        protected readonly bool ordered;

        protected int[] prevNext;

        protected int headIndex;

        protected Entry<K, V> headEntry;

        public OrderedMap(int initialCapacity, float loadFactor) : base(initialCapacity, loadFactor)
        {
            ordered = false;
        }

        public OrderedMap(int initialCapacity) : base(initialCapacity)
        {
            ordered = false;
        }

        public OrderedMap() : base()
        {
            ordered = false;
        }
        public OrderedMap(ObjectMap<K, V> m) : base(m)
        {
            ordered = false;
        }

        public OrderedMap(int initialCapacity, float loadFactor, bool ordered) : base(initialCapacity, loadFactor)
        {

            this.ordered = ordered;
        }

        internal OrderedMap(int initialCapacity, float loadFactor, bool ordered, bool withValues) : base(initialCapacity, loadFactor, withValues)
        {
            this.ordered = ordered;
        }

        public override V Get(object key)
        {
            int i = PositionOf(key);
            if (i == NO_INDEX)
            {
                return default;
            }
            UpdateIndex(i);
            return (V)(keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE);
        }

        public override void Clear()
        {
            base.Clear();
            headIndex = NO_INDEX;
            headEntry = null;
        }

        internal override void Resize(int newCapacity)
        {
            base.Resize(newCapacity);
            if (prevNext != null)
            {
                prevNext = CollectionUtils.CopyOf(prevNext, (threshold + 1) << 1);
            }
            else if (threshold > 0)
            {
                prevNext = new int[(threshold + 1) << 1];
            }
        }

        protected bool RemoveEldestEntry(Entry<K, V> eldest)
        {
            return false;
        }

        internal override void Init()
        {
            if (threshold > 0)
            {
                prevNext = new int[(threshold + 1) << 1];
            }
            headIndex = NO_INDEX;
            headEntry = null;
        }

        internal override void AddBind(int i)
        {
            InsertIndex(i);
            if (headEntry == null)
            {
                headEntry = new Entry<K, V>(headIndex, this);
            }
            if (RemoveEldestEntry(headEntry))
            {
                RemoveKey(headEntry.key, headIndex);
            }
        }

        internal override void RemoveBind(int i)
        {
            RemoveIndex(i);
        }

        internal override void UpdateBind(int i)
        {
            UpdateIndex(i);
            if (headEntry != null && headIndex == i && keyIndexShift > 0)
                headEntry.value = (V)keyValueTable[(i << keyIndexShift) + 2];
        }

        internal override void RelocateBind(int newIndex, int oldIndex)
        {
            if (size == 1)
            {
                prevNext[(newIndex << 1) + 2] = prevNext[(newIndex << 1) + 3] = newIndex;
            }
            else
            {
                int prev = prevNext[(oldIndex << 1) + 2];
                int next = prevNext[(oldIndex << 1) + 3];
                prevNext[(newIndex << 1) + 2] = prev;
                prevNext[(newIndex << 1) + 3] = next;
                prevNext[(prev << 1) + 3] = prevNext[(next << 1) + 2] = newIndex;
            }
            if (headIndex == oldIndex)
            {
                headIndex = newIndex;
                headEntry = null;
            }
        }

        internal virtual void InsertIndex(int i)
        {
            if (headIndex == NO_INDEX)
            {
                prevNext[(i << 1) + 2] = prevNext[(i << 1) + 3] = headIndex = i;
            }
            else
            {
                int last = prevNext[(headIndex << 1) + 2];
                prevNext[(i << 1) + 2] = last;
                prevNext[(i << 1) + 3] = headIndex;
                prevNext[(headIndex << 1) + 2] = prevNext[(last << 1) + 3] = i;
            }
        }

        internal virtual void UpdateIndex(int i)
        {
            if (ordered)
            {
                RemoveIndex(i);
                InsertIndex(i);
                modCount++;
            }
        }

        internal virtual void RemoveIndex(int i)
        {
            if (size == 0)
            {
                headIndex = NO_INDEX;
                headEntry = null;
            }
            else
            {
                int prev = prevNext[(i << 1) + 2];
                int next = prevNext[(i << 1) + 3];
                prevNext[(next << 1) + 2] = prev;
                prevNext[(prev << 1) + 3] = next;
                if (headIndex == i)
                {
                    headIndex = next;
                    headEntry = null;
                }
            }
        }

        internal override int IterateFirst()
        {
            return headIndex;
        }
        internal override int IterateNext(int i)
        {
            i = prevNext[(i << 1) + 3];
            return i == headIndex ? NO_INDEX : i;
        }

    }

}
