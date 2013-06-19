package loon.action.sprite.effect;

import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;
import loon.utils.MathUtils;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
/**
 * 0.3.2版新增类，单一色彩的圆弧渐变特效
 */
public class ArcEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
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

	@Override
	public void createUI(GLEx g) {
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
			float length = MathUtils.sqrt(MathUtils.pow(width / 2f, 2.0f)
					+ MathUtils.pow(height / 2f, 2.0f));
			float x = getX() + (width / 2 - length);
			float y = getY() + (height / 2 - length);
			float w = width / 2 + length - x;
			float h = height / 2 + length - y;
			float deg = 360f / this.div * this.count;
			g.fillArc(x, y, w, h, 20, 0, this.sign[this.turn] * deg);
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

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
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
	public void dispose() {

	}

}
