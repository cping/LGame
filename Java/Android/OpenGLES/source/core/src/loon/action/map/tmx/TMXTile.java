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
public class TMXTile {

	public int index;

	public String name;

	public String type;

	public int x;

	public int y;

	public int width;

	public int height;

	String image;

	public TMXProperty props;

	public TMXTile(XMLElement element) throws RuntimeException {
		name = element.getAttribute("name", "");
		type = element.getAttribute("type", "");
		x = element.getIntAttribute("x", 0);
		y = element.getIntAttribute("y", 0);
		String w = element.getAttribute("width", null);
		String h = element.getAttribute("height", null);
		width = Integer.parseInt(w == null || "".equals(w) ? "0" : w);
		height = Integer.parseInt(h == null || "".equals(h) ? "0" : h);
		XMLElement imageElement = element
				.getChildrenByName("image");
		if (imageElement != null) {
			image = imageElement.getAttribute("source", null);
		}
		XMLElement propsElement = element
				.getChildrenByName("properties");
		if (propsElement != null) {
			props = new TMXProperty();
			ArrayList<XMLElement> property = propsElement.list("property");
			for (int i = 0; i < property.size(); i++) {
				XMLElement propElement = property.get(i);
				String name = propElement.getAttribute("name", null);
				String value = propElement.getAttribute("value", null);
				props.setProperty(name, value);
			}

		}
	}

}
