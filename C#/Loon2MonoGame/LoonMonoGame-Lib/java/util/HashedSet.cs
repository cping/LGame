using System.Collections;
using System.Collections.Generic;

namespace java.util
{

    public class HashedSet : ExtendedCollectionBase, ISet, IExtendedCollection, ICollection, IEnumerable
    {

        private readonly Dictionary<object, object> dictionary;

        public HashedSet()
            : this(0)
        {
        }

        public HashedSet(ICollection c)
            : this()
        {
            base.AddAll(c);
        }

        public HashedSet(int capacity)
            : this(capacity, 1f)
        {
        }

        public HashedSet(int capacity, float loadFactor)
        {
            this.dictionary = new Dictionary<object, object>(capacity);
        }

        public override bool Add(object e)
        {
            if (!this.dictionary.ContainsKey(e))
            {
                this.dictionary.Add(e, e);
                return true;
            }
            return false;
        }

        public void Clear()
        {
            this.dictionary.Clear();
        }

        public override bool Contains(object e)
        {
            return this.dictionary.ContainsKey(e);
        }

        public override IEnumerator GetEnumerator()
        {
            return this.dictionary.Values.GetEnumerator();
        }

        public override bool Remove(object e)
        {
            int count = this.dictionary.Count;
            this.dictionary.Remove(e);
            return (count != this.dictionary.Count);
        }

        protected override ICollection InnerCollection
        {
            get
            {
                return this.dictionary.Keys;
            }
        }
    }

}
