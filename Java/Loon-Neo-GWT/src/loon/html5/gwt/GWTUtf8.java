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
package loon.html5.gwt;

public class GWTUtf8 {

	private static final char REPLACEMENT = '\ufffd';
	private static final int UTF8_ACCEPT = 0;
	private static final int UTF8_REJECT = 12;

	private static final byte[] BYTE_TABLE = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3,
			11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };

	private static final byte[] TRANSITION_TABLE = { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12,
			12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12,
			24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12,
			12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12,
			12, 12, 12, 12, 12, 12, 12, 12, 12 };

	private int codePoint;
	private int state;
	private final char[] utf16Char = new char[2];
	private char[] charBuffer;
	private int charOffset;

	public GWTUtf8() {
		this.state = UTF8_ACCEPT;
	}

	protected void reset() {
		state = UTF8_ACCEPT;
	}

	public int decode(byte[] b, int offset, int length, char[] charBuffer, int charOffset) {
		this.charBuffer = charBuffer;
		this.charOffset = charOffset;
		int end = offset + length;
		for (int i = offset; i < end; i++)
			decode(b[i]);
		return this.charOffset - charOffset;
	}

	private void decode(byte b) {

		if (b > 0 && state == UTF8_ACCEPT) {
			charBuffer[charOffset++] = (char) (b & 0xFF);
		} else {
			int i = b & 0xFF;
			int type = BYTE_TABLE[i];
			codePoint = state == UTF8_ACCEPT ? (0xFF >> type) & i : (i & 0x3F) | (codePoint << 6);
			int next = TRANSITION_TABLE[state + type];

			switch (next) {
			case UTF8_ACCEPT:
				state = next;
				if (codePoint < Character.MIN_HIGH_SURROGATE) {
					charBuffer[charOffset++] = (char) codePoint;
				} else {
					int codePointLength = Character.toChars(codePoint, utf16Char, 0);
					charBuffer[charOffset++] = utf16Char[0];
					if (codePointLength == 2)
						charBuffer[charOffset++] = utf16Char[1];
				}
				break;

			case UTF8_REJECT:
				codePoint = 0;
				state = UTF8_ACCEPT;
				charBuffer[charOffset++] = REPLACEMENT;
				break;

			default:
				state = next;
				break;
			}
		}
	}
}
