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
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.ClickButtonSkin;
import loon.component.skin.SkinManager;
import loon.event.CallFunction;
import loon.font.BMFont;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.StringUtils;

/**
 * 与LButton的差异在于，它内置有默认UI图片，并且可以选择大小，而不是必须按照图片大小拆分
 */
public class LClickButton extends LComponent implements FontSet<LClickButton> {

	private CallFunction _function;

	private LTexture idleClick, hoverClick, clickedClick;

	private IFont font;

	private boolean pressed, selected, over, grayButton;

	private int pressedTime, offsetLeft, offsetTop;

	private LColor fontColor;

	private String text = null;

	public static LClickButton makePath(String path) {
		LTexture tex = LTextures.loadTexture(path);
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, tex.getWidth(), tex.getHeight(), tex, tex,
				tex);
	}

	public static LClickButton make(int width, int height, String idle, String hover, String clicked) {
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, LTextures.loadTexture(idle),
				LTextures.loadTexture(hover), LTextures.loadTexture(clicked));
	}

	public static LClickButton make(int width, int height, LTexture idle, LTexture hover, LTexture clicked) {
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, idle, hover, clicked);
	}

	public static LClickButton make(LTexture texture) {
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, texture.getWidth(), texture.getHeight(),
				texture, texture, texture);
	}

	public static LClickButton make(String text) {
		return new LClickButton(text, 0, 0, 1, 1);
	}

	public static LClickButton make(String text, int width, int height) {
		return new LClickButton(text, 0, 0, width, height);
	}

	public static LClickButton make(String text, int x, int y, int width, int height) {
		return new LClickButton(text, x, y, width, height);
	}

	public static LClickButton make(String text, int width, int height, String clickPath) {
		LTexture texture = LTextures.loadTexture(clickPath);
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, texture, texture, texture);
	}

	public static LClickButton make(String text, int width, int height, LTexture clicked) {
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, clicked, clicked, clicked);
	}

	public static LClickButton make(IFont font, String text, int width, int height, LTexture clicked) {
		return new LClickButton(text, font, SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height,
				clicked, clicked, clicked);
	}

	public static LClickButton make(String text, int width, int height, LTexture hover, LTexture clicked) {
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, hover, hover, clicked);
	}

	public static LClickButton make(IFont font, String text, int width, int height, LTexture hover, LTexture clicked) {
		return new LClickButton(text, font, SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height,
				hover, hover, clicked);
	}

	public static LClickButton make(String text, int width, int height, LTexture idle, LTexture hover,
			LTexture clicked) {
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, idle, hover, clicked);
	}

	public LClickButton(String text, int x, int y, int width, int height) {
		this(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), x, y, width, height,
				SkinManager.get().getClickButtonSkin().getIdleClickTexture(),
				SkinManager.get().getClickButtonSkin().getHoverClickTexture(),
				SkinManager.get().getClickButtonSkin().getClickedTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height) {
		this(text, font, color, x, y, width, height, SkinManager.get().getClickButtonSkin().getIdleClickTexture(),
				SkinManager.get().getClickButtonSkin().getHoverClickTexture(),
				SkinManager.get().getClickButtonSkin().getClickedTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, String path) {
		this(text, font, color, x, y, width, height, LTextures.loadTexture(path), LTextures.loadTexture(path),
				LTextures.loadTexture(path));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, String a, String b,
			String c) {
		this(text, font, color, x, y, width, height, LTextures.loadTexture(a), LTextures.loadTexture(b),
				LTextures.loadTexture(c));
	}

	public LClickButton(ClickButtonSkin skin, String text, int x, int y, int width, int height) {
		this(text, skin.getFont(), skin.getFontColor(), x, y, width, height, skin.getIdleClickTexture(),
				skin.getHoverClickTexture(), skin.getClickedTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, LTexture idle,
			LTexture hover, LTexture clicked) {
		super(x, y, width, height);
		this.text = text;
		this.font = font;
		this.fontColor = color;
		this.idleClick = idle;
		this.hoverClick = hover;
		this.clickedClick = clicked;
		if (idle == null && hover == null && clicked == null) {
			idleClick = SkinManager.get().getClickButtonSkin().getIdleClickTexture();
			hoverClick = SkinManager.get().getClickButtonSkin().getHoverClickTexture();
			clickedClick = SkinManager.get().getClickButtonSkin().getClickedTexture();
		} else if (idle == null) {
			idleClick = SkinManager.get().getClickButtonSkin().getIdleClickTexture();
		} else if (hover == null) {
			hoverClick = SkinManager.get().getClickButtonSkin().getHoverClickTexture();
		} else if (clicked == null) {
			clickedClick = SkinManager.get().getClickButtonSkin().getClickedTexture();
		}
		freeRes().add(idleClick, hoverClick, clickedClick);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_component_visible) {
			return;
		}
		if (grayButton) {
			if (!isEnabled()) {
				g.draw(clickedClick, x, y, getWidth(), getHeight(),
						_component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
			} else if (isTouchPressed()) {
				g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else if (isTouchOver()) {
				g.draw(hoverClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else {
				g.draw(idleClick, x, y, getWidth(), getHeight(),
						_component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
			}
		} else {
			if (!isEnabled()) {
				g.draw(clickedClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else if (isTouchPressed()) {
				g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else if (isTouchOver()) {
				g.draw(hoverClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else {
				g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor);
			}
		}
		if (!StringUtils.isEmpty(text)) {
			if (font instanceof BMFont) {
				font.drawString(g, text, x + getOffsetLeft() + (getWidth() - font.stringWidth(text)) / 2,
						(y + getOffsetTop() + (getHeight() - font.getHeight()) / 2) - 5, fontColor);
			} else {
				font.drawString(g, text, x + getOffsetLeft() + (getWidth() - font.stringWidth(text)) / 2,
						(y + getOffsetTop() + (getHeight() - font.getHeight()) / 2) - (LSystem.isDesktop() ? 2 : 0),
						fontColor);

			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
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

	public LClickButton checked() {
		this.pressed = true;
		this.selected = true;
		return this;
	}

	public LClickButton unchecked() {
		this.pressed = false;
		this.selected = false;
		return this;
	}

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return this.pressed;
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
		if (_function != null) {
			_function.call(this);
		}
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

	public LClickButton setText(String t) {
		if (StringUtils.isEmpty(t) || t.equals(text)) {
			return this;
		}
		this.text = t;
		if (font instanceof LFont) {
			LSTRDictionary.get().bind((LFont) font, text);
		}
		return this;
	}

	public int getOffsetLeft() {
		return offsetLeft;
	}

	public LClickButton setOffsetLeft(int offsetLeft) {
		this.offsetLeft = offsetLeft;
		return this;
	}

	public int getOffsetTop() {
		return offsetTop;
	}

	public LClickButton setOffsetTop(int offsetTop) {
		this.offsetTop = offsetTop;
		return this;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public LClickButton setFont(IFont font) {
		this.font = font;
		return this;
	}

	public boolean isPressed() {
		return pressed;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LClickButton setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public LTexture getIdleClick() {
		return idleClick;
	}

	public LClickButton setIdleClick(LTexture idleClick) {
		this.idleClick = idleClick;
		return this;
	}

	public LTexture getHoverClick() {
		return hoverClick;
	}

	public LClickButton setHoverClick(LTexture hoverClick) {
		this.hoverClick = hoverClick;
		return this;
	}

	public LTexture getClickedClick() {
		return clickedClick;
	}

	public LClickButton setClickedClick(LTexture clickedClick) {
		this.clickedClick = clickedClick;
		return this;
	}

	public LClickButton setTexture(LTexture clickedClick) {
		this.clickedClick = clickedClick;
		this.idleClick = clickedClick;
		this.hoverClick = clickedClick;
		return this;
	}

	public LClickButton setTexture(String path) {
		setTexture(LTextures.loadTexture(path));
		return this;
	}

	public boolean isGrayButton() {
		return grayButton;
	}

	public LClickButton setGrayButton(boolean g) {
		this.grayButton = g;
		return this;
	}

	public CallFunction getFunction() {
		return _function;
	}

	public LClickButton setFunction(CallFunction function) {
		this._function = function;
		return this;
	}

	@Override
	public String getUIName() {
		return "ClickButton";
	}

}
