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
package loon.font;

import loon.HorizontalAlign;
import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.opengl.LSTRFont;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 一个统一的文字显示用类，用以统一文字的基础显示效果
 */
public class Text implements LRelease {

	private boolean _initNativeDraw = false, _closed = false;
	protected IFont _font;
	protected float _space = 0;
	protected float _lineWidthMaximum;
	protected float _lineAlignmentWidth;

	protected TextOptions _textOptions;

	protected int _charactersToDraw;
	protected int _vertexCountToDraw;

	protected CharSequence _chars;
	protected TArray<CharSequence> _lines = new TArray<CharSequence>(1);
	protected FloatArray _lineWidths = new FloatArray(1);
	protected float _width = 0, _height = 0;

	public Text(final IFont font, final CharSequence chars) {
		this(font, chars, new TextOptions());
	}

	public Text(final IFont font, final CharSequence chars, final TextOptions opt) {
		this._font = font;
		this._textOptions = opt;
		this.setText(chars);
	}

	public float getWidth() {
		return this._width;
	}

	public float getHeight() {
		return this._height;
	}

	public IFont getFont() {
		return this._font;
	}

	public boolean isEmpty() {
		return this._chars == null || _chars.length() == 0;
	}

	public CharSequence getText() {
		return this._chars;
	}

	public void setFont(final IFont font) {
		this.setText(font, this._chars);
	}

	public void setText(final IFont font, final CharSequence chars) {
		this._font = font;
		this.setText(chars);
	}

	public void setText(final CharSequence chars) {
		if (LSystem.base() == null) {
			return;
		}
		this._chars = chars != null ? chars : "";
		final IFont font = this._font;
		this._lines.clear();
		this._lineWidths.clear();
		if (this._textOptions._autoWrap == AutoWrap.NONE) {
			this._lines = FontUtils.splitLines(this._chars, this._lines);
		} else {
			this._lines = FontUtils.splitLines(this._font, this._chars, this._lines, this._textOptions._autoWrap,
					this._textOptions._autoWrapWidth);
		}
		final int lineCount = this._lines.size;
		float maxLineWidth = 0;
		for (int i = 0; i < lineCount; i++) {
			final float lineWidth = FontUtils.measureText(font, this._lines.get(i));
			maxLineWidth = MathUtils.max(maxLineWidth, lineWidth);
			this._lineWidths.add(lineWidth);
		}
		this._lineWidthMaximum = maxLineWidth;
		if (this._textOptions._autoWrap == AutoWrap.NONE) {
			this._lineAlignmentWidth = this._lineWidthMaximum;
		} else {
			this._lineAlignmentWidth = this._textOptions._autoWrapWidth;
		}
		this._width = this._lineAlignmentWidth;
		if (_width <= 0) {
			_width = _lineWidths.get(0) * StringUtils.countOccurrences(chars, '\n');
		}
		this._height = lineCount * font.getHeight() + (lineCount - 1) * this._textOptions._leading;
		if (_height <= 0) {
			_height = _font.getHeight();
		}
		this._initNativeDraw = false;
	}

	private String toString(CharSequence ch) {
		String mes = null;
		if (ch instanceof String) {
			mes = (String) ch;
		} else if (ch instanceof StringBuffer) {
			mes = ((StringBuffer) ch).toString();
		} else if (ch instanceof StringBuilder) {
			mes = ((StringBuilder) ch).toString();
		} else {
			mes = new StringBuffer(ch).toString();
		}
		return mes;
	}

	private void initLFont() {
		if(_closed){
			return;
		}
		if (!_initNativeDraw) {
			if (_font instanceof LFont) {
				LSTRDictionary.get().bind((LFont) _font, _lines);
			}
			if (LSystem.isDesktop()) {
				if (_font instanceof LFont) {
					LSTRFont strfont = LSTRDictionary.get().STRFont((LFont) _font);
					if (strfont != null) {
						if (_textOptions._autoWrap != AutoWrap.VERTICAL) {
							strfont.setUpdateX(0);
						} else {
							strfont.setUpdateX(1);
						}
					}
				}
			}
			_initNativeDraw = true;
		}
	}

	public void paintNonStyleString(GLEx g, String mes, float offsetX, float offsetY, LColor color) {
		if(_closed){
			return;
		}
		initLFont();
		_font.drawString(g, mes, offsetX, offsetY, color);
	}

	public void paintString(GLEx g, String mes, float offsetX, float offsetY, LColor color) {
		if(_closed){
			return;
		}
		initLFont();
		if (_textOptions._autoWrap != AutoWrap.VERTICAL) {
			switch (_textOptions._horizontalAlign) {
			case CENTER:
				_font.drawString(g, mes, (getWidth() / 2 - _font.stringWidth(mes) / 2) + offsetX, offsetY, color);
				break;
			case LEFT:
				_font.drawString(g, mes, offsetX, offsetY, color);
				break;
			case RIGHT:
				_font.drawString(g, mes, getWidth() - _font.stringWidth(mes) + offsetX, offsetY, color);
				break;
			default:
				break;
			}
		} else if (_textOptions._autoWrap == AutoWrap.VERTICAL) {
			float viewX = 0;
			int idx = 0;
			if (_textOptions._autoWrap == AutoWrap.VERTICAL) {
				char ch = mes.charAt(0);
				float viewY = 0;
				if (ch != '\n') {
					viewY = offsetY + idx * (_font.stringHeight(mes) + _textOptions.getLeading());
					idx++;
				} else {
					viewX += _font.getSize() + getLeading();
					viewY = 0;
					idx = 0;
				}
				switch (_textOptions._horizontalAlign) {
				case CENTER:
					_font.drawString(g, mes, viewX + offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2), viewY,
							color);
					break;
				case LEFT:
					_font.drawString(g, mes, viewX + offsetX, viewY, color);
					break;
				case RIGHT:
					_font.drawString(g, mes, viewX + offsetX + getWidth() - _font.stringWidth(mes), viewY, color);
					break;
				default:
					break;
				}

			} else {
				switch (_textOptions._horizontalAlign) {
				case CENTER:
					_font.drawString(g, mes, offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2),
							offsetY + _textOptions.getLeading(), color);
					break;
				case LEFT:
					_font.drawString(g, mes, offsetX, +_textOptions.getLeading(), color);
					break;
				case RIGHT:
					_font.drawString(g, mes, offsetX + getWidth() - _font.stringWidth(mes),
							offsetY + _textOptions.getLeading(), color);
					break;
				default:
					break;
				}
			}

		} else {
			switch (_textOptions._horizontalAlign) {
			case CENTER:
				_font.drawString(g, mes, offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2),
						offsetY + _textOptions.getLeading(), color);
				break;
			case LEFT:
				_font.drawString(g, mes, offsetX, offsetY + _textOptions.getLeading(), color);
				break;
			case RIGHT:
				_font.drawString(g, mes, offsetX + getWidth() - _font.stringWidth(mes), offsetY, color);
				break;
			default:
				break;
			}
		}
	}

	public void paintString(GLEx g, float offsetX, float offsetY, LColor color) {
		if(_closed){
			return;
		}
		initLFont();
		if (_lines.size == 1) {
			paintString(g, toString(_lines.get(0)), offsetX, offsetY, color);
		} else if (_textOptions._autoWrap == AutoWrap.VERTICAL) {
			float viewX = 0;
			int idx = 0;
			for (int i = 0, size = _lines.size; i < size; i++) {
				CharSequence c = _lines.get(i);
				String mes = toString(c);
				if (_textOptions._autoWrap == AutoWrap.VERTICAL) {
					char ch = mes.charAt(0);
					float viewY = 0;
					if (ch != '\n') {
						viewY = offsetY + idx * (_font.stringHeight(mes) + _textOptions.getLeading());
						idx++;
					} else {
						viewX += _font.getSize() + getLeading();
						viewY = 0;
						idx = 0;
					}
					switch (_textOptions._horizontalAlign) {
					case CENTER:
						_font.drawString(g, mes, viewX + offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2), viewY,
								color);
						break;
					case LEFT:
						_font.drawString(g, mes, viewX + offsetX, viewY, color);
						break;
					case RIGHT:
						_font.drawString(g, mes, viewX + offsetX + getWidth() - _font.stringWidth(mes), viewY, color);
						break;
					default:
						break;
					}

				} else {
					switch (_textOptions._horizontalAlign) {
					case CENTER:
						_font.drawString(g, mes, offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2), offsetY

								+ i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
						break;
					case LEFT:
						_font.drawString(g, mes, offsetX,
								offsetY + i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
						break;
					case RIGHT:
						_font.drawString(g, mes, offsetX + getWidth() - _font.stringWidth(mes), offsetY

								+ i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
						break;
					default:
						break;
					}
				}
			}
		} else {
			for (int i = 0, size = _lines.size; i < size; i++) {
				CharSequence c = _lines.get(i);
				String mes = toString(c);
				switch (_textOptions._horizontalAlign) {
				case CENTER:
					_font.drawString(g, mes, offsetX + (getWidth() / 2 - _font.stringWidth(mes) / 2), offsetY

							+ i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
					break;
				case LEFT:
					_font.drawString(g, mes, offsetX,
							offsetY + i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
					break;
				case RIGHT:
					_font.drawString(g, mes, offsetX + getWidth() - _font.stringWidth(mes), offsetY

							+ i * (_font.stringHeight(mes) + _textOptions.getLeading()), color);
					break;
				default:
					break;
				}
			}
		}

	}

	public TArray<CharSequence> getLines() {
		return this._lines;
	}

	public FloatArray getLineWidths() {
		return this._lineWidths;
	}

	public float getLineAlignmentWidth() {
		return this._lineAlignmentWidth;
	}

	public float getLineWidthMaximum() {
		return this._lineWidthMaximum;
	}

	public float getSpace() {
		return getLeading();
	}

	public float getLeading() {
		return this._textOptions._leading;
	}

	public void setLeading(final float leading) {
		this._textOptions._leading = leading;
		this.initText();
	}

	public void setSpace(final float space) {
		setLeading(space);
	}

	public HorizontalAlign getHorizontalAlign() {
		return this._textOptions._horizontalAlign;
	}

	public void setHorizontalAlign(final HorizontalAlign horizontalAlign) {
		this._textOptions._horizontalAlign = horizontalAlign;
		this.initText();
	}

	public AutoWrap getAutoWrap() {
		return this._textOptions._autoWrap;
	}

	public void setAutoWrap(final AutoWrap autoWrap) {
		this._textOptions._autoWrap = autoWrap;
		this.initText();
	}

	public void initText() {
		this.setText(this._chars);
	}

	public float getAutoWrapWidth() {
		return this._textOptions._autoWrapWidth;
	}

	public void setAutoWrapWidth(final float autoWrapWidth) {
		this._textOptions._autoWrapWidth = autoWrapWidth;
		this.initText();
	}

	public TextOptions getTextOptions() {
		return this._textOptions;
	}

	public void setTextOptions(final TextOptions opt) {
		this._textOptions = opt;
	}

	@Override
	public String toString() {
		return new StringBuffer(_chars).toString();
	}

	@Override
	public void close() {
		if (LSystem.isDesktop()) {
			if (_font instanceof LFont) {
				LSTRFont font = LSTRDictionary.get().STRFont((LFont) _font);
				if (font != null) {
					font.setUpdateX(0);
				}
			}
		}
		_chars = null;
		_lines = null;
		_lineWidths = null;
		_closed = true;
		_initNativeDraw = false;
	}

	public boolean isClosed() {
		return _closed;
	}

}
