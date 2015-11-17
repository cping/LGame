package loon.action.map.tmx;

import loon.utils.xml.XMLElement;


public class TMXImageLayer extends TMXMapLayer {
	private TMXImage image;

	public TMXImageLayer(TMXMap map) {
		super(map, "", 0, 0, map.getWidth(), map.getHeight(), 1.0f, true,
				TmxLayerType.IMAGE);
	}

	public void parse(XMLElement element) {

		name = element.getAttribute("name", "");

		x = element.getIntAttribute("x", 0);
		y = element.getIntAttribute("y", 0);

		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("image");
		if (nodes!=null) {
			image = new TMXImage();
			image.parse(nodes, getMap().getFilePath());
		}

		nodes = element.getChildrenByName("properties");
		if (nodes!=null) {
			properties.parse(nodes);
		}
	}

	public TMXImage getImage() {
		return image;
	}
}
