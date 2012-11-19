package org.loon.framework.javase.game.action.map.tmx;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.loon.framework.javase.game.action.sprite.SpriteSheet;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.resource.Resources;
import org.w3c.dom.Document;
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
public class TMXTileSet {
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

	public TMXTileSet(TMXTiledMap map, Element element, boolean loadImage)
			throws RuntimeException {
		this.map = map;
		name = element.getAttribute("name");
		firstGID = Integer.parseInt(element.getAttribute("firstgid"));
		String source = element.getAttribute("source");

		if ((source != null) && (!source.equals(""))) {
			try {
				InputStream in = Resources.getResourceAsStream(map
						.getTilesLocation()
						+ "/" + source);
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(in);
				Element docElement = doc.getDocumentElement();
				element = docElement;

			} catch (Exception e) {
				throw new RuntimeException(this.map.tilesLocation + "/"
						+ source);
			}
		}
		String tileWidthString = element.getAttribute("tilewidth");
		String tileHeightString = element.getAttribute("tileheight");
		if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
			throw new RuntimeException(
					"tileWidthString.length == 0 || tileHeightString.length == 0");
		}
		tileWidth = Integer.parseInt(tileWidthString);
		tileHeight = Integer.parseInt(tileHeightString);

		String sv = element.getAttribute("spacing");
		if ((sv != null) && (!"".equals(sv))) {
			tileSpacing = Integer.parseInt(sv);
		}

		String mv = element.getAttribute("margin");
		if ((mv != null) && (!"".equals(mv))) {
			tileMargin = Integer.parseInt(mv);
		}

		NodeList list = element.getElementsByTagName("image");
		Element imageNode = (Element) list.item(0);
		String fileName = imageNode.getAttribute("source");

		if (loadImage) {
			LImage image = new LImage(map.getTilesLocation() + "/" + fileName);
			setTileSetImage(image);
		}

		NodeList pElements = element.getElementsByTagName("tile");
		for (int i = 0; i < pElements.getLength(); i++) {
			Element tileElement = (Element) pElements.item(i);

			int id = Integer.parseInt(tileElement.getAttribute("id"));
			id += firstGID;
			TMXProperty tileProps = new TMXProperty();

			Element propsElement = (Element) tileElement.getElementsByTagName(
					"properties").item(0);
			NodeList properties = propsElement.getElementsByTagName("property");
			for (int p = 0; p < properties.getLength(); p++) {
				Element propElement = (Element) properties.item(p);

				String name = propElement.getAttribute("name");
				String value = propElement.getAttribute("value");

				tileProps.setProperty(name, value);
			}

			props.put(new Integer(id), tileProps);
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

	public void setTileSetImage(LImage image) {
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
		return (TMXProperty) props.get(new Integer(globalID));
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
}