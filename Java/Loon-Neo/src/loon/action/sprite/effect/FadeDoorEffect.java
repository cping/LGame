/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 单纯的瓦片开门关门效果合集类
 */
public class FadeDoorEffect extends BaseAbstractEffect {

	public final static int LEFT_RIGHT = 0;

	public final static int LEFT_CENTER_RIGHT = 1;

	public final static int TOP_BOTTOM = 2;

	public final static int TOP_CENTER_BOTTOM = 3;

	private final RectBox _door_left = new RectBox();

	private final RectBox _door_right = new RectBox();

	private final RectBox _door_top = new RectBox();

	private final RectBox _door_bottom = new RectBox();

	private final RectBox _door_center = new RectBox();

	private int _type;

	private int _dir;

	private int _step;

	private int _increase;

	private float _halfWidth;

	private float _halfHeight;

	private LTexture _replaceTexture;

	public FadeDoorEffect(int type, LColor color) {
		this(type, LEFT_RIGHT, color);
	}

	public FadeDoorEffect(int type, int dir, LColor color) {
		this(type, dir, color, (Image) null);
	}

	public FadeDoorEffect(int type, int dir, LColor color, Image img) {
		this(type, dir, color, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), img);
	}

	public FadeDoorEffect(int type, int dir, LColor color, String path) {
		this(type, dir, color, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(),
				StringUtils.isEmpty(path) ? null : BaseIO.loadImage(path));
	}

	public FadeDoorEffect(int type, int dir, LColor color, float w, float h) {
		this(type, dir, color, 0, 6, w, h, (Image) null);
	}

	public FadeDoorEffect(int type, int dir, LColor color, float w, float h, Image img) {
		this(type, dir, color, 0, 6, w, h, img);
	}

	public FadeDoorEffect(int type, int dir, LColor color, long delay, int i, float w, float h, String path) {
		this(type, dir, color, delay, i, w, h, StringUtils.isEmpty(path) ? null : BaseIO.loadImage(path));
	}

	public FadeDoorEffect(int type, int dir, LColor color, long delay, int i, float w, float h) {
		this(type, dir, color, delay, i, w, h, (Image) null);
	}

	public FadeDoorEffect(int type, int dir, LColor color, long delay, int i, float w, float h, Image img) {
		super();
		this.setColor(color);
		this.setDelay(delay);
		this.setSize(w, h);
		this.setIncrease(i);
		this.setRepaint(true);
		this.updateSize(type, dir, w, h);
		this.setReplaceImage(img);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		_step += _increase;
		switch (_dir) {
		case LEFT_RIGHT:
		case LEFT_CENTER_RIGHT:
			if (_step >= _halfWidth - _increase) {
				_completed = true;
			}
			break;
		case TOP_BOTTOM:
		case TOP_CENTER_BOTTOM:
			if (_step >= _halfHeight - _increase) {
				_completed = true;
			}
			break;
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		final float newX = drawX(offsetX);
		final float newY = drawY(offsetY);
		if (_type == TYPE_FADE_OUT && _completed) {
			if (_replaceTexture != null) {
				g.draw(_replaceTexture, newX, newY, _width, _height, _baseColor);
			} else {
				g.fillRect(newX, newY, _width, _height, _baseColor);
			}
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		if (_replaceTexture != null) {
			switch (_dir) {
			case LEFT_RIGHT:
				if (_type == TYPE_FADE_OUT) {
					g.draw(_replaceTexture, newX + _door_left.x + _step, newY + _door_left.y, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_right.x - _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _door_right.width, 0, _door_right.width, _door_right.height,
							_baseColor);
				} else {
					g.draw(_replaceTexture, newX + _door_left.x - _step, newY + _door_left.y, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_right.x + _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _door_right.width, 0, _door_right.width, _door_right.height,
							_baseColor);
				}
				break;
			case LEFT_CENTER_RIGHT:
				if (_type == TYPE_FADE_OUT) {
					g.draw(_replaceTexture, newX + _door_left.x + _step, newY + _door_left.y, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_center.x - _step, newY + _door_center.y, _door_center.width,
							_door_center.height, 0, _door_center.height, _door_center.width, _door_center.height,
							_baseColor);
					g.draw(_replaceTexture, newX + _door_right.x + _step, newY + _door_right.y, _door_right.width,
							_door_right.height, 0, _door_right.height * 2f, _door_right.width, _door_right.height,
							_baseColor);
				} else {
					g.draw(_replaceTexture, newX + _door_left.x - _step, newY + _door_left.y, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_center.x + _step, newY + _door_center.y, _door_center.width,
							_door_center.height, 0, _door_center.height, _door_center.width, _door_center.height,
							_baseColor);
					g.draw(_replaceTexture, newX + _door_right.x - _step, newY + _door_right.y, _door_right.width,
							_door_right.height, 0, _door_right.height * 2f, _door_right.width, _door_right.height,
							_baseColor);
				}
				break;
			case TOP_CENTER_BOTTOM:
				if (_type == TYPE_FADE_OUT) {
					g.draw(_replaceTexture, newX + _door_left.x, newY + _door_left.y + _step, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_center.x, newY + _door_center.y - _step, _door_center.width,
							_door_center.height, _door_center.width, 0, _door_center.width, _door_center.height,
							_baseColor);
					g.draw(_replaceTexture, newX + _door_right.x, newY + _door_right.y + _step, _door_right.width,
							_door_right.height, _door_right.width * 2, 0, _door_right.width, _door_right.height,
							_baseColor);
				} else {
					g.draw(_replaceTexture, newX + _door_left.x, newY + _door_left.y - _step, _door_left.width,
							_door_left.height, 0, 0, _door_left.width, _door_left.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_center.x, newY + _door_center.y + _step, _door_center.width,
							_door_center.height, _door_center.width, 0, _door_center.width, _door_center.height,
							_baseColor);
					g.draw(_replaceTexture, newX + _door_right.x, newY + _door_right.y - _step, _door_right.width,
							_door_right.height, _door_right.width * 2, 0, _door_right.width, _door_right.height,
							_baseColor);
				}
				break;
			case TOP_BOTTOM:
				if (_type == TYPE_FADE_OUT) {

					g.draw(_replaceTexture, newX + _door_top.x, newY + _door_top.y + _step, _door_top.width,
							_door_top.height, 0, 0, _width, _door_top.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_bottom.x, newY + _door_bottom.y - _step, _door_bottom.width,
							_door_bottom.height, 0, _door_bottom.height, _width, _door_bottom.height, _baseColor);
				} else {
					g.draw(_replaceTexture, newX + _door_top.x, newY + _door_top.y - _step, _door_top.width,
							_door_top.height, 0, 0, _width, _door_top.height, _baseColor);
					g.draw(_replaceTexture, newX + _door_bottom.x, newY + _door_bottom.y + _step, _door_bottom.width,
							_door_bottom.height, 0, _door_bottom.height, _width, _door_bottom.height, _baseColor);
				}
				break;
			}

		} else {
			switch (_dir) {
			case LEFT_RIGHT:
				if (_type == TYPE_FADE_OUT) {
					g.fillRect(newX + _door_left.x + _step, newY + _door_left.y, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_right.x - _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _baseColor);
				} else {
					g.fillRect(newX + _door_left.x - _step, newY + _door_left.y, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_right.x + _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _baseColor);
				}
				break;
			case LEFT_CENTER_RIGHT:
				if (_type == TYPE_FADE_OUT) {
					g.fillRect(newX + _door_left.x + _step, newY + _door_left.y, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_center.x - _step, newY + _door_center.y, _door_center.width,
							_door_center.height, _baseColor);
					g.fillRect(newX + _door_right.x + _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _baseColor);
				} else {
					g.fillRect(newX + _door_left.x - _step, newY + _door_left.y, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_center.x + _step, newY + _door_center.y, _door_center.width,
							_door_center.height, _baseColor);
					g.fillRect(newX + _door_right.x - _step, newY + _door_right.y, _door_right.width,
							_door_right.height, _baseColor);
				}
				break;
			case TOP_CENTER_BOTTOM:
				if (_type == TYPE_FADE_OUT) {
					g.fillRect(newX + _door_left.x, newY + _door_left.y + _step, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_center.x, newY + _door_center.y - _step, _door_center.width,
							_door_center.height, _baseColor);
					g.fillRect(newX + _door_right.x, newY + _door_right.y + _step, _door_right.width,
							_door_right.height, _baseColor);
				} else {
					g.fillRect(newX + _door_left.x, newY + _door_left.y - _step, _door_left.width, _door_left.height,
							_baseColor);
					g.fillRect(newX + _door_center.x, newY + _door_center.y + _step, _door_center.width,
							_door_center.height, _baseColor);
					g.fillRect(newX + _door_right.x, newY + _door_right.y - _step, _door_right.width,
							_door_right.height, _baseColor);
				}
				break;
			case TOP_BOTTOM:
				if (_type == TYPE_FADE_OUT) {
					g.fillRect(newX + _door_top.x, newY + _door_top.y + _step, _door_top.width, _door_top.height,
							_baseColor);
					g.fillRect(newX + _door_bottom.x, newY + _door_bottom.y - _step, _door_bottom.width,
							_door_bottom.height, _baseColor);
				} else {
					g.fillRect(newX + _door_top.x, newY + _door_top.y - _step, _door_top.width, _door_top.height,
							_baseColor);
					g.fillRect(newX + _door_bottom.x, newY + _door_bottom.y + _step, _door_bottom.width,
							_door_bottom.height, _baseColor);
				}
				break;
			}
		}

	}

	public void setIncrease(int i) {
		this._increase = LSystem.toIScaleFPS(i, 1);
	}

	public int getIncrease() {
		return this._increase;
	}

	private void updateSize(int type, int dir, float w, float h) {
		this._step = 0;
		this._type = type;
		this._dir = dir;
		if (_dir == LEFT_RIGHT || _dir == TOP_BOTTOM) {
			this._halfWidth = MathUtils.iceil(w / 2f) + 1f;
			this._halfHeight = MathUtils.iceil(h / 2f) + 1f;
		} else if (_dir == LEFT_CENTER_RIGHT) {
			this._halfWidth = MathUtils.iceil(w) + 1f;
			this._halfHeight = MathUtils.iceil(h / 3f) + 1f;
		} else if (_dir == TOP_CENTER_BOTTOM) {
			this._halfWidth = MathUtils.iceil(w / 3f) + 1f;
			this._halfHeight = MathUtils.iceil(h) + 1f;
		}
		switch (type) {
		case TYPE_FADE_OUT:
			if (_dir == LEFT_RIGHT) {
				_door_left.set(-_halfWidth, 0, _halfWidth, h);
				_door_right.set(w, 0, _halfWidth, h);
			} else if (_dir == TOP_BOTTOM) {
				_door_top.set(0, -_halfHeight, w, _halfHeight);
				_door_bottom.set(0, h, w, _halfHeight);
			} else if (_dir == LEFT_CENTER_RIGHT) {
				_door_left.set(-_halfWidth, 0, w, _halfHeight);
				_door_center.set(w, _halfHeight, w, _halfHeight);
				_door_right.set(-_halfWidth, _halfHeight * 2f, w, _halfHeight);
			} else if (_dir == TOP_CENTER_BOTTOM) {
				_door_left.set(0, -_halfHeight, _halfWidth, h);
				_door_center.set(_halfWidth, _halfHeight, _halfWidth, h);
				_door_right.set(_halfWidth * 2f, -_halfHeight, _halfWidth, h);
			}
			break;
		case TYPE_FADE_IN:
			if (_dir == LEFT_RIGHT) {
				_door_left.set(0, 0, _halfWidth, h);
				_door_right.set(_halfWidth, 0, _halfWidth, h);
			} else if (_dir == TOP_BOTTOM) {
				_door_top.set(0, 0, w, _halfHeight);
				_door_bottom.set(0, _halfHeight, w, _halfHeight);
			} else if (_dir == LEFT_CENTER_RIGHT) {
				_door_left.set(0, 0, w, _halfHeight);
				_door_center.set(0, _halfHeight, w, _halfHeight);
				_door_right.set(0, _halfHeight * 2f, w, _halfHeight);
			} else if (_dir == TOP_CENTER_BOTTOM) {
				_door_left.set(0, 0, _halfWidth, h);
				_door_center.set(_halfWidth, 0, _halfWidth, h);
				_door_right.set(_halfWidth * 2f, 0, _halfWidth, h);
			}
			break;
		}

	}

	public int getStep() {
		return this._step;
	}

	public LTexture getReplaceTexture() {
		return _replaceTexture;
	}

	public FadeDoorEffect setReplaceImage(String path) {
		return setReplaceImage(BaseIO.loadImage(path));
	}

	public FadeDoorEffect setReplaceImage(Image img) {
		if (img != null) {
			Image bitmap = null;
			if (MathUtils.equal(_width, img.getWidth()) && MathUtils.equal(_height, img.getHeight())) {
				bitmap = img;
			} else {
				bitmap = img.scale(_width, _height);
			}
			_replaceTexture = bitmap.onHaveToClose(true).texture();
			if (LColor.black.equals(_baseColor)) {
				setColor(LColor.white);
			}
		} else {
			_replaceTexture = null;
		}
		return this;
	}

	@Override
	public FadeDoorEffect setTexture(LTexture tex) {
		return setReplaceImage(tex.getImage());
	}

	@Override
	public FadeDoorEffect setTexture(String path) {
		return setReplaceImage(path);
	}

	@Override
	public FadeDoorEffect reset() {
		super.reset();
		updateSize(_type, _dir, getWidth(), getHeight());
		return this;
	}

	@Override
	public void close() {
		super.close();
		_door_left.clear();
		_door_right.clear();
		_door_top.clear();
		_door_bottom.clear();
		_door_center.clear();
		if (_replaceTexture != null) {
			_replaceTexture.close();
			_replaceTexture = null;
		}
	}

}
