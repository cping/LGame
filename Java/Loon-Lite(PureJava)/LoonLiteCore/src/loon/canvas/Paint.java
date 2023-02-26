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
package loon.canvas;

import loon.LSystem;
import loon.font.IFont;
import loon.font.LFont;
import loon.utils.IntMap;

public class Paint {

	private static IntMap<LFont> PAINT_SIZES = new IntMap<LFont>(
			10);
	
	public enum Style {
		FILL, STROKE, FILL_AND_STROKE
	}

	private int textSize = 20;

	private IFont textFont;

	public int alpha = 255;

	public int strokeWidth = 1;

	public int color = LColor.DEF_COLOR;

	public Style style = Style.FILL;

	public Paint() {
		this.textFont = LSystem.getSystemGameFont();
	}

	public void setRGB(int r, int g, int b) {
		this.color = LColor.getRGB(r, g, b);
	}
	
	public void setARGB(int r, int g, int b, int a) {
		this.color = LColor.getARGB(r, g, b, a);
	}

	public void setColor(int c) {
		this.color = c;
	}

	public void setColor(LColor c) {
		this.color = c.getARGB();
	}

	public void setStyle(Style s) {
		this.style = s;
	}

	public void setStrokeWidth(float s) {
		this.strokeWidth = (int) s;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getColor() {
		return color;
	}

	public Style getStyle() {
		return style;
	}

	public void setTextSize(int size) {
		LFont font = PAINT_SIZES.get(size);
		if (font == null) {
			font = LFont.getFont(size);
			PAINT_SIZES.put(size, font);
		}
		this.textFont = font;
		this.textSize = size;
	}

	public int getTextSize() {
		return this.textSize;
	}

	public IFont getFont() {
		return textFont;
	}

	public IFont setFont(LFont font) {
		this.textFont = font;
		if (font != null) {
			this.textSize = font.getSize();
		}
		return this.textFont;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int a) {
		this.alpha = a;
		this.color = (alpha << 24) | (color & 0xFFFFFF);
	}
}
