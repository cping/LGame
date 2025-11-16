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
 * 0.3.2版新增类，单一色彩的圆弧渐变特效
 */
public class FadeArcEffect extends BaseAbstractEffect {

	private final int _arcDiv;

	private int _step;

	private int _curTurn = 1;

	private int _tmpColor;

	private int[] _sign = { -1, 1 };

	private int _sleep;

	public FadeArcEffect(int type, LColor c) {
		this(type, c, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public FadeArcEffect(int type, LColor c, int x, int y, int width, int height) {
		this(type, c, x, y, width, height, 10);
	}

	public FadeArcEffect(int type, LColor c, int x, int y, int width, int height, int div) {
		this(type, c, x, y, width, height, div, 1);
	}

	public FadeArcEffect(int type, LColor c, int x, int y, int width, int height, int div, int sleep) {
		this.setLocation(x, y);
		this.setSize(width, height);
		this.setDelay(200);
		this.setColor(c == null ? LColor.black : c);
		this.setRepaint(true);
		this.setTurn(type);
		this.setSleep(sleep);
		this._arcDiv = div;
	}

	public FadeArcEffect setSleep(float s) {
		this._sleep = LSystem.toIScaleFPS(s, 1);
		return this;
	}

	public int getSleep() {
		return _sleep;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			_step += _sleep;
			if (_step != 0) {
				final float v = (MathUtils.DEG_FULL / this._arcDiv) * this._step;
				if (v >= MathUtils.DEG_FULL) {
					this._completed = true;
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_curTurn == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
		}
		if (_curTurn == TYPE_FADE_IN && _completed) {
			return;
		}
		_tmpColor = g.color();
		g.setColor(_baseColor);
		if (_curTurn == TYPE_FADE_IN && this._step - _sleep <= 0) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
		} else {
			final float deg = MathUtils.DEG_FULL / this._arcDiv * this._step;
			if (deg != 0 && deg <= MathUtils.DEG_FULL) {
				final float length = MathUtils.sqrt(MathUtils.pow(_width / 2f, 2f) + MathUtils.pow(_height / 2f, 2f));
				final float x = drawX(_width / 2f - length + offsetX) - LSystem.LAYER_TILE_SIZE / 2f;
				final float y = drawY(_height / 2f - length + offsetY) - LSystem.LAYER_TILE_SIZE / 2f;
				final float w = (_width / 2f + length - x) + LSystem.LAYER_TILE_SIZE;
				final float h = (_height / 2f + length - y) + LSystem.LAYER_TILE_SIZE;
				if (_curTurn == TYPE_FADE_IN) {
					final float v = this._sign[this._curTurn] * deg;
					g.fillArc(x, y, w, h, 0, MathUtils.DEG_FULL + v);
				} else {
					if (_step < _sleep) {
						return;
					}
					g.fillArc(x, y, w, h, 0, this._sign[this._curTurn] * deg);
				}
			}
		}
		g.setColor(_tmpColor);
	}

	@Override
	public FadeArcEffect reset() {
		super.reset();
		this._step = 0;
		return this;
	}

	public int getTurn() {
		return _curTurn;
	}

	public FadeArcEffect setTurn(int turn) {
		this._curTurn = turn;
		return this;
	}

	@Override
	public FadeArcEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
