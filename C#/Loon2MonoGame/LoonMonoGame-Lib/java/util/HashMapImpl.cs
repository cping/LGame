namespace java.util
{
    public class HashMapImpl<K, V> : Map<K, V>
    {
        internal readonly System.Collections.Generic.Dictionary<K, V> data;
        private bool hasNullKey;
        private object valueForNullKey;

        public HashMapImpl()
        {
            data = new System.Collections.Generic.Dictionary<K, V>();
            hasNullKey = false;
            valueForNullKey = null;
        }

        public HashMapImpl(Map<K, V> m) : this()
        {
            PutAll(m);
        }

        public virtual void Clear()
        {
            data.Clear();
            hasNullKey = false;
            valueForNullKey = null;
        }

        public virtual bool ContainsKey(K key)
        {
            if (key == null) { return hasNullKey; }
            else { return data.ContainsKey(key); }
        }

        public virtual bool ContainsValue(V value)
        {
            if (hasNullKey)
            {
                if (value == null)
                {
                    if (valueForNullKey == null) { return true; }
                }
                else
                {
                    if (value.Equals(valueForNullKey)) { return true; }
                }
            }
            return data.ContainsValue(value);
        }

        public override bool Equals(object o)
        {
            if (o == null || !(o is Map<K, V>)) { return false; }
            Map<K, V> m = (Map<K, V>)o;
            if (Size() != m.Size()) { return false; }
            for (Iterator<K> it = KeySet().Iterator(); it.HasNext();)
            {
                K k = it.Next();
                if (!m.ContainsKey(k)) { return false; }
                object v1 = Get(k);
                object v2 = m.Get(k);
                if (!(v1 == null ? v2 == null : v1.Equals(v2))) { return false; }
            }
            return true;
        }

        public virtual V Get(K key)
        {
            if (key == null) { return (V)valueForNullKey; }
            V v;
            data.TryGetValue(key, out v);
            return v;
        }

        public override int GetHashCode()
        {
            int sum = 0;
            for (Iterator<K> it = this.KeySet().Iterator(); it.HasNext();)
            {
                K k = it.Next();
                V v = Get(k);
                int c = (k == null ? 0 : k.GetHashCode()) ^ (v == null ? 0 : v.GetHashCode());
                sum = (sum + c) & -1;
            }
            return sum;
        }

        public virtual bool IsEmpty()
        {
            return Size() <= 0;
        }

        public virtual Set<K> KeySet()
        {
            return new HashMapKeyView<K, V>(this);
        }

        public virtual V Put(K key, V value)
        {
            if (key == null)
            {
                object prev = valueForNullKey;
                hasNullKey = true;
                valueForNullKey = value;
                return (V)prev;
            }
            else
            {
                V prev;
                data.TryGetValue(key, out prev);
                data[key] = value;
                return prev;
            }
        }

        public virtual void PutAll(Map<K, V> m)
        {
            for (Iterator<K> it = m.KeySet().Iterator(); it.HasNext();)
            {
                K key = it.Next();
                this.Put(key, m.Get(key));
            }
        }

        public virtual V Remove(K key)
        {
            if (key == null)
            {
                if (!hasNullKey)
                {
                    return default;
                }
                else
                {
                    object prev = valueForNullKey;
                    hasNullKey = false;
                    valueForNullKey = null;
                    return (V)prev;
                }
            }
            else
            {
                V prev;
                if (data.TryGetValue(key, out prev)) { data.Remove(key); }
                return prev;
            }
        }

        public virtual int Size()
        {
            return data.Count + (hasNullKey ? 1 : 0);
        }

        public override string ToString()
        {
            System.Text.StringBuilder b = new System.Text.StringBuilder("{");
            bool first = true;
            for (Iterator<K> it = KeySet().Iterator(); it.HasNext();)
            {
                K k = it.Next();
                V v = Get(k);
                if (first)
                {
                    first = false;
                }
                else
                {
                    b.Append(", ");
                }
                b.Append(k == null ? "null" : k.ToString());
                b.Append("=");
                b.Append(v == null ? "null" : v.ToString());
            }
            b.Append("}");
            return b.ToString();
        }

        public virtual Collection<V> Values()
        {
            return new HashMapValueView<K, V>(this);
        }

        public virtual V GetOrDefault(K key, V def)
        {
            return java.util.Map_Java<K, V>.GetOrDefault(this, key, def);
        }

        public virtual void ForEach(java.util.function.BiConsumer biconsumer)
        {
            java.util.Map_Java<K, V>.ForEach(this, biconsumer);
        }
    }

    class HashMapKeyView<K, V> : AbstractCollection<K>, Set<K>
    {
        private readonly HashMapImpl<K, V> map;

        public HashMapKeyView(HashMapImpl<K, V> m)
        {
            this.map = m;
        }

        public override void Clear()
        {
            map.Clear();
        }

        public override bool Contains(K o)
        {
            return map.ContainsKey(o);
        }

        public override bool Equals(object o)
        {
            if (o == null || !(o is HashMapKeyView<K, V>)) { return false; }
            HashMapKeyView<K, V> c = (HashMapKeyView<K, V>)o;
            if (Size() != c.Size())
            {
                return false;
            }
            for (Iterator<K> it = Iterator(); it.HasNext();)
            {
                if (!c.map.ContainsKey(it.Next())) return false;
            }
            return true;
        }

        public override int GetHashCode()
        {
            int h = 0;
            for (Iterator<K> it = Iterator(); it.HasNext();)
            {
                object e = it.Next();
                h = (h + (e == null ? 0 : e.GetHashCode())) & -1;
            }
            return h;
        }

        public override Iterator<K> Iterator()
        {
            return new HashMapKeyIterator<K, V>(map);
        }

        public override bool Remove(K o)
        {
            if (map.ContainsKey(o))
            {
                map.Remove(o);
                return true;
            }
            return false;
        }

        public override int Size()
        {
            return map.Size();
        }

    }

    class HashMapValueView<K, V> : AbstractCollection<V>
    {
        private readonly HashMapImpl<K, V> map;

        public HashMapValueView(HashMapImpl<K, V> m)
        {
            this.map = m;
        }

        public override bool Contains(V o)
        {
            return map.ContainsValue(o);
        }

        public override Iterator<V> Iterator()
        {
            return new HashMapValueIterator<K, V>(map);
        }

        public override int Size()
        {
            return map.Size();
        }

    }

    class HashMapValueIterator<K, V> : Iterator<V>, Enumeration<V>
    {
        private readonly HashMapImpl<K, V> map;
        private readonly K[] keys;
        private int n;

        public HashMapValueIterator(HashMapImpl<K, V> map)
        {
            this.map = map;
            this.keys = new K[map.Size()];
            map.data.Keys.CopyTo(this.keys, 0);
            this.n = 0;
        }

        public bool HasNext()
        {
            return n < keys.Length;
        }

        public V Next()
        {
            K k = keys[n];
            n++;
            return map.Get(k);
        }

        public void Remove()
        {
            map.Remove(keys[n - 1]);
        }

        public bool HasMoreElements()
        {
            return HasNext();
        }

        public V NextElement()
        {
            return Next();
        }
    }

    class HashMapKeyIterator<K, V> : Iterator<K>, Enumeration<K>
    {
        private readonly HashMapImpl<K, V> map;
        private readonly K[] keys;
        private int n;

        public HashMapKeyIterator(HashMapImpl<K, V> map)
        {
            this.map = map;
            this.keys = new K[map.Size()];
            map.data.Keys.CopyTo(this.keys, 0);
            this.n = 0;
        }

        public bool HasNext()
        {
            return n < keys.Length;
        }

        public K Next()
        {
            K k = keys[n];
            n++;
            return k;
        }

        public void Remove()
        {
            map.Remove(keys[n - 1]);
        }

        public bool HasMoreElements()
        {
            return HasNext();
        }

        public K NextElement()
        {
            return Next();
        }
    }
}
