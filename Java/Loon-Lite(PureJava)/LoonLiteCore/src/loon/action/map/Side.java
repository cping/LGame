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
import loon.geom.Vector2f;

/**
 * 地图边界设定用类
 */
public class Side implements Config {

	private int _direction = EMPTY;

	private Vector2f _pos = new Vector2f();

	public Side() {
		this(EMPTY);
	}

	public Side(int dir) {
		this._direction = dir;
	}

	protected void updateDirection() {
		this._pos = Field2D.getDirection(_direction);
	}

	public int dx() {
		return this._pos.x();
	}

	public int dy() {
		return this._pos.y();
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

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (other instanceof Side) {
			return (_direction == ((Side) other)._direction);
		}
		return false;
	}

	public static int getOppositeSide(int side) {
		if (side == Config.TUP) {
			return Config.TDOWN;
		}
		if (side == Config.UP) {
			return Config.DOWN;
		}
		if (side == Config.TDOWN) {
			return Config.TUP;
		}
		if (side == Config.DOWN) {
			return Config.UP;
		}
		if (side == Config.TLEFT) {
			return Config.TRIGHT;
		}
		if (side == Config.LEFT) {
			return Config.RIGHT;
		}
		if (side == Config.TRIGHT) {
			return Config.TLEFT;
		}
		if (side == Config.RIGHT) {
			return Config.LEFT;
		}
		return Config.EMPTY;
	}

	public static int getSideFromDirection(Vector2f direction) {
		return getSideFromDirection(Vector2f.ZERO(), direction, 1);
	}

	public static int getSideFromDirection(Vector2f initVector, Vector2f direction) {
		return getSideFromDirection(initVector, direction, 1);
	}

	public static int getSideFromDirection(final Vector2f initVector, final Vector2f direction, final int val) {
		Vector2f[] directions = { initVector.move_left(val), initVector.move_right(val), initVector.move_up(val),
				initVector.move_down(val) };
		int[] directionEnum = { Config.TLEFT, Config.TRIGHT, Config.TUP, Config.TDOWN };
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
