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

public class QRMode {

	public static final int MODE_NUMBER = 1;

	public static final int MODE_ALPHA_NUM = 2;

	public static final int MODE_8BIT_BYTE = 4;

	public static final int MODE_ECI = 7;

	public static final int MODE_KANJI = 8;

	private static final int[][] MODES = { { MODE_NUMBER, 10, 12, 14 }, { MODE_ALPHA_NUM, 9, 11, 13 },
			{ MODE_8BIT_BYTE, 8, 16, 16 }, { MODE_KANJI, 8, 10, 12 }, { MODE_ECI, 0, 0, 0 } };

	private final int modeBits;

	private final int[] numBitsCharCount;

	public static QRMode getMode(int mode) {
		switch (mode) {
		case MODE_NUMBER:
			return new QRMode(MODE_NUMBER, MODES[0]);
		case MODE_ALPHA_NUM:
			return new QRMode(MODE_ALPHA_NUM, MODES[1]);
		case MODE_8BIT_BYTE:
			return new QRMode(MODE_8BIT_BYTE, MODES[2]);
		case MODE_KANJI:
			return new QRMode(MODE_KANJI, MODES[3]);
		default:
			return new QRMode(MODE_ECI, MODES[4]);
		}
	}

	public QRMode(int mode, int... ccbits) {
		modeBits = mode;
		numBitsCharCount = ccbits;
	}
	
	public int getModeBits() {
		return modeBits;
	}

	public int getBits(int index) {
		return numBitsCharCount[index];
	}
	
	public int numCharCountBits(int ver) {
		if (!(QRCode.MIN_VERSION <= ver && ver <= QRCode.MAX_VERSION)) {
			return 0;
		}
		return numBitsCharCount[(ver + 7) / 17];
	}
}
