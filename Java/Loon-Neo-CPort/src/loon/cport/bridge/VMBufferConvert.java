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
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.teavm.interop.Address;

import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;

public final class VMBufferConvert {

	final static class TeaVMBufferState {

		final int position;
		final int limit;
		final int mark;
		final int capacity;

		TeaVMBufferState(Buffer buffer) {
			this.position = buffer.position();
			this.limit = buffer.limit();
			this.capacity = buffer.capacity();
			int tempMark = -1;
			try {
				buffer.reset();
				tempMark = buffer.position();
			} catch (Exception e) {
				tempMark = -1;
			} finally {
				buffer.position(this.position);
			}
			this.mark = tempMark;
		}
	}

	final static class TeaVMBufferStatePool {

		boolean saveAndRestore;

		final ObjectMap<Buffer, TeaVMBufferState> stateMap = new ObjectMap<Buffer, TeaVMBufferState>();

		private TeaVMBufferStatePool(boolean sr) {
			saveAndRestore = sr;
		}

		public void save(Buffer buffer) {
			if (saveAndRestore) {
				if (stateMap.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
					stateMap.clear();
				}
				stateMap.put(buffer, new TeaVMBufferState(buffer));
			}
		}

		public void saveAll(Buffer... buffers) {
			if (saveAndRestore) {
				for (Buffer b : buffers) {
					save(b);
				}
			}
		}

		public void restore(Buffer buffer) {
			if (saveAndRestore && stateMap.size > 0) {
				TeaVMBufferState state = stateMap.get(buffer);
				if (state != null) {
					buffer.position(state.position);
					buffer.limit(state.limit);
					if (state.mark >= 0) {
						buffer.position(state.mark);
						buffer.mark();
						buffer.position(state.position);
					}
				}
			} else {
				buffer.rewind();
			}
		}

		public void restoreAll() {
			if (saveAndRestore) {
				for (Keys<Buffer> keys = stateMap.keys(); keys.hasNext();) {
					Buffer buffer = keys.next();
					if (buffer != null) {
						restore(buffer);
					}
				}
			}
		}

		public void freeState(Buffer buffer) {
			if (buffer != null) {
				stateMap.remove(buffer);
			}
		}

		public void clear() {
			stateMap.clear();
		}
	}

	private final static TeaVMBufferStatePool bufferStatePool = new TeaVMBufferStatePool(false);

	private static final VMWeakMap<ByteBuffer, Map<Integer, byte[]>> byteCache = new VMWeakMap<ByteBuffer, Map<Integer, byte[]>>();
	private static final VMWeakMap<ShortBuffer, Map<Integer, short[]>> shortCache = new VMWeakMap<ShortBuffer, Map<Integer, short[]>>();
	private static final VMWeakMap<IntBuffer, Map<Integer, int[]>> intCache = new VMWeakMap<IntBuffer, Map<Integer, int[]>>();
	private static final VMWeakMap<LongBuffer, Map<Integer, long[]>> longCache = new VMWeakMap<LongBuffer, Map<Integer, long[]>>();
	private static final VMWeakMap<FloatBuffer, Map<Integer, float[]>> floatCache = new VMWeakMap<FloatBuffer, Map<Integer, float[]>>();
	private static final VMWeakMap<DoubleBuffer, Map<Integer, double[]>> doubleCache = new VMWeakMap<DoubleBuffer, Map<Integer, double[]>>();
	private static final VMWeakMap<CharBuffer, Map<Integer, char[]>> charCache = new VMWeakMap<CharBuffer, Map<Integer, char[]>>();

	private static <B, A> Map<Integer, A> getOrCreatePool(VMWeakMap<B, Map<Integer, A>> cache, B buffer) {
		Map<Integer, A> pool = cache.get(buffer);
		if (pool == null) {
			pool = Collections.synchronizedMap(new LinkedHashMap<Integer, A>(16, 0.75f, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<Integer, A> eldest) {
					return size() > LSystem.DEFAULT_MAX_CACHE_SIZE;
				}
			});
			cache.put(buffer, pool);
		}
		return pool;
	}

	public static byte[] getArray(ByteBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, byte[]> pool = getOrCreatePool(byteCache, buffer);
		byte[] result = pool.get(size);
		if (result == null) {
			result = new byte[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static short[] getArray(ShortBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, short[]> pool = getOrCreatePool(shortCache, buffer);
		short[] result = pool.get(size);
		if (result == null) {
			result = new short[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static int[] getArray(IntBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, int[]> pool = getOrCreatePool(intCache, buffer);
		int[] result = pool.get(size);
		if (result == null) {
			result = new int[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static long[] getArray(LongBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, long[]> pool = getOrCreatePool(longCache, buffer);
		long[] result = pool.get(size);
		if (result == null) {
			result = new long[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static float[] getArray(FloatBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, float[]> pool = getOrCreatePool(floatCache, buffer);
		float[] result = pool.get(size);
		if (result == null) {
			result = new float[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static double[] getArray(DoubleBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, double[]> pool = getOrCreatePool(doubleCache, buffer);
		double[] result = pool.get(size);
		if (result == null) {
			result = new double[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	public static char[] getArray(CharBuffer buffer) {
		final int size = buffer.remaining();
		final Map<Integer, char[]> pool = getOrCreatePool(charCache, buffer);
		char[] result = pool.get(size);
		if (result == null) {
			result = new char[size];
			pool.put(size, result);
		}
		if (buffer.hasArray()) {
			System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), result, 0, size);
		} else {
			int pos = buffer.position();
			buffer.get(result, 0, size);
			buffer.position(pos);
		}
		return result;
	}

	private VMBufferConvert() {
	}

	public static boolean isStateSaved() {
		return bufferStatePool.saveAndRestore;
	}

	public static void saveState(boolean s) {
		bufferStatePool.saveAndRestore = s;
	}

	public static Address ofNNCacheAddress(Buffer buffer) {
		if (buffer == null) {
			return Address.fromInt(0);
		}
		final int size = buffer.remaining();
		if (buffer instanceof ByteBuffer) {
			final ByteBuffer arrayBuffer = (ByteBuffer) buffer;
			final byte[] result = new byte[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof ShortBuffer) {
			final ShortBuffer arrayBuffer = (ShortBuffer) buffer;
			final short[] result = new short[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof IntBuffer) {
			final IntBuffer arrayBuffer = (IntBuffer) buffer;
			final int[] result = new int[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof LongBuffer) {
			final LongBuffer arrayBuffer = (LongBuffer) buffer;
			final long[] result = new long[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof FloatBuffer) {
			final FloatBuffer arrayBuffer = (FloatBuffer) buffer;
			final float[] result = new float[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof DoubleBuffer) {
			final DoubleBuffer arrayBuffer = (DoubleBuffer) buffer;
			final double[] result = new double[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		} else if (buffer instanceof CharBuffer) {
			final CharBuffer arrayBuffer = (CharBuffer) buffer;
			final char[] result = new char[size];
			if (arrayBuffer.hasArray()) {
				System.arraycopy(arrayBuffer.array(), arrayBuffer.arrayOffset() + arrayBuffer.position(), result, 0,
						size);
			} else {
				int pos = arrayBuffer.position();
				arrayBuffer.get(result, 0, size);
				arrayBuffer.position(pos);
			}
			return Address.ofData(result);
		}
		throw new LSysException("Unsupported buffer type : " + buffer.getClass().getName());
	}

	public static Address ofNAddress(Buffer buffer) {
		if (buffer == null) {
			return Address.fromInt(0);
		}
		if (buffer instanceof ByteBuffer) {
			return Address.ofData(getArray((ByteBuffer) buffer));
		} else if (buffer instanceof ShortBuffer) {
			return Address.ofData(getArray((ShortBuffer) buffer));
		} else if (buffer instanceof IntBuffer) {
			return Address.ofData(getArray((IntBuffer) buffer));
		} else if (buffer instanceof LongBuffer) {
			return Address.ofData(getArray((LongBuffer) buffer));
		} else if (buffer instanceof FloatBuffer) {
			return Address.ofData(getArray((FloatBuffer) buffer));
		} else if (buffer instanceof DoubleBuffer) {
			return Address.ofData(getArray((DoubleBuffer) buffer));
		} else if (buffer instanceof CharBuffer) {
			return Address.ofData(getArray((CharBuffer) buffer));
		}
		throw new LSysException("Unsupported buffer type : " + buffer.getClass().getName());
	}

	public static Address save(Buffer buffer) {
		if (buffer == null) {
			return Address.fromInt(0);
		}
		if (buffer instanceof ByteBuffer) {
			return save((ByteBuffer) buffer);
		} else if (buffer instanceof ShortBuffer) {
			return save((ShortBuffer) buffer);
		} else if (buffer instanceof IntBuffer) {
			return save((IntBuffer) buffer);
		} else if (buffer instanceof LongBuffer) {
			return save((LongBuffer) buffer);
		} else if (buffer instanceof FloatBuffer) {
			return save((FloatBuffer) buffer);
		} else if (buffer instanceof DoubleBuffer) {
			return save((DoubleBuffer) buffer);
		} else if (buffer instanceof CharBuffer) {
			return save((CharBuffer) buffer);
		}
		throw new LSysException("Unsupported buffer type : " + buffer.getClass().getName());
	}

	public static void restore(Buffer buffer, Address address) {
		if (buffer instanceof ByteBuffer) {
			restore((ByteBuffer) buffer, address);
		} else if (buffer instanceof ShortBuffer) {
			restore((ShortBuffer) buffer, address);
		} else if (buffer instanceof IntBuffer) {
			restore((IntBuffer) buffer, address);
		} else if (buffer instanceof LongBuffer) {
			restore((LongBuffer) buffer, address);
		} else if (buffer instanceof FloatBuffer) {
			restore((FloatBuffer) buffer, address);
		} else if (buffer instanceof DoubleBuffer) {
			restore((DoubleBuffer) buffer, address);
		} else if (buffer instanceof CharBuffer) {
			restore((CharBuffer) buffer, address);
		} else {
			throw new LSysException("Unsupported buffer type : " + buffer.getClass().getName());
		}
	}

	public static Address save(ByteBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(ShortBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(IntBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(LongBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(FloatBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(DoubleBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static Address save(CharBuffer buffer) {
		bufferStatePool.save(buffer);
		return Address.ofData(getArray(buffer));
	}

	public static void restore(ByteBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getByte());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(ShortBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getShort());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(IntBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getInt());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(LongBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getLong());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(FloatBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getFloat());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(DoubleBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getDouble());
		}
		bufferStatePool.restore(buffer);
	}

	public static void restore(CharBuffer buffer, Address address) {
		buffer.rewind();
		final int size = MathUtils.min(buffer.remaining(), buffer.capacity());
		for (int a = 0; a < size; a++) {
			buffer.put(address.getChar());
		}
		bufferStatePool.restore(buffer);
	}

	public static void clearCache() {
		byteCache.clear();
		shortCache.clear();
		intCache.clear();
		longCache.clear();
		floatCache.clear();
		doubleCache.clear();
		charCache.clear();
	}

	public static void cleanupCache() {
		byteCache.cleanup();
		shortCache.cleanup();
		intCache.cleanup();
		longCache.cleanup();
		floatCache.cleanup();
		doubleCache.cleanup();
		charCache.cleanup();
	}

	public static void freeState(Buffer buffer) {
		bufferStatePool.freeState(buffer);
	}

	public static int getPoolCount() {
		return bufferStatePool.stateMap.size;
	}

	public static void restoreAll() {
		bufferStatePool.restoreAll();
	}

	public static void clearState() {
		bufferStatePool.clear();
	}
}
