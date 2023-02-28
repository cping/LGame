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
import loon.component.skin.ClickButtonSkin;
import loon.component.skin.SkinManager;
import loon.events.ActionKey;
import loon.events.CallFunction;
import loon.events.SysKey;
import loon.font.BMFont;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 与LButton的差异在于，它内置有默认UI图片，并且可以选择大小，而不是必须按照图片大小拆分
 */
public class LClickButton extends LComponent implements FontSet<LClickButton> {

	private ActionKey onTouch = new ActionKey();

	private LTexture idleClick, hoverClick, disableClick;

	private IFont font;

	private boolean over, grayButton, lightClickedButton;

	private int pressedTime, offsetLeft, offsetTop;

	private LColor fontColor;

	private String text = null;

	private CallFunction _function;

	public static LClickButton makePath(String path) {
		LTexture tex = LSystem.loadTexture(path);
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, tex.getWidth(), tex.getHeight(), tex, tex,
				tex);
	}

	public static LClickButton make(int width, int height, String idle, String hover, String clicked) {
		return new LClickButton(null, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, LSystem.loadTexture(idle),
				LSystem.loadTexture(hover), LSystem.loadTexture(clicked));
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
		LTexture texture = LSystem.loadTexture(clickPath);
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, texture, texture, texture);
	}

	public static LClickButton make(String text, int width, int height, LTexture clicked) {
		return new LClickButton(text, SkinManager.get().getClickButtonSkin().getFont(),
				SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height, clicked, clicked, clicked);
	}

	public static LClickButton make(IFont font, String text, int x, int y, int width, int height) {
		return new LClickButton(text, font, SkinManager.get().getClickButtonSkin().getFontColor(), x, y, width, height);
	}

	public static LClickButton make(IFont font, String text, int width, int height) {
		return new LClickButton(text, font, SkinManager.get().getClickButtonSkin().getFontColor(), 0, 0, width, height);
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
				SkinManager.get().getClickButtonSkin().getDisableTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height) {
		this(text, font, color, x, y, width, height, SkinManager.get().getClickButtonSkin().getIdleClickTexture(),
				SkinManager.get().getClickButtonSkin().getHoverClickTexture(),
				SkinManager.get().getClickButtonSkin().getDisableTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, String path) {
		this(text, font, color, x, y, width, height, LSystem.loadTexture(path), LSystem.loadTexture(path),
				LSystem.loadTexture(path));
	}

	public LClickButton(String text, LColor color, int x, int y, int width, int height, LTexture idle, LTexture hover,
			LTexture clicked) {
		this(text, SkinManager.get().getClickButtonSkin().getFont(), color, x, y, width, height, idle, hover, clicked);
	}

	public LClickButton(String text, LColor color, int x, int y, int width, int height, String a, String b, String c) {
		this(text, SkinManager.get().getClickButtonSkin().getFont(), color, x, y, width, height, LSystem.loadTexture(a),
				LSystem.loadTexture(b), LSystem.loadTexture(c));
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, String a, String b,
			String c) {
		this(text, font, color, x, y, width, height, LSystem.loadTexture(a), LSystem.loadTexture(b),
				LSystem.loadTexture(c));
	}

	public LClickButton(ClickButtonSkin skin, String text, int x, int y, int width, int height) {
		this(text, skin.getFont(), skin.getFontColor(), x, y, width, height, skin.getIdleClickTexture(),
				skin.getHoverClickTexture(), skin.getDisableTexture());
	}

	public LClickButton(String text, LColor color, int x, int y, int width, int height) {
		this(text, SkinManager.get().getClickButtonSkin().getFont(), color, x, y, width, height,
				SkinManager.get().getClickButtonSkin().getIdleClickTexture(),
				SkinManager.get().getClickButtonSkin().getHoverClickTexture(),
				SkinManager.get().getClickButtonSkin().getDisableTexture());
	}

	public LClickButton(String text, IFont font, LColor color, int x, int y, int width, int height, LTexture idle,
			LTexture hover, LTexture disable) {
		super(x, y, width, height);
		this.text = text;
		this.font = font;
		this.fontColor = color;
		this.idleClick = idle;
		this.hoverClick = hover;
		this.disableClick = disable;
		this.lightClickedButton = true;
		if (idle == null && hover == null && disable == null) {
			idleClick = SkinManager.get().getClickButtonSkin().getIdleClickTexture();
			hoverClick = SkinManager.get().getClickButtonSkin().getHoverClickTexture();
			disableClick = SkinManager.get().getClickButtonSkin().getDisableTexture();
		} else if (idle == null) {
			idleClick = SkinManager.get().getClickButtonSkin().getIdleClickTexture();
		} else if (hover == null) {
			hoverClick = SkinManager.get().getClickButtonSkin().getHoverClickTexture();
		} else if (disable == null) {
			disableClick = SkinManager.get().getClickButtonSkin().getDisableTexture();
		}
		freeRes().add(idleClick, hoverClick, disableClick);
		autoSize();
	}

	public void autoSize() {
		if (StringUtils.isEmpty(text) && idleClick != null && getWidth() <= 1 && getHeight() <= 1) {
			this.setWidth(MathUtils.max(getWidth(), idleClick.getWidth()));
			this.setHeight(MathUtils.max(getHeight(), idleClick.getHeight()));
		} else if (getWidth() <= 1f || getHeight() <= 1f) {
			PointF size = FontUtils.getTextWidthAndHeight(font, text, getWidth(), getHeight());
			this.setWidth(MathUtils.max(getWidth(), size.x));
			this.setHeight(MathUtils.max(getHeight(), size.y));
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_component_visible) {
			return;
		}
		if (grayButton) {
			if (!isEnabled()) {
				g.draw(disableClick, x, y, getWidth(), getHeight(),
						_component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
			} else if (isTouchPressed()) {
				if (lightClickedButton) {
					g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor == null ? LColor.lightGray
							: _component_baseColor.mul(LColor.lightGray));
				} else {
					g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor);
				}
			} else if (isTouchOver()) {
				g.draw(hoverClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else {
				g.draw(idleClick, x, y, getWidth(), getHeight(),
						_component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
			}
		} else {
			if (!isEnabled()) {
				g.draw(disableClick, x, y, getWidth(), getHeight(), _component_baseColor);
			} else if (isTouchPressed()) {
				if (lightClickedButton) {
					g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor == null ? LColor.lightGray
							: _component_baseColor.mul(LColor.lightGray));
				} else {
					g.draw(idleClick, x, y, getWidth(), getHeight(), _component_baseColor);
				}
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
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			onTouch.release();
			this.pressedTime = 0;
		}
	}

	public LClickButton checked() {
		onTouch.press();
		return this;
	}

	public LClickButton unchecked() {
		onTouch.release();
		return this;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		this.over = this.intersects(getUITouchX(), getUITouchY());
		if (!onTouch.isPressed()) {
			this.onTouch.press();
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		if (!onTouch.isPressed()) {
			onTouch.press();
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			try {
				_function.call(this);
			} catch (Throwable t) {
				LSystem.error("LClickButton call() exception", t);
			}
		}
		if (onTouch.isPressed()) {
			onTouch.release();
		}
	}

	@Override
	protected void processTouchEntered() {
		this.over = true;
	}

	@Override
	protected void processTouchExited() {
		this.over = false;
		if (onTouch.isPressed()) {
			onTouch.release();
		}
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected() && SysKey.isKeyPressed(SysKey.ENTER)) {
			if (!onTouch.isPressed()) {
				this.pressedTime = 5;
				this.onTouch.press();
				this.doClick();
			}
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected() && SysKey.isKeyRelease(SysKey.ENTER)) {
			if (onTouch.isPressed()) {
				onTouch.release();
			}
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

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return onTouch.isPressed();
	}

	public boolean isPressed() {
		return isTouchPressed();
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LClickButton setFontColor(LColor c) {
		this.fontColor = new LColor(c);
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
		return disableClick;
	}

	public LClickButton setClickedClick(LTexture disableClick) {
		this.disableClick = disableClick;
		return this;
	}

	public LClickButton setTexture(LTexture click) {
		this.disableClick = click;
		this.idleClick = click;
		this.hoverClick = click;
		return this;
	}

	public LClickButton setTexture(String path) {
		setTexture(LSystem.loadTexture(path));
		return this;
	}

	public boolean isLightClickedButton() {
		return lightClickedButton;
	}

	public LClickButton setLightClickedButton(boolean clickedButton) {
		this.lightClickedButton = clickedButton;
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

	public boolean isOver() {
		return over;
	}

	@Override
	public String getUIName() {
		return "ClickButton";
	}

}
