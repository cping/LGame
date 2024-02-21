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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import loon.LSysException;
import loon.LSystem;

public class IndexBufferObjectSubData implements IndexData {

	protected ShortBuffer buffer;
	protected ByteBuffer byteBuffer;

	protected int bufferHandle;
	protected final boolean isDirect;

	protected boolean isDirty = true;
	protected boolean isBound = false;

	protected final int usage;

	public IndexBufferObjectSubData(boolean isStatic, int maxIndices) {
		byteBuffer = LSystem.base().support().newByteBuffer(maxIndices * 2);
		isDirect = true;
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
		buffer = byteBuffer.asShortBuffer();
		((Buffer) buffer).flip();
		((Buffer) byteBuffer).flip();
		bufferHandle = createBufferObject();
	}

	public IndexBufferObjectSubData(int maxIndices) {
		byteBuffer = LSystem.base().support().newByteBuffer(maxIndices * 2);
		this.isDirect = true;
		usage = GL20.GL_STATIC_DRAW;
		buffer = byteBuffer.asShortBuffer();
		((Buffer) buffer).flip();
		((Buffer) byteBuffer).flip();
		bufferHandle = createBufferObject();
	}

	private int createBufferObject() {
		GL20 gl = LSystem.base().graphics().gl;
		int result = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, result);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.capacity(), null, usage);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		return result;
	}

	@Override
	public int getNumIndices() {
		return buffer.limit();
	}

	@Override
	public int getNumMaxIndices() {
		return buffer.capacity();
	}

	@Override
	public void setIndices(short[] indices, int offset, int count) {
		isDirty = true;
		((Buffer) buffer).clear();
		buffer.put(indices, offset, count);
		((Buffer) buffer).flip();
		((Buffer) byteBuffer).position(0);
		((Buffer) byteBuffer).limit(count << 1);
		if (isBound) {
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, byteBuffer.limit(),
					byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public void setIndices(ShortBuffer indices) {
		int pos = indices.position();
		isDirty = true;
		((Buffer) buffer).clear();
		buffer.put(indices);
		((Buffer) buffer).flip();
		((Buffer) indices).position(pos);
		((Buffer) byteBuffer).position(0);
		((Buffer) byteBuffer).limit(buffer.limit() << 1);
		if (isBound) {
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, byteBuffer.limit(),
					byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public void updateIndices(int targetOffset, short[] indices, int offset, int count) {
		isDirty = true;
		final int pos = byteBuffer.position();
		((Buffer) byteBuffer).position(targetOffset * 2);
		LSystem.base().support().copy(indices, offset, byteBuffer, count);
		((Buffer) byteBuffer).position(pos);
		((Buffer) buffer).position(0);
		if (isBound) {
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, byteBuffer.limit(),
					byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public ShortBuffer getBuffer(boolean dirty) {
		isDirty |= dirty;
		return buffer;
	}

	@Override
	public void bind() {
		if (bufferHandle == 0) {
			throw new LSysException("bufferHandle == 0");
		}
		LSystem.base().graphics().gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
		if (isDirty) {
			((Buffer) byteBuffer).limit(buffer.limit() * 2);
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, byteBuffer.limit(),
					byteBuffer);
			isDirty = false;
		}
		isBound = true;
	}

	@Override
	public void unbind() {
		LSystem.base().graphics().gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		isBound = false;
	}

	@Override
	public void invalidate() {
		bufferHandle = createBufferObject();
		isDirty = true;
	}

	@Override
	public void close() {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
	}

}
