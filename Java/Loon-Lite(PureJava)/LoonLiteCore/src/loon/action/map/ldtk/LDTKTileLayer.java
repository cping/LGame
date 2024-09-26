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
package loon.action.map.ldtk;

import loon.Json;
import loon.LObject;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.action.map.TileMapCollision;
import loon.events.ChangeEvent;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class LDTKTileLayer extends LDTKLayer implements TileMapCollision, ChangeEvent<LDTKTile>, LRelease {

	private ActionBind _follow;

	private RectBox _tileLayerRect;

	private Vector2f _offset = new Vector2f();

	private LDTKTile[] _mapTiles;

	private LTexture _mapTexture;

	private LDTKMap _map;

	private LDTKLevel _level;

	private Field2D _mapToArray;

	private final PointF _scrollDrag;

	private int _notSetTileID;

	private int _cellsTilesPerRow;

	private int _cellsTilesPerCol;

	private IntArray _limits;

	private boolean _roll;

	public LDTKTileLayer(LDTKMap map, LDTKLevel level, Json.Object v, boolean intGrid) {
		super(v);
		this._map = map;
		this._level = level;
		this._limits = new IntArray();
		this._scrollDrag = new PointF();
		this._notSetTileID = -1;
		String tilesetRelPath = v.getString("__tilesetRelPath");
		if (!StringUtils.isEmpty(map.getDir()) && tilesetRelPath.indexOf(map.getDir()) == -1) {
			tilesetRelPath = map.getDir() + LSystem.FS + tilesetRelPath;
		}
		this._mapTexture = LTextures.loadTexture(tilesetRelPath);
		this._widthInPixels = level.getWidth();
		this._heightInPixels = level.getHeight();
		this._cellsTilesPerRow = _widthInPixels / _gridSize.x;
		this._cellsTilesPerCol = _heightInPixels / _gridSize.y;
		Json.Array tiles = v.getArray(intGrid ? "autoLayerTiles" : "gridTiles");
		this._mapTiles = new LDTKTile[tiles.length()];
		for (int i = 0; i < tiles.length(); i++) {
			Json.Object tileValue = tiles.getObject(i);
			Json.Array pixelPosition = tileValue.getArray("px");
			Json.Array sourcePosition = tileValue.getArray("src");
			final int flipFlags = tileValue.getInt("f");
			final int typeFlag = tileValue.getInt("t");
			boolean flipX = false, flipY = false;
			switch (flipFlags) {
			case 0:
				flipX = flipY = false;
				break;
			case 1:
				flipX = true;
				flipY = false;
				break;
			case 2:
				flipX = false;
				flipY = true;
				break;
			default:
				flipX = flipY = true;
				break;
			}
			float posX = sourcePosition.getNumber(0);
			float posY = sourcePosition.getNumber(1);
			LTexture tile = _mapTexture.sub(posX, posY, _gridSize.x, _gridSize.y);
			float pixelX = pixelPosition.getNumber(0);
			float pixelY = pixelPosition.getNumber(1);
			int tx = MathUtils.iceil(pixelX / _gridSize.x);
			int ty = MathUtils.iceil(pixelY / _gridSize.y);
			int tileId = ty * _cellsTilesPerRow + tx;
			_mapTiles[i] = new LDTKTile(this, tileId, typeFlag, tile, pixelX, pixelY, _gridSize.x, _gridSize.y, tx, ty);
			_mapTiles[i].flip(flipX, flipY);
			if (!_limits.contains(typeFlag)) {
				_limits.add(typeFlag);
			}
		}
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		if (this._roll) {
			this._offset = this.toRollPosition(this._offset);
		}
		final float old = g.alpha();
		g.setAlpha(_opacity);
		final float offX = _pixelOffsetX + _offset.x() + offsetX;
		final float offY = _pixelOffsetY + _offset.y() + offsetY;
		for (int i = _mapTiles.length - 1; i > -1; i--) {
			LDTKTile tile = _mapTiles[i];
			if (tile != null && tile.isVisible()) {
				g.draw(tile.getTexture(), tile.getX() + offX, tile.getY() + offY, tile.getDirection());
			}
		}
		g.setAlpha(old);
	}

	public boolean contains(XY point) {
		if (point == null) {
			return false;
		}
		return contains(point.getX(), point.getY());
	}

	public boolean contains(float x, float y) {
		return _tileLayerRect.contains(x, y);
	}

	public RectBox getCollisionBox() {
		if (_tileLayerRect == null) {
			_tileLayerRect = new RectBox(_pixelOffsetX + _offset.x, _pixelOffsetY + _offset.y, _widthInPixels,
					_heightInPixels);
		} else {
			_tileLayerRect.set(_pixelOffsetX + _offset.x, _pixelOffsetY + _offset.y, _widthInPixels, _heightInPixels);
		}
		return _tileLayerRect;
	}

	public int[] getMapTypes() {
		return _limits.toArray();
	}

	public LDTKTileLayer addLimitToField2D() {
		getField2D().setLimit(_limits.toArray());
		return this;
	}

	public LDTKTileLayer removeLimitToField2D() {
		getField2D().setLimit((int[])null);
		return this;
	}

	public LDTKTile findTileById(int id) {
		if (_mapTiles != null) {
			for (int i = _mapTiles.length - 1; i > -1; i--) {
				LDTKTile tile = _mapTiles[i];
				if (tile.getId() == id) {
					return tile;
				}
			}
		}
		return null;
	}

	public LDTKTile findTileByFlagId(int flagid) {
		if (_mapTiles != null) {
			for (int i = _mapTiles.length - 1; i > -1; i--) {
				LDTKTile tile = _mapTiles[i];
				if (tile.getTypeIdFlag() == flagid) {
					return tile;
				}
			}
		}
		return null;
	}

	public int getPixelsAtFieldType(float x, float y) {
		if (_mapTiles == null || _mapTiles.length == 0) {
			return -1;
		}
		return getField2D().getPixelsAtFieldType(x, y);
	}

	public int getTilesAtFieldType(int x, int y) {
		if (_mapTiles == null || _mapTiles.length == 0) {
			return -1;
		}
		return getField2D().getTileType(x, y);
	}

	public LDTKTile getPixelsAtTile(float x, float y) {
		if (_mapTiles == null || _mapTiles.length == 0) {
			return null;
		}
		for (int i = _mapTiles.length - 1; i > -1; i--) {
			LDTKTile tile = _mapTiles[i];
			if (tile.contains(x, y)) {
				return tile;
			}
		}
		return null;
	}

	public LDTKTile getTilePosAtTile(int x, int y) {
		if (_mapTiles == null || _mapTiles.length == 0) {
			return null;
		}
		for (int i = _mapTiles.length - 1; i > -1; i--) {
			LDTKTile tile = _mapTiles[i];
			if (tile.getTileX() == x && tile.getTileY() == y) {
				return tile;
			}
		}
		return null;
	}

	@Override
	public Field2D getField2D() {
		if (_mapToArray == null) {
			_mapToArray = new Field2D(_cellsTilesPerRow, _cellsTilesPerCol, _gridSize.x, _gridSize.y, _notSetTileID);
		}
		if (_dirty) {
			_mapToArray.fill(_notSetTileID);
			_mapToArray.setSize(_cellsTilesPerRow, _cellsTilesPerCol);
			_mapToArray.setTile(_gridSize.x, _gridSize.y);
			for (int i = _mapTiles.length - 1; i > -1; i--) {
				LDTKTile tile = _mapTiles[i];
				if (tile.isVisible()) {
					_mapToArray.setTileType(tile.getTileX(), tile.getTileY(), tile.getTypeIdFlag());
				}
			}
			_dirty = false;
		}
		return _mapToArray;
	}

	public int getCellsTilesPerRow() {
		return _cellsTilesPerRow;
	}

	public int getCellsTilesPerCol() {
		return _cellsTilesPerCol;
	}

	public float centerX() {
		return ((_map.getX() + _map.getContainerX() + _map.getContainerWidth()) - (getPixelOffsetX() + getViewWidth()))
				* 0.5f;
	}

	public float centerY() {
		return (_map.getY() + _map.getContainerY() + _map.getContainerHeight() - (getPixelOffsetY() + getViewHeight()))
				* 0.5f;
	}

	public LDTKTileLayer scrollDown(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min((this._offset.y + distance),
				(MathUtils.max(0, _map.getContainerHeight() - this.getViewHeight())));
		if (this._offset.y >= 0) {
			this._offset.y = 0;
		}
		return this;
	}

	public LDTKTileLayer scrollLeft(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min(this._offset.x - distance, this.getPixelOffsetX());
		float limitX = (_map.getContainerWidth() - this.getViewWidth());
		if (this._offset.x <= limitX) {
			this._offset.x = limitX;
		}
		return this;
	}

	public LDTKTileLayer scrollRight(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min((this._offset.x + distance),
				(MathUtils.max(0, this.getViewWidth() - _map.getContainerWidth())));
		if (this._offset.x >= 0) {
			this._offset.x = 0;
		}
		return this;
	}

	public LDTKTileLayer scrollUp(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min(this._offset.y - distance, 0);
		float limitY = (_map.getContainerHeight() - this.getViewHeight());
		if (this._offset.y <= limitY) {
			this._offset.y = limitY;
		}
		return this;
	}

	public LDTKTileLayer scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public LDTKTileLayer scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public LDTKTileLayer scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public LDTKTileLayer scroll(float x, float y) {
		return scroll(x, y, 4f);
	}

	public LDTKTileLayer scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public LDTKTileLayer scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 4f);
	}

	public LDTKTileLayer scroll(float x1, float y1, float x2, float y2, float distance) {
		if (this._follow != null) {
			return this;
		}
		if (x1 < x2 && x1 > centerX()) {
			scrollRight(distance);
		} else if (x1 > x2) {
			scrollLeft(distance);
		}
		if (y1 < y2 && y1 > centerY()) {
			scrollDown(distance);
		} else if (y1 > y2) {
			scrollUp(distance);
		}
		_scrollDrag.set(x2, y2);
		return this;
	}

	public LDTKMap getLDTKMap() {
		return _map;
	}

	public LDTKLevel getLevel() {
		return _level;
	}

	public LDTKTile[] getTiles() {
		return _mapTiles;
	}

	public LTexture getTilemapTexture() {
		return _mapTexture;
	}

	public boolean isRoll() {
		return _roll;
	}

	public LDTKTileLayer setRoll(boolean roll) {
		this._roll = roll;
		return this;
	}

	public Vector2f toRollPosition(Vector2f pos) {
		pos.x = pos.x % _widthInPixels;
		pos.y = pos.y % _heightInPixels;
		if (pos.x < 0f) {
			pos.x += _widthInPixels;
		}
		if (pos.x < 0f) {
			pos.y += _heightInPixels;
		}
		return pos;
	}

	protected float limitOffsetX(float newOffsetX) {
		float offsetX = _map.getContainerWidth() / 2 - newOffsetX;
		offsetX = MathUtils.min(offsetX, 0);
		offsetX = MathUtils.max(offsetX, _map.getContainerWidth() - getViewWidth());
		return offsetX;
	}

	protected float limitOffsetY(float newOffsetY) {
		float offsetY = _map.getContainerHeight() / 2 - newOffsetY;
		offsetY = MathUtils.min(offsetY, 0);
		offsetY = MathUtils.max(offsetY, _map.getContainerHeight() - getViewHeight());
		return offsetY;
	}

	public LDTKTileLayer followActionObject() {
		if (_follow != null) {
			float offsetX = limitOffsetX(_follow.getX());
			float offsetY = limitOffsetY(_follow.getY());
			if (offsetX != 0 || offsetY != 0) {
				setOffset(offsetX, offsetY);
				getField2D().setOffset(_offset);
			}
		}
		return this;
	}

	public LDTKTileLayer setNotSetTileId(int id) {
		this._notSetTileID = id;
		this._dirty = true;
		return this;
	}

	public int getNotSetTileId() {
		return this._notSetTileID;
	}

	@Override
	public Vector2f getTileCollision(LObject<?> o, float newX, float newY) {
		return getField2D().getTileCollision(o.getX(), o.getY(), o.getWidth(), o.getHeight(), newX, newY);
	}

	@Override
	public int tilesToPixelsX(float x) {
		return MathUtils.ifloor(x * _gridSize.x);
	}

	@Override
	public int tilesToPixelsY(float y) {
		return MathUtils.ifloor(y * _gridSize.y);
	}

	@Override
	public int pixelsToTilesWidth(float x) {
		return MathUtils.ifloor(x / _gridSize.x);
	}

	@Override
	public int pixelsToTilesHeight(float y) {
		return MathUtils.ifloor(y / _gridSize.y);
	}

	@Override
	public boolean isHit(int px, int py) {
		return getField2D().isHit(px, py);
	}

	@Override
	public boolean isPixelHit(int px, int py) {
		return isPixelHit(px, py, 0, 0);
	}

	@Override
	public boolean isPixelTUp(int px, int py) {
		return isPixelHit(px, py, 0, -1);
	}

	@Override
	public boolean isPixelTRight(int px, int py) {
		return isPixelHit(px, py, 1, 0);
	}

	@Override
	public boolean isPixelTLeft(int px, int py) {
		return isPixelHit(px, py, -1, 0);
	}

	@Override
	public boolean isPixelTDown(int px, int py) {
		return isPixelHit(px, py, 0, 1);
	}

	public boolean isPixelHit(int px, int py, int movePx, int movePy) {
		Field2D arrayMap = getField2D();
		return isHit(arrayMap.pixelsToTilesWidth(arrayMap.offsetXPixel(px)) + movePx,
				arrayMap.pixelsToTilesHeight(arrayMap.offsetYPixel(py)) + movePy);
	}

	@Override
	public void onChange(LDTKTile v) {
		this._dirty = true;
	}

	public LDTKTileLayer centerOffset() {
		this._offset.set(centerX(), centerY());
		return this;
	}

	public LDTKTileLayer setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	public LDTKTileLayer setOffset(Vector2f o) {
		if (o == null) {
			return this;
		}
		this._offset = o;
		return this;
	}

	@Override
	public Vector2f getOffset() {
		return _offset;
	}

	@Override
	public int getTileWidth() {
		return _gridSize.x;
	}

	@Override
	public int getTileHeight() {
		return _gridSize.y;
	}

	@Override
	public int getRow() {
		return _cellsTilesPerRow;
	}

	@Override
	public int getCol() {
		return _cellsTilesPerCol;
	}

	@Override
	public int[][] getMap() {
		return getField2D().getMap();
	}

	@Override
	public void close() {
		if (_mapTexture != null) {
			_mapTexture.close(true);
		}
		_dirty = true;
	}

}
