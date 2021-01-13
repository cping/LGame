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

import loon.canvas.LColor;
import loon.component.Print;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class LogDisplay {

	private static class LogDisplayItem {
		public String text;
		public LColor color;

		public LogDisplayItem(String mes, LColor col) {
			this.text = mes;
			this.color = col;
		}

	}

	private final TArray<LogDisplayItem> _texts;

	private IFont _textFont;

	private LColor _textFontColor;

	private int _textAmount = 5;

	private int _textHeight = 20;

	private int _width = 1, _height = 1;

	private int _space = 5;

	public LogDisplay() {
		this(LSystem.getSystemLogFont());
	}

	public LogDisplay(IFont font) {
		this(font, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), LColor.white);
	}

	public LogDisplay(IFont font, int w, int h, LColor color) {
		this._texts = new TArray<LogDisplayItem>(18);
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
		this._width = w;
		this._height = h;
	}

	public void paint(GLEx g, int x, int y) {
		int offset = 0;
		int height = _texts.size() * _textHeight;
		for (int i = _texts.size - 1; i > -1; i--) {
			LogDisplayItem mes = _texts.get(i);
			paintText(g, mes.text, x, y, offset, height, mes.color);
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

		int limitWidth = _width - _space;
		TArray<String> textList = Print.formatMessage(message, _textFont, limitWidth);
		boolean limit = (textList.size * _textFont.getSize() > limitWidth);
		if (limit || textList.size > 0 || message.indexOf(LSystem.LF) != -1) {
			if (limit) {
				textList = Print.formatMessage(message, _textFont, limitWidth - _space);
			}
			for (String text : textList) {
				_texts.add(new LogDisplayItem(text, color));
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
		StrBuilder sbr = new StrBuilder();
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
		this._textFont = font;
		this._textHeight = font.getSize() + 5;
		this._textAmount = ((_height - font.getHeight()) / this._textHeight) - 3;
		this._space = _textFont.getSize() / 4;
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
