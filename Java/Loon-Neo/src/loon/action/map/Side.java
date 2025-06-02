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
package loon.action.map;

import loon.LSystem;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.MathUtils;

/**
 * 地图边界设定用类
 */
public class Side implements Config {

	public final static int TOP = TUP;

	public final static int BOTTOM = TDOWN;

	private final Vector2f _pos = new Vector2f();

	private int _direction = EMPTY;

	public Side() {
		this(EMPTY);
	}

	public Side(int dir) {
		this._direction = dir;
	}

	public Side pos(Vector2f v) {
		if (v == null) {
			return this;
		}
		this._pos.set(v);
		return this;
	}

	protected void updateDirection() {
		this._pos.set(Field2D.getDirection(_direction));
	}

	public int dx() {
		return this._pos.x();
	}

	public int dy() {
		return this._pos.y();
	}

	public Vector2f updatePostion() {
		updateDirection();
		return this._pos;
	}

	public int getDirection() {
		return this._direction;
	}

	public Side setDirection(int dir) {
		this._direction = dir;
		this.updateDirection();
		return this;
	}

	public boolean estDirection(Side side) {
		switch (getDirection()) {
		case TLEFT:
		case TRIGHT:
			if (side.getDirection() == TDOWN || side.getDirection() == TUP) {
				return true;
			} else {
				return false;
			}
		case TDOWN:
		case TUP:
			if (side.getDirection() == TLEFT || side.getDirection() == TRIGHT) {
				return true;
			} else {
				return false;
			}
		case LEFT:
		case RIGHT:
			if (side.getDirection() == DOWN || side.getDirection() == UP) {
				return true;
			} else {
				return false;
			}
		case DOWN:
		case UP:
			if (side.getDirection() == LEFT || side.getDirection() == RIGHT) {
				return true;
			} else {
				return false;
			}
		case EMPTY:
		default:
			return false;
		}
	}

	public int getOppositeSide() {
		return getOppositeSide(_direction);
	}

	public int updateOppositeSide(int side) {
		int dir = getOppositeSide(side);
		setDirection(dir);
		return dir;
	}

	public int getPointToSide(RectBox rect) {
		return getPointCollisionSide(rect, _pos);
	}

	public int fromDirection(Vector2f direction) {
		if (direction == null) {
			return EMPTY;
		}
		if (MathUtils.abs(direction.x) >= MathUtils.abs(direction.y)) {
			if (direction.x <= 0) {
				return TLEFT;
			}
			return TRIGHT;
		}
		if (direction.y <= 0) {
			return TUP;
		}
		return TDOWN;
	}

	@Override
	public int hashCode() {
		int hashCode = 66;
		hashCode = LSystem.unite(hashCode, _pos.hashCode());
		hashCode = LSystem.unite(hashCode, _direction);
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (other instanceof Side) {
			Side side = (Side) other;
			return (_direction == side._direction) && (_pos.equals(side._pos));
		}
		return false;
	}

	public Side reset() {
		this._direction = EMPTY;
		this._pos.setZero();
		return this;
	}

	public static int getOppositeSide(int side) {
		if (side == Side.TUP) {
			return Side.TDOWN;
		}
		if (side == Side.UP) {
			return Side.DOWN;
		}
		if (side == Side.TDOWN) {
			return Side.TUP;
		}
		if (side == Side.DOWN) {
			return Side.UP;
		}
		if (side == Side.TLEFT) {
			return Side.TRIGHT;
		}
		if (side == Side.LEFT) {
			return Side.RIGHT;
		}
		if (side == Side.TRIGHT) {
			return Side.TLEFT;
		}
		if (side == Side.RIGHT) {
			return Side.LEFT;
		}
		return Side.EMPTY;
	}

	public static int getPointCollisionSide(final RectBox rect, final XY pos) {
		int side = EMPTY;
		if (pos.getX() == rect.x && pos.getY() >= rect.y && pos.getY() <= rect.height) {
			return TLEFT;
		} else if (pos.getX() == rect.width && pos.getY() >= rect.y && pos.getY() <= rect.height) {
			return TRIGHT;
		} else if (pos.getY() == rect.y && pos.getX() >= rect.x && pos.getX() <= rect.width) {
			return TOP;
		} else if (pos.getY() == rect.height && pos.getX() >= rect.x && pos.getX() <= rect.width) {
			return BOTTOM;
		} else if (pos.getX() < rect.x) {
			side = TLEFT;
		} else if (pos.getX() > rect.width) {
			side = TRIGHT;
		} else if (pos.getY() < rect.y) {
			side = TOP;
		} else if (pos.getY() > rect.height) {
			side = BOTTOM;
		}
		return side;
	}

	public static int getCollisionSide(final RectBox r0, final RectBox r1) {
		int result = EMPTY;

		final boolean isLeft = r0.x + r0.width / 2 < r1.x + r1.width / 2;
		final boolean isAbove = r0.y + r0.height / 2 > r1.y + r1.height / 2;

		float horizontalDir;
		float verticalDir;

		if (isLeft) {
			horizontalDir = r0.x + r0.width - r1.x;
		} else {
			horizontalDir = r1.x + r1.width - r0.x;
		}

		if (isAbove) {
			verticalDir = r1.y + r1.height - r0.y;
		} else {
			verticalDir = r0.y + r0.height - r1.y;
		}

		if (horizontalDir < verticalDir) {
			if (isLeft) {
				result = TLEFT;
			} else {
				result = TRIGHT;
			}
		} else if (isAbove) {
			result = TUP;
		} else {
			result = TDOWN;
		}

		return result;
	}

	public static RectBox getOverlapRect(final RectBox a, final RectBox b) {
		final float left = MathUtils.max(a.x, b.x);
		final float right = MathUtils.min(a.x + a.width, b.x + b.width);

		final float top = MathUtils.max(a.y, b.y);
		final float bottom = MathUtils.min(a.y + a.height, b.y + b.height);

		return new RectBox(left, top, right - left, bottom - top);
	}

	public static int getSideFromDirection(Vector2f direction) {
		return getSideFromDirection(Vector2f.ZERO(), direction, 1f);
	}

	public static int getSideFromDirection(Vector2f initVector, Vector2f direction) {
		return getSideFromDirection(initVector, direction, 1f);
	}

	public static int getSideFromDirection(final Vector2f initVector, final Vector2f direction, final float val) {
		Vector2f[] directions = { initVector.move_left(val), initVector.move_right(val), initVector.move_up(val),
				initVector.move_down(val) };
		int[] directionEnum = { Side.TLEFT, Side.TRIGHT, Side.TUP, Side.TDOWN };
		float max = -Float.MAX_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < directions.length; i++) {
			if (directions[i].dot(direction) > max) {
				max = directions[i].dot(direction);
				maxIndex = i;
			}
		}
		return directionEnum[maxIndex];
	}

	public static String getDirectionName(final int direction) {
		String dirName = LSystem.UNKNOWN;
		switch (direction) {
		case TUP:
			dirName = "UP";
			break;
		case TLEFT:
			dirName = "LEFT";
			break;
		case TDOWN:
			dirName = "DOWN";
			break;
		case TRIGHT:
			dirName = "RIGHT";
			break;
		case UP:
			dirName = "UP_ISO";
			break;
		case LEFT:
			dirName = "LEFT_ISO";
			break;
		case DOWN:
			dirName = "DOWN_ISO";
			break;
		case RIGHT:
			dirName = "RIGHT_ISO";
			break;
		default:
			dirName = "EMPTY";
			break;
		}
		return dirName;
	}

	public String getDirectionName() {
		return getDirectionName(_direction);
	}

	@Override
	public String toString() {
		return getDirectionName();
	}

}
