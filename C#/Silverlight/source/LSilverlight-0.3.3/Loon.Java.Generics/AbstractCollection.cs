namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Collections;

    public abstract class AbstractCollection<E> : ICollection<E>, IEnumerable<E>, IEnumerable
    {
        protected AbstractCollection()
        {
        }

        public virtual void Add(E obj)
        {
            throw new NotSupportedException();
        }

        public virtual bool AddAll<T>(ICollection<T> c) where T : E
        {
            foreach (T local in c)
            {
                this.Add(local);
            }
            return true;
        }

        public virtual void Clear()
        {
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            while (iterator.HasNext())
            {
                iterator.Next();
                iterator.Remove();
            }
        }

        public virtual bool Contains(E obj)
        {
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            if (obj == null)
            {
                while (iterator.HasNext())
                {
                    if (iterator.Next() == null)
                    {
                        return true;
                    }
                }
            }
            else
            {
                while (iterator.HasNext())
                {
                    if (obj.Equals(iterator.Next()))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public virtual bool ContainsAll(ICollection<E> collection)
        {
            foreach (E local in collection)
            {
                if (!this.Contains(local))
                {
                    return false;
                }
            }
            return true;
        }

        public virtual void CopyTo(E[] array, int arrayIndex)
        {
            E[] localArray = this.ToArray();
            for (int i = 0; i < this.Count; i++)
            {
                array[i] = localArray[i];
            }
        }

        public IEnumerator<E> GetEnumerator()
        {
            return new IEnumeratorAdapter<E>(this.Iterator());
        }

        public virtual bool IsEmpty()
        {
            return (this.Count == 0);
        }

        public abstract Loon.Java.Generics.JavaListInterface.IIterator<E> Iterator();
        public virtual bool Remove(object obj)
        {
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            if (obj == null)
            {
                while (iterator.HasNext())
                {
                    if (iterator.Next() == null)
                    {
                        iterator.Remove();
                        return true;
                    }
                }
            }
            else
            {
                while (iterator.HasNext())
                {
                    if (obj.Equals(iterator.Next()))
                    {
                        iterator.Remove();
                        return true;
                    }
                }
            }
            return false;
        }

        public virtual bool Remove(E item)
        {
            return this.Remove(item);
        }

        public virtual bool RemoveAll(ICollection<E> collection)
        {
            bool flag = false;
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

        public virtual bool RetainAll(ICollection<E> collection)
        {
            bool flag = false;
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            while (iterator.HasNext())
            {
                if (!collection.Contains(iterator.Next()))
                {
                    iterator.Remove();
                    flag = true;
                }
            }
            return flag;
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return new IEnumeratorAdapter<E>(this.Iterator());
        }

        public virtual E[] ToArray()
        {
            int count = this.Count;
            int num2 = 0;
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            E[] localArray = new E[count];
            while (num2 < count)
            {
                localArray[num2++] = iterator.Next();
            }
            return localArray;
        }

        public virtual T[] ToArray<T>(T[] contents) where T : E
        {
            int count = this.Count;
            int index = 0;
            if (count > contents.Length)
            {
                contents = (T[])Array.CreateInstance(contents.GetType().GetElementType(), count);
            }
            foreach (E local in this)
            {
                contents[index++] = (T)local;
            }
            if (index < contents.Length)
            {
                contents[index] = default(T);
            }
            return contents;
        }

        public override string ToString()
        {
            if (this.IsEmpty())
            {
                return "[]";
            }
            StringBuilder builder = new StringBuilder(this.Count * 0x10);
            builder.Append('[');
            Loon.Java.Generics.JavaListInterface.IIterator<E> iterator = this.Iterator();
            while (iterator.HasNext())
            {
                object obj2 = iterator.Next();
                if (obj2 != this)
                {
                    builder.Append(obj2);
                }
                else
                {
                    builder.Append("(this Collection)");
                }
                if (iterator.HasNext())
                {
                    builder.Append(", ");
                }
            }
            builder.Append(']');
            return builder.ToString();
        }

        public abstract int Count { get; }

        public bool IsReadOnly
        {
            get
            {
                return true;
            }
        }
    }

}
