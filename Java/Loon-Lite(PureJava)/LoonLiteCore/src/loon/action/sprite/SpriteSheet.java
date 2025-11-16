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
import loon.opengl.GLEx;

public class SpriteSheet implements LRelease {

	private int _margin, _spacing;

	private int _tw, _th;

	private int _width, _height;

	private LTexture[][] _subImages;

	private LTexture _target;

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
		this._width = (int) img.width();
		this._height = (int) img.height();
		this._target = img;
		this._tw = tw;
		this._th = th;
		this._margin = m;
		this._spacing = s;
	}

	private void update() {
		if (_subImages != null) {
			return;
		}
		if (!_target.isLoaded()) {
			_target.loadTexture();
		}
		int tilesAcross = ((_width - (_margin * 2) - _tw) / (_tw + _spacing)) + 1;
		int tilesDown = ((_height - (_margin * 2) - _th) / (_th + _spacing)) + 1;
		if ((_height - _th) % (_th + _spacing) != 0) {
			tilesDown++;
		}
		_subImages = new LTexture[tilesAcross][tilesDown];
		for (int x = 0; x < tilesAcross; x++) {
			for (int y = 0; y < tilesDown; y++) {
				_subImages[x][y] = getImage(x, y);
			}
		}
	}

	public LTexture[][] getTextures() {
		return _subImages;
	}

	public boolean contains(int x, int y) {
		if ((x < 0) || (x >= _subImages.length)) {
			return false;
		}
		if ((y < 0) || (y >= _subImages[0].length)) {
			return false;
		}
		return true;
	}

	private void checkImage(int x, int y) {
		update();
		if ((x < 0) || (x >= _subImages.length)) {
			throw new LSysException("SubImage out of sheet bounds " + x + "," + y);
		}
		if ((y < 0) || (y >= _subImages[0].length)) {
			throw new LSysException("SubImage out of sheet bounds " + x + "," + y);
		}
	}

	public LTexture getImage(int x, int y) {
		checkImage(x, y);
		if ((x < 0) || (x >= _subImages.length)) {
			throw new LSysException("SubTexture2D out of sheet bounds: " + x + "," + y);
		}
		if ((y < 0) || (y >= _subImages[0].length)) {
			throw new LSysException("SubTexture2D out of sheet bounds: " + x + "," + y);
		}
		return _target.copy(x * (_tw + _spacing) + _margin, y * (_th + _spacing) + _margin, _tw, _th);
	}

	public int getHorizontalCount() {
		update();
		return _subImages.length;
	}

	public int getVerticalCount() {
		update();
		return _subImages[0].length;
	}

	public LTexture getSubImage(int x, int y) {
		checkImage(x, y);
		return _subImages[x][y];
	}

	public void draw(GLEx g, int x, int y, int sx, int sy) {
		draw(g, x, y, sx, sy, null);
	}

	public void draw(GLEx g, int x, int y, int sx, int sy, LColor color) {
		if (_target.isBatch()) {
			final float nx = sx * _tw;
			final float ny = sy * _th;
			_target.draw(x, y, _tw, _th, nx, ny, nx + _tw, ny + _th, color);
		} else {
			checkImage(sx, sy);
			g.draw(_subImages[sx][sy], x, y);
		}
	}

	public SpriteSheet glBegin() {
		_target.glBegin();
		return this;
	}

	public SpriteSheet glEnd() {
		_target.glEnd();
		return this;
	}

	public int getMargin() {
		return _margin;
	}

	public SpriteSheet setMargin(int margin) {
		this._margin = margin;
		return this;
	}

	public int getSpacing() {
		return _spacing;
	}

	public SpriteSheet setSpacing(int spacing) {
		this._spacing = spacing;
		return this;
	}

	public Cache newCache() {
		return _target.newBatchCache();
	}

	public LTexture getTarget() {
		return _target;
	}

	public SpriteSheet setTarget(LTexture target) {
		if (this._target != null) {
			this._target.close();
			this._target = null;
		}
		this._target = target;
		return this;
	}

	public int getTileWidth() {
		return _tw;
	}

	public SpriteSheet setTileWidth(int tw) {
		this._tw = tw;
		return this;
	}

	public int getTileHeight() {
		return _th;
	}

	public SpriteSheet setTileHeight(int th) {
		this._th = th;
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public SpriteSheet setWidth(int width) {
		this._width = width;
		return this;
	}

	public int getHeight() {
		return _height;
	}

	public SpriteSheet setHeight(int height) {
		this._height = height;
		return this;
	}

	public boolean isClosed() {
		return _target == null || _target.isClosed();
	}

	@Override
	public void close() {
		if (_subImages != null) {
			synchronized (_subImages) {
				for (int i = 0; i < _subImages.length; i++) {
					for (int j = 0; j < _subImages[i].length; j++) {
						_subImages[i][j].close();
					}
				}
				this._subImages = null;
			}
		}
		if (_target != null) {
			_target.close();
			_target = null;
		}
	}

}
