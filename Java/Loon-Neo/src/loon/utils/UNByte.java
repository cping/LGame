package loon.utils;

public class UNByte {

	private short unByte;

	public UNByte() {
	}

	public UNByte(byte unsigned) {
		this.read(unsigned);
	}

	public UNByte(short tobecomeunsigned) {
		this.setValue(tobecomeunsigned);
	}

	public UNByte(ArrayByte bb, int offset) {
		this.read(bb, offset);
	}

	public UNByte(byte[] bytes, int offset) {
		this.read(bytes, offset);
	}

	public void read(byte i) {
		int firstByte = (0x000000FF & ((int) i));
		unByte = (short) firstByte;
	}

	public void read(ArrayByte bb, int offset) {
		int initial_pos = bb.position();
		bb.setPosition(offset);
		int firstByte = (0x000000FF & ((int) bb.get()));
		unByte = (short) firstByte;
		bb.setPosition(initial_pos);
	}

	public void read(byte[] bytes, int offset) {
		int firstByte = (0x000000FF & ((int) bytes[offset]));
		unByte = (short) firstByte;
	}

	public byte write() {
		return (byte) (unByte & 0xFF);
	}

	public boolean write(ArrayByte bb, int offset) {
		if (bb.limit() - 1 < offset) {
			return false;
		}
		bb.setPosition(offset);
		bb.writeByte((byte) (unByte & 0xFF));
		return true;
	}

	public boolean write(byte[] bytes, int offset) {
		if (bytes.length - 1 < offset) {
			return false;
		}
		bytes[offset] = (byte) (unByte & 0xFF);
		return true;
	}

	public byte[] writeByteArray() {
		byte[] b = new byte[1];
		b[0] = (byte) (unByte & 0xFF);
		return b;
	}

	public short getValue() {
		return unByte;
	}

	public void setValue(short value) {
		unByte = value;
	}
}
