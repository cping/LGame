using System.Collections;

namespace java.util
{
    public class IEnumeratorAdapter<V> : IEnumerator
    {
        private object current;
        private readonly Iterator<V> enume;

        public IEnumeratorAdapter(Iterator<V> enume)
        {
            if (enume != null)
            {
                this.enume = enume;
                if (enume.HasNext())
                {
                    this.current = enume.Next();
                }
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

        public object Current
        {
            get
            {
                return this.current;
            }
        }
    }
}
