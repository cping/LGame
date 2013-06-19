package loon.core.graphics.opengl;

import java.util.HashMap;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTextureBatch.GLCache;


/**
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.2
 */
public class LSTRFont implements LRelease {

	private boolean useCache;

	/**
	 * 获得指定字符串的LImage图像
	 * 
	 * @param fontName
	 * @param style
	 * @param size
	 * @param color
	 * @param text
	 * @return
	 */
	public static LImage createFontImage(String fontName, int style, int size,
			LColor color, String text) {
		return createFontImage(LFont.getFont(fontName, style, size), color,
				text);
	}

	/**
	 * 获得指定字符串的LImage图像
	 * 
	 * @param font
	 * @param color
	 * @param text
	 * @return
	 */
	public static LImage createFontImage(LFont font, LColor color, String text) {
		LImage image = new LImage(font.stringWidth(text), font.getHeight(),
				true);
		LGraphics g = image.getLGraphics();
		g.setColor(color);
		g.setFont(font);
		g.setAntiAlias(true);
		g.drawString(text, 0, -font.getAscent());
		g.dispose();
		return image;
	}

	private HashMap<String, GLCache> displays;

	private int totalCharSet = 256;

	private HashMap<Character, IntObject> customChars = new HashMap<Character, IntObject>();

	private IntObject[] charArray = new IntObject[totalCharSet];

	private LColor[] colors;

	private LFont font;

	private IntObject intObject;

	private GLCache display;

	private float ascent;

	private int charCurrent;

	private int totalWidth;

	private int textureWidth = 512;

	private int textureHeight = 512;

	private int fontSize = 0;

	private int fontHeight = 0;

	private boolean antiAlias;

	private LTextureBatch fontBatch;

	private class IntObject {

		public int width;

		public int height;

		public int storedX;

		public int storedY;

	}

	public LSTRFont(LFont font, boolean antiAlias) {
		this(font, antiAlias, (char[]) null);
	}

	public LSTRFont(LFont font, boolean antiAlias, String strings) {
		this(font, antiAlias, strings.toCharArray());
	}

	public LSTRFont(LFont font, String strings) {
		this(font, true, strings.toCharArray());
	}

	public LSTRFont(LFont font, boolean antiAlias, char[] additionalChars) {
		if (displays == null) {
			displays = new HashMap<String, GLCache>(totalCharSet);
		} else {
			displays.clear();
		}
		this.useCache = true;
		this.font = font;
		this.fontSize = font.getSize();
		this.ascent = font.getAscent();
		this.antiAlias = antiAlias;
		this.make(additionalChars);
	}

	private void make(char[] customCharsArray) {
		if (customCharsArray != null && customCharsArray.length > totalCharSet) {
			textureWidth *= 2;
		}
		try {
			LImage imgTemp = new LImage(textureWidth, textureHeight, true);
			LGraphics g = imgTemp.getLGraphics();
			g.setColor(LColor.white);
			g.setFont(font);
			if (antiAlias) {
				g.setAntiAlias(antiAlias);
			}

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

				int charwidth = font.charWidth(ch);
				if (charwidth <= 0) {
					charwidth = 1;
				}
				int charheight = font.getHeight();
				if (charheight <= 0) {
					charheight = fontSize;
				}

				IntObject newIntObject = new IntObject();

				newIntObject.width = charwidth;
				newIntObject.height = charheight;

				if (positionX + newIntObject.width >= textureWidth) {
					g.drawString(sbr.toString(), 0, positionY - font.getAscent());
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
			if(sbr.length()>0){
				g.drawString(sbr.toString(), 0, positionY - font.getAscent());
                sbr = null;
			}
			LTexture texture = new LTexture(GLLoader.getTextureData(imgTemp),
					Format.LINEAR);

			fontBatch = new LTextureBatch(texture);

			if (imgTemp != null) {
				imgTemp.dispose();
				imgTemp = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawString(String chars, float x, float y, float rotation,
			LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, rotation,
				chars, color, 0, chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float rotation) {
		drawString(x, y, 1f, 1f, 0, 0, rotation,
				chars, null, 0, chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float sx, float sy,
			float ax, float ay, float rotation, LColor c) {
		drawString(x, y, sx, sy, ax, ay, rotation, chars, null, 0,
				chars.length() - 1);
	}

	private void drawString(float x, float y, float sx, float sy, float ax,
			float ay, float rotation, String chars, LColor c, int startIndex,
			int endIndex) {

		if (displays.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			synchronized (displays) {
				for (GLCache cache : displays.values()) {
					if (cache != null) {
						cache.dispose();
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
			ax = font.stringWidth(chars) / 2;
			ay = font.getHeight();
		}
		if (useCache) {
			display = displays.get(chars);
			if (display == null) {
				fontBatch.glBegin();
				char[] charList = chars.toCharArray();
				for (int i = 0; i < charList.length; i++) {
					charCurrent = charList[i];
					if (charCurrent < totalCharSet) {
						intObject = charArray[charCurrent];
					} else {
						intObject =  customChars.get(
								(char) charCurrent);
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
				fontBatch.commitQuad(c, x, y, sx, sy, ax, ay, rotation);
				displays.put(chars, display = fontBatch.newGLCache());
			} else if (display != null && fontBatch != null
					&& fontBatch.getTexture() != null) {
				LTextureBatch.commitQuad(fontBatch.getTexture(), display, c, x,
						y,sx, sy, ax, ay, rotation);
			}
		} else {
			fontBatch.glBegin();
			char[] charList = chars.toCharArray();
			for (int i = 0; i < charList.length; i++) {
				charCurrent = charList[i];
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject =  customChars.get(
							(char) charCurrent);
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
			fontBatch.commitQuad(c, x, y, sx, sy, ax, ay, rotation);
		}
	}

	public void addChar(char c, float x, float y, LColor color) {
		this.charCurrent = c;
		if (charCurrent < totalCharSet) {
			intObject = charArray[charCurrent];
		} else {
			intObject =  customChars.get(
					(char) charCurrent);
		}
		if (intObject != null) {
			if (color != null) {
				setImageColor(color);
			}
			fontBatch.draw(colors, x, y + font.getAscent(), intObject.width,
					intObject.height, intObject.storedX, intObject.storedY,
					intObject.storedX + intObject.width, intObject.storedY
							+ intObject.height);
			if (colors != null) {
				colors = null;
			}
		}
	}

	public void startChar() {
		fontBatch.glBegin();
	}

	public void stopChar() {
		GLEx g = GLEx.self;
		if (g != null) {
			int old = g.getBlendMode();
			g.setBlendMode(GL.MODE_SPEED);
			fontBatch.glEnd();
			g.setBlendMode(old);
		}
	}

	public void postCharCache() {
		GLEx g = GLEx.self;
		if (g != null) {
			int old = g.getBlendMode();
			g.setBlendMode(GL.MODE_SPEED);
			fontBatch.postLastCache();
			g.setBlendMode(old);
		}
	}

	public GLCache saveCharCache() {
		fontBatch.disposeLastCache();
		return fontBatch.newGLCache();
	}

	public LTextureBatch getFontBatch() {
		return fontBatch;
	}

	private void setImageColor(float r, float g, float b) {
		setColor(LTexture.TOP_LEFT, r, g, b);
		setColor(LTexture.TOP_RIGHT, r, g, b);
		setColor(LTexture.BOTTOM_LEFT, r, g, b);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b);
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
				intObject =  customChars.get(
						(char) currentChar);
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

	@Override
	public void dispose() {
		if (fontBatch != null) {
			fontBatch.destoryAll();
		}
		for (GLCache c : displays.values()) {
			if (c != null) {
				c.dispose();
				c = null;
			}
		}
	}

}
