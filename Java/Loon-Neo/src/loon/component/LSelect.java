/**
 * Copyright 2008 - 2010
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
 * @version 0.1
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.skin.SelectSkin;
import loon.component.skin.SkinManager;
import loon.events.ActionKey;
import loon.events.SysKey;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 一个选项器UI,与LMenuSelect的最大区别在于这个的UI大小是固定的,而LMenuSelect会随着注入的内容不同而自行改变UI大小
 */
public class LSelect extends LContainer implements FontSet<LSelect> {

	private LTexture _tempTexture = null;

	private IFont _messageFont;

	private LColor _fontColor = LColor.white;

	private LColor _cursorColor = LColor.white;

	private int _left, _top, _type, _nTop;

	private int _sizeFont, _doubleSizeFont, _tmpOffset;

	private int _messageLeft, _nLeft, _messageTop, _selectSize, _selectFlag;

	private int _space;

	private float _autoAlpha;

	private LTimer _delay;

	private String[] _selects;

	private String _message, _result;

	private LTexture _cursor, _buoyage;

	private boolean _isAutoAlpha, _isSelect;

	private boolean _clicked;

	private ActionKey _eventClick = new ActionKey();

	public LSelect(int x, int y, int width, int height) {
		this(SkinManager.get().getMessageSkin().getFont(), x, y, width, height);
	}

	public LSelect(IFont font, int x, int y, int width, int height) {
		this(font, (LTexture) null, x, y, width, height);
	}

	public LSelect(String fileName) {
		this(SkinManager.get().getMessageSkin().getFont(), fileName);
	}

	public LSelect(IFont font, String fileName) {
		this(font, fileName, 0, 0);
	}

	public LSelect(IFont font, String fileName, int x, int y) {
		this(font, LSystem.loadTexture(fileName), x, y);
	}

	public LSelect(LTexture formImage) {
		this(SkinManager.get().getMessageSkin().getFont(), formImage);
	}

	public LSelect(IFont font, LTexture formImage) {
		this(font, formImage, 0, 0);
	}

	public LSelect(IFont font, LTexture formImage, int x, int y) {
		this(font, formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LSelect(LTexture formImage, int x, int y) {
		this(SkinManager.get().getMessageSkin().getFont(), formImage, x, y, formImage.getWidth(),
				formImage.getHeight());
	}

	public LSelect(IFont font, LTexture formImage, int x, int y, int width, int height) {
		this(font, formImage, x, y, width, height, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LSelect(SelectSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LSelect(IFont font, LTexture formImage, int x, int y, int width, int height, LColor fontColor) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(createTempTexture(width, height, 0.3f, 15f));
		} else {
			this.setBackground(formImage);
		}
		this._fontColor = fontColor;
		this._messageFont = (font == null ? LSystem.getSystemGameFont() : font);
		this.customRendering = true;
		this._selectFlag = -1;
		this._space = 30;
		this._tmpOffset = -(width / 10);
		this._delay = new LTimer(150);
		this._autoAlpha = 0.25F;
		this._isAutoAlpha = true;
		this.setCursor(LSystem.getSystemImagePath() + "creese.png");
		this.setElastic(true);
		this.setLocked(true);
	}

	public LSelect setMessage(String message, TArray<String> list) {
		return setMessage(message, StringUtils.toStrings(list));
	}

	public LSelect setMessage(String[] selects) {
		return setMessage(null, selects);
	}

	public LSelect setMessage(TArray<String> list) {
		return setMessage(null, list);
	}

	public LSelect setMessage(String message, String[] selects) {
		this._message = message;
		this._selects = selects;
		this._selectSize = selects.length;
		if (_doubleSizeFont == 0) {
			_doubleSizeFont = LSystem.getFontSize();
		}
		if (_messageFont instanceof LFont) {
			LSTRDictionary.get().bind((LFont) _messageFont, selects);
		}
		return this;
	}

	protected LTexture createTempTexture(int w, int h, float alpha, float r) {
		Image img = Image.createImage(w, h);
		Canvas canvas = img.getCanvas();
		canvas.setAlpha(alpha);
		canvas.fillRoundRect(0, 0, w, h, r);
		return (_tempTexture = img.texture());
	}

	public LSelect setLeftOffset(int left) {
		this._left = left;
		return this;
	}

	public LSelect setTopOffset(int top) {
		this._top = top;
		return this;
	}

	public int getLeftOffset() {
		return _left;
	}

	public int getTopOffset() {
		return _top;
	}

	public int getResultIndex() {
		return _selectFlag - 1;
	}

	public LSelect setDelay(long timer) {
		_delay.setDelay(timer);
		return this;
	}

	public long getDelay() {
		return _delay.getDelay();
	}

	public String getResult() {
		return _result;
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (_isAutoAlpha && _buoyage != null) {
			if (_delay.action(elapsedTime)) {
				if (_autoAlpha < 0.95F) {
					_autoAlpha += 0.05F;
				} else {
					_autoAlpha = 0.25F;
				}
			}
		}
		if (!isClickUp()) {
			if (_selects != null) {
				final int touchY = _input.getTouchIntY();
				_selectFlag = _selectSize - (((_nTop + _space) - (touchY == 0 ? 1 : touchY)) / _doubleSizeFont);
				if (_selectFlag < 1) {
					_selectFlag = 0;
				}
				if (_selectFlag > _selectSize) {
					_selectFlag = _selectSize;
				}
			}
		}
	}

	@Override
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!isVisible()) {
			return;
		}
		final int oldColor = g.color();
		_sizeFont = _messageFont.getSize();
		_doubleSizeFont = _sizeFont * 2;
		if (_doubleSizeFont == 0) {
			_doubleSizeFont = LSystem.getFontSize();
		}
		_messageLeft = (x + _doubleSizeFont + _sizeFont / 2) + _tmpOffset + _left + _doubleSizeFont;
		if (_message != null) {
			_messageTop = y + _doubleSizeFont + _top - 10;
			_messageFont.drawString(g, _message, _messageLeft, _messageTop - _messageFont.getAscent(), _fontColor);
		} else {
			_messageTop = y + _top;
		}
		_nTop = _messageTop;
		if (_selects != null) {
			_nLeft = _messageLeft - _sizeFont / 4;
			for (int i = 0; i < _selects.length; i++) {
				_nTop += _space;
				_type = i + 1;
				_isSelect = (_type == (_selectFlag > 0 ? _selectFlag : 1));
				if ((_buoyage != null) && _isSelect) {
					g.setAlpha(_autoAlpha);
					g.draw(_buoyage, _nLeft, _nTop - MathUtils.iceil(_buoyage.getHeight() / 1.5f),
							_component_baseColor);
					g.setAlpha(1F);
				}
				_messageFont.drawString(g, _selects[i], _messageLeft, _nTop - _messageFont.getAscent(), _fontColor);
				if ((_cursor != null) && _isSelect) {
					g.draw(_cursor, _nLeft, _nTop - _cursor.getHeight() / 2, _cursorColor);
				}

			}
		}
		g.setColor(oldColor);
	}

	public LSelect setCursorColor(LColor c) {
		_cursorColor = c;
		return this;
	}

	public LColor getCursorColor() {
		return _cursorColor;
	}

	public boolean isClick() {
		return _clicked;
	}

	@Override
	protected void processTouchPressed() {
		if (!_eventClick.isPressed()) {
			this._clicked = false;
			super.processTouchPressed();
			_eventClick.press();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (_eventClick.isPressed()) {
			this._clicked = true;
			if ((this._selects != null) && (this._selectFlag > 0)) {
				this._result = this._selects[_selectFlag - 1];
			}
			super.processTouchReleased();
			_eventClick.release();
		}
	}

	@Override
	protected void processKeyPressed() {
		super.processKeyPressed();
		if (this.isSelected() && this.isKeyDown(SysKey.ENTER)) {
			this._clicked = true;
		}
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	public LSelect setFontColor(LColor f) {
		this._fontColor = f;
		return this;
	}

	public IFont getMessageFont() {
		return _messageFont;
	}

	public LSelect setMessageFont(IFont m) {
		this._messageFont = m;
		return this;
	}

	@Override
	public LSelect setFont(IFont newFont) {
		return this.setMessageFont(newFont);
	}

	@Override
	public IFont getFont() {
		return getMessageFont();
	}

	public LTexture getCursor() {
		return _cursor;
	}

	public LSelect setNotCursor() {
		this._cursor = null;
		return this;
	}

	public LSelect setCursor(LTexture cursor) {
		this._cursor = cursor;
		return this;
	}

	public LSelect setCursor(String fileName) {
		setCursor(LSystem.loadTexture(fileName));
		return this;
	}

	public LTexture getBuoyage() {
		return _buoyage;
	}

	public LSelect setNotBuoyage() {
		this._cursor = null;
		return this;
	}

	public LSelect setBuoyage(LTexture buoyage) {
		this._buoyage = buoyage;
		return this;
	}

	public LSelect setBuoyage(String fileName) {
		setBuoyage(LSystem.loadTexture(fileName));
		return this;
	}

	public boolean isFlashBuoyage() {
		return _isAutoAlpha;
	}

	public LSelect setFlashBuoyage(boolean flashBuoyage) {
		this._isAutoAlpha = flashBuoyage;
		return this;
	}

	public int getSpace() {
		return _space;
	}

	public LSelect setSpace(int space) {
		this._space = space;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "Select";
	}

	@Override
	public void destory() {
		if (_tempTexture != null) {
			_tempTexture.close(true);
			_tempTexture = null;
		}
	}

}
