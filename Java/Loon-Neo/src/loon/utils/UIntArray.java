/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils;

import java.util.Arrays;

import loon.LSysException;

public class UIntArray implements IArray {

	public enum UIntMode {
		UINT8, UINT16, UINT32
	}

	private final UIntMode uintmode;

	private byte[] bytebuffer;

	private int length;

	private int position = 0;

	private boolean ordered;

	private boolean littleEndian;

	public UIntArray() {
		this(false);
	}

	public UIntArray(boolean little) {
		this(UIntMode.UINT8, little);
	}

	public UIntArray(UIntMode mode, boolean little) {
		this(true, CollectionUtils.INITIAL_CAPACITY, mode, little);
	}

	public UIntArray(int capacity, UIntMode mode, boolean little) {
		this(true, capacity, mode, little);
	}

	public UIntArray(boolean ordered, int capacity, UIntMode mode, boolean little) {
		this.uintmode = mode;
		this.littleEndian = little;
		this.ordered = ordered;
		bytebuffer = new byte[capacity];
	}

	public UIntArray(UIntArray array, UIntMode mode, boolean little) {
		this.uintmode = mode;
		this.littleEndian = little;
		this.ordered = array.ordered;
		length = array.length;
		bytebuffer = new byte[length];
		System.arraycopy(array.bytebuffer, 0, bytebuffer, 0, length);
	}

	public UIntArray(byte[] array, UIntMode mode, boolean little) {
		this(true, array, 0, array.length, mode, little);
	}

	public UIntArray(boolean ordered, byte[] array, int startIndex, int count, UIntMode mode, boolean little) {
		this(ordered, count, mode, little);
		length = count;
		System.arraycopy(array, startIndex, bytebuffer, 0, count);
	}

	private void checkAvailable(int length) throws IndexOutOfBoundsException {
		if (available() < length) {
			throw new IndexOutOfBoundsException();
		}
	}

	public int readInt() {
		return readByte() & 0xff;
	}

	public String readByteHex() {
		return StringUtils.toHex(readByte());
	}

	public byte readByte() {
		checkAvailable(1);
		if (position + 1 > length) {
			return 0;
		}
		return get(position++);
	}

	public boolean writeByte(byte value) {
		byte[] bytebuffer = this.bytebuffer;
		if (length == bytebuffer.length) {
			bytebuffer = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		if (this.length > position && position >= 0) {
			bytebuffer[position++] = value;
		} else {
			bytebuffer[this.length] = value;
			this.length++;
			this.position++;
		}
		if (position > length) {
			position = length;
		}
		return true;
	}

	public boolean writeByte(int index, byte value) {
		byte[] bytebuffer = this.bytebuffer;
		if (length == bytebuffer.length || index >= bytebuffer.length) {
			bytebuffer = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		if (this.length > index && index >= 0) {
			bytebuffer[index] = value;
		} else {
			bytebuffer[index] = value;
			this.length++;
			this.position++;
		}
		return true;
	}

	public boolean writeUInt(long value) {
		if (length + 4 >= bytebuffer.length) {
			bytebuffer = ensureCapacity(4);
		}
		byte firstByte = (byte) ((value & 0xFF000000L) >> 24);
		byte secondByte = (byte) ((value & 0x00FF0000L) >> 16);
		byte thirdByte = (byte) ((value & 0x0000FF00L) >> 8);
		byte fourthByte = (byte) (value & 0x000000FFL);
		switch (this.uintmode) {
		case UINT8:
			return writeByte((byte) (value & 0xff));
		case UINT16:
			if (this.littleEndian) {
				writeByte(fourthByte);
				writeByte(thirdByte);
			} else {
				writeByte(thirdByte);
				writeByte(fourthByte);
			}
			return true;
		case UINT32:
			if (this.littleEndian) {
				writeByte(fourthByte);
				writeByte(thirdByte);
				writeByte(secondByte);
				writeByte(firstByte);
			} else {
				writeByte(firstByte);
				writeByte(secondByte);
				writeByte(thirdByte);
				writeByte(fourthByte);
			}
			return true;
		}
		return false;
	}

	public long readUInt() {
		long result = 0l;
		int firstByte = 0;
		int secondByte = 0;
		int thirdByte = 0;
		int fourthByte = 0;
		switch (this.uintmode) {
		case UINT8:
			return (0x000000FF & (int) readByte());
		case UINT16:
			firstByte = (0x000000FF & (int) readByte());
			secondByte = (0x000000FF & (int) readByte());
			if (littleEndian) {
				result = ((long) (secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
			} else {
				result = ((long) (firstByte << 8 | secondByte)) & 0xFFFFFFFFL;
			}
			return result;
		case UINT32:
			firstByte = (0x000000FF & (int) readByte());
			secondByte = (0x000000FF & (int) readByte());
			thirdByte = (0x000000FF & (int) readByte());
			fourthByte = (0x000000FF & (int) readByte());
			if (littleEndian) {
				result = ((long) (fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
			} else {
				result = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;

			}
			return result;
		}
		return result;
	}

	public byte getByte(int index) {
		return get(index);
	}

	public int position() {
		return position;
	}

	public void position(int p) {
		setPosition(p);
	}

	public void setPosition(int position) throws IndexOutOfBoundsException {
		if (position < 0 || position > bytebuffer.length || position > length) {
			throw new IndexOutOfBoundsException();
		}
		this.position = position;
	}

	public int available() {
		return size() - position();
	}

	public void unshift(byte value) {
		if (length > 0) {
			byte[] bytebuffer = this.bytebuffer;
			byte[] newItems = new byte[length + 1];
			newItems[0] = value;
			System.arraycopy(bytebuffer, 0, newItems, 1, length);
			this.length = newItems.length;
			this.bytebuffer = newItems;
		} else {
			writeByte(value);
		}
	}

	public void addAll(UIntArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(UIntArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.bytebuffer, offset, length);
	}

	public void addAll(byte... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(byte[] array, int offset, int length) {
		byte[] bytebuffer = this.bytebuffer;
		int lengthNeeded = length + length;
		if (lengthNeeded > bytebuffer.length) {
			bytebuffer = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, bytebuffer, length, length);
		length += length;
	}

	public byte get(int index) {
		if (index >= length) {
			return 0;
		}
		return bytebuffer[index];
	}

	public void set(int index, byte value) {
		if (index >= length) {
			return;
		}
		bytebuffer[index] = value;
	}

	public void incr(int index, byte value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		bytebuffer[index] += value;
	}

	public void mul(int index, byte value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		bytebuffer[index] *= value;
	}

	public void insert(int index, byte value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		byte[] bytebuffer = this.bytebuffer;
		if (length == bytebuffer.length)
			bytebuffer = relength(MathUtils.max(8, (byte) (length * 1.75f)));
		if (ordered)
			System.arraycopy(bytebuffer, index, bytebuffer, index + 1, length - index);
		else
			bytebuffer[length] = bytebuffer[index];
		length++;
		bytebuffer[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new LSysException("first can't be >= length: " + first + " >= " + length);
		if (second >= length)
			throw new LSysException("second can't be >= length: " + second + " >= " + length);
		byte[] bytebuffer = this.bytebuffer;
		byte firstValue = bytebuffer[first];
		bytebuffer[first] = bytebuffer[second];
		bytebuffer[second] = firstValue;
	}

	public boolean contains(byte value) {
		int i = length - 1;
		byte[] bytebuffer = this.bytebuffer;
		while (i >= 0)
			if (bytebuffer[i--] == value)
				return true;
		return false;
	}

	public int indexOf(byte value) {
		byte[] bytebuffer = this.bytebuffer;
		for (int i = 0, n = length; i < n; i++)
			if (bytebuffer[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(byte value) {
		byte[] bytebuffer = this.bytebuffer;
		for (int i = length - 1; i >= 0; i--)
			if (bytebuffer[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(byte value) {
		byte[] bytebuffer = this.bytebuffer;
		for (int i = 0, n = length; i < n; i++) {
			if (bytebuffer[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public byte removeIndex(int index) {
		if (index >= length) {
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		byte[] bytebuffer = this.bytebuffer;
		byte value = bytebuffer[index];
		length--;
		if (ordered) {
			System.arraycopy(bytebuffer, index + 1, bytebuffer, index, length - index);
		} else {
			bytebuffer[index] = bytebuffer[length];
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
		byte[] bytebuffer = this.bytebuffer;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(bytebuffer, start + count, bytebuffer, start, length - (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				bytebuffer[start + i] = bytebuffer[lastIndex - i];
		}
		length -= count;
	}

	public boolean removeAll(UIntArray array) {
		int length = this.length;
		int startlength = length;
		byte[] bytebuffer = this.bytebuffer;
		for (int i = 0, n = array.length; i < n; i++) {
			int item = array.get(i);
			for (int ii = 0; ii < length; ii++) {
				if (item == bytebuffer[ii]) {
					removeIndex(ii);
					length--;
					break;
				}
			}
		}
		return length != startlength;
	}

	public byte pop() {
		return bytebuffer[--length];
	}

	public byte shift() {
		return removeIndex(0);
	}

	public byte peek() {
		return bytebuffer[length - 1];
	}

	public byte first() {
		if (length == 0) {
			throw new LSysException("Array is empty.");
		}
		return bytebuffer[0];
	}

	@Override
	public void clear() {
		length = 0;
	}

	public byte[] shrink() {
		if (bytebuffer.length != length)
			relength(length);
		return bytebuffer;
	}

	public byte[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > bytebuffer.length)
			relength(MathUtils.max(8, lengthNeeded));
		return bytebuffer;
	}

	protected byte[] relength(int newlength) {
		byte[] newItems = new byte[newlength];
		byte[] bytebuffer = this.bytebuffer;
		System.arraycopy(bytebuffer, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.bytebuffer = newItems;
		return newItems;
	}

	public UIntArray sort() {
		Arrays.sort(bytebuffer, 0, length);
		return this;
	}

	public boolean isSorted(boolean order) {
		final byte[] arrays = this.bytebuffer;
		int orderCount = 0;
		byte temp = -1;
		byte v = order ? Byte.MIN_VALUE : Byte.MAX_VALUE;
		for (int i = 0; i < length; i++) {
			temp = v;
			v = arrays[i];
			if (order) {
				if (temp <= v) {
					orderCount++;
				}
			} else {
				if (temp >= v) {
					orderCount++;
				}
			}
		}
		return orderCount == length;
	}

	public void reverse() {
		byte[] bytebuffer = this.bytebuffer;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			byte temp = bytebuffer[i];
			bytebuffer[i] = bytebuffer[ii];
			bytebuffer[ii] = temp;
		}
	}

	public void shuffle() {
		byte[] bytebuffer = this.bytebuffer;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			byte temp = bytebuffer[i];
			bytebuffer[i] = bytebuffer[ii];
			bytebuffer[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public byte random() {
		if (length == 0) {
			return 0;
		}
		return bytebuffer[MathUtils.random(0, length - 1)];
	}

	public UIntArray randomIntArray() {
		return new UIntArray(randomArrays(), this.uintmode, this.littleEndian);
	}

	public byte[] randomArrays() {
		if (length == 0) {
			return new byte[0];
		}
		byte v = 0;
		byte[] newArrays = CollectionUtils.copyOf(bytebuffer, length);
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

	public byte[] toArray() {
		byte[] array = new byte[length];
		System.arraycopy(bytebuffer, 0, array, 0, length);
		return array;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof UIntArray))
			return false;
		UIntArray array = (UIntArray) object;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (bytebuffer[i] != array.bytebuffer[i])
				return false;
		return true;
	}

	static public UIntArray with(UIntMode mode, byte... array) {
		return with(mode, false, array);
	}

	static public UIntArray with(UIntMode mode, boolean little, byte... array) {
		return new UIntArray(array, mode, little);
	}

	public UIntArray splice(int begin, int end) {
		UIntArray longs = new UIntArray(slice(begin, end), this.uintmode, this.littleEndian);
		if (end - begin >= length) {
			bytebuffer = new byte[0];
			length = 0;
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static byte[] slice(byte[] array, int begin, int end) {
		if (begin > end) {
			throw new LSysException("UIntArray begin > end");
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		byte[] ret = new byte[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static byte[] slice(byte[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public UIntArray slice(int size) {
		return new UIntArray(slice(this.bytebuffer, size, this.length), this.uintmode, this.littleEndian);
	}

	public UIntArray slice(int begin, int end) {
		return new UIntArray(slice(this.bytebuffer, begin, end), this.uintmode, this.littleEndian);
	}

	public static byte[] concat(byte[] array, byte[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static byte[] concat(byte[] array, int alen, byte[] other, int blen) {
		byte[] ret = new byte[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public UIntArray concat(UIntArray o) {
		return new UIntArray(concat(this.bytebuffer, this.length, o.bytebuffer, o.length), this.uintmode,
				this.littleEndian);
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0 || bytebuffer == null;
	}

	public int sum() {
		if (length == 0) {
			return 0;
		}
		int total = 0;
		for (int i = length - 1; i > -1; i--) {
			total += bytebuffer[i];
		}
		return total;
	}

	public int average() {
		if (length == 0) {
			return 0;
		}
		return this.sum() / length;
	}

	public byte min() {
		byte v = this.bytebuffer[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			byte n = this.bytebuffer[i];
			if (n < v) {
				v = n;
			}
		}
		return v;
	}

	public byte max() {
		byte v = this.bytebuffer[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			byte n = this.bytebuffer[i];
			if (n > v) {
				v = n;
			}
		}
		return v;
	}

	public String toString(String separator) {
		if (length == 0)
			return "";
		byte[] bytebuffer = this.bytebuffer;
		StrBuilder buffer = new StrBuilder(32);
		buffer.append(StringUtils.toHex(bytebuffer[0]));
		for (int i = 1; i < length; i++) {
			buffer.append(separator);
			buffer.append(StringUtils.toHex(bytebuffer[i]));
		}
		return buffer.toString();
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		byte[] bytebuffer = this.bytebuffer;
		StrBuilder buffer = new StrBuilder(CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		buffer.append(StringUtils.toHex(bytebuffer[0]));
		for (int i = 1; i < length; i++) {
			buffer.append(split);
			buffer.append(StringUtils.toHex(bytebuffer[i]));
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public String toString() {
		return toString(',');
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = 31 * hashCode + bytebuffer[i];
		}
		return hashCode;
	}

	public UIntMode getUIntMode() {
		return uintmode;
	}

	public boolean isLittleEndian() {
		return littleEndian;
	}

}
