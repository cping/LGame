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
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.event.Updateable;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Affine2f;
import loon.geom.PointI;
import loon.geom.RectF;
import loon.utils.CharArray;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.OrderedSet;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class LSTRFont implements IFont, LRelease {

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
		return new LSTRFont(LFont.getFont(size), LSTRDictionary.getAddedString(), true);
	}

	private class CharRect extends RectF {

		public Character name;

		public LColor color;

	}

	private static class IntObject {

		public int width;

		public int height;

		public int storedX;

		public int storedY;

	}

	private static class UpdateStringFont implements Updateable {

		private LSTRFont strfont;

		public UpdateStringFont(LSTRFont strf) {
			this.strfont = strf;
		}

		@Override
		public void action(Object a) {
			if (strfont._isClose) {
				return;
			}
			strfont.fontSize = strfont.font.getSize();
			strfont.ascent = strfont.font.getAscent();
			if (strfont.additionalChars != null && strfont.additionalChars.length > strfont.totalCharSet) {
				strfont.textureWidth *= 2;
			}
			if (strfont.textureWidth > strfont._maxTextureWidth || strfont.textureHeight > strfont._maxTextureHeight) {
				strfont._outBounds = true;
			}
			Canvas canvas = LSystem.base().graphics().createCanvas(strfont.textureWidth, strfont.textureHeight);
			canvas.setColor(LColor.white);
			canvas.setFont(strfont.font);
			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;
			int customCharsLength = (strfont.additionalChars != null) ? strfont.additionalChars.length : 0;
			strfont.totalCharSet = customCharsLength == 0 ? strfont.totalCharSet : 0;
			StrBuilder sbr = new StrBuilder(strfont.totalCharSet);
			final boolean clipFont = LSystem.isTrueFontClip();
			final OrderedSet<Character> outchached = new OrderedSet<Character>();
			// 本地字体怎么都不如ttf或者fnt字体清晰准确,差异太大，只能尽量保证显示效果……
			for (int i = 0, size = strfont.totalCharSet + customCharsLength; i < size; i++) {

				boolean outchar = false;

				char ch = (i < strfont.totalCharSet) ? (char) i : strfont.additionalChars[i - strfont.totalCharSet];

				TextLayout layout = strfont.font.getLayoutText(String.valueOf(ch));

				int charwidth = layout.charWidth(ch);

				if (charwidth <= 0) {
					charwidth = 1;
				}

				int charheight = (int) layout.getHeight();
				if (charheight <= 0) {
					charheight = strfont.fontSize;
				}
				IntObject newIntObject = new IntObject();

				if (clipFont) {
					if (StringUtils.isAlphabetLower(ch)) {
						if (charwidth % 2 != 0) {
							charwidth += 1;
						}
						if (charheight % 2 != 0) {
							charheight += 1;
						}
					}
				} else {
					if (ch == 'i' && charheight > 24) {
						charheight -= 4;
					}
				}

				newIntObject.width = charwidth;
				newIntObject.height = charheight;

				if (clipFont) {
					// 发现部分环境字体如果整体渲染到canvas的话，会导致纹理切的不整齐(实际上就是间距和从系统获取的不符合),
					// 保险起见一个个字体粘贴……
					if (positionY <= strfont.textureHeight - newIntObject.height
							&& positionX <= strfont.textureWidth - newIntObject.width) {
						canvas.fillText(layout, positionX, positionY);
					} else {
						outchached.add(ch);
						strfont._outBounds = true;
						outchar = true;
					}
					if (positionX + newIntObject.width >= strfont.textureWidth) {
						positionX = 0;
						positionY += rowHeight;
						rowHeight = 0;
					}
				} else {
					boolean checkplusA = positionY + newIntObject.height <= strfont.textureHeight;
					boolean checkplusB = positionX + newIntObject.width <= strfont.textureWidth;

					// 一次渲染一整行本地字体到纹理，这样对系统开销最小，不过某些平台切的不整齐(实际上就是间距和从系统获取的不符合)
					// 若显示有问题，请设定clipFont = true
					if (!checkplusB) {
						layout = strfont.font.getLayoutText(sbr.toString());
						if (checkplusA) {
							canvas.fillText(layout, 0, positionY);
						} else {
							outchached.add(ch);
							strfont._outBounds = true;
							outchar = true;
							if (sbr.length() > 0) {
								for (int n = 0; n < sbr.length(); n++) {
									char temp = sbr.charAt(n);
									strfont._chars.removeValue(temp);
									outchached.add(temp);
								}
							}
						}
						sbr.delete(0, sbr.length());
						positionX = 0;
						positionY += rowHeight;
						rowHeight = 0;
					}
					if (checkplusA) {
						sbr.append(ch);
					} else {
						outchached.add(ch);
						strfont._outBounds = true;
						outchar = true;
					}
				}

				newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;

				if (newIntObject.height > strfont.fontHeight) {
					strfont.fontHeight = newIntObject.height;
				}

				if (newIntObject.height > rowHeight) {
					rowHeight = newIntObject.height;
				}
				positionX += newIntObject.width;
				if (i < strfont.totalCharSet) {
					strfont.charArray[i] = newIntObject;
				} else {
					strfont.customChars.put(ch, newIntObject);
				}

				if (!outchar) {
					strfont._chars.add(ch);
				}
			}
			if (sbr.length() > 0) {
				TextLayout layout = strfont.font.getLayoutText(sbr.toString());
				if (positionY <= strfont.textureHeight - strfont.fontSize) {
					canvas.fillText(layout, 0, positionY);
				} else {
					for (int i = 0; i < sbr.length(); i++) {
						outchached.add(sbr.charAt(i));
					}
					strfont._outBounds = true;
				}
				sbr = null;
			}
			LTextureBatch tmpbatch = strfont.fontBatch;
			strfont.fontBatch = new LTextureBatch(strfont.texture = canvas.toTexture());
			strfont.fontBatch.setBlendState(BlendState.AlphaBlend);
			if (tmpbatch != null) {
				tmpbatch.close();
			}
			// 若字符串超过当前纹理大小,则创建新纹理保存
			if (strfont._outBounds) {
				StrBuilder temp = new StrBuilder(outchached.size());
				for (LIterator<Character> it = outchached.iterator(); it.hasNext();) {
					temp.append(it.next());
				}
				strfont._childFont = new LSTRFont(strfont.font, temp.toString(), strfont.isasyn, strfont.textureWidth,
						strfont.textureHeight, strfont._maxTextureWidth, strfont._maxTextureHeight);
			}
			if (positionX > strfont.textureWidth || positionY > strfont.textureHeight) {
				strfont._outBounds = true;
			}
			strfont._initChars = true;
			strfont.isDrawing = false;
		}

	}

	private final PointI _offset = new PointI();

	private final CharArray _chars;

	private final int _maxTextureWidth;

	private final int _maxTextureHeight;

	private boolean _isClose = false;

	private boolean _outBounds = false;

	private LSTRFont _childFont = null;

	private int _initDraw = -1;

	private int _drawLimit = 0;

	private int textureWidth = 512;

	private int textureHeight = 512;

	private float updateX = 0, updateY = 0;

	private char newLineFlag = '\n';

	private LTexture texture;

	private boolean useCache, isDrawing, isasyn;

	private float offsetX = 1, offsetY = 1;

	private IntMap<Cache> displays;

	private int totalCharSet = 256;

	private IntMap<IntObject> customChars = new IntMap<IntObject>();

	private IntObject[] charArray = new IntObject[totalCharSet];

	private LColor[] colors = null;

	private String text;

	private LFont font;

	private IntObject intObject;

	private Cache display;

	private float ascent;

	private int charCurrent;

	private int totalWidth = 0, totalHeight = 0;

	private int fontSize = 0;

	private int fontHeight = 0;

	private LTextureBatch fontBatch;

	private boolean _initChars = false;

	private char[] additionalChars = null;

	private TArray<CharRect> _childChars;

	private void putChildChars(Character ch, float x, float y, float w, float h, LColor c) {
		if (_childChars == null) {
			_childChars = new TArray<CharRect>();
		}
		CharRect obj = new CharRect();
		obj.name = ch;
		obj.x = x;
		obj.y = y;
		obj.width = w;
		obj.height = h;
		obj.color = c;
		_childChars.add(obj);
	}

	public LSTRFont(LFont font) {
		this(font, (char[]) null, true);
	}

	public LSTRFont(LFont font, String strings) {
		this(font, strings.toCharArray(), true);
	}

	public LSTRFont(LFont font, String strings, int width, int height, int maxWidth, int maxHeight) {
		this(font, strings.toCharArray(), true, width, height, maxWidth, maxHeight);
	}

	public LSTRFont(LFont font, String[] strings) {
		this(font, StringUtils.merge(strings).toCharArray(), true);
	}

	public LSTRFont(LFont font, boolean asyn) {
		this(font, (char[]) null, asyn);
	}

	public LSTRFont(LFont font, String strings, boolean asyn) {
		this(font, strings.toCharArray(), asyn);
	}

	public LSTRFont(LFont font, String strings, boolean asyn, int tw, int th, int maxWidth, int maxHeight) {
		this(font, strings.toCharArray(), asyn, tw, th, maxWidth, maxHeight);
	}

	public LSTRFont(LFont font, String[] strings, boolean asyn) {
		this(font, StringUtils.merge(strings).toCharArray(), asyn);
	}

	public LSTRFont(LFont font, char[] charMessage, boolean asyn) {
		this(font, charMessage, asyn, 512, 512, 1024, 1024);
	}

	public LSTRFont(LFont font, char[] charMessage, boolean asyn, int tw, int th, int maxWidth, int maxHeight) {
		CharSequence chs = " " + StringUtils.unificationChars(charMessage);
		this._chars = new CharArray(chs.length());
		this._maxTextureWidth = maxWidth;
		this._maxTextureHeight = maxHeight;
		this.textureWidth = tw;
		this.textureHeight = th;
		this.displays = new IntMap<Cache>(totalCharSet);
		this.useCache = true;
		this.font = font;
		this.isasyn = asyn;
		this.fontSize = font.getSize();
		this.fontHeight = font.getHeight();
		this.ascent = font.getAscent();
		int customCharsLength = (additionalChars != null) ? additionalChars.length : 0;
		this.totalCharSet = customCharsLength == 0 ? totalCharSet : 0;
		if (chs != null && chs.length() > 0) {
			StrBuilder tmp = new StrBuilder(chs);
			this.text = tmp.toString();
			this.additionalChars = text.toCharArray();
			if (additionalChars != null && additionalChars.length > totalCharSet) {
				textureWidth *= 2;
			}
			this.make(asyn);
		}
		if (StringUtils.isEmpty(text.trim())) {
			_isClose = true;
		}
		this._drawLimit = 0;
	}

	private void make() {
		make(isasyn);
	}

	private synchronized void make(boolean asyn) {
		if (_isClose) {
			return;
		}
		if (_initChars) {
			return;
		}
		if (isDrawing) {
			return;
		}
		isDrawing = true;
		updateX = 0;
		updateY = LSystem.isHTML5() ? 1f : 0;
		Updateable update = new UpdateStringFont(this);
		if (asyn) {
			LSystem.load(update);
		} else {
			update.action(null);
		}
	}

	public LTexture getTexture() {
		return texture;
	}

	@Override
	public void drawString(GLEx g, String chars, float x, float y, float sx, float sy, float ax, float ay,
			float rotation, LColor c) {
		drawString(chars, x, y, sx, sy, ax, ay, rotation, c);
	}

	public void drawString(String chars, float x, float y) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, LColor.white, 0, chars.length());
	}

	public void drawString(String chars, float x, float y, LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, color, 0, chars.length());
	}

	public void drawString(String chars, float x, float y, float rotation, LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, color, 0, chars.length());
	}

	public void drawString(String chars, float x, float y, float rotation) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, LColor.white, 0, chars.length());
	}

	public void drawString(String chars, float x, float y, float sx, float sy, float ax, float ay, float rotation,
			LColor c) {
		drawString(x, y, sx, sy, ax, ay, rotation, chars, c, 0, chars.length());
	}

	private final boolean cehckRunning(String chars) {
		if (_isClose) {
			return false;
		}
		if (StringUtils.isEmpty(chars)) {
			return false;
		}
		make();
		if (processing()) {
			return false;
		}
		if (_initDraw < _drawLimit) {
			_initDraw++;
			return false;
		}
		if (texture.isClosed()) {
			return false;
		}
		return true;
	}

	private void drawString(float mx, float my, float sx, float sy, float ax, float ay, float rotation, String chars,
			LColor c, int startIndex, int endIndex) {
		if (!cehckRunning(chars)) {
			return;
		}
		if (displays.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
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

		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		if (rotation != 0 && (ax == 0 && ay == 0)) {
			TextLayout layout = font.getLayoutText(chars);
			ax = layout.bounds.width / 2;
			ay = layout.bounds.height;
		}
		if (useCache) {
			display = displays.get(chars);
			if (display == null) {
				clearChildString();
				fontBatch.begin();
				float old = fontBatch.getFloatColor();
				fontBatch.setColor(c);
				for (int i = startIndex; i < endIndex; i++) {
					char ch = chars.charAt(i);
					charCurrent = ch;
					if (charCurrent < totalCharSet) {
						intObject = charArray[charCurrent];

					} else {
						intObject = customChars.get(charCurrent);
					}
					if (charCurrent == newLineFlag) {
						totalHeight += fontSize;
						totalWidth = 0;
					}
					if (intObject != null) {
						if (!checkOutBounds() || containsChar(ch)) {
							fontBatch.drawQuad(totalWidth, totalHeight, (totalWidth + intObject.width) - offsetX,
									(totalHeight + intObject.height) - offsetY, intObject.storedX, intObject.storedY,
									intObject.storedX + intObject.width - offsetX,
									intObject.storedY + intObject.height - offsetY);
						} else if (checkOutBounds()) {
							putChildChars(ch, totalWidth, totalHeight, (totalWidth + intObject.width) - offsetX,
									(totalHeight + intObject.height) - offsetY, null);
						}
						totalWidth += intObject.width;
					}
				}
				fontBatch.setBlendState(BlendState.AlphaBlend);
				fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
				fontBatch.setColor(old);
				displays.put(chars, display = fontBatch.newCache());
			} else if (display != null && fontBatch != null && fontBatch.toTexture() != null) {
				fontBatch.postCache(display, c, x, y, sx, sy, ax, ay, rotation);
			}
		} else {
			clearChildString();
			fontBatch.begin();
			float old = fontBatch.getFloatColor();
			fontBatch.setColor(c);

			for (int i = startIndex; i < endIndex; i++) {
				char ch = chars.charAt(i);
				charCurrent = ch;
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject = customChars.get(charCurrent);
				}
				if (charCurrent == newLineFlag) {
					totalHeight += fontSize;
					totalWidth = 0;
				}
				if (intObject != null) {
					if (!checkOutBounds() || containsChar(ch)) {
						fontBatch.drawQuad(totalWidth, totalHeight, (totalWidth + intObject.width) - offsetX,
								(totalHeight + intObject.height) - offsetY, intObject.storedX, intObject.storedY,
								intObject.storedX + intObject.width - offsetX,
								intObject.storedY + intObject.height - offsetY);
					} else if (checkOutBounds()) {
						putChildChars(ch, totalWidth, totalHeight, (totalWidth + intObject.width) - offsetX,
								(totalHeight + intObject.height) - offsetY, null);
					}
					totalWidth += intObject.width;
				}
			}
			fontBatch.setColor(old);
			fontBatch.setBlendState(BlendState.AlphaBlend);
			fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
		}
		if (checkOutBounds() && _childChars != null) {
			_childFont._drawChildString(_childChars, mx, my, sx, sy, ax, ay, rotation, chars, c, startIndex, endIndex);
		}
	}

	private void _drawChildString(TArray<CharRect> child, float mx, float my, float sx, float sy, float ax, float ay,
			float rotation, String chars, LColor c, int startIndex, int endIndex) {
		if (child == null) {
			return;
		}
		if (!cehckRunning(chars)) {
			return;
		}
		if (displays.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
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
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
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
				for (int i = 0; i < child.size; i++) {
					CharRect rect = child.get(i);
					if (rect != null) {
						char ch = rect.name;
						intObject = customChars.get(ch);
						if (intObject != null && containsChar(ch)) {
							fontBatch.drawQuad(rect.x, rect.y, rect.width, rect.height, intObject.storedX,
									intObject.storedY, intObject.storedX + intObject.width - offsetX,
									intObject.storedY + intObject.height - offsetY);
						} else if (checkOutBounds()) {
							putChildChars(ch, rect.x, rect.y, rect.width, rect.height, null);
						}
					}
				}
				fontBatch.setBlendState(BlendState.AlphaBlend);
				fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
				fontBatch.setColor(old);
				displays.put(chars, display = fontBatch.newCache());
			} else if (display != null && fontBatch != null && fontBatch.toTexture() != null) {
				fontBatch.postCache(display, c, x, y, sx, sy, ax, ay, rotation);
			}
		} else {
			fontBatch.begin();
			float old = fontBatch.getFloatColor();
			fontBatch.setColor(c);
			for (int i = 0; i < child.size; i++) {
				CharRect rect = child.get(i);
				if (rect != null) {
					char ch = rect.name;
					intObject = customChars.get(ch);
					if (intObject != null && containsChar(ch)) {
						fontBatch.drawQuad(rect.x, rect.y, rect.width, rect.height, intObject.storedX,
								intObject.storedY, intObject.storedX + intObject.width - offsetX,
								intObject.storedY + intObject.height - offsetY);
					} else if (checkOutBounds()) {
						putChildChars(ch, rect.x, rect.y, rect.width, rect.height, null);
					}
				}
			}
			fontBatch.setColor(old);
			fontBatch.setBlendState(BlendState.AlphaBlend);
			fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
		}
		if (checkOutBounds() && _childChars != null) {
			_childFont._drawChildString(_childChars, mx, my, sx, sy, ax, ay, rotation, chars, c, startIndex, endIndex);
		}
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

	private void drawString(GLEx gl, float mx, float my, float sx, float sy, float ax, float ay, float rotation,
			String chars, LColor c, int startIndex, int endIndex) {
		if (!cehckRunning(chars)) {
			return;
		}
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		int old = gl.color();
		boolean childDraw = false;
		final boolean anchor = ax != 0 || ay != 0;
		final boolean scale = sx != 1f || sy != 1f;
		final boolean angle = rotation != 0;
		final boolean update = scale || angle || anchor;
		final int blend = gl.getBlendMode();
		try {
			gl.setBlendMode(LSystem.MODE_NORMAL);
			gl.setTint(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (scale) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(sx, sy);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			for (int i = startIndex; i < endIndex; i++) {
				char ch = chars.charAt(i);
				charCurrent = ch;
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject = customChars.get(charCurrent);
				}
				if (charCurrent == newLineFlag) {
					totalHeight += fontSize;
					totalWidth = 0;
				}
				if (intObject != null) {
					if (!checkOutBounds() || containsChar(ch)) {
						gl.draw(texture, x + totalWidth, y + totalHeight, intObject.width * sx, intObject.height * sy,
								StringUtils.isChinese((char) charCurrent) ? intObject.storedX - updateX
										: intObject.storedX,
								intObject.storedY, intObject.width, intObject.height - updateY, c);
					} else if (checkOutBounds()) {
						putChildChars(ch, x + totalWidth, y + totalHeight, intObject.width * sx, intObject.height * sy,
								null);
						childDraw = true;
					}
					totalWidth += intObject.width;
				}
			}
		} finally {
			gl.setBlendMode(blend);
			gl.setTint(old);
			if (update) {
				gl.restoreTx();
			}
		}
		if (childDraw && _childChars != null) {
			_childFont._drawChildString(_childChars, gl, mx, my, sx, sy, ax, ay, rotation, chars, c, startIndex,
					endIndex);
			_childChars.clear();
		}
	}

	private void _drawChildString(TArray<CharRect> child, GLEx gl, float mx, float my, float sx, float sy, float ax,
			float ay, float rotation, String chars, LColor c, int startIndex, int endIndex) {
		if (!cehckRunning(chars)) {
			return;
		}
		final float x = mx + _offset.x;
		final float y = my + _offset.y;
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		int old = gl.color();
		boolean childDraw = false;
		final boolean anchor = ax != 0 || ay != 0;
		final boolean scale = sx != 1f || sy != 1f;
		final boolean angle = rotation != 0;
		final boolean update = scale || angle || anchor;
		final int blend = gl.getBlendMode();
		try {
			gl.setBlendMode(LSystem.MODE_NORMAL);
			gl.setTint(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (scale) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(sx, sy);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			for (int i = 0; i < child.size; i++) {
				CharRect rect = child.get(i);
				if (rect != null) {
					char ch = rect.name;
					intObject = customChars.get(ch);
					if (intObject != null && containsChar(ch)) {
						gl.draw(texture, rect.x, rect.y, rect.width, rect.height,
								StringUtils.isChinese((char) charCurrent) ? intObject.storedX - updateX
										: intObject.storedX,
								intObject.storedY, intObject.width, intObject.height - updateY, c);
					} else if (checkOutBounds()) {
						putChildChars(ch, rect.x, rect.y, rect.width, rect.height, null);
						childDraw = true;
					}
				}
			}
		} finally {
			gl.setBlendMode(blend);
			gl.setTint(old);
			if (update) {
				gl.restoreTx();
			}
		}
		if (childDraw && _childChars != null) {
			_childFont._drawChildString(_childChars, gl, mx, my, sx, sy, ax, ay, rotation, chars, c, startIndex,
					endIndex);
			_childChars.clear();
		}
	}

	public String getChars() {
		return _chars.getString();
	}

	public void setUpdateX(float x) {
		this.updateX = x;
	}

	public void setUpdateY(float y) {
		this.updateY = y;
	}
	
	private boolean checkCharRunning(){
		if (_isClose) {
			return false;
		}
		make();
		if (processing()) {
			return false;
		}
		if (_initDraw < _drawLimit) {
			_initDraw++;
			return false;
		}
		if (texture.isClosed()) {
			return false;
		}
		return true;
	}

	public void addChar(char c, float x, float y, LColor color) {
		if(!checkCharRunning()){
			return;
		}
		if (!checkOutBounds() || containsChar(c)) {
			this.charCurrent = c;
			if (charCurrent < totalCharSet) {
				intObject = charArray[charCurrent];
			} else {
				intObject = customChars.get(charCurrent);
			}
			if (intObject != null) {
				if (color != null) {
					setImageColor(color);
				}
				fontBatch.setBlendState(BlendState.AlphaBlend);
				if (c == newLineFlag) {
					fontBatch.draw(colors, x, y + fontSize, intObject.width - offsetX, intObject.height - offsetY,
							intObject.storedX, intObject.storedY, intObject.storedX + intObject.width - offsetX,
							intObject.storedY + intObject.height - offsetY);
				} else {
					fontBatch.draw(colors, x, y, intObject.width - offsetX, intObject.height - offsetY,
							intObject.storedX, intObject.storedY, intObject.storedX + intObject.width - offsetX,
							intObject.storedY + intObject.height - offsetY);
				}
				if (colors != null) {
					colors = null;
				}
			}
		} else if (checkOutBounds()) {
			putChildChars(c, x, y, intObject.width, intObject.height, color);
		}
	}

	private void clearChildString() {
		if (checkOutBounds() && _childChars != null) {
			_childChars.clear();
		}
	}

	public void startChar() {
		if(!checkCharRunning()){
			return;
		}
		clearChildString();
		fontBatch.begin();
	}

	public void stopChar() {
		if(!checkCharRunning()){
			return;
		}
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.end();
			GLUtils.setBlendMode(g, old);
		}
		postChildString();
	}

	private void postChildString() {
		if (checkOutBounds() && _childChars != null) {
			int len = _childChars.size;
			if (len > 0) {
				_childFont.startChar();
				for (int i = 0; i < len; i++) {
					CharRect rect = _childChars.get(i);
					_childFont.addChar(rect.name, rect.x, rect.y, rect.color);
				}
				_childFont.stopChar();
			}
		}
	}

	private boolean checkOutBounds() {
		return _outBounds && _childFont != null;
	}

	private boolean processing() {
		return fontBatch == null || isDrawing;
	}

	public void postCharCache() {
		if(!checkCharRunning()){
			return;
		}
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.postLastCache();
			GLUtils.setBlendMode(g, old);
		}
		postChildString();
	}

	public Cache saveCharCache() {
		if(!checkCharRunning()){
			return null;
		}
		fontBatch.disposeLastCache();
		return fontBatch.newCache();
	}

	public LTextureBatch getFontBatch() {
		return fontBatch;
	}

	private void setImageColor(float r, float g, float b, float a) {
		setColor(Painter.TOP_LEFT, r, g, b, a);
		setColor(Painter.TOP_RIGHT, r, g, b, a);
		setColor(Painter.BOTTOM_LEFT, r, g, b, a);
		setColor(Painter.BOTTOM_RIGHT, r, g, b, a);
	}

	private void setImageColor(LColor c) {
		if (c == null) {
			return;
		}
		setImageColor(c.r, c.g, c.b, c.a);
	}

	private void setColor(int corner, float r, float g, float b, float a) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		colors[corner].a = a;
	}

	public boolean containsChar(char c) {
		return _chars.contains(c);
	}

	public boolean containsChars(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}
		int count = 0;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (_chars.contains(str.charAt(i))) {
				count++;
			}
		}
		return count == len;
	}

	@Override
	public int charWidth(char c) {
		if (_isClose) {
			return 0;
		}
		make();
		if (c == '\n') {
			return 0;
		}
		if (processing()) {
			return font.charWidth(c);
		}
		if (texture.isClosed()) {
			return 0;
		}
		if (c < totalCharSet) {
			intObject = charArray[c];
		} else {
			intObject = customChars.get((int) c);
		}
		if (intObject != null) {
			return intObject.width;
		}
		return font.charWidth(c);
	}

	public int getWidth(String s) {
		if (_isClose) {
			return 0;
		}
		make();
		if (processing()) {
			return font.stringWidth(s);
		}
		if (texture.isClosed()) {
			return 0;
		}
		int totalWidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		char[] charList = s.toCharArray();
		int maxWidth = 0;
		for (int i = 0; i < charList.length; i++) {
			currentChar = charList[i];
			if (currentChar < totalCharSet) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get(currentChar);
			}
			if (intObject != null) {
				if (currentChar == newLineFlag) {
					maxWidth = MathUtils.max(maxWidth, totalWidth);
					totalWidth = 0;
				}
				totalWidth += intObject.width;
			}
		}
		return MathUtils.max(maxWidth, totalWidth);
	}

	public int getHeight(String s) {
		if (_isClose) {
			return 0;
		}
		make();
		if (processing()) {
			return font.stringHeight(s);
		}
		if (texture.isClosed()) {
			return 0;
		}
		int currentChar = 0;
		char[] charList = s.toCharArray();
		int lines = 0;
		int height = 0;
		int maxHeight = 0;
		for (int i = 0; i < charList.length; i++) {
			currentChar = charList[i];
			if (currentChar < totalCharSet) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get(currentChar);
			}
			if (intObject != null) {
				maxHeight = MathUtils.max(maxHeight, intObject.height);
				height = maxHeight;
			}
			if (currentChar == newLineFlag) {
				lines++;
				height = 0;
			}
		}
		return lines * getLineHeight() + height;
	}

	@Override
	public int getHeight() {
		return fontHeight;
	}

	@Override
	public int getSize() {
		return fontSize;
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

	public int getTotalCharSet() {
		return totalCharSet;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
		if (checkOutBounds()) {
			_childFont.setUseCache(useCache);
		}
	}

	public char getNewLineFlag() {
		return newLineFlag;
	}

	public void setNewLineFlag(char newLineFlag) {
		this.newLineFlag = newLineFlag;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public boolean isAsyn() {
		return isasyn;
	}

	public void setAsyn(boolean a) {
		this.isasyn = a;
	}

	@Override
	public int stringWidth(String width) {
		return getWidth(width);
	}

	@Override
	public int stringHeight(String height) {
		return getHeight(height);
	}

	@Override
	public void setAssent(float assent) {

	}

	@Override
	public void setSize(int size) {

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

	public boolean isClosed() {
		return _isClose;
	}

	public int getDrawLimit() {
		return _drawLimit;
	}

	public void setDrawLimit(int d) {
		this._drawLimit = d;
	}

	public int getMaxTextureWidth() {
		return _maxTextureWidth;
	}

	public int getMaxTextureHeight() {
		return _maxTextureHeight;
	}

	public boolean isOutBounds() {
		return _outBounds;
	}

	@Override
	public synchronized void close() {
		if (_isClose) {
			return;
		}
		for (Cache c : displays.values()) {
			if (c != null) {
				c.close();
			}
		}
		displays.clear();
		displays = null;
		if (fontBatch != null) {
			fontBatch.close();
			fontBatch = null;
		}
		if (texture != null) {
			texture.close(true);
			texture = null;
		}
		if (customChars != null) {
			customChars.clear();
			customChars = null;
		}
		charArray = null;
		isDrawing = false;
		_initChars = false;
		_initDraw = -1;
		_isClose = true;
		if (checkOutBounds()) {
			_childFont.close();
			_childFont = null;
		}
	}

}
