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

import loon.action.map.AStarFindHeuristic;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class TileManager {

	public static final int DEF_RATE = 5;

	public static final int DEF_SOLID = 1;

	public static final float DEF_TILE_SCALE = 1f;

	private final int[][] limit;

	private final ObjectMap<Integer, ObjectMap<Integer, TileImpl>> tilesX;

	private final AStarFindHeuristic heuristic;

	private int rate = DEF_RATE;

	private int solid = DEF_SOLID;

	private float tileScale = DEF_TILE_SCALE;

	public TileManager(AStarFindHeuristic h, int tileXsize, int tileYsize) {
		this(h, tileXsize, tileYsize, DEF_TILE_SCALE);
	}

	public TileManager(AStarFindHeuristic h, int tileXsize, int tileYsize, float ts) {
		this(h, tileXsize, tileYsize, DEF_RATE, DEF_SOLID, ts);
	}

	public TileManager(AStarFindHeuristic h, int tileXsize, int tileYsize, int r, int s, float ts) {
		this.tilesX = new ObjectMap<Integer, ObjectMap<Integer, TileImpl>>(32);
		this.limit = new int[tileXsize][tileYsize];
		this.heuristic = h;
		this.rate = r;
		this.solid = s;
		this.tileScale = ts;
	}

	public void setLimit(int x, int y, int flag) {
		if (limit != null) {
			limit[x][y] = flag;
		}
	}

	public int getLimit(int x, int y) {
		if (limit == null) {
			return 0;
		}
		return limit[x][y];
	}

	public void put(TileImpl tile) {
		ObjectMap<Integer, TileImpl> tilesY = tilesX.get(tile.getX());
		if (tilesY == null) {
			tilesY = new ObjectMap<Integer, TileImpl>(32);
			tilesX.put(tile.getX(), tilesY);
		}
		tilesY.put(tile.getY(), tile);
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

	public int[][] getLimit() {
		return limit;
	}

	public TArray<TileImpl> findMovePath(TArray<TArray<TileImpl>> list, TileImpl start, TileImpl end, boolean player) {
		return TileImplPathFind.find(this, heuristic, list, start, end, player);
	}
}
