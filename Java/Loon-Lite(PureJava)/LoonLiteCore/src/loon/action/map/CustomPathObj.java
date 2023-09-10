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

import loon.action.map.CustomPath.AngleType;
import loon.action.map.CustomPath.PathType;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class CustomPathObj {

	protected AngleType _angleType;

	protected PathType _pathType;

	protected float _arcAngle;

	private Vector2f _start;
	private Vector2f _end;

	protected Vector2f _circleCenter;

	protected float _calculatedLength;

	protected float _sideLength;

	public CustomPathObj(PathType t, float startX, float startY, float endX, float endY) {
		this._pathType = t;
		this._sideLength = 32;
		this._start = new Vector2f(startX, startY);
		this._end = new Vector2f(endX, endY);
	}

	public Vector2f length() {
		return length(_calculatedLength);
	}

	public Vector2f length(float lengthStart) {
		if (_pathType == PathType.Line) {
			return _start.add(_end.sub(_start)).length(lengthStart);
		} else if (_pathType == PathType.Move) {
			return _start;
		} else {
			final Vector2f centerToStart = _start.sub(_circleCenter);
			final float radius = centerToStart.len();
			final float anglePerArcLength = 1f / radius;
			final float angleToRotateBy = MathUtils.sign(_arcAngle) * anglePerArcLength * lengthStart;
			return centerToStart.rotateRadians(angleToRotateBy).add(_circleCenter);
		}
	}

	public float getDifferenceX() {
		return _end.x - _start.x;
	}

	public float getDifferenceY() {
		return _end.y - _start.y;
	}

	public Vector2f getStart() {
		return _start;
	}

	public Vector2f getEnd() {
		return _end;
	}

	public Vector2f getCircleCenter() {
		return _circleCenter;
	}

	public float getCalculatedLength() {
		return _calculatedLength;
	}

	public CustomPathObj setCalculatedLength(float c) {
		this._calculatedLength = c;
		return this;
	}

	public AngleType getAngleType() {
		return _angleType;
	}

	public CustomPathObj setAngleType(AngleType a) {
		this._angleType = a;
		return this;
	}

	public float getSideLength() {
		return _sideLength;
	}

	public CustomPathObj setSideLength(float side) {
		this._sideLength = side;
		return this;
	}

}
