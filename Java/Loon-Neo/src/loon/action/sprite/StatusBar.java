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

	private LColor colorback, colorbefore, colorafter;

	protected boolean showValue, hitObject, deadObject;

	private int initValue, maxValue, minValue;

	private int currentWidth, goalWidth;

	private LColor fontColor = new LColor(LColor.white);

	private String numberString;

	private IFont numberFont;

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
		this.initValue = this.minValue = v;
		this.maxValue = max;
		this.currentWidth = (width * v) / maxValue;
		this.goalWidth = (width * minValue) / maxValue;
		this.setWidth(width);
		this.setHeight(height);
		this.setFont(font);
		this.hitObject = true;
		if (back == null) {
			this.colorback = LColor.gray;
		} else {
			this.colorback = back;
		}
		if (before == null) {
			this.colorbefore = LColor.red;
		} else {
			this.colorbefore = before;
		}
		if (after == null) {
			this.colorafter = LColor.orange;
		} else {
			this.colorafter = after;
		}
		this.setLocation(x, y);
		this.setRepaint(true);
	}

	public void matchProgressToWidth() {
		this.matchProgressToWidth(minValue, maxValue);
	}

	public void matchProgressToWidthMin(int min) {
		this.matchProgressToWidth(min, maxValue);
	}

	public void matchProgressToWidthMax(int max) {
		this.matchProgressToWidth(minValue, max);
	}

	public void matchProgressToWidth(int min, int max) {
		this.currentWidth = MathUtils.iceil(MathUtils.clamp(((_width * initValue) / max), 0, _width));
		this.goalWidth = MathUtils.iceil(MathUtils.clamp(((_width * min) / max), 0, _width));
	}

	public StatusBar set(int v) {
		this.initValue = v;
		this.maxValue = v;
		this.minValue = v;
		this.matchProgressToWidth();
		return this;
	}

	public StatusBar empty() {
		this.initValue = 0;
		this.minValue = 0;
		this.matchProgressToWidth();
		return this;
	}

	private void drawBar(GLEx g, float v1, float v2, float size, float x, float y) {
		final float alpha = g.alpha();
		final float cv1 = MathUtils.floorPositive(_width * v1) / size;
		final float cv2;
		if (MathUtils.equal(v1, v2)) {
			cv2 = cv1;
		} else {
			cv2 = MathUtils.floorPositive((_width * v2) / size);
		}
		g.setAlpha(_objectAlpha);
		if (cv1 <= _width || cv2 <= _height) {
			g.fillRect(x, y, _width, _height, LColor.combine(colorback, _baseColor));
		} else {
			g.fillRect(x, y, cv1, _height, LColor.combine(colorback, _baseColor));
		}
		if (minValue <= initValue) {
			if (MathUtils.equal(cv1, _width)) {
				g.fillRect(x, y, cv1, _height, LColor.combine(colorbefore, _baseColor));
			} else {
				if (!deadObject) {
					g.fillRect(x, y, cv2, _height, LColor.combine(colorafter, _baseColor));
				}
				g.fillRect(x, y, cv1, _height, LColor.combine(colorbefore, _baseColor));
			}
		} else if (MathUtils.equal(cv2, _width)) {
			g.fillRect(x, y, cv2, _height, LColor.combine(colorbefore, _baseColor));
		} else {
			g.fillRect(x, y, cv1, _height, LColor.combine(colorafter, _baseColor));
			g.fillRect(x, y, cv2, _height, LColor.combine(colorbefore, _baseColor));
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
		this.minValue = MathUtils.mid(v, 0, maxValue);
		this.matchProgressToWidth();
		return this;
	}

	public StatusBar setDead(boolean d) {
		this.deadObject = d;
		return this;
	}

	public boolean state() {
		if (currentWidth == goalWidth) {
			return false;
		}
		if (currentWidth > goalWidth) {
			currentWidth -= LSystem.toIScaleFPS(1);
			initValue = MathUtils.mid(minValue, (int) ((currentWidth * maxValue) / _width), initValue);
		} else {
			currentWidth += LSystem.toIScaleFPS(1);
			initValue = MathUtils.mid(initValue, (int) ((currentWidth * maxValue) / _width), minValue);
		}
		return true;
	}

	public float getPercentage() {
		return (float) this.initValue / maxValue;
	}

	public StatusBar setPercentage(float p) {
		setUpdate(MathUtils.iceil(MathUtils.clamp((p * maxValue), 0, maxValue)));
		return this;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		final float offX = drawX(offsetX);
		final float offY = drawY(offsetY);
		drawBar(g, goalWidth, currentWidth, _width, offX, offY);
		if (this.showValue) {
			String displayValue = null;
			if (StringUtils.isEmpty(numberString)) {
				displayValue = String.valueOf(initValue);
			} else {
				displayValue = StringUtils.format(numberString, String.valueOf(initValue));
			}
			IFont font = g.getFont();
			if (numberFont == null) {
				numberFont = font;
			}
			g.setFont(numberFont);
			int width = numberFont.stringWidth(displayValue);
			int height = MathUtils.min(numberFont.getSize(), numberFont.getHeight());
			g.drawString(displayValue, offX + ((_width - width) / 2) + _offsetTextX,
					offY + ((_height - height) / 2) - 1 + _offsetTextY, fontColor);
			g.setFont(font);
		}
	}

	public IFont getFont() {
		return this.numberFont;
	}

	public StatusBar setFont(IFont font) {
		if (font == null) {
			return this;
		}
		this.numberFont = font;
		return this;
	}

	public StatusBar setFontColor(LColor c) {
		this.fontColor = c;
		return this;
	}

	public LColor getFontColor() {
		return this.fontColor;
	}

	public boolean isShowNumber() {
		return showValue;
	}

	public StatusBar setShowNumber(boolean shown) {
		this.showValue = shown;
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_visible && hitObject) {
			state();
		}
	}

	public int getMaxValue() {
		return maxValue;
	}

	public StatusBar setMaxValue(int m) {
		this.maxValue = MathUtils.max(m, this.minValue);
		this.matchProgressToWidthMax(m);
		this.state();
		return this;
	}

	public int getMinValue() {
		return minValue;
	}

	public StatusBar setMinValue(int m) {
		this.minValue = MathUtils.min(m, this.maxValue);
		this.matchProgressToWidthMin(m);
		this.state();
		return this;
	}

	public int getValue() {
		return initValue;
	}

	public StatusBar setValue(int v) {
		this.initValue = v;
		return this;
	}

	public boolean isNotZero() {
		return !isZero();
	}

	public boolean isZero() {
		return MathUtils.equal(this.initValue, 0);
	}

	public boolean isMin() {
		return MathUtils.equal(this.initValue, this.minValue);
	}

	public boolean isMax() {
		return MathUtils.equal(this.initValue, this.maxValue);
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
		return hitObject;
	}

	public StatusBar setHit(boolean hit) {
		this.hitObject = hit;
		return this;
	}

	public LColor getColorback() {
		return colorback;
	}

	public StatusBar setColorback(LColor c) {
		this.colorback = new LColor(c);
		return this;
	}

	public LColor getColorbefore() {
		return colorbefore;
	}

	public StatusBar setColorbefore(LColor c) {
		this.colorbefore = new LColor(c);
		return this;
	}

	public LColor getColorafter() {
		return colorafter;
	}

	public StatusBar setColorafter(LColor c) {
		this.colorafter = new LColor(c);
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
		return numberString;
	}

	public StatusBar setText(String text) {
		return setText(text, 0f, 0f);
	}

	public StatusBar setText(String text, float x, float y) {
		this.numberString = text;
		if (StringUtils.isNullOrEmpty(text)) {
			showValue = false;
		} else {
			showValue = true;
		}
		this._offsetTextX = x;
		this._offsetTextY = y;
		return this;
	}

	public StatusBar setNumber(float v) {
		set(MathUtils.iceil(v));
		return setShowNumber(true);
	}

	public String getFormatNumber() {
		return numberString;
	}

	public StatusBar setFormatNumber(String num) {
		this.numberString = num;
		return this;
	}

}
