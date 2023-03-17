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

import loon.LSystem;
import loon.action.ActionBind;
import loon.action.map.items.Attribute;
import loon.action.sprite.Animation;
import loon.geom.RectI;
import loon.geom.RectI.Range;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class TileImpl implements Tile {

	private RectI rect;

	protected int idx = -1;

	protected int solidType = -1;

	protected int imgId = -1;

	protected Attribute attribute;

	protected boolean isAnimation;

	protected Animation animation;

	protected TArray<TileEvent> events = new TArray<>();

	protected boolean solid = false, closed = false, open = false;

	protected float G = 0f;

	protected float H = 0f;

	protected TileImpl parent = null;

	protected ActionBind bind = null;

	protected Object userData = null;

	private TArray<Vector2f> neighbours;

	private TileState state;

	public TileImpl(int idx) {
		this(idx, 0, 0);
	}

	public TileImpl(int idx, ActionBind bind) {
		this(idx, bind, bind.x(), bind.y(), (int) bind.getWidth(), (int) bind.getHeight());
	}

	public TileImpl(int idx, int x, int y) {
		this(idx, x, y, 1, 1);
	}

	public TileImpl(int idx, int x, int y, int w, int h) {
		this(idx, null, x, y, w, h);
	}

	public TileImpl(int idx, ActionBind bind, int x, int y, int w, int h) {
		this.idx = idx;
		this.imgId = idx;
		this.solidType = idx;
		this.state = new TileState(0);
		this.rect = new RectI(x, y, w, h);
		this.bind = bind;
	}

	public TileImpl cpy() {
		return new TileImpl(this.idx).cpy(this);
	}

	public TileImpl cpy(TileImpl other) {
		this.rect.set(other.rect);
		this.idx = other.idx;
		this.solidType = other.solidType;
		this.imgId = other.imgId;
		this.userData = other.userData;
		this.state.setResult(other.state.getResult());
		if (attribute != null) {
			attribute.setAttribute(other.attribute);
		} else {
			attribute = other.attribute;
		}
		isAnimation = other.isAnimation;
		if (events != null) {
			events.addAll(other.events);
		} else {
			events = other.events;
		}
		solid = other.solid;
		closed = other.closed;
		open = other.open;
		G = other.G;
		H = other.H;
		parent = other.parent;
		bind = other.bind;
		if (neighbours != null) {
			neighbours.addAll(other.neighbours);
		} else {
			neighbours = other.neighbours;
		}
		return this;
	}

	public void calcNeighbours() {
		this.calcNeighbours(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public void calcNeighbours(int maxX, int maxY) {
		if (this.neighbours == null) {
			this.neighbours = new TArray<>();
		} else {
			this.neighbours.clear();
		}
		if (this.rect.y > 0) {
			if (this.rect.x > 0) {
				this.neighbours.add(new Vector2f(this.rect.x - 1, this.rect.y - 1)); // 上
				// 左
				this.neighbours.add(new Vector2f(this.rect.x - 1, this.rect.y)); // 左
			}
			this.neighbours.add(new Vector2f(this.rect.x, this.rect.y - 1)); // 上中
			if (this.rect.x < maxX) {
				this.neighbours.add(new Vector2f(this.rect.x + 1, this.rect.y - 1)); // 中
				// 右
				this.neighbours.add(new Vector2f(this.rect.x + 1, this.rect.y)); // 右
			}
		}
		if (this.rect.y < maxY) {
			if (this.rect.x > 0) {
				this.neighbours.add(new Vector2f(this.rect.x - 1, this.rect.y + 1)); // 下
				// 左
			}
			this.neighbours.add(new Vector2f(this.rect.x, this.rect.y + 1)); // 下中
			if (this.rect.x < maxX) {
				this.neighbours.add(new Vector2f(this.rect.x + 1, this.rect.y + 1)); // 下
				// 右
			}
		}
	}

	public int getId() {
		return this.idx;
	}

	protected void setId(int id) {
		this.idx = id;
		this.imgId = id;
		this.solidType = id;
	}

	@Override
	public TileImpl at(int id, int x, int y, int w, int h) {
		TileImpl impl = cpy();
		impl.setId(id);
		impl.setX(x);
		impl.setY(y);
		impl.setWidth(w);
		impl.setHeight(h);
		return impl;
	}

	public Vector2f getPos() {
		return Vector2f.at(getX(), getY());
	}

	@Override
	public TileImpl at(int x, int y, int w, int h) {
		return at(this.idx, x, y, w, h);
	}

	@Override
	public void setX(int x) {
		this.rect.x = x;
	}

	@Override
	public void setY(int y) {
		this.rect.x = y;
	}

	@Override
	public void setWidth(int w) {
		this.rect.width = w;
	}

	@Override
	public void setHeight(int h) {
		this.rect.height = h;
	}

	@Override
	public int getX() {
		return rect.x;
	}

	@Override
	public int getY() {
		return rect.y;
	}

	@Override
	public int getWidth() {
		return rect.width;
	}

	@Override
	public int getHeight() {
		return rect.height;
	}

	public boolean inside(int x, int y) {
		return rect.inside(x, y);
	}

	public Range getRange() {
		return rect.getRange();
	}

	public TArray<Vector2f> getNeighbours() {
		return neighbours;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public TileImpl setAttribute(Attribute attribute) {
		this.attribute = attribute;
		return this;
	}

	public Animation getAnimation() {
		return animation;
	}

	public TileImpl setAnimation(Animation animation) {
		this.isAnimation = !(animation == null);
		this.animation = animation;
		return this;
	}

	public TArray<TileEvent> getEvents() {
		return events;
	}

	public ActionBind getBind() {
		return bind;
	}

	public int getImgId() {
		return imgId;
	}

	public TileImpl setImgId(int imgId) {
		this.imgId = imgId;
		return this;
	}

	public TileImpl setUserData(Object data) {
		this.userData = data;
		return this;
	}

	public Object getUserData() {
		return userData;
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public void setAnimation(boolean ani) {
		this.isAnimation = ani;
	}

	public boolean isClosed() {
		return closed;
	}

	public TileState getState() {
		return state;
	}

	public TileImpl setState(TileState state) {
		this.state = state;
		return this;
	}

	public TileImpl getParent() {
		return parent;
	}

	public TileImpl setParent(TileImpl parent) {
		this.parent = parent;
		return this;
	}

	public TileImpl setHCost(float hCost) {
		this.H = hCost;
		return this;
	}

	public float getHCost() {
		return this.H;
	}

	public TileImpl setGCost(float gCost) {
		this.G = gCost;
		return this;
	}

	public float getGCost() {
		return this.G;
	}

	public float getWeight() {
		return this.G + this.H;
	}

	public float getFCost() {
		return this.getWeight();
	}

	public int distance(Tile other) {
		return MathUtils.abs(getX() - other.getX()) + MathUtils.abs(getY() - other.getY());
	}

	@Override
	public Tile getTileImpl() {
		return this;
	}

	@Override
	public String toString() {
		return "{" + getX() + "," + getY() + "}";
	}

}