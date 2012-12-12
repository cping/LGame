namespace Loon.Java.Collections
{

    using System;
    using System.Collections.Generic;
    using System.Collections;

    public interface IIterator
    {
        bool HasNext();
        object Next();
        void Remove();
    }

    public interface IRemoveableIterator : IIterator
    {
    }

    public interface IListIterator : IRemoveableIterator, IIterator
    {
        bool HasPrevious();
        int NextIndex();
        object Previous();
        int PreviousIndex();
        void Set(object x);
    }

    public class IEnumeratorAdapter : IEnumerator
    {
        private object current;
        private IIterator enume;

        public IEnumeratorAdapter(IIterator enume)
        {
            if (enume != null)
            {
                this.enume = enume;
                if (enume.HasNext())
                {
                    this.current = enume.Next();
                }
            }
        }

        public bool MoveNext()
        {
            bool flag = this.enume.HasNext();
            if (flag)
            {
                this.current = this.enume.Next();
            }
            return flag;
        }

        public void Reset()
        {
        }

        public object Current
        {
            get
            {
                return this.current;
            }
        }
    }


    public interface IExtendedCollection : ICollection, IEnumerable
    {
        bool Add(object e);
        bool AddAll(ICollection c);
        void Clear();
        bool Contains(object e);
        bool ContainsAll(ICollection c);
        bool Remove(object e);
        bool RemoveAll(ICollection c);
        bool RetainAll(ICollection c);
        object[] ToArray();
        object[] ToArray(object[] arr);
    }

    public interface ISet : IExtendedCollection, ICollection, IEnumerable
    {
    }
 
}
