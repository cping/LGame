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
package loon.font;

import loon.LSystem;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;

public final class Font {

	public final static Style toFontStyle(String name) {
		if (StringUtils.isEmpty(name)) {
			return Style.PLAIN;
		}
		String newName = name.trim().toLowerCase();
		if ("plain".equals(newName)) {
			return Style.PLAIN;
		} else if ("bold".equals(newName)) {
			return Style.BOLD;
		} else if ("italic".equals(newName)) {
			return Style.ITALIC;
		} else if ("bold_italic".equals(newName)) {
			return Style.BOLD_ITALIC;
		}
		return Style.PLAIN;
	}

	public static enum Style {
		PLAIN, BOLD, ITALIC, BOLD_ITALIC
	}

	public final String name;

	public final Style style;

	public final float size;

	public Font(String name, Style style, float size) {
		if (LSystem.isMobile()) {
			if (name != null) {
				String familyName = name.toLowerCase();
				if (familyName.equals("serif") || familyName.equals("timesroman")) {
					this.name = "serif";
				} else if (familyName.equals("sansserif") || familyName.equals("helvetica")) {
					this.name = "sans-serif";
				} else if (familyName.equals("monospaced") || familyName.equals("courier")
						|| familyName.equals("dialog") || familyName.equals("黑体")) {
					this.name = "monospace";
				} else {
					this.name = name;
				}
			} else {
				this.name = "monospace";
			}
		} else {
			this.name = name;
		}
		this.style = style;
		if (size % 2 == 0) {
			this.size = size;
		} else {
			this.size = size + 1;
		}
	}

	public Font(String name, float size) {
		this(name, Font.toFontStyle(LSystem.getSystemGameFontStyle()), size);
	}

	public Font derive(float size) {
		return new Font(name, style, size);
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ style.hashCode() ^ (int) size;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Font)) {
			return false;
		}
		Font ofont = (Font) other;
		return name.equals(ofont.name) && style == ofont.style && size == ofont.size;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Font");
		builder.kv("name", name).comma().kv("style", style).comma().kv("size", size + "pt");
		return builder.toString();
	}
}
