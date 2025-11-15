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

import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

public final class EmulatorButtons implements LRelease {

	private final static int offset = 10;

	private LTexture _dpad, _buttons;

	private EmulatorButton _up, _left, _right, _down;

	private EmulatorButton _triangle, _square, _circle, _cancel;

	private EmulatorListener _emulatorListener;

	private int _offsetX, _offsetY, _width, _height;

	private int _offsetLeftPad = 0;

	private int _offsetRightPad = 0;

	private boolean _visible, _closed;

	private float _ealpha = 0.5f;

	private LTexturePack _pack;

	public EmulatorButtons(EmulatorListener el) {
		this(el, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), LSystem.getEmulatorScale());
	}

	public EmulatorButtons(EmulatorListener el, int w, int h) {
		this(el, w, h, LSystem.getEmulatorScale());
	}

	public EmulatorButtons(EmulatorListener el, int w, int h, float scale) {
		this._emulatorListener = el;
		if (_pack == null) {
			_pack = new LTexturePack();
			_pack.putImage(LSystem.getSystemImagePath() + "e1.png");
			_pack.putImage(LSystem.getSystemImagePath() + "e2.png");
			_pack.pack();
		}

		this._dpad = _pack.getTextureAll(0);
		this._buttons = _pack.getTextureAll(1);

		if (scale <= 0f) {
			this._up = new EmulatorButton(_dpad, 40, 40, 40, 0, true, 60, 60);
			this._left = new EmulatorButton(_dpad, 40, 40, 0, 40, true, 60, 60);
			this._right = new EmulatorButton(_dpad, 40, 40, 80, 40, true, 60, 60);
			this._down = new EmulatorButton(_dpad, 40, 40, 40, 80, true, 60, 60);

			this._triangle = new EmulatorButton(_buttons, 48, 48, 48, 0, true, 68, 68);
			this._square = new EmulatorButton(_buttons, 48, 48, 0, 48, true, 68, 68);
			this._circle = new EmulatorButton(_buttons, 48, 48, 96, 48, true, 68, 68);
			this._cancel = new EmulatorButton(_buttons, 48, 48, 48, 96, true, 68, 68);
		} else {

			this._up = new EmulatorButton(_dpad, 40, 40, 40, 0, true, MathUtils.ifloor(60 * scale),
					MathUtils.ifloor(60 * scale));
			this._left = new EmulatorButton(_dpad, 40, 40, 0, 40, true, MathUtils.ifloor(60 * scale),
					MathUtils.ifloor(60 * scale));
			this._right = new EmulatorButton(_dpad, 40, 40, 80, 40, true, MathUtils.ifloor(60 * scale),
					MathUtils.ifloor(60 * scale));
			this._down = new EmulatorButton(_dpad, 40, 40, 40, 80, true, MathUtils.ifloor(60 * scale),
					MathUtils.ifloor(60 * scale));

			this._triangle = new EmulatorButton(_buttons, 48, 48, 48, 0, true, MathUtils.ifloor(68 * scale),
					MathUtils.ifloor(68 * scale));
			this._square = new EmulatorButton(_buttons, 48, 48, 0, 48, true, MathUtils.ifloor(68 * scale),
					MathUtils.ifloor(68 * scale));
			this._circle = new EmulatorButton(_buttons, 48, 48, 96, 48, true, MathUtils.ifloor(68 * scale),
					MathUtils.ifloor(68 * scale));
			this._cancel = new EmulatorButton(_buttons, 48, 48, 48, 96, true, MathUtils.ifloor(68 * scale),
					MathUtils.ifloor(68 * scale));
		}
		this._up._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unUpClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onUpClick();
				}
			}
		};
		this._left._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unLeftClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onLeftClick();
				}
			}
		};
		this._right._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unRightClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onRightClick();
				}
			}
		};
		this._down._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unDownClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onDownClick();
				}
			}
		};

		this._triangle._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unTriangleClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onTriangleClick();
				}
			}
		};
		this._square._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unSquareClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onSquareClick();
				}
			}
		};
		this._circle._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unCircleClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onCircleClick();
				}
			}
		};
		this._cancel._monitor = new EmulatorButton.Monitor() {
			@Override
			public void free() {
				if (_emulatorListener != null) {
					_emulatorListener.unCancelClick();
				}
			}

			@Override
			public void call() {
				if (_emulatorListener != null) {
					_emulatorListener.onCancelClick();
				}
			}
		};

		this._visible = true;

		this.updateSize(w, h);
	}

	public EmulatorButtons updateSize(int w, int h) {
		return updateSize(0, 0, w, h);
	}

	public EmulatorButtons updateSize(int x, int y, int w, int h) {
		this._width = w;
		this._height = h;
		this.setLocation(x, y);
		return this;
	}

	/**
	 * 移动模拟按钮集合位置(此为相对坐标，默认居于屏幕下方)
	 * 
	 * @param x
	 * @param y
	 */
	public EmulatorButtons setLocation(int x, int y) {
		if (!_visible) {
			return this;
		}
		this._offsetX = x;
		this._offsetY = y;
		_up.setLocation(_offsetLeftPad + (_offsetX + _up.getWidth()) + offset,
				_offsetLeftPad + _offsetY + (_height - _up.getHeight() * 3) - offset);
		_left.setLocation(_offsetLeftPad + (_offsetX + 0) + offset,
				_offsetLeftPad + _offsetY + (_height - _left.getHeight() * 2) - offset);
		_right.setLocation(_offsetLeftPad + (_offsetX + _right.getWidth() * 2) + offset,
				_offsetLeftPad + _offsetY + (_height - _right.getHeight() * 2) - offset);
		_down.setLocation(_offsetLeftPad + (_offsetX + _down.getWidth()) + offset,
				_offsetLeftPad + _offsetY + (_height - _down.getHeight()) - offset);

		if (LSystem.viewSize.height >= LSystem.viewSize.width) {
			_triangle.setLocation(_offsetRightPad + _offsetX + (_width - _triangle.getWidth() * 2) - offset,
					_offsetRightPad + _height - (_triangle.getHeight() * 4) - (offset * 2));
			_square.setLocation(_offsetRightPad + _offsetX + (_width - _square.getWidth()) - offset,
					_offsetRightPad + _height - (_square.getHeight() * 3) - (offset * 2));
			_circle.setLocation(_offsetRightPad + _offsetX + (_width - _circle.getWidth() * 3) - offset,
					_offsetRightPad + _height - (_circle.getHeight() * 3) - (offset * 2));
			_cancel.setLocation(_offsetRightPad + _offsetX + (_width - _cancel.getWidth() * 2) - offset,
					_offsetRightPad + _offsetY + _height - (_circle.getHeight() * 2) - (offset * 2));
		} else {
			_triangle.setLocation(_offsetRightPad + _offsetX + (_width - _triangle.getWidth() * 2) - offset,
					_offsetRightPad + _height - (_triangle.getHeight() * 3) - offset);
			_square.setLocation(_offsetRightPad + _offsetX + (_width - _square.getWidth()) - offset,
					_offsetRightPad + _height - (_square.getHeight() * 2) - offset);
			_circle.setLocation(_offsetX + (_width - _circle.getWidth() * 3) - offset,
					_offsetRightPad + _height - (_offsetRightPad + _circle.getHeight() * 2) - offset);
			_cancel.setLocation(_offsetRightPad + _offsetX + (_width - _cancel.getWidth() * 2) - offset,
					_offsetRightPad + _offsetY + _height - (_circle.getHeight()) - offset);
		}
		return this;
	}

	public EmulatorButtons hide() {
		hideLeft();
		hideRight();
		return this;
	}

	public EmulatorButtons show() {
		showLeft();
		showRight();
		return this;
	}

	public EmulatorButtons hideLeft() {
		_up.disable(true);
		_left.disable(true);
		_right.disable(true);
		_down.disable(true);
		return this;
	}

	public EmulatorButtons showLeft() {
		_up.disable(false);
		_left.disable(false);
		_right.disable(false);
		_down.disable(false);
		return this;
	}

	public EmulatorButtons hideRight() {
		_triangle.disable(true);
		_square.disable(true);
		_circle.disable(true);
		_cancel.disable(true);
		return this;
	}

	public EmulatorButtons showRight() {
		_triangle.disable(false);
		_square.disable(false);
		_circle.disable(false);
		_cancel.disable(false);
		return this;
	}

	public int getX() {
		return _offsetX;
	}

	public int getY() {
		return _offsetY;
	}

	/**
	 * 获得模拟按钮的集合
	 * 
	 * @return
	 */
	public EmulatorButton[] getEmulatorButtons() {
		return new EmulatorButton[] { _up, _left, _right, _down, _triangle, _square, _circle, _cancel };
	}

	public EmulatorButtons draw(GLEx g) {
		if (!_visible) {
			return this;
		}
		float tmp = g.alpha();
		g.setAlpha(_ealpha);
		_up.draw(g);
		_left.draw(g);
		_right.draw(g);
		_down.draw(g);
		_triangle.draw(g);
		_square.draw(g);
		_circle.draw(g);
		_cancel.draw(g);
		g.setAlpha(tmp);
		return this;
	}

	public EmulatorButtons setAlpha(float a) {
		this._ealpha = a;
		return this;
	}

	public float getAlpha() {
		return this._ealpha;
	}

	public EmulatorButtons hit(int id, float x, float y, boolean flag) {

		if (!_visible) {
			return this;
		}

		_up.hit(id, x, y, flag);
		_left.hit(id, x, y, flag);
		_right.hit(id, x, y, flag);
		_down.hit(id, x, y, flag);

		_triangle.hit(id, x, y, flag);
		_square.hit(id, x, y, flag);
		_circle.hit(id, x, y, flag);
		_cancel.hit(id, x, y, flag);
		return this;

	}

	public EmulatorButtons unhit(int id, float x, float y) {

		if (!_visible) {
			return this;
		}

		_up.unhit(id, x, y);
		_left.unhit(id, x, y);
		_right.unhit(id, x, y);
		_down.unhit(id, x, y);

		_triangle.unhit(id, x, y);
		_square.unhit(id, x, y);
		_circle.unhit(id, x, y);
		_cancel.unhit(id, x, y);
		return this;
	}

	public boolean isVisible() {
		return _visible;
	}

	public EmulatorButtons setVisible(boolean v) {
		if (!v) {
			release();
		}
		this._visible = v;
		return this;
	}

	public EmulatorListener getEmulatorListener() {
		return _emulatorListener;
	}

	public EmulatorButtons setEmulatorListener(EmulatorListener emulator) {
		this._emulatorListener = emulator;
		return this;
	}

	public EmulatorButtons disableDirection() {
		_up.disable(true);
		_left.disable(true);
		_right.disable(true);
		_down.disable(true);
		return this;
	}

	public EmulatorButtons disableShapeButton() {
		_triangle.disable(true);
		_cancel.disable(true);
		_circle.disable(true);
		_square.disable(true);
		return this;
	}

	public EmulatorButton getCancel() {
		return _cancel;
	}

	public EmulatorButton getCircle() {
		return _circle;
	}

	public EmulatorButton getDown() {
		return _down;
	}

	public EmulatorButton getLeft() {
		return _left;
	}

	public EmulatorButton getRight() {
		return _right;
	}

	public EmulatorButton getSquare() {
		return _square;
	}

	public EmulatorButton getTriangle() {
		return _triangle;
	}

	public EmulatorButton getUp() {
		return _up;
	}

	public EmulatorButtons release() {
		_up.unhit();
		_left.unhit();
		_right.unhit();
		_down.unhit();

		_triangle.unhit();
		_square.unhit();
		_circle.unhit();
		_cancel.unhit();

		if (_emulatorListener != null) {
			_emulatorListener.unUpClick();
			_emulatorListener.unLeftClick();
			_emulatorListener.unRightClick();
			_emulatorListener.unDownClick();
			_emulatorListener.unTriangleClick();
			_emulatorListener.unSquareClick();
			_emulatorListener.unCircleClick();
			_emulatorListener.unCancelClick();
		}
		return this;
	}

	public int getOffsetLeftPad() {
		return _offsetLeftPad;
	}

	public EmulatorButtons setOffsetLeftPad(int offsetLeftPad) {
		this._offsetLeftPad = offsetLeftPad;
		return this;
	}

	public int getOffsetRightPad() {
		return _offsetRightPad;
	}

	public EmulatorButtons setOffsetRightPad(int offsetRightPad) {
		this._offsetRightPad = offsetRightPad;
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_pack != null) {
			_pack.close();
			_pack = null;
		}
		if (_dpad != null) {
			_dpad.close();
			_dpad = null;
		}
		if (_buttons != null) {
			_buttons.close();
			_buttons = null;
		}
		_closed = true;
	}

}
