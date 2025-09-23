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
package loon.canvas;

import loon.utils.MathUtils;

public final class LColorLinear {

	private float[][] _colors;

	private int _colorCount;

	private LColorLinear(int c) {
		this._colorCount = c;
		this._colors = new float[_colorCount][4];
	}

	public LColorLinear(LColor[] colors) {
		this(colors.length);
		for (int i = 0; i < _colorCount; i++) {
			_colors[i] = colors[i].toArray();
		}
	}

	public LColorLinear(int[] colors) {
		this(colors.length);
		for (int i = 0; i < _colorCount; i++) {
			_colors[i] = LColor.rgbaToFloats(colors[i]);
		}
	}

	public LColor getColor(float t) {
		float span = (float) 1 / (_colorCount - 1);
		t = t % 1f;
		int s1 = (int) (t / span);
		if (s1 < 0) {
			s1 = 0;
		}
		int s2 = s1 + 1;
		if (s2 >= _colorCount) {
			s2 -= _colorCount;
		}
		float pos = (t % span) / span;
		return new LColor(MathUtils.lerp(_colors[s1][0], _colors[s2][0], pos),
				MathUtils.lerp(_colors[s1][1], _colors[s2][1], pos),
				MathUtils.lerp(_colors[s1][2], _colors[s2][2], pos),
				MathUtils.lerp(_colors[s1][3], _colors[s2][3], pos));
	}

}
