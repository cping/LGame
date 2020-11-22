package loon.utils;

import java.util.Arrays;

import loon.LSysException;
import loon.events.QueryEvent;

public class CharArray implements IArray {

	/**
	 * 产生一组指定范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static CharArray range(int start, int end) {
		CharArray array = new CharArray(end - start);
		for (int i = start; i < end; i++) {
			array.add((char) i);
		}
		return array;
	}

	/**
	 * 产生一组指定范围的随机数据
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static CharArray rangeRandomArrays(int begin, int end) {
		return rangeRandom(begin, end, (end - begin));
	}

	/**
	 * 产生一组指定范围的随机数据
	 * 
	 * @param begin
	 * @param end
	 * @param size
	 * @return
	 */
	public static CharArray rangeRandom(int begin, int end, int size) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		if ((end - begin) < size) {
			throw new LSysException("Size out Range between begin and end !");
		}
		char[] randSeed = new char[end - begin];
		for (int i = begin; i < end; i++) {
			randSeed[i - begin] = (char) i;
		}
		char[] charArrays = new char[size];
		for (int i = 0; i < size; i++) {
			final int len = randSeed.length - i - 1;
			int j = MathUtils.random(len);
			charArrays[i] = (char) randSeed[j];
			randSeed[j] = (char) randSeed[len];
		}
		return new CharArray(charArrays);
	}

	public char[] items;
	public int length;
	public boolean ordered;

	public CharArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public CharArray(int capacity) {
		this(true, capacity);
	}

	public CharArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		this.items = new char[capacity];
	}

	public CharArray(CharArray array) {
		this.ordered = array.ordered;
		length = array.length;
		items = new char[length];
		System.arraycopy(array.items, 0, items, 0, length);
	}

	public CharArray(char[] array) {
		this(true, array, 0, array.length);
	}

	public CharArray(char[] array,int size) {
		this(true, array, 0, size);
	}
	
	public CharArray(boolean ordered, char[] array, int startIndex, int count) {
		this(ordered, count);
		length = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void unshift(char value) {
		if (length > 0) {
			char[] items = this.items;
			char[] newItems = new char[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
		} else {
			add(value);
		}
	}

	public void push(char value) {
		add(value);
	}

	public void add(char value) {
		char[] items = this.items;
		if (length == items.length) {
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(CharArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(CharArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(char... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(char[] array, int offset, int len) {
		char[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.length) {
			items = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, this.length, len);
		this.length += len;
	}

	public char get(int index) {
		if (index >= length) {
			return 0;
		}
		return items[index];
	}

	public void set(int index, char value) {
		if (index >= length) {
			int size = length;
			for (int i = size; i < index + 1; i++) {
				add(' ');
			}
			items[index] = value;
			return;
		}
		items[index] = value;
	}

	public void incr(int index, char value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] += value;
	}

	public void mul(int index, char value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] *= value;
	}

	public void insert(int index, char value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		char[] items = this.items;
		if (length == items.length)
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, length - index);
		else
			items[length] = items[index];
		length++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new LSysException("first can't be >= length: " + first + " >= " + length);
		if (second >= length)
			throw new LSysException("second can't be >= length: " + second + " >= " + length);
		char[] items = this.items;
		char firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(char value) {
		int i = length - 1;
		char[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(char value) {
		char[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(char value) {
		char[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(char value) {
		char[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public int removeIndex(int index) {
		if (index >= length) {
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		char[] items = this.items;
		char value = items[index];
		length--;
		if (ordered) {
			System.arraycopy(items, index + 1, items, index, length - index);
		} else {
			items[index] = items[length];
		}
		return value;
	}

	public void removeRange(int start, int end) {
		if (end >= length) {
			throw new LSysException("end can't be >= length: " + end + " >= " + length);
		}
		if (start > end) {
			throw new LSysException("start can't be > end: " + start + " > " + end);
		}
		char[] items = this.items;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(items, start + count, items, start, length - (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		length -= count;
	}

	public boolean removeAll(CharArray array) {
		int length = this.length;
		int startlength = length;
		char[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			int item = array.get(i);
			for (int ii = 0; ii < length; ii++) {
				if (item == items[ii]) {
					removeIndex(ii);
					length--;
					break;
				}
			}
		}
		return length != startlength;
	}

	public int pop() {
		return items[--length];
	}

	public int shift() {
		return removeIndex(0);
	}

	public int peek() {
		return items[length - 1];
	}

	public int first() {
		if (length == 0) {
			throw new LSysException("Array is empty.");
		}
		return items[0];
	}

	@Override
	public void clear() {
		length = 0;
	}

	public char[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public char[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(MathUtils.max(8, lengthNeeded));
		return items;
	}

	protected char[] relength(int newlength) {
		char[] newItems = new char[newlength];
		char[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public CharArray sort() {
		Arrays.sort(items, 0, length);
		return this;
	}

	public void reverse() {
		char[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			char temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle() {
		char[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			char temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public char random() {
		if (length == 0) {
			return 0;
		}
		return items[MathUtils.random(0, length - 1)];
	}

	public CharArray randomCharArray() {
		return new CharArray(randomArrays());
	}

	public char[] randomArrays() {
		if (length == 0) {
			return new char[0];
		}
		char v = (char) -1;
		char[] newArrays = CollectionUtils.copyOf(items, length);
		for (int i = 0; i < length; i++) {
			v = random();
			for (int j = 0; j < i; j++) {
				if (newArrays[j] == v) {
					v = random();
					j = -1;
				}

			}
			newArrays[i] = v;
		}
		return newArrays;
	}

	public char[] toArray() {
		char[] array = new char[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof CharArray))
			return false;
		CharArray array = (CharArray) o;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}

	static public CharArray with(char... array) {
		return new CharArray(array);
	}

	public CharArray splice(int begin, int end) {
		CharArray longs = new CharArray(slice(begin, end));
		if (end - begin >= length) {
			items = new char[0];
			length = 0;
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static char[] slice(char[] array, int begin, int end) {
		if (begin > end) {
			throw new LSysException("CharArray begin > end");
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		char[] ret = new char[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static char[] slice(char[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public CharArray slice(int size) {
		return new CharArray(slice(this.items, size, this.length));
	}

	public CharArray slice(int begin, int end) {
		return new CharArray(slice(this.items, begin, end));
	}

	public static char[] concat(char[] array, char[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static char[] concat(char[] array, int alen, char[] other, int blen) {
		char[] ret = new char[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public CharArray concat(CharArray o) {
		return new CharArray(concat(this.items, this.length, o.items, o.length));
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0 || items == null;
	}
	
	public CharArray cpy() {
		return new CharArray(this);
	}

	public byte[] getBytes() {
		return getBytes(0);
	}

	public byte[] getBytes(int order) {
		char[] items = this.items;
		int size = items.length;
		ArrayByte bytes = new ArrayByte(size * 2);
		bytes.setOrder(order);
		for (int i = 0; i < size; i++) {
			bytes.writeChar(items[i]);
		}
		return bytes.getBytes();
	}

	public CharArray where(QueryEvent<Character> test) {
		CharArray list = new CharArray();
		for (int i = 0; i < length; i++) {
			Character t = Character.valueOf(get(i));
			if (test.hit(t)) {
				list.add(t);
			}
		}
		return list;
	}

	public Character find(QueryEvent<Character> test) {
		for (int i = 0; i < length; i++) {
			Character t = Character.valueOf(get(i));
			if (test.hit(t)) {
				return t;
			}
		}
		return null;
	}

	public boolean remove(QueryEvent<Character> test) {
		for (int i = length - 1; i > -1; i--) {
			Character t = get(i);
			if (test.hit(t)) {
				return removeValue(t);
			}
		}
		return false;
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		char[] items = this.items;
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
			buffer.append(split);
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	public String getString() {
		return new String(items);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = 31 * hashCode + items[i];
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(',');
	}
}
