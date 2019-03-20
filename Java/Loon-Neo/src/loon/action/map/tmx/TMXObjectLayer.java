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

import loon.action.map.tmx.objects.TMXObject;
import loon.canvas.LColor;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXObjectLayer extends TMXMapLayer {

	private LColor color;

	private TArray<TMXObject> objects;

	public TMXObjectLayer(TMXMap map) {
		super(map, "", 0, 0, map.getWidth(), map.getHeight(), 1.0f, true,
				TmxLayerType.OBJECT);

		objects = new TArray<>();
		color = new LColor(LColor.TRANSPARENT);
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

	public void parse(XMLElement element) {
		name = element.getAttribute("name", "");
		if (element.hasAttribute("color")) {
			String colorString = element.getAttribute("color",
					LColor.white.toString()).trim();
			if (colorString.startsWith("#")) {
				colorString = colorString.substring(1);
			}
			color = new LColor(Integer.decode(colorString));
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

				TMXObject object = new TMXObject();
				object.parse(objectNode);

				objects.add(object);
			}
		}
	}
}
