package org.loon.framework.android.game.action.sprite;

import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.NumberUtils;

import android.graphics.Bitmap;


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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class StatusBar extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean hit, visible, showValue, dead;

	private int width, height;

	private int value, valueMax, valueMin;

	private float w, we;

	private String hpString;

	private LColor color;

	public StatusBar(int width, int height) {
		this(0, 0, width, height);
	}

	public StatusBar(int x, int y, int width, int height) {
		this(100, 100, x, y, width, height);
	}

	public StatusBar(int value, int max, int x, int y, int width, int height) {
		this.value = value;
		this.valueMax = max;
		this.valueMin = value;
		this.w = (float)(width * value) / valueMax;
		this.we = (float)(width * valueMin) / valueMax;
		this.width = width;
		this.height = height;
		this.visible = true;
		this.hit = true;
		this.setLocation(x, y);
		this.color = LColor.red;
	}

	public void set(int v) {
		this.value = v;
		this.valueMax = v;
		this.valueMin = v;
		this.w = (width * value) / valueMax;
		this.we = (width * valueMin) / valueMax;
	}

	public void empty() {
		this.value = 0;
		this.valueMin = 0;
		this.w = (width * value) / valueMax;
		this.we = (width * valueMin) / valueMax;
	}

	private void drawBar(LGraphics g, int i, int j, int k, int x, int y) {
		g.setColor(LColor.gray);
		g.fillRect(x, y, width, height);
		if (valueMin <= value) {
			if (!dead) {
				g.setColor(LColor.orange);
			}
			g.fillRect(x, y, (width * j) / k, height);
			g.setColor(color);
			g.fillRect(x, y, (width * i) / k, height);
		} else {
			g.setColor(LColor.orange);
			g.fillRect(x, y, (width * i) / k, height);
			g.setColor(color);
			g.fillRect(x, y, (width * j) / k, height);
		}
		g.setColor(LColor.white);
	}

	public void updateTo(int v1, int v2) {
		this.setValue(v1);
		this.setUpdate(v2);
	}

	public void setUpdate(int val) {
		valueMin = NumberUtils.mid(0, val, valueMax);
		w = (float) (width * value) / valueMax;
		we = (float) (width * valueMin) / valueMax;
	}

	public void setDead(boolean d) {
		this.dead = d;
	}

	public boolean state() {
		if (w == we)
			return false;
		if (w > we) {
			w--;
			value = NumberUtils.mid(valueMin, ((int) w * valueMax) / width,
					value);
		} else {
			w++;
			value = NumberUtils.mid(value, ((int) w * valueMax) / width,
					valueMin);
		}
		return true;
	}

	public void createUI(LGraphics g) {
		if (visible) {
			if (showValue) {
				hpString = "" + value;
				g.setColor(LColor.white);
				int w = g.getFont().stringWidth(hpString);
				int h = g.getFont().getSize();
				g.drawString("" + value, (x() + width / 2 - w / 2) + 2, (y()
						+ height / 2 + h / 2));
			}
			drawBar(g, (int) we, (int) w, width, x(), y());
		}
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isShowHP() {
		return showValue;
	}

	public void setShowHP(boolean showHP) {
		this.showValue = showHP;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

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
		this.w = (width * value) / valueMax;
		this.we = (width * valueMin) / valueMax;
		this.state();
	}

	public int getMinValue() {
		return valueMin;
	}

	public void setMinValue(int valueMin) {
		this.valueMin = valueMin;
		this.w = (width * value) / valueMax;
		this.we = (width * valueMin) / valueMax;
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

	public float getAlpha() {
		return 0;
	}

	public Bitmap getBitmap() {
		return null;
	}

	public void dispose() {
		
	}

}
