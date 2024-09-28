/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.geom;

public class PerlinNoise {
	
	private int _octaves;
	
	private float _amplitude;
	
	private float _frequency;
	
	private float _persistence;
	
	private int _seed;

	public PerlinNoise(int seed, float persistence, float frequency, float amplitude, int octaves) {
		set(seed, persistence, frequency, amplitude, octaves);
	}

	public PerlinNoise() {
		set(0, 0, 0, 0, 0);
	}

	public float getHeight(float x, float y) {
		return this._amplitude * total(x, y);
	}

	public int getSeed() {
		return this._seed;
	}

	public int getOctaves() {
		return this._octaves;
	}

	public float getAmplitude() {
		return this._amplitude;
	}

	public float getFrequency() {
		return this._frequency;
	}

	public float getPersistence() {
		return this._persistence;
	}

	public final void set(int seed, float persistence, float frequency, float amplitude, int octaves) {
		this._seed = 2 + seed * seed;
		this._octaves = octaves;
		this._amplitude = amplitude;
		this._frequency = frequency;
		this._persistence = persistence;
	}

	public PerlinNoise setSeed(int seed) {
		this._seed = 2 + seed * seed;
		return this;
	}

	public PerlinNoise setOctaves(int octaves) {
		this._octaves = octaves;
		return this;
	}

	public PerlinNoise setAmplitude(float amplitude) {
		this._amplitude = amplitude;
		return this;
	}

	public PerlinNoise setFrequency(float frequency) {
		this._frequency = frequency;
		return this;
	}

	public PerlinNoise setPersistence(float persistence) {
		this._persistence = persistence;
		return this;
	}

	private float total(float x, float y) {
		float t = 0f;
		float amp = 1f;
		float freq = this._frequency;

		for (int k = 0; k < this._octaves; k++) {
			t += getValue(y * freq + this._seed, x * freq + this._seed) * amp;
			amp *= this._persistence;
			freq *= 2;
		}

		return t;
	}

	private float getValue(float x, float y) {
		int Xint = (int) x;
		int Yint = (int) y;
		float Xfrac = x - Xint;
		float Yfrac = y - Yint;

		float n01 = noise(Xint - 1, Yint - 1);
		float n02 = noise(Xint + 1, Yint - 1);
		float n03 = noise(Xint - 1, Yint + 1);
		float n04 = noise(Xint + 1, Yint + 1);
		float n05 = noise(Xint - 1, Yint);
		float n06 = noise(Xint + 1, Yint);
		float n07 = noise(Xint, Yint - 1);
		float n08 = noise(Xint, Yint + 1);
		float n09 = noise(Xint, Yint);

		float n12 = noise(Xint + 2, Yint - 1);
		float n14 = noise(Xint + 2, Yint + 1);
		float n16 = noise(Xint + 2, Yint);

		float n23 = noise(Xint - 1, Yint + 2);
		float n24 = noise(Xint + 1, Yint + 2);
		float n28 = noise(Xint, Yint + 2);

		float n34 = noise(Xint + 2, Yint + 2);

		float x0y0 = 0.0625f * (n01 + n02 + n03 + n04) + 0.125f * (n05 + n06 + n07 + n08) + 0.25f * (n09);
		float x1y0 = 0.0625f * (n07 + n12 + n08 + n14) + 0.125f * (n09 + n16 + n02 + n04) + 0.25f * (n06);
		float x0y1 = 0.0625f * (n05 + n06 + n23 + n24) + 0.125f * (n03 + n04 + n09 + n28) + 0.25f * (n08);
		float x1y1 = 0.0625f * (n09 + n16 + n28 + n34) + 0.125f * (n08 + n14 + n06 + n24) + 0.25f * (n04);

		float v1 = interpolate(x0y0, x1y0, Xfrac);
		float v2 = interpolate(x0y1, x1y1, Xfrac);
		float fin = interpolate(v1, v2, Yfrac);

		return fin;
	}

	private float interpolate(float x, float y, float a) {
		float negA = 1f - a;
		float negASqr = negA * negA;
		float fac1 = 3f * (negASqr) - 2f * (negASqr * negA);
		float aSqr = a * a;
		float fac2 = 3f * aSqr - 2f * (aSqr * a);
		return x * fac1 + y * fac2;
	}

	private float noise(int x, int y) {
		int n = x + y * 57;
		n = (n << 13) ^ n;
		int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
		return 1f - (float) (t) * (9.313226E-10f);
	}
}
