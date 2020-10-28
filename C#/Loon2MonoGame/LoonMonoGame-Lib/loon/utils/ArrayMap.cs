using java.lang;

namespace loon.utils
{
    public class ArrayMap
    {

        public class Entry
        {

            internal int index;

            internal int hashCode;

            public object key;

            public object value;

            internal Entry next;

            protected Entry(int hashCode, object key, object value, Entry next) : this(-1, hashCode, key, value, next)
            {

            }

            public Entry(int index, int hc, object key, object value, Entry next)
            {
                this.index = index;
                this.hashCode = hc;
                this.key = key;
                this.value = value;
                this.next = next;
            }

            public object GetKey()
            {
                return key;
            }

            public object GetValue()
            {
                return value;
            }

            public object SetValue(object value)
            {
                object oldValue = value;
                this.value = value;
                return oldValue;
            }

            public int GetIndex()
            {
                return this.index;
            }

            internal void Clear()
            {
                key = null;
                value = null;
                next = null;
            }

            public override bool Equals(object o)
            {
                if (this == o)
                {
                    return true;
                }
                Entry e = (Entry)o;
                return (key != null ? key.Equals(e.key) : e.key == null)
                        && (value != null ? value.Equals(e.value) : e.value == null);
            }

            public override int GetHashCode()
            {
                return hashCode;
            }

            public override string ToString()
            {
                return key + "=" + value;
            }

        }


        private int threshold;

        private Entry[] keysTable;

        private Entry[] valuesTable;

        private int size = 0;

        private float loadFactor;

        private int removed = 0;

        public ArrayMap() : this(CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public ArrayMap(int initialCapacity) : this(initialCapacity, 0.85f)
        {

        }

        public ArrayMap(ArrayMap map) : this()
        {

            PutAll(map);
        }

        public ArrayMap(int initialCapacity, float factor)
        {
            if (initialCapacity <= 0)
            {
                initialCapacity = CollectionUtils.INITIAL_CAPACITY;
            }
            this.keysTable = new Entry[initialCapacity];
            this.valuesTable = new Entry[initialCapacity];
            this.threshold = (int)(initialCapacity * factor);
            this.loadFactor = factor;
        }


        public int Size()
        {
            return size;
        }

        public bool IsEmpty()
        {
            return size == 0;
        }

        public bool ContainsValue(object value)
        {
            return IndexOf(value) >= 0;
        }

        protected int IndexOf(Entry entry)
        {
            if (entry != null)
            {
                Entry value;
                int start = 0;
                int len = size - 1;
                for (; start <= len;)
                {
                    int mid = start + (len - start) / 2;
                    value = valuesTable[mid];
                    if (entry.index < value.index)
                    {
                        len = mid - 1;
                    }
                    else if (entry.index > value.index)
                    {
                        start = mid + 1;
                    }
                    else
                    {
                        if (entry == value)
                        {
                            return mid;
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                for (int i = 0; i < size; i++)
                {
                    value = valuesTable[i];
                    if (value == entry)
                    {
                        return i;
                    }
                }
            }
            else
            {
                for (int i = 0; i < size; i++)
                {
                    if (valuesTable[i] == null)
                    {
                        return i;
                    }
                }
            }
            return -1;
        }

        public int IndexOf(object value)
        {
            if (value != null)
            {
                object data = null;
                for (int i = 0; i < size; i++)
                {
                    data = valuesTable[i].value;
                    if (data == value || data.Equals(value))
                    {
                        return i;
                    }
                }
            }
            else
            {
                for (int i = 0; i < size; i++)
                {
                    if (valuesTable[i].value == null)
                    {
                        return i;
                    }
                }
            }
            return -1;
        }

        public bool ContainsKey(object key)
        {
            Entry[] table = keysTable;
            if (key != null)
            {
                int hashCode = CollectionUtils.GetLimitHash(key.GetHashCode());
                int index = (hashCode & 0x7FFFFFFF) % table.Length;
                for (Entry e = table[index]; e != null; e = e.next)
                {
                    if (e.hashCode == hashCode && key.Equals(e.key))
                    {
                        return true;
                    }
                }
            }
            else
            {
                for (Entry e = table[0]; e != null; e = e.next)
                {
                    if (e.key == null)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public object Get(object key)
        {
            Entry[] table = keysTable;
            if (key != null)
            {
                int hashCode = CollectionUtils.GetLimitHash(key.GetHashCode());
                int index = (hashCode & 0x7FFFFFFF) % table.Length;
                for (Entry e = table[index]; e != null; e = e.next)
                {
                    if (e.hashCode == hashCode && key.Equals(e.key))
                    {
                        return e.value;
                    }
                }
            }
            else
            {
                for (Entry e = table[0]; e != null; e = e.next)
                {
                    if (e.key == null)
                    {
                        return e.value;
                    }
                }
            }
            return null;
        }

        public object GetValue(object key)
        {
            return Get(key);
        }

        public object Get(int index)
        {
            if (index < 0 || index >= size)
            {
                return null;
            }
            Entry entry = GetEntry(index);
            if (entry != null)
            {
                return entry.value;
            }
            return null;
        }

        public object GetKey(int index)
        {
            if (index < 0 || index >= size)
            {
                return null;
            }
            Entry entry = GetEntry(index);
            if (entry != null)
            {
                return entry.key;
            }
            return null;
        }

        public Entry GetEntry(int index)
        {
            if (index >= size)
            {
                throw new LSysException("Index:" + index + ", Size:" + size);
            }
            return valuesTable[index];
        }

        public void PutAll(ArrayMap map)
        {
            EnsureCapacity();
            for (int i = 0; i < map.size; i++)
            {
                Entry e = map.GetEntry(i);
                Put(e.key, e.value);
            }
        }

        public object Put(object key, object value)
        {
            int hashCode = 0;
            int index = 0;
            Entry e;
            if (key != null)
            {
                hashCode = CollectionUtils.GetLimitHash(key.GetHashCode());
                index = (hashCode & 0x7FFFFFFF) % keysTable.Length;
                for (e = keysTable[index]; e != null; e = e.next)
                {
                    if ((e.hashCode == hashCode) && key.Equals(e.key))
                    {
                        return SwapValue(e, value);
                    }
                }
            }
            else
            {
                for (e = keysTable[0]; e != null; e = e.next)
                {
                    if (e.key == null)
                    {
                        return SwapValue(e, value);
                    }
                }
            }
            EnsureCapacity();
            index = (hashCode & 0x7FFFFFFF) % keysTable.Length;
            e = null;
            if (removed < 0)
            {
                removed = 0;
            }
            if (removed == 0)
            {
                e = new Entry(size, hashCode, key, value, keysTable[index]);
            }
            else
            {
                e = new Entry(removed + size, hashCode, key, value, keysTable[index]);
            }
            keysTable[index] = e;
            valuesTable[size++] = e;
            return null;
        }

        public void Set(int index, object value)
        {
            GetEntry(index).SetValue(value);
        }

        public object Remove(object key)
        {
            Entry e = RemoveMap(key);
            if (e != null)
            {
                object value = e.value;
                int index = IndexOf(e);
                RemoveList(index);
                e.Clear();
                return value;
            }
            return null;
        }

        public object Remove(int index)
        {
            Entry e = RemoveList(index);
            object value = e.value;
            RemoveMap(e.key);
            e.Clear();
            return value;
        }

        public void Clear()
        {
            int length = keysTable.Length;
            for (int i = 0; i < length; i++)
            {
                keysTable[i] = null;
                valuesTable[i] = null;
            }
            size = 0;
            removed = 0;
        }

        public int GetRemoved()
        {
            return removed;
        }

        public Entry[] ToEntrys()
        {
            Entry[] lists = CollectionUtils.CopyOf(valuesTable, size);
            return lists;
        }

        public object[] ToArray()
        {
            object[] array = new object[size];
            for (int i = 0; i < size; i++)
            {
                array[i] = Get(i);
            }
            return array;
        }

        public override bool Equals(object o)
        {
            if (!(o is ArrayMap))
            {
                return false;
            }
            ArrayMap e = (ArrayMap)o;
            if (size != e.size)
            {
                return false;
            }
            for (int i = 0; i < size; i++)
            {
                if (!valuesTable[i].Equals(e.valuesTable[i]))
                {
                    return false;
                }
            }
            return true;
        }

        public ArrayMap Cpy()
        {
            ArrayMap copy = new ArrayMap();
            copy.threshold = threshold;
            copy.keysTable = keysTable;
            copy.valuesTable = valuesTable;
            copy.size = size;
            return copy;
        }

        private Entry RemoveMap(object key)
        {
            int hashCode = 0;
            int index = 0;
            if (key != null)
            {
                hashCode = CollectionUtils.GetLimitHash(key.GetHashCode());
                index = (hashCode & 0x7FFFFFFF) % keysTable.Length;
                for (Entry e = keysTable[index], prev = null; e != null; prev = e, e = e.next)
                {
                    if ((e.hashCode == hashCode) && key.Equals(e.key))
                    {
                        if (prev != null)
                        {
                            prev.next = e.next;
                        }
                        else
                        {
                            keysTable[index] = e.next;
                        }
                        return e;
                    }
                }
            }
            else
            {
                for (Entry e = keysTable[index], prev = null; e != null; prev = e, e = e.next)
                {
                    if ((e.hashCode == hashCode) && e.key == null)
                    {
                        if (prev != null)
                        {
                            prev.next = e.next;
                        }
                        else
                        {
                            keysTable[index] = e.next;
                        }
                        return e;
                    }
                }
            }
            return null;
        }

        private Entry RemoveList(int index)
        {
            Entry e = valuesTable[index];
            int numMoved = size - index - 1;
            if (numMoved > 0)
            {
                JavaSystem.Arraycopy(valuesTable, index + 1, valuesTable, index, numMoved);
            }
            valuesTable[--size] = null;
            removed++;
            return e;
        }

        private void EnsureCapacity()
        {
            if (size >= threshold)
            {
                Entry[] oldTable = valuesTable;
                int newCapacity = oldTable.Length * 2 + 1;
                Entry[] newMapTable = new Entry[newCapacity];
                Entry[] newListTable = new Entry[newCapacity];
                threshold = (int)(newCapacity * loadFactor);
                JavaSystem.Arraycopy(oldTable, 0, newListTable, 0, size);
                for (int i = 0; i < size; i++)
                {
                    Entry old = oldTable[i];
                    int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
                    Entry e = old;
                    old = old.next;
                    e.next = newMapTable[index];
                    newMapTable[index] = e;
                    newListTable[i].index = i;
                }
                keysTable = newMapTable;
                valuesTable = newListTable;
                removed = 0;
            }
        }

        private object SwapValue(Entry entry, object value)
        {
            object old = entry.value;
            entry.value = value;
            return old;
        }

        public void Reverse()
        {
            for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                Entry tempKey = keysTable[i];
                keysTable[i] = keysTable[ii];
                keysTable[ii] = tempKey;
                Entry tempValue = valuesTable[i];
                valuesTable[i] = valuesTable[ii];
                valuesTable[ii] = tempValue;
            }
        }

        public void Shuffle()
        {
            for (int i = size - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                Entry tempKey = keysTable[i];
                keysTable[i] = keysTable[ii];
                keysTable[ii] = tempKey;
                Entry tempValue = valuesTable[i];
                valuesTable[i] = valuesTable[ii];
                valuesTable[ii] = tempValue;
            }
        }

        public void truncate(int newSize)
        {
            if (size <= newSize)
            {
                return;

            }
            for (int i = newSize; i < size; i++)
            {
                keysTable[i] = null;
                valuesTable[i] = null;
            }
            size = newSize;
        }


        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = size - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + (keysTable[i] == null ? 0 : keysTable[i].GetHashCode());
                hashCode = 31 * hashCode + (valuesTable[i] == null ? 0 : valuesTable[i].GetHashCode());
            }
            return hashCode;
        }

        public override string ToString()
        {
            return ToString(',');
        }

        public string ToString(char split)
        {
            if (size == 0)
            {
                return "[]";
            }
            Entry[] values = this.valuesTable;
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append('[');
            for (int i = 0; i < size; i++)
            {
                object key = values[i].key;
                object value = values[i].value;
                buffer.Append(key == this ? "(this Map)" : key);
                buffer.Append('=');
                buffer.Append(value == this ? "(this Map)" : value);
                if (i < size - 1)
                {
                    buffer.Append(split).Append(' ');
                }
            }
            buffer.Append(']');
            return buffer.ToString();
        }

    }
}
