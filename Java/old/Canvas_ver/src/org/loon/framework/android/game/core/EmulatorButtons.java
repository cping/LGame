package org.loon.framework.android.game.core;

import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.GraphicsUtils;
import org.loon.framework.android.game.utils.MultitouchUtils;
import org.loon.framework.android.game.core.EmulatorButton;
import org.loon.framework.android.game.core.EmulatorListener;
import org.loon.framework.android.game.core.LSystem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class EmulatorButtons {

	private final Bitmap dpad, buttons;

	private EmulatorButton up, left, right, down;

	private EmulatorButton triangle, square, circle, cancel;

	private EmulatorListener emulatorListener;

	private int offsetX, offsetY, width, height;

	private final int offset = 10;

	private float alpha;

	private float DEFAULT_ALPHA = 0.5F;

	protected static Paint BUTTON_PAINT;

	private boolean visible;

	public EmulatorButtons(EmulatorListener el) {
		this(el, LSystem.getSystemHandler().getWidth(), LSystem
				.getSystemHandler().getHeight());
	}

	public EmulatorButtons(EmulatorListener el, int w, int h) {
		this(el, w, h,
				LSystem.EMULATOR_BUTTIN_SCALE);
	}
	
	public EmulatorButtons(EmulatorListener el, int w, int h,float scale) {

		this.emulatorListener = el;
		this.dpad = GraphicsUtils.loadBitmap(LSystem.FRAMEWORK_IMG_NAME+"e1.png", false);
		this.buttons = GraphicsUtils.loadBitmap(LSystem.FRAMEWORK_IMG_NAME+"e2.png", false);
		this.width = w;
		this.height = h;

		this.setAlpha(DEFAULT_ALPHA);
		
		if (scale <= 1f) {
			this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true, 60, 60);
			this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true, 60, 60);
			this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true, 60, 60);
			this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true, 60, 60);

			this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
					68, 68);
			this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true, 68,
					68);
			this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true, 68,
					68);
			this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true, 68,
					68);
		} else {

			this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true,
					(int) (60 * scale), (int) (60 * scale));
			this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true,
					(int) (60 * scale), (int) (60 * scale));
			this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true,
					(int) (60 * scale), (int) (60 * scale));
			this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true,
					(int) (60 * scale), (int) (60 * scale));

			this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true,
					(int) (68 * scale), (int) (68 * scale));
			this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true,
					(int) (68 * scale), (int) (68 * scale));
			this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true,
					(int) (68 * scale), (int) (68 * scale));
			this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true,
					(int) (68 * scale), (int) (68 * scale));
		}
		
		this.visible = true;

		this.setLocation(0, 0);
	}

	/**
	 * 移动模拟按钮集合位置(此为相对坐标，默认居于屏幕下方)
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		if (!visible) {
			return;
		}
		this.offsetX = x;
		this.offsetY = y;
		up.setLocation((offsetX + up.getWidth()) + offset, offsetY
				+ (height - up.getHeight() * 3) - offset);
		left.setLocation((offsetX + 0) + offset, offsetY
				+ (height - left.getHeight() * 2) - offset);
		right.setLocation((offsetX + right.getWidth() * 2) + offset, offsetY
				+ (height - right.getHeight() * 2) - offset);
		down.setLocation((offsetX + down.getWidth()) + offset, offsetY
				+ (height - down.getHeight()) - offset);

		if (LSystem.getSystemHandler().getHeight() <= 320) {
			triangle.setLocation(offsetX + (width - triangle.getWidth() * 2)
					- offset, offsetY + offset);
			square.setLocation(offsetX + (width - square.getWidth()) - offset,
					offsetY + square.getHeight() + offset);
			circle.setLocation(offsetX + (width - circle.getWidth() * 3)
					- offset, offsetY + circle.getHeight() + offset);
			cancel.setLocation(offsetX + (width - cancel.getWidth() * 2)
					- offset, offsetY + offset + cancel.getHeight() * 2);
		} else {
			triangle.setLocation(offsetX + (width - triangle.getWidth() * 2),
					offsetY + offset + 80);
			square.setLocation(offsetX + (width - square.getWidth()), offsetY
					+ square.getHeight() + offset + 80);
			circle.setLocation(offsetX + (width - circle.getWidth() * 3),
					offsetY + circle.getHeight() + offset + 80);
			cancel.setLocation(offsetX + (width - cancel.getWidth() * 2),
					offsetY + offset + cancel.getHeight() * 2 + 80);
		}
	}

	public void hide() {
		hideLeft();
		hideRight();
	}

	public void show() {
		showLeft();
		showRight();
	}

	public void hideLeft() {
		up.disable(true);
		left.disable(true);
		right.disable(true);
		down.disable(true);
	}

	public void showLeft() {
		up.disable(false);
		left.disable(false);
		right.disable(false);
		down.disable(false);
	}

	public void hideRight() {
		triangle.disable(true);
		square.disable(true);
		circle.disable(true);
		cancel.disable(true);
	}

	public void showRight() {
		triangle.disable(false);
		square.disable(false);
		circle.disable(false);
		cancel.disable(false);
	}

	/**
	 * 当触发模拟按钮时，自动分配事件
	 * 
	 * @param e
	 */
	public void onEmulatorButtonEvent(MotionEvent e) {
		if (!visible) {
			return;
		}
		float touchX = 0;
		float touchY = 0;
		int code = e.getAction();
		if (MultitouchUtils.isMultitouch()) {
			int pointerCount = MultitouchUtils.getPointerCount(e);
			int id = 0;
			for (int idx = 0; idx < pointerCount; idx++) {
				id = MultitouchUtils.getPointId(e, idx);
				touchX = MultitouchUtils.getX(e, id) / LSystem.scaleWidth;
				touchY = MultitouchUtils.getY(e, id) / LSystem.scaleHeight;
				switch (code) {
				case MotionEvent.ACTION_DOWN:
					if (idx == 0) {
						hit(id, touchX, touchY);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (idx == 0) {
						unhit(id);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_1_DOWN:
					if (idx == 0) {
						hit(id, touchX, touchY);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_1_UP:
					if (idx == 0) {
						unhit(id);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_2_DOWN:
					if (idx == 1) {
						hit(id, touchX, touchY);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_2_UP:
					if (idx == 1) {
						unhit(id);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_3_DOWN:
					if (idx == 2) {
						hit(id, touchX, touchY);
					}
					break;
				case MultitouchUtils.ACTION_POINTER_3_UP:
					if (idx == 2) {
						unhit(id);
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
					release();
					break;
				}
			}
		} else {
			touchX = e.getX() / LSystem.scaleWidth;
			touchY = e.getY() / LSystem.scaleHeight;
			switch (code) {
			case MotionEvent.ACTION_DOWN:
				hit(0, touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				unhit(0);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				release();
				break;
			}
		}
	}

	public int getX() {
		return offsetX;
	}

	public int getY() {
		return offsetY;
	}

	/**
	 * 获得模拟按钮的集合
	 * 
	 * @return
	 */
	public EmulatorButton[] getEmulatorButtons() {
		return new EmulatorButton[] { up, left, right, down, triangle, square,
				circle, cancel };
	}

	/**
	 * 绘制模拟按钮(LGraphics模式)
	 * 
	 * @param g
	 */
	public void draw(LGraphics g) {
		if (!visible) {
			return;
		}

		g.setAlpha(alpha);
		up.draw(g);
		left.draw(g);
		right.draw(g);
		down.draw(g);

		triangle.draw(g);
		square.draw(g);
		circle.draw(g);
		cancel.draw(g);
		g.setAlpha(1.0F);
	}

	/**
	 * 绘制模拟按钮(Canvas模式)
	 * 
	 * @param g
	 */
	public void draw(Canvas g) {
		if (!visible) {
			return;
		}

		up.draw(g);
		left.draw(g);
		right.draw(g);
		down.draw(g);

		triangle.draw(g);
		square.draw(g);
		circle.draw(g);
		cancel.draw(g);

	}

	/**
	 * 点击事件触发器
	 * 
	 */
	private void checkOn() {

		if (emulatorListener == null) {
			return;
		}
		if (up.isClick()) {
			emulatorListener.onUpClick();
		}
		if (left.isClick()) {
			emulatorListener.onLeftClick();
		}
		if (right.isClick()) {
			emulatorListener.onRightClick();
		}
		if (down.isClick()) {
			emulatorListener.onDownClick();
		}

		if (triangle.isClick()) {
			emulatorListener.onTriangleClick();
		}
		if (square.isClick()) {
			emulatorListener.onSquareClick();
		}
		if (circle.isClick()) {
			emulatorListener.onCircleClick();
		}
		if (cancel.isClick()) {
			emulatorListener.onCancelClick();
		}
	}

	public void hit(int id, float x, float y) {
		hit(id, Math.round(x), Math.round(y));
	}

	public void hit(int id, int x, int y) {

		if (!visible) {
			return;
		}

		up.hit(id, x, y);
		left.hit(id, x, y);
		right.hit(id, x, y);
		down.hit(id, x, y);

		triangle.hit(id, x, y);
		square.hit(id, x, y);
		circle.hit(id, x, y);
		cancel.hit(id, x, y);

		checkOn();
	}

	/**
	 * 放开事件触发器
	 * 
	 * @param id
	 */
	private void checkUn(int id) {
		if (emulatorListener == null) {
			return;
		}
		if (up.isClick() && up.getPointerId() == id) {
			emulatorListener.unUpClick();
		}
		if (left.isClick() && left.getPointerId() == id) {
			emulatorListener.unLeftClick();
		}
		if (right.isClick() && right.getPointerId() == id) {
			emulatorListener.unRightClick();
		}
		if (down.isClick() && down.getPointerId() == id) {
			emulatorListener.unDownClick();
		}

		if (triangle.isClick() && triangle.getPointerId() == id) {
			emulatorListener.unTriangleClick();
		}
		if (square.isClick() && square.getPointerId() == id) {
			emulatorListener.unSquareClick();
		}
		if (circle.isClick() && circle.getPointerId() == id) {
			emulatorListener.unCircleClick();
		}
		if (cancel.isClick() && cancel.getPointerId() == id) {
			emulatorListener.unCancelClick();
		}
	}

	public void unhit(int id) {

		if (!visible) {
			return;
		}

		checkUn(id);

		up.unhit(id);
		left.unhit(id);
		right.unhit(id);
		down.unhit(id);

		triangle.unhit(id);
		square.unhit(id);
		circle.unhit(id);
		cancel.unhit(id);
	}

	/**
	 * 设定按钮组透明度
	 * 
	 * @param a
	 */
	public void setAlpha(float a) {
		if (a == 1.0f) {
			alpha = a;
			BUTTON_PAINT = null;
		} else if (a > 0.1f && a < 1.0f) {
			if (BUTTON_PAINT == null) {
				BUTTON_PAINT = new Paint();
			}
			BUTTON_PAINT.setAlpha((int) (255 * (alpha = a)));
		} else {
			alpha = DEFAULT_ALPHA;
			if (BUTTON_PAINT == null) {
				BUTTON_PAINT = new Paint();
			}
			BUTTON_PAINT.setAlpha((int) (255 * alpha));
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public EmulatorListener getEmulatorListener() {
		return emulatorListener;
	}

	public void setEmulatorListener(EmulatorListener emulator) {
		this.emulatorListener = emulator;
	}

	public EmulatorButton getUp() {
		return up;
	}

	public EmulatorButton getLeft() {
		return left;
	}

	public EmulatorButton getRight() {
		return right;
	}

	public EmulatorButton getDown() {
		return down;
	}

	public EmulatorButton getTriangle() {
		return triangle;
	}

	public EmulatorButton getSquare() {
		return square;
	}

	public EmulatorButton getCircle() {
		return circle;
	}

	public EmulatorButton getCancel() {
		return cancel;
	}

	public void release() {
		if (!visible) {
			return;
		}

		up.unhit();
		left.unhit();
		right.unhit();
		down.unhit();

		triangle.unhit();
		square.unhit();
		circle.unhit();
		cancel.unhit();
	}

}
