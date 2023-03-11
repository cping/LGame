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
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.typedarrays.shared.TypedArrays;

/**
 * A buffer of ints.
 * <p>
 * A int buffer can be created in either of the following ways:
 * </p>
 * <ul>
 * <li>{@link #allocate(int) Allocate} a new int array and create a buffer based
 * on it;</li>
 * <li>{@link #wrap(int[]) Wrap} an existing int array to create a new
 * buffer;</li>
 * <li>Use {@link java.nio.ByteBuffer#asIntBuffer() ByteBuffer.asIntBuffer} to
 * create a int buffer based on a byte buffer.</li>
 * </ul>
 */
public final class IntBuffer extends Buffer implements Comparable<IntBuffer>, loon.jni.HasArrayBufferView {

	/**
	 * Sliced version of the underlying byte buffer, not the underlying byte buffer
	 * directly
	 */
	private final ByteBuffer byteBuffer;
	private final Int32Array intArray;

	static IntBuffer wrap(ByteBuffer byteBuffer) {
		return new IntBuffer(byteBuffer.slice());
	}

	/**
	 * Creates an int buffer based on a newly allocated int array.
	 *
	 * @param capacity the capacity of the new buffer.
	 * @return the created int buffer.
	 * @throws IllegalArgumentException if {@code capacity} is less than zero.
	 */
	public static IntBuffer allocate(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(capacity * 4);
		bb.order(ByteOrder.nativeOrder());
		return bb.asIntBuffer();
	}

	IntBuffer(ByteBuffer byteBuffer) {
		super((byteBuffer.capacity() >> 2));
		this.byteBuffer = byteBuffer;
		this.byteBuffer.clear();
		this.intArray = TypedArrays.createInt32Array(byteBuffer.byteArray.buffer(), byteBuffer.byteArray.byteOffset(),
				capacity);
	}

	/**
	 * Compacts this int buffer.
	 * <p>
	 * The remaining ints will be moved to the head of the buffer, starting from
	 * position zero. Then the position is set to {@code remaining()}; the limit is
	 * set to capacity; the mark is cleared.
	 * </p>
	 *
	 * @return this buffer.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public IntBuffer compact() {
		byteBuffer.limit(limit << 2);
		byteBuffer.position(position << 2);
		byteBuffer.compact();
		byteBuffer.clear();
		position = limit - position;
		limit = capacity;
		mark = UNSET_MARK;
		return this;
	}

	/**
	 * Compares the remaining ints of this buffer to another int buffer's remaining
	 * ints.
	 *
	 * @param otherBuffer another int buffer.
	 * @return a negative value if this is less than {@code other}; 0 if this equals
	 *         to {@code
	 *         other}; a positive value if this is greater than {@code other}.
	 * @exception ClassCastException if {@code other} is not an int buffer.
	 */
	public int compareTo(IntBuffer otherBuffer) {
		int compareRemaining = (remaining() < otherBuffer.remaining()) ? remaining() : otherBuffer.remaining();
		int thisPos = position;
		int otherPos = otherBuffer.position;
		// BEGIN android-changed
		int thisInt, otherInt;
		while (compareRemaining > 0) {
			thisInt = get(thisPos);
			otherInt = otherBuffer.get(otherPos);
			if (thisInt != otherInt) {
				return thisInt < otherInt ? -1 : 1;
			}
			thisPos++;
			otherPos++;
			compareRemaining--;
		}
		// END android-changed
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
	public IntBuffer duplicate() {
		IntBuffer buf = new IntBuffer(byteBuffer.duplicate());
		buf.limit = limit;
		buf.position = position;
		buf.mark = mark;
		return buf;
	}

	/**
	 * Checks whether this int buffer is equal to another object.
	 * <p>
	 * If {@code other} is not a int buffer then {@code false} is returned. Two int
	 * buffers are equal if and only if their remaining ints are exactly the same.
	 * Position, limit, capacity and mark are not considered.
	 * </p>
	 *
	 * @param other the object to compare with this int buffer.
	 * @return {@code true} if this int buffer is equal to {@code other},
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object other) {
		if (!(other instanceof IntBuffer)) {
			return false;
		}
		IntBuffer otherBuffer = (IntBuffer) other;

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
	 * Returns the int at the current position and increases the position by 1.
	 *
	 * @return the int at the current position.
	 * @exception BufferUnderflowException if the position is equal or greater than
	 *                                     limit.
	 */
	public int get() {
		// if (position == limit) {
		// throw new BufferUnderflowException();
		// }
		return intArray.get(position++);
	}

	/**
	 * Reads ints from the current position into the specified int array and
	 * increases the position by the number of ints read.
	 * <p>
	 * Calling this method has the same effect as {@code get(dest, 0, dest.length)}.
	 * </p>
	 *
	 * @param dest the destination int array.
	 * @return this buffer.
	 * @exception BufferUnderflowException if {@code dest.length} is greater than
	 *                                     {@code remaining()}.
	 */
	public IntBuffer get(int[] dest) {
		return get(dest, 0, dest.length);
	}

	/**
	 * Reads ints from the current position into the specified int array, starting
	 * from the specified offset, and increases the position by the number of ints
	 * read.
	 *
	 * @param dest the target int array.
	 * @param off  the offset of the int array, must not be negative and not greater
	 *             than {@code
	 * dest.length}.
	 * @param len  the number of ints to read, must be no less than zero and not
	 *             greater than {@code
	 * dest.length - off}.
	 * @return this buffer.
	 * @exception IndexOutOfBoundsException if either {@code off} or {@code len} is
	 *                                      invalid.
	 * @exception BufferUnderflowException  if {@code len} is greater than
	 *                                      {@code remaining()}.
	 */
	public IntBuffer get(int[] dest, int off, int len) {
		int length = dest.length;
		if (off < 0 || len < 0 || (long) len + (long) off > length) {
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
	 * Returns an int at the specified index; the position is not changed.
	 *
	 * @param index the index, must not be negative and less than limit.
	 * @return an int at the specified index.
	 * @exception IndexOutOfBoundsException if index is invalid.
	 */
	public int get(int index) {
		// if (index < 0 || index >= limit) {
		// throw new IndexOutOfBoundsException();
		// }
		return intArray.get(index);
	}

	/**
	 * Indicates whether this buffer is based on a int array and is read/write.
	 *
	 * @return {@code true} if this buffer is based on a int array and provides
	 *         read/write access, {@code false} otherwise.
	 */
	public final boolean hasArray() {
		return false;
	}

	/**
	 * Calculates this buffer's hash code from the remaining chars. The position,
	 * limit, capacity and mark don't affect the hash code.
	 *
	 * @return the hash code calculated from the remaining ints.
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
	 * An int buffer is direct if it is based on a byte buffer and the byte buffer
	 * is direct.
	 * </p>
	 *
	 * @return {@code true} if this buffer is direct, {@code false} otherwise.
	 */
	public boolean isDirect() {
		return true;
	}

	/**
	 * Returns the byte order used by this buffer when converting ints from/to
	 * bytes.
	 * <p>
	 * If this buffer is not based on a byte buffer, then always return the
	 * platform's native byte order.
	 * </p>
	 *
	 * @return the byte order used by this buffer when converting ints from/to
	 *         bytes.
	 */
	public ByteOrder order() {
		return ByteOrder.nativeOrder();
	}

	/**
	 * Writes the given int to the current position and increases the position by 1.
	 *
	 * @param i the int to write.
	 * @return this buffer.
	 * @exception BufferOverflowException if position is equal or greater than
	 *                                    limit.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public IntBuffer put(int c) {
		// if (position == limit) {
		// throw new BufferOverflowException();
		// }
		intArray.set(position++, c);
		return this;
	}

	/**
	 * Writes ints from the given int array to the current position and increases
	 * the position by the number of ints written.
	 * <p>
	 * Calling this method has the same effect as {@code put(src, 0, src.length)}.
	 * </p>
	 *
	 * @param src the source int array.
	 * @return this buffer.
	 * @exception BufferOverflowException if {@code remaining()} is less than
	 *                                    {@code src.length}.
	 * @exception ReadOnlyBufferException if no changes may be made to the contents
	 *                                    of this buffer.
	 */
	public final IntBuffer put(int[] src) {
		return put(src, 0, src.length);
	}

	/**
	 * Writes ints from the given int array, starting from the specified offset, to
	 * the current position and increases the position by the number of ints
	 * written.
	 *
	 * @param src the source int array.
	 * @param off the offset of int array, must not be negative and not greater than
	 *            {@code src.length}.
	 * @param len the number of ints to write, must be no less than zero and not
	 *            greater than {@code src.length - off}.
	 * @return this buffer.
	 * @exception BufferOverflowException   if {@code remaining()} is less than
	 *                                      {@code len}.
	 * @exception IndexOutOfBoundsException if either {@code off} or {@code len} is
	 *                                      invalid.
	 * @exception ReadOnlyBufferException   if no changes may be made to the
	 *                                      contents of this buffer.
	 */
	public IntBuffer put(int[] src, int off, int len) {
		int length = src.length;
		if (off < 0 || len < 0 || (long) len + (long) off > length) {
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
	 * Writes all the remaining ints of the {@code src} int buffer to this buffer's
	 * current position, and increases both buffers' position by the number of ints
	 * copied.
	 *
	 * @param src the source int buffer.
	 * @return this buffer.
	 * @exception BufferOverflowException  if {@code src.remaining()} is greater
	 *                                     than this buffer's {@code remaining()}.
	 * @exception IllegalArgumentException if {@code src} is this buffer.
	 * @exception ReadOnlyBufferException  if no changes may be made to the contents
	 *                                     of this buffer.
	 */
	public IntBuffer put(IntBuffer src) {
		if (src == this) {
			throw new IllegalArgumentException();
		}
		if (src.remaining() > remaining()) {
			throw new BufferOverflowException();
		}
		int[] contents = new int[src.remaining()];
		src.get(contents);
		put(contents);
		return this;
	}

	/**
	 * Write a int to the specified index of this buffer; the position is not
	 * changed.
	 *
	 * @param index the index, must not be negative and less than the limit.
	 * @param i     the int to write.
	 * @return this buffer.
	 * @exception IndexOutOfBoundsException if index is invalid.
	 * @exception ReadOnlyBufferException   if no changes may be made to the
	 *                                      contents of this buffer.
	 */
	public IntBuffer put(int index, int c) {
		// if (index < 0 || index >= limit) {
		// throw new IndexOutOfBoundsException();
		// }
		intArray.set(index, c);
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
	public IntBuffer slice() {
		byteBuffer.limit(limit << 2);
		byteBuffer.position(position << 2);
		IntBuffer result = new IntBuffer(byteBuffer.slice());
		byteBuffer.clear();
		return result;
	}

	/**
	 * Returns a string represents of the state of this int buffer.
	 *
	 * @return a string represents of the state of this int buffer.
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
		return intArray;
	}

	public int getElementSize() {
		return 4;
	}

	public int getElementType() {
		return 0x1404; // GL_INT
	}

	public boolean isReadOnly() {
		return false;
	}
}
