package org.test.lianliankan;

import loon.LTexture;
import loon.action.sprite.Animation;
import loon.action.sprite.Picture;
import loon.opengl.GLEx;

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
				g.draw(img, x() + (getWidth() - img.getWidth()) / 2, y()
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
