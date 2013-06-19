package loon.action.sprite;

import loon.core.LObject;
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
public class Blood extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class Drop {
		public float x, y, xspeed, yspeed;
	}

	private float xSpeed, ySpeed;

	private LTimer timer;

	private int step, limit;

	private Drop[] drops;

	private boolean visible;

	private LColor color;

	public Blood(int x, int y) {
		this(LColor.red, x, y);
	}

	public Blood(LColor c, int x, int y) {
		this.setLocation(x, y);
		this.color = c;
		this.timer = new LTimer(20);
		this.drops = new Drop[20];
		this.limit = 50;
		for (int i = 0; i < drops.length; ++i) {
			setBoolds(i, x, y, 6.f * (MathUtils.random() - 0.5f), -2.0f
					* MathUtils.random());
		}
		this.xSpeed = 0F;
		this.ySpeed = 0.5F;
		this.step = 0;
		this.visible = true;
	}

	public void setBoolds(int index, float x, float y, float xs, float ys) {
		if (index > drops.length - 1) {
			return;
		}
		drops[index] = new Drop();
		drops[index].x = x;
		drops[index].y = y;
		drops[index].xspeed = xs;
		drops[index].yspeed = ys;
	}

	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			for (int i = 0; i < drops.length; ++i) {
				drops[i].xspeed += xSpeed;
				drops[i].yspeed += ySpeed;
				drops[i].x -= drops[i].xspeed;
				drops[i].y += drops[i].yspeed;
			}
			step++;
			if (step > limit) {
				this.visible = false;
			}
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		g.setColor(color);
		for (int i = 0; i < drops.length; ++i) {
			g.fillOval((int) drops[i].x, (int) drops[i].y, 2, 2);
		}
		g.resetColor();
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1);
		}
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public RectBox getCollisionBox() {
		return null;
	}

	public float getXSpeed() {
		return xSpeed;
	}

	public void setXSpeed(float speed) {
		this.xSpeed = speed;
	}

	public float getYSpeed() {
		return ySpeed;
	}

	public void setYSpeed(float speed) {
		this.ySpeed = speed;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
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
