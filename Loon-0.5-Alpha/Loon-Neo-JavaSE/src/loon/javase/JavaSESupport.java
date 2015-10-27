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
package loon.javase;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.Support;
import loon.jni.NativeSupport;

public class JavaSESupport implements Support {

	@Override
	public void copy(float[] src, Buffer dst, int numFloats) {
		NativeSupport.copy(src, dst, numFloats);
	}

	@Override
	public void copy(float[] src, Buffer dst, int offset, int numFloats) {
		NativeSupport.copy(src, dst, offset, numFloats);
	}

	@Override
	public IntBuffer newIntBuffer(int[] src) {
		return NativeSupport.newIntBuffer(src);
	}

	@Override
	public FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		return NativeSupport.newFloatBuffer(src, offset, numFloats);
	}

	@Override
	public ByteBuffer clone(ByteBuffer dst) {
		return NativeSupport.clone(dst);
	}

	@Override
	public FloatBuffer clone(FloatBuffer dst) {
		return NativeSupport.clone(dst);
	}

	@Override
	public void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		NativeSupport.copy(src, srcOffset, dst, numElements);
	}

	@Override
	public void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		NativeSupport.copy(src, srcOffset, dst, numElements);
	}

	@Override
	public void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		NativeSupport.copy(src, srcOffset, dst, numElements);
	}

	@Override
	public void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		NativeSupport.copy(src, srcOffset, dst, numElements);
	}

	@Override
	public void copy(Buffer src, Buffer dst, int numElements) {
		NativeSupport.copy(src, dst, numElements);
	}

	@Override
	public ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		return NativeSupport.replaceBytes(dst, src);
	}

	@Override
	public FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		return NativeSupport.replaceFloats(dst, src);
	}

	@Override
	public ByteBuffer getByteBuffer(byte[] bytes) {
		return NativeSupport.getByteBuffer(bytes);
	}

	@Override
	public FloatBuffer getFloatBuffer(float[] floats) {
		return NativeSupport.getFloatBuffer(floats);
	}

	@Override
	public ByteBuffer newByteBuffer(int numBytes) {
		return NativeSupport.newByteBuffer(numBytes);
	}

	@Override
	public FloatBuffer newFloatBuffer(int numFloats) {
		return NativeSupport.newFloatBuffer(numFloats);
	}

	@Override
	public ShortBuffer newShortBuffer(int numShorts) {
		return NativeSupport.newShortBuffer(numShorts);
	}

	@Override
	public IntBuffer newIntBuffer(int numInts) {
		return NativeSupport.newIntBuffer(numInts);
	}

	@Override
	public void put(Buffer buffer, float[] source, int offset, int length) {
		NativeSupport.put(buffer, source, offset, length);
	}

	@Override
	public int getAllocatedBytesUnsafe() {
		return NativeSupport.getAllocatedBytesUnsafe();
	}

	@Override
	public void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		NativeSupport.disposeUnsafeByteBuffer(buffer);
	}

	@Override
	public ByteBuffer newUnsafeByteBuffer(int numBytes) {
		return NativeSupport.newUnsafeByteBuffer(numBytes);
	}

	@Override
	public ByteBuffer allocateDirect(int capacity) {
		return NativeSupport.allocateDirect(capacity);
	}

	@Override
	public void clear(Buffer buffer) {
		NativeSupport.clear(buffer);
	}

	@Override
	public void filterColor(int maxPixel, int pixelStart, int pixelEnd,
			int[] src, int[] dst, int[] colors, int c1, int c2) {
		NativeSupport.filterColor(maxPixel, pixelStart, pixelEnd, src, dst,
				colors, c1, c2);
	}

	@Override
	public void filterFractions(int size, float[] fractions, int width,
			int height, int[] pixels, int numElements) {
		NativeSupport.filterFractions(size, fractions, width, height, pixels,
				numElements);
	}

	@Override
	public void mul(float[] mata, float[] matb) {
		NativeSupport.mul(mata, matb);
	}

	@Override
	public void mulVec(float[] mat, float[] vec) {
		NativeSupport.mulVec(mat, vec);
	}

	@Override
	public void mulVec(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {
		NativeSupport.mulVec(mat, vecs, offset, numVecs, stride);
	}

	@Override
	public void prj(float[] mat, float[] vec) {
		NativeSupport.prj(mat, vec);
	}

	@Override
	public void prj(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {
		NativeSupport.prj(mat, vecs, offset, numVecs, stride);
	}

	@Override
	public void rot(float[] mat, float[] vec) {
		NativeSupport.rot(mat, vec);
	}

	@Override
	public void rot(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {
		NativeSupport.rot(mat, vecs, offset, numVecs, stride);
	}

	@Override
	public boolean inv(float[] values) {
		return NativeSupport.inv(values);
	}

	@Override
	public float det(float[] values) {
		return NativeSupport.det(values);
	}

	@Override
	public int[] toColorKey(int[] buffer, int colorKey) {
		return NativeSupport.toColorKey(buffer, colorKey);
	}

	@Override
	public int[] toColorKeys(int[] buffer, int[] colors) {
		return NativeSupport.toColorKeys(buffer, colors);
	}

	@Override
	public int[] toColorKeyLimit(int[] buffer, int start, int end) {
		return NativeSupport.toColorKeyLimit(buffer, start, end);
	}

	@Override
	public int[] toGray(int[] buffer, int w, int h) {
		return NativeSupport.toGray(buffer, w, h);
	}

	@Override
	public boolean isNative() {
		return NativeSupport.UseLoonNative();
	}

}
