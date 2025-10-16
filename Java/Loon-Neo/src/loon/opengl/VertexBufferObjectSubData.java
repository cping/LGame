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
import java.nio.FloatBuffer;

import loon.LSysException;
import loon.LSystem;

public class VertexBufferObjectSubData extends BaseBufferSupport implements VertexData {

	protected final VertexAttributes attributes;

	protected FloatBuffer floatBuffer;

	protected ByteBuffer byteBuffer;

	protected int bufferHandle;

	protected final boolean isDirect;
	protected final boolean isStatic;

	protected final int usage;

	protected boolean isDirty = false;
	protected boolean isBound = false;

	public VertexBufferObjectSubData(boolean isStatic, int numVertices, VertexAttribute... attributes) {
		this.isStatic = isStatic;
		this.attributes = new VertexAttributes(attributes);
		byteBuffer = getSupport().newByteBuffer(this.attributes.vertexSize * numVertices);
		isDirect = true;
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
		floatBuffer = byteBuffer.asFloatBuffer();
		bufferHandle = createBufferObject();
		((Buffer) floatBuffer).flip();
		((Buffer) byteBuffer).flip();
	}

	private int createBufferObject() {
		GL20 gl = LSystem.base().graphics().gl;
		int result = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, result);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.capacity(), null, usage);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		return result;
	}

	@Override
	public VertexAttributes getAttributes() {
		return attributes;
	}

	@Override
	public int getNumVertices() {
		return floatBuffer.limit() * 4 / attributes.vertexSize;
	}

	@Override
	public int getNumMaxVertices() {
		return byteBuffer.capacity() / attributes.vertexSize;
	}

	@Override
	public FloatBuffer getBuffer(boolean dirty) {
		isDirty |= dirty;
		return floatBuffer;
	}

	private void bufferChanged() {
		if (isBound) {
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, byteBuffer.limit(), byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		isDirty = true;
		if (isDirect) {
			getSupport().copy(vertices, byteBuffer, offset, count);
			((Buffer) floatBuffer).position(0);
			((Buffer) floatBuffer).limit(count);
		} else {
			((Buffer) floatBuffer).clear();
			floatBuffer.put(vertices, offset, count);
			((Buffer) floatBuffer).flip();
			((Buffer) byteBuffer).position(0);
			((Buffer) byteBuffer).limit(floatBuffer.limit() << 2);
		}
		bufferChanged();
	}

	@Override
	public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
		isDirty = true;
		if (isDirect) {
			final int pos = byteBuffer.position();
			((Buffer) byteBuffer).position(targetOffset * 4);
			getSupport().copy(vertices, sourceOffset, byteBuffer, count);
			((Buffer) byteBuffer).position(pos);
		} else {
			throw new LSysException("Buffer must be allocated direct.");
		}
		bufferChanged();
	}

	@Override
	public void bind(final ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(final ShaderProgram shader, final int[] locations) {
		final GL20 gl = LSystem.base().graphics().gl;

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
		if (isDirty) {
			((Buffer) byteBuffer).limit(floatBuffer.limit() * 4);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
			isDirty = false;
		}

		final int numAttributes = attributes.size();
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = shader.getAttributeLocation(attribute.alias);
				if (location < 0) {
					continue;
				}
				shader.enableVertexAttribute(location);
				shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType, attribute.normalized,
						attributes.vertexSize, attribute.offset);
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = locations[i];
				if (location < 0) {
					continue;
				}
				shader.enableVertexAttribute(location);
				shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType, attribute.normalized,
						attributes.vertexSize, attribute.offset);
			}
		}
		isBound = true;
	}

	@Override
	public void unbind(final ShaderProgram shader) {
		unbind(shader, null);
	}

	@Override
	public void unbind(final ShaderProgram shader, final int[] locations) {
		final GL20 gl = LSystem.base().graphics().gl;
		final int numAttributes = attributes.size();
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				shader.disableVertexAttribute(attributes.get(i).alias);
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final int location = locations[i];
				if (location >= 0)
					shader.disableVertexAttribute(location);
			}
		}
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		isBound = false;
	}

	public void invalidate() {
		bufferHandle = createBufferObject();
		isDirty = true;
	}

	public int getBufferHandle() {
		return bufferHandle;
	}

	@Override
	public void close() {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
		floatBuffer = null;
		byteBuffer = null;
	}
}
