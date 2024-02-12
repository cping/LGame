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

	private Vector2f movePosOne, movePosTwo;

	private int halfWidth, halfHeight, multiples, direction;

	private boolean special;

	private boolean _createTexture;

	private LColor _splitColor;

	private RectBox limit;

	public SplitEffect(String fileName, int d) {
		this(LSystem.loadTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.init(t.getWidth(), t.getHeight());
		this._image = t;
		this.direction = d;
		this.limit = limit;
	}

	public SplitEffect(LColor color, float w, float h, int d) {
		this(color, w, h, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LColor color, float w, float h, RectBox limit, int d) {
		this._image = null;
		this._splitColor = color;
		this._createTexture = true;
		this.direction = d;
		this.limit = limit;
		this.init(w, h);
	}

	protected void init(float w, float h) {
		this.setRepaint(true);
		this.setSize(w, h);
		this.setDelay(10);
		this.halfWidth = (int) (_width / 2f);
		this.halfHeight = (int) (_height / 2f);
		this.multiples = 2;
		this.movePosOne = new Vector2f();
		this.movePosTwo = new Vector2f();
		switch (direction) {
		case Config.UP:
		case Config.DOWN:
			special = true;
		case Config.TLEFT:
		case Config.TRIGHT:
			movePosOne.set(0, 0);
			movePosTwo.set(halfWidth, 0);
			break;
		case Config.LEFT:
		case Config.RIGHT:
			special = true;
		case Config.TUP:
		case Config.TDOWN:
			movePosOne.set(0, 0);
			movePosTwo.set(0, halfHeight);
			break;
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_createTexture) {
			return;
		}
		if (!_completed) {
			if (_timer.action(elapsedTime)) {
				switch (direction) {
				case Config.LEFT:
				case Config.RIGHT:
				case Config.TLEFT:
				case Config.TRIGHT:
					movePosOne.move_multiples(Field2D.TLEFT, multiples);
					movePosTwo.move_multiples(Field2D.TRIGHT, multiples);
					break;
				case Config.UP:
				case Config.DOWN:
				case Config.TUP:
				case Config.TDOWN:
					movePosOne.move_multiples(Field2D.TUP, multiples);
					movePosTwo.move_multiples(Field2D.TDOWN, multiples);
					break;
				}

				if (special) {
					if (!limit.intersects(movePosOne.x, movePosOne.y, halfHeight, halfWidth)
							&& !limit.intersects(movePosTwo.x, movePosTwo.y, halfHeight, halfWidth)) {
						this._completed = true;
					}
				} else if (!limit.intersects(movePosOne.x, movePosOne.y, halfWidth, halfHeight)
						&& !limit.intersects(movePosTwo.x, movePosTwo.y, halfWidth, halfHeight)) {
					this._completed = true;
				}
			}
		}
		checkAutoRemove();
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (_createTexture) {
			if (_splitColor != null) {
				_image = TextureUtils.createTexture(width(), height(), _splitColor);
			}
			_createTexture = false;
			return;
		}
		if (!_completed) {
			final float x1 = movePosOne.x + drawX(offsetX);
			final float y1 = movePosOne.y + drawY(offsetY);

			final float x2 = movePosTwo.x + drawX(offsetX);
			final float y2 = movePosTwo.y + drawY(offsetY);

			switch (direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				g.draw(_image, x1, y1, _width, halfHeight, 0, 0, _width, halfHeight);
				g.draw(_image, x2, y2, _width, halfHeight, 0, halfHeight, _width, _height - halfHeight);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				g.draw(_image, x1, y1, halfWidth, _height, 0, 0, halfWidth, _height);
				g.draw(_image, x2, y2, halfWidth, _height, halfWidth, 0, _width - halfWidth, _height);
				break;

			}
		}
	}

	public int getMultiples() {
		return multiples;
	}

	public SplitEffect setMultiples(int multiples) {
		this.multiples = multiples;
		return this;
	}

	@Override
	public SplitEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

}
