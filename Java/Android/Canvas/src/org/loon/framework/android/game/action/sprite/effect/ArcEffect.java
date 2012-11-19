package org.loon.framework.android.game.action.sprite.effect;

import org.loon.framework.android.game.action.sprite.ISprite;
import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimer;

import android.graphics.Bitmap;

/**
 * Copyright 2008 - 2011
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
/**
 * 单一色彩的圆弧渐变特效
 */
public class ArcEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private int count;

	private int div = 10;

	private int turn = 1;

	private int[] sign = { 1, -1 };

	private int width, height;

	private LColor color;

	private boolean visible, complete;

	private LTimer timer;

	public ArcEffect(LColor c) {
		this(c, 0, 0, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public ArcEffect(LColor c, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.timer = new LTimer(200);
		this.color = c == null ? LColor.black : c;
		this.visible = true;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public boolean isComplete() {
		return complete;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (complete) {
			return;
		}
		if (this.count >= this.div) {
			this.complete = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (complete) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		if (count <= 1) {
			g.setColor(color);
			g.fillRect(x(), y(), width, height);
			g.resetColor();
		} else {
			g.setColor(color);
			int length = (int) Math.sqrt(Math.pow(width / 2, 2.0f)
					+ Math.pow(height / 2, 2.0f));
			int x = x() + (width / 2 - length);
			int y = y() + (height / 2 - length);
			int w = width / 2 + length - x;
			int h = height / 2 + length - y;
			int deg = 360 / this.div * this.count;
			g.fillArc(x, y, w, h, 0, -360 - (this.sign[this.turn] * deg));
			g.resetColor();
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		this.complete = false;
		this.count = 0;
		this.turn = 1;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public Bitmap getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void dispose() {

	}

}
