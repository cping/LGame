namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Collections;
    using Loon.Java.Collections;

    public class JavaListInterface
    {
#pragma warning disable
        public interface IExtendedCollection<T> : ICollection<T>, IEnumerable<T>, IEnumerable
        {
            bool Add(T e);
            bool AddAll(ICollection<T> c);
            bool ContainsAll(ICollection<T> c);
            bool RemoveAll(ICollection<T> c);
            bool RetainAll(ICollection<T> c);
            T[] ToArray();
            T[] ToArray(T[] arr);
        }
#pragma warning restore
        public interface IIterator<T> 
        {
            bool HasNext();
            T Next();
            void Remove();
        }

        public interface IListIterator<T> : IIterator<T>, IIterator
        {
            void Add(T x);
            bool HasPrevious();
            int NextIndex();
            T Previous();
            int PreviousIndex();
            void Set(T x);
        }
#pragma warning disable
        public interface IQueue<E> : ICollection<E>, IEnumerable<E>, IEnumerable
        {
            bool Add(E e);
            E Element();
            bool Offer(E e);
            E Peek();
            E Poll();
            E Remove();
        }
#pragma warning restore
        public interface ISet<T> : IExtendedCollection<T>, ICollection<T>, IEnumerable<T>, IEnumerable
        {
        }

 

 

 

 

 

 

 

 

 

 

    }
}
