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

import java.util.Iterator;

import loon.utils.TArray;

public class Path extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TArray<PointF> localPoints;

	private float cx;

	private float cy;

	private boolean closed;

	private TArray<TArray<PointF>> holes;

	private TArray<PointF> hole;

	public Path() {
		this(0, 0);
	}

	public Path(float sx, float sy) {
		moveTo(sx, sy);
	}

	public void reset() {
		this.clear();
	}

	public void set(float sx, float sy) {
		localPoints.add(new PointF(sx, sy));
		cx = sx;
		cy = sy;
		pointsDirty = true;
	}

	public void addPath(Path p, float px, float py) {
		for (Iterator<PointF> it = p.localPoints.iterator(); it.hasNext();) {
			PointF pos = it.next();
			if (hole != null) {
				hole.add(new PointF(px + pos.x, py + pos.y));
			} else {
				localPoints.add(new PointF(px + pos.x, py + pos.y));
			}
		}
		pointsDirty = true;
	}

	public void moveTo(float sx, float sy) {
		if (holes == null) {
			holes = new TArray<TArray<PointF>>(10);
		}
		if (localPoints == null) {
			localPoints = new TArray<PointF>(10);
		}
		this.set(sx, sy);
	}

	public void quadTo(float x1, float y1, float x2, float y2) {
		if (hole != null) {
			hole.add(new PointF(x1, y1));
			hole.add(new PointF(x2, y2));
		} else {
			localPoints.add(new PointF(x1, y1));
			localPoints.add(new PointF(x2, y2));
		}
		cx = x2;
		cy = y2;
		pointsDirty = true;
	}

    public void push(PointF point) {
        if (localPoints == null) {
        	localPoints = new TArray<PointF>();
        }
        localPoints.add(point);
    	pointsDirty = true;
    }
    
	@Override
	public void clear() {
		if (hole != null) {
			hole.clear();
		}
		if (localPoints != null) {
			localPoints.clear();
		}
		pointsDirty = true;
	}

	public void lineTo(float x, float y) {
		if (hole != null) {
			hole.add(new PointF(x, y));
		} else {
			localPoints.add(new PointF(x, y));
		}
		cx = x;
		cy = y;
		pointsDirty = true;
	}

	public void close() {
		closed = true;
	}

	public void curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2) {
		curveTo(x, y, cx1, cy1, cx2, cy2, 10);
	}

	public void curveTo(float x, float y, float cx1, float cy1, float cx2, float cy2, int segments) {
		if ((cx == x) && (cy == y)) {
			return;
		}
		Curve curve = new Curve(new Vector2f(cx, cy), new Vector2f(cx1, cy1), new Vector2f(cx2, cy2),
				new Vector2f(x, y));
		float step = 1.0f / segments;

		for (int i = 1; i < segments + 1; i++) {
			float t = i * step;
			Vector2f p = curve.pointAt(t);
			if (hole != null) {
				hole.add(new PointF(p.x, p.y));
			} else {
				localPoints.add(new PointF(p.x, p.y));
			}
			cx = p.x;
			cy = p.y;
		}
		pointsDirty = true;
	}

	@Override
	protected void createPoints() {
		points = new float[localPoints.size * 2];
		for (int i = 0; i < localPoints.size; i++) {
			PointF p = localPoints.get(i);
			points[(i * 2)] = p.x;
			points[(i * 2) + 1] = p.y;
		}
	}

	@Override
	public Shape transform(Matrix3 transform) {
		Path p = new Path(cx, cy);
		p.localPoints = transform(localPoints, transform);
		for (int i = 0; i < holes.size; i++) {
			p.holes.add(transform(holes.get(i), transform));
		}
		p.closed = this.closed;
		return p;
	}

	private TArray<PointF> transform(TArray<PointF> pts, Matrix3 t) {
		float[] in = new float[pts.size * 2];
		float[] out = new float[pts.size * 2];
		for (int i = 0; i < pts.size; i++) {
			PointF point = (pts.get(i));
			in[i * 2] = point.x;
			in[(i * 2) + 1] = point.y;
		}
		t.transform(in, 0, out, 0, pts.size);
		TArray<PointF> outList = new TArray<PointF>();
		for (int i = 0; i < pts.size; i++) {
			outList.add(new PointF(out[(i * 2)], out[(i * 2) + 1]));
		}
		return outList;
	}

	@Override
	public boolean closed() {
		return closed;
	}
}
