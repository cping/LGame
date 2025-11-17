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

/**
 * 像素化三角旋转效果(进入画面),用来代表技能释放
 */
public class PixelDarkInEffect extends PixelBaseEffect {

	private float _viewX, _viewY;

	private TriangleEffect[] _ts;

	public PixelDarkInEffect(LColor color) {
		this(color, 0, 0, LSystem.viewSize.getWidth() / 2, LSystem.viewSize.getHeight() / 2);
	}

	public PixelDarkInEffect(LColor color, float x, float y, float w, float h) {
		super(color, x, y, w, h);
		_viewX = x;
		_viewY = y;
		float[][][] res = { { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } },
				{ { -120f, 30f }, { -96f, -15f }, { -144f, -15f } }, { { 120f, 30f }, { 144f, -15f }, { 96f, -15f } },
				{ { 0.0f, -90f }, { 24f, -135f }, { -24f, -135f } },
				{ { 0.0f, 150f }, { 24f, 105f }, { -24f, 105f } } };
		_ts = new TriangleEffect[5];
		_ts[0] = new TriangleEffect(w, h, res[0], 0.0f, 0.0f, 3f);
		_ts[1] = new TriangleEffect(w, h, res[1], 3f, 0.0f, 9f);
		_ts[2] = new TriangleEffect(w, h, res[2], -3f, 0.0f, 9f);
		_ts[3] = new TriangleEffect(w, h, res[3], +0.0f, 3f, 9f);
		_ts[4] = new TriangleEffect(w, h, res[4], +0.0f, -3f, 9f);
		this._limit = 160;
		_triangleEffects.add(_ts);
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
		if (super._frame < 40) {
			int size = _ts.length;
			for (int i = 0; i < size; i++) {
				_ts[i].drawPaint(g, x, y);
			}
		} else {
			_ts[0].drawPaint(g, x, y);
		}
		g.setColor(tmp);
		if (_frame >= _limit) {
			super._completed = true;
		}
	}

}
