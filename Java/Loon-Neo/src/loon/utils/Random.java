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

	private final static float FLOAT_VALUE = 2.3283064E-10f;
	private final static double DOUBLE_VALUE = 2.3283064E-10d;

	private final static long LowerMask = 0x7fffffff;
	private final static long UpperMask = 0x80000000;
	private final static long Bitmask32 = 0xffffffff;

	private final static int N = 624;

	private final static long W = 32;
	private final static long M = 397;
	private final static long A = 0x9908b0df;
	private final static long U = 11;
	private final static long S = 7;
	private final static long B = 0x9d2c5680;
	private final static long T = 15;
	private final static long C = 0xefc60000;
	private final static long L = 18;
	private final static long F = 1812433253;

	private final LongArray _mt;

	private int _index;

	public Random() {
		this(0);
	}

	public Random(long seed) {
		_mt = new LongArray(N);
		if (seed != 0) {
			_mt.set(0, seed);
		} else {
			_mt.set(0, (TimeUtils.nanoTime()) >>> 0);
		}
		for (int i = 1; i < N; i++) {
			long s = _mt.get(i - 1) ^ (_mt.get(i - 1) >>> (W - 2));
			_mt.set(i, (((F * ((s & 0xffff0000) >>> 16)) << 16) + F * (s & 0xffff) + i) >>> 0);
		}
		_index = N;
	}

	private void update() {
		long[] mag01 = { 0x0, A };
		long y = 0;
		int i = 0;
		for (; i < N - M; i++) {
			y = (_mt.get(i) & UpperMask) | (_mt.get(i + 1) & LowerMask);
			_mt.set(i, _mt.get((int) (i + M)) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & Bitmask32));
		}
		for (; i < N - 1; i++) {
			y = (_mt.get(i) & UpperMask) | (_mt.get(i + 1) & LowerMask);
			_mt.set(i, _mt.get((int) (i + (M - N))) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & Bitmask32));
		}
		y = (_mt.get(N - 1) & UpperMask) | (_mt.get(0) & LowerMask);
		_mt.set(N - 1, _mt.get((int) (M - 1)) ^ (y >>> 1) ^ (mag01[(int) (y & 0x1)] & Bitmask32));

		_index = 0;
	}

	public boolean nextBoolean() {
		return next(0) > 0;
	}

	public int nextInt(int min, int max) {
		if (max <= 0) {
			return 0;
		}
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * nextFloat() + min)), min, max);
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
		return MathUtils.clamp(MathUtils.abs(MathUtils.floor((max - min + 1) * nextFloat() + min)), min, max);
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
		return MathUtils.clamp(MathUtils.floor((max - min + 1) * nextDouble() + min), min, max);
	}

	public float nextFloat(float min, float max) {
		if (max <= 0) {
			return 0f;
		}
		return MathUtils.clamp((max - min) * nextFloat() + min, min, max);
	}

	public float nextFloat(float range) {
		if (range <= 0) {
			return 0f;
		}
		return nextFloat() * range;
	}

	public float nextFloat() {
		return next(32) * FLOAT_VALUE;
	}

	public double nextDouble() {
		return next(32) * DOUBLE_VALUE;
	}

	public byte[] nextBytes(int range) {
		if (range <= 0) {
			return new byte[0];
		}
		return nextBytes(new byte[range]);
	}

	public byte[] nextBytes(byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len;) {
			for (int rnd = nextInt(), n = MathUtils.min(len - i, 4); n-- > 0; rnd >>= 8) {
				bytes[i++] = (byte) rnd;
			}
		}
		return bytes;
	}

	public int nextInt() {
		return (int) (next(16));
	}

	public long nextLong() {
		return next(0);
	}

	protected long next(int bits) {
		if (_index >= N) {
			update();
		}

		long y = _mt.get(_index++);

		y ^= y >>> U;
		y ^= (y << S) & B;
		y ^= (y << T) & C;
		y ^= y >>> L;

		return (y >>> bits);
	}

	public IntArray rangeInt(int len, int min, int max) {
		IntArray result = new IntArray(len);
		for (int i = 0; i < len; i++) {
			result.add(nextInt(min, max));
		}
		return result;
	}

	public LongArray rangeLong(int len, int min, int max) {
		LongArray result = new LongArray(len);
		for (int i = 0; i < len; i++) {
			result.add(nextLong(min, max));
		}
		return result;
	}

	public FloatArray rangeFloat(int len, int min, int max) {
		FloatArray result = new FloatArray(len);
		for (int i = 0; i < len; i++) {
			result.add(nextFloat(min, max));
		}
		return result;
	}

	public BoolArray rangeBool(int len) {
		BoolArray result = new BoolArray(len);
		for (int i = 0; i < len; i++) {
			result.add(nextBoolean());
		}
		return result;
	}

	public boolean chance(float value) {
		return nextFloat() < value;
	}

	public float nextAngleRad() {
		return nextFloat(0f, MathUtils.TWO_PI);
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
		final float angle = nextFloat() * MathUtils.TWO_PI;
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

	public Vector2f nextCircleVec2(float radius) {
		final float calRadius = MathUtils.sqrt(nextFloat(0f, 1f)) * radius;
		final float angle = nextAngleRad();
		final float x = (calRadius * MathUtils.cos(angle));
		final float y = (calRadius * MathUtils.sin(angle));
		return new Vector2f(MathUtils.abs(x), MathUtils.abs(y));
	}

	public LColor nextColor() {
		return nextColor(0, 1f);
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

	public char randomChar(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return (char) -1;
		}
		return cs.charAt(nextInt(cs.length() - 1));
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
