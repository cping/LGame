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

public class Font {

	public static enum Style {
		PLAIN, BOLD, ITALIC, BOLD_ITALIC
	}

	public final String name;

	public final Style style;

	public final float size;

	public Font(String name, Style style, float size) {
		this.name = name;
		this.style = style;
		this.size = size;
	}

	public Font(String name, float size) {
		this(name, Style.PLAIN, size);
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
		if (!(other instanceof Font)){
			return false;
		}
		Font ofont = (Font) other;
		return name.equals(ofont.name) && style == ofont.style
				&& size == ofont.size;
	}

	@Override
	public String toString() {
		return name + " " + style + " " + size + "pt";
	}
}
