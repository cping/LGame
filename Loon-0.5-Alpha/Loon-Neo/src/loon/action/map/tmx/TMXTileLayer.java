package loon.action.map.tmx;

import java.nio.IntBuffer;
import java.util.ArrayList;

import loon.LSystem;
import loon.action.map.tmx.tiles.TmxMapTile;
import loon.utils.Base64Coder;
import loon.utils.CompressionUtils;
import loon.utils.MathUtils;
import loon.utils.xml.XMLElement;

public class TMXTileLayer extends TMXMapLayer {

	public enum Encoding {
		XML, BASE64, CSV
	}

	public enum Compression {
		NONE, GZIP, ZLIB
	}

	private TmxMapTile[] tileMap;

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

		tileMap = new TmxMapTile[width * height];

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
			parseBase64(dataElement.getContents());
			break;

		case CSV:
			parseCSV(dataElement.getContents());
			break;
		}
	}

	private void parseXML(XMLElement element) {
		XMLElement nodes = element.getChildrenByName("tile");
		ArrayList<XMLElement> list = nodes.list();
		for (int tileCount = 0; tileCount < list.size(); tileCount++) {
			XMLElement tileElement = list.get(tileCount);

			int gid = MathUtils.parseUnsignedInt(tileElement.getAttribute(
					"gid", "-1"));

			int tileSetIndex = map.findTileSetIndex(gid);

			if (tileSetIndex != -1) {
				TMXTileSet tileSet = map.getTileset(tileSetIndex);
				tileMap[tileCount] = new TmxMapTile(gid, tileSet.getFirstGID(),
						tileSetIndex);
			} else
				tileMap[tileCount] = new TmxMapTile(gid, 0, -1);
		}
	}

	private void parseBase64(String base64) {
		char[] enc = base64.trim().toCharArray();
		byte[] dec = Base64Coder.decodeBase64(enc);
		try {
			if (compression == Compression.GZIP) {
				dec = CompressionUtils.decompressGZIP(dec);
			} else

			if (compression == Compression.ZLIB) {
				dec = CompressionUtils.decompressZLIB(dec);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		IntBuffer intBuffer = LSystem.base().support().getByteBuffer(dec)
				.asIntBuffer();
		int[] out = new int[intBuffer.remaining()];
		intBuffer.get(out);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int gid = out[y * width + x];

				int tileSetIndex = map.findTileSetIndex(gid);

				if (tileSetIndex != -1) {
					TMXTileSet tileSet = map.getTileset(tileSetIndex);
					tileMap[y * width + x] = new TmxMapTile(gid,
							tileSet.getFirstGID(), tileSetIndex);
				} else {
					tileMap[y * width + x] = new TmxMapTile(gid, 0, -1);
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
				tileMap[tileCount] = new TmxMapTile(gid, tileSet.getFirstGID(),
						tileSetIndex);
			} else {
				tileMap[tileCount] = new TmxMapTile(gid, 0, -1);
			}

			tileCount++;
		}
	}

	public void setTileGID(int x, int y, int gid) {
		int tileSetIndex = map.findTileSetIndex(gid);
		if (tileSetIndex != -1) {
			TMXTileSet tileSet = map.getTileset(tileSetIndex);
			tileMap[y * width + x] = new TmxMapTile(gid, tileSet.getFirstGID(),
					tileSetIndex);
		} else {
			tileMap[y * width + x] = new TmxMapTile(gid, 0, -1);
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

	public TmxMapTile getTile(int x, int y) {
		return tileMap[y * width + x];
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public Compression getCompression() {
		return compression;
	}
}
