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
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public abstract class TextLayout {

	public final String text;

	public final TextFormat format;

	public final RectBox bounds;

	public final Dimension size;

	public abstract float ascent();

	public abstract float descent();

	public abstract float leading();

	public abstract int stringWidth(String message);

	public abstract int getHeight();

	public abstract int charWidth(char ch);

	protected TextLayout(String text, TextFormat format, RectBox bounds, float height) {
		this.text = StringUtils.isEmpty(text) ? LSystem.EMPTY : text.trim();
		this.format = format;
		this.bounds = bounds;
		this.size = new Dimension(MathUtils.max(bounds.x(), 0) + bounds.width(), height);
	}

	protected void setBounds(float x, float y, float w, float h) {
		bounds.setBounds(x, y, w, h);
	}

	protected void setHeight(int height) {
		size.setSize(MathUtils.max(bounds.x(), 0) + bounds.width(), height);
	}

	public static String normalizeEOL(String text) {
		return text.replace(LSystem.NL, LSystem.LS).replace(LSystem.CR, LSystem.LF);
	}
}
