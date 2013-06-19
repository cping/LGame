/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import loon.core.LRelease;
import loon.core.graphics.opengl.GLAttributes.Usage;
import loon.core.graphics.opengl.GLAttributes.VertexAttribute;
import loon.jni.NativeSupport;


public class GLMesh {

	public static class IndexArray implements LRelease {

		private ShortBuffer buffer;

		private ByteBuffer byteBuffer;

		public IndexArray(int maxIndices) {
			byteBuffer = NativeSupport.newByteBuffer(maxIndices * 2);
			buffer = byteBuffer.asShortBuffer();
			buffer.flip();
			byteBuffer.flip();
		}

		public int getNumIndices() {
			return buffer.limit();
		}

		public int getNumMaxIndices() {
			return buffer.capacity();
		}

		public void setIndices(short[] indices, int offset, int count) {
			buffer.clear();
			buffer.put(indices, offset, count);
			buffer.flip();
			byteBuffer.position(0);
			byteBuffer.limit(count << 1);
		}

		public ShortBuffer getBuffer() {
			return buffer;
		}

		public void bind() {
		}

		public void unbind() {
		}

		@Override
		public void dispose() {
			if (byteBuffer != null) {
				NativeSupport.freeMemory(byteBuffer);
				byteBuffer = null;
			}
		}
	}

	public static class VertexArray implements LRelease {

		final GLAttributes attributes;
		final FloatBuffer buffer;
		final ByteBuffer byteBuffer;
		boolean isBound = false;

		public VertexArray(int numVertices, VertexAttribute... attributes) {
			this(numVertices, new GLAttributes(attributes));
		}

		public VertexArray(int numVertices, GLAttributes attributes) {
			this.attributes = attributes;
			byteBuffer = NativeSupport.newByteBuffer(this.attributes.vertexSize
					* numVertices);
			buffer = byteBuffer.asFloatBuffer();
			buffer.flip();
			byteBuffer.flip();
		}

		@Override
		public void dispose() {
			if (byteBuffer != null) {
				NativeSupport.freeMemory(byteBuffer);
			}
		}

		public FloatBuffer getBuffer() {
			return buffer;
		}

		public int getNumVertices() {
			return buffer.limit() * 4 / attributes.vertexSize;
		}

		public int getNumMaxVertices() {
			return byteBuffer.capacity() / attributes.vertexSize;
		}

		public GLAttributes getAttributes() {
			return attributes;
		}

		public void setVertices(float[] vertices, int offset, int count) {
			NativeSupport.copy(vertices, buffer, offset, count);
			buffer.position(0);
			buffer.limit(count);
		}

		public void bind() {
			GL10 gl = GLEx.gl10;
			int textureUnit = 0;
			int numAttributes = attributes.size();
			byteBuffer.limit(buffer.limit() * 4);
			for (int i = 0; i < numAttributes; i++) {
				VertexAttribute attribute = attributes.get(i);

				switch (attribute.usage) {
				case Usage.Position:
					byteBuffer.position(attribute.offset);
					gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
					gl.glVertexPointer(attribute.numComponents, GL.GL_FLOAT,
							attributes.vertexSize, byteBuffer);
					break;

				case Usage.Color:
				case Usage.ColorPacked:
					int colorType = GL.GL_FLOAT;
					if (attribute.usage == Usage.ColorPacked) {
						colorType = GL.GL_UNSIGNED_BYTE;
					}
					byteBuffer.position(attribute.offset);
					gl.glEnableClientState(GL.GL_COLOR_ARRAY);
					gl.glColorPointer(attribute.numComponents, colorType,
							attributes.vertexSize, byteBuffer);
					break;

				case Usage.Normal:
					byteBuffer.position(attribute.offset);
					gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
					gl.glNormalPointer(GL.GL_FLOAT, attributes.vertexSize,
							byteBuffer);
					break;

				case Usage.TextureCoordinates:
					gl.glClientActiveTexture(GL.GL_TEXTURE0 + textureUnit);
					gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
					byteBuffer.position(attribute.offset);
					gl.glTexCoordPointer(attribute.numComponents,
							GL.GL_FLOAT, attributes.vertexSize, byteBuffer);
					textureUnit++;
					break;

				default:

				}
			}
			isBound = true;
		}

		public void unbind() {
			GL10 gl = GLEx.gl10;
			int textureUnit = 0;
			int numAttributes = attributes.size();
			for (int i = 0; i < numAttributes; i++) {
				VertexAttribute attribute = attributes.get(i);
				switch (attribute.usage) {
				case Usage.Position:
					break;
				case Usage.Color:
				case Usage.ColorPacked:
					gl.glDisableClientState(GL.GL_COLOR_ARRAY);
					break;
				case Usage.Normal:
					gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
					break;
				case Usage.TextureCoordinates:
					gl.glClientActiveTexture(GL.GL_TEXTURE0 + textureUnit);
					gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
					textureUnit++;
					break;
				default:
				}
			}
			byteBuffer.position(0);
			isBound = false;
		}

	}

	public enum VertexDataType {
		VertexArray, VertexBufferObject, VertexBufferObjectSubData,
	}

	final VertexArray vertices;
	final IndexArray indices;
	boolean autoBind = true;

	final boolean isVertexArray;

	public GLMesh(boolean isStatic, int maxVertices, int maxIndices,
			GLAttributes attributes) {
		vertices = new VertexArray(maxVertices, attributes);
		indices = new IndexArray(maxIndices);
		isVertexArray = true;
	}

	public GLMesh(boolean isStatic, int maxVertices, int maxIndices,
			VertexAttribute... attributes) {
		vertices = new VertexArray(maxVertices, attributes);
		indices = new IndexArray(maxIndices);
		isVertexArray = true;
	}

	public GLMesh(VertexDataType type, boolean isStatic, int maxVertices,
			int maxIndices, VertexAttribute... attributes) {
		vertices = new VertexArray(maxVertices, attributes);
		indices = new IndexArray(maxIndices);
		isVertexArray = true;
	}

	public void setVertices(float[] vertices) {
		this.vertices.setVertices(vertices, 0, vertices.length);
	}

	public void setVertices(float[] vertices, int offset, int count) {
		this.vertices.setVertices(vertices, offset, count);
	}

	public void getVertices (float[] vertices) {
		getVertices(0, -1, vertices);
	}
	
	public void getVertices (int srcOffset, float[] vertices) {
		getVertices(srcOffset, -1, vertices);
	}

	public void getVertices (int srcOffset, int count, float[] vertices) {
		getVertices(srcOffset, count, vertices, 0);
	}
	
	public void getVertices (int srcOffset, int count, float[] vertices, int destOffset) {
		final int max = getNumVertices() * getVertexSize() / 4;
		if (count == -1) {
			count = max - srcOffset;
			if (count > vertices.length - destOffset){
				count = vertices.length - destOffset;
			}
		}
		if (srcOffset < 0 || count <= 0 || (srcOffset + count) > max || destOffset < 0 || destOffset >= vertices.length){
			throw new IndexOutOfBoundsException();
		}
		if ((vertices.length - destOffset) < count){
			throw new IllegalArgumentException("not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
		}
		int pos = getVerticesBuffer().position();
		getVerticesBuffer().position(srcOffset);
		getVerticesBuffer().get(vertices, destOffset, count);
		getVerticesBuffer().position(pos);
	}

	public void setIndices(short[] indices) {
		this.indices.setIndices(indices, 0, indices.length);
	}

	public void setIndices(short[] indices, int offset, int count) {
		this.indices.setIndices(indices, offset, count);
	}

	public void getIndices(short[] buffer) {
		if (buffer.length < getNumIndices()) {
			throw new IllegalArgumentException(
					"not enough room in indices array, has " + buffer.length
							+ " floats, needs " + getNumIndices());
		}
		ShortBuffer result = indices.getBuffer();
		int pos = result.position();
		result.position(0);
		result.get(buffer, 0, getNumIndices());
		result.position(pos);
	}

	public int getNumIndices() {
		return indices.getNumIndices();
	}

	public int getNumVertices() {
		return vertices.getNumVertices();
	}

	public int getMaxVertices() {
		return vertices.getNumMaxVertices();
	}

	public int getMaxIndices() {
		return indices.getNumMaxIndices();
	}

	public int getVertexSize() {
		return vertices.getAttributes().vertexSize;
	}

	public void setAutoBind(boolean autoBind) {
		this.autoBind = autoBind;
	}

	public void bind() {
		vertices.bind();
		if (!isVertexArray && indices.getNumIndices() > 0) {
			indices.bind();
		}
	}

	public void unbind() {
		vertices.unbind();
		if (!isVertexArray && indices.getNumIndices() > 0) {
			indices.unbind();
		}
	}

	public void render(int primitiveType) {
		render(primitiveType, 0,
				indices.getNumMaxIndices() > 0 ? getNumIndices()
						: getNumVertices());
	}

	public void render(int primitiveType, int offset, int count) {
		if (count == 0) {
			return;
		}
		if (autoBind) {
			bind();
		}
		if (isVertexArray) {
			if (indices.getNumIndices() > 0) {
				ShortBuffer buffer = indices.getBuffer();
				int oldPosition = buffer.position();
				int oldLimit = buffer.limit();
				buffer.position(offset);
				buffer.limit(offset + count);
				GLEx.gl10.glDrawElements(primitiveType, count,
						GL.GL_UNSIGNED_SHORT, buffer);
				buffer.position(oldPosition);
				buffer.limit(oldLimit);
			} else {
				GLEx.gl10.glDrawArrays(primitiveType, offset, count);
			}
		} else {
			if (indices.getNumIndices() > 0) {
				GLEx.gl11.glDrawElements(primitiveType, count,
						GL.GL_UNSIGNED_SHORT, offset * 2);
			} else {
				GLEx.gl11.glDrawArrays(primitiveType, offset, count);
			}
		}
		if (autoBind) {
			unbind();
		}
	}

	public void dispose() {
		vertices.dispose();
		indices.dispose();
	}

	public VertexAttribute getVertexAttribute(int usage) {
		GLAttributes attributes = vertices.getAttributes();
		int len = attributes.size();
		for (int i = 0; i < len; i++) {
			if (attributes._attributes[i].usage == usage) {
				return attributes._attributes[i];
			}
		}
		return null;
	}

	public GLAttributes getVertexAttributes() {
		return vertices.getAttributes();
	}

	public FloatBuffer getVerticesBuffer() {
		return vertices.getBuffer();
	}

	public ShortBuffer getIndicesBuffer() {
		return indices.getBuffer();
	}

}
