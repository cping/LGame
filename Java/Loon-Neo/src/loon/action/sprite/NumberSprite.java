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
package loon.action.sprite;

import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.timer.CountdownTimer;

/**
 * 显示大写数字
 * <p>
 * 
 * example:
 * 
 * <pre>
 * NumberSprite number = new NumberSprite("1334");
 * number.setLocation(125, 125);
 * add(number);
 * </pre>
 * 
 */
public class NumberSprite extends Entity {

	private CountdownTimer _countdownTimer;

	private String _label;
	// 0
	public static final int[][] ZERO = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 0, 1 },
			{ 1, 1, 1 }, };

	// 1
	public static final int[][] ONE = { { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, };

	// 2
	public static final int[][] TWO = { { 1, 1, 1 }, { 0, 0, 1 }, { 1, 1, 1 }, { 1, 0, 0 }, { 1, 0, 0 }, { 1, 1, 1 }, };

	// 3
	public static final int[][] THREE = { { 1, 1, 1 }, { 0, 0, 1 }, { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 1, 1, 1 }, };

	// 4
	public static final int[][] FOUR = { { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 0, 0, 1 }, };

	// 5
	public static final int[][] FIVE = { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 1, 1, 1 }, };

	// 6
	public static final int[][] SIX = { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 1, 1 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, };

	// 7
	public static final int[][] SEVEN = { { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 0, 0, 1 }, };

	// 8
	public static final int[][] EIGHT = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, { 1, 0, 1 }, { 1, 0, 1 },
			{ 1, 1, 1 }, };

	// 9
	public static final int[][] NINE = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 0, 0, 1 }, };

	private int unit;

	public NumberSprite(String mes) {
		this(mes, LColor.white);
	}

	public NumberSprite(String mes, LColor color) {
		this(mes, color, 5);
	}

	public NumberSprite(String mes, int unit) {
		this(mes, LColor.white, unit);
	}

	public NumberSprite(String mes, LColor color, int unit) {
		this(null, mes, color, unit);
	}

	public NumberSprite(CountdownTimer time, LColor color, int unit) {
		this(time, null, color, unit);
	}

	public NumberSprite(CountdownTimer timer, String mes, LColor color, int unit) {
		this._countdownTimer = timer;
		this._label = timer == null ? mes : timer.getTime();
		this.setColor(color);
		this.unit = unit;
		this.setRepaint(true);
		if (_label != null) {
			int size = _label.length() * unit;
			super.setWidth(size * 3);
			super.setHeight(2 * unit * 6);
		}
	}

	public NumberSprite setUnit(int unit) {
		this.unit = unit;
		return this;
	}

	public int getUnit() {
		return unit;
	}

	private int[][] getNum(int num) {
		if (num == 0) {
			return ZERO;
		} else if (num == 1) {
			return ONE;
		} else if (num == 2) {
			return TWO;
		} else if (num == 3) {
			return THREE;
		} else if (num == 4) {
			return FOUR;
		} else if (num == 5) {
			return FIVE;
		} else if (num == 6) {
			return SIX;
		} else if (num == 7) {
			return SEVEN;
		} else if (num == 8) {
			return EIGHT;
		} else if (num == 9) {
			return NINE;
		} else {
			return ZERO;
		}
	}

	public NumberSprite drawNumber(GLEx g, int x, int y, int[][] num) {
		int tmp = g.color();
		g.setColor(_baseColor);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				if (num[j][i] == 1) {
					g.fillRect(x + (unit * i), y + (unit * j), unit, unit);
				}
			}
		}
		g.setColor(tmp);
		return this;
	}

	public NumberSprite drawNumber(GLEx g, int x, int y, String num) {
		if (StringUtils.isEmpty(num)) {
			return this;
		}
		int index, size = unit * 9, offset = unit * 2;
		for (int i = 0; i < num.length(); i++) {
			String number = num.substring(i, i + 1);
			if (MathUtils.isNan(number)) {
				index = Integer.parseInt(number);
				drawNumber(g, x + (unit * (4 * i)), y, getNum(index));
			} else {
				if (StringUtils.isChinese(number.charAt(0))) {
					size = unit * 5;
					offset = unit * 2;
					IFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2 + offset + 4);
					g.setFont(oldFont);

				} else {
					IFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2 + offset);
					g.setFont(oldFont);
				}
			}
		}
		return this;
	}

	public NumberSprite drawNumber(Canvas g, int x, int y, int[][] num) {
		int tmp1 = g.getFillColor();
		int tmp2 = g.getStrokeColor();
		g.setColor(_baseColor);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				if (num[j][i] == 1) {
					g.fillRect(x + (unit * i), y + (unit * j), unit, unit);
				}
			}
		}
		g.setFillColor(tmp1);
		g.setStrokeColor(tmp2);
		return this;
	}

	public NumberSprite drawNumber(Canvas g, int x, int y, String num) {
		if (StringUtils.isEmpty(num)) {
			return this;
		}
		int index, size = unit * 10, offset = unit / 2;
		for (int i = 0; i < num.length(); i++) {
			String number = num.substring(i, i + 1);
			if (MathUtils.isNan(number)) {
				index = Integer.parseInt(number);
				drawNumber(g, x + (unit * (4 * i)), y, getNum(index));
			} else {
				int tmp1 = g.getFillColor();
				int tmp2 = g.getStrokeColor();
				g.setColor(_baseColor);
				if (StringUtils.isChinese(number.charAt(0))) {
					size = unit * 5;
					offset = unit * 2;
					LFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2 + offset + 4 - g.getFont().getAscent());
					g.setFont(oldFont);
				} else {
					LFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2 + offset - g.getFont().getAscent() + 4);
					g.setFont(oldFont);
				}
				g.setFillColor(tmp1);
				g.setStrokeColor(tmp2);
			}
		}
		return this;
	}

	@Override
	public void setWidth(float w) {
		super.setWidth(w);
		this.unit = (int) MathUtils.max(unit, w);
	}

	@Override
	public void setHeight(float h) {
		super.setHeight(h);
		this.unit = (int) MathUtils.max(unit, h);
	}

	@Override
	protected void onUpdate(long elapsedTime) {
		if (_countdownTimer != null) {
			_label = _countdownTimer.getTime();
		}
	}

	public NumberSprite setCountdownTimer(CountdownTimer timer) {
		this._countdownTimer = timer;
		return this;
	}

	public CountdownTimer getCountdownTimer() {
		return this._countdownTimer;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		drawNumber(g, (int) drawX(offsetX), (int) drawY(offsetY), _label);
	}

}
