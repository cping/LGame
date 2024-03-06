/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.map;

import loon.LRelease;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 工具用类,同时绘制多个斜角网格区域用
 */
public class TileIsoRectGrid implements LRelease {

	private TileIsoRect[][] _grids;

	private float _rotation;

	private float _angle;

	private int _rows;

	private int _cols;

	private boolean _closed;

	private boolean _visible;

	private boolean _showCoordinate;

	private LColor _color;

	private LColor _fontColor;

	private Vector2f _offset;

	private IFont _font;

	private int _pixelInWidth;

	private int _pixelInHeight;

	private int _tileWidth;

	private int _tileHeight;

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th) {
		this(row, col, px, py, tw, th, 0.5f);
	}

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th, float angle) {
		this(row, col, px, py, tw, th, angle, false);
	}

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th, float angle, boolean sin) {
		this._grids = new TileIsoRect[col][row];
		this._rows = row;
		this._cols = col;
		final float newAngle = sin ? MathUtils.sin(angle) : angle;
		final float newX = newAngle * tw;
		final float newY = newAngle * th;
		float newHalfTileWidth = 0f;
		float newHalfTileHeight = 0f;
		if (newAngle < 1f) {
			newHalfTileWidth = tw * newAngle;
			newHalfTileHeight = th * newAngle;
		} else {
			newHalfTileWidth = tw / 2f + newX / 2f;
			newHalfTileHeight = th / 2f + newY / 2f;
		}
		for (int x = 0; x < col; x++) {
			for (int y = 0; y < row; y++) {
				float hx = px + (x + y) * newHalfTileWidth;
				float hy = 0;
				if (newAngle < 1f) {
					if (MathUtils.equal(newHalfTileWidth, newHalfTileHeight)) {
						hy = py + x * newHalfTileWidth - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth > newHalfTileHeight) {
						hy = py + x * newHalfTileWidth / 2f - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth < newHalfTileHeight) {
						hy = py + x * (newHalfTileHeight * newAngle + tw / 2f) - y * newHalfTileHeight
								+ (th * row) * newAngle - newHalfTileHeight;
					}
				} else {
					if (MathUtils.equal(newHalfTileWidth, newHalfTileHeight)) {
						hy = py + x * newHalfTileWidth - y * newHalfTileHeight + (th * row) / 2f + (newY * row) / 2f
								- newHalfTileHeight;
					}else if (newHalfTileWidth > newHalfTileHeight) {
						hy = py + x * newHalfTileWidth / 2f - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth < newHalfTileHeight) {
						hy = py + x * (newHalfTileWidth*newAngle + tw/2f) - y * newHalfTileHeight + (th * row) / 2f + (newY * row) / 2f
								- newHalfTileHeight;
					}
					
				}
				_grids[x][y] = TileIsoRect.createPos(hx, hy, tw, th, angle);
			}
		}
		this._tileWidth = MathUtils.ifloor(newHalfTileWidth * 2f);
		this._tileHeight = MathUtils.ifloor(newHalfTileHeight * 2f);
		this._pixelInWidth = MathUtils.ifloor(_rows * newHalfTileWidth);
		this._pixelInHeight = MathUtils.ifloor(_cols * newHalfTileWidth);
		this._angle = angle;
		this._visible = true;
	}

	public int getTileWidth() {
		return this._tileWidth;
	}

	public int getTileHeight() {
		return this._tileHeight;
	}

	public int getPixelInWidth() {
		return this._pixelInWidth;
	}

	public int getPixelInHeight() {
		return this._pixelInHeight;
	}

	public TileIsoRect getTile(int x, int y) {
		if (x >= 0 && x < _rows && y >= 0 && y < _cols) {
			return _grids[y][x];
		}
		return null;
	}

	public TileIsoRect pixelToTile(float px, float py) {
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && rect.getRect().contains(px, py)) {
					return rect;
				}
			}
		}
		return null;
	}

	public TArray<TileIsoRect> findFlag(int flag) {
		final TArray<TileIsoRect> rects = new TArray<TileIsoRect>();
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && flag == rect.getFlag()) {
					rects.add(rect);
				}
			}
		}
		return rects;
	}

	public TArray<TileIsoRect> findTag(Object tag) {
		final TArray<TileIsoRect> rects = new TArray<TileIsoRect>();
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && (tag == rect.getTag() || tag.equals(rect.getTag()))) {
					rects.add(rect);
				}
			}
		}
		return rects;
	}

	public boolean contains(float px, float py) {
		return pixelToTile(px, py) != null;
	}

	public float getAngle() {
		return _angle;
	}

	public int getRow() {
		return this._rows;
	}

	public int getCol() {
		return this._cols;
	}

	public boolean isVisible() {
		return _visible;
	}

	public TileIsoRectGrid setVisible(boolean v) {
		this._visible = v;
		return this;
	}

	public void draw(GLEx g) {
		draw(g, 0f, 0f);
	}

	public void draw(GLEx g, float offx, float offy) {
		if (!_visible || _closed) {
			return;
		}
		final IFont font = g.getFont();
		if (_font != null) {
			g.setFont(_font);
		}
		float newX = offx;
		float newY = offy;
		if (_offset != null) {
			newX += _offset.getX();
			newY += _offset.getY();
		}
		final int color = g.color();
		final boolean saveColor = _color != null;
		if (saveColor) {
			g.setColor(_color);
		}
		final boolean updateTrans = (newX != 0f || newY != 0f);
		if (updateTrans) {
			g.translate(newX, newY);
		}
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				if (_showCoordinate) {
					_grids[x][y].draw(g, x + "," + y, _fontColor);
				} else {
					_grids[x][y].draw(g);
				}
			}
		}
		if (updateTrans) {
			g.translate(-newX, -newY);
		}
		if (saveColor) {
			g.setColor(color);
		}
		g.setFont(font);
	}

	public Vector2f getOffset() {
		return this._offset;
	}

	public TileIsoRectGrid setOffset(Vector2f offset) {
		this._offset = offset;
		return this;
	}

	public LColor getColor() {
		return _color;
	}

	public TileIsoRectGrid setColor(LColor c) {
		this._color = c;
		return this;
	}

	public LColor getFontColor() {
		return _fontColor;
	}

	public TileIsoRectGrid setFontColor(LColor c) {
		this._fontColor = c;
		return this;
	}

	public IFont getFont() {
		return _font;
	}

	public TileIsoRectGrid setFont(IFont f) {
		this._font = f;
		return this;
	}

	public TileIsoRectGrid setFill(boolean f) {
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].setFill(f);
			}
		}
		return this;
	}

	public TileIsoRectGrid setAngle(float newAngle) {
		this._angle = newAngle;
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].setAngle(newAngle);
			}
		}
		return this;
	}

	public TileIsoRectGrid setRotation(float r) {
		this._rotation = r;
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].setRatation(r);
			}
		}
		return this;
	}

	public float getRotation() {
		return _rotation;
	}

	public boolean isShowCoordinate() {
		return _showCoordinate;
	}

	public TileIsoRectGrid setShowCoordinate(boolean c) {
		this._showCoordinate = c;
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].close();
			}
		}
		_closed = true;
	}
}
