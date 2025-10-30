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
package loon.opengl;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.FontTrans;
import loon.font.IFont;
import loon.font.ITranslator;
import loon.font.LFont;
import loon.geom.Affine2f;
import loon.geom.PointI;
import loon.utils.CharArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public final class LSTRFont extends FontTrans implements IFont, LRelease {

	// 每次渲染图像到纹理时，同时追加一些常用非中文标记上去，以避免LSTRFont反复重构纹理(有字符重复检测,用户使用中已有下列字符时则不会重复添加)
	private final static String ADDED = "0123456789iagbfhkdnocpqrstumwvlxyzjeJBIAFGHKCDOMSNPQTUVWXYZLRE=:.,+!?@#$&%^*(-)~{}\"'\\/<>[▼│_]▲【】◆…→↓，：。～？！＃＄％＆＇（）＊＋．＠［＼└／］＾＿｛｜｝├─｀＜＞";

	public final static boolean isAllInBaseCharsPool(String c) {
		if (StringUtils.isNullOrEmpty(c)) {
			return false;
		}
		final int charsCount = c.length();
		int idx = 0;
		for (int j = 0; j < charsCount; j++) {
			for (int i = 0; i < ADDED.length(); i++) {
				final char ch = c.charAt(j);
				if (ADDED.charAt(i) == ch || ch == LSystem.SPACE) {
					idx++;
					break;
				}
			}
		}
		return idx == charsCount;
	}

	public final static String getBaseCharsPool() {
		return ADDED;
	}

	/*
	 * 获得一个默认的LSTRFont.
	 *
	 * 比如:
	 *
	 * 游戏全局使用默认LSTRFont(除log字体外,log字体需要设置setSystemLogFont)
	 *
	 * LSystem.setSystemGameFont(LSTRFont.getDefaultFont());
	 *
	 */
	public final static LSTRFont getDefaultFont() {
		return getFont(20);
	}

	public final static LSTRFont getFont(int size) {
		return new LSTRFont(LFont.getFont(size));
	}

	private final char newLineFlag = LSystem.LF;

	private final PointI _offset = new PointI();

	private boolean _outBounds = false;

	private int _drawLimit = 0;

	private int advanceSpace = 8;

	private boolean isasyn;

	private float offsetX = 1, offsetY = 1;

	private String text;

	private LFont font;

	private float ascent;

	private int pixelColor = LColor.DEF_COLOR;

	private int pixelFontSize = 0, fontSize = 0;

	private int fontHeight = 0;

	private float fontScale = 1f;

	private CharArray _chars;

	public LSTRFont(LFont font) {
		this(font, LSystem.EMPTY);
	}

	public LSTRFont(LFont font, String message) {
		this(font, (StringUtils.isNullOrEmpty(message) ? LSystem.EMPTY : message).toCharArray());
	}

	public LSTRFont(LFont font, char[] charMessage) {
		CharSequence chs = StringUtils.unificationChars(charMessage);
		this._chars = new CharArray(chs.length());
		this.font = font;
		this.pixelFontSize = font.getSize();
		this.fontHeight = font.getHeight();
		this.ascent = font.getAscent();
		this.advanceSpace = MathUtils.max(1, pixelFontSize / 2);
		this._drawLimit = 0;
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y) {
		drawString(gl, x, y, 1f, 1f, 0, chars, LColor.white);
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y, LColor color) {
		drawString(gl, x, y, 1f, 1f, 0, chars, color);
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y, float rotation, LColor color) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, color);
	}

	public void drawString(GLEx gl, String chars, float x, float y, float rotation) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, LColor.white);
	}

	public void drawString(GLEx gl, String chars, float x, float y, float sx, float sy, float rotation, LColor c) {
		drawString(gl, x, y, sx, sy, rotation, chars, c);
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy, float rotation, String chars, LColor c) {
		drawString(gl, x, y, sx, sy, 0, 0, rotation, chars, c, 0, chars.length());
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy, float ax, float ay, float rotation,
			String chars, LColor c) {
		drawString(gl, x, y, sx, sy, ax, ay, rotation, chars, c, 0, chars.length());
	}

	@Override
	public void drawString(GLEx gl, String chars, float x, float y, float sx, float sy, float ax, float ay,
			float rotation, LColor c) {
		drawString(gl, x, y, sx, sy, ax, ay, rotation, chars, c, 0, chars.length());
	}

	private void drawString(GLEx gl, float mx, float my, float sx, float sy, float ax, float ay, float rotation,
			String msg, LColor c, int startIndex, int endIndex) {
		if (StringUtils.isNullOrEmpty(msg)) {
			return;
		}
		String newMessage = toMessage(msg);
		if (checkEndIndexUpdate(endIndex, msg, newMessage)) {
			endIndex = newMessage.length();
		}
		final float nsx = sx * fontScale;
		final float nsy = sy * fontScale;
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		int old = gl.color();
		final boolean anchor = ax != 0 || ay != 0;
		final boolean scale = (nsx != 1f || nsy != 1f);
		final boolean angle = rotation != 0;
		final boolean update = angle || anchor;
		final int blend = gl.getBlendMode();
		try {
			gl.setTint(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.getWidth(newMessage, false) / 2;
					float centerY = y + this.getHeight(newMessage, false) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (scale) {
					float centerX = x + this.getWidth(newMessage, false) / 2;
					float centerY = y + this.getHeight(newMessage, false) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(nsx, nsy);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			gl.synchTransform();
			Canvas canvas = gl.getCanvas();
			canvas.setFont(font);
			canvas.drawText(newMessage, x, y, c);
		} finally {
			gl.setBlendMode(blend);
			gl.setTint(old);
			if (update) {
				gl.restoreTx();
			}
		}
	}

	public int getPixelColor() {
		return this.pixelColor;
	}

	public LSTRFont setPixelColor(int pixel) {
		this.pixelColor = pixel;
		return this;
	}

	public LSTRFont setPixelColor(LColor color) {
		this.pixelColor = (color == null ? LColor.DEF_COLOR : color.getARGB());
		return this;
	}

	public int getPixelFontSize() {
		return this.pixelFontSize == 0 ? this.font.getSize() : this.pixelFontSize;
	}

	public LSTRFont setPixelFontSize(int size) {
		this.pixelFontSize = size;
		return this;
	}

	public LSTRFont setFontSize(int size) {
		this.setSize(size);
		return this;
	}

	@Override
	public void setSize(int size) {
		this.fontSize = size;
		this.fontScale = (float) size / (float) this.pixelFontSize;
	}

	@Override
	public int charWidth(char c) {
		if (c == newLineFlag) {
			return 0;
		}
		return font.charWidth(c);
	}

	public int getWidth(String msg) {
		return getWidth(msg, true);
	}

	public int getWidth(String msg, boolean filter) {
		String newMessage = msg;
		if (filter) {
			newMessage = toMessage(msg);
		}
		return font.stringWidth(newMessage);
	}

	public int getHeight(String msg) {
		return getHeight(msg, true);
	}

	public int getHeight(String msg, boolean filter) {
		String newMessage = msg;
		if (filter) {
			newMessage = toMessage(msg);
		}
		return font.stringWidth(newMessage);
	}

	@Override
	public int getHeight() {
		return fontHeight;
	}

	@Override
	public int getSize() {
		return fontSize == 0 ? pixelFontSize : fontSize;
	}

	public int getLineHeight() {
		return fontHeight;
	}

	@Override
	public float getAscent() {
		return ascent;
	}

	public LFont getFont() {
		return font;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public LSTRFont setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public LSTRFont setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public boolean isAsyn() {
		return isasyn;
	}

	public LSTRFont setAsyn(boolean a) {
		this.isasyn = a;
		return this;
	}

	@Override
	public int stringWidth(String width) {
		return getWidth(width, true);
	}

	@Override
	public int stringHeight(String height) {
		return getHeight(height, true);
	}

	@Override
	public void setAssent(float assent) {

	}

	@Override
	public PointI getOffset() {
		return _offset;
	}

	@Override
	public void setOffset(PointI val) {
		_offset.set(val.x, val.y);
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
				msg = msg.substring(0, ++i) + "...";
				break;
			}
		}
		return msg;
	}

	public String getText() {
		return text;
	}

	public int getTextSize() {
		return text.length();
	}

	@Override
	public String getFontName() {
		return font.getFontName();
	}

	public int getAdvanceSpace() {
		return advanceSpace;
	}

	public LSTRFont setAdvanceSpace(int s) {
		this.advanceSpace = s;
		return this;
	}

	public int getDrawLimit() {
		return _drawLimit;
	}

	public void setDrawLimit(int d) {
		this._drawLimit = d;
	}

	public boolean isOutBounds() {
		return _outBounds;
	}

	public int getTextCount() {
		return _chars != null ? _chars.size() : 0;
	}

	public String getChars() {
		return _chars.getString();
	}

	public boolean containsChar(char c) {
		return _chars.contains(c);
	}

	public boolean containsChars(String msg) {
		return containsChars(msg, true);
	}

	public boolean containsChars(String msg, boolean filter) {
		if (StringUtils.isNullOrEmpty(msg)) {
			return true;
		}
		String newMessage = msg;
		if (filter) {
			newMessage = toMessage(msg);
		}
		int count = 0;
		int len = newMessage.length();
		for (int i = 0; i < len; i++) {
			if (_chars.contains(newMessage.charAt(i))) {
				count++;
			}
		}
		return count == len;
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

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public synchronized void close() {

	}

}
