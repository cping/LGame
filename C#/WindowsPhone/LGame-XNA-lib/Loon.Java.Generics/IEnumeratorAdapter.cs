namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Collections;

    public class IEnumeratorAdapter<T> : IEnumerator<T>, IDisposable, IEnumerator
    {

        private T current;

        private Loon.Java.Generics.JavaListInterface.IIterator<T> enume;

        public IEnumeratorAdapter(Loon.Java.Generics.JavaListInterface.IIterator<T> enume)
        {
            this.current = default(T);
            if (enume != null)
            {
                this.enume = enume;
            }
        }

        public void Dispose()
        {
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

        public T Current
        {
            get
            {
                return this.current;
            }
        }

        object IEnumerator.Current
        {
            get
            {
                return this.current;
            }
        }
    }


}
