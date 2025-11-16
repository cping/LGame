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
package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 一个附带渐变效果的状态条,可以使用为血条动态显示之类的
 */
public class StatusBar extends Entity {

	private float _offsetTextX, _offsetTextY;

	private LColor _colorback, _colorbefore, _colorafter;

	protected boolean _showValue, _hitObject, _deadObject;

	protected boolean _updated = false;

	private int _initValue, _maxValue, _minValue;

	private int _currentWidth, _goalWidth;

	private int _speed;

	private LColor _fontColor = new LColor(LColor.white);

	private String _numberString;

	private IFont _numberFont;

	public StatusBar(int width, int height) {
		this(0, 0, width, height);
	}

	public StatusBar(int x, int y, int width, int height) {
		this(100, 100, x, y, width, height);
	}

	public StatusBar(IFont font, int x, int y, int width, int height) {
		this(font, 100, 100, x, y, width, height);
	}

	public StatusBar(int v, int max, int x, int y, int width, int height) {
		this(v, max, x, y, width, height, LColor.gray, LColor.red, LColor.orange);
	}

	public StatusBar(IFont font, int v, int max, int x, int y, int width, int height) {
		this(font, v, max, x, y, width, height, LColor.gray, LColor.red, LColor.orange);
	}

	public StatusBar(int v, int max, int x, int y, int width, int height, LColor back, LColor before, LColor after) {
		this(LSystem.getSystemGameFont(), v, max, x, y, width, height, back, before, after);
	}

	public StatusBar(IFont font, int v, int max, int x, int y, int width, int height, LColor back, LColor before,
			LColor after) {
		this._initValue = this._minValue = v;
		this._maxValue = max;
		this._currentWidth = (width * v) / _maxValue;
		this._goalWidth = (width * _minValue) / _maxValue;
		this.setWidth(width);
		this.setHeight(height);
		this.setFont(font);
		this._hitObject = true;
		if (back == null) {
			this._colorback = LColor.gray;
		} else {
			this._colorback = back;
		}
		if (before == null) {
			this._colorbefore = LColor.red;
		} else {
			this._colorbefore = before;
		}
		if (after == null) {
			this._colorafter = LColor.orange;
		} else {
			this._colorafter = after;
		}
		this._speed = 1;
		this._updated = false;
		this.setLocation(x, y);
		this.setRepaint(true);
	}

	public void matchProgressToWidth() {
		this.matchProgressToWidth(_minValue, _maxValue);
	}

	public void matchProgressToWidthMin(int min) {
		this.matchProgressToWidth(min, _maxValue);
	}

	public void matchProgressToWidthMax(int max) {
		this.matchProgressToWidth(_minValue, max);
	}

	public void matchProgressToWidth(int min, int max) {
		this._currentWidth = MathUtils.iceil(MathUtils.clamp(((_width * _initValue) / max), 0, _width));
		this._goalWidth = MathUtils.iceil(MathUtils.clamp(((_width * min) / max), 0, _width));
	}

	public StatusBar set(int v) {
		return set(v, v, v);
	}

	public StatusBar set(int v, int min) {
		return set(v, min, v);
	}

	public StatusBar set(int v, int min, int max) {
		if (this._initValue == v && this._minValue == min && this._maxValue == max) {
			return this;
		}
		this._initValue = v;
		this._minValue = min;
		this._maxValue = max;
		this._updated = true;
		this.matchProgressToWidth();
		return this;
	}

	public StatusBar empty() {
		this._initValue = 0;
		this._minValue = 0;
		this._updated = true;
		this.matchProgressToWidth();
		return this;
	}

	private void drawBar(GLEx g, float v1, float v2, float size, float x, float y) {
		final float alpha = g.alpha();
		final float cv1 = MathUtils.ceilPositive(_width * v1) / size;
		final float cv2;
		if (MathUtils.equal(v1, v2)) {
			cv2 = cv1;
		} else {
			cv2 = MathUtils.ceilPositive((_width * v2) / size);
		}
		g.setAlpha(_objectAlpha);
		if (cv1 <= _width || cv2 <= _height) {
			g.fillRect(x, y, _width, _height, LColor.combine(_colorback, _baseColor));
		} else {
			g.fillRect(x, y, cv1, _height, LColor.combine(_colorback, _baseColor));
		}
		if (_minValue <= _initValue) {
			if (MathUtils.equal(cv1, _width)) {
				g.fillRect(x, y, cv1, _height, LColor.combine(_colorbefore, _baseColor));
			} else {
				if (!_deadObject) {
					g.fillRect(x, y, cv2, _height, LColor.combine(_colorafter, _baseColor));
				}
				g.fillRect(x, y, cv1, _height, LColor.combine(_colorbefore, _baseColor));
			}
		} else if (MathUtils.equal(cv2, _width)) {
			g.fillRect(x, y, cv2, _height, LColor.combine(_colorbefore, _baseColor));
		} else {
			g.fillRect(x, y, cv1, _height, LColor.combine(_colorafter, _baseColor));
			g.fillRect(x, y, cv2, _height, LColor.combine(_colorbefore, _baseColor));
		}
		g.setAlpha(alpha);
	}

	/**
	 * 将状态条从开始值变更为结束值
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	public StatusBar updateTo(int begin, int end) {
		this.setValue(begin);
		this.setUpdate(end);
		return this;
	}

	/**
	 * 直接改变状态条为指定数值
	 *
	 * @param val
	 * @return
	 */
	public StatusBar setUpdate(int v) {
		if (v != this._minValue) {
			this._minValue = MathUtils.mid(v, 0, _maxValue);
			this._updated = true;
			this.matchProgressToWidth();
		}
		return this;
	}

	public StatusBar setDead(boolean d) {
		this._deadObject = d;
		return this;
	}

	public StatusBar setSpeed(int v) {
		this._speed = v;
		return this;
	}

	public int getSpeed() {
		return this._speed;
	}

	public boolean state() {
		if (_updated) {
			if (_currentWidth == _goalWidth) {
				_updated = false;
				return false;
			}
			if (_currentWidth > _goalWidth) {
				_currentWidth -= LSystem.toIScaleFPS(_speed);
				_initValue = MathUtils.mid(_minValue, (int) ((_currentWidth * _maxValue) / _width), _initValue);
				if (_currentWidth < _goalWidth) {
					_currentWidth = _goalWidth;
					_updated = false;
					return false;
				}
			} else if (_currentWidth < _goalWidth) {
				_currentWidth += LSystem.toIScaleFPS(_speed);
				_initValue = MathUtils.mid(_initValue, (int) ((_currentWidth * _maxValue) / _width), _minValue);
				if (_currentWidth > _goalWidth) {
					_currentWidth = _goalWidth;
					_updated = false;
					return false;
				}
			}
		}
		return true;
	}

	public boolean isUpdated() {
		return this._updated;
	}

	public float getPercentage() {
		return (float) this._initValue / _maxValue;
	}

	public StatusBar setPercentage(float p) {
		setUpdate(MathUtils.iceil(MathUtils.clamp((p * _maxValue), 0, _maxValue)));
		return this;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		final float offX = drawX(offsetX);
		final float offY = drawY(offsetY);
		drawBar(g, _goalWidth, _currentWidth, _width, offX, offY);
		if (this._showValue) {
			String displayValue = null;
			if (StringUtils.isEmpty(_numberString)) {
				displayValue = String.valueOf(_initValue);
			} else {
				displayValue = StringUtils.format(_numberString, String.valueOf(_initValue));
			}
			IFont font = g.getFont();
			if (_numberFont == null) {
				_numberFont = font;
			}
			g.setFont(_numberFont);
			int width = _numberFont.stringWidth(displayValue);
			int height = MathUtils.min(_numberFont.getSize(), _numberFont.getHeight());
			g.drawString(displayValue, offX + ((_width - width) / 2) + _offsetTextX,
					offY + ((_height - height) / 2) - 1 + _offsetTextY, _fontColor);
			g.setFont(font);
		}
	}

	public IFont getFont() {
		return this._numberFont;
	}

	public StatusBar setFont(IFont font) {
		if (font == null) {
			return this;
		}
		this._numberFont = font;
		return this;
	}

	public StatusBar setFontColor(LColor c) {
		this._fontColor = c;
		return this;
	}

	public LColor getFontColor() {
		return this._fontColor;
	}

	public boolean isShowNumber() {
		return _showValue;
	}

	public StatusBar setShowNumber(boolean shown) {
		this._showValue = shown;
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_visible && _hitObject) {
			state();
		}
	}

	public int getMaxValue() {
		return _maxValue;
	}

	public StatusBar setMaxValue(int m) {
		if (m != this._maxValue) {
			this._maxValue = MathUtils.max(m, this._minValue);
			this._updated = true;
			this.matchProgressToWidthMax(m);
			this.state();
		}
		return this;
	}

	public int getMinValue() {
		return _minValue;
	}

	public StatusBar setMinValue(int m) {
		if (m != this._minValue) {
			this._minValue = MathUtils.min(m, this._maxValue);
			this._updated = true;
			this.matchProgressToWidthMin(m);
			this.state();
		}
		return this;
	}

	public int getValue() {
		return _initValue;
	}

	public StatusBar setValue(int v) {
		if (_initValue != v) {
			this._initValue = v;
			this._updated = true;
		}
		return this;
	}

	public boolean isNotZero() {
		return !isZero();
	}

	public boolean isZero() {
		return MathUtils.equal(this._initValue, 0);
	}

	public boolean isMin() {
		return MathUtils.equal(this._initValue, this._minValue);
	}

	public boolean isMax() {
		return MathUtils.equal(this._initValue, this._maxValue);
	}

	public StatusBar addSelf(float v) {
		return setUpdate(MathUtils.iceil(getValue() + v));
	}

	public StatusBar subSelf(float v) {
		return setUpdate(MathUtils.iceil(getValue() - v));
	}

	public StatusBar mulSelf(float v) {
		return setUpdate(MathUtils.iceil(getValue() * v));
	}

	public StatusBar divSelf(float v) {
		return setUpdate(MathUtils.iceil(getValue() / v));
	}

	public boolean isHit() {
		return _hitObject;
	}

	public StatusBar setHit(boolean hit) {
		this._hitObject = hit;
		return this;
	}

	public LColor getColorback() {
		return _colorback;
	}

	public StatusBar setColorback(LColor c) {
		this._colorback = new LColor(c);
		return this;
	}

	public LColor getColorbefore() {
		return _colorbefore;
	}

	public StatusBar setColorbefore(LColor c) {
		this._colorbefore = new LColor(c);
		return this;
	}

	public LColor getColorafter() {
		return _colorafter;
	}

	public StatusBar setColorafter(LColor c) {
		this._colorafter = new LColor(c);
		return this;
	}

	public float getOffsetTextX() {
		return _offsetTextX;
	}

	public StatusBar setOffsetTextX(float x) {
		this._offsetTextX = x;
		return this;
	}

	public float getOffsetTextY() {
		return _offsetTextY;
	}

	public StatusBar setOffsetTextY(float y) {
		this._offsetTextY = y;
		return this;
	}

	public String getText() {
		return _numberString;
	}

	public StatusBar setText(String text) {
		return setText(text, 0f, 0f);
	}

	public StatusBar setText(String text, float x, float y) {
		this._numberString = text;
		if (StringUtils.isNullOrEmpty(text)) {
			_showValue = false;
		} else {
			_showValue = true;
		}
		this._offsetTextX = x;
		this._offsetTextY = y;
		return this;
	}

	public StatusBar setNumber(float v) {
		set(MathUtils.iceil(v));
		return setShowNumber(true);
	}

	public StatusBar setNumber(float v, float min) {
		set(MathUtils.iceil(v), MathUtils.iceil(min));
		return setShowNumber(true);
	}

	public StatusBar setNumber(float v, float min, float max) {
		set(MathUtils.iceil(v), MathUtils.iceil(min), MathUtils.iceil(max));
		return setShowNumber(true);
	}

	public String getFormatNumber() {
		return _numberString;
	}

	public StatusBar setFormatNumber(String num) {
		this._numberString = num;
		return this;
	}

}
