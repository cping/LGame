/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.utils.collection;

import java.math.BigDecimal;

public class SHA1 {

	public SHA1() {
		if (_key == null || _key.length == 0) {
			_precompute();
			reset();
		}
	}

	public static LongArray hash(LongArray data) {
		SHA1 sha = new SHA1();
		if (sha._key == null || sha._key.length == 0) {
			sha._precompute();
			sha.reset();
		}
		return sha.update(data)._finalize();
	}

	int blockSize = 512;

	LongArray _init = new LongArray();

	LongArray _key = new LongArray();

	LongArray _h;

	LongArray _buffer = new LongArray();

	int _length = 0;

	public SHA1 reset() {
		this._h = this._init.slice(0);
		this._buffer = new LongArray();
		this._length = 0;
		return this;
	}

	public SHA1 update(Object d) {
		LongArray data = null;
		if (d instanceof String) {
			data = BigNumber.utf8_toBits((String) d);
		} else {
			data = (LongArray) d;
		}

		int i;
		if (_buffer == null) {
			_buffer = new LongArray();
		}
		LongArray b = this._buffer = BitArray.concat(this._buffer, data);

		int ol = this._length, nl = this._length = ol
				+ (int) BitArray.bitLength(data);

		for (i = this.blockSize + ol & -this.blockSize; i <= nl; i += this.blockSize) {
			this._block(b.splice(0, 16));
		}

		return this;
	}

	public LongArray _finalize() {
		int i;
		LongArray b = this._buffer, h = this._h;
		long[] a = new long[] { BitArray.partial(1, 1) };

		b = BitArray.concat(b, new LongArray(a));

		for (i = b.length + 2; (i & 15) > 0; i++) {
			b.push(0);
		}

		b.push((long) Math.floor(this._length / 0x100000000l));
		b.push(this._length | 0);

		while (b.length > 0) {
			this._block(b.splice(0, 16));
		}

		return h;
	}

	public long frac(double x) {
		double math_floor = Math.floor(x);
		BigDecimal intx = new BigDecimal(x - math_floor);
		intx = intx.multiply(BigDecimal.valueOf(0x100000000l));
		return intx.longValue() | 0;
	}

	public void _precompute() {
		_init = new LongArray(new long[] { 0x67452301, 0xEFCDAB89, 0x98BADCFE,
				0x10325476, 0xC3D2E1F0 });
		_key = new LongArray(new long[] { 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC,
				0xCA62C1D6 });
	}

	public long _f(long t, long b, long c, long d) {
		if (t <= 19) {
			return (int) ((b & c) | (~b & d));
		} else if (t <= 39) {
			return (int) (b ^ c ^ d);
		} else if (t <= 59) {
			return (int) ((b & c) | (b & d) | (c & d));
		} else if (t <= 79) {
			return (int) (b ^ c ^ d);
		}
		return 0;
	}

	public long _S(long n, long x) {
		return (int) ((x << n) | (Convert.MOVE_RightUShift(x, 32 - (int) n)));
	}

	public void _block(LongArray words) {

		int t;
		long tmp, a, b, c, d, e;
		LongArray w = words.slice(0);
		LongArray h = this._h;

		a = h.items[0];
		b = h.items[1];
		c = h.items[2];
		d = h.items[3];
		e = h.items[4];

		for (t = 0; t <= 79; t++) {
			if (t >= 16) {
				w.items[t] = (int) (this._S(1, w.items[t - 3] ^ w.items[t - 8]
						^ w.items[t - 14] ^ w.items[t - 16]));
			}
			tmp = (this._S(5, a)
					+ this._f(t, b, c, d)
					+ e
					+ w.items[t]
					+ (int) (this._key.items[(int) Math.floor((double) t / 20d)]) | 0);
			e = d;
			d = c;
			c = this._S(30, b);
			b = a;
			a = tmp;
		}

		h.items[0] = (int) ((h.items[0] + a) | 0);
		h.items[1] = (int) ((h.items[1] + b) | 0);
		h.items[2] = (int) ((h.items[2] + c) | 0);
		h.items[3] = (int) ((h.items[3] + d) | 0);
		h.items[4] = (int) ((h.items[4] + e) | 0);

	}
}
