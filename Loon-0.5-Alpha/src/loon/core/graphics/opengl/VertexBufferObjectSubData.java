package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import loon.jni.NativeSupport;

public class VertexBufferObjectSubData implements VertexData {
	final VertexAttributes attributes;
	final FloatBuffer buffer;
	final ByteBuffer byteBuffer;
	int bufferHandle;
	final boolean isDirect;
	final boolean isStatic;
	final int usage;
	boolean isDirty = false;
	boolean isBound = false;

	public VertexBufferObjectSubData(boolean isStatic, int numVertices,
			VertexAttribute... attributes) {
		this.isStatic = isStatic;
		this.attributes = new VertexAttributes(attributes);
		byteBuffer = NativeSupport.newByteBuffer(this.attributes.vertexSize
				* numVertices);
		isDirect = true;

		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
		buffer = byteBuffer.asFloatBuffer();
		bufferHandle = createBufferObject();
		buffer.flip();
		byteBuffer.flip();
	}

	private int createBufferObject() {
		int result = GLEx.gl20.glGenBuffer();
		GLEx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, result);
		GLEx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.capacity(),
				null, usage);
		GLEx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
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
			GLEx.gl20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0,
					byteBuffer.limit(), byteBuffer);
			isDirty = false;
		}
	}

	@Override
	public void setVertices(float[] vertices, int offset, int count) {
		isDirty = true;
		if (isDirect) {
			NativeSupport.copy(vertices, byteBuffer, offset, count);
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
			NativeSupport.copy(vertices, sourceOffset, byteBuffer, count);
			byteBuffer.position(pos);
		} else {
			throw new RuntimeException("Buffer must be allocated direct."); // Should
																			// never
																			// happen
		}
		bufferChanged();
	}

	/**
	 * Binds this VertexBufferObject for rendering via glDrawArrays or
	 * glDrawElements
	 * 
	 * @param shader
	 *            the shader
	 */
	@Override
	public void bind(final ShaderProgram shader) {
		bind(shader, null);
	}

	@Override
	public void bind(final ShaderProgram shader, final int[] locations) {
		final GL20 gl = GLEx.gl20;

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
		final GL20 gl = GLEx.gl20;
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

	@Override
	public void dispose() {
		GL20 gl = GLEx.gl20;
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
	}

	public int getBufferHandle() {
		return bufferHandle;
	}
}
