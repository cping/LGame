using java.lang;
using loon.events;

namespace loon.utils
{

	public class BoolArray : IArray
	{

	public static BoolArray Range(int start, int end, bool value)
	{
		BoolArray array = new BoolArray(end - start);
		for (int i = start; i < end; i++)
		{
			array.Add(value);
		}
		return array;
	}

	public static BoolArray RangeRandom(int begin, int end)
	{
		if (begin > end)
		{
			int temp = begin;
			begin = end;
			end = temp;
		}
		int size = end - begin;
		bool[] boolArrays = new bool[size];
		for (int i = 0; i < size; i++)
		{
			boolArrays[i] = MathUtils.RandomBoolean();
		}
		return new BoolArray(boolArrays);
	}

	public bool[] items;
	public int length;
	public bool ordered;

	public BoolArray(): this(true, CollectionUtils.INITIAL_CAPACITY)
	{
		
	}

	public BoolArray(int capacity): this(true, capacity)
	{
		
	}

	public BoolArray(bool ordered, int capacity)
	{
		this.ordered = ordered;
		items = new bool[capacity];
	}

	public BoolArray(BoolArray array)
	{
		this.ordered = array.ordered;
		length = array.length;
		items = new bool[length];
		JavaSystem.Arraycopy(array.items, 0, items, 0, length);
	}

	public BoolArray(bool[] array): this(true, array, 0, array.Length)
	{
		
	}

	public BoolArray(bool ordered, bool[] array, int startIndex, int count): this(ordered, count)
	{
		length = count;
		JavaSystem.Arraycopy(array, startIndex, items, 0, count);
	}

	public void Unshift(bool value)
	{
		if (length > 0)
		{
			bool[] items = this.items;
			bool[] newItems = new bool[length + 1];
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

	public void Push(bool value)
	{
		Add(value);
	}

	public void Add(bool value)
	{
		bool[] items = this.items;
		if (length == items.Length)
		{
			items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
		}
		items[length++] = value;
	}

	public void AddAll(BoolArray array)
	{
		AddAll(array, 0, array.length);
	}

	public void AddAll(BoolArray array, int offset, int length)
	{
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		AddAll(array.items, offset, length);
	}

	public void AddAll(params bool[] array)
	{
		AddAll(array, 0, array.Length);
	}

	public void AddAll(bool[] array, int offset, int len)
	{
		bool[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.Length)
		{
			items = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
		}
		JavaSystem.Arraycopy(array, offset, items, this.length, len);
		this.length += len;
	}

	public bool Get(int index)
	{
		if (index >= length)
		{
			return false;
		}
		return items[index];
	}

	public void Set(int index, bool value)
	{
		if (index >= length)
		{
			int size = length;
			for (int i = size; i < index + 1; i++)
			{
				Add(false);
			}
			items[index] = value;
			return;
		}
		items[index] = value;
	}

	public void Incr(int index, bool value)
	{
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] = !value;
	}

	public void Mul(int index, bool value)
	{
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] |= value;
	}

	public void Insert(int index, bool value)
	{
		if (index > length)
		{
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		bool[] items = this.items;
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
		bool[] items = this.items;
		bool firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public bool Contains(bool value)
	{
		int i = length - 1;
		bool[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int IndexOf(bool value)
	{
		bool[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int LastIndexOf(bool value)
	{
		bool[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public bool RemoveValue(bool value)
	{
		bool[] items = this.items;
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

	public bool RemoveIndex(int index)
	{
		if (index >= length)
		{
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		bool[] items = this.items;
		bool value = items[index];
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
		bool[] items = this.items;
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

	public bool RemoveAll(BoolArray array)
	{
		int length = this.length;
		int startlength = length;
		bool[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++)
		{
			bool item = array.Get(i);
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

	public bool Pop()
	{
		return items[--length];
	}

	public bool Shift()
	{
		return RemoveIndex(0);
	}

	public bool Peek()
	{
		return items[length - 1];
	}

	public bool First()
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

	public bool[] Shrink()
	{
		if (items.Length != length)
			Relength(length);
		return items;
	}

	public bool[] EnsureCapacity(int additionalCapacity)
	{
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.Length)
			Relength(MathUtils.Max(8, lengthNeeded));
		return items;
	}

	protected bool[] Relength(int newlength)
	{
		bool[] newItems = new bool[newlength];
		bool[] items = this.items;
		JavaSystem.Arraycopy(items, 0, newItems, 0, MathUtils.Min(length, newItems.Length));
		this.items = newItems;
		return newItems;
	}

	public BoolArray Sort()
	{
		for (int i = 0; i < length; i++)
		{
			bool swap = false;
			for (int j = 0; j < length - i; j++)
			{
				if (items[j + 1])
				{
					bool temp = items[j + 1];
					items[j + 1] = items[j];
					items[j] = temp;
					swap = true;
				}
			}
			if (!swap)
			{
				break;
			}
		}
		return this;
	}

	public void Reverse()
	{
		bool[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
		{
			int ii = lastIndex - i;
			bool temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void Shuffle()
	{
		bool[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
		{
			int ii = MathUtils.Random(i);
			bool temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void Truncate(int newlength)
	{
		if (length > newlength)
			length = newlength;
	}

	public bool Random()
	{
		if (length == 0)
		{
			return false;
		}
		return items[MathUtils.Random(0, length - 1)];
	}

	public BoolArray RandomBoolArray()
	{
		return new BoolArray(RandomArrays());
	}

	public bool[] RandomArrays()
	{
		if (length == 0)
		{
			return new bool[0];
		}
		bool v = false;
		bool[] newArrays = CollectionUtils.CopyOf(items, length);
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

	public bool[] ToArray()
	{
		bool[] array = new bool[length];
		JavaSystem.Arraycopy(items, 0, array, 0, length);
		return array;
	}

	public override bool Equals(object o)
	{
		if (o == this)
			return true;
		if (!(o is BoolArray))
			return false;
		BoolArray array = (BoolArray)o;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}

	static public BoolArray With(params bool[] array)
	{
		return new BoolArray(array);
	}

	public BoolArray Splice(int begin, int end)
	{
		BoolArray longs = new BoolArray(Slice(begin, end));
		if (end - begin >= length)
		{
			items = new bool[0];
			length = 0;
			return longs;
		}
		else
		{
			RemoveRange(begin, end - 1);
		}
		return longs;
	}

	public static bool[] Slice(bool[] array, int begin, int end)
	{
		if (begin > end)
		{
			throw new LSysException("BoolArray begin > end");
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
		bool[] ret = new bool[elements];
		JavaSystem.Arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static bool[] Slice(bool[] array, int begin)
	{
		return Slice(array, begin, array.Length);
	}

	public BoolArray Slice(int size)
	{
		return new BoolArray(Slice(this.items, size, this.length));
	}

	public BoolArray Slice(int begin, int end)
	{
		return new BoolArray(Slice(this.items, begin, end));
	}

	public static bool[] Concat(bool[] array, bool[] other)
	{
		return Concat(array, array.Length, other, other.Length);
	}

	public static bool[] Concat(bool[] array, int alen, bool[] other, int blen)
	{
		bool[] ret = new bool[alen + blen];
		JavaSystem.Arraycopy(array, 0, ret, 0, alen);
		JavaSystem.Arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public BoolArray Concat(BoolArray o)
	{
		return new BoolArray(Concat(this.items, this.length, o.items, o.length));
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
		bool[] items = this.items;
		ArrayByte bytes = new ArrayByte(items.Length);
		bytes.SetOrder(order);
		for (int i = 0; i < items.Length; i++)
		{
			bytes.WriteBoolean(items[i]);
		}
		return bytes.GetBytes();
	}

	public BoolArray Where(QueryEvent<Boolean> test)
	{
		BoolArray list = new BoolArray();
		for (int i = 0; i < length; i++)
		{
			Boolean t = Boolean.ValueOf(Get(i));
			if (test.Hit(t))
			{
				list.Add(t.BooleanValue());
			}
		}
		return list;
	}

	public bool Find(QueryEvent<Boolean> test)
	{
		for (int i = 0; i < length; i++)
		{
			Boolean t = Boolean.ValueOf(Get(i));
			if (test.Hit(t))
			{
				return t.BooleanValue();
			}
		}
		return false;
	}

	public bool Remove(QueryEvent<Boolean> test)
	{
		for (int i = length - 1; i > -1; i--)
		{
			Boolean t = Boolean.ValueOf(Get(i));
			if (test.Hit(t))
			{
				return RemoveValue(t.BooleanValue());
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
		bool[] items = this.items;
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

	public override int GetHashCode()
	{
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--)
		{
			hashCode = hashCode * 31 + (items[i] ? 1231 : 1237);
		}
		return hashCode;
	}
}

}
