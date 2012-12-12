namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Collections;

    public abstract class ExtendedCollectionBase<T> : Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>, ICollection<T>, IEnumerable<T>, IEnumerable
    {
        protected ExtendedCollectionBase()
        {
        }

        public abstract bool Add(T e);
        public virtual bool AddAll(ICollection<T> c)
        {
            bool flag = false;
            foreach (T local in c)
            {
                flag |= this.Add(local);
            }
            return flag;
        }

        public abstract void Clear();
        private static bool CollectionContains(ICollection<T> c, T v)
        {
            foreach (T local in c)
            {
                if (object.Equals(local, v))
                {
                    return true;
                }
            }
            return false;
        }

        public abstract bool Contains(T e);
        public virtual bool ContainsAll(ICollection<T> c)
        {
            foreach (T local in c)
            {
                if (!this.Contains(local))
                {
                    return false;
                }
            }
            return true;
        }

        public void CopyTo(T[] array, int index)
        {
            this.InnerCollection.CopyTo(array, index);
        }

        public virtual IEnumerator<T> GetEnumerator()
        {
            return this.InnerCollection.GetEnumerator();
        }

        public static Loon.Java.Generics.JavaListInterface.ISet<T> ReadOnly(Loon.Java.Generics.JavaListInterface.ISet<T> set)
        {
            return new ReadOnlySet<T>(set);
        }

        public abstract bool Remove(T e);
        public virtual bool RemoveAll(ICollection<T> c)
        {
            bool flag = false;
            foreach (T local in c)
            {
                flag |= this.Remove(local);
            }
            return flag;
        }

        public virtual bool RetainAll(ICollection<T> c)
        {
            List<T> list = new List<T>(this.InnerCollection);
            foreach (T local in list)
            {
                if (!ExtendedCollectionBase<T>.CollectionContains(c, local))
                {
                    this.Remove(local);
                }
            }
            return (this.Count != list.Count);
        }

        void ICollection<T>.Add(T item)
        {
            this.Add(item);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        public virtual T[] ToArray()
        {
            T[] localArray = new T[this.InnerCollection.Count];
            int num = 0;
            foreach (T local in this.InnerCollection)
            {
                localArray[num++] = local;
            }
            return localArray;
        }

        public T[] ToArray(T[] arr)
        {
            int count = this.Count;
            if (arr.Length < count)
            {
                arr = (T[])Array.CreateInstance(arr.GetType().GetElementType(), count);
            }
            this.CopyTo(arr, 0);
            if (arr.Length > count)
            {
                arr[count] = default(T);
            }
            return arr;
        }

        public virtual int Count
        {
            get
            {
                return this.InnerCollection.Count;
            }
        }

        protected abstract ICollection<T> InnerCollection { get; }

        public abstract bool IsEmpty { get; }

        public bool IsReadOnly
        {
            get
            {
                throw new NotImplementedException();
            }
        }

        public virtual bool IsSynchronized
        {
            get
            {
                return false;
            }
        }

        public virtual object SyncRoot
        {
            get
            {
                return null;
            }
        }
    }
}
