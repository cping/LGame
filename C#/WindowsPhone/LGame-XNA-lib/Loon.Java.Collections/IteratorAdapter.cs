namespace Loon.Java.Collections
{
    using System;
    using Loon.Java.Collections;
    using System.Collections;

    public class IteratorAdapter : IIterator
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

        public object Next()
        {
            this.MoveNext();
            this.callMoveNext = true;
            return this.enumerator.Current;
        }

        public void Remove()
        {
            throw new NotImplementedException("IteratorAdapter Remove");
        }
    }


}
