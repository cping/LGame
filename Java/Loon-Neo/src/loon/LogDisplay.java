package loon;

import loon.canvas.LColor;
import loon.component.Print;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.TArray;

public class LogDisplay {

	private final TArray<String> _texts;

	private IFont _font;

	private LColor _fontColor;

	private int _displayAmount = 5;

	private int _textHeight = 20;

	private int _width = 1, _height = 1;

	public LogDisplay() {
		this(LFont.newFont(LSystem.isHTML5() ? 20 : 15), LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(),
				LColor.white);
	}

	public LogDisplay(IFont font, int w, int h, LColor color) {
		this._texts = new TArray<String>(18);
		this._fontColor = color;
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
			String mes = _texts.get(i);
			paintText(g, mes, x, y, offset, height);
			offset++;
		}
	}

	private void paintText(GLEx g, String message, int x, int y, int offset, int height) {
		_font.drawString(g, message, x, y + height - ((offset + 1) * _textHeight), _fontColor);
	}

	public void addText(String message) {
		if (message != null && (message.length() * _font.getSize() > _width)) {
			TArray<String> mes = Print.formatMessage(message, _font, _width - _font.getSize());
			for (String text : mes) {
				_texts.add(text);
			}
		} else {
			_texts.add(message);
		}
		if (_texts.size() > _displayAmount) {
			_texts.removeIndex(0);
		}
	}

	public void clear() {
		_texts.clear();
	}

	public void setFont(IFont font) {
		this._font = font;
		this._textHeight = font.getSize() + 5;
		this._displayAmount = ((_height - font.getHeight()) / this._textHeight) - 3;
	}

	public IFont getFont() {
		return _font;
	}

	public LColor getFontColor() {
		return _fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
	}

}
