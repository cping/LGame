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
package loon.action.map.tmx;

import loon.Json;
import loon.LSystem;
import loon.action.map.tmx.objects.TMXObject;
import loon.canvas.LColor;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXObjectLayer extends TMXMapLayer {

	private LColor color;

	private TArray<TMXObject> objects;

	public TMXObjectLayer(TMXMap map) {
		super(map, LSystem.EMPTY, 0, 0, map.getWidth(), map.getHeight(), 1.0f, true, TmxLayerType.OBJECT);
		this.objects = new TArray<TMXObject>();
		this.color = new LColor(LColor.TRANSPARENT);
	}

	public TMXObject getObject(int index) {
		return objects.get(index);
	}

	public int getNumObjects() {
		return objects.size;
	}

	public LColor getColor() {
		return color;
	}

	public TArray<TMXObject> getObjects() {
		return objects;
	}

	public void parse(Json.Object element) {

		id = element.getInt("id", 0);
		name = element.getString("name", LSystem.EMPTY);

		if (element.containsKey("color")) {
			color = new LColor(element.getString("color", LColor.white.toString()).trim());
		}

		opacity = element.getNumber("opacity", 1f);
		visible = element.getBoolean("visible", true);

		Json.Array nodes = element.getArray("properties",null);
		if (nodes != null) {
			properties.parse(nodes);
		}

		nodes = element.getArray("objects");
		if (nodes != null) {
			for (int i = 0; i < nodes.length(); i++) {
				Json.Object objectNode = nodes.getObject(i);

				TMXObject o = new TMXObject();
				o.parse(objectNode);

				objects.add(o);
			}
		}
	}
	
	public void parse(XMLElement element) {

		id = element.getIntAttribute("id", 0);
		name = element.getAttribute("name", LSystem.EMPTY);

		if (element.hasAttribute("color")) {
			color = new LColor(element.getAttribute("color", LColor.white.toString()).trim());
		}

		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}

		nodes = element.getChildrenByName("object");
		if (nodes != null) {
			TArray<XMLElement> list = nodes.list();
			for (int i = 0; i < list.size; i++) {
				XMLElement objectNode = list.get(i);

				TMXObject o = new TMXObject();
				o.parse(objectNode);

				objects.add(o);
			}
		}
	}
}
