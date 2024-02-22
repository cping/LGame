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

import loon.Json;
import loon.LSystem;
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
		properties = new ObjectMap<String, Object>();
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

	public TMXProperties remove(String key) {
		this.properties.remove(key);
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

	public void parse(Json.Array properties) {
		for (int p = 0; p < properties.length(); p++) {
			Json.Object property = properties.getObject(p);
			String name = property.getString("name", LSystem.EMPTY);
			String value = property.getString("value", LSystem.EMPTY);
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

	public void parse(XMLElement element) {
		TArray<XMLElement> properties = element.list("property");
		if (properties == null) {
			properties = element.getParent().list("property");
		}
		for (int p = 0; p < properties.size; p++) {
			XMLElement property = properties.get(p);
			String name = property.getAttribute("name", LSystem.EMPTY);
			String value = property.getAttribute("value", LSystem.EMPTY);
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
