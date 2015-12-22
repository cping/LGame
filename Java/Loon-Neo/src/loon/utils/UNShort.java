package loon.utils;

public class UNShort {

	private char unShort;

	public UNShort() {
	}

	public UNShort(short unsigned) {
		this.read(unsigned);
	}

	public UNShort(char tobecomeunsigned) {
		this.setValue(tobecomeunsigned);
	}

	public UNShort(ArrayByte bb, int offset) {
		this.read(bb, offset);
	}

	public UNShort(byte[] bytes, int offset) {
		this.read(bytes, offset);
	}

	public void read(short i) {

		ArrayByte bb = new ArrayByte(2);
		bb.writeShort(i);

		int firstByte = (0x000000FF & ((int) bb.get(0)));
		int secondByte = (0x000000FF & ((int) bb.get(1)));

		unShort = (char) (firstByte << 8 | secondByte);
	}

	public void read(ArrayByte bb, int offset) {

		int initial_pos = bb.position();
		bb.setPosition(offset);

		int firstByte = (0x000000FF & ((int) bb.get()));
		int secondByte = (0x000000FF & ((int) bb.get()));

		unShort = (char) (firstByte << 8 | secondByte);
		bb.setPosition(initial_pos);
	}

	public void read(byte[] bytes, int offset) {

		int firstByte = (0x000000FF & ((int) bytes[offset]));
		int secondByte = (0x000000FF & ((int) bytes[offset + 1]));

		unShort = (char) (firstByte << 8 | secondByte);
	}

	public byte[] write() {
		byte[] buf = new byte[2];

		buf[0] = (byte) ((unShort & 0xFF00) >> 8);
		buf[1] = (byte) (unShort & 0x00FF);

		return buf;
	}

	public boolean write(ArrayByte bb, int offset) {
		if (bb.limit() - 2 < offset) {
			return false;
		}
		bb.setPosition(offset);
		byte[] buf = new byte[2];

		buf[0] = (byte) ((unShort & 0xFF00) >> 8);
		buf[1] = (byte) (unShort & 0x00FF);

		bb.write(buf);
		return true;
	}

	public boolean write(byte[] bytes, int offset) {
		if (bytes.length - 2 < offset) {
			return false;
		}

		bytes[offset] = (byte) ((unShort & 0xFF00) >> 8);
		bytes[offset + 1] = (byte) (unShort & 0x00FF);

		return true;
	}

	public char getValue() {
		return unShort;
	}

	public void setValue(char value) {
		unShort = value;
	}
}
