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
import loon.canvas.LColor;
import loon.opengl.GLEx;

/**
 * 折角样黑幕过渡效果
 */
public class FadeSwipeEffect extends BaseAbstractEffect {

	protected int _type;

	protected float triangle = 90;

	public static FadeSwipeEffect create(int type, LColor c) {
		return create(type, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public static FadeSwipeEffect create(int type, int timer, LColor c) {
		return new FadeSwipeEffect(c, timer, type, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public static FadeSwipeEffect create(int type, LColor c, int w, int h) {
		return new FadeSwipeEffect(c, 1000, type, w, h);
	}

	public FadeSwipeEffect(int type, LColor c) {
		this(c, 1000, type, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public FadeSwipeEffect(LColor c, long delay, int type, int w, int h) {
		this._type = type;
		this._timer.setDelay(delay);
		this.setColor(c);
		this.setSize(w, h);
		this.setRepaint(true);
	}

	public int getEffectType() {
		return _type;
	}

	public FadeSwipeEffect setEffectType(int type) {
		this._type = type;
		return this;
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (completedAfterBlackScreen(g, sx, sy)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(sx), drawY(sy), _width, _height, _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		float percent = _timer.getPercentage();
		final int tmp = g.color();
		if (_type == TYPE_FADE_IN) {
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
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			_completed = true;
		}
	}

	public int getFadeType() {
		return _type;
	}

	public float getTriangle() {
		return triangle;
	}

	public FadeSwipeEffect setTriangle(float triangle) {
		this.triangle = triangle;
		return this;
	}

	@Override
	public FadeSwipeEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(true);
		return this;
	}
}
