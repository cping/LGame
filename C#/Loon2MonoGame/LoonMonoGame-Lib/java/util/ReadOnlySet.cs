using System;
using System.Collections;

namespace java.util
{
    public class ReadOnlySet : ISet, IExtendedCollection, ICollection, IEnumerable
    {

        private readonly ISet set;

        public ReadOnlySet(ISet set)
        {
            this.set = set;
        }

        public bool Add(object e)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public bool AddAll(ICollection c)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public void Clear()
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public bool Contains(object e)
        {
            return this.set.Contains(e);
        }

        public bool ContainsAll(ICollection c)
        {
            return this.set.ContainsAll(c);
        }

        public void CopyTo(Array array, int index)
        {
            this.set.CopyTo(array, index);
        }

        public IEnumerator GetEnumerator()
        {
            return this.set.GetEnumerator();
        }

        public bool Remove(object e)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public bool RemoveAll(ICollection c)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public bool RetainAll(ICollection c)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public object[] ToArray()
        {
            return this.set.ToArray();
        }

        public object[] ToArray(object[] arr)
        {
            return this.set.ToArray(arr);
        }

        public int Count
        {
            get
            {
                return this.set.Count;
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
                return this.set.SyncRoot;
            }
        }
    }
}
