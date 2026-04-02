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

import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class Direction {
	// 基本方向
	// 北 (90°)
	public static final Direction UP = new Direction(0, -1, "up", "north");
	// 南 (270°)
	public static final Direction DOWN = new Direction(0, 1, "down", "south");
	// 西 (180°)
	public static final Direction LEFT = new Direction(-1, 0, "left", "west");
	// 东 (0°)
	public static final Direction RIGHT = new Direction(1, 0, "right", "east");

	// 对角方向
	// 西北 (135°)
	public static final Direction UP_LEFT = new Direction(-1, -1, "up_left", "northwest");
	// 东北 (45°)
	public static final Direction UP_RIGHT = new Direction(1, -1, "up_right", "northeast");
	// 西南 (225°)
	public static final Direction DOWN_LEFT = new Direction(-1, 1, "down_left", "southwest");
	// 东南 (315°)
	public static final Direction DOWN_RIGHT = new Direction(1, 1, "down_right", "southeast");

	// 无方向
	public static final Direction NONE = new Direction(0, 0, "none", "none");

	// 所有方向列表
	public static final TArray<Direction> ALL_DIRECTIONS = TArray.with(UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT,
			DOWN_LEFT, DOWN_RIGHT, NONE);

	// 名称映射表
	public static final ObjectMap<String, Direction> MAP_BY_NAME = new ObjectMap<String, Direction>();
	static {
		for (Direction d : ALL_DIRECTIONS) {
			MAP_BY_NAME.put(d.getName(), d);
			MAP_BY_NAME.put(d.getFullName(), d);
		}
	}
	// x方向位移
	private final int dx;
	// y方向位移
	private final int dy;
	// 简称
	private final String name;
	// 全称
	private final String fullName;

	private Direction(int dx, int dy, String name, String fullName) {
		this.dx = dx;
		this.dy = dy;
		this.name = name;
		this.fullName = fullName;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	/**
	 * 根据位移获取方向
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static Direction fromDelta(int dx, int dy) {
		if (dx == 0 && dy < 0) {
			return UP;
		}
		if (dx == 0 && dy > 0) {
			return DOWN;
		}
		if (dx < 0 && dy == 0) {
			return LEFT;
		}
		if (dx > 0 && dy == 0) {
			return RIGHT;
		}
		if (dx < 0 && dy < 0) {
			return UP_LEFT;
		}
		if (dx > 0 && dy < 0) {
			return UP_RIGHT;
		}
		if (dx < 0 && dy > 0) {
			return DOWN_LEFT;
		}
		if (dx > 0 && dy > 0) {
			return DOWN_RIGHT;
		}
		return NONE;
	}

	/**
	 * 获取反方向
	 * 
	 * @return
	 */
	public Direction getOpposite() {
		if (this == UP) {
			return DOWN;
		}
		if (this == DOWN) {
			return UP;
		}
		if (this == LEFT) {
			return RIGHT;
		}
		if (this == RIGHT) {
			return LEFT;
		}
		if (this == UP_LEFT) {
			return DOWN_RIGHT;
		}
		if (this == UP_RIGHT) {
			return DOWN_LEFT;
		}
		if (this == DOWN_LEFT) {
			return UP_RIGHT;
		}
		if (this == DOWN_RIGHT) {
			return UP_LEFT;
		}
		return NONE;
	}

	public static Direction fromAngle(float angleDegrees) {
		float radians = MathUtils.toRadians(angleDegrees);
		int dx = MathUtils.round(MathUtils.cos(radians));
		int dy = MathUtils.round(MathUtils.sin(radians));
		return fromDelta(dx, dy);
	}

	public boolean isDiagonal() {
		return dx != 0 && dy != 0;
	}

	public boolean isCardinal() {
		return (this == UP || this == DOWN || this == LEFT || this == RIGHT);
	}

	public boolean isNone() {
		return this == NONE;
	}

	public static Direction fromString(String name) {
		if (name == null) {
			return NONE;
		}
		Direction d = MAP_BY_NAME.get(name.toLowerCase());
		return d != null ? d : NONE;
	}

	public int[] toVector() {
		return new int[] { dx, dy };
	}

	public float[] toUnitVector() {
		if (isNone()) {
			return new float[] { 0f, 0f };
		}
		float length = MathUtils.sqrt(dx * dx + dy * dy);
		return new float[] { dx / length, dy / length };
	}

	public float toAngle() {
		return MathUtils.toDegrees(MathUtils.atan2(dy, dx));
	}

	public Direction rotate90Clockwise() {
		return fromDelta(dy, -dx);
	}

	public Direction rotate90CounterClockwise() {
		return fromDelta(-dy, dx);
	}

	public Direction rotate(int degrees) {
		float radians = MathUtils.toRadians(degrees);
		float cos = MathUtils.cos(radians);
		float sin = MathUtils.sin(radians);
		int newDx = MathUtils.round(dx * cos - dy * sin);
		int newDy = MathUtils.round(dx * sin + dy * cos);
		return fromDelta(newDx, newDy);
	}

	public float rotateTo(Direction target) {
		float currentAngle = this.toAngle();
		float targetAngle = target.toAngle();
		float diff = targetAngle - currentAngle;
		while (diff > 180) {
			diff -= 360;
		}
		while (diff < -180) {
			diff += 360;
		}
		return diff;
	}

	public float distance(Direction other) {
		int dxDiff = this.dx - other.dx;
		int dyDiff = this.dy - other.dy;
		return MathUtils.sqrt(dxDiff * dxDiff + dyDiff * dyDiff);
	}

	public float dot(Direction other) {
		return this.dx * other.dx + this.dy * other.dy;
	}

	public float angleBetween(Direction other) {
		float dot = this.dot(other);
		float mag1 = MathUtils.sqrt(this.dx * this.dx + this.dy * this.dy);
		float mag2 = MathUtils.sqrt(other.dx * other.dx + other.dy * other.dy);
		if (mag1 == 0 || mag2 == 0) {
			return 0f;
		}
		float cosTheta = dot / (mag1 * mag2);
		cosTheta = MathUtils.max(-1.0f, MathUtils.min(1.0f, cosTheta));
		return MathUtils.toDegrees(MathUtils.acos(cosTheta));
	}

	public String getDirType() {
		if (isNone()) {
			return "none";
		}
		if (isCardinal()) {
			return "cardinal";
		}
		if (isDiagonal()) {
			return "diagonal";
		}
		return "unknown";
	}

	public final static TArray<Direction> values() {
		return ALL_DIRECTIONS;
	}

	public final static TArray<Direction> baseValues() {
		return TArray.with(UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT);
	}

	@Override
	public String toString() {
		return name.toUpperCase();
	}
}