/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.canvas.Path2D;
import loon.utils.FloatArray;

public class Path extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Path2D _path2d;

	public Path() {
		this(-1f, -1f);
	}

	public Path(float sx, float sy) {
		if (_path2d == null) {
			_path2d = new Path2D();
		}
		if (sx != -1f && sy != -1f) {
			_path2d.moveTo(sx, sy);
		}
		pointsDirty = true;
	}

	public Path reset() {
		_path2d.reset();
		pointsDirty = true;
		return this;
	}

	public Path set(float sx, float sy) {
		_path2d.moveTo(sx, sy);
		pointsDirty = true;
		return this;
	}

	public Path addPath(Path p, float px, float py) {
		_path2d.addPath(p._path2d, px, py);
		pointsDirty = true;
		return this;
	}

	public Path moveTo(float sx, float sy) {
		this.set(sx, sy);
		return this;
	}

	public Path quadTo(float x1, float y1, float x2, float y2) {
		_path2d.quadraticCurveTo(x2, y2, x1, y1);
		pointsDirty = true;
		return this;
	}

	public Path push(PointF point) {
		_path2d.lineTo(point.x, point.y);
		pointsDirty = true;
		return this;
	}

	public float getLastX() {
		return _path2d.getLastX();
	}

	public float getLastY() {
		return _path2d.getLastY();
	}

	@Override
	public void clear() {
		_path2d.reset();
		pointsDirty = true;
	}

	public Path setDirty() {
		return setDirty(true);
	}

	public Path setDirty(boolean d) {
		this.pointsDirty = d;
		return this;
	}

	public Path2D getPath2D() {
		return _path2d;
	}

	public Path lineTo(float x, float y) {
		_path2d.lineTo(x, y);
		pointsDirty = true;
		return this;
	}

	public Path lineTo(float x1, float y1, float x2, float y2) {
		_path2d.line(x1, y1, x2, y2);
		pointsDirty = true;
		return this;
	}

	public Path curveTo(float x, float y, float cx1, float cy2) {
		_path2d.curveTo(cx1, cy2, x, y);
		return this;
	}

	public Path curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2) {
		_path2d.cubicCurveTo(cx1, cy1, cx2, cy2, x, y);
		return this;
	}

	public Path curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2, int segments) {
		if ((_path2d.getLastX() == x) && (_path2d.getLastY() == y)) {
			return this;
		}
		Curve curve = new Curve(new Vector2f(_path2d.getLastX(), _path2d.getLastY()), new Vector2f(cx1, cy1),
				new Vector2f(cx2, cy2), new Vector2f(x, y));
		float step = 1.0f / segments;

		for (int i = 1; i < segments + 1; i++) {
			float t = i * step;
			Vector2f p = curve.pointAt(t);
			_path2d.lineTo(p.x, p.y);
		}
		pointsDirty = true;
		return this;
	}

	@Override
	protected void createPoints() {
		this.points = _path2d.getResult();
	}

	@Override
	public Shape transform(Matrix3 transform) {
		Path newPath = new Path();
		newPath._path2d.update(transform(_path2d, transform));
		return newPath;
	}

	private FloatArray transform(Path2D path, Matrix3 t) {
		float[] in = path.getResult();
		float[] out = new float[path.size()];
		t.transform(in, 0, out, 0, path.size());
		FloatArray outList = new FloatArray();
		for (int i = 0; i < path.size(); i++) {
			outList.add(out[(i * 2)]);
			outList.add(out[(i * 2) + 1]);
		}
		return outList;
	}

	@Override
	public int hashCode() {
		return _path2d.hashCode();
	}

	@Override
	public String toString() {
		return _path2d.toString();
	}

	public Path close() {
		_path2d.close();
		return this;
	}

	@Override
	public boolean isEmpty() {
		return _path2d.isEmpty();
	}

	@Override
	public boolean closed() {
		return _path2d.isClosed();
	}
}
