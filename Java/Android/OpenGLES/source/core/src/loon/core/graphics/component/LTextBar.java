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
package loon.core.graphics.component;

import loon.Key;
import loon.Touch;
import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

public class LTextBar extends LComponent {

	private LTexture left, right, body;

	private LColor fontColor;

	protected LFont font;

	protected String text;

	private boolean over, pressed;

	private int pressedTime;

	protected boolean hideBackground = false;

	public LTextBar(String txt, int x, int y, LColor c) {
		this(txt, DefUI.getDefaultTextures(3), DefUI.getDefaultTextures(3),
				DefUI.getDefaultTextures(4), x, y, c, LFont.getDefaultFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y, LColor c) {
		this(txt, left, right, body, x, y, c, LFont.getDefaultFont());
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y) {
		this(txt, left, right, body, x, y, LColor.black);
	}

	public LTextBar(String txt, int x, int y) {
		this(txt, x, y, LColor.black);
	}

	public LTextBar(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y, LColor c, LFont f) {
		super(x, y, f.stringWidth(txt) + (left != null ? left.getWidth() : 0)
				+ (right != null ? right.getWidth() : 0), body != null ? body
				.getHeight() : f.getHeight());

		this.text = txt;
		this.fontColor = c;
		this.font = f;
		this.left = left;
		this.right = right;
		this.body = body;
	}

	public LTextBar(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (hideBackground) {
			if (left != null) {
				g.drawString(text, x + left.getWidth(), y + font.getHeight()
						+ (font.getHeight() / 2 - font.getHeight() / 2) - 4,
						fontColor);
			} else {
				g.drawString(text, x, y + font.getHeight(), fontColor);
			}
		} else {
			if (left != null) {
				g.drawTexture(left, x, y);
			}
			if (body != null) {
				if (text != null && text.length() > 0 && !"_".equals(text)) {
					for (float i = 0; i < textWidth(); i += body.getWidth()) {
						i = i > textWidth() - body.getWidth() ? textWidth() : i;
						float fit = i / body.getWidth();
						float overflow = body.getWidth() * (fit % 1);
						boolean last = overflow != 0;
						g.drawTexture(body, x + i - overflow + left.getWidth(),
								y, last ? overflow : body.getWidth() * 2,
								body.getHeight(), 0, 0,
								last ? overflow : body.getWidth(),
								body.getHeight());
					}
				} else {
					g.drawTexture(body,
							x + 1 - body.getWidth() + left.getWidth(), y,
							body.getWidth() * 2, body.getHeight(), 0, 0,
							body.getWidth(), body.getHeight());
				}
			}
			if (right != null) {
				g.drawTexture(right, x + left.getWidth() + textWidth() - 1, y);
			}
			if (left != null) {
				g.drawString(text, x + left.getWidth(), y + font.getHeight()
						+ (font.getHeight() / 2 - font.getHeight() / 2) - 4,
						fontColor);
			} else {
				g.drawString(text, x, y + font.getHeight(), fontColor);
			}
		}
	}

	public float textWidth() {
		return font.stringWidth(text);
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
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	public void downClick() {
		if (Click != null) {
			Click.DownClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void upClick() {
		if (Click != null) {
			Click.UpClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	@Override
	protected void processTouchDragged() {
		if (this.input.getTouchPressed() == Touch.TOUCH_MOVE) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(),
					this.input.getTouchY());
		}
	}

	@Override
	protected void processTouchClicked() {
		if (this.input.getTouchReleased() == Touch.TOUCH_UP) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (this.input.getTouchPressed() == Touch.TOUCH_DOWN) {
			this.downClick();
			this.pressed = true;
		}
	}

	@Override
	protected void processTouchReleased() {
		if (this.input.getTouchReleased() == Touch.TOUCH_UP) {
			this.upClick();
			this.pressed = false;
		}
	}

	protected void processTouchEntered() {
		this.over = true;
	}

	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == Key.ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected() && this.input.getKeyReleased() == Key.ENTER) {
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

}
