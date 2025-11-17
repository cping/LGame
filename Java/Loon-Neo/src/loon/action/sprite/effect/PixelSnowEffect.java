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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 像素化雪
 */
public class PixelSnowEffect extends PixelBaseEffect {

	private float _viewX, _viewY;

	private TriangleEffect _first;

	private TriangleEffect[] _force;

	private TriangleEffect[] _dif;

	private LColor[] _colors;

	private boolean _onlySnow = false;

	public void setOnlySnow(boolean flag) {
		this._onlySnow = flag;
	}

	public boolean getOnlySnow() {
		return _onlySnow;
	}

	private final float[][] _fdelta = { { 0.0f, 3f }, { 2.3999999999999999f, -1.5f }, { -2.3999999999999999f, -1.5f } };

	public PixelSnowEffect(LColor color) {
		this(color, 0, 0, LSystem.viewSize.getWidth() / 2, LSystem.viewSize.getHeight() / 2);
	}

	public PixelSnowEffect(LColor color, float x, float y, float w, float h) {
		super(color, x, y, w, h);
		this._viewX = x;
		this._viewY = y;
		this._first = new TriangleEffect(w, h, _fdelta, 0.0f, 0.0f, 3f);
		float[][] vector = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		this._force = new TriangleEffect[32];
		for (int j = 0; j < _force.length; j++) {
			float nx = MathUtils.random(200) - 100;
			nx /= 45f;
			float ny = MathUtils.random(200) - 100;
			ny /= 45f;
			_force[j] = new TriangleEffect(w, h, vector, nx, ny, 12f);
		}
		float res[][] = { { 32f, 0.0f }, { -16f, 24f }, { -16f, -24f } };
		_dif = new TriangleEffect[32];
		_colors = new LColor[32];
		for (int j = 0; j < _dif.length; j++) {
			float d1 = MathUtils.random(9000);
			d1 /= 155f;
			float nx = MathUtils.random(8000) + 2000;
			float ny = MathUtils.random(8000) + 2000;
			nx /= 155f;
			ny /= 155f;
			nx *= MathUtils.cos((d1 * 3.1415926535897931f) / 180f);
			ny *= MathUtils.sin((d1 * 3.1415926535897931f) / 180f);
			if (MathUtils.random(2) == 1) {
				nx *= -1f;
			}
			if (MathUtils.random(2) == 1) {
				ny *= -1f;
			}
			nx /= 25f;
			ny /= 25f;
			_dif[j] = new TriangleEffect(w, h, res, nx, ny, MathUtils.random(30) + 3);
			int r = MathUtils.random(64) + 192;
			_colors[j] = new LColor((int) (color.r * r), (int) (color.g * r), (int) (color.b * r), color.getAlpha());
		}
		this._limit = 160;
		_triangleEffects.add(_force);
		_triangleEffects.add(_dif);
		_triangleEffects.add(new TriangleEffect[] { _first });
		setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super._completed) {
			return;
		}
		float x = _viewX - tx;
		float y = _viewY - ty;
		int tmp = g.color();
		g.setColor(_baseColor);
		if (_onlySnow) {
			for (int i = 0; i < _dif.length; i++) {
				g.setColor(_colors[i]);
				_dif[i].drawPaint(g, x, y);
			}
		} else {
			if (super._frame < 120) {
				float[][] delta = _first.getDelta();
				for (int j = 0; j < delta.length; j++) {
					for (int i = 0; i < delta[j].length; i++) {
						delta[j][i] += _fdelta[j][i] / 45f;
					}
				}
				_first.setDelta(delta);
				_first.resetAverage();
				_first.drawPaint(g, x, y);
				for (int j = 0; j < super._frame * 2 && j < _force.length; j++) {
					_force[j].drawPaint(g, x, y);
				}
			} else if (super._frame < 240) {
				for (int i = 0; i < _dif.length; i++) {
					g.setColor(_colors[i]);
					_dif[i].drawPaint(g, x, y);
				}
			}
		}
		g.setColor(tmp);
		if (super._frame >= _limit) {
			super._completed = true;
		}
	}

}
