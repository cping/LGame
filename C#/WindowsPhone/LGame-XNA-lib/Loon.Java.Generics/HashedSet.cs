namespace Loon.Java.Generics
{

    using System;
    using System.Collections.Generic;
    using System.Collections;

    public class HashedSet<T> : ExtendedCollectionBase<T>, Loon.Java.Generics.JavaListInterface.ISet<T>, Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>, ICollection<T>, IEnumerable<T>, IEnumerable
    {

        private Dictionary<T, T> dictionary;

        public HashedSet()
            : this(0)
        {
        }

        public HashedSet(ICollection<T> c)
            : this()
        {
            this.AddAll(c);
        }

        public HashedSet(int capacity)
            : this(capacity, 1f)
        {
        }

        public HashedSet(int capacity, float loadFactor)
        {
            this.dictionary = new Dictionary<T, T>(capacity);
        }

        public override bool Add(T e)
        {
            if (!this.dictionary.ContainsKey(e))
            {
                this.dictionary.Add(e, e);
                return true;
            }
            return false;
        }

        public override void Clear()
        {
            this.dictionary.Clear();
        }

        public override bool Contains(T e)
        {
            return this.dictionary.ContainsKey(e);
        }

        public override IEnumerator<T> GetEnumerator()
        {
            return (IEnumerator<T>)this.dictionary.Values.GetEnumerator();
        }

        public override bool Remove(T e)
        {
            int count = this.dictionary.Count;
            this.dictionary.Remove(e);
            return (count != this.dictionary.Count);
        }

        protected override ICollection<T> InnerCollection
        {
            get
            {
                return this.dictionary.Keys;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return (this.dictionary.Count == 0);
            }
        }
    }


}
