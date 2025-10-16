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

import loon.Support;

public final class VertexArray extends BaseBufferSupport implements VertexData {

	VertexAttributes attributes;
	FloatBuffer floatBuffer;
	ByteBuffer byteBuffer;
	boolean isBound = false;

	public VertexArray(int numVertices, VertexAttribute... attributes) {
		this(numVertices, new VertexAttributes(attributes));
	}

	public VertexArray(int numVertices, VertexAttributes attributes) {
		this.attributes = attributes;
		byteBuffer = getSupport().newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
		floatBuffer = byteBuffer.asFloatBuffer();
		((Buffer) floatBuffer).flip();
		((Buffer) byteBuffer).flip();
	}

	@Override
	public FloatBuffer getBuffer(boolean dirty) {
		return floatBuffer;
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
	public void setVertices(float[] vertices, int offset, int count) {
		final Support support = getSupport();
		if (support.isNative()) {
			support.copy(vertices, byteBuffer, offset, count);
		} else {
			support.copy(vertices, floatBuffer, offset, count);
		}
		((Buffer) floatBuffer).position(0);
		((Buffer) floatBuffer).limit(count);
	}

	@Override
	public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
		final int pos = byteBuffer.position();
		((Buffer) byteBuffer).position(targetOffset * 4);
		getSupport().copy(vertices, byteBuffer, sourceOffset, count);
		((Buffer) byteBuffer).position(pos);
	}

	@Override
	public void bind(final ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(final ShaderProgram shader, final int[] locations) {
		final int numAttributes = attributes.size();
		((Buffer) byteBuffer).limit(floatBuffer.limit() * 4);
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = shader.getAttributeLocation(attribute.alias);
				if (location < 0) {
					continue;
				}
				shader.enableVertexAttribute(location);
				if (attribute.vertexType == GL20.GL_FLOAT) {
					((Buffer) floatBuffer).position(attribute.offset / 4);
					shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType,
							attribute.normalized, attributes.vertexSize, floatBuffer);
				} else {
					((Buffer) byteBuffer).position(attribute.offset);
					shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType,
							attribute.normalized, attributes.vertexSize, byteBuffer);
				}
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = locations[i];
				if (location < 0) {
					continue;
				}
				shader.enableVertexAttribute(location);
				if (attribute.vertexType == GL20.GL_FLOAT) {
					((Buffer) floatBuffer).position(attribute.offset / 4);
					shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType,
							attribute.normalized, attributes.vertexSize, floatBuffer);
				} else {
					((Buffer) byteBuffer).position(attribute.offset);
					shader.setVertexAttribute(location, attribute.numComponents, attribute.vertexType,
							attribute.normalized, attributes.vertexSize, byteBuffer);
				}
			}
		}
		isBound = true;
	}

	@Override
	public void unbind(ShaderProgram shader) {
		unbind(shader, null);
	}

	@Override
	public void unbind(ShaderProgram shader, int[] locations) {
		final int numAttributes = attributes.size();
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				shader.disableVertexAttribute(attributes.get(i).alias);
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final int location = locations[i];
				if (location >= 0) {
					shader.disableVertexAttribute(location);
				}
			}
		}
		isBound = false;
	}

	@Override
	public VertexAttributes getAttributes() {
		return attributes;
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void close() {
		getSupport().disposeUnsafeByteBuffer(byteBuffer);
		byteBuffer = null;
		floatBuffer = null;
	}

}
