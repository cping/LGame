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
public class Side {

	public final static int None = 0;

	public final static int Top = 1;

	public final static int Left = 2;

	public final static int Right = 3;

	public final static int Bottom = 4;

	private int _direction = None;

	public Side() {
		this(None);
	}

	public Side(int dir) {
		this._direction = dir;
	}

	public int getDirection() {
		return this._direction;
	}

	public boolean estDirection(Side side) {
		switch (getDirection()) {
		case Left:
		case Right:
			if (side.getDirection() == Bottom || side.getDirection() == Top) {
				return true;
			} else {
				return false;
			}
		case Bottom:
		case Top:
			if (side.getDirection() == Left || side.getDirection() == Right) {
				return true;
			} else {
				return false;
			}
		case None:
		default:
			return false;
		}
	}

	public int getOppositeSide() {
		return getOppositeSide(_direction);
	}

	public static int getOppositeSide(int side) {
		if (side == Side.Top) {
			return Side.Bottom;
		}
		if (side == Side.Bottom) {
			return Side.Top;
		}
		if (side == Side.Left) {
			return Side.Right;
		}
		if (side == Side.Right) {
			return Side.Left;
		}
		return Side.None;
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
		int[] directionEnum = { Side.Left, Side.Right, Side.Top, Side.Bottom };
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
