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
import loon.LTextures;
import loon.canvas.LColor;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.font.LFont;
import loon.opengl.GLEx;

// 与LButton的差异在于，内置有默认UI图片，并且可以选择大小，而不是必须按照图片大小拆分
public class LClickButton extends LComponent {

	private LTexture idleClick, hoverClick, clickedClick;

	private LFont font;

	private boolean over, pressed;

	private int pressedTime, offsetLeft, offsetTop;

	private LColor fontColor;

	private String text = null;

	public LClickButton(String text, int x, int y, int width, int height) {
		this(text, LFont.getDefaultFont(), LColor.white, x, y, width, height,
				DefUI.getDefaultTextures(7), DefUI.getDefaultTextures(8), DefUI
						.getDefaultTextures(9));
	}

	public LClickButton(String text, LFont font, LColor color, int x, int y,
			int width, int height) {
		this(text, font, color, x, y, width, height, DefUI
				.getDefaultTextures(7), DefUI.getDefaultTextures(8), DefUI
				.getDefaultTextures(9));
	}

	public LClickButton(String text, LFont font, LColor color, int x, int y,
			int width, int height, String a, String b, String c) {
		this(text, font, color, x, y, width, height, LTextures.loadTexture(a),
				LTextures.loadTexture(b), LTextures.loadTexture(c));
	}

	public LClickButton(String text, LFont font, LColor color, int x, int y,
			int width, int height, LTexture idle, LTexture hover,
			LTexture clicked) {
		super(x, y, width, height);
		this.text = text;
		this.font = font;
		this.fontColor = color;
		this.idleClick = idle;
		this.hoverClick = hover;
		this.clickedClick = clicked;
	}

	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (!isEnabled()) {
			g.draw(clickedClick, x, y, getWidth(), getHeight());
		} else if (isTouchPressed()) {
			g.draw(idleClick, x, y, getWidth(), getHeight());
		} else if (isTouchOver()) {
			g.draw(hoverClick, x, y, getWidth(), getHeight());
		} else {
			g.draw(idleClick, x, y, getWidth(), getHeight());
		}
		if (text != null) {
			LFont old = g.getFont();
			g.setFont(font);
			g.drawString(text,
					x + getOffsetLeft() + (getWidth() - font.stringWidth(text))
							/ 2,
					(y + getOffsetTop() + (getHeight() - font.getHeight())
							/ 2 + font.getHeight()) - 5, fontColor);
			g.setFont(old);
		}
	}

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
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

	protected void processTouchDragged() {
		if (this.input.getTouchPressed() == SysTouch.TOUCH_MOVE) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(),
					this.input.getTouchY());
		}
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
	protected void processTouchClicked() {
		int code = this.input.getTouchReleased();
		if (code == SysTouch.TOUCH_DOWN || code == SysTouch.TOUCH_UP) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (this.input.getTouchPressed() == SysTouch.TOUCH_DOWN) {
			this.downClick();
			this.pressed = true;
		}
	}

	@Override
	protected void processTouchReleased() {
		if (this.input.getTouchReleased() == SysTouch.TOUCH_UP) {
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
		if (this.isSelected() && this.input.getKeyPressed() == SysKey.ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected() && this.input.getKeyReleased() == SysKey.ENTER) {
			this.pressed = false;
		}
	}

	public int getOffsetLeft() {
		return offsetLeft;
	}

	public void setOffsetLeft(int offsetLeft) {
		this.offsetLeft = offsetLeft;
	}

	public int getOffsetTop() {
		return offsetTop;
	}

	public void setOffsetTop(int offsetTop) {
		this.offsetTop = offsetTop;
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public boolean isPressed() {
		return pressed;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	@Override
	public String getUIName() {
		return "ClickButton";
	}

}
