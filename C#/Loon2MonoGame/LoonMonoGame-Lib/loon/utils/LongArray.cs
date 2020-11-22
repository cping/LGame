using java.lang;
using java.util;

namespace loon.utils
{

    public class LongArray : IArray
    {

        public static LongArray Range(int start, int end)
        {
            LongArray array = new LongArray(end - start);
            for (int i = start; i < end; i++)
            {
                array.Add(i);
            }
            return array;
        }

        public static LongArray RangeRandom(int begin, int end)
        {
            return RangeRandom(begin, end, (end - begin));
        }

        public static LongArray RangeRandom(int begin, int end, int size)
        {
            if (begin > end)
            {
                int temp = begin;
                begin = end;
                end = temp;
            }
            if ((end - begin) < size)
            {
                throw new LSysException("Size out Range between begin and end !");
            }
            long[] randSeed = new long[end - begin];
            for (int i = begin; i < end; i++)
            {
                randSeed[i - begin] = i;
            }
            long[] longArrays = new long[size];
            for (int i = 0; i < size; i++)
            {
                int len = randSeed.Length - i - 1;
                int j = MathUtils.Random(len);
                longArrays[i] = randSeed[j];
                randSeed[j] = randSeed[len];
            }
            return new LongArray(longArrays);
        }

        public long[] items;
        public int length;
        public bool ordered;

        public LongArray() : this(true, CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public LongArray(int capacity) : this(true, capacity)
        {

        }

        public LongArray(bool ordered, int capacity)
        {
            this.ordered = ordered;
            items = new long[capacity];
        }

        public LongArray(LongArray array)
        {
            this.ordered = array.ordered;
            length = array.length;
            items = new long[length];
            JavaSystem.Arraycopy(array.items, 0, items, 0, length);
        }

        public LongArray(long[] array,int size) : this(true, array, 0, size)
        {

        }

        public LongArray(long[] array) : this(true, array, 0, array.Length)
        {

        }

        public LongArray(bool ordered, long[] array, int startIndex, int count) : this(ordered, count)
        {

            length = count;
            JavaSystem.Arraycopy(array, startIndex, items, 0, count);
        }

        public void Unshift(long value)
        {
            if (length > 0)
            {
                long[] items = this.items;
                long[] newItems = new long[length + 1];
                newItems[0] = value;
                JavaSystem.Arraycopy(items, 0, newItems, 1, length);
                this.length = newItems.Length;
                this.items = newItems;
            }
            else
            {
                Add(value);
            }
        }

        public void Push(long value)
        {
            Add(value);
        }

        public void Add(long value)
        {
            long[] items = this.items;
            if (length == items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            }
            items[length++] = value;
        }

        public void AddAll(LongArray array)
        {
            AddAll(array, 0, array.length);
        }

        public void AddAll(LongArray array, int offset, int length)
        {
            if (offset + length > array.length)
                throw new LSysException(
                        "offset + length must be <= length: " + offset + " + "
                                + length + " <= " + array.length);
            AddAll(array.items, offset, length);
        }

        public void AddAll(params long[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(long[] array, int offset, int length)
        {
            long[] items = this.items;
            int lengthNeeded = length + length;
            if (lengthNeeded > items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
            }
            JavaSystem.Arraycopy(array, offset, items, length, length);
            length += length;
        }

        public long Get(int index)
        {
            if (index >= length)
            {
                return 0;
            }
            return items[index];
        }

        public void Set(int index, long value)
        {
            if (index >= length)
            {
                int size = length;
                for (int i = size; i < index + 1; i++)
                {
                    Add(0);
                }
                items[index] = value;
                return;
            }
            items[index] = value;
        }

        public void Incr(int index, int value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: "
                        + index + " >= " + length);
            items[index] += value;
        }

        public void Mul(int index, int value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: "
                        + index + " >= " + length);
            items[index] *= value;
        }

        public void Insert(int index, long value)
        {
            if (index > length)
            {
                throw new LSysException("index can't be > length: "
                        + index + " > " + length);
            }
            long[] items = this.items;
            if (length == items.Length)
                items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            if (ordered)
                JavaSystem.Arraycopy(items, index, items, index + 1, length - index);
            else
                items[length] = items[index];
            length++;
            items[index] = value;
        }

        public void Swap(int first, int second)
        {
            if (first >= length)
                throw new LSysException("first can't be >= length: "
                        + first + " >= " + length);
            if (second >= length)
                throw new LSysException("second can't be >= length: "
                        + second + " >= " + length);
            long[] items = this.items;
            long firstValue = items[first];
            items[first] = items[second];
            items[second] = firstValue;
        }

        public bool Contains(long value)
        {
            int i = length - 1;
            long[] items = this.items;
            while (i >= 0)
                if (items[i--] == value)
                    return true;
            return false;
        }

        public int IndexOf(long value)
        {
            long[] items = this.items;
            for (int i = 0, n = length; i < n; i++)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public int LastIndexOf(long value)
        {
            long[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public LongArray RandomFloatArray()
        {
            return new LongArray(RandomArrays());
        }

        public long[] RandomArrays()
        {
            if (length == 0)
            {
                return new long[0];
            }
            long v = 0L;
            long[] newArrays = CollectionUtils.CopyOf(items, length);
            for (int i = 0; i < length; i++)
            {
                v = Random();
                for (int j = 0; j < i; j++)
                {
                    if (newArrays[j] == v)
                    {
                        v = Random();
                        j = -1;
                    }
                }
                newArrays[i] = v;
            }
            return newArrays;
        }

        public bool RemoveValue(long value)
        {
            long[] items = this.items;
            for (int i = 0, n = length; i < n; i++)
            {
                if (items[i] == value)
                {
                    RemoveIndex(i);
                    return true;
                }
            }
            return false;
        }

        public long RemoveIndex(int index)
        {
            if (index >= length)
            {
                throw new LSysException("index can't be >= length: "
                        + index + " >= " + length);
            }
            long[] items = this.items;
            long value = items[index];
            length--;
            if (ordered)
            {
                JavaSystem.Arraycopy(items, index + 1, items, index, length - index);
            }
            else
            {
                items[index] = items[length];
            }
            return value;
        }

        public void RemoveRange(int start, int end)
        {
            if (end >= length)
            {
                throw new LSysException("end can't be >= length: "
                        + end + " >= " + length);
            }
            if (start > end)
            {
                throw new LSysException("start can't be > end: "
                        + start + " > " + end);
            }
            long[] items = this.items;
            int count = end - start + 1;
            if (ordered)
            {
                JavaSystem.Arraycopy(items, start + count, items, start, length
                        - (start + count));
            }
            else
            {
                int lastIndex = this.length - 1;
                for (int i = 0; i < count; i++)
                    items[start + i] = items[lastIndex - i];
            }
            length -= count;
        }

        public bool RemoveAll(LongArray array)
        {
            int length = this.length;
            int startlength = length;
            long[] items = this.items;
            for (int i = 0, n = array.length; i < n; i++)
            {
                long item = array.Get(i);
                for (int ii = 0; ii < length; ii++)
                {
                    if (item == items[ii])
                    {
                        RemoveIndex(ii);
                        length--;
                        break;
                    }
                }
            }
            return length != startlength;
        }

        public long Pop()
        {
            return items[--length];
        }

        public long Shift()
        {
            return RemoveIndex(0);
        }

        public long Peek()
        {
            return items[length - 1];
        }

        public long First()
        {
            if (length == 0)
            {
                throw new LSysException("Array is empty.");
            }
            return items[0];
        }

        public void Clear()
        {
            length = 0;
        }

        public long[] Shrink()
        {
            if (items.Length != length)
                Relength(length);
            return items;
        }

        public long[] EnsureCapacity(int additionalCapacity)
        {
            int lengthNeeded = length + additionalCapacity;
            if (lengthNeeded > items.Length)
                Relength(MathUtils.Max(8, lengthNeeded));
            return items;
        }

        protected long[] Relength(int newlength)
        {
            long[] newItems = new long[newlength];
            long[] items = this.items;
            JavaSystem.Arraycopy(items, 0, newItems, 0,
                    MathUtils.Min(length, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public void Sort()
        {
            Arrays.Sort(items, 0, length);
        }

        public void Reverse()
        {
            long[] items = this.items;
            for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                long temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Shuffle()
        {
            long[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                long temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Truncate(int newlength)
        {
            if (length > newlength)
                length = newlength;
        }

        public long Random()
        {
            if (length == 0)
            {
                return 0;
            }
            return items[MathUtils.Random(0, length - 1)];
        }

        public int[] ToArray()
        {
            int[] array = new int[length];
            JavaSystem.Arraycopy(items, 0, array, 0, length);
            return array;
        }


        public override bool Equals(object o)
        {
            if (o == this)
                return true;
            if (!(o is LongArray))
                return false;
            LongArray array = (LongArray)o;
            int n = length;
            if (n != array.length)
                return false;
            for (int i = 0; i < n; i++)
                if (items[i] != array.items[i])
                    return false;
            return true;
        }

        static public LongArray With(params long[] array)
        {
            return new LongArray(array);
        }

        public LongArray Splice(int begin, int end)
        {
            LongArray longs = new LongArray(Slice(begin, end));
            if (end - begin >= length)
            {
                items = new long[0];
                length = 0;
                return longs;
            }
            else
            {
                RemoveRange(begin, end - 1);
            }
            return longs;
        }

        public static long[] Slice(long[] array, int begin, int end)
        {
            if (begin > end)
            {
                throw new LSysException("LongArray begin > end");
            }
            if (begin < 0)
            {
                begin = array.Length + begin;
            }
            if (end < 0)
            {
                end = array.Length + end;
            }
            int elements = end - begin;
            long[] ret = new long[elements];
            JavaSystem.Arraycopy(array, begin, ret, 0, elements);
            return ret;
        }

        public static long[] Slice(long[] array, int begin)
        {
            return Slice(array, begin, array.Length);
        }

        public LongArray Slice(int size)
        {
            return new LongArray(Slice(this.items, size, this.length));
        }

        public LongArray Slice(int begin, int end)
        {
            return new LongArray(Slice(this.items, begin, end));
        }

        public static long[] Concat(long[] array, long[] other)
        {
            return Concat(array, array.Length, other, other.Length);
        }

        public static long[] Concat(long[] array, int alen, long[] other, int blen)
        {
            long[] ret = new long[alen + blen];
            JavaSystem.Arraycopy(array, 0, ret, 0, alen);
            JavaSystem.Arraycopy(other, 0, ret, alen, blen);
            return ret;
        }

        public LongArray Concat(LongArray o)
        {
            return new LongArray(Concat(this.items, this.length, o.items, o.length));
        }

        public long Sum()
        {
            if (length == 0)
            {
                return 0;
            }
            long total = 0;
            for (int i = length - 1; i > -1; i--)
            {
                total += items[i];
            }
            return total;
        }

        public long Average()
        {
            if (length == 0)
            {
                return 0;
            }
            return this.Sum() / length;
        }

        public long Min()
        {
            long v = this.items[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                long n = this.items[i];
                if (n < v)
                {
                    v = n;
                }
            }
            return v;
        }

        public long Max()
        {
            long v = this.items[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                long n = this.items[i];
                if (n > v)
                {
                    v = n;
                }
            }
            return v;
        }

        public int Size()
        {
            return length;
        }


        public bool IsEmpty()
        {
            return length == 0 || items == null;
        }

        public sbyte[] GetBytes()
        {
            long[] items = this.items;
            ArrayByte bytes = new ArrayByte(items.Length * 8);
            for (int i = 0; i < items.Length; i++)
            {
                bytes.WriteLong(items[i]);
            }
            return bytes.GetBytes();
        }

        public override int GetHashCode()
        {
            long hashCode = 1;
            for (int i = length - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + items[i];
            }
            return (int)hashCode;
        }

        public LongArray Cpy()
        {
            return new LongArray(this);
        }

        public string ToString(char split)
        {
            if (length == 0)
            {
                return "[]";
            }
            long[] items = this.items;
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append('[');
            buffer.Append(items[0]);
            for (int i = 1; i < length; i++)
            {
                buffer.Append(split);
                buffer.Append(items[i]);
            }
            buffer.Append(']');
            return buffer.ToString();
        }

        public override string ToString()
        {
            return ToString(',');
        }
    }
}
