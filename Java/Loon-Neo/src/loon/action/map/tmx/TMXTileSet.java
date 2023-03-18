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
import loon.action.map.tmx.tiles.TMXTerrain;
import loon.action.map.tmx.tiles.TMXTile;
import loon.geom.Vector2f;
import loon.utils.TArray;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class TMXTileSet {

	private int firstGID;

	private String name;

	private int tileWidth;
	private int tileHeight;
	private int spacing;
	private int margin;

	private Vector2f tileOffset;
	private TMXImage image;

	private TArray<TMXTerrain> terrainTypes;
	private TArray<TMXTile> tiles;

	private TMXProperties properties;

	public TMXTileSet() {
		this.tileOffset = new Vector2f();
		this.terrainTypes = new TArray<TMXTerrain>();
		this.tiles = new TArray<TMXTile>();
		this.properties = new TMXProperties();
	}

	public void parse(XMLElement element, String tilesLocation) {

		this.firstGID = element.getIntAttribute("firstgid", 1);
		String source = element.getAttribute("source", LSystem.EMPTY);
		if (!LSystem.EMPTY.equals(source)) {
			try {
				XMLDocument doc = XMLParser.parse(tilesLocation + "/" + source);
				XMLElement docElement = doc.getRoot();
				element = docElement;
			} catch (Throwable e) {
				LSystem.error(tilesLocation + "/" + source);
			}
		}

		tileWidth = element.getIntAttribute("tilewidth", 0);
		tileHeight = element.getIntAttribute("tileheight", 0);
		margin = element.getIntAttribute("margin", 0);
		spacing = element.getIntAttribute("spacing", 0);

		name = element.getAttribute("name", LSystem.EMPTY);

		XMLElement nodes = element.getChildrenByName("tileoffset");

		if (nodes != null) {
			XMLElement childElement = nodes;
			tileOffset.x = childElement.getFloatAttribute("x", 0);
			tileOffset.y = childElement.getFloatAttribute("y", 0);
		}

		nodes = element.getChildrenByName("terraintypes");

		if (nodes != null) {
			TArray<XMLElement> list = nodes.list();
			for (XMLElement terrain : list) {
				TMXTerrain terrainType = new TMXTerrain();
				terrainType.parse(terrain);
				terrainTypes.add(terrainType);
			}
		}

		nodes = element.getChildrenByName("image");

		if (nodes != null) {
			image = new TMXImage();
			image.parse(nodes, tilesLocation);
		}

		int tileCount = (image.getWidth() / tileWidth) * (image.getHeight() / tileHeight);

		for (int tID = 0; tID < tileCount; tID++) {
			TMXTile tile = new TMXTile(tID + firstGID);
			tiles.add(tile);
		}

		nodes = element.getChildrenByName("tile");

		if (nodes != null) {

			TArray<XMLElement> list = nodes.list();
			for (int i = 0; i < list.size; i++) {
				XMLElement tileNode = list.get(i);
				TMXTile tile = new TMXTile(i);
				tile.parse(tileNode);
				tiles.get(tile.getID()).parse(tileNode);
			}
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public int getFirstGID() {
		return firstGID;
	}

	public String getName() {
		return name;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getSpacing() {
		return spacing;
	}

	public int getMargin() {
		return margin;
	}

	public Vector2f getTileOffset() {
		return tileOffset;
	}

	public TMXImage getImage() {
		return image;
	}

	public TArray<TMXTerrain> getTerrainTypes() {
		return terrainTypes;
	}

	public TMXTile getTile(int id) {
		for (TMXTile tile : tiles) {
			if (tile.getID() == id) {
				return tile;
			}
		}
		return null;
	}

	public TArray<TMXTile> getTiles() {
		return tiles;
	}

	public TMXProperties getProperties() {
		return properties;
	}
}
