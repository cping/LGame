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
import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.map.TileMapCollision;
import loon.events.GameTouch;
import loon.geom.ShapeUtils;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.CollectionUtils;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.EaseTimer;

public class MoveObject extends ActionObject {

	public static interface CollisionListener {

		public void onCollision(float dstX, float dstY, float srcX, float srcY);

	}

	public static interface DirectionListener {

		public void onDirection(int dir);
	}

	private boolean _collisionIgnore;

	private CollisionListener _collisionListener;

	private DirectionListener _directionListener;

	private TArray<Vector2f> _findPath = new TArray<Vector2f>();

	private boolean _moving;

	private boolean _allDirection;

	private boolean _bevelDirection;

	private float _startX, _startY, _endX, _endY;

	private float _speed;

	private float _touchX, _touchY;

	private int _movingLength;

	private int _lastDirection = EMPTY;

	private boolean _isClicked;

	private boolean _isCompleted;

	private boolean _isCheckCollision;

	private EaseTimer _timer;

	private AStarFindHeuristic _heuristic;

	public MoveObject(float x, float y, String path) {
		this(x, y, 0, 0, Animation.getDefaultAnimation(path), null);
	}

	public MoveObject(float x, float y, Animation animation) {
		this(x, y, 0, 0, animation, null);
	}

	public MoveObject(float x, float y, Animation animation, TileMapCollision map) {
		this(x, y, 0, 0, animation, map);
	}

	public MoveObject(float x, float y, float dw, float dh, Animation animation, TileMapCollision map) {
		super(x, y, dw, dh, animation, map);
		if (map == null) {
			this.tiles = new TileMap(LSystem.viewSize.newField2D());
		}
		this._timer = EaseTimer.at(1f, EasingMode.Linear);
		this._isCheckCollision = true;
		this._isCompleted = false;
		this._allDirection = false;
		this._speed = 4f;
	}

	public MoveObject updateMove() {
		synchronized (MoveObject.class) {
			if (!getCollisionArea().contains(_touchX, _touchY)) {
				if (_findPath != null) {
					_findPath.clear();
				}
				_findPath = AStarFinder.find(_heuristic, tiles.getField2D(), tiles.pixelsToTilesWidth(x()),
						tiles.pixelsToTilesHeight(y()), tiles.pixelsToTilesWidth(_touchX - tiles.getOffset().x),
						tiles.pixelsToTilesHeight(_touchY - tiles.getOffset().y), _bevelDirection, _allDirection);
			} else if (_findPath != null) {
				_findPath.clear();
			}
		}
		return this;
	}

	public MoveObject pressedLeft() {
		setDirection(TLEFT);
		return this;
	}

	public MoveObject pressedRight() {
		setDirection(TRIGHT);
		return this;
	}

	public MoveObject pressedDown() {
		setDirection(TDOWN);
		return this;
	}

	public MoveObject pressedUp() {
		setDirection(TUP);
		return this;
	}

	public MoveObject pressedIsoLeft() {
		setDirection(LEFT);
		return this;
	}

	public MoveObject pressedIsoRight() {
		setDirection(RIGHT);
		return this;
	}

	public MoveObject pressedIsoDown() {
		setDirection(DOWN);
		return this;
	}

	public MoveObject pressedIsoUp() {
		setDirection(UP);
		return this;
	}

	public MoveObject releaseDirection() {
		setDirection(EMPTY);
		return this;
	}

	private boolean isCollisionTile(int x, int y) {
		return tiles.isHit(x, y);
	}

	private boolean moveState() {
		this._movingLength = 0;
		return moveTo(getDirection());
	}

	private void updateDirection(final int dir) {
		if (_lastDirection != dir) {
			if (_directionListener != null) {
				_directionListener.onDirection(dir);
			}
			_lastDirection = dir;
		}
	}

	private boolean moveTo(int dir) {
		if (_isClicked) {
			return false;
		}
		final float moveSpeed = getMoveSpeed();

		float px = getX();
		float py = getY();

		boolean rMoved = false;
		if (_isCheckCollision) {
			int x = tiles.pixelsToTilesWidth(px);
			int y = tiles.pixelsToTilesHeight(py);
			if (dir == TLEFT) {
				int nextX = x - 1;
				int nextY = y;
				if (nextX < 0) {
					nextX = 0;
				}
				if (isCollisionTile(nextX, nextY)) {
					px -= moveSpeed;
					if (px < 0) {
						px = 0;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileWidth()) {
						x--;
						px = x * tiles.getTileWidth();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedLeftRight = true;
				} else {
					_groundedLeftRight = false;
				}
			} else if (dir == TRIGHT) {
				int nextX = x + 1;
				int nextY = y;
				if (nextX > tiles.getRow() - 1) {
					nextX = tiles.getRow() - 1;
				}
				if (isCollisionTile(nextX, nextY)) {
					px += moveSpeed;
					float width = tiles.getWidth() - getWidth() + moveSpeed;
					if (px > width) {
						px = width;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileWidth()) {
						x++;
						px = x * tiles.getTileWidth();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedLeftRight = true;
				} else {
					_groundedLeftRight = false;
				}
			} else if (dir == TUP) {
				int nextX = x;
				int nextY = y - 1;
				if (nextY < 0) {
					nextY = 0;
				}
				if (isCollisionTile(nextX, nextY)) {
					py -= moveSpeed;
					if (py < 0) {
						py = 0;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileHeight()) {
						y--;
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedTopBottom = true;
				} else {
					_groundedTopBottom = false;
				}
			} else if (dir == TDOWN) {
				int nextX = x;
				int nextY = y + 1;
				if (nextY > tiles.getCol() - 1) {
					nextY = tiles.getCol() - 1;
				}
				if (isCollisionTile(nextX, nextY)) {
					py += moveSpeed;
					float width = tiles.getHeight() - getHeight() + moveSpeed;
					if (py > width) {
						py = width;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileHeight()) {
						y++;
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedTopBottom = true;
				} else {
					_groundedTopBottom = false;
				}
			} else if (dir == LEFT) {
				int nextX = x - 1;
				int nextY = y - 1;
				if (nextX < 0) {
					nextX = 0;
				}
				if (nextY < 0) {
					nextY = 0;
				}
				if (isCollisionTile(nextX, nextY)) {
					px -= moveSpeed;
					py -= moveSpeed;
					if (px < 0) {
						px = 0;
						rMoved = true;
					}
					if (py < 0) {
						py = 0;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileWidth()) {
						x--;
						y--;
						px = x * tiles.getTileWidth();
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedTopBottom = true;
				} else {
					_groundedTopBottom = false;
				}
			} else if (dir == RIGHT) {
				int nextX = x + 1;
				int nextY = y + 1;
				if (nextX > tiles.getRow() - 1) {
					nextX = tiles.getRow() - 1;
				}
				if (nextY > tiles.getCol() - 1) {
					nextY = tiles.getCol() - 1;
				}
				if (isCollisionTile(nextX, nextY)) {
					px += moveSpeed;
					py += moveSpeed;
					float width = tiles.getWidth() - getWidth() + moveSpeed;
					float height = tiles.getHeight() - getHeight() + moveSpeed;
					if (px > width) {
						px = width;
						rMoved = true;
					}
					if (py > height) {
						py = height;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileWidth()) {
						x++;
						y++;
						px = x * tiles.getTileWidth();
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedTopBottom = true;
				} else {
					_groundedTopBottom = false;
				}
			} else if (dir == UP) {
				int nextX = x + 1;
				int nextY = y - 1;
				if (nextX > tiles.getRow() - 1) {
					nextX = tiles.getRow() - 1;
				}
				if (nextY < 0) {
					nextY = 0;
				}
				if (isCollisionTile(nextX, nextY)) {
					px += moveSpeed;
					py -= moveSpeed;
					float width = tiles.getWidth() - getWidth() + moveSpeed;
					if (px > width) {
						px = width;
						rMoved = true;
					}
					if (py < 0) {
						py = 0;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileHeight()) {
						x++;
						y--;
						px = x * tiles.getTileWidth();
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedLeftRight = true;
				} else {
					_groundedLeftRight = false;
				}
			} else if (dir == DOWN) {
				int nextX = x - 1;
				int nextY = y + 1;
				if (nextX < 0) {
					nextX = 0;
				}
				if (nextY > tiles.getCol() - 1) {
					nextY = tiles.getCol() - 1;
				}
				if (isCollisionTile(nextX, nextY)) {
					px -= moveSpeed;
					py += moveSpeed;
					float height = tiles.getHeight() - getHeight() + moveSpeed;
					if (px < 0) {
						px = 0;
						rMoved = true;
					}
					if (py > height) {
						py = height;
						rMoved = true;
					}
					_movingLength += moveSpeed;
					moveObject(px, py);
					if (_movingLength >= tiles.getTileHeight()) {
						x++;
						y++;
						px = x * tiles.getTileWidth();
						py = y * tiles.getTileHeight();
						moveObject(px, py);
						rMoved = true;
					}
					_groundedLeftRight = true;
				} else {
					_groundedLeftRight = false;
				}
			}
		} else {
			if (dir == TLEFT) {
				move_left(moveSpeed);
			} else if (dir == TRIGHT) {
				move_right(moveSpeed);
			} else if (dir == TUP) {
				move_up(moveSpeed);
			} else if (dir == TDOWN) {
				move_down(moveSpeed);
			} else if (dir == LEFT) {
				move_45D_left(moveSpeed);
			} else if (dir == RIGHT) {
				move_45D_right(moveSpeed);
			} else if (dir == UP) {
				move_45D_up(moveSpeed);
			} else if (dir == DOWN) {
				move_45D_down(moveSpeed);
			}
			if (!_collisionIgnore && _collisionListener != null) {
				_collisionListener.onCollision(px, py, getX(), getY());
			}
		}
		updateDirection(dir);
		return rMoved;
	}

	@Override
	public void onCollision(ISprite spr, int dir) {
		if (!_collisionIgnore) {
			super.onCollision(spr, dir);
		}
	}

	public MoveObject onTouch(GameTouch e) {
		if (e == null) {
			return this;
		}
		return this.onTouch(e.getX(), e.getY());
	}

	public MoveObject onTouch(XY pos) {
		if (pos == null) {
			return this;
		}
		return this.onTouch(pos.getX(), pos.getY());
	}

	public MoveObject onTouch(float x, float y) {
		if (!_isClicked) {
			this._touchX = x;
			this._touchY = y;
			this._isClicked = true;
			this._timer.reset();
			this.updateMove();
		}
		return this;
	}

	@Override
	public float getTouchX() {
		return _touchX;
	}

	@Override
	public float getTouchY() {
		return _touchY;
	}

	public MoveObject onPosition(GameTouch e) {
		return this.onPosition(e.getX(), e.getY());
	}

	public MoveObject onPosition(float x, float y) {
		if (_findPath == null) {
			return this;
		}
		synchronized (_findPath) {
			if (_findPath != null) {
				_findPath.clear();
			}
		}
		this.moveObject(x, y);
		this._isClicked = false;
		return this;
	}

	private void moveObject(float x, float y) {
		if (_collisionListener != null) {
			_collisionListener.onCollision(getX(), getY(), x, y);
		}
		this.setLocation(x, y);
	}

	@Override
	public void onProcess(long elapsedTime) {
		super.onProcess(elapsedTime);

		_timer.update(elapsedTime);

		if (!isStaticObject()) {
			if (!_isClicked) {
				this._moving = moveState();
			}
			if (tiles == null || _findPath == null || isComplete()) {
				if (_isClicked) {
					setDirection(EMPTY);
				}
				_isClicked = false;
				return;
			}

			synchronized (_findPath) {
				if (_endX == _startX && _endY == _startY) {
					if (_findPath != null) {
						if (_findPath.size > 1) {
							Vector2f moveStart = _findPath.get(0);
							Vector2f moveEnd = _findPath.get(1);
							_startX = tiles.tilesToPixelsX(moveStart.x());
							_startY = tiles.tilesToPixelsY(moveStart.y());
							_endX = moveEnd.x() * tiles.getTileWidth();
							_endY = moveEnd.y() * tiles.getTileHeight();
							setDirection(Field2D.getDirection(_startX, _startY, _endX, _endY));
							_findPath.removeIndex(0);
						} else {
							_findPath.clear();
						}
					}
				}
				switch (getDirection()) {
				case Config.TUP:
					_startY -= getMoveSpeed();
					if (_startY < _endY) {
						_startY = _endY;
					}
					break;
				case Config.TDOWN:
					_startY += getMoveSpeed();
					if (_startY > _endY) {
						_startY = _endY;
					}
					break;
				case Config.TLEFT:
					_startX -= getMoveSpeed();
					if (_startX < _endX) {
						_startX = _endX;
					}
					break;
				case Config.TRIGHT:
					_startX += getMoveSpeed();
					if (_startX > _endX) {
						_startX = _endX;
					}
					break;
				case Config.UP:
					_startX += getMoveSpeed();
					_startY -= getMoveSpeed();
					if (_startX > _endX) {
						_startX = _endX;
					}
					if (_startY < _endY) {
						_startY = _endY;
					}
					break;
				case Config.DOWN:
					_startX -= getMoveSpeed();
					_startY += getMoveSpeed();
					if (_startX < _endX) {
						_startX = _endX;
					}
					if (_startY > _endY) {
						_startY = _endY;
					}
					break;
				case Config.LEFT:
					_startX -= getMoveSpeed();
					_startY -= getMoveSpeed();
					if (_startX < _endX) {
						_startX = _endX;
					}
					if (_startY < _endY) {
						_startY = _endY;
					}
					break;
				case Config.RIGHT:
					_startX += getMoveSpeed();
					_startY += getMoveSpeed();
					if (_startX > _endX) {
						_startX = _endX;
					}
					if (_startY > _endY) {
						_startY = _endY;
					}
					break;
				}

				final Vector2f tile = _isCheckCollision ? tiles.getTileCollision(this, _startX, _startY) : null;

				if (tile != null) {
					int sx = tiles.tilesToPixelsX(tile.x);
					int sy = tiles.tilesToPixelsY(tile.y);
					if (sx > 0) {
						sx = (int) (sx - getWidth());
						_groundedLeftRight = true;
					} else if (sx < 0) {
						sx = tiles.tilesToPixelsX(tile.x);
						_groundedLeftRight = true;
					}
					if (sy > 0) {
						sy = (int) (sy - getHeight());
						_groundedTopBottom = true;
					} else if (sy < 0) {
						sy = tiles.tilesToPixelsY(tile.y);
						_groundedTopBottom = true;
					}
				} else {
					freeGround();
					moveObject(_startX, _startY);
				}

			}
			updateDirection(getDirection());
		}
	}

	protected float getMoveSpeed() {
		return _speed * _timer.getProgress();
	}

	public EaseTimer getTimer() {
		return _timer;
	}

	public MoveObject setEasingMode(EasingMode ease) {
		_timer.setEasingMode(ease);
		return this;
	}

	public float getSpeed() {
		return _speed;
	}

	public MoveObject setSpeed(int speed) {
		this._speed = speed;
		return this;
	}

	public boolean isComplete() {
		return _findPath == null || _findPath.size == 0 || _isCompleted;
	}

	public MoveObject setComplete(boolean c) {
		this._isCompleted = true;
		return this;
	}

	public float getRotationTo(float x, float y) {
		float r = MathUtils.atan2(x - x(), y - y());
		return ShapeUtils.getAngleDiff(_objectRotation, r);
	}

	public AStarFindHeuristic getHeuristic() {
		return _heuristic;
	}

	public MoveObject setHeuristic(AStarFindHeuristic heuristic) {
		this._heuristic = heuristic;
		return this;
	}

	public boolean isAllDirection() {
		return _allDirection;
	}

	public MoveObject setAllDirection(boolean allDirection) {
		this._allDirection = allDirection;
		return this;
	}

	public boolean isBevelDirection() {
		return _bevelDirection;
	}

	public MoveObject setBevelDirection(boolean bevelDirection) {
		this._bevelDirection = bevelDirection;
		return this;
	}

	public boolean isAllowCheckCollision() {
		return _isCheckCollision;
	}

	public MoveObject setAllowCheckCollision(boolean c) {
		this._isCheckCollision = c;
		return this;
	}

	public boolean isMoving() {
		return _moving;
	}

	public CollisionListener getCollisionListener() {
		return _collisionListener;
	}

	public MoveObject setCollisionListener(CollisionListener c) {
		this._collisionListener = c;
		return this;
	}

	public DirectionListener getDirectionListener() {
		return _directionListener;
	}

	public MoveObject setDirectionListener(DirectionListener d) {
		this._directionListener = d;
		return this;
	}

	public boolean isCollisionIgnore() {
		return _collisionIgnore;
	}

	public MoveObject setCollisionIgnore(boolean c) {
		this._collisionIgnore = c;
		return this;
	}

	@Override
	public int hashCode() {
		if (tiles == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, _allDirection);
		hashCode = LSystem.unite(hashCode, _bevelDirection);
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesWidth(x()));
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesHeight(y()));
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesWidth(_touchX - tiles.getOffset().x));
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesHeight(_touchY - tiles.getOffset().y));
		hashCode = LSystem.unite(hashCode, tiles.getWidth());
		hashCode = LSystem.unite(hashCode, tiles.getHeight());
		hashCode = LSystem.unite(hashCode, tiles.getTileWidth());
		hashCode = LSystem.unite(hashCode, tiles.getTileHeight());
		hashCode = LSystem.unite(hashCode, CollectionUtils.hashCode(tiles.getMap()));
		return hashCode;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (_findPath != null) {
			_findPath.clear();
			_findPath = null;
		}
	}

}
