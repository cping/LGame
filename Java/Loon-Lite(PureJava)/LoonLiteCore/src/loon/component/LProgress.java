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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.ProgressSkin;
import loon.component.skin.SkinManager;
import loon.events.ValueListener;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 一个进度条用UI
 */
public class LProgress extends LComponent {

	// 默认提供了四种进度条模式，分别是游戏类血槽，普通的UI形式，圆形UI模式，以及用户自制图像.(默认为游戏模式)
	public enum ProgressType {
		GAME, UI, CircleUI, Custom
	}

	private LTexture _defaultColorTexture;
	private LTexture _bgTexture;
	private LTexture _bgTextureEnd;
	private LTexture _bgProgressTexture;
	private LTexture _bgProgressStart;

	private ValueListener _listener;

	private float _percentage = 1f;

	private float _minValue = 0f;

	private float _maxValue = 0f;

	private boolean _vertical = false;

	private LTexture _texture;

	private ProgressType _progressType;

	public LProgress(int x, int y, int width, int height) {
		this(ProgressType.GAME, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(LColor color, int x, int y, int width, int height) {
		this(ProgressType.GAME, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, int x, int y, int width, int height) {
		this(type, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width, int height) {
		this(type, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressSkin skin, int x, int y, int width, int height) {
		this(ProgressType.Custom, skin.getColor(), x, y, width, height, skin.getBackgroundTexture(),
				skin.getProgressTexture());
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width, int height, LTexture bg,
			LTexture bgProgress) {
		super(x, y, width, height);
		this._progressType = type;
		this._component_baseColor = color == null ? LColor.red : color;
		switch (_progressType) {
		case GAME:
			this._texture = LTextures.loadTexture(LSystem.getSystemImagePath() + "bar.png");
			this._bgTexture = _texture.cpy(3, 0, 1, _texture.height() - 2);
			this._bgProgressTexture = _texture.cpy(1, 0, 1, _texture.height() - 2);
			this._bgProgressStart = _texture.cpy(0, 0, 1, _texture.height() - 2);
			this._bgTextureEnd = _texture.cpy(4, 0, 1, _texture.height() - 2);
			break;
		case UI:
		case CircleUI:
			if (_defaultColorTexture == null || _defaultColorTexture.isClosed()) {
				_defaultColorTexture = LSystem.base().graphics().finalColorTex();
			}
			this._bgTexture = SkinManager.get().getProgressSkin().getBackgroundTexture();
			this._bgProgressTexture = _defaultColorTexture;
			break;
		default:
			this._bgTexture = bg;
			this._bgProgressTexture = bgProgress;
			break;
		}
		this.reset();
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_progressType != ProgressType.CircleUI) {
			draw(g, x, y);
		} else {
			float radius = MathUtils.min(getWidth(), getHeight());
			float size = radius / 6f;
			g.drawStrokeGradientCircle(x, y, 0, 360f, getWidth(), getHeight(), size, LColor.gray, LColor.darkGray);
			g.drawStrokeGradientCircle(x, y, 0, 360f * _percentage, getWidth(), getHeight(), size,
					_component_baseColor.darker(), _component_baseColor);

		}
	}

	@Override
	public void update(final long elapsedTime) {
		super.update(elapsedTime);
		if (_listener != null) {
			_listener.onChange(this, _percentage);
		}
	}

	public void draw(GLEx batch, int x, int y) {
		if (_vertical) {
			float size = 0;
			switch (_progressType) {
			case GAME:
				size = getWidth() * (1f - _percentage);
				float posY = getHeight() / 2;
				batch.draw(_bgTexture, x + getHeight() / 2 + getWidth() / 2, y - posY, size, getHeight(), 0f, 0f, 90);
				batch.setColor(_component_baseColor);
				size = getWidth() * _percentage;
				batch.draw(_bgProgressTexture, x + getHeight() / 2 + getWidth() / 2, y + getWidth() - size - posY,
						getWidth() * _percentage, getHeight(), 0f, 0f, 90);
				batch.resetColor();
				break;
			case UI:
				batch.draw(_bgTexture, x, y, getHeight(), getWidth());
				batch.setColor(_component_baseColor);
				size = (getWidth() * _percentage - 2);
				batch.draw(_bgProgressTexture, x + 1, y + getWidth() - size - 1, getHeight() - 2, size);
				batch.resetColor();
				break;
			default:
				batch.draw(_bgTexture, x, y, getHeight(), getWidth());
				batch.setColor(_component_baseColor);
				size = (getWidth() * _percentage);
				batch.draw(_bgProgressTexture, x, y + getWidth() - size, getHeight(), size);
				batch.resetColor();
				break;
			}
		} else {
			switch (_progressType) {
			case GAME:
				batch.draw(_bgTexture, x + getWidth() * _percentage + 1, y, getWidth() * (1 - _percentage),
						getHeight());
				batch.draw(_bgTextureEnd, x + getWidth() + 1, y, _bgTextureEnd.width(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(_bgProgressTexture, x + 1, y, getWidth() * _percentage, getHeight());
				batch.draw(_bgProgressStart, x, y, _bgProgressStart.width(), getHeight());
				batch.resetColor();
				break;
			case UI:
				batch.draw(_bgTexture, x, y, getWidth(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(_bgProgressTexture, x + 1, y + 1, getWidth() * _percentage - 2, getHeight() - 2);
				batch.resetColor();
				break;
			default:
				batch.draw(_bgTexture, x, y, getWidth(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(_bgProgressTexture, x, y, getWidth() * _percentage, getHeight());
				batch.resetColor();
				break;
			}
		}
	}

	@Override
	public LProgress reset() {
		super.reset();
		this._percentage = 1f;
		this._minValue = 0f;
		this._maxValue = 100f;
		return this;
	}

	public boolean isZero() {
		return MathUtils.equal(this.getValue(), 0f);
	}

	public boolean isMin() {
		return MathUtils.equal(this.getValue(), this._minValue);
	}

	public boolean isMax() {
		return MathUtils.equal(this.getValue(), this._maxValue);
	}

	public float getValue() {
		return this._percentage * this._maxValue;
	}

	public LProgress setValue(float v) {
		float process = 0f;
		if (v < _minValue) {
			process = _minValue / _maxValue;
		} else if (v > _maxValue) {
			process = 1f;
		} else {
			process = v / _maxValue;
		}
		this._percentage = process;
		return this;
	}

	public float getMinValue() {
		return _minValue;
	}

	public LProgress setMinValue(float v) {
		if (v > this._maxValue) {
			this._maxValue = v;
			return this;
		}
		this._minValue = v;
		setValue(getValue());
		return this;
	}

	public float getMaxValue() {
		return _maxValue;
	}

	public LProgress setMaxValue(float v) {
		if (v < this._minValue) {
			this._minValue = v;
			return this;
		}
		this._maxValue = v;
		setValue(getValue());
		return this;
	}

	public LProgress setPercentage(float p) {
		setValue(p * _maxValue);
		return this;
	}

	public LProgress setValue(float v, float min, float max) {
		setMinValue(min);
		setMaxValue(max);
		setValue(v);
		return this;
	}

	public boolean isVertical() {
		return _vertical;
	}

	public void setVertical(boolean vertical) {
		this._vertical = vertical;
	}

	public float getPercentage() {
		return this._percentage;
	}

	public ValueListener getListener() {
		return _listener;
	}

	public void setListener(ValueListener listener) {
		this._listener = listener;
	}

	@Override
	public String getUIName() {
		return "Progress";
	}

	@Override
	public void destory() {
		if (_texture != null) {
			if (_bgTexture != null) {
				_bgTexture.close();
			}
			if (_bgTextureEnd != null) {
				_bgTextureEnd.close();
			}
			if (_bgProgressTexture != null) {
				_bgProgressTexture.close();
			}
			if (_bgProgressStart != null) {
				_bgProgressStart.close();
			}
			_texture.close();
		}
	}

}
