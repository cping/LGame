package org.loon.framework.android.game.action.sprite.j2me;

import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;


/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class TiledLayer extends Layer {
	private final int rows, cols;

	LImage img;

	private int tileHeight, tileWidth, numStaticTiles;

	private int[][] tiles;

	int[] animatedTiles;

	int numAnimatedTiles;

	public TiledLayer(int cols, int rows, LImage img, int tileWidth,
			int tileHeight) {

		super(0, 0, cols * tileWidth, rows * tileHeight, true);

		if (img == null)
			throw new NullPointerException();
		if (cols <= 0 || rows <= 0 || tileHeight <= 0 || tileWidth <= 0)
			throw new IllegalArgumentException();
		if (img.getWidth() % tileWidth != 0
				|| img.getHeight() % tileHeight != 0)
			throw new IllegalArgumentException();

		this.img = img;
		this.cols = cols;
		this.rows = rows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.numStaticTiles = (img.getWidth() / tileWidth)
				* (img.getHeight() / tileHeight);
		this.tiles = new int[rows][cols];
		this.animatedTiles = new int[5];
		this.numAnimatedTiles = 0;
	}

	public int createAnimatedTile(int staticTileIndex) {
		synchronized (this) {
			if (staticTileIndex < 0 || staticTileIndex > numStaticTiles)
				throw new IndexOutOfBoundsException();

			if (numAnimatedTiles == animatedTiles.length) {
				int[] temp = new int[numAnimatedTiles + 6];
				System.arraycopy(animatedTiles, 0, temp, 0, numAnimatedTiles);
				animatedTiles = temp;
			}

			animatedTiles[numAnimatedTiles] = staticTileIndex;
			numAnimatedTiles++;
			return -numAnimatedTiles;
		}
	}

	public int getAnimatedTile(int index) {
		synchronized (this) {
			index = -index - 1;
			if (index < 0 || index >= numAnimatedTiles)
				throw new IndexOutOfBoundsException();
			return animatedTiles[index];
		}
	}

	public void setAnimatedTile(int index, int staticTileIndex) {
		synchronized (this) {
			index = -index - 1;
			if (index < 0 || index >= numAnimatedTiles)
				throw new IndexOutOfBoundsException();
			if (staticTileIndex < 0 || staticTileIndex > numStaticTiles)
				throw new IndexOutOfBoundsException();

			animatedTiles[index] = staticTileIndex;
		}
	}

	public int getCell(int col, int row) {
		return this.tiles[row][col];
	}

	public void setCell(int col, int row, int index) {
		synchronized (this) {
			if (-index - 1 >= numAnimatedTiles || index > numStaticTiles)
				throw new IndexOutOfBoundsException();
			tiles[row][col] = index;
		}
	}

	public void setStaticTileSet(LImage image, int tileWidth, int tileHeight) {
		synchronized (this) {
			if (img == null)
				throw new NullPointerException();
			if (tileHeight <= 0 || tileWidth <= 0)
				throw new IllegalArgumentException();
			if (img.getWidth() % tileWidth != 0
					|| img.getHeight() % tileHeight != 0)
				throw new IllegalArgumentException();

			int newNumStaticTiles = (img.getWidth() / getCellWidth())
					* (img.getHeight() / getCellHeight());

			int w = cols * tileWidth;
			int h = rows * tileHeight;

			setSize(w, h);

			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;

			if (newNumStaticTiles >= numStaticTiles) {
				this.numStaticTiles = newNumStaticTiles;
				return;
			}

			this.numStaticTiles = newNumStaticTiles;
			this.animatedTiles = new int[5];
			this.numAnimatedTiles = 0;
			this.fillCells(0, 0, getColumns(), getRows(), 0);
		}
	}

	public void fillCells(int col, int row, int numCols, int numRows, int index) {
		synchronized (this) {
			if (numCols < 0 || numRows < 0)
				throw new IllegalArgumentException();
			if (row < 0 || col < 0 || col + numCols > this.cols
					|| row + numRows > this.rows)
				throw new IndexOutOfBoundsException();
			if (-index - 1 >= numAnimatedTiles || index > numStaticTiles)
				throw new IndexOutOfBoundsException();

			int rMax = row + numRows;
			int cMax = col + numCols;
			for (int r = row; r < rMax; r++) {
				for (int c = col; c < cMax; c++) {
					tiles[r][c] = index;
				}
			}
		}
	}

	public final int getColumns() {
		return cols;
	}

	public final int getRows() {
		return rows;
	}

	public final int getCellWidth() {
		return tileWidth;
	}

	public final int getCellHeight() {
		return tileHeight;
	}

	public final void paint(LGraphics g) {
		synchronized (this) {
			if (!this.isVisible())
				return;

			int x = getX();
			int y = getY();

			int c0 = 0;
			int r0 = 0;
			int cMax = getColumns();
			int rMax = getRows();

			int tW = getCellWidth();
			int tH = getCellHeight();

			int x0 = x;
			int anchor = LGraphics.LEFT | LGraphics.TOP;

			int imgCols = img.getWidth() / tW;

			for (int r = r0; r < rMax; r++, y += tH) {
				x = x0;
				for (int c = c0; c < cMax; c++, x += tW) {
					int tile = getCell(c, r);
					if (tile < 0)
						tile = getAnimatedTile(tile);
					if (tile == 0)
						continue;

					tile--;

					int xSrc = tW * (tile % imgCols);
					int ySrc = (tile / imgCols) * tH;

					g.drawRegion(img, xSrc, ySrc, tW, tH, Sprite.TRANS_NONE, x,
							y, anchor);

				}
			}
		}
	}

}

