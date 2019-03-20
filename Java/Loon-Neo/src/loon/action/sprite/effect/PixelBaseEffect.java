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
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 此像素非真像素,而是指'像素风格',实际是三角形纹理贴图效果……
 *
 */
public abstract class PixelBaseEffect extends Entity {

	protected boolean completed;

	protected TArray<TriangleEffect[]> triangleEffects = new TArray<TriangleEffect[]>();

	protected float[] start;

	protected float[] target;

	protected int frame;

	protected LTimer timer;

	protected int limit = 90;

	public abstract void draw(GLEx g, float tx, float ty);

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2) {
		this.reset();
		this.setEffectPosition(x1, y1, x2, y2);
		this.setSize(x2,y2);
		this.setColor(c);
		this.timer = new LTimer(10);
		this.frame = 0;
		this.completed = false;
		this.setRepaint(true);
		this.setDeform(false);
	}

	public void setEffectDelay(long timer) {
		for (TriangleEffect[] ts : triangleEffects) {
			if (ts != null) {
				int size = ts.length;
				for (int i = 0; i < size; i++) {
					if (ts[i] != null) {
						ts[i].setDelay(timer);
					}
				}
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		this.start = new float[2];
		this.target = new float[2];
		this.frame = 0;
	}

	public void setEffectPosition(float x1, float y1, float x2, float y2) {
		this.start[0] = x1;
		this.start[1] = y1;
		this.target[0] = x2;
		this.target[1] = y2;
	}

	public float next() {
		this.frame++;
		for (TriangleEffect[] ts : triangleEffects) {
			if (ts != null) {
				int size = ts.length;
				for (int i = 0; i < size; i++) {
					if (ts[i] != null) {
						_rotation = ts[i].next();
					}
				}
			}
		}
		return _rotation;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		int tmp = g.getPixSkip();
		boolean useTex = g.isAlltextures() && LSystem.isHTML5();
		if (useTex) {
			g.setPixSkip(4);
		}
		draw(g, drawX(offsetX), drawY(offsetY));
		if (useTex) {
			g.setPixSkip(tmp);
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
				next();
			}
		}
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}
