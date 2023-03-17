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
package loon.action.map.tmx.tiles;

import loon.LSystem;
import loon.action.map.tmx.TMXProperties;
import loon.utils.xml.XMLElement;

public class TMXTerrain {

	private String name;

	private int tileID;

	private TMXProperties properties;

	public TMXTerrain() {
		properties = new TMXProperties();
	}

	public void parse(XMLElement element) {
		name = element.getAttribute("name", LSystem.EMPTY);
		tileID = element.getIntAttribute("tile", 0);
		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public String getName() {
		return name;
	}

	public int getTileID() {
		return tileID;
	}

	public TMXProperties getProperties() {
		return properties;
	}
}
