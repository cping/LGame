/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
import loon.action.map.colider.Tile;
import loon.action.map.colider.TileGenerator;
import loon.action.map.colider.TileImpl;
import loon.action.map.colider.TileImplFinder;
import loon.action.map.items.Door;
import loon.action.map.items.TileRoom;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * Tile对象到地图的转化管理用类(Field2D类是处理简单的数组地图用的,这个是处理复杂的Tile对象集合的(主要是Tile对象功能上比较复杂),渲染工具为TileManager)
 */
public class Grid2D implements LRelease {

	private TileImpl[][] _tiles = null;

	private Field2D _fieldMap = null;

	private int _viewWidth;
	private int _viewHeight;
	private int _mapWidth;
	private int _mapHeight;
	private int _tileWidth;
	private int _tileHeight;

	private final TArray<Door> _doors = new TArray<Door>();
	private final TileGenerator _tileGenerator;

	public Grid2D(int mapWidth, int mapHeight) {
		this(mapWidth, mapHeight, null);
	}

	public Grid2D(int mapWidth, int mapHeight, TileGenerator generator) {
		this(mapWidth, mapHeight, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, generator);
	}

	public Grid2D(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {
		this(mapWidth, mapHeight, tileWidth, tileHeight, null);
	}

	public Grid2D(int mapWidth, int mapHeight, int tileWidth, int tileHeight, TileGenerator generator) {
		if (mapWidth <= 0 || mapHeight <= 0) {
			throw new IllegalArgumentException("Cannot create grid with 0 or negative size !");
		}
		if (tileWidth < 0 || tileHeight < 0) {
			throw new IllegalArgumentException("Cannot create grid with tiles of negative size !");
		}
		this._tileGenerator = generator;
		this.calcDefaultMap(mapWidth, mapHeight, tileWidth, tileHeight);
	}

	public boolean isGenerated() {
		int id = -1;
		for (int x = 0; x < _mapWidth; x++)
			for (int y = 0; y < _mapHeight; y++)
				if (id == -1) {
					id = get(x, y).getId();
				} else if (id != get(x, y).getId()) {
					return false;
				}

		return true;
	}

	public Grid2D calcDefaultMap() {
		return calcDefaultMap(this._tileWidth, this._tileHeight);
	}

	public Grid2D calcDefaultMap(int tileWidth, int tileHeight) {
		return calcDefaultMap(this._mapWidth, this._mapHeight, tileWidth, tileHeight);
	}

	public Grid2D calcDefaultMap(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {
		this._mapWidth = mapWidth;
		this._mapHeight = mapHeight;
		this._tileWidth = tileWidth;
		this._tileHeight = tileHeight;
		this._viewWidth = mapWidth * tileWidth;
		this._viewHeight = mapHeight * tileHeight;
		this._tiles = new TileImpl[mapWidth][mapHeight];
		if (_tileGenerator != null) {
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					set(x, y, _tileGenerator.apply(x, y));
				}
			}
		} else {
			int count = 0;
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					set(x, y, new TileImpl(count++, x, y));
				}
			}
		}
		return this;
	}

	public TileGenerator getTileGenerator() {
		return this._tileGenerator;
	}

	public Door getDoor(TileImpl tileA, TileImpl tileB) {
		return getDoor(tileA.getTileRoom(), tileB.getTileRoom());
	}

	public Door getDoor(TileRoom tileA, TileRoom tileB) {
		for (int i = _doors.size - 1; i > -1; i--) {
			Door door = _doors.get(i);
			if (door.getRoom1() == tileA && door.getRoom2() == tileB
					|| door.getRoom1() == tileB && door.getRoom2() == tileA) {
				return door;
			}
		}
		return null;
	}

	public Grid2D generateMap() {
		while (!isGenerated()) {
			final int dir = MathUtils.random(Config.TLEFT, Config.TDOWN);
			final Vector2f c0 = new Vector2f(MathUtils.random(0, _mapWidth - 1), MathUtils.random(0, _mapHeight - 1));
			final Vector2f c1 = c0.add(Field2D.getDirection(dir));
			if (c1.x >= 0 && c1.x < _mapWidth && c1.y >= 0 && c1.y < _mapHeight) {
				final TileImpl cc0 = get(c0);
				final TileImpl cc1 = get(c1);
				if (cc0.getId() != cc1.getId()) {
					Door door = getDoor(cc0, cc1);
					if (door == null) {
						door = new Door(cc0.getTileRoom(), cc1.getTileRoom());
						_doors.add(door);
						extendRegion(cc1.getId(), cc0.getId(), c1);
					}
					door.setOpen(true);
				}
			}
		}
		return this;
	}

	protected void extendRegion(int fromId, int toId, Vector2f w) {
		if (w.x < 0 || w.x >= _mapWidth || w.y < 0 || w.y >= _mapHeight) {
			return;
		}
		TileImpl c = get(w);
		if (c.getId() != fromId) {
			return;
		}
		c.setId(toId);
		for (int i = Config.TLEFT; i <= Config.TDOWN; i++) {
			Vector2f p = Field2D.getDirection(i);
			extendRegion(fromId, toId, w.add(p));
		}
	}

	public TArray<Door> getDoors() {
		return new TArray<Door>(_doors);
	}

	public int getMapWidth() {
		return _mapWidth;
	}

	public int getMapHeight() {
		return _mapHeight;
	}

	public int getWidth() {
		return _viewWidth;
	}

	public int getHeight() {
		return _viewHeight;
	}

	public int getTileWidth() {
		return _tileWidth;
	}

	public int getTileHeight() {
		return _tileHeight;
	}

	public int toTileX(float x) {
		return MathUtils.floor(x / _tileHeight);
	}

	public int toTileY(float y) {
		return MathUtils.floor(y / _tileHeight);
	}

	public int toPixelX(float x) {
		return MathUtils.floor(x * _tileWidth);
	}

	public int toPixelY(float y) {
		return MathUtils.floor(y * _tileHeight);
	}

	public boolean isWithin(float x, float y) {
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}

	public TileImpl get(XY pos) {
		return get((int) pos.getX(), (int) pos.getY());
	}

	public TileImpl get(int x, int y) {
		return _tiles[x][y];
	}

	public Grid2D set(int x, int y, TileImpl node) {
		if (node != null) {
			node.setWidth(_tileWidth);
			node.setHeight(_tileHeight);
			_tiles[x][y] = node;
		}
		return this;
	}

	public TileImpl getRight(Tile tile) {
		return getRight(tile.getX(), tile.getY());
	}

	public TileImpl getLeft(Tile tile) {
		return getLeft(tile.getX(), tile.getY());
	}

	public TileImpl getUp(Tile tile) {
		return getUp(tile.getX(), tile.getY());
	}

	public TileImpl getDown(Tile tile) {
		return getDown(tile.getX(), tile.getY());
	}

	public TileImpl getRightUp(Tile tile) {
		return getRightUp(tile.getX(), tile.getY());
	}

	public TileImpl getLeftUp(Tile tile) {
		return getLeftUp(tile.getX(), tile.getY());
	}

	public TileImpl getRightDown(Tile tile) {
		return getRightDown(tile.getX(), tile.getY());
	}

	public TileImpl getLeftDown(Tile tile) {
		return getLeftDown(tile.getX(), tile.getY());
	}

	public TileImpl getRight(int x, int y) {
		return getNotNull(x + 1, y);
	}

	public TileImpl getLeft(int x, int y) {
		return getNotNull(x - 1, y);
	}

	public TileImpl getUp(int x, int y) {
		return getNotNull(x, y - 1);
	}

	public TileImpl getDown(int x, int y) {
		return getNotNull(x, y + 1);
	}

	public TileImpl getRightUp(int x, int y) {
		return getNotNull(x + 1, y + 1);
	}

	public TileImpl getLeftUp(int x, int y) {
		return getNotNull(x - 1, y + 1);
	}

	public TileImpl getRightDown(int x, int y) {
		return getNotNull(x + 1, y - 1);
	}

	public TileImpl getLeftDown(int x, int y) {
		return getNotNull(x - 1, y - 1);
	}

	public TileImpl getRandomTile() {
		int x = MathUtils.nextInt(getWidth() - 1);
		int y = MathUtils.nextInt(getHeight() - 1);
		return get(x, y);
	}

	private TileImpl getNotNull(int x, int y) {
		if (isWithin(x, y)) {
			return get(x, y);
		}
		return null;
	}

	public TArray<TileImpl> getNeighbors(int x, int y) {
		return getNeighbors(x, y, false);
	}

	public TArray<TileImpl> getNeighbors(int x, int y, boolean all) {
		TArray<TileImpl> result = new TArray<TileImpl>();
		TileImpl left = getLeft(x, y);
		TileImpl right = getRight(x, y);
		TileImpl up = getUp(x, y);
		TileImpl down = getDown(x, y);
		if (left != null) {
			result.add(left);
		}
		if (right != null) {
			result.add(right);
		}
		if (up != null) {
			result.add(up);
		}
		if (down != null) {
			result.add(down);
		}
		if (all) {
			TileImpl leftUp = getLeftUp(x, y);
			TileImpl rightUp = getRightUp(x, y);
			TileImpl leftDown = getLeftDown(x, y);
			TileImpl rightDown = getRightUp(x, y);
			if (leftUp != null) {
				result.add(leftUp);
			}
			if (rightUp != null) {
				result.add(rightUp);
			}
			if (leftDown != null) {
				result.add(leftDown);
			}
			if (rightDown != null) {
				result.add(rightDown);
			}
		}
		return result;
	}

	public TArray<TileImpl> getTiles() {
		TArray<TileImpl> list = new TArray<TileImpl>();
		for (int x = 0; x < _mapWidth; x++) {
			for (int y = 0; y < _mapHeight; y++) {
				list.add(get(x, y));
			}
		}
		return list;
	}

	public Field2D getField2D() {
		if (_fieldMap == null) {
			_fieldMap = new Field2D(this._mapWidth, this._mapHeight, this._tileWidth, this._tileHeight);
		}
		for (int x = 0; x < _mapWidth; x++) {
			for (int y = 0; y < _mapHeight; y++) {
				final TileImpl impl = get(x, y);
				if (impl != null) {
					_fieldMap.setTileType(x, y, impl.getId());
				}
			}
		}
		return _fieldMap;
	}

	public Grid2D setFromField2D(Field2D f) {
		for (int x = 0; x < _mapWidth; x++) {
			for (int y = 0; y < _mapHeight; y++) {
				final TileImpl impl = get(x, y);
				if (impl != null) {
					impl.setId(f.getTileType(x, y));
				}
			}
		}
		return this;
	}

	public TileImpl[][] getData() {
		return _tiles;
	}

	public TileImplFinder getFinder() {
		return new TileImplFinder(this);
	}

	public TileImplFinder getFinder(boolean all) {
		return new TileImplFinder(this, all);
	}

	public TileImplFinder getFinder(AStarFindHeuristic heuristic, boolean all) {
		return new TileImplFinder(this, heuristic, all);
	}

	@Override
	public void close() {
		_tiles = null;
		_doors.clear();
	}

}
