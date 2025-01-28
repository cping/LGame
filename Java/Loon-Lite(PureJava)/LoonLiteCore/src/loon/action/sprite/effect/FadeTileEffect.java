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
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 瓦片(向四周散开或向中心聚拢)淡入淡出效果
 *
 */
public class FadeTileEffect extends BaseAbstractEffect {

	private float sizeWidth;

	private float sizeHeight;

	private int tileWidth, tileHeight;

	private int count;

	private int speed = 1;

	private int tmpflag = 0;

	private boolean[][] conversions;
	private boolean[][] boolTemps;

	private boolean usefore = false;

	private LColor fore = LColor.white;

	private int _type;

	public FadeTileEffect(int type, LColor c) {
		this(type, 1, 1, c, LColor.white);
	}

	public FadeTileEffect(int type) {
		this(type, 1, 1, LColor.black, LColor.white);
	}

	public FadeTileEffect(int type, int count, int speed, LColor back, LColor fore) {
		this(type, count, speed, back, fore, LSystem.viewSize.getTileWidthSize(), LSystem.viewSize.getTileHeightSize());
	}

	public FadeTileEffect(int type, int count, int speed, LColor back, LColor fore, float w, float h) {
		this(type, count, speed, back, fore, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), w, h);
	}

	public FadeTileEffect(int type, int count, int speed, LColor back, LColor fore, float maxWidth, float maxHeight,
			float w, float h) {
		this(type, count, speed, 60, back, fore, maxWidth, maxHeight, w, h);
	}

	public FadeTileEffect(int type, int count, int speed, long delay, LColor back, LColor fore, float maxWidth,
			float maxHeight, float w, float h) {
		this.setRepaint(true);
		this.setSize(maxWidth, maxHeight);
		this.setDelay(delay);
		this.setColor(back);
		this._type = type;
		this.count = count;
		this.speed = speed;
		this.sizeWidth = w;
		this.sizeHeight = h;
		this.tileWidth = MathUtils.ifloor(((maxWidth / w)) + 1);
		this.tileHeight = MathUtils.ifloor(((maxHeight / h)) + 1);
		this.conversions = new boolean[tileWidth][tileHeight];
		this.boolTemps = new boolean[tileWidth][tileHeight];
		this.fore = fore;
		this.pack();
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

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			int count = 0;
			if (ISprite.TYPE_FADE_OUT == _type) {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							boolTemps[x][y] = false;
						}
					}
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							if (!boolTemps[x][y] && conversions[x][y]) {
								boolTemps[x][y] = true;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x - 1][y]) {
										conversions[x - 1][y] = true;
										boolTemps[x - 1][y] = true;
									}
								}
								if (x < tileWidth - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x + 1][y]) {
										conversions[x + 1][y] = true;
										boolTemps[x + 1][y] = true;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y - 1]) {
										conversions[x][y - 1] = true;
										boolTemps[x][y - 1] = true;
									}
								}
								if (y < tileHeight - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y + 1]) {
										conversions[x][y + 1] = true;
										boolTemps[x][y + 1] = true;
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
					_completed = true;
				}
			} else {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							boolTemps[x][y] = true;
						}
					}
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							if (boolTemps[x][y] && !conversions[x][y]) {
								boolTemps[x][y] = false;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x - 1][y]) {
										conversions[x - 1][y] = false;
										boolTemps[x - 1][y] = false;
									}
								}
								if (x < tileWidth - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x + 1][y]) {
										conversions[x + 1][y] = false;
										boolTemps[x + 1][y] = false;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y - 1]) {
										conversions[x][y - 1] = false;
										boolTemps[x][y - 1] = false;
									}
								}
								if (y < tileHeight - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y + 1]) {
										conversions[x][y + 1] = false;
										boolTemps[x][y + 1] = false;
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
					_completed = true;
				}
				if (count >= tileWidth) {
					tmpflag++;
				}

			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		final int old = g.color();
		for (int x = 0; x < tileWidth; x++) {
			for (int y = 0; y < tileHeight; y++) {
				final float offX = x * sizeWidth + offsetX;
				final float offY = y * sizeHeight + offsetY;
				if (usefore) {
					if (conversions[x][y]) {
						g.fillRect(drawX(offX), drawY(offY), sizeWidth, sizeHeight, _baseColor);
					} else if (!conversions[x][y] && filledObject(x, y)) {
						g.fillRect(drawX(offX), drawY(offY), sizeWidth, sizeHeight, fore);
					}
				} else {
					if (conversions[x][y]) {
						g.fillRect(drawX(offX), drawY(offY), sizeWidth, sizeHeight, _baseColor);
					}
				}
			}
		}
		g.setTint(old);
	}

	public FadeTileEffect pack() {
		this.tmpflag = 0;
		if (ISprite.TYPE_FADE_OUT == _type) {
			for (int x = 0; x < tileWidth; x++) {
				for (int y = 0; y < tileHeight; y++) {
					conversions[x][y] = false;
					boolTemps[x][y] = false;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, tileWidth) - 1][MathUtils.random(1, tileHeight) - 1] = true;
			}
		} else {
			for (int x = 0; x < tileWidth; x++) {
				for (int y = 0; y < tileHeight; y++) {
					conversions[x][y] = true;
					boolTemps[x][y] = true;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, tileWidth) - 1][MathUtils.random(1, tileHeight) - 1] = false;
			}
		}
		return this;
	}

	@Override
	public FadeTileEffect reset() {
		super.reset();
		this.pack();
		return this;
	}

	public int getFadeType() {
		return _type;
	}

	public int getCount() {
		return count;
	}

	@Override
	public FadeTileEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	public void _onDestroy() {
		super._onDestroy();
		conversions = null;
		boolTemps = null;
	}

}
