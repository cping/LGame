package loon.core.graphics;

import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.CollectionUtils;


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
// 自0.3.2版起新增的像素处理类
public class LPixmapData {

	private int width, height;

	private boolean hasAlpha;

	private boolean isDirty, isClose;

	private int[] pixels, finalPixels;

	private LImage buffer;

	private LTexture texture;

	public LPixmapData(final int width, final int height) {
		init(width, height, true);
	}

	public LPixmapData(final int width, final int height, final boolean alpha) {
		init(width, height, alpha);
	}

	private void init(final int width, final int height, final boolean alpha) {
		this.width = width;
		this.height = height;
		this.hasAlpha = alpha;
		this.buffer = LImage.createImage(width, height, alpha);
		this.pixels = buffer.getPixels();
		this.finalPixels = CollectionUtils.copyOf(pixels);
		this.texture = new LTexture(width, height, hasAlpha);
	}

	private LPixmapData(final int[] pixels, final int width, final int height) {
		init(width, height, true);
		this.buffer.setPixels(pixels, 0, width, 0, 0, width, height);
		this.finalPixels = CollectionUtils.copyOf(pixels);
		this.texture = new LTexture(width, height, hasAlpha);
	}

	public LPixmapData(String resName) {
		this(GraphicsUtils.loadImage(resName));
	}

	public LPixmapData(LImage pix) {
		this(pix.getPixels(), pix.getWidth(), pix.getHeight());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int size() {
		return width * height;
	}

	public int get(final int x, final int y) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			return pixels[x + y * width];
		} else {
			return 0;
		}
	}

	/**
	 * 提交数据修改结果
	 */
	public void submit() {
		if (!isDirty) {
			if (buffer == null) {
				return;
			}
			synchronized (buffer) {
				buffer.setPixels(pixels, width, height);
			}
			isDirty = true;
		}
	}

	public void put(final int x, final int y, final int color) {
		if (x > -1 && y > -1 && x < width && y < height) {
			pixels[x + y * width] = color;
		}
	}

	public void put(final int index, final int color) {
		pixels[index] = color;
	}

	public void reset() {
		if (isClose) {
			return;
		}
		this.pixels = CollectionUtils.copyOf(finalPixels);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void draw(GLEx g, float x, float y, float w, float h) {
		if (isClose) {
			return;
		}
		synchronized (texture) {
			if (isDirty) {
				g.copyImageToTexture(texture, buffer, 0, 0);
				g.drawTexture(texture, x, y, w, h);
				isDirty = false;
			} else {
				g.drawTexture(texture, x, y, w, h);
			}
		}
	}

	public void draw(GLEx g, float x, float y) {
		draw(g, x, y, width, height);
	}

	public boolean isClose() {
		return this.isClose;
	}

	public void dispose() {
		this.isClose = true;
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
		if (buffer != null) {
			buffer.dispose();
			buffer = null;
		}
	}
}
