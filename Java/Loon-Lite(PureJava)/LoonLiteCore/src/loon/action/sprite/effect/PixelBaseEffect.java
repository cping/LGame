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

import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * PixelBaseEffect效果包含一系列不需要图片的,也不必依赖Shader的,可以直接使用在任意游戏中的效果.
 * 
 * PS:此像素非真像素,而是指'像素风格',实际还是三角形纹理贴图效果……
 *
 */
public abstract class PixelBaseEffect extends Entity {

	protected TArray<TriangleEffect[]> triangleEffects = new TArray<TriangleEffect[]>();

	protected boolean completed;

	protected boolean autoRemoved;

	protected float[] startLocation;

	protected float[] targetLocation;

	protected int frame;

	protected LTimer timer;

	protected int limit;

	public abstract void draw(GLEx g, float tx, float ty);

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2) {
		this(c, x1, y1, x2, y2, 10, 90);
	}

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2, long delay, int limit) {
		this.reset();
		this.setEffectPosition(x1, y1, x2, y2);
		this.setSize(x2, y2);
		this.setColor(c);
		this.timer = new LTimer(delay);
		this.limit = limit;
		this.frame = 0;
		this.completed = false;
		this.setRepaint(true);
		this.setDeform(false);
	}

	public PixelBaseEffect setDelay(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public PixelBaseEffect setEffectDelay(long timer) {
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
		return this;
	}

	@Override
	public PixelBaseEffect reset() {
		super.reset();
		this.startLocation = new float[2];
		this.targetLocation = new float[2];
		this.frame = 0;
		return this;
	}

	public PixelBaseEffect setEffectPosition(float x1, float y1, float x2, float y2) {
		this.startLocation[0] = x1;
		this.startLocation[1] = y1;
		this.targetLocation[0] = x2;
		this.targetLocation[1] = y2;
		return this;
	}

	public float next() {
		this.frame++;
		for (TriangleEffect[] ts : triangleEffects) {
			if (ts != null) {
				int size = ts.length;
				for (int i = 0; i < size; i++) {
					TriangleEffect te = ts[i];
					if (te != null) {
						_objectRotation = te.next();
					}
				}
			}
		}
		return _objectRotation;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (!isVisible()) {
			return;
		}
		draw(g, drawX(offsetX), drawY(offsetY));
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
				next();
			}
		} else {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public int getLimit() {
		return limit;
	}

	public PixelBaseEffect setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public boolean isCompleted() {
		return completed;
	}

	public PixelBaseEffect setStop(boolean c) {
		this.completed = c;
		return this;
	}
	
	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public PixelBaseEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
		triangleEffects.clear();
		startLocation = null;
		targetLocation = null;
	}

}
