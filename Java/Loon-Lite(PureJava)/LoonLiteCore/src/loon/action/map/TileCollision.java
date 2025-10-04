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
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.BooleanValue;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个用于布尔值碰撞关系的瓦片地图管理类，仅处理对应坐标可碰撞或者不可碰撞
 */
public class TileCollision implements LRelease {

	private final TArray<BooleanValue> _tileCollisionList = new TArray<BooleanValue>();

	private final Vector2f _tempPos = new Vector2f();

	private final Vector2f _offset = new Vector2f();

	private LTexture _collMapCache;

	private Vector2f _tileSize;

	private Vector2f _tileCollisionSize;

	private PointI _pixelSelected;

	private Field2D _currentMap;

	private float _currentAlpha;

	private LColor _collisionColor = LColor.red.cpy();

	private LColor _notCollisionColor = LColor.blue.cpy();

	private LColor _selectedColor = LColor.white.cpy();

	private boolean _dirty = true;

	public TileCollision(String pathName, int tileWidth, int tileHeight) {
		this(new Field2D(pathName, tileWidth, tileHeight));
	}

	public TileCollision(Field2D map) {
		if (map == null) {
			final int size = LSystem.LAYER_TILE_SIZE;
			this.setMap(null, size, size, LSystem.viewSize.getWidth() / size, LSystem.viewSize.getHeight() / size);
		} else {
			this.setMap(map, map.getTileWidth(), map.getTileHeight(), map.getWidth(), map.getHeight());
		}
		this.setAlpha(0.5f);
	}

	public TileCollision(float width, float height) {
		this(null, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, width, height);
	}

	public TileCollision(Field2D map, float tileSizeW, float tileSizeH, float width, float height) {
		this.setMap(map, tileSizeW, tileSizeH, width, height);
	}

	public TileCollision setMap(Field2D map, float tileSizeW, float tileSizeH, float width, float height) {
		this._currentMap = map;
		this._tileSize = Vector2f.at(tileSizeW, tileSizeH);
		this._tileCollisionSize = Vector2f.at(width, height);
		this.setAlpha(0.5f);
		this.updateMap();
		return this;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public Field2D getFieldMap() {
		return this._currentMap;
	}

	public TileCollision updateMap() {
		_tileCollisionList.clear();
		if (_currentMap != null && _currentMap.isLimited()) {
			for (int y = 0; y < _currentMap.getHeight(); y++) {
				for (int x = 0; x < _currentMap.getWidth(); x++) {
					_tileCollisionList.add(new BooleanValue(!_currentMap.isHit(x, y)));
				}
			}
		} else {
			for (int i = MathUtils.ifloor(_tileCollisionSize.area()); i > -1; i--) {
				_tileCollisionList.add(new BooleanValue(false));
			}
		}
		_dirty = true;
		return this;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public TileCollision offset(Vector2f v) {
		_offset.set(v);
		return this;
	}

	public TileCollision offset(float px, float py) {
		_offset.set(px, py);
		return this;
	}

	public TileCollision setLimit(int... limits) {
		if (_currentMap != null) {
			_currentMap.setLimit(limits);
		}
		updateMap();
		return this;
	}

	protected void createCacheMap() {
		if (_dirty) {
			Canvas g = Image.createCanvas(getWorldWidth(), getWorldHeight());
			final int w = getWidth();
			final int h = getHeight();
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					final int tx = MathUtils.ifloor(x * _tileSize.x);
					final int ty = MathUtils.ifloor(y * _tileSize.y);
					final BooleanValue v = getTileData(x, y);
					if (v.get()) {
						g.setColor(_collisionColor);
						g.fillRect(tx, ty, _tileSize.x, _tileSize.y);
						g.setColor(_collisionColor.darker());
						g.strokeRect(tx, ty, _tileSize.x, _tileSize.y);
					} else {
						g.setColor(_notCollisionColor);
						g.fillRect(tx, ty, _tileSize.x, _tileSize.y);
						g.setColor(_notCollisionColor.darker());
						g.strokeRect(tx, ty, _tileSize.x, _tileSize.y);
					}
				}
			}
			_collMapCache = g.toTexture().setForcedDelete(true);
			_dirty = false;
		}
	}

	public void drawCollision(GLEx g) {
		this.drawCollision(g, _offset.x, _offset.y);
	}

	public void drawCollision(GLEx g, float px, float py) {
		final int oldColor = g.color();
		final int w = getWidth();
		final int h = getHeight();
		createCacheMap();
		if (_collMapCache != null) {
			g.draw(_collMapCache, px, py);
		}
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				final int tx = MathUtils.ifloor(x * _tileSize.x);
				final int ty = MathUtils.ifloor(y * _tileSize.y);
				if (_pixelSelected != null && _pixelSelected.equals(x, y)) {
					g.setColor(_selectedColor);
					g.drawOval(px + tx + 2f, py + ty + 2f, _tileSize.x - 4f, _tileSize.y - 4f);
				}
			}
		}
		g.setColor(oldColor);
	}

	public TileCollision onTouch(float x, float y) {
		final int newX = MathUtils.ifloor((x - _offset.x) / _tileSize.x);
		final int newY = MathUtils.ifloor((y - _offset.y) / _tileSize.y);
		if (this._pixelSelected == null) {
			this._pixelSelected = new PointI(newX, newY);
		} else {
			this._pixelSelected.set(newX, newY);
		}
		_dirty = true;
		return this;
	}

	public TileCollision onTouch(Vector2f v) {
		if (v == null) {
			return this;
		}
		return onTouch(v.x, v.y);
	}

	public TileCollision onTouch(PointI v) {
		if (v == null) {
			return this;
		}
		return onTouch(v.x, v.y);
	}

	public PointI getSelected() {
		return this._pixelSelected;
	}

	public int getWorldWidth() {
		return getWidth() * getTileWidth();
	}

	public int getWorldHeight() {
		return getHeight() * getTileHeight();
	}

	public int getWidth() {
		return this._tileCollisionSize.x();
	}

	public int getHeight() {
		return this._tileCollisionSize.y();
	}

	public int getTileWidth() {
		return this._tileSize.x();
	}

	public int getTileHeight() {
		return this._tileSize.y();
	}

	public Vector2f tileSize() {
		return _tileSize.cpy();
	}

	public boolean setPixelTileData(float px, float py, boolean data) {
		return setPixelTileData(_tempPos.set(px, py).sub(_offset).div(_tileSize), data);
	}

	public boolean setPixelTileData(Vector2f pixPos, boolean data) {
		return setTileData(pixPos.sub(_offset).div(_tileSize), data);
	}

	public boolean setTileData(float px, float py, boolean data) {
		return setTileData(_tempPos.set(px, py), data);
	}

	public boolean setTileData(Vector2f pos, boolean data) {
		if (pos.isArrayCheck(_tileCollisionSize)) {
			BooleanValue v = getTileData(pos);
			if (v != null) {
				v.set(data);
				_dirty = true;
				return true;
			}
		}
		return false;
	}

	public BooleanValue getTileData(Vector2f pos) {
		if (pos == null) {
			return null;
		}
		if (pos.isArrayCheck(_tileCollisionSize)) {
			final int idx = MathUtils.ifloor(MathUtils.max(0, pos.y) * _tileCollisionSize.x + MathUtils.max(0, pos.x));
			return _tileCollisionList.get(idx);
		} else {
			return null;
		}
	}

	public BooleanValue getTileData(float x, float y) {
		return getTileData(_tempPos.set(x, y));
	}

	public BooleanValue getPixelTileData(Vector2f pixPos) {
		return getTileData(pixPos.sub(_offset).div(_tileSize));
	}

	public BooleanValue getPixelTileData(float x, float y) {
		return getTileData(_tempPos.set(x, y).sub(_offset).div(_tileSize));
	}

	public float getAlpha() {
		return _currentAlpha;
	}

	public TileCollision setAlpha(float a) {
		this._currentAlpha = a;
		this._collisionColor.setAlpha(a);
		this._notCollisionColor.setAlpha(a);
		_dirty = true;
		return this;
	}

	public LColor getCollisionColor() {
		return _collisionColor;
	}

	public TileCollision setCollisionColor(LColor c) {
		if (c == null) {
			return this;
		}
		this._collisionColor = c.cpy().setAlpha(_currentAlpha);
		_dirty = true;
		return this;
	}

	public LColor getNotCollisionColor() {
		return _notCollisionColor;
	}

	public TileCollision setNotCollisionColor(LColor c) {
		if (c == null) {
			return this;
		}
		this._notCollisionColor = c.cpy().setAlpha(_currentAlpha);
		_dirty = true;
		return this;
	}

	@Override
	public void close() {
		_tileCollisionList.clear();
		if (_currentMap != null) {
			_currentMap.close();
			_currentMap = null;
		}
		if (_collMapCache != null) {
			_collMapCache.close();
			_collMapCache = null;
		}
	}

}
