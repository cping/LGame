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
import java.nio.ShortBuffer;

import loon.LSystem;

public class IndexBufferObject implements IndexData {
	ShortBuffer buffer;
	ByteBuffer byteBuffer;
	int bufferHandle;
	final boolean isDirect;
	boolean isDirty = true;
	boolean isBound = false;
	final int usage;

	public IndexBufferObject(boolean isStatic, int maxIndices) {
		byteBuffer = LSystem.base().support()
				.newUnsafeByteBuffer(maxIndices * 2);
		isDirect = true;

		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
	}

	public IndexBufferObject(int maxIndices) {
		byteBuffer = LSystem.base().support()
				.newUnsafeByteBuffer(maxIndices * 2);
		this.isDirect = true;

		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		usage = GL20.GL_STATIC_DRAW;
	}

	public int getNumIndices() {
		return buffer.limit();
	}

	public int getNumMaxIndices() {
		return buffer.capacity();
	}

	public void setIndices(short[] indices, int offset, int count) {
		isDirty = true;
		buffer.clear();
		buffer.put(indices, offset, count);
		buffer.flip();
		byteBuffer.position(0);
		byteBuffer.limit(count << 1);

		if (isBound) {
			LSystem.base().graphics().gl.glBufferData(
					GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(),
					byteBuffer, usage);
			isDirty = false;
		}
	}

	public void setIndices(ShortBuffer indices) {
		isDirty = true;
		int pos = indices.position();
		buffer.clear();
		buffer.put(indices);
		buffer.flip();
		indices.position(pos);
		byteBuffer.position(0);
		byteBuffer.limit(buffer.limit() << 1);

		if (isBound) {
			LSystem.base().graphics().gl.glBufferData(
					GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(),
					byteBuffer, usage);
			isDirty = false;
		}
	}

	public ShortBuffer getBuffer() {
		isDirty = true;
		return buffer;
	}

	public void bind() {
		if (bufferHandle == 0) {
			throw new RuntimeException("No buffer allocated!");
		}
		LSystem.base().graphics().gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER,
				bufferHandle);
		if (isDirty) {
			byteBuffer.limit(buffer.limit() * 2);
			LSystem.base().graphics().gl.glBufferData(
					GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(),
					byteBuffer, usage);
			isDirty = false;
		}
		isBound = true;
	}

	public void unbind() {
		LSystem.base().graphics().gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER,
				0);
		isBound = false;
	}

	public void invalidate() {
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		isDirty = true;
	}

	public void close() {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
		LSystem.base().support().disposeUnsafeByteBuffer(byteBuffer);
	}
}
