namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Collections;

    public abstract class AbstractSet<E> : AbstractCollection<E>, Loon.Java.Generics.JavaListInterface.ISet<E>, Loon.Java.Generics.JavaListInterface.IExtendedCollection<E>, IEnumerable<E>, IEnumerable
    {
        protected AbstractSet()
        {
        }
#pragma warning disable
        public virtual bool Add(E e)
        {
            throw new NotImplementedException();
        }
#pragma warning restore
        public bool AddAll(ICollection<E> c)
        {
            throw new NotImplementedException();
        }

        public override bool Equals(object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj is Loon.Java.Generics.JavaListInterface.ISet<E>)
            {
                Loon.Java.Generics.JavaListInterface.ISet<E> collection = (Loon.Java.Generics.JavaListInterface.ISet<E>)obj;
                try
                {
                    return ((this.Count == collection.Count) && this.ContainsAll(collection));
                }
                catch (Exception)
                {
                    return false;
                }
            }
            return false;
        }
#pragma warning disable
        public virtual IEnumerator<E> GetEnumerator()
        {
            return new IEnumeratorAdapter<E>(this.Iterator());
        }
#pragma warning restore
        public override int GetHashCode()
        {
            int num = 0;
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            while (iterator.HasNext())
            {
                object obj2 = iterator.Next();
                num += (obj2 == null) ? 0 : obj2.GetHashCode();
            }
            return num;
        }

        public override Loon.Java.Generics.JavaListInterface.IIterator<E> Iterator()
        {
            throw new NotImplementedException();
        }

        public override bool RemoveAll(ICollection<E> collection)
        {
            bool flag = false;
            if (this.Count <= collection.Count)
            {
                Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
                while (iterator.HasNext())
                {
                    if (collection.Contains(iterator.Next()))
                    {
                        iterator.Remove();
                        flag = true;
                    }
                }
                return flag;
            }
            foreach (E local in collection)
            {
                flag = this.Remove(local) || flag;
            }
            return flag;
        }

        public virtual E[] ToArray(E[] arr)
        {
            return base.ToArray<E>(arr);
        }

        public override int Count
        {
            get
            {


                throw new NotImplementedException();
            }
        }
    }


}
