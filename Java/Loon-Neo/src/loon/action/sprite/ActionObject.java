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
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.StrBuilder;

/**
 * 和瓦片地图绑定的动作对象,用来抽象一些简单的地图中精灵动作
 */
public abstract class ActionObject extends Entity implements Config {

	private Side _currentSide;

	protected Attribute attribute;

	protected Animation animation;

	protected TileMap tiles;

	protected float velocityX;

	protected float velocityY;

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
	void onProcess(long elapsedTime) {
		if (animation != null) {
			animation.update(elapsedTime);
			LTexture texture = animation.getSpriteImage();
			if (texture != null) {
				_image = texture;
			}
		}
	}

	public Vector2f collisionTileMap() {
		return collisionTileMap(0f, 0f);
	}

	public Vector2f collisionTileMap(float speedX, float speedY) {

		float x = getX();
		float y = getY();

		velocityX += speedX;
		velocityY += speedY;

		float newX = x + velocityX;

		Vector2f tile = tiles.getTileCollision(this, newX, y);

		if (tile == null) {
			x = newX;
		} else {
			if (velocityX > 0) {
				x = tiles.tilesToPixelsX(tile.x) - getWidth();
			} else if (velocityX < 0) {
				x = tiles.tilesToPixelsY(tile.x + 1);
			}
			velocityX = -velocityX;
		}

		float newY = y + velocityY;

		tile = tiles.getTileCollision(this, x, newY);
		if (tile == null) {
			y = newY;
		} else {
			if (velocityY > 0) {
				y = tiles.tilesToPixelsY(tile.y) - getHeight();
				velocityY = 0;
			} else if (velocityY < 0) {
				y = tiles.tilesToPixelsY(tile.y + 1);
				velocityY = 0;
			}
		}
		return tile != null ? tile.set(x, y) : Vector2f.at(x, y);
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

	public String getDirectionString() {
		return Side.getDirectionName(getDirection());
	}

	public float getVelocityX() {
		return this.velocityX;
	}

	public float getVelocityY() {
		return this.velocityY;
	}

	public ActionObject setVelocityX(final float vx) {
		this.velocityX = vx;
		return this;
	}

	public ActionObject setVelocityY(final float vy) {
		this.velocityY = vy;
		return this;
	}

	public ActionObject setVelocity(final float v) {
		return setVelocity(v, v);
	}

	public ActionObject setVelocity(final float vx, final float vy) {
		this.setVelocityX(vx);
		this.setVelocityY(vy);
		return this;
	}

	public ActionObject drag(final float drag) {
		this.velocityX = (drag * this.velocityX);
		this.velocityY = (drag * this.velocityY);
		return this;
	}

	public boolean isMovingLeft() {
		return this.velocityX < 0f;
	}

	public boolean isMovingRight() {
		return this.velocityX > 0f;
	}

	public boolean isMovingUp() {
		return this.velocityY < 0f;
	}

	public boolean isMovingDown() {
		return this.velocityY > 0f;
	}

	@Override
	public LTexture getBitmap() {
		return animation.getSpriteImage();
	}

	@Override
	public void toString(final StrBuilder s) {
		s.append(LSystem.LS);
		s.append(" [");
		s.append(_currentSide);
		s.append("]");
	}

	@Override
	public String toString() {
		final StrBuilder sbr = new StrBuilder();
		sbr.append(super.toString());
		this.toString(sbr);
		return sbr.toString();
	}

	@Override
	public int hashCode() {
		if (tiles == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, super.hashCode());
		if (attribute != null) {
			hashCode = LSystem.unite(hashCode, attribute.hashCode());
		}
		if (animation != null) {
			hashCode = LSystem.unite(hashCode, animation.hashCode());
		}
		if (tiles != null) {
			hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesWidth(x()));
			hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesHeight(y()));
			hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesWidth(tiles.getOffset().x));
			hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesHeight(tiles.getOffset().y));
			hashCode = LSystem.unite(hashCode, tiles.getWidth());
			hashCode = LSystem.unite(hashCode, tiles.getHeight());
			hashCode = LSystem.unite(hashCode, tiles.getTileWidth());
			hashCode = LSystem.unite(hashCode, tiles.getTileHeight());
			hashCode = LSystem.unite(hashCode, CollectionUtils.hashCode(tiles.getMap()));
		}
		return hashCode;
	}

	@Override
	public void close() {
		super.close();
		if (animation != null) {
			animation.close();
		}
	}

}
