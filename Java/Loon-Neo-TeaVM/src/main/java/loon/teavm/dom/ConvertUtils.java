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
package loon.teavm.dom;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.PlatformDetector;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.TypedArray;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.jso.typedarrays.Uint8Array;

public class ConvertUtils {

	public static String decodeUtf8(ArrayBuffer arrayBufer) {
		String result = "";
		int i = 0;
		int c1 = 0;

		int c2 = 0;
		int c3 = 0;

		Uint8Array data = new Uint8Array(arrayBufer);

		if (data.getLength() >= 3 && data.get(0) == 0xef && data.get(1) == 0xbb && data.get(2) == 0xbf) {
			i = 3;
		}

		while (i < data.getLength()) {
			c1 = data.get(i);

			if (c1 < 128) {
				result += JSString.fromCharCode(c1);
				i++;
			} else if (c1 > 191 && c1 < 224) {
				if (i + 1 >= data.getLength()) {
					throw new RuntimeException("UTF-8 Decode failed. Two byte character was truncated.");
				}
				c2 = data.get(i + 1);
				result += JSString.fromCharCode(((c1 & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				if (i + 2 >= data.getLength()) {
					throw new RuntimeException("UTF-8 Decode failed. Multi byte character was truncated.");
				}
				c2 = data.get(i + 1);
				c3 = data.get(i + 2);
				result += JSString.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
		}
		return result;
	}

	public static int getElementSize(Buffer buffer) {
		if (buffer instanceof ByteBuffer) {
			return 1;
		} else if (buffer instanceof CharBuffer) {
			return 2;
		} else if (buffer instanceof ShortBuffer) {
			return 2;
		} else if (buffer instanceof IntBuffer) {
			return 4;
		} else if (buffer instanceof FloatBuffer) {
			return 4;
		} else if (buffer instanceof LongBuffer) {
			return 8;
		} else if (buffer instanceof DoubleBuffer) {
			return 8;
		}
		return 1;
	}

	public static Float32Array copy(FloatBuffer buffer) {
		return getFloat32Array(buffer);
	}

	public static Int16Array copy(ShortBuffer buffer) {
		return getInt16Array(buffer);
	}

	public static Int32Array copy(IntBuffer buffer) {
		return getInt32Array(buffer);
	}

	public static Float32Array toFloat32Array(JSObject o) {
		if (o == null) {
			return new Float32Array(0);
		}
		if (o instanceof ArrayBufferView) {
			ArrayBufferView buffer = ((ArrayBufferView) o);
			return new Float32Array(buffer.getBuffer());
		} else if (o instanceof ArrayBuffer) {
			return new Float32Array((ArrayBuffer) o);
		}
		return (Float32Array) o;
	}

	public static Int32Array toInt32Array(JSObject o) {
		if (o == null) {
			return new Int32Array(0);
		}
		if (o instanceof ArrayBufferView) {
			ArrayBufferView buffer = ((ArrayBufferView) o);
			return new Int32Array(buffer.getBuffer());
		} else if (o instanceof ArrayBuffer) {
			return new Int32Array((ArrayBuffer) o);
		}
		return (Int32Array) o;
	}

	public static int toNumber(JSObject o) {
		if (o == null) {
			return -1;
		}
		if (o instanceof JSNumber) {
			return ((JSNumber) o).intValue();
		} else if (o instanceof JSBoolean) {
			return ((JSBoolean) o).booleanValue() ? 1 : 0;
		} else if (JSString.isInstance(o)) {
			return Integer.parseInt(((JSString) o).stringValue());
		} else {
			return o.hashCode();
		}
	}

	public static byte[] toByteArray(TypedArray array) {
		Int8Array intArray = new Int8Array(array);
		return intArray.copyToJavaArray();
	}

	public static ArrayBufferView getTypedArray(Buffer buffer) {
		return getTypedArray(false, buffer);
	}

	public static ArrayBufferView getTypedArray(boolean isUnsigned, Buffer buffer) {
		if (buffer instanceof ByteBuffer) {
			if (isUnsigned) {
				return getUint8Array(buffer);
			} else {
				return getInt8Array(buffer);
			}
		} else if (buffer instanceof ShortBuffer) {
			if (isUnsigned) {
				return getUint16Array(buffer);
			} else {
				return getInt16Array(buffer);
			}
		} else if (buffer instanceof IntBuffer) {
			return getInt32Array(buffer);
		} else if (buffer instanceof FloatBuffer) {
			return getFloat32Array(buffer);
		}
		throw new RuntimeException("No support for buffer " + buffer.getClass());
	}

	public static Int8Array getInt8Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Int8Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				if (buffer.hasArray()) {
					return Int8Array.copyFromJavaArray(buffer.array());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new byte[capacity];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					return Int8Array.copyFromJavaArray(array);
				}
			} else {
				ArrayBufferView typedArray = getTypedArray(false, buff);
				return new Int8Array(typedArray.getBuffer());
			}
		}
	}

	public static Uint8Array getUint8Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Uint8Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				if (buffer.hasArray()) {
					var typedArray = Int8Array.copyFromJavaArray(buffer.array());
					return new Uint8Array(typedArray.getBuffer());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new byte[capacity];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					var typedArray = Int8Array.copyFromJavaArray(array);
					return new Uint8Array(typedArray.getBuffer());
				}
			} else {
				ArrayBufferView typedArray = getTypedArray(true, buff);
				return new Uint8Array(typedArray.getBuffer());
			}
		}
	}

	public static Int16Array getInt16Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Int16Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof ShortBuffer) {
				ShortBuffer buffer = (ShortBuffer) buff;
				if (buffer.hasArray()) {
					return Int16Array.copyFromJavaArray(buffer.array());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new short[buffer.capacity()];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					return Int16Array.copyFromJavaArray(array);
				}
			} else if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				Int8Array array = getInt8Array(buffer);
				return new Int16Array(array);
			} else {
				throw new RuntimeException(
						"getInt16Array - Unsupported buffer type " + buff.getClass().getSimpleName());
			}
		}
	}

	public static Uint16Array getUint16Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Uint16Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof ShortBuffer) {
				ShortBuffer buffer = (ShortBuffer) buff;
				if (buffer.hasArray()) {
					var typedArray = Int16Array.copyFromJavaArray(buffer.array());
					return new Uint16Array(typedArray.getBuffer());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new short[buffer.capacity()];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					var typedArray = Int16Array.copyFromJavaArray(array);
					return new Uint16Array(typedArray.getBuffer());
				}
			} else if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				Uint8Array array = getUint8Array(buffer);
				return new Uint16Array(array.getBuffer());
			} else {
				throw new RuntimeException(
						"getUint16Array - Unsupported buffer type " + buff.getClass().getSimpleName());
			}
		}
	}

	public static Int32Array getInt32Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Int32Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof IntBuffer) {
				IntBuffer buffer = (IntBuffer) buff;
				if (buffer.hasArray()) {
					return Int32Array.copyFromJavaArray(buffer.array());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new int[buffer.capacity()];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					return Int32Array.copyFromJavaArray(array);
				}
			} else if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				Int8Array array = getInt8Array(buffer);
				return new Int32Array(array);
			} else {
				throw new RuntimeException(
						"getInt32Array - Unsupported buffer type " + buff.getClass().getSimpleName());
			}
		}
	}

	public static Float32Array getFloat32Array(Buffer buff) {
		if (PlatformDetector.isJavaScript() || buff.isDirect()) {
			return Float32Array.fromJavaBuffer(buff);
		} else {
			if (buff instanceof FloatBuffer) {
				FloatBuffer buffer = (FloatBuffer) buff;
				if (buffer.hasArray()) {
					return Float32Array.copyFromJavaArray(buffer.array());
				} else {
					int position = buffer.position();
					int limit = buffer.limit();
					int capacity = buffer.capacity();
					buffer.position(0);
					buffer.limit(capacity);
					var array = new float[buffer.capacity()];
					buffer.get(array);
					buffer.position(position);
					buffer.limit(limit);
					return Float32Array.copyFromJavaArray(array);
				}
			} else if (buff instanceof ByteBuffer) {
				ByteBuffer buffer = (ByteBuffer) buff;
				Int8Array array = getInt8Array(buffer);
				return new Float32Array(array);
			} else {
				throw new RuntimeException(
						"getFloat32Array - Unsupported buffer type " + buff.getClass().getSimpleName());
			}
		}
	}

	public static Int8Array getInt8Array(byte[] buffer) {
		return Int8Array.copyFromJavaArray(buffer);
	}

	public static void copy(Int8Array in, ByteBuffer out) {
		if (PlatformDetector.isJavaScript() || out.isDirect()) {
			Int8Array array = Int8Array.fromJavaBuffer(out);
			array.set(in);
		} else {
			var data = in.copyToJavaArray();
			out.put(data);
		}
	}
}
