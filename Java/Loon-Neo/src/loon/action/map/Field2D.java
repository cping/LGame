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

import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.action.map.colider.Tile;
import loon.action.map.colider.TileHelper;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.ArrayMap;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 2维数组到地图数据的转化与处理用类
 */
public class Field2D implements IArray, Config {

	private final static float ANGULAR = 0.706F;

	final static private ArrayMap directions = new ArrayMap(9);

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
	}

	private String _objectName = "Field2D";

	private Vector2f _offset = new Vector2f();

	private RectBox _rectTemp = null;

	private Tile _tileImpl;

	private static Vector2f _tempDir;

	public Object Tag;

	private TArray<Vector2f> result;

	private int[][] mapArrays;

	private int[] moveLimited;

	// default size
	private int tileWidth = 32;

	private int tileHeight = 32;

	private int width, height;

	public static final int angle(Vector2f source, Vector2f target) {
		int nx = target.x() - source.x();
		int ny = target.y() - source.y();
		int r = MathUtils.sqrt(nx * nx + ny * ny);
		float cos = nx / r;
		int angle = MathUtils.floor(MathUtils.acos(cos) * 180 / MathUtils.PI);
		if (ny < 0) {
			angle = 360 - angle;
		}
		return angle;
	}

	public static final float getDirectionToAngle(int dir) {
		switch (dir) {
		case Config.UP:
			return 45;
		case Config.LEFT:
			return 315;
		case Config.RIGHT:
			return 135;
		case Config.DOWN:
			return 225;
		case Config.TRIGHT:
			return 90;
		case Config.TDOWN:
			return 180;
		case Config.TLEFT:
			return 270;
		case Config.TUP:
		default:
			return 0;
		}
	}

	public static final Vector2f getDirectionToPoint(int dir, int value) {
		Vector2f direction = null;
		switch (dir) {
		case Config.UP:
			direction = new Vector2f(value, -value);
			break;
		case Config.LEFT:
			direction = new Vector2f(-value, -value);
			break;
		case Config.RIGHT:
			direction = new Vector2f(value, value);
			break;
		case Config.DOWN:
			direction = new Vector2f(-value, value);
			break;
		case Config.TUP:
			direction = new Vector2f(0, -value);
			break;
		case Config.TLEFT:
			direction = new Vector2f(-value, 0);
			break;
		case Config.TRIGHT:
			direction = new Vector2f(value, 0);
			break;
		case Config.TDOWN:
			direction = new Vector2f(0, value);
			break;
		default:
			direction = new Vector2f(0, 0);
			break;
		}
		return direction;
	}

	public static final int getDirection(int x, int y) {
		return getDirection(x, y, Config.EMPTY);
	}

	public static final int getDirection(int x, int y, int value) {
		int newX = 0;
		int newY = 0;
		if (x > 0) {
			newX = 1;
		} else if (x < 0) {
			newX = -1;
		}
		if (y > 0) {
			newY = 1;
		} else if (y < 0) {
			newY = -1;
		}
		if (_tempDir == null) {
			_tempDir = new Vector2f(newX, newY);
		} else {
			_tempDir.set(newX, newY);
		}
		Integer result = (Integer) directions.get(_tempDir);
		if (result != null) {
			return result;
		} else {
			return value;
		}
	}

	public static final Vector2f getDirection(int type) {
		if (type > Config.TDOWN) {
			type = Config.TDOWN;
		}
		return getDirectionToPoint(type, 1).cpy();
	}

	private static void insertArrays(int[][] arrays, int index, int px, int py) {
		arrays[index][0] = px;
		arrays[index][1] = py;
	}

	public static final int getDirection(Vector2f source, Vector2f target, int dirNumber) {
		int angleValue = angle(source, target);
		return getDirection(source, target, angleValue, dirNumber);
	}

	public static final int getDirection(Vector2f source, Vector2f target, float angleValue, int dirNumber) {
		if (dirNumber == 4) {
			if (angleValue < 90) {
				return Config.RIGHT;
			} else if (angleValue < 180) {
				return Config.DOWN;
			} else if (angleValue < 270) {
				return Config.LEFT;
			} else {
				return Config.UP;
			}
		} else if (dirNumber == 6) {
			if (angleValue > 337 || angleValue < 23) {
				return Config.TRIGHT;
			} else if (angleValue > 270) {
				return Config.UP;
			} else if (angleValue > 202) {
				return Config.LEFT;
			} else if (angleValue > 157) {
				return Config.TLEFT;
			} else if (angleValue > 90) {
				return Config.DOWN;
			} else {
				return Config.RIGHT;
			}
		} else if (dirNumber == 8) {
			if (angleValue > 337 || angleValue < 23) {
				return Config.TRIGHT;
			} else if (angleValue > 292) {
				return Config.UP;
			} else if (angleValue > 247) {
				return Config.TUP;
			} else if (angleValue > 202) {
				return Config.LEFT;
			} else if (angleValue > 157) {
				return Config.TLEFT;
			} else if (angleValue > 112) {
				return Config.DOWN;
			} else if (angleValue > 67) {
				return Config.TDOWN;
			} else {
				return Config.RIGHT;
			}
		}
		return Config.EMPTY;
	}

	public static final int getDirection(Vector2f source, Vector2f target) {
		if (source.x - target.x > 0) {
			if (source.y - target.y > 0) {
				return Config.LEFT;
			} else if (source.y - target.y < 0) {
				return Config.DOWN;
			} else {
				return Config.TLEFT;
			}
		} else if (source.x - target.x < 0) {
			if (source.y - target.y > 0) {
				return Config.UP;
			} else if (source.y - target.y < 0) {
				return Config.RIGHT;
			} else {
				return Config.TRIGHT;
			}
		} else {
			if (source.y - target.y > 0) {
				return Config.TUP;
			} else if (source.y - target.y < 0) {
				return Config.TDOWN;
			} else {
				return Config.EMPTY;
			}
		}
	}

	public static final int getDirection(float angle) {
		float tup = MathUtils.sin(angle) * 0 + MathUtils.cos(angle) * -1;
		float tright = MathUtils.sin(angle) * 1 + MathUtils.cos(angle) * 0;
		float tleft = MathUtils.sin(angle) * -1 + MathUtils.cos(angle) * 0;
		float tdown = MathUtils.sin(angle) * 0 + MathUtils.cos(angle) * 1;
		if (tup > ANGULAR) {
			return TUP;
		}
		if (tright > ANGULAR) {
			return TRIGHT;
		}
		if (tleft > ANGULAR) {
			return TLEFT;
		}
		if (tdown > ANGULAR) {
			return TDOWN;
		}
		return EMPTY;
	}

	public Field2D(Field2D field) {
		cpy(field);
	}

	public Field2D(String fileName, int tw, int th) {
		set(TileMapConfig.loadAthwartArray(fileName), tw, th);
	}

	public Field2D(int[][] mapArrays) {
		this(mapArrays, 0, 0);
	}

	public Field2D(int[][] mapArrays, int tw, int th) {
		this.set(mapArrays, tw, th);
	}

	public void cpy(Field2D field) {
		this.set(CollectionUtils.copyOf(field.mapArrays), field.tileWidth, field.tileHeight);
	}

	public Tile getTile(int x, int y) {
		return _tileImpl.at(x, y);
	}

	public void set(int[][] mapArrays, int tw, int th) {
		this.setMap(mapArrays);
		this.setTileWidth(tw);
		this.setTileHeight(th);
		this.width = mapArrays[0].length;
		this.height = mapArrays.length;
		if (_tileImpl == null) {
			this._tileImpl = new TileHelper(tileWidth, tileHeight);
		} else {
			this._tileImpl.setWidth(tileWidth);
			this._tileImpl.setHeight(tileHeight);
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
		return moveLimited;
	}

	public void setLimit(int[] limit) {
		this.moveLimited = limit;
	}

	public int getType(int x, int y) {
		try {
			return mapArrays[x][y];
		} catch (Exception e) {
			return -1;
		}
	}

	public void setType(int x, int y, int tile) {
		try {
			this.mapArrays[x][y] = tile;
		} catch (Exception e) {
		}
	}

	public int[][] getMap() {
		return CollectionUtils.copyOf(mapArrays);
	}

	public void setMap(int[][] mapArrays) {
		this.mapArrays = mapArrays;
	}

	public int getPixelsAtFieldType(Vector2f pos) {
		return getPixelsAtFieldType(pos.x, pos.y);
	}

	public int getPixelsAtFieldType(float x, float y) {
		int itsX = pixelsToTilesWidth(x);
		int itsY = pixelsToTilesHeight(y);
		return getPixelsAtFieldType(itsX, itsY);
	}

	public boolean isHit(Vector2f point) {
		int type = get(mapArrays, point);
		if (type == -1) {
			return false;
		}
		if (moveLimited != null) {
			for (int i = 0; i < moveLimited.length; i++) {
				if (moveLimited[i] == type) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean inside(int x, int y) {
		return CollisionHelper.intersect(0, 0, width * tileWidth, height * tileHeight, x, y);
	}

	public boolean inside(float x, float y) {
		return inside((int) x, (int) y);
	}

	public boolean isHit(int px, int py) {
		int type = get(mapArrays, px, py);
		if (type == -1) {
			return false;
		}
		if (moveLimited != null) {
			for (int i = 0; i < moveLimited.length; i++) {
				if (moveLimited[i] == type) {
					return false;
				}
			}
		}
		return true;
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

	private int get(int[][] mapArrays, int px, int py) {
		try {
			if (px >= 0 && px < width && py >= 0 && py < height) {
				return mapArrays[py][px];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private int get(int[][] mapArrays, Vector2f point) {
		try {
			if (point.x() >= 0 && point.x() < width && point.y() >= 0 && point.y() < height) {
				return mapArrays[point.y()][point.x()];
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public RectBox getRect() {
		if (_rectTemp == null) {
			_rectTemp = new RectBox(0, 0, getViewWidth(), getViewHeight());
		} else {
			_rectTemp.setSize(getViewWidth(), getViewHeight());
		}
		return _rectTemp;
	}

	public Field2D setValues(int val) {
		int w = mapArrays[0].length;
		int h = mapArrays.length;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				mapArrays[i][j] = val;
			}
		}
		return this;
	}

	public Tile getTileImpl() {
		return _tileImpl;
	}

	public void setTileImpl(Tile tileImpl) {
		this._tileImpl = tileImpl;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public void setOffset(Vector2f offset) {
		this._offset = offset;
	}

	public int offsetXPixel(int x) {
		return x - _offset.x();
	}

	public int offsetYPixel(int y) {
		return y - _offset.y();
	}

	@Override
	public boolean isEmpty() {
		return mapArrays == null || mapArrays.length == 0;
	}

	public void setName(String n) {
		this._objectName = n;
	}

	public String getName() {
		return this._objectName;
	}

	@Override
	public int size() {
		return width * height;
	}

	@Override
	public void clear() {
		set(new int[height][width], width, height);
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char split) {
		if (isEmpty()) {
			return "[]";
		}
		StringBuilder buffer = new StringBuilder(size() * 2 + height + 2);
		buffer.append('[');
		buffer.append(LSystem.LS);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				buffer.append(mapArrays[i][j]);
				if (j < width - 1) {
					buffer.append(split);
				}
			}
			buffer.append(LSystem.LS);
		}
		buffer.append(']');
		return buffer.toString();
	}

}
