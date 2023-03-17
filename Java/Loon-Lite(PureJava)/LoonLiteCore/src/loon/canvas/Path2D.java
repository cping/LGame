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
package loon.canvas;

import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StrBuilder;
import loon.utils.TArray;

/**
 * 一个Path的本地具体实现,用于Pixmap和GLEx渲染
 */
public class Path2D implements Path {

	public static enum PathCommand {
		MoveTo, LineTo, CurveTo, CubicCurveTo, Closed
	}

	private final TArray<PathCommand> _commands;

	private final FloatArray _tempData;
	private final FloatArray _data;

	private float _lastX;
	private float _lastY;

	private float _lastStartX;
	private float _lastStartY;

	public Path2D() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public Path2D(int capacity) {
		this._commands = new TArray<>(capacity);
		this._data = new FloatArray(capacity);
		this._tempData = new FloatArray(capacity);
	}

	public Path verticalMoveTo(float y) {
		return moveTo(_lastX, y);
	}

	public Path verticalMoveToRel(float y) {
		return moveTo(_lastX, y + _lastY);
	}

	public Path horizontalMoveTo(float x) {
		return moveTo(x, _lastY);
	}

	public Path horizontalMoveToRel(float x) {
		return moveTo(x + _lastX, _lastY);
	}

	public Path lineToRel(float x, float y) {
		return lineTo(x + _lastX, y + _lastY);
	}

	public Path moveToRel(float x, float y) {
		return moveTo(x + _lastX, y + _lastY);
	}

	@Override
	public Path moveTo(float x, float y) {
		PathCommand cmd = _commands.last();
		if (cmd != PathCommand.MoveTo) {
			_commands.add(PathCommand.MoveTo);
			_data.add(x);
			_data.add(y);
		} else {
			_data.set(_data.size() - 2, x);
			_data.set(_data.size() - 1, y);
		}
		_lastX = x;
		_lastY = y;
		_lastStartX = x;
		_lastStartY = y;
		return this;
	}

	@Override
	public Path lineTo(float x, float y) {
		_commands.add(PathCommand.LineTo);
		_data.add(x);
		_data.add(y);
		_lastX = x;
		_lastY = y;
		return this;
	}

	public Path curveTo(float controlX, float controlY, float anchorX, float anchorY) {
		_commands.add(PathCommand.CurveTo);
		_data.add(controlX);
		_data.add(controlY);
		_data.add(anchorX);
		_data.add(anchorY);
		_lastX = anchorX;
		_lastY = anchorY;
		return this;
	}

	public Path cubicCurveTo(float controlX1, float controlY1, float controlX2, float controlY2, float anchorX,
			float anchorY) {
		_commands.add(PathCommand.CubicCurveTo);
		_data.add(controlX1);
		_data.add(controlY1);
		_data.add(controlX2);
		_data.add(controlY2);
		_data.add(anchorX);
		_data.add(anchorY);
		_lastX = anchorX;
		_lastY = anchorY;
		return this;
	}

	public Path quadToRel(float controlX, float controlY, float x, float y) {
		return quadTo(controlX + _lastX, controlY + _lastY, x + _lastX, y + _lastY);
	}

	public Path quadTo(float controlX, float controlY, float x, float y) {
		return cubicCurveTo(_lastX + 2.0f / 3.0f * (controlX - _lastX), _lastY + 2.0f / 3.0f * (controlY - _lastY),
				x + 2.0f / 3.0f * (controlX - x), y + 2.0f / 3.0f * (controlY - y), x, y);
	}

	public Path drawRect(float x, float y, float width, float height) {
		float x2 = x + width;
		float y2 = y + height;
		this.moveTo(x, y);
		this.lineTo(x2, y);
		this.lineTo(x2, y2);
		this.lineTo(x, y2);
		this.lineTo(x, y);
		return this;
	}

	public Path cubicToRel(float controlX1, float controlY1, float controlX2, float controlY2, float x, float y) {
		return cubicCurveTo(controlX1 + _lastX, controlY1 + _lastY, controlX2 + _lastX, controlY2 + _lastY, x + _lastX,
				y + _lastY);
	}

	@Override
	public Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
		return curveTo(cpx, cpy, x, y);
	}

	@Override
	public Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
		return cubicCurveTo(c1x, c1y, c2x, c2y, x, y);
	}

	public Path line(float startX, float startY, float stopX, float stopY) {
		moveTo(startX, startY).lineTo(stopX, stopY);
		return this;
	}

	public Path lines(float... points) {
		return lines(points, 0, points.length / 4);
	}

	public Path lines(float[] points, int offset, int count) {
		int i = offset;
		while (i <= offset + (count * 4) - 1) {
			moveTo(points[i++], points[i++]).lineTo(points[i++], points[i++]);
		}
		return this;
	}

	public Path lines(FloatArray points) {
		return lines(points, 0, points.size() / 4);
	}

	public Path lines(FloatArray points, int offset, int count) {
		int i = offset;
		while (i <= offset + (count * 4) - 1) {
			moveTo(points.get(i++), points.get(i++)).lineTo(points.get(i++), points.get(i++));
		}
		return this;
	}

	public Path lines(Vector2f... points) {
		return lines(points, 0, points.length / 2);
	}

	public Path lines(Vector2f[] points, int offset, int count) {
		int i = offset;
		while (i <= offset + (count * 2) - 1) {
			Vector2f start = points[i++];
			Vector2f end = points[i++];
			moveTo(start.x, start.y).lineTo(end.x, end.y);
		}
		return this;
	}

	public Path polyline(float... points) {
		if (points.length < 4) {
			return this;
		}

		moveTo(points[0], points[1]);
		for (int i = 2; i < points.length; i += 2) {
			lineTo(points[i], points[i + 1]);
		}
		return this;
	}

	public Path polyline(FloatArray points) {
		if (points.size() < 4) {
			return this;
		}

		moveTo(points.get(0), points.get(1));
		for (int i = 2; i < points.size(); i += 2) {
			lineTo(points.get(i), points.get(i + 1));
		}
		return this;
	}

	public Path polyline(Vector2f... points) {
		if (points.length < 2) {
			return this;
		}

		moveTo(points[0].x, points[0].y);
		for (int i = 1; i < points.length; i++) {
			lineTo(points[i].x, points[i].y);
		}
		return this;
	}

	public Path polygon(float... points) {
		if (points.length < 4) {
			return this;
		}

		int i = 0;
		moveTo(points[i++], points[i++]);
		while (i < points.length) {
			lineTo(points[i++], points[i++]);
		}
		close();
		return this;
	}

	public Path polygon(FloatArray points) {
		if (points.size() < 4) {
			return this;
		}

		int i = 0;
		moveTo(points.get(i++), points.get(i++));
		while (i < points.size()) {
			lineTo(points.get(i++), points.get(i++));
		}
		close();
		return this;
	}

	public Path polygon(Vector2f... points) {
		if (points.length < 2) {
			return this;
		}
		int i = 0;
		moveTo(points[i].x, points[i].y);
		i++;
		while (i < points.length) {
			lineTo(points[i].x, points[i].y);
			i++;
		}
		close();
		return this;
	}

	public Path arcToBezier(float x, float y, float radiusX, float radiusY, float startAngle, float endAngle) {
		return arcToBezier(x, y, radiusX, radiusY, startAngle, endAngle, false);
	}

	public Path arcToBezier(float x, float y, float radiusX, float radiusY, float startAngle, float endAngle,
			boolean anticlockwise) {
		float halfPI = MathUtils.PI * 0.5f;
		float start = startAngle;
		float end = start;
		if (anticlockwise) {
			end += -halfPI - (start % halfPI);
			if (end < endAngle) {
				end = endAngle;
			}
		} else {
			end += halfPI - (start % halfPI);
			if (end > endAngle) {
				end = endAngle;
			}
		}
		float currentX = x + MathUtils.cos(start) * radiusX;
		float currentY = y + MathUtils.sin(start) * radiusY;
		if (this._lastX != currentX || this._lastY != currentY) {
			this.moveTo(currentX, currentY);
		}
		float u = MathUtils.cos(start);
		float v = MathUtils.sin(start);
		for (int i = 0; i < 4; i++) {
			float addAngle = end - start;
			float a = 4f * MathUtils.tan(addAngle / 4f) / 3f;
			float x1 = currentX - v * a * radiusX;
			float y1 = currentY + u * a * radiusY;
			u = MathUtils.cos(end);
			v = MathUtils.sin(end);
			currentX = x + u * radiusX;
			currentY = y + v * radiusY;
			float x2 = currentX + v * a * radiusX;
			float y2 = currentY - u * a * radiusY;
			this.cubicCurveTo(x1, y1, x2, y2, currentX, currentY);
			if (end == endAngle) {
				break;
			}
			start = end;
			if (anticlockwise) {
				end = start - halfPI;
				if (end < endAngle) {
					end = endAngle;
				}
			} else {
				end = start + halfPI;
				if (end > endAngle) {
					end = endAngle;
				}
			}

		}
		return this;
	}

	public Path drawCircle(float x, float y, float radius) {
		return this.arcToBezier(x, y, radius, radius, 0, MathUtils.TWO_PI);
	}

	public Path drawEllipse(float x, float y, float width, float height) {
		float radiusX = width * 0.5f;
		float radiusY = height * 0.5f;
		x += radiusX;
		y += radiusY;
		return this.arcToBezier(x, y, radiusX, radiusY, 0, MathUtils.TWO_PI);
	}

	public Path drawRoundRect(float x, float y, float width, float height, float ellipseWidth, float ellipseHeight) {
		float radiusX = MathUtils.max(0f, (ellipseWidth * 0.5f));
		float radiusY = MathUtils.max(0f, ellipseHeight > 0f ? (ellipseHeight * 0.5f) : radiusX);

		if (radiusX == 0f || radiusY == 0f) {
			this.drawRect(x, y, width, height);
			return this;
		}

		float hw = width * 0.5f;
		float hh = height * 0.5f;
		if (radiusX > hw) {
			radiusX = hw;
		}
		if (radiusY > hh) {
			radiusY = hh;
		}
		if (hw == radiusX && hh == radiusY) {
			if (radiusX == radiusY) {
				this.drawCircle(x + radiusX, y + radiusY, radiusX);
			} else {
				this.drawEllipse(x, y, radiusX * 2, radiusY * 2);
			}
			return this;
		}

		float right = x + width;
		float bottom = y + height;
		float xlw = x + radiusX;
		float xrw = right - radiusX;
		float ytw = y + radiusY;
		float ybw = bottom - radiusY;
		this.moveTo(right, ybw);
		this.curveTo(right, bottom, xrw, bottom);
		this.lineTo(xlw, bottom);
		this.curveTo(x, bottom, x, ybw);
		this.lineTo(x, ytw);
		this.curveTo(x, y, xlw, y);
		this.lineTo(xrw, y);
		this.curveTo(right, y, right, ytw);
		this.lineTo(right, ybw);
		return this;
	}

	public float getArea() {
		_tempData.clear();
		float area = 0;
		int segmentStart = 0;
		float lastStartX = 0;
		float lastStartY = 0;
		float lastX = 0;
		float lastY = 0;
		int segmentCommandsCount = 0;

		for (int i = 0; i < _commands.size; i++) {
			PathCommand cmd = _commands.get(i);
			switch (cmd) {
			case MoveTo:
				if (segmentCommandsCount != 0) {
					_tempData.insert(segmentStart, lastStartX);
					_tempData.insert(segmentStart + 1, lastStartY);
					_tempData.add(lastStartX);
					_tempData.add(lastStartY);
					area += getSegmentArea();

					_tempData.clear();
					segmentStart = _tempData.size();
					segmentCommandsCount = 0;
				}

				lastStartX = _data.get(i + 0);
				lastStartY = _data.get(i + 1);

				lastX = lastStartX;
				lastY = lastStartY;

				break;
			case CurveTo:
				lastX = _data.get(i + 0);
				lastY = _data.get(i + 1);

				_tempData.add(lastX);
				_tempData.add(lastY);

				_tempData.add(_data.get(i + 2));
				_tempData.add(_data.get(i + 3));
				segmentCommandsCount++;

				break;
			case LineTo:
				lastX = _data.get(i + 0);
				lastY = _data.get(i + 1);

				_tempData.add(lastX);
				_tempData.add(lastY);
				segmentCommandsCount++;

				break;
			case CubicCurveTo:
				float x1 = lastX;
				float y1 = lastY;
				float x2 = _data.get(i + 0);
				float y2 = _data.get(i + 1);
				float x3 = _data.get(i + 2);
				float y3 = _data.get(i + 3);
				float x4 = _data.get(i + 4);
				float y4 = _data.get(i + 5);
				tesselateBezier(x1, y1, x2, y2, x3, y3, x4, y4, 0);
				segmentCommandsCount++;

				break;
			case Closed:
				break;
			default:
			}
		}

		_tempData.insert(segmentStart, lastStartX);
		_tempData.insert(segmentStart + 1, lastStartY);
		_tempData.add(lastStartX);
		_tempData.add(lastStartY);
		area += getSegmentArea();

		return area;
	}

	private static boolean checkTesselationTolerance(float x1, float y1, float x2, float y2, float x3, float y3,
			float x4, float y4) {
		float dx = x4 - x1;
		float dy = y4 - y1;
		float d2 = MathUtils.abs(((x2 - x4) * dy - (y2 - y4) * dx));
		float d3 = MathUtils.abs(((x3 - x4) * dy - (y3 - y4) * dx));
		return ((d2 + d3) * (d2 + d3) < 0.25f * (dx * dx + dy * dy));
	}

	private void tesselateBezier(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
			int level) {
		if (level > 8) {
			return;
		} else if (level == 8 || checkTesselationTolerance(x1, y1, x2, y2, x3, y3, x4, y4)) {
			_tempData.add(x4);
			_tempData.add(y4);
			return;
		}

		float x12 = (x1 + x2) * 0.5f;
		float y12 = (y1 + y2) * 0.5f;
		float x23 = (x2 + x3) * 0.5f;
		float y23 = (y2 + y3) * 0.5f;
		float x34 = (x3 + x4) * 0.5f;
		float y34 = (y3 + y4) * 0.5f;
		float x123 = (x12 + x23) * 0.5f;
		float y123 = (y12 + y23) * 0.5f;
		float x234 = (x23 + x34) * 0.5f;
		float y234 = (y23 + y34) * 0.5f;
		float x1234 = (x123 + x234) * 0.5f;
		float y1234 = (y123 + y234) * 0.5f;

		tesselateBezier(x1, y1, x12, y12, x123, y123, x1234, y1234, level + 1);
		tesselateBezier(x1234, y1234, x234, y234, x34, y34, x4, y4, level + 1);
	}

	private float getSegmentArea() {
		float area = 0;
		int size = _tempData.size();
		for (int i = 0; i < size; i += 2) {
			int y1 = i + 1;
			int x2 = (i + 2) % size;
			int y2 = (i + 3) % size;
			area += _tempData.get(i) * _tempData.get(y2);
			area -= _tempData.get(x2) * _tempData.get(y1);
		}
		area *= 0.5f;
		return area;
	}

	public float[] getPointfX() {
		float[] list = new float[size() / 2];
		for (int i = 0, j = 0; i < _data.length; i += 2, j++) {
			list[j] = _data.get(i);
		}
		return list;
	}

	public float[] getPointfY() {
		float[] list = new float[size() / 2];
		for (int i = 0, j = 0; i < _data.length; i += 2, j++) {
			list[j] = _data.get(i + 1);
		}
		return list;
	}

	public int[] getPointiX() {
		int[] list = new int[size() / 2];
		for (int i = 0, j = 0; i < _data.length; i += 2, j++) {
			list[j] = (int) _data.get(i);
		}
		return list;
	}

	public int[] getPointiY() {
		int[] list = new int[size() / 2];
		for (int i = 0, j = 0; i < _data.length; i += 2, j++) {
			list[j] = (int) _data.get(i + 1);
		}
		return list;
	}

	public boolean isEmpty() {
		return _data.size() == 0;
	}

	@Override
	public Path reset() {
		_commands.clear();
		_data.clear();
		_tempData.clear();
		_lastX = 0f;
		_lastY = 0f;
		_lastStartX = 0f;
		_lastStartY = 0f;
		return this;
	}

	public float getLastX() {
		return _lastX;
	}

	public float getLastY() {
		return _lastY;
	}

	public float getLastStartX() {
		return _lastStartX;
	}

	public float getLastStartY() {
		return _lastStartY;
	}

	public int size() {
		return _data.size();
	}

	public boolean isClosed() {
		return _commands.last() == PathCommand.Closed;
	}

	public Path2D update(FloatArray arrays) {
		_data.clear();
		_data.addAll(arrays);
		return this;
	}

	public TArray<Vector2f> getVecs() {
		TArray<Vector2f> list = new TArray<>(_data.size() / 2);
		for (int i = 0; i < _data.size(); i += 2) {
			list.add(new Vector2f(_data.get(i), _data.get(i + 1)));
		}
		return list;
	}

	public Path2D addPath(Path2D p, float px, float py) {
		_commands.addAll(p._commands);
		for (int i = 0; i < p._data.size(); i += 2) {
			_data.add(p._data.get(i) + px);
			_data.add(p._data.get(i + 1) + py);
		}
		return this;
	}

	public float[] getResult() {
		return _data.toArray();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (int i = 0, n = _data.size(); i < n; i++) {
			result = prime * result + NumberUtils.floatToIntBits(_data.get(i));
		}
		return result;
	}

	@Override
	public String toString() {
		StrBuilder builder = new StrBuilder("Path:\n");
		for (int i = 0; i < _commands.size; i++) {
			PathCommand cmd = _commands.get(i);
			switch (cmd) {
			case MoveTo:
				builder.append("moveTo: ");
				builder.append(_data.get(i + 0));
				builder.append(", ");
				builder.append(_data.get(i + 1));
				builder.append("\n");
				break;
			case LineTo:
				builder.append("lineTo: ");
				builder.append(_data.get(i + 0));
				builder.append(", ");
				builder.append(_data.get(i + 1));
				builder.append("\n");
				break;
			case CubicCurveTo:
				builder.append("cubiccurveTo: ");
				builder.append(_data.get(i + 0));
				builder.append(", ");
				builder.append(_data.get(i + 1));
				builder.append(",");
				builder.append(_data.get(i + 2));
				builder.append(", ");
				builder.append(_data.get(i + 3));
				builder.append(", ");
				builder.append(_data.get(i + 4));
				builder.append(", ");
				builder.append(_data.get(i + 5));
				builder.append("\n");
				break;
			case CurveTo:
				builder.append("cubicTo: ");
				builder.append(_data.get(i + 0));
				builder.append(", ");
				builder.append(_data.get(i + 1));
				builder.append(",");
				builder.append(_data.get(i + 2));
				builder.append(", ");
				builder.append(_data.get(i + 3));
				builder.append("\n");
				break;
			case Closed:
				builder.append("closed\n");
				break;
			default:
			}
		}
		return builder.toString();
	}

	@Override
	public Path close() {
		_commands.add(PathCommand.Closed);
		_lastX = _lastStartX;
		_lastY = _lastStartY;
		return this;
	}

}
