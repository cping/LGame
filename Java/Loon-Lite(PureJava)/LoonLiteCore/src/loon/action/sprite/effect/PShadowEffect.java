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
package loon.action.sprite.effect;

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.opengl.GLEx;
import loon.utils.BufferUtils;

/**
 * 类似于吉里吉里的图片（黑白）渐变特效,按从0-255像素的趋势逐渐把一张图片透明化,从而实现各种渐变效果.
 */
public class PShadowEffect extends BaseAbstractEffect {

	private Pixmap _pixmap;

	private int indexD, indexW, block;

	private boolean flag = true;

	private int[] deasilTrans, widdershinTrans;

	public PShadowEffect(String fileName) {
		this(BaseIO.loadImage(fileName));
	}

	public PShadowEffect(Image img) {
		this(img, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public PShadowEffect(Image img, int w, int h) {
		this(img, 0, 0, w, h);
	}

	public PShadowEffect(Image img, int x, int y, int w, int h) {
		if (deasilTrans == null) {
			deasilTrans = new int[256];
			for (int i = 0; i < 256; i++) {
				deasilTrans[i] = LColor.getRGB(i, i, i);
			}
		}
		if (widdershinTrans == null) {
			widdershinTrans = new int[256];
			int idx = 0;
			for (int i = 0; i < 256; i++) {
				widdershinTrans[idx++] = deasilTrans[i];
			}
		}
		this.setDelay(10);
		this.setLocation(x, y);
		this.setSize(w, h);
		this.setEffect(img);
	}

	public PShadowEffect setEffect(String path) {
		return setEffect(BaseIO.loadImage(path));
	}

	public PShadowEffect setEffect(Image img) {
		if (_pixmap != null) {
			_pixmap.close();
			_pixmap = null;
		}
		if (img.getWidth() > 160 || img.getHeight() > 160) {
			float scale = 0.5f;
			this._pixmap = Image.getResize(img, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale))
					.getPixmap();
		} else {
			this._pixmap = img.getPixmap();
		}
		this.setTexture(_pixmap.getImage(false).texture());
		this.indexD = 255;
		this.indexW = 0;
		this.block = 8;
		this._completed = false;
		img.close();
		img = null;
		return this;
	}

	public PShadowEffect resetEffect() {
		this.indexD = 255;
		this.indexW = 0;
		this.block = 8;
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_pixmap == null) {
			return;
		}
		_completed = isCompleted();
		if (isVisible() && _timer.action(elapsedTime) && !_completed) {
			int[] pixels = _pixmap.getData();
			if (flag) {
				int[] colors = new int[block];
				for (int i = 0; i < block; i++) {
					colors[i] = widdershinTrans[indexW++];
				}
				BufferUtils.toColorKeys(pixels, colors, 0);
			} else {
				int[] colors = new int[block];
				for (int i = 0; i < block; i++) {
					colors[i] = deasilTrans[indexD--];
				}
				for (int i = 0; i < block; i++) {
					BufferUtils.toColorKeys(pixels, colors, 0);
				}
			}
			LTexture tex = super.getBitmap();
			if (tex != null && tex.pixelWidth() == _pixmap.getWidth() && tex.pixelHeight() == _pixmap.getHeight()) {
				tex.update(_pixmap.getImage(false), false);
			} else {
				setTexture(_pixmap.getImage(false).texture());
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		completedAfterBlackScreen(g, offsetX, offsetY);
	}

	@Override
	public boolean isCompleted() {
		return _completed || (flag ? (indexW >= 255) : (indexD <= 0));
	}

	public boolean isBlackToWhite() {
		return flag;
	}

	public void setBlackToWhite(boolean flag) {
		this.flag = flag;
	}

	public int getBlockSize() {
		return block;
	}

	public void setBlockSize(int block) {
		this.block = block;
	}

	@Override
	public PShadowEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	public void _onDestroy() {
		super._onDestroy();
		if (_pixmap != null) {
			_pixmap.close();
			_pixmap = null;
		}
	}

}
