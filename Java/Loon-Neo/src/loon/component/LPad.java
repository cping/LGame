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
import loon.LTexture.Format;
import loon.action.map.Config;
import loon.events.ActionKey;
import loon.events.SysTouch;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

/**
 * 游戏手柄用UI(Loon默认jar中有图,当然也可以自定义),支持上下左右四方向移动,八方向移动请使用LControl
 */
public class LPad extends LComponent {

	private ActionKey lockedKey;

	private boolean isLimitClick = false;

	private boolean isLeft, isRight, isUp, isDown, isClick;

	public static interface ClickListener {

		public void up();

		public void down();

		public void left();

		public void right();

		public void other();

	}

	public ClickListener listener;

	private int lastDir = -1;

	private float centerX, centerY;

	private float offsetX, offsetY;

	private int dotWidth, dotHeight;

	private int angle;

	private int baseWidth, baseHeight;

	private int backWidth, backHeight;

	private LTexturePack pack;

	private float scale_pad;

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
		this.offsetX = 6 * scale;
		this.offsetY = 6 * scale;
		this.pack = p;
		this.dotWidth = (int) (p.getEntry("dot").width() * scale);
		this.dotHeight = (int) (p.getEntry("dot").height() * scale);
		this.baseWidth = (int) (p.getEntry("fore").width() * scale);
		this.baseHeight = (int) (p.getEntry("fore").height() * scale);
		this.backWidth = (int) (p.getEntry("back").width() * scale);
		this.backHeight = (int) (p.getEntry("back").height() * scale);
		this.centerX = (baseWidth - dotWidth) / 2f + offsetX;
		this.centerY = (baseHeight - dotHeight) / 2f + offsetY;
		this.scale_pad = scale;
		this.lockedKey = new ActionKey();
		p.setFormat(Format.LINEAR);
	}

	public float getScale() {
		return scale_pad;
	}

	void freeClick() {
		if (isLimitClick) {
			lockedKey.release();
		}
		this.isLeft = false;
		this.isRight = false;
		this.isDown = false;
		this.isUp = false;
		this.isClick = false;
		if (listener != null) {
			listener.other();
		}
		this.lastDir = -1;
	}

	@Override
	protected void processTouchReleased() {
		freeClick();
		super.processTouchReleased();
	}

	@Override
	protected void processTouchPressed() {
		if (isLimitClick) {
			if (lockedKey.isPressed()) {
				return;
			}
			lockedKey.press();
		}
		final float x = MathUtils.bringToBounds(0, baseWidth, SysTouch.getX() - getScreenX()) / baseWidth - 0.5f;
		final float y = MathUtils.bringToBounds(0, baseHeight, SysTouch.getY() - getScreenY()) / baseHeight - 0.5f;
		if (x == 0 && y == 0) {
			return;
		}
		try {
			if (MathUtils.abs(x) > MathUtils.abs(y)) {
				if (x > 0) {
					this.isRight = true;
					this.isClick = true;
					this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 + dotWidth * 0.75f;
					this.centerY = offsetY + y + (baseHeight - dotHeight) / 2;
					if (listener != null) {
						listener.right();
					}
					this.lastDir = Config.TRIGHT;
				} else if (x < 0) {
					this.isLeft = true;
					this.isClick = true;
					this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - dotWidth * 0.75f;
					this.centerY = offsetY + y + (baseHeight - dotHeight) / 2;
					if (listener != null) {
						listener.left();
					}
					this.lastDir = Config.TLEFT;
				} else if (x == 0) {
					freeClick();
				}
			} else {
				if (y > 0) {
					this.isDown = true;
					this.isClick = true;
					this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
					this.centerY = offsetY + y + (baseHeight - dotHeight) / 2 + dotHeight * 0.75f;
					if (listener != null) {
						listener.down();
					}
					this.lastDir = Config.TDOWN;
				} else if (y < 0) {
					this.isUp = true;
					this.isClick = true;
					this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
					this.centerY = offsetY + y + (baseHeight - dotHeight) / 2 - dotHeight * 0.75f;
					if (listener != null) {
						listener.up();
					}
					this.lastDir = Config.TUP;
				} else if (y == 0) {
					freeClick();
				}
			}
		} catch (Throwable t) {
			LSystem.error("LPad click exception", t);
		}
		super.processTouchPressed();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (SysTouch.isUp()) {
			freeClick();
		}
		pack.glBegin();
		pack.draw(0, x, y, backWidth, backHeight, _component_baseColor);
		if (isClick) {
			if (angle < 360) {
				angle += 1;
			} else {
				angle = 0;
			}
			pack.draw(2, x + centerX, y + centerY, dotWidth, dotHeight, angle, _component_baseColor);
		}
		pack.draw(1, x + (backWidth - baseWidth) * 0.5f, y + (backHeight - baseHeight) * 0.5f, baseWidth, baseHeight,
				_component_baseColor);
		pack.glEnd();
	}

	public boolean isLastLeft() {
		return lastDir == Config.TLEFT;
	}

	public boolean isLastRight() {
		return lastDir == Config.TRIGHT;
	}

	public boolean isLastUp() {
		return lastDir == Config.TUP;
	}

	public boolean isLastDown() {
		return lastDir == Config.TDOWN;
	}

	public int getDirection() {
		return lastDir;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public boolean isRight() {
		return isRight;
	}

	public boolean isUp() {
		return isUp;
	}

	public boolean isDown() {
		return isDown;
	}

	public boolean isClick() {
		return isClick;
	}

	public ClickListener getListener() {
		return listener;
	}

	public LPad setListener(ClickListener l) {
		this.listener = l;
		return this;
	}

	public float getBoxOffsetX() {
		return offsetX;
	}

	public LPad setBoxOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getBoxOffsetY() {
		return offsetY;
	}

	public LPad setBoxOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public boolean isLimitClick() {
		return isLimitClick;
	}

	public LPad setLimitClick(boolean l) {
		this.isLimitClick = l;
		this.lockedKey.reset();
		return this;
	}
	
	@Override
	public String getUIName() {
		return "Pad";
	}

	@Override
	public void close() {
		super.close();
		if (pack != null) {
			pack.close();
		}
	}

}
