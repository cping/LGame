namespace java.util
{
    public class HashTable<K, V> : HashMapImpl<K, V>
    {
        public HashTable() : base()
        {
        }

        public HashTable(Map<K, V> m) : base(m)
        {
        }

        public override void Clear()
        {
            lock (this) { base.Clear(); }
        }

        public override bool ContainsKey(K key)
        {
            lock (this) { return base.ContainsKey(key); }
        }

        public override bool ContainsValue(V value)
        {
            lock (this) { return base.ContainsValue(value); }
        }

        public override bool Equals(object o)
        {
            lock (this) { return base.Equals(o); }
        }

        public override V Get(K key)
        {
            lock (this) { return base.Get(key); }
        }

        public override int GetHashCode()
        {
            lock (this) { return base.GetHashCode(); }
        }

        public override bool IsEmpty()
        {
            lock (this) { return base.IsEmpty(); }
        }

        public override Set<K> KeySet()
        {
            lock (this) { return base.KeySet(); }
        }

        public override V Put(K key, V value)
        {
            lock (this) { return base.Put(key, value); }
        }

        public override void PutAll(Map<K, V> m)
        {
            lock (this) { base.PutAll(m); }
        }

        public override V Remove(K key)
        {
            lock (this) { return base.Remove(key); }
        }

        public override int Size()
        {
            lock (this) { return base.Size(); }
        }

        public override string ToString()
        {
            lock (this) { return base.ToString(); }
        }

        public override Collection<V> Values()
        {
            lock (this) { return base.Values(); }
        }

        public virtual object Clone()
        {
            lock (this) { return new HashTable<K, V>(this); }
        }

        public virtual bool Contains(V value)
        {
            lock (this) { return base.ContainsValue(value); }
        }

        public virtual Enumeration<V> Elements()
        {
            lock (this) { return new HashMapValueIterator<K, V>(this); }
        }

        public virtual Enumeration<K> Keys()
        {
            lock (this) { return new HashMapKeyIterator<K, V>(this); }
        }
    }
}
