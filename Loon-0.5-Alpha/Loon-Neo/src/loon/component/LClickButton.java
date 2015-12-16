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
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;

// 与LButton的差异在于，内置有默认UI图片，并且可以选择大小，而不是必须按照图片大小拆分
public class LClickButton extends LComponent {

	private LTexture idleClick, hoverClick, clickedClick;

	private IFont font;

	private boolean over, pressed, grayButton, selected;

	private int pressedTime, offsetLeft, offsetTop;

	private LColor fontColor;

	private String text = null;

	public static LClickButton make(LTexture texture) {
		return new LClickButton(null, LFont.getDefaultFont(), LColor.white, 0,
				0, texture.getWidth(), texture.getHeight(), texture, texture,
				texture);
	}

	public static LClickButton make(String text) {
		return new LClickButton(text, 0, 0, 1, 1);
	}

	public static LClickButton make(String text, int width, int height) {
		return new LClickButton(text, 0, 0, width, height);
	}

	public static LClickButton make(String text, int x, int y, int width,
			int height) {
		return new LClickButton(text, x, y, width, height);
	}

	public static LClickButton make(String text, int width, int height,
			String clickPath) {
		LTexture texture = LTextures.loadTexture(clickPath);
		return new LClickButton(text, LFont.getDefaultFont(), LColor.white, 0,
				0, width, height, texture, texture, texture);
	}

	public static LClickButton make(String text, int width, int height,
			LTexture clicked) {
		return new LClickButton(text, LFont.getDefaultFont(), LColor.white, 0,
				0, width, height, clicked, clicked, clicked);
	}

	public static LClickButton make(String text, int width, int height,
			LTexture hover, LTexture clicked) {
		return new LClickButton(text, LFont.getDefaultFont(), LColor.white, 0,
				0, width, height, hover, hover, clicked);
	}

	public static LClickButton make(String text, int width, int height,
			LTexture idle, LTexture hover, LTexture clicked) {
		return new LClickButton(text, LFont.getDefaultFont(), LColor.white, 0,
				0, width, height, idle, hover, clicked);
	}

	public LClickButton(String text, int x, int y, int width, int height) {
		this(text, LFont.getDefaultFont(), LColor.white, x, y, width, height,
				DefUI.getDefaultTextures(7), DefUI.getDefaultTextures(8), DefUI
						.getDefaultTextures(9));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y,
			int width, int height) {
		this(text, font, color, x, y, width, height, DefUI
				.getDefaultTextures(7), DefUI.getDefaultTextures(8), DefUI
				.getDefaultTextures(9));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y,
			int width, int height, String path) {
		this(text, font, color, x, y, width, height, LTextures
				.loadTexture(path), LTextures.loadTexture(path), LTextures
				.loadTexture(path));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y,
			int width, int height, String a, String b, String c) {
		this(text, font, color, x, y, width, height, LTextures.loadTexture(a),
				LTextures.loadTexture(b), LTextures.loadTexture(c));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y,
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

		if (grayButton) {
			if (!isEnabled()) {
				g.draw(clickedClick, x, y, getWidth(), getHeight(), LColor.gray);
			} else if (isTouchPressed()) {
				g.draw(idleClick, x, y, getWidth(), getHeight());
			} else if (isTouchOver()) {
				g.draw(hoverClick, x, y, getWidth(), getHeight());
			} else {
				g.draw(idleClick, x, y, getWidth(), getHeight(), LColor.gray);
			}
		} else {
			if (!isEnabled()) {
				g.draw(clickedClick, x, y, getWidth(), getHeight());
			} else if (isTouchPressed()) {
				g.draw(idleClick, x, y, getWidth(), getHeight());
			} else if (isTouchOver()) {
				g.draw(hoverClick, x, y, getWidth(), getHeight());
			} else {
				g.draw(idleClick, x, y, getWidth(), getHeight());
			}
		}
		if (text != null) {
			font.drawString(
					g,
					text,
					x + getOffsetLeft() + (getWidth() - font.stringWidth(text))
							/ 2,
					(y + getOffsetTop() + (getHeight() - font.getHeight()) / 2) - 5,
					fontColor);
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
		if (selected) {
			pressed = true;
			return;
		}
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			this.pressed = false;
		}
	}

	public void checked() {
		this.pressed = true;
		this.selected = true;
	}

	public void unchecked() {
		this.pressed = false;
		this.selected = false;
	}

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	@Override
	protected void processTouchDragged() {
		this.over = this.pressed = this.intersects(this.input.getTouchX(),
				this.input.getTouchY());
	}

	@Override
	protected void processTouchClicked() {
		this.doClick();
	}

	@Override
	protected void processTouchPressed() {
		this.downClick();
		this.pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		this.upClick();
		this.pressed = false;
	}

	@Override
	protected void processTouchEntered() {
		this.over = true;
	}

	@Override
	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.pressed = false;
		}
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
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

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
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

	public LTexture getIdleClick() {
		return idleClick;
	}

	public void setIdleClick(LTexture idleClick) {
		this.idleClick = idleClick;
	}

	public LTexture getHoverClick() {
		return hoverClick;
	}

	public void setHoverClick(LTexture hoverClick) {
		this.hoverClick = hoverClick;
	}

	public LTexture getClickedClick() {
		return clickedClick;
	}

	public void setClickedClick(LTexture clickedClick) {
		this.clickedClick = clickedClick;
	}

	public void setTexture(LTexture clickedClick) {
		this.clickedClick = clickedClick;
		this.idleClick = clickedClick;
		this.hoverClick = clickedClick;
	}

	public void setTexture(String path) {
		setTexture(LTextures.loadTexture(path));
	}

	public boolean isGrayButton() {
		return grayButton;
	}

	public void setGrayButton(boolean g) {
		this.grayButton = g;
	}

	@Override
	public String getUIName() {
		return "ClickButton";
	}

}
