namespace Loon.Java.Collections
{
    using System;
    using System.Collections.Generic;
    using Loon.Java.Collections;
    using System.Collections;

    public class ArrayListIterator:IListIterator, IRemoveableIterator, IIterator
    {
        private int index;
        private IList list;

        public ArrayListIterator(IList list)
            : this(list, -1)
        {
        }

        public ArrayListIterator(IList list, int index)
        {
            this.list = list;
            this.index = index;
        }

        public void Add(object o)
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

        public object Next()
        {
            if (!this.HasNext())
            {
                throw new InvalidOperationException();
            }
            return this.list[++this.index];
        }

        public int NextIndex()
        {
            return this.index;
        }

        public object Previous()
        {
            if (!this.HasPrevious())
            {
                throw new InvalidOperationException();
            }
            return this.list[this.index--];
        }

        public int PreviousIndex()
        {
            return (this.index - 1);
        }

        public void Remove()
        {
            throw new InvalidOperationException();
        }

        public void Set(object o)
        {
            throw new InvalidOperationException();
        }
    }

    public interface ICloneable
    {
        object Clone();
    }

}
