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

public class QRAlphaNum extends QRData {

	public QRAlphaNum(String data) {
		super(QRMode.MODE_ALPHA_NUM, data);
	}

	@Override
	public void write(QRBitBuffer buffer) {
		char[] c = getData().toCharArray();
		int i = 0;
		while (i + 1 < c.length) {
			buffer.put(getCode(c[i]) * 45 + getCode(c[i + 1]), 11);
			i += 2;
		}
		if (i < c.length) {
			buffer.put(getCode(c[i]), 6);
		}
	}

	@Override
	public int getLength() {
		return getData().length();
	}

	private static int getCode(char c) {

		if ('0' <= c && c <= '9') {
			return c - '0';
		} else if ('A' <= c && c <= 'Z') {
			return c - 'A' + 10;
		} else {
			switch (c) {
			case ' ':
				return 36;
			case '$':
				return 37;
			case '%':
				return 38;
			case '*':
				return 39;
			case '+':
				return 40;
			case '-':
				return 41;
			case '.':
				return 42;
			case '/':
				return 43;
			case ':':
				return 44;
			default:
				throw new LSysException("illegal char :" + c);
			}
		}

	}
}
