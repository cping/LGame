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

import java.util.HashMap;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.utils.GLUtils;
import loon.utils.StringUtils;

public class LSTRFont implements LRelease {

	private boolean useCache;

	private HashMap<String, Cache> displays;

	private int totalCharSet = 256;

	private HashMap<Character, IntObject> customChars = new HashMap<Character, IntObject>();

	private IntObject[] charArray = new IntObject[totalCharSet];

	private LColor[] colors = null;

	private LFont font;

	private IntObject intObject;

	private Cache display;

	private float ascent;

	private int charCurrent;

	private int totalWidth;

	private int textureWidth = 512;

	private int textureHeight = 512;

	private int fontSize = 0;

	private int fontHeight = 0;

	private LTextureBatch fontBatch;

	private class IntObject {

		public int width;

		public int height;

		public int storedX;

		public int storedY;

	}

	public LSTRFont(LFont font) {
		this(font, (char[]) null);
	}

	public LSTRFont(LFont font, String strings) {
		this(font, strings.toCharArray());
	}

	public LSTRFont(LFont font, char[] additionalChars) {
		if (displays == null) {
			displays = new HashMap<String, Cache>(totalCharSet);
		} else {
			displays.clear();
		}
		this.useCache = true;
		this.font = font;
		this.fontSize = (int) font.getSize();
		this.ascent = font.getAscent();
		this.make(additionalChars);
	}

	private LTexture texture;

	private void make(char[] customCharsArray) {
		if (customCharsArray != null && customCharsArray.length > totalCharSet) {
			textureWidth *= 2;
		}
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(textureWidth, textureHeight);
		canvas.setColor(LColor.white);
		canvas.setFont(font);
		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;
		int customCharsLength = (customCharsArray != null) ? customCharsArray.length
				: 0;
		this.totalCharSet = customCharsLength == 0 ? totalCharSet : 0;
		StringBuilder sbr = new StringBuilder(totalCharSet);
		for (int i = 0; i < totalCharSet + customCharsLength; i++) {
			char ch = (i < totalCharSet) ? (char) i : customCharsArray[i
					- totalCharSet];

			TextLayout layout = font.getLayoutText(String.valueOf(ch));

			int charwidth = layout.charWidth(ch);

			if (charwidth <= 0) {
				charwidth = 1;
			}

			int charheight = (int) layout.getHeight();
			if (charheight <= 0) {
				charheight = fontSize;
			}

			IntObject newIntObject = new IntObject();

			newIntObject.width = charwidth;
			newIntObject.height = charheight;

			if (positionX + newIntObject.width >= textureWidth) {
				layout = font.getLayoutText(sbr.toString());
				canvas.fillText(layout, 0, positionY);
				sbr.delete(0, sbr.length());
				positionX = 0;
				positionY += rowHeight;
				rowHeight = 0;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			if (newIntObject.height > fontHeight) {
				fontHeight = newIntObject.height;
			}

			if (newIntObject.height > rowHeight) {
				rowHeight = newIntObject.height;
			}

			sbr.append(ch);

			positionX += newIntObject.width;

			if (i < totalCharSet) {
				charArray[i] = newIntObject;
			} else {
				customChars.put(ch, newIntObject);
			}

		}
		if (sbr.length() > 0) {
			TextLayout layout = font.getLayoutText(sbr.toString());
			canvas.fillText(layout, 0, positionY);
			sbr = null;
		}

		LTextureBatch tmpbatch = fontBatch;
		fontBatch = new LTextureBatch(
				texture = canvas.toTexture(LTexture.Format.LINEAR));
		fontBatch.setBlendState(BlendState.AlphaBlend);
		if (tmpbatch != null) {
			tmpbatch.destoryAll();
		}

	}

	public LTexture getTexture() {
		return texture;
	}

	public void drawString(String chars, float x, float y) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, LColor.white, 0, chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, color, 0, chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float rotation,
			LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, color, 0,
				chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float rotation) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, LColor.white, 0,
				chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float sx, float sy,
			float ax, float ay, float rotation, LColor c) {
		drawString(x, y, sx, sy, ax, ay, rotation, chars, c, 0,
				chars.length() - 1);
	}

	private void drawString(float x, float y, float sx, float sy, float ax,
			float ay, float rotation, String chars, LColor c, int startIndex,
			int endIndex) {
		if (StringUtils.isEmpty(chars)) {
			return;
		}
		if (displays.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			synchronized (displays) {
				for (Cache cache : displays.values()) {
					if (cache != null) {
						cache.close();
						cache = null;
					}
				}
			}
			displays.clear();
		}

		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		if (rotation != 0 && (ax == 0 && ay == 0)) {
			TextLayout layout = font.getLayoutText(chars);
			ax = layout.bounds.width / 2;
			ay = layout.bounds.height;
		}
		if (useCache) {
			display = displays.get(chars);
			if (display == null) {

				fontBatch.begin();
				float old = fontBatch.getFloatColor();
				fontBatch.setColor(c);
				char[] charList = chars.toCharArray();
				for (int i = 0; i < charList.length; i++) {
					charCurrent = charList[i];
					if (charCurrent < totalCharSet) {
						intObject = charArray[charCurrent];
					} else {
						intObject = customChars.get((char) charCurrent);
					}
					if (intObject != null) {
						if ((i >= startIndex) || (i <= endIndex)) {
							fontBatch.drawQuad(totalWidth, 0,
									(totalWidth + intObject.width),
									intObject.height, intObject.storedX,
									intObject.storedY, intObject.storedX
											+ intObject.width,
									intObject.storedY + intObject.height);
						}
						totalWidth += intObject.width;
					}
				}
				fontBatch.setColor(old);
				fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
				displays.put(chars, display = fontBatch.newCache());
			} else if (display != null && fontBatch != null
					&& fontBatch.toTexture() != null) {
				fontBatch.postCache(display, c, x, y, sx, sy, ax, ay, rotation);
			}
		} else {
			fontBatch.begin();
			float old = fontBatch.getFloatColor();
			fontBatch.setColor(c);
			char[] charList = chars.toCharArray();
			for (int i = 0; i < charList.length; i++) {
				charCurrent = charList[i];
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject = customChars.get((char) charCurrent);
				}
				if (intObject != null) {
					if ((i >= startIndex) || (i <= endIndex)) {
						fontBatch.drawQuad(totalWidth, 0,
								(totalWidth + intObject.width),
								intObject.height, intObject.storedX,
								intObject.storedY, intObject.storedX
										+ intObject.width, intObject.storedY
										+ intObject.height);
					}
					totalWidth += intObject.width;
				}
			}
			fontBatch.setColor(old);
			fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
		}
	}

	public void addChar(char c, float x, float y, LColor color) {
		this.charCurrent = c;
		if (charCurrent < totalCharSet) {
			intObject = charArray[charCurrent];
		} else {
			intObject = customChars.get((char) charCurrent);
		}
		if (intObject != null) {
			if (color != null) {
				setImageColor(color);
			}
			fontBatch.draw(colors, x, y - font.getAscent(), intObject.width,
					intObject.height, intObject.storedX, intObject.storedY,
					intObject.storedX + intObject.width, intObject.storedY
							+ intObject.height);
			if (colors != null) {
				colors = null;
			}
		}
	}

	public void startChar() {
		fontBatch.begin();
	}

	public void stopChar() {
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.end();
			GLUtils.setBlendMode(g, old);
		}
	}

	public void postCharCache() {
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.postLastCache();
			GLUtils.setBlendMode(g, old);
		}
	}

	public Cache saveCharCache() {
		fontBatch.disposeLastCache();
		return fontBatch.newCache();
	}

	public LTextureBatch getFontBatch() {
		return fontBatch;
	}

	private void setImageColor(float r, float g, float b) {
		setColor(Painter.TOP_LEFT, r, g, b);
		setColor(Painter.TOP_RIGHT, r, g, b);
		setColor(Painter.BOTTOM_LEFT, r, g, b);
		setColor(Painter.BOTTOM_RIGHT, r, g, b);
	}

	private void setImageColor(LColor c) {
		if (c == null) {
			return;
		}
		setImageColor(c.r, c.g, c.b);
	}

	private void setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
	}

	public int charWidth(char c) {
		return font.charWidth(c);
	}

	public int getWidth(String s) {
		int totalWidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		char[] charList = s.toCharArray();
		for (int i = 0; i < charList.length; i++) {
			currentChar = charList[i];
			if (currentChar < totalCharSet) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get((char) currentChar);
			}

			if (intObject != null)
				totalWidth += intObject.width;
		}
		return totalWidth;
	}

	public int getHeight() {
		return fontHeight;
	}

	public int getSize() {
		return fontSize;
	}

	public int getLineHeight() {
		return fontHeight;
	}

	public float getAscent() {
		return ascent;
	}

	public LFont getFont() {
		return font;
	}

	public int getTotalCharSet() {
		return totalCharSet;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void close() {
		if (fontBatch != null) {
			fontBatch.destoryAll();
		}
		for (Cache c : displays.values()) {
			if (c != null) {
				c.close();
				c = null;
			}
		}
	}

}
