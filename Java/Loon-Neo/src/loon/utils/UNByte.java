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

public class UNByte {

	private short unByte;

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
		int firstByte = (0x000000FF & (i));
		unByte = (short) firstByte;
	}

	public void read(ArrayByte bb, int offset) {
		int initial_pos = bb.position();
		bb.setPosition(offset);
		int firstByte = (0x000000FF & (bb.get()));
		unByte = (short) firstByte;
		bb.setPosition(initial_pos);
	}

	public void read(byte[] bytes, int offset) {
		int firstByte = (0x000000FF & (bytes[offset]));
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

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = LSystem.unite(hashCode, unByte);
		return hashCode;
	}
}
