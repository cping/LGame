package org.loon.framework.android.game.action.map.tmx;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public class TMXTileGroup {

	public int index;

	public String name;

	public ArrayList<TMXTile> objects;

	public int width;

	public int height;

	public TMXProperty props;

	public TMXTileGroup(Element element) throws RuntimeException {
		name = element.getAttribute("name");
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		objects = new ArrayList<TMXTile>();

		Element propsElement = (Element) element.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement.getElementsByTagName("property");
			if (properties != null) {
				props = new TMXProperty();
				for (int p = 0; p < properties.getLength(); p++) {
					Element propElement = (Element) properties.item(p);

					String name = propElement.getAttribute("name");
					String value = propElement.getAttribute("value");
					props.setProperty(name, value);
				}
			}
		}

		NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objElement = (Element) objectNodes.item(i);
			TMXTile object = new TMXTile(objElement);
			object.index = i;
			objects.add(object);
		}
	}
}
