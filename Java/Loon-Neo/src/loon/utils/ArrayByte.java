/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.utils;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Comparator;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;

/**
 * Loon的Byte[]操作用类
 * 
 * 它的作用在于针对二进制数据进行读取,写入以及处理,总之是字节层操作时才会用到的工具类.
 */
public class ArrayByte implements IArray, LRelease {

	public interface ByteArrayComparator extends Comparator<byte[]> {

		int compare(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2,
				final int length2);
	}

	private static class ByteArrayDataComparator implements ByteArrayComparator {

		@Override
		public int compare(final byte[] buffer1, final byte[] buffer2) {
			return compare(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length);
		}

		@Override
		public int compare(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2,
				final int offset2, final int length2) {
			if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
				return 0;
			}
			final int enda = offset1 + length1;
			final int endb = offset2 + length2;
			for (int i = offset1, j = offset2; i < enda && j < endb; i++, j++) {
				int a = buffer1[i] & 0xff;
				int b = buffer2[j] & 0xff;
				if (a != b) {
					return a - b;
				}
			}
			return length1 - length2;
		}
	}

	private static final ByteArrayDataComparator BYTES_COMPARATOR = new ByteArrayDataComparator();

	public static boolean checkHead(byte[] head, byte[] target) {
		if (head == null || target == null) {
			return false;
		}
		if (target.length < head.length)
			return false;
		return getDefaultByteArrayComparator().compare(head, 0, head.length, target, 0, head.length) == 0;
	}

	public static ByteArrayComparator getDefaultByteArrayComparator() {
		return BYTES_COMPARATOR;
	}

	public static int compare(final byte[] a, final byte[] b) {
		return getDefaultByteArrayComparator().compare(a, b);
	}

	public static byte[] max(final byte[] a, final byte[] b) {
		return getDefaultByteArrayComparator().compare(a, b) > 0 ? a : b;
	}

	public static byte[] min(final byte[] a, final byte[] b) {
		return getDefaultByteArrayComparator().compare(a, b) < 0 ? a : b;
	}

	public static byte[] nullToEmpty(final byte[] bytes) {
		return bytes == null ? new byte[0] : bytes;
	}

	public static boolean isEmpty(final byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

	/**
	 * 返回指定字符串符合UTF8编码的子符长度
	 * 
	 * @param str
	 * @return
	 */
	public static int getUTF8ByteLength(CharSequence str) {
		int len = 0;
		int ch = 0;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch < 128) {
				len += 1;
			} else if (ch < 2048) {
				len += 2;
			} else if ((ch & 0xFC00) == 0xD800 && (str.charAt(i + 1) & 0xFC00) == 0xDC00) {
				++i;
				len += 4;
			} else {
				len += 3;
			}
		}
		return len;
	}

	/**
	 * 转化字符串为ArrayByte
	 * 
	 * @param str
	 * @return
	 */
	public static ArrayByte encodeUTF8(CharSequence str) {
		return encodeUTF8(str, BIG_ENDIAN);
	}

	/**
	 * 转化字符串为ArrayByte
	 * 
	 * @param str
	 * @param orderType
	 * @return
	 */
	public static ArrayByte encodeUTF8(CharSequence str, int orderType) {
		int offset = 0;
		int c1 = 0;
		int c2 = 0;
		IntArray buffer = new IntArray(getUTF8ByteLength(str));
		for (int i = 0; i < str.length(); i++) {
			c1 = str.charAt(i);
			if (c1 < 128) {
				buffer.set(offset++, c1);
			} else if (c1 < 2048) {
				buffer.set(offset++, c1 >> 6 | 192);
				buffer.set(offset++, c1 & 63 | 128);
			} else if ((c1 & 0xFC00) == 0xD800 && ((c2 = str.charAt(i + 1)) & 0xFC00) == 0xDC00) {
				c1 = 0x10000 + ((c1 & 0x03FF) << 10) + (c2 & 0x03FF);
				++i;
				buffer.set(offset++, c1 >> 18 | 240);
				buffer.set(offset++, c1 >> 12 & 63 | 128);
				buffer.set(offset++, c1 >> 6 & 63 | 128);
				buffer.set(offset++, c1 & 63 | 128);
			} else {
				buffer.set(offset++, c1 >> 12 | 224);
				buffer.set(offset++, c1 >> 6 & 63 | 128);
				buffer.set(offset++, c1 & 63 | 128);
			}
		}
		ArrayByte bytes = new ArrayByte(offset);
		bytes.setByteOrder(orderType);
		for (int i = 0; i < offset; i++) {
			bytes.writeByte(buffer.get(i));
		}
		return bytes;
	}

	// 倒序注入和读取字节数据,既多字节数字的[最高]有效字节位于字节序列的最前面,依次递减.
	public static final int BIG_ENDIAN = 0;

	// 正序注入和读取字节数据,既多字节数字的[最低]有效字节位于字节序列的最前面,依次递增.
	public static final int LITTLE_ENDIAN = 1;

	private byte[] data;

	private int position;

	private int byteOrder;

	private boolean expandArray = true;

	public ArrayByte() {
		this(4096);
	}

	public ArrayByte(int length) {
		this(new byte[length]);
	}

	public ArrayByte(String base64) {
		if (!Base64Coder.isBase64(base64)) {
			throw new LSysException("it is not base64 :" + base64);
		}
		this.data = Base64Coder.decodeBase64(base64.toCharArray());
		reset();
	}

	public ArrayByte(byte[] data) {
		this.data = data;
		reset();
	}

	public void reset() {
		setOrder(BIG_ENDIAN);
	}

	public void setOrder(int type) {
		expandArray = true;
		position = 0;
		byteOrder = type;
	}

	public byte get(int idx) {
		return data[idx];
	}

	public byte get() {
		return data[position++];
	}

	public int getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(int byteOrder) {
		this.byteOrder = byteOrder;
	}

	public byte[] readByteArray(int readLength) throws Exception {
		byte[] readBytes = new byte[readLength];
		read(readBytes);
		return readBytes;
	}

	public int length() {
		return data.length;
	}

	public void setLength(int length) {
		if (length != data.length) {
			byte[] oldData = data;
			data = new byte[length];
			System.arraycopy(oldData, 0, data, 0, MathUtils.min(oldData.length, length));
			if (position > length) {
				position = length;
			}
		}
	}

	public int position() {
		return position;
	}

	public void setPosition(int position) throws IndexOutOfBoundsException {
		if (position < 0 || position > data.length) {
			throw new IndexOutOfBoundsException();
		}
		this.position = position;
	}

	public void truncate() {
		setLength(position);
	}

	public int available() {
		return length() - position();
	}

	private void checkAvailable(int length) throws IndexOutOfBoundsException {
		if (available() < length) {
			throw new IndexOutOfBoundsException();
		}
	}

	public int read() throws IndexOutOfBoundsException {
		checkAvailable(1);
		return data[position++] & 0xff;
	}

	public byte readByte() throws IndexOutOfBoundsException {
		checkAvailable(1);
		return data[position++];
	}

	public int read(byte[] buffer) throws IndexOutOfBoundsException {
		return read(buffer, 0, buffer.length);
	}

	public int read(byte[] buffer, int offset, int length) throws IndexOutOfBoundsException {
		if (length == 0) {
			return 0;
		}
		checkAvailable(length);
		System.arraycopy(data, position, buffer, offset, length);
		position += length;
		return length;
	}

	public long skip(long n) {
		long remaining = n;
		int nr;
		if (n <= 0) {
			return 0;
		}
		int size = (int) MathUtils.min(2048, remaining);
		byte[] skipBuffer = new byte[size];
		while (remaining > 0) {
			nr = read(skipBuffer, 0, (int) MathUtils.min(size, remaining));
			if (nr < 0) {
				break;
			}
			remaining -= nr;
		}
		return n - remaining;
	}

	public void read(OutputStream out) throws IOException {
		out.write(data, position, data.length - position);
		position = data.length;
	}

	public boolean readBoolean() throws IndexOutOfBoundsException {
		return (readByte() != 0);
	}

	protected int read2Byte() throws IndexOutOfBoundsException {
		checkAvailable(2);
		if (byteOrder == LITTLE_ENDIAN) {
			return ((data[position++] & 0xff) | ((data[position++] & 0xff) << 8));
		} else {
			return (((data[position++] & 0xff) << 8) | (data[position++] & 0xff));
		}
	}

	public char readChar() throws IndexOutOfBoundsException {
		return (char) read2Byte();
	}

	public short readShort() throws IndexOutOfBoundsException {
		return (short) read2Byte();
	}

	public int readInt() throws IndexOutOfBoundsException {
		checkAvailable(4);
		if (byteOrder == LITTLE_ENDIAN) {
			return (data[position++] & 0xff) | ((data[position++] & 0xff) << 8) | ((data[position++] & 0xff) << 16)
					| ((data[position++] & 0xff) << 24);
		} else {
			return ((data[position++] & 0xff) << 24) | ((data[position++] & 0xff) << 16)
					| ((data[position++] & 0xff) << 8) | (data[position++] & 0xff);
		}
	}

	public double readDouble() throws IndexOutOfBoundsException {
		return NumberUtils.longBitsToDouble(readLong());
	}

	public long readLong() throws IndexOutOfBoundsException {
		checkAvailable(8);
		if (byteOrder == LITTLE_ENDIAN) {
			return (readInt() & 0xffffffffL) | ((readInt() & 0xffffffffL) << 32L);
		} else {
			return ((readInt() & 0xffffffffL) << 32L) | (readInt() & 0xffffffffL);
		}
	}

	public float readFloat() throws IndexOutOfBoundsException {
		return NumberUtils.intBitsToFloat(readInt());
	}

	public String readUTF() throws LSysException {
		checkAvailable(2);
		int utfLength = readShort() & 0xffff;
		checkAvailable(utfLength);

		int goalPosition = position() + utfLength;

		StringBuffer string = new StringBuffer(utfLength);
		while (position() < goalPosition) {
			int a = readByte() & 0xff;
			if ((a & 0x80) == 0) {
				string.append((char) a);
			} else {
				int b = readByte() & 0xff;
				if ((b & 0xc0) != 0x80) {
					throw new LSysException(String.valueOf(b));
				}

				if ((a & 0xe0) == 0xc0) {
					char ch = (char) (((a & 0x1f) << 6) | (b & 0x3f));
					string.append(ch);
				} else if ((a & 0xf0) == 0xe0) {
					int c = readByte() & 0xff;
					if ((c & 0xc0) != 0x80) {
						throw new LSysException(String.valueOf(c));
					}
					char ch = (char) (((a & 0x0f) << 12) | ((b & 0x3f) << 6) | (c & 0x3f));
					string.append(ch);
				} else {
					throw new LSysException("null");
				}
			}
		}
		return string.toString();
	}

	private void ensureCapacity(int dataSize) {
		if (position + dataSize > data.length) {
			if (expandArray) {
				setLength((position + dataSize) * 2);
			} else {
				setLength(position + dataSize);
			}
		}
	}

	public void writeByte(byte v) {
		ensureCapacity(1);
		data[position++] = v;
	}

	public void writeByte(int v) {
		ensureCapacity(1);
		data[position++] = (byte) v;
	}

	public void write(byte[] buffer) {
		write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int offset, int length) {
		if (length == 0) {
			return;
		}
		ensureCapacity(length);
		System.arraycopy(buffer, offset, data, position, length);
		position += length;
	}

	public void writeBoolean(boolean v) {
		writeByte(v ? -1 : 0);
	}

	public void writeChar(char v) {
		writeShort(v);
	}

	public void writeShort(int v) {
		ensureCapacity(2);
		if (byteOrder == LITTLE_ENDIAN) {
			data[position++] = (byte) (v & 0xff);
			data[position++] = (byte) ((v >> 8) & 0xff);
		} else {
			data[position++] = (byte) ((v >> 8) & 0xff);
			data[position++] = (byte) (v & 0xff);
		}
	}

	public void writeInt(byte[] ba, int start, int len) {
		int end = start + len;
		for (int i = start; i < end; i++) {
			writeInt(ba[i]);
		}
	}

	public void writeInt(int v) {
		ensureCapacity(4);
		if (byteOrder == LITTLE_ENDIAN) {
			data[position++] = (byte) (v & 0xff);
			data[position++] = (byte) ((v >> 8) & 0xff);
			data[position++] = (byte) ((v >> 16) & 0xff);
			data[position++] = (byte) (v >>> 24);
		} else {
			data[position++] = (byte) (v >>> 24);
			data[position++] = (byte) ((v >> 16) & 0xff);
			data[position++] = (byte) ((v >> 8) & 0xff);
			data[position++] = (byte) (v & 0xff);
		}
	}

	public void writeLong(long v) {
		ensureCapacity(8);
		if (byteOrder == LITTLE_ENDIAN) {
			writeInt((int) (v & 0xffffffffL));
			writeInt((int) (v >>> 32));
		} else {
			writeInt((int) (v >>> 32));
			writeInt((int) (v & 0xffffffffL));
		}
	}

	public void writeFloat(float v) {
		writeInt(NumberUtils.floatToIntBits(v));
	}

	public void writeUTF(String s) throws LSysException {

		int utfLength = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch > 0 && ch < 0x80) {
				utfLength++;
			} else if (ch == 0 || (ch >= 0x80 && ch < 0x800)) {
				utfLength += 2;
			} else {
				utfLength += 3;
			}
		}

		if (utfLength > 65535) {
			throw new LSysException(utfLength +" > 65535");
		}

		ensureCapacity(2 + utfLength);
		writeShort(utfLength);

		for (int i = 0; i < s.length(); i++) {
			int ch = s.charAt(i);
			if (ch > 0 && ch < 0x80) {
				writeByte(ch);
			} else if (ch == 0 || (ch >= 0x80 && ch < 0x800)) {
				writeByte(0xc0 | (0x1f & (ch >> 6)));
				writeByte(0x80 | (0x3f & ch));
			} else {
				writeByte(0xe0 | (0x0f & (ch >> 12)));
				writeByte(0x80 | (0x3f & (ch >> 6)));
				writeByte(0x80 | (0x3f & ch));
			}
		}
	}

	public byte[] getData() {
		return data;
	}

	public byte[] getBytes() {
		truncate();
		return data;
	}

	public int limit() {
		return data == null ? 0 : data.length;
	}

	public boolean isExpandArray() {
		return expandArray;
	}

	public void setExpandArray(boolean expandArray) {
		this.expandArray = expandArray;
	}

	public ArrayByte setArray(ArrayByte uarr, int offset) {
		int max = uarr.length() < (this.length() - offset) ? uarr.length() : (length() - offset);
		this.write(uarr.data, offset, max);
		return this;
	}

	public ArrayByte slice(int begin, int end) {
		if (end == -1) {
			end = this.length();
		}
		int len = end - begin;
		ArrayByte bytes = new ArrayByte(len);
		bytes.write(this.data, begin, len);
		return bytes;
	}

	public String toUTF8String() {
		byte[] buffer = getData();
		try {
			return new String(buffer, LSystem.ENCODING);
		} catch (Throwable e) {
			return new String(buffer);
		}
	}

	public ArrayByte cryptARC4Data(String privateKey) {
		return ARC4.cryptData(privateKey, this);
	}

	public String cryptMD5Data() {
		return MD5.get().encryptBytes(this);
	}

	@Override
	public int size() {
		return length();
	}

	@Override
	public void clear() {
		this.reset();
		this.data = new byte[length()];
	}

	@Override
	public boolean isEmpty() {
		return this.data == null || length() == 0;
	}

	@Override
	public String toString() {
		return new String(Base64Coder.encode(data));
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = data.length - 1; i > -1; i--) {
			hashCode = 31 * hashCode + data[i];
		}
		return hashCode;
	}

	@Override
	public void close() {
		data = null;
	}

}
