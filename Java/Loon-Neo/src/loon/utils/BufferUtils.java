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

public class BufferUtils {

	public static int[] toColorKey(int[] buffer, int colors) {
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKey(buffer, colors);
		}
		return toColorKey(buffer, colors, LColor.TRANSPARENT);
	}

	public static int[] toColorKey(int[] buffer, int colors, int newColor) {
		return toColorKey(buffer, colors, newColor, false);
	}

	public static int[] toColorKey(int[] buffer, int colors, int newColor, boolean alpha) {
		return toColorKey(buffer, colors, newColor, 0.15f, alpha);
	}

	public static int[] toColorKey(int[] buffer, int colorKey, int newColor, float vague, boolean alpha) {
		final LColor srcPixel = new LColor();
		final LColor dstPixel = new LColor(colorKey);
		int size = buffer.length;
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

	public static int[] toColorKeys(int[] buffer, int[] colors) {
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKeys(buffer, colors);
		}
		return toColorKeys(buffer, colors, LColor.TRANSPARENT);
	}

	public static int[] toColorKeys(int[] buffer, int[] colors, int newColor) {
		return toColorKeys(buffer, colors, newColor, false);
	}

	public static int[] toColorKeys(int[] buffer, int[] colors, int newColor, boolean alpha) {
		return toColorKeys(buffer, colors, newColor, 0.15f, alpha);
	}

	public static int[] toColorKeys(int[] buffer, int[] colors, int newColor, float vague, boolean alpha) {
		final LColor srcPixel = new LColor();
		final LColor dstPixel = new LColor();
		int length = colors.length;
		int size = buffer.length;
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

	public static int[] toColorKeyLimit(int[] buffer, int start, int end) {
		if (LSystem.base() != null) {
			return LSystem.base().support().toColorKeyLimit(buffer, start, end);
		}
		return toColorKeyLimit(buffer, start, end, LColor.TRANSPARENT);
	}

	public static int[] toColorKeyLimit(int[] buffer, int start, int end, int newColor) {
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

	public static int[] toGray(int[] buffer, int w, int h) {
		return LSystem.base().support().toGray(buffer, w, h);
	}

	public static void makeBuffer(byte[] data, int size, int tag) {
		LSystem.base().support().makeBuffer(data, size, tag);
	}

	public void copy(float[] src, Buffer dst, int numFloats) {
		LSystem.base().support().copy(src, dst, numFloats);
	}

	public void copy(float[] src, Buffer dst, int offset, int numFloats) {
		LSystem.base().support().copy(src, dst, offset, numFloats);
	}

	public void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		LSystem.base().support().copy(src, srcOffset, dst, numElements);
	}

	public IntBuffer newIntBuffer(int[] src) {
		return LSystem.base().support().newIntBuffer(src);
	}

	public FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		return LSystem.base().support().newFloatBuffer(src, offset, numFloats);
	}

	public ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		return LSystem.base().support().replaceBytes(dst, src);
	}

	public FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		return LSystem.base().support().replaceFloats(dst, src);
	}

	public ByteBuffer getByteBuffer(byte[] bytes) {
		return LSystem.base().support().getByteBuffer(bytes);
	}

	public FloatBuffer getFloatBuffer(float[] floats) {
		return LSystem.base().support().getFloatBuffer(floats);
	}

	public ByteBuffer newByteBuffer(int numBytes) {
		return LSystem.base().support().newByteBuffer(numBytes);
	}

	public FloatBuffer newFloatBuffer(int numFloats) {
		return LSystem.base().support().newFloatBuffer(numFloats);
	}

	public ShortBuffer newShortBuffer(int numShorts) {
		return LSystem.base().support().newShortBuffer(numShorts);
	}

	public IntBuffer newIntBuffer(int numInts) {
		return LSystem.base().support().newIntBuffer(numInts);
	}

	public int getAllocatedBytesUnsafe() {
		return LSystem.base().support().getAllocatedBytesUnsafe();
	}

	public void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		LSystem.base().support().disposeUnsafeByteBuffer(buffer);
	}

	public ByteBuffer newUnsafeByteBuffer(int numBytes) {
		return LSystem.base().support().newUnsafeByteBuffer(numBytes);
	}

	public ByteBuffer allocateDirect(int capacity) {
		return LSystem.base().support().allocateDirect(capacity);
	}

	public void clear(Buffer buffer) {
		LSystem.base().support().clear(buffer);
	}

	public void filterColor(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst, int[] colors, int c1,
			int c2) {
		LSystem.base().support().filterColor(maxPixel, pixelStart, pixelEnd, src, dst, colors, c1, c2);
	}

	public void filterFractions(int size, float[] fractions, int width, int height, int[] pixels, int numElements) {
		LSystem.base().support().filterFractions(size, fractions, width, height, pixels, numElements);
	}

	public void mul(float[] mata, float[] matb) {
		LSystem.base().support().mul(mata, matb);
	}

	public void mulVec(float[] mat, float[] vec) {
		LSystem.base().support().mulVec(mat, vec);
	}

	public void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().mulVec(mat, vecs, offset, numVecs, stride);
	}

	public void prj(float[] mat, float[] vec) {
		LSystem.base().support().prj(mat, vec);
	}

	public void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().prj(mat, vecs, offset, numVecs, stride);
	}

	public void rot(float[] mat, float[] vec) {
		LSystem.base().support().rot(mat, vec);
	}

	public void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
		LSystem.base().support().rot(mat, vecs, offset, numVecs, stride);
	}

	public boolean inv(float[] values) {
		return LSystem.base().support().inv(values);
	}

	public float det(float[] values) {
		return LSystem.base().support().det(values);
	}

}
