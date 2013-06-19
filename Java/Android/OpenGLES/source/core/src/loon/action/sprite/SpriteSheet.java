package loon.action.sprite;

import loon.core.LRelease;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureBatch.GLCache;

/**
 * 
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
public class SpriteSheet implements LRelease {

	private int margin, spacing;

	private int tw, th;

	private int width, height;

	private LTexture[][] subImages;

	private LTexture target;

	public SpriteSheet(String fileName, int tw, int th, int s, int m) {
		this(new LTexture(fileName), tw, th, s, m);
	}

	public SpriteSheet(String fileName, int tw, int th) {
		this(new LTexture(fileName), tw, th, 0, 0);
	}

	public SpriteSheet(LTexture image, int tw, int th) {
		this(image, tw, th, 0, 0);
	}

	public SpriteSheet(LTexture img, int tw, int th, int s, int m) {
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.target = img;
		this.tw = tw;
		this.th = th;
		this.margin = m;
		this.spacing = s;
	}

	private void update() {
		if (subImages != null) {
			return;
		}
		if (!target.isLoaded()) {
			target.loadTexture();
		}
		int tilesAcross = ((width - (margin * 2) - tw) / (tw + spacing)) + 1;
		int tilesDown = ((height - (margin * 2) - th) / (th + spacing)) + 1;
		if ((height - th) % (th + spacing) != 0) {
			tilesDown++;
		}
		subImages = new LTexture[tilesAcross][tilesDown];
		for (int x = 0; x < tilesAcross; x++) {
			for (int y = 0; y < tilesDown; y++) {
				subImages[x][y] = getImage(x, y);
			}
		}
	}

	public LTexture[][] getTextures() {
		return subImages;
	}

	private void checkImage(int x, int y) {
		update();
		if ((x < 0) || (x >= subImages.length)) {
			throw new RuntimeException("SubImage out of sheet bounds " + x
					+ "," + y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new RuntimeException("SubImage out of sheet bounds " + x
					+ "," + y);
		}
	}

	public LTexture getImage(int x, int y) {
		checkImage(x, y);
		if ((x < 0) || (x >= subImages.length)) {
			throw new RuntimeException("SubTexture2D out of sheet bounds: " + x
					+ "," + y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new RuntimeException("SubTexture2D out of sheet bounds: " + x
					+ "," + y);
		}
		return target.getSubTexture(x * (tw + spacing) + margin, y
				* (th + spacing) + margin, tw, th);
	}

	public int getHorizontalCount() {
		update();
		return subImages.length;
	}

	public int getVerticalCount() {
		update();
		return subImages[0].length;
	}

	public LTexture getSubImage(int x, int y) {
		checkImage(x, y);
		return subImages[x][y];
	}

	public void draw(GLEx g, int x, int y, int sx, int sy) {
		draw(g, x, y, sx, sy, null);
	}

	public void draw(GLEx g, int x, int y, int sx, int sy, LColor[] color) {
		if (target.isBatch()) {
			final float nx = sx * tw;
			final float ny = sy * th;
			target.draw(x, y, tw, th, nx, ny, nx + tw, ny + th, color);
		} else {
			checkImage(sx, sy);
			g.drawTexture(subImages[sx][sy], x, y);
		}
	}

	public void glBegin() {
		target.glBegin();
	}

	public void glEnd() {
		target.glEnd();
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public int getSpacing() {
		return spacing;
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public GLCache newCache() {
		return target.newBatchCache();
	}

	public GLCache newCache(boolean reset) {
		return target.newBatchCache(reset);
	}

	public LTexture getTarget() {
		return target;
	}

	public void setTarget(LTexture target) {
		if (this.target != null) {
			this.target.destroy();
			this.target = null;
		}
		this.target = target;
	}

	@Override
	public void dispose() {
		if (subImages != null) {
			synchronized (subImages) {
				for (int i = 0; i < subImages.length; i++) {
					for (int j = 0; j < subImages[i].length; j++) {
						subImages[i][j].destroy();
					}
				}
				this.subImages = null;
			}
		}
		if (target != null) {
			target.destroy();
			target = null;
		}
	}
}
