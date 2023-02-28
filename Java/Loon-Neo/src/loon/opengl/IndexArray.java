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
package loon.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.LSystem;

public class IndexArray implements IndexData {

	private static IntBuffer tmpHandle;

	private ShortBuffer buffer;
	private ByteBuffer byteBuffer;

	private final boolean empty;

	public IndexArray(int maxIndices) {
		if (tmpHandle == null) {
			tmpHandle = LSystem.base().support().newIntBuffer(1);
		}
		empty = maxIndices == 0;
		if (empty) {
			maxIndices = 1;
		}
		byteBuffer = LSystem.base().support().newUnsafeByteBuffer(maxIndices * 2);
		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
	}

	@Override
	public int getNumIndices() {
		return empty ? 0 : buffer.limit();
	}

	@Override
	public int getNumMaxIndices() {
		return empty ? 0 : buffer.capacity();
	}

	@Override
	public void setIndices(short[] indices, int offset, int count) {
		buffer.clear();
		buffer.put(indices, offset, count);
		buffer.flip();
		byteBuffer.position(0);
		byteBuffer.limit(count << 1);
	}

	@Override
	public void setIndices(ShortBuffer indices) {
		int pos = indices.position();
		buffer.clear();
		buffer.limit(indices.remaining());
		buffer.put(indices);
		buffer.flip();
		indices.position(pos);
		byteBuffer.position(0);
		byteBuffer.limit(buffer.limit() << 1);
	}

	@Override
	public ShortBuffer getBuffer() {
		return buffer;
	}

	@Override
	public void bind() {
	}

	@Override
	public void unbind() {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void close() {
		LSystem.base().support().disposeUnsafeByteBuffer(byteBuffer);
	}
}
