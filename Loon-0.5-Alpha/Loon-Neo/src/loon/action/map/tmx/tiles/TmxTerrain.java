package loon.action.map.tmx.tiles;

import loon.action.map.tmx.TMXProperties;
import loon.utils.xml.XMLElement;

public class TmxTerrain {

	private String name;

	private int tileID;

	private TMXProperties properties;

	public TmxTerrain() {
		properties = new TMXProperties();
	}

	public void parse(XMLElement element) {
		name = element.getAttribute("name", "");
		tileID = element.getIntAttribute("tile", 0);
		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}
	}

	public String getName() {
		return name;
	}

	public int getTileID() {
		return tileID;
	}

	public TMXProperties getProperties() {
		return properties;
	}
}
