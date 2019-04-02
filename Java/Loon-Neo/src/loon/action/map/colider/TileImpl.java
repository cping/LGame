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
package loon.action.map.colider;

import loon.action.map.Attribute;
import loon.action.sprite.Animation;
import loon.geom.Vector2f;
import loon.utils.TArray;

public class TileImpl implements Tile {

	private int _x, _y, _width, _height;

	public int idx = -1;

	public int solidType = -1;

	public int imgId = -1;

	public Attribute attribute;

	public boolean isAnimation;

	public Animation animation;

	public TArray<TileEvent> events = new TArray<TileEvent>();

	public boolean solid, closed = false, open = false;

	public float G = 0f;

	public float H = 0f;

	public TileImpl parent = null;

	private TArray<Vector2f> neighbours;

	public TileImpl(int idx) {
		this(idx, 0, 0);
	}

	public TileImpl(int idx, int x, int y) {
		this.idx = idx;
		this._x = x;
		this._y = y;
	}

	public float getWeight() {
		return this.G + this.H;
	}

	public void calcNeighbours(int maxX, int maxY) {
		if (this.neighbours != null) {
			return;
		}
		this.neighbours = new TArray<Vector2f>();
		if (this._y > 0) {
			if (this._x > 0) {
				this.neighbours.add(new Vector2f(this._x - 1, this._y - 1)); // 上
																				// 左
				this.neighbours.add(new Vector2f(this._x - 1, this._y)); // 左
			}
			this.neighbours.add(new Vector2f(this._x, this._y - 1)); // 上中
			if (this._x < maxX) {
				this.neighbours.add(new Vector2f(this._x + 1, this._y - 1)); // 中
																				// 右
				this.neighbours.add(new Vector2f(this._x + 1, this._y)); // 右
			}
		}
		if (this._y < maxY) {
			if (this._x > 0) {
				this.neighbours.add(new Vector2f(this._x - 1, this._y + 1)); // 下
																				// 左
			}
			this.neighbours.add(new Vector2f(this._x, this._y + 1)); // 下中
			if (this._x < maxX) {
				this.neighbours.add(new Vector2f(this._x + 1, this._y + 1)); // 下
																				// 右
			}
		}
	}

	@Override
	public TileImpl at(int id, int x, int y) {
		this.idx = id;
		this.setX(x);
		this.setY(y);
		return this;
	}

	@Override
	public TileImpl at(int x, int y) {
		return at(0, x, y);
	}

	@Override
	public void setX(int x) {
		this._x = x;
	}

	@Override
	public void setY(int y) {
		this._x = y;
	}

	@Override
	public void setWidth(int w) {
		this._width = w;
	}

	@Override
	public void setHeight(int h) {
		this._height = h;
	}

	@Override
	public int getX() {
		return _x;
	}

	@Override
	public int getY() {
		return _y;
	}

	@Override
	public int getWidth() {
		return _width;
	}

	@Override
	public int getHeight() {
		return _height;
	}

	public TArray<Vector2f> getNeighbours() {
		return neighbours;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public TArray<TileEvent> getEvents() {
		return events;
	}

	public boolean isClosed() {
		return closed;
	}

}
