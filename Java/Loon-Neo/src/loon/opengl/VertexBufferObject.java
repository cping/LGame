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

public final class VertexBufferObject extends BaseBufferSupport implements VertexData {

	private VertexAttributes attributes;
	private FloatBuffer floatBuffer;
	private ByteBuffer byteBuffer;
	private boolean ownsBuffer;
	private int bufferHandle;
	private int usage;
	boolean isDirty = false;
	boolean isBound = false;

	public VertexBufferObject(boolean isStatic, int numVertices, VertexAttribute... attributes) {
		this(isStatic, numVertices, new VertexAttributes(attributes));
	}

	public VertexBufferObject(boolean isStatic, int numVertices, VertexAttributes attributes) {
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		ByteBuffer data = getSupport().newUnsafeByteBuffer(attributes.vertexSize * numVertices);
		((Buffer) data).limit(0);
		setBuffer(data, true, attributes);
		setUsage(isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW);
	}

	protected VertexBufferObject(int usage, ByteBuffer data, boolean ownsBuffer, VertexAttributes attributes) {
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		setBuffer(data, ownsBuffer, attributes);
		setUsage(usage);
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

	protected void setBuffer(Buffer data, boolean ownsBuffer, VertexAttributes value) {
		if (isBound) {
			throw new LSysException("Cannot change attributes while VBO is bound");
		}
		if (this.ownsBuffer && byteBuffer != null) {
			getSupport().disposeUnsafeByteBuffer(byteBuffer);
		}
		attributes = value;
		if (data instanceof ByteBuffer) {
			byteBuffer = (ByteBuffer) data;
		} else {
			throw new LSysException("Only ByteBuffer is currently supported");
		}
		this.ownsBuffer = ownsBuffer;
		final int l = byteBuffer.limit();
		byteBuffer.limit(byteBuffer.capacity());
		floatBuffer = byteBuffer.asFloatBuffer();
		byteBuffer.limit(l);
		floatBuffer.limit(l / 4);
	}

	private void bufferChanged() {
		if (isBound) {
			LSystem.base().graphics().gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
			isDirty = false;
		}
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		isDirty = true;
		if (getSupport().isNative()) {
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
		final int pos = byteBuffer.position();
		((Buffer) byteBuffer).position(targetOffset * 4);
		getSupport().copy(vertices, byteBuffer, sourceOffset, count);
		((Buffer) byteBuffer).position(pos);
		((Buffer) floatBuffer).position(0);
		bufferChanged();
	}

	protected int getUsage() {
		return usage;
	}

	protected void setUsage(int value) {
		if (isBound) {
			throw new LSysException("Cannot change usage while VBO is bound");
		}
		usage = value;
	}

	@Override
	public void bind(ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(ShaderProgram shader, int[] locations) {
		final GL20 gl = LSystem.base().graphics().gl;

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
		if (isDirty) {
			byteBuffer.limit(floatBuffer.limit() * 4);
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

	@Override
	public void invalidate() {
		bufferHandle = LSystem.base().graphics().gl.glGenBuffer();
		isDirty = true;
	}

	@Override
	public void close() {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
		if (ownsBuffer) {
			getSupport().disposeUnsafeByteBuffer(byteBuffer);
		}
		floatBuffer = null;
		byteBuffer = null;
	}
}
