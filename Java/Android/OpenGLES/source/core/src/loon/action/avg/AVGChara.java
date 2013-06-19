package loon.action.avg;

import java.io.IOException;

import loon.action.sprite.ISprite;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.utils.StringUtils;

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
public class AVGChara implements LRelease {

	private LTexture characterCG;

	private int width;

	private int height;

	int x;

	int y;

	int flag = -1;

	float time;

	float currentFrame;

	float opacity;

	protected boolean isMove, isAnimation, isVisible = true;

	int maxWidth, maxHeight;

	private int moveX;

	private int direction;

	private int moveSleep = 10;

	private boolean moving;

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
	public AVGChara(LTexture image, final int x, final int y, int width,
			int height) {
		this.load(image, x, y, width, height, LSystem.screenRect.width,
				LSystem.screenRect.height);
	}

	public AVGChara(LTexture image, final int x, final int y) {
		this.load(image, x, y);
	}

	public AVGChara(final String resName, final int x, final int y) {
		this(resName, x, y, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public AVGChara(final String resName, final int x, final int y,
			final int w, final int h) {
		String path = resName;
		if (StringUtils.startsWith(path, '"')) {
			path = resName.replaceAll("\"", "");
		}
		if (path.endsWith(".an")) {
			this.x = x;
			this.y = y;
			this.isAnimation = true;
			try {
				this.anm = new AVGAnm(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.maxWidth = w;
			this.maxHeight = h;
		} else {
			this.load(LTextures.loadTexture(path), x, y);
		}
	}

	String tmp_path;

	void update(String path) {
		this.tmp_path = path;
	}

	private void load(LTexture image, final int x, final int y) {
		this.load(image, x, y, image.getWidth(), image.getHeight(),
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	private void load(LTexture image, final int x, final int y, int width,
			int height, final int w, final int h) {
		this.maxWidth = w;
		this.maxHeight = h;
		this.isAnimation = false;
		this.characterCG = image;
		this.isMove = true;
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

	@Override
	public void finalize() {
		flush();
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
		isMove = move;
	}

	public void flush() {
		characterCG = null;
		x = 0;
		y = 0;
	}

	public int getNext() {
		return moveX;
	}

	public int getMaxNext() {
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
		g.drawTexture(characterCG, moveX, y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		if (isMove) {
			int move = x - this.moveX;
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

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getMoveSleep() {
		return moveSleep;
	}

	public void setMoveSleep(int moveSleep) {
		this.moveSleep = moveSleep;
	}

	public int getMoveX() {
		return moveX;
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	void setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public LTexture getTexture() {
		return characterCG;
	}

	@Override
	public void dispose() {
		this.isVisible = false;
		if (characterCG != null) {
			characterCG.destroy();
			characterCG = null;
		}
		if (anm != null) {
			anm.dispose();
			anm = null;
			isAnimation = false;
		}
	}

}
