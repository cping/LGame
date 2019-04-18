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
import java.nio.FloatBuffer;

import loon.LSysException;
import loon.LSystem;

public class VertexBufferObjectSubData implements VertexData {
	
	protected final VertexAttributes attributes;
	
	protected final FloatBuffer buffer;
	
	protected final ByteBuffer byteBuffer;
	
	protected int bufferHandle;
	
	protected final boolean isDirect;
	protected final boolean isStatic;
	
	protected final int usage;
	
	protected boolean isDirty = false;
	protected boolean isBound = false;

	public VertexBufferObjectSubData(boolean isStatic, int numVertices,
			VertexAttribute... attributes) {
		this.isStatic = isStatic;
		this.attributes = new VertexAttributes(attributes);
		byteBuffer = LSystem.base().support().newByteBuffer(this.attributes.vertexSize
				* numVertices);
		isDirect = true;
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
		buffer = byteBuffer.asFloatBuffer();
		bufferHandle = createBufferObject();
		buffer.flip();
		byteBuffer.flip();
	}

	private int createBufferObject() {
		GL20 gl = LSystem.base().graphics().gl;
		int result = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, result);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.capacity(), null,
				usage);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		return result;
	}

	@Override
	public VertexAttributes getAttributes() {
		return attributes;
	}

	@Override
	public int getNumVertices() {
		return buffer.limit() * 4 / attributes.vertexSize;
	}

	@Override
	public int getNumMaxVertices() {
		return byteBuffer.capacity() / attributes.vertexSize;
	}

	@Override
	public FloatBuffer getBuffer() {
		isDirty = true;
		return buffer;
	}

	private void bufferChanged() {
		if (isBound) {
			LSystem.base().graphics().gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0,
					byteBuffer.limit(), byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		isDirty = true;
		if (isDirect) {
			LSystem.base().support().copy(vertices, byteBuffer, offset, count);
			buffer.position(0);
			buffer.limit(count);
		} else {
			buffer.clear();
			buffer.put(vertices, offset, count);
			buffer.flip();
			byteBuffer.position(0);
			byteBuffer.limit(buffer.limit() << 2);
		}

		bufferChanged();
	}

	@Override
	public void updateVertices(int targetOffset, float[] vertices,
			int sourceOffset, int count) {
		isDirty = true;
		if (isDirect) {
			final int pos = byteBuffer.position();
			byteBuffer.position(targetOffset * 4);
			LSystem.base().support().copy(vertices, sourceOffset, byteBuffer, count);
			byteBuffer.position(pos);
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
			byteBuffer.limit(buffer.limit() * 4);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(),
					byteBuffer, usage);
			isDirty = false;
		}

		final int numAttributes = attributes.size();
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = shader
						.getAttributeLocation(attribute.alias);
				if (location < 0){
					continue;
				}
				shader.enableVertexAttribute(location);
				shader.setVertexAttribute(location, attribute.numComponents,
						attribute.type, attribute.normalized,
						attributes.vertexSize, attribute.offset);
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = locations[i];
				if (location < 0)
					continue;
				shader.enableVertexAttribute(location);

				shader.setVertexAttribute(location, attribute.numComponents,
						attribute.type, attribute.normalized,
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
	}
}
