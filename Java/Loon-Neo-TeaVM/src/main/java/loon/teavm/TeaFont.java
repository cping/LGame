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
package loon.teavm;

import loon.font.Font;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class TeaFont {

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
		return style + " " + size + "px " + getFontName(fontName);
	}

	protected final static String getFontName(final String fontName) {
		if (!StringUtils.isChinaLanguage(fontName)) {
			return fontName;
		}
		if ("微软雅黑".equals(fontName)) {
			return "Microsoft YaHei";
		} else if ("宋体".equals(fontName)) {
			return "SimSun";
		} else if ("黑体".equals(fontName)) {
			return "SimHei";
		} else if ("仿宋".equals(fontName)) {
			return "FangSong";
		} else if ("楷体".equals(fontName)) {
			return "KaiTi";
		} else if ("隶书".equals(fontName)) {
			return "LiSu";
		} else if ("幼圆".equals(fontName)) {
			return "YouYuan";
		} else if ("华文细黑".equals(fontName)) {
			return "STXihei";
		} else if ("华文华文楷体黑".equals(fontName)) {
			return "STKaiti";
		} else if ("华文宋体".equals(fontName)) {
			return "STSong";
		} else if ("华文中宋".equals(fontName)) {
			return "STZhongsong";
		} else if ("华文仿宋".equals(fontName)) {
			return "STFangsong";
		} else if ("方正舒体".equals(fontName)) {
			return "FZShuTi";
		} else if ("方正姚体".equals(fontName)) {
			return "FZYaoti";
		} else if ("华文彩云".equals(fontName)) {
			return "STCaiyun";
		} else if ("华文琥珀".equals(fontName)) {
			return "STHupo";
		} else if ("华文隶书".equals(fontName)) {
			return "STLiti";
		} else if ("华文行楷".equals(fontName)) {
			return "STXingkai";
		} else if ("华文新魏".equals(fontName)) {
			return "STXinwei";
		} else {
			return "sans-serif";
		}
	}
}
