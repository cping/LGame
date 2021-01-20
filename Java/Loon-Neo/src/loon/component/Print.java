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

	private int _lazyFlag = 1;
	
	private String _messages;

	private boolean _onComplete, _newLine, _visible, _closed;

	private StrBuilder _messageBuffer = new StrBuilder(_messageLength);

	private float _alpha;
	
	private int _width, _height, _leftoffset, _topoffset, _nextflag, _messageCount;

	private int _textsize, _waitdelay, _textoffsetSize, _leftsize, _fontSize, _fontHeight;

	private final PointF _iconLocation;

	private final Vector2f _printLocation;

	private LTexture _creeseIcon;

	private LSTRFont _defaultFont;

	private IFont _curFont;

	private boolean _nativeFont = false;

	private boolean _isEnglish, _isWait, _isIconFlag;

	private float _iconX, _iconY, _offsetIconX, _offsetIconY;

	// 默认0，左1,右2
	private Mode dirmode = Mode.NONE;

	public Print(Vector2f _printLocation, IFont font, int width, int height) {
		this(LSystem.EMPTY,  font, _printLocation, width, height);
	}

	public Print(String context, IFont size, Vector2f pos, int width, int height) {
		this.setMessage(context, size);
		this._printLocation = pos;
		this._width = width;
		this._height = height;
		this._waitdelay = 0;
		this._lazyFlag = 1;
		this._messageLength = 10;
		this._isWait = false;
		this._isIconFlag = true;
		_iconLocation = new PointF();
	}

	public void setMessage(String context, IFont font) {
		setMessage(context, font, false);
	}

	private static class PrintUpdate implements Updateable {

		Print _print;

		boolean _isComplete = false, _drawDrawingFont = false;

		private IFont _font = null;

		private String _context = null;

		private PrintUpdate(Print print, String context, IFont font, boolean complete, boolean drawFont) {
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
			_isComplete = complete;
			_drawDrawingFont = drawFont;
		}

		@Override
		public void action(Object a) {
			if (_context == null) {
				return;
			}
			if (_print._defaultFont != null && !_print._defaultFont.isClosed() && !_drawDrawingFont) {
				_print._defaultFont.close();
			}
			// 如果是默认的loon系统字体
			if (_font instanceof LFont) {
				if (_drawDrawingFont) {
					LSTRDictionary.Dict dict = LSTRDictionary.get().bind((LFont) _font, _context);
					_print._defaultFont = dict.getSTR();
					_print._curFont = _font;
				} else {
					_print._defaultFont = new LSTRFont((LFont) _font, _context, LSystem.isHTML5());
				}
				// 其他字体(一般是Bitmap Font)
			} else {
				_print._curFont = _font;
			}
			_print._lazyFlag = 1;
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

	public void setMessage(String context, IFont _curfontSize, boolean isComplete) {
		setMessage(context, _curfontSize, isComplete, false);
	}

	public void setMessage(String context, IFont _curfontSize, boolean isComplete, boolean drawFont) {
		LSystem.load(new PrintUpdate(this, context, _curfontSize, isComplete, this._nativeFont = drawFont));
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

	protected int maxFontHeignt(IFont _curfontSize, char[] _showMessages, int _textsize) {
		int _height = 0;
		for (int i = 0; i < _textsize; i++) {
			_height = MathUtils.max(_height, _curfontSize.stringHeight(String.valueOf(_showMessages[i])));
		}
		return MathUtils.max(_curfontSize.getHeight(), _height);
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
				this._textoffsetSize = _width / 2 - (_fontSize * _messageLength) / 2 + (int) (_fontSize * 4);
				break;
			}
			this._leftsize = _textoffsetSize;
			this._index = _offsettext = _curfontSize = _perfontSize = 0;

			int hashCode = 1;
			hashCode = LSystem.unite(hashCode, _textsize);
			hashCode = LSystem.unite(hashCode, _leftsize);
			hashCode = LSystem.unite(hashCode, _fontSize);
			hashCode = LSystem.unite(hashCode, _fontHeight);

			if (_defaultFont == null) {
				return;
			}

			if (hashCode == _lazyFlag) {
				_defaultFont.postCharCache();
				if (_isIconFlag && _iconX != 0 && _iconY != 0) {
					fixIconPos();
					g.draw(_creeseIcon, _iconLocation.x, _iconLocation.y);
				}
				return;
			}

			_defaultFont.startChar();
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
				if (_showMessages[i] == 'n' && _showMessages[i > 0 ? i - 1 : 0] == '\\') {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == '\n') {
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
				} else if (_textChar == '/') {
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
				} else if (_textChar == '\\') {
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
					_curfontSize = _perfontSize;
				}
				_leftsize += _curfontSize;
				if (_curfontSize <= 10 && StringUtils.isSingle(_textChar)) {
					_leftsize += 12;
				}
				if (i != _textsize - 1) {
					_defaultFont.addChar(_textChar, _printLocation.x + _leftsize + _leftoffset,
							(_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset, _fontColor);
				} else if (!_newLine && !_onComplete) {
					_iconX = _printLocation.x + _leftsize + _leftoffset;
					_iconY = (_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset + _defaultFont.getAscent();
					if (_isIconFlag && _iconX != 0 && _iconY != 0) {
						fixIconPos();
						g.draw(_creeseIcon, _iconLocation.x, _iconLocation.y);
					}
				}
				_index++;
			}

			_defaultFont.stopChar();
			_defaultFont.saveCharCache();

			_lazyFlag = hashCode;

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

	public void drawBMFont(GLEx g, LColor old) {
		synchronized (_showMessages) {
			this._textsize = _showMessages.length;
			if (_nativeFont) {
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
				this._textoffsetSize = _width / 2 - (_fontSize * _messageLength) / 2 + (int) (_fontSize * 4);
				break;
			}
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
				if (_showMessages[i] == 'n' && _showMessages[i > 0 ? i - 1 : 0] == '\\') {
					_index = 0;
					_leftsize = _textoffsetSize;
					_offsettext++;
					continue;
				} else if (_textChar == '\n') {
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
				} else if (_textChar == '/') {
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
				} else if (_textChar == '\\') {
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
					_curfontSize = _perfontSize;
				}
				_leftsize += _curfontSize;
				if (_curfontSize <= 10 && StringUtils.isSingle(_textChar)) {
					_leftsize += 12;
				}
				if (i != _textsize - 1) {
					_curFont.drawString(g, tmpText, _printLocation.x + _leftsize + _leftoffset,
							(_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset, _fontColor);
				} else if (!_newLine && !_onComplete) {
					_iconX = _printLocation.x + _leftsize + _leftoffset;
					_iconY = (_offsettext * _fontHeight) + _printLocation.y + _fontSize + _topoffset + _curFont.getAscent();
					if (_isIconFlag && _iconX != 0 && _iconY != 0) {
						fixIconPos();
						g.draw(_creeseIcon, _iconLocation.x, _iconLocation.y);
					}
				}
				_index++;
			}
			if (_onComplete) {
				if (_isIconFlag && _iconX != 0 && _iconY != 0) {
					fixIconPos();
					g.draw(_creeseIcon, _iconLocation.x, _iconLocation.y);
				}
			}
			if (_messageCount == _nextflag) {
				_onComplete = true;
			}

		}
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
		synchronized (_showMessages) {
			this._onComplete = true;
			this._messageCount = _messages.length();
			this._nextflag = _messageCount;
			this._showMessages = (_messages + "_").toCharArray();
			this._textsize = _showMessages.length;
		}
	}

	public boolean isComplete() {
		if (_isWait) {
			if (_onComplete) {
				_waitdelay++;
			}
			return _onComplete && _waitdelay > 100;
		}
		return _onComplete;
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
				this._messageBuffer.append('_');
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
