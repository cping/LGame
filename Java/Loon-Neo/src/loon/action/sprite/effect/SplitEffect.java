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
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 图片拆分样黑幕过渡效果
 */
public class SplitEffect extends Entity implements BaseEffect {

	private Vector2f movePosOne, movePosTwo;

	private int halfWidth, halfHeight, multiples, direction;

	private boolean completed, autoRemoved, special;

	private RectBox limit;

	private LTimer timer;

	public SplitEffect(String fileName, int d) {
		this(LSystem.loadTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.setRepaint(true);
		this._image = t;
		this.setSize(t.width(), t.height());
		this.halfWidth = (int) (_width / 2f);
		this.halfHeight = (int) (_height / 2f);
		this.multiples = 2;
		this.direction = d;
		this.limit = limit;
		this.timer = new LTimer(10);
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

	public SplitEffect setDelay(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
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
						this.completed = true;
					}
				} else if (!limit.intersects(movePosOne.x, movePosOne.y, halfWidth, halfHeight)
						&& !limit.intersects(movePosTwo.x, movePosTwo.y, halfWidth, halfHeight)) {
					this.completed = true;
				}
			}
		}
		if (this.completed) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (!completed) {
			final float x1 = movePosOne.x + getX() + offsetX + _offset.x;
			final float y1 = movePosOne.y + getY() + offsetY + _offset.y;

			final float x2 = movePosTwo.x + getX() + offsetX + _offset.x;
			final float y2 = movePosTwo.y + getY() + offsetY + _offset.y;

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

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public SplitEffect setStop(boolean c) {
		this.completed = c;
		return this;
	}
	
	public int getMultiples() {
		return multiples;
	}

	public SplitEffect setMultiples(int multiples) {
		this.multiples = multiples;
		return this;
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public SplitEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}
