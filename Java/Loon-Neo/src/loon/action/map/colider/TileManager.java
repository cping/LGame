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
package loon.action.map.colider;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.Grid2D;
import loon.opengl.GLEx;

public class TileManager {

	public static interface TileDrawListener<T> {

		public void update(long elapsedTime, T tile);

		public void draw(GLEx g, T tile, float x, float y);

	}

	private Grid2D grid;

	private TileImplFinder finder;

	private TileDrawListener<TileImpl> listener;

	public TileManager(Grid2D grid) {
		this(grid, true);
	}

	public TileManager(Grid2D grid, boolean all) {
		this(grid, null, all);
	}

	public TileManager(Grid2D grid, AStarFindHeuristic heuristic, boolean all) {
		this.grid = grid;
		this.finder = new TileImplFinder(grid, heuristic, all);
	}

	public void drawTiles(GLEx g, float offsetX, float offsetY) {
		final TileImpl[][] tiles = this.grid.getData();
		for (int y = 0; y < tiles[0].length; y++) {
			for (int x = 0; x < tiles.length; x++) {
				TileImpl tile = grid.get(x, y);
				if (tile != null) {
					if (listener != null && tile != null) {
						listener.draw(g, tile, offsetX, offsetY);
					}
				}
			}
		}
	}

	public void updateTiles(long elapsedTime) {
		final TileImpl[][] tiles = this.grid.getData();
		for (int y = 0; y < tiles[0].length; y++) {
			for (int x = 0; x < tiles.length; x++) {
				TileImpl tile = grid.get(x, y);
				if (tile != null) {
					if (listener != null && tile != null) {
						listener.update(elapsedTime, tile);
					}
				}
			}
		}
	}

	public int toTileX(float x) {
		return grid.toTileX(x);
	}

	public int toTileY(float y) {
		return grid.toTileY(y);
	}

	public TileImplFinder getFinder() {
		return finder;
	}

	public TileDrawListener<TileImpl> getTileListener() {
		return listener;
	}

	public TileManager setTileListener(TileDrawListener<TileImpl> listener) {
		this.listener = listener;
		return this;
	}

	public Grid2D getGrid() {
		return grid;
	}

	public TileManager setGrid(Grid2D grid) {
		this.grid = grid;
		return this;
	}
}
