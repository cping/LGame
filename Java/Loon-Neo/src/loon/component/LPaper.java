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
import loon.LTexture.Format;
import loon.LTextures;
import loon.action.sprite.Animation;
import loon.opengl.GLEx;

public class LPaper extends LContainer {

	private Animation animation = new Animation();

	public LPaper(LTexture background, int x, int y) {
		super(x, y, background.getWidth(), background.getHeight());
		this.customRendering = true;
		this.setBackground(background);
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public LPaper(LTexture background) {
		this(background, 0, 0);
	}

	public LPaper(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public LPaper(String fileName) {
		this(fileName, 0, 0);
	}

	public LPaper(int x, int y, int w, int h) {
		this(LTextures.createTexture(w < 1 ? w = 1 : w, h < 1 ? h = 1 : h,
				Format.LINEAR), x, y);
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
	protected void processTouchClicked() {
		if (!input.isMoving()) {
			this.doClick();
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
		if (visible) {
			if (animation.getSpriteImage() != null) {
				g.draw(animation.getSpriteImage(), x, y, baseColor);
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
		if (visible) {
			super.update(elapsedTime);
			animation.update(elapsedTime);
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!input.isMoving()) {
			this.downClick();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!input.isMoving()) {
			this.upClick();
		}
	}


	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	@Override
	public String getUIName() {
		return "Paper";
	}

	@Override
	public void close() {
		super.close();
		if (animation != null) {
			animation.close();
			animation = null;
		}
	}

}
