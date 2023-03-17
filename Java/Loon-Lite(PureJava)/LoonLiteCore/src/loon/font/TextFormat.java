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

import loon.utils.StringKeyValue;

public class TextFormat {

	public final Font font;

	public final boolean antialias;

	public TextFormat() {
		this(null);
	}

	public TextFormat(Font font) {
		this(font, true);
	}

	public TextFormat(Font font, boolean antialias) {
		this.font = font;
		this.antialias = antialias;
	}

	public TextFormat withFont(Font font) {
		return new TextFormat(font, this.antialias);
	}

	public TextFormat withFont(String name, Font.Style style, float size) {
		return withFont(new Font(name, style, size));
	}

	public TextFormat withFont(String name, float size) {
		return withFont(new Font(name, size));
	}

	public TextFormat withAntialias(boolean antialias) {
		return new TextFormat(this.font, antialias);
	}

	public Font getFont() {
		return font;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TextFormat) {
			TextFormat ofmt = (TextFormat) other;
			return (font == ofmt.font || (font != null && font.equals(ofmt.font))) && antialias == ofmt.antialias;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = (antialias ? 1 : 0);
		if (font != null)
			hash ^= font.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("TextFormat");
		builder.kv("font", font)
		.comma()
		.kv("antialias", antialias);
		return builder.toString();
	}
}
