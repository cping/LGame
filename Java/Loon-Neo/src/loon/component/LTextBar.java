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
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.component.skin.TextBarSkin;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 一个单纯的文字框组件UI,长度可动态改变
 */
public class LTextBar extends LComponent {

	private LTimer _waitTimer;

	private boolean _drawUI = false;

	private LTexture left, right, body;

	private float _maxWidth = -1;

	private LColor _fontColor;

	protected IFont _font;

	protected String _text;

	protected float _offsetX = 0, _offsetY = 0;

	private String _lastText = null;

	private TArray<String> _messages = null;

	private boolean _displayFlag;

	private boolean _masked = false;

	private boolean _over, _pressed;

	protected boolean _hideBackground = false;

	protected boolean _flashCursor = false;

	protected char _maskCharacter = '*';

	protected char _cursor = '_';

	private float _pressedTime;

	public LTextBar(String txt, float x, float y, LColor c) {
		this(txt, SkinManager.get().getTextBarSkin().getLeftTexture(),
				SkinManager.get().getTextBarSkin().getRightTexture(),
				SkinManager.get().getTextBarSkin().getBodyTexture(), x, y, c,
				SkinManager.get().getTextBarSkin().getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, float x, float y, LColor c) {
		this(txt, left, right, body, x, y, c, SkinManager.get().getTextBarSkin().getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, float x, float y) {
		this(txt, left, right, body, x, y, SkinManager.get().getTextBarSkin().getFontColor());
	}

	public LTextBar(IFont font, String txt, float x, float y) {
		this(SkinManager.get().getTextBarSkin(), txt, x, y, font);
	}

	public LTextBar(String txt, float x, float y) {
		this(txt, x, y, SkinManager.get().getTextBarSkin().getFontColor());
	}

	public LTextBar(TextBarSkin skin, String txt, float x, float y, IFont font) {
		this(txt, skin.getLeftTexture(), skin.getRightTexture(), skin.getBodyTexture(), x, y, skin.getFontColor(),
				font);
	}

	public LTextBar(TextBarSkin skin, String txt, float x, float y) {
		this(txt, skin.getLeftTexture(), skin.getRightTexture(), skin.getBodyTexture(), x, y, skin.getFontColor(),
				skin.getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, float x, float y, LColor c, IFont f) {
		this(txt, left, right, body, x, y, c, f, -1);
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, float x, float y, LColor c, IFont f,
			float maxWidth) {
		super(MathUtils.ifloor(x), MathUtils.ifloor(y), 0, 0);
		float w = f.stringWidth(txt) + (left != null ? left.getWidth() : 0)
				+ (right != null ? right.getWidth() : 0) * 3;
		float h = body != null ? body.getHeight() : f.getHeight();
		this._fontColor = c;
		this._waitTimer = new LTimer(500);
		if (maxWidth == -1 && body != null) {
			this._maxWidth = w;
		} else {
			this._maxWidth = maxWidth;
		}
		this.left = left;
		this.right = right;
		this.body = body;
		this.setFont(f);
		this.setText(txt);
		this.setSize(w, h);
		freeRes().add(left, right, body);
		autoSize();
	}

	public LTextBar(float x, float y, float width, float height) {
		super(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(width), MathUtils.ifloor(height));
	}

	public void autoSize() {
		if (getWidth() <= 1f || getHeight() <= 1f) {
			PointF size = FontUtils.getTextWidthAndHeight(_font, _text, getWidth(), getHeight());
			this.setWidth(MathUtils.max(getWidth(), size.x));
			this.setHeight(MathUtils.max(getHeight(), size.y));
		}
	}

	public LTextBar setMaxWidth(float w) {
		this._maxWidth = w;
		return this;
	}

	public float getMaxWidth() {
		return _maxWidth;
	}

	public boolean isDrawUI() {
		return _drawUI;
	}

	public LTextBar setDrawUI(boolean d) {
		this._drawUI = d;
		return this;
	}

	public boolean isEmpty() {
		return _messages == null ? false : _messages.isEmpty();
	}

	public boolean isNotEmpty() {
		return _messages == null ? false : _messages.isNotEmpty();
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		float height = (_messages == null ? getHeight() : _messages.size * _font.getHeight() + 5);
		if (_drawUI) {
			float width = textWidth() + _font.getSize() + 5;
			if (isPointInUI()) {
				g.fillRect(x, y, width, height, LColor.gray);
			} else {
				g.fillRect(x, y, width, height, LColor.black);
			}
			if (_messages != null) {
				for (int i = 0, size = _messages.size; i < size; i++) {
					String text = _messages.get(i);
					drawMessage(g, _font, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)),
							_fontColor);

				}
			} else {
				drawMessage(g, _font, _text, x + 5, y, _fontColor);
			}
			g.drawRect(x, y, width, height, LColor.black);
			return;
		} else {
			if (_hideBackground) {
				if (_messages != null) {
					for (int i = 0, size = _messages.size; i < size; i++) {
						String text = _messages.get(i);
						drawMessage(g, _font, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)),
								_fontColor);

					}
				} else {
					drawMessage(g, _font, _text, x + 5, y, _fontColor);
				}
			} else {
				if (left != null) {
					g.draw(left, x, y, left.getWidth(), MathUtils.max(body.getHeight(), height), _component_baseColor);
				}
				if (body != null) {
					if (left != null) {
						g.draw(body, x + left.getWidth(), y, textWidth() + _font.getSize(),
								MathUtils.max(body.getHeight(), height), _component_baseColor);
					} else {
						g.draw(body, x, y, 0, _maxWidth, _component_baseColor);
					}
				}
				if (right != null && body != null) {
					float w = 0;
					if (_messages == null) {
						w = textWidth();
					} else {
						w = textWidth() + _font.getSize();
					}
					g.draw(right, x + left.getWidth() + w, y, left.getWidth(), MathUtils.max(body.getHeight(), height),
							_component_baseColor);
				}
				if (left != null) {
					if (_messages != null) {
						for (int i = 0, size = _messages.size; i < size; i++) {
							String text = _messages.get(i);
							drawMessage(g, _font, text, x + _offsetX + left.getWidth() + 5,
									y + _offsetY + i * (_font.stringHeight(text)), _fontColor);
						}
					} else {
						drawMessage(g, _font, _text, x + left.getWidth() + 5, y, _fontColor);
					}
				} else {
					if (_messages != null) {
						for (int i = 0, size = _messages.size; i < size; i++) {
							String text = _messages.get(i);
							drawMessage(g, _font, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)),
									_fontColor);

						}
					} else {
						drawMessage(g, _font, _text, x + 5, y, _fontColor);
					}
				}
			}
		}
	}

	private final void drawMessage(GLEx g, IFont font, String mes, float x, float y, LColor fontColor) {
		if (StringUtils.isNullOrEmpty(mes)) {
			return;
		}
		final float size = font.getSize() / 6f;
		if (_flashCursor) {
			final int len = mes.length() - 1;
			final char end = mes.charAt(len);
			if (_displayFlag) {
				if (_masked) {
					if (end == _cursor) {
						font.drawString(g, StringUtils.cpy(_maskCharacter, len) + _cursor, x, y + size, fontColor);
					} else {
						font.drawString(g, StringUtils.cpy(_maskCharacter, mes.length()), x, y + size, fontColor);
					}
				} else {
					font.drawString(g, mes, x, y + size, fontColor);
				}
			} else {
				if (end == _cursor) {
					if (_masked) {
						font.drawString(g, StringUtils.cpy(_maskCharacter, len), x, y + size, fontColor);
					} else {
						font.drawString(g, mes.substring(0, len), x, y + size, fontColor);
					}
				} else {
					if (_masked) {
						font.drawString(g, StringUtils.cpy(_maskCharacter, mes.length()), x, y + size, fontColor);
					} else {
						font.drawString(g, mes, x, y + size, fontColor);
					}
				}
			}
		} else {
			font.drawString(g, mes, x, y + size, fontColor);
		}
	}

	public LTextBar setCursor(char c) {
		this._cursor = c;
		return this;
	}

	public char getCursor() {
		return this._cursor;
	}

	public float textWidth() {
		if (_messages != null) {
			return _font.stringWidth(_messages.get(0)) - _font.getSize() / 2;
		} else {
			return _font.stringWidth(_text);
		}
	}

	public LTexture getLeft() {
		return left;
	}

	public void setLeft(LTexture left) {
		this.left = left;
	}

	public LTexture getRight() {
		return right;
	}

	public void setRight(LTexture right) {
		this.right = right;
	}

	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	public void setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
	}

	public IFont getFont() {
		return _font;
	}

	public LTextBar setFont(IFont font) {
		if (font == null) {
			return this;
		}
		this._font = font;
		return this;
	}

	public String getText() {
		return _text;
	}

	public LTextBar setText(String mes) {
		if (StringUtils.isEmpty(mes)) {
			this._text = LSystem.EMPTY;
			this._messages = Print.formatMessage(_text, _font, _maxWidth);
			return this;
		}
		if (!mes.equals(_lastText)) {
			this._text = mes;
			this._messages = Print.formatMessage(_text, _font, _maxWidth);
		}
		return this;
	}

	public LTextBar addText(String mes) {
		if (StringUtils.isEmpty(mes)) {
			return this;
		}
		this._text += mes;
		return this;
	}

	public LTextBar replaceText(String src, String dst) {
		if (StringUtils.isEmpty(src) || StringUtils.isEmpty(dst)) {
			return this;
		}
		this._text = StringUtils.replace(_text, src, dst);
		this._messages = Print.formatMessage(_text, _font, _maxWidth);
		return this;
	}

	public LTextBar formatText(String src, Object... o) {
		if (StringUtils.isEmpty(src) || o == null) {
			return this;
		}
		this._text = StringUtils.format(_text, src, o);
		this._messages = Print.formatMessage(_text, _font, _maxWidth);
		return this;
	}

	@Override
	protected void processTouchDragged() {
		this._over = this._pressed = this.intersects(getUITouchX(), getUITouchY());
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		this._pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		this._pressed = false;
	}

	@Override
	protected void processTouchEntered() {
		this._over = true;
	}

	@Override
	protected void processTouchExited() {
		this._over = this._pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this._pressedTime = 5;
			this._pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected()) {
			this._pressed = false;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (this._pressedTime > 0 && --this._pressedTime <= 0) {
			this._pressed = false;
		}
		if (_flashCursor && _waitTimer.action(elapsedTime)) {
			_displayFlag = !_displayFlag;
		}
	}

	public LTextBar setCursorWaitDelay(long time) {
		_waitTimer.setDelay(time);
		return this;
	}

	public long getCursorWaitDelay() {
		return _waitTimer.getDelay();
	}

	public boolean isTouchOver() {
		return this._over;
	}

	public boolean isTouchPressed() {
		return this._pressed;
	}

	public boolean isHideBackground() {
		return _hideBackground;
	}

	public LTextBar setHideBackground(boolean hideBackground) {
		this._hideBackground = hideBackground;
		return this;
	}

	public char getMaskCharacter() {
		return _maskCharacter;
	}

	public LTextBar setMaskCharacter(char m) {
		this._maskCharacter = m;
		return this;
	}

	public boolean isMasked() {
		return _masked;
	}

	public LTextBar setMasked(boolean m) {
		this._masked = m;
		return this;
	}

	public float getBoxOffsetX() {
		return _offsetX;
	}

	public LTextBar setBoxOffsetX(float offsetX) {
		this._offsetX = offsetX;
		return this;
	}

	public float getBoxOffsetY() {
		return _offsetY;
	}

	public LTextBar setBoxOffsetY(float offsetY) {
		this._offsetY = offsetY;
		return this;
	}

	public boolean isFlashCursor() {
		return _flashCursor;
	}

	public LTextBar setFlashCursor(boolean c) {
		this._flashCursor = c;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextBar";
	}

	@Override
	public void destory() {

	}

}
