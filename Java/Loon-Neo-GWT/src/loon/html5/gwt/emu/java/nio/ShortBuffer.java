/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.nio;

import com.google.gwt.typedarrays.shared.ArrayBufferView;
import com.google.gwt.typedarrays.shared.Int16Array;
import com.google.gwt.typedarrays.shared.TypedArrays;

/**
 * A buffer of shorts.
 * <p>
 * A short buffer can be created in either of the following ways:
 * </p>
 * <ul>
 * <li>{@link #allocate(int) Allocate} a new short array and create a buffer
 * based on it;</li>
 * <li>{@link #wrap(short[]) Wrap} an existing short array to create a new
 * buffer;</li>
 * <li>Use {@link java.nio.ByteBuffer#asShortBuffer() ByteBuffer.asShortBuffer}
 * to create a short buffer based on a byte buffer.</li>
 * </ul>
 */
public final class ShortBuffer extends Buffer implements Comparable<ShortBuffer>, loon.jni.HasArrayBufferView {

	private final ByteBuffer byteBuffer;
	private final Int16Array shortArray;

	static ShortBuffer wrap(ByteBuffer byteBuffer) {
		return new ShortBuffer(byteBuffer.slice());
	}

	/**
	 * Creates a short buffer based on a newly allocated short array.
	 *
	 * @param capacity the capacity of the new buffer.
	 * @return the created short buffer.
	 * @throws IllegalArgumentException if {@code capacity} is less than zero.
	 */
	public static ShortBuffer allocate(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(capacity * 2);
		bb.order(ByteOrder.nativeOrder());
		return bb.asShortBuffer();
	}

	ShortBuffer(ByteBuffer byteBuffer) {
		super((byteBuffer.capacity() >> 1));
		this.byteBuffer = byteBuffer;
		this.byteBuffer.clear();
		this.shortArray = TypedArrays.createInt16Array(byteBuffer.byteArray.buffer(), byteBuffer.byteArray.byteOffset(),
				capacity);
	}

	/**
	 * Compacts this short buffer.
	 * <p>
	 * The remaining shorts will be moved to the head of the buffer, starting from
	 * position zero. Then the position is set to {@code remaining()}; the limit is
	 * set to capacity; the mark is cleared.
	 * </p>
	 *
	 * @return this buffer.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public ShortBuffer compact() {
		byteBuffer.limit(limit << 1);
		byteBuffer.position(position << 1);
		byteBuffer.compact();
		byteBuffer.clear();
		position = limit - position;
		limit = capacity;
		mark = UNSET_MARK;
		return this;
	}

	/**
	 * Compare the remaining shorts of this buffer to another short buffer's
	 * remaining shorts.
	 *
	 * @param otherBuffer another short buffer.
	 * @return a negative value if this is less than {@code otherBuffer}; 0 if this
	 *         equals to {@code otherBuffer}; a positive value if this is greater
	 *         than {@code otherBuffer}.
	 * @exception ClassCastException if {@code otherBuffer} is not a short buffer.
	 */
	public int compareTo(ShortBuffer otherBuffer) {
		int compareRemaining = (remaining() < otherBuffer.remaining()) ? remaining() : otherBuffer.remaining();
		int thisPos = position;
		int otherPos = otherBuffer.position;
		short thisByte, otherByte;
		while (compareRemaining > 0) {
			thisByte = get(thisPos);
			otherByte = otherBuffer.get(otherPos);
			if (thisByte != otherByte) {
				return thisByte < otherByte ? -1 : 1;
			}
			thisPos++;
			otherPos++;
			compareRemaining--;
		}
		return remaining() - otherBuffer.remaining();
	}

	/**
	 * Returns a duplicated buffer that shares its content with this buffer.
	 * <p>
	 * The duplicated buffer's position, limit, capacity and mark are the same as
	 * this buffer. The duplicated buffer's read-only property and byte order are
	 * the same as this buffer's.
	 * </p>
	 * <p>
	 * The new buffer shares its content with this buffer, which means either
	 * buffer's change of content will be visible to the other. The two buffer's
	 * position, limit and mark are independent.
	 * </p>
	 *
	 * @return a duplicated buffer that shares its content with this buffer.
	 */
	public ShortBuffer duplicate() {
		ShortBuffer buf = new ShortBuffer(byteBuffer.duplicate());
		buf.limit = limit;
		buf.position = position;
		buf.mark = mark;
		return buf;
	}

	/**
	 * Checks whether this short buffer is equal to another object.
	 * <p>
	 * If {@code other} is not a short buffer then {@code false} is returned. Two
	 * short buffers are equal if and only if their remaining shorts are exactly the
	 * same. Position, limit, capacity and mark are not considered.
	 * </p>
	 *
	 * @param other the object to compare with this short buffer.
	 * @return {@code true} if this short buffer is equal to {@code other},
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object other) {
		if (!(other instanceof ShortBuffer)) {
			return false;
		}
		ShortBuffer otherBuffer = (ShortBuffer) other;

		if (remaining() != otherBuffer.remaining()) {
			return false;
		}

		int myPosition = position;
		int otherPosition = otherBuffer.position;
		boolean equalSoFar = true;
		while (equalSoFar && (myPosition < limit)) {
			equalSoFar = get(myPosition++) == otherBuffer.get(otherPosition++);
		}

		return equalSoFar;
	}

	/**
	 * Returns the short at the current position and increases the position by 1.
	 *
	 * @return the short at the current position.
	 * @exception BufferUnderflowException if the position is equal or greater than
	 *                                     limit.
	 */
	public short get() {
		// if (position == limit) {
		// throw new BufferUnderflowException();
		// }
		return shortArray.get(position++);
	}

	/**
	 * Reads shorts from the current position into the specified short array and
	 * increases the position by the number of shorts read.
	 * <p>
	 * Calling this method has the same effect as {@code get(dest, 0, dest.length)}.
	 * </p>
	 *
	 * @param dest the destination short array.
	 * @return this buffer.
	 * @exception BufferUnderflowException if {@code dest.length} is greater than
	 *                                     {@code remaining()}.
	 */
	public ShortBuffer get(short[] dest) {
		return get(dest, 0, dest.length);
	}

	/**
	 * Reads shorts from the current position into the specified short array,
	 * starting from the specified offset, and increases the position by the number
	 * of shorts read.
	 *
	 * @param dest the target short array.
	 * @param off  the offset of the short array, must not be negative and not
	 *             greater than {@code
	 * dest.length}.
	 * @param len  the number of shorts to read, must be no less than zero and not
	 *             greater than {@code dest.length - off}.
	 * @return this buffer.
	 * @exception IndexOutOfBoundsException if either {@code off} or {@code len} is
	 *                                      invalid.
	 * @exception BufferUnderflowException  if {@code len} is greater than
	 *                                      {@code remaining()}.
	 */
	public ShortBuffer get(short[] dest, int off, int len) {
		int length = dest.length;
		if (off < 0 || len < 0 || (long) off + (long) len > length) {
			throw new IndexOutOfBoundsException();
		}
		if (len > remaining()) {
			throw new BufferUnderflowException();
		}
		for (int i = off; i < off + len; i++) {
			dest[i] = get();
		}
		return this;
	}

	/**
	 * Returns the short at the specified index; the position is not changed.
	 *
	 * @param index the index, must not be negative and less than limit.
	 * @return a short at the specified index.
	 * @exception IndexOutOfBoundsException if index is invalid.
	 */
	public short get(int index) {
		// if (index < 0 || index >= limit) {
		// throw new IndexOutOfBoundsException();
		// }
		return shortArray.get(index);
	}

	/**
	 * Indicates whether this buffer is based on a short array and is read/write.
	 *
	 * @return {@code true} if this buffer is based on a short array and provides
	 *         read/write access, {@code false} otherwise.
	 */
	public final boolean hasArray() {
		return false;
	}

	/**
	 * Calculates this buffer's hash code from the remaining chars. The position,
	 * limit, capacity and mark don't affect the hash code.
	 *
	 * @return the hash code calculated from the remaining shorts.
	 */
	public int hashCode() {
		int myPosition = position;
		int hash = 0;
		while (myPosition < limit) {
			hash = hash + get(myPosition++);
		}
		return hash;
	}

	/**
	 * Indicates whether this buffer is direct. A direct buffer will try its best to
	 * take advantage of native memory APIs and it may not stay in the Java heap, so
	 * it is not affected by garbage collection.
	 * <p>
	 * A short buffer is direct if it is based on a byte buffer and the byte buffer
	 * is direct.
	 * </p>
	 *
	 * @return {@code true} if this buffer is direct, {@code false} otherwise.
	 */
	public boolean isDirect() {
		return true;
	}

	/**
	 * Returns the byte order used by this buffer when converting shorts from/to
	 * bytes.
	 * <p>
	 * If this buffer is not based on a byte buffer, then always return the
	 * platform's native byte order.
	 * </p>
	 *
	 * @return the byte order used by this buffer when converting shorts from/to
	 *         bytes.
	 */
	public ByteOrder order() {
		return ByteOrder.nativeOrder();
	}

	/**
	 * Writes the given short to the current position and increases the position by
	 * 1.
	 *
	 * @param s the short to write.
	 * @return this buffer.
	 * @exception BufferOverflowException if position is equal or greater than
	 *                                    limit.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public ShortBuffer put(short c) {
		// if (position == limit) {
		// throw new BufferOverflowException();
		// }
		shortArray.set(position++, c);
		return this;
	}

	/**
	 * Writes shorts from the given short array to the current position and
	 * increases the position by the number of shorts written.
	 * <p>
	 * Calling this method has the same effect as {@code
	 * put(src, 0, src.length)}.
	 * </p>
	 *
	 * @param src the source short array.
	 * @return this buffer.
	 * @exception BufferOverflowException if {@code remaining()} is less than
	 *                                    {@code src.length}.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public final ShortBuffer put(short[] src) {
		return put(src, 0, src.length);
	}

	/**
	 * Writes shorts from the given short array, starting from the specified offset,
	 * to the current position and increases the position by the number of shorts
	 * written.
	 *
	 * @param src the source short array.
	 * @param off the offset of short array, must not be negative and not greater
	 *            than {@code src.length}.
	 * @param len the number of shorts to write, must be no less than zero and not
	 *            greater than {@code src.length - off}.
	 * @return this buffer.
	 * @exception BufferOverflowException   if {@code remaining()} is less than
	 *                                      {@code len}.
	 * @exception IndexOutOfBoundsException if either {@code off} or {@code len} is
	 *                                      invalid.
	 * @exception ReadOnlyBufferException   if no changes may be made to the
	 *                                      contents of this buffer.
	 */
	public ShortBuffer put(short[] src, int off, int len) {
		int length = src.length;
		if (off < 0 || len < 0 || (long) off + (long) len > length) {
			throw new IndexOutOfBoundsException();
		}

		if (len > remaining()) {
			throw new BufferOverflowException();
		}
		for (int i = off; i < off + len; i++) {
			put(src[i]);
		}
		return this;
	}

	/**
	 * Writes all the remaining shorts of the {@code src} short buffer to this
	 * buffer's current position, and increases both buffers' position by the number
	 * of shorts copied.
	 *
	 * @param src the source short buffer.
	 * @return this buffer.
	 * @exception BufferOverflowException  if {@code src.remaining()} is greater
	 *                                     than this buffer's {@code remaining()}.
	 * @exception IllegalArgumentException if {@code src} is this buffer.
	 * @exception ReadOnlyBufferException  if no changes may be made to the contents
	 *                                     of this buffer.
	 */
	public ShortBuffer put(ShortBuffer src) {
		if (src == this) {
			throw new IllegalArgumentException();
		}
		if (src.remaining() > remaining()) {
			throw new BufferOverflowException();
		}
		short[] contents = new short[src.remaining()];
		src.get(contents);
		put(contents);
		return this;
	}

	/**
	 * Writes a short to the specified index of this buffer; the position is not
	 * changed.
	 *
	 * @param index the index, must not be negative and less than the limit.
	 * @param s     the short to write.
	 * @return this buffer.
	 * @exception IndexOutOfBoundsException if index is invalid.
	 * @exception ReadOnlyBufferException   if no changes may be made to the
	 *                                      contents of this buffer.
	 */
	public ShortBuffer put(int index, short c) {
		// if (index < 0 || index >= limit) {
		// throw new IndexOutOfBoundsException();
		// }
		shortArray.set(index, c);
		return this;
	}

	/**
	 * Returns a sliced buffer that shares its content with this buffer.
	 * <p>
	 * The sliced buffer's capacity will be this buffer's {@code remaining()}, and
	 * its zero position will correspond to this buffer's current position. The new
	 * buffer's position will be 0, limit will be its capacity, and its mark is
	 * cleared. The new buffer's read-only property and byte order are same as this
	 * buffer's.
	 * </p>
	 * <p>
	 * The new buffer shares its content with this buffer, which means either
	 * buffer's change of content will be visible to the other. The two buffer's
	 * position, limit and mark are independent.
	 * </p>
	 *
	 * @return a sliced buffer that shares its content with this buffer.
	 */
	public ShortBuffer slice() {
		byteBuffer.limit(limit << 1);
		byteBuffer.position(position << 1);
		ShortBuffer result = new ShortBuffer(byteBuffer.slice());
		byteBuffer.clear();
		return result;
	}

	/**
	 * Returns a string representing the state of this short buffer.
	 *
	 * @return a string representing the state of this short buffer.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName());
		buf.append(", status: capacity="); //$NON-NLS-1$
		buf.append(capacity());
		buf.append(" position="); //$NON-NLS-1$
		buf.append(position());
		buf.append(" limit="); //$NON-NLS-1$
		buf.append(limit());
		return buf.toString();
	}

	public ArrayBufferView getTypedArray() {
		return shortArray;
	}

	public int getElementSize() {
		return 2;
	}

	public int getElementType() {
		return 0x1402; // GL_SHORT
	}

	public boolean isReadOnly() {
		return false;
	}
}
