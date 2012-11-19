namespace Loon.Java.Collections
{
    using System;
    using System.Collections.Generic;
    using System.Collections;
    using Loon.Java.Collections;

    public class ListSet : ExtendedCollectionBase, ISet, IExtendedCollection, ICollection, IEnumerable
    {

        private List<object> list;

        public ListSet()
        {
            this.list = new List<object>();
        }

        public ListSet(ICollection c)
            : this()
        {
            base.AddAll(c);
        }

        public override bool Add(object e)
        {
            if (!this.list.Contains(e))
            {
                this.list.Add(e);
                return true;
            }
            return false;
        }

        public void Clear()
        {
            this.list.Clear();
        }

        public override bool Contains(object e)
        {
            return this.list.Contains(e);
        }

        public override bool Remove(object e)
        {
            int count = this.list.Count;
            this.list.Remove(e);
            return (count != this.list.Count);
        }

        protected override ICollection InnerCollection
        {
            get
            {
                return this.list;
            }
        }
    }


}
