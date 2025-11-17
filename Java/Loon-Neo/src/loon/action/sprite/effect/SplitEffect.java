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
import loon.LTexture;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;

/**
 * 图片拆分样黑幕过渡效果
 */
public class SplitEffect extends BaseAbstractEffect {

	private Vector2f _movePosOne, _movePosTwo;

	private int _halfWidth, _halfHeight, _multiples, _direction;

	private boolean _special;

	private boolean _createTexture;

	private LColor _splitColor;

	private RectBox _limit;

	public SplitEffect(String fileName, int d) {
		this(LSystem.loadTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.init(t.getWidth(), t.getHeight());
		this._image = t;
		this._direction = d;
		this._limit = limit;
	}

	public SplitEffect(LColor color, float w, float h, int d) {
		this(color, w, h, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LColor color, float w, float h, RectBox limit, int d) {
		this._image = null;
		this._splitColor = color;
		this._createTexture = true;
		this._direction = d;
		this._limit = limit;
		this.init(w, h);
	}

	protected void init(float w, float h) {
		this.setRepaint(true);
		this.setSize(w, h);
		this.setDelay(10);
		this._halfWidth = (int) (_width / 2f);
		this._halfHeight = (int) (_height / 2f);
		this._multiples = LSystem.toIScaleFPS(2);
		this._movePosOne = new Vector2f();
		this._movePosTwo = new Vector2f();
		switch (_direction) {
		case Config.UP:
		case Config.DOWN:
			_special = true;
		case Config.TLEFT:
		case Config.TRIGHT:
			_movePosOne.set(0, 0);
			_movePosTwo.set(_halfWidth, 0);
			break;
		case Config.LEFT:
		case Config.RIGHT:
			_special = true;
		case Config.TUP:
		case Config.TDOWN:
			_movePosOne.set(0, 0);
			_movePosTwo.set(0, _halfHeight);
			break;
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_createTexture) {
			return;
		}
		if (!_completed) {
			if (_timer.action(elapsedTime)) {
				switch (_direction) {
				case Config.LEFT:
				case Config.RIGHT:
				case Config.TLEFT:
				case Config.TRIGHT:
					_movePosOne.move_multiples(Field2D.TLEFT, _multiples);
					_movePosTwo.move_multiples(Field2D.TRIGHT, _multiples);
					break;
				case Config.UP:
				case Config.DOWN:
				case Config.TUP:
				case Config.TDOWN:
					_movePosOne.move_multiples(Field2D.TUP, _multiples);
					_movePosTwo.move_multiples(Field2D.TDOWN, _multiples);
					break;
				}

				if (_special) {
					if (!_limit.intersects(_movePosOne.x, _movePosOne.y, _halfHeight, _halfWidth)
							&& !_limit.intersects(_movePosTwo.x, _movePosTwo.y, _halfHeight, _halfWidth)) {
						this._completed = true;
					}
				} else if (!_limit.intersects(_movePosOne.x, _movePosOne.y, _halfWidth, _halfHeight)
						&& !_limit.intersects(_movePosTwo.x, _movePosTwo.y, _halfWidth, _halfHeight)) {
					this._completed = true;
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_createTexture) {
			if (_splitColor != null) {
				_image = TextureUtils.createTexture(width(), height(), _splitColor);
			}
			_createTexture = false;
			return;
		}
		if (!_completed) {
			final float x1 = _movePosOne.x + drawX(offsetX);
			final float y1 = _movePosOne.y + drawY(offsetY);

			final float x2 = _movePosTwo.x + drawX(offsetX);
			final float y2 = _movePosTwo.y + drawY(offsetY);

			switch (_direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				g.draw(_image, x1, y1, _width, _halfHeight, 0, 0, _width, _halfHeight);
				g.draw(_image, x2, y2, _width, _halfHeight, 0, _halfHeight, _width, _height - _halfHeight);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				g.draw(_image, x1, y1, _halfWidth, _height, 0, 0, _halfWidth, _height);
				g.draw(_image, x2, y2, _halfWidth, _height, _halfWidth, 0, _width - _halfWidth, _height);
				break;

			}
		}
	}

	public int getMultiples() {
		return _multiples;
	}

	public SplitEffect setMultiples(int m) {
		this._multiples = LSystem.toIScaleFPS(m);
		return this;
	}

	@Override
	public SplitEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
