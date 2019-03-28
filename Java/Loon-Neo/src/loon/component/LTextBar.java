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

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.component.skin.TextBarSkin;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个单纯的文字框组件UI,长度可动态改变
 */
public class LTextBar extends LComponent {

	private boolean _drawUI = false;

	private static final LColor FOCUSED = new LColor(0x58543c);

	private static final LColor UNFOCUSED = new LColor(0x817b58);

	private LTexture left, right, body;

	private int _maxWidth = -1;

	private LColor _fontColor;

	protected IFont _font;

	protected String _text;

	protected float _offsetX = 0, _offsetY = 0;

	private String _lastText = null;

	private TArray<String> _messages = null;

	private boolean over, pressed;

	private int pressedTime;

	protected boolean hideBackground = false;

	public LTextBar(String txt, int x, int y, LColor c) {
		this(txt, SkinManager.get().getTextBarSkin().getLeftTexture(),
				SkinManager.get().getTextBarSkin().getRightTexture(),
				SkinManager.get().getTextBarSkin().getBodyTexture(), x, y, c,
				SkinManager.get().getTextBarSkin().getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, int x, int y, LColor c) {
		this(txt, left, right, body, x, y, c, SkinManager.get().getTextBarSkin().getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, int x, int y) {
		this(txt, left, right, body, x, y, SkinManager.get().getTextBarSkin().getFontColor());
	}

	public LTextBar(IFont font, String txt, int x, int y) {
		this(SkinManager.get().getTextBarSkin(), txt, x, y, font);
	}

	public LTextBar(String txt, int x, int y) {
		this(txt, x, y, SkinManager.get().getTextBarSkin().getFontColor());
	}

	public LTextBar(TextBarSkin skin, String txt, int x, int y, IFont font) {
		this(txt, skin.getLeftTexture(), skin.getRightTexture(), skin.getBodyTexture(), x, y, skin.getFontColor(),
				font);
	}

	public LTextBar(TextBarSkin skin, String txt, int x, int y) {
		this(txt, skin.getLeftTexture(), skin.getRightTexture(), skin.getBodyTexture(), x, y, skin.getFontColor(),
				skin.getFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, int x, int y, LColor c, IFont f) {
		this(txt, left, right, body, x, y, c, f, -1);
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body, int x, int y, LColor c, IFont f,
			int maxWidth) {
		super(x, y, 0, 0);
		int w = f.stringWidth(txt) + (left != null ? left.getWidth() : 0) + (right != null ? right.getWidth() : 0) * 3;
		int h = (int) (body != null ? body.getHeight() : f.getHeight());
		this._fontColor = c;
		this._font = f;
		if (maxWidth == -1 && body != null) {
			this._maxWidth = w;
		} else {
			this._maxWidth = maxWidth;
		}
		this.setSize(w, h);
		this.left = left;
		this.right = right;
		this.body = body;
		this.setText(txt);
		freeRes().add(left, right, body);
	}

	public LTextBar(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public void setMaxWidth(float w) {
		this._maxWidth = (int) w;
	}

	public int getMaxWidth() {
		return _maxWidth;
	}

	public boolean isDrawUI() {
		return _drawUI;
	}

	public LTextBar setDrawUI(boolean d) {
		this._drawUI = d;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		float height = (_messages == null ? getHeight() : _messages.size * _font.getHeight() + 5);
		if (_drawUI) {
			float width = textWidth() + _font.getSize() + 5;
			if (isPointInUI()) {
				g.fillRect(x, y, width, height, FOCUSED);
			} else {
				g.fillRect(x, y, width, height, UNFOCUSED);
			}
			if (_messages != null) {
				for (int i = 0, size = _messages.size; i < size; i++) {
					String text = _messages.get(i);
					drawString(g, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)), _fontColor);

				}
			} else {
				drawString(g, _text, x + 5, y, _fontColor);
			}
			g.drawRect(x, y, width, height, LColor.black);
			return;
		} else {
			if (hideBackground) {
				if (_messages != null) {
					for (int i = 0, size = _messages.size; i < size; i++) {
						String text = _messages.get(i);
						drawString(g, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)),
								_fontColor);

					}
				} else {
					drawString(g, _text, x + 5, y, _fontColor);
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
							drawString(g, text, x + _offsetX + left.getWidth() + 5,
									y + _offsetY + i * (_font.stringHeight(text)), _fontColor);
						}
					} else {
						drawString(g, _text, x + left.getWidth() + 5, y, _fontColor);
					}
				} else {
					if (_messages != null) {
						for (int i = 0, size = _messages.size; i < size; i++) {
							String text = _messages.get(i);
							drawString(g, text, x + _offsetX + 5, y + _offsetY + i * (_font.stringHeight(text)),
									_fontColor);

						}
					} else {
						drawString(g, _text, x + 5, y, _fontColor);
					}
				}
			}
		}
	}

	private final void drawString(GLEx g, String mes, float x, float y, LColor fontColor) {
		_font.drawString(g, mes, x, y, fontColor);
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

	public void setFont(IFont font) {
		this._font = font;
	}

	public String getText() {
		return _text;
	}

	public void setText(String mes) {
		if (!mes.equals(_lastText)) {
			this._text = mes;
			this._messages = Print.formatMessage(mes, _font, _maxWidth);
		}
	}

	@Override
	protected void processTouchDragged() {
		this.over = this.pressed = this.intersects(getUITouchX(), getUITouchY());
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		this.pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		this.pressed = false;
	}

	protected void processTouchEntered() {
		this.over = true;
	}

	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.pressed = false;
		}
	}

	public void update(long timer) {
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			this.pressed = false;
		}
	}

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	public boolean isHideBackground() {
		return hideBackground;
	}

	public void setHideBackground(boolean hideBackground) {
		this.hideBackground = hideBackground;
	}

	@Override
	public String getUIName() {
		return "TextBar";
	}

	public float getOffsetX() {
		return _offsetX;
	}

	public void setOffsetX(float offsetX) {
		this._offsetX = offsetX;
	}

	public float getOffsetY() {
		return _offsetY;
	}

	public void setOffsetY(float offsetY) {
		this._offsetY = offsetY;
	}

}
