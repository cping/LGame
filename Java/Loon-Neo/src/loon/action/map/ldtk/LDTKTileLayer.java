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
import loon.action.map.Field2D;
import loon.action.map.TileMapCollision;
import loon.events.ChangeEvent;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class LDTKTileLayer extends LDTKLayer implements TileMapCollision, ChangeEvent<LDTKTile>, LRelease {

	private Vector2f _offect = new Vector2f();

	private LDTKTile[] _mapTiles;

	private LTexture _mapTexture;

	private LDTKMap _map;

	private LDTKLevel _level;

	private Field2D _mapToArray;

	private int _cellsTilesPerRow;

	private int _cellsTilesPerCol;

	private IntArray _limits;

	protected boolean _dirty;

	public LDTKTileLayer(LDTKMap map, LDTKLevel level, Json.Object v, boolean intGrid) {
		super(v);
		this._map = map;
		this._level = level;
		this._limits = new IntArray();
		String tilesetRelPath = v.getString("__tilesetRelPath");
		if (!StringUtils.isEmpty(map.getDir()) && tilesetRelPath.indexOf(map.getDir()) == -1) {
			tilesetRelPath = map.getDir() + LSystem.FS + tilesetRelPath;
		}
		this._mapTexture = LTextures.loadTexture(tilesetRelPath);
		this._cellsTilesPerRow = level.getWidth() / _gridSize;
		this._cellsTilesPerCol = level.getHeight() / _gridSize;
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
			case 2:
				flipX = false;
				flipY = true;
			default:
				flipX = flipY = true;
				break;
			}
			float posX = sourcePosition.getNumber(0);
			float posY = sourcePosition.getNumber(1);
			LTexture tile = _mapTexture.sub(posX, posY, _gridSize, _gridSize);
			float pixelX = pixelPosition.getNumber(0);
			float pixelY = pixelPosition.getNumber(1);
			int tx = MathUtils.iceil(pixelX / _gridSize);
			int ty = MathUtils.iceil(pixelY / _gridSize);
			int tileId = ty * _cellsTilesPerRow + tx;
			_mapTiles[i] = new LDTKTile(this, tileId, typeFlag, tile, pixelX, pixelY, _gridSize, _gridSize, tx, ty);
			_mapTiles[i].flip(flipX, flipY);
			if (!_limits.contains(typeFlag)) {
				_limits.add(typeFlag);
			}
		}
		this._dirty = true;
	}

	public void draw(GLEx g) {
		draw(g, 0, 0);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		final float old = g.alpha();
		g.setAlpha(_opacity);
		for (int i = _mapTiles.length - 1; i > -1; i--) {
			LDTKTile tile = _mapTiles[i];
			if (tile != null && tile.isVisible()) {
				g.draw(tile.getTexture(), tile.getX() + offsetX, tile.getY() + offsetY, tile.getDirection());
			}
		}
		g.setAlpha(old);
	}

	public int[] getMapTypes() {
		return _limits.toArray();
	}

	public LDTKTileLayer addLimitToField2D() {
		getField2D().setLimit(_limits.toArray());
		return this;
	}

	public LDTKTileLayer removeLimitToField2D() {
		getField2D().setLimit(null);
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
		if (_mapTiles.length == 0) {
			return -1;
		}
		return getField2D().getPixelsAtFieldType(x, y);
	}

	public int getTilesAtFieldType(int x, int y) {
		if (_mapTiles.length == 0) {
			return -1;
		}
		return getField2D().getTileType(x, y);
	}

	public LDTKTile getPixelsAtTile(float x, float y) {
		if (_mapTiles.length == 0) {
			return null;
		}
		if (_mapTiles != null) {
			for (int i = _mapTiles.length - 1; i > -1; i--) {
				LDTKTile tile = _mapTiles[i];
				if (tile.contains(x, y)) {
					return tile;
				}
			}
		}
		return null;
	}

	public LDTKTile getTilePosAtTile(int x, int y) {
		if (_mapTiles.length == 0) {
			return null;
		}
		if (_mapTiles != null) {
			for (int i = _mapTiles.length - 1; i > -1; i--) {
				LDTKTile tile = _mapTiles[i];
				if (tile.getTileX() == x && tile.getTileY() == y) {
					return tile;
				}
			}
		}
		return null;
	}

	@Override
	public Field2D getField2D() {
		if (_mapToArray == null) {
			_mapToArray = new Field2D(_cellsTilesPerRow, _cellsTilesPerCol, _gridSize, _gridSize);
		}
		if (_dirty) {
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

	@Override
	public Vector2f getTileCollision(LObject<?> o, float newX, float newY) {
		return getField2D().getTileCollision(o.getX(), o.getY(), o.getWidth(), o.getHeight(), newX, newY);
	}

	@Override
	public int tilesToPixelsX(float x) {
		return MathUtils.ifloor(x * _gridSize);
	}

	@Override
	public int tilesToPixelsY(float y) {
		return MathUtils.ifloor(y * _gridSize);
	}

	@Override
	public int pixelsToTilesWidth(float x) {
		return MathUtils.ifloor(x / _gridSize);
	}

	@Override
	public int pixelsToTilesHeight(float y) {
		return MathUtils.ifloor(y / _gridSize);
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

	public LDTKTileLayer setOffset(Vector2f o) {
		if (o == null) {
			return this;
		}
		this._offect = o;
		return this;
	}

	@Override
	public Vector2f getOffset() {
		return _offect;
	}

	@Override
	public int getTileWidth() {
		return _gridSize;
	}

	@Override
	public int getTileHeight() {
		return _gridSize;
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
