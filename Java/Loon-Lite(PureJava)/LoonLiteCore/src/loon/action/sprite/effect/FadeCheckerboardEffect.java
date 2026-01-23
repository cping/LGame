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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.Duration;

/**
 * 西洋棋式淡入淡出效果
 */
public class FadeCheckerboardEffect extends BaseAbstractEffect {

	private LColor _tempColor = new LColor();

	private long _time;

	private float _currentFrame;

	private int _type;

	private int _step;

	private int _rows = 8;

	private int _cols = 8;

	public FadeCheckerboardEffect(int type, LColor c) {
		this(type, 8, 8, c);
	}

	public FadeCheckerboardEffect(int type, int rows, int cols, LColor c) {
		this(type, c, rows, cols, 1);
	}

	public FadeCheckerboardEffect(int type, LColor c, int rows, int cols, int step) {
		this(type, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), rows, cols, step);
	}

	public FadeCheckerboardEffect(int type, long delay, int rows, int cols, LColor c) {
		this(type, delay, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), rows, cols, 1);
	}

	public FadeCheckerboardEffect(int type, LColor c, int w, int h, int rows, int cols, int step) {
		this(type, 120, c, w, h, rows, cols, step);
	}

	public FadeCheckerboardEffect(int type, long delay, LColor c, int w, int h, int rows, int cols, int step) {
		this._type = type;
		this._rows = rows;
		this._cols = cols;
		this.setDelay(delay);
		this.setColor(c);
		this.setSize(w, h);
		this.setRepaint(true);
		this.setStep(step);
	}

	@Override
	public long getDelay() {
		return _time;
	}

	@Override
	public float getDelayS() {
		return Duration.ofS(_time);
	}

	@Override
	public FadeCheckerboardEffect setDelay(long delay) {
		this._time = delay;
		if (_type == TYPE_FADE_IN) {
			this._currentFrame = this._time;
		} else {
			this._currentFrame = 0;
		}
		return this;
	}

	@Override
	public FadeCheckerboardEffect setDelayS(float s) {
		return setDelay(Duration.ofS(s));
	}

	public float getCurrentFrame() {
		return _currentFrame;
	}

	public FadeCheckerboardEffect setCurrentFrame(float currentFrame) {
		this._currentFrame = currentFrame;
		return this;
	}

	public int getEffectType() {
		return _type;
	}

	public FadeCheckerboardEffect setEffectType(int type) {
		this._type = type;
		return this;
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (completedAfterBlackScreen(g, sx, sy)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(sx), drawY(sy), _width, _height, _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		final float progress = _currentFrame / _time;
		if (progress <= 0.09f) {
			return;
		}
		if (progress >= 0.91f) {
			g.fillRect(drawX(sx), drawY(sy), _width, _height, _baseColor);
			return;
		}
		final float cellW = getWidth() / (float) _cols;
		final float cellH = getHeight() / (float) _rows;
		float alpha = 0f;
		for (int row = 0; row < _rows; row++) {
			for (int col = 0; col < _cols; col++) {
				float v = ((row + col) % 2);
				boolean checkerin = (progress > v);
				if (checkerin) {
					alpha = progress;
				} else {
					alpha = 1.0f - progress;
				}
				float eased = (-(MathUtils.cos(MathUtils.PI * progress) - 1f) / 2f);
				float scaleY = checkerin ? eased : 1f - eased;
				alpha = scaleY;
				if (checkerin) {
					g.fillRect(col * cellW, row * cellH + (cellH * (1f - scaleY) / 2f), cellW, cellH,
							_tempColor.setColor(_baseColor).setAlpha(alpha));
				}
			}
		}
	}

	@Override
	public void onUpdate(long timer) {
		if (checkAutoRemove()) {
			return;
		}
		if (_type == TYPE_FADE_IN) {
			_currentFrame -= _step;
			if (_currentFrame <= _step) {
				_completed = true;
			}
		} else {
			_currentFrame += _step;
			if (_currentFrame >= _time - _step) {
				_completed = true;
			}
		}
	}

	public FadeCheckerboardEffect setStep(int s) {
		_step = LSystem.toIScaleFPS(s, 1);
		return this;
	}

	public int getStep() {
		return _step;
	}

	public int getFadeType() {
		return _type;
	}

	@Override
	public FadeCheckerboardEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
