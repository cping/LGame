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

import loon.LSysException;
import loon.LSystem;
import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;

/**
 * Loon内置的Font实现,当用户无自定义IFont时,默认使用此类实现文字渲染
 */
public class LFont extends FontTrans implements IFont {

	public static LFont getDefaultFont() {
		return newFont();
	}

	public static LFont newFont() {
		return newFont(20);
	}

	public static LFont newFont(int size) {
		return LFont.getFont(LSystem.getSystemGameFontName(), Style.PLAIN, size);
	}

	private final static String tmp = "H";

	private IntMap<Vector2f> fontSizes = new IntMap<Vector2f>(50);

	private PointI _offset = new PointI();

	private String lastText = tmp;

	private TextFormat textFormat = null;

	private TextLayout textLayout = null;

	private int _size = -1;

	private float _ascent = -1;

	private boolean useCache, closed;

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean u) {
		this.useCache = u;
	}

	LFont() {
		this(LSystem.getSystemGameFontName(), Style.PLAIN, 20, true);
	}

	LFont(String name, Style style, int size, boolean antialias) {
		if (StringUtils.isEmpty(name)) {
			throw new LSysException("Font name is null !");
		}
		this.textFormat = new TextFormat(new Font(name, style, MathUtils.max(1, size)), antialias);
		LSystem.pushFontPool(this);
	}

	public static LFont getFont(int size) {
		return LFont.getFont(LSystem.getSystemGameFontName(), size);
	}

	public static LFont getFont(String familyName, int size) {
		return new LFont(familyName, Style.PLAIN, size, true);
	}

	public static LFont getFont(String familyName, int styleType, int size) {
		Style style = Style.PLAIN;
		switch (styleType) {
		default:
		case 0:
			style = Style.PLAIN;
			break;
		case 1:
			style = Style.BOLD;
			break;
		case 2:
			style = Style.ITALIC;
			break;
		case 3:
			style = Style.BOLD_ITALIC;
			break;
		}
		return new LFont(familyName, style, size, true);
	}

	public static LFont getFont(String familyName, Style style, int size) {
		return new LFont(familyName, style, size, true);
	}

	public static LFont getFont(String familyName, Style style, int size, boolean antialias) {
		return new LFont(familyName, style, size, antialias);
	}

	public TextFormat getFormat() {
		return textFormat;
	}

	public TextLayout getTextLayout() {
		return textLayout;
	}

	@Override
	public void drawString(GLEx g, String msg, float tx, float ty) {
		drawString(g, msg, tx, ty, LColor.white);
	}

	@Override
	public void drawString(GLEx g, String msg, float tx, float ty, LColor c) {
		drawString(g, msg, tx, ty, 0, c);
	}

	@Override
	public void drawString(GLEx g, String msg, float tx, float ty, float angle, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (useCache) {
			LSTRDictionary.get().drawString(this, newMessage, _offset.x + tx, _offset.y + ty, angle, c);
		} else {
			LSTRDictionary.get().drawString(g, this, newMessage, _offset.x + tx, _offset.y + ty, angle, c);
		}
	}

	@Override
	public void drawString(GLEx g, String msg, float tx, float ty, float sx, float sy, float ax, float ay, float angle,
			LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (useCache) {
			LSTRDictionary.get().drawString(this, newMessage, _offset.x + tx, _offset.y + ty, sx, sy, ax, ay, angle, c);
		} else {
			LSTRDictionary.get().drawString(g, this, newMessage, _offset.x + tx, _offset.y + ty, sx, sy, ax, ay, angle,
					c);
		}
	}

	private void initLayout(String msg) {
		if (LSystem.base() == null) {
			return;
		}
		if (msg == null || textLayout == null || !msg.equals(lastText)) {
			textLayout = LSystem.base().graphics().layoutText(tmp, this.textFormat);
		}
	}

	@Override
	public int charWidth(char ch) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(String.valueOf(ch));
		return textLayout.bounds.width;
	}

	@Override
	public int stringWidth(String msg) {
		if (LSystem.base() == null || StringUtils.isNullOrEmpty(msg)) {
			return 0;
		}
		String newMessage = toMessage(msg);
		initLayout(newMessage);
		if (newMessage.indexOf(LSystem.LF) == -1) {
			return textLayout.stringWidth(newMessage);
		} else {
			StrBuilder sbr = new StrBuilder();
			int width = 0;
			for (int i = 0, size = newMessage.length(); i < size; i++) {
				char ch = newMessage.charAt(i);
				if (ch == LSystem.LF) {
					width = MathUtils.max(textLayout.stringWidth(sbr.toString()), width);
					sbr.setLength(0);
				} else {
					sbr.append(ch);
				}
			}

			return width;
		}
	}

	public int charHeight(char ch) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(String.valueOf(ch));
		return getHeight();
	}

	@Override
	public int stringHeight(String msg) {
		if (LSystem.base() == null || StringUtils.isNullOrEmpty(msg)) {
			return 0;
		}
		String newMessage = toMessage(msg);
		initLayout(newMessage);
		if (newMessage.indexOf(LSystem.LF) == -1) {
			return getHeight();
		} else {
			String[] list = StringUtils.split(newMessage, LSystem.LF);
			return list.length * getHeight();
		}
	}

	public boolean isBold() {
		return textFormat.font.style == Style.BOLD;
	}

	public boolean isItalic() {
		return textFormat.font.style == Style.ITALIC;
	}

	public boolean isPlain() {
		return textFormat.font.style == Style.PLAIN;
	}

	@Override
	public int getSize() {
		return this._size == -1 ? (int) textFormat.font.size : this._size;
	}

	public int getStyle() {
		return textFormat.font.style.ordinal();
	}

	public String getFontName() {
		return textFormat.font.name;
	}

	@Override
	public int getHeight() {
		initLayout(tmp);
		return MathUtils.max(getSize(), textLayout == null ? 0 : textLayout.bounds.height);
	}

	@Override
	public float getAscent() {
		initLayout(tmp);
		return this._ascent == -1 ? textLayout == null ? 0 : textLayout.ascent() : this._ascent;
	}

	public float getDescent() {
		initLayout(tmp);
		return textLayout.descent();
	}

	public float getLeading() {
		initLayout(tmp);
		return textLayout.leading();
	}

	private int fontHash = 1;

	@Override
	public int hashCode() {
		if (fontHash == 1) {
			fontHash = LSystem.unite(textFormat.font.name.charAt(0), fontHash);
			fontHash = LSystem.unite(textFormat.font.name.length(), fontHash);
			fontHash = LSystem.unite(textFormat.font.name.hashCode(), fontHash);
			fontHash = LSystem.unite(textFormat.font.style.ordinal(), fontHash);
			fontHash = LSystem.unite((int) textFormat.font.size, fontHash);
		}
		return fontHash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof LFont)) {
			return false;
		}
		LFont font = (LFont) o;
		if (this == font) {
			return true;
		}
		if (hashCode() == font.hashCode()) {
			return true;
		}
		if (font.textFormat == textFormat) {
			return true;
		}
		if (font.textFormat.font.name.equals(textFormat.font.name) && font.textFormat.font.size == textFormat.font.size
				&& font.textFormat.font.style.equals(textFormat.font.style)) {
			return true;
		}
		return false;
	}

	public Vector2f getOrigin(String msg) {
		return getOrigin(msg, true);
	}

	public Vector2f getOrigin(String msg, boolean filter) {
		String newMessage = msg;
		if (filter) {
			newMessage = toMessage(msg);
		}
		Vector2f result = fontSizes.get(newMessage);
		if (result == null) {
			result = new Vector2f(stringWidth(newMessage) / 2f, getHeight() / 2f);
			fontSizes.put(newMessage, result);
		}
		return result;
	}

	public TextLayout getLayoutText(String msg) {
		return getLayoutText(msg, true);
	}

	public TextLayout getLayoutText(String msg, boolean filter) {
		String newMessage = msg;
		if (filter) {
			newMessage = toMessage(msg);
		}
		return LSystem.base().graphics().layoutText(newMessage, this.textFormat);
	}

	@Override
	public String confineLength(String msg, int width) {
		String newMessage = toMessage(msg);
		int length = 0;
		for (int i = 0; i < newMessage.length(); i++) {
			length += stringWidth(String.valueOf(newMessage.charAt(i)));
			if (length >= width) {
				int pLength = stringWidth("...");
				while (length + pLength >= width && i >= 0) {
					length -= stringWidth(String.valueOf(newMessage.charAt(i)));
					i--;
				}
				newMessage = newMessage.substring(0, ++i) + "...";
				break;
			}
		}
		return newMessage;
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
	public String toString() {
		StringKeyValue builder = new StringKeyValue("LFont");
		builder.addValue(textFormat.toString());
		return builder.toString();
	}

	@Override
	public ITranslator getTranslator() {
		return _translator;
	}

	@Override
	public IFont setTranslator(ITranslator translator) {
		this._translator = translator;
		return this;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		closed = true;
		LSystem.popFontPool(this);
	}
}
