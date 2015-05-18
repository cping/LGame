namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Collections;

    public abstract class AbstractQueue<E> : AbstractCollection<E>, Loon.Java.Generics.JavaListInterface.IQueue<E>, ICollection<E>, IEnumerable<E>, IEnumerable
    {
        protected AbstractQueue()
        {
        }
#pragma warning disable
        public virtual bool Add(E o)
        {
            if (o == null)
            {
                throw new NullReferenceException();
            }
            if (!this.Offer(o))
            {
                throw new Exception();
            }
            return true;
        }
#pragma warning restore
        public override bool AddAll<T>(ICollection<T> c)
        {
            if (c == null)
            {
                throw new NullReferenceException();
            }
            if (this == c)
            {
                throw new NullReferenceException();
            }
            return base.AddAll<T>(c);
        }

        public override void Clear()
        {
            while (this.Poll() != null)
            {
            }
        }

        public E Element()
        {
            E local = this.Peek();
            if (local == null)
            {
                throw new NullReferenceException();
            }
            return local;
        }

#pragma warning disable
        public IEnumerator GetEnumerator()
        {
            return new IEnumeratorAdapter<E>(this.Iterator());
        }
#pragma warning restore

        public virtual bool Offer(E e)
        {
            throw new NotImplementedException();
        }

        public virtual E Peek()
        {
            throw new NotImplementedException();
        }

        public virtual E Poll()
        {
            throw new NotImplementedException();
        }

        public E Remove()
        {
            E local = this.Poll();
            if (local == null)
            {
                throw new NullReferenceException();
            }
            return local;
        }
    }

}

