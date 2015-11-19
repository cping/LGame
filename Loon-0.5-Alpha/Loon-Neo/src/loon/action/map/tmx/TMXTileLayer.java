package loon.action.map.tmx;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import loon.action.map.Field2D;
import loon.action.map.TileMapConfig;
import loon.action.map.tmx.tiles.TMXMapTile;
import loon.utils.Base64Coder;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXTileLayer extends TMXMapLayer {

	public enum Encoding {
		XML, BASE64, CSV
	}

	public enum Compression {
		NONE, GZIP, ZLIB
	}

	private TMXMapTile[] tileMap;

	private Encoding encoding;
	private Compression compression;

	public TMXTileLayer(TMXMap map) {
		super(map, "", 0, 0, map.getWidth(), map.getHeight(), 1.0f, true,
				TmxLayerType.TILE);

		encoding = Encoding.XML;
		compression = Compression.NONE;
	}

	public void parse(XMLElement element) {

		name = element.getAttribute("name", "");

		x = element.getIntAttribute("x", 0);
		y = element.getIntAttribute("y", 0);

		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null)
			properties.parse(nodes);

		tileMap = new TMXMapTile[width * height];

		XMLElement dataElement = element.getChildrenByName("data");

		if (dataElement.hasAttribute("encoding")) {
			switch (dataElement.getAttribute("encoding", "").trim()
					.toLowerCase()) {
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
			switch (dataElement.getAttribute("compression", "").trim()
					.toLowerCase()) {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case CSV:
			parseCSV(dataElement.getContents());
			break;
		}
	}

	private void parseXML(XMLElement element) {
		XMLElement nodes = element.getChildrenByName("tile");
		TArray<XMLElement> list = nodes.list();
		for (int tileCount = 0; tileCount < list.size; tileCount++) {
			XMLElement tileElement = list.get(tileCount);

			int gid = MathUtils.parseUnsignedInt(tileElement.getAttribute(
					"gid", "-1"));

			int tileSetIndex = map.findTileSetIndex(gid);

			if (tileSetIndex != -1) {
				TMXTileSet tileSet = map.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(),
						tileSetIndex);
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
			is = new BufferedInputStream(new GZIPInputStream(
					new ByteArrayInputStream(bytes), bytes.length));
		} else if (compression == Compression.ZLIB) {
			is = new BufferedInputStream(new InflaterInputStream(
					new ByteArrayInputStream(bytes)));
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
					throw new RuntimeException(
							"Error Reading TMX Layer Data: Premature end of tile data");
				}
				int gid = byteToInt(temp[0]) | byteToInt(temp[1]) << 8
						| byteToInt(temp[2]) << 16 | byteToInt(temp[3]) << 24;

				int tileSetIndex = map.findTileSetIndex(gid);

				if (tileSetIndex != -1) {
					TMXTileSet tileSet = map.getTileset(tileSetIndex);
					tileMap[y * width + x] = new TMXMapTile(gid,
							tileSet.getFirstGID(), tileSetIndex);
				} else {
					tileMap[y * width + x] = new TMXMapTile(gid, 0, -1);
				}
			}
		}
	}

	private void parseCSV(String csv) {
		String[] tokens = csv.split(",");
		int tileCount = 0;

		for (String token : tokens) {
			int gid = MathUtils.parseUnsignedInt(token.trim());

			int tileSetIndex = map.findTileSetIndex(gid);

			if (tileSetIndex != -1) {
				TMXTileSet tileSet = map.getTileset(tileSetIndex);
				tileMap[tileCount] = new TMXMapTile(gid, tileSet.getFirstGID(),
						tileSetIndex);
			} else {
				tileMap[tileCount] = new TMXMapTile(gid, 0, -1);
			}

			tileCount++;
		}
	}

	public void setTileGID(int x, int y, int gid) {
		int tileSetIndex = map.findTileSetIndex(gid);
		if (tileSetIndex != -1) {
			TMXTileSet tileSet = map.getTileset(tileSetIndex);
			tileMap[y * width + x] = new TMXMapTile(gid, tileSet.getFirstGID(),
					tileSetIndex);
		} else {
			tileMap[y * width + x] = new TMXMapTile(gid, 0, -1);
		}
	}

	public int getTileID(int x, int y) {
		return tileMap[y * width + x].getID();
	}

	public int getTileGID(int x, int y) {
		return tileMap[y * width + x].getGID();
	}

	public int getTileTileSetIndex(int x, int y) {
		return tileMap[y * width + x].getTileSetID();
	}

	public boolean isTileFlippedHorizontally(int x, int y) {
		return tileMap[y * width + x].isFlippedHorizontally();
	}

	public boolean isTileFlippedVertically(int x, int y) {
		return tileMap[y * width + x].isFlippedVertically();
	}

	public boolean isTileFlippedDiagonally(int x, int y) {
		return tileMap[y * width + x].isFlippedDiagonally();
	}

	public TMXMapTile getTile(int x, int y) {
		return tileMap[y * width + x];
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
		Field2D field2d = new Field2D(TileMapConfig.reversalXandY(tmp),
				getMap().getTileWidth(), getMap().getTileHeight());
		field2d.setName(name);
		field2d.Tag = this;
		return field2d;
	}
}
