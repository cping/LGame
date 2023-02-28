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
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 像素化的气泡特效,制造气泡在指定范围内飘荡
 */
public class PixelBubbleEffect extends Entity implements BaseEffect {

	private boolean _completed;

	private TArray<Block> _bubbleBlocks;

	private boolean _dirty;

	private int _bubbleSize;

	private int _radius;

	private float _moveSpeed;

	private static class Block {

		float _speed;
		float _x;
		float _y;
		float _sign;
		float _alpha;
		int _amount;

		float _cellWidth;

		float _cellHeight;

		PixelBubbleEffect _effect;

		public Block(PixelBubbleEffect effect, float speed, float width, float x, float y) {
			this._effect = effect;
			this._speed = speed;
			this._x = x;
			this._y = y;
			this._alpha = 0.05f + MathUtils.random() * 0.8f;
			this._amount = 0;

			float v = MathUtils.floor(MathUtils.random() * 2);
			if (v == 1) {
				this._sign = -1;
			} else {
				this._sign = 1;
			}
			_cellWidth = MathUtils.random(4, width);
			_cellHeight = MathUtils.random(4, width);
		}

		void update(long elapsedTime) {
			this._amount += this._sign * this._speed;
		}

		void paint(GLEx g, float offsetX, float offsetY) {
			g.setColor(_effect._baseColor.setAlpha(_alpha));
			g.fillOval(this._x + offsetX + MathUtils.cos(this._amount / _effect.getWidth()) * _effect._radius,
					this._y + offsetY + MathUtils.sin(this._amount / _effect.getHeight()) * _effect._radius, _cellWidth,
					_cellHeight);

		}

	}

	public PixelBubbleEffect(LColor color) {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), color);
	}

	public PixelBubbleEffect(float x, float y, float width, float height) {
		this(x, y, width, height, LColor.red);
	}

	public PixelBubbleEffect(float x, float y, float width, float height, LColor color) {
		this(x, y, width, height, 1f, color);
	}

	public PixelBubbleEffect(float x, float y, float width, float height, float speed, LColor color) {
		this(x, y, width, height, 50, 100, speed, color);
	}

	public PixelBubbleEffect(float x, float y, float width, float height, int size, int radius, float speed,
			LColor color) {
		this.setLocation(x, y);
		this.setSize(width, height);
		this.setColor(color);
		this._radius = radius;
		this._moveSpeed = speed;
		this._bubbleSize = size;
		this._repaintDraw = true;
		_dirty = true;
	}

	public void pack() {
		if (_dirty || _bubbleBlocks == null) {
			createFireBlocks();
			_dirty = false;
		}
	}

	protected void createFireBlocks() {
		if (_bubbleBlocks == null) {
			_bubbleBlocks = new TArray<Block>(_bubbleSize);
		}
		for (int i = 0; i < _bubbleSize; i++) {
			float randomX = MathUtils.round(MathUtils.random() * getWidth());
			float randomY = MathUtils.round(MathUtils.random() * getHeight());
			float speed = _moveSpeed + 0.2f + MathUtils.random() * 3;
			float size = MathUtils.random() * _bubbleSize;
			Block circle = new Block(this, speed, size, randomX, randomY);
			_bubbleBlocks.add(circle);
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_completed) {
			return;
		}
		if (_dirty) {
			pack();
			return;
		}
		for (int i = 0; i < _bubbleBlocks.size; i++) {
			Block block = _bubbleBlocks.get(i);
			block.update(elapsedTime);
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (_completed) {
			return;
		}
		if (_dirty) {
			pack();
			return;
		}
		int tint = g.color();
		for (int i = 0; i < _bubbleBlocks.size; i++) {
			_bubbleBlocks.get(i).paint(g, drawX(offsetX), drawY(offsetY));
		}
		g.setTint(tint);
	}

	@Override
	public PixelBubbleEffect reset() {
		super.reset();
		this._dirty = true;
		this._completed = false;
		return this;
	}

	@Override
	public boolean isCompleted() {
		return _completed;
	}

	@Override
	public void close() {
		super.close();
		_completed = true;
		_bubbleBlocks.clear();
	}

}
