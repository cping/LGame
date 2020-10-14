using System.Collections;
using System.Collections.Generic;

namespace java.util
{
    public class ListSet : ExtendedCollectionBase, ISet, IExtendedCollection, ICollection, IEnumerable
    {

        private System.Collections.Generic.List<object> list;

        public ListSet()
        {
            this.list = new System.Collections.Generic.List<object>();
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
