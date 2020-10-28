namespace java.util
{
    public interface Map<K, V>
    {
        void Clear();
        bool ContainsKey(K key);
        bool ContainsValue(V value);
        bool Equals(object o);
        void ForEach(java.util.function.BiConsumer biconsumer);
        V Get(K key);
        V GetOrDefault(K key, V def);
        int GetHashCode();
        bool IsEmpty();
        Set<K> KeySet();
        V Put(K key, V value);
        void PutAll(Map<K, V> m);
        V Remove(K key);
        int Size();
        Collection<V> Values();
        string ToString();
    }
    public static class Map_Java<K, V>
    {
        public static V GetOrDefault(Map<K, V> @this, K key, V def)
        {
            return @this.ContainsKey(key) ? @this.Get(key) : def;
        }
        public static void ForEach(Map<K, V> @this, java.util.function.BiConsumer biconsumer)
        {
            java.util.Iterator<K> i = @this.KeySet().Iterator();
            while (i.HasNext())
            {
                K key = i.Next();
                V value = @this.Get(key);
                biconsumer.Accept(key, value);
            }
        }
    }
}
