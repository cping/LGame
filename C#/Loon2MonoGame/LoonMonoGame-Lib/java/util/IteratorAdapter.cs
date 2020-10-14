using System;
using System.Collections;

namespace java.util
{
    public class IteratorAdapter<V> : Iterator<V>
    {
        private bool callMoveNext;
        private IEnumerator enumerator;
        private bool hasNext;

        public IteratorAdapter(IEnumerator e)
        {
            this.enumerator = e;
            this.callMoveNext = true;
        }

        public bool HasNext()
        {
            this.MoveNext();
            return this.hasNext;
        }

        private void MoveNext()
        {
            if (this.callMoveNext)
            {
                this.hasNext = this.enumerator.MoveNext();
                this.callMoveNext = false;
            }
        }

        public V Next()
        {
            this.MoveNext();
            this.callMoveNext = true;
            return (V)this.enumerator.Current;
        }

        public void Remove()
        {
            throw new NotImplementedException("IteratorAdapter Remove");
        }
    }


}
