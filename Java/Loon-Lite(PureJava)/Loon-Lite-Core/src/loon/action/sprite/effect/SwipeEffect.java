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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 折角样黑幕过渡效果
 */
public class SwipeEffect extends Entity implements BaseEffect {

	private LTimer timer = new LTimer(450);

	protected int type;

	protected float triangle = 90;

	protected boolean finished;

	protected boolean autoRemoved;

	public static SwipeEffect getInstance(int type, LColor c) {
		return getInstance(type, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public static SwipeEffect getInstance(int type, int timer, LColor c) {
		return new SwipeEffect(c, timer, type, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public static SwipeEffect getInstance(int type, LColor c, int w, int h) {
		return new SwipeEffect(c, 3000, type, w, h);
	}

	public SwipeEffect(int type, LColor c) {
		this(c, 3000, type, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public SwipeEffect(LColor c, int delay, int type, int w, int h) {
		this.type = type;
		this.timer.setDelay(delay);
		this.setColor(c);
		this.setSize(w, h);
		this.setRepaint(true);
	}

	@Override
	public boolean isCompleted() {
		return finished;
	}

	public void setStop(boolean finished) {
		this.finished = finished;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (finished) {
			return;
		}
		float percent = timer.getPercentage();

		LColor tmp = g.getColor();

		if (type == TYPE_FADE_IN) {
			float width = getWidth() + (2 * triangle);
			float height = getHeight();
			float x = drawX(sx + (percent * width - triangle));
			float y = drawY(sy + 0);
			g.setColor(_baseColor);
			g.fillRect(x + triangle, y, width, height);
			g.fillTriangle(x, height, x + triangle, height, x + triangle, y);
		} else {
			float x = drawX(sx + (percent * (triangle + getWidth()) - triangle));
			float y = drawY(sy + 0);
			float width = percent * (getWidth() + triangle);
			float height = getHeight();
			g.setColor(_baseColor);
			g.fillRect(-triangle, y, width, height);
			g.fillTriangle(x, y, x + triangle, y, x, height);
		}

		g.setColor(tmp);

		return;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			finished = true;
		}
		if (this.finished) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public int getFadeType() {
		return type;
	}

	public float getTriangle() {
		return triangle;
	}

	public SwipeEffect setTriangle(float triangle) {
		this.triangle = triangle;
		return this;
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public SwipeEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		finished = true;
	}

}
