using System;
using Loon.Java;
using System.Runtime.CompilerServices;
using System.Collections.Generic;
using Loon.Java.Collections;

namespace Loon.Utils.Collection
{

    public class ArrayMap
    {

        private const float LOAD_FACTOR = 0.75f;

        private int threshold;

        private ArrayMap.Entry[] keyTables;

        private ArrayMap.Entry[] valueTables;

        private int size;

        public ArrayMap():this(CollectionUtils.INITIAL_CAPACITY)
        {
            
        }

        public ArrayMap(int initialCapacity)
        {
            this.size = 0;
            if (initialCapacity <= 0)
            {
                initialCapacity = CollectionUtils.INITIAL_CAPACITY;
            }
            keyTables = new ArrayMap.Entry[initialCapacity];
            valueTables = new ArrayMap.Entry[initialCapacity];
            threshold = (int)(initialCapacity * LOAD_FACTOR);
        }

        public int Size()
        {
            return size;
        }

        public bool IsEmpty()
        {
            return size == 0;
        }

        public override int GetHashCode()
        {
            return System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(this);
        }

        public bool ContainsValue(object value_ren)
        {
            return IndexOf(value_ren) >= 0;
        }

        public int IndexOf(object value_ren)
        {
            if (value_ren != null)
            {
                for (int i = 0; i < size; i++)
                {
                    if (value_ren.Equals(valueTables[i].value_ren))
                    {
                        return i;
                    }
                }
            }
            else
            {
                for (int i_0 = 0; i_0 < size; i_0++)
                {
                    if (valueTables[i_0].value_ren == null)
                    {
                        return i_0;
                    }
                }
            }
            return -1;
        }

        public bool ContainsKey(object key)
        {
            ArrayMap.Entry[] table = keyTables;
            if (key != null)
            {
                int hashCode = key.GetHashCode();
                int index = (hashCode & 0x7FFFFFFF) % table.Length;
                for (ArrayMap.Entry e = table[index]; e != null; e = e.next)
                {
                    if (e.hashCode == hashCode && key.Equals(e.key))
                    {
                        return true;
                    }
                }
            }
            else
            {
                for (ArrayMap.Entry e_0 = table[0]; e_0 != null; e_0 = e_0.next)
                {
                    if (e_0.key == null)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public object Get(object key)
        {
            ArrayMap.Entry[] table = keyTables;
            if (key != null)
            {
                int hashCode = key.GetHashCode();
                int index = (hashCode & 0x7FFFFFFF) % table.Length;
                for (ArrayMap.Entry e = table[index]; e != null; e = e.next)
                {
                    if (e.hashCode == hashCode && key.Equals(e.key))
                    {
                        return e.value_ren;
                    }
                }
            }
            else
            {
                for (ArrayMap.Entry e_0 = table[0]; e_0 != null; e_0 = e_0.next)
                {
                    if (e_0.key == null)
                    {
                        return e_0.value_ren;
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
            ArrayMap.Entry entry = GetEntry(index);
            if (entry != null)
            {
                return entry.value_ren;
            }
            return null;
        }

        public object GetKey(int index)
        {
            if (index < 0 || index >= size)
            {
                return null;
            }
            ArrayMap.Entry entry = GetEntry(index);
            if (entry != null)
            {
                return entry.key;
            }
            return null;
        }

        public ArrayMap.Entry GetEntry(int index)
        {
            if (index >= size)
            {
                throw new IndexOutOfRangeException("Index:" + index + ", Size:"
                                    + size.ToString());
            }
            return valueTables[index];
        }

        public object Put(object key, object value_ren)
        {
            int hashCode = 0;
            int index = 0;
            if (key != null)
            {
                hashCode = key.GetHashCode();
                index = (hashCode & 0x7FFFFFFF) % keyTables.Length;
                for (ArrayMap.Entry e = keyTables[index]; e != null; e = e.next)
                {
                    if ((e.hashCode == hashCode) && key.Equals(e.key))
                    {
                        return SwapValue(e, value_ren);
                    }
                }
            }
            else
            {
                for (ArrayMap.Entry e_0 = keyTables[0]; e_0 != null; e_0 = e_0.next)
                {
                    if (e_0.key == null)
                    {
                        return SwapValue(e_0, value_ren);
                    }
                }
            }
            EnsureCapacity();
            index = (hashCode & 0x7FFFFFFF) % keyTables.Length;
            ArrayMap.Entry e_1 = new ArrayMap.Entry(hashCode, key, value_ren, keyTables[index]);
            keyTables[index] = e_1;
            valueTables[size++] = e_1;
            return null;
        }

        public void Set(int index, object value_ren)
        {
            GetEntry(index).SetValue(value_ren);
        }

        public object Remove(object key)
        {
            ArrayMap.Entry e = RemoveMap(key);
            if (e != null)
            {
                object value_ren = e.value_ren;
                RemoveList(IndexOf(e));
                e.Clear();
                return value_ren;
            }
            return null;
        }

        public object Remove(int index)
        {
            ArrayMap.Entry e = RemoveList(index);
            object value_ren = e.value_ren;
            RemoveMap(e.key);
            e.value_ren = null;
            return value_ren;
        }

        public void Clear()
        {
            int length = keyTables.Length;
            for (int i = 0; i < length; i++)
            {
                keyTables[i] = null;
                valueTables[i] = null;
            }
            size = 0;
        }

        public ArrayMap.Entry[] ToEntrys()
        {
            ArrayMap.Entry[] lists = (ArrayMap.Entry[])CollectionUtils.CopyOf(valueTables, size);
            return lists;
        }

        public List<Entry> ToList()
        {
            List<Entry> lists = new List<ArrayMap.Entry>(size);
            for (int i = 0; i < size; i++)
            {
                lists.Add(valueTables[i]);
            }
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

        public sealed override bool Equals(object o)
        {
            if (!GetType().IsInstanceOfType(o))
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
                if (!valueTables[i].Equals(e.valueTables[i]))
                {
                    return false;
                }
            }
            return true;
        }

        public virtual object Clone()
        {
            ArrayMap copy = new ArrayMap();
            copy.threshold = threshold;
            copy.keyTables = keyTables;
            copy.valueTables = valueTables;
            copy.size = size;
            return copy;
        }

        private int IndexOf(ArrayMap.Entry entry)
        {
            for (int i = 0; i < size; i++)
            {
                if (valueTables[i] == entry)
                {
                    return i;
                }
            }
            return -1;
        }

        private ArrayMap.Entry RemoveMap(object key)
        {
            int hashCode = 0;
            int index = 0;

            if (key != null)
            {
                hashCode = key.GetHashCode();
                index = (hashCode & 0x7FFFFFFF) % keyTables.Length;
                for (ArrayMap.Entry e = keyTables[index], prev = null; e != null; prev = e, e = e.next)
                {
                    if ((e.hashCode == hashCode) && key.Equals(e.key))
                    {
                        if (prev != null)
                        {
                            prev.next = e.next;
                        }
                        else
                        {
                            keyTables[index] = e.next;
                        }
                        return e;
                    }
                }
            }
            else
            {
                for (ArrayMap.Entry e_0 = keyTables[index], prev_1 = null; e_0 != null; prev_1 = e_0, e_0 = e_0.next)
                {
                    if ((e_0.hashCode == hashCode) && e_0.key == null)
                    {
                        if (prev_1 != null)
                        {
                            prev_1.next = e_0.next;
                        }
                        else
                        {
                            keyTables[index] = e_0.next;
                        }
                        return e_0;
                    }
                }
            }
            return null;
        }

        private ArrayMap.Entry RemoveList(int index)
        {
            ArrayMap.Entry e = valueTables[index];
            int numMoved = size - index - 1;
            if (numMoved > 0)
            {
                System.Array.Copy((Array)(valueTables), index + 1, (Array)(valueTables), index, numMoved);
            }
            valueTables[--size] = null;
            return e;
        }

        private void EnsureCapacity()
        {
            if (size >= threshold)
            {
                ArrayMap.Entry[] oldTable = valueTables;
                int newCapacity = oldTable.Length * 2 + 1;
                ArrayMap.Entry[] newMapTable = new ArrayMap.Entry[newCapacity];
                ArrayMap.Entry[] newListTable = new ArrayMap.Entry[newCapacity];
                threshold = (int)(newCapacity * LOAD_FACTOR);
                System.Array.Copy((Array)(oldTable), 0, (Array)(newListTable), 0, size);
                for (int i = 0; i < size; i++)
                {
                    ArrayMap.Entry old = oldTable[i];
                    int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
                    ArrayMap.Entry e = old;
                    old = old.next;
                    e.next = newMapTable[index];
                    newMapTable[index] = e;
                }
                keyTables = newMapTable;
                valueTables = newListTable;
            }
        }

        private object SwapValue(ArrayMap.Entry entry, object value_ren)
        {
            object old = entry.value_ren;
            entry.value_ren = value_ren;
            return old;
        }

        public void Reverse()
        {
            for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                ArrayMap.Entry tempKey = keyTables[i];
                keyTables[i] = keyTables[ii];
                keyTables[ii] = tempKey;
                ArrayMap.Entry tempValue = valueTables[i];
                valueTables[i] = valueTables[ii];
                valueTables[ii] = tempValue;
            }
        }

        public void Shuffle()
        {
            for (int i = size - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                ArrayMap.Entry tempKey = keyTables[i];
                keyTables[i] = keyTables[ii];
                keyTables[ii] = tempKey;
                ArrayMap.Entry tempValue = valueTables[i];
                valueTables[i] = valueTables[ii];
                valueTables[ii] = tempValue;
            }
        }

        public void Truncate(int newSize)
        {
            if (size <= newSize)
            {
                return;

            }
            for (int i = newSize; i < size; i++)
            {
                keyTables[i] = null;
                valueTables[i] = null;
            }
            size = newSize;
        }

        public class Entry
        {

            internal int hashCode;

            internal object key;

            internal object value_ren;

            internal ArrayMap.Entry next;

            public Entry(int hashCode_0, object key_1, object value_ren,
                    ArrayMap.Entry next_2)
            {

                this.hashCode = hashCode_0;
                this.key = key_1;
                this.value_ren = value_ren;
                this.next = next_2;
            }

            public object GetKey()
            {
                return key;
            }

            public object GetValue()
            {
                return value_ren;
            }

            public object SetValue(object value_ren)
            {
                object oldValue = value_ren;
                this.value_ren = value_ren;
                return oldValue;
            }

            public void Clear()
            {
                key = null;
                value_ren = null;
                next = null;
            }

            public override bool Equals(object o)
            {
                if ((object)this == o)
                {
                    return true;
                }
                ArrayMap.Entry e = (ArrayMap.Entry)o;
                return ((key != null) ? key.Equals(e.key) : e.key == null)
                        && ((value_ren != null) ? value_ren.Equals(e.value_ren) : e.value_ren == null);
            }

            public override int GetHashCode()
            {
                return hashCode;
            }

            public override string ToString()
            {
                return key + "=" + value_ren;
            }

        }
    }

}
