package org.loon.framework.javase.game.action.map.tmx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.resource.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
public class TMXTiledMap {

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
		this.load(Resources.getResourceAsStream(fileName), fileName.substring(0,
				fileName.lastIndexOf("/")));
	}

	public TMXTiledMap(String fileName, String tileSetsLocation)
			throws RuntimeException {
		load(Resources.getResourceAsStream(fileName), tileSetsLocation);
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
			TMXLayer layer = (TMXLayer) layers.get(i);

			if (layer.name.equals(name)) {
				return i;
			}
		}

		return -1;
	}

	public LImage getTileImage(int x, int y, int layerIndex) {
		TMXLayer layer = (TMXLayer) layers.get(layerIndex);

		int tileSetIndex = layer.data[x][y][0];
		if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.size())) {
			TMXTileSet tileSet = (TMXTileSet) tileSets.get(tileSetIndex);

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

	public int getTileId(int x, int y, int layerIndex) {
		TMXLayer layer = (TMXLayer) layers.get(layerIndex);
		return layer.getTileID(x, y);
	}

	public void setTileId(int x, int y, int layerIndex, int tileid) {
		TMXLayer layer = (TMXLayer) layers.get(layerIndex);
		layer.setTileID(x, y, tileid);
	}

	public String getMapProperty(String propertyName, String def) {
		if (props == null)
			return def;
		return props.getProperty(propertyName, def);
	}

	public String getLayerProperty(int layerIndex, String propertyName,
			String def) {
		TMXLayer layer = (TMXLayer) layers.get(layerIndex);
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

	public void draw(LGraphics g, int tx, int ty) {
		draw(g, 0, 0, tx, ty);
	}

	public void draw(LGraphics g, int x, int y, int tx, int ty) {
		draw(g, x, y, tx, ty, defWidth, defHeight, false);
	}

	public void draw(LGraphics g, int x, int y, int layer) {
		draw(g, x, y, 0, 0, getWidth(), getHeight(), layer, false);
	}

	public void draw(LGraphics g, int x, int y, int sx, int sy, int width,
			int height) {
		draw(g, x, y, sx, sy, width, height, false);
	}

	public void draw(LGraphics g, int x, int y, int sx, int sy, int width,
			int height, int l, boolean lineByLine) {
		TMXLayer layer = (TMXLayer) layers.get(l);
		for (int ty = 0; ty < height; ty++) {
			layer.draw(g, x, y, sx, sy, width, ty, lineByLine, tileWidth,
					tileHeight);
		}
	}

	public void draw(LGraphics g, int x, int y, int sx, int sy, int width,
			int height, boolean lineByLine) {
		for (int ty = 0; ty < height; ty++) {
			for (int i = 0; i < layers.size(); i++) {
				TMXLayer layer = (TMXLayer) layers.get(i);
				layer.draw(g, x, y, sx, sy, width, ty, lineByLine, tileWidth,
						tileHeight);
			}
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
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					return new InputSource(
							new ByteArrayInputStream(new byte[0]));
				}
			});

			Document doc = builder.parse(in);
			Element docElement = doc.getDocumentElement();

			String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new RuntimeException(
						"Only orthogonal maps supported, found " + orient);
			}

			width = Integer.parseInt(docElement.getAttribute("width"));
			height = Integer.parseInt(docElement.getAttribute("height"));
			tileWidth = Integer.parseInt(docElement.getAttribute("tilewidth"));
			tileHeight = Integer
					.parseInt(docElement.getAttribute("tileheight"));

			Element propsElement = (Element) docElement.getElementsByTagName(
					"properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement
						.getElementsByTagName("property");
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

			if (loadTileSets) {
				TMXTileSet tileSet = null;
				TMXTileSet lastSet = null;

				NodeList setNodes = docElement.getElementsByTagName("tileset");
				for (int i = 0; i < setNodes.getLength(); i++) {
					Element current = (Element) setNodes.item(i);

					tileSet = new TMXTileSet(this, current, true);
					tileSet.index = i;

					if (lastSet != null) {
						lastSet.setLimit(tileSet.firstGID - 1);
					}
					lastSet = tileSet;

					tileSets.add(tileSet);
				}
			}

			NodeList layerNodes = docElement.getElementsByTagName("layer");
			for (int i = 0; i < layerNodes.getLength(); i++) {
				Element current = (Element) layerNodes.item(i);
				TMXLayer layer = new TMXLayer(this, current);
				layer.index = i;

				layers.add(layer);
			}

			NodeList objectGroupNodes = docElement
					.getElementsByTagName("objectgroup");

			for (int i = 0; i < objectGroupNodes.getLength(); i++) {
				Element current = (Element) objectGroupNodes.item(i);
				TMXTileGroup objectGroup = new TMXTileGroup(current);
				objectGroup.index = i;

				objectGroups.add(objectGroup);
			}

			defWidth = screenRect.getWidth() / tileWidth;
			defHeight = screenRect.getHeight() / tileHeight;

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse map", e);
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
		return (TMXTileSet) tileSets.get(index);
	}

	public TMXTileSet getTileSetByGID(int gid) {
		for (int i = 0; i < tileSets.size(); i++) {
			TMXTileSet set = (TMXTileSet) tileSets.get(i);
			if (set.contains(gid)) {
				return set;
			}
		}

		return null;
	}

	public TMXTileSet findTileSet(int gid) {
		for (int i = 0; i < tileSets.size(); i++) {
			TMXTileSet set = (TMXTileSet) tileSets.get(i);

			if (set.contains(gid)) {
				return set;
			}
		}

		return null;
	}

	protected void rendered(int visualY, int mapY, int layer) {
	}

	public int getObjectGroupCount() {
		return objectGroups.size();
	}

	public int getObjectCount(int groupID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			return grp.objects.size();
		}
		return -1;
	}

	public String getObjectName(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.name;
			}
		}
		return null;
	}

	public String getObjectType(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.type;
			}
		}
		return null;
	}

	public int getObjectX(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.x;
			}
		}
		return -1;
	}

	public int getObjectY(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.y;
			}
		}
		return -1;
	}

	public int getObjectWidth(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.width;
			}
		}
		return -1;
	}

	public int getObjectHeight(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);
				return object.height;
			}
		}
		return -1;
	}

	public String getObjectImage(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);

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
			TMXTileGroup grp = (TMXTileGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				TMXTile object = (TMXTile) grp.objects.get(objectID);

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

}
