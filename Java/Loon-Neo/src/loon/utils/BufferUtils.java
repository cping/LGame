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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.LSystem;
import loon.canvas.LColor;

public final class BufferUtils {

	public final static int[] toColorKey(int[] buffer, int colors) {
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKey(buffer, colors);
		}
		return toColorKey(buffer, colors, LColor.TRANSPARENT);
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
			final int pixel = buffer[i];
			srcPixel.setColor(pixel);
			final float r = MathUtils.interval(srcPixel.r, dstPixel.r, vague, dstPixel.r, srcPixel.r);
			final float g = MathUtils.interval(srcPixel.g, dstPixel.g, vague, dstPixel.g, srcPixel.g);
			final float b = MathUtils.interval(srcPixel.b, dstPixel.b, vague, dstPixel.b, srcPixel.b);
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
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKeys(buffer, colors);
		}
		return toColorKeys(buffer, colors, LColor.TRANSPARENT);
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
				final int pixel = buffer[i];
				srcPixel.setColor(pixel);
				final float r = MathUtils.interval(srcPixel.r, dstPixel.r, vague, dstPixel.r, srcPixel.r);
				final float g = MathUtils.interval(srcPixel.g, dstPixel.g, vague, dstPixel.g, srcPixel.g);
				final float b = MathUtils.interval(srcPixel.b, dstPixel.b, vague, dstPixel.b, srcPixel.b);
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
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKeyLimit(buffer, start, end);
		}
		return toColorKeyLimit(buffer, start, end, LColor.TRANSPARENT);
	}

	public final static int[] toColorKeyLimit(int[] buffer, int start, int end, int newColor) {
		final int sred = LColor.getRed(start);
		final int sgreen = LColor.getGreen(start);
		final int sblue = LColor.getBlue(start);
		final int ered = LColor.getRed(end);
		final int egreen = LColor.getGreen(end);
		final int eblue = LColor.getBlue(end);
		final int size = buffer.length;
		for (int i = 0; i < size; i++) {
			final int pixel = buffer[i];
			final int r = LColor.getRed(pixel);
			final int g = LColor.getGreen(pixel);
			final int b = LColor.getBlue(pixel);
			if ((r >= sred && g >= sgreen && b >= sblue) && (r <= ered && g <= egreen && b <= eblue)) {
				buffer[i] = newColor;
			}
		}
		return buffer;
	}

	public final static int[] toGray(int[] buffer, int w, int h) {
		return LSystem.base().support().toGray(buffer, w, h);
	}

	public final static void makeBuffer(byte[] data, int size, int tag) {
		LSystem.base().support().makeBuffer(data, size, tag);
	}

	public final static void copy(float[] src, Buffer dst, int numFloats) {
		LSystem.base().support().copy(src, dst, numFloats);
	}

	public final static void copy(float[] src, Buffer dst, int offset, int numFloats) {
		LSystem.base().support().copy(src, dst, offset, numFloats);
	}

	public final static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public final static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public final static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public final static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public final static IntBuffer newIntBuffer(int[] src) {
		return LSystem.base().support().newIntBuffer(src);
	}

	public final static FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		return LSystem.base().support().newFloatBuffer(src, offset, numFloats);
	}

	public final static ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		return LSystem.base().support().replaceBytes(dst, src);
	}

	public final static FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		return LSystem.base().support().replaceFloats(dst, src);
	}

	public final static ByteBuffer getByteBuffer(byte[] bytes) {
		return LSystem.base().support().getByteBuffer(bytes);
	}

	public final static FloatBuffer getFloatBuffer(float[] floats) {
		return LSystem.base().support().getFloatBuffer(floats);
	}

	public final static ByteBuffer newByteBuffer(int numBytes) {
		return LSystem.base().support().newByteBuffer(numBytes);
	}

	public final static FloatBuffer newFloatBuffer(int numFloats) {
		return LSystem.base().support().newFloatBuffer(numFloats);
	}

	public final static ShortBuffer newShortBuffer(int numShorts) {
		return LSystem.base().support().newShortBuffer(numShorts);
	}

	public final static IntBuffer newIntBuffer(int numInts) {
		return LSystem.base().support().newIntBuffer(numInts);
	}

	public final static int getAllocatedBytesUnsafe() {
		return LSystem.base().support().getAllocatedBytesUnsafe();
	}

	public final static void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		LSystem.base().support().disposeUnsafeByteBuffer(buffer);
	}

	public final static ByteBuffer newUnsafeByteBuffer(int numBytes) {
		return LSystem.base().support().newUnsafeByteBuffer(numBytes);
	}

	public final static ByteBuffer allocateDirect(int capacity) {
		return LSystem.base().support().allocateDirect(capacity);
	}

	public final static void clear(Buffer buffer) {
		LSystem.base().support().clear(buffer);
	}

	public final static void filterColor(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst, int[] colors,
			int c1, int c2) {
		LSystem.base().support().filterColor(maxPixel, pixelStart, pixelEnd, src, dst, colors, c1, c2);
	}

	public final static void filterFractions(int size, float[] fractions, int width, int height, int[] pixels,
			int numElements) {
		LSystem.base().support().filterFractions(size, fractions, width, height, pixels, numElements);
	}

	public final static void mul(float[] mata, float[] matb) {
		LSystem.base().support().mul(mata, matb);
	}

	public final static void mulVec(float[] mat, float[] vec) {
		LSystem.base().support().mulVec(mat, vec);
	}

	public final static void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().mulVec(mat, vecs, offset, numVecs, stride);
	}

	public final static void prj(float[] mat, float[] vec) {
		LSystem.base().support().prj(mat, vec);
	}

	public final static void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().prj(mat, vecs, offset, numVecs, stride);
	}

	public final static void rot(float[] mat, float[] vec) {
		LSystem.base().support().rot(mat, vec);
	}

	public final static void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().rot(mat, vecs, offset, numVecs, stride);
	}

	public final static boolean inv(float[] values) {
		return LSystem.base().support().inv(values);
	}

	public final static float det(float[] values) {
		return LSystem.base().support().det(values);
	}

}
