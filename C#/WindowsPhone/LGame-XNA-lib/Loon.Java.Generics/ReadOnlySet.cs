namespace Loon.Java.Generics
{

    using System;
    using System.Collections.Generic;
    using System.Collections;

    public class ReadOnlySet<T> : Loon.Java.Generics.JavaListInterface.ISet<T>, Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>, ICollection<T>, IEnumerable<T>, IEnumerable
    {

        private Loon.Java.Generics.JavaListInterface.ISet<T> set;

        public ReadOnlySet(Loon.Java.Generics.JavaListInterface.ISet<T> set)
        {
            this.set = set;
        }

        public bool Add(T e)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public bool AddAll(ICollection<T> c)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void Clear()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public bool Contains(T e)
        {
            return this.set.Contains(e);
        }

        public bool ContainsAll(ICollection<T> c)
        {
            return this.set.ContainsAll(c);
        }

        public void CopyTo(T[] array, int index)
        {
            this.set.CopyTo(array, index);
        }

        public IEnumerator<T> GetEnumerator()
        {
            return this.set.GetEnumerator();
        }

        bool Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.Add(T e)
        {
            throw new NotImplementedException();
        }

        bool Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.AddAll(ICollection<T> c)
        {
            throw new NotImplementedException();
        }

        bool Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.ContainsAll(ICollection<T> c)
        {
            throw new NotImplementedException();
        }

        bool Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.RemoveAll(ICollection<T> c)
        {
            throw new NotImplementedException();
        }

        bool Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.RetainAll(ICollection<T> c)
        {
            throw new NotImplementedException();
        }

        T[] Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.ToArray()
        {
            throw new NotImplementedException();
        }

        T[] Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>.ToArray(T[] arr)
        {
            throw new NotImplementedException();
        }

        public bool Remove(T e)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public bool RemoveAll(ICollection<T> c)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public bool RetainAll(ICollection<T> c)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        void ICollection<T>.Add(T item)
        {
            throw new NotImplementedException();
        }

        void ICollection<T>.Clear()
        {
            throw new NotImplementedException();
        }

        bool ICollection<T>.Contains(T item)
        {
            throw new NotImplementedException();
        }

        void ICollection<T>.CopyTo(T[] array, int arrayIndex)
        {
            throw new NotImplementedException();
        }

        bool ICollection<T>.Remove(T item)
        {
            throw new NotImplementedException();
        }

        IEnumerator<T> IEnumerable<T>.GetEnumerator()
        {
            throw new NotImplementedException();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            throw new NotImplementedException();
        }

        public T[] ToArray()
        {
            return this.set.ToArray();
        }

        public T[] ToArray(T[] arr)
        {
            return this.set.ToArray(arr);
        }

        public virtual int Count
        {
            get
            {
                return this.set.Count;
            }
        }

        public bool IsReadOnly
        {
            get
            {
                throw new NotImplementedException();
            }
        }

        public bool IsSynchronized
        {
            get
            {
                return false;
            }
        }

        public object SyncRoot
        {
            get
            {
                return false;
            }
        }

        int ICollection<T>.Count
        {
            get
            {
                throw new NotImplementedException();
            }
        }

        bool ICollection<T>.IsReadOnly
        {
            get
            {
                throw new NotImplementedException();
            }
        }
    }

}
