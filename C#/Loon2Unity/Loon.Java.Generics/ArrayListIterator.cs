namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using Loon.Java.Collections;

    public class ArrayListIterator<T> : Loon.Java.Generics.JavaListInterface.IListIterator<T>, Loon.Java.Generics.JavaListInterface.IIterator<T>, IIterator
    {

        private int index;
        private IList<T> list;

        public ArrayListIterator(T[] list)
            : this(list, -1)
        {
        }

        public ArrayListIterator(IList<T> list)
            : this(list, -1)
        {
        }

        public ArrayListIterator(T[] list, int index)
            : this((IList<T>)list, index)
        {
        }

        public ArrayListIterator(IList<T> list, int index)
        {
            this.list = list;
            this.index = index;
        }

        public void Add(T o)
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

        bool IIterator.HasNext()
        {
            throw new NotImplementedException();
        }

        object IIterator.Next()
        {
            return this.Next();
        }

        void IIterator.Remove()
        {
            throw new NotImplementedException();
        }

        public T Next()
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

        public T Previous()
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
            this.list.Remove(this.list[this.index]);
            this.index = -1;
        }

        public void Set(T o)
        {
            if ((this.index < 0) || (this.index >= this.list.Count))
            {
                throw new InvalidOperationException();
            }
            this.list[this.index] = o;
        }
    }
}
