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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.CRC32;

import loon.canvas.LColor;

public final class NativeSupport {

	public static boolean isAllowLoonLoadLwjglLib = true;

	private final static HashSet<String> loadedLibraries = new HashSet<String>();

	private static String getProperty(final String propName) {
		return System.getProperty(propName, "").toLowerCase();
	}

	static ClassLoader classLoader;

	static boolean isWindows = getProperty("os.name").contains("windows");
	static boolean isLinux = getProperty("os.name").contains("linux");
	static boolean isFreeBSD = getProperty("os.name").contains("bsd");
	static boolean isMac = getProperty("os.name").contains("mac");
	static boolean isARM = getProperty("os.arch").startsWith("arm") || getProperty("os.arch").startsWith("aarch64");
	static boolean is64Bit = getProperty("os.arch").contains("64") || getProperty("os.arch").startsWith("armv8");
	static boolean isUnknown = !(isWindows && isLinux && isMac && isARM && is64Bit);
	static boolean isAndroid = false;
	static boolean isIos = false;

	static {
		try {
			classLoader = NativeSupport.class.getClassLoader();
		} catch (Exception e) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		String vm = getProperty("java.vm.name");
		if (vm != null && vm.contains("Dalvik")) {
			isAndroid = true;
			isWindows = false;
			isLinux = false;
			isFreeBSD = false;
			isMac = false;
			is64Bit = false;
		} else if (!isAndroid && !isWindows && !isLinux && !isMac && !isFreeBSD) {
			isIos = true;
			isAndroid = false;
			isWindows = false;
			isLinux = false;
			isFreeBSD = false;
			isMac = false;
			is64Bit = false;
		}
		System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");
		if (!isInJavaWebStart()) {
			if (isAllowLoonLoadLwjglLib) {
				File nativesDir = null;
				try {
					if (isWindows) {
						nativesDir = export(is64Bit ? "lwjgl.dll" : "lwjgl32.dll", null).getParentFile();
						export(is64Bit ? "OpenAL.dll" : "OpenAL32.dll", nativesDir.getName());
						export(is64Bit ? "glfw.dll" : "glfw32.dll", nativesDir.getName());
						export(is64Bit ? "jemalloc.dll" : "jemalloc32.dll", nativesDir.getName());
					} else if (isMac) {
						nativesDir = export("liblwjgl.dylib", null).getParentFile();
						export("libglfw.dylib", nativesDir.getName());
						export("libjemalloc.dylib", nativesDir.getName());
						export("libopenal.dylib", nativesDir.getName());
					} else if (isLinux || isFreeBSD) {
						nativesDir = export(is64Bit ? "liblwjgl.so" : "liblwjgl32.so", null).getParentFile();
						export(is64Bit ? "libglfw.so" : "libglfw32.so", nativesDir.getName());
						export(is64Bit ? "libjemalloc.so" : "libjemalloc32.so", nativesDir.getName());
						export(is64Bit ? "libopenal.so" : "libopenal32.so", nativesDir.getName());
					}
				} catch (Throwable ex) {
					throw new RuntimeException("Unable to extract LWJGL natives.", ex);
				}
				System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
			}
			try {
				loadJNI("lplus");
				useLoonNative = true;
				System.out.println("Support of the native method call");
			} catch (Throwable e) {
				useLoonNative = false;
			}
		} else {
			useLoonNative = true;
		}
	}

	private static InputStream openResource(final String resName) throws IOException {
		File file = new File(resName);
		if (file.exists()) {
			try {
				return new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new IOException(resName + " file not found !");
			}
		} else {
			if (classLoader != null) {
				InputStream in = null;
				try {
					in = classLoader.getResourceAsStream(resName);
				} catch (Exception e) {
					throw new RuntimeException(resName + " not found!");
				}
				return in;
			} else {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					throw new IOException(resName + " not found!");
				}
			}
		}
	}

	private static String getLoonPath(final String flag) {
		return "/" + flag + "loonlwjgl" + getProperty("user.name");
	}

	private static String getLoonPath() {
		return getLoonPath("");
	}

	public static String CRC(InputStream input) {
		if (input == null) {
			return "" + System.nanoTime();
		}
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096];
		try {
			for (;;) {
				int length = input.read(buffer);
				if (length == -1) {
					break;
				}
				crc.update(buffer, 0, length);
			}
		} catch (Exception ex) {
			try {
				input.close();
			} catch (Exception ignored) {
			}
		}
		return Long.toString(crc.getValue());
	}

	public static String libNames(String libraryName) {
		if (isWindows) {
			return libraryName + (is64Bit ? "64.dll" : ".dll");
		}
		if (isLinux || isFreeBSD) {
			return "lib" + libraryName + (is64Bit ? "64.so" : ".so");
		}
		if (isMac) {
			return "lib" + libraryName + ".dylib";
		}
		return libraryName;
	}

	public static synchronized void loadJNI(String libraryName) throws Throwable {
		libraryName = libNames(libraryName);
		if (loadedLibraries.contains(libraryName)) {
			return;
		}
		try {
			synchronized (NativeSupport.class) {
				if (isAndroid) {
					System.loadLibrary(libraryName);
				} else {
					System.load(export(libraryName, null).getAbsolutePath());
				}
				loadedLibraries.add(libraryName);
			}
		} catch (Throwable ex) {
			throw new Exception("Couldn't load shared library '" + libraryName + "' for target: "
					+ getProperty("os.name") + (is64Bit ? ", 64-bit" : ", 32-bit"), ex);
		}
	}

	private static boolean canWrite(File file) {
		File parent = file.getParentFile();
		File tempFile;
		if (file.exists()) {
			if (!file.canWrite() || !canExecute(file)) {
				return false;
			}
			tempFile = new File(parent, UUID.randomUUID().toString());
		} else {
			parent.mkdirs();
			if (!parent.isDirectory()) {
				return false;
			}
			tempFile = file;
		}
		try {
			new FileOutputStream(tempFile).close();
			if (!canExecute(tempFile)) {
				return false;
			}
			return true;
		} catch (Throwable ex) {
			return false;
		} finally {
			tempFile.delete();
		}
	}

	private static boolean canExecute(File file) {
		try {
			Method canExecute = File.class.getMethod("canExecute");
			if ((Boolean) canExecute.invoke(file)) {
				return true;
			}
			Method setExecutable = File.class.getMethod("setExecutable", boolean.class, boolean.class);
			setExecutable.invoke(file, true, false);
			return (Boolean) canExecute.invoke(file);
		} catch (Exception ignored) {
		}
		return false;
	}

	public static File export(String sourcePath, String dirName) throws IOException {
		try {
			InputStream ins = openResource(sourcePath);
			if (ins == null) {
				return null;
			}
			String sourceCrc = CRC(ins);
			if (dirName == null) {
				dirName = sourceCrc;
			}
			File extractedFile = getExportFile(dirName, new File(sourcePath).getName());
			if (extractedFile == null) {
				extractedFile = getExportFile(UUID.randomUUID().toString(), new File(sourcePath).getName());
				if (extractedFile == null) {
					throw new IOException(
							"Unable to find writable path to extract file. Is the user home directory writable?");
				}
			}
			return export(sourcePath, sourceCrc, extractedFile);
		} catch (RuntimeException ex) {
			File file = new File(getProperty("java.library.path"), sourcePath);
			if (file.exists()) {
				return file;
			}
			throw ex;
		}
	}

	private static File export(String sourcePath, String sourceCrc, File extractedFile) throws IOException {
		String extractedCrc = null;
		if (extractedFile.exists()) {
			try {
				extractedCrc = CRC(new FileInputStream(extractedFile));
			} catch (FileNotFoundException ignored) {
			}
		}
		if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
			InputStream input = null;
			FileOutputStream output = null;
			try {
				input = openResource(sourcePath);
				if (input == null) {
					return null;
				}
				boolean canCreated = extractedFile.getParentFile().mkdirs();
				output = new FileOutputStream(extractedFile);
				byte[] buffer = new byte[4096];
				for (;;) {
					int length = input.read(buffer);
					if (length == -1) {
						break;
					}
					output.write(buffer, 0, length);
				}
				if (!canCreated && !extractedFile.exists()) {
					throw new IOException(
							"Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath());
				}
			} catch (IOException ex) {
				throw new IOException(
						"Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath(), ex);
			} finally {
				try {
					input.close();
					input = null;
					output.close();
					output = null;
				} catch (Exception ignored) {
				}
			}
		}

		return extractedFile;
	}

	private static File getExportFile(String dirName, String fileName) {
		File idealFile = new File(getProperty("java.io.tmpdir") + getLoonPath() + "/" + dirName, fileName);
		if (canWrite(idealFile)) {
			return idealFile;
		}
		try {
			File file = File.createTempFile(dirName, null);
			if (file.delete()) {
				file = new File(file, fileName);
				if (canWrite(file)) {
					return file;
				}
			}
		} catch (IOException ignored) {
		}

		File file = new File(getProperty("user.home") + getLoonPath(".") + dirName, fileName);
		if (canWrite(file)) {
			return file;
		}
		file = new File(".temp/" + dirName, fileName);
		if (canWrite(file)) {
			return file;
		}
		if (System.getenv("APP_SANDBOX_CONTAINER_ID") != null) {
			return idealFile;
		}
		return null;
	}

	private static boolean nativesLoaded;

	public static final int SIZEOF_BYTE = 1;

	public static final int SIZEOF_SHORT = 2;

	public static final int SIZEOF_FLOAT = 4;

	public static final int SIZEOF_INT = SIZEOF_FLOAT;

	public static final int SIZEOF_DOUBLE = 8;

	public static final int SIZEOF_LONG = SIZEOF_DOUBLE;

	private static boolean useLoonNative = false;

	private static boolean isInJavaWebStart() {
		try {
			Method method = Class.forName("javax.jnlp.ServiceManager").getDeclaredMethod("lookup",
					new Class<?>[] { String.class });
			method.invoke(null, "javax.jnlp.PersistenceService");
			return true;
		} catch (Throwable ignored) {
			return false;
		}
	}

	public static boolean UseLoonNative() {
		return useLoonNative;
	}

	public static void OpenLoonNative() {
		useLoonNative = true;
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
			dst.position(0);
			if (dst instanceof ByteBuffer)
				dst.limit(numFloats << 2);
			else if (dst instanceof FloatBuffer)
				dst.limit(numFloats);
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

	public static FloatBuffer newFloatBuffer(float[] src, int offset, int numFloats) {
		FloatBuffer buffer = newFloatBuffer(numFloats);
		copy(src, buffer, offset, numFloats);
		return buffer;
	}

	public static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements);
			dst.limit(dst.position() + bytesToElements(dst, numElements));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 1);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 1);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	public static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
		if (useLoonNative) {
			bufferCopy(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
			dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
		} else {
			putBuffer(dst, src, srcOffset, numElements);
		}
	}

	private static int positionInBytes(Buffer dst) {
		if (dst instanceof ByteBuffer) {
			return dst.position();
		} else if (dst instanceof ShortBuffer) {
			return dst.position() << 1;
		} else if (dst instanceof IntBuffer) {
			return dst.position() << 2;
		} else if (dst instanceof FloatBuffer) {
			return dst.position() << 2;
		} else {
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
		}
	}

	private static int bytesToElements(Buffer dst, int bytes) {
		if (dst instanceof ByteBuffer) {
			return bytes;
		} else if (dst instanceof ShortBuffer) {
			return bytes >>> 1;
		} else if (dst instanceof IntBuffer) {
			return bytes >>> 2;
		} else if (dst instanceof FloatBuffer) {
			return bytes >>> 2;
		} else {
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
		}
	}

	private static int elementsToBytes(Buffer dst, int elements) {
		if (dst instanceof ByteBuffer) {
			return elements;
		} else if (dst instanceof ShortBuffer) {
			return elements << 1;
		} else if (dst instanceof IntBuffer) {
			return elements << 2;
		} else if (dst instanceof FloatBuffer) {
			return elements << 2;
		} else {
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
		}
	}

	private static void putBuffer(Buffer dst, Object src, int offset, int numFloats) {
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
		ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	public static FloatBuffer newFloatBuffer(int numFloats) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	public static ShortBuffer newShortBuffer(int numShorts) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asShortBuffer();
	}

	public static IntBuffer newIntBuffer(int numInts) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	private static ArrayList<ByteBuffer> unsafeBuffers = new ArrayList<ByteBuffer>();

	private static int allocatedUnsafe = 0;

	public static int getAllocatedBytesUnsafe() {
		return allocatedUnsafe;
	}

	public static void disposeUnsafeByteBuffer(ByteBuffer buffer) {
		int size = buffer.capacity();
		synchronized (unsafeBuffers) {
			if (!unsafeBuffers.remove(buffer)) {
				throw new IllegalArgumentException("buffer not allocated with newUnsafeByteBuffer or already disposed");
			}
		}
		allocatedUnsafe -= size;
		freeMemory(buffer);
	}

	public static ByteBuffer newUnsafeByteBuffer(int numBytes) {
		if (useLoonNative) {
			ByteBuffer buffer = allocateDirect(numBytes);
			buffer.order(ByteOrder.nativeOrder());
			allocatedUnsafe += numBytes;
			synchronized (unsafeBuffers) {
				unsafeBuffers.add(buffer);
			}
			return buffer;
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
			buffer.order(ByteOrder.nativeOrder());
			allocatedUnsafe += numBytes;
			synchronized (unsafeBuffers) {
				unsafeBuffers.add(buffer);
			}
			return buffer;
		}
	}

	public static ByteBuffer allocateDirect(final int capacity) {
		if (useLoonNative) {
			return bufferDirect(capacity);
		} else {
			return ByteBuffer.allocateDirect(capacity);
		}
	}

	private static void freeMemory(Buffer buffer) {
		if (useLoonNative) {
			bufferFreeDirect(buffer);
		} else {
			buffer.clear();
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

	public final static void filterColor(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst, int[] colors,
			int c1, int c2) {
		if (useLoonNative) {
			if (src == null) {
				return;
			}
			updateArray(maxPixel, pixelStart, pixelEnd, src, dst, colors, c1, c2);
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

	public static void filterFractions(int size, float[] fractions, int width, int height, int[] pixels,
			int numElements) {
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
	}

	public static void mulVec(float[] mat, float[] vec) {
		if (useLoonNative) {
			jnimulVec(mat, vec);
		} else {
			float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03];
			float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13];
			float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23];
			vec[0] = x;
			vec[1] = y;
			vec[2] = z;
		}

	}

	public static void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
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
			float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
			float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
			float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w;
			float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
			vec[0] = x;
			vec[1] = y;
			vec[2] = z;
		}
	}

	public static void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
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

	public static void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
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

	public static int[] toColorKey(int[] buffer, int colorKey) {
		if (useLoonNative) {
			return setColorKey(buffer, colorKey);
		} else {
			int size = buffer.length;
			for (int i = 0; i < size; i++) {
				int pixel = buffer[i];
				if (pixel == colorKey) {
					buffer[i] = 0x00FFFFFF;
				}
			}
		}
		return buffer;
	}

	public static int[] toColorKeys(int[] buffer, int[] colors) {
		if (useLoonNative) {
			return setColorKeys(buffer, colors);
		} else {
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
		}
		return buffer;
	}

	public static int[] toColorKeyLimit(int[] buffer, int start, int end) {
		if (useLoonNative) {
			return setColorKeyLimit(buffer, start, end);
		} else {
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
					buffer[i] = 0x00FFFFFF;
				}
			}
		}
		return buffer;
	}

	public static int[] toGray(int[] buffer, int w, int h) {
		if (useLoonNative) {
			return getGray(buffer, w, h);
		} else {
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
	}

	private native static void jniencode(byte[] src, int size, int tag);

	private native static void jnimul(float[] mata, float[] matb);

	private native static void jnimulVec(float[] mat, float[] vec);

	private native static void jnimulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride);

	private native static void jniprj(float[] mat, float[] vec);

	private native static void jniprj(float[] mat, float[] vecs, int offset, int numVecs, int stride);

	private native static void jnirot(float[] mat, float[] vec);

	private native static void jnirot(float[] mat, float[] vecs, int offset, int numVecs, int stride);

	private native static boolean jniinv(float[] values);

	private native static float jnidet(float[] values);

	private native static ByteBuffer bufferDirect(final int size);

	private native static void bufferClear(Buffer buffer, int numBytes);

	private native static void bufferFreeDirect(final Buffer buffer);

	private native static void bufferPut(final Buffer buffer, final float[] source, final int length, final int offset);

	private native static void bufferCopy(float[] src, Buffer dst, int numFloats, int offset);

	private native static void bufferCopy(byte[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(char[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(short[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(int[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(long[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(float[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(double[] src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void bufferCopy(Buffer src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	private native static void updateArray(int maxPixel, int pixelStart, int pixelEnd, int[] src, int[] dst,
			int[] colors, int c1, int c2);

	private native static void updateFractions(int size, float[] src, int width, int height, int[] dst,
			int numElements);

	private native static int[] setColorKey(int[] buffer, int colorKey);

	private native static int[] setColorKeys(int[] buffer, int[] colorKey);

	private native static int[] setColorKeyLimit(int[] buffer, int start, int end);

	private native static int[] getGray(int[] buffer, int w, int h);
}
