/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.component;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.opengl.LSTRFont;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * LMessage的文字打印器具体实现
 */
public class Print implements FontSet<Print>, LRelease {

	public static enum Mode {
		NONE, LEFT, RIGHT, CENTER
	}

	/**
	 * 解析并重构字符串,为超过指定长度的字符串加换行符
	 * 
	 * @param text
	 * @param font
	 * @param width
	 * @return
	 */
	public static String prepareString(String text, IFont font, float width) {
		if (font == null || StringUtils.isEmpty(text)) {
			return "";
		}
		StrBuilder sbr = new StrBuilder();
		TArray<String> list = formatMessage(text, font, width);
		for (int i = 0; i < list.size; i++) {
			String temp = list.get(i);
			if (!StringUtils.isEmpty(temp)) {
				sbr.append(list.get(i));
				if (i < list.size - 1) {
					sbr.append('\n');
				}
			}
		}
		return sbr.toString();
	}

	/**
	 * 返回指定字符串，匹配指定字体后，在指定宽度内的每行应显示字符串.
	 * 
	 * PS:此项不处理'\n'外的特殊操作符
	 * 
	 * @param text
	 * @param font
	 * @param width
	 * @return
	 */
	public static TArray<String> formatMessage(String text, IFont font, float width) {
		return FontUtils.splitLines(text, font, width);
	}

	private int index, offset, font, tmp_font;

	private char text;

	private char[] showMessages;

	private LColor fontColor = new LColor(LColor.white);

	private int interceptMaxString;

	private int interceptCount;

	private int messageLength = 10;

	private String messages;

	private boolean onComplete, newLine, visible, closed;

	private StrBuilder messageBuffer = new StrBuilder(messageLength);

	private int width, height, leftOffset, topOffset, next, messageCount;

	private float alpha;

	private int size, wait, tmp_dir, left, fontSize, fontHeight;

	private final PointF iconLocation;

	private final Vector2f printLocation;

	private LTexture creeseIcon;

	private LSTRFont strings;

	private IFont ifont;

	private boolean nativeFont = false;

	private boolean isEnglish, isWait, isIconFlag;

	private float iconX, iconY, offsetIconX, offsetIconY;

	private int lazyHashCade = 1;

	// 默认0，左1,右2
	private Mode dirmode = Mode.NONE;

	public Print(Vector2f printLocation, IFont font, int width, int height) {
		this(LSystem.EMPTY, font, printLocation, width, height);
	}

	public Print(String context, IFont font, Vector2f pos, int width, int height) {
		this.setMessage(context, font);
		this.printLocation = pos;
		this.width = width;
		this.height = height;
		this.wait = 0;
		this.messageLength = 10;
		this.isWait = false;
		this.isIconFlag = true;
		iconLocation = new PointF();
	}

	public void setMessage(String context, IFont font) {
		setMessage(context, font, false);
	}

	private static class PrintUpdate implements Updateable {

		Print _print;

		boolean _isComplete = false, _drawDrawingFont = false;

		private IFont _font = null;

		private String _context = null;

		private PrintUpdate(Print print, String context, IFont font, boolean isComplete, boolean drawFont) {
			_print = print;
			if (context != null) {
				if (StringUtils.isEnglishAndNumeric(context)) {
					_print.setEnglish(true);
				} else {
					_print.setEnglish(false);
				}
			}
			_context = context;
			_font = font;
			_isComplete = isComplete;
			_drawDrawingFont = drawFont;
		}

		@Override
		public void action(Object a) {
			if (_context == null) {
				return;
			}
			if (_print.strings != null && !_print.strings.isClosed() && !_drawDrawingFont) {
				_print.strings.close();
			}
			// 如果是默认的loon系统字体
			if (_font instanceof LFont) {
				if (_drawDrawingFont) {
					LSTRDictionary.Dict dict = LSTRDictionary.get().bind((LFont) _font, _context);
					_print.strings = dict.getSTR();
					_print.ifont = _font;
				} else {
					_print.strings = new LSTRFont((LFont) _font, _context, LSystem.isHTML5());
				}
				// 其他字体(一般是Bitmap Font)
			} else {
				_print.ifont = _font;
			}
			_print.lazyHashCade = 1;
			_print.wait = 0;
			_print.visible = false;
			_print.showMessages = new char[] { '\0' };
			_print.interceptMaxString = 0;
			_print.next = 0;
			_print.messageCount = 0;
			_print.interceptCount = 0;
			_print.size = 0;
			_print.tmp_dir = 0;
			_print.left = 0;
			_print.fontSize = 0;
			_print.fontHeight = 0;
			_print.messages = _context;
			_print.next = _context.length();
			_print.onComplete = false;
			_print.newLine = false;
			_print.messageCount = 0;
			_print.messageBuffer.delete(0, _print.messageBuffer.length());
			if (_isComplete) {
				_print.complete();
			}
			_print.visible = true;
		}
	}

	public void setMessage(String context, IFont font, boolean isComplete) {
		setMessage(context, font, isComplete, false);
	}

	public void setMessage(String context, IFont font, boolean isComplete, boolean drawFont) {
		LSystem.load(new PrintUpdate(this, context, font, isComplete, this.nativeFont = drawFont));
	}

	public String getMessage() {
		return messages;
	}

	private LColor getColor(char flagName) {
		if ('r' == flagName || 'R' == flagName) {
			return LColor.red;
		} else if ('b' == flagName || 'B' == flagName) {
			return LColor.black;
		} else if ('l' == flagName || 'L' == flagName) {
			return LColor.blue;
		} else if ('g' == flagName || 'G' == flagName) {
			return LColor.green;
		} else if ('o' == flagName || 'O' == flagName) {
			return LColor.orange;
		} else if ('y' == flagName || 'Y' == flagName) {
			return LColor.yellow;
		} else if ('m' == flagName || 'M' == flagName) {
			return LColor.magenta;
		} else if ('d' == flagName || 'D' == flagName) {
			return LColor.darkGray;
		} else if ('e' == flagName || 'E' == flagName) {
			return LColor.green;
		} else if ('p' == flagName || 'P' == flagName) {
			return LColor.pink;
		}
		return null;
	}

	public void draw(GLEx g) {
		draw(g, LColor.white);
	}

	private void drawMessage(GLEx gl, LColor old) {
		if (!visible) {
			return;
		}
		if ((strings == null && ifont != null) || nativeFont) {
			drawBMFont(gl, old);
		} else if (strings != null) {
			drawDefFont(gl, old);
		}
	}

	protected int maxFontHeignt(IFont font, char[] showMessages, int size) {
		int height = 0;
		for (int i = 0; i < size; i++) {
			height = MathUtils.max(height, font.stringHeight(String.valueOf(showMessages[i])));
		}
		return MathUtils.max(font.getHeight(), height);
	}

	public void drawDefFont(GLEx g, LColor old) {
		synchronized (showMessages) {
			this.size = showMessages.length;
			this.fontSize = strings.getSize();
			this.fontHeight = maxFontHeignt(strings, showMessages, size);
			switch (dirmode) {
			default:
			case NONE:
				this.tmp_dir = 2;
				break;
			case LEFT:
				this.tmp_dir = (width - (fontSize * messageLength)) / 2 - (int) (fontSize * 1.5);
				break;
			case RIGHT:
				this.tmp_dir = (fontSize * messageLength) / 2;
				break;
			case CENTER:
				this.tmp_dir = width / 2 - (fontSize * messageLength) / 2 + (int) (fontSize * 4);
				break;
			}
			this.left = tmp_dir;
			this.index = offset = font = tmp_font = 0;

			int hashCode = 1;
			hashCode = LSystem.unite(hashCode, size);
			hashCode = LSystem.unite(hashCode, left);
			hashCode = LSystem.unite(hashCode, fontSize);
			hashCode = LSystem.unite(hashCode, fontHeight);

			if (strings == null) {
				return;
			}

			if (hashCode == lazyHashCade) {
				strings.postCharCache();
				if (isIconFlag && iconX != 0 && iconY != 0) {
					fixIconPos();
					g.draw(creeseIcon, iconLocation.x, iconLocation.y);
				}
				return;
			}

			strings.startChar();
			fontColor = old;

			for (int i = 0; i < size; i++) {
				text = showMessages[i];
				if (text == '\0') {
					continue;
				}
				if (interceptCount < interceptMaxString) {
					interceptCount++;
					continue;
				} else {
					interceptMaxString = 0;
					interceptCount = 0;
				}
				if (showMessages[i] == 'n' && showMessages[i > 0 ? i - 1 : 0] == '\\') {
					index = 0;
					left = tmp_dir;
					offset++;
					continue;
				} else if (text == '\n') {
					index = 0;
					left = tmp_dir;
					offset++;
					continue;
				} else if (text == '<') {
					LColor color = getColor(showMessages[i < size - 1 ? i + 1 : i]);
					if (color != null) {
						interceptMaxString = 1;
						fontColor = color;
					}
					continue;
				} else if (showMessages[i > 0 ? i - 1 : i] == '<' && getColor(text) != null) {
					continue;
				} else if (text == '/') {
					if (showMessages[i < size - 1 ? i + 1 : i] == '>') {
						interceptMaxString = 1;
						fontColor = old;
					}
					continue;
				} else if (index > messageLength) {
					index = 0;
					left = tmp_dir;
					offset++;
					newLine = false;
				} else if (text == '\\') {
					continue;
				}
				tmp_font = strings.charWidth(text);
				if (!isEnglish) {
					if (Character.isLetter(text)) {
						if (tmp_font < fontSize) {
							font = fontSize;
						} else {
							font = tmp_font;
						}
					} else {
						font = fontSize;
					}
				} else {
					font = tmp_font;
				}
				left += font;
				if (font <= 10 && StringUtils.isSingle(text)) {
					left += 12;
				}
				if (i != size - 1) {
					strings.addChar(text, printLocation.x + left + leftOffset,
							(offset * fontHeight) + printLocation.y + fontSize + topOffset, fontColor);
				} else if (!newLine && !onComplete) {
					iconX = printLocation.x + left + leftOffset;
					iconY = (offset * fontHeight) + printLocation.y + fontSize + topOffset + strings.getAscent();
					if (isIconFlag && iconX != 0 && iconY != 0) {
						fixIconPos();
						g.draw(creeseIcon, iconLocation.x, iconLocation.y);
					}
				}
				index++;
			}

			strings.stopChar();
			strings.saveCharCache();

			lazyHashCade = hashCode;

			if (messageCount == next) {
				onComplete = true;
			}
		}
	}

	protected PointF fixIconPos() {
		final int iw = creeseIcon.getWidth();
		final int ih = creeseIcon.getHeight();
		final int fixValue = 2;
		iconLocation.set(iconX + offsetIconX, iconY + offsetIconY);
		if (iw + iconLocation.getX() >= printLocation.x + getWidth() - fixValue) {
			iconLocation.x -= iw / fixValue - fixValue;
		}
		if (ih + iconLocation.getY() >= printLocation.y + getHeight() - fixValue) {
			iconLocation.y += ih / fixValue - fixValue;
		}
		return iconLocation;
	}

	public void drawBMFont(GLEx g, LColor old) {
		synchronized (showMessages) {
			this.size = showMessages.length;
			if (nativeFont) {
				this.fontSize = strings.getSize();
				this.fontHeight = maxFontHeignt(strings, showMessages, size);
			} else {
				this.fontSize = ifont.getSize();
				this.fontHeight = maxFontHeignt(ifont, showMessages, size);
			}
			switch (dirmode) {
			default:
			case NONE:
				this.tmp_dir = 0;
				break;
			case LEFT:
				this.tmp_dir = (width - (fontSize * messageLength)) / 2 - (int) (fontSize * 1.5);
				break;
			case RIGHT:
				this.tmp_dir = (fontSize * messageLength) / 2;
				break;
			case CENTER:
				this.tmp_dir = width / 2 - (fontSize * messageLength) / 2 + (int) (fontSize * 4);
				break;
			}
			this.left = tmp_dir;
			this.index = offset = font = tmp_font = 0;
			fontColor = old;
			for (int i = 0; i < size; i++) {
				text = showMessages[i];
				if (text == '\0') {
					continue;
				}
				if (interceptCount < interceptMaxString) {
					interceptCount++;
					continue;
				} else {
					interceptMaxString = 0;
					interceptCount = 0;
				}
				if (showMessages[i] == 'n' && showMessages[i > 0 ? i - 1 : 0] == '\\') {
					index = 0;
					left = tmp_dir;
					offset++;
					continue;
				} else if (text == '\n') {
					index = 0;
					left = tmp_dir;
					offset++;
					continue;
				} else if (text == '<') {
					LColor color = getColor(showMessages[i < size - 1 ? i + 1 : i]);
					if (color != null) {
						interceptMaxString = 1;
						fontColor = color;
					}
					continue;
				} else if (showMessages[i > 0 ? i - 1 : i] == '<' && getColor(text) != null) {
					continue;
				} else if (text == '/') {
					if (showMessages[i < size - 1 ? i + 1 : i] == '>') {
						interceptMaxString = 1;
						fontColor = old;
					}
					continue;
				} else if (index > messageLength) {
					index = 0;
					left = tmp_dir;
					offset++;
					newLine = false;
				} else if (text == '\\') {
					continue;
				}
				String tmpText = String.valueOf(text);
				tmp_font = ifont.charWidth(text);
				if (!isEnglish) {
					if (Character.isLetter(text)) {
						if (tmp_font < fontSize) {
							font = fontSize;
						} else {
							font = tmp_font;
						}
					} else {
						font = fontSize;
					}
				} else {
					font = tmp_font;
				}
				left += font;
				if (font <= 10 && StringUtils.isSingle(text)) {
					left += 12;
				}
				if (i != size - 1) {
					ifont.drawString(g, tmpText, printLocation.x + left + leftOffset,
							(offset * fontHeight) + printLocation.y + fontSize + topOffset, fontColor);
				} else if (!newLine && !onComplete) {
					iconX = printLocation.x + left + leftOffset;
					iconY = (offset * fontHeight) + printLocation.y + fontSize + topOffset + ifont.getAscent();
					if (isIconFlag && iconX != 0 && iconY != 0) {
						fixIconPos();
						g.draw(creeseIcon, iconLocation.x, iconLocation.y);
					}
				}
				index++;
			}
			if (onComplete) {
				if (isIconFlag && iconX != 0 && iconY != 0) {
					fixIconPos();
					g.draw(creeseIcon, iconLocation.x, iconLocation.y);
				}
			}
			if (messageCount == next) {
				onComplete = true;
			}

		}
	}

	public synchronized void draw(GLEx g, LColor old) {
		if (!visible) {
			return;
		}
		alpha = g.alpha();
		if (alpha != 1f) {
			g.setAlpha(1f);
		}
		drawMessage(g, old);
		if (alpha != 1f) {
			g.setAlpha(alpha);
		}
	}

	public Print setX(int x) {
		printLocation.setX(x);
		return this;
	}

	public Print setY(int y) {
		printLocation.setY(y);
		return this;
	}

	public int getX() {
		return printLocation.x();
	}

	public int getY() {
		return printLocation.y();
	}

	public void complete() {
		synchronized (showMessages) {
			this.onComplete = true;
			this.messageCount = messages.length();
			this.next = messageCount;
			this.showMessages = (messages + "_").toCharArray();
			this.size = showMessages.length;
		}
	}

	public boolean isComplete() {
		if (isWait) {
			if (onComplete) {
				wait++;
			}
			return onComplete && wait > 100;
		}
		return onComplete;
	}

	public boolean next() {
		synchronized (messageBuffer) {
			if (!onComplete) {
				if (messageCount == next) {
					onComplete = true;
					return false;
				}
				if (messageBuffer.length() > 0) {
					messageBuffer.delete(messageBuffer.length() - 1, messageBuffer.length());
				}
				this.messageBuffer.append(messages.charAt(messageCount));
				this.messageBuffer.append('_');
				this.showMessages = messageBuffer.toString().toCharArray();
				this.size = showMessages.length;
				this.messageCount++;
			} else {
				return false;
			}
			return true;
		}
	}

	public LTexture getCreeseIcon() {
		return creeseIcon;
	}

	public Print setCreeseIcon(LTexture icon) {
		if (this.creeseIcon != null) {
			creeseIcon.close();
			creeseIcon = null;
		}
		this.creeseIcon = icon;
		return this;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public Print setMessageLength(int messageLength) {
		this.messageLength = messageLength;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public Print setHeight(int height) {
		this.height = height;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public Print setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public Print setLeftOffset(int l) {
		this.leftOffset = l;
		return this;
	}

	public int getTopOffset() {
		return topOffset;
	}

	public Print setTopOffset(int t) {
		this.topOffset = t;
		return this;
	}

	public boolean isEnglish() {
		return isEnglish;
	}

	public Print setEnglish(boolean isEnglish) {
		this.isEnglish = isEnglish;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public Print setVisible(boolean v) {
		this.visible = v;
		return this;
	}

	public Mode getTextMode() {
		return dirmode;
	}

	public Print setTextMode(Mode mode) {
		this.dirmode = mode;
		return this;
	}

	public Print left() {
		setTextMode(Mode.LEFT);
		return this;
	}

	public Print right() {
		setTextMode(Mode.RIGHT);
		return this;
	}

	public Print center() {
		setTextMode(Mode.CENTER);
		return this;
	}

	@Override
	public Print setFont(IFont font) {
		this.ifont = font;
		return this;
	}

	@Override
	public IFont getFont() {
		return ifont;
	}

	@Override
	public Print setFontColor(LColor color) {
		this.fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	public boolean isWait() {
		return isWait;
	}

	public Print setWait(boolean isWait) {
		this.isWait = isWait;
		return this;
	}

	public boolean isIconFlag() {
		return isIconFlag;
	}

	public Print setIconFlag(boolean isIconFlag) {
		this.isIconFlag = isIconFlag;
		return this;
	}

	public float getIconX() {
		return iconX;
	}

	public float getIconY() {
		return iconY;
	}

	public float getOffsetIconX() {
		return offsetIconX;
	}

	public Print setOffsetIconX(float offsetIconX) {
		this.offsetIconX = offsetIconX;
		return this;
	}

	public float getOffsetIconY() {
		return offsetIconY;
	}

	public Print setOffsetIconY(float offsetIconY) {
		this.offsetIconY = offsetIconY;
		return this;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (!nativeFont) {
			if (strings != null) {
				strings.close();
				strings = null;
			}
		}
		if (creeseIcon != null) {
			creeseIcon.close();
			creeseIcon = null;
		}
		closed = true;
	}

}
