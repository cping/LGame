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

import loon.LSystem;
import loon.utils.xml.XMLElement;

public class TMXImageLayer extends TMXMapLayer {

	private TMXImage image;

	public TMXImageLayer(TMXMap map) {
		super(map, LSystem.EMPTY, 0, 0, map.getWidth(), map.getHeight(), 1.0f, true, TmxLayerType.IMAGE);
	}

	public TMXImageLayer parse(XMLElement element) {

		name = element.getAttribute("name", LSystem.EMPTY);

		x = element.getIntAttribute("x", 0);
		y = element.getIntAttribute("y", 0);

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
