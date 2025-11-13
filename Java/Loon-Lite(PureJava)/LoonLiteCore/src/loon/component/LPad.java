/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.component;

import loon.LSystem;
import loon.action.map.Config;
import loon.events.ActionKey;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

/**
 * 游戏手柄用UI(Loon默认jar中有图,当然也可以自定义),支持上下左右四方向移动,八方向移动请使用LControl
 */
public class LPad extends LComponent {

	private ActionKey _lockedKey;

	private boolean _limitClick = false;

	private boolean _isLeft, _isRight, _isUp, _isDown, _isClick;

	public static interface ClickListener {

		public void up();

		public void down();

		public void left();

		public void right();

		public void other();

	}

	public ClickListener _listener;

	private int _lastDir = -1;

	private float _centerX, _centerY;

	private float _offsetX, _offsetY;

	private int _dotWidth, _dotHeight;

	private int _angle;

	private int _baseWidth, _baseHeight;

	private int _backWidth, _backHeight;

	private LTexturePack _pack;

	private float _scale_pad;

	public LPad(int x, int y) {
		this(x, y, 1.2f);
	}

	public LPad(int x, int y, float scale) {
		this(x, y, LSystem.getSystemImagePath() + "pad_ui.txt", scale);
	}

	public LPad(int x, int y, String config, float scale) {
		this(x, y, new LTexturePack(config), scale);
	}

	public LPad(int x, int y, LTexturePack p, float scale) {
		super(x, y, (int) (p.getEntry("fore").width() * scale), (int) (p.getEntry("fore").height() * scale));
		this._offsetX = 6 * scale;
		this._offsetY = 6 * scale;
		this._pack = p;
		this._dotWidth = (int) (p.getEntry("dot").width() * scale);
		this._dotHeight = (int) (p.getEntry("dot").height() * scale);
		this._baseWidth = (int) (p.getEntry("fore").width() * scale);
		this._baseHeight = (int) (p.getEntry("fore").height() * scale);
		this._backWidth = (int) (p.getEntry("back").width() * scale);
		this._backHeight = (int) (p.getEntry("back").height() * scale);
		this._centerX = (_baseWidth - _dotWidth) / 2f + _offsetX;
		this._centerY = (_baseHeight - _dotHeight) / 2f + _offsetY;
		this._scale_pad = scale;
		this._lockedKey = new ActionKey();
	}

	public float getScale() {
		return _scale_pad;
	}

	@Override
	protected void processTouchReleased() {
		freeClick();
		super.processTouchReleased();
	}

	@Override
	protected void processTouchDragged() {
		clickedPad();
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		clickedPad();
		super.processTouchPressed();
	}

	private void clickedPad() {
		if (_input == null) {
			return;
		}
		if (_limitClick) {
			if (_lockedKey.isPressed()) {
				return;
			}
			_lockedKey.press();
		}
		final float x = MathUtils.bringToBounds(0, _baseWidth, getTouchX() - getScreenX()) / _baseWidth - 0.5f;
		final float y = MathUtils.bringToBounds(0, _baseHeight, getTouchY() - getScreenY()) / _baseHeight - 0.5f;
		if (x == 0 && y == 0) {
			return;
		}
		try {
			cancel();
			if (MathUtils.abs(x) > MathUtils.abs(y)) {
				if (x > 0) {
					this._isRight = true;
					this._isClick = true;
					this._centerX = _offsetX + x + (_baseWidth - _dotWidth) / 2 + _dotWidth * 0.75f;
					this._centerY = _offsetY + y + (_baseHeight - _dotHeight) / 2;
					if (_listener != null) {
						_listener.right();
					}
					this._lastDir = Config.TRIGHT;
				} else if (x < 0) {
					this._isLeft = true;
					this._isClick = true;
					this._centerX = _offsetX + x + (_baseWidth - _dotWidth) / 2 - _dotWidth * 0.75f;
					this._centerY = _offsetY + y + (_baseHeight - _dotHeight) / 2;
					if (_listener != null) {
						_listener.left();
					}
					this._lastDir = Config.TLEFT;
				} else if (x == 0) {
					freeClick();
				}
			} else {
				if (y > 0) {
					this._isDown = true;
					this._isClick = true;
					this._centerX = _offsetX + x + (_baseWidth - _dotWidth) / 2 - 1;
					this._centerY = _offsetY + y + (_baseHeight - _dotHeight) / 2 + _dotHeight * 0.75f;
					if (_listener != null) {
						_listener.down();
					}
					this._lastDir = Config.TDOWN;
				} else if (y < 0) {
					this._isUp = true;
					this._isClick = true;
					this._centerX = _offsetX + x + (_baseWidth - _dotWidth) / 2 - 1;
					this._centerY = _offsetY + y + (_baseHeight - _dotHeight) / 2 - _dotHeight * 0.75f;
					if (_listener != null) {
						_listener.up();
					}
					this._lastDir = Config.TUP;
				} else if (y == 0) {
					freeClick();
				}
			}
		} catch (Throwable t) {
			LSystem.error("LPad click exception", t);
		}
	}

	public LPad cancel() {
		if (_listener != null) {
			_listener.other();
		}
		this._isLeft = false;
		this._isRight = false;
		this._isDown = false;
		this._isUp = false;
		this._isClick = false;
		return this;
	}

	private void freeClick() {
		if (_limitClick) {
			_lockedKey.release();
		}
		this.cancel();
		this._lastDir = -1;
	}

	@Override
	public void process(long elapsedTime) {
		if (isPadUp()) {
			freeClick();
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		_pack.initGL(g);
		_pack.draw(0, x, y, _backWidth, _backHeight, _component_baseColor);
		if (isPadDown()) {
			if (_angle < 360) {
				_angle += 1;
			} else {
				_angle = 0;
			}
			_pack.draw(2, x + _centerX, y + _centerY, _dotWidth, _dotHeight, _angle, _component_baseColor);
		}
		_pack.draw(1, x + (_backWidth - _baseWidth) * 0.5f, y + (_backHeight - _baseHeight) * 0.5f, _baseWidth,
				_baseHeight, _component_baseColor);
	}

	public boolean isLastLeft() {
		return _lastDir == Config.TLEFT;
	}

	public boolean isLastRight() {
		return _lastDir == Config.TRIGHT;
	}

	public boolean isLastUp() {
		return _lastDir == Config.TUP;
	}

	public boolean isLastDown() {
		return _lastDir == Config.TDOWN;
	}

	public int getDirection() {
		return _lastDir;
	}

	public boolean isLeft() {
		return _isLeft;
	}

	public boolean isRight() {
		return _isRight;
	}

	public boolean isUp() {
		return _isUp;
	}

	public boolean isDown() {
		return _isDown;
	}

	public boolean isPadDown() {
		return _isClick && isTouchDownClick() && isPointInUI();
	}

	public boolean isPadUp() {
		return (_isClick && isClickUp());
	}

	public ClickListener getListener() {
		return _listener;
	}

	public LPad setListener(ClickListener l) {
		this._listener = l;
		return this;
	}

	public float getBoxOffsetX() {
		return _offsetX;
	}

	public LPad setBoxOffsetX(float offsetX) {
		this._offsetX = offsetX;
		return this;
	}

	public float getBoxOffsetY() {
		return _offsetY;
	}

	public LPad setBoxOffsetY(float offsetY) {
		this._offsetY = offsetY;
		return this;
	}

	public boolean isLimitClick() {
		return _limitClick;
	}

	public LPad setLimitClick(boolean l) {
		this._limitClick = l;
		this._lockedKey.reset();
		return this;
	}

	@Override
	public String getUIName() {
		return "Pad";
	}

	@Override
	public void destory() {
		if (_pack != null) {
			_pack.close();
		}
	}

}
