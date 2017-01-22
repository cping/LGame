package loon.action.sprite;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class StatusBar extends Entity {

	private LColor colorback, colorbefore, colorafter;

	protected boolean hit, showValue, dead;

	private int value, valueMax, valueMin;

	private int current, goal;

	private LColor fontColor = new LColor(LColor.white);

	private String hpString;

	public StatusBar(int width, int height) {
		this(0, 0, width, height);
	}

	public StatusBar(int x, int y, int width, int height) {
		this(100, 100, x, y, width, height);
	}

	public StatusBar(int value, int max, int x, int y, int width, int height) {
		this(value, max, x, y, width, height, LColor.gray, LColor.red,
				LColor.orange);
	}

	public StatusBar(int value, int max, int x, int y, int width, int height,
			LColor back, LColor before, LColor after) {
		this.value = value;
		this.valueMax = max;
		this.valueMin = value;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
		this.setWidth(width);
		this.setHeight(height);
		this.hit = true;
		this.colorback = LColor.gray;
		this.colorbefore = LColor.red;
		this.colorafter = LColor.orange;
		this.setLocation(x, y);
		this.setRepaint(true);
	}

	public void set(int v) {
		this.value = v;
		this.valueMax = v;
		this.valueMin = v;
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
	}

	public void empty() {
		this.value = 0;
		this.valueMin = 0;
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
	}

	private void drawBar(GLEx g, float v1, float v2, float size, float x,
			float y) {
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

	public void updateTo(int v1, int v2) {
		this.setValue(v1);
		this.setUpdate(v2);
	}

	public void setUpdate(int val) {
		valueMin = MathUtils.mid(0, val, valueMax);
		current = (int) ((_width * value) / valueMax);
		goal = (int) ((_width * valueMin) / valueMax);
	}

	public void setDead(boolean d) {
		this.dead = d;
	}

	public boolean state() {
		if (current == goal) {
			return false;
		}
		if (current > goal) {
			current--;
			value = MathUtils.mid(valueMin,
					(int) ((current * valueMax) / _width), value);
		} else {
			current++;
			value = MathUtils.mid(value, (int) ((current * valueMax) / _width),
					valueMin);
		}
		return true;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		drawBar(g, goal, current, _width, getX() + offsetX, getY() + offsetY);
		if (showValue) {
			hpString = String.valueOf(value);
			int current = g.getFont().stringWidth(hpString);
			int h = g.getFont().getHeight();
			g.drawString(hpString, (x() + _width / 2 - current / 2) + 2 + _offset.x + offsetX, (y()
					+ _height / 2 - h) + _offset.y + offsetY, fontColor);
		}
	}

	public void setFontColor(LColor c) {
		this.fontColor = c;
	}

	public LColor getFontColor() {
		return this.fontColor;
	}

	public boolean isShowHP() {
		return showValue;
	}

	public void setShowHP(boolean showHP) {
		this.showValue = showHP;
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

	public void setMaxValue(int valueMax) {
		this.valueMax = MathUtils.min(valueMax, 0);
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		this.state();
	}

	public int getMinValue() {
		return valueMin;
	}

	public void setMinValue(int valueMin) {
		this.valueMin = MathUtils.min(valueMin, 0);
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		this.state();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public LColor getColorback() {
		return colorback;
	}

	public void setColorback(LColor colorback) {
		this.colorback = colorback;
	}

	public LColor getColorbefore() {
		return colorbefore;
	}

	public void setColorbefore(LColor colorbefore) {
		this.colorbefore = colorbefore;
	}

	public LColor getColorafter() {
		return colorafter;
	}

	public void setColorafter(LColor colorafter) {
		this.colorafter = colorafter;
	}
}
