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

public class QRNumber extends QRData {

	public QRNumber(String data) {
		super(QRMode.MODE_NUMBER, data);
	}

	@Override
	public void write(QRBitBuffer buffer) {

		String data = getData();
		int i = 0;

		while (i + 2 < data.length()) {
			int num = parseInt(data.substring(i, i + 3));
			buffer.put(num, 10);
			i += 3;
		}
		if (i < data.length()) {
			if (data.length() - i == 1) {
				int num = parseInt(data.substring(i, i + 1));
				buffer.put(num, 4);
			} else if (data.length() - i == 2) {
				int num = parseInt(data.substring(i, i + 2));
				buffer.put(num, 7);
			}
		}
	}

	@Override
	public int getLength() {
		return getData().length();
	}

	private static int parseInt(String s) {
		int num = 0;
		for (int i = 0; i < s.length(); i++) {
			num = num * 10 + parseInt(s.charAt(i));
		}
		return num;
	}

	private static int parseInt(char c) {
		if ('0' <= c && c <= '9') {
			return c - '0';
		}
		throw new LSysException("illegal char :" + c);
	}
}
