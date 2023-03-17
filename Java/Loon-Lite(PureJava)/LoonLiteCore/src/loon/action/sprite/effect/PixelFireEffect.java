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
 * 像素化火焰
 *
 * <pre>
 * add(new PixelFireEffect(90, 190, 150, 150));
 * </pre>
 *
 */
public class PixelFireEffect extends Entity implements BaseEffect {

	private int _countCompleted;

	private boolean _completed;

	private TArray<FireBlock> _fireBlocks;

	private int _count;

	private int _size;

	private float _angle;

	private float _speed;

	private boolean _dirty;

	private static class FireBlock {

		int _id;
		float _x;
		float _y;
		float _vx;
		float _vy;
		float _alpha;

		boolean _init;

		PixelFireEffect _effect;

		public FireBlock(PixelFireEffect effect, int id, float x, float y, float vx, float vy, float alpha) {
			this._effect = effect;
			this._id = id;
			this._x = x;
			this._y = y;
			this._vx = vx;
			this._vy = vy;
			this._alpha = alpha;
			_init = true;
		}

		void update(long elapsedTime) {
			float delta = MathUtils.max(elapsedTime / 1000f, LSystem.MIN_SECONE_SPEED_FIXED) * _effect._speed;
			_y -= _vy;
			if (_id % 2 == 0) {
				_x += MathUtils.sin(_vx, _effect._angle, 1f, true);
			} else {
				_x += MathUtils.cos(_vx, _effect._angle, 0.5f, true) ;
			}
			_x = MathUtils.max(_x, _effect.getWidth());
			_effect._angle += delta;
			if (_init) {
				_alpha -= delta;
			} else {
				_alpha += delta;
			}
			if (_alpha <= 0f) {
				this.reset();
			} else if (_alpha >= 1f) {
				_init = true;
			}
		}

		void reset() {
			_init = false;
			if (_id % 2 == 0) {
				_vy = MathUtils.randomFloor(5.5f, 8.5f);
				_vx = MathUtils.randomFloor(0.1f, 2.5f);
			} else {
				_vy = MathUtils.randomFloor(2.5f, 5.5f);
				_vx = MathUtils.randomFloor(2.1f, 3.5f);
			}
			_alpha = MathUtils.randomFloor(0.1f, 0.5f);
		}

		void paint(GLEx g, float offsetX, float offsetY) {
			if (_id % 2 == 0) {
				g.setColor(_effect._baseColor
						.setColor(_effect._baseColor.r, _effect._baseColor.g / 2, _effect._baseColor.b / 3)
						.setAlpha(_alpha));
				g.fillRect(offsetX + _x, offsetY + _y, _effect._size, _effect._size);
			} else {
				g.setColor(_effect._baseColor
						.setColor(_effect._baseColor.r, _effect._baseColor.g / 3, _effect._baseColor.b / 3)
						.setAlpha(_alpha));
				g.fillOval(offsetX + _x, offsetY + _y, MathUtils.random(1, _effect._size * 2),
						MathUtils.random(1, _effect._size * 2));
			}
		}

		boolean completed() {
			return _init;
		}

	}

	public PixelFireEffect(float x, float y, float width, float height) {
		this(x, y, width, height, 0);
	}

	public PixelFireEffect(float x, float y, float width, float height, float angle) {
		this(x, y, width, height, 6, 50, 0.5f, angle, LColor.red);
	}

	public PixelFireEffect(float x, float y, float width, float height, int size, int amount, float speed, float angle,
			LColor color) {
		this.setLocation(x, y);
		this.setSize(width, height);
		this.setColor(color);
		this._size = size;
		this._speed = speed;
		this._count = amount;
		this._angle = angle;
		this._repaintDraw = true;
		_dirty = true;
	}

	public void pack() {
		if (_dirty || _fireBlocks == null) {
			createFireBlocks();
			_dirty = false;
		}
	}

	protected void createFireBlocks() {
		if (_fireBlocks == null) {
			_fireBlocks = new TArray<>(_count);
		}
		for (int i = 0; i < _count; i++) {
			float x = MathUtils.randomFloor(getX(), getWidth());
			float y = MathUtils.randomFloor(getY(), getHeight());
			float vx = MathUtils.randomFloor(0.5f, 1.5f);
			float vy = MathUtils.randomFloor(0.1f, 0.5f);
			float alpha = MathUtils.randomFloor(0.001f, 1f);
			FireBlock fire = new FireBlock(this, i, x, y, vx, vy, alpha);
			_fireBlocks.add(fire);
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
		for (int i = 0; i < _fireBlocks.size; i++) {
			FireBlock block = _fireBlocks.get(i);
			block.update(elapsedTime);
			if (block.completed()) {
				_countCompleted++;
			}
		}
		if (_countCompleted >= _fireBlocks.size) {
			_completed = true;
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
		for (int i = 0; i < _fireBlocks.size; i++) {
			_fireBlocks.get(i).paint(g, drawX(offsetX), drawY(offsetY));
		}
		g.setTint(tint);
	}

	@Override
	public PixelFireEffect reset() {
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
	public void close(){
		super.close();
		_completed = true;
		_fireBlocks.clear();
	}

}
