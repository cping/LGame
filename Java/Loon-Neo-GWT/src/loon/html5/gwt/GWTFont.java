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
import loon.utils.PathUtils;

final class GWTFont {

	public static final Font DEFAULT = new Font("sans-serif", Font.Style.PLAIN, 12);

	public static String toCSS(Font font) {
		String fontName = font.name;
		if (fontName == null) {
			fontName = "monospace";
		}
		final String familyName = fontName.trim().toLowerCase();
		final float size = font.size;
		final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
		if (("ttf".equals(ext) || "otf".equals(ext))) {
			fontName = PathUtils.getBaseFileName(fontName);
		} else {
			// 针对不同浏览器（主要是手机），有可能字体不支持，请自行引入font的css解决……
			if (Loon.self != null && (!Loon.self.isDesktop())) {
				if (fontName != null) {
					if (familyName.equals("serif") || familyName.equals("timesroman")) {
						fontName = "serif";
					} else if (familyName.equals("sansserif") || familyName.equals("helvetica")) {
						fontName = "sans-serif";
					} else if (familyName.equals("monospaced") || familyName.equals("courier")
							|| familyName.equals("dialog") || familyName.equals("黑体")) {
						fontName = "monospace";
					}
				}
			}
		}
		if (!fontName.startsWith("\"") && fontName.contains(" ")) {
			fontName = '"' + fontName + '"';
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
		return style + " " + size + "px " + fontName;
	}
}
