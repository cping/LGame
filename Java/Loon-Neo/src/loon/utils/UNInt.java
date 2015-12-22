package loon.utils;

public class UNInt {

	private long unInt;

	public UNInt() {
	}

	public UNInt(int unsigned) {
		this.read(unsigned);
	}

	public UNInt(long tobecomeunsigned) {
		this.setValue(tobecomeunsigned);
	}

	public UNInt(ArrayByte bb, int offset) {
		this.read(bb, offset);
	}

	public UNInt(byte[] bytes, int offset) {
		this.read(bytes, offset);
	}

	public void read(int i) {

		ArrayByte bb = new ArrayByte(4);
		bb.writeInt(i);

		int firstByte = (0x000000FF & ((int) bb.get(0)));
		int secondByte = (0x000000FF & ((int) bb.get(1)));
		int thirdByte = (0x000000FF & ((int) bb.get(2)));
		int fourthByte = (0x000000FF & ((int) bb.get(3)));

		unInt = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
	}

	public void read(ArrayByte bb, int offset) {
		int initial_pos = bb.position();
		bb.setPosition(offset);

		int firstByte = (0x000000FF & ((int) bb.get()));
		int secondByte = (0x000000FF & ((int) bb.get()));
		int thirdByte = (0x000000FF & ((int) bb.get()));
		int fourthByte = (0x000000FF & ((int) bb.get()));

		unInt = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
		bb.setPosition(initial_pos);
	}

	public void read(byte[] bytes, int offset) {

		int firstByte = (0x000000FF & ((int) bytes[offset]));
		int secondByte = (0x000000FF & ((int) bytes[offset]));
		int thirdByte = (0x000000FF & ((int) bytes[offset]));
		int fourthByte = (0x000000FF & ((int) bytes[offset]));

		unInt = ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
	}

	public byte[] write() {
		byte[] buf = new byte[4];

		buf[0] = (byte) ((unInt & 0xFF000000L) >> 24);
		buf[1] = (byte) ((unInt & 0x00FF0000L) >> 16);
		buf[2] = (byte) ((unInt & 0x0000FF00L) >> 8);
		buf[3] = (byte) (unInt & 0x000000FFL);

		return buf;
	}

	public boolean write(ArrayByte bb, int offset) {
		if (bb.limit() - 4 < offset) {
			return false;
		}
		bb.setPosition(offset);

		byte[] buf = new byte[4];

		buf[0] = (byte) ((unInt & 0xFF000000L) >> 24);
		buf[1] = (byte) ((unInt & 0x00FF0000L) >> 16);
		buf[2] = (byte) ((unInt & 0x0000FF00L) >> 8);
		buf[3] = (byte) (unInt & 0x000000FFL);

		bb.write(buf);
		return true;
	}

	public boolean write(byte[] bytes, int offset) {
		if (bytes.length - 4 < offset) {
			return false;
		}

		bytes[offset] = (byte) ((unInt & 0xFF000000L) >> 24);
		bytes[offset + 1] = (byte) ((unInt & 0x00FF0000L) >> 16);
		bytes[offset + 2] = (byte) ((unInt & 0x0000FF00L) >> 8);
		bytes[offset + 3] = (byte) (unInt & 0x000000FFL);

		return true;
	}

	public long getValue() {
		return unInt;
	}

	public void setValue(long value) {
		unInt = value;
	}
}
