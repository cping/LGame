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

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public final class EmulatorButton {

	private final LColor _color = new LColor(LColor.gray.r, LColor.gray.g, LColor.gray.b, 0.5f);

	private boolean _disabled;

	private boolean _onClick;

	private RectBox _bounds;

	private LTexture _bitmap;

	private float _scaleWidth, _scaleHeight;

	private int _id = -1;

	Monitor _monitor;

	static interface Monitor {

		void call();

		void free();

	}

	public EmulatorButton(String fileName, int w, int h, int x, int y) {
		this(LSystem.loadTexture(fileName), w, h, x, y, true);
	}

	public EmulatorButton(LTexture img, int w, int h, int x, int y) {
		this(img, w, h, x, y, true);
	}

	public EmulatorButton(String fileName, int x, int y) {
		this(LSystem.loadTexture(fileName), 0, 0, x, y, false);
	}

	public EmulatorButton(LTexture img, int x, int y) {
		this(img, 0, 0, x, y, false);
	}

	public EmulatorButton(LTexture img, int w, int h, int x, int y, boolean flag) {
		this(img, w, h, x, y, flag, img.getWidth(), img.getHeight());
	}

	public EmulatorButton(LTexture img, int w, int h, int x, int y, boolean flag, int sizew, int sizeh) {
		if (flag) {
			this._bitmap = img.copy(x, y, w, h);
		} else {
			this._bitmap = img;
		}
		this._scaleWidth = sizew;
		this._scaleHeight = sizeh;
		this._bounds = new RectBox(0, 0, _scaleWidth, _scaleHeight);
	}

	public boolean isClick() {
		return _onClick;
	}

	public RectBox getBounds() {
		return _bounds;
	}

	public EmulatorButton hit(int nid, float x, float y) {
		if (_disabled) {
			return this;
		}
		hit(nid, x, y, false);
		return this;
	}

	public EmulatorButton hit(int nid, float x, float y, boolean flag) {
		if (_disabled) {
			return this;
		}
		if (flag && nid == _id) {
			_onClick = _bounds.intersects(x, y);
			if (_monitor != null) {
				if (_onClick) {
					_monitor.call();
				}
			}
		} else if (!_onClick) {
			_onClick = _bounds.intersects(x, y);
			_id = nid;
			if (_onClick && _monitor != null) {
				_monitor.call();
			}
		}
		return this;
	}

	public EmulatorButton hit(float x, float y) {
		if (_disabled) {
			return this;
		}
		if (!_onClick) {
			_onClick = _bounds.intersects(x, y);
			_id = 0;
			if (_onClick && _monitor != null) {
				try {
					_monitor.call();
				} catch (Throwable t) {
					LSystem.error("EmulatorButton call() exception", t);
				}
			}
		}
		return this;
	}

	public EmulatorButton unhit(int nid, float x, float y) {
		if (_disabled) {
			return this;
		}
		if (_onClick && nid == _id) {
			_onClick = false;
			_id = 0;
			if (_monitor != null) {
				try {
					_monitor.free();
				} catch (Throwable t) {
					LSystem.error("EmulatorButton free() exception", t);
				}
			}
		}
		return this;
	}

	public EmulatorButton unhit() {
		if (_disabled) {
			return this;
		}
		if (_onClick) {
			_id = 0;
			_onClick = false;
			if (_monitor != null) {
				_monitor.free();
			}
		}
		return this;
	}

	public EmulatorButton setX(int x) {
		this._bounds.setX(x);
		return this;
	}

	public EmulatorButton setY(int y) {
		this._bounds.setY(y);
		return this;
	}

	public int getX() {
		return _bounds.x();
	}

	public int getY() {
		return _bounds.y();
	}

	public EmulatorButton setLocation(int x, int y) {
		this._bounds.setX(x);
		this._bounds.setY(y);
		return this;
	}

	public EmulatorButton setPointerId(int id) {
		this._id = id;
		return this;
	}

	public int getPointerId() {
		return this._id;
	}

	public boolean isEnabled() {
		return _disabled;
	}

	public EmulatorButton disable(boolean flag) {
		this._disabled = flag;
		return this;
	}

	public int getHeight() {
		return _bounds.height;
	}

	public int getWidth() {
		return _bounds.width;
	}

	public EmulatorButton setSize(float w, float h) {
		this._bounds.setWidth(w);
		this._bounds.setHeight(h);
		return this;
	}

	public EmulatorButton setBounds(float x, float y, float w, float h) {
		this._bounds.setBounds(x, y, w, h);
		return this;
	}

	public synchronized EmulatorButton setClickImage(LTexture on) {
		if (on == null) {
			return this;
		}
		if (_bitmap != null) {
			_bitmap.close();
		}
		this._bitmap = on;
		this.setSize(on.width(), on.height());
		return this;
	}

	public void draw(SpriteBatch batch) {
		if (!_disabled) {
			if (_onClick) {
				float old = batch.getFloatColor();
				batch.setColor(_color);
				batch.draw(_bitmap, _bounds.x, _bounds.y, _scaleWidth, _scaleHeight);
				batch.setColor(old);
			} else {
				batch.draw(_bitmap, _bounds.x, _bounds.y, _scaleWidth, _scaleHeight);
			}
		}
	}

	public void draw(GLEx g) {
		if (!_disabled) {
			if (_onClick) {
				g.draw(_bitmap, _bounds.x, _bounds.y, _scaleWidth, _scaleHeight, _color);
			} else {
				g.draw(_bitmap, _bounds.x, _bounds.y, _scaleWidth, _scaleHeight);
			}
		}
	}
}
