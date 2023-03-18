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
package loon.action.map.items;

import loon.utils.MathUtils;

public class Attribute {

	private String _name;

	private Object _attribute;

	public Attribute(String name, Object attribute) {
		this._name = name;
		this._attribute = attribute;
	}

	public String getName() {
		return this._name;
	}

	public Attribute setName(String name) {
		this._name = name;
		return this;
	}

	public Attribute setAttribute(Object attribute) {
		this._attribute = attribute;
		return this;
	}
	
	public Object getAttribute() {
		return this._attribute;
	}

	public float getAttributeFloat() {
		String result = String.valueOf(this._attribute);
		if (MathUtils.isNan(result)) {
			return Float.parseFloat(result);
		}
		return -1f;
	}

	public int getAttributeInt() {
		String result = String.valueOf(this._attribute);
		if (MathUtils.isNan(result)) {
			return Integer.parseInt(result);
		}
		return -1;
	}

	public Attribute cpy(Attribute other) {
		this._name = other._name;
		this._attribute = other._attribute;
		return this;
	}
}
