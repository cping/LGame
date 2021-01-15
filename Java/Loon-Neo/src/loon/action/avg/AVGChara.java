
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.avg;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.Visible;
import loon.action.sprite.ISprite;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.StringUtils;

public class AVGChara implements Visible, XY, LRelease {

	private LTexture characterCG;

	private float width;

	private float height;

	protected float x;

	protected float y;

	protected int flag = -1;

	protected float time;

	protected float currentFrame;

	protected float opacity;

	protected boolean moved, showAnimation, visible;

	protected int maxWidth, maxHeight;

	private float moveX;

	private int direction;

	private int moveSleep = 10;

	private boolean moving, closed;

	protected AVGAnm anm;

	/**
	 * 构造函数，初始化角色图
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public AVGChara(LTexture image, final int x, final int y, int width, int height) {
		this.load(image, x, y, width, height, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public AVGChara(LTexture image, final int x, final int y) {
		this.load(image, x, y);
	}

	public AVGChara(final String resName, final int x, final int y) {
		this(resName, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public AVGChara(final String resName, final int x, final int y, final int w, final int h) {
		String path = resName;
		if (StringUtils.startsWith(path, LSystem.DOUBLE_QUOTES)) {
			path = resName.replaceAll("\"", LSystem.EMPTY);
		}
		if (path.endsWith(".an")) {
			this.x = x;
			this.y = y;
			this.showAnimation = true;
			this.anm = new AVGAnm(path);
			this.maxWidth = w;
			this.maxHeight = h;
		} else {
			this.load(LSystem.loadTexture(path), x, y);
		}
		this.visible = true;
	}

	private void load(LTexture image, final int x, final int y) {
		this.load(image, x, y, image.getWidth(), image.getHeight(), LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	private void load(LTexture image, final int x, final int y, int width, int height, final int w, final int h) {
		this.maxWidth = w;
		this.maxHeight = h;
		this.showAnimation = false;
		this.characterCG = image;
		this.moved = true;
		this.visible = true;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.moveX = 0;
		this.direction = getDirection();
		if (direction == 0) {
			this.moveX = -(width / 2);
		} else {
			this.moveX = maxWidth;
		}
	}

	public void setFlag(int f, float delay) {
		this.flag = f;
		this.time = delay;
		if (flag == ISprite.TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
	}

	public int getScreenWidth() {
		return maxWidth;
	}

	public int getScreenHeight() {
		return maxHeight;
	}

	private int getDirection() {
		int offsetX = maxWidth / 2;
		if (x < offsetX) {
			return 0;
		} else {
			return 1;
		}
	}

	public void setMove(boolean move) {
		moved = move;
	}

	public boolean isMoved() {
		return moved;
	}

	public void flush() {
		characterCG = null;
		x = 0;
		y = 0;
	}

	public float getNext() {
		return moveX;
	}

	public float getMaxNext() {
		return x;
	}

	public boolean next() {
		moving = false;
		if (moveX != x) {
			for (int sleep = 0; sleep < moveSleep; sleep++) {
				if (direction == 0) {
					moving = (x > moveX);
				} else {
					moving = (x < moveX);
				}
				if (moving) {
					switch (direction) {
					case 0:
						moveX += 1;
						break;
					case 1:
						moveX -= 1;
						break;
					default:
						moveX = x;
						break;
					}
				} else {
					moveX = x;
				}
			}
		}
		return moving;
	}

	void update(long t) {

	}

	void draw(GLEx g) {
		g.draw(characterCG, moveX, y);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (moved) {
			float move = x - this.moveX;
			if (move < 0) {
				this.moveX = this.x;
				this.x = x;
				direction = 1;
			} else {
				this.moveX = move;
				this.x = x;
			}
		} else {
			this.moveX = x;
			this.x = x;
		}

	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getMoveSleep() {
		return moveSleep;
	}

	public void setMoveSleep(int moveSleep) {
		this.moveSleep = moveSleep;
	}

	public float getMoveX() {
		return moveX;
	}

	public boolean isAnimation() {
		return showAnimation;
	}

	void setAnimation(boolean a) {
		this.showAnimation = a;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	public LTexture getTexture() {
		return characterCG;
	}

	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public void close() {
		this.visible = false;
		if (characterCG != null) {
			characterCG.close();
			characterCG = null;
		}
		if (anm != null) {
			anm.close();
			anm = null;
			showAnimation = false;
		}
		closed = true;
	}

}
