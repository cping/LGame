/**
 * Copyright 2014
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
 * @email javachenpeng@yahoo.com
 * @version 0.4.2
 */
package loon.action.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import loon.action.map.ArrayInt2DAStar.TileFactory;
import loon.core.geom.RectBox;
@SuppressWarnings("unchecked")
public class HexagonMap<T> implements GeometryMap, Iterable<TileVisit<T>> {

	public static final int LEFT = -3;
	public static final int DOWNLEFT = -2;
	public static final int UPLEFT = -1;
	public static final int NONE = 0;
	public static final int DOWNRIGHT = 1;
	public static final int UPRIGHT = 2;
	public static final int RIGHT = 3;

	protected int columns, rows;
	protected Object[][] tiles;
	protected Hexagon origin;
	protected Hexagon[][] hexagons;

	public HexagonMap() {

	}

	public HexagonMap<T> configure(Hexagon origin) {
		this.origin = origin;
		return this;
	}

	public HexagonMap<T> configure(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		this.tiles = new Object[columns][rows];
		this.hexagons = new Hexagon[columns][rows];
		return this;
	}

	public Hexagon getOrigin() {
		return origin;
	}

	private RectBox rect = null;

	public RectBox getRect() {
		if (rect == null) {
			rect = new RectBox(origin.getX(), origin.getY(), origin.getX()
					+ (rows > 1 ? columns * origin.getWidth()
							+ origin.getHalfWidth() : columns
							* origin.getWidth()), origin.getY() + rows
					* origin.getBaseHeight() + origin.getEndHeight());
		}
		return rect;
	}

	public boolean contains(int[] position) {
		int m0 = position[0] + (position[1] >> 1);
		return m0 >= 0 && m0 < columns && position[1] >= 0
				&& position[1] < rows;
	}


	public T getTile(int[] position) {
		return (T) tiles[position[0] + (position[1] >> 1)][position[1]];
	}

	public void setTile(int[] position, T tile) {
		tiles[position[0] + (position[1] >> 1)][position[1]] = tile;
	}

	public void fill(TileFactory<T> factory) {
		for (int j = 0; j < columns; j++) {
			for (int i = 0; i < rows; i++) {
				tiles[i][j] = factory.create(j - (i >> 1), i);
			}
		}
	}

	@Override
	public Geometry coordinate(int[] position) {
		int m0 = position[0] + (position[1] >> 1);
		Hexagon hexagon = hexagons[m0][position[1]];
		if (hexagon == null) {
			hexagon = new Hexagon(position[0] * origin.getWidth() + position[1]
					* origin.getHalfWidth() + origin.getX(), position[1]
					* origin.getBaseHeight() + origin.getY(),
					origin.getHalfWidth(), origin.getMidHeight(),
					origin.getEndHeight());
			hexagons[m0][position[1]] = hexagon;
		}
		return hexagon;
	}

	private int[] position = new int[2];

	@Override
	public int[] decoordinate(int x, int y) {
		int m0, n;
		int xBlock = (x - origin.getX())
				/ (origin.getHalfWidth() + origin.getHalfWidth());
		int xOdd = (x - origin.getX())
				% (origin.getHalfWidth() + origin.getHalfWidth());
		int yBlock = (y - origin.getY())
				/ (origin.getEndHeight() + origin.getMidHeight());
		int yOdd = (y - origin.getY())
				% (origin.getEndHeight() + origin.getMidHeight());
		int yOdd0 = Math.round((float) origin.getEndHeight()
				/ (float) origin.getHalfWidth() * xOdd);
		if ((yBlock & 1) == 0) {
			if (yOdd < origin.getEndHeight() - yOdd0) {
				m0 = xBlock - 1;
				n = yBlock - 1;
			} else if (yOdd < yOdd0 - origin.getEndHeight()) { 
				m0 = xBlock;
				n = yBlock - 1;
			} else { 
				m0 = xBlock;
				n = yBlock;
			}
		} else { 
			if (xOdd < origin.getHalfWidth()) {
				if (yOdd < yOdd0) {
					m0 = xBlock;
					n = yBlock - 1;
				} else { 
					m0 = xBlock - 1;
					n = yBlock;
				}
			} else { 
				if (yOdd < origin.getEndHeight() + origin.getEndHeight()
						- yOdd0) { 
					m0 = xBlock;
					n = yBlock - 1;
				} else { 
					m0 = xBlock;
					n = yBlock;
				}
			}
		}
		if (m0 >= 0 && m0 < columns && n >= 0 && n < rows) {
			position[0] = m0 - (n >> 1);
			position[1] = n;
			return position;
		}
		return null;
	}

	@Override
	public int distance(int[] start, int[] end) {
		Integer c = end[0] - start[0];
		Integer r = end[1] - start[1];
		if (c.compareTo(0) == r.compareTo(0)) {
			c = c < 0 ? -c : c;
			r = r < 0 ? -r : r;
			return c + r;
		} else {
			c = c < 0 ? -c : c;
			r = r < 0 ? -r : r;
			return r > c ? r : c;
		}
	}

	static int[][] orientationsByOffset = { { LEFT, UPLEFT, UPRIGHT },
			{ LEFT, NONE, RIGHT }, { DOWNLEFT, DOWNRIGHT, RIGHT } };

	@Override
	public int orientate(int[] start, int[] end) {
		Integer c = end[0] - start[0];
		Integer r = end[1] - start[1];
		c = c.compareTo(0);
		r = r.compareTo(0);
		return orientationsByOffset[r + 1][c + 1];
	}

	private int[][] positions = new int[6][2];

	@Override
	public int[][] adjacent(int[] position) {
		positions[0][0] = position[0] - 1;
		positions[0][1] = position[1];
		positions[1][0] = position[0];
		positions[1][1] = position[1] - 1;
		positions[2][0] = position[0] + 1;
		positions[2][1] = position[1] - 1;
		positions[3][0] = position[0] + 1;
		positions[3][1] = position[1];
		positions[4][0] = position[0];
		positions[4][1] = position[1] + 1;
		positions[5][0] = position[0] - 1;
		positions[5][1] = position[1] + 1;
		return positions;
	}

	static int[][] offsetsByOrientation = { { -1, 0 }, // LEFT
			{ -1, 1 }, // DOWNLEFT
			{ 0, -1 }, // UPLEFT
			{ 0, 0 }, // NONE
			{ 0, 1 }, // DOWNRIGHT
			{ 1, -1 }, // UPRIGHT
			{ 1, 0 }, // RIGHT
	};

	@Override
	public int[] adjacent(int[] position, int orientation) {
		int[] offset = offsetsByOrientation[orientation + 3];
		this.position[0] = position[0] + offset[0];
		this.position[1] = position[1] + offset[1];
		return this.position;
	}

	@Override
	public List<int[]> lineRegion(int[] start, int[] end) {
		int dx = end[0] - start[0];
		int dy = end[1] - start[1];
		if (dx == 0 || dy == 0 || dx == -dy) {
			List<int[]> positions = new ArrayList<int[]>();
			int ax = dx < 0 ? -dx : dx;
			int ay = dy < 0 ? -dy : dy;
			int len = ax < ay ? ay : ax;
			int x = start[0];
			int y = start[1];
			int x1 = dx / len;
			int y1 = dy / len;
			for (int i = 0; i <= len; i++) {
				positions.add(new int[] { x, y });
				x += x1;
				y += y1;
			}
			return positions;
		}
		return null;
	}

	@Override
	public List<int[]> circleRegion(int[] center, int radius) {
		List<int[]> positions = new ArrayList<int[]>();
		int i, j, k;
		for (j = -radius; j <= radius; j++) {
			if (j < 0) {
				i = -radius - j;
				k = radius;
			} else {
				i = -radius;
				k = radius - j;
			}
			while (i <= k) {
				positions.add(new int[] { center[0] + i, center[1] + j });
				i++;
			}
		}
		return positions;
	}

	protected class CellIterator implements Iterator<TileVisit<T>> {
		int m, n;
		int columns, rows;
		int i, j, k;

		protected CellIterator(int m, int n, int columns, int rows) {
			this.m = m;
			this.n = n;
			this.columns = columns;
			this.rows = rows;
			k = n >> 1;
		}

		@Override
		public boolean hasNext() {
			return i < columns && j < rows;
		}

		@Override
		public TileVisit<T> next() {
			TileVisit<T> tile = new TileVisit<T>();
			tile.tile = (T) tiles[i + m][j + n];
			tile.position[0] = i + m - k;
			tile.position[1] = j + n;
			if (++i >= columns) {
				k = (++j + n) >> 1;
				i = 0;
			}
			return tile;
		}

		@Override
		public void remove() {
			throw new RuntimeException("not supported");
		}
	}

	@Override
	public Iterator<TileVisit<T>> iterator() {
		return new CellIterator(0, 0, columns, rows);
	}

	public Iterable<TileVisit<T>> part(RectBox inRect) {
		int m = inRect.Left() / (origin.getHalfWidth() + origin.getHalfWidth())
				- 1;
		if (m < 0) {
			m = 0;
		}
		int n = inRect.Top() / (origin.getEndHeight() + origin.getMidHeight())
				- 1;
		if (n < 0) {
			n = 0;
		}
		int columns = inRect.width
				/ (origin.getHalfWidth() + origin.getHalfWidth()) + 3;
		if (m + columns >= this.columns) {
			columns = this.columns - m;
		}
		int rows = inRect.height
				/ (origin.getEndHeight() + origin.getMidHeight()) + 3;
		if (n + rows >= this.rows) {
			rows = this.rows - n;
		}
		final CellIterator iterator = new CellIterator(m, n, columns, rows);
		return new Iterable<TileVisit<T>>() {
			@Override
			public Iterator<TileVisit<T>> iterator() {
				return iterator;
			}
		};
	}

}
