using java.lang;
using java.util;
using loon.events;

namespace loon.utils
{
	public class IntArray : IArray
	{

	public static IntArray Range(int start, int end)
	{
		IntArray array = new IntArray(end - start);
		for (int i = start; i < end; i++)
		{
			array.Add(i);
		}
		return array;
	}

	public static IntArray RangeRandom(int begin, int end)
	{
		return RangeRandom(begin, end, (end - begin));
	}

	public static IntArray RangeRandom(int begin, int end, int size)
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
		int[] randSeed = new int[end - begin];
		for (int i = begin; i < end; i++)
		{
			randSeed[i - begin] = i;
		}
		int[] intArrays = new int[size];
		for (int i = 0; i < size; i++)
		{
			int len = randSeed.Length - i - 1;
			int j = MathUtils.Random(len);
			intArrays[i] = randSeed[j];
			randSeed[j] = randSeed[len];
		}
		return new IntArray(intArrays);
	}

	public int[] items;
	public int length;
	public bool ordered;

	public IntArray() : this(true, CollectionUtils.INITIAL_CAPACITY)
	{

	}

	public IntArray(int capacity) : this(true, capacity)
	{
	
	}

	public IntArray(bool ordered, int capacity)
	{
		this.ordered = ordered;
		items = new int[capacity];
	}

	public IntArray(IntArray array)
	{
		this.ordered = array.ordered;
		length = array.length;
		items = new int[length];
		JavaSystem.Arraycopy(array.items, 0, items, 0, length);
	}

	public IntArray(int[] array):this(true, array, 0, array.Length)
	{
		
	}

	public IntArray(bool ordered, int[] array, int startIndex, int count): this(ordered, count)
	{
		length = count;
		JavaSystem.Arraycopy(array, startIndex, items, 0, count);
	}

	public void Unshift(int value)
	{
		if (length > 0)
		{
			int[] items = this.items;
			int[] newItems = new int[length + 1];
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

	public void Push(int value)
	{
		Add(value);
	}

	public void Add(int value)
	{
		int[] items = this.items;
		if (length == items.Length)
		{
			items = Relength(MathUtils.Max(8, (int)(length * 1.75f)));
		}
		items[length++] = value;
	}

	public void AddAll(IntArray array)
	{
		AddAll(array, 0, array.length);
	}

	public void AddAll(IntArray array, int offset, int length)
	{
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		AddAll(array.items, offset, length);
	}

	public void AddAll(params int[] array)
	{
		AddAll(array, 0, array.Length);
	}

	public void AddAll(int[] array, int offset, int len)
	{
		int[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.Length)
		{
			items = Relength(MathUtils.Max(8, (int)(lengthNeeded * 1.75f)));
		}
		JavaSystem.Arraycopy(array, offset, items, this.length, len);
		this.length += len;
	}

	public int Get(int index)
	{
		if (index >= length)
		{
			return 0;
		}
		return items[index];
	}

	public void Set(int index, int value)
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
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] += value;
	}

	public void Mul(int index, int value)
	{
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] *= value;
	}

	public void Insert(int index, int value)
	{
		if (index > length)
		{
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		int[] items = this.items;
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
		int[] items = this.items;
		int firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public bool Contains(int value)
	{
		int i = length - 1;
		int[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int IndexOf(int value)
	{
		int[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int LastIndexOf(int value)
	{
		int[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public bool RemoveValue(int value)
	{
		int[] items = this.items;
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
		int[] items = this.items;
		int value = items[index];
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
		int[] items = this.items;
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

	public bool RemoveAll(IntArray array)
	{
		int length = this.length;
		int startlength = length;
		int[] items = this.items;
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

	public int[] Shrink()
	{
		if (items.Length != length)
			Relength(length);
		return items;
	}

	public int[] EnsureCapacity(int additionalCapacity)
	{
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.Length)
			Relength(MathUtils.Max(8, lengthNeeded));
		return items;
	}

	protected int[] Relength(int newlength)
	{
		int[] newItems = new int[newlength];
		int[] items = this.items;
		JavaSystem.Arraycopy(items, 0, newItems, 0, MathUtils.Min(length, newItems.Length));
		this.items = newItems;
		return newItems;
	}

	public IntArray Sort()
	{
		Arrays.Sort(items, 0, length);
		return this;
	}

	public bool IsSorted(bool order)
	{
		int[] arrays = this.items;
		int orderCount = 0;
		int temp = -1;
		int v = order ? Integer.MIN_VALUE_JAVA : Integer.MAX_VALUE_JAVA;
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
		int[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++)
		{
			int ii = lastIndex - i;
			int temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void Shuffle()
	{
		int[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
		{
			int ii = MathUtils.Random(i);
			int temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void Truncate(int newlength)
	{
		if (length > newlength)
			length = newlength;
	}

	public int Random()
	{
		if (length == 0)
		{
			return 0;
		}
		return items[MathUtils.Random(0, length - 1)];
	}

	public IntArray RandomIntArray()
	{
		return new IntArray(RandomArrays());
	}

	public int[] RandomArrays()
	{
		if (length == 0)
		{
			return new int[0];
		}
		int v = 0;
		int[] newArrays = CollectionUtils.CopyOf(items, length);
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
		if (!(o is IntArray))
			return false;
		IntArray array = (IntArray)o;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}


	static public IntArray With(params int[] array)
	{
		return new IntArray(array);
	}

	public IntArray Splice(int begin, int end)
	{
		IntArray longs = new IntArray(Slice(begin, end));
		if (end - begin >= length)
		{
			items = new int[0];
			length = 0;
			return longs;
		}
		else
		{
			RemoveRange(begin, end - 1);
		}
		return longs;
	}

	public static int[] Slice(int[] array, int begin, int end)
	{
		if (begin > end)
		{
			throw new LSysException("IntArray begin > end");
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
		int[] ret = new int[elements];
		JavaSystem.Arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static int[] Slice(int[] array, int begin)
	{
		return Slice(array, begin, array.Length);
	}

	public IntArray Slice(int size)
	{
		return new IntArray(Slice(this.items, size, this.length));
	}

	public IntArray Slice(int begin, int end)
	{
		return new IntArray(Slice(this.items, begin, end));
	}

	public static int[] Concat(int[] array, int[] other)
	{
		return Concat(array, array.Length, other, other.Length);
	}

	public static int[] Concat(int[] array, int alen, int[] other, int blen)
	{
		int[] ret = new int[alen + blen];
		JavaSystem.Arraycopy(array, 0, ret, 0, alen);
		JavaSystem.Arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public IntArray Concat(IntArray o)
	{
		return new IntArray(Concat(this.items, this.length, o.items, o.length));
	}

	public int Size()
	{
		return length;
	}

	public bool IsEmpty()
	{
		return length == 0 || items == null;
	}

	public byte[] GetBytes()
	{
		return GetBytes(0);
	}

	public byte[] GetBytes(int order)
	{
		int[] items = this.items;
		ArrayByte bytes = new ArrayByte(items.Length * 4);
		bytes.SetOrder(order);
		for (int i = 0; i < items.Length; i++)
		{
			bytes.WriteInt(items[i]);
		}
		return bytes.GetBytes();
	}

	public IntArray Where(QueryEvent<Integer> test)
	{
		IntArray list = new IntArray();
		for (int i = 0; i < length; i++)
		{
			Integer t = Integer.ValueOf(Get(i));
			if (test.Hit(t))
			{
				list.Add(t.IntValue());
			}
		}
		return list;
	}

	public Integer Find(QueryEvent<Integer> test)
	{
		for (int i = 0; i < length; i++)
		{
			Integer t = Integer.ValueOf(Get(i));
			if (test.Hit(t))
			{
				return t;
			}
		}
		return null;
	}

	public bool Remove(QueryEvent<Integer> test)
	{
		for (int i = length - 1; i > -1; i--)
		{
			Integer t = Integer.ValueOf(Get(i));
			if (test.Hit(t))
			{
				return RemoveValue(t.IntValue());
			}
		}
		return false;
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
			total += items[i];
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

	public int Min()
	{
		int v = this.items[0];
		int size = this.length;
		for (int i = size - 1; i > -1; i--)
		{
			int n = this.items[i];
			if (n < v)
			{
				v = n;
			}
		}
		return v;
	}

	public int Max()
	{
		int v = this.items[0];
		int size = this.length;
		for (int i = size - 1; i > -1; i--)
		{
			int n = this.items[i];
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
		int[] items = this.items;
		StrBuilder buffer = new StrBuilder();
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
			hashCode = 31 * hashCode + items[i];
		}
		return hashCode;
	}
}

}
