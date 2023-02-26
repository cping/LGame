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

	private LTexture sliderImage, barImage;
	private ValueListener _listener;
	private float _value, _sliderWidth, _sliderHeight, _padding, _barImageHeight;
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
		_vertical = vertical;
		if (vertical) {
			this.sliderImage = sliderText;
			this._sliderWidth = width * 0.5f;
			this._sliderHeight = width * 0.5f;
			this.barImage = barText;
			this._padding = width / 100;
			this._barImageHeight = height;
			setWidth((int) (width * 0.12));
		} else {
			this.sliderImage = sliderText;
			this._sliderWidth = height * 0.5f;
			this._sliderHeight = height * 0.5f;
			this.barImage = barText;
			this._padding = width / 100;
			this._barImageHeight = height / 10;
			setHeight(height);
		}
		freeRes().add(sliderText, barText);
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
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (_vertical) {
			g.draw(barImage, x + _padding, y + getHeight() / 2 - _barImageHeight / 2, getWidth() - _padding * 2,
					_barImageHeight);
			g.draw(sliderImage, x + _padding * (getWidth() - _padding * 2) - _sliderWidth / 2 + getWidth() / 2,
					y + _value * (getHeight()) - _sliderHeight / 2, _sliderWidth, _sliderHeight);
		} else {
			g.draw(barImage, x + _padding, y + getHeight() / 2 - _barImageHeight / 2, getWidth() - _padding * 2,
					_barImageHeight);
			g.draw(sliderImage, x + _padding + _value * (getWidth() - _padding * 2) - _sliderWidth / 2,
					y + getHeight() / 2 - _sliderHeight / 2, _sliderWidth, _sliderHeight);
		}
	}

	public float getValue() {
		return _value;
	}

	public void setValue(float v) {
		this._value = v;
	}

	public float getSliderWidth() {
		return _sliderWidth;
	}

	public void setSliderWidth(float s) {
		this._sliderWidth = s;
	}

	public float getSliderHeight() {
		return _sliderHeight;
	}

	public void setSliderHeight(float s) {
		this._sliderHeight = s;
	}

	public LTexture getSliderImage() {
		return sliderImage;
	}

	public void setSliderImage(LTexture s) {
		this.sliderImage = s;
	}

	public void setSliderImage(LTexture s, float width, float height) {
		this.sliderImage = s;
		setSliderWidth(width);
		setSliderHeight(height);
	}

	public void setBarImage(LTexture b) {
		this.barImage = b;
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

	public LSlider setPercentage(float p) {
		if (p >= 0f && p <= 1f) {
			this._value = p;
		} else {
			if (p > 1f) {
				this._value = 1f;
			} else if (p < 0f) {
				this._value = 0f;
			}
		}
		return this;
	}

	@Override
	public String getUIName() {
		return "Slider";
	}

}
