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
public class XMLDocument {

	private String header;

	private XMLElement root;

	public XMLDocument(XMLElement e) {
		this("<?xml version=\"1.0\" standalone=\"yes\" ?>\n", e);
	}

	public XMLElement getRoot() {
		return this.root;
	}

	public String getHeader() {
		return this.header;
	}

	@Override
	public String toString() {
		return this.header + this.root.toString();
	}

	public XMLDocument(String header, XMLElement root) {
		this.header = header;
		this.root = root;
	}

}
