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
package loon.html5.gwt;

import loon.font.Font;

class GWTFont {

	public static final Font DEFAULT = new Font("sans-serif", Font.Style.PLAIN,
			12);

	public static String toCSS(Font font) {
		String name = font.name;
		if (!name.startsWith("\"") && name.contains(" ")) {
			name = '"' + name + '"';
		}
		String style = "";
		switch (font.style) {
		case BOLD:
			style = "bold";
			break;
		case ITALIC:
			style = "italic";
			break;
		case BOLD_ITALIC:
			style = "bold italic";
			break;
		default:
			break;
		}
		return style + " " + font.size + "px " + name;
	}
}
