package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import loon.jni.NativeSupport;

public class IndexArray implements IndexData {
	final static IntBuffer tmpHandle = NativeSupport.newIntBuffer(1);

	ShortBuffer buffer;
	ByteBuffer byteBuffer;

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

	public void setIndices(ShortBuffer indices) {
		int pos = indices.position();
		buffer.clear();
		buffer.limit(indices.remaining());
		buffer.put(indices);
		buffer.flip();
		indices.position(pos);
		byteBuffer.position(0);
		byteBuffer.limit(buffer.limit() << 1);
	}

	public ShortBuffer getBuffer() {
		return buffer;
	}

	public void bind() {
	}

	public void unbind() {
	}

	public void invalidate() {
	}

	public void dispose() {
		NativeSupport.freeMemory(byteBuffer);
	}
}
