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

	private LColor colorback, colorbefore, colorafter;

	protected boolean hit, showValue, dead;

	private int value, valueMax, valueMin;

	private int current, goal;

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

	public StatusBar(int value, int max, int x, int y, int width, int height) {
		this(value, max, x, y, width, height, LColor.gray, LColor.red, LColor.orange);
	}

	public StatusBar(IFont font, int value, int max, int x, int y, int width, int height) {
		this(font, value, max, x, y, width, height, LColor.gray, LColor.red, LColor.orange);
	}

	public StatusBar(int value, int max, int x, int y, int width, int height, LColor back, LColor before,
			LColor after) {
		this(LSystem.getSystemGameFont(), value, max, x, y, width, height, back, before, after);
	}

	public StatusBar(IFont font, int value, int max, int x, int y, int width, int height, LColor back, LColor before,
			LColor after) {
		this.value = value;
		this.valueMax = max;
		this.valueMin = value;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
		this.setWidth(width);
		this.setHeight(height);
		this.setFont(font);
		this.hit = true;
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

	public StatusBar set(int v) {
		this.value = v;
		this.valueMax = v;
		this.valueMin = v;
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		return this;
	}

	public StatusBar empty() {
		this.value = 0;
		this.valueMin = 0;
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		return this;
	}

	private void drawBar(GLEx g, float v1, float v2, float size, float x, float y) {
		float cv1 = (_width * v1) / size;
		float cv2;
		if (v1 == v2) {
			cv2 = cv1;
		} else {
			cv2 = (_width * v2) / size;
		}
		if (cv1 < _width || cv2 < _height) {
			g.fillRect(x, y, _width, _height, colorback);
		}
		if (valueMin < value) {
			if (cv1 == _width) {
				g.fillRect(x, y, cv1, _height, colorbefore);
			} else {
				if (!dead) {
					g.fillRect(x, y, cv2, _height, colorafter);
				}
				g.fillRect(x, y, cv1, _height, colorbefore);
			}
		} else {
			if (cv2 == _width) {
				g.fillRect(x, y, cv2, _height, colorbefore);
			} else {
				g.fillRect(x, y, cv1, _height, colorafter);
				g.fillRect(x, y, cv2, _height, colorbefore);
			}
		}
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
	public StatusBar setUpdate(int val) {
		this.valueMin = MathUtils.mid(0, val, valueMax);
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		return this;
	}

	public StatusBar setDead(boolean d) {
		this.dead = d;
		return this;
	}

	public boolean state() {
		if (current == goal) {
			return false;
		}
		if (current > goal) {
			current--;
			value = MathUtils.mid(valueMin, (int) ((current * valueMax) / _width), value);
		} else {
			current++;
			value = MathUtils.mid(value, (int) ((current * valueMax) / _width), valueMin);
		}
		return true;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		drawBar(g, goal, current, _width, getX() + offsetX, getY() + offsetY);
		if (this.showValue) {
			String displayValue = null;
			if (StringUtils.isEmpty(numberString)) {
				displayValue = String.valueOf(value);
			} else {
				displayValue = StringUtils.format(numberString, String.valueOf(value));
			}
			IFont font = g.getFont();
			if (numberFont == null) {
				numberFont = font;
			}
			g.setFont(numberFont);
			int width = numberFont.stringWidth(displayValue);
			int height = numberFont.getHeight();
			g.drawString(displayValue, (x() + _width / 2 - width / 2) + _offset.x + offsetX,
					(y() + _height / 2 - height / 2) + _offset.y + offsetY - 2, fontColor);
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
		if (_visible && hit) {
			state();
		}
	}

	public int getMaxValue() {
		return valueMax;
	}

	public StatusBar setMaxValue(int valueMax) {
		this.valueMax = MathUtils.min(valueMax, 0);
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		this.state();
		return this;
	}

	public int getMinValue() {
		return valueMin;
	}

	public StatusBar setMinValue(int valueMin) {
		this.valueMin = MathUtils.min(valueMin, 0);
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		this.state();
		return this;
	}

	public int getValue() {
		return value;
	}

	public StatusBar setValue(int value) {
		this.value = value;
		return this;
	}

	public boolean isHit() {
		return hit;
	}

	public StatusBar setHit(boolean hit) {
		this.hit = hit;
		return this;
	}

	public LColor getColorback() {
		return colorback;
	}

	public StatusBar setColorback(LColor c) {
		this.colorback = c;
		return this;
	}

	public LColor getColorbefore() {
		return colorbefore;
	}

	public StatusBar setColorbefore(LColor c) {
		this.colorbefore = c;
		return this;
	}

	public LColor getColorafter() {
		return colorafter;
	}

	public StatusBar setColorafter(LColor c) {
		this.colorafter = c;
		return this;
	}

	public String getFormatNumber() {
		return numberString;
	}

	public StatusBar setFormatNumber(String num) {
		this.numberString = num;
		return this;
	}
}
