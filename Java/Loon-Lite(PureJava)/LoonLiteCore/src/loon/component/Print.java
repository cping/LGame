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
import loon.geom.PointF;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * LMessage的文字打印器具体实现
 */
public final class Print implements FontSet<Print>, LRelease {

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
					sbr.append(LSystem.LF);
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

	private int _index, _offsettext, _curfontSize, _perfontSize;

	private char _textChar;

	private char[] _showMessages;

	private LColor _fontColor = new LColor(LColor.white);

	private int _interceptMaxString;

	private int _interceptCount;

	private int _messageLength = 10;

	private int _waitDealyMax = 100;

	private int _fixEnglishFontSpace = 0;

	private int _fixOtherFontSpace = 12;

	private int _fixMinFontSpace = 6;

	private String _messages;

	private boolean _onComplete, _newLine, _visible, _closed;

	private StrBuilder _messageBuffer = new StrBuilder(_messageLength);

	private LColor _fontGradientTempColor = new LColor();

	private float _alpha, _spaceTextX, _spaceTextY;

	private int _width, _height, _leftoffset, _topoffset, _nextflag, _messageCount;

	private int _textsize, _waitdelay, _textoffsetSize, _leftsize, _fontSize, _fontHeight;

	private final PointF _iconLocation;

	private final Vector2f _printLocation;

	private LTexture _creeseIcon;

	private LSTRFont _defaultFont;

	private IFont _curFont;

	private boolean _nativeFont = false;

	private boolean _gradientFontColor = false;

	private boolean _isEnglish, _isWait, _isIconFlag;

	private float _iconX, _iconY, _offsetIconX, _offsetIconY;

	// 默认0，左1,右2
	private Mode dirmode = Mode.NONE;

	public Print(Vector2f printLocation, IFont font, int width, int height) {
		this(LSystem.EMPTY, font, printLocation, width, height);
	}

	public Print(String context, IFont size, Vector2f pos, int width, int height) {
		this.setMessage(context, size);
		this._printLocation = pos;
		this._width = width;
		this._height = height;
		this._waitdelay = 0;
		this._messageLength = 10;
		this._fixEnglishFontSpace = 0;
		this._fixOtherFontSpace = 12;
		this._fixMinFontSpace = 6;
		this._isWait = false;
		this._isIconFlag = true;
		_iconLocation = new PointF();
	}

	private static class PrintUpdate implements Updateable {

		Print _print;

		boolean _isComplete = false;

		private IFont _font = null;

		private String _context = null;

		private PrintUpdate(Print print, String context, IFont font, boolean complete, boolean drawFont) {
			_print = print;
			if (context != null && print != null) {
				_print.setEnglish(LSTRFont.isAllInBaseCharsPool(context));
			}
			_context = context;
			_font = font;
			_isComplete = complete;
		}

		@Override
		public void action(Object a) {
			if (_context == null) {
				return;
			}
			// 如果是默认的loon系统字体
			_print._curFont = _font;
			_print._waitdelay = 0;
			_print._visible = false;
			_print._showMessages = new char[] { '\0' };
			_print._interceptMaxString = 0;
			_print._nextflag = 0;
			_print._messageCount = 0;
			_print._interceptCount = 0;
			_print._textsize = 0;
			_print._textoffsetSize = 0;
			_print._leftsize = 0;
			_print._fontSize = 0;
			_print._fontHeight = 0;
			_print._messages = _context;
			_print._nextflag = _context.length();
			_print._onComplete = false;
			_print._newLine = false;
			_print._messageCount = 0;
			_print._messageBuffer.setLength(0);
			if (_isComplete) {
				_print.complete();
			}
			_print._visible = true;
		}
	}

	public boolean isGradientFontColor() {
		return _gradientFontColor;
	}

	public Print setGradientFontColor(boolean g) {
		this._gradientFontColor = g;
		this._nativeFont = g;
		return this;
	}

	public void setMessage(String context, IFont font) {
		setMessage(context, font, _gradientFontColor);
	}

	public void setMessage(String context, IFont curfontSize, boolean isComplete) {
		setMessage(context, curfontSize, isComplete, _gradientFontColor);
	}

	public void setMessage(String context, IFont curfontSize, boolean isComplete, boolean drawFont) {
		LSystem.load(new PrintUpdate(this, context, curfontSize, isComplete, this._nativeFont = drawFont));
	}

	public String getMessage() {
		return _messages;
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
		if (!_visible) {
			return;
		}
		if ((_defaultFont == null && _curFont != null) || _nativeFont) {
			drawBMFont(gl, old);
		} else if (_defaultFont != null) {
			drawDefFont(gl, old);
		}
	}

	protected int maxFontHeignt(final IFont curfontSize, final char[] showMessages, final int textsize) {
		int height = 0;
		for (int i = 0; i < textsize; i++) {
			height = MathUtils.max(height, curfontSize.stringHeight(String.valueOf(showMessages[i])));
		}
		return MathUtils.max(curfontSize.getHeight(), height);
	}

	private LColor getGradientFontColor(float curIndex, float maxTextCount, LColor color) {
		if (!_onComplete && _gradientFontColor) {
			float alpha = MathUtils.clamp((1f - curIndex / maxTextCount) + 0.05f, 0, 1f);
			_fontGradientTempColor.setColor(color, alpha);
			return _fontGradientTempColor;
		} else {
			return color;
		}
	}

	public void drawDefFont(GLEx g, LColor old) {
		synchronized (_showMessages) {
			this._textsize = _showMessages.length;
			this._fontSize = _defaultFont.getSize();
			this._fontHeight = maxFontHeignt(_defaultFont, _showMessages, _textsize);
			switch (dirmode) {
			default:
			case NONE:
				this._textoffsetSize = 2;
				break;
			case LEFT:
				this._textoffsetSize = (_width - (_fontSize * _messageLength)) / 2 - (int) (_fontSize * 1.5);
				break;
			case RIGHT:
				this._textoffsetSize = (_fontSize * _messageLength) / 2;
				break;
			case CENTER:
				this._textoffsetSize = _width / 2 - (_fontSize * _messageLength) / 2 + _fontSize * 4;
				break;
			}
			this._leftsize = _textoffsetSize;
			this._index = _offsettext = _curfontSize = _perfontSize = 0;

			if (_defaultFont == null) {
				return;
			}

			final int minTextSize = MathUtils.ifloor(_fontSize * 0.45f);
			final int maxTextSize = MathUtils.ifloor(_fontSize * 0.9f);
			_fontColor = old;

			for (int i = 0; i < _textsize; i++) {
				_textChar = _showMessages[i];
				if (_textChar == '\0') {
					continue;
				}
				if (_interceptCount < _interceptMaxString) {
					_interceptCount++;
					continue;
				} else {
					_interceptMaxString = 0;
					_interceptCount = 0;
				}
				if (_showMessages[i] == 'n' && _showMessages[i > 0 ? i - 1 : 0] == LSystem.BACKSLASH) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == LSystem.LF) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == '<') {
					LColor color = getColor(_showMessages[i < _textsize - 1 ? i + 1 : i]);
					if (color != null) {
						_interceptMaxString = 1;
						_fontColor = color;
					}
					continue;
				} else if (_showMessages[i > 0 ? i - 1 : i] == '<' && getColor(_textChar) != null) {
					continue;
				} else if (_textChar == LSystem.SLASH) {
					if (_showMessages[i < _textsize - 1 ? i + 1 : i] == '>') {
						_interceptMaxString = 1;
						_fontColor = old;
					}
					continue;
				} else if (_index > _messageLength) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					_newLine = false;
				} else if (_textChar == LSystem.BACKSLASH) {
					continue;
				}
				_perfontSize = _defaultFont.charWidth(_textChar);
				if (!_isEnglish) {
					if (Character.isLetter(_textChar)) {
						if (_perfontSize < _fontSize) {
							_curfontSize = _fontSize;
						} else {
							_curfontSize = _perfontSize;
						}
					} else {
						_curfontSize = _fontSize;
					}
				} else {
					_curfontSize = MathUtils.clamp(_perfontSize, minTextSize, maxTextSize);
				}
				_curfontSize = MathUtils.max(_fixMinFontSpace, _curfontSize);
				_leftsize += _curfontSize;
				if (!_isEnglish && _curfontSize <= _fixOtherFontSpace && StringUtils.isSingle(_textChar)) {
					_leftsize += _fixOtherFontSpace;
				} else if (_isEnglish) {
					_leftsize += _fixEnglishFontSpace;
				}
				if (i != _textsize - 1) {
					g.drawString(String.valueOf(_textChar), (_printLocation.x + _leftsize + _leftoffset) + _spaceTextX,
							((_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset) + _spaceTextY,
							_fontColor);
				} else if (!_newLine && !_onComplete) {
					_iconX = _printLocation.x + _leftsize + _leftoffset;
					_iconY = (_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset
							+ _defaultFont.getAscent();
					if (_isIconFlag && _iconX != 0 && _iconY != 0) {
						fixIconPos();
						g.draw(_creeseIcon, _iconLocation.x + _spaceTextX, _iconLocation.y + _spaceTextY);
					}
				}
				_index++;
			}

			if (_messageCount == _nextflag) {
				_onComplete = true;
			}
		}
	}

	protected PointF fixIconPos() {
		final int iw = _creeseIcon.getWidth();
		final int ih = _creeseIcon.getHeight();
		final int fixValue = 2;
		_iconLocation.set(_iconX + _offsetIconX, _iconY + _offsetIconY);
		if (iw + _iconLocation.getX() >= _printLocation.x + getWidth() - fixValue) {
			_iconLocation.x -= iw / fixValue - fixValue;
		}
		if (ih + _iconLocation.getY() >= _printLocation.y + getHeight() - fixValue) {
			_iconLocation.y += ih / fixValue - fixValue;
		}
		return _iconLocation;
	}

	public float getSpaceTextX() {
		return _spaceTextX;
	}

	public float getSpaceTextY() {
		return _spaceTextY;
	}

	public Print setSpaceTextX(float x) {
		this._spaceTextX = x;
		return this;
	}

	public Print setSpaceTextY(float y) {
		this._spaceTextY = y;
		return this;
	}

	public void drawBMFont(GLEx g, LColor old) {
		synchronized (_showMessages) {
			this._textsize = _showMessages.length;
			if (_nativeFont && _defaultFont != null) {
				this._fontSize = _defaultFont.getSize();
				this._fontHeight = maxFontHeignt(_defaultFont, _showMessages, _textsize);
			} else {
				this._fontSize = _curFont.getSize();
				this._fontHeight = maxFontHeignt(_curFont, _showMessages, _textsize);
			}
			switch (dirmode) {
			default:
			case NONE:
				this._textoffsetSize = 0;
				break;
			case LEFT:
				this._textoffsetSize = (_width - (_fontSize * _messageLength)) / 2 - (int) (_fontSize * 1.5);
				break;
			case RIGHT:
				this._textoffsetSize = (_fontSize * _messageLength) / 2;
				break;
			case CENTER:
				this._textoffsetSize = _width / 2 - (_fontSize * _messageLength) / 2 + _fontSize * 4;
				break;
			}

			final int minTextSize = MathUtils.ifloor(_fontSize * 0.45f);
			final int maxTextSize = MathUtils.ifloor(_fontSize * 0.9f);
			this._leftsize = _textoffsetSize;
			this._index = _offsettext = _curfontSize = _perfontSize = 0;
			_fontColor = old;
			for (int i = 0; i < _textsize; i++) {
				_textChar = _showMessages[i];
				if (_textChar == '\0') {
					continue;
				}
				if (_interceptCount < _interceptMaxString) {
					_interceptCount++;
					continue;
				} else {
					_interceptMaxString = 0;
					_interceptCount = 0;
				}
				if (_showMessages[i] == 'n' && _showMessages[i > 0 ? i - 1 : 0] == LSystem.BACKSLASH) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == LSystem.LF) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == '<') {
					LColor color = getColor(_showMessages[i < _textsize - 1 ? i + 1 : i]);
					if (color != null) {
						_interceptMaxString = 1;
						_fontColor = color;
					}
					continue;
				} else if (_showMessages[i > 0 ? i - 1 : i] == '<' && getColor(_textChar) != null) {
					continue;
				} else if (_textChar == LSystem.SLASH) {
					if (_showMessages[i < _textsize - 1 ? i + 1 : i] == '>') {
						_interceptMaxString = 1;
						_fontColor = old;
					}
					continue;
				} else if (_index > _messageLength) {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					_newLine = false;
				} else if (_textChar == LSystem.BACKSLASH) {
					continue;
				}
				String tmpText = String.valueOf(_textChar);
				_perfontSize = _curFont.charWidth(_textChar);
				if (!_isEnglish) {
					if (Character.isLetter(_textChar)) {
						if (_perfontSize < _fontSize) {
							_curfontSize = _fontSize;
						} else {
							_curfontSize = _perfontSize;
						}
					} else {
						_curfontSize = _fontSize;
					}
				} else {
					_curfontSize = MathUtils.clamp(_perfontSize, minTextSize, maxTextSize);
				}
				_curfontSize = MathUtils.max(_fixMinFontSpace, _curfontSize);
				_leftsize += _curfontSize;
				if (!_isEnglish && _curfontSize <= _fixOtherFontSpace && StringUtils.isSingle(_textChar)) {
					_leftsize += _fixOtherFontSpace;
				} else if (_isEnglish) {
					_leftsize += _fixEnglishFontSpace;
				}
				if (i != _textsize - 1) {
					_curFont.drawString(g, tmpText, (_printLocation.x + _leftsize + _leftoffset) + _spaceTextX,
							((_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset) + _spaceTextY,
							getGradientFontColor(i, _textsize, _fontColor));
				} else if (!_newLine && !_onComplete) {
					_iconX = _printLocation.x + _leftsize + _leftoffset;
					_iconY = (_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset
							+ _curFont.getAscent();
					if (_isIconFlag && _iconX != 0 && _iconY != 0) {
						fixIconPos();
						g.draw(_creeseIcon, _iconLocation.x + _spaceTextX, _iconLocation.y + _spaceTextY);
					}
				}
				_index++;
			}
			if (_onComplete) {
				if (_isIconFlag && _iconX != 0 && _iconY != 0) {
					fixIconPos();
					g.draw(_creeseIcon, _iconLocation.x + _spaceTextX, _iconLocation.y + _spaceTextY);
				}
			}
			if (_messageCount == _nextflag) {
				_onComplete = true;
			}

		}
	}

	public Print setFixOtherFontSpace(int f) {
		this._fixOtherFontSpace = f;
		return this;
	}

	public int getFixOtherFontSpace() {
		return this._fixOtherFontSpace;
	}

	public Print setFixEnglishFontSpace(int f) {
		this._fixEnglishFontSpace = f;
		return this;
	}

	public int getFixEnglishFontSpace() {
		return this._fixEnglishFontSpace;
	}

	public Print setFixMinFontSpace(int f) {
		this._fixMinFontSpace = f;
		return this;
	}

	public int getFixMinFontSpace() {
		return this._fixMinFontSpace;
	}

	public synchronized void draw(GLEx g, LColor old) {
		if (!_visible) {
			return;
		}
		_alpha = g.alpha();
		if (_alpha != 1f) {
			g.setAlpha(1f);
		}
		drawMessage(g, old);
		if (_alpha != 1f) {
			g.setAlpha(_alpha);
		}
	}

	public Print setX(int x) {
		_printLocation.setX(x);
		return this;
	}

	public Print setY(int y) {
		_printLocation.setY(y);
		return this;
	}

	public int getX() {
		return _printLocation.x();
	}

	public int getY() {
		return _printLocation.y();
	}

	public void complete() {
		this._onComplete = true;
		this._messageCount = _messages.length();
		this._nextflag = _messageCount;
		this._showMessages = (_messages + "_").toCharArray();
		this._textsize = _showMessages.length;
	}

	public boolean isComplete() {
		if (_isWait) {
			if (_onComplete) {
				_waitdelay++;
			}
			return _onComplete && _waitdelay > _waitDealyMax;
		}
		return _onComplete;
	}

	public int getWaitDelayMax() {
		return _waitDealyMax;
	}

	public Print setWaitDelayMax(int w) {
		this._waitDealyMax = w;
		return this;
	}

	public boolean next() {
		synchronized (_messageBuffer) {
			if (!_onComplete) {
				if (_messageCount == _nextflag) {
					_onComplete = true;
					return false;
				}
				if (_messageBuffer.length() > 0) {
					_messageBuffer.delete(_messageBuffer.length() - 1, _messageBuffer.length());
				}
				this._messageBuffer.append(_messages.charAt(_messageCount));
				this._messageBuffer.append(LSystem.UNDERLINE);
				this._showMessages = _messageBuffer.toString().toCharArray();
				this._textsize = _showMessages.length;
				this._messageCount++;
			} else {
				return false;
			}
			return true;
		}
	}

	public LTexture getCreeseIcon() {
		return _creeseIcon;
	}

	public Print setCreeseIcon(LTexture icon) {
		if (this._creeseIcon != null) {
			_creeseIcon.close();
			_creeseIcon = null;
		}
		this._creeseIcon = icon;
		return this;
	}

	public int getMessageLength() {
		return _messageLength;
	}

	public Print setMessageLength(int l) {
		this._messageLength = l;
		return this;
	}

	public int getHeight() {
		return _height;
	}

	public Print setHeight(int h) {
		this._height = h;
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public Print setWidth(int w) {
		this._width = w;
		return this;
	}

	public int getLeftOffset() {
		return _leftoffset;
	}

	public Print setLeftOffset(int l) {
		this._leftoffset = l;
		return this;
	}

	public int getTopOffset() {
		return _topoffset;
	}

	public Print setTopOffset(int t) {
		this._topoffset = t;
		return this;
	}

	public boolean isEnglish() {
		return _isEnglish;
	}

	public Print setEnglish(boolean e) {
		this._isEnglish = e;
		return this;
	}

	public boolean isVisible() {
		return _visible;
	}

	public Print setVisible(boolean v) {
		this._visible = v;
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
	public Print setFont(IFont f) {
		this._curFont = f;
		return this;
	}

	@Override
	public IFont getFont() {
		return _curFont;
	}

	@Override
	public Print setFontColor(LColor color) {
		this._fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	public boolean isWait() {
		return _isWait;
	}

	public Print setWait(boolean w) {
		this._isWait = w;
		return this;
	}

	public boolean isIconFlag() {
		return _isIconFlag;
	}

	public Print setIconFlag(boolean f) {
		this._isIconFlag = f;
		return this;
	}

	public float getIconX() {
		return _iconX;
	}

	public float getIconY() {
		return _iconY;
	}

	public float getOffsetIconX() {
		return _offsetIconX;
	}

	public Print setOffsetIconX(float x) {
		this._offsetIconX = x;
		return this;
	}

	public float getOffsetIconY() {
		return _offsetIconY;
	}

	public Print setOffsetIconY(float y) {
		this._offsetIconY = y;
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (!_nativeFont) {
			if (_defaultFont != null) {
				_defaultFont.close();
				_defaultFont = null;
			}
		}
		if (_creeseIcon != null) {
			_creeseIcon.close();
			_creeseIcon = null;
		}
		_closed = true;
	}

}
