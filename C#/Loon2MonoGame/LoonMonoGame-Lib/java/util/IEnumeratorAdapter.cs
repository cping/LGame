using System;
using System.Collections;
using System.Collections.Generic;

namespace java.util
{
    public class IEnumeratorAdapter<V> : IEnumerator<V>, IDisposable, IEnumerator
    {
        private V current;
        private readonly Iterator<V> enume;

        public IEnumeratorAdapter(Iterator<V> enume)
        {
            this.current = default;
            if (enume != null)
            {
                this.enume = enume;
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

        public void Dispose()
        {
        }

        public V Current
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
