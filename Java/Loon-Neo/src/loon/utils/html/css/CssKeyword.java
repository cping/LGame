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

import loon.utils.html.css.CssDimensions.Rect;

public class CssKeyword extends CssValue {

	public CssKeyword(String key) {
		super("Text");
		super.valueString = key;
	}

	public String getKeyword() {
		return this.valueString;
	}

	@Override
	public void setKeyword(CssKeyword keyword) {
	}

	@Override
	public void setColor(CssColor color) {
	}

	@Override
	public void setLength(CssLength length) {
	}

	@Override
	public float toPx() {
		return Rect.getFloat(this.valueString);
	}
}