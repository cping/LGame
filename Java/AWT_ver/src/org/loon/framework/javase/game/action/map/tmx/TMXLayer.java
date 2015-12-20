package org.loon.framework.javase.game.action.map.tmx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
public class TMXLayer {

	private static byte[] base64 = new byte[256];

	static {
		for (int i = 0; i < 256; i++) {
			base64[i] = -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			base64[i] = (byte) (i - 'A');
		}
		for (int i = 'a'; i <= 'z'; i++) {
			base64[i] = (byte) (26 + i - 'a');
		}
		for (int i = '0'; i <= '9'; i++) {
			base64[i] = (byte) (52 + i - '0');
		}
		base64['+'] = 62;
		base64['/'] = 63;
	}

	// 基础地图
	private final TMXTiledMap map;
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
	public TMXLayer(TMXTiledMap map, Element element) throws RuntimeException {
		this.map = map;
		name = element.getAttribute("name");
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		data = new int[width][height][3];

		// 获得当前图层属性
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

		Element dataNode = (Element) element.getElementsByTagName("data").item(
				0);
		String encoding = dataNode.getAttribute("encoding");
		String compression = dataNode.getAttribute("compression");

		// 进行base64的压缩解码
		if ("base64".equals(encoding) && "gzip".equals(compression)) {
			try {
				Node cdata = dataNode.getFirstChild();
				char[] enc = cdata.getNodeValue().trim().toCharArray();
				byte[] dec = decodeBase64(enc);
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
			TMXTileSet set = map.findTileSet(tile);

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
	public void draw(LGraphics g, int x, int y, int sx, int sy, int width,
			int ty, boolean isLine, int mapTileWidth, int mapTileHeight) {

		int tileCount = map.getTileSetCount();

		int nx, ny, sheetX, sheetY, tileOffsetY;

		for (int tileset = 0; tileset < tileCount; tileset++) {

			TMXTileSet set = null;

			for (int tx = 0; tx < width; tx++) {

				nx = sx + tx;
				ny = sy + ty;

				if ((nx < 0) || (ny < 0)) {
					continue;
				}
				if ((nx >= this.width) || (ny >= this.height)) {
					continue;
				}

				if (data[nx][ny][0] == tileset) {
					if (set == null) {
						set = map.getTileSet(tileset);
					}

					sheetX = set.getTileX(data[nx][ny][1]);
					sheetY = set.getTileY(data[nx][ny][1]);

					tileOffsetY = set.tileHeight - mapTileHeight;

					set.tiles.draw(g, x + (tx * mapTileWidth), y
							+ (ty * mapTileHeight) - tileOffsetY, sheetX,
							sheetY);
				}
			}

			if (isLine) {
				if (set != null) {
					set = null;
				}
				map.rendered(ty, ty + sy, index);
			}
		}
	}

	/**
	 * 进行base64格式解码以获得相关的图层数据
	 * 
	 * @param data
	 * @return
	 */
	private byte[] decodeBase64(char[] data) {
		int temp = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || base64[data[ix]] < 0) {
				--temp;
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3) {
			len += 2;
		}
		if ((temp % 4) == 2) {
			len += 1;
		}
		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : base64[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException("index != " + out.length);
		}

		return out;
	}
}