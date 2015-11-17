package loon.action.map.tmx;

import java.util.ArrayList;
import java.util.List;

import loon.action.map.tmx.tiles.TMXTerrain;
import loon.action.map.tmx.tiles.TMXTile;
import loon.geom.Vector2f;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class TMXTileSet {
	
	private int firstGID;

	private String name;

	private int tileWidth;
	private int tileHeight;
	private int spacing;
	private int margin;

	private Vector2f tileOffset;
	private TMXImage image;

	private List<TMXTerrain> terrainTypes;
	private List<TMXTile> tiles;

	private TMXProperties properties;

	public TMXTileSet() {
		tileOffset = new Vector2f();

		terrainTypes = new ArrayList<>();
		tiles = new ArrayList<>();

		properties = new TMXProperties();
	}

	public void parse(XMLElement element, String tilesLocation) {

		this.firstGID = element.getIntAttribute("firstgid", 1);
		String source = element.getAttribute("source", "");
		if (!"".equals(source)) {
			try {
				XMLDocument doc = XMLParser.parse(tilesLocation + "/" + source);
				XMLElement docElement = doc.getRoot();
				element = docElement;
			} catch (Exception e) {
				throw new RuntimeException(tilesLocation + "/" + source);
			}
		}

		tileWidth = element.getIntAttribute("tilewidth", 0);
		tileHeight = element.getIntAttribute("tileheight", 0);
		margin = element.getIntAttribute("margin", 0);
		spacing = element.getIntAttribute("spacing", 0);

		name = element.getAttribute("name", "");

		XMLElement nodes = element.getChildrenByName("tileoffset");

		if (nodes != null) {
			XMLElement childElement = nodes;
			tileOffset.x = childElement.getFloatAttribute("x", 0);
			tileOffset.y = childElement.getFloatAttribute("y", 0);
		}

		nodes = element.getChildrenByName("terraintypes");

		if (nodes != null) {
			ArrayList<XMLElement> list = nodes.list();
			for (XMLElement terrain : list) {
				TMXTerrain terrainType = new TMXTerrain();
				terrainType.parse(terrain);
				terrainTypes.add(terrainType);
			}
		}

		nodes = element.getChildrenByName("image");

		if (nodes != null) {
			image = new TMXImage();
			image.parse(nodes, tilesLocation);
		}

		int tileCount = (image.getWidth() / tileWidth)
				* (image.getHeight() / tileHeight);

		for (int tID = 0; tID < tileCount; tID++) {
			TMXTile tile = new TMXTile(tID + firstGID);
			tiles.add(tile);
		}

		nodes = element.getChildrenByName("tile");

		if (nodes != null) {

			ArrayList<XMLElement> list = nodes.list();
			for (int i = 0; i < list.size(); i++) {
				XMLElement tileNode = list.get(i);
				TMXTile tile = new TMXTile(i);
				tile.parse(tileNode);
				tiles.get(tile.getID()).parse(tileNode);
			}
		}

		nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public int getFirstGID() {
		return firstGID;
	}

	public String getName() {
		return name;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getSpacing() {
		return spacing;
	}

	public int getMargin() {
		return margin;
	}

	public Vector2f getTileOffset() {
		return tileOffset;
	}

	public TMXImage getImage() {
		return image;
	}

	public List<TMXTerrain> getTerrainTypes() {
		return terrainTypes;
	}

	public TMXTile getTile(int id) {
		for (TMXTile tile : tiles) {
			if (tile.getID() == id) {
				return tile;
			}
		}
		return null;
	}

	public List<TMXTile> getTiles() {
		return tiles;
	}

	public TMXProperties getProperties() {
		return properties;
	}
}
