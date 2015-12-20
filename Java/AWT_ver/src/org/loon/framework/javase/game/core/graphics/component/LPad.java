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
package org.loon.framework.javase.game.core.graphics.component;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LComponent;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.utils.GraphicsUtils;

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

	private BufferedImage back;

	private BufferedImage fore;

	private BufferedImage dot;

	private float scale_pad;

	public LPad(int x, int y) {
		this(x, y, 1.2f);
	}

	public LPad(int x, int y, float scale) {
		this(x, y, GraphicsUtils.loadBufferedImage(LSystem.FRAMEWORK_IMG_NAME
				+ "pad_ui_back.png"), GraphicsUtils
				.loadBufferedImage(LSystem.FRAMEWORK_IMG_NAME
						+ "pad_ui_fore.png"), GraphicsUtils
				.loadBufferedImage(LSystem.FRAMEWORK_IMG_NAME
						+ "pad_ui_dot.png"), scale);
	}

	public LPad(int x, int y, BufferedImage b, BufferedImage f,
			BufferedImage d, float scale) {
		super(x, y, (int) (f.getWidth() * scale), (int) (f.getHeight() * scale));
		this.offsetX = 6 * scale;
		this.offsetY = 6 * scale;
		this.fore = f;
		this.back = b;
		this.dot = d;
		this.dotWidth = (int) (d.getWidth() * scale);
		this.dotHeight = (int) (d.getHeight() * scale);
		this.baseWidth = (int) (f.getWidth() * scale);
		this.baseHeight = (int) (f.getHeight() * scale);
		this.backWidth = (int) (b.getWidth() * scale);
		this.backHeight = (int) (b.getHeight() * scale);
		this.centerX = (baseWidth - dotWidth) / 2 + offsetX;
		this.centerY = (baseHeight - dotHeight) / 2 + offsetY;
		this.scale_pad = scale;
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

	protected void processTouchReleased() {
		freeClick();
	}

	static final float bringToBounds(final float minValue,
			final float maxValue, final float v) {
		return Math.max(minValue, Math.min(maxValue, v));
	}

	protected void processTouchPressed() {
		final float x = bringToBounds(0, baseWidth, input.getTouchX()
				- getScreenX())
				/ baseWidth - 0.5f;
		final float y = bringToBounds(0, baseHeight, input.getTouchY()
				- getScreenY())
				/ baseHeight - 0.5f;
		if (x == 0 && y == 0) {
			return;
		}
		if (Math.abs(x) > Math.abs(y)) {
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

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {
		if (input.isTouchClickUp()) {
			freeClick();
		}
		RenderingHints old = g.getRenderingHints();
		GraphicsUtils.setExcellentRenderingHints(g);
		g.drawImage(back, x, y, backWidth, backHeight);
		if (isClick) {
			if (angle < 360) {
				angle += 1;
			} else {
				angle = 0;
			}
			g.drawImage(dot, x + centerX, y + centerY, dotWidth, dotHeight,
					angle);
		}
		g.drawImage(fore, (int) (x + (backWidth - baseWidth) * 0.5f),
				(int) (y + (backHeight - baseHeight) * 0.5f), baseWidth,
				baseHeight);
		g.setRenderingHints(old);
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

	public String getUIName() {
		return "Pad";
	}

	public void dispose() {
		if (back != null) {
			back.flush();
			back = null;
		}
		if (fore != null) {
			fore.flush();
			fore = null;
		}
		if (dot != null) {
			dot.flush();
			dot = null;
		}
	}

}
