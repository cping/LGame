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
package loon.canvas;

import loon.utils.MathUtils;

/**
 * Alpha处理用类
 */
public class Alpha {

	public static final Alpha ZERO = new Alpha(0f);
	public static final Alpha FULL = new Alpha(1f);

	private static final int SCALE_SHORT_MODE = 0x11;
	private static final float MAX_INT_VALUE = 255f;
	private static final int HEX_BASE = 16;

	private float _alpha = 0f;

	public Alpha(final String color) {
		this._alpha = getString(color);
	}

	public Alpha(final float a) {
		this._alpha = MathUtils.clamp(a, 0f, 1f);
	}

	public Alpha linear(final Alpha end, final float t) {
		return new Alpha(this._alpha + t * (end._alpha - this._alpha));
	}

	public final float setLinear(final float endAlpha, final float t) {
		return (this._alpha = MathUtils.clamp(this._alpha + t * (endAlpha - this._alpha), 0f, 1f));
	}

	public Alpha mutiply(final float factor) {
		return new Alpha(_alpha * factor);
	}

	public final float setMutiply(final float factor) {
		return (this._alpha = MathUtils.clamp(_alpha * factor, 0f, 1f));
	}

	public final float getAlpha() {
		return _alpha;
	}

	private float getString(final String color) {
		if (color == null) {
			return 1f;
		}
		if (color.length() == 2) {
			return (Integer.parseInt(color.substring(1, 2), HEX_BASE) * SCALE_SHORT_MODE) / MAX_INT_VALUE;
		} else {
			return Integer.parseInt(color.substring(1, 3), HEX_BASE) / MAX_INT_VALUE;
		}
	}

	public Alpha setAlpha(final float a) {
		this._alpha = a;
		return this;
	}

	@Override
	public String toString() {
		return "(" + _alpha + ")";
	}

}
