package loon.action.sprite;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;

public class StatusBar extends Entity {

	private static final long serialVersionUID = 1L;

	private final static IntMap<LTexture> colors = new IntMap<LTexture>(10);

	private final static float[] backPos = { 0, 0, 3, 3 };

	private final static float[] beforePos = { 5, 0, 7, 3 };

	private final static float[] afterPos = { 0, 5, 3, 7 };

	private static int quoteCount = 0;

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
		quoteCount++;
		this.value = value;
		this.valueMax = max;
		this.valueMin = value;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
		this.setWidth(width);
		this.setHeight(height);
		this.hit = true;
		this.setTexture(loadBarColor(LColor.gray, LColor.red, LColor.orange));
		this.setLocation(x, y);
		this.setRepaint(true);
	}

	/**
	 * 顺序为背景，前景，中景
	 * 
	 * @param c1
	 * @param c2
	 * @param c3
	 * @return
	 */
	public LTexture loadBarColor(LColor c1, LColor c2, LColor c3) {
		if (colors.size > 10) {
			synchronized (colors) {
				for (LTexture tex2d : colors.values()) {
					if (tex2d != null) {
						tex2d.close();
						tex2d = null;
					}
				}
				colors.clear();
			}
		}
		int hash = 1;
		hash = LSystem.unite(hash, c1.getRGB());
		hash = LSystem.unite(hash, c2.getRGB());
		hash = LSystem.unite(hash, c3.getRGB());
		LTexture texture = null;
		synchronized (colors) {
			texture = colors.get(hash);
		}
		if (texture == null) {
			Canvas g = LSystem.base().graphics().createCanvas(8, 8);
			g.setColor(c1);
			g.fillRect(0, 0, 4, 4);
			g.setColor(c2);
			g.fillRect(4, 0, 4, 4);
			g.setColor(c3);
			g.fillRect(0, 4, 4, 4);
			g.close();
			texture = g.toTexture();
			if (g.image != null) {
				g.image.close();
			}
			colors.put(hash, texture);
		}
		return (this._image = texture);
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
			g.draw(_image, x, y, _width, _height, backPos[0], backPos[1],
					backPos[2], backPos[3]);
		}
		if (valueMin < value) {
			if (cv1 == _width) {
				g.draw(_image, x, y, cv1, _height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			} else {
				if (!dead) {
					g.draw(_image, x, y, cv2, _height, afterPos[0],
							afterPos[1], afterPos[2], afterPos[3]);
				}
				g.draw(_image, x, y, cv1, _height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			}
		} else {
			if (cv2 == _width) {
				g.draw(_image, x, y, cv2, _height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			} else {
				g.draw(_image, x, y, cv1, _height, afterPos[0], afterPos[1],
						afterPos[2], afterPos[3]);
				g.draw(_image, x, y, cv2, _height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
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
		drawBar(g, goal, current, _width, getX(), getY());
		if (showValue) {
			hpString = String.valueOf(value);
			int current = g.getFont().stringWidth(hpString);
			int h = g.getFont().getHeight();
			g.drawString(hpString, (x() + _width / 2 - current / 2) + 2, (y()
					+ _height / 2 - h), fontColor);
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
		this.valueMax = valueMax;
		this.current = (int) ((_width * value) / valueMax);
		this.goal = (int) ((_width * valueMin) / valueMax);
		this.state();
	}

	public int getMinValue() {
		return valueMin;
	}

	public void setMinValue(int valueMin) {
		this.valueMin = valueMin;
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

	@Override
	public void close() {
		super.close();
		synchronized (colors) {
			quoteCount--;
			if (quoteCount <= 0) {
				if (colors != null) {
					for (LTexture tex2d : colors.values()) {
						if (tex2d != null) {
							tex2d.close();
							tex2d = null;
						}
					}
					colors.clear();
				}
			}
		}
	}
}
