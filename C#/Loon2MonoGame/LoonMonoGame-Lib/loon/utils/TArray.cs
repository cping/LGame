using java.lang;
using java.util;
using java.util.function;

namespace loon.utils
{

    public class TArray<T> : Iterable<T>, IArray
    {


        public class ArrayIterable<N> : Iterable<N>
        {

            private readonly TArray<N> array;
            private readonly bool allowRemove;
            private ArrayIterator<N> iterator1, iterator2;

            public ArrayIterable(TArray<N> array) : this(array, true)
            {

            }

            public ArrayIterable(TArray<N> array, bool allowRemove)
            {
                this.array = array;
                this.allowRemove = allowRemove;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<N>.ForEach(this, consumer);
            }

            public Iterator<N> Iterator()
            {
                if (iterator1 == null)
                {
                    iterator1 = new ArrayIterator<N>(array, allowRemove);
                    iterator2 = new ArrayIterator<N>(array, allowRemove);
                }
                if (!iterator1.valid)
                {
                    iterator1.index = 0;
                    iterator1.valid = true;
                    iterator2.valid = false;
                    return iterator1;
                }
                iterator2.index = 0;
                iterator2.valid = true;
                iterator1.valid = false;
                return iterator2;
            }
        }

        public class ArrayIterator<N> : LIterator<N>, Iterable<N>
        {

            private readonly TArray<N> arrays;
            private readonly bool allowRemove;
            internal int index;
            internal bool valid = true;

            public ArrayIterator(TArray<N> array) : this(array, true)
            {

            }

            public ArrayIterator(TArray<N> array, bool allowRemove)
            {
                this.arrays = array;
                this.allowRemove = allowRemove;
            }


            public bool HasNext()
            {
                if (!valid)
                {
                    throw new LSysException("iterator() cannot be used nested.");
                }
                return index < arrays.size;
            }


            public N Next()
            {
                if (index >= arrays.size)
                {
                    return default;
                }
                if (!valid)
                {
                    throw new LSysException("iterator() cannot be used nested.");
                }
                return arrays.items[index++];
            }

            public void Remove()
            {
                if (!allowRemove)
                {
                    throw new LSysException("Remove not allowed.");
                }
                index--;
                arrays.RemoveIndex(index);
            }

            public void Reset()
            {
                index = 0;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<N>.ForEach(this, consumer);
            }

            public Iterator<N> Iterator()
            {
                return this;
            }
        }

        public static TArray<T> At(int capacity)
        {
            return new TArray<T>(capacity);
        }

        public static TArray<T> At(TArray<T> array)
        {
            return new TArray<T>(array);
        }

        public static TArray<T> At()
        {
            return At(0);
        }
        public static TArray<T> With(params T[] array)
        {
            return new TArray<T>(array);
        }

        public void ForEach(Consumer consumer)
        {
            Iterable_Java<T>.ForEach(this, consumer);
        }

        public T[] items;

        public int size;

        public bool ordered;

        public TArray() : this(true, CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public TArray(int capacity) : this(true, capacity)
        {

        }

        public TArray(bool ordered, int capacity)
        {
            this.ordered = ordered;
            items = new T[capacity];
        }

        public TArray(TArray<T> array) : this(array.ordered, array.size)
        {

            size = array.size;
            JavaSystem.Arraycopy(array.items, 0, items, 0, size);
        }

        public TArray(params T[] array) : this(true, array, 0, array.Length)
        {

        }

        public TArray(bool ordered, T[] array, int start, int count) : this(ordered, count)
        {
            size = count;
            JavaSystem.Arraycopy(array, start, items, 0, size);
        }

        public TArray(Array<T> vals) : this()
        {

            for (; vals.HashNext();)
            {
                Add(vals.Next());
            }
            vals.StopNext();
        }


        public bool Add(T value)
        {
            T[] items = this.items;
            if (size == items.Length)
            {
                items = Resize(MathUtils.Max(8, (int)(size * 1.75f)));
            }
            items[size++] = value;
            return true;
        }

        public void AddAll(TArray<T> array)
        {
            AddAll(array, 0, array.size);
        }

        public void AddAll(TArray<T> array, int start, int count)
        {
            if (start + count > array.size)
            {
                throw new LSysException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
            }
            AddAll(array.items, start, count);
        }

        public void AddAll(params T[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(T[] array, int start, int count)
        {
            T[] items = this.items;
            int sizeNeeded = size + count;
            if (sizeNeeded > items.Length)
            {
                items = Resize(MathUtils.Max(8, (int)(sizeNeeded * 1.75f)));
            }
            JavaSystem.Arraycopy(array, start, items, size, count);
            size += count;
        }

        public T Get(int index)
        {
            if (index >= size)
                throw new LSysException("index can't be >= size: " + index + " >= " + size);
            return items[index];
        }

        public void Set(int index, T value)
        {
            if (index >= size)
                throw new LSysException("index can't be >= size: " + index + " >= " + size);
            items[index] = value;
        }

        public void Insert(int index, T value)
        {
            if (index > size)
                throw new LSysException("index can't be > size: " + index + " > " + size);
            T[] items = this.items;
            if (size == items.Length)
                items = Resize(MathUtils.Max(8, (int)(size * 1.75f)));
            if (ordered)
                JavaSystem.Arraycopy(items, index, items, index + 1, size - index);
            else
                items[size] = items[index];
            size++;
            items[index] = value;
        }

        public void Swap(int first, int second)
        {
            if (first >= size)
                throw new LSysException("first can't be >= size: " + first + " >= " + size);
            if (second >= size)
                throw new LSysException("second can't be >= size: " + second + " >= " + size);
            T[] items = this.items;
            T firstValue = items[first];
            items[first] = items[second];
            items[second] = firstValue;
        }

        public bool Contains(T value)
        {
            return Contains(value, false);
        }

        public bool Contains(T value, bool identity)
        {
            T[] items = this.items;
            int i = size - 1;
            if (identity || value == null)
            {
                while (i >= 0)
                    if ((object)items[i--] == (object)value)
                        return true;
            }
            else
            {
                while (i >= 0)
                    if (value.Equals(items[i--]))
                        return true;
            }
            return false;
        }

        public int IndexOf(T value)
        {
            return IndexOf(value, false);
        }

        public int IndexOf(T value, bool identity)
        {
            T[] items = this.items;
            if (identity || value == null)
            {
                for (int i = 0, n = size; i < n; i++)
                    if ((object)items[i] == (object)value)
                        return i;
            }
            else
            {
                for (int i = 0, n = size; i < n; i++)
                    if (value.Equals(items[i]))
                        return i;
            }
            return -1;
        }

        public int LastIndexOf(T value, bool identity)
        {
            T[] items = this.items;
            if (identity || value == null)
            {
                for (int i = size - 1; i >= 0; i--)
                    if ((object)items[i] == (object)value)
                        return i;
            }
            else
            {
                for (int i = size - 1; i >= 0; i--)
                    if (value.Equals(items[i]))
                        return i;
            }
            return -1;
        }

        public bool RemoveValue(T value)
        {
            T[] items = this.items;
            for (int i = 0, n = size; i < n; i++)
            {
                if ((object)items[i] == (object)value || value.Equals(items[i]))
                {
                    RemoveIndex(i);
                    return true;
                }
            }
            return false;
        }

        public bool RemoveValue(T value, bool identity)
        {
            T[] items = this.items;
            if (identity || value == null)
            {
                for (int i = 0, n = size; i < n; i++)
                {
                    if ((object)items[i] == (object)value)
                    {
                        RemoveIndex(i);
                        return true;
                    }
                }
            }
            else
            {
                for (int i = 0, n = size; i < n; i++)
                {
                    if (value.Equals(items[i]))
                    {
                        RemoveIndex(i);
                        return true;
                    }
                }
            }
            return false;
        }

        public T RemoveIndex(int index)
        {
            if (index >= size)
                throw new LSysException("index can't be >= size: " + index + " >= " + size);
            T[] items = this.items;
            T value = (T)items[index];
            size--;
            if (ordered)
                JavaSystem.Arraycopy(items, index + 1, items, index, size - index);
            else
                items[index] = items[size];
            items[size] = default;
            return value;
        }

        public void RemoveRange(int start, int end)
        {
            if (end >= size)
                throw new LSysException("end can't be >= size: " + end + " >= " + size);
            if (start > end)
                throw new LSysException("start can't be > end: " + start + " > " + end);
            T[] items = this.items;
            int count = end - start + 1;
            if (ordered)
                JavaSystem.Arraycopy(items, start + count, items, start, size - (start + count));
            else
            {
                int lastIndex = this.size - 1;
                for (int i = 0; i < count; i++)
                    items[start + i] = items[lastIndex - i];
            }
            size -= count;
        }

        public bool Remove(T value)
        {
            return Remove(value, false);
        }

        public bool Remove(T value, bool identity)
        {
            T[] items = this.items;
            if (identity || value == null)
            {
                for (int i = 0; i < size; i++)
                {
                    if ((object)items[i] == (object)value)
                    {
                        RemoveIndex(i);
                        return true;
                    }
                }
            }
            else
            {
                for (int i = 0; i < size; i++)
                {
                    if (value.Equals(items[i]))
                    {
                        RemoveIndex(i);
                        return true;
                    }
                }
            }
            return false;
        }

        public bool RemoveAll(TArray<T> array)
        {
            return RemoveAll(array, false);
        }

        public bool RemoveAll(TArray<T> array, bool identity)
        {
            if (array.size == 0)
            {
                return true;
            }
            int size = this.size;
            int startSize = size;
            T[] items = this.items;
            if (identity)
            {
                for (int i = 0, n = array.size; i < n; i++)
                {
                    T item = array.Get(i);
                    for (int ii = 0; ii < size; ii++)
                    {
                        if ((object)item == (object)items[ii])
                        {
                            RemoveIndex(ii);
                            size--;
                            break;
                        }
                    }
                }
            }
            else
            {
                for (int i = 0, n = array.size; i < n; i++)
                {
                    T item = array.Get(i);
                    for (int ii = 0; ii < size; ii++)
                    {
                        if (item.Equals(items[ii]))
                        {
                            RemoveIndex(ii);
                            size--;
                            break;
                        }
                    }
                }
            }
            return size != startSize;
        }

        public TArray<T> Cpy()
        {
            return new TArray<T>(items);
        }

        public T Pop()
        {
            if (size == 0)
                throw new LSysException("TArray is empty.");
            --size;
            T item = items[size];
            items[size] = default;
            return item;
        }

        public T Peek()
        {
            if (size == 0)
                throw new LSysException("TArray is empty.");
            return items[size - 1];
        }

        public T First()
        {
            if (size == 0)
                throw new LSysException("TArray is empty.");
            return items[0];
        }


        public void Clear()
        {
            T[] items = this.items;
            for (int i = 0, n = size; i < n; i++)
                items[i] = default;
            size = 0;
        }


        public bool IsEmpty()
        {
            return this.size == 0;
        }

        public T[] Shrink()
        {
            if (items.Length != size)
                Resize(size);
            return items;
        }

        public T[] EnsureCapacity(int additionalCapacity)
        {
            int sizeNeeded = size + additionalCapacity;
            if (sizeNeeded > items.Length)
                Resize(MathUtils.Max(8, sizeNeeded));
            return items;
        }

        protected T[] Resize(int newSize)
        {
            T[] items = this.items;
            T[] newItems = new T[newSize];
            JavaSystem.Arraycopy(items, 0, newItems, 0, MathUtils.Min(size, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public TArray<T> Reverse()
        {
            T[] items = this.items;
            for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                T temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
            return this;
        }

        public TArray<T> Shuffle()
        {
            T[] items = this.items;
            for (int i = size - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                T temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
            return this;
        }

        public TArray<T> Unshift(T o)
        {
            T[] items = this.items;
            int len = items.Length;
            T[] newItems = new T[len + 1];
            newItems[0] = o;
            JavaSystem.Arraycopy(items, 0, newItems, 1, items.Length);
            this.items = newItems;
            this.size++;
            return this;
        }

        public void Truncate(int newSize)
        {
            if (size <= newSize)
                return;
            for (int i = newSize; i < size; i++)
                items[i] = default;
            size = newSize;
        }

        public T Last()
        {
            return items[size < 1 ? 0 : size - 1];
        }

        public T RemoveFirst()
        {
            return RemoveIndex(0);
        }

        public T RemoveLast()
        {
            return RemoveIndex(size < 1 ? 0 : size - 1);
        }

        public T Random()
        {
            if (size == 0)
                return default;
            return items[MathUtils.Random(0, size - 1)];
        }

        public TArray<T> RandomArrays()
        {
            if (size == 0)
            {
                return new TArray<T>();
            }
            T v = default;
            TArray<T> newArrays = new TArray<T>(size);
            for (int i = 0; i < size; i++)
            {
                newArrays.Add(Get(i));
            }
            for (int i = 0; i < size; i++)
            {
                v = Random();
                for (int j = 0; j < i; j++)
                {
                    if ((object)newArrays.Get(j) == (object)v)
                    {
                        v = Random();
                        j = -1;
                    }

                }
                newArrays.Set(i, v);
            }
            return newArrays;
        }

        public object[] ToArray()
        {
            object[] result = new object[size];
            JavaSystem.Arraycopy(items, 0, result, 0, size);
            return result;
        }

        public T[] ToArray(T[] a)
        {
            int length = this.size;
            if (a.Length < size)
            {
                a = new T[length];
            }
            T[] result = a;
            for (int i = 0; i < length; ++i)
            {
                result[i] = Get(i);
            }
            if (a.Length > length)
            {
                a[length] = default;
            }
            return a;
        }

        public override bool Equals(object o)
        {
            if (o == this)
                return true;
            if (!(o is TArray<T>))
            {
                return false;
            }
            TArray<T> array = (TArray<T>)o;
            int n = size;
            if (n != array.size)
                return false;
            T[] items1 = this.items;
            T[] items2 = array.items;
            for (int i = 0; i < n; i++)
            {
                T o1 = items1[i];
                T o2 = items2[i];
                if (!(o1 == null ? o2 == null : o1.Equals(o2)))
                    return false;
            }
            return true;
        }

        public TArray<T> Concat(TArray<T> array)
        {
            TArray<T> all = new TArray<T>(this);
            all.AddAll(array);
            return all;
        }


        private ArrayIterable<T> iterable;
        public Iterator<T> Iterator()
        {
            if (iterable == null)
            {
                iterable = new ArrayIterable<T>(this);
            }
            return iterable.Iterator();
        }



        public bool RetainAll(TArray<T> array)
        {
            T[] elementData = this.items;
            int r = 0, w = 0;
            bool modified = false;
            try
            {
                for (; r < size; r++)
                    if (array.Contains(elementData[r]))
                    {
                        elementData[w++] = elementData[r];
                    }
            }
            finally
            {
                if (r != size)
                {
                    JavaSystem.Arraycopy(elementData, r, elementData, w, size - r);
                    w += size - r;
                }
                if (w != size)
                {
                    for (int i = w; i < size; i++)
                    {
                        elementData[i] = default;
                    }
                    size = w;
                    modified = true;
                }
            }
            return modified;
        }

        public override int GetHashCode()
        {
            if (!ordered)
            {
                return base.GetHashCode();
            }
            int hashCode = 1;
            for (int i = size - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + (items[i] == null ? 0 : items[i].GetHashCode());
            }
            return hashCode;
        }


        public override string ToString()
        {
            if (size == 0)
                return "[]";
            T[] items = this.items;
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append('[');
            buffer.Append(items[0]);
            for (int i = 1; i < size; i++)
            {
                buffer.Append(", ");
                buffer.Append(items[i]);
            }
            buffer.Append(']');
            return buffer.ToString();
        }

        public string ToString(string separator)
        {
            if (size == 0)
            {
                return "";
            }
            T[] items = this.items;
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append(items[0]);
            for (int i = 1; i < size; i++)
            {
                buffer.Append(separator);
                buffer.Append(items[i]);
            }
            return buffer.ToString();
        }

        public int Size()
        {
            return size;
        }
    }
}
