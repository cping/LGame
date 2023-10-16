/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import loon.LSystem;
import loon.LTexture;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.Side;
import loon.action.map.TileMap;
import loon.action.map.items.Attribute;
import loon.utils.StrBuilder;

/**
 * 和瓦片地图绑定的动作对象,用来抽象一些简单的地图中精灵动作
 */
public abstract class ActionObject extends Entity implements Config {

	private Side _currentSide;

	protected Attribute attribute;

	protected Animation animation;

	protected TileMap tiles;

	public ActionObject(float x, float y, String path) {
		this(x, y, 0, 0, Animation.getDefaultAnimation(path), null);
	}

	public ActionObject(float x, float y, Animation animation) {
		this(x, y, 0, 0, animation, null);
	}

	public ActionObject(float x, float y, Animation animation, TileMap map) {
		this(x, y, 0f, 0f, animation, map);
	}

	public ActionObject(float x, float y, float dw, float dh, Animation animation, TileMap map) {
		super(animation == null ? null : animation.getSpriteImage(), x, y, dw, dh);
		this.setTexture(animation.getSpriteImage());
		this._currentSide = new Side();
		this.tiles = map;
		this.animation = animation;
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (animation != null) {
			animation.update(elapsedTime);
			LTexture texture = animation.getSpriteImage();
			if (texture != null) {
				_image = texture;
			}
		}
	}

	public TileMap getTileMap() {
		return tiles;
	}

	@Override
	public Field2D getField2D() {
		return tiles.getField2D();
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public ActionObject setAttribute(Attribute attribute) {
		this.attribute = attribute;
		return this;
	}

	public Animation getAnimation() {
		return animation;
	}

	public ActionObject setAnimation(Animation a) {
		this.animation = a;
		return this;
	}

	public Side getCurrentSide() {
		return _currentSide;
	}

	public ActionObject setCurrentSide(int side) {
		_currentSide.setDirection(side);
		return this;
	}

	public ActionObject setIndex(int index) {
		if (animation == null) {
			return this;
		}
		if (animation instanceof AnimationStorage) {
			((AnimationStorage) animation).playIndex(index);
		}
		return this;
	}

	public int getDirection() {
		return _currentSide.getDirection();
	}

	public ActionObject setDirection(int dir) {
		this._currentSide.setDirection(dir);
		return this;
	}

	public ActionObject updateLocation() {
		this.setLocation(getLocation().add(this._currentSide.updatePostion()));
		return this;
	}

	public ActionObject flipSide() {
		if (_currentSide.getDirection() == Side.TLEFT || _currentSide.getDirection() == Side.TRIGHT) {
			flipHorizontalSide();
		} else {
			flipVerticalSide();
		}
		return this;
	}

	public ActionObject flipHorizontalSide() {
		if (_currentSide.getDirection() == Side.TRIGHT) {
			_currentSide.setDirection(Side.TLEFT);
		} else if (_currentSide.getDirection() == Side.TLEFT) {
			_currentSide.setDirection(Side.TRIGHT);
		}
		return this;
	}

	private ActionObject flipVerticalSide() {
		if (_currentSide.getDirection() == Side.TOP) {
			_currentSide.setDirection(Side.BOTTOM);
		} else if (_currentSide.getDirection() == Side.BOTTOM) {
			_currentSide.setDirection(Side.TOP);
		}
		return this;
	}

	public TileMap getTiles() {
		return tiles;
	}

	public ActionObject setTiles(TileMap tile) {
		this.tiles = tile;
		return this;
	}

	@Override
	public LTexture getBitmap() {
		return animation.getSpriteImage();
	}

	@Override
	public void toString(final StrBuilder s) {
		s.append(super.toString());
		s.append(LSystem.LS);
		s.append(" [");
		s.append(_currentSide);
		s.append("]");
	}

	@Override
	public String toString() {
		final StrBuilder sbr = new StrBuilder();
		this.toString(sbr);
		return sbr.toString();
	}

	@Override
	public void close() {
		super.close();
		if (animation != null) {
			animation.close();
		}
	}

}
