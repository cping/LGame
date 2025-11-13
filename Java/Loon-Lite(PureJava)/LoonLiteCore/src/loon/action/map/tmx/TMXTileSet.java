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
import loon.action.map.tmx.tiles.TMXTerrain;
import loon.action.map.tmx.tiles.TMXTile;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.res.TextResource;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class TMXTileSet {

	private int _firstGID;
	private int _lastGID;

	private int _hTileCount;
	private int _vTileCount;

	private String _name;

	private int _tileWidth;
	private int _tileHeight;
	private int _spacing;
	private int _margin;

	private Vector2f _tileOffset;
	private TMXImage _image;

	private TArray<TMXTerrain> _terrainTypes;
	private TArray<TMXTile> _tiles;

	private TMXProperties _properties;

	public TMXTileSet() {
		this._tileOffset = new Vector2f();
		this._terrainTypes = new TArray<TMXTerrain>();
		this._tiles = new TArray<TMXTile>();
		this._properties = new TMXProperties();
	}

	public void parse(Json.Object element, String tilesLocation) {

		this._firstGID = element.getInt("firstgid", 1);
		final String source = element.getString("source", LSystem.EMPTY);
		final String path = StringUtils.isEmpty(tilesLocation) ? source
				: PathUtils.normalizeCombinePaths(tilesLocation, source);

		if (!LSystem.EMPTY.equals(path)) {
			try {
				element = (Json.Object) TextResource.get().loadJsonObject(path);
			} catch (Throwable e) {
				LSystem.error(path);
			}
		}

		_tileWidth = element.getInt("tilewidth", 0);
		_tileHeight = element.getInt("tileheight", 0);
		_margin = element.getInt("margin", 0);
		_spacing = element.getInt("spacing", 0);

		_name = element.getString("name", LSystem.EMPTY);

		Json.Object node = element.getObject("tileoffset", null);

		if (node != null) {
			_tileOffset.x = node.getNumber("x", 0);
			_tileOffset.y = node.getNumber("y", 0);
		}

		Json.Array nodes = element.getArray("terraintypes", null);

		if (nodes != null) {
			for (int i = 0; i < nodes.length(); i++) {
				TMXTerrain terrainType = new TMXTerrain();
				terrainType.parse(nodes.getObject(i));
				_terrainTypes.add(terrainType);
			}
		}

		if (element.containsKey("image")) {
			_image = new TMXImage();
			_image.parse(element, tilesLocation);
		}

		int tileCount = 0;
		if (_image != null) {
			tileCount = (_image.getWidth() / _tileWidth) * (_image.getHeight() / _tileHeight);
			this._hTileCount = MathUtils.abs(this._image.getWidth() / (this._tileWidth + this._spacing));
			this._vTileCount = MathUtils.abs(this._image.getHeight() / (this._tileHeight + this._margin));
			this._lastGID = MathUtils.max(this._firstGID + (((this._hTileCount * this._vTileCount) - 1)), 0);
		}

		for (int tID = 0; tID < tileCount; tID++) {
			TMXTile tile = new TMXTile(tID + _firstGID);
			_tiles.add(tile);
		}

		nodes = element.getArray("tiles", null);
		if (nodes != null) {
			for (int i = 0; i < nodes.length(); i++) {
				Json.Object tileNode = nodes.getObject(i);
				TMXTile tile = new TMXTile(i);
				tile.parse(tileNode);
				_tiles.get(tile.getID()).parse(tileNode);
			}
		}

		nodes = element.getArray("properties", null);
		if (nodes != null) {
			_properties.parse(nodes);
		}
	}

	public void parse(XMLElement element, String tilesLocation) {

		this._firstGID = element.getIntAttribute("firstgid", 1);
		final String source = element.getAttribute("source", LSystem.EMPTY);
		final String path = StringUtils.isEmpty(tilesLocation) ? source
				: PathUtils.normalizeCombinePaths(tilesLocation, source);

		if (!LSystem.EMPTY.equals(path)) {
			try {
				XMLDocument doc = XMLParser.parse(path);
				XMLElement docElement = doc.getRoot();
				element = docElement;
			} catch (Throwable e) {
				LSystem.error(path);
			}
		}

		_tileWidth = element.getIntAttribute("tilewidth", 0);
		_tileHeight = element.getIntAttribute("tileheight", 0);
		_margin = element.getIntAttribute("margin", 0);
		_spacing = element.getIntAttribute("spacing", 0);

		_name = element.getAttribute("name", LSystem.EMPTY);

		XMLElement nodes = element.getChildrenByName("tileoffset");

		if (nodes != null) {
			XMLElement childElement = nodes;
			_tileOffset.x = childElement.getFloatAttribute("x", 0);
			_tileOffset.y = childElement.getFloatAttribute("y", 0);
		}

		nodes = element.getChildrenByName("terraintypes");

		if (nodes != null) {
			TArray<XMLElement> list = nodes.list();
			for (XMLElement terrain : list) {
				TMXTerrain terrainType = new TMXTerrain();
				terrainType.parse(terrain);
				_terrainTypes.add(terrainType);
			}
		}

		nodes = element.getChildrenByName("image");

		if (nodes != null) {
			_image = new TMXImage();
			_image.parse(nodes, tilesLocation);
		}
		int tileCount = 0;
		if (_image != null) {
			tileCount = (_image.getWidth() / _tileWidth) * (_image.getHeight() / _tileHeight);
			this._hTileCount = MathUtils.abs(this._image.getWidth() / (this._tileWidth + this._spacing));
			this._vTileCount = MathUtils.abs(this._image.getHeight() / (this._tileHeight + this._margin));
			this._lastGID = MathUtils.max(this._firstGID + (((this._hTileCount * this._vTileCount) - 1)), 0);
		}
		for (int tID = 0; tID < tileCount; tID++) {
			TMXTile tile = new TMXTile(tID + _firstGID);
			_tiles.add(tile);
		}

		nodes = element.getChildrenByName("tile");

		if (nodes != null) {

			TArray<XMLElement> list = nodes.list();
			for (int i = 0; i < list.size; i++) {
				XMLElement tileNode = list.get(i);
				TMXTile tile = new TMXTile(i);
				tile.parse(tileNode);
				_tiles.get(tile.getID()).parse(tileNode);
			}
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			_properties.parse(nodes);
		}
	}

	public boolean contains(int gid) {
		return gid >= this._firstGID && gid <= this._lastGID;
	}

	public int getFirstGID() {
		return _firstGID;
	}

	public int getLastGID() {
		return _lastGID;
	}

	public String getName() {
		return _name;
	}

	public int getHorizontalTileCount() {
		return this._hTileCount;
	}

	public int getVerticalTileCount() {
		return this._vTileCount;
	}

	public int getTileWidth() {
		return _tileWidth;
	}

	public int getTileHeight() {
		return _tileHeight;
	}

	public int getSpacing() {
		return _spacing;
	}

	public int getMargin() {
		return _margin;
	}

	public Vector2f getTileOffset() {
		return _tileOffset;
	}

	public TMXImage getImage() {
		return _image;
	}

	public String getSource() {
		if (_image != null) {
			return _image.getSource();
		}
		return null;
	}

	public TArray<TMXTerrain> getTerrainTypes() {
		return _terrainTypes;
	}

	public TMXTile getTile(int id) {
		for (TMXTile tile : _tiles) {
			if (tile.getID() == id) {
				return tile;
			}
		}
		return null;
	}

	public TArray<TMXTile> getTiles() {
		return _tiles;
	}

	public TMXProperties getProperties() {
		return _properties;
	}
}
