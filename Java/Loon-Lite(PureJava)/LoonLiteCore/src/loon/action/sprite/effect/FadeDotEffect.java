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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 黑幕过渡效果,一组圆形逐渐染黑屏幕或者黑幕逐渐缩小为一群圆并最终缩小不见
 */
public class FadeDotEffect extends BaseAbstractEffect {

	private static class Dot {

		private float x;

		private float y;

		private float growSpeed;

		private float rad;

		private int type;

		private boolean finished, fade_allowed;

		private float currentFrame;

		private float time;

		public Dot(int type, int time, int rad, int w, int h, float sleep) {
			this.type = type;
			if (time <= -1) {
				fade_allowed = true;
			}
			this.time = time;
			if (type == ISprite.TYPE_FADE_IN) {
				if (rad <= 0 || rad >= 360) {
					// 360速度较慢，越界的话索性改成260……
					rad = 260;
				}
				this.rad = rad;
				this.currentFrame = time;
			} else {
				if (rad <= 0) {
					rad = 0;
				}
				this.rad = 0;
				this.currentFrame = 0;
			}
			x = (MathUtils.random(0, 1f) * w);
			y = (MathUtils.random(0, 1f) * h);
			growSpeed = MathUtils.max(sleep, 1f) + (MathUtils.random(0, 1f));
		}

		public void update(long elapsedTime) {
			if (type == ISprite.TYPE_FADE_IN) {
				currentFrame--;
				rad -= growSpeed * (elapsedTime / 5f) * 0.6f;
				if (rad <= 0) {
					rad = 0;
					finished = true;
				}
			} else {
				currentFrame++;
				rad += growSpeed * (elapsedTime / 5f) * 0.4f;
				if (rad >= 360) {
					rad = 360;
					finished = true;
				}
			}
		}

		public void paint(GLEx g, float offsetX, float offsetY) {
			if (rad >= 0 && rad <= 360) {
				float a = g.alpha();
				if (!fade_allowed) {
					float alpha = currentFrame / time;
					g.setAlpha(alpha);
				}
				g.fillOval(x - rad + offsetX, y - rad + offsetY, rad * 2, rad * 2);
				g.setAlpha(a);
			}
		}
	}

	private int countCompleted = 0;

	private TArray<Dot> dots;

	private int count = 4;

	private int _type = ISprite.TYPE_FADE_IN;

	private int dot_time = 0;
	private int dot_rad = 0;
	private int dot_width = 0;
	private int dot_height = 0;

	private float _sleep = 0;

	public FadeDotEffect(LColor c) {
		this(ISprite.TYPE_FADE_IN, 280, c);
	}

	public FadeDotEffect(int type, LColor c) {
		this(type, 280, c);
	}

	public FadeDotEffect(int type, int time, LColor c) {
		this(type, time, c, 5);
	}

	public FadeDotEffect(int type, int time, LColor c, int count) {
		this(type, time, c, count, 1f);
	}

	public FadeDotEffect(int type, int time, LColor c, int count, float sleep) {
		this(type, time, time, count, c, sleep);
	}

	public FadeDotEffect(int type, int time, int rad, int count, LColor c, float sleep) {
		this(type, time, rad, count, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), sleep);
	}

	public FadeDotEffect(int type, int time, int rad, int count, LColor c, int w, int h, float sleep) {
		this._type = type;
		this.count = count;
		this.dot_time = time;
		this.dot_rad = rad;
		this.dot_width = w;
		this.dot_height = h;
		this.setColor(c);
		this.setSize(w, h);
		this.setRepaint(true);
		this.setSleep(sleep);
		this.updateDots();
	}

	public FadeDotEffect setSleep(float s) {
		this._sleep = MathUtils.max(s, 1f);
		return this;
	}

	public float getSleep() {
		return this._sleep;
	}

	protected void updateDots() {
		if (dots != null) {
			dots.clear();
			dots = null;
		}
		dots = new TArray<Dot>();
		for (int i = 0; i < count; i++) {
			dots.add(new Dot(_type, dot_time, dot_rad, dot_width, dot_height, 1f));
		}
		_completed = false;
	}

	@Override
	public FadeDotEffect reset() {
		super.reset();
		updateDots();
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			for (int i = 0; i < dots.size; i++) {
				Dot dot = dots.get(i);
				if (dot != null) {
					dot.update(elapsedTime);
					if (dot.finished) {
						countCompleted++;
					}
				}
			}
			if (countCompleted >= dots.size) {
				_completed = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		for (int i = 0; i < dots.size; i++) {
			dots.get(i).paint(g, drawX(offsetX), drawY(offsetY));
		}
		g.setColor(tmp);
	}

	public int getCount() {
		return count;
	}

	public int getFadeType() {
		return _type;
	}

	@Override
	public FadeDotEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (dots != null) {
			dots.clear();
			dots = null;
		}
	}

}
