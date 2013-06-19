package loon.action.map.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LTouch;
import loon.core.resource.Resources;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;


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
public class TMXTiledMap implements LRelease {

	protected int width;

	protected int height;

	protected int tileWidth;

	protected int tileHeight;

	private RectBox screenRect;

	protected String tilesLocation;

	protected TMXProperty props;

	protected ArrayList<TMXTileSet> tileSets = new ArrayList<TMXTileSet>();

	protected ArrayList<TMXLayer> layers = new ArrayList<TMXLayer>();

	protected ArrayList<TMXTileGroup> objectGroups = new ArrayList<TMXTileGroup>();

	private boolean loadTileSets = true;

	private int defWidth, defHeight;

	public TMXTiledMap(String fileName) throws RuntimeException {
		this(fileName, true);
	}

	public TMXTiledMap(String fileName, boolean loadTileSets)
			throws RuntimeException {
		this.loadTileSets = loadTileSets;
		fileName = fileName.replace('\\', '/');
		String res = null;
		if (fileName.indexOf("/") != -1) {
			res = fileName.substring(0, fileName.lastIndexOf("/"));
		} else {
			res = fileName;
		}
		try {
			this.load(Resources.openResource(fileName), res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TMXTiledMap(String fileName, String tileSetsLocation)
			throws RuntimeException {
		try {
			load(Resources.openResource(fileName), tileSetsLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TMXTiledMap(InputStream in) throws RuntimeException {
		load(in, "");
	}

	public TMXTiledMap(InputStream in, String tileSetsLocation)
			throws RuntimeException {
		load(in, tileSetsLocation);
	}

	public String getTilesLocation() {
		return tilesLocation;
	}

	public int getLayerIndex(String name) {
		for (int i = 0; i < layers.size(); i++) {
			TMXLayer layer = layers.get(i);
			if (layer.name.equals(name)) {
				return i;
			}
		}
		return -1;
	}

	public LTexture getTileImage(int x, int y, int layerIndex) {
		TMXLayer layer = layers.get(layerIndex);

		int tileSetIndex = layer.data[x][y][0];
		if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.size())) {
			TMXTileSet tileSet = tileSets.get(tileSetIndex);

			int sheetX = tileSet.getTileX(layer.data[x][y][1]);
			int sheetY = tileSet.getTileY(layer.data[x][y][1]);

			return tileSet.tiles.getSubImage(sheetX, sheetY);
		}

		return null;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public TMXLayer getLayer(int id) {
		return layers.get(id);
	}

	public int getTileId(int x, int y, int layerIndex) {
		TMXLayer layer = layers.get(layerIndex);
		return layer.getTileID(x, y);
	}

	public void setTileId(int x, int y, int layerIndex, int tileid) {
		TMXLayer layer = layers.get(layerIndex);
		layer.setTileID(x, y, tileid);
	}

	public String getMapProperty(String propertyName, String def) {
		if (props == null)
			return def;
		return props.getProperty(propertyName, def);
	}

	public String getLayerProperty(int layerIndex, String propertyName,
			String def) {
		TMXLayer layer = layers.get(layerIndex);
		if (layer == null || layer.props == null)
			return def;
		return layer.props.getProperty(propertyName, def);
	}

	public String getTileProperty(int tileID, String propertyName, String def) {
		if (tileID == 0) {
			return def;
		}

		TMXTileSet set = findTileSet(tileID);

		TMXProperty props = set.getProperties(tileID);
		if (props == null) {
			return def;
		}
		return props.getProperty(propertyName, def);
	}

	public void draw(GLEx g, LTouch e) {
		int x = e.x() / tileWidth;
		int y = e.y() / tileHeight;
		draw(g, 0, 0, x, y, width - defWidth, height - defHeight, false);
	}

	public void draw(GLEx g, int tx, int ty) {
		draw(g, 0, 0, tx, ty);
	}

	public void draw(GLEx g, int x, int y, int tx, int ty) {
		draw(g, x, y, tx, ty, defWidth, defHeight, false);
	}

	public void draw(GLEx g, int x, int y, int layer) {
		draw(g, x, y, 0, 0, getWidth(), getHeight(), layer, false);
	}

	public void draw(GLEx g, int x, int y, int sx, int sy, int width, int height) {
		draw(g, x, y, sx, sy, width, height, false);
	}

	public void draw(GLEx g, int x, int y, int sx, int sy, int width,
			int height, int index, boolean lineByLine) {
		TMXLayer layer = layers.get(index);
		layer.draw(g, x, y, sx, sy, width, height, lineByLine, tileWidth,
				tileHeight);
	}

	public void draw(GLEx g, int x, int y, int sx, int sy, int width,
			int height, boolean lineByLine) {
		for (int i = 0; i < layers.size(); i++) {
			TMXLayer layer = layers.get(i);
			layer.draw(g, x, y, sx, sy, width, height, lineByLine, tileWidth,
					tileHeight);
		}
	}

	public int getLayerCount() {
		return layers.size();
	}

	private void load(InputStream in, String tileSetsLocation)
			throws RuntimeException {

		screenRect = LSystem.screenRect;

		tilesLocation = tileSetsLocation;

		try {
			XMLDocument doc = XMLParser.parse(in);
			XMLElement docElement = doc.getRoot();

			String orient = docElement.getAttribute("orientation", "");
			if (!"orthogonal".equals(orient)) {
				throw new RuntimeException(
						"Only orthogonal maps supported, found " + orient);
			}

			width = docElement.getIntAttribute("width", 0);
			height = docElement.getIntAttribute("height", 0);
			tileWidth = docElement.getIntAttribute("tilewidth", 0);
			tileHeight = docElement.getIntAttribute("tileheight", 0);

			XMLElement propsElement =  docElement
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

			if (loadTileSets) {
				TMXTileSet tileSet = null;
				TMXTileSet lastSet = null;

				ArrayList<XMLElement> setNodes = docElement.list("tileset");
				for (int i = 0; i < setNodes.size(); i++) {
					XMLElement current = setNodes.get(i);

					tileSet = new TMXTileSet(this, current, true);
					tileSet.index = i;

					if (lastSet != null) {
						lastSet.setLimit(tileSet.firstGID - 1);
					}
					lastSet = tileSet;

					tileSets.add(tileSet);
				}
			}

			ArrayList<XMLElement> layerNodes = docElement.list("layer");
			for (int i = 0; i < layerNodes.size(); i++) {
				XMLElement current = layerNodes.get(i);
				TMXLayer layer = new TMXLayer(this, current);
				layer.index = i;

				layers.add(layer);
			}

			ArrayList<XMLElement> objectGroupNodes = docElement
					.list("objectgroup");

			for (int i = 0; i < objectGroupNodes.size(); i++) {
				XMLElement current = objectGroupNodes.get(i);
				TMXTileGroup objectGroup = new TMXTileGroup(current);
				objectGroup.index = i;

				objectGroups.add(objectGroup);
			}

			defWidth = (int) (screenRect.getWidth() / tileWidth);
			defHeight = (int) (screenRect.getHeight() / tileHeight);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to parse map", ex);
		}
	}

	public int getScreenWidth() {
		return defWidth;
	}

	public int getScreenHeight() {
		return defHeight;
	}

	public int getTileSetCount() {
		return tileSets.size();
	}

	public TMXTileSet getTileSet(int index) {
		return tileSets.get(index);
	}

	public TMXTileSet getTileSetByGID(int gid) {
		for (int i = 0; i < tileSets.size(); i++) {
			TMXTileSet set = tileSets.get(i);
			if (set.contains(gid)) {
				return set;
			}
		}

		return null;
	}

	public TMXTileSet findTileSet(int gid) {
		for (int i = 0; i < tileSets.size(); i++) {
			TMXTileSet set =  tileSets.get(i);

			if (set.contains(gid)) {
				return set;
			}
		}

		return null;
	}

	protected void draw(GLEx g, int x, int y, int sx, int sy, int width,
			int height, int layer) {
	}

	public int getObjectGroupCount() {
		return objectGroups.size();
	}

	public int getObjectCount(int groupID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			return grp.objects.size();
		}
		return -1;
	}

	public String getObjectName(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.name;
			}
		}
		return null;
	}

	public String getObjectType(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.type;
			}
		}
		return null;
	}

	public int getObjectX(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.x;
			}
		}
		return -1;
	}

	public int getObjectY(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.y;
			}
		}
		return -1;
	}

	public int getObjectWidth(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.width;
			}
		}
		return -1;
	}

	public int getObjectHeight(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);
				return object.height;
			}
		}
		return -1;
	}

	public String getObjectImage(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);

				if (object == null) {
					return null;
				}

				return object.image;
			}
		}

		return null;
	}

	public String getObjectProperty(int groupID, int objectID,
			String propertyName, String def) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp =  objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object =  grp.objects.get(objectID);

				if (object == null) {
					return def;
				}
				if (object.props == null) {
					return def;
				}

				return object.props.getProperty(propertyName, def);
			}
		}
		return def;
	}

	@Override
	public void dispose() {
		if (tileSets != null) {
			for (TMXTileSet tmx : tileSets) {
				if (tmx != null) {
					tmx.dispose();
					tmx = null;
				}
			}
		}
	}

}
