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

import java.util.LinkedList;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.core.LSystem;
import loon.core.geom.ShapeUtils;
import loon.core.geom.Vector2f;
import loon.core.input.LTouch;
import loon.core.timer.LTimer;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;


public class MoveObject extends SpriteBatchObject {

	private boolean allDirection;

	private LinkedList<Vector2f> findPath = new LinkedList<Vector2f>();

	private int startX, startY, endX, endY, moveX, moveY;

	private int speed, touchX, touchY;

	private int direction = EMPTY;

	protected final static int BLOCK_SIZE = 32;

	private boolean isComplete;

	private LTimer timer;

	private AStarFindHeuristic heuristic;

	private int movingLength;


	public MoveObject(float x, float y, Animation animation, TileMap map) {
		this(x, y, 0, 0, animation, map);
	}

	public MoveObject(float x, float y, float dw, float dh,
			Animation animation, TileMap map) {
		super(x, y, dw, dh, animation, map);
		this.timer = new LTimer(0);
		this.isComplete = false;
		this.allDirection = false;
		this.speed = 4;
	}

	public void updateMove() {
		synchronized (MoveObject.class) {
			if (!getCollisionArea().contains(touchX, touchY)) {

				if (findPath != null) {
					findPath.clear();
				}
				findPath = AStarFinder
						.find(heuristic,
								tiles.getField(),
								tiles.pixelsToTilesWidth(x()),
								tiles.pixelsToTilesHeight(y()),
								tiles.pixelsToTilesWidth(touchX
										- tiles.getOffset().x),
								tiles.pixelsToTilesHeight(touchY
										- tiles.getOffset().y), allDirection);
			} else if (findPath != null) {
				findPath.clear();
			}
		}
	}

	public void pressedLeft() {
		direction = LEFT;
	}

	public void pressedRight() {
		direction = RIGHT;
	}

	public void pressedDown() {
		direction = DOWN;
	}

	public void pressedUp() {
		direction = UP;
	}

	public void releaseDirection() {
		this.direction = EMPTY;
	}

	private boolean moveState() {
		movingLength = 0;
		switch (direction) {
		case LEFT:
			if (moveLeft()) {
				return true;
			}
			break;
		case RIGHT:
			if (moveRight()) {
				return true;
			}
			break;
		case UP:
			if (moveUp()) {
				return true;
			}
			break;
		case DOWN:
			if (moveDown()) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean moveLeft() {
		int px = x();
		int py = y();
		int x = tiles.pixelsToTilesWidth(px);
		int y = tiles.pixelsToTilesHeight(py);
		int nextX = x - 1;
		int nextY = y;
		if (nextX < 0) {
			nextX = 0;
		}
		if (tiles.isHit(nextX, nextY)) {
			px -= speed;
			if (px < 0) {
				px = 0;
			}
			movingLength += speed;
			setLocation(px, py);
			if (movingLength >= tiles.getTileWidth()) {
				x--;
				px = x * tiles.getTileWidth();
				setLocation(px, py);
				return true;
			}
		} else {
			px = x * tiles.getTileWidth();
			py = y * tiles.getTileHeight();
			setLocation(px, py);
		}

		return false;
	}

	private boolean moveRight() {
		int px = x();
		int py = y();
		int x = tiles.pixelsToTilesWidth(px);
		int y = tiles.pixelsToTilesHeight(py);
		int nextX = x + 1;
		int nextY = y;

		if (nextX > tiles.getRow() - 1) {
			nextX = tiles.getRow() - 1;
		}
		if (tiles.isHit(nextX, nextY)) {
			px += speed;
			if (px > tiles.getWidth() - tiles.getTileWidth()) {
				px = tiles.getWidth() - tiles.getTileWidth();
			}
			movingLength += speed;
			setLocation(px, py);
			if (movingLength >= tiles.getTileWidth()) {
				x++;
				px = x * tiles.getTileWidth();
				setLocation(px, py);
				return true;
			}
		} else {
			px = x * tiles.getTileWidth();
			py = y * tiles.getTileHeight();
			setLocation(px, py);
		}

		return false;
	}

	private boolean moveUp() {
		int px = x();
		int py = y();
		int x = tiles.pixelsToTilesWidth(px);
		int y = tiles.pixelsToTilesHeight(py);
		int nextX = x;
		int nextY = y - 1;
		if (nextY < 0) {
			nextY = 0;
		}
		if (tiles.isHit(nextX, nextY)) {
			py -= speed;
			if (py < 0) {
				py = 0;
			}
			movingLength += speed;
			setLocation(px, py);
			if (movingLength >= tiles.getTileHeight()) {
				y--;
				py = y * tiles.getTileHeight();
				setLocation(px, py);
				return true;
			}
		} else {
			px = x * tiles.getTileWidth();
			py = y * tiles.getTileHeight();
			setLocation(px, py);
		}

		return false;
	}

	private boolean moveDown() {
		int px = x();
		int py = y();
		int x = tiles.pixelsToTilesWidth(px);
		int y = tiles.pixelsToTilesHeight(py);
		int nextX = x;
		int nextY = y + 1;
		if (nextY > tiles.getCol() - 1) {
			nextY = tiles.getCol() - 1;
		}
		if (tiles.isHit(nextX, nextY)) {
			py += speed;
			if (py > tiles.getHeight() - tiles.getTileHeight()) {
				py = tiles.getHeight() - tiles.getTileHeight();
			}
			movingLength += speed;
			setLocation(px, py);
			if (movingLength >= tiles.getTileHeight()) {
				y++;
				py = y * tiles.getTileHeight();
				setLocation(px, py);
				return true;
			}
		} else {
			px = x * tiles.getTileWidth();
			py = y * tiles.getTileHeight();
			setLocation(px, py);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (tiles == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, allDirection);
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesWidth(x()));
		hashCode = LSystem.unite(hashCode, tiles.pixelsToTilesHeight(y()));
		hashCode = LSystem.unite(hashCode,
				tiles.pixelsToTilesWidth(touchX - tiles.getOffset().x));
		hashCode = LSystem.unite(hashCode,
				tiles.pixelsToTilesHeight(touchY - tiles.getOffset().y));
		hashCode = LSystem.unite(hashCode, tiles.getWidth());
		hashCode = LSystem.unite(hashCode, tiles.getHeight());
		hashCode = LSystem.unite(hashCode, tiles.getTileWidth());
		hashCode = LSystem.unite(hashCode, tiles.getTileHeight());
		hashCode = LSystem.unite(hashCode,
				CollectionUtils.hashCode(tiles.getMap()));
		return hashCode;
	}

	public void onTouch(LTouch e) {
		this.onTouch(e.x(), e.y());
	}

	public void onTouch(int x, int y) {
		this.touchX = x;
		this.touchY = y;
		this.updateMove();
	}

	public int getTouchX() {
		return touchX;
	}

	public int getTouchY() {
		return touchY;
	}

	public void onPosition(LTouch e) {
		this.onPosition(e.getX(), e.getY());
	}

	public void onPosition(float x, float y) {
		if (findPath == null) {
			return;
		}
		synchronized (findPath) {
			if (findPath != null) {
				findPath.clear();
			}
		}
		this.setLocation(x, y);
	}
	
	private boolean isMoving;

	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {

			isMoving = moveState();

			if (tiles == null || findPath == null) {
				return;
			}
			if (isComplete()) {
				return;
			}

			synchronized (findPath) {
				if (endX == startX && endY == startY) {
					if (findPath != null) {
						if (findPath.size() > 1) {
							Vector2f moveStart = findPath.get(0);
							Vector2f moveEnd = findPath.get(1);
							startX = tiles.tilesToPixelsX(moveStart.x());
							startY = tiles.tilesToPixelsY(moveStart.y());
							endX = moveEnd.x() * tiles.getTileWidth();
							endY = moveEnd.y() * tiles.getTileHeight();
							moveX = moveEnd.x() - moveStart.x();
							moveY = moveEnd.y() - moveStart.y();
							direction = Field2D.getDirection(moveX, moveY);
							findPath.remove(0);
						} else {
							findPath.clear();
						}
					}
				}
				switch (direction) {
				case Config.TUP:
					startY -= speed;
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.TDOWN:
					startY += speed;
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Config.TLEFT:
					startX -= speed;
					if (startX < endX) {
						startX = endX;
					}
					break;
				case Config.TRIGHT:
					startX += speed;
					if (startX > endX) {
						startX = endX;
					}
					break;
				case Config.UP:
					startX += speed;
					startY -= speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.DOWN:
					startX -= speed;
					startY += speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Config.LEFT:
					startX -= speed;
					startY -= speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.RIGHT:
					startX += speed;
					startY += speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					break;
				}

				Vector2f tile = tiles.getTileCollision(this, startX, startY);

				if (tile != null) {
					int sx = tiles.tilesToPixelsX(tile.x);
					int sy = tiles.tilesToPixelsY(tile.y);
					if (sx > 0) {
						sx = sx - getWidth();
					} else if (sx < 0) {
						sx = tiles.tilesToPixelsX(tile.x);
					}
					if (sy > 0) {
						sy = sy - getHeight();
					} else if (sy < 0) {
						sy = tiles.tilesToPixelsY(tile.y);
					}
				} else {
					setLocation(startX, startY);
				}

			}
		}
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long d) {
		timer.setDelay(d);
	}

	public int getDirection() {
		return direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isComplete() {
		return findPath == null || findPath.size() == 0 || isComplete;
	}

	public void setComplete(boolean c) {
		this.isComplete = true;
	}

	public float getRotationTo(float x, float y) {
		float r = MathUtils.atan2(x - x(), y - y());
		return ShapeUtils.getAngleDiff(rotation, r);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (findPath != null) {
			findPath.clear();
			findPath = null;
		}
	}

	public AStarFindHeuristic getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(AStarFindHeuristic heuristic) {
		this.heuristic = heuristic;
	}

	public boolean isAllDirection() {
		return allDirection;
	}

	public void setAllDirection(boolean allDirection) {
		this.allDirection = allDirection;
	}

	public boolean isMoving() {
		return isMoving;
	}

}
