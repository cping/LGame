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
package loon.opengl.light;

import java.util.Comparator;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Circle;
import loon.geom.Polygon;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class LightShapeSystem implements LRelease {

	private class SortVertices implements Comparator<Vector2f> {

		private Vector2f _lightPos;

		public SortVertices(Vector2f light) {
			this.setLightVector(light);
		}

		public void setLightVector(Vector2f light) {
			this._lightPos = light;
		}

		@Override
		public int compare(Vector2f o1, Vector2f o2) {
			final Vector2f d1 = o1.sub(_lightPos);
			final Vector2f d2 = o2.sub(_lightPos);
			final float t1 = d1.getAngle();
			final float t2 = d2.getAngle();
			return (int) MathUtils.signum(t1 - t2);

		}

	}

	protected final SortVertices _sortVertices;

	private final LColor _lightPolygonColor = LColor.lightGray.cpy();

	private final LColor _lightShowShapeColor = LColor.blue.cpy();

	private final LColor _lightBlackColor = LColor.black.cpy();

	private final LColor _lightCenterCircleColor = LColor.yellow.cpy();

	private final TArray<Vector2f> _lightVertices;

	private final IntArray _lightSwappables;

	private final Vector2f _lightVertex = new Vector2f();

	private final Vector2f _lightTemp = new Vector2f();

	private final TArray<Vector2f> _lightPoints;

	private final ObjectMap<Vector2f, LightShape> _lightVertexMap;

	private boolean _updateLight;

	private float _lastPosX = -1f, _lastPosY = -1f;

	private final TArray<LightRect> _lightRects;

	private final TArray<LightCircle> _lightCircles;

	private final TArray<LightPolygon> _lightPolygons;

	private final TArray<LightShape> _lightAllShapes;

	private final TArray<LightShape> _lightVisibleShapes;

	private final LightRect _lightSizes;

	private final Polygon _lightPolygon;

	private final Circle _lightCircle;

	private final Circle _lightRadius;

	private boolean _dirty;

	public LightShapeSystem() {
		this(20f, 0f, 0f, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public LightShapeSystem(float lightSize, float x, float y, float w, float h) {
		_lightSizes = new LightRect(x, y, w, h);
		_lightVertexMap = new ObjectMap<Vector2f, LightShape>();
		_lightSwappables = new IntArray();
		_lightPolygons = new TArray<LightPolygon>();
		_lightCircles = new TArray<LightCircle>();
		_lightRects = new TArray<LightRect>();
		_lightPoints = new TArray<Vector2f>();
		_lightVertices = new TArray<Vector2f>();
		_lightPolygon = new Polygon();
		_lightVisibleShapes = new TArray<LightShape>();
		_lightAllShapes = new TArray<LightShape>();
		_sortVertices = new SortVertices(_lightTemp);
		_lightCircle = new Circle(w / 2f, h / 2f, lightSize);
		_lightRadius = new Circle(_lightCircle.getCenterX(), _lightCircle.getCenterY(),
				MathUtils.max(_lightSizes.getMaxX(), _lightSizes.getMaxY()) / 2);
	}

	public LightShapeSystem setLightCircleCenter(float x, float y) {
		_lightCircle.setCenterX(x);
		_lightCircle.setCenterY(y);
		return this;
	}

	public LightShapeSystem setLightCircleSizeRadius(float r) {
		_lightCircle.setRadius(r);
		return this;
	}

	public LightShapeSystem setLightRadius(float r) {
		_lightRadius.setRadius(r);
		return this;
	}

	public TArray<LightShape> getLightShowShapes() {
		return _lightVisibleShapes;
	}

	public LightShapeSystem addLightRect(float x, float y, float w, float h) {
		return addLightRect(new LightRect(x, y, w, h));
	}

	public LightShapeSystem addLightRect(LightRect rect) {
		_lightRects.add(rect);
		_dirty = true;
		return this;
	}

	public LightShapeSystem addLightCircle(float x, float y, float r) {
		return addLightCircle(new LightCircle(x, y, r));
	}

	public LightShapeSystem addLightCircle(LightCircle rect) {
		_lightCircles.add(rect);
		_dirty = true;
		return this;
	}

	public LightShapeSystem addLightPoly(float[] shapes) {
		return addLightPoly(new LightPolygon(shapes));
	}

	public LightShapeSystem addLightPoly(LightPolygon poly) {
		_lightPolygons.add(poly);
		_dirty = true;
		return this;
	}

	public LightShapeSystem clear() {
		_lightAllShapes.clear();
		_dirty = true;
		return this;
	}

	protected void checkDirty() {
		if (_dirty) {
			_lightAllShapes.clear();
			_lightAllShapes.addAll(_lightRects);
			_lightAllShapes.addAll(_lightCircles);
			_lightAllShapes.addAll(_lightPolygons);
			_lightAllShapes.add(_lightSizes);
			_dirty = false;
		}
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public boolean isUpdateLight() {
		return this._updateLight;
	}

	public void updateLight(float tx, float ty) {
		if (_lastPosX != tx || _lastPosY != ty) {
			checkDirty();
			setLightCircleCenter(tx, ty);
			_lightTemp.set(_lightCircle.getCenterX() + 0.5f, _lightCircle.getCenterY() + 0.5f);
			_lightVisibleShapes.clear();
			_lightSwappables.clear();
			_lightVertices.clear();
			for (int i = 0; i < _lightAllShapes.size; i++) {
				LightShape shape = _lightAllShapes.get(i);
				if (shape != null) {
					Vector2f[] list = shape.getVertices(_lightTemp);
					for (int j = 0; j < list.length; j++) {
						Vector2f vertex = list[j];
						_lightVertices.add(vertex);
						_lightVertexMap.put(vertex, shape);
					}
				}
			}
			_lightVertices.sort(_sortVertices);
			_lightPoints.clear();
			_lightVertex.setZero();
			for (int n = 0; n < _lightVertices.size; n++) {
				Vector2f vert = _lightVertices.get(n);
				Vector2f blocking = null;
				LightShape blockingShape = null;
				LightShape testShape = _lightVertexMap.get(vert);
				_lightVertex.set(vert);
				_lightVertex.subtractSelf(_lightTemp);
				for (int i = 0; i < _lightAllShapes.size; i++) {
					LightShape shape = _lightAllShapes.get(i);
					if (shape != null) {
						Vector2f intersect = shape.getIntersection(_lightTemp, _lightVertex, vert);
						if (intersect == null) {
							continue;
						}
						if (blocking == null || intersect.distance(_lightTemp) < blocking.distance(_lightTemp)) {
							blocking = intersect;
							blockingShape = shape;
						}
					}
				}
				if (blocking == null) {
					_lightPoints.add(vert);
				} else if (blocking.distance(_lightTemp) > vert.distance(_lightTemp)) {
					_lightPoints.add(vert);
					if (blockingShape != testShape && !testShape.contains(_lightTemp)) {
						_lightPoints.add(blocking);
						_lightVertexMap.put(blocking, blockingShape);
						_lightSwappables.add(_lightPoints.size() - 2);
					}
				}
			}
			for (int i = 0; i < _lightPoints.size; i++) {
				_lightVisibleShapes.add(_lightVertexMap.get(_lightPoints.get(i)));
			}
			for (int j = 0; j < _lightSwappables.size(); j++) {
				int n = _lightSwappables.get(j);
				int s1 = n + 1;
				int before = (n - 1 + _lightPoints.size()) % _lightPoints.size();
				int after = (s1 + 1) % _lightPoints.size();
				LightShape beforeShape = _lightVertexMap.get(_lightPoints.get(before)),
						afterShape = _lightVertexMap.get(_lightPoints.get(after)),
						shape1 = _lightVertexMap.get(_lightPoints.get(n)),
						shape2 = _lightVertexMap.get(_lightPoints.get(s1));
				if (shape1 == afterShape || shape2 == beforeShape) {
					_lightPoints.swap(n, s1);
				}
			}
			_lightPolygon.setPolygon(_lightPoints, _dirty);
			_lastPosX = tx;
			_lastPosY = ty;
			_updateLight = true;
		} else {
			_updateLight = false;
		}
	}

	public LColor getLightPolyColor() {
		return _lightPolygonColor;
	}

	public LightShapeSystem setLightPolyColor(LColor c) {
		this._lightPolygonColor.setColor(c);
		return this;
	}

	public LColor getLightBlackColor() {
		return _lightBlackColor;
	}

	public LightShapeSystem setLightBlackColor(LColor c) {
		this._lightBlackColor.setColor(c);
		return this;
	}

	public LColor getLightCenterCircleColor() {
		return _lightCenterCircleColor;
	}

	public LightShapeSystem setLightCenterCircleColor(LColor c) {
		this._lightCenterCircleColor.setColor(c);
		return this;
	}

	public LColor getLightShowShapeColor() {
		return _lightShowShapeColor;
	}

	public LightShapeSystem setLightShowShapeColor(LColor c) {
		this._lightShowShapeColor.setColor(c);
		return this;
	}

	public void drawDebug(GLEx g) {
		int oldColor = g.color();
		g.setColor(_lightPolygonColor);
		g.fill(_lightPolygon);
		for (int i = _lightAllShapes.size - 1; i > -1; i--) {
			LightShape shape = _lightAllShapes.get(i);
			if (shape == _lightSizes) {
				continue;
			}
			if (_lightVisibleShapes.contains(shape)) {
				g.setColor(_lightShowShapeColor);
			} else {
				g.setColor(_lightBlackColor);
			}
			g.fill((Shape) shape);
		}
		_lightRadius.setCenter(_lightCircle.getCenterPos());
		g.setColor(_lightPolygonColor);
		g.draw(_lightSizes);
		g.setColor(_lightCenterCircleColor);
		g.fill(_lightCircle);
		g.setColor(oldColor);
	}

	@Override
	public void close() {
		this.clear();
		this._lastPosX = this._lastPosY = -1f;
	}

}
