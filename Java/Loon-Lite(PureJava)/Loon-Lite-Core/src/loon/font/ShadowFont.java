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

import loon.canvas.LColor;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;

/**
 * 一个IFont的阴影字体实现,内部是LFont
 */
public class ShadowFont implements IFont {

	private boolean withShadow = false, closed = false;
	private LColor shadowColor = new LColor(0f, 0f, 0f, 1f);
	private float shadowAlpha = 1f;
	private LSTRFont strfont;

	private int _size = -1;
	private float _ascent = -1;

	private PointI _offset = new PointI();

	public LSTRFont getStrFont() {
		return strfont;
	}

	public LFont getFont() {
		return strfont.getFont();
	}

	public ShadowFont(LFont font, String[] messages, String append, boolean shadow) {
		if (append != null) {
			int size = messages.length + 1;
			String[] dest = new String[size];
			dest[size - 1] = append;
			System.arraycopy(messages, 0, dest, 0, messages.length);
			this.strfont = new LSTRFont(font, dest, true);
		} else {
			this.strfont = new LSTRFont(font, messages, true);
		}
		this.withShadow = shadow;
	}

	public ShadowFont(LFont font, String message, String append, boolean shadow) {
		this.strfont = new LSTRFont(font, message + append, true);
		this.withShadow = shadow;
	}

	public void drawString(String text, float x, float y, LColor color) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * color.a);
			strfont.drawString(text, x + _offset.x, y + _offset.y, shadowColor);
		}
		strfont.drawString(text, x + _offset.x, y + _offset.y, color);
	}

	@Override
	public void drawString(GLEx g, String text, float x, float y) {
		drawString(g, text, x, y, LColor.white);
	}

	@Override
	public void drawString(GLEx g, String text, float x, float y, LColor color) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * color.a);
			strfont.drawString(g, text, x + _offset.x, y + _offset.y, shadowColor);
		}
		strfont.drawString(g, text, x + _offset.x, y + _offset.y, color);
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y, float rotation, LColor c) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * c.a);
			strfont.drawString(g, string, x + _offset.x, y + _offset.y, rotation, shadowColor);
		}
		strfont.drawString(g, string, x + _offset.x, y + _offset.y, rotation, c);
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y, float sx, float sy, float ax, float ay,
			float rotation, LColor c) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * c.a);
			strfont.drawString(g, x + _offset.x, y + _offset.y, sx, sy, ax, ay, rotation, string, shadowColor);
		}
		strfont.drawString(g, x + _offset.x, y + _offset.y, sx, sy, ax, ay, rotation, string, c);
	}

	public void setShadowColor(LColor color) {
		this.shadowColor = color;
	}

	public void setShadowAlpha(float alpha) {
		this.shadowAlpha = alpha;
	}

	public void setShadow(boolean shadow) {
		this.withShadow = shadow;
	}

	public boolean isShadowEffect() {
		return this.withShadow;
	}

	public LColor getShadowColor() {
		return this.shadowColor;
	}

	public boolean isWithShadow() {
		return withShadow;
	}

	public void setWithShadow(boolean withShadow) {
		this.withShadow = withShadow;
	}

	public float getShadowAlpha() {
		return shadowAlpha;
	}

	@Override
	public int stringWidth(String width) {
		return strfont.getWidth(width);
	}

	@Override
	public int stringHeight(String height) {
		return strfont.getHeight(height);
	}

	@Override
	public int getHeight() {
		return strfont.getHeight();
	}

	@Override
	public float getAscent() {
		return this._ascent == -1 ? strfont.getAscent() : this._ascent;
	}

	@Override
	public String confineLength(String s, int width) {
		int length = 0;
		for (int i = 0; i < s.length(); i++) {
			length += stringWidth(String.valueOf(s.charAt(i)));
			if (length >= width) {
				int pLength = stringWidth("...");
				while (length + pLength >= width && i >= 0) {
					length -= stringWidth(String.valueOf(s.charAt(i)));
					i--;
				}
				s = s.substring(0, ++i) + "...";
				break;
			}
		}
		return s;
	}

	@Override
	public int getSize() {
		return this._size == -1 ? strfont.getSize() : this._size;
	}

	@Override
	public PointI getOffset() {
		return _offset;
	}

	@Override
	public void setOffset(PointI val) {
		_offset.set(val);
	}

	@Override
	public void setOffsetX(int x) {
		_offset.x = x;
	}

	@Override
	public void setOffsetY(int y) {
		_offset.y = y;
	}

	@Override
	public void setAssent(float assent) {
		this._ascent = assent;
	}

	@Override
	public void setSize(int size) {
		this._size = size;
	}

	@Override
	public int charWidth(char c) {
		return strfont.charWidth(c);
	}

	@Override
	public void close() {
		this.closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public String getFontName() {
		return strfont.getFontName();
	}
	
	@Override
	public String toString() {
		return getFont().toString();
	}

}
