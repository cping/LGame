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
import loon.canvas.ImageFormat;

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

	public static ArrayByte at() {
		return new ArrayByte();
	}

	public static ArrayByte of(int length) {
		return new ArrayByte(length);
	}

	public static ArrayByte of(int length, int offset) {
		return new ArrayByte(length).setPosition(offset);
	}

	public static ArrayByte of(String base64) {
		return new ArrayByte(base64);
	}

	public static ArrayByte of(String base64, int pos, int order) {
		return new ArrayByte(base64, pos, order);
	}

	public static ArrayByte of(byte[] data) {
		return new ArrayByte(data);
	}

	public static int[] toIntArray(byte[] data, boolean length) {
		final int[] result;
		int n = (((data.length & 3) == 0) ? (data.length >>> 2) : ((data.length >>> 2) + 1));
		if (length) {
			result = new int[n + 1];
			result[n] = data.length;
		} else {
			result = new int[n];
		}
		n = data.length;
		for (int i = 0; i < n; i++) {
			result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
		}
		return result;
	}

	public static byte[] toByteArray(int[] data, boolean length) {
		int n = data.length << 2;
		if (length) {
			int m = data[data.length - 1];
			if (m > n) {
				return null;
			} else {
				n = m;
			}
		}
		final byte[] result = new byte[n];
		for (int i = 0; i < n; i++) {
			result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
		}
		return result;
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

	public static byte[] toZLIB(byte[] raw) {
		return toZLIB(BIG_ENDIAN, raw);
	}

	public static byte[] toZLIB(int order, byte[] raw) {
		ArrayByte zlib = new ArrayByte(raw.length + 6 + raw.length / 32000 * 5);
		zlib.setOrder(order);
		byte tmp = 8;
		zlib.writeByte(tmp);
		zlib.writeByte((31 - (tmp << 8) % 31) % 31);
		int pos = 0;
		while (raw.length - pos > 32000) {
			writeUncompressedDeflateBlock(zlib, false, raw, pos, 32000);
			pos += 32000;
		}
		writeUncompressedDeflateBlock(zlib, true, raw, pos, (char) (raw.length - pos));
		zlib.writeInt(calcADLER32(raw));
		return zlib.getBytes();
	}

	private static void writeUncompressedDeflateBlock(ArrayByte zlib, boolean last, byte[] raw, int off, int len) {
		zlib.writeByte((byte) (last ? 1 : 0));
		zlib.writeByte((byte) (len & 0xFF));
		zlib.writeByte((byte) ((len & 0xFF00) >> 8));
		zlib.writeByte((byte) ((len ^ 0xFFFFFFFF) & 0xFF));
		zlib.writeByte((byte) (((len ^ 0xFFFFFFFF) & 0xFF00) >> 8));
		zlib.write(raw, off, len);
	}

	private static int calcADLER32(byte[] raw) {
		int s1 = 1;
		int s2 = 0;
		for (int i = 0; i < raw.length; i++) {
			int abs = (raw[i] >= 0) ? raw[i] : (raw[i] + 256);
			s1 = (s1 + abs) % 65521;
			s2 = (s2 + s1) % 65521;
		}
		return (s2 << 16) + s1;
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

	private byte[] _buffer;

	private int _position;

	private int _byteOrder;

	private boolean _expandArray = true;

	public ArrayByte() {
		this(4096);
	}

	public ArrayByte(int length) {
		this(new byte[length]);
	}

	public ArrayByte(String base64) {
		this(base64, 0, BIG_ENDIAN);
	}

	public ArrayByte(String base64, int pos, int order) {
		if (!Base64Coder.isBase64(base64)) {
			throw new LSysException("it is not base64 :" + base64);
		}
		this.setBuffer(Base64Coder.decodeBase64(base64.toCharArray()), pos, order);
	}

	public ArrayByte(ArrayByte b) {
		this(b._buffer, b._position, b._byteOrder);
	}

	public ArrayByte(byte[] data) {
		this(data, 0, BIG_ENDIAN);
	}

	public ArrayByte(byte[] data, int pos, int order) {
		this.setBuffer(data, pos, order);
	}

	protected void setBuffer(byte[] data, int pos, int order) {
		this._buffer = data;
		this.setOrder(order);
		this._position = pos;
	}

	public ArrayByte setOrder(int type) {
		_expandArray = true;
		_position = 0;
		_byteOrder = type;
		return this;
	}

	public ArrayByte set(int idx, byte v) {
		_buffer[idx] = v;
		return this;
	}

	public ArrayByte reset() {
		return setOrder(_byteOrder);
	}

	public byte get(int idx) {
		return _buffer[idx];
	}

	public byte get() {
		return _buffer[_position++];
	}

	public int getByteOrder() {
		return _byteOrder;
	}

	public ArrayByte setByteOrder(int byteOrder) {
		this._byteOrder = byteOrder;
		return this;
	}

	public byte[] readByteArray(int readLength) throws Exception {
		byte[] readBytes = new byte[readLength];
		read(readBytes);
		return readBytes;
	}

	public int length() {
		return _buffer.length;
	}

	public ArrayByte setLength(int length) {
		if (length != _buffer.length) {
			byte[] oldData = _buffer;
			_buffer = new byte[length];
			System.arraycopy(oldData, 0, _buffer, 0, MathUtils.min(oldData.length, length));
			if (_position > length) {
				_position = length;
			}
		}
		return this;
	}

	public int position() {
		return _position;
	}

	public ArrayByte setPosition(int position) throws LSysException {
		if (position < 0 || position > _buffer.length) {
			throw new LSysException("ArrayByte Index Out Of Bounds !");
		}
		this._position = position;
		return this;
	}

	public void truncate() {
		setLength(_position);
	}

	public int available() {
		return length() - position();
	}

	private void checkAvailable(int length) throws LSysException {
		if (available() < length) {
			throw new LSysException("ArrayByte Index Out Of Bounds !");
		}
	}

	public int read() throws LSysException {
		checkAvailable(1);
		return _buffer[_position++] & 0xff;
	}

	public byte readByte() throws LSysException {
		checkAvailable(1);
		return _buffer[_position++];
	}

	public int read(byte[] buffer) throws LSysException {
		return read(buffer, 0, buffer.length);
	}

	public int read(byte[] buffer, int offset, int length) throws LSysException {
		if (length == 0) {
			return 0;
		}
		checkAvailable(length);
		System.arraycopy(_buffer, _position, buffer, offset, length);
		_position += length;
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

	public ArrayByte read(OutputStream out) throws IOException {
		out.write(_buffer, _position, _buffer.length - _position);
		_position = _buffer.length;
		return this;
	}

	public boolean readBoolean() throws LSysException {
		return (readByte() != 0);
	}

	protected int read2Byte() throws LSysException {
		checkAvailable(2);
		if (_byteOrder == LITTLE_ENDIAN) {
			return ((_buffer[_position++] & 0xff) | ((_buffer[_position++] & 0xff) << 8));
		} else {
			return (((_buffer[_position++] & 0xff) << 8) | (_buffer[_position++] & 0xff));
		}
	}

	public char readChar() throws LSysException {
		return (char) read2Byte();
	}

	public short readShort() throws LSysException {
		return (short) read2Byte();
	}

	public long readUInt8() throws LSysException {
		return (0x000000FF & (int) readByte());
	}

	public long readUInt16() throws LSysException {
		int firstByte = (0x000000FF & (int) readByte());
		int secondByte = (0x000000FF & (int) readByte());
		long result = 0;
		if (this._byteOrder == LITTLE_ENDIAN) {
			result = (long) ((secondByte << 8 | firstByte) & 0xFFFFFFFFL);
		} else {
			result = (long) ((firstByte << 8 | secondByte) & 0xFFFFFFFFL);
		}
		return result;
	}

	public long readUInt32() throws LSysException {
		int firstByte = (0x000000FF & (int) readByte());
		int secondByte = (0x000000FF & (int) readByte());
		int thirdByte = (0x000000FF & (int) readByte());
		int fourthByte = (0x000000FF & (int) readByte());
		long result = 0;
		if (this._byteOrder == LITTLE_ENDIAN) {
			result = ((long) (fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
		} else {
			result = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
		}
		return result;
	}

	public int readInt() throws LSysException {
		checkAvailable(4);
		if (_byteOrder == LITTLE_ENDIAN) {
			return (_buffer[_position++] & 0xff) | ((_buffer[_position++] & 0xff) << 8)
					| ((_buffer[_position++] & 0xff) << 16) | ((_buffer[_position++] & 0xff) << 24);
		} else {
			return ((_buffer[_position++] & 0xff) << 24) | ((_buffer[_position++] & 0xff) << 16)
					| ((_buffer[_position++] & 0xff) << 8) | (_buffer[_position++] & 0xff);
		}
	}

	public double readDouble() throws LSysException {
		return NumberUtils.longBitsToDouble(readLong());
	}

	public long readLong() throws LSysException {
		checkAvailable(8);
		if (_byteOrder == LITTLE_ENDIAN) {
			return (readInt() & 0xffffffffL) | ((readInt() & 0xffffffffL) << 32);
		} else {
			return ((readInt() & 0xffffffffL) << 32) | (readInt() & 0xffffffffL);
		}
	}

	public float readFloat() throws LSysException {
		return NumberUtils.intBitsToFloat(readInt());
	}

	public String readUTF() throws LSysException {
		checkAvailable(2);
		int utfLength = readShort() & 0xffff;
		checkAvailable(utfLength);

		int goalPosition = position() + utfLength;

		StrBuilder strings = new StrBuilder(utfLength);
		while (position() < goalPosition) {
			int a = readByte() & 0xff;
			if ((a & 0x80) == 0) {
				strings.append((char) a);
			} else {
				int b = readByte() & 0xff;
				if ((b & 0xc0) != 0x80) {
					throw new LSysException(String.valueOf(b));
				}

				if ((a & 0xe0) == 0xc0) {
					char ch = (char) (((a & 0x1f) << 6) | (b & 0x3f));
					strings.append(ch);
				} else if ((a & 0xf0) == 0xe0) {
					int c = readByte() & 0xff;
					if ((c & 0xc0) != 0x80) {
						throw new LSysException(String.valueOf(c));
					}
					char ch = (char) (((a & 0x0f) << 12) | ((b & 0x3f) << 6) | (c & 0x3f));
					strings.append(ch);
				} else {
					throw new LSysException(LSystem.NULL);
				}
			}
		}
		return strings.toString();
	}

	private void ensureCapacity(int dataSize) {
		if (_position + dataSize > _buffer.length) {
			if (_expandArray) {
				setLength((_position + dataSize) * 2);
			} else {
				setLength(_position + dataSize);
			}
		}
	}

	public ArrayByte writeByte(byte v) {
		ensureCapacity(1);
		_buffer[_position++] = v;
		return this;
	}

	public ArrayByte writeByte(int v) {
		ensureCapacity(1);
		_buffer[_position++] = (byte) v;
		return this;
	}

	public ArrayByte write(byte[] data) {
		write(data, 0, data.length);
		return this;
	}

	public ArrayByte write(byte[] data, int offset, int length) {
		if (length == 0) {
			return this;
		}
		ensureCapacity(length);
		System.arraycopy(data, offset, _buffer, _position, length);
		_position += length;
		return this;
	}

	public ArrayByte writeBoolean(boolean v) {
		return writeByte(v ? -1 : 0);
	}

	public ArrayByte writeChar(char v) {
		return writeShort(v);
	}

	public ArrayByte writeShort(int v) {
		ensureCapacity(2);
		if (_byteOrder == LITTLE_ENDIAN) {
			_buffer[_position++] = (byte) (v & 0xff);
			_buffer[_position++] = (byte) ((v >> 8) & 0xff);
		} else {
			_buffer[_position++] = (byte) ((v >> 8) & 0xff);
			_buffer[_position++] = (byte) (v & 0xff);
		}
		return this;
	}

	public ArrayByte writeInt(byte[] ba, int start, int len) {
		int end = start + len;
		for (int i = start; i < end; i++) {
			writeInt(ba[i]);
		}
		return this;
	}

	public ArrayByte writeInt(int v) {
		ensureCapacity(4);
		if (_byteOrder == LITTLE_ENDIAN) {
			_buffer[_position++] = (byte) (v & 0xff);
			_buffer[_position++] = (byte) ((v >> 8) & 0xff);
			_buffer[_position++] = (byte) ((v >> 16) & 0xff);
			_buffer[_position++] = (byte) (v >>> 24);
		} else {
			_buffer[_position++] = (byte) (v >>> 24);
			_buffer[_position++] = (byte) ((v >> 16) & 0xff);
			_buffer[_position++] = (byte) ((v >> 8) & 0xff);
			_buffer[_position++] = (byte) (v & 0xff);
		}
		return this;
	}

	public ArrayByte writeLong(long v) {
		ensureCapacity(8);
		if (_byteOrder == LITTLE_ENDIAN) {
			writeInt((int) (v & 0xffffffffL));
			writeInt((int) (v >>> 32));
		} else {
			writeInt((int) (v >>> 32));
			writeInt((int) (v & 0xffffffffL));
		}
		return this;
	}

	public ArrayByte writeFloat(float v) {
		return writeInt(NumberUtils.floatToIntBits(v));
	}

	public ArrayByte writeUTF(String s) throws LSysException {
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
			throw new LSysException(utfLength + " > 65535");
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
		return this;
	}

	public int capacity() {
		return _buffer.length;
	}

	public byte[] getData() {
		return _buffer;
	}

	public byte[] getBytes() {
		truncate();
		return _buffer;
	}

	public int limit() {
		return _buffer == null ? 0 : _buffer.length;
	}

	public boolean isExpandArray() {
		return _expandArray;
	}

	public ArrayByte setExpandArray(boolean expandArray) {
		this._expandArray = expandArray;
		return this;
	}

	public ArrayByte setArray(ArrayByte uarr, int offset) {
		int max = uarr.length() < (this.length() - offset) ? uarr.length() : (length() - offset);
		this.write(uarr._buffer, offset, max);
		return this;
	}

	public ArrayByte slice(int length) {
		return slice(this._position, length);
	}

	public ArrayByte slice(int begin, int end) {
		if (end == -1) {
			end = this.length();
		}
		int len = end - begin;
		ArrayByte bytes = new ArrayByte(len);
		bytes.write(this._buffer, begin, len);
		return bytes;
	}

	public byte[] unwrap() {
		try {
			int size = this.length();
			return this.slice(0, size).readByteArray(size);
		} catch (Exception e) {
			return null;
		}
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

	public String toBase64() {
		return toString();
	}

	public String toBase64Hex() {
		return CharUtils.toHex(Base64Coder.encode(_buffer));
	}

	public String toCRC32Hex() {
		return CRC32.toHexString(_buffer);
	}

	public String toCRC64Hex() {
		return CRC64.toHexString(_buffer);
	}

	public String toBase64ConvertCRC32Hex() {
		final byte[] bytes = Base64Coder.encode(_buffer);
		return CRC32.toHexString(bytes);
	}

	public String toBase64ConvertCRC64Hex() {
		final byte[] bytes = Base64Coder.encode(_buffer);
		return CRC64.toHexString(bytes);
	}

	public ImageFormat getImageFormat() {
		return new ImageFormat(this);
	}

	@Override
	public int size() {
		return length();
	}

	@Override
	public void clear() {
		this.reset();
		this._buffer = null;
		this._buffer = new byte[length()];
	}

	public ArrayByte cpy() {
		return new ArrayByte(CollectionUtils.copyOf(_buffer), _position, _byteOrder);
	}

	@Override
	public boolean isEmpty() {
		return this._buffer == null || length() == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArrayByte)) {
			return false;
		}
		final ArrayByte o = (ArrayByte) obj;
		final int size = size();
		if (size != o.size()) {
			return false;
		}
		final int order = this._byteOrder;
		final int rOrder = o._byteOrder;
		if (order != rOrder) {
			return false;
		}
		final int mark = this._position;
		final byte[] buf = this._buffer;
		final int rMark = o._position;
		final byte[] rBuf = o._buffer;
		for (int i = 0; i < size; ++i) {
			if (buf[i + mark] != rBuf[i + rMark]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return new String(Base64Coder.encode(_buffer));
	}

	@Override
	public int hashCode() {
		final int prime = 66;
		int hashCode = 1;
		for (int i = _buffer.length - 1; i > -1; i--) {
			hashCode = prime * LSystem.unite(hashCode, _buffer[i]);
		}
		return hashCode;
	}

	@Override
	public void close() {
		_buffer = null;
	}

}
