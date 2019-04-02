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

import java.util.Iterator;

import loon.action.map.colider.TileImpl;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.SortedList;
import loon.utils.TArray;

public class HexagonMap {

	protected static class Node implements Comparable<Node> {
		
		protected int[] position;
		protected int f, g, h;
		protected Node parent;

		public Node(int[] position) {
			this.position = CollectionUtils.copyOf(position);
		}

		public boolean equals(int[] position) {
			return this.position[0] == position[0] && this.position[1] == position[1];
		}

		@Override
		public int compareTo(Node another) {
			int result = this.f - another.f;
			if (result == 0) {
				result = this.h - another.h;
			}
			return result;
		}
	}

	protected static class NodeList extends TArray<Node> {

		public Node find(int[] position) {
			for (int i = size - 1; i >= 0; i--) {
				Node node = get(i);
				if (node.equals(position)) {
					return node;
				}
			}
			return null;
		}

		public void insert(Node node) {
			for (int i = size; i > 0; i--) {
				Node n = get(i - 1);
				if (node.compareTo(n) >= 0) {
					insert(i, node);
					return;
				}
			}
			insert(0, node);
		}

		public void sort(Node node) {
			for (int i = size - 1; i >= 0; i--) {
				Node n = get(i);
				if (n == node) {
					for (; i > 0; i--) {
						n = get(i - 1);
						if (node.compareTo(n) >= 0) {
							return;
						} else {
							set(i, n);
							set(i - 1, node);
						}
					}
				}
			}
		}
	}

	public static class Path {
		public SortedList<int[]> positions;
		public int cost;
	}

	protected class CellIterator implements Iterator<TileVisit<TileImpl>> {
		
		protected int m, n;
		protected int columns, rows;
		protected int i, j, k;

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
		public TileVisit<TileImpl> next() {
			TileVisit<TileImpl> tile = new TileVisit<TileImpl>();
			tile.tile = (TileImpl) tiles[i + m][j + n];
			tile.position[0] = i + m - k;
			tile.position[1] = j + n;
			if (++i >= columns) {
				k = (++j + n) >> 1;
				i = 0;
			}
			return tile;
		}

	}

	public Iterator<TileVisit<TileImpl>> iterator() {
		return new CellIterator(0, 0, columns, rows);
	}

	public Iterable<TileVisit<TileImpl>> part(RectBox inRect) {
		int m = inRect.Left() / (origin.getHalfWidth() + origin.getHalfWidth()) - 1;
		if (m < 0) {
			m = 0;
		}
		int n = inRect.Top() / (origin.getEndHeight() + origin.getMidHeight()) - 1;
		if (n < 0) {
			n = 0;
		}
		int columns = inRect.width / (origin.getHalfWidth() + origin.getHalfWidth()) + 3;
		if (m + columns >= this.columns) {
			columns = this.columns - m;
		}
		int rows = inRect.height / (origin.getEndHeight() + origin.getMidHeight()) + 3;
		if (n + rows >= this.rows) {
			rows = this.rows - n;
		}
		final CellIterator iterator = new CellIterator(m, n, columns, rows);
		return new Iterable<TileVisit<TileImpl>>() {

			@Override
			public Iterator<TileVisit<TileImpl>> iterator() {
				return iterator;
			}
		};
	}

	public static final int LEFT = -3;
	public static final int DOWNLEFT = -2;
	public static final int UPLEFT = -1;
	public static final int NONE = 0;
	public static final int DOWNRIGHT = 1;
	public static final int UPRIGHT = 2;
	public static final int RIGHT = 3;

	public static final int[][] orientationsByOffset = { { LEFT, UPLEFT, UPRIGHT }, { LEFT, NONE, RIGHT },
			{ DOWNLEFT, DOWNRIGHT, RIGHT } };

	public static final int[][] offsetsByOrientation = { { -1, 0 }, // LEFT
			{ -1, 1 }, // DOWNLEFT
			{ 0, -1 }, // UPLEFT
			{ 0, 0 }, // NONE
			{ 0, 1 }, // DOWNRIGHT
			{ 1, -1 }, // UPRIGHT
			{ 1, 0 }, // RIGHT
	};

	private RectBox rectTemp = null;

	private AStarFindHeuristic heuristic = null;

	protected int columns, rows;
	protected TileImpl[][] tiles;
	protected Hexagon origin;
	protected Hexagon[][] hexagons;

	private int[] limitTypes = { -1 };

	private int[] position = new int[2];

	private int[][] positions = new int[6][2];

	public static Path findPath(HexagonMap map, Vector2f start, Vector2f end) {
		return findPath(map, start.toInt(), end.toInt(), 0);
	}

	public static Path findPath(HexagonMap map, int[] start, int[] end) {
		return findPath(map, start, end, 0);
	}

	public static Path findPath(HexagonMap map, Vector2f start, Vector2f end, int endRadius) {
		return findPath(map, start.toInt(), end.toInt(), endRadius);
	}

	public static Path findPath(HexagonMap map, int[] start, int[] end, int endRadius) {
		NodeList openNodes = new NodeList();
		NodeList closedNodes = new NodeList();
		openNodes.add(new Node(start));
		Node found = null;
		for (;;) {
			if (openNodes.size == 0) {
				return null;
			}
			Node node = openNodes.removeIndex(0);
			int distance = map.distance(node.position, end);
			if (distance <= endRadius) {
				found = node;
				break;
			}
			closedNodes.add(node);
			int[][] positions = map.adjacent(node.position);
			for (int[] position : positions) {
				if (!map.isLimit(position)) {
					continue;
				}
				if (closedNodes.find(position) != null) {
					continue;
				}
				Node openNode = openNodes.find(position);
				if (openNode == null) {
					Node newNode = new Node(position);
					newNode.g = node.g + map.getLimitType(position);
					newNode.h = (int) (map.distance(position, end) * map.baseScore(position, end));
					newNode.f = newNode.g + newNode.h;
					newNode.parent = node;
					openNodes.insert(newNode);
				} else {
					int g = node.g + map.getLimitType(position);
					if (openNode.g > g) {
						openNode.g = g;
						openNode.f = openNode.g + openNode.h;
						openNode.parent = node;
						openNodes.sort(openNode);
					}
				}
			}
		}
		if (found == null) {
			return null;
		}
		Path path = new Path();
		path.cost = found.g;
		SortedList<int[]> positions = new SortedList<int[]>();
		while (found != null) {
			positions.addFirst(found.position.clone());
			found = found.parent;
		}
		path.positions = positions;
		return path;
	}

	public HexagonMap() {

	}

	public boolean isLimit(int[] position) {
		return contains(position) && limitTypes[getTile(position).idx] >= 0;
	}

	public int getLimitType(int[] position) {
		return limitTypes[getTile(position).idx];
	}

	public float baseScore(int[] position, int[] end) {
		if (heuristic != null) {
			return heuristic.getScore(position[0], position[1], end[0], end[1]);
		}
		return 10f;
	}

	public void fill(int id) {
		for(int j = 0; j < columns; j++) {
			for(int i = 0; i < rows; i++) {
				tiles[i][j] = new TileImpl(id, i, j);
			}
		}
	}
	
	public HexagonMap configure(int x, int y, int halfWidth, int midHeight, int endHeight, int columns, int rows) {
		configure(new Hexagon(x, y, halfWidth, midHeight, endHeight));
		configure(columns, rows);
		return this;
	}

	public HexagonMap configure(Hexagon origin) {
		this.origin = origin;
		return this;
	}

	public HexagonMap configure(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		this.tiles = new TileImpl[columns][rows];
		this.hexagons = new Hexagon[columns][rows];
		return this;
	}

	public Hexagon getOrigin() {
		return origin;
	}

	public RectBox getRect() {
		if (rectTemp == null) {
			rectTemp = new RectBox(origin.getX(), origin.getY(), origin.getX()
					+ (rows > 1 ? columns * origin.getWidth() + origin.getHalfWidth() : columns * origin.getWidth()),
					origin.getY() + rows * origin.getBaseHeight() + origin.getEndHeight());
		} else {
			rectTemp.setBounds(origin.getX(), origin.getY(), origin.getX()
					+ (rows > 1 ? columns * origin.getWidth() + origin.getHalfWidth() : columns * origin.getWidth()),
					origin.getY() + rows * origin.getBaseHeight() + origin.getEndHeight());
		}
		return rectTemp;
	}

	public boolean contains(Vector2f pos) {
		return contains(pos.toInt());
	}

	public boolean contains(int[] position) {
		int m0 = position[0] + (position[1] >> 1);
		return m0 >= 0 && m0 < columns && position[1] >= 0 && position[1] < rows;
	}

	public int[] getLimitTypes() {
		return limitTypes;
	}

	public void setLimitTypes(int[] limitTypes) {
		this.limitTypes = limitTypes;
	}

	public TileImpl getTile(Vector2f pos) {
		return getTile(pos.toInt());
	}

	public TileImpl getTile(int[] position) {
		return tiles[position[0] + (position[1] >> 1)][position[1]];
	}

	public HexagonMap setTile(Vector2f pos, TileImpl tile) {
		return setTile(pos.toInt(), tile);
	}

	public HexagonMap setTile(int[] position, TileImpl tile) {
		tiles[position[0] + (position[1] >> 1)][position[1]] = tile;
		return this;
	}

	public Hexagon coordinate(Vector2f pos) {
		return coordinate(pos.toInt());
	}

	public Hexagon coordinate(int[] position) {
		int m0 = position[0] + (position[1] >> 1);
		Hexagon hexagon = hexagons[m0][position[1]];
		if (hexagon == null) {
			hexagon = new Hexagon(position[0] * origin.getWidth() + position[1] * origin.getHalfWidth() + origin.getX(),
					position[1] * origin.getBaseHeight() + origin.getY(), origin.getHalfWidth(), origin.getMidHeight(),
					origin.getEndHeight());
			hexagons[m0][position[1]] = hexagon;
		}
		return hexagon;
	}

	public Vector2f decoordinate(int x, int y) {
		int m0, n;
		int xBlock = (x - origin.getX()) / (origin.getHalfWidth() + origin.getHalfWidth());
		int xOdd = (x - origin.getX()) % (origin.getHalfWidth() + origin.getHalfWidth());
		int yBlock = (y - origin.getY()) / (origin.getEndHeight() + origin.getMidHeight());
		int yOdd = (y - origin.getY()) % (origin.getEndHeight() + origin.getMidHeight());
		int yOdd0 = MathUtils.round(origin.getEndHeight() / origin.getHalfWidth() * xOdd);
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
				if (yOdd < origin.getEndHeight() + origin.getEndHeight() - yOdd0) {
					m0 = xBlock;
					n = yBlock - 1;
				} else {
					m0 = xBlock;
					n = yBlock;
				}
			}
		}
		if (m0 >= 0 && m0 < columns && n >= 0 && n < rows) {
			return Vector2f.at(m0 - (n >> 1), n);
		}
		return null;
	}

	public int distance(Vector2f start, Vector2f end) {
		return distance(start.toInt(), end.toInt());
	}

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

	public int orientate(Vector2f start, Vector2f end) {
		return orientate(start.toInt(), end.toInt());
	}

	public int orientate(int[] start, int[] end) {
		Integer c = end[0] - start[0];
		Integer r = end[1] - start[1];
		c = c.compareTo(0);
		r = r.compareTo(0);
		return orientationsByOffset[r + 1][c + 1];
	}

	public AStarFindHeuristic getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(AStarFindHeuristic heuristic) {
		this.heuristic = heuristic;
	}

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

	public int[] adjacent(int[] position, int orientation) {
		int[] offset = offsetsByOrientation[orientation + 3];
		this.position[0] = position[0] + offset[0];
		this.position[1] = position[1] + offset[1];
		return this.position;
	}

	public TArray<Vector2f> lineRegion(Vector2f start, Vector2f end) {
		return lineRegion(start.toInt(), end.toInt());
	}

	public TArray<Vector2f> lineRegion(int[] start, int[] end) {
		int dx = end[0] - start[0];
		int dy = end[1] - start[1];
		if (dx == 0 || dy == 0 || dx == -dy) {
			TArray<Vector2f> positions = new TArray<Vector2f>();
			int ax = dx < 0 ? -dx : dx;
			int ay = dy < 0 ? -dy : dy;
			int len = ax < ay ? ay : ax;
			int x = start[0];
			int y = start[1];
			int x1 = dx / len;
			int y1 = dy / len;
			for (int i = 0; i <= len; i++) {
				positions.add(Vector2f.at(x, y));
				x += x1;
				y += y1;
			}
			return positions;
		}
		return null;
	}

	public TArray<Vector2f> circleRegion(Vector2f center, int radius) {
		return circleRegion(center.toInt(), radius);
	}

	public TArray<Vector2f> circleRegion(int[] center, int radius) {
		TArray<Vector2f> positions = new TArray<Vector2f>();
		int i, j, k;
		for (j = -radius; j <= radius; j++) {
			if (j < 0) {
				i = -radius - j;
				k = radius;
			} else {
				i = -radius;
				k = radius - j;
			}
			for (; i <= k;) {
				positions.add(Vector2f.at(center[0] + i, center[1] + j));
				i++;
			}
		}
		return positions;
	}

}
