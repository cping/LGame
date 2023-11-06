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
package loon.action.sprite;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch.Cache;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.opengl.GLEx;

public class SpriteSheet implements LRelease {

	private int margin, spacing;

	private int tw, th;

	private int width, height;

	private LTexture[][] subImages;

	private LTexture target;

	public SpriteSheet(String fileName, int tw, int th, int s, int m) {
		this(LSystem.loadTexture(fileName), tw, th, s, m);
	}

	public SpriteSheet(String fileName, int tw, int th) {
		this(LSystem.loadTexture(fileName), tw, th, 0, 0);
	}

	public SpriteSheet(LTexture image, int tw, int th) {
		this(image, tw, th, 0, 0);
	}

	public SpriteSheet(LTexture img, int tw, int th, int s, int m) {
		this.width = (int) img.width();
		this.height = (int) img.height();
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
			Updateable update = new Updateable() {

				@Override
				public void action(Object a) {
					target.loadTexture();
				}
			};
			LSystem.load(update);
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

	public boolean contains(int x, int y) {
		if ((x < 0) || (x >= subImages.length)) {
			return false;
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			return false;
		}
		return true;
	}

	private void checkImage(int x, int y) {
		update();
		if ((x < 0) || (x >= subImages.length)) {
			throw new LSysException("SubImage out of sheet bounds " + x + "," + y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new LSysException("SubImage out of sheet bounds " + x + "," + y);
		}
	}

	public LTexture getImage(int x, int y) {
		checkImage(x, y);
		if ((x < 0) || (x >= subImages.length)) {
			throw new LSysException("SubTexture2D out of sheet bounds: " + x + "," + y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new LSysException("SubTexture2D out of sheet bounds: " + x + "," + y);
		}
		return target.copy(x * (tw + spacing) + margin, y * (th + spacing) + margin, tw, th);
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

	public void draw(GLEx g, int x, int y, int sx, int sy, LColor color) {
		if (target.isBatch()) {
			final float nx = sx * tw;
			final float ny = sy * th;
			target.draw(x, y, tw, th, nx, ny, nx + tw, ny + th, color);
		} else {
			checkImage(sx, sy);
			g.draw(subImages[sx][sy], x, y);
		}
	}

	public SpriteSheet glBegin() {
		target.glBegin();
		return this;
	}

	public SpriteSheet glEnd() {
		target.glEnd();
		return this;
	}

	public int getMargin() {
		return margin;
	}

	public SpriteSheet setMargin(int margin) {
		this.margin = margin;
		return this;
	}

	public int getSpacing() {
		return spacing;
	}

	public SpriteSheet setSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}

	public Cache newCache() {
		return target.newBatchCache();
	}

	public LTexture getTarget() {
		return target;
	}

	public SpriteSheet setTarget(LTexture target) {
		if (this.target != null) {
			this.target.close();
			this.target = null;
		}
		this.target = target;
		return this;
	}

	public int getTileWidth() {
		return tw;
	}

	public SpriteSheet setTileWidth(int tw) {
		this.tw = tw;
		return this;
	}

	public int getTileHeight() {
		return th;
	}

	public SpriteSheet setTileHeight(int th) {
		this.th = th;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public SpriteSheet setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public SpriteSheet setHeight(int height) {
		this.height = height;
		return this;
	}

	public boolean isClosed() {
		return target == null || target.isClosed();
	}

	@Override
	public void close() {
		if (subImages != null) {
			synchronized (subImages) {
				for (int i = 0; i < subImages.length; i++) {
					for (int j = 0; j < subImages[i].length; j++) {
						subImages[i][j].close();
					}
				}
				this.subImages = null;
			}
		}
		if (target != null) {
			target.close();
			target = null;
		}
	}

}
