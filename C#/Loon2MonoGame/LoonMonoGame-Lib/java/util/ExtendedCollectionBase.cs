using System;
using System.Collections;
using System.Collections.Generic;

namespace java.util
{
    public abstract class ExtendedCollectionBase
    {
        protected ExtendedCollectionBase()
        {
        }

        public abstract bool Add(object e);
        public bool AddAll(ICollection c)
        {
            bool flag = false;
            foreach (object obj2 in c)
            {
                flag |= this.Add(obj2);
            }
            return flag;
        }

        private static bool CollectionContains(ICollection c, object v)
        {
            foreach (object obj2 in c)
            {
                if (object.Equals(obj2, v))
                {
                    return true;
                }
            }
            return false;
        }

        public abstract bool Contains(object e);

        public virtual bool ContainsAll(ICollection c)
        {
            foreach (object obj2 in c)
            {
                if (!this.Contains(obj2))
                {
                    return false;
                }
            }
            return true;
        }

        public void CopyTo(Array array, int index)
        {
            this.InnerCollection.CopyTo(array, index);
        }

        public virtual IEnumerator GetEnumerator()
        {
            return this.InnerCollection.GetEnumerator();
        }

        public static ISet ReadOnly(ISet set)
        {
            return new ReadOnlySet(set);
        }

        public abstract bool Remove(object e);
        public virtual bool RemoveAll(ICollection c)
        {
            bool flag = false;
            foreach (object obj2 in c)
            {
                flag |= this.Remove(obj2);
            }
            return flag;
        }

        public virtual bool RetainAll(ICollection c)
        {
            int idx = 0;
            System.Collections.Generic.List<object> result = new System.Collections.Generic.List<object>(this.InnerCollection.Count);
            for (IEnumerator e = InnerCollection.GetEnumerator(); e.MoveNext();)
            {
                result[idx] = e.Current;
                idx++;
            }
            foreach (object obj2 in result)
            {
                if (!CollectionContains(c, obj2))
                {
                    this.Remove(obj2);
                }
            }
            return (this.Count != result.Count);
        }

        public virtual object[] ToArray()
        {
            object[] objArray = new object[this.InnerCollection.Count];
            int num = 0;
            foreach (object obj2 in this.InnerCollection)
            {
                objArray[num++] = obj2;
            }
            return objArray;
        }

        public virtual object[] ToArray(object[] arr)
        {
            int count = this.Count;
            if (arr.Length < count)
            {
                arr = (object[])Array.CreateInstance(arr.GetType().GetElementType(), count);
            }
            this.CopyTo(arr, 0);
            if (arr.Length > count)
            {
                arr[count] = null;
            }
            return arr;
        }

        public int Count
        {
            get
            {
                return this.InnerCollection.Count;
            }
        }

        protected abstract ICollection InnerCollection { get; }

        public bool IsSynchronized
        {
            get
            {
                return this.InnerCollection.IsSynchronized;
            }
        }

        public object SyncRoot
        {
            get
            {
                return this.InnerCollection.SyncRoot;
            }
        }
    }
}
