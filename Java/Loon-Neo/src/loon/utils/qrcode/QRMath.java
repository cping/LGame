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

public class QRMath {

	private static int[] EXP_TABLE;
	private static int[] LOG_TABLE;

	public static void init() {
		if (EXP_TABLE == null || LOG_TABLE == null) {
			EXP_TABLE = new int[256];
			for (int i = 0; i < 8; i++) {
				EXP_TABLE[i] = 1 << i;
			}
			for (int i = 8; i < 256; i++) {
				EXP_TABLE[i] = EXP_TABLE[i - 4] ^ EXP_TABLE[i - 5] ^ EXP_TABLE[i - 6] ^ EXP_TABLE[i - 8];
			}
			LOG_TABLE = new int[256];
			for (int i = 0; i < 255; i++) {
				LOG_TABLE[EXP_TABLE[i]] = i;
			}
		}
	}

	public static int glog(int n) {
		init();
		if (n < 1) {
			throw LSystem.runThrow("log(" + n + ")");
		}
		return LOG_TABLE[n];
	}

	public static int gexp(int n) {
		init();
		while (n < 0) {
			n += 255;
		}
		while (n >= 256) {
			n -= 255;
		}
		return EXP_TABLE[n];
	}
}
