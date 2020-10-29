package loon.utils;

import java.io.IOException;
import java.io.OutputStream;

import loon.LRelease;

public class ArrayByteOutput extends OutputStream implements LRelease {

	private final ArrayByte _buffer;

	public ArrayByteOutput(int size) {
		_buffer = new ArrayByte(size);
	}

	public ArrayByteOutput() {
		this(8192 * 10);
	}

	public ArrayByte getArrayByte() {
		return _buffer;
	}

	public byte[] toByteArray() {
		return _buffer.getBytes();
	}

	@Override
	public void write(int b) throws IOException {
		_buffer.writeByte(b);
	}

	@Override
	public void close() {
		_buffer.close();
	}

}
