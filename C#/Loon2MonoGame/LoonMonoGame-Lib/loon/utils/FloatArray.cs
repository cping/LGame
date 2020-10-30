using java.lang;
using java.util;
using loon.events;

namespace loon.utils
{
    public class FloatArray : IArray
    {

        public static FloatArray Range(int start, int end)
        {
            FloatArray array = new FloatArray(end - start);
            for (int i = start; i < end; i++)
            {
                array.Add(i + MathUtils.Random(0f, 0.99f));
            }
            return array;
        }

        public static FloatArray RangeRandom(int begin, int end)
        {
            return RangeRandom(begin, end, (end - begin));
        }

        public static FloatArray RangeRandom(int begin, int end, int size)
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
            float[] randSeed = new float[end - begin];
            for (int i = begin; i < end; i++)
            {
                randSeed[i - begin] = i + MathUtils.Random(0f, 0.99f);
            }
            float[] floatArrays = new float[size];
            for (int i = 0; i < size; i++)
            {
                int len = randSeed.Length - i - 1;
                int j = MathUtils.Random(len);
                floatArrays[i] = randSeed[j];
                randSeed[j] = randSeed[len];
            }
            return new FloatArray(floatArrays);
        }

        public float[] items;
        public int length;
        public bool ordered;

        public FloatArray() : this(true, CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public FloatArray(int capacity) : this(true, capacity)
        {

        }

        public FloatArray(bool ordered, int capacity)
        {
            this.ordered = ordered;
            items = new float[capacity];
        }

        public FloatArray(FloatArray array)
        {
            this.ordered = array.ordered;
            length = array.length;
            items = new float[length];
            JavaSystem.Arraycopy(array.items, 0, items, 0, length);
        }

        public FloatArray(float[] array) : this(true, array, 0, array.Length)
        {

        }

        public FloatArray(bool ordered, float[] array, int startIndex, int count) : this(ordered, count)
        {
            length = count;
            JavaSystem.Arraycopy(array, startIndex, items, 0, count);
        }

        public void Unshift(float value)
        {
            if (length > 0)
            {
                float[] items = this.items;
                float[] newItems = new float[length + 1];
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

        public void Push(float value)
        {
            Add(value);
        }

        public void Add(float value)
        {
            float[] items = this.items;
            if (length == items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            }
            items[length++] = value;
        }

        public void AddAll(FloatArray array)
        {
            AddAll(array, 0, array.length);
        }

        public void AddAll(FloatArray array, int offset, int length)
        {
            if (offset + length > array.length)
                throw new LSysException(
                        "offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
            AddAll(array.items, offset, length);
        }

        public void AddAll(params float[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(float[] array, int offset, int len)
        {
            float[] items = this.items;
            int lengthNeeded = this.length + len;
            if (lengthNeeded > items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
            }
            JavaSystem.Arraycopy(array, offset, items, this.length, len);
            this.length += len;
        }

        public float Get(int index)
        {
            if (index >= length)
            {
                return 0;
            }
            return items[index];
        }

        public void Set(int index, float value)
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

        public void Incr(int index, float value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            items[index] += value;
        }

        public void Mul(int index, float value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            items[index] *= value;
        }

        public void Insert(int index, float value)
        {
            if (index > length)
            {
                throw new LSysException("index can't be > length: " + index + " > " + length);
            }
            float[] items = this.items;
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
                throw new LSysException("first can't be >= length: " + first + " >= " + length);
            if (second >= length)
                throw new LSysException("second can't be >= length: " + second + " >= " + length);
            float[] items = this.items;
            float firstValue = items[first];
            items[first] = items[second];
            items[second] = firstValue;
        }

        public bool Contains(float value)
        {
            int i = length - 1;
            float[] items = this.items;
            while (i >= 0)
                if (items[i--] == value)
                    return true;
            return false;
        }

        public int IndexOf(float value)
        {
            float[] items = this.items;
            for (int i = 0, n = length; i < n; i++)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public int LastIndexOf(float value)
        {
            float[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public bool RemoveValue(float value)
        {
            float[] items = this.items;
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

        public float RemoveIndex(int index)
        {
            if (index >= length)
            {
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            }
            float[] items = this.items;
            float value = items[index];
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
                throw new LSysException("end can't be >= length: " + end + " >= " + length);
            }
            if (start > end)
            {
                throw new LSysException("start can't be > end: " + start + " > " + end);
            }
            float[] items = this.items;
            int count = end - start + 1;
            if (ordered)
            {
                JavaSystem.Arraycopy(items, start + count, items, start, length - (start + count));
            }
            else
            {
                int lastIndex = this.length - 1;
                for (int i = 0; i < count; i++)
                    items[start + i] = items[lastIndex - i];
            }
            length -= count;
        }

        public bool RemoveAll(FloatArray array)
        {
            int length = this.length;
            int startlength = length;
            float[] items = this.items;
            for (int i = 0, n = array.length; i < n; i++)
            {
                float item = array.Get(i);
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

        public float Pop()
        {
            return items[--length];
        }

        public float Shift()
        {
            return RemoveIndex(0);
        }

        public float Peek()
        {
            return items[length - 1];
        }

        public float First()
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

        public float[] Shrink()
        {
            if (items.Length != length)
                Relength(length);
            return items;
        }

        public float[] EnsureCapacity(int additionalCapacity)
        {
            int lengthNeeded = length + additionalCapacity;
            if (lengthNeeded > items.Length)
                Relength(MathUtils.Max(8, lengthNeeded));
            return items;
        }

        protected float[] Relength(int newlength)
        {
            float[] newItems = new float[newlength];
            float[] items = this.items;
            JavaSystem.Arraycopy(items, 0, newItems, 0, MathUtils.Min(length, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public FloatArray Sort()
        {
            Arrays.Sort(items, 0, length);
            return this;
        }

        public bool IsSorted(bool order)
        {
            float[] arrays = this.items;
            int orderCount = 0;
            float temp = -1;
            float v = order ? Float.MIN_VALUE_JAVA : Float.MAX_VALUE_JAVA;
            for (int i = 0; i < length; i++)
            {
                temp = v;
                v = arrays[i];
                if (order)
                {
                    if (temp <= v)
                    {
                        orderCount++;
                    }
                }
                else
                {
                    if (temp >= v)
                    {
                        orderCount++;
                    }
                }
            }
            return orderCount == length;
        }

        public void Reverse()
        {
            float[] items = this.items;
            for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                float temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Shuffle()
        {
            float[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                float temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Truncate(int newlength)
        {
            if (length > newlength)
                length = newlength;
        }

        public float Random()
        {
            if (length == 0)
            {
                return 0;
            }
            return items[MathUtils.Random(0, length - 1)];
        }

        public FloatArray RandomFloatArray()
        {
            return new FloatArray(RandomArrays());
        }

        public float[] RandomArrays()
        {
            if (length == 0)
            {
                return new float[0];
            }
            float v = 0f;
            float[] newArrays = CollectionUtils.CopyOf(items, length);
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

        public float[] ToArray()
        {
            float[] array = new float[length];
            JavaSystem.Arraycopy(items, 0, array, 0, length);
            return array;
        }

        public override bool Equals(object o)
        {
            if (o == this)
                return true;
            if (!(o is FloatArray))
                return false;
            FloatArray array = (FloatArray)o;
            int n = length;
            if (n != array.length)
                return false;
            for (int i = 0; i < n; i++)
                if (items[i] != array.items[i])
                    return false;
            return true;
        }

        static public FloatArray With(params float[] array)
        {
            return new FloatArray(array);
        }

        public FloatArray Splice(int begin, int end)
        {
            FloatArray longs = new FloatArray(Slice(begin, end));
            if (end - begin >= length)
            {
                items = new float[0];
                length = 0;
                return longs;
            }
            else
            {
                RemoveRange(begin, end - 1);
            }
            return longs;
        }

        public static float[] Slice(float[] array, int begin, int end)
        {
            if (begin > end)
            {
                throw new LSysException("FloatArray begin > end");
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
            float[] ret = new float[elements];
            JavaSystem.Arraycopy(array, begin, ret, 0, elements);
            return ret;
        }

        public static float[] Slice(float[] array, int begin)
        {
            return Slice(array, begin, array.Length);
        }

        public FloatArray Slice(int size)
        {
            return new FloatArray(Slice(this.items, size, this.length));
        }

        public FloatArray Slice(int begin, int end)
        {
            return new FloatArray(Slice(this.items, begin, end));
        }

        public static float[] Concat(float[] array, float[] other)
        {
            return Concat(array, array.Length, other, other.Length);
        }

        public static float[] Concat(float[] array, int alen, float[] other, int blen)
        {
            float[] ret = new float[alen + blen];
            JavaSystem.Arraycopy(array, 0, ret, 0, alen);
            JavaSystem.Arraycopy(other, 0, ret, alen, blen);
            return ret;
        }

        public FloatArray Concat(FloatArray o)
        {
            return new FloatArray(Concat(this.items, this.length, o.items, o.length));
        }

        public int Size()
        {
            return length;
        }


        public bool IsEmpty()
        {
            return length == 0 || items == null;
        }

        public FloatArray Where(QueryEvent<Float> test)
        {
            FloatArray list = new FloatArray();
            for (int i = 0; i < length; i++)
            {
                Float t = Float.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    list.Add(t.FloatValue());
                }
            }
            return list;
        }

        public Float Find(QueryEvent<Float> test)
        {
            for (int i = 0; i < length; i++)
            {
                Float t = Float.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    return t;
                }
            }
            return null;
        }

        public bool Remove(QueryEvent<Float> test)
        {
            for (int i = length - 1; i > -1; i--)
            {
                Float t = Float.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    return RemoveValue(t.FloatValue());
                }
            }
            return false;
        }

        public float Sum()
        {
            if (length == 0)
            {
                return 0;
            }
            float total = 0;
            for (int i = length - 1; i > -1; i--)
            {
                total += items[i];
            }
            return total;
        }

        public float Average()
        {
            if (length == 0)
            {
                return 0;
            }
            return this.Sum() / length;
        }

        public float Min()
        {
            float v = this.items[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                float n = this.items[i];
                if (n < v)
                {
                    v = n;
                }
            }
            return v;
        }

        public float Max()
        {
            float v = this.items[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                float n = this.items[i];
                if (n > v)
                {
                    v = n;
                }
            }
            return v;
        }

        public sbyte[] GetBytes()
        {
            return GetBytes(0);
        }

        public sbyte[] GetBytes(int order)
        {
            float[] items = this.items;
            ArrayByte bytes = new ArrayByte(items.Length * 4);
            bytes.SetOrder(order);
            for (int i = 0; i < items.Length; i++)
            {
                bytes.WriteFloat(items[i]);
            }
            return bytes.GetBytes();
        }

        public override int GetHashCode()
        {
            uint hashCode = 1;
            for (int i = length - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + NumberUtils.FloatToIntBits(items[i]);
            }
            return (int)hashCode;
        }

        public string ToString(char split)
        {
            if (length == 0)
            {
                return "[]";
            }
            float[] items = this.items;
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
