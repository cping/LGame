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
package loon.utils;

import loon.LSystem;

/**
 * ARC4加密算法(伪随机算法，非RSA的正式加密算法，安全度低，但是计算量也低，比较适合游戏数据加密)，用来为Loon中数据进行一些简单的加密<br>
 * 
 * 大体用法就是这样的:<br>
 * 
 * <pre>
 * // 第一次加密是加密了
 * ArrayByte bytes = ARC4.cryptData("ABCDDDA", "我有一个秘密,那就是,打死我也不说");
 * // 再来一次就解密了
 * bytes = ARC4.cryptData("ABCDDDA", bytes);
 * System.out.println(bytes.toUTF8String());
 * </pre>
 */
public class ARC4 {

	/**
	 * 用密钥加密指定数据(ARC4这算法加密一次是加密,加密两次就变成解密了……)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static ArrayByte cryptData(String key, ArrayByte value) {
		try {
			ARC4 rc4 = new ARC4(key);
			return rc4.getCrypt(value);
		} catch (Throwable e) {
			return value;
		}
	}

	/**
	 * 用密钥加密指定数据(ARC4这算法加密一次是加密,加密两次就变成解密了……)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static ArrayByte cryptData(String key, String v) {
		try {
			ARC4 rc4 = new ARC4(key);
			return rc4.getCrypt(v.getBytes(LSystem.ENCODING));
		} catch (Throwable e) {
			return new ArrayByte(v.getBytes());
		}
	}

	private final byte[] key;
	private final byte[] state;
	private int x;
	private int y;

	public ARC4(ArrayByte key) {
		this(key.getData());
	}

	public ARC4(String key) throws Exception {
		this(key.getBytes(LSystem.ENCODING));
	}

	/**
	 * 用指定的密钥生成一个ARC4对象，密钥长度至多只允许256位
	 * 
	 * @param key
	 */
	public ARC4(byte[] key) {
		this.state = new byte[256];
		int length = MathUtils.min(256, key.length);
		byte[] keyCopy = new byte[length];
		System.arraycopy(key, 0, keyCopy, 0, length);
		this.key = keyCopy;
		reset();
	}

	public void reset() {
		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}
		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (j + state[i] + key[i % key.length]) & 0xff;
			byte temp = state[i];
			state[i] = state[j];
			state[j] = temp;
		}

		x = 0;
		y = 0;
	}

	/**
	 * ARC4这算法加密一次是加密,加密两次就变成解密了……
	 * 
	 * @param data
	 * @return
	 */
	public ArrayByte getCrypt(byte[] data) {
		byte[] buffer = new byte[data.length];
		crypt(data, buffer);
		return new ArrayByte(buffer);
	}

	/**
	 * ARC4这算法加密一次是加密,加密两次就变成解密了……
	 * 
	 * @param input
	 * @return
	 */
	public ArrayByte getCrypt(ArrayByte input) {
		byte[] output = new byte[input.available()];
		crypt(input.getData(), output);
		return new ArrayByte(output);
	}

	/**
	 * ARC4这算法加密一次是加密,加密两次就变成解密了……
	 * 
	 * @param data
	 */
	public void crypt(byte[] data) {
		crypt(data, data);
	}

	/**
	 * ARC4这算法加密一次是加密,加密两次就变成解密了……
	 * 
	 * @param input
	 * @param output
	 */
	public void crypt(byte[] input, byte[] output) {
		for (int i = 0; i < input.length; i++) {
			x = (x + 1) & 0xff;
			y = (state[x] + y) & 0xff;
			byte temp = state[x];
			state[x] = state[y];
			state[y] = temp;
			output[i] = (byte) ((input[i] ^ state[(state[x] + state[y]) & 0xff]));
		}
	}
}
