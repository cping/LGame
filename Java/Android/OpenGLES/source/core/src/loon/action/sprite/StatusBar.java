package loon.action.sprite;

import java.util.HashMap;

import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;


/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
public class StatusBar extends LObject implements ISprite {

	private static final long serialVersionUID = 1L;

	protected final static HashMap<Integer, LTexture> colors = new HashMap<Integer, LTexture>(
			10);

	private final static int[] backPos = { 1, 1, 3, 3 };

	private final static int[] beforePos = { 5, 1, 7, 3 };

	private final static int[] afterPos = { 1, 5, 3, 7 };

	private static int quoteCount = 0;

	protected boolean hit, visible, showValue, dead;

	private int width, height;

	private int value, valueMax, valueMin;

	private int current, goal;

	private String hpString;

	private LTexture texture;

	private static boolean useBegin;

	public StatusBar(int width, int height) {
		this(0, 0, width, height);
	}

	public StatusBar(int x, int y, int width, int height) {
		this(100, 100, x, y, width, height);
	}

	public StatusBar(int value, int max, int x, int y, int width, int height) {
		synchronized (StatusBar.class) {
			quoteCount++;
		}
		this.value = value;
		this.valueMax = max;
		this.valueMin = value;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
		this.width = width;
		this.height = height;
		this.visible = true;
		this.hit = true;
		this.texture = loadBarColor(LColor.gray, LColor.red, LColor.orange);
		this.setLocation(x, y);
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
		if (colors.size() > 10) {
			synchronized (colors) {
				for (LTexture tex2d : colors.values()) {
					if (tex2d != null) {
						tex2d.destroy();
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
			LImage image = new LImage(8, 8, false);
			LGraphics g = image.getLGraphics();
			g.setColor(c1);
			g.fillRect(0, 0, 4, 4);
			g.setColor(c2);
			g.fillRect(4, 0, 4, 4);
			g.setColor(c3);
			g.fillRect(0, 4, 4, 4);
			g.dispose();
			texture = image.getTexture();
			colors.put(hash, texture);
		}
		return (this.texture = texture);
	}

	public void set(int v) {
		this.value = v;
		this.valueMax = v;
		this.valueMin = v;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
	}

	public void empty() {
		this.value = 0;
		this.valueMin = 0;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
	}

	public static void glBegin() {
		synchronized (colors) {
			for (LTexture tex2d : colors.values()) {
				if (tex2d != null) {
					tex2d.glBegin();
				}
			}
			useBegin = true;
		}
	}

	public static void glEnd() {
		synchronized (colors) {
			for (LTexture tex2d : colors.values()) {
				if (tex2d != null) {
					tex2d.glEnd();
				}
			}
			useBegin = false;
		}
	}

	private void drawBar(GLEx g, float v1, float v2, float size, float x,
			float y) {
		float cv1 = (width * v1) / size;
		float cv2;
		if (v1 == v2) {
			cv2 = cv1;
		} else {
			cv2 = (width * v2) / size;
		}
		if (!useBegin) {
			texture.glBegin();
		}
		if (cv1 < width || cv2 < height) {
			texture.draw(x, y, width, height, backPos[0], backPos[1],
					backPos[2], backPos[3]);
		}
		if (valueMin < value) {
			if (cv1 == width) {
				texture.draw(x, y, cv1, height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			} else {
				if (!dead) {
					texture.draw(x, y, cv2, height, afterPos[0], afterPos[1],
							afterPos[2], afterPos[3]);
				}
				texture.draw(x, y, cv1, height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			}
		} else {
			if (cv2 == width) {
				texture.draw(x, y, cv2, height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			} else {
				texture.draw(x, y, cv1, height, afterPos[0], afterPos[1],
						afterPos[2], afterPos[3]);
				texture.draw(x, y, cv2, height, beforePos[0], beforePos[1],
						beforePos[2], beforePos[3]);
			}
		}
		if (!useBegin) {
			texture.glEnd();
		}
	}

	public void updateTo(int v1, int v2) {
		this.setValue(v1);
		this.setUpdate(v2);
	}

	public void setUpdate(int val) {
		valueMin = MathUtils.mid(0, val, valueMax);
		current = (width * value) / valueMax;
		goal = (width * valueMin) / valueMax;
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
					((current * valueMax) / width), value);
		} else {
			current++;
			value = MathUtils.mid(value, ((current * valueMax) / width),
					valueMin);
		}
		return true;
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			if (showValue) {
				hpString = "" + value;
				g.setColor(LColor.white);
				int current = g.getFont().stringWidth(hpString);
				int h = g.getFont().getSize();
				g.drawString("" + value, (x() + width / 2 - current / 2) + 2,
						(y() + height / 2 + h / 2));
			}
			drawBar(g, goal, current, width, getX(), getY());
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isShowHP() {
		return showValue;
	}

	public void setShowHP(boolean showHP) {
		this.showValue = showHP;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void update(long elapsedTime) {
		if (visible && hit) {
			state();
		}
	}

	public int getMaxValue() {
		return valueMax;
	}

	public void setMaxValue(int valueMax) {
		this.valueMax = valueMax;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
		this.state();
	}

	public int getMinValue() {
		return valueMin;
	}

	public void setMinValue(int valueMin) {
		this.valueMin = valueMin;
		this.current = (width * value) / valueMax;
		this.goal = (width * valueMin) / valueMax;
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
	public LTexture getBitmap() {
		return texture;
	}

	@Override
	public void dispose() {
		synchronized (colors) {
			quoteCount--;
			if (quoteCount <= 0) {
				if (colors != null) {
					for (LTexture tex2d : colors.values()) {
						if (tex2d != null) {
							tex2d.destroy();
							tex2d = null;
						}
					}
					colors.clear();
				}
			}
		}
	}
}
