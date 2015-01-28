package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import loon.jni.NativeSupport;

public class IndexBufferObject implements IndexData {
	ShortBuffer buffer;
	ByteBuffer byteBuffer;
	int bufferHandle;
	final boolean isDirect;
	boolean isDirty = true;
	boolean isBound = false;
	final int usage;

	public IndexBufferObject(boolean isStatic, int maxIndices) {
		byteBuffer = NativeSupport.newByteBuffer(maxIndices * 2);
		isDirect = true;

		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
		bufferHandle = GLEx.gl.glGenBuffer();
		usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
	}

	public IndexBufferObject(int maxIndices) {
		byteBuffer = NativeSupport.newByteBuffer(maxIndices * 2);
		this.isDirect = true;

		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
		bufferHandle = GLEx.gl.glGenBuffer();
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
			GLEx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
					byteBuffer.limit(), byteBuffer, usage);
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
			GLEx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
					byteBuffer.limit(), byteBuffer, usage);
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
		GLEx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
		if (isDirty) {
			byteBuffer.limit(buffer.limit() * 2);
			GLEx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
					byteBuffer.limit(), byteBuffer, usage);
			isDirty = false;
		}
		isBound = true;
	}

	public void unbind() {
		GLEx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		isBound = false;
	}

	public void invalidate() {
		bufferHandle = GLEx.gl.glGenBuffer();
		isDirty = true;
	}

	public void dispose() {
		GLEx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLEx.gl.glDeleteBuffer(bufferHandle);
		bufferHandle = 0;
		NativeSupport.freeMemory(byteBuffer);
	}
}
