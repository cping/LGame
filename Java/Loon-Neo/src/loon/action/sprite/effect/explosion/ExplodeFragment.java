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

	protected float _baseCx;
	protected float _baseCy;
	protected float _baseRadius;
	protected float _top;
	protected float _bottom;
	protected float _mag;
	protected float _neg;
	protected float _life;
	protected float _overflow;

	final float _vwidth;

	final float _vheight;

	final float _stop;

	public ExplodeFragment(int color, float x, float y, RectI bound, float vw, float vh, float end, LTexture tex) {
		super(color, x, y, bound, tex);
		this._vwidth = vw;
		this._vheight = vh;
		this._stop = end;
	}

	@Override
	protected void caculate(float factor) {
		float f = 0f;
		float normalization = factor / _stop;
		if (normalization < _life || normalization > 1f - _overflow) {
			_alpha = 0f;
			return;
		}
		normalization = (normalization - _life) / (1f - _life - _overflow);
		float f2 = normalization * _stop;
		if (normalization >= 0.7f) {
			f = (normalization - 0.7f) / 0.3f;
		}
		_alpha = 1f - f;
		f = _bottom * f2;
		_cx = _baseCx + f;
		_cy = (float) (_baseCy - this._neg * MathUtils.pow(f, 2f)) - f * _mag;
		_width = _vwidth + (_baseRadius - _vwidth) * f2;
		_height = _vheight + (_baseRadius - _vheight) * f2;
	}
}
