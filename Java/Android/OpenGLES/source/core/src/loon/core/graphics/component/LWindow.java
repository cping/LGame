package loon.core.graphics.component;

import loon.action.sprite.Animation;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
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
public class LWindow extends LContainer {

	private String title;

	private LTexture barTexture;

	private Animation animation = new Animation();

	private int barheight;

	private LFont font = LFont.getDefaultFont();

	private LColor fontColor;

	public LWindow(String title, int x, int y, int width, int height) {
		this(title, LColor.white, DefUI.getDefaultTextures(0), DefUI
				.getDefaultTextures(1), x, y, width, height, 40);
	}

	public LWindow(String title, int x, int y, int width, int height,
			String barFile, String backgroundFile) {
		this(title, LColor.white, LTextures.loadTexture(barFile), LTextures
				.loadTexture(backgroundFile), x, y, width, height, 40);
	}

	public LWindow(String txt, LColor color, LTexture bar, LTexture background,
			int x, int y, int width, int height, int barheight) {
		super(x, y, width, height);
		this.customRendering = true;
		this.barTexture = bar;
		this.barheight = barheight;
		this.title = txt;
		this.fontColor = color;
		this.setBackground(background.scale(width, height));
		this.setElastic(true);
		this.setLocked(false);
		this.setLayer(100);
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

	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (visible) {
			g.drawTexture(barTexture, x, y, w, this.barheight);
			if (title != null) {
				float height = font.getHeight();
				LFont old = g.getFont();
				g.setFont(font);
				g.drawString(title, x + 5, y + height, fontColor);
				g.setFont(old);
			}
			if (animation.getSpriteImage() != null) {
				g.drawTexture(animation.getSpriteImage(), x, y);
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

	public void paint(GLEx g) {

	}

	public void update(long elapsedTime) {
		if (visible) {
			super.update(elapsedTime);
			animation.update(elapsedTime);
		}
	}

	protected void processTouchClicked() {
		if (!input.isMoving()) {
			super.processTouchClicked();
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
			super.processTouchDragged();
		}
	}

	protected void processTouchPressed() {
		if (!input.isMoving()) {
			super.processTouchPressed();
		}
	}

	protected void processTouchReleased() {
		if (!input.isMoving()) {
			super.processTouchReleased();
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	public String getUIName() {
		return "Window";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public void dispose() {
		super.dispose();
		if (animation != null) {
			animation.dispose();
			animation = null;
		}
	}

}
