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

	private float _sizeWidth;

	private float _sizeHeight;

	private int _tileWidth, _tileHeight;

	private int _count;

	private int _speed = 1;

	private int _tmpflag = 0;

	private boolean[][] _conversions;
	private boolean[][] _boolTemps;

	private boolean _usefore = false;

	private LColor _fore = LColor.white;

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
		this._count = count;
		this._speed = speed;
		this._sizeWidth = w;
		this._sizeHeight = h;
		this._tileWidth = MathUtils.ifloor(((maxWidth / w)) + 1);
		this._tileHeight = MathUtils.ifloor(((maxHeight / h)) + 1);
		this._conversions = new boolean[_tileWidth][_tileHeight];
		this._boolTemps = new boolean[_tileWidth][_tileHeight];
		this._fore = fore;
		this.pack();
	}

	private boolean filledObject(int x, int y) {
		if (x > 0) {
			if (_conversions[x - 1][y]) {
				return true;
			}
		} else if (x < _tileWidth - 1) {
			if (_conversions[x + 1][y]) {
				return true;
			}
		} else if (y > 0) {
			if (_conversions[x][y - 1]) {
				return true;
			}
		} else if (y < _tileHeight - 1) {
			if (_conversions[x][y + 1]) {
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
				for (int i = 0; i < _speed; i++) {
					for (int x = 0; x < _tileWidth; x++) {
						for (int y = 0; y < _tileHeight; y++) {
							_boolTemps[x][y] = false;
						}
					}
					for (int x = 0; x < _tileWidth; x++) {
						for (int y = 0; y < _tileHeight; y++) {
							if (!_boolTemps[x][y] && _conversions[x][y]) {
								_boolTemps[x][y] = true;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!_conversions[x - 1][y]) {
										_conversions[x - 1][y] = true;
										_boolTemps[x - 1][y] = true;
									}
								}
								if (x < _tileWidth - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (!_conversions[x + 1][y]) {
										_conversions[x + 1][y] = true;
										_boolTemps[x + 1][y] = true;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!_conversions[x][y - 1]) {
										_conversions[x][y - 1] = true;
										_boolTemps[x][y - 1] = true;
									}
								}
								if (y < _tileHeight - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (!_conversions[x][y + 1]) {
										_conversions[x][y + 1] = true;
										_boolTemps[x][y + 1] = true;
									}
								}

							}
						}
					}
				}

				for (int x = 0; x < _tileWidth; x++) {
					for (int y = 0; y < _tileHeight; y++) {
						if (!_conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (count == 0) {
					_completed = true;
				}
			} else {
				for (int i = 0; i < _speed; i++) {
					for (int x = 0; x < _tileWidth; x++) {
						for (int y = 0; y < _tileHeight; y++) {
							_boolTemps[x][y] = true;
						}
					}
					for (int x = 0; x < _tileWidth; x++) {
						for (int y = 0; y < _tileHeight; y++) {
							if (_boolTemps[x][y] && !_conversions[x][y]) {
								_boolTemps[x][y] = false;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (_conversions[x - 1][y]) {
										_conversions[x - 1][y] = false;
										_boolTemps[x - 1][y] = false;
									}
								}
								if (x < _tileWidth - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (_conversions[x + 1][y]) {
										_conversions[x + 1][y] = false;
										_boolTemps[x + 1][y] = false;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (_conversions[x][y - 1]) {
										_conversions[x][y - 1] = false;
										_boolTemps[x][y - 1] = false;
									}
								}
								if (y < _tileHeight - 1 && !(MathUtils.random(1, 2) == 1)) {
									if (_conversions[x][y + 1]) {
										_conversions[x][y + 1] = false;
										_boolTemps[x][y + 1] = false;
									}
								}

							}
						}
					}
				}
				for (int x = 0; x < _tileWidth; x++) {
					for (int y = 0; y < _tileHeight; y++) {
						if (!_conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (_tmpflag >= _tileHeight) {
					_completed = true;
				}
				if (count >= _tileWidth) {
					_tmpflag++;
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
		for (int x = 0; x < _tileWidth; x++) {
			for (int y = 0; y < _tileHeight; y++) {
				final float offX = x * _sizeWidth + offsetX;
				final float offY = y * _sizeHeight + offsetY;
				if (_usefore) {
					if (_conversions[x][y]) {
						g.fillRect(drawX(offX), drawY(offY), _sizeWidth, _sizeHeight, _baseColor);
					} else if (!_conversions[x][y] && filledObject(x, y)) {
						g.fillRect(drawX(offX), drawY(offY), _sizeWidth, _sizeHeight, _fore);
					}
				} else {
					if (_conversions[x][y]) {
						g.fillRect(drawX(offX), drawY(offY), _sizeWidth, _sizeHeight, _baseColor);
					}
				}
			}
		}
		g.setTint(old);
	}

	public FadeTileEffect pack() {
		this._tmpflag = 0;
		if (ISprite.TYPE_FADE_OUT == _type) {
			for (int x = 0; x < _tileWidth; x++) {
				for (int y = 0; y < _tileHeight; y++) {
					_conversions[x][y] = false;
					_boolTemps[x][y] = false;
				}
			}
			for (int i = 0; i < _count; i++) {
				_conversions[MathUtils.random(1, _tileWidth) - 1][MathUtils.random(1, _tileHeight) - 1] = true;
			}
		} else {
			for (int x = 0; x < _tileWidth; x++) {
				for (int y = 0; y < _tileHeight; y++) {
					_conversions[x][y] = true;
					_boolTemps[x][y] = true;
				}
			}
			for (int i = 0; i < _count; i++) {
				_conversions[MathUtils.random(1, _tileWidth) - 1][MathUtils.random(1, _tileHeight) - 1] = false;
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
		return _count;
	}

	@Override
	public FadeTileEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		_conversions = null;
		_boolTemps = null;
	}

}
