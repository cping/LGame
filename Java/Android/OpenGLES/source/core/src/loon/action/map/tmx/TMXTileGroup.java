package loon.action.map.tmx;

import java.util.ArrayList;

import loon.utils.xml.XMLElement;


/**
 * 
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.0
 */
public class TMXTileGroup {

	public int index;

	public String name;

	public ArrayList<TMXTile> objects;

	public int width;

	public int height;

	public TMXProperty props;

	public TMXTileGroup(XMLElement element) throws RuntimeException {
		name = element.getAttribute("name", null);
		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		objects = new ArrayList<TMXTile>();

		XMLElement propsElement = element.getChildrenByName("properties");
		if (propsElement != null) {
			ArrayList<XMLElement> properties = propsElement.list("property");
			if (properties != null) {
				props = new TMXProperty();
				for (int p = 0; p < properties.size(); p++) {
					XMLElement propElement = properties.get(p);
					String name = propElement.getAttribute("name", null);
					String value = propElement.getAttribute("value", null);
					props.setProperty(name, value);
				}
			}
		}
		ArrayList<XMLElement> objectNodes = element.list("object");
		for (int i = 0; i < objectNodes.size(); i++) {
			XMLElement objElement = objectNodes.get(i);
			TMXTile object = new TMXTile(objElement);
			object.index = i;
			objects.add(object);
		}
	}

}
