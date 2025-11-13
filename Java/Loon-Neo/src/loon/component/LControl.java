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
import loon.LTexture;
import loon.action.collision.CollisionHelper;
import loon.action.map.Config;
import loon.component.skin.ControlSkin;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 内置的滑杆控制器UI(LGame的jar中默认有图,当然也允许自定义)，用以操作八方向行走等
 */
public class LControl extends LComponent {

	private static final float SIDE = 0.5f;

	private static final float DIAGONAL = 0.354f;

	public static interface DigitalListener {

		public void up();

		public void down();

		public void left();

		public void right();

		public void up45();

		public void down45();

		public void left45();

		public void right45();

	}

	public DigitalListener _control;

	private LTexture _controlBase;

	private LTexture _controlDot;

	private int _baseWidth, _baseHeight;

	private int _dotWidth, _dotHeight;

	private float _centerX, _centerY;

	private boolean _allowDiagonal;

	private int _lastDir = -1;

	public LControl(int x, int y) {
		this(x, y, 128, 128, 64, 64);
	}

	public LControl(int x, int y, int bw, int bh, int dw, int dh) {
		this(x, y, LSystem.loadTexture(LSystem.getSystemImagePath() + "control_base.png"),
				LSystem.loadTexture(LSystem.getSystemImagePath() + "control_dot.png"), bw, bh, dw, dh);
	}

	public LControl(int x, int y, String basename, String dot, int bw, int bh, int dw, int dh) {
		this(x, y, LSystem.loadTexture(basename), LSystem.loadTexture(dot), bw, bh, dw, dh);
	}

	public LControl(ControlSkin skin, int x, int y, int bw, int bh, int dw, int dh) {
		this(x, y, skin.getControlBaseTexture(), skin.getControlDotTexture(), bw, bh, dw, dh);
	}

	public LControl(int x, int y, LTexture basefile, LTexture dot, int bw, int bh, int dw, int dh) {
		super(x, y, bw, bh);
		this._controlBase = basefile;
		this._controlDot = dot;
		this._baseWidth = bw;
		this._baseHeight = bh;
		this._dotWidth = dw;
		this._dotHeight = dh;
		this._allowDiagonal = true;
		this.centerOffset();
		freeRes().add(basefile, dot);
	}

	public boolean isAllowDiagonal() {
		return this._allowDiagonal;
	}

	public void setAllowDiagonal(final boolean a) {
		this._allowDiagonal = a;
	}

	private void centerOffset() {
		this._centerX = (_baseWidth - _dotWidth) / 2f;
		this._centerY = (_baseHeight - _dotHeight) / 2f;
	}

	@Override
	public void processTouchPressed() {
		final float relativeX = MathUtils.bringToBounds(0, _baseWidth, getTouchX() - getScreenX()) / _baseWidth - 0.5f;
		final float relativeY = MathUtils.bringToBounds(0, _baseHeight, getTouchY() - getScreenY()) / _baseHeight
				- 0.5f;
		onUpdateControlDot(relativeX, relativeY);
		super.processTouchPressed();
	}

	@Override
	public void processTouchReleased() {
		centerOffset();
		super.processTouchReleased();
	}

	private void position(final float x, final float y, final int direction) {
		this._centerX = _dotWidth * 0.5f + x * _baseWidth;
		this._centerY = _dotHeight * 0.5f + y * _baseHeight;
		try {
			if (_control != null) {
				switch (direction) {
				case Config.TUP:
					_control.up();
					break;
				case Config.UP:
					_control.up45();
					break;
				case Config.TRIGHT:
					_control.right();
					break;
				case Config.RIGHT:
					_control.right45();
					break;
				case Config.TDOWN:
					_control.down();
					break;
				case Config.DOWN:
					_control.down45();
					break;
				case Config.TLEFT:
					_control.left();
					break;
				case Config.LEFT:
					_control.left45();
					break;
				default:
					break;
				}
				_lastDir = direction;
			}
		} catch (Throwable t) {
			LSystem.error("LControl click exception", t);
		}
	}

	private void onUpdateControlDot(final float x, final float y) {
		if (x == 0 && y == 0) {
			position(0, 0, Config.EMPTY);
			return;
		}
		if (this._allowDiagonal) {
			final float angle = MathUtils.toDegrees(MathUtils.atan2(x, y)) + 180;
			if (CollisionHelper.checkAngle(0, angle) || CollisionHelper.checkAngle(360, angle)) {
				position(0, -SIDE, Config.TUP);
			} else if (CollisionHelper.checkAngle(45, angle)) {
				position(-DIAGONAL, -DIAGONAL, Config.LEFT);
			} else if (CollisionHelper.checkAngle(90, angle)) {
				position(-SIDE, 0, Config.TLEFT);
			} else if (CollisionHelper.checkAngle(135, angle)) {
				position(-DIAGONAL, DIAGONAL, Config.DOWN);
			} else if (CollisionHelper.checkAngle(180, angle)) {
				position(0, SIDE, Config.TDOWN);
			} else if (CollisionHelper.checkAngle(225, angle)) {
				position(DIAGONAL, DIAGONAL, Config.RIGHT);
			} else if (CollisionHelper.checkAngle(270, angle)) {
				position(SIDE, 0, Config.TRIGHT);
			} else if (CollisionHelper.checkAngle(315, angle)) {
				position(DIAGONAL, -DIAGONAL, Config.UP);
			} else {
				position(0, 0, Config.EMPTY);
			}
		} else {
			if (MathUtils.abs(x) > MathUtils.abs(y)) {
				if (x > 0) {
					position(SIDE, 0, Config.RIGHT);
				} else if (x < 0) {
					position(-SIDE, 0, Config.LEFT);
				} else if (x == 0) {
					position(0, 0, Config.EMPTY);
				}
			} else {
				if (y > 0) {
					position(0, SIDE, Config.DOWN);
				} else if (y < 0) {
					position(0, -SIDE, Config.UP);
				} else if (y == 0) {
					position(0, 0, Config.EMPTY);
				}
			}
		}

	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (!isVisible()) {
			return;
		}
		final float alpha = g.alpha();
		g.setAlpha(0.5f);
		g.draw(_controlBase, x, y, _baseWidth, _baseHeight);
		g.draw(_controlDot, x + _centerX, y + _centerY, _dotWidth, _dotHeight);
		g.setAlpha(alpha);

	}

	public boolean isLastTLeft() {
		return _lastDir == Config.TLEFT;
	}

	public boolean isLastTRight() {
		return _lastDir == Config.TRIGHT;
	}

	public boolean isLastTUp() {
		return _lastDir == Config.TUP;
	}

	public boolean isLastTDown() {
		return _lastDir == Config.TDOWN;
	}

	public boolean isLastLeft() {
		return _lastDir == Config.LEFT;
	}

	public boolean isLastRight() {
		return _lastDir == Config.RIGHT;
	}

	public boolean isLastUp() {
		return _lastDir == Config.UP;
	}

	public boolean isLastDown() {
		return _lastDir == Config.DOWN;
	}

	public int getDirection() {
		return _lastDir;
	}

	public DigitalListener getDigitalListener() {
		return _control;
	}

	public LControl setControl(DigitalListener c) {
		this._control = c;
		return this;
	}

	@Override
	public String getUIName() {
		return "Control";
	}

	@Override
	public void destory() {

	}

}
