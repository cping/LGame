using java.lang;
using java.util;
using java.util.function;
using System.Collections;
using System.Collections.Generic;

namespace loon.utils
{
    public class IntMap<T> : IArray,IEnumerable<T>, Iterable<T>
    {

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Entry<T>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {

            public long key;
            public T value;

            public Entry(long k, T v)
            {
                key = k;
                value = v;
            }

            public long GetKey()
            {
                return key;
            }

            public T GetValue()
            {
                return value;
            }

            public T SetValue(T newValue)
            {
                T oldValue = value;
                value = newValue;
                return oldValue;
            }

            public override bool Equals(object o)
            {
                if (!(o is Entry<T>))
                {
                    return false;
                }
                Entry<T> e = (Entry<T>)o;
                long k1 = GetKey();
                long k2 = e.GetKey();
                if (k1 == k2)
                {
                    T v1 = GetValue();
                    T v2 = e.GetValue();
                    if ((object)v1 == (object)v2 || (v1 != null && v1.Equals(v2)))
                    {
                        return true;
                    }
                }
                return false;
            }


            public override int GetHashCode()
            {
                return (int)(key ^ (value == null ? 0 : value.GetHashCode()));
            }


            public override string ToString()
            {
                return GetKey() + "=" + GetValue();
            }
        }

        private const int EMPTY = 0;

        private float loader_factor;

        private bool locked;
        private int capacity;
        private long[] keysTable;
        private T[] valuesTable;

        public int size;

        public IntMap() : this(CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public IntMap(int capacity) : this(MathUtils.NextPowerOfTwo(capacity), 0.85f)
        {

        }

        public IntMap(int capacity, float factor)
        {
            this.loader_factor = factor;
            this.Resize(MathUtils.NextPowerOfTwo(capacity));
        }

        public IntMap(IntMap<T> data) : this(data, 0.85f)
        {

        }

        public IntMap(IntMap<T> data, float factor)
        {
            this.loader_factor = factor;
            int neededCapacity = MathUtils.NextPowerOfTwo(data.size);
            if ((float)data.size / neededCapacity > loader_factor)
            {
                neededCapacity *= 2;
            }
            this.size = data.size;
            this.keysTable = data.keysTable;
            this.valuesTable = data.valuesTable;
            Resize(neededCapacity);
        }


        public override bool Equals(object obj)
        {
            if (!(obj is IntMap<T>))
            {
                return false;
            }
            IntMap<T> other = (IntMap<T>)obj;
            if (other.size != size)
            {
                return false;
            }
            int found = 0;
            for (int i = 0; found < size; ++i)
            {
                long kh = keysTable[i];
                if ((int)kh != 0)
                {
                    int j = other.Find(kh);
                    if (j < 0 || !valuesTable[i].Equals(other.valuesTable[j]))
                    {
                        return false;
                    }
                    ++found;
                }
            }
            return true;
        }

        public int[] Keys()
        {
            int[] keys = new int[size];
            int found = 0;
            for (int i = 0; found < size; ++i)
            {
                long kh = keysTable[i];
                if ((int)kh != 0)
                {
                    keys[found++] = (int)(kh >> 32);
                }
            }
            return keys;
        }

        public Iterable<T> Values()
        {
            return new IntMapIterator<T>(this);
        }

        public int Size()
        {
            return size;
        }

        public int Capacity()
        {
            return capacity;
        }

        public bool ContainsKey(object key)
        {
            if (key == null)
            {
                return false;
            }
            return ContainsKey(key.GetHashCode());
        }

        public bool ContainsKey(int key)
        {
            return Find(CollectionUtils.GetHashKey(key)) >= 0;
        }

        public T Get(object key)
        {
            if (key == null)
            {
                return default;
            }
            return Get(key.GetHashCode(), default);
        }

        public T Get(object key, T defaultValue)
        {
            if (key == null)
            {
                return default;
            }
            return Get(key.GetHashCode(), defaultValue);
        }

        public T Get(int key)
        {
            return Get(key, default);
        }

        public T Get(int key, T defaultValue)
        {
            int index = Find(CollectionUtils.GetHashKey(key));
            if (index >= 0)
            {
                return valuesTable[index];
            }
            return defaultValue;
        }

        public Entry<T>[] GetEntrys()
        {
            Entry<T>[] entrys = new Entry<T>[size];
            int found = 0;
            for (int i = 0; i < capacity; i++)
            {
                long key = keysTable[i];
                if (key != EMPTY)
                {
                    entrys[found] = new Entry<T>(key, valuesTable[i]);
                    found++;
                }
            }
            return entrys;
        }


        public void Clear()
        {
            if (locked)
            {
                return;
            }
            for (int i = 0; i < capacity; ++i)
            {
                keysTable[i] = 0;
                valuesTable[i] = default;
            }
            size = 0;
        }

        public void LockArray()
        {
            locked = true;
        }

        public void UnlockArray()
        {
            locked = false;
        }

        public void Put(object key, T value)
        {
            if (key == null)
            {
                return;
            }
            Put(key.GetHashCode(), value);
        }

        public void Put(int key, T value)
        {
            if (locked)
            {
                return;
            }
            if (value == null)
            {
                return;
            }
            if ((float)size / capacity > loader_factor)
            {
                Resize(capacity * 2);
            }
            Put(CollectionUtils.GetHashKey(key), value);
        }

        public T Remove(int key)
        {
            if (locked)
            {
                return default;
            }
            int index = Find(CollectionUtils.GetHashKey(key));
            if (index < 0)
            {
                return default;
            }
            for (int i = 0; i < capacity; ++i)
            {
                int curr = (index + i) & (capacity - 1);
                int next = (index + i + 1) & (capacity - 1);

                int h = (int)keysTable[next];
                if (h == 0 || FindIndex(h, next) == 0)
                {
                    T data = valuesTable[curr];
                    keysTable[curr] = 0;
                    valuesTable[curr] = default;
                    --size;
                    return data;
                }
                keysTable[curr] = keysTable[next];
                valuesTable[curr] = valuesTable[next];
            }
            return default;
        }

        private void Put(long keyHash, T value)
        {
            int startIndex = (int)keyHash & (capacity - 1);
            int probe = 0;
            for (int i = 0; i < capacity; ++i, ++probe)
            {
                int index = (startIndex + i) & (capacity - 1);
                long kh = keysTable[index];
                int h = (int)kh;
                if (h == 0)
                {
                    keysTable[index] = keyHash;
                    valuesTable[index] = value;
                    ++size;
                    return;
                }
                if (kh == keyHash)
                {
                    valuesTable[index] = value;
                    return;
                }
                int d = FindIndex(h, index);
                if (probe > d)
                {
                    probe = d;
                    long tempHK = keysTable[index];
                    T tempVal = valuesTable[index];
                    keysTable[index] = keyHash;
                    valuesTable[index] = value;
                    keyHash = tempHK;
                    value = tempVal;
                }
            }
        }

        private int Find(long keyHash)
        {
            int startIndex = (int)keyHash & (capacity - 1);
            for (int i = 0; i < capacity; ++i)
            {
                int index = (startIndex + i) & (capacity - 1);
                long kh = keysTable[index];
                if (kh == keyHash)
                {
                    return index;
                }
                int h = (int)kh;
                if (h == 0)
                {
                    return -1;
                }
                int d = FindIndex(h, index);
                if (i > d)
                {
                    return -1;
                }
            }
            return -1;
        }

        private void Resize(int newCapacity)
        {
            if (newCapacity < size)
            {
                return;
            }

            int oldSize = size;
            long[] oldHashKeys = keysTable;
            T[] oldValues = valuesTable;

            size = 0;
            capacity = newCapacity;
            keysTable = new long[newCapacity];
            valuesTable = new T[newCapacity];

            int found = 0;
            for (int i = 0; found < oldSize; ++i)
            {
                long kh = oldHashKeys[i];
                if ((int)kh != 0)
                {
                    Put(kh, oldValues[i]);
                    ++found;
                }
            }
        }

        private int FindIndex(int hash, int indexStored)
        {
            int startIndex = hash & (capacity - 1);
            if (startIndex <= indexStored)
            {
                return indexStored - startIndex;
            }
            return indexStored + (capacity - startIndex);
        }


        public bool IsEmpty()
        {
            return valuesTable == null || size == 0;
        }


        public Iterator<T> Iterator()
        {
            return new IntMapIterator<T>(this);
        }

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class IntMapIterator<T> : LIterator<T>, Iterable<T>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {

            int _index = 0;
            int _found = 0;
            readonly IntMap<T> _map;

            internal IntMapIterator(IntMap<T> map)
            {
                this._map = map;
            }


            public bool HasNext()
            {
                return _found < _map.size;
            }


            public T Next()
            {
                for (; _index < _map.capacity; ++_index)
                {
                    T value = _map.valuesTable[_index];
                    if (value != null)
                    {
                        ++_index;
                        ++_found;
                        return value;
                    }
                }
                return default;
            }


            public void Remove()
            {
            }


            public Iterator<T> Iterator()
            {
                return this;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<T>.ForEach(this, consumer);
            }
        }


        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = size - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + (int)keysTable[i];
                hashCode = 31 * hashCode + (valuesTable[i] == null ? 0 : valuesTable[i].GetHashCode());
            }
            return hashCode;
        }

        public void ForEach(Consumer consumer)
        {
            Iterable_Java<T>.ForEach(this, consumer);
        }

        public IEnumerator<T> GetEnumerator()
        {
            return new IEnumeratorAdapter<T>(this.Iterator());
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public override string ToString()
        {
            if (size == 0)
            {
                return "[]";
            }
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append('[');
            long[] keyTable = this.keysTable;
            T[] valueTable = this.valuesTable;
            int i = keyTable.Length;
            while (i-- > 0)
            {
                long key = keyTable[i];
                if (key == EMPTY)
                    continue;
                buffer.Append(key);
                buffer.Append('=');
                buffer.Append(valueTable[i]);
                break;
            }
            while (i-- > 0)
            {
                long key = keyTable[i];
                if (key == EMPTY)
                    continue;
                buffer.Append(", ");
                buffer.Append(key);
                buffer.Append('=');
                buffer.Append(valueTable[i]);
            }
            buffer.Append(']');
            return buffer.ToString();
        }

    }


}
