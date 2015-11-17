package loon.action.map.tmx;

import java.util.ArrayList;
import java.util.List;

import loon.action.map.tmx.objects.TMXObject;
import loon.canvas.LColor;
import loon.utils.xml.XMLElement;

public class TMXObjectLayer extends TMXMapLayer {

	private LColor color;

	private List<TMXObject> objects;

	public TMXObjectLayer(TMXMap map) {
		super(map, "", 0, 0, map.getWidth(), map.getHeight(), 1.0f, true,
				TmxLayerType.OBJECT);

		objects = new ArrayList<>();
		color = new LColor(LColor.TRANSPARENT);
	}

	public TMXObject getObject(int index) {
		return objects.get(index);
	}

	public int getNumObjects() {
		return objects.size();
	}

	public LColor getColor() {
		return color;
	}

	public List<TMXObject> getObjects() {
		return objects;
	}

	public void parse(XMLElement element) {
		name = element.getAttribute("name", "");
		if (element.hasAttribute("color")) {
			String colorString = element.getAttribute("color",
					LColor.white.toString()).trim();
			if (colorString.startsWith("#")) {
				colorString = colorString.substring(1);
			}
			color = new LColor(Integer.decode(colorString));
		}
		opacity = element.getFloatAttribute("opacity", 1f);
		visible = element.getBoolAttribute("visible", true);

		XMLElement nodes = element.getChildrenByName("properties");
		if (nodes != null) {
			properties.parse(nodes);
		}

		nodes = element.getChildrenByName("object");
		if (nodes != null) {
			ArrayList<XMLElement> list = nodes.list();
			for (int i = 0; i < list.size(); i++) {
				XMLElement objectNode = list.get(i);

				TMXObject object = new TMXObject();
				object.parse(objectNode);

				objects.add(object);
			}
		}
	}
}
