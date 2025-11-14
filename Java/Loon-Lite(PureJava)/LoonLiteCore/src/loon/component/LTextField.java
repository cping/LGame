/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 * Example1:
 * 
 *          new LTextField(LSystem.EMPTY, 0, 0,35);
 *          
 *  Example3:
 *           
 *          LTextField field = new LTextField(LSystem.EMPTY, 0,0,35);
 *  		field.setFontColor(LColor.white);
 *  		field.setHideBackground(true);
 * 
 *  文本输入类，可以用setHideBackground函数隐藏背景，从而把其放置到理想的输入背景中
 * 
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.events.ActionKey;
import loon.events.GameKey;
import loon.events.SysInputFactory;
import loon.events.SysKey;
import loon.events.SysInputFactory.OnscreenKeyboard;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.CharUtils;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 文字输入用UI
 */
public class LTextField extends LTextBar {

	public static final int INPUT_STRING = 0;

	public static final int INPUT_SIGNED_INTEGER_NUM = 1;

	public static final int INPUT_UNSIGNED_INTEGER_NUM = 2;

	public static final int INPUT_INTEGER = INPUT_SIGNED_INTEGER_NUM;

	public static final int INPUT_SIGNED_FLOATING_POINT_NUM = 3;

	public static final int INPUT_UNSIGNED_FLOATING_POINT_NUM = 4;

	public static final int INPUT_FLOATING_POINT_NUM = INPUT_SIGNED_INTEGER_NUM;

	private ActionKey _keyLock = new ActionKey();

	public static LTextField at(int x, int y) {
		return new LTextField(x, y);
	}

	public OnscreenKeyboard getOnscreenKeyboard() {
		return SysInputFactory.getKeyBoard();
	}

	public void setOnscreenKeyboard(OnscreenKeyboard keyboard) {
		SysInputFactory.setKeyBoard(keyboard);
	}

	protected int _inputType = INPUT_STRING;
	protected int _startidx, _limit;

	public LTextField(String txt, LTexture left, LTexture right, LTexture body, int x, int y, LColor textcolor,
			int type, int limit) {
		this(SkinManager.get().getTextBarSkin().getFont(), txt, left, right, body, x, y, textcolor, type, limit);
	}

	public LTextField(IFont font, String txt, int x, int y, LColor textcolor, int inp, int limit) {
		this(font, txt, SkinManager.get().getTextBarSkin().getLeftTexture(),
				SkinManager.get().getTextBarSkin().getRightTexture(),
				SkinManager.get().getTextBarSkin().getBodyTexture(), x, y, textcolor, inp, limit);
	}

	public LTextField(String txt, int x, int y, LColor textcolor, int inp, int limit) {
		this(txt, SkinManager.get().getTextBarSkin().getLeftTexture(),
				SkinManager.get().getTextBarSkin().getRightTexture(),
				SkinManager.get().getTextBarSkin().getBodyTexture(), x, y, textcolor, inp, limit);
	}

	public LTextField(String txt, LTexture left, LTexture right, LTexture body, int x, int y, int type, int limit) {
		this(txt, left, right, body, x, y, SkinManager.get().getTextBarSkin().getFontColor(), type, limit);
	}

	public LTextField(String txt, int x, int y, int type, int limit) {
		this(txt, x, y, SkinManager.get().getTextBarSkin().getFontColor(), type, limit);
	}

	public LTextField(String txt, int x, int y, int limit) {
		this(txt, x, y, SkinManager.get().getTextBarSkin().getFontColor(), INPUT_STRING, limit);
	}

	public LTextField() {
		this(0, 0);
	}

	public LTextField(int x, int y) {
		this(LSystem.EMPTY, x, y, SkinManager.get().getTextBarSkin().getFontColor(), INPUT_STRING, 128);
	}

	public LTextField(String txt, int x, int y) {
		this(txt, x, y, SkinManager.get().getTextBarSkin().getFontColor(), INPUT_STRING, 128);
	}

	public LTextField(IFont font, String txt, LTexture left, LTexture right, LTexture body, int x, int y,
			LColor textcolor, int type, int limit) {
		super(txt, left, right, body, x, y, textcolor, font);
		this._font = font;
		this._inputType = type;
		if (txt != null) {
			this._startidx = txt.length();
		}
		this._limit = limit + _startidx;
		this.setFocusable(true);
		this.setFlashCursor(true);
		freeRes().add(left, right, body);
	}

	public int getInputType() {
		return _inputType;
	}

	public LTextField setInputType(int type) {
		_inputType = type;
		return this;
	}

	public String getInput() {
		String result = _text.substring(_startidx);
		if ((result.charAt(result.length() - 1) == _cursor || result.length() == 0) && _inputType != INPUT_STRING) {
			return "0";
		}
		return result;
	}

	public boolean wasEntered() {
		return this._input.getKeyPressed() == SysKey.ENTER || !this.isFocusable();
	}

	@Override
	protected void keyReleased(GameKey key) {
		super.keyReleased(key);
		_keyLock.release();
	}

	@Override
	protected void keyPressed(GameKey key) {
		super.keyPressed(key);

		if (!isDesktopFocusable()) {
			return;
		}
		if (_keyLock.isPressed()) {
			return;
		}

		char nextchar = key.getKeyChar();
		if (nextchar == 0 && (StringUtils.isCJK(nextchar) || CharUtils.isAlphaOrDigit(nextchar))) {
			return;
		}
		switch (nextchar) {
		case LSystem.BACKSLASH:
		case LSystem.TAB:
		case LSystem.LF:
		case LSystem.CR:
			break;
		case LSystem.ERRORCODE1:
		case LSystem.ERRORCODE2:
			return;
		default:
			if (nextchar < 32) {
				return;
			}
		}
		boolean isatstart = _text.length() == _startidx;
		if (((key.getKeyCode() == SysKey.BACK) || (key.getKeyCode() == SysKey.BACKSPACE)) && _text.length() != 0
				&& !isatstart) {
			_text = _text.substring(0, _text.length() - 1);
			return;
		}
		// input data max length
		if (_text.length() == _limit) {
			return;
		}
		boolean valid = true;
		if (_inputType != INPUT_STRING) {
			switch (_inputType) {
			case INPUT_UNSIGNED_INTEGER_NUM:
				valid = CharUtils.isDigitCharacter(nextchar);
				break;
			case INPUT_SIGNED_INTEGER_NUM:
				valid = CharUtils.isDigitCharacter(nextchar) || nextchar == '-' && isatstart;
				break;
			case INPUT_UNSIGNED_FLOATING_POINT_NUM:
				valid = CharUtils.isDigitCharacter(nextchar) || nextchar == '.';
				break;
			case INPUT_SIGNED_FLOATING_POINT_NUM:
				valid = CharUtils.isDigitCharacter(nextchar) || nextchar == '.' || nextchar == '-' && isatstart;
				break;
			}
		}
		if (valid && SysKey.getKeyCode() != SysKey.BACK && SysKey.getKeyCode() != SysKey.BACKSPACE) {
			if (SysKey.getKeyCode() == SysKey.ENTER) {
				_text += LSystem.LS;
			} else {
				_text += nextchar;
			}
		} else if (SysKey.getKeyCode() == SysKey.BACK || SysKey.getKeyCode() == SysKey.BACKSPACE) {
			if (_text.length() > 0) {
				_text = _text.substring(0, _text.length() - 1);
			} else {
				_text = "";
			}
		}
		_keyLock.release();
	}

	@Override
	public void processTouchReleased() {
		super.processTouchReleased();
		getOnscreenKeyboard().show(true);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		addCursor();
		super.createUI(g, x, y);
		removeCursor();
	}

	protected void addCursor() {
		if (!isFocusable()) {
			return;
		}
		_text += _cursor;
		setText(_text);
	}

	protected void removeCursor() {
		if (!isFocusable()) {
			return;
		}
		if (_text.charAt(_text.length() - 1) == _cursor) {
			_text = _text.substring(0, MathUtils.max(_startidx, _text.length() - 1));
		}
	}

	public LTextField setLimit(int l) {
		this._limit = l;
		return this;
	}

	public int getLimit() {
		return this._limit;
	}

	@Override
	public String getUIName() {
		return "TextField";
	}

}
