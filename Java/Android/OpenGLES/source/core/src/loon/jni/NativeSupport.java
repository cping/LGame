/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version SIZEOF_SHORT.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-SIZEOF_SHORT.0
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
 * @version 0.3.3
 */
package loon.jni;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import loon.core.LSystem;

//自0.3.3起，将部分耗时代码本地化。如果携带有lplus库时，将调用so文件，否则依旧纯Java。
//用户可有选择的使用(事实上，随着Android版本的提高，本地运行与虚拟机运行的速度差距已经
//在逐渐缩小)。
public final class NativeSupport {

	private static final String POSTFIX64BIT = "64";

	private static void doLoadLibrary(final String lib_name) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				String library_path = System
						.getProperty("org.loon.librarypath");
				if (library_path != null) {
					System.load(library_path + File.separator
							+ System.mapLibraryName(lib_name));
				} else {
					System.loadLibrary(lib_name);
				}
				return null;
			}
		});
	}

	public static void loadLibrary(final String lib_name)
			throws UnsatisfiedLinkError {
		String osArch = System.getProperty("os.arch");
		boolean is64bit = "amd64".equals(osArch) || "x86_64".equals(osArch);
		if (is64bit) {
			try {
				doLoadLibrary(lib_name + POSTFIX64BIT);
				return;
			} catch (UnsatisfiedLinkError e) {
			}
		}
		try {
			doLoadLibrary(lib_name);
		} catch (UnsatisfiedLinkError e) {
			try {
				doLoadLibrary(lib_name + POSTFIX64BIT);
				return;
			} catch (UnsatisfiedLinkError ex) {
				throw new UnsatisfiedLinkError(ex.getMessage());
			}
		}
	}

	public static final int SIZEOF_BYTE = 1;

	public static final int SIZEOF_SHORT = 2;

	public static final int SIZEOF_FLOAT = 4;

	public static final int SIZEOF_INT = SIZEOF_FLOAT;

	public static final int SIZEOF_DOUBLE = 8;

	public static final int SIZEOF_LONG = SIZEOF_DOUBLE;

	private static boolean useLoonNative;

	static {
		if (LSystem.isEmulator() && !LSystem.isAndroidVersionHigher(8)) {
			useLoonNative = false;
		} else {
			try {
				loadLibrary("lplus");
				useLoonNative = true;
				System.out.println("Support of the native method call");
			} catch (Error e) {
				useLoonNative = false;
			}
		}
	}

	public static boolean UseLoonNative() {
		return useLoonNative;
	}

	public static void CloseLoonNative() {
		useLoonNative = false;
	}

	public static void makeBuffer(byte[] data, int size, int tag) {
		if (useLoonNative) {
			jniencode(data, size, tag);
		} else {
			for (int i = 0; i < size; i++) {
				data[i] ^= tag;
			}
		}
	}

	public static void copy(float[] src, Buffer dst, int numFloats) {
		copy(src, dst, 0, numFloats);
	}

	public static void copy(float[] src, Buffer dst, int offset, int numFloats) {
		if (useLoonNative) {
			bufferCopy(src, dst, numFloats, offset);
			if (dst instanceof ByteBuffer) {
				dst.limit(numFloats << SIZEOF_SHORT);
			} else if (dst instanceof FloatBuffer) {
				dst.limit(numFloats);
			}
		} else {
			putBuffer(dst, src, offset, numFloats);
		}
	}

	public static IntBuffer newIntBuffer(final int[] src) {
		if (src == null) {
			return null;
		}
		int size = src.length;
		IntBuffer buffer = newIntBuffer(size);
		copy(src, 0, buffer, size);
		return buffer;
	}

	public static FloatBuffer newFloatBuffer(float[] src, int offset,
			int numFloats) {
		FloatBuffer buffer = newFloatBuffer(numFloats);
		copy(src, buffer, offset, numFloats);
		return buffer;
	}

	public static ByteBuffer clone(final ByteBuffer dst) {
		if (dst == null) {
			return null;
		}
		int size = dst.limit();
		ByteBuffer copy = newByteBuffer(size);
		copy(copy, dst, size);
		return copy;
	}

	public static FloatBuffer clone(final FloatBuffer dst) {
		if (dst == null) {
			return null;
		}
		int size = dst.limit();
		FloatBuffer copy = newFloatBuffer(size);
		copy(copy, dst, size);
		return copy;
	}

	public static void copy(byte[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements);
			dst.limit(dst.position() + bytesToElements(dst, numElements));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(short[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << SIZEOF_BYTE, dst,
					positionInBytes(dst), numElements << SIZEOF_BYTE);
			dst.limit(dst.position()
					+ bytesToElements(dst, numElements << SIZEOF_BYTE));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(char[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << SIZEOF_BYTE, dst,
					positionInBytes(dst), numElements << SIZEOF_BYTE);
			dst.limit(dst.position()
					+ bytesToElements(dst, numElements << SIZEOF_BYTE));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(int[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << SIZEOF_SHORT, dst,
					positionInBytes(dst), numElements << SIZEOF_SHORT);
			dst.limit(dst.position()
					+ bytesToElements(dst, numElements << SIZEOF_SHORT));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(long[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << 3, dst, positionInBytes(dst),
					numElements << 3);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(float[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << SIZEOF_SHORT, dst,
					positionInBytes(dst), numElements << SIZEOF_SHORT);
			dst.limit(dst.position()
					+ bytesToElements(dst, numElements << SIZEOF_SHORT));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(double[] src, int srcOffset, Buffer dst,
			int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset << 3, dst, positionInBytes(dst),
					numElements << 3);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(Buffer src, Buffer dst, int numElements) {
		if (useLoonNative) {
			int numBytes = elementsToBytes(src, numElements);
			bufferCopy(src, positionInBytes(src), dst, positionInBytes(dst),
					numBytes);
			dst.limit(dst.position() + bytesToElements(dst, numBytes));
		} else {
			putBuffer(dst, src, numElements);
		}
	}

	private static int positionInBytes(Buffer dst) {
		if (dst instanceof ByteBuffer) {
			return dst.position();
		} else if (dst instanceof ShortBuffer) {
			return dst.position() << SIZEOF_BYTE;
		} else if (dst instanceof CharBuffer) {
			return dst.position() << SIZEOF_BYTE;
		} else if (dst instanceof IntBuffer) {
			return dst.position() << SIZEOF_SHORT;
		} else if (dst instanceof LongBuffer) {
			return dst.position() << 3;
		} else if (dst instanceof FloatBuffer) {
			return dst.position() << SIZEOF_SHORT;
		} else if (dst instanceof DoubleBuffer) {
			return dst.position() << 3;
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
	}

	private static int bytesToElements(Buffer dst, int bytes) {
		if (dst instanceof ByteBuffer) {
			return bytes;
		} else if (dst instanceof ShortBuffer) {
			return bytes >>> SIZEOF_BYTE;
		} else if (dst instanceof CharBuffer) {
			return bytes >>> SIZEOF_BYTE;
		} else if (dst instanceof IntBuffer) {
			return bytes >>> SIZEOF_SHORT;
		} else if (dst instanceof LongBuffer) {
			return bytes >>> 3;
		} else if (dst instanceof FloatBuffer) {
			return bytes >>> SIZEOF_SHORT;
		} else if (dst instanceof DoubleBuffer) {
			return bytes >>> 3;
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
	}

	private static int elementsToBytes(Buffer dst, int elements) {
		if (dst instanceof ByteBuffer) {
			return elements;
		} else if (dst instanceof ShortBuffer) {
			return elements << SIZEOF_BYTE;
		} else if (dst instanceof CharBuffer) {
			return elements << SIZEOF_BYTE;
		} else if (dst instanceof IntBuffer) {
			return elements << SIZEOF_SHORT;
		} else if (dst instanceof LongBuffer) {
			return elements << 3;
		} else if (dst instanceof FloatBuffer) {
			return elements << SIZEOF_SHORT;
		} else if (dst instanceof DoubleBuffer) {
			return elements << 3;
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
	}

	private static void putBuffer(Buffer dst, Object src, int offset,
			int numFloats) {
		if (dst instanceof ByteBuffer) {
			byte[] buffer = (byte[]) src;
			ByteBuffer writer = (ByteBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof ShortBuffer) {
			short[] buffer = (short[]) src;
			ShortBuffer writer = (ShortBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof CharBuffer) {
			char[] buffer = (char[]) src;
			CharBuffer writer = (CharBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof IntBuffer) {
			int[] buffer = (int[]) src;
			IntBuffer writer = (IntBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof LongBuffer) {
			long[] buffer = (long[]) src;
			LongBuffer writer = (LongBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof FloatBuffer) {
			float[] buffer = (float[]) src;
			FloatBuffer writer = (FloatBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else if (dst instanceof DoubleBuffer) {
			double[] buffer = (double[]) src;
			DoubleBuffer writer = (DoubleBuffer) dst;
			writer.limit(numFloats);
			writer.put(buffer, offset, numFloats);
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
		dst.position(0);
	}

	private static void putBuffer(Buffer dst, Buffer src, int numFloats) {
		if (dst instanceof ByteBuffer) {
			ByteBuffer buffer = (ByteBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((ByteBuffer) src);
		} else if (dst instanceof ShortBuffer) {
			ShortBuffer buffer = (ShortBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((ShortBuffer) src);
		} else if (dst instanceof CharBuffer) {
			CharBuffer buffer = (CharBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((CharBuffer) src);
		} else if (dst instanceof IntBuffer) {
			IntBuffer buffer = (IntBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((IntBuffer) src);
		} else if (dst instanceof LongBuffer) {
			LongBuffer buffer = (LongBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((LongBuffer) src);
		} else if (dst instanceof FloatBuffer) {
			FloatBuffer buffer = (FloatBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((FloatBuffer) src);
		} else if (dst instanceof DoubleBuffer) {
			DoubleBuffer buffer = (DoubleBuffer) dst;
			buffer.limit(numFloats);
			buffer.put((DoubleBuffer) src);
		} else {
			throw new RuntimeException("Can't copy to a "
					+ dst.getClass().getName() + " instance");
		}
		dst.position(0);
	}

	public static ByteBuffer replaceBytes(ByteBuffer dst, float[] src) {
		int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	public static FloatBuffer replaceFloats(FloatBuffer dst, float[] src) {
		int size = src.length;
		dst.clear();
		copy(src, 0, dst, size);
		dst.position(0);
		return dst;
	}

	public static ByteBuffer getByteBuffer(byte[] bytes) {
		if (useLoonNative) {
			final int size = bytes.length;
			ByteBuffer buffer = newByteBuffer(size);
			copy(bytes, 0, buffer, size);
			buffer.position(0);
			return buffer;
		} else {
			ByteBuffer buffer = newByteBuffer(bytes.length).put(bytes);
			buffer.position(0);
			return buffer;
		}
	}

	public static FloatBuffer getFloatBuffer(float[] floats) {
		if (useLoonNative) {
			final int size = floats.length;
			FloatBuffer buffer = newFloatBuffer(size);
			copy(floats, 0, buffer, size);
			buffer.position(0);
			return buffer;
		} else {
			FloatBuffer buffer = newFloatBuffer(floats.length).put(floats);
			buffer.position(0);
			return buffer;
		}
	}

	public static ByteBuffer newByteBuffer(int numBytes) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numBytes);
			buffer.order(ByteOrder.nativeOrder());
			return buffer;
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
			buffer.order(ByteOrder.nativeOrder());
			return buffer;
		}
	}

	public static FloatBuffer newFloatBuffer(int numFloats) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numFloats * SIZEOF_FLOAT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asFloatBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats
					* SIZEOF_FLOAT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asFloatBuffer();
		}
	}

	public static DoubleBuffer newDoubleBuffer(int numDoubles) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numDoubles * SIZEOF_DOUBLE);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asDoubleBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numDoubles
					* SIZEOF_DOUBLE);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asDoubleBuffer();
		}
	}

	public static ShortBuffer newShortBuffer(int numShorts) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numShorts * SIZEOF_SHORT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asShortBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts
					* SIZEOF_SHORT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asShortBuffer();
		}
	}

	public static CharBuffer newCharBuffer(int numChars) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numChars * SIZEOF_SHORT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asCharBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numChars
					* SIZEOF_SHORT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asCharBuffer();
		}
	}

	public static IntBuffer newIntBuffer(int numInts) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numInts * SIZEOF_FLOAT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asIntBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numInts
					* SIZEOF_FLOAT);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asIntBuffer();
		}
	}

	public static LongBuffer newLongBuffer(int numLongs) {
		if (useLoonNative) {
			ByteBuffer buffer = bufferDirect(numLongs * SIZEOF_DOUBLE);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asLongBuffer();
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numLongs
					* SIZEOF_DOUBLE);
			buffer.order(ByteOrder.nativeOrder());
			return buffer.asLongBuffer();
		}
	}

	public static void put(final Buffer buffer, final float[] source,
			final int offset, final int length) {
		if (useLoonNative) {
			bufferPut(buffer, source, length, offset);
			buffer.position(0);
			buffer.limit(length << SIZEOF_SHORT);
		} else {
			putBuffer(buffer, source, offset, length);
		}
	}

	public static ByteBuffer allocateDirect(final int capacity) {
		if (useLoonNative) {
			return bufferDirect(capacity);
		} else {
			return ByteBuffer.allocateDirect(capacity);
		}
	}

	public static void freeMemory(Buffer buffer) {
		if (useLoonNative) {
			bufferFreeDirect(buffer);
		} else {
			buffer = null;
		}
	}

	public static void clear(Buffer buffer) {
		if (useLoonNative) {
			bufferClear(buffer, buffer.limit());
		} else {
			buffer.clear();
		}
	}

	public final static void filterColor(int maxPixel, int pixelStart,
			int pixelEnd, int[] src, int[] dst, int[] colors, int c1, int c2) {
		if (useLoonNative) {
			if (src == null) {
				return;
			}
			updateArray(maxPixel, pixelStart, pixelEnd, src, dst, colors, c1,
					c2);
		} else {
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
	}

	public static void filterFractions(int size, float[] fractions, int width,
			int height, int[] pixels, int numElements) {
		if (useLoonNative) {
			updateFractions(size, fractions, width, height, pixels, numElements);
		} else {
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

	public static void mul(float[] mata, float[] matb) {
		if (useLoonNative) {
			jnimul(mata, matb);
		} else {
			float[] tmp = new float[16];
			tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10]
					+ mata[M02] * matb[M20] + mata[M03] * matb[M30];
			tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11]
					+ mata[M02] * matb[M21] + mata[M03] * matb[M31];
			tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12]
					+ mata[M02] * matb[M22] + mata[M03] * matb[M32];
			tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13]
					+ mata[M02] * matb[M23] + mata[M03] * matb[M33];
			tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10]
					+ mata[M12] * matb[M20] + mata[M13] * matb[M30];
			tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11]
					+ mata[M12] * matb[M21] + mata[M13] * matb[M31];
			tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12]
					+ mata[M12] * matb[M22] + mata[M13] * matb[M32];
			tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13]
					+ mata[M12] * matb[M23] + mata[M13] * matb[M33];
			tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10]
					+ mata[M22] * matb[M20] + mata[M23] * matb[M30];
			tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11]
					+ mata[M22] * matb[M21] + mata[M23] * matb[M31];
			tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12]
					+ mata[M22] * matb[M22] + mata[M23] * matb[M32];
			tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13]
					+ mata[M22] * matb[M23] + mata[M23] * matb[M33];
			tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10]
					+ mata[M32] * matb[M20] + mata[M33] * matb[M30];
			tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11]
					+ mata[M32] * matb[M21] + mata[M33] * matb[M31];
			tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12]
					+ mata[M32] * matb[M22] + mata[M33] * matb[M32];
			tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13]
					+ mata[M32] * matb[M23] + mata[M33] * matb[M33];
			System.arraycopy(tmp, 0, mata, 0, 16);
		}
	}

	public static void mulVec(float[] mat, float[] vec) {
		if (useLoonNative) {
			jnimulVec(mat, vec);
		} else {
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

	}

	public static void mulVec(float[] mat, float[] vecs, int offset,
			int numVecs, int stride) {
		if (useLoonNative) {
			jnimulVec(mat, vecs, offset, numVecs, stride);
		} else {
			for (int i = 0; i < numVecs; i++) {
				float[] vecPtr = new float[stride];
				System.arraycopy(vecs, offset, vecPtr, 0, stride);
				mulVec(mat, vecPtr);
			}
		}
	}

	public static void prj(float[] mat, float[] vec) {
		if (useLoonNative) {
			jniprj(mat, vec);
		} else {
			float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31]
					+ vec[2] * mat[M32] + mat[M33]);
			float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2]
					* mat[M02] + mat[M03])
					* inv_w;
			float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2]
					* mat[M12] + mat[M13])
					* inv_w;
			float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2]
					* mat[M22] + mat[M23])
					* inv_w;
			vec[0] = x;
			vec[1] = y;
			vec[2] = z;
		}
	}

	public static void prj(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {
		if (useLoonNative) {
			jniprj(mat, vecs, offset, numVecs, stride);
		} else {
			for (int i = 0; i < numVecs; i++) {
				float[] vecPtr = new float[stride];
				System.arraycopy(vecs, offset, vecPtr, 0, stride);
				prj(mat, vecPtr);
			}
		}
	}

	public static void rot(float[] mat, float[] vec) {
		if (useLoonNative) {
			jnirot(mat, vec);
		} else {
			float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
			float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
			float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
			vec[0] = x;
			vec[1] = y;
			vec[2] = z;
		}
	}

	public static void rot(float[] mat, float[] vecs, int offset, int numVecs,
			int stride) {
		if (useLoonNative) {
			jnirot(mat, vecs, offset, numVecs, stride);
		} else {
			for (int i = 0; i < numVecs; i++) {
				float[] vecPtr = new float[stride];
				System.arraycopy(vecs, offset, vecPtr, 0, stride);
				rot(mat, vecPtr);
			}
		}
	}

	public static boolean inv(float[] values) {
		if (useLoonNative) {
			return jniinv(values);
		} else {
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
	}

	public static float det(float[] values) {
		if (useLoonNative) {
			return jnidet(values);
		} else {
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
	}

	private native static void jniencode(byte[] src, int size, int tag);

	private native static void jnimul(float[] mata, float[] matb);

	private native static void jnimulVec(float[] mat, float[] vec);

	private native static void jnimulVec(float[] mat, float[] vecs, int offset,
			int numVecs, int stride);

	private native static void jniprj(float[] mat, float[] vec);

	private native static void jniprj(float[] mat, float[] vecs, int offset,
			int numVecs, int stride);

	private native static void jnirot(float[] mat, float[] vec);

	private native static void jnirot(float[] mat, float[] vecs, int offset,
			int numVecs, int stride);

	private native static boolean jniinv(float[] values);

	private native static float jnidet(float[] values);

	private native static ByteBuffer bufferDirect(final int size);

	private native static void bufferClear(Buffer buffer, int numBytes);

	private native static void bufferFreeDirect(final Buffer buffer);

	private native static void bufferPut(final Buffer buffer,
			final float[] source, final int length, final int offset);

	private native static void bufferCopy(float[] src, Buffer dst,
			int numFloats, int offset);

	private native static void bufferCopy(byte[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(char[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(short[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(int[] src, int srcOffset, Buffer dst,
			int dstOffset, int numBytes);

	private native static void bufferCopy(long[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(float[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(double[] src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(Buffer src, int srcOffset,
			Buffer dst, int dstOffset, int numBytes);

	private native static void updateArray(int maxPixel, int pixelStart,
			int pixelEnd, int[] src, int[] dst, int[] colors, int c1, int c2);

	private native static void updateFractions(int size, float[] src,
			int width, int height, int[] dst, int numElements);
}
