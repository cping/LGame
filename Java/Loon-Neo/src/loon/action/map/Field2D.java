/**
 * Copyright 2008 - 2011
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
 * @version 0.1.1
 */
package loon.action.map;

import loon.action.map.colider.Tile;
import loon.action.map.colider.TileHelper;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class Field2D implements Config {

	private RectBox _rect = null;

	public Object Tag;

	private String name = "Field2D";

	private Tile tileImpl;

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return this.name;
	}

	private final static float angular = MathUtils.cos(MathUtils.PI / 4);

	public static int getDirection(float angle) {
		float tup = MathUtils.sin(angle) * 0 + MathUtils.cos(angle) * -1;
		float tright = MathUtils.sin(angle) * 1 + MathUtils.cos(angle) * 0;
		float tleft = MathUtils.sin(angle) * -1 + MathUtils.cos(angle) * 0;
		float tdown = MathUtils.sin(angle) * 0 + MathUtils.cos(angle) * 1;
		if (tup > angular) {
			return TUP;
		}
		if (tright > angular) {
			return TRIGHT;
		}
		if (tleft > angular) {
			return TLEFT;
		}
		if (tdown > angular) {
			return TDOWN;
		}
		return EMPTY;
	}

	private static Vector2f vector2;

	final static private ObjectMap<Vector2f, Integer> directions = new ObjectMap<Vector2f, Integer>(
			9);

	final static private IntMap<Vector2f> directionValues = new IntMap<Vector2f>(
			9);

	static {
		directions.put(new Vector2f(0, 0), Config.EMPTY);
		directions.put(new Vector2f(1, -1), Config.UP);
		directions.put(new Vector2f(-1, -1), Config.LEFT);
		directions.put(new Vector2f(1, 1), Config.RIGHT);
		directions.put(new Vector2f(-1, 1), Config.DOWN);
		directions.put(new Vector2f(0, -1), Config.TUP);
		directions.put(new Vector2f(-1, 0), Config.TLEFT);
		directions.put(new Vector2f(1, 0), Config.TRIGHT);
		directions.put(new Vector2f(0, 1), Config.TDOWN);

		directionValues.put(Config.EMPTY, new Vector2f(0, 0));
		directionValues.put(Config.UP, new Vector2f(1, -1));
		directionValues.put(Config.LEFT, new Vector2f(-1, -1));
		directionValues.put(Config.RIGHT, new Vector2f(1, 1));
		directionValues.put(Config.DOWN, new Vector2f(-1, 1));
		directionValues.put(Config.TUP, new Vector2f(0, -1));
		directionValues.put(Config.TLEFT, new Vector2f(-1, 0));
		directionValues.put(Config.TRIGHT, new Vector2f(1, 0));
		directionValues.put(Config.TDOWN, new Vector2f(0, 1));

	}

	private TArray<Vector2f> result;

	private int[][] data;

	private int[] limit;

	// default size
	private int tileWidth = 32;

	private int tileHeight = 32;

	private int width, height;

	public Field2D(Field2D field) {
		cpy(field);
	}

	public Field2D(String fileName, int tw, int th) {
		set(TileMapConfig.loadAthwartArray(fileName), tw, th);
	}

	public Field2D(int[][] data) {
		this(data, 0, 0);
	}

	public Field2D(int[][] data, int tw, int th) {
		this.set(data, tw, th);
	}

	public void cpy(Field2D field) {
		this.set(CollectionUtils.copyOf(field.data), field.tileWidth,
				field.tileHeight);
	}

	public Tile getTile(int x, int y) {
		return tileImpl.at(x, y);
	}

	public void set(int[][] data, int tw, int th) {
		this.setMap(data);
		this.setTileWidth(tw);
		this.setTileHeight(th);
		this.width = data[0].length;
		this.height = data.length;
		if (tileImpl == null) {
			this.tileImpl = new TileHelper(tileWidth, tileHeight);
		} else {
			this.tileImpl.setWidth(tileWidth);
			this.tileImpl.setHeight(tileHeight);
		}
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setTile(int tw, int th) {
		this.tileWidth = tw;
		this.tileHeight = th;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int pixelsToTilesWidth(float x) {
		return MathUtils.floor(x / tileWidth);
	}

	public int pixelsToTilesWidth(int x) {
		return MathUtils.floor(x / tileWidth);
	}

	public int pixelsToTilesHeight(float y) {
		return MathUtils.floor(y / tileHeight);
	}

	public int pixelsToTilesHeight(int y) {
		return MathUtils.floor(y / tileHeight);
	}

	public int tilesToWidthPixels(int tiles) {
		return tiles * tileWidth;
	}

	public int tilesToHeightPixels(int tiles) {
		return tiles * tileHeight;
	}

	public int tilesToWidthPixels(float tiles) {
		return (int) (tiles * tileWidth);
	}

	public int tilesToHeightPixels(float tiles) {
		return (int) (tiles * tileHeight);
	}

	public int getViewWidth() {
		return tilesToWidthPixels(width);
	}

	public int getViewHeight() {
		return tilesToWidthPixels(height);
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int[] getLimit() {
		return limit;
	}

	public void setLimit(int[] limit) {
		this.limit = limit;
	}

	public int getType(int x, int y) {
		try {
			return data[x][y];
		} catch (Exception e) {
			return -1;
		}
	}

	public void setType(int x, int y, int tile) {
		try {
			this.data[x][y] = tile;
		} catch (Exception e) {
		}
	}

	public int[][] getMap() {
		return data;
	}

	public void setMap(int[][] data) {
		this.data = data;
	}

	public boolean isHit(Vector2f point) {
		int type = get(data, point);
		if (type == -1) {
			return false;
		}
		if (limit != null) {
			for (int i = 0; i < limit.length; i++) {
				if (limit[i] == type) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean inside(float x, float y) {
		RectI rect = new RectI(0, 0, width * tileWidth, height * tileHeight);
		return rect.inside((int) x, (int) y);
	}

	public boolean isHit(int px, int py) {
		int type = get(data, px, py);
		if (type == -1) {
			return false;
		}

		if (limit != null) {
			for (int i = 0; i < limit.length; i++) {
				if (limit[i] == type) {
					return false;
				}
			}
		}
		return true;
	}

	public static int getDirection(int x, int y) {
		return getDirection(x, y, Config.EMPTY);
	}

	public static int getDirection(int x, int y, int value) {
		if (vector2 == null) {
			vector2 = new Vector2f(x, y);
		} else {
			vector2.set(x, y);
		}
		Integer result = directions.get(vector2);
		if (result != null) {
			return result;
		} else {
			return value;
		}
	}

	public static Vector2f getDirection(int type) {
		return directionValues.get(type);
	}

	private static void insertArrays(int[][] arrays, int index, int px, int py) {
		arrays[index][0] = px;
		arrays[index][1] = py;
	}

	public int[][] neighbors(int px, int py, boolean flag) {
		int[][] pos = new int[8][2];
		insertArrays(pos, 0, px, py - 1);
		insertArrays(pos, 0, px + 1, py);
		insertArrays(pos, 0, px, py + 1);
		insertArrays(pos, 0, px - 1, py);
		if (flag) {
			insertArrays(pos, 0, px - 1, py - 1);
			insertArrays(pos, 0, px + 1, py - 1);
			insertArrays(pos, 0, px + 1, py + 1);
			insertArrays(pos, 0, px - 1, py + 1);
		}
		return pos;
	}

	public TArray<Vector2f> neighbors(Vector2f pos, boolean flag) {
		if (result == null) {
			result = new TArray<Vector2f>(8);
		} else {
			result.clear();
		}
		int x = pos.x();
		int y = pos.y();
		result.add(new Vector2f(x, y - 1));
		result.add(new Vector2f(x + 1, y));
		result.add(new Vector2f(x, y + 1));
		result.add(new Vector2f(x - 1, y));
		if (flag) {
			result.add(new Vector2f(x - 1, y - 1));
			result.add(new Vector2f(x + 1, y - 1));
			result.add(new Vector2f(x + 1, y + 1));
			result.add(new Vector2f(x - 1, y + 1));
		}
		return result;
	}

	private int get(int[][] data, int px, int py) {
		try {
			if (px >= 0 && px < width && py >= 0 && py < height) {
				return data[py][px];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private int get(int[][] data, Vector2f point) {
		try {
			if (point.x() >= 0 && point.x() < width && point.y() >= 0
					&& point.y() < height) {
				return data[point.y()][point.x()];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public RectBox getRect() {
		if (_rect == null) {
			_rect = new RectBox(0, 0, getViewWidth(), getViewHeight());
		} else {
			_rect.setSize(getViewWidth(), getViewHeight());
		}
		return _rect;
	}

	public Tile getTileImpl() {
		return tileImpl;
	}

	public void setTileImpl(Tile tileImpl) {
		this.tileImpl = tileImpl;
	}

}
