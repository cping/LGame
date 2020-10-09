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
package loon;

import loon.LTexture.Format;
import loon.action.sprite.SpriteBatch;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;

public class EmulatorButtons implements LRelease {

	private LTexture dpad, buttons;

	private EmulatorButton up, left, right, down;

	private EmulatorButton triangle, square, circle, cancel;

	private EmulatorListener emulatorListener;

	private int offsetX, offsetY, width, height;

	private final static int offset = 10;

	private boolean visible, closed;

	private float ealpha = 0.5f;

	private LTexturePack pack;

	public EmulatorButtons(EmulatorListener el) {
		this(el, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), LSystem.getEmulatorScale());
	}

	public EmulatorButtons(EmulatorListener el, int w, int h) {
		this(el, w, h, LSystem.getEmulatorScale());
	}

	public EmulatorButtons(EmulatorListener el, int w, int h, float scale) {
		this.emulatorListener = el;
		if (pack == null) {
			pack = new LTexturePack();
			pack.putImage(LSystem.getSystemImagePath() + "e1.png");
			pack.putImage(LSystem.getSystemImagePath() + "e2.png");
			pack.pack(Format.LINEAR);

		}

		this.dpad = pack.getTextureAll(0);
		this.buttons = pack.getTextureAll(1);
		this.width = w;
		this.height = h;

		if (scale <= 0f) {
			this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true, 60, 60);
			this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true, 60, 60);
			this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true, 60, 60);
			this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true, 60, 60);

			this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true, 68, 68);
			this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true, 68, 68);
			this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true, 68, 68);
			this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true, 68, 68);
		} else {

			this.up = new EmulatorButton(dpad, 40, 40, 40, 0, true, (int) (60 * scale), (int) (60 * scale));
			this.left = new EmulatorButton(dpad, 40, 40, 0, 40, true, (int) (60 * scale), (int) (60 * scale));
			this.right = new EmulatorButton(dpad, 40, 40, 80, 40, true, (int) (60 * scale), (int) (60 * scale));
			this.down = new EmulatorButton(dpad, 40, 40, 40, 80, true, (int) (60 * scale), (int) (60 * scale));

			this.triangle = new EmulatorButton(buttons, 48, 48, 48, 0, true, (int) (68 * scale), (int) (68 * scale));
			this.square = new EmulatorButton(buttons, 48, 48, 0, 48, true, (int) (68 * scale), (int) (68 * scale));
			this.circle = new EmulatorButton(buttons, 48, 48, 96, 48, true, (int) (68 * scale), (int) (68 * scale));
			this.cancel = new EmulatorButton(buttons, 48, 48, 48, 96, true, (int) (68 * scale), (int) (68 * scale));
		}
		this.up._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unUpClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onUpClick();
				}
			}
		};
		this.left._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unLeftClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onLeftClick();
				}
			}
		};
		this.right._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unRightClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onRightClick();
				}
			}
		};
		this.down._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unDownClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onDownClick();
				}
			}
		};

		this.triangle._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unTriangleClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onTriangleClick();
				}
			}
		};
		this.square._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unSquareClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onSquareClick();
				}
			}
		};
		this.circle._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unCircleClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onCircleClick();
				}
			}
		};
		this.cancel._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (emulatorListener != null) {
					emulatorListener.unCancelClick();
				}
			}

			@Override
			public void call() {
				if (emulatorListener != null) {
					emulatorListener.onCancelClick();
				}
			}
		};

		this.visible = true;

		this.setLocation(0, 0);
	}

	private int offsetLeftPad = 0;

	private int offsetRightPad = 0;

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
		up.setLocation(offsetLeftPad + (offsetX + up.getWidth()) + offset,
				offsetLeftPad + offsetY + (height - up.getHeight() * 3) - offset);
		left.setLocation(offsetLeftPad + (offsetX + 0) + offset,
				offsetLeftPad + offsetY + (height - left.getHeight() * 2) - offset);
		right.setLocation(offsetLeftPad + (offsetX + right.getWidth() * 2) + offset,
				offsetLeftPad + offsetY + (height - right.getHeight() * 2) - offset);
		down.setLocation(offsetLeftPad + (offsetX + down.getWidth()) + offset,
				offsetLeftPad + offsetY + (height - down.getHeight()) - offset);

		if (LSystem.viewSize.height >= LSystem.viewSize.width) {
			triangle.setLocation(offsetRightPad + offsetX + (width - triangle.getWidth() * 2) - offset,
					offsetRightPad + height - (triangle.getHeight() * 4) - (offset * 2));
			square.setLocation(offsetRightPad + offsetX + (width - square.getWidth()) - offset,
					offsetRightPad + height - (square.getHeight() * 3) - (offset * 2));
			circle.setLocation(offsetRightPad + offsetX + (width - circle.getWidth() * 3) - offset,
					offsetRightPad + height - (circle.getHeight() * 3) - (offset * 2));
			cancel.setLocation(offsetRightPad + offsetX + (width - cancel.getWidth() * 2) - offset,
					offsetRightPad + offsetY + height - (circle.getHeight() * 2) - (offset * 2));
		} else {
			triangle.setLocation(offsetRightPad + offsetX + (width - triangle.getWidth() * 2) - offset,
					offsetRightPad + height - (triangle.getHeight() * 3) - offset);
			square.setLocation(offsetRightPad + offsetX + (width - square.getWidth()) - offset,
					offsetRightPad + height - (square.getHeight() * 2) - offset);
			circle.setLocation(offsetX + (width - circle.getWidth() * 3) - offset,
					offsetRightPad + height - (offsetRightPad + circle.getHeight() * 2) - offset);
			cancel.setLocation(offsetRightPad + offsetX + (width - cancel.getWidth() * 2) - offset,
					offsetRightPad + offsetY + height - (circle.getHeight()) - offset);
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
		return new EmulatorButton[] { up, left, right, down, triangle, square, circle, cancel };
	}

	public void draw(GLEx g) {
		if (!visible) {
			return;
		}
		float tmp = g.alpha();
		g.setAlpha(ealpha);
		up.draw(g);
		left.draw(g);
		right.draw(g);
		down.draw(g);
		triangle.draw(g);
		square.draw(g);
		circle.draw(g);
		cancel.draw(g);
		g.setAlpha(tmp);
	}

	public void draw(SpriteBatch g) {
		if (!visible) {
			return;
		}
		float tmp = g.alpha();
		g.setAlpha(ealpha);
		up.draw(g);
		left.draw(g);
		right.draw(g);
		down.draw(g);
		triangle.draw(g);
		square.draw(g);
		circle.draw(g);
		cancel.draw(g);
		g.setAlpha(tmp);
	}

	public void setAlpha(float a) {
		this.ealpha = a;
	}

	public float getAlpha() {
		return this.ealpha;
	}

	public void hit(int id, float x, float y, boolean flag) {

		if (!visible) {
			return;
		}

		up.hit(id, x, y, flag);
		left.hit(id, x, y, flag);
		right.hit(id, x, y, flag);
		down.hit(id, x, y, flag);

		triangle.hit(id, x, y, flag);
		square.hit(id, x, y, flag);
		circle.hit(id, x, y, flag);
		cancel.hit(id, x, y, flag);

	}

	public void unhit(int id, float x, float y) {

		if (!visible) {
			return;
		}

		up.unhit(id, x, y);
		left.unhit(id, x, y);
		right.unhit(id, x, y);
		down.unhit(id, x, y);

		triangle.unhit(id, x, y);
		square.unhit(id, x, y);
		circle.unhit(id, x, y);
		cancel.unhit(id, x, y);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean v) {
		if (!v) {
			release();
		}
		this.visible = v;
	}

	public EmulatorListener getEmulatorListener() {
		return emulatorListener;
	}

	public void setEmulatorListener(EmulatorListener emulator) {
		this.emulatorListener = emulator;
	}

	public EmulatorButtons disableDirection() {
		up.disable(true);
		left.disable(true);
		right.disable(true);
		down.disable(true);
		return this;
	}

	public EmulatorButtons disableShapeButton() {
		triangle.disable(true);
		cancel.disable(true);
		circle.disable(true);
		square.disable(true);
		return this;
	}

	public EmulatorButton getCancel() {
		return cancel;
	}

	public EmulatorButton getCircle() {
		return circle;
	}

	public EmulatorButton getDown() {
		return down;
	}

	public EmulatorButton getLeft() {
		return left;
	}

	public EmulatorButton getRight() {
		return right;
	}

	public EmulatorButton getSquare() {
		return square;
	}

	public EmulatorButton getTriangle() {
		return triangle;
	}

	public EmulatorButton getUp() {
		return up;
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

	public int getOffsetLeftPad() {
		return offsetLeftPad;
	}

	public void setOffsetLeftPad(int offsetLeftPad) {
		this.offsetLeftPad = offsetLeftPad;
	}

	public int getOffsetRightPad() {
		return offsetRightPad;
	}

	public void setOffsetRightPad(int offsetRightPad) {
		this.offsetRightPad = offsetRightPad;
	}
	
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (pack != null) {
			pack.close();
			pack = null;
		}
		if (dpad != null) {
			dpad.close();
			dpad = null;
		}
		if (buttons != null) {
			buttons.close();
			buttons = null;
		}
		closed = true;
	}


}
