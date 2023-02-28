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
package loon.utils.html.css;

import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.html.HtmlElement;

public class CssStyleNode {

	protected HtmlElement node;

	protected ObjectMap<String, CssValue> values;

	protected TArray<CssStyleNode> children;

	public CssDisplay getValueOfDisplay() {
		CssKeyword kw = (CssKeyword) values.get("display");
		if (kw == null) {
			return CssDisplay.Inline;
		}
		if (kw.getKeyword().equals("block")) {
			return CssDisplay.Block;
		} else if (kw.getKeyword().equals("none")) {
			return CssDisplay.None;
		} else {
			return CssDisplay.Inline;
		}
	}

	public CssValue getValueOf(String name) {
		return values.get(name);
	}

	public CssValue getCssValueOf(String name, String defValue) {
		CssValue v = values.get(name);
		if (v != null) {
			return v;
		}
		return new CssKeyword(defValue);
	}

	public CssValue addValue(String key, CssValue value) {
		return values.put(key, value);
	}

	public CssValue removeValue(String key) {
		return values.remove(key);
	}

	public CssValue find(CssValue defualtValue, String... args) {
		for (int i = 0; i < args.length; i++) {
			String name = args[i];
			CssValue v = values.get(name);
			if (v != null)
				return v;
		}
		return defualtValue;
	}

	public int childCount() {
		return children.size;
	}

}
