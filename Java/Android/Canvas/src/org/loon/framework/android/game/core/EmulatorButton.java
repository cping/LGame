package org.loon.framework.android.game.core;

import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterFactory;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class EmulatorButton {

	private boolean disabled;

	private boolean click, onClick;

	private RectBox bounds;

	private Bitmap bitmap, bitmap1;

	private int id;

	public EmulatorButton(String fileName, int w, int h, int x, int y) {
		this(GraphicsUtils.loadBitmap(fileName, true), w, h, x, y, true);
	}

	public EmulatorButton(Bitmap img, int w, int h, int x, int y) {
		this(img, w, h, x, y, true);
	}

	public EmulatorButton(String fileName, int x, int y) {
		this(GraphicsUtils.loadBitmap(fileName, true), 0, 0, x, y, false);
	}

	public EmulatorButton(Bitmap img, int x, int y) {
		this(img, 0, 0, x, y, false);
	}

	public EmulatorButton(Bitmap img, int w, int h, int x, int y, boolean flag) {
		this(img, w, h, x, y, flag, img.getWidth(), img.getHeight());
	}

	public EmulatorButton(Bitmap img, int w, int h, int x, int y, boolean flag,
			int sizew, int sizeh) {
		if (flag) {
			LImage tmp = new LImage(img);
			this.bitmap = GraphicsUtils.drawClipImage(tmp, w, h, x, y, true)
					.getBitmap();
			tmp = null;
		} else {
			this.bitmap = img;
		}
		if (bitmap.getWidth() != sizew || bitmap.getHeight() != sizeh) {
			Bitmap src = bitmap;
			this.bitmap = GraphicsUtils.getResize(src, sizew, sizeh, true);
			if (src != null) {
				src.recycle();
				src = null;
			}
		}
		this.bitmap1 = ImageFilterFactory.getGray(bitmap);
		this.bounds = new RectBox(0, 0, bitmap.getWidth(), bitmap.getHeight());

	}

	public boolean isClick() {
		return click;
	}

	public void hit(int nid, int x, int y) {
		onClick = bounds.contains(x, y)
				|| bounds.intersects(x, y, bounds.width / 2, bounds.height / 4);
		if (nid == id) {
			click = false;
		}
		if (!disabled && !click) {
			setPointerId(nid);
			click = onClick;
		}
	}

	public void hit(int x, int y) {
		onClick = bounds.contains(x, y)
				|| bounds.intersects(x, y, bounds.width / 2, bounds.height / 4);
		if (!disabled && !click) {
			click = onClick;
		}
	}

	public void unhit(int nid) {
		if (id == nid) {
			click = false;
			onClick = false;
		}
	}

	public void unhit() {
		click = false;
		onClick = false;
	}

	public void setX(int x) {
		this.bounds.setX(x);
	}

	public void setY(int y) {
		this.bounds.setY(y);
	}

	public int getX() {
		return bounds.getX();
	}

	public int getY() {
		return bounds.getY();
	}

	public void setLocation(int x, int y) {
		this.bounds.setX(x);
		this.bounds.setY(y);
	}

	public void setPointerId(int id) {
		this.id = id;
	}

	public RectBox getBounds() {
		return bounds;
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

	public synchronized void setClickImage(Bitmap i) {
		setClickImage(null, i);
	}

	public synchronized void setClickImage(Bitmap on, Bitmap un) {
		if (un == null) {
			return;
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (bitmap1 != null) {
			bitmap1.recycle();
			bitmap1 = null;
		}
		this.bitmap = un == null ? on : un;
		this.bitmap1 = on == null ? ImageFilterFactory.getGray(un) : on;
		this.setSize(un.getWidth(), un.getHeight());
	}

	public synchronized void setOnClickImage(Bitmap img) {
		this.bitmap1 = img;
	}

	public synchronized void setUnClickImage(Bitmap img) {
		this.bitmap = img;
	}

	public void draw(LGraphics g) {
		if (!disabled) {
			if (click && onClick) {
				g.drawBitmap(bitmap1, bounds.x, bounds.y);
			} else {
				g.drawBitmap(bitmap, bounds.x, bounds.y);
			}
		}
	}

	public void draw(Canvas g) {
		if (!disabled) {
			if (click && onClick) {
				g.drawBitmap(bitmap1, bounds.x, bounds.y,
						EmulatorButtons.BUTTON_PAINT);
			} else {
				g.drawBitmap(bitmap, bounds.x, bounds.y,
						EmulatorButtons.BUTTON_PAINT);
			}
		}
	}

}
