namespace Loon.Java.Generics
{

    using System;
    using System.Collections.Generic;
    using System.Collections;

    public class ListSet<T> : ExtendedCollectionBase<T>, Loon.Java.Generics.JavaListInterface.ISet<T>, Loon.Java.Generics.JavaListInterface.IExtendedCollection<T>, ICollection<T>, IEnumerable<T>, IEnumerable
    {

        private List<T> list;

        public ListSet()
        {
            this.list = new List<T>();
        }

        public ListSet(ICollection<T> c)
            : this()
        {
            this.AddAll(c);
        }

        public override bool Add(T e)
        {
            if (!this.list.Contains(e))
            {
                this.list.Add(e);
                return true;
            }
            return false;
        }

        public override void Clear()
        {
            this.list.Clear();
        }

        public override bool Contains(T e)
        {
            return this.list.Contains(e);
        }

        public override bool Remove(T e)
        {
            int count = this.list.Count;
            this.list.Remove(e);
            return (count != this.list.Count);
        }

        protected override ICollection<T> InnerCollection
        {
            get
            {
                return this.list;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return (this.list.Count == 0);
            }
        }
    }
}
