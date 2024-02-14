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
import loon.utils.IntArray;
import loon.utils.MathUtils;

/**
 * 锯齿状关门与开门效果
 */
public class FadeDoorIrregularEffect extends BaseAbstractEffect {

	private final IntArray _horizontalgrids = new IntArray();

	private final IntArray _tmpInts = new IntArray();

	private float _divValue = 2f;

	private int _maxSize;

	private int _type;

	private int _step;

	private int _boardWidth;

	private int _count;

	public FadeDoorIrregularEffect(int t, LColor color) {
		this(t, color, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public FadeDoorIrregularEffect(int t, LColor color, float w, float h) {
		this(t, color, w, h, 0, 12);
	}

	public FadeDoorIrregularEffect(int t, LColor color, float w, float h, long delay, int step) {
		super();
		this.setColor(color);
		this.setDelay(delay);
		this.setSize(w, h);
		this.setRepaint(true);
		this.pack();
		this._type = t;
		this._step = step;
	}

	private void pack() {
		final int maxTileSize = MathUtils.iceil(getWidth() / _divValue);
		final int tileSize = maxTileSize / 6;
		int tileValue = 0;
		_tmpInts.clear();
		for (; tileValue < maxTileSize;) {
			_tmpInts.add(tileValue);
			tileValue += tileSize;
		}
		_horizontalgrids.clear();
		int max = MathUtils.iceil((getHeight() / tileSize));
		for (int i = 0; i < max; i++) {
			final boolean v = MathUtils.randomBoolean();
			tileValue = _tmpInts.random();
			_horizontalgrids.add(v ? tileValue : -tileValue);
		}
		this._maxSize = _horizontalgrids.max();
		this._boardWidth = MathUtils.iceil(getWidth() / _horizontalgrids.length);
	}

	@Override
	public FadeDoorIrregularEffect reset() {
		super.reset();
		this.pack();
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			_count += _step;
			_completed = (_count >= getWidth() + (_maxSize * _divValue + _step));
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), getWidth(), getHeight(), _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		final float left = getCenterX();
		final float top = getCenterY();
		final float dx = drawX(offsetX);
		final float dy = drawY(offsetY);
		final float halfWidth = getWidth() / _divValue;
		final float halfHeight = getHeight() / _divValue;
		switch (_type) {
		case TYPE_FADE_IN:
			for (int i = 0; i < _horizontalgrids.length; i++) {
				final float board = this._horizontalgrids.get(i);
				final float x1 = left + dx - halfWidth;
				final float y1 = top + dy + (i * _boardWidth - halfHeight);
				final float w1 = halfWidth + board - this._count / _divValue;
				final float h1 = _boardWidth;
				g.fillRect(x1, y1, w1, h1, _baseColor);
				final float x2 = left + dx + (board + this._count / _divValue);
				final float y2 = top + dy + (i * _boardWidth - halfHeight);
				final float w2 = getWidth() - board - this._count / _divValue;
				final float h2 = _boardWidth;
				g.fillRect(x2, y2, w2, h2, _baseColor);
			}
			break;
		case TYPE_FADE_OUT:
			for (int i = 0; i < _horizontalgrids.length; i++) {
				final float board = this._horizontalgrids.get(i);
				final float x1 = dx + (left + (halfWidth));
				final float y1 = dy + top + (i * _boardWidth - halfHeight);
				final float w1 = halfWidth + board - this._count / _divValue;
				final float h1 = _boardWidth;
				g.fillRect(x1, y1, w1, h1, _baseColor);
				final float x2 = dx - getWidth() / 2 + (board + this._count / _divValue);
				final float y2 = dy + top + (i * _boardWidth - halfHeight);
				final float w2 = -halfWidth + (-board - this._count / _divValue);
				final float h2 = _boardWidth;
				g.fillRect(x2, y2, w2, h2, _baseColor);
			}
			break;
		}
	}

	public int getEffectType() {
		return _type;
	}

	public int getStep() {
		return _step;
	}

	public FadeDoorIrregularEffect setStep(int s) {
		this._step = s;
		return this;
	}

	public float getDivValue() {
		return _divValue;
	}

	public FadeDoorIrregularEffect setDivValue(float d) {
		this._divValue = d;
		return this;
	}

	@Override
	public FadeDoorIrregularEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
