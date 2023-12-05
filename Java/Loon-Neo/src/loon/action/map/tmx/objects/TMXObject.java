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
	private String name;
	private String type;

	private int x;
	private int y;
	private int width;
	private int height;
	private int id;
	private int gid;

	private double rotation;
	private boolean visible;

	private TMXEllipse ellipse;
	private TMXPolygon polygon;
	private TMXPolyLine polyLine;

	private TMXProperties properties;

	public TMXObject() {
		properties = new TMXProperties();
	}

	public void parse(Json.Object element) {

		name = element.containsKey("name") ? element.getString("name", LSystem.EMPTY) : "TmxObject";
		type = element.containsKey("type") ? element.getString("name", LSystem.EMPTY) : "TmxObject";

		id = element.getInt("id", 0);
		x = element.getInt("x", 0);
		y = element.getInt("y", 0);
		width = element.getInt("width", 0);
		height = element.getInt("height", 0);
		gid = element.getInt("gid", -1);
		rotation = element.getInt("rotation", 0);
		visible = element.getBoolean("visible", false);

		if (element.containsKey("ellipse")) {
			ellipse = new TMXEllipse();
			ellipse.set(x, y, width, height);
		}

		Json.Object node = element.getObject("polygon", null);
		if (node != null) {
			polygon = new TMXPolygon();
			polygon.parse(node);
		}

		node = element.getObject("polyline", null);
		if (node != null) {
			polyLine = new TMXPolyLine();
			polyLine.parse(node);
		}

		Json.Array nodes = element.getArray("properties", null);
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public void parse(XMLElement element) {

		name = element.hasAttribute("name") ? element.getAttribute("name", LSystem.EMPTY) : "TmxObject";
		type = element.hasAttribute("type") ? element.getAttribute("name", LSystem.EMPTY) : "TmxObject";

		id = element.getIntAttribute("id", 0);
		x = element.getIntAttribute("x", 0);
		y = element.getIntAttribute("y", 0);
		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		gid = element.getIntAttribute("gid", -1);
		rotation = element.getIntAttribute("rotation", 0);
		visible = element.getBoolAttribute("visible", false);

		XMLElement nodes = element.getChildrenByName("ellipse");
		if (nodes != null) {
			ellipse = new TMXEllipse();
			ellipse.set(x, y, width, height);
		}

		nodes = element.getChildrenByName("polygon");
		if (nodes != null) {
			polygon = new TMXPolygon();
			polygon.parse(nodes);
		}

		nodes = element.getChildrenByName("polyline");
		if (nodes != null) {
			polyLine = new TMXPolyLine();
			polyLine.parse(nodes);
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public String getName() {
		return name;
	}

	public String getTypeCode() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return id;
	}

	public int getGID() {
		return gid;
	}

	public double getRotation() {
		return rotation;
	}

	public boolean isVisible() {
		return visible;
	}

	public TMXEllipse getEllipse() {
		return ellipse;
	}

	public TMXPolygon getPolygon() {
		return polygon;
	}

	public TMXPolyLine getPolyLine() {
		return polyLine;
	}

	public TMXProperties getProperties() {
		return properties;
	}
}
