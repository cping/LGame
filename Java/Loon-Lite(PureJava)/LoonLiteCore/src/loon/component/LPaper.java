/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.component;

import loon.LTexture;
import loon.LSystem;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class LPaper extends LContainer {

	private Animation animation;

	public LPaper(LTexture background, float x, float y) {
		this(background, x, y, background == null ? 0 : background.getWidth(),
				background == null ? 0 : background.getHeight());
	}

	public LPaper(LTexture background, float x, float y, float w, float h) {
		this(background, null, x, y, w, h);
	}

	public LPaper(LTexture background, LColor color, float x, float y, float w, float h) {
		super(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h));
		this.customRendering = true;
		this.animation = new Animation();
		if (background != null && (color == null || LColor.white.equals(color))) {
			this.setBackground(background, (w != 0 && h != 0) ? false : true);
		} else if (background == null && color != null) {
			this.setBackground(color);
		} else {
			this.setBackground(LColor.black);
		}
		this.setElastic(true);
		this.setLocked(true);
	}

	public LPaper(LTexture background) {
		this(background, 0, 0);
	}

	public LPaper(LColor color) {
		this((LTexture) null, color, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public LPaper(String fileName, int x, int y) {
		this(LSystem.loadTexture(fileName), x, y);
	}

	public LPaper(String fileName) {
		this(fileName, 0, 0);
	}

	public LPaper(int x, int y, int w, int h) {
		this((LTexture) null, x, y, w, h);
	}

	public LPaper(LColor color, int x, int y, int w, int h) {
		this((LTexture) null, color, x, y, w, h);
	}

	public Animation getAnimation() {
		return this.animation;
	}

	public LPaper setAnimation(Animation animation) {
		this.animation = animation;
		return this;
	}

	public LPaper addAnimationFrame(String fileName, long timer) {
		animation.addFrame(fileName, timer);
		return this;
	}

	public LPaper addAnimationFrame(LTexture image, long timer) {
		animation.addFrame(image, timer);
		return this;
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
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	@Override
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (isVisible()) {
			if (animation.getSpriteImage() != null) {
				g.draw(animation.getSpriteImage(), x, y, w, h, _component_baseColor);
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

	@Override
	public void update(long elapsedTime) {
		if (isVisible()) {
			super.update(elapsedTime);
			animation.update(elapsedTime);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "Paper";
	}

	@Override
	public void destory() {
		if (animation != null) {
			animation.close();
			animation = null;
		}
	}

}
