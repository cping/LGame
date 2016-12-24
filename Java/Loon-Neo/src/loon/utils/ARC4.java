package loon.utils;

/**
 * ARC4加密算法(伪随机算法，非RSA的正式加密算法，安全度低，但是计算量也低，比较适合游戏数据加密)，用来为Loon中数据进行一些简单的加密
 */
public class ARC4 {

	private byte[] key;
	private byte[] state;
	private int x;
	private int y;

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

	public void crypt(byte[] data) {
		crypt(data, data);
	}

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
