/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.utils.qrcode;

import loon.utils.CollectionUtils;
import loon.utils.StrBuilder;

public class QRBitBuffer {

	private byte[] buffer;
	private int length;

	public QRBitBuffer() {
		buffer = new byte[CollectionUtils.INITIAL_CAPACITY];
		length = 0;
	}

	private boolean get(int index) {
		return ((buffer[index / 8] >>> (7 - index % 8)) & 1) == 1;
	}

	public void put(int num, int length) {
		for (int i = 0; i < length; i++) {
			putBit(((num >>> (length - i - 1)) & 1) == 1);
		}
	}

	public void putBit(boolean bit) {
		if (length == buffer.length * 8) {
			buffer = CollectionUtils.expand(buffer, CollectionUtils.INITIAL_CAPACITY);
		}
		if (bit) {
			buffer[length / 8] |= (0x80 >>> (length % 8));
		}
		length++;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getLengthInBits() {
		return length;
	}

	@Override
	public String toString() {
		StrBuilder buffer = new StrBuilder();
		for (int i = 0; i < getLengthInBits(); i++) {
			buffer.append(get(i) ? '1' : '0');
		}
		return buffer.toString();
	}

}
