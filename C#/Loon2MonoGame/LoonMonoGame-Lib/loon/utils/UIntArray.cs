using java.lang;
using java.util;

namespace loon.utils
{
    public class UIntArray : IArray
    {
        public enum UIntMode
        {
            UINT8, UINT16, UINT32, UINT64
        }

        private readonly UIntMode uintmode;

        private sbyte[] bytebuffer;

        private int length;

        private int position = 0;

        private readonly bool ordered;

        private readonly bool littleEndian;

        public UIntArray() : this(false)
        {

        }

        public UIntArray(bool little) : this(UIntMode.UINT8, little)
        {

        }

        public UIntArray(UIntMode mode, bool little) : this(true, CollectionUtils.INITIAL_CAPACITY, mode, little)
        {

        }

        public UIntArray(int capacity, UIntMode mode, bool little) : this(true, capacity, mode, little)
        {

        }

        public UIntArray(bool ordered, int capacity, UIntMode mode, bool little)
        {
            this.uintmode = mode;
            this.littleEndian = little;
            this.ordered = ordered;
            bytebuffer = new sbyte[capacity];
        }

        public UIntArray(UIntArray array, UIntMode mode, bool little)
        {
            this.uintmode = mode;
            this.littleEndian = little;
            this.ordered = array.ordered;
            length = array.length;
            bytebuffer = new sbyte[length];
            JavaSystem.Arraycopy(array.bytebuffer, 0, bytebuffer, 0, length);
        }

        public UIntArray(sbyte[] array, UIntMode mode, bool little) : this(true, array, 0, array.Length, mode, little)
        {

        }

        public UIntArray(bool ordered, sbyte[] array, int startIndex, int count, UIntMode mode, bool little) : this(ordered, count, mode, little)
        {

            length = count;
            JavaSystem.Arraycopy(array, startIndex, bytebuffer, 0, count);
        }

        private void CheckAvailable(int length)
        {
            if (Available() < length)
            {
                throw new IndexOutOfBoundsException();
            }
        }

        public int ReadInt()
        {
            return ReadByte() & 0xff;
        }

        public string ReadByteHex()
        {
            return StringUtils.ToHex(ReadByte());
        }

        public sbyte ReadByte()
        {
            CheckAvailable(1);
            if (position + 1 > length)
            {
                return 0;
            }
            return Get(position++);
        }

        public bool WriteByte(sbyte value)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            if (length == bytebuffer.Length)
            {
                bytebuffer = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            }
            if (this.length > position && position >= 0)
            {
                bytebuffer[position++] = value;
            }
            else
            {
                bytebuffer[this.length] = value;
                this.length++;
                this.position++;
            }
            if (position > length)
            {
                position = length;
            }
            return true;
        }

        public bool WriteByte(int index, sbyte value)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            if (length == bytebuffer.Length || index >= bytebuffer.Length)
            {
                bytebuffer = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
            }
            if (this.length > index && index >= 0)
            {
                bytebuffer[index] = value;
            }
            else
            {
                bytebuffer[index] = value;
                this.length++;
                this.position++;
            }
            return true;
        }

        public bool WriteUInt(long value)
        {
            if (length + 4 >= bytebuffer.Length)
            {
                bytebuffer = EnsureCapacity(4);
            }
            sbyte firstByte = (sbyte)((value & 0xFF000000L) >> 24);
            sbyte secondByte = (sbyte)((value & 0x00FF0000L) >> 16);
            sbyte thirdByte = (sbyte)((value & 0x0000FF00L) >> 8);
            sbyte fourthByte = (sbyte)(value & 0x000000FFL);
            switch (this.uintmode)
            {
                case UIntMode.UINT8:
                    return WriteByte((sbyte)(value & 0xff));
                case UIntMode.UINT16:
                    if (this.littleEndian)
                    {
                        WriteByte(fourthByte);
                        WriteByte(thirdByte);
                    }
                    else
                    {
                        WriteByte(thirdByte);
                        WriteByte(fourthByte);
                    }
                    return true;
                case UIntMode.UINT32:
                    if (this.littleEndian)
                    {
                        WriteByte(fourthByte);
                        WriteByte(thirdByte);
                        WriteByte(secondByte);
                        WriteByte(firstByte);
                    }
                    else
                    {
                        WriteByte(firstByte);
                        WriteByte(secondByte);
                        WriteByte(thirdByte);
                        WriteByte(fourthByte);
                    }
                    return true;
                case UIntMode.UINT64:
                    sbyte firstByte2 = (sbyte)((value & 0xFF000000L) >> 56);
                    sbyte secondByte2 = (sbyte)((value & 0x00FF0000L) >> 48);
                    sbyte thirdByte2 = (sbyte)((value & 0x0000FF00L) >> 40);
                    sbyte fourthByte2 = (sbyte)((value & 0x0000FF00L) >> 32);
                    if (this.littleEndian)
                    {
                        WriteByte(fourthByte);
                        WriteByte(thirdByte);
                        WriteByte(secondByte);
                        WriteByte(firstByte);
                        WriteByte(fourthByte2);
                        WriteByte(thirdByte2);
                        WriteByte(secondByte2);
                        WriteByte(firstByte2);
                    }
                    else
                    {
                        WriteByte(firstByte2);
                        WriteByte(secondByte2);
                        WriteByte(thirdByte2);
                        WriteByte(fourthByte2);
                        WriteByte(firstByte);
                        WriteByte(secondByte);
                        WriteByte(thirdByte);
                        WriteByte(fourthByte);
                    }
                    return true;
            }
            return false;
        }

        public long ReadUInt()
        {
            long result = 0;
            int firstByte = 0;
            int secondByte = 0;
            int thirdByte = 0;
            int fourthByte = 0;
            switch (this.uintmode)
            {
                case UIntMode.UINT8:
                    return (0x000000FF & (int)ReadByte());
                case UIntMode.UINT16:
                    firstByte = (0x000000FF & (int)ReadByte());
                    secondByte = (0x000000FF & (int)ReadByte());
                    if (littleEndian)
                    {
                        result = ((long)(secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
                    }
                    else
                    {
                        result = ((long)(firstByte << 8 | secondByte)) & 0xFFFFFFFFL;
                    }
                    return result;
                case UIntMode.UINT32:
                    firstByte = (0x000000FF & (int)ReadByte());
                    secondByte = (0x000000FF & (int)ReadByte());
                    thirdByte = (0x000000FF & (int)ReadByte());
                    fourthByte = (0x000000FF & (int)ReadByte());
                    if (littleEndian)
                    {
                        result = ((long)(fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
                    }
                    else
                    {
                        result = ((long)(firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;

                    }
                    return result;
                case UIntMode.UINT64:
                    firstByte = (0x000000FF & (int)ReadByte());
                    secondByte = (0x000000FF & (int)ReadByte());
                    thirdByte = (0x000000FF & (int)ReadByte());
                    fourthByte = (0x000000FF & (int)ReadByte());
                    int firstByte2 = (0x000000FF & (int)ReadByte());
                    int secondByte2 = (0x000000FF & (int)ReadByte());
                    int thirdByte2 = (0x000000FF & (int)ReadByte());
                    int fourthByte2 = (0x000000FF & (int)ReadByte());
                    if (littleEndian)
                    {
                        result = ((long)(fourthByte2 << 56 | thirdByte2 << 48 | secondByte2 << 40 | firstByte2 << 32
                                | fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
                    }
                    else
                    {
                        result = ((long)(firstByte << 56 | secondByte << 48 | thirdByte << 40 | fourthByte << 32
                                | firstByte2 << 24 | secondByte2 << 16 | thirdByte2 << 8 | fourthByte2)) & 0xFFFFFFFFL;

                    }
                    return result;
            }
            return result;
        }

        public sbyte GetByte(int index)
        {
            return Get(index);
        }

        public int Position()
        {
            return position;
        }

        public void Position(int p)
        {
            SetPosition(p);
        }

        public void SetPosition(int position)
        {
            if (position < 0 || position > bytebuffer.Length || position > length)
            {
                throw new IndexOutOfBoundsException();
            }
            this.position = position;
        }

        public int Available()
        {
            return Size() - Position();
        }

        public void Unshift(sbyte value)
        {
            if (length > 0)
            {
                sbyte[] bytebuffer = this.bytebuffer;
                sbyte[] newItems = new sbyte[length + 1];
                newItems[0] = value;
                JavaSystem.Arraycopy(bytebuffer, 0, newItems, 1, length);
                this.length = newItems.Length;
                this.bytebuffer = newItems;
            }
            else
            {
                WriteByte(value);
            }
        }

        public void AddAll(UIntArray array)
        {
            AddAll(array, 0, array.length);
        }

        public void AddAll(UIntArray array, int offset, int length)
        {
            if (offset + length > array.length)
                throw new LSysException(
                        "offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
            AddAll(array.bytebuffer, offset, length);
        }

        public void AddAll(params sbyte[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(sbyte[] array, int offset, int length)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            int lengthNeeded = length + length;
            if (lengthNeeded > bytebuffer.Length)
            {
                bytebuffer = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
            }
            JavaSystem.Arraycopy(array, offset, bytebuffer, length, length);
            length += length;
        }

        public sbyte Get(int index)
        {
            if (index >= length)
            {
                return 0;
            }
            return bytebuffer[index];
        }

        public void Set(int index, sbyte value)
        {
            if (index >= length)
            {
                return;
            }
            bytebuffer[index] = value;
        }

        public void Incr(int index, sbyte value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            bytebuffer[index] += value;
        }

        public void Mul(int index, sbyte value)
        {
            if (index >= length)
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            bytebuffer[index] *= value;
        }

        public void Insert(int index, sbyte value)
        {
            if (index > length)
            {
                throw new LSysException("index can't be > length: " + index + " > " + length);
            }
            sbyte[] bytebuffer = this.bytebuffer;
            if (length == bytebuffer.Length)
                bytebuffer = Relength(MathUtils.Max(8, (sbyte)(length * 1.75f)));
            if (ordered)
                JavaSystem.Arraycopy(bytebuffer, index, bytebuffer, index + 1, length - index);
            else
                bytebuffer[length] = bytebuffer[index];
            length++;
            bytebuffer[index] = value;
        }

        public void Swap(int first, int second)
        {
            if (first >= length)
                throw new LSysException("first can't be >= length: " + first + " >= " + length);
            if (second >= length)
                throw new LSysException("second can't be >= length: " + second + " >= " + length);
            sbyte[] bytebuffer = this.bytebuffer;
            sbyte firstValue = bytebuffer[first];
            bytebuffer[first] = bytebuffer[second];
            bytebuffer[second] = firstValue;
        }

        public bool Contains(sbyte value)
        {
            int i = length - 1;
            sbyte[] bytebuffer = this.bytebuffer;
            while (i >= 0)
                if (bytebuffer[i--] == value)
                    return true;
            return false;
        }

        public int IndexOf(sbyte value)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = 0, n = length; i < n; i++)
                if (bytebuffer[i] == value)
                    return i;
            return -1;
        }

        public int LastIndexOf(sbyte value)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = length - 1; i >= 0; i--)
                if (bytebuffer[i] == value)
                    return i;
            return -1;
        }

        public bool RemoveValue(sbyte value)
        {
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = 0, n = length; i < n; i++)
            {
                if (bytebuffer[i] == value)
                {
                    RemoveIndex(i);
                    return true;
                }
            }
            return false;
        }

        public sbyte RemoveIndex(int index)
        {
            if (index >= length)
            {
                throw new LSysException("index can't be >= length: " + index + " >= " + length);
            }
            sbyte[] bytebuffer = this.bytebuffer;
            sbyte value = bytebuffer[index];
            length--;
            if (ordered)
            {
                JavaSystem.Arraycopy(bytebuffer, index + 1, bytebuffer, index, length - index);
            }
            else
            {
                bytebuffer[index] = bytebuffer[length];
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
            sbyte[] bytebuffer = this.bytebuffer;
            int count = end - start + 1;
            if (ordered)
            {
                JavaSystem.Arraycopy(bytebuffer, start + count, bytebuffer, start, length - (start + count));
            }
            else
            {
                int lastIndex = this.length - 1;
                for (int i = 0; i < count; i++)
                    bytebuffer[start + i] = bytebuffer[lastIndex - i];
            }
            length -= count;
        }

        public bool RemoveAll(UIntArray array)
        {
            int length = this.length;
            int startlength = length;
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = 0, n = array.length; i < n; i++)
            {
                int item = array.Get(i);
                for (int ii = 0; ii < length; ii++)
                {
                    if (item == bytebuffer[ii])
                    {
                        RemoveIndex(ii);
                        length--;
                        break;
                    }
                }
            }
            return length != startlength;
        }

        public sbyte Pop()
        {
            return bytebuffer[--length];
        }

        public sbyte Shift()
        {
            return RemoveIndex(0);
        }

        public sbyte Peek()
        {
            return bytebuffer[length - 1];
        }

        public sbyte First()
        {
            if (length == 0)
            {
                throw new LSysException("Array is empty.");
            }
            return bytebuffer[0];
        }


        public void Clear()
        {
            length = 0;
        }

        public sbyte[] Shrink()
        {
            if (bytebuffer.Length != length)
                Relength(length);
            return bytebuffer;
        }

        public sbyte[] EnsureCapacity(int additionalCapacity)
        {
            int lengthNeeded = length + additionalCapacity;
            if (lengthNeeded > bytebuffer.Length)
                Relength(MathUtils.Max(8, lengthNeeded));
            return bytebuffer;
        }

        protected sbyte[] Relength(int newlength)
        {
            sbyte[] newItems = new sbyte[newlength];
            sbyte[] bytebuffer = this.bytebuffer;
            JavaSystem.Arraycopy(bytebuffer, 0, newItems, 0, MathUtils.Min(length, newItems.Length));
            this.bytebuffer = newItems;
            return newItems;
        }

        public UIntArray Sort()
        {
            Arrays.Sort(bytebuffer, 0, length);
            return this;
        }

        public bool IsSorted(bool order)
        {
            sbyte[] arrays = this.bytebuffer;
            int orderCount = 0;
            sbyte temp = -1;
            sbyte v = order ? Byte.MIN_VALUE_JAVA : Byte.MAX_VALUE_JAVA;
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
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
            {
                int ii = lastIndex - i;
                sbyte temp = bytebuffer[i];
                bytebuffer[i] = bytebuffer[ii];
                bytebuffer[ii] = temp;
            }
        }

        public void Shuffle()
        {
            sbyte[] bytebuffer = this.bytebuffer;
            for (int i = length - 1; i >= 0; i--)
            {
                int ii = MathUtils.Random(i);
                sbyte temp = bytebuffer[i];
                bytebuffer[i] = bytebuffer[ii];
                bytebuffer[ii] = temp;
            }
        }

        public void Truncate(int newlength)
        {
            if (length > newlength)
                length = newlength;
        }

        public sbyte Random()
        {
            if (length == 0)
            {
                return 0;
            }
            return bytebuffer[MathUtils.Random(0, length - 1)];
        }

        public UIntArray RandomIntArray()
        {
            return new UIntArray(RandomArrays(), this.uintmode, this.littleEndian);
        }

        public sbyte[] RandomArrays()
        {
            if (length == 0)
            {
                return new sbyte[0];
            }
            sbyte v = 0;
            sbyte[] newArrays = CollectionUtils.CopyOf(bytebuffer, length);
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

        public sbyte[] ToArray()
        {
            sbyte[] array = new sbyte[length];
            JavaSystem.Arraycopy(bytebuffer, 0, array, 0, length);
            return array;
        }


        public override bool Equals(object o)
        {
            if (o == this)
                return true;
            if (!(o is UIntArray))
                return false;
            UIntArray array = (UIntArray)o;
            int n = length;
            if (n != array.length)
                return false;
            for (int i = 0; i < n; i++)
                if (bytebuffer[i] != array.bytebuffer[i])
                    return false;
            return true;
        }

        static public UIntArray With(UIntMode mode, params sbyte[] array)
        {
            return With(mode, false, array);
        }

        static public UIntArray With(UIntMode mode, bool little, sbyte[] array)
        {
            return new UIntArray(array, mode, little);
        }

        public UIntArray Splice(int begin, int end)
        {
            UIntArray longs = new UIntArray(Slice(begin, end), this.uintmode, this.littleEndian);
            if (end - begin >= length)
            {
                bytebuffer = new sbyte[0];
                length = 0;
                return longs;
            }
            else
            {
                RemoveRange(begin, end - 1);
            }
            return longs;
        }

        public static sbyte[] Slice(sbyte[] array, int begin, int end)
        {
            if (begin > end)
            {
                throw new LSysException("UIntArray begin > end");
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
            sbyte[] ret = new sbyte[elements];
            JavaSystem.Arraycopy(array, begin, ret, 0, elements);
            return ret;
        }

        public static sbyte[] Slice(sbyte[] array, int begin)
        {
            return Slice(array, begin, array.Length);
        }

        public UIntArray Slice(int size)
        {
            return new UIntArray(Slice(this.bytebuffer, size, this.length), this.uintmode, this.littleEndian);
        }

        public UIntArray Slice(int begin, int end)
        {
            return new UIntArray(Slice(this.bytebuffer, begin, end), this.uintmode, this.littleEndian);
        }

        public static sbyte[] Concat(sbyte[] array, sbyte[] other)
        {
            return Concat(array, array.Length, other, other.Length);
        }

        public static sbyte[] Concat(sbyte[] array, int alen, sbyte[] other, int blen)
        {
            sbyte[] ret = new sbyte[alen + blen];
            JavaSystem.Arraycopy(array, 0, ret, 0, alen);
            JavaSystem.Arraycopy(other, 0, ret, alen, blen);
            return ret;
        }

        public UIntArray Concat(UIntArray o)
        {
            return new UIntArray(Concat(this.bytebuffer, this.length, o.bytebuffer, o.length), this.uintmode,
                    this.littleEndian);
        }


        public int Size()
        {
            return length;
        }


        public bool IsEmpty()
        {
            return length == 0 || bytebuffer == null;
        }

        public int Sum()
        {
            if (length == 0)
            {
                return 0;
            }
            int total = 0;
            for (int i = length - 1; i > -1; i--)
            {
                total += bytebuffer[i];
            }
            return total;
        }

        public int Average()
        {
            if (length == 0)
            {
                return 0;
            }
            return this.Sum() / length;
        }

        public sbyte Min()
        {
            sbyte v = this.bytebuffer[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                sbyte n = this.bytebuffer[i];
                if (n < v)
                {
                    v = n;
                }
            }
            return v;
        }

        public sbyte Max()
        {
            sbyte v = this.bytebuffer[0];
            int size = this.length;
            for (int i = size - 1; i > -1; i--)
            {
                sbyte n = this.bytebuffer[i];
                if (n > v)
                {
                    v = n;
                }
            }
            return v;
        }

        public string ToString(char split)
        {
            if (length == 0)
            {
                return "[]";
            }
            sbyte[] bytebuffer = this.bytebuffer;
            StrBuilder buffer = new StrBuilder(CollectionUtils.INITIAL_CAPACITY);
            buffer.Append('[');
            buffer.Append(StringUtils.ToHex(bytebuffer[0]));
            for (int i = 1; i < length; i++)
            {
                buffer.Append(split);
                buffer.Append(StringUtils.ToHex(bytebuffer[i]));
            }
            buffer.Append(']');
            return buffer.ToString();
        }


        public override string ToString()
        {
            return ToString(',');
        }


        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = length - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + bytebuffer[i];
            }
            return hashCode;
        }

        public UIntMode GetUIntMode()
        {
            return uintmode;
        }

        public bool IsLittleEndian()
        {
            return littleEndian;
        }
    }

}
