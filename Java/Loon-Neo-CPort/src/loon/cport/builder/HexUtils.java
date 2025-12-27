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
package loon.cport.builder;

public class HexUtils {

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	private static final int[] HEX_BYTES = new int[128];

	static {
		for (int i = 0; i < HEX_CHARS.length; i++) {
			HEX_BYTES[HEX_CHARS[i]] = i;
			if (HEX_CHARS[i] >= 'A') {
				HEX_BYTES[HEX_CHARS[i] - 'A' + 'a'] = i;
			}
		}
	}

	public static byte[] decode(String string) {
		byte[] bytes = new byte[string.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			char c0 = string.charAt((i * 2));
			char c1 = string.charAt((i * 2) + 1);
			bytes[i] = (byte) ((HEX_BYTES[c0] << 4) + (HEX_BYTES[c1]));
		}
		return bytes;
	}

	public static String encode(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			chars[(i * 2)] = HEX_CHARS[(b & 0xFF) >>> 4];
			chars[(i * 2) + 1] = HEX_CHARS[(b & 0x0F)];
		}
		return String.valueOf(chars);
	}

}
