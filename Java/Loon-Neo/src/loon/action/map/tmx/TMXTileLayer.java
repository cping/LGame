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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import loon.Json;
import loon.LSysException;
import loon.LSystem;
import loon.action.map.Field2D;
import loon.action.map.TileMapConfig;
import loon.action.map.tmx.tiles.TMXMapTile;
import loon.canvas.LColor;
import loon.utils.Base64Coder;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXTileLayer extends TMXMapLayer {

	public static enum Encoding {
		XML, BASE64, CSV
	}

	public static enum Compression {
		NONE, GZIP, ZLIB
	}

	private TMXMapTile[] tileMap;

	private Encoding encoding;
	private Compression compression;

	public TMXTileLayer(TMXMap map) {
		super(map, LSystem.EMPTY, 0, 0, map.getWidth(), map.getHeight(), 1.0f, true, TmxLayerType.TILE);

		encoding = Encoding.XML;
		compression = Compression.NONE;
	}

	public void parse(Json.Object element) {

		id = element.getInt("id", 0);
		name = element.getString("name", LSystem.EMPTY);

		if (element.containsKey("tintcolor")) {
			tintColor = new LColor(element.getString("tintcolor", LColor.white.toString()).trim());
		}

		offsetX = element.getNumber("x", 0);
		offsetY = element.getNumber("y", 0);

		offsetX = element.getNumber("offsetx", offsetX);
		offsetY = element.getNumber("offsety", offsetY);

		parallaxX = element.getNumber("parallaxx", 0f);
		parallaxY = element.getNumber("parallaxy", 0f);

		opacity = element.getNumber("opacity", 1f);
		visible = element.getBoolean("visible", true);

		Json.Array nodes = element.getArray("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}

		tileMap = new TMXMapTile[width * height];

		if (element.containsKey("encoding")) {
			switch (element.getString("encoding", LSystem.EMPTY).trim().toLowerCase()) {
			case "base64":
				encoding = Encoding.BASE64;
				break;
			case "csv":
				encoding = Encoding.CSV;
				break;
			case "xml":
			case "json":
			default:
				encoding = Encoding.XML;
			}
		}

		if (element.containsKey("compression")) {
			switch (element.getString("compression", LSystem.EMPTY).trim().toLowerCase()) {
			case "gzip":
				compression = Compression.GZIP;
				break;
			case "zlib":
				compression = Compression.ZLIB;
				break;
			default:
				compression = Compression.NONE;
			}
		}

		if (element.isArray("data")) {
			encoding = Encoding.CSV;
			parseArray2D(element.getArray("data", null));
		} else {
			final String dataContext = element.getString("data", null);
			switch (encoding) {
			case XML:
				parseJSON(element);
				break;
			case BASE64:
				try {
					parseBase64(dataContext);
				} catch (Throwable e) {
					LSystem.error("TMXTile parse base64 exception", e);
				}
				break;
			case CSV:
				parseCSV(dataContext);
				break;
			}
		}
	}

	public void parse(XMLElement element) {

		id = element.getIntAttribute("id", 0);
		name = element.getAttribute("name", LSystem.EMPTY);

		if (element.hasAttribute("tintcolor")) {
			tintColor = new LColor(element.getAttribute("tintcolor", LColor.white.toString()).trim());
		}

		offsetX = element.getFloatAttribute("x", 0);
		offsetY = element.getFloatAttribute("y", 0);

		offsetX = element.getFloatAttribute("offsetx", offsetX);
		offsetY = element.getFloatAttribute("offsety", offsetY);

		parallaxX = element.getFloatAttribute("parallaxx", 0f);
		parallaxY = element.getFloatAttribute("parallaxy", 0f);

		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}

		tileMap = new TMXMapTile[width * height];

		XMLElement dataElement = element.getChildrenByName("data");

		if (dataElement.hasAttribute("encoding")) {
			switch (dataElement.getAttribute("encoding", LSystem.EMPTY).trim().toLowerCase()) {
			case "base64":
				encoding = Encoding.BASE64;
				break;
			case "csv":
				encoding = Encoding.CSV;
				break;
			default:
				encoding = Encoding.XML;
			}
		}

		if (dataElement.hasAttribute("compression")) {
			switch (dataElement.getAttribute("compression", LSystem.EMPTY).trim().toLowerCase()) {
			case "gzip":
				compression = Compression.GZIP;
				break;
			case "zlib":
				compression = Compression.ZLIB;
				break;
			default:
				compression = Compression.NONE;
			}
		}

		switch (encoding) {
		case XML:
			parseXML(dataElement);
			break;

		case BASE64:
			try {
				parseBase64(dataElement.getContents());
			} catch (Throwable e) {
				LSystem.error("TMXTile parse base64 exception", e);
			}
			break;

		case CSV:
			parseCSV(dataElement.getContents());
			break;
		}
	}

	private void parseJSON(Json.Object element) {
		Json.Array nodes = element.getArray("tiles");
		for (int tileCount = 0; tileCount < nodes.length(); tileCount++) {
			Json.Object tileElement = nodes.getObject(tileCount);
			int gid = MathUtils.parseUnsignedInt(tileElement.getString("gid", "-1"));
			int tileSetIndex = parent.findTileSetIndex(gid);
			if (tileSetIndex != -1) {
				TMXTileSet tileSet = parent.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
			} else
				tileMap[tileCount] = new TMXMapTile(gid, 0, -1);
		}
	}

	private void parseXML(XMLElement element) {
		XMLElement nodes = element.getChildrenByName("tile");
		TArray<XMLElement> list = nodes.list();
		for (int tileCount = 0; tileCount < list.size; tileCount++) {
			XMLElement tileElement = list.get(tileCount);

			int gid = MathUtils.parseUnsignedInt(tileElement.getAttribute("gid", "-1"));

			int tileSetIndex = parent.findTileSetIndex(gid);

			if (tileSetIndex != -1) {
				TMXTileSet tileSet = parent.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
			} else
				tileMap[tileCount] = new TMXMapTile(gid, 0, -1);
		}
	}

	private static int byteToInt(byte b) {
		return b & 0xFF;
	}

	private void parseBase64(String base64) throws Exception {
		byte[] bytes = Base64Coder.decodeBase64(base64.toCharArray());
		InputStream is = null;
		if (compression == null || compression == Compression.NONE) {
			is = new ByteArrayInputStream(bytes);
		} else if (compression == Compression.GZIP) {
			is = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length));
		} else if (compression == Compression.ZLIB) {
			is = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
		}
		byte[] temp = new byte[4];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int read = is.read(temp);
				while (read < temp.length) {
					int curr = is.read(temp, read, temp.length - read);
					if (curr == -1)
						break;
					read += curr;
				}
				if (read != temp.length) {
					throw new LSysException("Error Reading TMX Layer Data: Premature end of tile data");
				}

				int gid = byteToInt(temp[0]) | byteToInt(temp[1]) << 8 | byteToInt(temp[2]) << 16
						| byteToInt(temp[3]) << 24;

				int tileSetIndex = parent.findTileSetIndex(gid);

				if (tileSetIndex != -1) {
					TMXTileSet tileSet = parent.getTileset(tileSetIndex);
					tileMap[y * width + x] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
				} else {
					tileMap[y * width + x] = new TMXMapTile(gid, 0, -1);
				}
			}
		}
	}

	private void parseArray2D(Json.Array arrays) {
		if (arrays == null) {
			return;
		}
		final int size = arrays.length();
		int tileCount = 0;
		for (int i = 0; i < size; i++) {
			final String token = arrays.getString(i).trim();
			final int gid = MathUtils.parseUnsignedInt(token);
			final int tileSetIndex = parent.findTileSetIndex(gid);
			if (tileSetIndex != -1) {
				TMXTileSet tileSet = parent.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
			} else {
				tileMap[tileCount] = new TMXMapTile(gid, 0, -1);
			}
			tileCount++;
		}
	}

	private void parseCSV(String csv) {
		String[] tokens = StringUtils.split(csv, LSystem.COMMA);
		int tileCount = 0;

		for (String token : tokens) {
			int gid = MathUtils.parseUnsignedInt(token.trim());

			int tileSetIndex = parent.findTileSetIndex(gid);

			if (tileSetIndex != -1) {
				TMXTileSet tileSet = parent.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
			} else {
				tileMap[tileCount] = new TMXMapTile(gid, 0, -1);
			}

			tileCount++;
		}
	}

	public void setTileGID(int x, int y, int gid) {
		int tileSetIndex = parent.findTileSetIndex(gid);
		if (tileSetIndex != -1) {
			TMXTileSet tileSet = parent.getTileset(tileSetIndex);
			tileMap[y * width + x] = new TMXMapTile(gid, tileSet.getFirstGID(), tileSetIndex);
		} else {
			tileMap[y * width + x] = new TMXMapTile(gid, 0, -1);
		}
	}

	public int getTileID(int x, int y) {
		if (x < 0 || y < 0) {
			return -1;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return -1;
		}
		return tileMap[y * width + x].getID();
	}

	public int getTileGID(int x, int y) {
		if (x < 0 || y < 0) {
			return -1;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return -1;
		}
		return tileMap[y * width + x].getGID();
	}

	public int getTileTileSetIndex(int x, int y) {
		if (x < 0 || y < 0) {
			return -1;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return -1;
		}
		return tileMap[y * width + x].getTileSetID();
	}

	public boolean isTileFlippedHorizontally(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return false;
		}
		return tileMap[y * width + x].isFlippedHorizontally();
	}

	public boolean isTileFlippedVertically(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return false;
		}
		return tileMap[y * width + x].isFlippedVertically();
	}

	public boolean isTileFlippedDiagonally(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return false;
		}
		return tileMap[y * width + x].isFlippedDiagonally();
	}

	public TMXMapTile getTile(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}
		final int len = y * width + x;
		if (len >= tileMap.length) {
			return null;
		}
		return tileMap[len];
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public Compression getCompression() {
		return compression;
	}

	public Field2D newGIDField2D() {
		return newField2D(0);
	}

	public Field2D newTileSetIDField2D() {
		return newField2D(1);
	}

	public Field2D newIDField2D() {
		return newField2D(2);
	}

	private Field2D newField2D(int mode) {
		int[][] tmp = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				switch (mode) {
				case 0:
					tmp[x][y] = tileMap[y * width + x].getGID();
					break;
				case 1:
					tmp[x][y] = tileMap[y * width + x].getTileSetID();
					break;
				default:
					tmp[x][y] = tileMap[y * width + x].getID();
					break;
				}
			}
		}
		Field2D field2d = new Field2D(TileMapConfig.reversalXandY(tmp), getMap().getTileWidth(),
				getMap().getTileHeight());
		field2d.setName(name);
		field2d.Tag = this;
		return field2d;
	}
}
