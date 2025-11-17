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
import loon.canvas.LColor;
import loon.geom.RectI;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public abstract class Fragment {

	protected float _cx;
	protected float _cy;
	protected float _ox;
	protected float _oy;
	protected int _color;

	protected float _width;
	protected float _height;
	protected float _alpha;
	protected RectI _parBound;
	protected LTexture _ovalTexture;

	private LColor _imgColor = LColor.white.cpy();

	public Fragment(int color, float x, float y, RectI bound, LTexture tex) {
		this._color = color;
		this._cx = x;
		this._cy = y;
		this._parBound = bound;
		_ovalTexture = tex;
	}

	public void reset() {
		this._cx = this._cy = 0;
		this._alpha = 0f;
	}

	protected void update(float factor) {
		_width = _width - factor * MathUtils.nextInt(2);
		_height = _height - factor * MathUtils.nextInt(2);
		_alpha = (1f - factor);
	}

	protected abstract void caculate(float factor);

	protected void oval(GLEx g, float x, float y) {
		int tint = g.color();
		if (_ovalTexture != null) {
			_imgColor.setColor(LColor.alpha(_color, _alpha));
			g.setColor(_imgColor);
			g.draw(_ovalTexture, x + _cx, y + _cy, _width, _height, _imgColor);
		} else {
			g.setColor(LColor.alpha(_color, _alpha));
			g.fillOval(x + _cx, y + _cy, _width, _height);
		}
		g.setTint(tint);
	}

	public void draw(GLEx g, float x, float y, float factor) {
		caculate(factor);
		oval(g, x, y);
	}
}
