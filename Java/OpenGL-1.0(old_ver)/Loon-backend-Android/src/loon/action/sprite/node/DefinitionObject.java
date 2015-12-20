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

import java.util.ArrayList;

import loon.core.geom.Vector2f;
import loon.utils.StringUtils;

public class DefinitionObject {

	private ArrayList<String> elementNames;

	public DefinitionObject parentDefinitionObject = null;

	public String fileName;

	public void childDefinitionObject(DefinitionObject childObject, String str) {
	}

	public void childDefinitionObjectDidFinishParsing(
			DefinitionObject childObject) {
	}

	public void childDefinitionObjectDidInit(DefinitionObject childObject) {
	}

	public void definitionObjectDidFinishParsing() {
	}

	public void definitionObjectDidInit() {
		this.elementNames = new ArrayList<String>();
	}

	public void definitionObjectDidReceiveString(String value) {
	}

	public DefinitionObject initWithParentObject(DefinitionObject parentObject) {
		this.parentDefinitionObject = parentObject;
		return this;
	}

	public static Vector2f strToVector2(String str) {
		String[] result = StringUtils.split(str, ",");
		String name = result[0];
		String value = result[1];
		return new Vector2f(Float.parseFloat(name), Float.parseFloat(value));
	}

	public void undefinedElementDidFinish(String elementName) {
		String result = this.elementNames.get(this.elementNames.size() - 1);
		if (result.equals(elementName)) {
			this.elementNames.remove(result);
		}
	}

	public void undefinedElementDidReceiveString(String str) {
	}

	public void undefinedElementDidStart(String elementName) {
		this.elementNames.add(elementName);
	}

	private boolean goto_flag = false;

	protected ArrayList<String> getResult(String v) {
		StringBuilder buffer = new StringBuilder();
		ArrayList<String> result = new ArrayList<String>(20);
		char[] chars = v.toCharArray();
		int size = chars.length;
		for (int i = 0; i < size; i++) {
			char ch = chars[i];
			if (ch == '/') {
				goto_flag = true;
			} else if ((ch == '\n' | ch == ';')) {
				String mess = buffer.toString();
				int len = mess.length();
				if (len > 0) {
					result.add(mess);
					buffer.delete(0, len);
				}
				goto_flag = false;
			} else if (!goto_flag && ch != 0x9) {
				buffer.append(ch);
			}
		}
		if (buffer.length() > 0) {
			result.add(buffer.toString());
		}
		buffer = null;
		return result;
	}
}
