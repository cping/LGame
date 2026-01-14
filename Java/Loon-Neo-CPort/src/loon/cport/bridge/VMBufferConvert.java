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

import org.teavm.interop.Address;

import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;

public final class VMBufferConvert {

	static class TeaVMBufferState {

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

	static class TeaVMBufferStatePool {

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

	private VMBufferConvert() {
	}

	public static boolean isStateSaved() {
		return bufferStatePool.saveAndRestore;
	}

	public void saveState(boolean s) {
		bufferStatePool.saveAndRestore = s;
	}

	public static Address save(Buffer buffer) {
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
		final byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(ShortBuffer buffer) {
		bufferStatePool.save(buffer);
		final short[] result = new short[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(IntBuffer buffer) {
		bufferStatePool.save(buffer);
		final int[] result = new int[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(LongBuffer buffer) {
		bufferStatePool.save(buffer);
		final long[] result = new long[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(FloatBuffer buffer) {
		bufferStatePool.save(buffer);
		final float[] result = new float[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(DoubleBuffer buffer) {
		bufferStatePool.save(buffer);
		final double[] result = new double[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
	}

	public static Address save(CharBuffer buffer) {
		bufferStatePool.save(buffer);
		final char[] result = new char[buffer.remaining()];
		buffer.get(result);
		return Address.ofData(result);
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
