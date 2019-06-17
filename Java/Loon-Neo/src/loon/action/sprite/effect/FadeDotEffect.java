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
import loon.action.sprite.Entity;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 黑幕过渡效果,一组圆形逐渐染黑屏幕或者黑幕逐渐缩小为一群圆并最终缩小不见
 */
public class FadeDotEffect extends Entity implements BaseEffect {

	private int countCompleted = 0;

	private boolean autoRemoved;

	private boolean finished;

	private TArray<Dot> dots;

	private int count = 4;

	private int type = ISprite.TYPE_FADE_IN;

	private int dot_time = 0;
	private int dot_rad = 0;
	private int dot_width = 0;
	private int dot_height = 0;

	private LTimer timer = new LTimer(0);

	private static class Dot {

		private float x;

		private float y;

		private float growSpeed;

		private float rad;

		private int type;

		private boolean finished, fade_allowed;

		private float currentFrame;

		private float time;

		public Dot(int type, int time, int rad, int w, int h) {
			this.type = type;
			if (time <= -1) {
				fade_allowed = true;
			}
			this.time = time;
			if (type == ISprite.TYPE_FADE_IN) {
				if (rad < 0 || rad > 360) {
					// 360速度较慢，越界的话索性改成260……
					rad = 260;
				}
				this.rad = rad;
				this.currentFrame = time;
			} else {
				if (rad < 0) {
					rad = 0;
				}
				this.rad = 0;
				this.currentFrame = 0;
			}
			x = (MathUtils.random(0, 1f) * w);
			y = (MathUtils.random(0, 1f) * h);
			growSpeed = 1f + (MathUtils.random(0, 1f));
		}

		public void update(long elapsedTime) {
			if (type == ISprite.TYPE_FADE_IN) {
				currentFrame--;
				rad -= growSpeed * (elapsedTime / 10) * 0.6f;
				if (rad <= 0) {
					rad = 0;
					finished = true;
				}
			} else {
				currentFrame++;
				rad += growSpeed * (elapsedTime / 10) * 0.4f;
				if (rad >= 360) {
					rad = 360;
					finished = true;
				}
			}
		}

		public void paint(GLEx g, float offsetX, float offsetY) {
			if (rad > 0 && rad < 360) {
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

	public FadeDotEffect(LColor c, int type, int time, int count) {
		this(type, time, time, count, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public FadeDotEffect(LColor c) {
		this(ISprite.TYPE_FADE_IN, 280, c);
	}

	public FadeDotEffect(int type, LColor c) {
		this(type, 280, c);
	}

	public FadeDotEffect(int type, int time, LColor c) {
		this(type, time, time, 5, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public FadeDotEffect(int type, int time, int rad, int count, LColor c, int w, int h) {
		this.type = type;
		this.count = count;
		this.dot_time = time;
		this.dot_rad = rad;
		this.dot_width = w;
		this.dot_height = h;
		this.setColor(c);
		this.setSize(w, h);
		this.setRepaint(true);
		this.updateDots();
	}

	protected void updateDots() {
		if (dots != null) {
			dots.clear();
			dots = null;
		}
		dots = new TArray<Dot>();
		for (int i = 0; i < count; i++) {
			dots.add(new Dot(type, dot_time, dot_rad, dot_width, dot_height));
		}
		finished = false;
	}

	@Override
	public void reset() {
		super.reset();
		updateDots();
	}

	public float getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	@Override
	public boolean isCompleted() {
		return finished;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (finished) {
			return;
		}
		if (timer.action(elapsedTime)) {
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
				finished = true;
			}
		}
		if (this.finished) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}
	
	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (finished) {
			return;
		}
		if (finished) {
			return;
		}
		boolean useText = g.isAlltextures() && LSystem.isHTML5();
		int skip = g.getPixSkip();
		if (useText) {
			g.setPixSkip(10);
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		for (int i = 0; i < dots.size; i++) {
			((Dot) dots.get(i)).paint(g, drawX(offsetX), drawY(offsetY));
		}
		if (useText) {
			g.setPixSkip(skip);
		}
		g.setColor(tmp);
	}

	public int getCount() {
		return count;
	}

	public int getFadeType() {
		return type;
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public FadeDotEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}
	
	@Override
	public void close() {
		super.close();
		finished = true;
		if (dots != null) {
			dots.clear();
			dots = null;
		}
	}

}
