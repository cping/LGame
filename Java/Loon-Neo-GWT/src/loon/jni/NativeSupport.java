/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.jni;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.Support;
import loon.canvas.LColor;

public final class NativeSupport implements Support {

	@Override
	public void makeBuffer(byte[] data, int size, int tag) {
		for (int i = 0; i < size; i++) {
			data[i] ^= tag;
		}
	}

	@Override
	public void copy(float[] src, Buffer dst, int numFloats) {
		copy(src, dst, 0, numFloats);
	}

	@Override
	public void copy(float[] src, Buffer dst, int offset, int numFloats) {
		putBuffer(dst, src, offset, numFloats);
	}

	@Override
	public IntBuffer newIntBuffer(final int[] src) {
		if (src == null) {
			return null;
		}
		int size = src.length;
		IntBuffer buffer = newIntBuffer(size);
		copy(src, 0, buffer, size);
		return buffer;
	}

	@Override
	public FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		FloatBuffer buffer = newFloatBuffer(numFloats);
		copy(src, buffer, offset, numFloats);
		return buffer;
	}

	@Override
	public void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	@Override
	public void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	@Override
	public void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	@Override
	public void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	private void putBuffer(Buffer dst, Object src, int offset, int numFloats) {
		if (dst instanceof ByteBuffer) {
			if (src instanceof byte[]) {
				ByteBuffer byteBuffer = (ByteBuffer) dst;
				int oldPosition = byteBuffer.position();
				byteBuffer.put((byte[]) src, offset, numFloats);
				byteBuffer.position(oldPosition);
				byteBuffer.limit(oldPosition + numFloats);
			} else {
				FloatBuffer floatBuffer = asFloatBuffer(dst);
				floatBuffer.clear();
				dst.position(0);
				floatBuffer.put((float[]) src, offset, numFloats);
				dst.position(0);
				dst.limit(numFloats << 2);
			}
		} else if (dst instanceof ShortBuffer) {
			ShortBuffer buffer = (ShortBuffer) dst;
			int oldPosition = buffer.position();
			buffer.put((short[]) src, offset, numFloats);
			buffer.position(oldPosition);
			buffer.limit(oldPosition + numFloats);
		} else if (dst instanceof IntBuffer) {
			IntBuffer buffer = (IntBuffer) dst;
			int[] source = (int[]) src;
			int oldPosition = buffer.position();
			buffer.put(source, offset, numFloats);
			buffer.position(oldPosition);
			buffer.limit(oldPosition + numFloats);
		} else if (dst instanceof FloatBuffer) {
			FloatBuffer floatBuffer = asFloatBuffer(dst);
			floatBuffer.clear();
			dst.position(0);
			floatBuffer.put((float[]) src, offset, numFloats);
			dst.position(0);
			dst.limit(numFloats);
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
		dst.position(0);
	}

	private final static FloatBuffer asFloatBuffer(final Buffer data) {
		FloatBuffer buffer = null;
		if (data instanceof ByteBuffer)
			buffer = ((ByteBuffer) data).asFloatBuffer();
		else if (data instanceof FloatBuffer)
			buffer = (FloatBuffer) data;
		if (buffer == null)
			throw new RuntimeException(
					"data must be a ByteBuffer or FloatBuffer");
		return buffer;
	}

	@Override
	public ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	@Override
	public FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	@Override
	public ByteBuffer getByteBuffer(byte[] bytes) {
		ByteBuffer buffer = newByteBuffer(bytes.length).put(bytes);
		buffer.position(0);
		return buffer;

	}

	@Override
	public FloatBuffer getFloatBuffer(float[] floats) {
		FloatBuffer buffer = newFloatBuffer(floats.length).put(floats);
		buffer.position(0);
		return buffer;
	}

	@Override
	public ByteBuffer newByteBuffer(int numBytes) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	@Override
	public FloatBuffer newFloatBuffer(int numFloats) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	@Override
	public ShortBuffer newShortBuffer(int numShorts) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asShortBuffer();
	}

	@Override
	public IntBuffer newIntBuffer(int numInts) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	public void put(final Buffer buffer, final float[] source,
			final int offset, final int length) {
		putBuffer(buffer, source, offset, length);
	}

	private int allocatedUnsafe = 0;

	@Override
	public int getAllocatedBytesUnsafe() {
		return allocatedUnsafe;
	}

	@Override
	public void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		freeMemory(buffer);
	}

	public ByteBuffer newUnsafeByteBuffer(int numBytes) {
		return newByteBuffer(numBytes);
	}

	@Override
	public ByteBuffer allocateDirect(final int capacity) {
		return ByteBuffer.allocateDirect(capacity);
	}

	private void freeMemory(Buffer buffer) {
		buffer.clear();
		buffer = null;
	}

	@Override
	public void clear(Buffer buffer) {
		buffer.clear();
	}

	@Override
	public final void filterColor(int maxPixel, int pixelStart, int pixelEnd,
			int[] src, int[] dst, int[] colors, int c1, int c2) {

		final int length = src.length;
		if (pixelStart < pixelEnd) {
			final int start = pixelStart + 1;
			final int end = pixelEnd + 1;
			if (end > maxPixel) {
				return;
			}
			for (int i = 0; i < length; i++) {
				if (dst[i] != 0xffffff) {
					for (int pixIndex = start; pixIndex < end; pixIndex++) {
						if (colors[pixIndex] == src[i]) {
							dst[i] = 0xffffff;
						} else if (src[i] == c1) {
							dst[i] = 0xffffff;
						}
					}
				}
			}
		} else {
			final int start = pixelEnd - 1;
			final int end = pixelStart;
			if (start < 0) {
				return;
			}
			for (int i = 0; i < length; i++) {
				if (dst[i] != 0xffffff) {
					for (int pixIndex = start; pixIndex < end; pixIndex++) {
						if (colors[pixIndex] == src[i]) {
							dst[i] = 0xffffff;
						} else if (src[i] == c2) {
							dst[i] = 0xffffff;
						}
					}
				}
			}
		}

	}

	@Override
	public void filterFractions(int size, float[] fractions, int width,
			int height, int[] pixels, int numElements) {

		int x, y;
		int idx = 0;
		for (int j = 0; j < size; j++) {
			idx = j * numElements;
			if (fractions[idx + 4] != 0xffffff) {
				if (fractions[idx + 5] <= 0) {
					fractions[idx + 0] += fractions[idx + 2];
					fractions[idx + 1] += fractions[idx + 3];
					fractions[idx + 3] += 0.1;
				} else {
					fractions[idx + 5]--;
				}
				x = (int) fractions[idx + 0];
				y = (int) fractions[idx + 1];
				if (x > -1 && y > -1 && x < width && y < height) {
					pixels[x + y * width] = (int) fractions[idx + 4];
				}
			}
		}

	}

	public final int M00 = 0;
	public final int M01 = 4;
	public final int M02 = 8;
	public final int M03 = 12;
	public final int M10 = 1;
	public final int M11 = 5;
	public final int M12 = 9;
	public final int M13 = 13;
	public final int M20 = 2;
	public final int M21 = 6;
	public final int M22 = 10;
	public final int M23 = 14;
	public final int M30 = 3;
	public final int M31 = 7;
	public final int M32 = 11;
	public final int M33 = 15;

	@Override
	public void mul(float[] mata, float[] matb) {

		float[] tmp = new float[16];
		tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02]
				* matb[M20] + mata[M03] * matb[M30];
		tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02]
				* matb[M21] + mata[M03] * matb[M31];
		tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02]
				* matb[M22] + mata[M03] * matb[M32];
		tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02]
				* matb[M23] + mata[M03] * matb[M33];
		tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12]
				* matb[M20] + mata[M13] * matb[M30];
		tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12]
				* matb[M21] + mata[M13] * matb[M31];
		tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12]
				* matb[M22] + mata[M13] * matb[M32];
		tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12]
				* matb[M23] + mata[M13] * matb[M33];
		tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22]
				* matb[M20] + mata[M23] * matb[M30];
		tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22]
				* matb[M21] + mata[M23] * matb[M31];
		tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22]
				* matb[M22] + mata[M23] * matb[M32];
		tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22]
				* matb[M23] + mata[M23] * matb[M33];
		tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32]
				* matb[M20] + mata[M33] * matb[M30];
		tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32]
				* matb[M21] + mata[M33] * matb[M31];
		tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32]
				* matb[M22] + mata[M33] * matb[M32];
		tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32]
				* matb[M23] + mata[M33] * matb[M33];
		System.arraycopy(tmp, 0, mata, 0, 16);

	}

	@Override
	public void mulVec(float[] mat, float[] vec) {

		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02]
				+ mat[M03];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12]
				+ mat[M13];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22]
				+ mat[M23];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	@Override
	public void mulVec(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			mulVec(mat, vecPtr);
		}

	}

	@Override
	public void prj(float[] mat, float[] vec) {

		float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2]
				* mat[M32] + mat[M33]);
		float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03])
				* inv_w;
		float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13])
				* inv_w;
		float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23])
				* inv_w;
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	@Override
	public void prj(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			prj(mat, vecPtr);
		}

	}

	@Override
	public void rot(float[] mat, float[] vec) {

		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	@Override
	public void rot(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			rot(mat, vecPtr);
		}

	}

	@Override
	public boolean inv(float[] values) {

		float[] tmp = new float[16];
		float l_det = det(values);
		if (l_det == 0)
			return false;
		tmp[M00] = values[M12] * values[M23] * values[M31] - values[M13]
				* values[M22] * values[M31] + values[M13] * values[M21]
				* values[M32] - values[M11] * values[M23] * values[M32]
				- values[M12] * values[M21] * values[M33] + values[M11]
				* values[M22] * values[M33];
		tmp[M01] = values[M03] * values[M22] * values[M31] - values[M02]
				* values[M23] * values[M31] - values[M03] * values[M21]
				* values[M32] + values[M01] * values[M23] * values[M32]
				+ values[M02] * values[M21] * values[M33] - values[M01]
				* values[M22] * values[M33];
		tmp[M02] = values[M02] * values[M13] * values[M31] - values[M03]
				* values[M12] * values[M31] + values[M03] * values[M11]
				* values[M32] - values[M01] * values[M13] * values[M32]
				- values[M02] * values[M11] * values[M33] + values[M01]
				* values[M12] * values[M33];
		tmp[M03] = values[M03] * values[M12] * values[M21] - values[M02]
				* values[M13] * values[M21] - values[M03] * values[M11]
				* values[M22] + values[M01] * values[M13] * values[M22]
				+ values[M02] * values[M11] * values[M23] - values[M01]
				* values[M12] * values[M23];
		tmp[M10] = values[M13] * values[M22] * values[M30] - values[M12]
				* values[M23] * values[M30] - values[M13] * values[M20]
				* values[M32] + values[M10] * values[M23] * values[M32]
				+ values[M12] * values[M20] * values[M33] - values[M10]
				* values[M22] * values[M33];
		tmp[M11] = values[M02] * values[M23] * values[M30] - values[M03]
				* values[M22] * values[M30] + values[M03] * values[M20]
				* values[M32] - values[M00] * values[M23] * values[M32]
				- values[M02] * values[M20] * values[M33] + values[M00]
				* values[M22] * values[M33];
		tmp[M12] = values[M03] * values[M12] * values[M30] - values[M02]
				* values[M13] * values[M30] - values[M03] * values[M10]
				* values[M32] + values[M00] * values[M13] * values[M32]
				+ values[M02] * values[M10] * values[M33] - values[M00]
				* values[M12] * values[M33];
		tmp[M13] = values[M02] * values[M13] * values[M20] - values[M03]
				* values[M12] * values[M20] + values[M03] * values[M10]
				* values[M22] - values[M00] * values[M13] * values[M22]
				- values[M02] * values[M10] * values[M23] + values[M00]
				* values[M12] * values[M23];
		tmp[M20] = values[M11] * values[M23] * values[M30] - values[M13]
				* values[M21] * values[M30] + values[M13] * values[M20]
				* values[M31] - values[M10] * values[M23] * values[M31]
				- values[M11] * values[M20] * values[M33] + values[M10]
				* values[M21] * values[M33];
		tmp[M21] = values[M03] * values[M21] * values[M30] - values[M01]
				* values[M23] * values[M30] - values[M03] * values[M20]
				* values[M31] + values[M00] * values[M23] * values[M31]
				+ values[M01] * values[M20] * values[M33] - values[M00]
				* values[M21] * values[M33];
		tmp[M22] = values[M01] * values[M13] * values[M30] - values[M03]
				* values[M11] * values[M30] + values[M03] * values[M10]
				* values[M31] - values[M00] * values[M13] * values[M31]
				- values[M01] * values[M10] * values[M33] + values[M00]
				* values[M11] * values[M33];
		tmp[M23] = values[M03] * values[M11] * values[M20] - values[M01]
				* values[M13] * values[M20] - values[M03] * values[M10]
				* values[M21] + values[M00] * values[M13] * values[M21]
				+ values[M01] * values[M10] * values[M23] - values[M00]
				* values[M11] * values[M23];
		tmp[M30] = values[M12] * values[M21] * values[M30] - values[M11]
				* values[M22] * values[M30] - values[M12] * values[M20]
				* values[M31] + values[M10] * values[M22] * values[M31]
				+ values[M11] * values[M20] * values[M32] - values[M10]
				* values[M21] * values[M32];
		tmp[M31] = values[M01] * values[M22] * values[M30] - values[M02]
				* values[M21] * values[M30] + values[M02] * values[M20]
				* values[M31] - values[M00] * values[M22] * values[M31]
				- values[M01] * values[M20] * values[M32] + values[M00]
				* values[M21] * values[M32];
		tmp[M32] = values[M02] * values[M11] * values[M30] - values[M01]
				* values[M12] * values[M30] - values[M02] * values[M10]
				* values[M31] + values[M00] * values[M12] * values[M31]
				+ values[M01] * values[M10] * values[M32] - values[M00]
				* values[M11] * values[M32];
		tmp[M33] = values[M01] * values[M12] * values[M20] - values[M02]
				* values[M11] * values[M20] + values[M02] * values[M10]
				* values[M21] - values[M00] * values[M12] * values[M21]
				- values[M01] * values[M10] * values[M22] + values[M00]
				* values[M11] * values[M22];

		float inv_det = 1.0f / l_det;
		values[M00] = tmp[M00] * inv_det;
		values[M01] = tmp[M01] * inv_det;
		values[M02] = tmp[M02] * inv_det;
		values[M03] = tmp[M03] * inv_det;
		values[M10] = tmp[M10] * inv_det;
		values[M11] = tmp[M11] * inv_det;
		values[M12] = tmp[M12] * inv_det;
		values[M13] = tmp[M13] * inv_det;
		values[M20] = tmp[M20] * inv_det;
		values[M21] = tmp[M21] * inv_det;
		values[M22] = tmp[M22] * inv_det;
		values[M23] = tmp[M23] * inv_det;
		values[M30] = tmp[M30] * inv_det;
		values[M31] = tmp[M31] * inv_det;
		values[M32] = tmp[M32] * inv_det;
		values[M33] = tmp[M33] * inv_det;
		return true;

	}

	@Override
	public float det(float[] values) {

		return values[M30] * values[M21] * values[M12] * values[M03]
				- values[M20] * values[M31] * values[M12] * values[M03]
				- values[M30] * values[M11] * values[M22] * values[M03]
				+ values[M10] * values[M31] * values[M22] * values[M03]
				+ values[M20] * values[M11] * values[M32] * values[M03]
				- values[M10] * values[M21] * values[M32] * values[M03]
				- values[M30] * values[M21] * values[M02] * values[M13]
				+ values[M20] * values[M31] * values[M02] * values[M13]
				+ values[M30] * values[M01] * values[M22] * values[M13]
				- values[M00] * values[M31] * values[M22] * values[M13]
				- values[M20] * values[M01] * values[M32] * values[M13]
				+ values[M00] * values[M21] * values[M32] * values[M13]
				+ values[M30] * values[M11] * values[M02] * values[M23]
				- values[M10] * values[M31] * values[M02] * values[M23]
				- values[M30] * values[M01] * values[M12] * values[M23]
				+ values[M00] * values[M31] * values[M12] * values[M23]
				+ values[M10] * values[M01] * values[M32] * values[M23]
				- values[M00] * values[M11] * values[M32] * values[M23]
				- values[M20] * values[M11] * values[M02] * values[M33]
				+ values[M10] * values[M21] * values[M02] * values[M33]
				+ values[M20] * values[M01] * values[M12] * values[M33]
				- values[M00] * values[M21] * values[M12] * values[M33]
				- values[M10] * values[M01] * values[M22] * values[M33]
				+ values[M00] * values[M11] * values[M22] * values[M33];

	}

	@Override
	public int[] toColorKey(int[] buffer, int colorKey) {

		int size = buffer.length;
		for (int i = 0; i < size; i++) {
			int pixel = buffer[i];
			if (pixel == colorKey) {
				buffer[i] = 0x00FFFFFF;
			}
		}

		return buffer;
	}

	@Override
	public int[] toColorKeys(int[] buffer, int[] colors) {

		int length = colors.length;
		int size = buffer.length;
		for (int n = 0; n < length; n++) {
			for (int i = 0; i < size; i++) {
				int pixel = buffer[i];
				if (pixel == colors[n]) {
					buffer[i] = 0x00FFFFFF;
				}
			}
		}

		return buffer;
	}

	@Override
	public int[] toColorKeyLimit(int[] buffer, int start, int end) {

		int sred = LColor.getRed(start);
		int sgreen = LColor.getGreen(start);
		int sblue = LColor.getBlue(start);
		int ered = LColor.getRed(end);
		int egreen = LColor.getGreen(end);
		int eblue = LColor.getBlue(end);
		int size = buffer.length;
		for (int i = 0; i < size; i++) {
			int pixel = buffer[i];
			int r = LColor.getRed(pixel);
			int g = LColor.getGreen(pixel);
			int b = LColor.getBlue(pixel);
			if ((r >= sred && g >= sgreen && b >= sblue)
					&& (r <= ered && g <= egreen && b <= eblue)) {
				buffer[i] = 0x00FFFFFF;
			}
		}

		return buffer;
	}

	@Override
	public int[] toGray(int[] buffer, int w, int h) {

		int size = w * h;
		int[] newResult = new int[size];
		System.arraycopy(buffer, 0, newResult, 0, size);
		int alpha = 0xFF << 24;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int idx = w * i + j;
				int color = newResult[idx];
				if (color != 0x00FFFFFF) {
					int red = ((color & 0x00FF0000) >> 16);
					int green = ((color & 0x0000FF00) >> 8);
					int blue = color & 0x000000FF;
					color = (red + green + blue) / 3;
					color = alpha | (color << 16) | (color << 8) | color;
					newResult[idx] = color;
				}
			}
		}
		return newResult;

	}

	@Override
	public boolean isNative() {
		return false;
	}

	@Override
	public void openNative() {
		// noop
	}

	@Override
	public void closeNative() {
		// noop
	}
}
