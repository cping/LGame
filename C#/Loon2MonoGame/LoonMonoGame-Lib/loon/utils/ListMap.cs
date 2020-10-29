using java.lang;

namespace loon.utils
{
   public class ListMap<K, V> : IArray
	{

	public K[] keys;
	public V[] values;
	public int size;
	public bool ordered;

	public ListMap(): this(true, CollectionUtils.INITIAL_CAPACITY)
	{
		
	}

	public ListMap(int capacity) : this(true, capacity)
	{
	
	}

	public ListMap(bool ordered, int capacity)
	{
		this.ordered = ordered;
		keys = new K[capacity];
		values = new V[capacity];
	}

	public ListMap(ListMap<K, V> array) : this(array.ordered, array.size)
	{
		size = array.size;
		JavaSystem.Arraycopy(array.keys, 0, keys, 0, size);
        JavaSystem.Arraycopy(array.values, 0, values, 0, size);
			
	}

	public void Put(K key, V value)
	{
		if (size == keys.Length)
			Resize(MathUtils.Max(8, (int)(size * 1.75f)));
		int index = IndexOfKey(key);
		if (index == -1)
			index = size++;
		keys[index] = key;
		values[index] = value;
	}

	public void Put(K key, V value, int index)
	{
		if (size == keys.Length)
			Resize(MathUtils.Max(8, (int)(size * 1.75f)));
		int existingIndex = IndexOfKey(key);
		if (existingIndex != -1)
			RemoveIndex(existingIndex);
		JavaSystem.Arraycopy(keys, index, keys, index + 1, size - index);
		JavaSystem.Arraycopy(values, index, values, index + 1, size - index);
		keys[index] = key;
		values[index] = value;
		size++;
	}

	public void PutAll(ListMap<K, V> map)
	{
		PutAll(map, 0, map.size);
	}

	public void PutAll(ListMap<K, V> map, int offset, int length)
	{
		if (offset + length > map.size)
			throw new LSysException(
					"offset + length must be <= size: " + offset + " + "
							+ length + " <= " + map.size);
		int sizeNeeded = size + length - offset;
		if (sizeNeeded >= keys.Length)
			Resize(MathUtils.Max(8, (int)(sizeNeeded * 1.75f)));
		JavaSystem.Arraycopy(map.keys, offset, keys, size, length);
		JavaSystem.Arraycopy(map.values, offset, values, size, length);
		size += length;
	}

	public V Get(K key)
	{
		K[] keys = this.keys;
		int i = size - 1;
		if (key == null)
		{
			for (; i >= 0; i--)
			{
				if ((object)keys[i] == (object)key)
					return values[i];
			}
		}
		else
		{
			for (; i >= 0; i--)
			{
				if (key.Equals(keys[i]))
					return values[i];
			}
		}
		return default;
	}

	public K GetKey(V value, bool identity)
	{
		V[] values = this.values;
		int i = size - 1;
		if (identity || value == null)
		{
			for (; i >= 0; i--)
			{
				if ((object)values[i] == (object)value)
				{
					return keys[i];
				}
			}
		}
		else
		{
			for (; i >= 0; i--)
			{
				if (value.Equals(values[i]))
				{
					return keys[i];
				}
			}
		}
		return default;
	}

	public K GetKeyAt(int index)
	{
		if (index >= size)
		{
			throw new LSysException(StringExtensions.ValueOf(index));
		}
		return keys[index];
	}

	public V GetValueAt(int index)
	{
		if (index >= size)
		{
			throw new LSysException(StringExtensions.ValueOf(index));
		}
		return values[index];
	}

	public K FirstKey()
	{
		if (size == 0)
			throw new LSysException("Map is empty.");
		return keys[0];
	}

	public V FirstValue()
	{
		if (size == 0)
			throw new LSysException("Map is empty.");
		return values[0];
	}

	public void SetKey(int index, K key)
	{
		if (index >= size)
			throw new LSysException(StringExtensions.ValueOf(index));
		keys[index] = key;
	}

	public void SetValue(int index, V value)
	{
		if (index >= size)
			throw new LSysException(StringExtensions.ValueOf(index));
		values[index] = value;
	}

	public void Insert(int index, K key, V value)
	{
		if (index > size)
			throw new LSysException(StringExtensions.ValueOf(index));
		if (size == keys.Length)
			Resize(MathUtils.Max(8, (int)(size * 1.75f)));
		if (ordered)
		{
			JavaSystem.Arraycopy(keys, index, keys, index + 1, size - index);
			JavaSystem.Arraycopy(values, index, values, index + 1, size - index);
		}
		else
		{
			keys[size] = keys[index];
			values[size] = values[index];
		}
		size++;
		keys[index] = key;
		values[index] = value;
	}

	public bool ContainsKey(K key)
	{
		K[] keys = this.keys;
		int i = size - 1;
		if (key == null)
		{
			while (i >= 0)
				if ((object)keys[i--] == (object)key)
					return true;
		}
		else
		{
			while (i >= 0)
				if (key.Equals(keys[i--]))
					return true;
		}
		return false;
	}

	public bool ContainsValue(V value, bool identity)
	{
		V[] values = this.values;
		int i = size - 1;
		if (identity || value == null)
		{
			while (i >= 0)
				if ((object)values[i--] == (object)value)
					return true;
		}
		else
		{
			while (i >= 0)
				if (value.Equals(values[i--]))
					return true;
		}
		return false;
	}

	public int IndexOfKey(K key)
	{
		K[] keys = this.keys;
		if (key == null)
		{
			for (int i = 0, n = size; i < n; i++)
				if ((object)keys[i] == (object)key)
					return i;
		}
		else
		{
			for (int i = 0, n = size; i < n; i++)
				if (key.Equals(keys[i]))
					return i;
		}
		return -1;
	}

	public int IndexOfValue(V value, bool identity)
	{
		V[] values = this.values;
		if (identity || value == null)
		{
			for (int i = 0, n = size; i < n; i++)
				if ((object)values[i] == (object)value)
					return i;
		}
		else
		{
			for (int i = 0, n = size; i < n; i++)
				if (value.Equals(values[i]))
					return i;
		}
		return -1;
	}

	public V RemoveKey(K key)
	{
		K[] keys = this.keys;
		if (key == null)
		{
			for (int i = 0, n = size; i < n; i++)
			{
				if ((object)keys[i] == (object)key)
				{
					V value = values[i];
					RemoveIndex(i);
					return value;
				}
			}
		}
		else
		{
			for (int i = 0, n = size; i < n; i++)
			{
				if (key.Equals(keys[i]))
				{
					V value = values[i];
					RemoveIndex(i);
					return value;
				}
			}
		}
		return default;
	}

	public bool RemoveValue(V value, bool identity)
	{
		V[] values = this.values;
		if (identity || value == null)
		{
			for (int i = 0, n = size; i < n; i++)
			{
				if ((object)values[i] == (object)value)
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
				if (value.Equals(values[i]))
				{
					RemoveIndex(i);
					return true;
				}
			}
		}
		return false;
	}

	public void RemoveIndex(int index)
	{
		if (index >= size)
			throw new LSysException(StringExtensions.ValueOf(index));
		K[] keys = this.keys;
		size--;
		if (ordered)
		{
			JavaSystem.Arraycopy(keys, index + 1, keys, index, size - index);
			JavaSystem.Arraycopy(values, index + 1, values, index, size - index);
		}
		else
		{
			keys[index] = keys[size];
			values[index] = values[size];
		}
		keys[size] = default;
		values[size] = default;
	}

	public K PeekKey()
	{
		return keys[size - 1];
	}

	public V PeekValue()
	{
		return values[size - 1];
	}

	public void Clear()
	{
		K[] keys = this.keys;
		V[] values = this.values;
		for (int i = 0, n = size; i < n; i++)
		{
			keys[i] = default;
			values[i] = default;
		}
		size = 0;
	}

	public void Shrink()
	{
		Resize(size);
	}

	public void EnsureCapacity(int additionalCapacity)
	{
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= keys.Length)
			Resize(MathUtils.Max(8, sizeNeeded));
	}

	protected void Resize(int newSize)
	{
		K[] newKeys = new K[newSize];
		JavaSystem.Arraycopy(keys, 0, newKeys, 0,
				MathUtils.Min(keys.Length, newKeys.Length));
		this.keys = newKeys;
		V[] newValues = new V[newSize];
		JavaSystem.Arraycopy(values, 0, newValues, 0,
				MathUtils.Min(values.Length, newValues.Length));
		this.values = newValues;
	}

	public void Reverse()
	{
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++)
		{
			int ii = lastIndex - i;
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	public void Shuffle()
	{
		for (int i = size - 1; i >= 0; i--)
		{
			int ii = MathUtils.Random(i);
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	public void Truncate(int newSize)
	{
		if (size <= newSize)
			return;
		for (int i = newSize; i < size; i++)
		{
			keys[i] = default;
			values[i] = default;
		}
		size = newSize;
	}

	

	public override int GetHashCode()
	{
		int hashCode = 1;
		for (int i = size - 1; i > -1; i--)
		{
			hashCode = 31 * hashCode + (keys[i] == null ? 0 : keys[i].GetHashCode());
			hashCode = 31 * hashCode + (values[i] == null ? 0 : values[i].GetHashCode());
		}
		return hashCode;
	}

	
	public int Size()
	{
		return size;
	}

	public bool IsEmpty()
	{
		return size == 0 || keys == null || values == null;
	}

	public override string ToString()
	{
		if (size == 0)
			return "[]";
		K[] keys = this.keys;
		V[] values = this.values;
		StrBuilder buffer = new StrBuilder(32);
		buffer.Append('[');
		buffer.Append(keys[0]);
		buffer.Append('=');
		buffer.Append(values[0]);
		for (int i = 1; i < size; i++)
		{
			buffer.Append(", ");
			buffer.Append(keys[i]);
			buffer.Append('=');
			buffer.Append(values[i]);
		}
		buffer.Append(']');
		return buffer.ToString();
	}

}

}
