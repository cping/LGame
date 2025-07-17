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

import loon.LRelease;
import loon.LSystem;
import loon.geom.Line;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 交给用户自定义行走路径的对象移动路径存储用类,此类没有寻径能力,仅仅是用来记录一组固定的行走路线
 */
public class CustomPath implements LRelease {

	public static CustomPathObj getObjToAbsolutePoint(CustomPath path, PathType t, float startX, float startY,
			float endX, float endY) {
		CustomPathObj pathSegment = new CustomPathObj(t, startX, startY, endX, endY);
		Vector2f end = pathSegment.getEnd();
		path._currentX = MathUtils.iceil(end.x);
		path._currentY = MathUtils.iceil(end.y);
		return pathSegment;
	}

	public static enum AngleType {
		Deg, Rad
	}

	public static enum PathType {
		Line, Arc, Move
	}

	private boolean _dirty;

	private boolean _moved;

	protected TArray<CustomPathObj> _paths = new TArray<CustomPathObj>();

	protected TArray<Vector2f> _steps = new TArray<Vector2f>();

	private CustomPathMove _pathMove;

	private float _currentX, _currentY;

	private float _positionX, _positionY;

	private float _scaleX, _scaleY;

	private int _count;

	private String _name;

	public CustomPath() {
		this(LSystem.UNKNOWN);
	}

	public CustomPath(String n) {
		this(n, 1f);
	}

	public CustomPath(TArray<Vector2f> list) {
		this(list, 1f);
	}

	public CustomPath(TArray<Vector2f> list, float scale) {
		this(list, scale, scale);
	}

	public CustomPath(TArray<Vector2f> list, float sx, float sy) {
		this(LSystem.UNKNOWN, list, sx, sy);
	}

	public CustomPath(String n, float scale) {
		this(n, scale, scale);
	}

	public CustomPath(String n, float sx, float sy) {
		this(n, null, sx, sy);
	}

	public CustomPath(String n, float px, float py, float sx, float sy) {
		this(n, null, px, py, sx, sy);
	}

	public CustomPath(String n, TArray<Vector2f> list, float sx, float sy) {
		this(n, list, 0f, 0f, sx, sy);
	}

	public CustomPath(String n, TArray<Vector2f> list, float px, float py, float sx, float sy) {
		this._name = n;
		this._currentX = px;
		this._currentY = py;
		this._scaleX = sx;
		this._scaleY = sy;
		if (list != null) {
			add(list);
		}
	}

	protected CustomPathObj moveToAbsolutePoint(PathType t, float endX, float endY) {
		return getObjToAbsolutePoint(this, t, _currentX, _currentY, endX, endY);
	}

	public CustomPathObj moveTo(float x, float y) {
		CustomPathObj pos = null;
		if (_steps.size == 0 || !_moved) {
			pos = moveToAbsolutePoint(PathType.Move, x, y);
			pos._calculatedLength = 0f;
			_dirty = true;
			_moved = true;
			_paths.add(pos);
		} else {
			final Vector2f lastPos = _steps.last();
			this._currentX = lastPos.x;
			this._currentY = lastPos.y;
			lineTo(x, y);
		}
		return pos;
	}

	public CustomPathObj lineTo(float x, float y) {
		CustomPathObj pos = moveToAbsolutePoint(PathType.Line, x, y);
		float xDiff = pos.getDifferenceX();
		float yDiff = pos.getDifferenceY();
		pos._calculatedLength = MathUtils.sqrt(xDiff * xDiff + yDiff * yDiff);
		_count += pos._calculatedLength;
		_dirty = true;
		_paths.add(pos);
		return pos;
	}

	public CustomPathObj arcTo(float endX, float endY, float angle) {
		return arcTo(endX, endY, angle, LSystem.LAYER_TILE_SIZE, AngleType.Deg);
	}

	public CustomPathObj arcTo(float endX, float endY, float angle, int side) {
		return arcTo(endX, endY, angle, side, AngleType.Deg);
	}

	public CustomPathObj arcTo(float endX, float endY, float angle, int side, AngleType angleType) {
		if (!_moved) {
			moveTo(endX, endY);
		}
		CustomPathObj pos = moveToAbsolutePoint(PathType.Arc, endX / 2f, endY / 2f);
		pos._angleType = angleType;
		pos._arcAngle = angle;
		pos._sideLength = side;
		updateArcLength(pos);
		_paths.add(pos);
		_count += pos._calculatedLength;
		return pos;
	}

	public Vector2f length(float length) {
		float lengthCount = 0f;
		float curLength = 0f;
		CustomPathObj pos = null;
		if (length >= _count && _paths.size > 0) {
			CustomPathObj segment = _paths.last();
			return segment.length();
		} else {
			for (int i = 0; i < _paths.size; i++) {
				float pathLength = _paths.get(i)._calculatedLength;
				if (lengthCount + pathLength > length) {
					pos = _paths.get(i);
					curLength = length - lengthCount;
					break;
				} else {
					lengthCount += pathLength;
				}
			}

			if (pos != null) {
				return pos.length(curLength);
			} else {
				return Vector2f.ZERO();
			}
		}
	}

	public Vector2f pointAtIndex(int index) {
		updateSteps();
		if (index < 0 || index >= _steps.size) {
			return null;
		}
		return _steps.get(index).cpy();
	}

	public Vector2f pointAtIndexEnd(int index) {
		updateSteps();
		if (index < 0 || index >= _steps.size) {
			return null;
		}
		if (index < _steps.size - 1) {
			Vector2f pos = _steps.get(index + 1);
			return pos;
		} else {
			return length(this._count);
		}
	}

	public float lengthAtIndex(int index) {
		float lengthCount = 0f;
		for (int i = 0; i < index; i++) {
			lengthCount += _paths.get(i)._calculatedLength;
		}
		return lengthCount;
	}

	protected void updateArcLength(CustomPathObj path) {
		Vector2f first = path.getStart().cpy();
		Vector2f second = path.getEnd().cpy();

		Vector2f firstToSecond = second.sub(first);

		float angleInRadians = path._angleType == AngleType.Rad ? path._arcAngle : MathUtils.toRadians(path._arcAngle);

		Vector2f firstTangent = firstToSecond.rotateRadians(-angleInRadians / 2);
		Vector2f secondTangent = firstToSecond.rotateRadians(angleInRadians / 2);

		Vector2f firstNormal = new Vector2f(firstTangent.y, -firstTangent.x);
		Vector2f secondNormal = new Vector2f(secondTangent.y, -secondTangent.x);

		Line firstLine = new Line(path.getStart().x, path.getStart().y, path.getStart().x + firstNormal.x,
				path.getStart().y + firstNormal.y);

		Line secondLine = new Line(path.getEnd().x, path.getEnd().y, path.getEnd().x + secondNormal.x,
				path.getEnd().y + secondNormal.y);

		Vector2f intersects = Line.getIntersects(firstLine, secondLine);

		float radius;

		if (intersects == null || intersects.equals(first) || intersects.equals(second)) {
			radius = (first.sub(second)).len() / 2f;
			path._circleCenter = first.add(second).div(2f);
		} else {
			path._circleCenter = intersects;

			radius = (first.sub(path._circleCenter)).len();
		}

		path._calculatedLength = MathUtils.abs(radius * angleInRadians);

	}

	public int length() {
		return _count;
	}

	public CustomPathMove getMovePath() {
		updateSteps();
		if (this._pathMove == null) {
			this._pathMove = new CustomPathMove(this);
		}
		return this._pathMove;
	}

	public int size() {
		updateSteps();
		return _steps.size;
	}

	public Vector2f getStep(int index) {
		updateSteps();
		if (index < 0 || index >= _steps.size) {
			return null;
		}
		if ((_positionX != 0f && _positionY != 0f) || (_scaleX != 1f && _scaleY != 1f)) {
			return _steps.get(index).add(_positionX, _positionY).mul(_scaleX, _scaleY);
		}
		return _steps.get(index).cpy();
	}

	public Vector2f first() {
		return getStep(0);
	}

	public Vector2f last() {
		return getStep(_steps.size < 1 ? 0 : _steps.size - 1);
	}

	public Vector2f get(int index) {
		return getStep(index);
	}

	public float getX(int index) {
		return getStep(index).getX();
	}

	public float getY(int index) {
		return getStep(index).getY();
	}

	public CustomPath append(float x, float y) {
		updateSteps();
		_steps.add(new Vector2f(x, y));
		return this;
	}

	public CustomPath prepend(float x, float y) {
		updateSteps();
		_steps.unshift(new Vector2f(x, y));
		return this;
	}

	public CustomPath remove(Vector2f step) {
		updateSteps();
		_steps.removeValue(step, true);
		return this;
	}

	public CustomPath removeIndex(int idx) {
		updateSteps();
		_steps.removeIndex(idx);
		return this;
	}

	public Vector2f pop() {
		updateSteps();
		return _steps.pop();
	}

	public TArray<Vector2f> getSteps() {
		updateSteps();
		return this._steps;
	}

	public CustomPath reverse() {
		updateSteps();
		_steps.reverse();
		return this;
	}

	public CustomPath add(CustomPath path) {
		if (path == this) {
			return this;
		}
		updateSteps();
		if (path != null) {
			path.updateSteps();
			_steps.addAll(path._steps);
		}
		return this;
	}

	public CustomPath addLoop(float startX, float startY, float endX, float endY, int count) {
		return addLoop(new Vector2f(startX, startY), new Vector2f(endX, endY), count);
	}

	public CustomPath addLoop(Vector2f start, Vector2f end, int count) {
		if (start == null || end == null) {
			return this;
		}
		updateSteps();
		for (int i = 0; i < count; i++) {
			_steps.add(start.cpy());
			_steps.add(end.cpy());
		}
		return this;
	}

	public CustomPath loop(int count) {
		updateSteps();
		final TArray<Vector2f> tmp = new TArray<Vector2f>();
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < _steps.size; j++) {
				Vector2f loc = _steps.get(j);
				if (loc != null) {
					tmp.add(loc.cpy());
				}
			}
		}
		_steps.clear();
		_steps.addAll(tmp);
		return this;
	}

	public CustomPath add(float... pos) {
		if (pos == null) {
			return this;
		}
		updateSteps();
		for (int i = 0, j = 0; i < pos.length; i += 2) {
			if (j < pos.length - 1) {
				float px = pos[i];
				float py = pos[i + 1];
				_steps.add(Vector2f.at(px, py));
			}
		}
		return this;
	}

	public CustomPath add(Vector2f... pos) {
		if (pos == null) {
			return this;
		}
		updateSteps();
		for (int i = 0; i < pos.length; i++) {
			Vector2f loc = pos[i];
			if (loc != null) {
				_steps.add(loc);
			}
		}
		return this;
	}

	public CustomPath add(TArray<Vector2f> v) {
		if (v == null) {
			return this;
		}
		updateSteps();
		for (int i = 0; i < v.size; i++) {
			Vector2f loc = v.get(i);
			if (loc != null) {
				_steps.add(loc);
			}
		}
		return this;
	}

	public CustomPath add(Vector2f step) {
		updateSteps();
		if (step != null) {
			_steps.add(step);
		}
		return this;
	}

	public CustomPath cpy() {
		updateSteps();
		return new CustomPath(this._name, this._steps, this._currentX, this._currentY, this._scaleX, this._scaleY);
	}

	public CustomPath cpyReverse() {
		return cpy().reverse();
	}

	public boolean contains(int x, int y) {
		updateSteps();
		return _steps.contains(new Vector2f(x, y), false);
	}

	public boolean containsScale(int x, int y) {
		updateSteps();
		return _steps.contains(new Vector2f(x, y).mul(_scaleX, _scaleY), false);
	}

	public boolean isScale() {
		return _scaleX != 1f || _scaleY != 1f;
	}

	public boolean isMoved() {
		return _positionX != 1f || _positionY != 1f;
	}

	public CustomPath setTileSize(float size) {
		return setScale(size);
	}

	public CustomPath setTileSize(float tileWidth, float tileHeight) {
		return setScale(tileWidth, tileHeight);
	}

	public CustomPath setScale(float s) {
		return setScale(s, s);
	}

	public CustomPath setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
		return this;
	}

	public CustomPath setPosition(float px, float py) {
		this._positionX = px;
		this._positionY = py;
		return this;
	}

	public float getTileWidth() {
		return getScaleX();
	}

	public float getTileHeight() {
		return getScaleY();
	}

	public float getScaleX() {
		return _scaleX;
	}

	public CustomPath setTileWidth(float tileWidth) {
		return setScaleX(tileWidth);
	}

	public CustomPath setScaleX(float _scaleX) {
		this._scaleX = _scaleX;
		return this;
	}

	public float getScaleY() {
		return _scaleY;
	}

	public float getPositionX() {
		return _positionX;
	}

	public CustomPath setPositionX(float px) {
		this._positionX = px;
		return this;
	}

	public float getPositionY() {
		return _positionY;
	}

	public CustomPath setPositionY(float py) {
		this._positionY = py;
		return this;
	}

	public CustomPath setTileHeight(float tileHeight) {
		return setScaleY(tileHeight);
	}

	public CustomPath setScaleY(float _scaleY) {
		this._scaleY = _scaleY;
		return this;
	}

	public CustomPath setName(String n) {
		if (!StringUtils.isEmpty(n)) {
			this._name = n;
		}
		return this;
	}

	public String getName() {
		return _name;
	}

	public boolean isEmpty() {
		updateSteps();
		return _steps.isEmpty();
	}

	protected void updateSteps() {
		if (_dirty) {
			for (int i = 0; i < _paths.size; i++) {
				CustomPathObj pos = _paths.get(i);
				if (pos != null) {
					if (pos._pathType == PathType.Arc) {
						float sideLength = pos._sideLength;
						float anglePieceRad = MathUtils.TWO_PI;
						float radius = pos._arcAngle;
						int sides = MathUtils.getCircleArcSideCount(radius,
								MathUtils.abs(anglePieceRad * MathUtils.RAD_TO_DEG), sideLength);
						float angleStep = MathUtils.toDegrees(anglePieceRad / sides);
						float currentRadius = radius;
						for (int j = 0; j < sides; j++) {
							float currentAngle = angleStep * j;
							float radian = MathUtils.toRadians(currentAngle);
							float endX = (pos._circleCenter.x + MathUtils.cos(radian) * currentRadius);
							float endY = (pos._circleCenter.y + MathUtils.sin(radian) * currentRadius);
							_steps.add(Vector2f.at(MathUtils.iceil(endX), MathUtils.iceil(endY)));
						}
					} else {
						if (i == 0) {
							_steps.add(pos.getStart());
							_steps.add(pos.getEnd());
						} else {
							_steps.add(pos.getEnd());
						}
					}
				}
			}
			_paths.clear();
			_dirty = !_dirty;
		}
	}

	public CustomPath clear() {
		_steps.clear();
		_paths.clear();
		_count = 0;
		_currentX = 0;
		_currentY = 0;
		_moved = _dirty = false;
		return this;
	}

	public boolean isDirty() {
		return _dirty;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (other instanceof CustomPath) {
			updateSteps();
			CustomPath path = (CustomPath) other;
			return path._positionX == this._positionX && path._positionY == this._positionY
					&& path._scaleX == this._scaleX && path._scaleY == this._scaleY && _steps.equals(path._steps);
		}
		return false;
	}

	@Override
	public int hashCode() {
		updateSteps();
		final int prime = 31;
		int result = 1;
		result = LSystem.unite(result, _scaleX);
		result = LSystem.unite(result, _scaleY);
		result = LSystem.unite(result, _positionX);
		result = LSystem.unite(result, _positionY);
		result = LSystem.unite(result, _steps.hashCode());
		return prime * result;
	}

	@Override
	public String toString() {
		updateSteps();
		final StringKeyValue builder = new StringKeyValue("CustomPath");
		builder.kv("name", _name).comma().kv("positionX", _positionX).comma().kv("positionY", _positionY).comma()
				.kv("scaleX", _scaleX).comma().kv("scaleY", _scaleY).comma().kv("steps", _steps.toString());
		return builder.toString();
	}

	@Override
	public void close() {
		clear();
	}

}
