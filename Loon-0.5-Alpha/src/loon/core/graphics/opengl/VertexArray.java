package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import loon.jni.NativeSupport;

public class VertexArray implements VertexData {
	final VertexAttributes attributes;
	final FloatBuffer buffer;
	final ByteBuffer byteBuffer;
	boolean isBound = false;

	public VertexArray(int numVertices, VertexAttribute... attributes) {
		this(numVertices, new VertexAttributes(attributes));
	}

	public VertexArray(int numVertices, VertexAttributes attributes) {
		this.attributes = attributes;
		byteBuffer = NativeSupport.newByteBuffer(this.attributes.vertexSize
				* numVertices);
		buffer = byteBuffer.asFloatBuffer();
		buffer.flip();
		byteBuffer.flip();
	}

	@Override
	public void dispose() {
		NativeSupport.freeMemory(byteBuffer);
	}

	@Override
	public FloatBuffer getBuffer() {
		return buffer;
	}

	@Override
	public int getNumVertices() {
		return buffer.limit() * 4 / attributes.vertexSize;
	}

	public int getNumMaxVertices() {
		return byteBuffer.capacity() / attributes.vertexSize;
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		NativeSupport.copy(vertices, buffer, offset, count);
		buffer.position(0);
		buffer.limit(count);

	}

	@Override
	public void updateVertices(int targetOffset, float[] vertices,
			int sourceOffset, int count) {
		final int pos = byteBuffer.position();
		byteBuffer.position(targetOffset * 4);
		NativeSupport.copy(vertices, byteBuffer, sourceOffset, count);
		byteBuffer.position(pos);
	}

	@Override
	public void bind(final ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(final ShaderProgram shader, final int[] locations) {

		final int numAttributes = attributes.size();
		byteBuffer.limit(buffer.limit() * 4);
		if (locations == null) {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = shader
						.getAttributeLocation(attribute.alias);
				if (location < 0)
					continue;
				shader.enableVertexAttribute(location);

				if (attribute.type == GL20.GL_FLOAT) {
					buffer.position(attribute.offset / 4);
					shader.setVertexAttribute(location,
							attribute.numComponents, attribute.type,
							attribute.normalized, attributes.vertexSize, buffer);
				} else {
					byteBuffer.position(attribute.offset);
					shader.setVertexAttribute(location,
							attribute.numComponents, attribute.type,
							attribute.normalized, attributes.vertexSize,
							byteBuffer);
				}
			}
		} else {
			for (int i = 0; i < numAttributes; i++) {
				final VertexAttribute attribute = attributes.get(i);
				final int location = locations[i];
				if (location < 0)
					continue;
				shader.enableVertexAttribute(location);

				if (attribute.type == GL20.GL_FLOAT) {
					buffer.position(attribute.offset / 4);
					shader.setVertexAttribute(location,
							attribute.numComponents, attribute.type,
							attribute.normalized, attributes.vertexSize, buffer);
				} else {
					byteBuffer.position(attribute.offset);
					shader.setVertexAttribute(location,
							attribute.numComponents, attribute.type,
							attribute.normalized, attributes.vertexSize,
							byteBuffer);
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
				if (location >= 0)
					shader.disableVertexAttribute(location);
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
}
