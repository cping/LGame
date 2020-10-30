using java.lang;
using java.util;
using loon.events;

namespace loon.utils
{
    public class CharArray : IArray
    {


        public static CharArray Range(int start, int end)
        {
            CharArray array = new CharArray(end - start);
            for (int i = start; i < end; i++)
            {
                array.Add((char)i);
            }
            return array;
        }

        public static CharArray RangeRandomArrays(int begin, int end)
        {
            return RangeRandom(begin, end, (end - begin));
        }

        public static CharArray RangeRandom(int begin, int end, int size)
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
            char[] randSeed = new char[end - begin];
            for (int i = begin; i < end; i++)
            {
                randSeed[i - begin] = (char)i;
            }
            char[] charArrays = new char[size];
            for (int i = 0; i < size; i++)
            {
                int len = randSeed.Length - i - 1;
                int j = MathUtils.Random(len);
                charArrays[i] = (char)randSeed[j];
                randSeed[j] = (char)randSeed[len];
            }
            return new CharArray(charArrays);
        }

        public char[] items;
        public int length;
        public bool ordered;

        public CharArray() : this(true, CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public CharArray(int capacity) : this(true, capacity)
        {

        }

        public CharArray(bool ordered, int capacity)
        {
            this.ordered = ordered;
            this.items = new char[capacity];
        }

        public CharArray(CharArray array)
        {
            this.ordered = array.ordered;
            length = array.length;
            items = new char[length];
            JavaSystem.Arraycopy(array.items, 0, items, 0, length);
        }

        public CharArray(char[] array) : this(true, array, 0, array.Length)
        {

        }

        public CharArray(bool ordered, char[] array, int startIndex, int count) : this(ordered, count)
        {
            length = count;
            JavaSystem.Arraycopy(array, startIndex, items, 0, count);
        }

        public void Unshift(char value)
        {
            if (length > 0)
            {
                char[] items = this.items;
                char[] newItems = new char[length + 1];
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

        public void Push(char value)
        {
            Add(value);
        }

        public void Add(char value)
        {
            char[] items = this.items;
            if (length == items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            }
            items[length++] = value;
        }

        public void AddAll(CharArray array)
        {
            AddAll(array, 0, array.length);
        }

        public void AddAll(CharArray array, int offset, int length)
        {
            if (offset + length > array.length)
                throw new LSysException(
                        "offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
            AddAll(array.items, offset, length);
        }

        public void AddAll(params char[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(char[] array, int offset, int len)
        {
            char[] items = this.items;
            int lengthNeeded = this.length + len;
            if (lengthNeeded > items.Length)
            {
                items = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
            }
            JavaSystem.Arraycopy(array, offset, items, this.length, len);
            this.length += len;
        }

        public char Get(int index)
        {
            if (index >= length)
            {
                return CharUtils.ToChar(0);
            }
            return items[index];
        }

        public void Set(int index, char value)
        {
            if (index >= length)
            {
                int size = length;
                for (int i = size; i < index + 1; i++)
                {
                    Add(' ');
                }
                items[index] = value;
                return;
            }
            items[index] = value;
        }

        public void Incr(int index, char value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            items[index] += value;
        }

        public void Mul(int index, char value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            items[index] *= value;
        }

        public void Insert(int index, char value)
        {
            if (index > length)
            {
                throw new LSysException("index can't be > length: " + index + " > " + length);
            }
            char[] items = this.items;
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
            char[] items = this.items;
            char firstValue = items[first];
            items[first] = items[second];
            items[second] = firstValue;
        }

        public bool Contains(char value)
        {
            int i = length - 1;
            char[] items = this.items;
            while (i >= 0)
                if (items[i--] == value)
                    return true;
            return false;
        }

        public int IndexOf(char value)
        {
            char[] items = this.items;
            for (int i = 0, n = length; i < n; i++)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public int LastIndexOf(char value)
        {
            char[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
                if (items[i] == value)
                    return i;
            return -1;
        }

        public bool RemoveValue(char value)
        {
            char[] items = this.items;
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

        public int RemoveIndex(int index)
        {
            if (index >= length)
            {
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            }
            char[] items = this.items;
            char value = items[index];
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
            char[] items = this.items;
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

        public bool RemoveAll(CharArray array)
        {
            int length = this.length;
            int startlength = length;
            char[] items = this.items;
            for (int i = 0, n = array.length; i < n; i++)
            {
                int item = array.Get(i);
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

        public int Pop()
        {
            return items[--length];
        }

        public int Shift()
        {
            return RemoveIndex(0);
        }

        public int Peek()
        {
            return items[length - 1];
        }

        public int First()
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

        public char[] Shrink()
        {
            if (items.Length != length)
                Relength(length);
            return items;
        }

        public char[] EnsureCapacity(int additionalCapacity)
        {
            int lengthNeeded = length + additionalCapacity;
            if (lengthNeeded > items.Length)
                Relength(MathUtils.Max(8, lengthNeeded));
            return items;
        }

        protected char[] Relength(int newlength)
        {
            char[] newItems = new char[newlength];
            char[] items = this.items;
            JavaSystem.Arraycopy(items, 0, newItems, 0, MathUtils.Min(length, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public CharArray Sort()
        {
            Arrays.Sort(items, 0, length);
            return this;
        }

        public void Reverse()
        {
            char[] items = this.items;
            for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                char temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Shuffle()
        {
            char[] items = this.items;
            for (int i = length - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                char temp = items[i];
                items[i] = items[ii];
                items[ii] = temp;
            }
        }

        public void Truncate(int newlength)
        {
            if (length > newlength)
                length = newlength;
        }

        public char Random()
        {
            if (length == 0)
            {
                return CharUtils.ToChar(0);
            }
            return items[MathUtils.Random(0, length - 1)];
        }

        public CharArray RandomCharArray()
        {
            return new CharArray(RandomArrays());
        }

        public char[] RandomArrays()
        {
            if (length == 0)
            {
                return new char[0];
            }
            char v = CharUtils.ToChar(-1);
            char[] newArrays = CollectionUtils.CopyOf(items, length);
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

        public char[] ToArray()
        {
            char[] array = new char[length];
            JavaSystem.Arraycopy(items, 0, array, 0, length);
            return array;
        }

        public override bool Equals(object o)
        {
            if (o == this)
                return true;
            if (!(o is CharArray))
                return false;
            CharArray array = (CharArray)o;
            int n = length;
            if (n != array.length)
                return false;
            for (int i = 0; i < n; i++)
                if (items[i] != array.items[i])
                    return false;
            return true;
        }

        static public CharArray With(params char[] array)
        {
            return new CharArray(array);
        }

        public CharArray Splice(int begin, int end)
        {
            CharArray longs = new CharArray(Slice(begin, end));
            if (end - begin >= length)
            {
                items = new char[0];
                length = 0;
                return longs;
            }
            else
            {
                RemoveRange(begin, end - 1);
            }
            return longs;
        }

        public static char[] Slice(char[] array, int begin, int end)
        {
            if (begin > end)
            {
                throw new LSysException("CharArray begin > end");
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
            char[] ret = new char[elements];
            JavaSystem.Arraycopy(array, begin, ret, 0, elements);
            return ret;
        }

        public static char[] Slice(char[] array, int begin)
        {
            return Slice(array, begin, array.Length);
        }

        public CharArray Slice(int size)
        {
            return new CharArray(Slice(this.items, size, this.length));
        }

        public CharArray Slice(int begin, int end)
        {
            return new CharArray(Slice(this.items, begin, end));
        }

        public static char[] Concat(char[] array, char[] other)
        {
            return Concat(array, array.Length, other, other.Length);
        }

        public static char[] Concat(char[] array, int alen, char[] other, int blen)
        {
            char[] ret = new char[alen + blen];
            JavaSystem.Arraycopy(array, 0, ret, 0, alen);
            JavaSystem.Arraycopy(other, 0, ret, alen, blen);
            return ret;
        }

        public CharArray Concat(CharArray o)
        {
            return new CharArray(Concat(this.items, this.length, o.items, o.length));
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
            return GetBytes(0);
        }

        public sbyte[] GetBytes(int order)
        {
            char[] items = this.items;
            int size = items.Length;
            ArrayByte bytes = new ArrayByte(size * 2);
            bytes.SetOrder(order);
            for (int i = 0; i < size; i++)
            {
                bytes.WriteChar(items[i]);
            }
            return bytes.GetBytes();
        }

        public CharArray Where(QueryEvent<Character> test)
        {
            CharArray list = new CharArray();
            for (int i = 0; i < length; i++)
            {
                Character t = Character.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    list.Add(t.CharValue());
                }
            }
            return list;
        }

        public Character Find(QueryEvent<Character> test)
        {
            for (int i = 0; i < length; i++)
            {
                Character t = Character.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    return t;
                }
            }
            return null;
        }

        public bool Remove(QueryEvent<Character> test)
        {
            for (int i = length - 1; i > -1; i--)
            {
                Character t = Character.ValueOf(Get(i));
                if (test.Hit(t))
                {
                    return RemoveValue(t.CharValue());
                }
            }
            return false;
        }

        public string ToString(char split)
        {
            if (length == 0)
            {
                return "[]";
            }
            char[] items = this.items;
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

        public string GetString()
        {
            return new string(items);
        }

        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = length - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + items[i];
            }
            return hashCode;
        }

        public override string ToString()
        {
            return ToString(',');
        }

    }
}
