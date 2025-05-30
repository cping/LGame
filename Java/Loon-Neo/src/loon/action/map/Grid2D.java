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

import loon.LSystem;
import loon.action.map.colider.Tile;
import loon.action.map.colider.TileGenerator;
import loon.action.map.colider.TileImpl;
import loon.action.map.colider.TileImplFinder;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * Tile对象到地图的转化管理用类(Field2D类是处理简单的数组地图用的,这个是处理复杂的Tile对象集合的(主要是Tile对象功能上比较复杂),需要和TileManager配合使用)
 */
public class Grid2D {

	private TileImpl[][] data = null;

	private Field2D fieldMap = null;

	private int width;
	private int height;
	private int mapWidth;
	private int mapHeight;
	private int tileWidth;
	private int tileHeight;

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

		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.width = mapWidth * tileWidth;
		this.height = mapHeight * tileHeight;

		this.data = new TileImpl[width][height];
		if (generator != null) {
			for (int y = 0; y < data[0].length; y++) {
				for (int x = 0; x < data.length; x++) {
					set(x, y, generator.apply(x, y));
				}
			}
		}
	}

	public Grid2D calcDefaultMap() {
		return calcDefaultMap(this.tileWidth, this.tileHeight);
	}

	public Grid2D calcDefaultMap(int tileWidth, int tileHeight) {
		return calcDefaultMap(this.width / tileWidth, this.height / tileHeight, tileWidth, tileHeight);
	}

	public Grid2D calcDefaultMap(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {

		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		this.width = (tileWidth * mapWidth);
		this.height = (tileHeight * mapHeight);

		this.data = new TileImpl[mapWidth][mapHeight];
		for (int x = 0; x < mapWidth; ++x) {
			for (int y = 0; y < mapHeight; ++y) {
				this.data[x][y] = new TileImpl(0, x, y, tileWidth, tileHeight);
			}
		}
		return this;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int toTileX(float x) {
		return MathUtils.floor(x / tileHeight);
	}

	public int toTileY(float y) {
		return MathUtils.floor(y / tileHeight);
	}

	public int toPixelX(float x) {
		return MathUtils.floor(x * tileWidth);
	}

	public int toPixelY(float y) {
		return MathUtils.floor(y * tileHeight);
	}

	public boolean isWithin(float x, float y) {
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}

	public TileImpl get(int x, int y) {
		return data[x][y];
	}

	public Grid2D set(int x, int y, TileImpl node) {
		node.setWidth(tileWidth);
		node.setHeight(tileHeight);
		data[x][y] = node;
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
		TArray<TileImpl> tiles = new TArray<TileImpl>();
		for (int y = 0; y < data[0].length; y++) {
			for (int x = 0; x < data.length; x++) {
				tiles.add(get(x, y));
			}
		}
		return tiles;
	}

	public Field2D getField2D() {
		if (fieldMap == null) {
			fieldMap = new Field2D(this.mapWidth, this.mapHeight, this.tileWidth, this.tileHeight);
		}
		for (int x = 0; x < fieldMap.getWidth(); x++) {
			for (int y = 0; y < fieldMap.getHeight(); y++) {
				TileImpl impl = get(x, y);
				if (impl != null) {
					fieldMap.setTileType(x, y, impl.getId());
				}
			}
		}
		return fieldMap;
	}

	public TileImpl[][] getData() {
		return data;
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

}
