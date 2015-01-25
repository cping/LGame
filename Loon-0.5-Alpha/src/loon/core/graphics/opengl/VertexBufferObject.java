package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import loon.jni.NativeSupport;

public class VertexBufferObject implements VertexData {
	final VertexAttributes attributes;
	final FloatBuffer buffer;
	final ByteBuffer byteBuffer;
	int bufferHandle;
	final boolean isStatic;
	final int usage;
	boolean isDirty = false;
	boolean isBound = false;

	public VertexBufferObject(boolean isStatic, int numVertices,
			VertexAttribute... attributes) {
		this(isStatic, numVertices, new VertexAttributes(attributes));
	}

	public VertexBufferObject(boolean isStatic, int numVertices,
			VertexAttributes attributes) {
		this.isStatic = isStatic;
		this.attributes = attributes;

		byteBuffer = NativeSupport.newByteBuffer(this.attributes.vertexSize
				* numVertices);
		buffer = byteBuffer.asFloatBuffer();
		buffer.flip();
		byteBuffer.flip();
		bufferHandle = GLEx.gl.glGenBuffer();
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
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
			GLEx.gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(),
					byteBuffer, usage);
			isDirty = false;
		}
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		isDirty = true;
		NativeSupport.copy(vertices, byteBuffer, offset, count);
		buffer.position(0);
		buffer.limit(count);
		bufferChanged();
	}

	@Override
	public void updateVertices(int targetOffset, float[] vertices,
			int sourceOffset, int count) {
		isDirty = true;
		final int pos = byteBuffer.position();
		byteBuffer.position(targetOffset * 4);
		NativeSupport.copy(vertices, byteBuffer, sourceOffset, count);
		byteBuffer.position(pos);
		buffer.position(0);
		bufferChanged();
	}

	@Override
	public void bind(ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(ShaderProgram shader, int[] locations) {
		final GL20 gl = GLEx.gl;

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
				if (location < 0)
					continue;
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
		final GL20 gl = GLEx.gl;
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
		bufferHandle = GLEx.gl.glGenBuffer();
		isDirty = true;
	}

	@Override
	public void dispose() {
		GL20 gl = GLEx.gl;
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
		NativeSupport.freeMemory(byteBuffer);
	}
}
