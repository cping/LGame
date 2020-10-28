using System;
using System.Collections;

namespace java.util
{

    public class ArrayListIterator<V> : IListIterator<V>, IRemoveableIterator<V>, Iterator<V>
    {
        private int index;
        private readonly IList list;

        public ArrayListIterator(IList list)
            : this(list, -1)
        {
        }

        public ArrayListIterator(IList list, int index)
        {
            this.list = list;
            this.index = index;
        }

        public void Add(V o)
        {
            this.list.Insert(++this.index, o);
        }

        public bool HasNext()
        {
            return ((this.index + 1) < this.list.Count);
        }

        public bool HasPrevious()
        {
            return (this.index >= 0);
        }

        public V Next()
        {
            if (!this.HasNext())
            {
                throw new InvalidOperationException();
            }
            return (V)this.list[++this.index];
        }

        public int NextIndex()
        {
            return this.index;
        }

        public V Previous()
        {
            if (!this.HasPrevious())
            {
                throw new InvalidOperationException();
            }
            return (V)this.list[this.index--];
        }

        public int PreviousIndex()
        {
            return (this.index - 1);
        }

        public void Remove()
        {
            throw new InvalidOperationException();
        }

        public void Set(V o)
        {
            throw new InvalidOperationException();
        }
    }

}
