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

import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.SetXY;
import loon.geom.Vector2f;

/**
 * 一个[伪随机]数值生成用类(不过以游戏来说应该够了,取代java同名类,这个速度快)
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
		return next(0) > 0;
	}

	public int nextInt(int min, int max) {
		if (max <= 0) {
			return 0;
		}
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * this.nextFloat() + min)), min, max);
	}

	public int nextInt(int range) {
		if (range <= 0) {
			return 0;
		}
		range += 1;
		int r = (int) next(17);
		int m = range;
		if ((range & m) == 0) {
			r = (int) ((range * (long) r) >> 31);
		} else {
			for (int u = r; u - (r = u % range) + m < 0; u = (int) next(17))
				;
		}
		return r;
	}

	public float nextLong(float range) {
		if (range <= 0) {
			return 0l;
		}
		return nextLong() * range;
	}

	public long nextLong(long min, long max) {
		if (max <= 0) {
			return 0l;
		}
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * this.nextFloat() + min)), min, max);
	}

	public double nextDouble(double range) {
		if (range <= 0) {
			return 0d;
		}
		return nextDouble() * range;
	}

	public double nextDouble(double min, double max) {
		if (max <= 0) {
			return 0d;
		}
		return MathUtils.clamp(MathUtils.floor((max - min + 1) * this.nextDouble() + min), min, max);
	}

	public float nextFloat(float min, float max) {
		if (max <= 0) {
			return 0f;
		}
		return MathUtils.clamp((max - min) * this.nextFloat() + min, min, max);
	}

	public float nextFloat(float range) {
		if (range <= 0) {
			return 0f;
		}
		return nextFloat() * range;
	}

	public float nextFloat() {
		return this.next(32) * 2.3283064E-10f;
	}

	public double nextDouble() {
		return this.next(32) * 2.3283064E-10d;
	}

	public int nextInt() {
		return (int) (next(16));
	}

	public long nextLong() {
		return next(0);
	}

	protected long next(int bits) {
		if (this._index >= this._n) {
			this.update();
		}

		long y = this._mt.get(this._index++);

		y ^= y >>> this._u;
		y ^= (y << this._s) & this._b;
		y ^= (y << this._t) & this._c;
		y ^= y >>> this._l;

		return (y >>> bits);
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

	public boolean chance(float value) {
		return nextFloat() < value;
	}

	public float nextAngleRad() {
		return nextFloat(0f, 2f * MathUtils.PI);
	}

	public float nextAngleDeg() {
		return nextFloat(0f, 360f);
	}

	public float nextSignFloat() {
		return nextFloat() < 0.5f ? -1.0f : 1.0f;
	}

	public int nextSignInt() {
		return nextFloat() < 0.5f ? -1 : 1;
	}

	public Vector2f nextVec2() {
		final float angle = nextFloat() * 2.0f * MathUtils.PI;
		final float x = MathUtils.cos(angle);
		final float y = MathUtils.sin(angle);
		return new Vector2f(MathUtils.abs(x), MathUtils.abs(y));
	}

	public Vector2f nextVec2(float max) {
		return nextVec2(0f, max);
	}

	public Vector2f nextVec2(float min, float max) {
		float v = nextFloat(min, max);
		return nextVec2().mul(v);
	}

	public Vector2f nextVec2(RectBox rect) {
		return nextVec2(nextFloat(rect.x, rect.x + rect.width), nextFloat(rect.y, rect.y + rect.height));
	}

	public LColor nextColor() {
		return nextColor(0, 255);
	}

	public LColor nextColor(float min, float max) {
		return nextColor(min, max, 1f);
	}

	public LColor nextColor(float min, float max, float alpha) {
		if (alpha <= 0) {
			return new LColor(nextFloat(min, max), nextFloat(min, max), nextFloat(min, max), nextFloat(min, max));
		}
		return new LColor(nextFloat(min, max), nextFloat(min, max), nextFloat(min, max), alpha);
	}

	public PointF nextPoint() {
		return nextPoint(LSystem.viewSize.getRect());
	}

	public PointF nextPoint(float min, float max) {
		final float x = nextFloat(min, max);
		final float y = nextFloat(min, max);
		return new PointF(x, y);
	}

	public PointF nextPoint(RectBox rect) {
		final float x = nextFloat(rect.x, rect.x + rect.width);
		final float y = nextFloat(rect.y, rect.y + rect.height);
		return new PointF(x, y);
	}

	public SetXY setPoint(SetXY point, RectBox rect) {
		point.setX(nextFloat(rect.x, rect.x + rect.width));
		point.setY(nextFloat(rect.y, rect.y + rect.height));
		return point;
	}
}
