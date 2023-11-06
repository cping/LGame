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
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * 黑幕过渡效果,画面变成圆形扩散或由圆形向中心集中最终消失
 */
public class FadeOvalEffect extends Entity implements BaseEffect {

	private final LColor[] oval_colors;

	private float max_time;
	private LTimer timer;
	private float elapsed;
	private boolean finished = false;
	private boolean autoRemoved = false;

	private int type = TYPE_FADE_IN;
	private int maxColorSize;

	public FadeOvalEffect(int type, LColor color) {
		this(type, color, LSystem.viewSize.width, LSystem.viewSize.height);
	}

	public FadeOvalEffect(int type, float w, float h) {
		this(type, LColor.black, 2200, w, h);
	}

	public FadeOvalEffect(int type, LColor oc, float w, float h) {
		this(type, oc, 2200, w, h);
	}

	public FadeOvalEffect(int type, LColor oc, int time, float w, float h) {
		this(type, oc, time, w, h, 5);
	}

	public FadeOvalEffect(int type, LColor oc, int time, float w, float h, int maxSize) {
		this.type = type;
		this.elapsed = 0;
		this.setSize(w, h);
		this.setColor(oc);
		this.elapsed = 0;
		this.maxColorSize = maxSize;
		this.oval_colors = new LColor[maxColorSize];
		for (int i = 0; i < maxColorSize; i++) {
			oval_colors[i] = new LColor(oc.r, oc.g, oc.b, 1F - 0.15f * i);
		}
		this.max_time = time;
		this.timer = new LTimer(0);
		this.setRepaint(true);
	}

	public FadeOvalEffect setDelay(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public boolean isCompleted() {
		return finished;
	}

	@Override
	public FadeOvalEffect setStop(boolean finished) {
		this.finished = finished;
		return this;
	}
	
	@Override
	public void onUpdate(long elapsedTime) {
		if (finished) {
			return;
		}
		if (timer.action(elapsedTime)) {
			if (type == TYPE_FADE_IN) {
				this.elapsed += elapsedTime / 20f;
				float progress = this.elapsed / this.max_time;
				this._width = (_width * MathUtils.pow(1f - progress, 2f));
				this._height = (_height * MathUtils.pow(1f - progress, 2f));
				if (this.elapsed >= this.max_time / 15f) {
					this.elapsed = -1;
					this._width = (this._height = 0f);
					this.finished = true;
				}
			} else {
				this.elapsed += elapsedTime;
				float progress = this.elapsed / this.max_time;
				this._width = (LSystem.viewSize.width * MathUtils.pow(progress, 2f));
				this._height = (LSystem.viewSize.height * MathUtils.pow(progress, 2f));
				if (this.elapsed >= this.max_time) {
					this.elapsed = -1;
					this._width = (this._height = MathUtils.max(LSystem.viewSize.width, LSystem.viewSize.height));
					this.finished = true;
				}
			}
		}
		if (this.finished) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (finished) {
			return;
		}
		if (this.elapsed > -1) {
			int tmp = g.getPixSkip();
			boolean usetex = LSystem.isHTML5();
			if (usetex) {
				g.setPixSkip(10);
			}
			int old = g.color();
			int size = maxColorSize;
			for (int i = size - 1; i >= 0; i--) {
				g.setColor(oval_colors[i]);
				float w = this._width + i * this._width * 0.1f;
				float h = this._height + i * this._height * 0.1f;
				g.fillOval(drawX((g.getWidth() / 2 - w / 2f) + sx), drawY((g.getHeight() / 2 - h / 2f) + sy), w, h);
			}
			g.setColor(old);
			if (usetex) {
				g.setPixSkip(tmp);
			}
		}
	}

	public int getFadeType() {
		return type;
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public FadeOvalEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		this.finished = true;
	}

}
