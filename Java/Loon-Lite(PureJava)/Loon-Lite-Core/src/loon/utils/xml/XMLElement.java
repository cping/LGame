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
package loon.utils.xml;

import java.util.Iterator;

import loon.LSysException;
import loon.LSystem;
import loon.utils.Base64Coder;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class XMLElement {

	private String name;

	private ObjectMap<String, XMLAttribute> attributes;

	private TArray<Object> contents;

	private XMLElement parent;

	public XMLElement() {
		this(null);
	}

	public XMLElement(String name) {
		this.attributes = new ObjectMap<String, XMLAttribute>();
		this.contents = new TArray<Object>();
		this.name = name;
	}

	public byte[] readContentBinHex() {
		byte[] buffer = new byte[0x1000];
		readBinHex(buffer, 0, 0x1000);
		return buffer;
	}

	public int readBinHex(byte[] buffer, int offset, int length) {
		if (offset < 0) {
			throw new LSysException("Offset must be non-negative integer.");
		} else if (length < 0) {
			throw new LSysException("Length must be non-negative integer.");
		} else if (buffer.length < offset + length) {
			throw new LSysException("buffer length is smaller than the sum of offset and length.");
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
			throw new LSysException("Unknown attribute name '" + name + "' in element '" + this.name + "' !");
		return this.attributes.get(name);
	}

	public String getAttribute(String name, String v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return (this.attributes.get(name)).getValue();
	}

	public int getIntAttribute(String name, int v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return (this.attributes.get(name)).getIntValue();
	}

	public float getFloatAttribute(String name, float v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return (this.attributes.get(name)).getFloatValue();
	}

	public double getDoubleAttribute(String name, double v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return (this.attributes.get(name)).getDoubleValue();
	}

	public boolean getBoolAttribute(String name, boolean v) {
		if (!this.attributes.containsKey(name)) {
			return v;
		}
		return (this.attributes.get(name)).getBoolValue();
	}

	public ObjectMap<String, XMLAttribute> getAttributes() {
		return this.attributes;
	}

	public boolean hasAttribute(String name) {
		return this.attributes.containsKey(name);
	}

	public Iterator<Object> elements() {
		return this.contents.iterator();
	}
	
	public TArray<XMLElement> list() {
		TArray<XMLElement> lists = new TArray<XMLElement>(contents.size);
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
			if ((!(o instanceof XMLElement)) || (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			return (XMLElement) o;
		}
		return null;
	}

	public TArray<XMLElement> find(String name) {
		TArray<XMLElement> v = new TArray<XMLElement>();
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

	public TArray<XMLElement> list(String name) {
		TArray<XMLElement> v = new TArray<XMLElement>();
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement)) || (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			v.add((XMLElement) o);
		}
		return v;
	}

	public Iterator<?> elements(String name) {
		TArray<Object> v = new TArray<Object>();
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement)) || (!((XMLElement) o).getName().equals(name))) {
				continue;
			}
			v.add(o);
		}
		return v.iterator();
	}

	public void addAllTo(TArray<XMLElement> list) {
		for (Iterator<?> e = elements(); e.hasNext();) {
			Object o = e.next();
			if ((!(o instanceof XMLElement)) || (!((XMLElement) o).getName().equals(name))) {
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

	public XMLAttribute addAttribute(String name, int value) {
		XMLAttribute attribute = new XMLAttribute(name, String.valueOf(value));
		this.attributes.put(name, attribute);
		return attribute;
	}

	public XMLAttribute addAttribute(String name, String value) {
		XMLAttribute attribute = new XMLAttribute(name, value);
		this.attributes.put(name, attribute);
		return attribute;
	}

	public XMLElement addContents(Object o) {
		this.contents.add(o);
		return this;
	}

	@Override
	public String toString() {
		StringBuffer builder = new StringBuffer();
		if (this.name == null) {
			builder.append(LSystem.EMPTY);
		} else {
			builder.append('<');
			builder.append(this.name);
		}
		for (String str2 : attributes.keys()) {
			builder.append(' ');
			builder.append(str2);
			builder.append(" = \"");
			builder.append(getAttribute(str2).getValue());
			builder.append("\"");
		}
		if (this.name != null) {
			builder.append(">\n");
		}
		builder.append(getContents());
		if (this.name != null) {
			builder.append("</" + this.name + ">\n");
		}
		return builder.toString();
	}

	public XMLElement dispose() {
		if (attributes != null) {
			attributes.clear();
			attributes = null;
		}
		if (contents != null) {
			contents.clear();
			contents = null;
		}
		return this;
	}

}
