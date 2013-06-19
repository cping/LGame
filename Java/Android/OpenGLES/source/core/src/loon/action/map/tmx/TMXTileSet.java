package loon.action.map.tmx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import loon.action.sprite.SpriteSheet;
import loon.core.LRelease;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.TextureUtils;
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
public class TMXTileSet implements LRelease {
	// 基础地图
	private final TMXTiledMap map;

	// 瓦片索引
	public int index;

	public String name;

	public int firstGID;

	public int lastGID = Integer.MAX_VALUE;

	public int tileWidth;

	public int tileHeight;

	public SpriteSheet tiles;

	public int tilesAcross;

	public int tilesDown;

	private HashMap<Integer, TMXProperty> props = new HashMap<Integer, TMXProperty>();

	protected int tileSpacing = 0;

	protected int tileMargin = 0;

	public TMXTileSet(TMXTiledMap map, XMLElement element, boolean loadImage)
			throws RuntimeException {
		this.map = map;
		this.name = element.getAttribute("name", null);
		this.firstGID = element.getIntAttribute("firstgid", 0);
		String source = element.getAttribute("source", "");
		if (!"".equals(source)) {
			try {
				InputStream in = Resources.openResource(map.getTilesLocation()
						+ "/" + source);
				XMLDocument doc = XMLParser.parse(in);
				XMLElement docElement = doc.getRoot();
				element = docElement;
			} catch (Exception e) {
				throw new RuntimeException(this.map.tilesLocation + '/'
						+ source);
			}
		}
		String tileWidthString = element.getAttribute("tilewidth", "");
		String tileHeightString = element.getAttribute("tileheight", "");
		if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
			throw new RuntimeException(
					"tileWidthString.length == 0 || tileHeightString.length == 0");
		}
		tileWidth = Integer.parseInt(tileWidthString);
		tileHeight = Integer.parseInt(tileHeightString);

		String sv = element.getAttribute("spacing", "");
		if ((sv != null) && (!"".equals(sv))) {
			tileSpacing = Integer.parseInt(sv);
		}

		String mv = element.getAttribute("margin", "");
		if ((mv != null) && (!"".equals(mv))) {
			tileMargin = Integer.parseInt(mv);
		}

		ArrayList<XMLElement> list = element.list("image");
		XMLElement imageNode = list.get(0);
		String fileName = imageNode.getAttribute("source", null);

		LColor trans = null;
		String t = imageNode.getAttribute("trans", null);
		if ((t != null) && (t.length() > 0)) {
			trans = new LColor(Integer.parseInt(t, 16));
		}

		if (loadImage) {
			String path = map.getTilesLocation() + "/" + fileName;
			LTexture image;
			if (trans != null) {
				image = TextureUtils.filterColor(path, trans);
			} else {
				image = LTextures.loadTexture(path);
			}
			setTileSetImage(image);
		}

		ArrayList<XMLElement> elements = element.list("tile");
		for (int i = 0; i < elements.size(); i++) {
			XMLElement tileElement = elements.get(i);

			int id = tileElement.getIntAttribute("id", 0);
			id += firstGID;
			TMXProperty tileProps = new TMXProperty();

			XMLElement propsElement = tileElement
					.getChildrenByName("properties");
			ArrayList<XMLElement> properties = propsElement.list("property");
			for (int p = 0; p < properties.size(); p++) {
				XMLElement propElement = properties.get(p);
				String name = propElement.getAttribute("name", null);
				String value = propElement.getAttribute("value", null);
				tileProps.setProperty(name, value);
			}
			props.put(id, tileProps);
		}
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getTileSpacing() {
		return tileSpacing;
	}

	public int getTileMargin() {
		return tileMargin;
	}

	public void setTileSetImage(LTexture image) {
		tiles = new SpriteSheet(image, tileWidth, tileHeight, tileSpacing,
				tileMargin);
		tilesAcross = tiles.getHorizontalCount();
		tilesDown = tiles.getVerticalCount();

		if (tilesAcross <= 0) {
			tilesAcross = 1;
		}
		if (tilesDown <= 0) {
			tilesDown = 1;
		}

		lastGID = (tilesAcross * tilesDown) + firstGID - 1;
	}

	public TMXProperty getProperties(int globalID) {
		return props.get(globalID);
	}

	public int getTileX(int id) {
		return id % tilesAcross;
	}

	public int getTileY(int id) {
		return id / tilesAcross;
	}

	public void setLimit(int limit) {
		lastGID = limit;
	}

	public boolean contains(int gid) {
		return (gid >= firstGID) && (gid <= lastGID);
	}

	@Override
	public void dispose() {
		if (tiles != null) {
			tiles.dispose();
			tiles = null;
		}
	}
}
