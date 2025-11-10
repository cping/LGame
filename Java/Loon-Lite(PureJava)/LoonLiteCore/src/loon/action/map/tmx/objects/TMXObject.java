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
import loon.action.map.tmx.TMXProperties;
import loon.utils.xml.XMLElement;

public class TMXObject {
	private String _name;
	private String _type;

	private int _x;
	private int _y;
	private int _width;
	private int _height;
	private int _id;
	private int _gid;

	private float _rotation;

	private boolean _visible;
	private boolean _isEllipse;
	private boolean _isPolygon;
	private boolean _isPolyLine;
	private boolean _isImage;

	private TMXEllipse _ellipse;
	private TMXPolygon _polygon;
	private TMXPolyLine _polyLine;

	private TMXProperties _properties;

	public TMXObject() {
		_properties = new TMXProperties();
	}

	public void parse(Json.Object element) {

		_name = element.containsKey("name") ? element.getString("name", LSystem.EMPTY) : "TmxObject";
		_type = element.containsKey("type") ? element.getString("name", LSystem.EMPTY) : "TmxObject";

		_id = element.getInt("id", 0);
		_x = element.getInt("x", 0);
		_y = element.getInt("y", 0);
		_width = element.getInt("width", 0);
		_height = element.getInt("height", 0);
		_gid = element.getInt("gid", -1);
		_rotation = element.getNumber("rotation", 0);
		_visible = element.getBoolean("visible", false);

		if (_gid != -1) {
			_isImage = true;
		}

		if (element.containsKey("ellipse")) {
			_ellipse = new TMXEllipse();
			_ellipse.set(_x, _y, _width, _height);
			_isEllipse = true;
			_isImage = false;
		}

		Json.Object node = element.getObject("polygon", null);
		if (node != null) {
			_polygon = new TMXPolygon();
			_polygon.parse(node);
			_isPolygon = true;
			_isImage = false;
		}

		node = element.getObject("polyline", null);
		if (node != null) {
			_polyLine = new TMXPolyLine();
			_polyLine.parse(node);
			_isPolyLine = true;
			_isImage = false;
		}

		Json.Array nodes = element.getArray("properties", null);
		if (nodes != null) {
			_properties.parse(nodes);
		}
	}

	public void parse(XMLElement element) {

		_name = element.hasAttribute("name") ? element.getAttribute("name", LSystem.EMPTY) : "TmxObject";
		_type = element.hasAttribute("type") ? element.getAttribute("name", LSystem.EMPTY) : "TmxObject";

		_id = element.getIntAttribute("id", 0);
		_x = element.getIntAttribute("x", 0);
		_y = element.getIntAttribute("y", 0);
		_width = element.getIntAttribute("width", 0);
		_height = element.getIntAttribute("height", 0);
		_gid = element.getIntAttribute("gid", -1);
		_rotation = element.getFloatAttribute("rotation", 0);
		_visible = element.getBoolAttribute("visible", false);

		if (_gid != -1) {
			_isImage = true;
		}

		XMLElement nodes = element.getChildrenByName("ellipse");
		if (nodes != null) {
			_ellipse = new TMXEllipse();
			_ellipse.set(_x, _y, _width, _height);
			_isEllipse = true;
			_isImage = false;
		}

		nodes = element.getChildrenByName("polygon");
		if (nodes != null) {
			_polygon = new TMXPolygon();
			_polygon.parse(nodes);
			_isPolygon = true;
			_isImage = false;
		}

		nodes = element.getChildrenByName("polyline");
		if (nodes != null) {
			_polyLine = new TMXPolyLine();
			_polyLine.parse(nodes);
			_isPolyLine = true;
			_isImage = false;
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			_properties.parse(nodes);
		}
	}

	public String getName() {
		return _name;
	}

	public String getTypeCode() {
		return _type;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public int getID() {
		return _id;
	}

	public int getGID() {
		return _gid;
	}

	public float getRotation() {
		return _rotation;
	}

	public boolean isVisible() {
		return _visible;
	}

	public boolean isEllipse() {
		return _isEllipse;
	}

	public boolean isPolygon() {
		return _isPolygon;
	}

	public boolean isPolyLine() {
		return _isPolyLine;
	}

	public boolean isImage() {
		return _isImage;
	}

	public TMXEllipse getEllipse() {
		return _ellipse;
	}

	public TMXPolygon getPolygon() {
		return _polygon;
	}

	public TMXPolyLine getPolyLine() {
		return _polyLine;
	}

	public TMXProperties getProperties() {
		return _properties;
	}
}
