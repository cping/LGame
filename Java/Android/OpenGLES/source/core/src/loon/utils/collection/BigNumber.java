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
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;

import loon.LSystem;
import loon.utils.StringUtils;

public class BigNumber {

	private final static String base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	class Montgomery {

		BigNumber m;

		long mt, mt2, mp, mpl, mph, um;

		public Montgomery(BigNumber m) {
			this.m = m;
			this.mt = m.limbs.length;
			this.mt2 = this.mt * 2;
			this.mp = m.invDigit();
			this.mpl = this.mp & 0x7fff;
			this.mph = this.mp >> 15;
			this.um = (1 << (m.radix - 15)) - 1;
		}

		public BigNumber reduce(BigNumber x) {
			long radixMod = x.radixMask + 1;
			while (x.limbs.length <= this.mt2) {
				x.limbs.set(x.limbs.length, 0);
			}
			for (int i = 0; i < this.mt; ++i) {
				long j = x.limbs.get(i) & 0x7fff;
				long u0 = (j * this.mpl + (((j * this.mph + (x.limbs.get(i) >> 15)
						* this.mpl) & this.um) << 15))
						& x.radixMask;
				j = i + this.mt;
				x.limbs.set(
						(int) j,
						x.limbs.get((int) j)
								+ this.m.am(0, u0, x, i, (long) 0, this.mt));
				while (x.limbs.get((int) j) >= radixMod) {
					x.limbs.set((int) j, x.limbs.get((int) j) - radixMod);
					long idx = ++j;
					x.limbs.set((int) idx, x.limbs.get((int) idx) + 1);
				}
			}
			x.trim();
			x = x.shiftRight(this.mt * this.m.radix);
			if (x.greaterEquals(this.m) > 0) {
				x = x.sub(this.m);
			}
			return x.trim().normalize().reduce();
		}

		public BigNumber square(BigNumber x) {
			return this.reduce(x.square());
		}

		public BigNumber multiply(BigNumber x, BigNumber y) {
			return this.reduce(x.mul(y));
		}

		public BigNumber convert(BigNumber x) {
			return x.abs().shiftLeft(this.mt * this.m.radix).mod(this.m);
		}

		public BigNumber revert(BigNumber x) {
			return this.reduce(x.copy());
		};

	}

	public static String encodeURIComponent(String s) {
		try {
			return URLEncoder.encode(s, LSystem.encoding)
					.replaceAll("\\+", "%20").replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'").replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
		} catch (Exception e) {
			return s;
		}
	}

	public static String decodeURIComponent(String s) {
		try {
			s = s.replaceAll("%20", "\\+").replaceAll("!", "\\%21")
					.replaceAll("'", "\\%27").replaceAll("(", "\\%28")
					.replaceAll(")", "\\%29").replaceAll("~", "\\%7E");

			return URLDecoder.decode(s, LSystem.encoding);
		} catch (Exception e) {
			return s;
		}
	}

	public final static BigNumber ZERO = new BigNumber(0);
	public final static BigNumber ONE = new BigNumber(1);

	public long radix = 24;
	public long maxMul = 8;
	public LongArray limbs;
	public long placeVal = (long) Math.pow(2, radix);
	public BigDecimal ipv = new BigDecimal(1d / placeVal);
	public long radixMask = (1 << radix) - 1;
	public long exponent;

	public static BigNumber bn(Object it) {
		return new BigNumber(it);
	}

	public BigNumber(Object it) {
		initWith(it);
	}

	public BigNumber() {
		initWith(0);
	}

	public LongArray get() {
		return limbs;
	}

	public BigNumber copy() {
		return new BigNumber(this);
	}

	public String toString(boolean flag) {
		this.fullReduce();
		String out = "";
		int i;
		String s;
		LongArray l = this.limbs;
		for (i = 0; i < this.limbs.length; i++) {
			s = BigInteger.valueOf(l.get(i)).toString(16);
			while (i < this.limbs.length - 1 && s.length() < 6) {
				s = "0" + s;
			}
			out = s + out;
		}
		return flag ? "0x" + out : out;
	}

	public String toString() {
		return toString(true);
	}

	public BigNumber normalize() {
		int i = 0;
		BigDecimal ipv = this.ipv;
		long carry = 0, pv = this.placeVal, l, m, ll = limbs.length, mask = this.radixMask;
		for (i = 0; i < ll || (carry != 0 && carry != -1); i++) {
			l = Convert.OR(limbs.items[i], 0).longValue() + carry;
			m = limbs.items[i] = l & mask;
			carry = ipv.multiply(BigDecimal.valueOf(l - m)).longValue();
		}
		if (carry == -1) {
			limbs.items[i - 1] -= pv;
		}
		return this;
	}

	public boolean equals(BigNumber that) {
		int difference = 0, i;
		this.fullReduce();
		that.fullReduce();
		for (i = 0; i < this.limbs.length || i < that.limbs.length; i++) {
			difference = (int) (difference | this.getLimb(i) ^ that.getLimb(i));
		}
		return (difference == 0);
	}

	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		BigNumber newthat = new BigNumber(that);
		return equals(newthat);
	}

	public long greaterEquals(BigNumber that) {
		int less = 0, greater = 0, i;
		long a, b;
		i = Math.max(this.limbs.length, that.limbs.length) - 1;
		for (; i >= 0; i--) {
			a = this.getLimb(i);
			b = that.getLimb(i);
			int at = (int) ((b - a) & ~less);
			greater = (int) (greater | at);
			int bt = (int) ((a - b) & ~greater);
			less = (int) (less | bt);
		}
		return Convert.MOVE_RightUShift((greater | ~less), 31);
	}

	public BigNumber[] divRem(BigNumber that) {
		BigNumber thisa = this.abs(), thata = that.abs(), quot = new BigNumber(
				0);
		int ci = 0;
		if (!(thisa.greaterEquals(thata) > 0)) {
			return new BigNumber[] { new BigNumber(0), this.copy() };
		} else if (thisa.equals(thata)) {
			return new BigNumber[] { new BigNumber(1), new BigNumber(0) };
		}

		for (; thisa.greaterEquals(thata) > 0; ci++) {
			thata.doubleM();
		}
		for (; ci > 0; ci--) {
			quot.doubleM();
			thata.halveM();
			if (thisa.greaterEquals(thata) > 0) {
				quot.addM(new BigNumber(1));
				thisa.subM(that).normalize();
			}
		}
		return new BigNumber[] { quot, thisa };
	}

	public BigNumber divRound(BigNumber that) {
		BigNumber[] dr = this.divRem(that);
		BigNumber quot = dr[0];
		BigNumber rem = dr[1];
		if (rem.doubleM().greaterEquals(that) > 0) {
			quot.addM(new BigNumber(1));
		}
		return quot;
	}

	public BigNumber div(BigNumber that) {
		BigNumber[] dr = this.divRem(that);
		return dr[0];
	}

	public BigNumber addM(BigNumber that) {
		int i = 0;
		LongArray l = this.limbs, ll = that.limbs;
		for (i = l.length; i < ll.length; i++) {
			l.set(i, 0);
		}
		for (i = 0; i < ll.length; i++) {
			l.set(i, l.get(i) + ll.get(i));
		}
		return this;
	}

	public BigNumber doubleM() {
		int i;
		long carry = 0, tmp, r = this.radix, m = this.radixMask;
		LongArray l = this.limbs;
		for (i = 0; i < l.length; i++) {
			tmp = l.get(i);
			tmp = tmp + tmp + carry;
			l.set(i, tmp & m);
			carry = tmp >> r;
		}
		if (carry > 0) {
			l.add(carry);
		}
		return this;
	}

	public BigNumber halveM() {
		int i;
		long carry = 0, tmp, r = this.radix;
		LongArray l = this.limbs;
		for (i = l.length - 1; i >= 0; i--) {
			tmp = l.get(i);
			l.set(i, (tmp + carry) >> 1);
			carry = (tmp & 1) << r;
		}
		if (!(l.get(l.length - 1) > 0)) {
			l.pop();
		}
		return this;
	}

	public BigNumber subM(BigNumber that) {
		int i;
		LongArray l = this.limbs, ll = that.limbs;
		for (i = l.length; i < ll.length; i++) {
			l.set(i, 0);
		}

		for (i = 0; i < ll.length; i++) {
			l.set(i, l.get(i) - ll.get(i));
		}
		return this;
	}

	public BigNumber trim() {
		LongArray l = this.limbs;
		long p;
		do {
			p = l.pop();
		} while (l.length > 0 && p == 0);
		l.push(p);
		return this;
	}

	public BigNumber mod(BigNumber that) {
		boolean neg = !(this.greaterEquals(BigNumber.ZERO) > 0);
		that = new BigNumber(that).normalize();
		BigNumber out = new BigNumber(this).normalize();
		int ci = 0;
		if (neg) {
			out = (new BigNumber(0)).subM(out).normalize();
		}
		for (; out.greaterEquals(that) > 0; ci++) {
			that.doubleM();
		}
		if (neg) {
			out = that.sub(out).normalize();
		}
		for (; ci > 0; ci--) {
			that.halveM();
			if (out.greaterEquals(that) > 0) {
				out.subM(that).normalize();
			}
		}
		return out.trim();
	}

	public BigNumber add(BigNumber that) {
		return this.copy().addM(that);
	}

	public BigNumber sub(BigNumber that) {
		return this.copy().subM(that);
	}

	public BigNumber inverseMod(BigNumber p) {
		BigNumber a = new BigNumber(1), b = new BigNumber(0), x = new BigNumber(
				this), y = new BigNumber(p), tmp;
		int i, nz = 1;
		if (!((p.limbs.get(0) & 1) > 0)) {
			throw new RuntimeException("inverseMod: p must be odd");
		}
		do {
			if ((x.limbs.get(0) & 1) > 0) {
				if (!(x.greaterEquals(y) > 0)) {
					tmp = x;
					x = y;
					y = tmp;
					tmp = a;
					a = b;
					b = tmp;
				}
				x.subM(y);
				x.normalize();
				if (!(a.greaterEquals(b) > 0)) {
					a.addM(p);
				}
				a.subM(b);
			}

			x.halveM();
			if ((a.limbs.get(0) & 1) > 0) {
				a.addM(p);
			}
			a.normalize();
			a.halveM();

			for (i = nz = 0; i < x.limbs.length; i++) {
				nz = (int) (nz | x.limbs.get(i));
			}
		} while (nz > 0);

		if (!y.equals(1)) {
			throw new RuntimeException(
					"inverseMod: p and x must be relatively prime");
		}

		return b;
	}

	public BigNumber mul(BigNumber that) {
		int i, j;
		LongArray a = this.limbs, b = that.limbs;
		int al = a.length, bl = b.length;
		BigNumber out = new BigNumber();
		LongArray c = out.limbs;
		long ai, ii = this.maxMul;

		for (i = 0; i < this.limbs.length + that.limbs.length + 1; i++) {
			c.set(i, 0);
		}

		for (i = 0; i < al; i++) {
			ai = a.get(i);
			for (j = 0; j < bl; j++) {
				c.set(i + j, c.get(i + j) + ai * b.get(j));
			}
			if (!(--ii > 0)) {

				ii = this.maxMul;
				out.cnormalize();
			}
		}

		return out.cnormalize().reduce();
	}

	public BigNumber square() {
		return this.mul(this);
	}

	public BigNumber power(BigNumber l) {
		LongArray nl = l.normalize().limbs;
		int i, j;
		BigNumber out = new BigNumber(BigNumber.ONE), pow = this;
		for (i = 0; i < nl.length; i++) {
			for (j = 0; j < this.radix; j++) {
				if ((nl.get(i) & (1 << j)) > 0) {
					out = out.mul(pow);
				}
				pow = pow.square();
			}
		}
		return out;
	}

	public BigNumber reduce() {
		return this;
	}

	public BigNumber mulmod(BigNumber that, BigNumber N) {
		return this.mod(N).mul(that.mod(N)).mod(N);
	}

	public long bitLength() {
		this.fullReduce();
		long out = this.radix * (this.limbs.length - 1), b = this.limbs
				.get(this.limbs.length - 1);
		for (; b > 0; b >>>= 1) {
			out++;
		}
		return out + 7 & -8;
	}

	public LongArray toBits() {
		return toBits(0);
	}

	public LongArray toBits(long len) {
		this.fullReduce();
		len = Convert.OR(Convert.OR(len, this.exponent).longValue(), this.bitLength())
				.longValue();

		int i = (int) Math.floor((len - 1) / 24);

		long e = (len + 7 & -8) % Convert.OR(this.radix, this.radix).longValue();
		long c = BitArray.partial(e, this.getLimb(i), 0);

		LongArray out = new LongArray(new long[] { c });
		for (i--; i >= 0; i--) {
			long a = BitArray.partial(Math.min(this.radix, len),
					this.getLimb(i));
			long[] arrays = new long[] { a };
			out = BitArray.concat(out, new LongArray(arrays));

			len -= this.radix;
		}
		return out;
	}

	public int sign() {
		return this.greaterEquals(ZERO) > 0 ? 1 : -1;
	}

	public BigNumber shiftRight(long that) {
		that = +that;
		if (that < 0) {
			return this.shiftLeft(that);
		}

		BigNumber a = new BigNumber(this);

		while (that >= this.radix) {
			a.limbs.shift();
			that -= this.radix;
		}
		while ((that--) > 0) {
			a.halveM();
		}
		return a;
	}

	public BigNumber shiftLeft(long that) {
		that = +that;
		if (that < 0) {
			return shiftRight(that);
		}

		BigNumber a = new BigNumber(this);

		while (that >= this.radix) {
			a.limbs.unshift(0);
			that -= this.radix;
		}

		while ((that--) > 0) {
			a.doubleM();
		}

		return a;
	}

	public long toNumber() {
		return this.limbs.get(0) | 0;
	}

	public long testBit(int bitIndex) {
		int limbIndex = (int) Math.floor((double) bitIndex
				/ (double) this.radix);
		int bitIndexInLimb = (int) (bitIndex % this.radix);
		if (limbIndex >= this.limbs.length) {
			return 0;
		}
		return Convert.MOVE_RightUShift(this.limbs.get(limbIndex), bitIndexInLimb) & 1;
	}

	public BigNumber setBitM(int bitIndex) {
		int limbIndex = (int) Math.floor(bitIndex / this.radix);
		int bitIndexInLimb = (int) (bitIndex % this.radix);
		while (limbIndex >= this.limbs.length) {
			this.limbs.push(0);
		}
		this.limbs.set(limbIndex, this.limbs.get(limbIndex)
				| 1 << bitIndexInLimb);
		this.cnormalize();
		return this;
	}

	public int modInt(int n) {
		return (int) (this.toNumber() % n);
	}

	public int jacobi(BigNumber o) {
		BigNumber a = this;

		BigNumber that = new BigNumber(o);
		if (that.sign() == -1) {
			return -1;
		}
		if (a.equals(BigNumber.ZERO)) {
			return 0;
		}
		if (a.equals(BigNumber.ONE)) {
			return 1;
		}

		int s = 0;

		int e = 0;
		while (!(a.testBit(e) > 0)) {
			e++;
		}

		BigNumber a1 = a.shiftRight(e);

		if ((e & 1) == 0) {
			s = 1;
		} else {
			int residue = that.modInt(8);
			if (residue == 1 || residue == 7) {
				s = 1;
			} else if (residue == 3 || residue == 5) {
				s = -1;
			}
		}
		if (that.modInt(4) == 3 && a1.modInt(4) == 3) {
			s = -s;
		}

		if (a1.equals(BigNumber.ONE)) {
			return s;
		} else {
			return s * that.mod(a1).jacobi(a1);
		}
	}

	public BigNumber powermod(BigNumber x, BigNumber N) {
		BigNumber result = new BigNumber(1), a = new BigNumber(this), k = new BigNumber(
				x);
		for (;;) {
			if ((k.limbs.get(0) & 1) > 0) {
				result = result.mulmod(a, N);
			}
			k.halveM();
			if (k.equals(0)) {
				break;
			}
			a = a.mulmod(a, N);
		}
		return result.normalize().reduce();
	}

	public BigNumber fromBits(LongArray bits) {
		BigNumber out = new BigNumber();
		LongArray words = new LongArray();
		long l = Math.min(Convert.OR(this.bitLength(), 0x100000000l).longValue(),
				BitArray.bitLength(bits)), e = l
				% Convert.OR(radix, radix).longValue();
		words.set(0, BitArray.extract(bits, 0, (int) e));
		for (; e < l; e += radix) {
			words.unshift(BitArray.extract(bits, (int) e, (int) radix));
		}
		out.limbs = words;
		return out;
	}

	public BigNumber cnormalize() {
		int i = 0;
		BigDecimal ipv = this.ipv;
		long carry = 0, l, m;
		LongArray limbs = this.limbs;
		int ll = limbs.length;
		long mask = this.radixMask;
		for (i = 0; i < ll - 1; i++) {
			l = limbs.items[i] + carry;
			m = limbs.items[i] = l & mask;
			carry = ipv.multiply(BigDecimal.valueOf((l - m))).longValue();
		}
		limbs.set(i, limbs.get(i) + carry);
		return this;
	}

	public long getLimb(int i) {
		return (i >= this.limbs.length) ? 0 : this.limbs.get(i);
	}

	public BigNumber fullReduce() {
		return this.normalize();
	}

	public long invDigit() {
		long radixMod = 1 + this.radixMask;

		if (this.limbs.length < 1) {
			return 0;
		}
		long x = this.limbs.get(0);
		if ((x & 1) == 0) {
			return 0;
		}
		long y = x & 3;
		y = (y * (2 - (x & 0xf) * y)) & 0xf;
		y = (y * (2 - (x & 0xff) * y)) & 0xff;
		y = (y * (2 - (((x & 0xffff) * y) & 0xffff))) & 0xffff;
		y = (y * (2 - x * y % radixMod)) % radixMod;
		return (y > 0) ? radixMod - y : -y;
	}

	public BigNumber neg() {
		return ZERO.sub(this);
	}

	public BigNumber abs() {
		if (this.sign() == -1) {
			return this.neg();
		} else
			return this;
	}

	public long nbits(long x) {
		long r = 1, t;
		if ((t = Convert.MOVE_RightUShift(x, 16)) != 0) {
			x = t;
			r += 16;
		}
		if ((t = x >> 8) != 0) {
			x = t;
			r += 8;
		}
		if ((t = x >> 4) != 0) {
			x = t;
			r += 4;
		}
		if ((t = x >> 2) != 0) {
			x = t;
			r += 2;
		}
		if ((t = x >> 1) != 0) {
			x = t;
			r += 1;
		}
		return r;
	}

	public long am(int i, long x, BigNumber w, int j, long c, long n) {
		long xl = x & 0xfff, xh = x >> 12;
		while (--n >= 0) {
			long l = this.limbs.get(i) & 0xfff;
			long h = this.limbs.get(i++) >> 12;
			long m = xh * l + h * xl;
			l = xl * l + ((m & 0xfff) << 12) + w.limbs.get(j) + c;
			c = (l >> 24) + (m >> 12) + xh * h;
			w.limbs.set(j++, (int) (l & 0xffffffl));
		}
		return c;
	}

	public void initWith(Object it) {
		if (it == null) {
			this.limbs = new LongArray();
			this.normalize();
			return;
		}
		int i = 0, k;
		if (it instanceof BigNumber) {
			BigNumber its = (BigNumber) it;
			this.limbs = its.limbs.slice(0);
		} else if (it instanceof String) {
			String itStr = (String) it;
			if (itStr.startsWith("0x")) {
				itStr = itStr.substring(2, itStr.length());
			}
			this.limbs = new LongArray();
			k = (int) (this.radix / 4);
			for (i = 0; i < itStr.length(); i += k) {
				int v = Integer.parseInt(
						itStr.substring(Math.max(itStr.length() - i - k, 0),
								itStr.length() - i), 16);
				this.limbs.add(v);
			}
		} else if (it instanceof LongArray) {
			LongArray itArray = (LongArray) it;
			this.limbs = itArray.slice(0);
		} else if (it instanceof Number) {
			this.limbs = new LongArray(new long[] { ((Number) it).longValue() });
			this.normalize();
		} else if (it instanceof byte[]) {
			this.limbs = new LongArray();
			byte[] buffer = (byte[]) it;
			ArrayByte ins = new ArrayByte(buffer);
			for (int j = 0; j < buffer.length; j += 4) {
				limbs.add(ins.readInt());
			}
			ins.dispose();
			this.normalize();
		} else if (it instanceof ArrayByte) {
			this.limbs = new LongArray();
			ArrayByte buffer = (ArrayByte) it;
			int c = -1;
			for (; (c = buffer.readByte()) != -1;) {
				limbs.add(c);
			}
			buffer.dispose();
			this.normalize();
		} else {
			this.limbs = new LongArray(new long[] { 0 });
		}
	}

	public BigNumber powermodMontgomery(BigNumber e, BigNumber m) {
		long i = e.bitLength(), k = 0;
		BigNumber r = new BigNumber(BigNumber.ONE);

		if (i <= 0) {
			return r;
		} else if (i < 18) {
			k = 1;
		} else if (i < 48) {
			k = 3;
		} else if (i < 144) {
			k = 4;
		} else if (i < 768) {
			k = 5;
		} else {
			k = 6;
		}

		if (i < 8 || !(m.testBit(0) > 0)) {
			return this.powermod(e, m);
		}

		Montgomery z = new Montgomery(m);

		e.trim().normalize();

		Array<BigNumber> g = new Array<BigNumber>();
		long n = 3, k1 = (int) (k - 1), km = (1 << k) - 1;
		g.set(1, z.convert(this));
		if (k > 1) {
			BigNumber g2 = z.square(g.get(1));
			while (n <= km) {
				g.set((int) n, z.multiply(g2, g.get((int) n - 2)));
				n += 2;
			}
		}

		int j = e.limbs.length - 1;
		long w;
		boolean is1 = true;
		BigNumber r2 = new BigNumber(), t;
		i = nbits(e.limbs.get(j)) - 1;
		while (j >= 0) {
			if (i >= k1)
				w = (e.limbs.get(j) >> (i - k1)) & km;
			else {
				w = (e.limbs.get(j) & ((1 << (i + 1)) - 1)) << (k1 - i);
				if (j > 0) {
					w |= e.limbs.get(j - 1) >> (this.radix + i - k1);
				}
			}

			n = k;
			while ((w & 1) == 0) {
				w >>= 1;
				--n;
			}
			if ((i -= n) < 0) {
				i += this.radix;
				--j;
			}
			if (is1) {
				r = g.get((int) w).copy();
				is1 = false;
			} else {
				while (n > 1) {
					r2 = z.square(r);
					r = z.square(r2);
					n -= 2;
				}
				if (n > 0)
					r2 = z.square(r);
				else {
					t = r;
					r = r2;
					r2 = t;
				}
				r = z.multiply(r2, g.get((int) w));
			}

			while (j >= 0 && (e.limbs.get(j) & (1 << i)) == 0) {
				r2 = z.square(r);
				t = r;
				r = r2;
				r2 = t;
				if (--i < 0) {
					i = this.radix - 1;
					--j;
				}
			}
		}
		return z.revert(r);
	}

	static LongArray randomWords(int nwords, int paranoia) {
		LongArray out = new LongArray();
		int i;
		for (i = 0; i < nwords; i += 4) {
			out.push(LSystem.random.nextInt());
			out.push(LSystem.random.nextInt());
			out.push(LSystem.random.nextInt());
			out.push(LSystem.random.nextInt());
		}
		return out.slice(0, nwords);
	}

	public static BigNumber random(BigNumber modulus, int paranoia) {
		LongArray words;
		int i, l = modulus.limbs.length;
		long m = modulus.limbs.get(l - 1) + 1;
		for (;;) {
			do {
				words = randomWords(l, paranoia);
				if (words.get(l - 1) < 0) {
					words.set(l - 1, (words.get(l - 1) + 0x100000000l));
				}
			} while (Math.floor((double) words.get(l - 1) / (double) m) == (Math
					.floor((double) 0x100000000l / (double) m)));
			words.set(l - 1, ((words.get(l - 1) % m)));
			for (i = 0; i < l - 1; i++) {
				words.set(i, words.get(i) & modulus.radixMask);
			}
			BigNumber out = new BigNumber(words);
			if (!(out.greaterEquals(modulus) > 0)) {
				return out;
			}
		}
	}

	public static String hex_fromBits(LongArray arr) {
		String out = "";
		int i;
		for (i = 0; i < arr.length; i++) {
			out += Long.toHexString(((int) (arr.get(i) | 0) + 0xF00000000000l))
					.substring(4);
		}
		return out.substring(0, (int) BitArray.bitLength(arr) / 4);
	}

	public static LongArray hex_toBits(String str) {
		int i = 0;
		LongArray out = new LongArray();
		int len;
		str = str.replace("0x", "");
		len = str.length();
		str = str + "00000000";
		for (i = 0; i < str.length(); i += 8) {
			String res = str.substring(i, i + 8);
			out.push(new BigInteger(res, 16).intValue() ^ 0);
		}
		return BitArray.clamp(out, len * 4);
	}

	public static String utf8_fromBits(LongArray arr) {
		String out = "";
		int i = 0;
		long bl = BitArray.bitLength(arr);
		long tmp = 0;
		for (i = 0; i < bl / 8; i++) {
			if ((i & 3) == 0) {
				tmp = arr.get(i / 4);
			}
			out += String.valueOf(Convert.MOVE_LeftShift(tmp, 24));
			tmp <<= 8;
		}
		return decodeURIComponent(StringUtils.escape(out));
	}

	public static LongArray utf8_toBits(String str) {
		str = StringUtils.unescape(encodeURIComponent(str));
		LongArray out = new LongArray();
		int i, tmp = 0;
		for (i = 0; i < str.length(); i++) {
			tmp = tmp << 8 | str.charAt(i);
			if ((i & 3) == 3) {
				out.push(tmp);
				tmp = 0;
			}
		}
		if ((i & 3) > 0) {
			out.push(BitArray.partial(8 * (i & 3), tmp));
		}
		return out;
	}

	public static LongArray bytes_fromBits(LongArray arr) {
		LongArray out = new LongArray();
		long bl = BitArray.bitLength(arr);
		int i = 0;
		long tmp = 0;
		for (i = 0; i < bl / 8; i++) {
			if ((i & 3) == 0) {
				tmp = arr.get(i / 4);
			}
			out.push(Convert.MOVE_RightUShift(tmp, 24));
			tmp <<= 8;
		}
		return out;
	}

	public static LongArray bytes_toBits(byte[] bytes) {
		LongArray out = new LongArray();
		int i = 0;
		long tmp = 0;
		for (i = 0; i < bytes.length; i++) {
			tmp = (int) (tmp << 8 | bytes[i]);
			if ((i & 3) == 3) {
				out.push(tmp);
				tmp = 0;
			}
		}
		if ((i & 3) > 0) {
			out.push(BitArray.partial(8 * (i & 3), tmp));
		}
		return out;
	}

	public static String base64_fromBits(LongArray arr) {
		return base64_fromBits(arr, false, false);
	}

	public static String base64_fromBits(LongArray arr, boolean _noEquals,
			boolean _url) {
		String out = "";
		int i, bits = 0;
		String c = base64_chars;
		long ta = 0, bl = BitArray.bitLength(arr);
		if (_url) {
			c = c.substring(0, 62) + "-_";
		}
		for (i = 0; out.length() * 6 < bl;) {
			out += c.charAt((int) (Convert.MOVE_RightUShift(
					(ta ^ Convert.MOVE_RightUShift(arr.get(i), bits)), 26)));
			if (bits < 6) {
				ta = arr.get(i) << (6 - bits);
				bits += 26;
				i++;
			} else {
				ta <<= 6;
				bits -= 6;
			}
		}
		while (((out.length() & 3) > 0) && !_noEquals) {
			out += "=";
		}
		return out;
	}

	public static LongArray base64_toBits(String str) {
		return base64_toBits(str, false);
	}

	public static LongArray base64_toBits(String str, boolean _url) {
		str = str.replace("=", "");
		LongArray out = new LongArray();
		int i, bits = 0;
		String c = base64_chars;
		long ta = 0, x;
		if (_url) {
			c = c.substring(0, 62) + "-_";
		}
		for (i = 0; i < str.length(); i++) {
			x = c.indexOf(str.charAt(i));
			if (x < 0) {
				throw new RuntimeException("this isn't base64!");
			}
			if (bits > 26) {
				bits -= 26;
				out.push(ta ^ Convert.MOVE_RightUShift(x, bits));
				ta = (int) (x << (32 - bits));
			} else {
				bits += 6;
				ta ^= (int) (x << (32 - bits));
			}
		}
		if ((bits & 56) > 0) {
			out.push(BitArray.partial(bits & 56, ta, 1));
		}
		return out;
	}

	public static LongArray fdh(Object d, long bytelen) {
		LongArray data = null;
		if (d instanceof String) {
			data = utf8_toBits((String) d);
		} else if (d instanceof LongArray) {
			data = (LongArray) d;
		}

		long bitlen = bytelen << 3;
		int counter = 0;
		LongArray output = new LongArray();

		while (BitArray.bitLength(output) < bitlen) {
			LongArray res = BitArray.concat(new LongArray(
					new long[] { counter }), data);
			LongArray hash = SHA512.hash(res);
			output = BitArray.concat(output, hash);
			counter++;
		}
		output = BitArray.clamp(output, bitlen);

		return output;
	}

	public static String signature(String secret, LongArray data) {
		HMAC hmac = new HMAC(hex_toBits(secret));
		return hex_fromBits(hmac.mac(data));
	}

	public static String hashSha512(LongArray data) {
		return hex_fromBits(SHA512.hash(data));
	}
}
