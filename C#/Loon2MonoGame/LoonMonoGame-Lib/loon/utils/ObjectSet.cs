using java.lang;
using java.util;
using java.util.function;
using System.Collections;
using System.Collections.Generic;

namespace loon.utils
{
    public class ObjectSet<E> : Iterable<E>, IArray, IEnumerable<E>
    {

        private ObjectMap<E, object> _map;

        public ObjectSet()
        {
            _map = new ObjectMap<E, object>(false);
        }

        public ObjectSet(ObjectSet<E> c)
        {
            _map = new ObjectMap<E, object>(false);
            AddAll(c);
        }

        public ObjectSet(int initialCapacity, float loadFactor)
        {
            _map = new ObjectMap<E, object>(initialCapacity, loadFactor, false);
        }

        public ObjectSet(int initialCapacity)
        {
            _map = new ObjectMap<E, object>(initialCapacity, false);
        }

        internal ObjectSet(int initialCapacity, float loadFactor, bool dummy)
        {
            _map = new OrderedMap<E, object>(initialCapacity, loadFactor, false, false);
        }

        public void AddAll(ObjectSet<E> list)
        {
            foreach (E key in list)
            {
                Add(key);
            }
        }


        public LIterator<E> Iterator()
        {
            return _map.GetKeys();
        }

        public int Size()
        {
            return _map.Size();
        }

        public bool IsEmpty()
        {
            return _map.IsEmpty();
        }

        public bool Contains(object o)
        {
            return _map.ContainsKey(o);
        }

        public bool Add(E e)
        {
            return _map.Put(e, null) == null;
        }

        public bool Remove(object o)
        {
            return _map.Remove(o) == ObjectMap<E, object>.FINAL_VALUE;
        }


        public override int GetHashCode()
        {
            if (_map == null)
            {
                return base.GetHashCode();
            }
            int hashCode = 1;
            for (LIterator<E> it = _map.GetKeys(); it.HasNext();)
            {
                E e = it.Next();
                hashCode = 31 * hashCode + (e == null ? 0 : e.GetHashCode());
            }
            return hashCode;
        }

        Iterator<E> Iterable<E>.Iterator()
        {
            return Iterator();
        }

        public void ForEach(Consumer consumer)
        {
            Iterable_Java<E>.ForEach(this, consumer);
        }

        public IEnumerator<E> GetEnumerator()
        {
            return new IEnumeratorAdapter<E>(this.Iterator());
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }
        public void Clear()
        {
            _map.Clear();
        }
        public override string ToString()
        {
            LIterator<E> it = Iterator();
            if (!it.HasNext())
            {
                return "[]";
            }
            StrBuilder sbr = new StrBuilder();
            sbr.Append('[');
            for (it = _map.GetKeys(); it.HasNext();)
            {
                E e = it.Next();
                sbr.Append((object)e == (object)this ? "(this ObjectSet)" : e.ToString());
                if (!it.HasNext())
                {
                    return sbr.Append(']').ToString();
                }
                sbr.Append(',').Append(' ');
            }
            return sbr.ToString();
        }


    }

}
