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
package loon.action.map.tmx.objects;

import loon.Json;
import loon.LSystem;
import loon.geom.Polygon;
import loon.utils.FloatArray;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXPolygon {

	private TArray<TMXPoint> _points;

	private FloatArray _arrays;

	private Polygon _polygon;

	public TMXPolygon() {
		_points = new TArray<TMXPoint>();
		_arrays = new FloatArray();
	}

	public Polygon getPolygon() {
		if (_polygon == null) {
			_polygon = new Polygon(_arrays.toArray());
		} else {
			_polygon.setPolygon(_arrays.toArray(), _arrays.length);
		}
		return _polygon;
	}

	public FloatArray getFloatPoints() {
		return _arrays;
	}

	public TMXPoint getPoint(int index) {
		return _points.get(index);
	}

	public TArray<TMXPoint> getPoints() {
		return _points;
	}

	public int getNumPoints() {
		return _points.size;
	}

	public void parse(Json.Object element) {

		final String pointsLine = element.getString("points", LSystem.EMPTY).trim();
		final String[] list = StringUtils.split(pointsLine, LSystem.SPACE);

		for (int i = 0; i < list.length; i++) {
			String[] subTokens = StringUtils.split(list[i], LSystem.COMMA);

			TMXPoint point = new TMXPoint();
			point.x = Integer.parseInt(subTokens[0].trim());
			point.y = Integer.parseInt(subTokens[1].trim());
			_arrays.add(point.x, point.y);

			_points.add(point);
		}
	}

	public void parse(XMLElement element) {

		final String pointsLine = element.getAttribute("points", LSystem.EMPTY).trim();
		final String[] list = StringUtils.split(pointsLine, LSystem.SPACE);

		for (int i = 0; i < list.length; i++) {
			String[] subTokens = StringUtils.split(list[i], LSystem.COMMA);

			TMXPoint point = new TMXPoint();
			point.x = Integer.parseInt(subTokens[0].trim());
			point.y = Integer.parseInt(subTokens[1].trim());
			_arrays.add(point.x, point.y);

			_points.add(point);
		}
	}

	@Override
	public String toString() {
		return StringUtils.format("TMXPolygon [" + _points.toString() + "]");
	}
}
