using java.lang;

namespace java.util
{
    public interface Collection<V> : Iterable<V>
    {
        bool Add(V o);
        bool AddAll(Collection<V> c);
        void Clear();
        bool Contains(V o);
        bool ContainsAll(Collection<V> c);
        bool Equals(object o);
        bool Remove(V o);
        bool RemoveIf(java.util.function.Predicate predicate);
        bool RemoveAll(Collection<V> c);
        bool RetainAll(Collection<V> c);
        int GetHashCode();
        bool IsEmpty();
        int Size();
        object[] ToArray();
        V[] ToArray(V[] a);
        string ToString();

    }


    public static class Collection_Java<V>
    {
        public static bool RemoveIf(Collection<V> @this, java.util.function.Predicate predicate)
        {
            java.util.Iterator<V> i = @this.Iterator();
            bool didremove = false;
            while (i.HasNext())
            {
                object o = i.Next();
                if (predicate.Test(o))
                {
                    didremove = true;
                    i.Remove();
                }
            }
            return didremove;
        }
    }
}
