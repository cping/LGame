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
package loon.cport.builder;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import loon.cport.bridge.NativeSupport;

public class StreamUtils {

	private final static NativeSupport support = new NativeSupport();

	public static final int DEFAULT_BUFFER_SIZE = 4096;
	public static final byte[] EMPTY_BYTES = new byte[0];

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		copyStream(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
		copyStream(input, output, new byte[bufferSize]);
	}

	public static void copyStream(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	public static void copyStream(InputStream input, ByteBuffer output) throws IOException {
		copyStream(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	public static void copyStream(InputStream input, ByteBuffer output, int bufferSize) throws IOException {
		copyStream(input, output, new byte[bufferSize]);
	}

	public static int copyStream(InputStream input, ByteBuffer output, byte[] buffer) throws IOException {
		int startPosition = output.position(), total = 0, bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			support.copy(buffer, 0, output, bytesRead);
			total += bytesRead;
			((Buffer) output).position(startPosition + total);
		}
		((Buffer) output).position(startPosition);
		return total;
	}

	public static byte[] copyStreamToByteArray(InputStream input) throws IOException {
		return copyStreamToByteArray(input, input.available());
	}

	public static byte[] copyStreamToByteArray(InputStream input, int estimatedSize) throws IOException {
		ByteArrayOutputStream baos = new OptimizedByteArrayOutputStream(Math.max(0, estimatedSize));
		copyStream(input, baos);
		return baos.toByteArray();
	}

	public static String copyStreamToString(InputStream input) throws IOException {
		return copyStreamToString(input, input.available(), null);
	}

	public static String copyStreamToString(InputStream input, int estimatedSize) throws IOException {
		return copyStreamToString(input, estimatedSize, null);
	}

	public static String copyStreamToString(InputStream input, int estimatedSize, String charset) throws IOException {
		InputStreamReader reader = charset == null ? new InputStreamReader(input)
				: new InputStreamReader(input, charset);
		StringWriter writer = new StringWriter(Math.max(0, estimatedSize));
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, charsRead);
		}
		return writer.toString();
	}

	public static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Throwable ignored) {
			}
		}
	}

	static public class OptimizedByteArrayOutputStream extends ByteArrayOutputStream {
		public OptimizedByteArrayOutputStream(int initialSize) {
			super(initialSize);
		}

		@Override
		public synchronized byte[] toByteArray() {
			if (count == buf.length)
				return buf;
			return super.toByteArray();
		}

		public byte[] getBuffer() {
			return buf;
		}
	}
}
