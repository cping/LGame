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
import loon.utils.TArray;

/**
 * PixelBaseEffect效果包含一系列不需要图片的,也不必依赖Shader的,可以直接使用在任意游戏中的效果.
 * 
 * PS:此像素非真像素,而是指'像素风格',实际还是三角形纹理贴图效果……
 *
 */
public abstract class PixelBaseEffect extends BaseAbstractEffect {

	protected TArray<TriangleEffect[]> _triangleEffects = new TArray<TriangleEffect[]>();

	protected float[] _startLocation;

	protected float[] _targetLocation;

	protected int _frame;

	protected int _limit;

	private int _counter;

	public abstract void draw(GLEx g, float tx, float ty);

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2) {
		this(c, x1, y1, x2, y2, 10, 90);
	}

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2, long delay, int limit) {
		this.reset();
		this.setEffectPosition(x1, y1, x2, y2);
		this.setSize(x2, y2);
		this.setColor(c);
		this.setDelay(delay);
		this._limit = limit;
		this._frame = 0;
		this._completed = false;
		this._counter = 1;
		this.setRepaint(true);
		this.setDeform(false);
	}

	public PixelBaseEffect setEffectDelay(long timer) {
		for (TriangleEffect[] ts : _triangleEffects) {
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
		this._startLocation = new float[2];
		this._targetLocation = new float[2];
		this._frame = 0;
		return this;
	}

	public PixelBaseEffect setEffectPosition(float x1, float y1, float x2, float y2) {
		this._startLocation[0] = x1;
		this._startLocation[1] = y1;
		this._targetLocation[0] = x2;
		this._targetLocation[1] = y2;
		return this;
	}

	public PixelBaseEffect setCounter(int c) {
		this._counter = c;
		return this;
	}

	public int getCounter() {
		return this._counter;
	}

	public float next() {
		return next(this._counter);
	}

	public float next(int c) {
		this._frame += c;
		for (TriangleEffect[] ts : _triangleEffects) {
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
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
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
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			next(MathUtils.iceil(this._counter * LSystem.getScaleFPS()));
		}
	}

	public int getLimit() {
		return _limit;
	}

	public PixelBaseEffect setLimit(int limit) {
		this._limit = limit;
		return this;
	}

	@Override
	public PixelBaseEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		_triangleEffects.clear();
		_startLocation = null;
		_targetLocation = null;
	}

}
