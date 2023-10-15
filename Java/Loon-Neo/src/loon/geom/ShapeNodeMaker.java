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

import loon.utils.TArray;

public class ShapeNodeMaker<T extends Shape> implements IV<T> {

	public final static <T extends Shape> T create(ShapeNodeType nodeType, float[] points) {
		return new ShapeNodeMaker<T>(nodeType, 0f, 0f, 0f, 0f, points, null).get();
	}

	public final static <T extends Shape> T create(ShapeNodeType nodeType, TArray<Vector2f> polys) {
		return new ShapeNodeMaker<T>(nodeType, 0f, 0f, 0f, 0f, null, polys).get();
	}

	public final static <T extends Shape> T create(ShapeNodeType nodeType, float x, float y, float w, float h) {
		return new ShapeNodeMaker<T>(nodeType, x, y, w, h, null, null).get();
	}

	public final static <T extends Shape> T create(ShapeNodeType nodeType, float x, float y) {
		return new ShapeNodeMaker<T>(nodeType, x, y, 0f, 0f, null, null).get();
	}

	private ShapeNodeType _nodeType;

	private T _value;

	@SuppressWarnings("unchecked")
	public ShapeNodeMaker(ShapeNodeType nodeType, float x, float y, float w, float h, float[] points,
			TArray<Vector2f> polys) {
		switch (nodeType) {
		case Point:
			_value = (T) new Point(x, y);
			break;
		case Rectangle:
			_value = (T) new RectBox(x, y, w, h);
			break;
		case Line:
			_value = (T) new Line(x, y, w, h);
			break;
		case Circle:
			_value = (T) Circle.rect(x, y, w, h);
			break;
		case Ellipse:
			_value = (T) Ellipse.rect(x, y, w, h);
			break;
		case Polygon:
			if (points != null) {
				_value = (T) new Polygon(points);
			} else if (polys != null) {
				_value = (T) new Polygon(polys);
			}
			break;
		case Triangle:
			_value = (T) Triangle2f.at(x, y, w, h);
			break;
		}
	}

	public ShapeNodeType getNodeType() {
		return _nodeType;
	}

	@Override
	public T get() {
		return _value;
	}

}
