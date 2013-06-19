package loon.utils.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import loon.core.LRelease;
import loon.net.Base64Coder;

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
public class XMLElement implements LRelease {

	private String name;

	private HashMap<String, XMLAttribute> attributes;

	private ArrayList<Object> contents;

	private XMLElement parent;

	XMLElement(String name) {
		this.attributes = new HashMap<String, XMLAttribute>();
		this.contents = new ArrayList<Object>();
		this.name = name;
	}

	public byte[] readContentBinHex() {
		byte[] buffer = new byte[0x1000];
		readBinHex(buffer, 0, 0x1000);
		return buffer;
	}

	public int readBinHex(byte[] buffer, int offset, int length) {
		if (offset < 0) {
			throw new IllegalArgumentException(
					"Offset must be non-negative integer.");
		} else if (length < 0) {
			throw new IllegalArgumentException(
					"Length must be non-negative integer.");
		} else if (buffer.length < offset + length) {
			throw new IllegalArgumentException(
					"buffer length is smaller than the sum of offset and length.");
		}
		if (length == 0) {
			return 0;
		}
		char[] chars = new char[length * 2];
		int charsLength = readValueChunk(chars, 0, length * 2);
		return Base64Coder.fromBinHexString(chars, offset, charsLength, buffer);
	}

	private int readValueChunk(char[] buffer, int offset, int length) {
		StringBuffer textCache = new StringBuffer(length);
		for (Iterator<?> e = elements(); e.hasNext();) {
			textCache.append(e.next().toString());
		}
		int min = textCache.length();
		if (min > length) {
			min = length;
		}
		String str = textCache.substring(0, min);
		System.arraycopy(str.toCharArray(), offset, buffer, 0, length);
		if (min < length) {
			return min + readValueChunk(buffer, offset + min, length - min);
		} else {
			return min;
		}
	}

	public XMLAttribute getAttribute(String name) {
		if (!this.attributes.containsKey(name))
			throw new Error("Unknown attribute name '" + name
					+ "' in element '" + this.name + "' !");
		return  this.attributes.get(name);
	}

	public String getAttribute(String name, String v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return ( this.attributes.get(name)).getValue();
	}

	public int getIntAttribute(String name, int v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return ( this.attributes.get(name)).getIntValue();
	}

	public float getFloatAttribute(String name, float v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return ( this.attributes.get(name)).getFloatValue();
	}

	public double getDoubleAttribute(String name, double v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return ( this.attributes.get(name)).getDoubleValue();
	}

	public boolean getBoolAttribute(String name, boolean v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return ( this.attributes.get(name)).getBoolValue();
	}

	public HashMap<String, XMLAttribute> getAttributes() {
		return this.attributes;
	}

	public boolean hasAttribute(String name) {
		return this.attributes.containsKey(name);
	}

	public Iterator<?> elements() {
		return this.contents.iterator();
	}

	public ArrayList<XMLElement> list() {
		ArrayList<XMLElement> lists = new ArrayList<XMLElement>(contents.size());
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if (!(o instanceof XMLElement)) {
				continue;
			}
			lists.add((XMLElement) o);
		}
		return lists;
	}
	
	public XMLElement getFirstChild() {
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if (!(o instanceof XMLElement)) {
				continue;
			}
			return (XMLElement) o;
		}
		return null;
	}

	public XMLElement getChildrenByName(String name) {
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement))
					|| (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			return (XMLElement) o;
		}
		return null;
	}

	public ArrayList<XMLElement> find(String name) {
		ArrayList<XMLElement> v = new ArrayList<XMLElement>();
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement))) {
				continue;
			}
			XMLElement ele = (XMLElement) o;
			if (!ele.equals(ele.getName())) {
				Iterator<?> it = ele.elements(name);
				for (; it.hasNext();) {
					XMLElement child = (XMLElement) it.next();
					child.parent = ele;
					v.add(child);
				}
				continue;
			} else if (ele.equals(ele.getName())) {
				v.add((XMLElement) o);
				continue;
			}
		}
		return v;
	}

	public ArrayList<XMLElement> list(String name) {
		ArrayList<XMLElement> v = new ArrayList<XMLElement>();
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement))
					|| (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			v.add((XMLElement) o);
		}
		return v;
	}

	public Iterator<?> elements(String name) {
		ArrayList<Object> v = new ArrayList<Object>();
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement))
					|| (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			v.add(o);
		}
		return v.iterator();
	}

	public void addAllTo(ArrayList<XMLElement> list) {
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement))
					|| (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			list.add((XMLElement) o);
		}
	}

	public String getName() {
		return this.name;
	}

	public XMLElement getParent() {
		return this.parent;
	}

	public String getContents() {
		StringBuffer sbr = new StringBuffer(1024);
		for (Iterator<?> e = elements(); e.hasNext();) {
			sbr.append(e.next().toString());
		}
		return sbr.toString();
	}

	@Override
	public String toString() {
		Set<?> set = this.attributes.keySet();
		String str1 = "<" + this.name;
		for (Iterator<?> it = set.iterator(); it.hasNext();) {
			String str2 = (String) it.next();
			str1 = str1 + " " + str2 + " = \"" + getAttribute(str2).getValue()
					+ "\"";
		}
		str1 = str1 + ">";
		str1 = str1 + getContents();
		str1 = str1 + "</" + this.name + ">";
		return str1;
	}

	XMLAttribute addAttribute(String name, String value) {
		XMLAttribute attribute = new XMLAttribute(name, value);
		this.attributes.put(name, attribute);
		return attribute;
	}

	void addContents(Object o) {
		this.contents.add(o);
	}

	@Override
	public void dispose() {
		if (attributes != null) {
			attributes.clear();
			attributes = null;
		}
		if (contents != null) {
			contents.clear();
			contents = null;
		}
	}

}
