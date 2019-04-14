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

public class QRECI extends QRData {

	public QRECI(String data) {
		super(QRMode.MODE_ECI, data);
	}

	@Override
	public void write(QRBitBuffer buffer) {
		char[] chs = getData().toCharArray();
		for (int i = 0; i < chs.length; i++) {
			final int assignVal = chs[i];
			if (assignVal < 0) {
				continue;
			} else if (assignVal < (1 << 7)) {
				buffer.put(assignVal, 8);
			} else if (assignVal < (1 << 14)) {
				buffer.put(2, 2);
				buffer.put(assignVal, 14);
			} else if (assignVal < 1000000) {
				buffer.put(6, 3);
				buffer.put(assignVal, 21);
			}
		}
	}

	@Override
	public int getLength() {
		try {
			return QRUtil.getEncodeBytes(getData()).length;
		} catch (Throwable e) {
			throw LSystem.runThrow(e.getMessage(), e);
		}
	}
}
