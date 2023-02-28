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

import loon.utils.StringUtils;

public class CssSelectorObject {

	public CssSelector selector;

	public String getSelectorTempString() {

		CssSelectorTemp temp = new CssSelectorTemp();

		if (selector != null) {
			temp.id = (StringUtils.isEmpty(selector.id) ? 0 : 1);
			temp.clazz = selector.classNames.size();
			temp.tag = (StringUtils.isEmpty(selector.tagName) ? 0 : 1);
		}

		return temp.get();
	}

	public CssSelectorTemp getSelectorTemp() {

		CssSelectorTemp temp = new CssSelectorTemp();

		if (selector != null & selector.id != null) {
			temp.id = (StringUtils.isEmpty(selector.id) ? 0 : 1);
		} else {
			temp.id = 0;
		}

		temp.clazz = selector.classNames.size();

		if (selector != null & selector.tagName != null) {
			temp.tag = (StringUtils.isEmpty(selector.tagName) ? 0 : 1);
		} else {
			temp.tag = 0;
		}

		return temp;
	}

	public void setSelector(CssSelector s) {
		this.selector = s;
	}
}
