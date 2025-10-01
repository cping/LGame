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
package loon.utils;

import loon.LSysException;
import loon.LSystem;

/**
 * 一个UUID生成器,作用是在没有UUID支持的环境获得UUID(为了算法通用,方便移植)
 *
 * ps:因为加入了游戏动态纹理内存占用量和纹理数量还有精灵桌面组件数量为因素,[在游戏运行时]是真随机值,不运行就是伪随机……
 */
public final class UUID {

	public static UUID convertUUID(String id) {
		if ((id == null) || (id.length() != 36)) {
			return null;
		}
		long lo, hi;
		lo = hi = 0;
		for (int i = 0, j = 0; i < 36; ++j) {

			switch (i) {
			case 8:
			case 13:
			case 18:
			case 23:
				if (id.charAt(i) != '-') {
					throw new LSysException(
							"UUID has to be represented by the standard 36-char representation");
				}
				++i;
			}
			int curr;
			char c = id.charAt(i);

			if (c >= '0' && c <= '9') {
				curr = (c - '0');
			} else if (c >= 'a' && c <= 'f') {
				curr = (c - 'a' + 10);
			} else if (c >= 'A' && c <= 'F') {
				curr = (c - 'A' + 10);
			} else {
				throw new LSysException(
						"Non-hex character at #" + i + ": '" + c + "' (value 0x" + CharUtils.toHex(c) + ")");
			}
			curr = (curr << 4);

			c = id.charAt(++i);

			if (c >= '0' && c <= '9') {
				curr |= (c - '0');
			} else if (c >= 'a' && c <= 'f') {
				curr |= (c - 'a' + 10);
			} else if (c >= 'A' && c <= 'F') {
				curr |= (c - 'A' + 10);
			} else {
				throw new LSysException(
						"Non-hex character at #" + i + ": '" + c + "' (value 0x" + CharUtils.toHex(c) + ")");
			}
			if (j < 8) {
				hi = (hi << 8) | curr;
			} else {
				lo = (lo << 8) | curr;
			}
			++i;
		}
		return new UUID(hi, lo);
	}

	private boolean dirty;

	private String uuidString;

	private long mostSigBits;

	private long leastSigBits;

	public UUID() {
		this(MathUtils.random);
	}

	public UUID(Random random) {
		this(random.nextLong(), random.nextLong());
	}

	public UUID(byte[] bytes) {
		checkUUIDByteArray(bytes, 0);
		long r1 = getLong(bytes, 0);
		long r2 = getLong(bytes, 8);
		this.set(r1, r2);
	}

	public UUID(long r1, long r2) {
		this.set(r1, r2);
	}

	public void set(long r1, long r2) {
		mostSigBits = r1;
		leastSigBits = r2;
		dirty = true;
	}

	protected int getInt(byte[] buffer, int offset) {
		return (buffer[offset] << 24) | ((buffer[offset + 1] & 0xFF) << 16) | ((buffer[offset + 2] & 0xFF) << 8)
				| (buffer[offset + 3] & 0xFF);
	}

	protected long getLong(byte[] buffer, int offset) {
		long hi = ((long) getInt(buffer, offset)) << 32;
		long lo = (((long) getInt(buffer, offset + 4)) << 32) >>> 32;
		return hi | lo;
	}

	protected void checkUUIDByteArray(byte[] bytes, int offset) {
		if (bytes == null) {
			throw new LSysException("Invalid byte[] passed: can not be null");
		}
		if (offset < 0) {
			throw new LSysException("Invalid offset (" + offset + ") passed: can not be negative");
		}
		if ((offset + 16) > bytes.length) {
			throw new LSysException(
					"Invalid offset (" + offset + ") passed: not enough room in byte array (need 16 bytes)");
		}
	}

	@Override
	public String toString() {
		if (dirty) {
			long millis = TimeUtils.millis();
			mostSigBits = mostSigBits + ((millis / 2) + MathUtils.nextInt((int) leastSigBits));
			leastSigBits = leastSigBits + ((millis / 3) + MathUtils.nextInt((int) mostSigBits));
			mostSigBits += MathUtils.max(LSystem.getTextureMemSize(), MathUtils.random(10)) * MathUtils.random(986429531);
			leastSigBits += MathUtils.max(LSystem.countTexture(), MathUtils.random(10)) * MathUtils.random(895318642);
			mostSigBits += LSystem.allSpritesCount() * MathUtils.random(135799876);
			leastSigBits += LSystem.allDesktopCount() * MathUtils.random(246805432);
			uuidString = (digits(mostSigBits >> 32, 8) + "-" + digits(mostSigBits >> 16, 4) + "-"
					+ digits(mostSigBits, 4) + "-" + digits(leastSigBits >> 48, 4) + "-" + digits(leastSigBits, 12));
			dirty = false;
		}
		return uuidString;
	}

	private String toUnsignedString(long val) {
		int mag = 64 - MathUtils.longOfZeros(val);
		int chars = MathUtils.max(((mag + (4 - 1)) / 4), 1);
		char[] buf = new char[chars];
		CharUtils.toUnsignedLong(val, 4, buf, 0, chars);
		return new String(buf);
	}

	private String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return toUnsignedString(hi | (val & (hi - 1))).substring(1);
	}

	@Override
	public int hashCode() {
		long hilo = mostSigBits ^ leastSigBits;
		return ((int) (hilo >> 32)) ^ (int) hilo;
	}
}
