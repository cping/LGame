package loon.action.map.tmx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LLight;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLUtils;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureBatch;
import loon.core.graphics.opengl.LTextureBatch.GLCache;
import loon.net.Base64Coder;
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
public class TMXLayer extends LLight implements LRelease {

	private class MapTileSet {

		GLCache cache;

		LTexture texture;

	}

	private MapTileSet mapTileSet;

	private HashMap<Integer, MapTileSet> lazyMaps = new HashMap<Integer, MapTileSet>(
			10);

	private int keyHashCode = 1;

	private int cx = 0, cy = 0;

	private TMXTileSet tmxTileSet;

	// 基础地图
	private final TMXTiledMap tmx;

	// 图层索引
	public int index;

	// XML文件名
	public String name;

	// 图层数据
	public int[][][] data;

	// 图层宽度(TMX格式的宽，即实际宽/瓦片大小)
	public int width;

	// 图层高度(TMX格式的高，即实际高/瓦片大小)
	public int height;

	// 图层属性
	public TMXProperty props;

	/**
	 * 根据TMX地图描述创建一个新层
	 * 
	 * @param map
	 * @param element
	 * @throws RuntimeException
	 */
	public TMXLayer(TMXTiledMap map, XMLElement element)
			throws RuntimeException {

		this.tmx = map;
		this.name = element.getAttribute("name", "");
		this.width = element.getIntAttribute("width", 0);
		this.height = element.getIntAttribute("height", 0);
		this.data = new int[width][height][3];
		this.maxLightSize(width, height);

		// 获得当前图层属性
		XMLElement propsElement = element.getChildrenByName("properties");

		if (propsElement != null) {
			props = new TMXProperty();
			ArrayList<XMLElement> properties = propsElement.list("property");
			for (int i = 0; i < properties.size(); i++) {
				XMLElement propElement = properties.get(i);
				String name = propElement.getAttribute("name", null);
				String value = propElement.getAttribute("value", null);
				props.setProperty(name, value);
			}
		}

		XMLElement dataNode = element.getChildrenByName("data");
		String encoding = dataNode.getAttribute("encoding", null);
		String compression = dataNode.getAttribute("compression", null);

		// 进行base64的压缩解码
		if ("base64".equals(encoding) && "gzip".equals(compression)) {
			try {
				char[] enc = dataNode.getContents().trim().toCharArray();
				byte[] dec = Base64Coder.decodeBase64(enc);
				GZIPInputStream is = new GZIPInputStream(
						new ByteArrayInputStream(dec));

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int tileId = 0;
						tileId |= is.read();
						tileId |= is.read() << 8;
						tileId |= is.read() << 16;
						tileId |= is.read() << 24;

						if (tileId == 0) {
							data[x][y][0] = -1;
							data[x][y][1] = 0;
							data[x][y][2] = 0;
						} else {
							TMXTileSet set = map.findTileSet(tileId);

							if (set != null) {
								data[x][y][0] = set.index;
								data[x][y][1] = tileId - set.firstGID;
							}
							data[x][y][2] = tileId;
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to decode base64 !");
			}
		} else {
			throw new RuntimeException("Unsupport tiled map type " + encoding
					+ "," + compression + " only gzip base64 Support !");
		}
	}

	public void clearCache() {
		if (lazyMaps != null) {
			lazyMaps.clear();
		}
	}

	/**
	 * 获得指定位置的瓦片ID
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getTileID(int x, int y) {
		return data[x][y][2];
	}

	/**
	 * 设置指定位置的瓦片ID
	 * 
	 * @param x
	 * @param y
	 * @param tile
	 */
	public void setTileID(int x, int y, int tile) {
		if (tile == 0) {
			data[x][y][0] = -1;
			data[x][y][1] = 0;
			data[x][y][2] = 0;
		} else {
			TMXTileSet set = tmx.findTileSet(tile);
			data[x][y][0] = set.index;
			data[x][y][1] = tile - set.firstGID;
			data[x][y][2] = tile;
		}
	}

	/**
	 * 渲染当前层画面到LGraphics之上
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param sx
	 * @param sy
	 * @param width
	 * @param ty
	 * @param isLine
	 * @param mapTileWidth
	 * @param mapTileHeight
	 */
	public void draw(GLEx g, int x, int y, int sx, int sy, int width,
			int height, boolean isLine, int mapTileWidth, int mapTileHeight) {

		if (width == 0 || height == 0) {
			return;
		}

		if (lightingOn) {
			GLUtils.setShadeModelSmooth(GLEx.gl10);
		}

		this.tmxTileSet = null;
		this.mapTileSet = null;

		for (int tileset = 0; tileset < tmx.getTileSetCount(); tileset++) {

			keyHashCode = 1;
			keyHashCode = LSystem.unite(keyHashCode, tileset);
			keyHashCode = LSystem.unite(keyHashCode, sx);
			keyHashCode = LSystem.unite(keyHashCode, sy);
			keyHashCode = LSystem.unite(keyHashCode, width);
			keyHashCode = LSystem.unite(keyHashCode, height);
			keyHashCode = LSystem.unite(keyHashCode, mapTileWidth);
			keyHashCode = LSystem.unite(keyHashCode, mapTileHeight);
			keyHashCode = LSystem.unite(keyHashCode, lightingOn);

			mapTileSet = lazyMaps.get(keyHashCode);

			if (!isLightDirty && mapTileSet != null) {

				mapTileSet.cache.x = x;
				mapTileSet.cache.y = y;

				LTextureBatch.commit(mapTileSet.texture, mapTileSet.cache);

				if (isLine) {
					tmx.draw(g, x, y, sx, sy, width, height, index);
				}
				if (lightingOn) {
					GLUtils.setShadeModelFlat(GLEx.gl10);
				}

				return;
			}

			for (int ty = 0; ty < height; ty++) {
				for (int tx = 0; tx < width; tx++) {

					if ((sx + tx < 0) || (sy + ty < 0)) {
						continue;
					}
					if ((sx + tx >= this.width) || (sy + ty >= this.height)) {
						continue;
					}

					if (data[sx + tx][sy + ty][0] == tileset) {
						if (tmxTileSet == null) {
							tmxTileSet = tmx.getTileSet(tileset);
							tmxTileSet.tiles.glBegin();

						}

						int sheetX = tmxTileSet
								.getTileX(data[sx + tx][sy + ty][1]);
						int sheetY = tmxTileSet
								.getTileY(data[sx + tx][sy + ty][1]);

						int tileOffsetY = tmxTileSet.tileHeight - mapTileHeight;

						cx = tx * mapTileWidth;
						cy = ty * mapTileHeight - tileOffsetY;

						if (lightingOn) {
							setLightColor(cx / mapTileWidth, cy / mapTileHeight);
						}

						tmxTileSet.tiles
								.draw(g, cx, cy, sheetX, sheetY, colors);
					}

				}
			}

			if (tmxTileSet != null) {

				tmxTileSet.tiles.glEnd();

				if (mapTileSet == null) {
					mapTileSet = new MapTileSet();
				} else {
					mapTileSet.texture = null;
					mapTileSet.cache.dispose();
					mapTileSet.cache = null;
				}

				mapTileSet.texture = tmxTileSet.tiles.getTarget();
				mapTileSet.cache = tmxTileSet.tiles.newCache();
				mapTileSet.cache.x = x;
				mapTileSet.cache.y = y;
				
				lazyMaps.put(keyHashCode, mapTileSet);

				if (lightingOn) {
					GLUtils.setShadeModelFlat(GLEx.gl10);
				}

				if (isLine) {
					tmx.draw(g, x, y, sx, sy, width, height, index);
				}

				isLightDirty = false;
				tmxTileSet = null;
			}
		}

	}

	@Override
	public void dispose() {
		if (lazyMaps != null) {
			lazyMaps.clear();
		}
	}
}
