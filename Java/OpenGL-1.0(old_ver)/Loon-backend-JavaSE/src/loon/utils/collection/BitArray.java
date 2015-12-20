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

import loon.utils.MathUtils;

public class BitArray {

	public static long extract(LongArray a, int bstart, int blength) {
		long x;
		int sh = (int) Math.floor((-bstart - blength) & 31);
		if (((bstart + blength - 1 ^ bstart) & -32) > 0) {
			x = Convert.MOVE_LeftShift(a.get(bstart / (int) (32 | 0)), (32 - sh))
					^ Convert.MOVE_RightUShift(a.get(bstart / 32 + (int) (1 | 0)),
							sh);
		} else {
			x = Convert.MOVE_LeftShift(a.get(bstart / (int) (32 | 0)), sh);
		}
		return x & ((1 << blength) - 1);
	}

	public static LongArray clamp(LongArray a, long len) {
		if (a.length * 32 < len) {
			return a;
		}
		a = a.slice(0, (int) Math.ceil(len / 32));
		int l = a.length;
		len = len & 31;
		if (l > 0 && len > 0) {
			a.set(l - 1,
					partial(len, a.get(l - 1) & 0x80000000 >> (len - 1), 1));
		}
		return a;
	}

	public static LongArray bitSlice(LongArray a, int bend) {
		return bitSlice(a, bend, bend * 32);
	}

	public static LongArray bitSlice(LongArray a, int bstart, int bend) {
		a = _shiftRight(a.slice(bstart / 32), 32 - (bstart & 31), 0, null)
				.slice(1);
		return (bend == 0) ? a : clamp(a, bend - bstart);
	}

	public static LongArray concat(LongArray a1, LongArray a2) {
		if (a1.length == 0 || a2.length == 0) {
			return a1.concat(a2);
		}
		long last = a1.get(a1.length - 1), shift = getPartial(last);
		// ok
		if (shift == 32) {
			return a1.concat(a2);
		} else {
			return _shiftRight(a2, shift, (int) last | 0,
					a1.slice(0, a1.length - 1));
		}
	}

	public static long bitLength(LongArray a) {
		int l = a.length;
		long x;
		if (l == 0) {
			return 0;
		}
		x = a.get(l - 1);
		return (l - 1) * 32 + getPartial(x);
	}

	public static long partial(long len, long x) {
		return partial(len, x, 0);
	}

	public static long partial(long len, long x, int _end) {
		if (len == 32) {
			return x;
		}

		return ((_end > 0 ? (int) x | 0 : (int) x << (32 - len)) + len * 0x10000000000l);
	}

	public static LongArray _shiftRight(LongArray a, int shift) {
		return _shiftRight(a, shift, 0, null);
	}

	public static LongArray _shiftRight(LongArray a, long shift, long carry,
			LongArray out) {
		int i;
		long last2 = 0, shift2;
		if (out == null) {
			out = new LongArray();
		}

		for (; shift >= 32; shift -= 32) {
			out.push(carry);
			carry = 0;
		}

		if (shift == 0) {
			return out.concat(a);
		}

		for (i = 0; i < a.length; i++) {
			out.push((int) (carry | Convert.MOVE_RightUShift(a.get(i), (int) shift)));
			carry = a.get(i) << (32 - shift);
		}

		last2 = a.length > 0 ? a.get(a.length - 1) : 0;

		shift2 = getPartial(last2);
		out.push(partial(shift + shift2 & 31, (shift + shift2 > 32) ? carry
				: out.pop(), 1));

		return out;
	}

	public static long getPartial(double x) {
		BigDecimal intx = new BigDecimal(MathUtils.round(x / 0x10000000000l));
		return Convert.OR(intx.longValue(), 32).longValue();
	}

	public static LongArray _xor4(LongArray x, LongArray y) {
		return new LongArray(new long[] { x.get(0) ^ y.get(0),
				x.get(1) ^ y.get(1), x.get(2) ^ y.get(2), x.get(3) ^ y.get(3) });
	}

	public static LongArray byteswapM(LongArray a) {
		int i;
		long v, m = 0xff00;
		for (i = 0; i < a.length; ++i) {
			v = a.get(i);
			a.set(i, Convert.MOVE_RightUShift(v, 24)
					| (Convert.MOVE_RightUShift(v, 8) & m) | ((v & m) << 8)
					| (v << 24));
		}
		return a;
	}
}
