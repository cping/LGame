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
package loon.action.map.colider;

import loon.LSystem;
import loon.action.map.AStarFindHeuristic;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

/**
 * 不规则地图的瓦片管理器(即地图最大长宽不统一的地图)
 */
public class TileManager {

	public static interface TileDrawListener<T> {

		public void update(long elapsedTime, T tile);

		public void draw(GLEx g, T tile, float x, float y);

	}

	public static final int DEF_RATE = 5;

	public static final int DEF_SOLID = 1;

	public static final float DEF_TILE_SCALE = 1f;

	private final ObjectMap<Integer, ObjectMap<Integer, TileImpl>> tilesX;

	private final AStarFindHeuristic heuristic;

	private int rate = DEF_RATE;

	private int solid = DEF_SOLID;

	private float tileScale = DEF_TILE_SCALE;

	private int width, height;

	private IntMap<Integer> limits = new IntMap<Integer>();

	private TileDrawListener<TileImpl> listener;

	public TileManager(AStarFindHeuristic h, int maxX, int maxY) {
		this(h, maxX, maxY, DEF_TILE_SCALE);
	}

	public TileManager(AStarFindHeuristic h, int maxX, int maxY, float ts) {
		this(h, maxX, maxY, DEF_RATE, DEF_SOLID, ts);
	}

	public TileManager(AStarFindHeuristic h, int maxX, int maxY, int r, int s, float ts) {
		this.tilesX = new ObjectMap<Integer, ObjectMap<Integer, TileImpl>>(32);
		this.width = maxX;
		this.height = maxY;
		this.heuristic = h;
		this.rate = r;
		this.solid = s;
		this.tileScale = ts;
	}

	public void put(TileImpl tile) {
		if (tile != null) {
			tile.calcNeighbours(width, height);
			ObjectMap<Integer, TileImpl> tilesY = tilesX.get(tile.getX());
			if (tilesY == null) {
				tilesY = new ObjectMap<Integer, TileImpl>(32);
				tilesX.put(tile.getX(), tilesY);
			}
			tilesY.put(tile.getY(), tile);
		}
	}

	public void remove(TileImpl tile) {
		ObjectMap<Integer, TileImpl> tilesY = tilesX.get(tile.getX());
		if (tilesY == null) {
			tilesY = new ObjectMap<Integer, TileImpl>(32);
			tilesX.put(tile.getX(), tilesY);
		}
		tilesY.remove(tile.getY());
	}

	public TileImpl getTile(int x, int y) {
		ObjectMap<Integer, TileImpl> tilesY = tilesX.get(x);
		if (tilesY != null) {
			return tilesY.get(y);
		}
		return null;
	}

	public TArray<TileImpl> getTileXArray(int x) {
		ObjectMap<Integer, TileImpl> tilesY = tilesX.get(x);
		if (tilesY != null) {
			TArray<TileImpl> tiles = new TArray<TileImpl>(tilesY.size());
			for (TileImpl tile : tilesY.values()) {
				tiles.add(tile);
			}
			return tiles;
		}
		return null;
	}

	public IntMap<Integer> getLimits() {
		return this.limits;
	}

	public TileManager putLimit(int x, int y, Integer value) {
		int tileCode = 1;
		tileCode = LSystem.unite(tileCode, x);
		tileCode = LSystem.unite(tileCode, y);
		limits.put(tileCode, value);
		return this;
	}

	public int getLimit(int x, int y) {
		int tileCode = 1;
		tileCode = LSystem.unite(tileCode, x);
		tileCode = LSystem.unite(tileCode, y);
		Integer v = limits.get(tileCode);
		return v == null ? -1 : v.intValue();
	}

	public int getRate() {
		return rate;
	}

	public int getSolid() {
		return solid;
	}

	public float getScale() {
		return tileScale;
	}

	public AStarFindHeuristic getHeuristic() {
		return heuristic;
	}

	public TileDrawListener<TileImpl> getTileListener() {
		return listener;
	}

	public void setTileListener(TileDrawListener<TileImpl> listener) {
		this.listener = listener;
	}

	public void drawTiles(GLEx g, float offsetX, float offsetY) {
		ObjectMap<Integer, ObjectMap<Integer, TileImpl>> list = this.tilesX;
		for (Entries<Integer, ObjectMap<Integer, TileImpl>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, ObjectMap<Integer, TileImpl>> entry = it.next();
			ObjectMap<Integer, TileImpl> tiles = entry.value;
			for (TileImpl tiley : tiles.values()) {
				if (listener != null && tiley != null) {
					listener.draw(g, tiley, offsetX, offsetY);
				}
			}
		}
	}

	public void updateTiles(long elapsedTime) {
		ObjectMap<Integer, ObjectMap<Integer, TileImpl>> list = this.tilesX;
		for (Entries<Integer, ObjectMap<Integer, TileImpl>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, ObjectMap<Integer, TileImpl>> entry = it.next();
			ObjectMap<Integer, TileImpl> tiles = entry.value;
			for (TileImpl tiley : tiles.values()) {
				if (listener != null && tiley != null) {
					listener.update(elapsedTime, tiley);
				}
			}
		}
	}

	public TArray<TileImpl> findMovePath(int startX, int startY, int endX, int endY, boolean player) {
		TileImpl startTile = getTile(startX, startY);
		TileImpl endTile = getTile(endX, endY);
		if (startTile != null && endTile != null) {
			return findMovePath(startTile, endTile, player);
		}
		if (startTile == null) {
			startTile = new TileImpl(0, startX, startY);
			startTile.calcNeighbours(width, height);
		}
		if (endTile == null) {
			endTile = new TileImpl(0, endX, endY);
			endTile.calcNeighbours(width, height);
		}
		return findMovePath(startTile, endTile, player);
	}

	public TArray<TileImpl> findMovePath(TileImpl start, TileImpl end, boolean player) {
		return findMovePath(this.tilesX, start, end, player);
	}

	public TArray<TileImpl> findMovePath(ObjectMap<Integer, ObjectMap<Integer, TileImpl>> list, TileImpl start,
			TileImpl end, boolean player) {
		return TileImplPathFind.find(this, heuristic, list, start, end, player);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
