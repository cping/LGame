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
package loon.core.graphics.component;

import loon.core.LSystem;
import loon.core.graphics.LComponent;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LInputFactory.Touch;
import loon.utils.MathUtils;


public class LPad extends LComponent {

	private boolean isLeft, isRight, isUp, isDown, isClick;

	public static interface ClickListener {

		public void up();

		public void down();

		public void left();

		public void right();

		public void other();

	}

	public ClickListener listener;

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
		this(x, y, LSystem.FRAMEWORK_IMG_NAME + "pad_ui.xml", scale);
	}

	public LPad(int x, int y, String config, float scale) {
		this(x, y, new LTexturePack(config), scale);
	}

	public LPad(int x, int y, LTexturePack p, float scale) {
		super(x, y, (int) (p.getEntry("fore").width() * scale), (int) (p
				.getEntry("fore").height() * scale));
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
		p.setFormat(Format.LINEAR);
	}

	public float getScale() {
		return scale_pad;
	}

	void freeClick() {
		this.isLeft = false;
		this.isRight = false;
		this.isDown = false;
		this.isUp = false;
		this.isClick = false;
		if (listener != null) {
			listener.other();
		}
	}

	@Override
	protected void processTouchReleased() {
		freeClick();
	}

	@Override
	protected void processTouchPressed() {
		final float x = MathUtils.bringToBounds(0, baseWidth, Touch.getX()
				- getScreenX())
				/ baseWidth - 0.5f;
		final float y = MathUtils.bringToBounds(0, baseHeight, Touch.getY()
				- getScreenY())
				/ baseHeight - 0.5f;
		if (x == 0 && y == 0) {
			return;
		}
		if (MathUtils.abs(x) > MathUtils.abs(y)) {
			if (x > 0) {
				this.isRight = true;
				this.isClick = true;
				this.centerX = offsetX + x + (baseWidth - dotWidth) / 2
						+ dotWidth * 0.75f;
				this.centerY = offsetY + y + (baseHeight - dotHeight) / 2;
				if (listener != null) {
					listener.right();
				}
			} else if (x < 0) {
				this.isLeft = true;
				this.isClick = true;
				this.centerX = offsetX + x + (baseWidth - dotWidth) / 2
						- dotWidth * 0.75f;
				this.centerY = offsetY + y + (baseHeight - dotHeight) / 2;
				if (listener != null) {
					listener.left();
				}
			} else if (x == 0) {
				freeClick();
			}
		} else {
			if (y > 0) {
				this.isDown = true;
				this.isClick = true;
				this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
				this.centerY = offsetY + y + (baseHeight - dotHeight) / 2
						+ dotHeight * 0.75f;
				if (listener != null) {
					listener.down();
				}
			} else if (y < 0) {
				this.isUp = true;
				this.isClick = true;
				this.centerX = offsetX + x + (baseWidth - dotWidth) / 2 - 1;
				this.centerY = offsetY + y + (baseHeight - dotHeight) / 2
						- dotHeight * 0.75f;
				if (listener != null) {
					listener.up();
				}
			} else if (y == 0) {
				freeClick();
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (Touch.isUp()) {
			freeClick();
		}
		pack.glBegin();
		pack.draw(0, x, y, backWidth, backHeight);
		if (isClick) {
			if (angle < 360) {
				angle += 1;
			} else {
				angle = 0;
			}
			pack.draw(2, x + centerX, y + centerY, dotWidth, dotHeight,
					angle, null);
		}
		pack.draw(1, x + (backWidth - baseWidth) * 0.5f, y
				+ (backHeight - baseHeight) * 0.5f, baseWidth, baseHeight);
		pack.glEnd();
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

	public void setListener(ClickListener listener) {
		this.listener = listener;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	@Override
	public String getUIName() {
		return "Pad";
	}

	@Override
	public void dispose() {
		if (pack != null) {
			pack.dispose();
		}
	}

}
