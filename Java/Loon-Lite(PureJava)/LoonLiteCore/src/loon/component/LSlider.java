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

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.component.skin.SliderSkin;
import loon.events.SysTouch;
import loon.events.ValueListener;
import loon.opengl.GLEx;

/**
 * 滑动块组件UI,用以进行简单的百分比调节
 */
public class LSlider extends LComponent {

	private LTexture _sliderImage, _barImage;

	private ValueListener _listener;

	private float _value, _sliderWidth, _sliderHeight, _padding, _barImageHeight;

	private float _stepSize = 0f;

	private float _minValue = 0f;

	private float _maxValue = 0f;

	private boolean _vertical;

	public LSlider(int x, int y, int width, int height) {
		this(LColor.gray.darker(), LColor.white, x, y, width, height, false);
	}

	public LSlider(int x, int y, int width, int height, boolean vertical) {
		this(LColor.gray.darker(), LColor.white, x, y, width, height, vertical);
	}

	public LSlider(LColor sliderColor, LColor barColor, int x, int y, int width, int height, boolean vertical) {
		this(SkinManager.get().getSliderSkin(sliderColor, barColor, vertical), x, y, width, height, vertical);
	}

	public LSlider(SliderSkin skin, int x, int y, int width, int height, boolean vertical) {
		this(skin.getSliderText(), skin.getBarText(), x, y, width, height, vertical);
	}

	public LSlider(LTexture sliderText, LTexture barText, int x, int y, int width, int height, boolean vertical) {
		super(x, y, width, height);
		this._vertical = vertical;
		if (vertical) {
			this._sliderImage = sliderText;
			this._sliderWidth = width * 0.5f;
			this._sliderHeight = width * 0.5f;
			this._barImage = barText;
			this._padding = width / 100f;
			this._barImageHeight = height;
			setWidth((int) (width * 0.12f));
		} else {
			this._sliderImage = sliderText;
			this._sliderWidth = height * 0.5f;
			this._sliderHeight = height * 0.5f;
			this._barImage = barText;
			this._padding = width / 100f;
			this._barImageHeight = height / 10f;
			setHeight(height);
		}
		this.reset();
		freeRes().add(sliderText, barText);
	}

	public LSlider reset() {
		this._stepSize = 0f;
		this._value = 0f;
		this._minValue = 0f;
		this._maxValue = 100f;
		return this;
	}

	@Override
	public void update(final long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (SysTouch.isDrag() || SysTouch.isDown()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				if (_vertical) {
					_value = (getUITouchY()) / getHeight();
				} else {
					_value = (getUITouchX()) / getWidth();
				}
				if (_listener != null) {
					_listener.onChange(this, _value);
				}
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_vertical) {
			g.draw(_barImage, x + _padding, y + getHeight() / 2 - _barImageHeight / 2, getWidth() - _padding * 2,
					_barImageHeight);
			g.draw(_sliderImage, x + _padding * (getWidth() - _padding * 2) - _sliderWidth / 2 + getWidth() / 2,
					y + _value * (getHeight()) - _sliderHeight / 2, _sliderWidth, _sliderHeight);
		} else {
			g.draw(_barImage, x + _padding, y + getHeight() / 2 - _barImageHeight / 2, getWidth() - _padding * 2,
					_barImageHeight);
			g.draw(_sliderImage, x + _padding + _value * (getWidth() - _padding * 2) - _sliderWidth / 2,
					y + getHeight() / 2 - _sliderHeight / 2, _sliderWidth, _sliderHeight);
		}
	}

	public float getPercentage() {
		return this._value;
	}

	public LSlider setPercentage(float p) {
		setValue(p * _maxValue);
		return this;
	}

	public LSlider setValue(float v, float min, float max) {
		setMinValue(min);
		setMaxValue(max);
		setValue(v);
		return this;
	}

	public float getMinValue() {
		return _minValue;
	}

	public LSlider setMinValue(float minValue) {
		if (minValue > this._maxValue) {
			this._maxValue = minValue;
			return this;
		}
		this._minValue = minValue;
		setStepSize(getStepSize());
		setValue(getValue());
		return this;
	}

	public float getMaxValue() {
		return _maxValue;
	}

	public LSlider setMaxValue(float maxValue) {
		if (maxValue < this._minValue) {
			this._minValue = maxValue;
			return this;
		}
		this._maxValue = maxValue;
		setStepSize(getStepSize());
		setValue(getValue());
		return this;
	}

	public float getStepSize() {
		return _stepSize < 0 ? 0f : _stepSize;
	}

	public LSlider setStepSize(float stepSize) {
		this._stepSize = stepSize;
		if (stepSize > 0) {
			float difference = this._maxValue - this._minValue;
			this._stepSize = difference < stepSize ? difference : stepSize;
		}
		setValue(getValue());
		return this;
	}

	public float getValue() {
		return _value * this._maxValue;
	}

	public LSlider setValue(float v) {
		this._value = v;
		if (this._stepSize > 0) {
			float halfStepSize = this._stepSize / 2f;
			if (this._value < 0f) {
				halfStepSize *= -1;
			}
			final int count = (int) ((this._value + halfStepSize) / this._stepSize);
			this._value = this._stepSize * count;
		}
		if (this._value > this._maxValue) {
			this._value = this._maxValue;
		} else if (this._value < this._minValue) {
			this._value = this._minValue;
		}
		this._value = this._value / this._maxValue;
		return this;
	}

	public boolean isVertical() {
		return this._vertical;
	}

	public float getSliderWidth() {
		return _sliderWidth;
	}

	public LSlider setSliderWidth(float s) {
		this._sliderWidth = s;
		return this;
	}

	public float getSliderHeight() {
		return _sliderHeight;
	}

	public LSlider setSliderHeight(float s) {
		this._sliderHeight = s;
		return this;
	}

	public LTexture getSliderImage() {
		return _sliderImage;
	}

	public LSlider setSliderImage(LTexture s) {
		this._sliderImage = s;
		return this;
	}

	public LSlider setSliderImage(LTexture s, float width, float height) {
		this._sliderImage = s;
		setSliderWidth(width);
		setSliderHeight(height);
		return this;
	}

	public LSlider setBarImage(LTexture b) {
		this._barImage = b;
		return this;
	}

	public ValueListener getListener() {
		return _listener;
	}

	public LSlider setListener(ValueListener v) {
		this._listener = v;
		return this;
	}

	public LSlider setPadding(float p) {
		this._padding = p;
		return this;
	}

	public LSlider setBarImageHeight(float b) {
		this._barImageHeight = b;
		return this;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		if (isPointInUI()) {
			if (_vertical) {
				_value = (getUITouchY()) / getHeight();
			} else {
				_value = (getUITouchX()) / getWidth();
			}
			if (_listener != null) {
				_listener.onChange(this, _value);
			}
		}
	}

	@Override
	public String getUIName() {
		return "Slider";
	}

	@Override
	public void destory() {

	}

}
