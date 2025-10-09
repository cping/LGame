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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.canvas.LColor;

public final class BufferUtils {

	public final static void makeBuffer(byte[] data, int size, int tag) {
		for (int i = 0; i < size; i++) {
			data[i] ^= tag;
		}
	}

	public final static void copy(float[] src, Buffer dst, int numFloats) {
		copy(src, dst, 0, numFloats);
	}

	public final static void copy(float[] src, Buffer dst, int offset, int numFloats) {
		putBuffer(dst, src, offset, numFloats);
	}

	public final static IntBuffer newIntBuffer(final int[] src) {
		if (src == null) {
			return null;
		}
		int size = src.length;
		IntBuffer buffer = newIntBuffer(size);
		copy(src, 0, buffer, size);
		return buffer;
	}

	public final static FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		FloatBuffer buffer = newFloatBuffer(numFloats);
		copy(src, buffer, offset, numFloats);
		return buffer;
	}

	public final static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	public final static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
		putBuffer(dst, src, srcOffset, numElements);
	}

	private final static void putBuffer(Buffer dst, Object src, int offset, int numFloats) {
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
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
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
			throw new RuntimeException("data must be a ByteBuffer or FloatBuffer");
		return buffer;
	}

	public final static ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		final int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	public final static FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		final int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	public final static ByteBuffer getByteBuffer(byte[] bytes) {
		final ByteBuffer buffer = newByteBuffer(bytes.length).put(bytes);
		buffer.position(0);
		return buffer;

	}

	public final static FloatBuffer getFloatBuffer(float[] floats) {
		final FloatBuffer buffer = newFloatBuffer(floats.length).put(floats);
		buffer.position(0);
		return buffer;
	}

	public final static ByteBuffer newByteBuffer(int numBytes) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	public final static FloatBuffer newFloatBuffer(int numFloats) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	public final static ShortBuffer newShortBuffer(int numShorts) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asShortBuffer();
	}

	public final static IntBuffer newIntBuffer(int numInts) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	public final static void put(final Buffer buffer, final float[] source, final int offset, final int length) {
		putBuffer(buffer, source, offset, length);
	}

	private static int allocatedUnsafe = 0;

	public final static int getAllocatedBytesUnsafe() {
		return allocatedUnsafe;
	}

	public final static void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		freeMemory(buffer);
	}

	public final static ByteBuffer newUnsafeByteBuffer(int numBytes) {
		return newByteBuffer(numBytes);
	}

	public final static ByteBuffer allocateDirect(final int capacity) {
		return ByteBuffer.allocateDirect(capacity);
	}

	private final static void freeMemory(Buffer buffer) {
		if (buffer != null) {
			buffer.clear();
			buffer = null;
		}
	}

	public final static void clear(Buffer buffer) {
		if (buffer != null) {
			buffer.clear();
		}
	}

	public static final void filterColor(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst, int[] colors,
			int c1, int c2) {
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

	public static void filterFractions(int size, float[] fractions, int width, int height, int[] pixels,
			int numElements) {

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

	public static final int M00 = 0;
	public static final int M01 = 4;
	public static final int M02 = 8;
	public static final int M03 = 12;
	public static final int M10 = 1;
	public static final int M11 = 5;
	public static final int M12 = 9;
	public static final int M13 = 13;
	public static final int M20 = 2;
	public static final int M21 = 6;
	public static final int M22 = 10;
	public static final int M23 = 14;
	public static final int M30 = 3;
	public static final int M31 = 7;
	public static final int M32 = 11;
	public static final int M33 = 15;

	public final static void mul(float[] mata, float[] matb) {

		float[] tmp = new float[16];
		tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
		tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
		tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
		tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
		tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
		tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
		tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
		tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
		tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
		tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
		tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
		tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
		tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
		tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
		tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
		tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
		System.arraycopy(tmp, 0, mata, 0, 16);

	}

	public final static void mulVec(float[] mat, float[] vec) {

		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	public final static void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			mulVec(mat, vecPtr);
		}

	}

	public final static void prj(float[] mat, float[] vec) {

		float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
		float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
		float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w;
		float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	public final static void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			prj(mat, vecPtr);
		}

	}

	public final static void rot(float[] mat, float[] vec) {

		float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
		float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
		float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
		vec[0] = x;
		vec[1] = y;
		vec[2] = z;

	}

	public final static void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {

		for (int i = 0; i < numVecs; i++) {
			float[] vecPtr = new float[stride];
			System.arraycopy(vecs, offset, vecPtr, 0, stride);
			rot(mat, vecPtr);
		}

	}

	public final static boolean inv(float[] values) {

		final float[] tmp = new float[16];
		float l_det = det(values);
		if (l_det == 0)
			return false;
		tmp[M00] = values[M12] * values[M23] * values[M31] - values[M13] * values[M22] * values[M31]
				+ values[M13] * values[M21] * values[M32] - values[M11] * values[M23] * values[M32]
				- values[M12] * values[M21] * values[M33] + values[M11] * values[M22] * values[M33];
		tmp[M01] = values[M03] * values[M22] * values[M31] - values[M02] * values[M23] * values[M31]
				- values[M03] * values[M21] * values[M32] + values[M01] * values[M23] * values[M32]
				+ values[M02] * values[M21] * values[M33] - values[M01] * values[M22] * values[M33];
		tmp[M02] = values[M02] * values[M13] * values[M31] - values[M03] * values[M12] * values[M31]
				+ values[M03] * values[M11] * values[M32] - values[M01] * values[M13] * values[M32]
				- values[M02] * values[M11] * values[M33] + values[M01] * values[M12] * values[M33];
		tmp[M03] = values[M03] * values[M12] * values[M21] - values[M02] * values[M13] * values[M21]
				- values[M03] * values[M11] * values[M22] + values[M01] * values[M13] * values[M22]
				+ values[M02] * values[M11] * values[M23] - values[M01] * values[M12] * values[M23];
		tmp[M10] = values[M13] * values[M22] * values[M30] - values[M12] * values[M23] * values[M30]
				- values[M13] * values[M20] * values[M32] + values[M10] * values[M23] * values[M32]
				+ values[M12] * values[M20] * values[M33] - values[M10] * values[M22] * values[M33];
		tmp[M11] = values[M02] * values[M23] * values[M30] - values[M03] * values[M22] * values[M30]
				+ values[M03] * values[M20] * values[M32] - values[M00] * values[M23] * values[M32]
				- values[M02] * values[M20] * values[M33] + values[M00] * values[M22] * values[M33];
		tmp[M12] = values[M03] * values[M12] * values[M30] - values[M02] * values[M13] * values[M30]
				- values[M03] * values[M10] * values[M32] + values[M00] * values[M13] * values[M32]
				+ values[M02] * values[M10] * values[M33] - values[M00] * values[M12] * values[M33];
		tmp[M13] = values[M02] * values[M13] * values[M20] - values[M03] * values[M12] * values[M20]
				+ values[M03] * values[M10] * values[M22] - values[M00] * values[M13] * values[M22]
				- values[M02] * values[M10] * values[M23] + values[M00] * values[M12] * values[M23];
		tmp[M20] = values[M11] * values[M23] * values[M30] - values[M13] * values[M21] * values[M30]
				+ values[M13] * values[M20] * values[M31] - values[M10] * values[M23] * values[M31]
				- values[M11] * values[M20] * values[M33] + values[M10] * values[M21] * values[M33];
		tmp[M21] = values[M03] * values[M21] * values[M30] - values[M01] * values[M23] * values[M30]
				- values[M03] * values[M20] * values[M31] + values[M00] * values[M23] * values[M31]
				+ values[M01] * values[M20] * values[M33] - values[M00] * values[M21] * values[M33];
		tmp[M22] = values[M01] * values[M13] * values[M30] - values[M03] * values[M11] * values[M30]
				+ values[M03] * values[M10] * values[M31] - values[M00] * values[M13] * values[M31]
				- values[M01] * values[M10] * values[M33] + values[M00] * values[M11] * values[M33];
		tmp[M23] = values[M03] * values[M11] * values[M20] - values[M01] * values[M13] * values[M20]
				- values[M03] * values[M10] * values[M21] + values[M00] * values[M13] * values[M21]
				+ values[M01] * values[M10] * values[M23] - values[M00] * values[M11] * values[M23];
		tmp[M30] = values[M12] * values[M21] * values[M30] - values[M11] * values[M22] * values[M30]
				- values[M12] * values[M20] * values[M31] + values[M10] * values[M22] * values[M31]
				+ values[M11] * values[M20] * values[M32] - values[M10] * values[M21] * values[M32];
		tmp[M31] = values[M01] * values[M22] * values[M30] - values[M02] * values[M21] * values[M30]
				+ values[M02] * values[M20] * values[M31] - values[M00] * values[M22] * values[M31]
				- values[M01] * values[M20] * values[M32] + values[M00] * values[M21] * values[M32];
		tmp[M32] = values[M02] * values[M11] * values[M30] - values[M01] * values[M12] * values[M30]
				- values[M02] * values[M10] * values[M31] + values[M00] * values[M12] * values[M31]
				+ values[M01] * values[M10] * values[M32] - values[M00] * values[M11] * values[M32];
		tmp[M33] = values[M01] * values[M12] * values[M20] - values[M02] * values[M11] * values[M20]
				+ values[M02] * values[M10] * values[M21] - values[M00] * values[M12] * values[M21]
				- values[M01] * values[M10] * values[M22] + values[M00] * values[M11] * values[M22];

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

	public final static float det(float[] values) {

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

	public final static int[] toColorKey(int[] buffer, int colors) {
		return toColorKey(buffer, colors, LColor.TRANSPARENT, false);
	}

	public final static int[] toColorKey(int[] buffer, int colors, int newColor) {
		return toColorKey(buffer, colors, newColor, false);
	}

	public final static int[] toColorKey(int[] buffer, int colors, int newColor, boolean alpha) {
		return toColorKey(buffer, colors, newColor, 0.15f, alpha);
	}

	public final static int[] toColorKey(int[] buffer, int colorKey, int newColor, float vague, boolean alpha) {
		final LColor srcPixel = new LColor();
		final LColor dstPixel = new LColor(colorKey);
		final int size = buffer.length;
		for (int i = 0; i < size; i++) {
			int pixel = buffer[i];
			srcPixel.setColor(pixel);
			float r = MathUtils.interval(srcPixel.r, dstPixel.r, vague, dstPixel.r, srcPixel.r);
			float g = MathUtils.interval(srcPixel.g, dstPixel.g, vague, dstPixel.g, srcPixel.g);
			float b = MathUtils.interval(srcPixel.b, dstPixel.b, vague, dstPixel.b, srcPixel.b);
			if (r == dstPixel.r && g == dstPixel.g && b == dstPixel.b) {
				buffer[i] = newColor;
			} else {
				if (alpha) {
					buffer[i] = srcPixel.setAlpha(0.5f).getARGB();
				} else {
					buffer[i] = pixel;
				}
			}
		}
		return buffer;
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors) {
		return toColorKeys(buffer, colors, LColor.TRANSPARENT, false);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor) {
		return toColorKeys(buffer, colors, newColor, false);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor, boolean alpha) {
		return toColorKeys(buffer, colors, newColor, 0.15f, alpha);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor, float vague, boolean alpha) {
		final LColor srcPixel = new LColor();
		final LColor dstPixel = new LColor();
		final int length = colors.length;
		final int size = buffer.length;
		for (int n = 0; n < length; n++) {
			dstPixel.setColor(colors[n]);
			for (int i = 0; i < size; i++) {
				int pixel = buffer[i];
				srcPixel.setColor(pixel);
				float r = MathUtils.interval(srcPixel.r, dstPixel.r, vague, dstPixel.r, srcPixel.r);
				float g = MathUtils.interval(srcPixel.g, dstPixel.g, vague, dstPixel.g, srcPixel.g);
				float b = MathUtils.interval(srcPixel.b, dstPixel.b, vague, dstPixel.b, srcPixel.b);
				if (r == dstPixel.r && g == dstPixel.g && b == dstPixel.b) {
					buffer[i] = newColor;
				} else {
					if (alpha) {
						buffer[i] = srcPixel.setAlpha(0.5f).getARGB();
					} else {
						buffer[i] = pixel;
					}
				}
			}
		}
		return buffer;
	}

	public final static int[] toColorKeyLimit(int[] buffer, int start, int end) {
		return toColorKeyLimit(buffer, start, end, LColor.TRANSPARENT);
	}

	public final static int[] toColorKeyLimit(int[] buffer, int start, int end, int newColor) {

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
			if ((r >= sred && g >= sgreen && b >= sblue) && (r <= ered && g <= egreen && b <= eblue)) {
				buffer[i] = newColor;
			}
		}

		return buffer;
	}

	public final static int[] toGray(int[] buffer, int w, int h) {

		final int size = w * h;
		final int[] newResult = new int[size];
		System.arraycopy(buffer, 0, newResult, 0, size);
		final int alpha = 0xFF << 24;
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

}
