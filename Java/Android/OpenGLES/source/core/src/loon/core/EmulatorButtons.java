package loon.core;

import loon.action.sprite.SpriteBatch;
import loon.core.EmulatorButton;
import loon.core.EmulatorListener;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.graphics.opengl.LTextureRegion;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.MultitouchUtils;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class EmulatorButtons implements LRelease {

	private LTextureRegion dpad, buttons;

	private EmulatorButton up, left, right, down;

	private EmulatorButton triangle, square, circle, cancel;

	private EmulatorListener emulatorListener;

	private int offsetX, offsetY, width, height;

	private final static int offset = 10;

	private boolean visible;

	private LTexturePack pack;

	public EmulatorButtons(EmulatorListener el) {
		this(el, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public EmulatorButtons(EmulatorListener el, int w, int h) {
		this(el, w, h, LSystem.EMULATOR_BUTTIN_SCALE);
	}

	public EmulatorButtons(EmulatorListener el, int w, int h, float scale) {
		this.emulatorListener = el;
		if (pack == null) {
			pack = new LTexturePack();
			pack.putImage(LSystem.FRAMEWORK_IMG_NAME + "e1.png");
			pack.putImage(LSystem.FRAMEWORK_IMG_NAME + "e2.png");
			pack.pack(Format.LINEAR);
		}
		RectBox.Rect2i bounds = pack.getEntry(0).getBounds();
		this.dpad = new LTextureRegion(pack.getTexture(), bounds.left,
				bounds.top, bounds.right, bounds.bottom);
		bounds = pack.getEntry(1).getBounds();
		this.buttons = new LTextureRegion(pack.getTexture(), bounds.left,
				bounds.top, bounds.right, bounds.bottom);
		this.width = w;
		this.height = h;

		if (scale <= 0f) {
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

		if (dpad != null) {
			dpad.dispose();
			dpad = null;
		}
		if (buttons != null) {
			buttons.dispose();
			buttons = null;
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
		left.setLocation((offsetX + 0) + offset,
				offsetY + (height - left.getHeight() * 2) - offset);
		right.setLocation((offsetX + right.getWidth() * 2) + offset, offsetY
				+ (height - right.getHeight() * 2) - offset);
		down.setLocation((offsetX + down.getWidth()) + offset, offsetY
				+ (height - down.getHeight()) - offset);

		if (LSystem.screenRect.height >= LSystem.screenRect.width) {
			triangle.setLocation(offsetX + (width - triangle.getWidth() * 2)
					- offset, height - (triangle.getHeight() * 4)
					- (offset * 2));
			square.setLocation(offsetX + (width - square.getWidth()) - offset,
					height - (square.getHeight() * 3) - (offset * 2));
			circle.setLocation(offsetX + (width - circle.getWidth() * 3)
					- offset, height - (circle.getHeight() * 3) - (offset * 2));
			cancel.setLocation(offsetX + (width - cancel.getWidth() * 2)
					- offset, offsetY + height - (circle.getHeight() * 2)
					- (offset * 2));
		} else {
			triangle.setLocation(offsetX + (width - triangle.getWidth() * 2)
					- offset, height - (triangle.getHeight() * 3) - offset);
			square.setLocation(offsetX + (width - square.getWidth()) - offset,
					height - (square.getHeight() * 2) - offset);
			circle.setLocation(offsetX + (width - circle.getWidth() * 3)
					- offset, height - (circle.getHeight() * 2) - offset);
			cancel.setLocation(offsetX + (width - cancel.getWidth() * 2)
					- offset, offsetY + height - (circle.getHeight()) - offset);
		}
	}

	public boolean isClick() {
		if (up.isClick()) {
			return true;
		}
		if (left.isClick()) {
			return true;
		}
		if (down.isClick()) {
			return true;
		}
		if (right.isClick()) {
			return true;
		}
		if (triangle.isClick()) {
			return true;
		}
		if (square.isClick()) {
			return true;
		}
		if (circle.isClick()) {
			return true;
		}
		if (cancel.isClick()) {
			return true;
		}
		return false;
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
		final int code = e.getAction();
		float touchX = 0;
		float touchY = 0;
		if (MultitouchUtils.isMultitouch()) {
			int action = code & MultitouchUtils.ACTION_MASK;
			int pointerIndex = (code & MultitouchUtils.ACTION_POINTER_ID_MASK) >> MultitouchUtils.ACTION_POINTER_ID_SHIFT;
			int pointerId = MultitouchUtils.getPointId(e, pointerIndex);
			synchronized (EmulatorButtons.class) {
				touchX = MultitouchUtils.getX(e, pointerId)
						/ LSystem.scaleWidth;
				touchY = MultitouchUtils.getY(e, pointerId)
						/ LSystem.scaleHeight;
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MultitouchUtils.ACTION_POINTER_DOWN:
					hit(pointerId, touchX, touchY);
					break;
				case MotionEvent.ACTION_UP:
				case MultitouchUtils.ACTION_POINTER_UP:
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					unhit(pointerId);
					break;
				case MotionEvent.ACTION_MOVE:
					hit(pointerId, touchX, touchY);
					break;
				case MultitouchUtils.ACTION_POINTER_2_DOWN:
					hit(pointerId, touchX, touchY);
					break;
				case MultitouchUtils.ACTION_POINTER_2_UP:
					unhit(pointerId);
					break;
				case MultitouchUtils.ACTION_POINTER_3_DOWN:
					hit(pointerId, touchX, touchY);
					break;
				case MultitouchUtils.ACTION_POINTER_3_UP:
					unhit(pointerId);
					break;
				default:
					release();
					break;
				}
			}
		} else {
			touchX = e.getX() / LSystem.scaleWidth;
			touchY = e.getY() / LSystem.scaleHeight;
			switch (code) {
			case MotionEvent.ACTION_DOWN:
			case MultitouchUtils.ACTION_POINTER_DOWN:
				hit(0, touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
			case MultitouchUtils.ACTION_POINTER_UP:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
				unhit(0);
				break;
			case MotionEvent.ACTION_MOVE:
				hit(0, touchX, touchY);
				break;
			default:
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

	private SpriteBatch batch = new SpriteBatch(10);

	public void draw(GLEx g) {
		if (!visible) {
			return;
		}
		batch.begin();
		batch.halfAlpha();
		up.draw(batch);
		left.draw(batch);
		right.draw(batch);
		down.draw(batch);
		triangle.draw(batch);
		square.draw(batch);
		circle.draw(batch);
		cancel.draw(batch);
		batch.resetColor();
		batch.end();
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

	public synchronized void unhit(int id) {

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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (!visible) {
			release();
		}
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

		up.unhit();
		left.unhit();
		right.unhit();
		down.unhit();

		triangle.unhit();
		square.unhit();
		circle.unhit();
		cancel.unhit();

		if (emulatorListener != null) {
			emulatorListener.unUpClick();
			emulatorListener.unLeftClick();
			emulatorListener.unRightClick();
			emulatorListener.unDownClick();
			emulatorListener.unTriangleClick();
			emulatorListener.unSquareClick();
			emulatorListener.unCircleClick();
			emulatorListener.unCancelClick();
		}
	}

	public void dispose() {
		if (pack != null) {
			pack.dispose();
			pack = null;
		}
		if (batch != null) {
			batch.dispose();
			batch = null;
		}
	}

}
