/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.utils;

import loon.LSystem;

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

		int firstByte = (0x000000FF & ((int) bytes[offset + 0]));
		int secondByte = (0x000000FF & ((int) bytes[offset + 1]));
		int thirdByte = (0x000000FF & ((int) bytes[offset + 2]));
		int fourthByte = (0x000000FF & ((int) bytes[offset + 3]));

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

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = LSystem.unite(hashCode, unInt);
		return hashCode;
	}
}
