package loon.action.map.tmx;

import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.ObjectMap.Keys;
import loon.utils.ObjectMap.Values;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXProperties {

	private ObjectMap<String, Object> properties;

	public TMXProperties() {
		properties = new ObjectMap<>();
	}

	public TMXProperties put(String key, Object value) {
		properties.put(key, value);
		return this;
	}

	public TMXProperties putAll(TMXProperties tmx) {
		ObjectMap<String, Object> data = tmx.properties;
		for (Entries<String, Object> key = data.iterator(); key.hasNext();) {
			Entry<String, Object> entry = key.next();
			properties.put(entry.key, entry.value);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) properties.get(key);
	}

	public <T> T get(String key, T defaultValue) {
		T value = get(key);
		return value == null ? defaultValue : value;
	}

	public boolean contains(String key) {
		return properties.containsKey(key);
	}

	public TMXProperties clear() {
		properties.clear();
		return this;
	}

	public ObjectMap<String, Object> getPropertiesMap() {
		return properties;
	}

	public Keys<String> getKeySet() {
		return getPropertiesMap().keys();
	}

	public Values<Object> getValues() {
		return getPropertiesMap().values();
	}

	public void parse(XMLElement element) {
		TArray<XMLElement> properties = element.list("property");
		if (properties == null) {
			properties = element.getParent().list("property");
		}
		for (int p = 0; p < properties.size; p++) {
			XMLElement property = properties.get(p);
			String name = property.getAttribute("name", "");
			String value = property.getAttribute("value", "");
			if (MathUtils.isNan(value)) {
				if (value.indexOf('.') != -1) {
					put(name, Float.parseFloat(value));
				} else {
					put(name, Integer.parseInt(value));
				}
			} else {
				put(name, value);
			}
		}
	}
}
