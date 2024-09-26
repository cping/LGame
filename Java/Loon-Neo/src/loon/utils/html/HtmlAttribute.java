/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.utils.html;

import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.html.css.CssParser;
import loon.utils.html.css.CssStyleSheet;

public class HtmlAttribute {

	private String _name;

	private String _value;

	protected HtmlElement _element;

	HtmlAttribute(String n, String v) {
		this._name = n;
		this._value = v;
	}

	public HtmlElement getElement() {
		return _element;
	}

	public boolean isStyle() {
		return "style".equals(_name);
	}

	public CssStyleSheet getStyleSheet() {
		if (isStyle()) {
			CssStyleSheet sheet = CssParser.loadText(this._value);
			return sheet;
		}
		return new CssStyleSheet();
	}

	public String getValue() {
		return this._value;
	}

	public int getIntValue() {
		if (!MathUtils.isNan(this._value)) {
			return 0;
		}
		try {
			return (int) Float.parseFloat(this._value);
		} catch (Throwable ex) {
			throw new LSysException(
					"Attribute '" + this._name + "' has value '" + this._value + "' which is not an integer !");
		}
	}

	public float getFloatValue() {
		if (!MathUtils.isNan(this._value)) {
			return 0;
		}
		try {
			return Float.parseFloat(this._value);
		} catch (Throwable ex) {
			throw new LSysException(
					"Attribute '" + this._name + "' has value '" + this._value + "' which is not an float !");
		}
	}

	public double getDoubleValue() {
		if (!MathUtils.isNan(this._value)) {
			return 0;
		}
		try {
			if (this._value.indexOf('b') != -1) {
				this._value = _value.replace("b", LSystem.EMPTY);
			}
			return Double.parseDouble(this._value);
		} catch (Throwable ex) {
			throw new LSysException(
					"Attribute '" + this._name + "' has value '" + this._value + "' which is not an double !");
		}
	}

	public boolean getBoolValue() {
		if (!StringUtils.isBoolean(this._value)) {
			return false;
		}
		if (_value == null) {
			return false;
		}
		return StringUtils.toBoolean(_value);
	}

	public String getName() {
		return this._name;
	}

}
