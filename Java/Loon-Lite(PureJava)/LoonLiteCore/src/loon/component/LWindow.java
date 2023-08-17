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
 *
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.component.layout.HorizontalLayout;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.VerticalLayout;
import loon.component.skin.SkinManager;
import loon.component.skin.WindowSkin;
import loon.events.CallFunction;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;

/**
 * 一个空的窗体UI,单纯用来添加其它组件到上面,也可以alert弹出一些信息给用户
 */
public class LWindow extends LContainer implements FontSet<LWindow> {

	private String _title;

	private LTexture _barTexture;

	private Animation animation = new Animation();

	private int _barheight;

	private IFont _font;

	private LColor _fontColor;

	public static LWindow alert(LTexture textureBtn, String title, String firstButton, String secondButton,
			String closeButton, float x, float y, CallFunction first, CallFunction second, CallFunction close,
			boolean vertical) {
		return alert(textureBtn, title, firstButton, secondButton, closeButton, x, y, first, second, close, vertical);
	}

	public static LWindow alert(IFont font, LTexture textureBtn, String title, String firstButton, String secondButton,
			String closeButton, float x, float y, CallFunction first, CallFunction second, CallFunction close,
			boolean vertical) {
		return alert(font, textureBtn, title, firstButton, secondButton, closeButton, x, y, first, second, close,
				vertical);
	}

	public static LWindow alert(String barPath, String backgroundPath, String btnPath, String title, String firstButton,
			String secondButton, String closeButton, float x, float y, float width, float height, float barheight,
			CallFunction first, CallFunction second, CallFunction close, LColor fontColor, boolean vertical) {
		return alert(LSystem.loadTexture(barPath), LSystem.loadTexture(backgroundPath),
				LSystem.loadTexture(btnPath), title, firstButton, secondButton, closeButton, x, y, width, height,
				barheight, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, String barPath, String backgroundPath, String btnPath, String title,
			String firstButton, String secondButton, String closeButton, float x, float y, float width, float height,
			float barheight, CallFunction first, CallFunction second, CallFunction close, LColor fontColor,
			boolean vertical) {
		return alert(font, LSystem.loadTexture(barPath), LSystem.loadTexture(backgroundPath),
				LSystem.loadTexture(btnPath), title, firstButton, secondButton, closeButton, x, y, width, height,
				barheight, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(String title, String firstButton, float x, float y, CallFunction first,
			boolean vertical) {
		return alert(title, firstButton, null, null, x, y, first, null, null, null, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, float x, float y, CallFunction first,
			boolean vertical) {
		return alert(font, title, firstButton, null, null, x, y, first, null, null, null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, float x, float y,
			CallFunction first, CallFunction second, boolean vertical) {
		return alert(title, firstButton, secondButton, null, x, y, first, second, null, null, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, float x, float y,
			CallFunction first, CallFunction second, boolean vertical) {
		return alert(font, title, firstButton, secondButton, null, x, y, first, second, null, null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, CallFunction first, CallFunction second, CallFunction close, boolean vertical) {
		return alert(title, firstButton, secondButton, closeButton, x, y, first, second, close, null, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, CallFunction first, CallFunction second, CallFunction close, boolean vertical) {
		return alert(font, title, firstButton, secondButton, closeButton, x, y, first, second, close, null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, CallFunction first, CallFunction second, CallFunction close, LColor fontColor, boolean vertical) {
		return alert((LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton, closeButton,
				x, y, LSystem.viewSize.getWidth() * 0.75f, LSystem.viewSize.getHeight() * 0.45f, 40, first, second,
				close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, CallFunction first, CallFunction second, CallFunction close, LColor fontColor,
			boolean vertical) {
		return alert(font, (LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton,
				closeButton, x, y, LSystem.viewSize.getWidth() * 0.75f, LSystem.viewSize.getHeight() * 0.45f, 40, first,
				second, close, fontColor, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, float barheight, CallFunction first, CallFunction second, CallFunction close, LColor fontColor,
			boolean vertical) {
		return alert((LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton, closeButton,
				x, y, LSystem.viewSize.getWidth() * 0.75f, LSystem.viewSize.getHeight() * 0.45f, barheight, first,
				second, close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, float barheight, CallFunction first, CallFunction second, CallFunction close,
			LColor fontColor, boolean vertical) {
		return alert(font, (LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton,
				closeButton, x, y, LSystem.viewSize.getWidth() * 0.75f, LSystem.viewSize.getHeight() * 0.45f, barheight,
				first, second, close, fontColor, vertical);
	}

	public static LWindow alert(String title, String firstButton, float x, float y, float width, float height,
			CallFunction first, boolean vertical) {
		return alert(title, firstButton, null, null, x, y, width, height, first, null, null, null, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, float x, float y, float width,
			float height, CallFunction first, boolean vertical) {
		return alert(font, title, firstButton, null, null, x, y, width, height, first, null, null, null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, float x, float y, float width,
			float height, CallFunction first, CallFunction second, boolean vertical) {
		return alert(title, firstButton, secondButton, null, x, y, width, height, first, second, null, null, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, float x, float y,
			float width, float height, CallFunction first, CallFunction second, boolean vertical) {
		return alert(font, title, firstButton, secondButton, null, x, y, width, height, first, second, null, null,
				vertical);
	}

	public static LWindow alert(String firstButton, float x, float y, float width, float height, CallFunction first,
			boolean vertical) {
		return alert(null, firstButton, null, null, x, y, width, height, first, null, null, null, vertical);
	}

	public static LWindow alert(IFont font, String firstButton, float x, float y, float width, float height,
			CallFunction first, boolean vertical) {
		return alert(font, null, firstButton, null, null, x, y, width, height, first, null, null, null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, float width, float height, CallFunction first, CallFunction second, CallFunction close,
			boolean vertical) {
		return alert(title, firstButton, secondButton, closeButton, x, y, width, height, first, second, close, null,
				vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, float width, float height, CallFunction first, CallFunction second, CallFunction close,
			boolean vertical) {
		return alert(font, title, firstButton, secondButton, closeButton, x, y, width, height, first, second, close,
				null, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, float width, float height, CallFunction first, CallFunction second, CallFunction close,
			LColor fontColor, boolean vertical) {
		return alert((LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton, closeButton,
				x, y, width, height, 40, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, float width, float height, CallFunction first, CallFunction second, CallFunction close,
			LColor fontColor, boolean vertical) {
		return alert(font, (LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton,
				closeButton, x, y, width, height, 40, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(String title, String firstButton, String secondButton, String closeButton, float x,
			float y, float width, float height, float barheight, CallFunction first, CallFunction second,
			CallFunction close, LColor fontColor, boolean vertical) {
		return alert((LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton, closeButton,
				x, y, width, height, barheight, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, String title, String firstButton, String secondButton, String closeButton,
			float x, float y, float width, float height, float barheight, CallFunction first, CallFunction second,
			CallFunction close, LColor fontColor, boolean vertical) {
		return alert(font, (LTexture) null, (LTexture) null, (LTexture) null, title, firstButton, secondButton,
				closeButton, x, y, width, height, barheight, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(LTexture bar, LTexture background, LTexture textureBtn, String title,
			String firstButton, String secondButton, String closeButton, float x, float y, float width, float height,
			float barheight, CallFunction first, CallFunction second, CallFunction close, LColor fontColor,
			boolean vertical) {
		return alert(SkinManager.get().getWindowSkin().getFont(), bar, background, textureBtn, title, firstButton,
				secondButton, closeButton, x, y, width, height, barheight, first, second, close, fontColor, vertical);
	}

	public static LWindow alert(IFont font, LTexture bar, LTexture background, LTexture textureBtn, String title,
			String firstButton, String secondButton, String closeButton, float x, float y, float width, float height,
			float barheight, CallFunction first, CallFunction second, CallFunction close, LColor fontColor,
			boolean vertical) {
		if (fontColor == null) {
			fontColor = LColor.white;
		}
		LWindow window = new LWindow(font, title, fontColor, bar, background, x, y, width, height, barheight);
		Screen screen = window.getScreen();
		window.setFontColor(fontColor);
		window.setColor(LColor.lightGray);
		if (x == 0 && y == 0) {
			screen.centerOn(window);
		}
		int btnWidth = (int) (width * 0.3f);
		int btnHeight = (int) (height * 0.3f);
		int count = 0;
		if (firstButton != null) {
			LClickButton firstBtn = LClickButton.make(font, firstButton, btnWidth, btnHeight, textureBtn);
			firstBtn.setFontColor(fontColor);
			firstBtn.setFunction(first);
			window.add(firstBtn);
			count++;
		}
		if (secondButton != null) {
			LClickButton secondBtn = LClickButton.make(font, secondButton, btnWidth, btnHeight, textureBtn);
			secondBtn.setFontColor(fontColor);
			secondBtn.setFunction(second);
			window.add(secondBtn);
			count++;
		}
		if (closeButton != null) {
			LClickButton closeBtn = LClickButton.make(font, closeButton, btnWidth, btnHeight, textureBtn);
			closeBtn.setFontColor(fontColor);
			closeBtn.setFunction(close);
			window.add(closeBtn);
			count++;
		}
		if (vertical) {
			int btnSize = btnHeight * count;
			int per = (int) (100 - (btnSize / width) * 100) / 2;
			if (per % 2 != 0) {
				per -= 1;
			}
			LayoutConstraints root = window.getRootConstraints();
			root.setPaddingLeft("5%");
			root.setPaddingRight("5%");
			root.setPaddingTop((title == null ? per : (per + 10)) + "%");
			root.setPaddingBottom(per + "%");
			window.packLayout(VerticalLayout.at().setChangeSize(true), 0, 0, 0, -2);
			screen.add(window);
		} else {
			int btnSize = btnWidth * count;
			int per = (int) (100 - (btnSize / width) * 100) / 2;
			if (per % 2 != 0) {
				per -= 1;
			}
			LayoutConstraints root = window.getRootConstraints();
			root.setPaddingLeft(per + "%");
			root.setPaddingRight(per + "%");
			root.setPaddingTop("35%");
			root.setPaddingBottom("25%");
			window.packLayout(HorizontalLayout.at().setChangeSize(!(btnSize < width)));
			screen.add(window);
		}
		window.in();
		return window;
	}

	public static LWindow pop(String title, float x, float y) {
		LWindow window = at(title, x, y);
		window.getScreen().centerOn(window);
		window.getScreen().add(window);
		return window;
	}

	public static LWindow at(String title, float x, float y) {
		if (LSystem.getProcess() != null) {
			return at(title, x, y, LSystem.viewSize.width * 0.75f, LSystem.viewSize.height * 0.45f);
		}
		return at(title, x, y, 200, 200);
	}

	public static LWindow at(String title, float x, float y, float width, float height) {
		return new LWindow(title, x, y, width, height);
	}

	public LWindow(IFont font, String title, float x, float y, float width, float height) {
		this(title, SkinManager.get().getWindowSkin().getFontColor(), SkinManager.get().getWindowSkin().getBarTexture(),
				SkinManager.get().getWindowSkin().getBackgroundTexture(), x, y, width, height, 40);
	}

	public LWindow(String title, float x, float y, float width, float height) {
		this(SkinManager.get().getWindowSkin().getFont(), title, SkinManager.get().getWindowSkin().getFontColor(),
				SkinManager.get().getWindowSkin().getBarTexture(),
				SkinManager.get().getWindowSkin().getBackgroundTexture(), x, y, width, height, 40);
	}

	public LWindow(String title, float x, float y, float width, float height, String barFile, String backgroundFile) {
		this(SkinManager.get().getWindowSkin().getFont(), title, SkinManager.get().getWindowSkin().getFontColor(),
				LSystem.loadTexture(barFile), LSystem.loadTexture(backgroundFile), x, y, width, height, 40);
	}

	public LWindow(String txt, LColor color, LTexture bar, LTexture background, float x, float y, float width,
			float height, float barheight) {
		this(SkinManager.get().getWindowSkin().getFont(), txt, color, bar, background, x, y, width, height, barheight);
	}

	public LWindow(WindowSkin skin, String txt, float x, float y, float width, float height, float barheight) {
		this(skin.getFont(), txt, skin.getFontColor(), skin.getBarTexture(), skin.getBackgroundTexture(), x, y, width,
				height, barheight);
	}

	public LWindow(IFont font, String txt, LColor color, LTexture bar, LTexture background, float x, float y,
			float width, float height, float barheight) {
		super((int) x, (int) y, (int) width, (int) height);
		this._font = font;
		this._barTexture = bar;
		if (_barTexture == null) {
			_barTexture = SkinManager.get().getWindowSkin().getBarTexture();
		}
		this._barheight = (int) barheight;
		this._title = txt;
		this._fontColor = color;
		this.onlyBackground(
				_background == null ? SkinManager.get().getWindowSkin().getBackgroundTexture() : _background);
		this.setElastic(true);
		this.setLocked(false);
		freeRes().add(_barTexture);
	}

	public Animation getAnimation() {
		return this.animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void addAnimationFrame(String fileName, long timer) {
		animation.addFrame(fileName, timer);
	}

	public void addAnimationFrame(LTexture image, long timer) {
		animation.addFrame(image, timer);
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	public void paint(GLEx g) {

	}

	@Override
	public void update(long elapsedTime) {
		if (isVisible()) {
			super.update(elapsedTime);
			animation.update(elapsedTime);
		}
	}

	@Override
	protected void processTouchClicked() {
		if (!input.isMoving()) {
			super.processTouchClicked();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!input.isMoving()) {
			super.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!input.isMoving()) {
			super.processTouchReleased();
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (isVisible()) {
			if (_background != null) {
				g.draw(_background, x, y, getWidth(), getHeight(), _component_baseColor);
			}
			if (_title != null) {
				g.draw(_barTexture, x, y, getWidth(), this._barheight, _component_baseColor);
				if (_font != null) {
					_font.drawString(g, _title, x + 5,
							y + (this._barheight - _font.getHeight()) / 2 - (LSystem.isDesktop() ? 5 : 0), _fontColor);
				}
			}
			if (animation.getSpriteImage() != null) {
				g.draw(animation.getSpriteImage(), x, y, _component_baseColor);
			}
			if (x != 0 && y != 0) {
				g.translate(x, y);
				paint(g);
				g.translate(-x, -y);
			} else {
				paint(g);
			}
		}

	}

	@Override
	public LComponent setBackground(LTexture back) {
		_background = back;
		return this;
	}

	@Override
	public LComponent setBackground(String path) {
		return setBackground(LSystem.loadTexture(path));
	}

	public LTexture getBarTexture() {
		return _barTexture;
	}

	public LComponent setBarTexture(LTexture bar) {
		this._barTexture = bar;
		return this;
	}

	public void setBarTexture(String path) {
		setBarTexture(LSystem.loadTexture(path));
	}

	@Override
	public String getUIName() {
		return "Window";
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public LWindow setFont(IFont font) {
		this._font = font;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	@Override
	public LWindow setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
		return this;
	}

	@Override
	public void destory() {
		if (animation != null) {
			animation.close();
			animation = null;
		}
	}

}
