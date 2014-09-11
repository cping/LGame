package game.test;

import loon.action.sprite.Animation;
import loon.action.sprite.Picture;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Grid extends Picture {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Animation animation, a1, a2;

	private int type, xpos, ypos;

	public Grid(LTexture img) {
		super(img);
	}

	public Grid(int x, int y) {
		super(x, y);
		xpos = x;
		ypos = y;

	}

	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public boolean isPassable() {
		return !isVisible();
	}

	public void createUI(GLEx g) {
		super.createUI(g);
		switch (type) {
		case 0:
			if (a1 == null) {
				a1 = Animation.getDefaultAnimation("assets/s.png", 3, 48, 48,
						100);
			}
			animation = a1;
			break;
		case 2:
			if (a2 == null) {
				a2 = Animation
						.getDefaultAnimation("assets/s1.png", 48, 48, 100);
			}
			animation = a2;
			break;
		default:
			break;
		}
		if (animation == null) {
			return;
		}
		if (type == 0 || type == 2) {
			LTexture img = animation.getSpriteImage();
			if (img != null) {
				g.drawTexture(img, x() + (getWidth() - img.getWidth()) / 2, y()
						+ (getHeight() - img.getHeight()) / 2);
			}
		}
	}

	public void update(long t) {
		super.update(t);
		if (animation != null) {
			animation.update(t);
		}
	}

	public void setBorder(int type) {
		this.type = type;

	}

}
