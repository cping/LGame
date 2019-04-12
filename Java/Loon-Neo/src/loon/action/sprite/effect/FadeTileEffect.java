/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.Entity;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * 瓦片(向四周散开或向中心聚拢)淡入淡出效果
 *
 */
public class FadeTileEffect extends Entity implements BaseEffect {

	private int tileWidth, tileHeight;

	private boolean completed;

	private int count;

	private int speed = 1;

	private int tmpflag = 0;

	private boolean[][] conversions;
	private boolean[][] temp;

	private boolean usefore = false;

	private LColor back = LColor.black;
	private LColor fore = LColor.white;

	private int type;

	private LTimer timer;

	public FadeTileEffect(int type, LColor c) {
		this(type, 1, 1, c, LColor.white);
	}

	public FadeTileEffect(int type) {
		this(type, 1, 1, LColor.black, LColor.white);
	}

	public FadeTileEffect(int type, int count, int speed, LColor back,
			LColor fore) {
		this(type, count, speed, back, fore, 64, 32);
	}

	public FadeTileEffect(int type, int count, int speed, LColor back,
			LColor fore, int w, int h) {
		this.type = type;
		this.count = count;
		this.speed = speed;
		this.setSize(w, h);
		this.timer = new LTimer(60);
		this.tileWidth = (int) (((LSystem.viewSize.getWidth() / w)) + 1);
		this.tileHeight = (int) (((LSystem.viewSize.getHeight() / h)) + 1);
		this.conversions = new boolean[tileWidth][tileHeight];
		this.temp = new boolean[tileWidth][tileHeight];
		this.back = back;
		this.fore = fore;
		this.setRepaint(true);
		this.reset();
	}

	private boolean filledObject(int x, int y) {
		if (x > 0) {
			if (conversions[x - 1][y]) {
				return true;
			}
		} else if (x < tileWidth - 1) {
			if (conversions[x + 1][y]) {
				return true;
			}
		} else if (y > 0) {
			if (conversions[x][y - 1]) {
				return true;
			}
		} else if (y < tileHeight - 1) {
			if (conversions[x][y + 1]) {
				return true;
			}
		}
		return false;
	}

	public float getDelay() {
		return timer.getDelay();
	}

	public void setDelay(int delay) {
		timer.setDelay(delay);
	}

	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			int count = 0;
			if (ISprite.TYPE_FADE_OUT == type) {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							temp[x][y] = false;
						}
					}
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							if (!temp[x][y] && conversions[x][y]) {
								temp[x][y] = true;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x - 1][y]) {
										conversions[x - 1][y] = true;
										temp[x - 1][y] = true;
									}
								}
								if (x < tileWidth - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x + 1][y]) {
										conversions[x + 1][y] = true;
										temp[x + 1][y] = true;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y - 1]) {
										conversions[x][y - 1] = true;
										temp[x][y - 1] = true;
									}
								}
								if (y < tileHeight - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y + 1]) {
										conversions[x][y + 1] = true;
										temp[x][y + 1] = true;
									}
								}

							}
						}
					}
				}

				for (int x = 0; x < tileWidth; x++) {
					for (int y = 0; y < tileHeight; y++) {
						if (!conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (count == 0) {
					completed = true;
				}
			} else {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							temp[x][y] = true;
						}
					}
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							if (temp[x][y] && !conversions[x][y]) {
								temp[x][y] = false;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x - 1][y]) {
										conversions[x - 1][y] = false;
										temp[x - 1][y] = false;
									}
								}
								if (x < tileWidth - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x + 1][y]) {
										conversions[x + 1][y] = false;
										temp[x + 1][y] = false;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y - 1]) {
										conversions[x][y - 1] = false;
										temp[x][y - 1] = false;
									}
								}
								if (y < tileHeight - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y + 1]) {
										conversions[x][y + 1] = false;
										temp[x][y + 1] = false;
									}
								}

							}
						}
					}
				}
				for (int x = 0; x < tileWidth; x++) {
					for (int y = 0; y < tileHeight; y++) {
						if (!conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (tmpflag >= tileHeight) {
					completed = true;
				}
				if (count >= tileWidth) {
					tmpflag++;
				}

			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(back);
		for (int x = 0; x < tileWidth; x++) {
			for (int y = 0; y < tileHeight; y++) {
				if (usefore) {
					if (conversions[x][y]) {
						g.setColor(back);
						g.fillRect((x * _width) + offsetX + _offset.x, (y * _height)
								+ offsetY + _offset.y, _width, _height);
					} else if (!conversions[x][y] && filledObject(x, y)) {
						g.setColor(fore);
						g.fillRect((x * _width) + offsetX + _offset.x, (y * _height)
								+ offsetY + _offset.y, _width, _height);
					}
				} else {
					if (conversions[x][y]) {
						g.fillRect((x * _width) + offsetX + _offset.x, (y * _height)
								+ offsetY + _offset.y, _width, _height);
					}
				}
			}
		}
		g.setColor(tmp);
	}

	@Override
	public void reset() {
		super.reset();
		this.completed = false;
		this.tmpflag = 0;
		if (ISprite.TYPE_FADE_OUT == type) {
			for (int x = 0; x < tileWidth; x++) {
				for (int y = 0; y < tileHeight; y++) {
					conversions[x][y] = false;
					temp[x][y] = false;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, tileWidth) - 1][MathUtils
						.random(1, tileHeight) - 1] = true;
			}
		} else {
			for (int x = 0; x < tileWidth; x++) {
				for (int y = 0; y < tileHeight; y++) {
					conversions[x][y] = true;
					temp[x][y] = true;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, tileWidth) - 1][MathUtils
						.random(1, tileHeight) - 1] = false;
			}
		}
	}

	public int getFadeType() {
		return type;
	}

	public int getCount() {
		return count;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
		conversions = null;
		temp = null;
	}

}
