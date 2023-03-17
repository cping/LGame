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
import loon.events.SysTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 内置的滑杆控制器UI(LGame的jar中默认有图,当然也允许自定义)，用以操作八方向行走等
 */
public class LControl extends LComponent {

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

	public DigitalListener control;

	private static final float SIDE = 0.5f;

	private static final float DIAGONAL = 0.354f;

	private LTexture controlBase;

	private LTexture controlDot;

	private int baseWidth, baseHeight;

	private int dotWidth, dotHeight;

	private float centerX, centerY;

	private boolean allowDiagonal;

	private int lastDir = -1;

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
		this.controlBase = basefile;
		this.controlDot = dot;
		this.baseWidth = bw;
		this.baseHeight = bh;
		this.dotWidth = dw;
		this.dotHeight = dh;
		this.allowDiagonal = true;
		this.centerOffset();
		freeRes().add(basefile, dot);
	}

	public boolean isAllowDiagonal() {
		return this.allowDiagonal;
	}

	public void setAllowDiagonal(final boolean a) {
		this.allowDiagonal = a;
	}

	private void centerOffset() {
		this.centerX = (baseWidth - dotWidth) / 2f;
		this.centerY = (baseHeight - dotHeight) / 2f;
	}

	@Override
	public void processTouchPressed() {
		final float relativeX = MathUtils.bringToBounds(0, baseWidth, SysTouch.getX() - getScreenX()) / baseWidth
				- 0.5f;
		final float relativeY = MathUtils.bringToBounds(0, baseHeight, SysTouch.getY() - getScreenY()) / baseHeight
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
		this.centerX = dotWidth * 0.5f + x * baseWidth;
		this.centerY = dotHeight * 0.5f + y * baseHeight;
		try {
			if (control != null) {
				switch (direction) {
				case Config.TUP:
					control.up();
					break;
				case Config.UP:
					control.up45();
					break;
				case Config.TRIGHT:
					control.right();
					break;
				case Config.RIGHT:
					control.right45();
					break;
				case Config.TDOWN:
					control.down();
					break;
				case Config.DOWN:
					control.down45();
					break;
				case Config.TLEFT:
					control.left();
					break;
				case Config.LEFT:
					control.left45();
					break;
				default:
					break;
				}
				lastDir = direction;
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
		if (this.allowDiagonal) {
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
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		float alpha = g.alpha();
		g.setAlpha(0.5f);
		g.draw(controlBase, x, y, baseWidth, baseHeight);
		g.draw(controlDot, x + centerX, y + centerY, dotWidth, dotHeight);
		g.setAlpha(alpha);

	}

	public boolean isLastTLeft() {
		return lastDir == Config.TLEFT;
	}

	public boolean isLastTRight() {
		return lastDir == Config.TRIGHT;
	}

	public boolean isLastTUp() {
		return lastDir == Config.TUP;
	}

	public boolean isLastTDown() {
		return lastDir == Config.TDOWN;
	}

	public boolean isLastLeft() {
		return lastDir == Config.LEFT;
	}

	public boolean isLastRight() {
		return lastDir == Config.RIGHT;
	}

	public boolean isLastUp() {
		return lastDir == Config.UP;
	}

	public boolean isLastDown() {
		return lastDir == Config.DOWN;
	}

	public int getDirection() {
		return lastDir;
	}

	@Override
	public String getUIName() {
		return "Control";
	}

	public DigitalListener getDigitalListener() {
		return control;
	}

	public LControl setControl(DigitalListener c) {
		this.control = c;
		return this;
	}

	@Override
	public void destory() {

	}

}
