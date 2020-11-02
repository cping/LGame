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


public class SHA512 {

	public SHA512() {
		if (_key == null || _key.length == 0) {
			_precompute();
			reset();
		}
	}

	public static LongArray hash(LongArray data) {
		SHA512 sha = new SHA512();
		if (sha._key == null || sha._key.length == 0) {
			sha._precompute();
			sha.reset();
		}
		return sha.update(data)._finalize();
	}

	int[] _keyr = new int[] { 0x28ae22, 0xef65cd, 0x4d3b2f, 0x89dbbc, 0x48b538,
			0x05d019, 0x194f9b, 0x6d8118, 0x030242, 0x706fbe, 0xe4b28c,
			0xffb4e2, 0x7b896f, 0x1696b1, 0xc71235, 0x692694, 0xf14ad2,
			0x4f25e3, 0x8cd5b5, 0xac9c65, 0x2b0275, 0xa6e483, 0x41fbd4,
			0x1153b5, 0x66dfab, 0xb43210, 0xfb213f, 0xef0ee4, 0xa88fc2,
			0x0aa725, 0x03826f, 0x0e6e70, 0xd22ffc, 0x26c926, 0xc42aed,
			0x95b3df, 0xaf63de, 0x77b2a8, 0xedaee6, 0x82353b, 0xf10364,
			0x423001, 0xf89791, 0x54be30, 0xef5218, 0x65a910, 0x71202a,
			0xbbd1b8, 0xd2d0c8, 0x41ab53, 0x8eeb99, 0x9b48a8, 0xc95a63,
			0x418acb, 0x63e373, 0xb2b8a3, 0xefb2fc, 0x172f60, 0xf0ab72,
			0x6439ec, 0x631e28, 0x82bde9, 0xc67915, 0x72532b, 0x26619c,
			0xc0c207, 0xe0eb1e, 0x6ed178, 0x176fba, 0xc898a6, 0xf90dae,
			0x1c471b, 0x047d84, 0xc72493, 0xc9bebc, 0x100d4c, 0x3e42b6,
			0x657e2a, 0xd6faec, 0x475817 };

	int[] _initr = new int[] { 0xbcc908, 0xcaa73b, 0x94f82b, 0x1d36f1,
			0xe682d1, 0x3e6c1f, 0x41bd6b, 0x7e2179 };

	final static int blockSize = 1024;

	LongArray _init = new LongArray();

	LongArray _key = new LongArray();

	LongArray _h;

	LongArray _buffer = new LongArray();

	int _length = 0;

	public SHA512 reset() {
		this._h = this._init.slice(0);
		this._buffer = new LongArray();
		this._length = 0;
		return this;
	}

	public SHA512 update(Object d) {
		LongArray data = null;
		if (d instanceof String) {
			data = BigNumber.utf8_toBits((String) d);
		} else if (d instanceof long[]) {
			data = new LongArray((long[]) d);
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

		for (i = 1024 + ol & -1024; i <= nl; i += 1024) {
			this._block(b.splice(0, 32));
		}

		return this;
	}

	public LongArray _finalize() {
		int i;
		LongArray b = this._buffer, h = this._h;
		long[] a = new long[] { BitArray.partial(1, 1) };

		b = BitArray.concat(b, new LongArray(a));

		for (i = b.length + 4; (i & 31) > 0; i++) {
			b.push(0);
		}

		b.push(0);
		b.push(0);
		b.push((long) Math.floor(this._length / 0x100000000l));
		b.push(this._length | 0);

		while (b.length > 0) {
			LongArray as = b.splice(0, 32);
			this._block(as);
		}

		return h;
	}

	public long frac(double x) {
		double math_floor = Math.floor(x);
		BigDecimal intx = new BigDecimal(x - math_floor);
		intx = intx.multiply(BigDecimal.valueOf(0x100000000l));
		return intx.longValue() | 0;
	}

	public long frac2(double x) {
		double math_floor = Math.floor(x);
		BigDecimal intx = new BigDecimal(x - math_floor);
		intx = intx.multiply(BigDecimal.valueOf(0x10000000000l));
		return intx.longValue() & 0xff;
	}

	public void _precompute() {
		int i = 0, factor;
		int prime = 2;
		outer: for (; i < 80; prime++) {
			for (factor = 2; factor * factor <= prime; factor++) {
				if (prime % factor == 0) {
					continue outer;
				}
			}
			if (i < 8) {
				this._init.set(i * 2, (int) frac(Math.pow(prime, 1d / 2d)));
				this._init.set(i * 2 + 1,
						Convert.MOVE_LeftShift(frac2(Math.pow(prime, 1d / 2d)), 24)
								| this._initr[i]);
			}
			this._key.set(i * 2, (int) frac(Math.pow(prime, 1d / 3d)));
			this._key.set(i * 2 + 1,
					Convert.MOVE_LeftShift(frac2(Math.pow(prime, 1d / 3d)), 24)
							| this._keyr[i]);
			i++;
		}

	}

	public void _block(LongArray words) {

		int i;
		long wrh = 0, wrl;
		LongArray w = words.slice(0);

		LongArray h = this._h;
		LongArray k = this._key;

		long h0h = h.items[0], h0l = h.items[1], h1h = h.items[2], h1l = h.items[3], h2h = h.items[4], h2l = h.items[5], h3h = h.items[6], h3l = h.items[7], h4h = h.items[8], h4l = h.items[9], h5h = h.items[10], h5l = h.items[11], h6h = h.items[12], h6l = h.items[13], h7h = h.items[14], h7l = h.items[15];

		long ah = h0h, al = h0l, bh = h1h, bl = h1l, ch = h2h, cl = h2l, dh = h3h, dl = h3l, eh = h4h, el = h4l, fh = h5h, fl = h5l, gh = h6h, gl = h6l, hh = h7h, hl = h7l;

		for (i = 0; i < 80; i++) {
			if (i < 16) {
				wrh = w.items[i * 2];
				wrl = w.items[i * 2 + 1];
			} else {
				long gamma0xh = w.items[(i - 15) * 2];
				long gamma0xl = w.items[(i - 15) * 2 + 1];

				long gamma0h = ((Convert.MOVE_LeftShift(gamma0xl, 31) | Convert
						.MOVE_RightUShift(gamma0xh, 1))
						^ (Convert.MOVE_LeftShift(gamma0xl, 24) | Convert
								.MOVE_RightUShift(gamma0xh, 8)) ^ Convert
						.MOVE_RightUShift(gamma0xh, 7));

				long gamma0l = ((Convert.MOVE_LeftShift(gamma0xh, 31) | Convert
						.MOVE_RightUShift(gamma0xl, 1))
						^ (Convert.MOVE_LeftShift(gamma0xh, 24) | Convert
								.MOVE_RightUShift(gamma0xl, 8)) ^ (Convert
						.MOVE_LeftShift(gamma0xh, 25) | Convert.MOVE_RightUShift(
						gamma0xl, 7)));

				// 434469372,2038397261,16

				long gamma1xh = w.items[(i - 2) * 2];
				long gamma1xl = w.items[(i - 2) * 2 + 1];

				long gamma1h = (Convert.MOVE_LeftShift(gamma1xl, 13) | Convert
						.MOVE_RightUShift(gamma1xh, 19))
						^ (Convert.MOVE_LeftShift(gamma1xh, 3) | Convert
								.MOVE_RightUShift(gamma1xl, 29))
						^ Convert.MOVE_RightUShift(gamma1xh, 6);
				long gamma1l = ((Convert.MOVE_LeftShift(gamma1xh, 13) | Convert
						.MOVE_RightUShift(gamma1xl, 19))
						^ (Convert.MOVE_LeftShift(gamma1xl, 3) | Convert
								.MOVE_RightUShift(gamma1xh, 29)) ^ (Convert
						.MOVE_LeftShift(gamma1xh, 26) | Convert.MOVE_RightUShift(
						gamma1xl, 6)));

				long wr7h = w.items[(i - 7) * 2];
				long wr7l = w.items[(i - 7) * 2 + 1];

				long wr16h = w.items[(i - 16) * 2];
				long wr16l = w.items[(i - 16) * 2 + 1];
				// 0,0,0,1346456388,16
				wrl = gamma0l + wr7l;
				wrh = gamma0h
						+ wr7h
						+ (Convert.MOVE_RightUShift(wrl, 0) < Convert.MOVE_RightUShift(
								gamma0l, 0) ? 1 : 0);
				// 2038397261,434469372,16
				wrl += gamma1l;
				wrh += gamma1h
						+ (Convert.MOVE_RightUShift(wrl, 0) < Convert.MOVE_RightUShift(
								gamma1l, 0) ? 1 : 0);
				// 2038397261,434469372,16
				wrl += wr16l;

				wrh += wr16h
						+ (Convert.MOVE_RightUShift(wrl, 0) < Convert.MOVE_RightUShift(
								wr16l, 0) ? 1 : 0);

				// 3384853649
				// 1346456388

			}
			wrh = (int) (wrh | 0);
			wrl = (int) (wrl | 0);
			w.set(i * 2, Convert.get(wrh));
			w.set(i * 2 + 1, Convert.get(wrl));

			long chh = (eh & fh) ^ (~eh & gh);
			long chl = (el & fl) ^ (~el & gl);

			long majh = (ah & bh) ^ (ah & ch) ^ (bh & ch);
			long majl = (al & bl) ^ (al & cl) ^ (bl & cl);

			long sigma0h = (Convert.MOVE_LeftShift(al, 4) | Convert.MOVE_RightUShift(ah,
					28))
					^ (Convert.MOVE_LeftShift(ah, 30) | Convert.MOVE_RightUShift(al, 2))
					^ (Convert.MOVE_LeftShift(ah, 25) | Convert.MOVE_RightUShift(al, 7));
			long sigma0l = (Convert.MOVE_LeftShift(ah, 4) | Convert.MOVE_RightUShift(al,
					28))
					^ (Convert.MOVE_LeftShift(al, 30) | Convert.MOVE_RightUShift(ah, 2))
					^ (Convert.MOVE_LeftShift(al, 25) | Convert.MOVE_RightUShift(ah, 7));

			long sigma1h = (Convert.MOVE_LeftShift(el, 18) | Convert.MOVE_RightUShift(eh,
					14))
					^ (Convert.MOVE_LeftShift(el, 14) | Convert.MOVE_RightUShift(eh, 18))
					^ (Convert.MOVE_LeftShift(eh, 23) | Convert.MOVE_RightUShift(el, 9));
			long sigma1l = (Convert.MOVE_LeftShift(eh, 18) | Convert.MOVE_RightUShift(el,
					14))
					^ (Convert.MOVE_LeftShift(eh, 14) | Convert.MOVE_RightUShift(el, 18))
					^ (Convert.MOVE_LeftShift(el, 23) | Convert.MOVE_RightUShift(eh, 9));

			long krh = k.get(i * 2);
			long krl = k.get(i * 2 + 1);

			long t1l = hl + sigma1l;
			long t1h = hh
					+ sigma1h
					+ (Convert.MOVE_RightUShift(t1l, 0) < Convert.MOVE_RightUShift(hl, 0) ? 1
							: 0);

			t1l += chl;
			t1h += chh
					+ (Convert.MOVE_RightUShift(t1l, 0) < Convert
							.MOVE_RightUShift(chl, 0) ? 1 : 0);

			t1l += krl;
			t1h += krh
					+ (Convert.MOVE_RightUShift(t1l, 0) < Convert
							.MOVE_RightUShift(krl, 0) ? 1 : 0);

			t1l += wrl;
			t1h += wrh
					+ (Convert.MOVE_RightUShift(t1l, 0) < Convert
							.MOVE_RightUShift(wrl, 0) ? 1 : 0);

			long t2l = sigma0l + majl;
			long t2h = sigma0h
					+ majh
					+ (Convert.MOVE_RightUShift(t2l, 0) < Convert.MOVE_RightUShift(
							sigma0l, 0) ? 1 : 0);

			hh = gh;
			hl = gl;
			gh = fh;
			gl = fl;
			fh = eh;
			fl = el;

			el = (int) ((dl + t1l) | 0);
			eh = (int) ((dh + t1h + (Convert.MOVE_RightUShift(el, 0) < Convert
					.MOVE_RightUShift(dl, 0) ? 1 : 0)) | 0);

			dh = ch;
			dl = cl;
			ch = bh;
			cl = bl;
			bh = ah;
			bl = al;
			al = (int) ((t1l + t2l) | 0);
			ah = (int) ((t1h + t2h + (Convert.MOVE_RightUShift(al, 0) < Convert
					.MOVE_RightUShift(t1l, 0) ? 1 : 0)) | 0);

		}

		h0l = h.items[1] = (int) ((h0l + al) | 0);

		h.items[0] = (int) (h0h + ah + (Convert.MOVE_RightUShift(h0l, 0) < Convert
				.MOVE_RightUShift(al, 0) ? 1 : 0)) | 0;
		h1l = h.items[3] = (h1l + bl) | 0;
		h.items[2] = (int) (h1h + bh + (Convert.MOVE_RightUShift(h1l, 0) < Convert
				.MOVE_RightUShift(bl, 0) ? 1 : 0)) | 0;
		h2l = h.items[5] = (int) (h2l + cl) | 0;
		h.items[4] = (int) (h2h + ch + (Convert.MOVE_RightUShift(h2l, 0) < Convert
				.MOVE_RightUShift(cl, 0) ? 1 : 0)) | 0;
		h3l = h.items[7] = (int) (h3l + dl) | 0;
		h.items[6] = (int) (h3h + dh + (Convert.MOVE_RightUShift(h3l, 0) < Convert
				.MOVE_RightUShift(dl, 0) ? 1 : 0)) | 0;
		h4l = h.items[9] = (int) (h4l + el) | 0;
		h.items[8] = (int) (h4h + eh + (Convert.MOVE_RightUShift(h4l, 0) < Convert
				.MOVE_RightUShift(el, 0) ? 1 : 0)) | 0;
		h5l = h.items[11] = (int) (h5l + fl) | 0;
		h.items[10] = (int) (h5h + fh + (Convert.MOVE_RightUShift(h5l, 0) < Convert
				.MOVE_RightUShift(fl, 0) ? 1 : 0)) | 0;
		h6l = h.items[13] = (int) (h6l + gl) | 0;
		h.items[12] = (int) (h6h + gh + (Convert.MOVE_RightUShift(h6l, 0) < Convert
				.MOVE_RightUShift(gl, 0) ? 1 : 0)) | 0;
		h7l = h.items[15] = (int) (h7l + hl) | 0;
		h.items[14] = (int) (h7h + hh + (Convert.MOVE_RightUShift(h7l, 0) < Convert
				.MOVE_RightUShift(hl, 0) ? 1 : 0)) | 0;

	}
}
