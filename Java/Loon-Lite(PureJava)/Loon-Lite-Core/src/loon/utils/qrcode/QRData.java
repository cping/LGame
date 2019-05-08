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

import loon.LSysException;

public abstract class QRData {

	private final int mode;

	private final String data;

	protected QRData(int mode, String data) {
		this.mode = mode;
		this.data = data;
	}

	public int getMode() {
		return mode;
	}

	public String getData() {
		return data;
	}

	public abstract int getLength();

	public abstract void write(QRBitBuffer buffer);

	public int getLengthInBits(int type) {
		if (type >= 0 && type < 10) {
			return QRMode.getMode(mode).getBits(1);
		} else if (type < 27) {
			return QRMode.getMode(mode).getBits(2);
		} else if (type < 41) {
			return QRMode.getMode(mode).getBits(3);
		} else {
			throw new LSysException("type:" + type);
		}
	}
}
