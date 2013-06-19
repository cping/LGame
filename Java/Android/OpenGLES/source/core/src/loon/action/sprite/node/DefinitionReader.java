/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.io.InputStream;
import java.util.HashMap;

import loon.utils.xml.XMLAttribute;
import loon.utils.xml.XMLComment;
import loon.utils.xml.XMLData;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLListener;
import loon.utils.xml.XMLParser;
import loon.utils.xml.XMLProcessing;

public class DefinitionReader {

	private Class<?> curClass;

	public final static String flag_source = "src";

	public final static String flag_type = "type";

	protected String _classType;

	protected String _source;

	public DefinitionObject currentDefinitionObject = null;

	private boolean isCurrentElementDefined = false;

	private static DefinitionReader instance;

	private final static HashMap<String, Class<?>> change = new HashMap<String, Class<?>>(
			10);

	static {
		change.put("image", DefImage.class);
		change.put("animation", DefAnimation.class);
	}

	public static DefinitionReader get() {
		synchronized (DefinitionReader.class) {
			if (instance == null) {
				instance = new DefinitionReader();
			}
			return instance;
		}
	}

	private DefinitionReader() {

	}

	protected String _path;

	public String getCurrentPath() {
		return this._path;
	}

	private void stopElement(String name) {
		Class<?> clazz = change.get(name.toLowerCase());
		if (clazz != null && clazz.equals(this.curClass)) {
			this.currentDefinitionObject.definitionObjectDidFinishParsing();
			if (this.currentDefinitionObject.parentDefinitionObject != null) {
				this.currentDefinitionObject.parentDefinitionObject
						.childDefinitionObjectDidFinishParsing(this.currentDefinitionObject);
			}
			this.currentDefinitionObject = this.currentDefinitionObject.parentDefinitionObject;
			if (this.currentDefinitionObject != null) {
				this.curClass = this.currentDefinitionObject.getClass();
				this.isCurrentElementDefined = true;
			} else {
				this.curClass = null;
				this.isCurrentElementDefined = false;
			}
		} else if (this.currentDefinitionObject != null) {
			this.currentDefinitionObject.undefinedElementDidFinish(name);
		}
	}

	private void parseContent(String str) {
		if (this.isCurrentElementDefined) {
			this.currentDefinitionObject.definitionObjectDidReceiveString(str);
		} else {
			this.currentDefinitionObject.undefinedElementDidReceiveString(str);
		}
	}

	private class DefListener implements XMLListener {

		@Override
		public void addHeader(int line, XMLProcessing xp) {

		}

		@Override
		public void addData(int line, XMLData data) {
			if (data != null) {
				String content = data.toString().trim();
				if (!"".equals(content)) {
					parseContent(content);
				}
			}
		}

		@Override
		public void addComment(int line, XMLComment c) {

		}

		@Override
		public void addAttribute(int line, XMLAttribute a) {
			if (a != null) {
				XMLElement ele = a.getElement();
				if (flag_source.equalsIgnoreCase(a.getName())) {
					_source = a.getValue();
				} else if (flag_type.equalsIgnoreCase(a.getName())) {
					_classType = a.getValue();
				} else if (ele != null) {
					_classType = ele.getName();
				}
			}
		}

		@Override
		public void addElement(int line, XMLElement e) {
			startElement(e != null ? e.getName() : _classType);
		}

		@Override
		public void endElement(int line, XMLElement e) {
			stopElement(e != null ? e.getName() : _classType);
		}
	}

	private final DefListener listener = new DefListener();

	public void load(String resName) {
		this._path = resName;
		this._classType = null;
		this._source = null;
		this.currentDefinitionObject = null;
		this.isCurrentElementDefined = false;
		XMLParser.parse(resName, listener);
	}

	public void load(InputStream res) {
		this._path = null;
		this._classType = null;
		this._source = null;
		this.currentDefinitionObject = null;
		this.isCurrentElementDefined = false;
		XMLParser.parse(res, listener);
	}

	private void startElement(String name) {
		Class<?> clazz = change.get(name.toLowerCase());
		if (clazz != null) {
			DefinitionObject childObject = null;
			try {
				childObject = (DefinitionObject) clazz.newInstance();
				if (_source != null) {
					childObject.fileName = _source;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.isCurrentElementDefined) {
				childObject.initWithParentObject(this.currentDefinitionObject);
			}
			childObject.definitionObjectDidInit();
			if (childObject.parentDefinitionObject != null) {
				childObject.parentDefinitionObject
						.childDefinitionObjectDidInit(childObject);
			}
			this.curClass = clazz;
			this.currentDefinitionObject = childObject;
			this.isCurrentElementDefined = true;
		} else {
			this.isCurrentElementDefined = false;
			if (this.currentDefinitionObject != null) {
				this.currentDefinitionObject.undefinedElementDidStart(name);
			}
		}
	}

}
