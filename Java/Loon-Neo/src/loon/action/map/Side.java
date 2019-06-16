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
	
	protected void updateDirection(){
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
		if (side == Side.TUP) {
			return Side.TDOWN;
		}
		if (side == Side.TDOWN) {
			return Side.TUP;
		}
		if (side == Side.TLEFT) {
			return Side.TRIGHT;
		}
		if (side == Side.TRIGHT) {
			return Side.TLEFT;
		}
		return Side.EMPTY;
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
}
