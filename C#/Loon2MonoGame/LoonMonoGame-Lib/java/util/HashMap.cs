using System;
using System.Collections.Generic;
using System.Text;

namespace java.util
{
    public class HashMap<K,V> : HashMapImpl<K, V>
    {
        public HashMap() : base()
        {
        }

        public HashMap(Map<K, V> m) : base(m)
        {
        }
    }
}
