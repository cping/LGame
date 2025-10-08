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

	private final LColor color = new LColor(LColor.gray.r, LColor.gray.g, LColor.gray.b, 0.5f);

	private boolean disabled;

	private boolean onClick;

	private RectBox bounds;

	private LTexture bitmap;

	private float scaleWidth, scaleHeight;

	private int id = -1;

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
			this.bitmap = img.copy(x, y, w, h);
		} else {
			this.bitmap = img;
		}
		this.scaleWidth = sizew;
		this.scaleHeight = sizeh;
		this.bounds = new RectBox(0, 0, scaleWidth, scaleHeight);
	}

	public boolean isClick() {
		return onClick;
	}

	public RectBox getBounds() {
		return bounds;
	}

	public EmulatorButton hit(int nid, float x, float y) {
		if (disabled) {
			return this;
		}
		hit(nid, x, y, false);
		return this;
	}

	public EmulatorButton hit(int nid, float x, float y, boolean flag) {
		if (disabled) {
			return this;
		}
		if (flag && nid == id) {
			onClick = bounds.intersects(x, y);
			if (_monitor != null) {
				if (onClick) {
					_monitor.call();
				}
			}
		} else if (!onClick) {
			onClick = bounds.intersects(x, y);
			id = nid;
			if (onClick && _monitor != null) {
				_monitor.call();
			}
		}
		return this;
	}

	public EmulatorButton hit(float x, float y) {
		if (disabled) {
			return this;
		}
		if (!onClick) {
			onClick = bounds.intersects(x, y);
			id = 0;
			if (onClick && _monitor != null) {
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
		if (disabled) {
			return this;
		}
		if (onClick && nid == id) {
			onClick = false;
			id = 0;
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
		if (disabled) {
			return this;
		}
		if (onClick) {
			id = 0;
			onClick = false;
			if (_monitor != null) {
				_monitor.free();
			}
		}
		return this;
	}

	public EmulatorButton setX(int x) {
		this.bounds.setX(x);
		return this;
	}

	public EmulatorButton setY(int y) {
		this.bounds.setY(y);
		return this;
	}

	public int getX() {
		return bounds.x();
	}

	public int getY() {
		return bounds.y();
	}

	public EmulatorButton setLocation(int x, int y) {
		this.bounds.setX(x);
		this.bounds.setY(y);
		return this;
	}

	public EmulatorButton setPointerId(int id) {
		this.id = id;
		return this;
	}

	public int getPointerId() {
		return this.id;
	}

	public boolean isEnabled() {
		return disabled;
	}

	public EmulatorButton disable(boolean flag) {
		this.disabled = flag;
		return this;
	}

	public int getHeight() {
		return bounds.height;
	}

	public int getWidth() {
		return bounds.width;
	}

	public EmulatorButton setSize(float w, float h) {
		this.bounds.setWidth(w);
		this.bounds.setHeight(h);
		return this;
	}

	public EmulatorButton setBounds(float x, float y, float w, float h) {
		this.bounds.setBounds(x, y, w, h);
		return this;
	}

	public synchronized EmulatorButton setClickImage(LTexture on) {
		if (on == null) {
			return this;
		}
		if (bitmap != null) {
			bitmap.close();
		}
		this.bitmap = on;
		this.setSize(on.width(), on.height());
		return this;
	}

	public void draw(SpriteBatch batch) {
		if (!disabled) {
			if (onClick) {
				float old = batch.getFloatColor();
				batch.setColor(color);
				batch.draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight);
				batch.setColor(old);
			} else {
				batch.draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight);
			}
		}
	}

	public void draw(GLEx g) {
		if (!disabled) {
			if (onClick) {
				g.draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight, color);
			} else {
				g.draw(bitmap, bounds.x, bounds.y, scaleWidth, scaleHeight);
			}
		}
	}
}
