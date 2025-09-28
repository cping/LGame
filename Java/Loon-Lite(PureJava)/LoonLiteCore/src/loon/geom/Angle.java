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
package loon.geom;

import loon.utils.MathUtils;

public final class Angle {

	public enum AngleType {
		Revolution, Degree, Radian, Gradian
	}

	public static Angle degrees(float degrees) {
		return new Angle(degrees, AngleType.Degree);
	}

	public static Angle radians(float radians) {
		return new Angle(radians, AngleType.Radian);
	}

	public static Angle ofPos(float x, float y) {
		final float degrees = MathUtils.toDegrees(MathUtils.atan2(x, -1 * y));
		final float inRangeDegrees = degrees + MathUtils.ceil(-degrees / 360) * 360;
		return degrees(inRangeDegrees);
	}

	public static Angle ofPos(XY pos) {
		return ofPos(pos.getX(), pos.getY());
	}

	public static Angle ofLine(Line line) {
		if (line == null) {
			return radians(0f);
		}
		return ofPos(line.getStart().sub(line.getEnd()));
	}

	private float _radians;

	public Angle(float arcLength, float radius) {
		this._radians = arcLength / radius;
	}

	public Angle(float angle, AngleType t) {
		setAngle(angle, t);
	}

	public Angle setAngle(float angle, AngleType t) {
		if (t == null) {
			return this;
		}
		switch (t) {
		case Revolution:
			_radians = MathUtils.toRadians(angle);
			break;
		case Degree:
			_radians = MathUtils.toDegrees(angle);
			break;
		case Radian:
			_radians = angle;
			break;
		case Gradian:
			_radians = MathUtils.toGradiansRadians(angle);
			break;
		default:
			_radians = 0f;
			break;
		}
		return this;
	}

	public Angle wrapPositive() {
		float newAngle = _radians % MathUtils.TAU;
		if (newAngle < 0f) {
			newAngle += MathUtils.TAU;
		}
		_radians = newAngle;
		return this;
	}

	public Angle wrap() {
		_radians = MathUtils.wrapAngle(_radians);
		return this;
	}

	public Line on(Line line) {
		final float radians = _radians;
		final float sinus = MathUtils.sin(radians);
		final float cosinus = MathUtils.cos(radians);
		final Vector2f lineEnd = line.getEnd();
		final Vector2f translated = line.getStart().sub(lineEnd);
		final float newX = translated.getX() * cosinus - translated.getY() * sinus + lineEnd.getX();
		final float newY = translated.getX() * sinus + translated.getY() * cosinus + lineEnd.getY();
		return Line.between(lineEnd, Vector2f.at(newX, newY));
	}

	public Angle add(Angle other) {
		return addDegrees(other.getDegrees());
	}

	public Angle addDegrees(final float degrees) {
		return degrees(degrees + degrees);
	}

	public float getRevolutions() {
		return _radians / MathUtils.TAU;
	}

	public Angle setRevolutions(float v) {
		this._radians = v * MathUtils.TAU;
		return this;
	}

	public float getDegrees() {
		return MathUtils.toDegrees(_radians);
	}

	public Angle setDegrees(float v) {
		_radians = MathUtils.toRadians(v);
		return this;
	}

	public Angle invertDegrees() {
		return Angle.degrees(1f - _radians);
	}

	public Angle invertRadians() {
		return Angle.radians(1f - _radians);
	}

	public float getRadians() {
		return this._radians;
	}

	public float getMinutes() {
		final float degrees = MathUtils.toDegrees(_radians);
		if (degrees < 0) {
			final float degreesfloor = MathUtils.ceil(degrees);
			return (degrees - degreesfloor) * 60.0f;
		} else {
			final float degreesfloor = MathUtils.floor(degrees);
			return (degrees - degreesfloor) * 60.0f;
		}
	}

	public Angle setMinutes(float v) {
		final float degrees = MathUtils.toDegrees(_radians);
		float degreesfloor = MathUtils.floor(degrees);
		degreesfloor += v / 60.0f;
		_radians = MathUtils.toRadians(degreesfloor);
		return this;
	}

	public float getSeconds() {
		final float degrees = MathUtils.toDegrees(_radians);
		if (degrees < 0) {
			final float degreesfloor = MathUtils.ceil(degrees);
			final float minutes = (degrees - degreesfloor) * 60f;
			final float minutesfloor = MathUtils.ceil(minutes);
			return (minutes - minutesfloor) * 60f;
		} else {
			final float degreesfloor = MathUtils.floor(degrees);
			final float minutes = (degrees - degreesfloor) * 60f;
			final float minutesfloor = MathUtils.floor(minutes);
			return (minutes - minutesfloor) * 60f;
		}
	}

	public Angle setSeconds(float v) {
		final float degrees = MathUtils.toDegrees(_radians);
		float degreesfloor = MathUtils.floor(degrees);
		final float minutes = (degrees - degreesfloor) * 60f;
		float minutesfloor = MathUtils.floor(minutes);
		minutesfloor += v / 60f;
		degreesfloor += minutesfloor / 60f;
		_radians = MathUtils.toRadians(degreesfloor);
		return this;
	}

	@Override
	public String toString() {
		return "Angle [" + getDegrees() + "]";
	}
}
