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

import loon.geom.Vector2f;

public class NoiseGenerator {

	private IntArray plist;

	private FloatArray seedX;

	private FloatArray seedY;

	private int length;

	private boolean _initial;

	public NoiseGenerator() {
		this(256);
	}

	public NoiseGenerator(int size) {
		this.length = size;
	}

	public float noise1D(float seed) {

		seed = MathUtils.clamp(seed, 0f, 1f);
		setSeed();

		int qx0 = MathUtils.floor(seed);
		int qx1 = (qx0 + 1);

		float tx0 = (seed - qx0);
		float tx1 = (tx0 - 1f);

		qx0 = qx0 & (length - 1);
		qx1 = qx1 & (length - 1);

		float v0 = seedX.get(qx0) * tx0;
		float v1 = seedY.get(qx1) * tx1;

		float wx = (3f - 2f * tx0) * tx0 * tx0;
		return v0 - wx * (v0 - v1);
	}

	private void setSeed() {
		if (!_initial) {
			this.plist = new IntArray(length);
			this.seedX = new FloatArray(length);
			this.seedY = new FloatArray(length);
			int i = 0;
			int j = 0;
			int nSwap = 0;
			while (i < length) {
				plist.add(i);
				i++;
			}
			i = 0;
			while (i < length) {
				j = MathUtils.random(1, Integer.MAX_VALUE) & (length - 1);
				nSwap = plist.get(i);
				plist.set(i, plist.get(j));
				plist.set(j, nSwap);
				i++;
			}
			for (i = 0; i < length - 1; i++) {
				Vector2f v = new Vector2f(MathUtils.random() - 0.5f, MathUtils.random() - 0.5f);
				v.normalizeSelf();
				seedX.add(v.x);
				seedY.add(v.y);
			}
			_initial = true;
		}
	}
}
