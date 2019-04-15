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

import loon.LSystem;
import loon.utils.CharUtils;

public class QRKANJI extends QRData {

	public QRKANJI(String data) {
		super(QRMode.MODE_KANJI, data);
	}

	@Override
	public void write(QRBitBuffer buffer) {
		try {
			byte[] data = QRUtil.getEncodeBytes(getData());
			int i = 0;
			while (i + 1 < data.length) {
				int c = ((0xff & data[i]) << 8) | (0xff & data[i + 1]);
				if (0x8140 <= c && c <= 0x9FFC) {
					c -= 0x8140;
				} else if (0xE040 <= c && c <= 0xEBBF) {
					c -= 0xC140;
				} else {
					throw LSystem.runThrow("illegal char at " + (i + 1) + "/" + CharUtils.toHex(c));
				}
				c = ((c >>> 8) & 0xff) * 0xC0 + (c & 0xff);
				buffer.put(c, 13);
				i += 2;
			}
			if (i < data.length) {
				throw LSystem.runThrow("illegal char at " + (i + 1));
			}
		} catch (Throwable e) {
			throw LSystem.runThrow(e.getMessage(), e);
		}
	}

	@Override
	public int getLength() {
		try {
			return QRUtil.getEncodeBytes(getData()).length / 2;
		} catch (Throwable e) {
			throw LSystem.runThrow(e.getMessage(), e);
		}
	}
}