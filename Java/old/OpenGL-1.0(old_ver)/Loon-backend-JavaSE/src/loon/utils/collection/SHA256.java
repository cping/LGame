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


public class SHA256 {
	
	public SHA256() {
		if (_key == null || _key.length == 0) {
			_precompute();
			reset();
		}
	}
	
	public static LongArray hash(LongArray data) {
		SHA256 sha = new SHA256();
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

	public SHA256 reset() {
		this._h = this._init.slice(0);
		this._buffer = new LongArray();
		this._length = 0;
		return this;
	}

	public SHA256 update(Object d) {
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

		for (i = 512 + ol & -512; i <= nl; i += 512) {
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

		b.push(0);
		b.push(0);
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
		int i = 0, factor;
		int prime = 2;
		outer: for (; i < 64; prime++) {
			for (factor = 2; factor * factor <= prime; factor++) {
				if (prime % factor == 0) {
					continue outer;
				}
			}
			if (i < 8) {
				this._init.set(i * 2, (int) frac(Math.pow(prime, 1d / 2d)));
			}
			this._key.set(i * 2, (int) frac(Math.pow(prime, 1d / 3d)));
			i++;
		}

	}

	public void _block(LongArray words) {

		int i;
		long tmp, a, b;
		LongArray w = words.slice(0);

		LongArray h = this._h;
		LongArray k = this._key;

		long h0 = h.items[0], h1 = h.items[1], h2 = h.items[2], h3 = h.items[3], h4 = h.items[4], h5 = h.items[5], h6 = h.items[6], h7 = h.items[7];

		for (i = 0; i < 64; i++) {
			if (i < 16) {
				tmp = w.items[i];
			} else {
				a = w.items[(i + 1) & 15];
				b = w.items[(i + 14) & 15];
				tmp = w.items[i & 15] = Convert
						.get(((Convert.MOVE_RightUShift(a, 7)
								^ Convert.MOVE_RightUShift(a, 18)
								^ Convert.MOVE_RightUShift(a, 3) ^ a << 25 ^ a << 14)
								+ (Convert.MOVE_RightUShift(b, 17)
										^ Convert.MOVE_RightUShift(b, 19)
										^ Convert.MOVE_RightUShift(b, 10) ^ b << 15 ^ b << 13)
								+ w.items[i & 15] + w.items[(i + 9) & 15]) | 0);
			}

			tmp = (tmp
					+ h7
					+ (Convert.MOVE_RightUShift(h4, 6) ^ Convert.MOVE_RightUShift(h4, 11)
							^ Convert.MOVE_RightUShift(h4, 25) ^ h4 << 26 ^ h4 << 21 ^ h4 << 7)
					+ (h6 ^ h4 & (h5 ^ h6)) + k.items[i]);
			h7 = h6;
			h6 = h5;
			h5 = h4;
			h4 = h3 + (int)(tmp | 0);
			h3 = h2;
			h2 = h1;
			h1 = h0;

			h0 = Convert.get((tmp + ((h1 & h2) ^ (h3 & (h1 ^ h2))) + (Convert.MOVE_RightUShift(h1 , 2) ^ Convert.MOVE_RightUShift(h1 ,13)
					^ Convert.MOVE_RightUShift(h1 , 22) ^ h1 << 30 ^ h1 << 19 ^ h1 << 10)) | 0);
		}
		h.items[0] = h.items[0] + (int) (h0 | 0);
		h.items[1] = h.items[1] + (int) (h1 | 0);
		h.items[2] = h.items[2] + (int) (h2 | 0);
		h.items[3] = h.items[3] + (int) (h3 | 0);
		h.items[4] = h.items[4] + (int) (h4 | 0);
		h.items[5] = h.items[5] + (int) (h5 | 0);
		h.items[6] = h.items[6] + (int) (h6 | 0);
		h.items[7] = h.items[7] + (int) (h7 | 0);

	}
}
