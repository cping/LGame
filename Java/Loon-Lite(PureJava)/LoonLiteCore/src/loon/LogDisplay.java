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
package loon;

import java.util.Iterator;

import loon.canvas.LColor;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class LogDisplay {

	public class LogDisplayItem {

		final String _text;
		final LColor _color;

		public LogDisplayItem(String mes, LColor col) {
			this._text = StringUtils.isEmpty(mes) ? LSystem.EMPTY : mes.trim();
			this._color = col;
		}

		public String getText() {
			return _text;
		}

		public String getColor() {
			return _text;
		}

	}

	private final static String _fontTest = "有";

	private final TArray<LogDisplayItem> _texts;

	private TArray<String> _textList;

	private IFont _textFont;

	private LColor _textFontColor;

	private int _textAmount = 5;

	private int _textHeight = 20;

	private int _width = 1, _height = 1;

	private int _space = 5;

	private int _defWidth = -1;

	private int _defHeight = -1;

	public LogDisplay() {
		this(LSystem.getSystemLogFont());
	}

	public LogDisplay(IFont font) {
		this(font, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), LColor.white);
	}

	public LogDisplay(IFont font, int w, int h, LColor color) {
		this._texts = new TArray<LogDisplayItem>();
		this._textFontColor = color;
		this.setSize(w, h);
		this.setFont(font);
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public void setSize(int w, int h) {
		if (w != this._width || h != this._height) {
			this._width = w;
			this._height = h;
			this._texts.clear();
			this.resetDef();
		}
	}

	public void paint(GLEx g, int x, int y) {
		int offset = 0;
		int height = _texts.size() * _textHeight;
		for (int i = _texts.size - 1; i > -1; i--) {
			LogDisplayItem mes = _texts.get(i);
			paintText(g, mes._text, x, y, offset, height, mes._color);
			offset++;
		}
	}

	private void paintText(GLEx g, String message, int x, int y, int offset, int height, LColor color) {
		_textFont.drawString(g, message, x, y + height - ((offset + 1) * _textHeight), color);
	}

	public LogDisplay addText(String message) {
		return addText(message, _textFontColor);
	}

	public LogDisplay addText(String message, LColor color) {
		if (StringUtils.isEmpty(message)) {
			return this;
		}
		final int limitWidth = _width - _space + getFontWidth();
		TArray<String> textList = FontUtils.splitLines(message, _textFont, limitWidth, _textList);
		final boolean limit = (message.length() * getFontWidth() > limitWidth);
		final boolean multipleMessage = (textList.size > 1);
		if (limit || multipleMessage) {
			if (limit && multipleMessage) {
				textList = FontUtils.splitLines(message, _textFont, limitWidth - _space - getFontWidth(), _textList);
			}
			for (Iterator<String> it = textList.iterator(); it.hasNext();) {
				_texts.add(new LogDisplayItem(it.next(), color));
			}
		} else {
			_texts.add(new LogDisplayItem(message, color));
		}
		if (LSystem.isMobile() && _texts.size() > MathUtils.max(_textAmount, 1) - 1) {
			_texts.removeIndex(0);
		} else if (_texts.size() > _textAmount) {
			_texts.removeIndex(0);
		}
		return this;
	}

	public String getText() {
		final StrBuilder sbr = new StrBuilder();
		if (_texts != null) {
			for (int i = 0; i < _texts.size; i++) {
				sbr.append(_texts.get(i));
				sbr.append(LSystem.LS);
			}
		}
		return sbr.toString();
	}

	public void clear() {
		_texts.clear();
	}

	public void setSpace(int s) {
		this._space = s;
	}

	public int getSpace() {
		return this._space;
	}

	public void setFont(IFont font) {
		if (font == null) {
			return;
		}
		this._textFont = font;
		final int textHeight = getFontHeight();
		this._textHeight = textHeight + 5;
		this._textAmount = ((_height - textHeight) / this._textHeight) - 3;
		this._space = (int) (getFontWidth() * 1.6f);
		this.resetDef();
	}

	private void resetDef() {
		_defWidth = _defHeight = -1;
	}

	private int getFontWidth() {
		if (_textFont != null) {
			if (_defWidth <= 0) {
				_defWidth = _textFont.stringWidth(_fontTest) + 1;
			}
			return MathUtils.max(_defWidth, LSystem.DEFAULT_SYS_FONT_SIZE);
		}
		return LSystem.DEFAULT_SYS_FONT_SIZE;
	}

	private int getFontHeight() {
		if (_textFont != null) {
			if (_defHeight <= 0) {
				_defHeight = _textFont.stringHeight(_fontTest) + 1;
			}
			return MathUtils.max(_defHeight, LSystem.DEFAULT_SYS_FONT_SIZE);
		}
		return LSystem.DEFAULT_SYS_FONT_SIZE;
	}

	public IFont getFont() {
		return _textFont;
	}

	public LColor getFontColor() {
		return _textFontColor;
	}

	public void setFontColor(LColor fontColor) {
		this._textFontColor = fontColor;
	}

}
