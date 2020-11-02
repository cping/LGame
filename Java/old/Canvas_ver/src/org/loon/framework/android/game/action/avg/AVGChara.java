package org.loon.framework.android.game.action.avg;

import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.GraphicsUtils;

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
public class AVGChara {

	private LImage characterCG;

	private int width;

	private int height;

	private float old_alpha;

	private int x;

	private int y;

	private boolean isMove;

	private int maxWidth;

	private int moveX;

	private int direction;

	private int moveSleep = 10;

	private boolean moving;

	/**
	 * 构造函数，初始化角色图
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public AVGChara(LImage image, final int x, final int y, int width, int height,
			int w) {
		this.characterCG = image;
		this.isMove = true;
		this.width = width;
		this.height = height;
		this.maxWidth = w;
		this.x = x;
		this.y = y;
		this.moveX = 0;
		this.direction = getDirection();
		if (direction == 0) {
			this.moveX = -(width / 2);
		} else {
			this.moveX = w;
		}
	}

	public AVGChara(LImage image, final int x, final int y, int w) {
		this(image, x, y, image.getWidth(), image.getHeight(), w);
	}

	public AVGChara(final String fileName, final int x, final int y, int w) {
		this(GraphicsUtils.loadNotCacheImage(fileName), x, y, w);
	}
	
	public void dispose() {
		if (characterCG != null) {
			characterCG.dispose();
			characterCG = null;
		}
	}
	
	public void finalize() {
		flush();
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
		old_alpha = 0;
		characterCG = null;
		x = 0;
		y = 0;
	}

	public float getNextAlpha() {
		float value = 1.0f;
		float start = getNext();
		float goal = getMaxNext();
		if (start < 0) {
			start += maxWidth;
		}
		if (goal < 0) {
			goal += maxWidth;
		}
		if (goal < start) {
			goal += start;
		}
		value = (float) ((start / goal) * 1.0);
		if (value < 0.1) {
			value = 0.1f;
		}
		if (value > 0.9) {
			value = 1.0f;
		}
		if (old_alpha < value) {
			old_alpha = value;
		} else {
			value = old_alpha;
		}
		return value;
	}

	public int getNext() {
		return moveX;
	}

	public int getMaxNext() {
		return x;
	}

	public synchronized boolean next() {
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
					old_alpha = 0;
				}
			}
		}
		return moving;
	}

	public synchronized void draw(LGraphics g) {
		g.drawImage(characterCG, moveX, y);
	}

	public LImage getCharacterCG() {
		return characterCG;
	}

	public void setCharacterCG(LImage characterCG) {
		this.characterCG = characterCG;
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
}
