namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using Loon.Java.Collections;

    public class IteratorAdapter<T> : Loon.Java.Generics.JavaListInterface.IIterator<T>, IIterator
    {

        private bool callMoveNext;
        private IEnumerator<T> enumerator;
        private bool hasNext;

        public IteratorAdapter(IEnumerator<T> e)
        {
            this.enumerator = e;
            this.callMoveNext = true;
        }

        public bool HasNext()
        {
            this.MoveNext();
            return this.hasNext;
        }

        bool IIterator.HasNext()
        {
            return this.HasNext();
        }

        object IIterator.Next()
        {
            return this.Next();
        }

        void IIterator.Remove()
        {
            throw new NotImplementedException();
        }

        private void MoveNext()
        {
            if (this.callMoveNext)
            {
                this.hasNext = this.enumerator.MoveNext();
                this.callMoveNext = false;
            }
        }

        public T Next()
        {
            this.MoveNext();
            this.callMoveNext = true;
            return this.enumerator.Current;
        }

        public void Remove()
        {
            throw new NotImplementedException("");
        }
    }


}
