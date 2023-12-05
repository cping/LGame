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
import loon.utils.xml.XMLElement;

public class TMXImageLayer extends TMXMapLayer {

	private TMXImage image;

	public TMXImageLayer(TMXMap map) {
		super(map, LSystem.EMPTY, 0, 0, map.getWidth(), map.getHeight(), 1.0f, true, TmxLayerType.IMAGE);
	}

	public TMXImageLayer parse(Json.Object element) {

		id = element.getInt("id", 0);
		name = element.getString("name", LSystem.EMPTY);

		offsetX = element.getNumber("x", 0);
		offsetY = element.getNumber("y", 0);

		offsetX = element.getNumber("offsetx", offsetX);
		offsetY = element.getNumber("offsety", offsetY);

		parallaxX = element.getNumber("parallaxx", 0f);
		parallaxY = element.getNumber("parallaxy", 0f);

		opacity = element.getNumber("opacity", 1f);
		visible = element.getBoolean("visible", true);

		if (element.containsKey("image")) {
			image = new TMXImage();
			image.parse(element, getMap().getFilePath());
		}

		Json.Array nodes = element.getArray("properties", null);
		if (nodes != null) {
			properties.parse(nodes);
		}
		return this;
	}

	public TMXImageLayer parse(XMLElement element) {

		id = element.getIntAttribute("id", 0);
		name = element.getAttribute("name", LSystem.EMPTY);

		offsetX = element.getFloatAttribute("x", 0);
		offsetY = element.getFloatAttribute("y", 0);

		offsetX = element.getFloatAttribute("offsetx", offsetX);
		offsetY = element.getFloatAttribute("offsety", offsetY);

		parallaxX = element.getFloatAttribute("parallaxx", 0f);
		parallaxY = element.getFloatAttribute("parallaxy", 0f);

		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("image");
		if (nodes != null) {
			image = new TMXImage();
			image.parse(nodes, getMap().getFilePath());
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
		return this;
	}

	public TMXImage getImage() {
		return image;
	}
}
