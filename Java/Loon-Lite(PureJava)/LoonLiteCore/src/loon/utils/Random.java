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

/**
 * 一个简单的[伪随机]数值生成用类,若运行环境没有Random,则loon的随机数生成会使用此类实现.
 * 如果想获得一个[高重复率]的[伪随机]生成器也可以直接用这个……
 *
 */
public class Random {

	private long _lowerMask = 0x7fffffff;
	private long _upperMask = 0x80000000;
	private long _bitmask32 = 0xffffffff;

	private long _w = 32;

	private int _n = 624;

	private long _m = 397;

	private long _a = 0x9908b0df;

	private long _u = 11;
	private long _s = 7;
	private long _b = 0x9d2c5680;
	private long _t = 15;
	private long _c = 0xefc60000;
	private long _l = 18;
	private long _f = 1812433253;

	private LongArray _mt;

	private int _index;

	public Random() {
		this(0);
	}

	public Random(long seed) {
		this._mt = new LongArray(this._n);
		if (seed != 0) {
			this._mt.set(0, seed);
		} else {
			this._mt.set(0, (TimeUtils.nanoTime()) >>> 0);
		}
		for (int i = 1; i < this._n; i++) {
			long s = this._mt.get(i - 1) ^ (this._mt.get(i - 1) >>> (this._w - 2));
			this._mt.set(i, (((this._f * ((s & 0xffff0000) >>> 16)) << 16) + this._f * (s & 0xffff) + i) >>> 0);
		}
		this._index = this._n;
	}

	private void update() {
		long[] mag01 = { 0x0, this._a };
		long y = 0;
		int i = 0;
		for (; i < this._n - this._m; i++) {
			y = (this._mt.get(i) & this._upperMask) | (this._mt.get(i + 1) & this._lowerMask);
			this._mt.set(i, this._mt.get((int) (i + this._m)) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & _bitmask32));
		}
		for (; i < this._n - 1; i++) {
			y = (this._mt.get(i) & this._upperMask) | (this._mt.get(i + 1) & this._lowerMask);
			this._mt.set(i,
					this._mt.get((int) (i + (this._m - this._n))) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & _bitmask32));
		}
		y = (this._mt.get(this._n - 1) & this._upperMask) | (this._mt.get(0) & this._lowerMask);
		this._mt.set(this._n - 1,
				this._mt.get((int) (this._m - 1)) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & _bitmask32));

		this._index = 0;
	}

	public boolean nextBoolean() {
		return nextInt(0, 1) != 0;
	}

	public int nextInt(int min, int max) {
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * this.nextFloat() + min)), min, max);
	}

	public long nextLong(long min, long max) {
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * this.nextFloat() + min)), min, max);
	}

	public double nextDouble(double min, double max) {
		return MathUtils.clamp(MathUtils.floor((max - min + 1) * this.nextDouble() + min), min, max);
	}

	public float nextFloat(float min, float max) {
		return MathUtils.clamp((max - min) * this.nextFloat() + min, min, max);
	}

	public float nextFloat() {
		return this.nextInt() * (1f / 4294967296f);
	}

	public double nextDouble() {
		return this.nextLong() * (1.0 / 4294967296.0);
	}

	public int nextInt() {
		return (int) nextLong();
	}

	public long nextLong() {
		if (this._index >= this._n) {
			this.update();
		}

		long y = this._mt.get(this._index++);

		y ^= y >>> this._u;
		y ^= (y << this._s) & this._b;
		y ^= (y << this._t) & this._c;
		y ^= y >>> this._l;

		return (y >>> 0);
	}

	public IntArray rangeInt(int len, int min, int max) {
		IntArray result = new IntArray(len);
		for (int i = 0; i < len; i++) {
			result.add(this.nextInt(min, max));
		}
		return result;
	}

	public LongArray rangeLong(int len, int min, int max) {
		LongArray result = new LongArray(len);
		for (int i = 0; i < len; i++) {
			result.add(this.nextLong(min, max));
		}
		return result;
	}

	public FloatArray rangeFloat(int len, int min, int max) {
		FloatArray result = new FloatArray(len);
		for (int i = 0; i < len; i++) {
			result.add(this.nextFloat(min, max));
		}
		return result;
	}

	public BoolArray rangeBool(int len) {
		BoolArray result = new BoolArray(len);
		for (int i = 0; i < len; i++) {
			result.add(this.nextBoolean());
		}
		return result;
	}
}
