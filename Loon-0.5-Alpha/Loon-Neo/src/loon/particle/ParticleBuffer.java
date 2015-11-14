/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.particle;

import java.util.List;

import loon.canvas.LColor;
import loon.opengl.ParticleBatch;
import loon.utils.MathUtils;

public class ParticleBuffer {
	
	protected final int _maxParticles;
	
	protected int _live;
	
	public static abstract class Effector {
		public abstract void apply(int index, float[] data, int start,
				float now, float dt);
	}

	public static abstract class Initializer {

		public void willInit(int count) {
		}

		public abstract void init(int index, float[] data, int start);
	}

	public static final int BIRTH = 0;

	public static final int LIFESPAN = BIRTH + 1;

	public static final int VEL_X = LIFESPAN + 1;

	public static final int VEL_Y = VEL_X + 1;

	public static final int M00 = VEL_Y + 1;

	public static final int M01 = M00 + 1;

	public static final int M10 = M01 + 1;

	public static final int M11 = M10 + 1;

	public static final int TX = M11 + 1;

	public static final int TY = TX + 1;

	public static final int ALPHA_RED = TY + 1;

	public static final int GREEN_BLUE = ALPHA_RED + 1;

	public static final int NUM_FIELDS = GREEN_BLUE + 1;

	public final float[] data;

	public final int[] alive;

	public static String dump(float[] data, int index, int start) {
		float a = LColor.decodeUpper(data[start + ALPHA_RED]);
		float r = LColor.decodeLower(data[start + ALPHA_RED]);
		float g = LColor.decodeUpper(data[start + GREEN_BLUE]);
		float b = LColor.decodeLower(data[start + GREEN_BLUE]);
		return index + " tx:" + data[start + M00] + "," + data[start + M01]
				+ "," + data[start + M10] + "," + data[start + M11] + ","
				+ data[start + TX] + "," + data[start + TY] + " vel:"
				+ data[start + VEL_X] + "," + data[start + VEL_Y] + " life:"
				+ data[start + BIRTH] + "," + data[start + LIFESPAN]
				+ " color:" + r + "," + g + "," + b + "," + a;
	}

	public static void multiply(float[] data, int start, float m00, float m01,
			float m10, float m11, float tx, float ty) {
		float pm00 = data[start + M00], pm01 = data[start + M01];
		float pm10 = data[start + M10], pm11 = data[start + M11];
		float ptx = data[start + TX], pty = data[start + TY];
		data[start + M00] = pm00 * m00 + pm10 * m01;
		data[start + M01] = pm01 * m00 + pm11 * m01;
		data[start + M10] = pm00 * m10 + pm10 * m11;
		data[start + M11] = pm01 * m10 + pm11 * m11;
		data[start + TX] = pm00 * tx + pm10 * ty + ptx;
		data[start + TY] = pm01 * tx + pm11 * ty + pty;
	}

	public ParticleBuffer(int maxParticles) {
		_maxParticles = maxParticles;
		data = new float[maxParticles * NUM_FIELDS];
		alive = new int[maxParticles / 32 + 1];
	}

	public boolean isAlive(int partidx) {
		return (alive[partidx / 32] & (1 << partidx % 32)) != 0;
	}

	public void setAlive(int partidx, boolean isAlive) {
		if (isAlive) {
			alive[partidx / 32] |= (1 << partidx % 32);
		} else {
			alive[partidx / 32] &= ~(1 << partidx % 32);
		}
	}

	public boolean isFull() {
		return _live >= _maxParticles;
	}

	public void add(int count, float now, List<? extends Initializer> initters) {
		if (isFull())
			return;
		int pp = 0, ppos = 0, icount = initters.size(), initted = 0;
		for (int aa = 0; aa < alive.length && initted < count; aa++) {
			int live = alive[aa], mask = 1;
			if (live == 0xFFFFFFFF) {
				pp += 32;
				ppos += 32 * NUM_FIELDS;
				continue;
			}
			for (int end = MathUtils.min(pp + 32, _maxParticles); pp < end
					&& initted < count; pp++, ppos += NUM_FIELDS, mask <<= 1) {
				if ((live & mask) != 0)
					continue;
				live |= mask;
				data[ppos + BIRTH] = now;
				for (int ii = 0; ii < icount; ii++) {
					initters.get(ii).init(pp, data, ppos);
				}
				initted++;
			}
			alive[aa] = live;
		}
	}

	public int apply(List<? extends Effector> effectors, float now, float dt) {
		int pp = 0, ppos = 0, ecount = effectors.size(), living = 0;
		for (int aa = 0; aa < alive.length; aa++) {
			int live = alive[aa], mask = 1, died = 0;
			for (int end = pp + 32; pp < end; pp++, ppos += NUM_FIELDS, mask <<= 1) {
				if ((live & mask) == 0) {
					continue;
				}

				if (now - data[ppos + BIRTH] > data[ppos + LIFESPAN]) {
					live &= ~mask;
					died++;
					continue;
				}

				for (int ee = 0; ee < ecount; ee++) {
					effectors.get(ee).apply(pp, data, ppos, now, dt);
				}
				living++;
			}

			if (died > 0) {
				alive[aa] = live;
			}
		}
		return living;
	}

	public void paint(ParticleBatch batch, float width, float height) {
		float ql = -width / 2, qt = -height / 2, qr = width / 2, qb = height / 2;
		int pp = 0, ppos = 0, rendered = 0;
		for (int aa = 0; aa < alive.length; aa++) {
			int live = alive[aa], mask = 1;
			for (int end = pp + 32; pp < end; pp++, ppos += NUM_FIELDS, mask <<= 1) {
				if ((live & mask) == 0)
					continue;
				batch.addParticle(ql, qt, qr, qb, data, ppos);
				rendered++;
			}
		}
		_live = rendered;
	}

}
