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
package loon.action.sprite.effect.explosion;

import loon.LTexture;
import loon.geom.RectI;
import loon.utils.MathUtils;

public class ExplodeFragment extends Fragment {

	public float baseCx;
	public float baseCy;
	public float baseRadius;
	public float top;
	public float bottom;
	public float mag;
	public float neg;
	public float life;
	public float overflow;

	final float vwidth;

	final float vheight;

	final float stop;

	public ExplodeFragment(int color, float x, float y, RectI bound, float vw, float vh, float end, LTexture tex) {
		super(color, x, y, bound, tex);
		this.vwidth = vw;
		this.vheight = vh;
		this.stop = end;
	}

	@Override
	protected void caculate(float factor) {
		float f = 0f;
		float normalization = factor / stop;
		if (normalization < life || normalization > 1f - overflow) {
			alpha = 0f;
			return;
		}
		normalization = (normalization - life) / (1f - life - overflow);
		float f2 = normalization * stop;
		if (normalization >= 0.7f) {
			f = (normalization - 0.7f) / 0.3f;
		}
		alpha = 1f - f;
		f = bottom * f2;
		cx = baseCx + f;
		cy = baseCy - this.neg * MathUtils.pow(f, 2f) - f * mag;
		width = vwidth + (baseRadius - vwidth) * f2;
		height = vheight + (baseRadius - vheight) * f2;
	}
}
