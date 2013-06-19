package loon.core;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;

/**
 * Copyright 2008 - 2010
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
public class EmulatorButton {

	private final LColor color = new LColor(LColor.gray.r, LColor.gray.g,
			LColor.gray.b, 0.5f);

	private boolean disabled;

	private boolean onClick;

	private RectBox bounds;

	private LTextureRegion bitmap;

	private float scaleWidth, scaleHeight;

	private int id = -1;

	Monitor _monitor;

	static interface Monitor {

		void call();

		void free();

	}

	public EmulatorButton(String fileName, int w, int h, int x, int y) {
		this(new LTextureRegion(fileName), w, h, x, y, true);
	}

	public EmulatorButton(LTextureRegion img, int w, int h, int x, int y) {
		this(img, w, h, x, y, true);
	}

	public EmulatorButton(String fileName, int x, int y) {
		this(new LTextureRegion(fileName), 0, 0, x, y, false);
	}

	public EmulatorButton(LTextureRegion img, int x, int y) {
		this(img, 0, 0, x, y, false);
	}

	public EmulatorButton(LTextureRegion img, int w, int h, int x, int y,
			boolean flag) {
		this(img, w, h, x, y, flag, img.getRegionWidth(), img.getRegionHeight());
	}

	public EmulatorButton(LTextureRegion img, int w, int h, int x, int y,
			boolean flag, int sizew, int sizeh) {
		if (flag) {
			this.bitmap = new LTextureRegion(img, x, y, w, h);
		} else {
			this.bitmap = new LTextureRegion(img);
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

	public void hit(int nid, float x, float y) {
		hit(nid, x, y, false);
	}

	public void hit(int nid, float x, float y, boolean flag) {
		if (flag) {
			if (nid == id) {
				onClick = bounds.contains(x, y);
				if (_monitor != null) {
					if (onClick) {
						_monitor.call();
					}
				}
			}
		} else {
			if (!onClick) {
				onClick = bounds.contains(x, y);
				id = nid;
				if (onClick && _monitor != null) {
					_monitor.call();
				}
			}
		}
	}

	public void hit(float x, float y) {
		if (!onClick) {
			onClick = bounds.contains(x, y);
			id = 0;
			if (onClick && _monitor != null) {
				_monitor.call();
			}
		}
	}

	public void unhit(int nid, float x, float y) {
		if (onClick && nid == id) {
			onClick = false;
			id = 0;
			if (_monitor != null) {
				_monitor.free();
			}
		}
	}

	public void unhit() {
		if (onClick) {
			id = 0;
			onClick = false;
			if (_monitor != null) {
				_monitor.free();
			}
		}
	}

	public void setX(int x) {
		this.bounds.setX(x);
	}

	public void setY(int y) {
		this.bounds.setY(y);
	}

	public int getX() {
		return bounds.x();
	}

	public int getY() {
		return bounds.y();
	}

	public void setLocation(int x, int y) {
		this.bounds.setX(x);
		this.bounds.setY(y);
	}

	public void setPointerId(int id) {
		this.id = id;
	}

	public int getPointerId() {
		return this.id;
	}

	public boolean isEnabled() {
		return disabled;
	}

	public void disable(boolean flag) {
		this.disabled = flag;
	}

	public int getHeight() {
		return bounds.height;
	}

	public int getWidth() {
		return bounds.width;
	}

	public void setSize(int w, int h) {
		this.bounds.setWidth(w);
		this.bounds.setHeight(h);
	}

	public void setBounds(int x, int y, int w, int h) {
		this.bounds.setBounds(x, y, w, h);
	}

	public synchronized void setClickImage(LTexture on) {
		if (on == null) {
			return;
		}
		if (bitmap != null) {
			bitmap.dispose();
		}
		this.bitmap = new LTextureRegion(on);
		this.setSize(on.getWidth(), on.getHeight());
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

}
