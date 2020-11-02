package org.loon.framework.android.game.action.map.tmx;

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

	public TMXTile(Element element) throws RuntimeException {
		name = element.getAttribute("name");
		type = element.getAttribute("type");
		x = Integer.parseInt(element.getAttribute("x"));
		y = Integer.parseInt(element.getAttribute("y"));
		String w = element.getAttribute("width");
		String h = element.getAttribute("height");
		width = Integer.parseInt(w == null || "".equals(w) ? "0" : w);
		height = Integer.parseInt(h == null || "".equals(h) ? "0" : h);
		Element imageElement = (Element) element.getElementsByTagName("image")
				.item(0);
		if (imageElement != null) {
			image = imageElement.getAttribute("source");
		}

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
	}

}
