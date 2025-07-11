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
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 黑幕过渡效果,画面变成圆形扩散或由圆形向中心集中最终消失
 */
public class FadeOvalEffect extends BaseAbstractEffect {

	private final LColor[] oval_colors;

	private float max_time;
	private float elapsed;

	private int type = TYPE_FADE_IN;
	private int maxColorSize;
	private float _initWidth;
	private float _initHeight;

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
		this.setSize(w, h);
		this.setColor(oc);
		this.setRepaint(true);
		this.type = type;
		this.elapsed = 0;
		this._initWidth = w;
		this._initHeight = h;
		this.maxColorSize = maxSize;
		this.oval_colors = new LColor[maxColorSize];
		for (int i = 0; i < maxColorSize; i++) {
			oval_colors[i] = new LColor(oc.r, oc.g, oc.b, 1F - 0.15f * i);
		}
		this.max_time = time;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			if (type == TYPE_FADE_IN) {
				this.elapsed += (elapsedTime * 3) / LSystem.getScaleFPS();
				float progress = this.elapsed / this.max_time;
				this._width = (_width * MathUtils.pow(1f - progress, 2f));
				this._height = (_height * MathUtils.pow(1f - progress, 2f));
				if (this.elapsed >= this.max_time) {
					this.elapsed = -1;
					this._width = (this._height = 0f);
					this._completed = true;
				}
			} else {
				this.elapsed += (elapsedTime * 3) / LSystem.getScaleFPS();
				float progress = this.elapsed / this.max_time;
				this._width = (_initWidth * MathUtils.pow(progress, 2f));
				this._height = (_initHeight * MathUtils.pow(progress, 2f));
				if (this.elapsed >= this.max_time) {
					this.elapsed = -1;
					this._width = (this._height = MathUtils.max(_initWidth, _initHeight));
					this._completed = true;
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (completedAfterBlackScreen(g, sx, sy)) {
			return;
		}
		if (this.elapsed > -1) {
			int old = g.color();
			int size = maxColorSize;
			for (int i = size - 1; i >= 0; i--) {
				g.setColor(oval_colors[i]);
				float w = this._width + i * this._width * 0.1f;
				float h = this._height + i * this._height * 0.1f;
				g.fillOval(drawX((g.getWidth() / 2 - w / 2f) + sx), drawY((g.getHeight() / 2 - h / 2f) + sy), w, h);
			}
			g.setColor(old);
		}
	}

	public int getFadeType() {
		return type;
	}

	@Override
	public FadeOvalEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
