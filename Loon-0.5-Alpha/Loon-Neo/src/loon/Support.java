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
package loon;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface Support {

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
	
	public boolean isNative();

	public void makeBuffer(byte[] data, int size, int tag);
	
	public void copy(float[] src, Buffer dst, int numFloats);

	public void copy(float[] src, Buffer dst, int offset, int numFloats);

	public IntBuffer newIntBuffer(final int[] src);

	public FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats);

	public void copy(byte[] src, int srcOffset, Buffer dst, int numElements);

	public void copy(short[] src, int srcOffset, Buffer dst, int numElements);

	public void copy(int[] src, int srcOffset, Buffer dst, int numElements);

	public void copy(float[] src, int srcOffset, Buffer dst, int numElements);

	public ByteBuffer replaceBytes(ByteBuffer dst, float[] src);

	public FloatBuffer replaceFloats(FloatBuffer dst, float[] src);

	public ByteBuffer getByteBuffer(byte[] bytes);

	public FloatBuffer getFloatBuffer(float[] floats);

	public ByteBuffer newByteBuffer(int numBytes);

	public FloatBuffer newFloatBuffer(int numFloats);

	public ShortBuffer newShortBuffer(int numShorts);

	public IntBuffer newIntBuffer(int numInts);

	public int getAllocatedBytesUnsafe();

	public void disposeUnsafeByteBuffer(ByteBuffer buffer);

	public ByteBuffer newUnsafeByteBuffer(int numBytes);

	public ByteBuffer allocateDirect(final int capacity);

	public void clear(Buffer buffer);

	public void filterColor(int maxPixel, int pixelStart, int pixelEnd,
			int[] src, int[] dst, int[] colors, int c1, int c2);

	public void filterFractions(int size, float[] fractions, int width,
			int height, int[] pixels, int numElements);

	public void mul(float[] mata, float[] matb);

	public void mulVec(float[] mat, float[] vec);

	public void mulVec(float[] mat, float[] vecs, int offset, int numVecs,
			int stride);

	public void prj(float[] mat, float[] vec);

	public void prj(float[] mat, float[] vecs, int offset, int numVecs,
			int stride);

	public void rot(float[] mat, float[] vec);

	public void rot(float[] mat, float[] vecs, int offset, int numVecs,
			int stride);

	public boolean inv(float[] values);

	public float det(float[] values);

	public int[] toColorKey(int[] buffer, int colorKey);

	public int[] toColorKeys(int[] buffer, int[] colors);

	public int[] toColorKeyLimit(int[] buffer, int start, int end);

	public int[] toGray(int[] buffer, int w, int h);

}
