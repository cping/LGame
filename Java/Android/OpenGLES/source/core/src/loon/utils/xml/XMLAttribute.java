package loon.utils.xml;


/**
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class XMLAttribute {

	private String name;

	private String value;

	XMLElement element;

	XMLAttribute(String n, String v) {
		this.name = n;
		this.value = v;
	}
	
	public XMLElement getElement() {
		return element;
	}

	public String getValue() {
		return this.value;
	}

	public int getIntValue() {
		try {
			return Integer.parseInt(this.value);
		} catch (Exception ex) {
			throw new RuntimeException("Attribute '" + this.name
					+ "' has value '" + this.value
					+ "' which is not an integer !");
		}
	}

	public float getFloatValue() {
		try {
			return Float.parseFloat(this.value);
		} catch (Exception ex) {
			throw new RuntimeException("Attribute '" + this.name
					+ "' has value '" + this.value
					+ "' which is not an float !");
		}
	}

	public double getDoubleValue() {
		try {
			return Double.parseDouble(this.value);
		} catch (Exception ex) {
			throw new RuntimeException("Attribute '" + this.name
					+ "' has value '" + this.value
					+ "' which is not an double !");
		}
	}

	public boolean getBoolValue() {
		if (value == null) {
			return false;
		}
		return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
	}

	public String getName() {
		return this.name;
	}

}
