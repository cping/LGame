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
package loon.cport.bridge;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.Support;
import loon.utils.MathUtils;

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
		ByteBuffer buffer = ByteBuffer.allocate(numBytes);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	@Override
	public FloatBuffer newFloatBuffer(int numFloats) {
		ByteBuffer buffer = ByteBuffer.allocate(numFloats * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	@Override
	public ShortBuffer newShortBuffer(int numShorts) {
		ByteBuffer buffer = ByteBuffer.allocate(numShorts * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asShortBuffer();
	}

	@Override
	public IntBuffer newIntBuffer(int numInts) {
		ByteBuffer buffer = ByteBuffer.allocate(numInts * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	public void put(final Buffer buffer, final float[] source, final int offset, final int length) {
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
	public final void filterColor(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst, int[] colors,
			int c1, int c2) {
		final int length = src.length;
		final int start, end, targetColor;
		if (pixelStart < pixelEnd) {
			start = pixelStart + 1;
			end = pixelEnd + 1;
			targetColor = c1;
			if (end > maxPixel) {
				return;
			}
		} else {
			start = pixelEnd - 1;
			end = pixelStart;
			targetColor = c2;
			if (start < 0)
				return;
		}
		for (int i = 0; i < length; i++) {
			if (dst[i] == 0xffffff) {
				continue;
			}
			if (src[i] == targetColor) {
				dst[i] = 0xffffff;
				continue;
			}
			for (int pixIndex = start; pixIndex < end; pixIndex++) {
				if (colors[pixIndex] == src[i]) {
					dst[i] = 0xffffff;
					break;
				}
			}
		}
	}

	@Override
	public void filterFractions(int size, float[] fractions, int width, int height, int[] pixels, int numElements) {
		for (int j = 0; j < size; j++) {
			final int idx = j * numElements;
			float fx = fractions[idx];
			float fy = fractions[idx + 1];
			float dx = fractions[idx + 2];
			float dy = fractions[idx + 3];
			final int color = (int) fractions[idx + 4];
			int delay = (int) fractions[idx + 5];
			if (color == 0xffffff) {
				continue;
			}
			if (delay <= 0) {
				fx += dx;
				fy += dy;
				dy += 0.1f;
			} else {
				delay--;
			}
			int x = (int) fx;
			int y = (int) fy;
			if (x >= 0 && y >= 0 && x < width && y < height) {
				pixels[x + y * width] = color;
			}
			fractions[idx] = fx;
			fractions[idx + 1] = fy;
			fractions[idx + 2] = dx;
			fractions[idx + 3] = dy;
			fractions[idx + 4] = color;
			fractions[idx + 5] = delay;
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
		float m00 = mata[M00], m01 = mata[M01], m02 = mata[M02], m03 = mata[M03];
		float m10 = mata[M10], m11 = mata[M11], m12 = mata[M12], m13 = mata[M13];
		float m20 = mata[M20], m21 = mata[M21], m22 = mata[M22], m23 = mata[M23];
		float m30 = mata[M30], m31 = mata[M31], m32 = mata[M32], m33 = mata[M33];
		float[] tmp = new float[16];
		tmp[M00] = m00 * matb[M00] + m01 * matb[M10] + m02 * matb[M20] + m03 * matb[M30];
		tmp[M01] = m00 * matb[M01] + m01 * matb[M11] + m02 * matb[M21] + m03 * matb[M31];
		tmp[M02] = m00 * matb[M02] + m01 * matb[M12] + m02 * matb[M22] + m03 * matb[M32];
		tmp[M03] = m00 * matb[M03] + m01 * matb[M13] + m02 * matb[M23] + m03 * matb[M33];
		tmp[M10] = m10 * matb[M00] + m11 * matb[M10] + m12 * matb[M20] + m13 * matb[M30];
		tmp[M11] = m10 * matb[M01] + m11 * matb[M11] + m12 * matb[M21] + m13 * matb[M31];
		tmp[M12] = m10 * matb[M02] + m11 * matb[M12] + m12 * matb[M22] + m13 * matb[M32];
		tmp[M13] = m10 * matb[M03] + m11 * matb[M13] + m12 * matb[M23] + m13 * matb[M33];
		tmp[M20] = m20 * matb[M00] + m21 * matb[M10] + m22 * matb[M20] + m23 * matb[M30];
		tmp[M21] = m20 * matb[M01] + m21 * matb[M11] + m22 * matb[M21] + m23 * matb[M31];
		tmp[M22] = m20 * matb[M02] + m21 * matb[M12] + m22 * matb[M22] + m23 * matb[M32];
		tmp[M23] = m20 * matb[M03] + m21 * matb[M13] + m22 * matb[M23] + m23 * matb[M33];
		tmp[M30] = m30 * matb[M00] + m31 * matb[M10] + m32 * matb[M20] + m33 * matb[M30];
		tmp[M31] = m30 * matb[M01] + m31 * matb[M11] + m32 * matb[M21] + m33 * matb[M31];
		tmp[M32] = m30 * matb[M02] + m31 * matb[M12] + m32 * matb[M22] + m33 * matb[M32];
		tmp[M33] = m30 * matb[M03] + m31 * matb[M13] + m32 * matb[M23] + m33 * matb[M33];
		System.arraycopy(tmp, 0, mata, 0, 16);
	}

	@Override
	public void mulVec(float[] mat, float[] vec) {
		float vx = vec[0], vy = vec[1], vz = vec[2];
		vec[0] = vx * mat[M00] + vy * mat[M01] + vz * mat[M02] + mat[M03];
		vec[1] = vx * mat[M10] + vy * mat[M11] + vz * mat[M12] + mat[M13];
		vec[2] = vx * mat[M20] + vy * mat[M21] + vz * mat[M22] + mat[M23];
	}

	@Override
	public void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		for (int i = 0; i < numVecs; i++) {
			int idx = offset + i * stride;
			float vx = vecs[idx], vy = vecs[idx + 1], vz = vecs[idx + 2];
			vecs[idx] = vx * mat[M00] + vy * mat[M01] + vz * mat[M02] + mat[M03];
			vecs[idx + 1] = vx * mat[M10] + vy * mat[M11] + vz * mat[M12] + mat[M13];
			vecs[idx + 2] = vx * mat[M20] + vy * mat[M21] + vz * mat[M22] + mat[M23];
		}
	}

	@Override
	public void prj(float[] mat, float[] vec) {
		float vx = vec[0], vy = vec[1], vz = vec[2];
		float inv_w = 1.0f / (vx * mat[M30] + vy * mat[M31] + vz * mat[M32] + mat[M33]);
		vec[0] = (vx * mat[M00] + vy * mat[M01] + vz * mat[M02] + mat[M03]) * inv_w;
		vec[1] = (vx * mat[M10] + vy * mat[M11] + vz * mat[M12] + mat[M13]) * inv_w;
		vec[2] = (vx * mat[M20] + vy * mat[M21] + vz * mat[M22] + mat[M23]) * inv_w;
	}

	@Override
	public void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		for (int i = 0; i < numVecs; i++) {
			int idx = offset + i * stride;
			float vx = vecs[idx], vy = vecs[idx + 1], vz = vecs[idx + 2];
			float inv_w = 1.0f / (vx * mat[M30] + vy * mat[M31] + vz * mat[M32] + mat[M33]);
			vecs[idx] = (vx * mat[M00] + vy * mat[M01] + vz * mat[M02] + mat[M03]) * inv_w;
			vecs[idx + 1] = (vx * mat[M10] + vy * mat[M11] + vz * mat[M12] + mat[M13]) * inv_w;
			vecs[idx + 2] = (vx * mat[M20] + vy * mat[M21] + vz * mat[M22] + mat[M23]) * inv_w;
		}
	}

	@Override
	public void rot(float[] mat, float[] vec) {
		float vx = vec[0], vy = vec[1], vz = vec[2];
		vec[0] = vx * mat[M00] + vy * mat[M01] + vz * mat[M02];
		vec[1] = vx * mat[M10] + vy * mat[M11] + vz * mat[M12];
		vec[2] = vx * mat[M20] + vy * mat[M21] + vz * mat[M22];
	}

	@Override
	public void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		for (int i = 0; i < numVecs; i++) {
			int idx = offset + i * stride;
			float vx = vecs[idx], vy = vecs[idx + 1], vz = vecs[idx + 2];
			vecs[idx] = vx * mat[M00] + vy * mat[M01] + vz * mat[M02];
			vecs[idx + 1] = vx * mat[M10] + vy * mat[M11] + vz * mat[M12];
			vecs[idx + 2] = vx * mat[M20] + vy * mat[M21] + vz * mat[M22];
		}
	}

	@Override
	public boolean inv(float[] v) {
		float m00 = v[M00], m01 = v[M01], m02 = v[M02], m03 = v[M03];
		float m10 = v[M10], m11 = v[M11], m12 = v[M12], m13 = v[M13];
		float m20 = v[M20], m21 = v[M21], m22 = v[M22], m23 = v[M23];
		float m30 = v[M30], m31 = v[M31], m32 = v[M32], m33 = v[M33];
		float det = det(v);
		if (det == 0f) {
			return false;
		}
		final float invDet = 1f / det;
		float[] tmp = new float[16];
		tmp[M00] = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33
				+ m11 * m22 * m33;
		tmp[M01] = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33
				- m01 * m22 * m33;
		tmp[M02] = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33
				+ m01 * m12 * m33;
		tmp[M03] = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23
				- m01 * m12 * m23;
		tmp[M10] = m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33
				- m10 * m22 * m33;
		tmp[M11] = m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33
				+ m00 * m22 * m33;
		tmp[M12] = m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33
				- m00 * m12 * m33;
		tmp[M13] = m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23
				+ m00 * m12 * m23;
		tmp[M20] = m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33
				+ m10 * m21 * m33;
		tmp[M21] = m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33
				- m00 * m21 * m33;
		tmp[M22] = m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33
				+ m00 * m11 * m33;
		tmp[M23] = m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23
				- m00 * m11 * m23;
		tmp[M30] = m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32
				- m10 * m21 * m32;
		tmp[M31] = m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32
				+ m00 * m21 * m32;
		tmp[M32] = m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32
				- m00 * m11 * m32;
		tmp[M33] = m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22
				+ m00 * m11 * m22;
		for (int i = 0; i < 16; i++) {
			v[i] = tmp[i] * invDet;
		}
		return true;
	}

	@Override
	public float det(float[] v) {
		float m00 = v[M00], m01 = v[M01], m02 = v[M02], m03 = v[M03];
		float m10 = v[M10], m11 = v[M11], m12 = v[M12], m13 = v[M13];
		float m20 = v[M20], m21 = v[M21], m22 = v[M22], m23 = v[M23];
		float m30 = v[M30], m31 = v[M31], m32 = v[M32], m33 = v[M33];
		return m30 * m21 * m12 * m03 - m20 * m31 * m12 * m03 - m30 * m11 * m22 * m03 + m10 * m31 * m22 * m03
				+ m20 * m11 * m32 * m03 - m10 * m21 * m32 * m03 - m30 * m21 * m02 * m13 + m20 * m31 * m02 * m13
				+ m30 * m01 * m22 * m13 - m00 * m31 * m22 * m13 - m20 * m01 * m32 * m13 + m00 * m21 * m32 * m13
				+ m30 * m11 * m02 * m23 - m10 * m31 * m02 * m23 - m30 * m01 * m12 * m23 + m00 * m31 * m12 * m23
				+ m10 * m01 * m32 * m23 - m00 * m11 * m32 * m23 - m20 * m11 * m02 * m33 + m10 * m21 * m02 * m33
				+ m20 * m01 * m12 * m33 - m00 * m21 * m12 * m33 - m10 * m01 * m22 * m33 + m00 * m11 * m22 * m33;
	}

	@Override
	public int[] toColorKey(int[] buffer, int colorKey) {
		return toColorKey(buffer, colorKey, 0, false);
	}

	public final static int[] toColorKey(int[] buffer, int colorKey, int newColor) {
		return toColorKey(buffer, colorKey, newColor, false);
	}

	public final static int[] toColorKey(int[] buffer, int colorKey, int newColor, boolean alpha) {
		return toColorKey(buffer, colorKey, newColor, 0.15f, alpha);
	}

	public final static int[] toColorKey(int[] buffer, int colorKey, int newColor, float vague, boolean alpha) {
		final int size = buffer.length;
		final int dr = (colorKey >> 16) & 0xFF;
		final int dg = (colorKey >> 8) & 0xFF;
		final int db = colorKey & 0xFF;
		final int vr = (int) (vague * 255);
		final int vg = vr;
		final int vb = vr;
		for (int i = 0; i < size; i++) {
			final int pixel = buffer[i];
			final int r = (pixel >> 16) & 0xFF;
			final int g = (pixel >> 8) & 0xFF;
			final int b = pixel & 0xFF;
			if (MathUtils.abs(r - dr) <= vr && MathUtils.abs(g - dg) <= vg && MathUtils.abs(b - db) <= vb) {
				buffer[i] = newColor;
			} else if (alpha) {
				buffer[i] = (pixel & 0x00FFFFFF) | (128 << 24);
			}
		}
		return buffer;
	}

	@Override
	public int[] toColorKeys(int[] buffer, int[] colors) {
		return toColorKeys(buffer, colors, 0, false);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor) {
		return toColorKeys(buffer, colors, newColor, false);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor, boolean alpha) {
		return toColorKeys(buffer, colors, newColor, 0.15f, alpha);
	}

	public final static int[] toColorKeys(int[] buffer, int[] colors, int newColor, float vague, boolean alpha) {
		final int size = buffer.length;
		final int vagueThreshold = (int) (vague * 255);
		final int len = colors.length;
		final int[] rs = new int[len];
		final int[] gs = new int[len];
		final int[] bs = new int[len];
		for (int n = 0; n < len; n++) {
			final int c = colors[n];
			rs[n] = (c >> 16) & 0xFF;
			gs[n] = (c >> 8) & 0xFF;
			bs[n] = c & 0xFF;
		}
		for (int i = 0; i < size; i++) {
			final int pixel = buffer[i];
			final int r = (pixel >> 16) & 0xFF;
			final int g = (pixel >> 8) & 0xFF;
			final int b = pixel & 0xFF;
			boolean matched = false;
			for (int n = 0; n < len; n++) {
				if (MathUtils.abs(r - rs[n]) <= vagueThreshold && MathUtils.abs(g - gs[n]) <= vagueThreshold
						&& MathUtils.abs(b - bs[n]) <= vagueThreshold) {
					matched = true;
					break;
				}
			}
			if (matched) {
				buffer[i] = newColor;
			} else if (alpha) {
				buffer[i] = (pixel & 0x00FFFFFF) | (128 << 24);
			}
		}
		return buffer;
	}

	@Override
	public int[] toColorKeyLimit(int[] buffer, int start, int end) {
		return toColorKeyLimit(buffer, start, end, 0);
	}

	public final static int[] toColorKeyLimit(int[] buffer, int start, int end, int newColor) {
		final int sred = (start >> 16) & 0xFF;
		final int sgreen = (start >> 8) & 0xFF;
		final int sblue = start & 0xFF;
		final int ered = (end >> 16) & 0xFF;
		final int egreen = (end >> 8) & 0xFF;
		final int eblue = end & 0xFF;
		for (int i = 0, size = buffer.length; i < size; i++) {
			final int pixel = buffer[i];
			final int r = (pixel >> 16) & 0xFF;
			final int g = (pixel >> 8) & 0xFF;
			final int b = pixel & 0xFF;
			if (r >= sred && r <= ered && g >= sgreen && g <= egreen && b >= sblue && b <= eblue) {
				buffer[i] = newColor;
			}
		}
		return buffer;
	}

	@Override
	public int[] toGray(int[] buffer, int w, int h) {
		final int size = w * h;
		final int alpha = 0xFF << 24;
		for (int i = 0; i < size; i++) {
			int color = buffer[i];
			if (color != 0x00FFFFFF) {
				final int r = (color >> 16) & 0xFF;
				final int g = (color >> 8) & 0xFF;
				final int b = color & 0xFF;
				final int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
				buffer[i] = alpha | (gray << 16) | (gray << 8) | gray;
			}
		}
		return buffer;
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
