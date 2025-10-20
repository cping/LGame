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

import java.util.Iterator;

import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.collision.CollisionHelper;
import loon.action.map.colider.Tile;
import loon.action.map.colider.TileHelper;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.TArray;

/**
 * 二维数组到地图数据的存储转化与处理用类
 */
public class Field2D implements IArray, Config, LRelease {

	public static interface MapSwitchMaker {

		public void loop(int flag, float x, float y);

	}

	private final static int[][][] NEIGHBORS = { { { 1, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } },
			{ { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { 0, 1 } } };

	private final static float ANGULAR = 0.706F;

	private Vector2f _offset = new Vector2f();

	private TArray<Vector2f> _result = null;

	private String _fieldName = "Field2D";

	private RectBox _rectTemp = null;

	private Tile _tileImpl = null;

	private int[][] _proxyArrays;

	private int[][] _mapArrays;

	private int[] _moveLimited;

	// default size
	private int _tileWidth = LSystem.LAYER_TILE_SIZE;

	private int _tileHeight = LSystem.LAYER_TILE_SIZE;

	private int _width, _height;

	private IntArray _allowMove;

	private boolean _mapDirty;

	private TileCollisionListener _tileCollisionListener;

	public Object Tag;

	public final static RectBox inflateBounds(RectBox rect, float x, float y) {
		if (x < rect.x) {
			rect.width += rect.x - x;
			rect.x = x;
		}
		if (y < rect.y) {
			rect.height += rect.y - y;
			rect.y = y;
		}
		if (x > rect.x + rect.width) {
			rect.width = MathUtils.iceil(x - rect.x);
		}
		if (y > rect.y + rect.height) {
			rect.height = MathUtils.iceil(y - rect.y);
		}
		return rect;
	}

	public final static TArray<PointI> getPosOfLine(int x0, int y0, int x1, int y1) {
		TArray<PointI> list = new TArray<PointI>();
		int dx = MathUtils.abs(x1 - x0);
		int dy = MathUtils.abs(y1 - y0);
		int sx = (x0 < x1) ? 1 : -1;
		int sy = (y0 < y1) ? 1 : -1;
		int err = dx - dy;
		for (;;) {
			list.add(new PointI(x0, y1));
			if ((x0 == x1) && (y0 == y1)) {
				break;
			}
			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
		return list;
	}

	public final static TArray<PointI> getPosOfParabola(int x0, int y0, int x1, int y1, int height) {
		if (x0 == x1) {
			return getPosOfLine(x0, y0, x1, y1);
		}
		TArray<PointI> list = new TArray<PointI>();
		int top_y, start_x, start_y, dest_x, dest_y;
		top_y = (y0 + y1) / 2 - height;
		if (y0 > y1) {
			start_y = y1;
			dest_y = y0;
		} else {
			start_y = y0;
			dest_y = y1;
		}
		if (x0 > x1) {
			start_x = x1;
			dest_x = x0;
		} else {
			dest_x = x1;
			start_x = x0;
		}
		int k = (int) -MathUtils.sqrt((top_y - start_y) / (top_y - dest_y));
		int v = (k * dest_x - start_x) / (k - 1);
		int u = (top_y - start_y) / ((start_x - v) * (start_x - v));
		for (int x = start_x; x <= dest_x; x++) {
			int y = top_y - u * (x - v) * (x - v);
			list.add(new PointI(x, y));
		}
		if (x0 > x1) {
			list = list.reverse();
		}
		return list;
	}

	public final static TArray<PointI> getPosOfParabola(int x1, int y1, int x2, int y2, int x3, int y3) {
		TArray<PointI> list = new TArray<PointI>();
		int a = (x1 * (y3 - y2) - x2 * y3 + y2 * x3 + y1 * (x2 - x3))
				/ (x1 * ((x3 * x3) - (x2 * x2)) - x2 * (x3 * x3) + (x2 * x2) * x3 + (x1 * x1) * (x2 - x3));
		int b = -((x1 * x1) * (y3 - y2) - (x2 * x2) * y3 + y2 * (x3 * x3) + y1 * ((x2 * x2) - (x3 * x3)))
				/ (x1 * ((x3 * x3) - (x2 * x2)) - x2 * (x3 * x3) + (x2 * x2) * x3 + (x1 * x1) * (x2 - x3));
		int c = (x1 * (y2 * (x3 * x3) - (x2 * x2) * y3) + (x1 * x1) * (x2 * y3 - y2 * x3)
				+ y1 * ((x2 * x2) * x3 - x2 * (x3 * x3)))
				/ (x1 * ((x3 * x3) - (x2 * x2)) - x2 * (x3 * x3) + (x2 * x2) * x3 + (x1 * x1) * (x2 - x3));
		int start_x;
		int end_x;
		if (x1 <= x3) {
			start_x = x1;
			end_x = x3;
		} else {
			start_x = x3;
			end_x = x1;
		}
		for (int x = start_x; x <= end_x; x++) {
			int y = a * x * x + b * x + c;
			list.add(new PointI(x, y));
		}
		return list;
	}

	public final static Vector2f shiftPosition(TArray<ActionBind> items, float x, float y, int direction) {
		return shiftPosition(items, x, y, direction, null);
	}

	public final static Vector2f shiftPosition(TArray<ActionBind> items, float x, float y, int direction,
			Vector2f output) {

		if (output == null) {
			output = new Vector2f();
		}

		float px;
		float py;

		if (items.size > 1) {
			int i = 0;
			float cx = 0f;
			float cy = 0f;
			ActionBind cur = null;

			if (direction == 0) {
				// 下方坐标转上方坐标

				int len = items.size - 1;

				ActionBind obj = items.get(len);
				px = obj.getX();
				py = obj.getY();

				for (i = len - 1; i >= 0; i--) {

					cur = items.get(i);
					cx = cur.getX();
					cy = cur.getX();

					cur.setX(px);
					cur.setY(py);

					px = cx;
					py = cy;
				}
				obj.setX(x);
				obj.setX(y);
			} else {
				// 上方坐标转下方坐标

				ActionBind obj = items.get(0);
				px = obj.getX();
				py = obj.getY();

				for (i = 1; i < items.size; i++) {

					cur = items.get(i);
					cx = cur.getX();
					cy = cur.getX();

					cur.setX(px);
					cur.setY(py);

					px = cx;
					py = cy;
				}
				obj.setX(x);
				obj.setX(y);
			}
		} else {

			ActionBind obj = items.get(0);
			px = obj.getX();
			py = obj.getY();
			obj.setX(x);
			obj.setX(y);
		}

		output.x = px;
		output.y = py;

		return output;
	}

	public final static Vector2f toXY(int index, int width, int height) {
		Vector2f out = new Vector2f();
		float nx = 0f;
		float ny = 0f;
		int total = width * height;
		if (index > 0 && index <= total) {
			if (index > width - 1) {
				ny = MathUtils.ifloor(index / width);
				nx = index - (ny * width);
			} else {
				nx = index;
			}
			out.set(nx, ny);
		}
		return out;
	}

	public final static float getRelation(float x, float x1, float x2, float y1, float y2, float scale) {
		if (scale <= 0f) {
			scale = 1f;
		}
		return ((y2 - y1) / MathUtils.pow((x2 - x1), scale) * 1f) * MathUtils.pow((x - x1), scale) + y1;
	}

	public final static int getSteps(int from, int to, int mapLength, boolean allowWrapping) {
		int steps = to - from;
		int distance = MathUtils.abs(steps);
		if (allowWrapping && mapLength - distance < distance)
			steps = (mapLength - distance) * ((steps < 0) ? 1 : -1);
		return steps;
	}

	public final static IntArray getStepsInRange(int from, int to, int mapLength, int range) {
		final IntArray steps = new IntArray();
		int step = to - from;
		for (; MathUtils.abs(step) <= range;) {
			steps.add(step);
			step += (step < 0) ? -mapLength : mapLength;
		}
		step = (mapLength - MathUtils.abs(to - from)) * ((step < 0) ? 1 : -1);
		for (; MathUtils.abs(step) <= range;) {
			steps.add(step);
			step += (step < 0) ? -mapLength : mapLength;
		}
		return steps;
	}

	public final static int getDistance(int from, int to, int mapLength, boolean allow) {
		int distance = MathUtils.abs(to - from);
		if (allow) {
			distance = MathUtils.min(distance, mapLength - distance);
		}
		return distance;
	}

	public final static float rotation(float srcX, float srcY, float dstX, float dstY) {
		int nx = MathUtils.ifloor(dstX - srcX);
		int ny = MathUtils.ifloor(dstY - srcY);
		return MathUtils.toDegrees(MathUtils.atan2(ny, nx));
	}

	public final static float rotation(Vector2f source, Vector2f target) {
		if (source == null || target == null) {
			return 0f;
		}
		return rotation(source.x, source.y, target.x, target.y);
	}

	public final static int angle(float srcX, float srcY, float dstX, float dstY) {
		float nx = dstX - srcX;
		float ny = dstY - srcY;
		float r = MathUtils.sqrt(nx * nx + ny * ny);
		float cos = nx / r;
		int angle = MathUtils.ifloor(MathUtils.acos(cos) * 180 / MathUtils.PI);
		if (ny < 0) {
			angle = 360 - angle;
		}
		return angle;
	}

	public final static int angle(Vector2f source, Vector2f target) {
		if (source == null || target == null) {
			return 0;
		}
		return angle(source.x, source.y, target.x, target.y);
	}

	public final static float getDirectionToAngle(int dir) {
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

	public final static Vector2f getDirectionToPoint(int dir, int v) {
		return getDirectionToPoint(dir, v, new Vector2f());
	}

	public final static Vector2f getDirectionToPoint(int dir, int v, Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		final Vector2f direction = out;
		switch (dir) {
		case Config.UP:
			direction.set(v, -v);
			break;
		case Config.LEFT:
			direction.set(-v, -v);
			break;
		case Config.RIGHT:
			direction.set(v, v);
			break;
		case Config.DOWN:
			direction.set(-v, v);
			break;
		case Config.TUP:
			direction.set(0, -v);
			break;
		case Config.TLEFT:
			direction.set(-v, 0);
			break;
		case Config.TRIGHT:
			direction.set(v, 0);
			break;
		case Config.TDOWN:
			direction.set(0, v);
			break;
		default:
			direction.set(0, 0);
			break;
		}
		return direction;
	}

	public final static int getDirection(int x, int y) {
		return getDirection(x, y, Config.EMPTY);
	}

	public final static int getDirection(int x, int y, int value) {
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
		int dir = getDirectionImpl(newX, newY);
		return Config.EMPTY == dir ? value : dir;
	}

	private final static int getDirectionImpl(int x, int y) {
		if (x == 0 && y == 0) {
			return Config.EMPTY;
		} else if (x == 1 && y == -1) {
			return Config.UP;
		} else if (x == 0 && y == -1) {
			return Config.TUP;
		} else if (x == -1 && y == -1) {
			return Config.LEFT;
		} else if (x == 1 && y == 1) {
			return Config.RIGHT;
		} else if (x == -1 && y == 1) {
			return Config.DOWN;
		} else if (x == -1 && y == 0) {
			return Config.TLEFT;
		} else if (x == 1 && y == 0) {
			return Config.TRIGHT;
		} else if (x == 0 && y == 1) {
			return Config.TDOWN;
		}
		return Config.EMPTY;
	}

	public final static Vector2f getDirection(int type) {
		if (type > Config.TDOWN) {
			type = Config.TDOWN;
		}
		return getDirectionToPoint(type, 1).cpy();
	}

	public final static String toDirection(int id) {
		switch (id) {
		default:
		case Config.EMPTY:
			return "EMPTY";
		case Config.LEFT:
			return "LEFT";
		case Config.RIGHT:
			return "RIGHT";
		case Config.UP:
			return "UP";
		case Config.DOWN:
			return "DOWN";
		case Config.TLEFT:
			return "TLEFT";
		case Config.TRIGHT:
			return "TRIGHT";
		case Config.TDOWN:
			return "TDOWN";
		case Config.TUP:
			return "TUP";
		}
	}

	private final static void insertArrays(int[][] arrays, int index, int px, int py) {
		arrays[index][0] = px;
		arrays[index][1] = py;
	}

	public final static int getDirection(Vector2f source, Vector2f target, int dirNumber) {
		int angleValue = angle(source, target);
		return getDirection(source, target, angleValue, dirNumber);
	}

	public final static int getDirection(Vector2f source, Vector2f target, float angleValue, int dirNumber) {
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

	public final static int getDirection(Vector2f source, Vector2f target) {
		return getDirection(source.x, source.y, target.x, target.y);
	}

	public final static int getDirection(float srcX, float srcY, float destX, float destY) {
		if (srcX - destX > 0) {
			if (srcY - destY > 0) {
				return Config.LEFT;
			} else if (srcY - destY < 0) {
				return Config.DOWN;
			} else {
				return Config.TLEFT;
			}
		} else if (srcX - destX < 0) {
			if (srcY - destY > 0) {
				return Config.UP;
			} else if (srcY - destY < 0) {
				return Config.RIGHT;
			} else {
				return Config.TRIGHT;
			}
		} else {
			if (srcY - destY > 0) {
				return Config.TUP;
			} else if (srcY - destY < 0) {
				return Config.TDOWN;
			} else {
				return Config.EMPTY;
			}
		}
	}

	public final static int getDirection(float angle) {
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

	public final static boolean[] generate(int w, int h, float f, int cluster, boolean forceFillRate) {
		final int length = w * h;
		boolean[] cur = new boolean[length];
		boolean[] off = new boolean[length];
		int fillDiff = -MathUtils.round(length * f);
		if (forceFillRate && cluster > 0) {
			f += (0.5f - f) * 0.5f;
		}
		int i;
		for (i = 0; i < length; i++) {
			off[i] = (MathUtils.random() < f);
			if (off[i]) {
				fillDiff++;
			}
		}
		for (i = 0; i < cluster; i++) {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int pos = x + y * w;
					int count = 0;
					int neighbours = 0;
					if (y > 0) {
						if (x > 0) {
							if (off[pos - w - 1]) {
								count++;
							}
							neighbours++;
						}
						if (off[pos - w]) {
							count++;
						}
						neighbours++;
						if (x < w - 1) {
							if (off[pos - w + 1]) {
								count++;
							}
							neighbours++;
						}
					}
					if (x > 0) {
						if (off[pos - 1]) {
							count++;
						}
						neighbours++;
					}
					if (off[pos]) {
						count++;
					}
					neighbours++;
					if (x < w - 1) {
						if (off[pos + 1]) {
							count++;
						}
						neighbours++;
					}
					if (y < h - 1) {
						if (x > 0) {
							if (off[pos + w - 1]) {
								count++;
							}
							neighbours++;
						}
						if (off[pos + w]) {
							count++;
						}
						neighbours++;
						if (x < w - 1) {
							if (off[pos + w + 1]) {
								count++;
							}
							neighbours++;
						}
					}
					cur[pos] = (2 * count >= neighbours);
					if (cur[pos] != off[pos])
						fillDiff += cur[pos] ? 1 : -1;
				}
			}
			final boolean[] tmp = cur;
			cur = off;
			off = tmp;
		}
		if (forceFillRate && MathUtils.min(w, h) > 2) {
			final int[] neighbours = { -w - 1, -w, -w + 1, -1, 0, 1, w - 1, w, w + 1 };
			boolean growing = (fillDiff < 0);
			while (fillDiff != 0) {
				int cell, tries = 0;
				do {
					cell = MathUtils.random(1, w - 1) + MathUtils.random(1, h - 1) * w;
					tries++;
				} while (off[cell] != growing && tries * 10 < length);
				for (int j = 0; j < neighbours.length; j++) {
					if (fillDiff != 0 && off[cell + j] != growing) {
						off[cell + j] = growing;
						fillDiff += growing ? 1 : -1;
					}
				}
			}
		}
		return off;
	}

	public final static Field2D of(int w, int h, int tw, int th, int fill) {
		return new Field2D(w, h, tw, th, fill);
	}

	public final static Field2D of(int w, int h, int fill) {
		return of(w, h, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, fill);
	}

	public final static Field2D of(int w, int h) {
		return of(w, h, 0);
	}

	public final static Field2D of(int tw, int th, String... map) {
		return new Field2D(map, tw, th);
	}

	public final static Field2D of(String... map) {
		return new Field2D(map, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE);
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

	public Field2D(String[] chars, int tw, int th) {
		this.set(TileMapConfig.loadStringMap(chars), tw, th);
	}

	public Field2D(int[][] mapArrays, int tw, int th) {
		this.set(mapArrays, tw, th);
	}

	public Field2D(int w, int h) {
		this(w, h, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, 0);
	}

	public Field2D(Screen screen, int tw, int th) {
		this(MathUtils.ifloor(screen.getWidth() / tw), MathUtils.ifloor(screen.getHeight() / th), tw, th, 0);
	}

	public Field2D(int w, int h, int tw, int th) {
		this(w, h, tw, th, 0);
	}

	public Field2D(int w, int h, int tw, int th, int v) {
		final int[][] newMap = new int[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				newMap[i][j] = v;
			}
		}
		this.set(newMap, tw, th);
	}

	public Field2D fill(int val) {
		return setValues(val);
	}

	public Field2D blankMap() {
		return fill(0);
	}

	public Field2D cpy() {
		return new Field2D(this);
	}

	public Field2D cpy(Field2D field) {
		this.set(field._mapArrays, field._tileWidth, field._tileHeight);
		if (field._offset != null) {
			this._offset = field._offset.cpy();
		}
		if (field._rectTemp != null) {
			this._rectTemp = field._rectTemp.cpy();
		}
		this._tileImpl = field._tileImpl;
		this.Tag = field.Tag;
		if (field._result != null) {
			this._result = field._result.cpy();
		}
		if (field._moveLimited != null) {
			this._moveLimited = CollectionUtils.copyOf(field._moveLimited);
		}
		this._width = field._width;
		this._height = field._height;
		if (field._allowMove != null) {
			this._allowMove = new IntArray(field._allowMove);
		}
		return this;
	}

	public Tile getTile(int x, int y) {
		if (contains(x, y)) {
			return _tileImpl.at(getTileType(x, y), x, y, this._tileWidth, this._tileHeight);
		}
		return null;
	}

	public Tile getPointTile(float px, float py) {
		int x = MathUtils.ifloor(px / this._tileWidth);
		int y = MathUtils.ifloor(py / this._tileHeight);
		return getTile(x, y);
	}

	public Field2D set(int[][] arrays) {
		return set(arrays, this._tileWidth, this._tileHeight);
	}

	public Field2D set(int[][] arrays, int tw, int th) {
		if (this._allowMove == null) {
			this._allowMove = new IntArray();
		}
		this.setMap(arrays);
		this.setTileWidth(tw);
		this.setTileHeight(th);
		if (arrays != null) {
			this._width = arrays[0].length;
			this._height = arrays.length;
		}
		if (_tileImpl == null) {
			this._tileImpl = new TileHelper(_tileWidth, _tileHeight);
		} else {
			this._tileImpl.setWidth(_tileWidth);
			this._tileImpl.setHeight(_tileHeight);
		}
		return this;
	}

	public Field2D setSize(int width, int height) {
		this._width = width;
		this._height = height;
		return this;
	}

	public Field2D setTile(int tw, int th) {
		this._tileWidth = tw;
		this._tileHeight = th;
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public int getDrawWidth() {
		return tilesToHeightPixels(_width);
	}

	public int getDrawHeight() {
		return tilesToHeightPixels(_height);
	}

	public int getHexagonWidth() {
		return getHexagonWidth(2f / 3f);
	}

	public int getHexagonWidth(float offset) {
		return MathUtils.ifloor(_width * offset);
	}

	public int getHexagonHeight() {
		return MathUtils.ifloor(_height / MathUtils.sqrt(3f)) - 1;
	}

	public PointI pixelsHexagonToTiles(float x, float y) {
		return pixelsHexagonToTiles(x, y, 2f / 3f);
	}

	public PointI pixelsHexagonToTiles(float x, float y, float offset) {
		float sqrte = MathUtils.sqrt(3f) / 3f;
		int hx = MathUtils.ifloor(offset * x / _tileWidth);
		int hy = MathUtils.ifloor((sqrte * y / _tileHeight + MathUtils.round(hx) % 2f) * sqrte);
		return new PointI(hx, hy);
	}

	public PointI pixelsIsometricToTiles(float x, float y) {
		int hx = MathUtils.ifloor(x / (_tileWidth * 0.5f));
		int hy = MathUtils.ifloor((y - hx * (_tileHeight / 2f)) / _tileHeight);
		return new PointI(hx + hy, hy);
	}

	public PointI tilesIsometricToPixels(float x, float y) {
		float hx = _tileWidth * x / 2f + _height * _tileWidth / 2f - y * _tileWidth / 2f;
		float hy = _tileHeight * y / 2f + _width * _tileHeight / 2f - x * _tileHeight / 2f;
		return new PointI(MathUtils.ifloor(hx), MathUtils.ifloor(hy));
	}

	public PointI pixelsOrthogonalToTiles(float x, float y) {
		int hx = MathUtils.ifloor(x / _tileWidth);
		int hy = MathUtils.ifloor(y / _tileHeight);
		return new PointI(hx, hy);
	}

	public int toPixelX(float x) {
		return tilesToWidthPixels(x);
	}

	public int toPixelY(float y) {
		return tilesToHeightPixels(y);
	}

	public int toTileX(float x) {
		return pixelsToTilesWidth(x);
	}

	public int toTileY(float y) {
		return pixelsToTilesHeight(y);
	}

	public int pixelsToTilesWidth(float x) {
		return MathUtils.ifloor(x / _tileWidth);
	}

	public int pixelsToTilesWidth(int x) {
		return MathUtils.ifloor(x / _tileWidth);
	}

	public int pixelsToTilesHeight(float y) {
		return MathUtils.ifloor(y / _tileHeight);
	}

	public int pixelsToTilesHeight(int y) {
		return MathUtils.ifloor(y / _tileHeight);
	}

	public int tilesToWidthPixels(int tiles) {
		return tiles * _tileWidth;
	}

	public int tilesToHeightPixels(int tiles) {
		return tiles * _tileHeight;
	}

	public int tilesToWidthPixels(float tiles) {
		return MathUtils.ifloor(tiles * _tileWidth);
	}

	public int tilesToHeightPixels(float tiles) {
		return MathUtils.ifloor(tiles * _tileHeight);
	}

	public int getViewWidth() {
		return tilesToWidthPixels(_width);
	}

	public int getViewHeight() {
		return tilesToWidthPixels(_height);
	}

	public int getTileHeight() {
		return _tileHeight;
	}

	public int getTileHalfHeight() {
		return _tileHeight / 2;
	}

	public Field2D setTileHeight(int tileHeight) {
		this._tileHeight = tileHeight;
		return this;
	}

	public int getTileHalfWidth() {
		return _tileWidth / 2;
	}

	public int getTileWidth() {
		return _tileWidth;
	}

	public Field2D setTileWidth(int tileWidth) {
		this._tileWidth = tileWidth;
		return this;
	}

	public Field2D addActionBindToMap(TArray<ActionBind> acts, ActionBind other) {
		return addActionBindToMap(acts, -1, other);
	}

	public Field2D addActionBindToMap(TArray<ActionBind> acts, int flagid, ActionBind other) {
		if (acts == null) {
			return this;
		}
		for (Iterator<ActionBind> it = acts.iterator(); it.hasNext();) {
			ActionBind act = it.next();
			if (act != null && act != other) {
				float x = act.getX();
				float y = act.getY();
				float w = act.getWidth();
				float h = act.getHeight();
				int dstTileX = pixelsToTilesWidth(x);
				int dstTileY = pixelsToTilesHeight(y);
				int dstTileWidth = dstTileX + pixelsToTilesWidth(w);
				int dstTileHeight = dstTileY + pixelsToTilesWidth(h);
				int fieldWidth = _mapArrays[0].length;
				int fieldHeight = _mapArrays.length;
				for (int i = 0; i < fieldHeight; i++) {
					for (int j = 0; j < fieldWidth; j++) {
						if (j > dstTileX && j < dstTileWidth && i > dstTileY && i < dstTileHeight) {
							_mapArrays[i][j] = flagid;
						}
					}
				}
			}
		}
		this._mapDirty = true;
		return this;
	}

	public boolean isValid(int tx, int ty) {
		return contains(tx, ty);
	}

	public boolean isLimited() {
		return _moveLimited != null && _moveLimited.length > 0;
	}

	public int[] getLimit() {
		return _moveLimited;
	}

	public Field2D setLimit(int... limit) {
		this._moveLimited = limit;
		return this;
	}

	public Field2D setAllowMove(int... args) {
		this._allowMove.addAll(args);
		return this;
	}

	public boolean contains(int x, int y) {
		return x >= 0 && x < _width && y >= 0 && y < _height;
	}

	public Field2D replaceType(int oldid, int newid) {
		int w = _mapArrays[0].length;
		int h = _mapArrays.length;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int id = _mapArrays[i][j];
				if (id == oldid) {
					_mapArrays[i][j] = newid;
				}
			}
		}
		this._mapDirty = true;
		return this;
	}

	public boolean isTileType(int x, int y, int type) {
		return getTileType(x, y) == type;
	}

	public int getTileType(int x, int y) {
		try {
			if (!contains(x, y)) {
				return -1;
			}
			return _mapArrays[y][x];
		} catch (Throwable e) {
			return -1;
		}
	}

	public Field2D setTileType(int x, int y, int tile) {
		try {
			if (!contains(x, y)) {
				return this;
			}
			this._mapArrays[y][x] = tile;
			this._mapDirty = true;
		} catch (Throwable e) {
		}
		return this;
	}

	protected int[][] getThisMap() {
		return _mapArrays;
	}

	public int[][] getNewMap() {
		return CollectionUtils.copyOf(_mapArrays);
	}

	public int[][] getMap() {
		final boolean isNull = (_proxyArrays == null);
		final int length = _mapArrays.length;
		if (isNull || ((length != _proxyArrays.length) || (_mapArrays[0].length != _proxyArrays[0].length))
				|| (_proxyArrays == _mapArrays)) {
			_proxyArrays = getNewMap();
		} else if (_mapDirty) {
			for (int i = 0; i < length; i++) {
				System.arraycopy(_mapArrays[i], 0, _proxyArrays[i], 0, length);
			}
			_mapDirty = false;
		}
		return _proxyArrays;
	}

	protected Field2D setMap(int[][] arrays) {
		if (arrays == null || arrays == this._mapArrays) {
			return this;
		}
		if (this._mapArrays != null
				&& (arrays.length == this._mapArrays.length && arrays[0].length == _mapArrays[0].length)) {
			final int w = arrays[0].length;
			final int h = arrays.length;
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					_mapArrays[i][j] = arrays[i][j];
				}
			}
		} else {
			this._mapArrays = CollectionUtils.copyOf(arrays);
		}
		this._mapDirty = true;
		return this;
	}

	public boolean isDirty() {
		return this._mapDirty;
	}

	public Field2D setDirty(boolean d) {
		this._mapDirty = d;
		return this;
	}

	public int getPixelsAtFieldType(Vector2f pos) {
		return getPixelsAtFieldType(pos.x, pos.y);
	}

	public int getPixelsAtFieldType(float x, float y) {
		int itsX = pixelsToTilesWidth(x);
		int itsY = pixelsToTilesHeight(y);
		return getTileType(itsX, itsY);
	}

	public IntArray getAllowMove() {
		return this._allowMove;
	}

	public boolean inside(int x, int y) {
		return CollisionHelper.intersects(0, 0, getDrawWidth(), getDrawHeight(), x, y);
	}

	public boolean inside(float x, float y) {
		return inside((int) x, (int) y);
	}

	public boolean isHit(Vector2f point) {
		return isHit(point.x(), point.y());
	}

	public boolean isHit(PointI point) {
		return isHit(point.x, point.y);
	}

	public boolean isHit(PointF point) {
		return isHit((int) point.x, (int) point.y);
	}

	public boolean isPixelHit(float px, float py) {
		return isHit(pixelsToTilesWidth(px), pixelsToTilesHeight(py));
	}

	public Vector2f getPixelLimitPos(float px, float py) {
		if (!isHit(pixelsToTilesWidth(px), pixelsToTilesHeight(py))) {
			return new Vector2f(px, py);
		}
		return null;
	}

	public boolean isNotMovable(int px, int py) {
		return !isHit(px, py);
	}

	public boolean isMovable(int px, int py) {
		return isHit(px, py);
	}

	public boolean isHit(int px, int py) {
		int type = get(_mapArrays, px, py);
		if (type == -1 && !_allowMove.contains(type)) {
			return false;
		}
		if (_moveLimited != null) {
			final int size = _moveLimited.length - 1;
			for (int i = size; i > -1; i--) {
				if (_moveLimited[i] == type) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 检测指定范围内存在的不允许移动的标记数量
	 * 
	 * @param x
	 * @param y
	 * @param scopeX
	 * @param scopeY
	 * @return
	 */
	public int getAdjacentNotMoves(int x, int y, int scopeX, int scopeY) {
		int startX = x - scopeX;
		int startY = y - scopeY;
		int endX = x + scopeX;
		int endY = y + scopeY;
		int newX = startX;
		int newY = startY;
		int wallCounter = 0;
		for (newY = startY; newY <= endY; newY++) {
			for (newX = startX; newX <= endX; newX++) {
				if (!(newX == x && newY == y)) {
					if (!isHit(newX, newY)) {
						wallCounter += 1;
					}
				}
			}
		}
		return wallCounter;
	}

	public Vector2f getRandomPos() {
		int tx = MathUtils.nextInt(0, _width - 1);
		int ty = MathUtils.nextInt(0, _height - 1);
		return new Vector2f(tx, ty);
	}

	public Vector2f getTileCollision(ActionBind bind, float newX, float newY) {
		if (bind == null) {
			return null;
		}
		return getTileCollision(bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight(), newX, newY);
	}

	public Vector2f getTileCollision(float srcX, float srcY, float srcWidth, float srcHeight, float newX, float newY) {
		newX = MathUtils.iceil(newX);
		newY = MathUtils.iceil(newY);

		float fromX = MathUtils.min(srcX, newX);
		float fromY = MathUtils.min(srcY, newY);
		float toX = MathUtils.max(srcX, newX);
		float toY = MathUtils.max(srcY, newY);

		int fromTileX = pixelsToTilesWidth(fromX);
		int fromTileY = pixelsToTilesHeight(fromY);
		int toTileX = pixelsToTilesWidth(toX + srcWidth - 1f);
		int toTileY = pixelsToTilesHeight(toY + srcHeight - 1f);

		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if ((x < 0) || (x >= getWidth())) {
					return new Vector2f(x, y);
				}
				if ((y < 0) || (y >= getHeight())) {
					return new Vector2f(x, y);
				}
				if (!this.isHit(x, y)) {
					return new Vector2f(x, y);
				}
			}
		}

		return null;
	}

	public boolean checkTileCollision(ActionBind bind, float newX, float newY) {
		if (bind == null) {
			return false;
		}
		return checkTileCollision(bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight(), newX, newY);
	}

	public boolean checkTileCollision(float srcX, float srcY, float srcWidth, float srcHeight, float newX, float newY) {
		if (_tileCollisionListener != null) {
			return _tileCollisionListener.checkTileCollision(srcX, srcY, srcWidth, srcHeight, newX, newY);
		}
		newX = MathUtils.iceil(newX);
		newY = MathUtils.iceil(newY);

		float fromX = MathUtils.min(srcX, newX);
		float fromY = MathUtils.min(srcY, newY);
		float toX = MathUtils.max(srcX, newX);
		float toY = MathUtils.max(srcY, newY);

		int fromTileX = pixelsToTilesWidth(fromX);
		int fromTileY = pixelsToTilesHeight(fromY);
		int toTileX = pixelsToTilesWidth(toX + srcWidth - 1f);
		int toTileY = pixelsToTilesHeight(toY + srcHeight - 1f);

		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if ((x < 0) || (x >= getWidth())) {
					return true;
				}
				if ((y < 0) || (y >= getHeight())) {
					return true;
				}
				if (!this.isHit(x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	public TileCollisionListener getTileCollisionListener() {
		return this._tileCollisionListener;
	}

	public Field2D setTileCollisionListener(TileCollisionListener t) {
		this._tileCollisionListener = t;
		return this;
	}

	public boolean notWidth(final int x) {
		return x < 0 || x >= _width;
	}

	public boolean notHeight(final int y) {
		return y < 0 || y >= _height;
	}

	public Vector2f toXY(int index) {
		return toXY(index, this._width, this._height);
	}

	public int[][] neighbors(int px, int py, boolean bevel, boolean diagonal) {
		final int[][] pos = new int[diagonal ? 8 : 4][2];
		if (bevel) {
			insertArrays(pos, 0, px - 1, py - 1);
			insertArrays(pos, 0, px + 1, py - 1);
			insertArrays(pos, 0, px + 1, py + 1);
			insertArrays(pos, 0, px - 1, py + 1);
			insertArrays(pos, 0, px + 1, py);
			insertArrays(pos, 0, px - 1, py);
			if (diagonal) {
				insertArrays(pos, 0, px, py - 1);
				insertArrays(pos, 0, px, py + 1);
			}
		} else {
			insertArrays(pos, 0, px, py - 1);
			insertArrays(pos, 0, px + 1, py);
			insertArrays(pos, 0, px, py + 1);
			insertArrays(pos, 0, px - 1, py);
			if (diagonal) {
				insertArrays(pos, 0, px - 1, py - 1);
				insertArrays(pos, 0, px + 1, py - 1);
				insertArrays(pos, 0, px + 1, py + 1);
				insertArrays(pos, 0, px - 1, py + 1);
			}
		}
		return pos;
	}

	public TArray<Vector2f> neighbors(Vector2f pos, boolean diagonal) {
		return neighbors(pos, false, diagonal);
	}

	public TArray<Vector2f> neighbors(Vector2f pos, boolean bevel, boolean diagonal) {
		if (_result == null) {
			_result = new TArray<Vector2f>(diagonal ? 8 : 4);
		} else {
			_result.clear();
		}
		int x = pos.x();
		int y = pos.y();
		if (bevel) {
			_result.add(new Vector2f(x - 1, y - 1));
			_result.add(new Vector2f(x + 1, y - 1));
			_result.add(new Vector2f(x + 1, y + 1));
			_result.add(new Vector2f(x - 1, y + 1));
			_result.add(new Vector2f(x + 1, y));
			_result.add(new Vector2f(x - 1, y));
			if (diagonal) {
				_result.add(new Vector2f(x, y - 1));
				_result.add(new Vector2f(x, y + 1));
			}
		} else {
			_result.add(new Vector2f(x, y - 1));
			_result.add(new Vector2f(x + 1, y));
			_result.add(new Vector2f(x, y + 1));
			_result.add(new Vector2f(x - 1, y));
			if (diagonal) {
				_result.add(new Vector2f(x - 1, y - 1));
				_result.add(new Vector2f(x + 1, y - 1));
				_result.add(new Vector2f(x + 1, y + 1));
				_result.add(new Vector2f(x - 1, y + 1));
			}
		}
		return _result;
	}

	public PointI getIndexToPixelPos(int idx) {
		PointI pos = getIndexToPos(idx);
		if (pos != null) {
			pos.set(tilesToWidthPixels(pos.x), tilesToHeightPixels(pos.y));
		}
		return pos;
	}

	public PointI getIndexToPos(int idx) {
		int count = 0;
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				if (idx == count) {
					return new PointI(x, y);
				}
				count++;
			}
		}
		return null;
	}

	protected int get(int[][] mapArrays, int px, int py) {
		try {
			if (contains(px, py)) {
				return mapArrays[py][px];
			} else {
				return -1;
			}
		} catch (Throwable e) {
			return -1;
		}
	}

	protected int get(int[][] mapArrays, Vector2f point) {
		return get(mapArrays, point.x(), point.y());
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
		int w = _mapArrays[0].length;
		int h = _mapArrays.length;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				_mapArrays[i][j] = val;
			}
		}
		this._mapDirty = true;
		return this;
	}

	public Tile getTileImpl() {
		return _tileImpl;
	}

	public Field2D setTileImpl(Tile tileImpl) {
		this._tileImpl = tileImpl;
		return this;
	}

	public Vector2f moveCursor(float posX, float posY, int dir, Vector2f result) {
		if (dir == TLEFT) {
			if (--posX < 0f) {
				posX = 0f;
			}
		} else if (dir == TRIGHT) {
			if (++posX >= getWidth()) {
				posX = getWidth() - 1f;
			}
		} else if (dir == TUP) {
			if (--posY < 0f) {
				posY = 0f;
			}
		} else if (dir == TDOWN) {
			if (++posY >= getHeight()) {
				posY = getHeight() - 1f;
			}
		}
		if (result != null) {
			return result.set(posX, posY);
		}
		return Vector2f.at(posX, posY);
	}

	public Field2D moveCursor(Vector2f cursorTilePos, int dir) {
		moveCursor(cursorTilePos.x, cursorTilePos.y, dir, cursorTilePos);
		return this;
	}

	public Field2D moveCursorPixel(Vector2f cursorPixelPos, int dir) {
		float x = pixelsToTilesWidth(cursorPixelPos.x);
		float y = pixelsToTilesHeight(cursorPixelPos.y);
		moveCursor(x, y, dir, cursorPixelPos);
		cursorPixelPos.set(tilesToWidthPixels(x), tilesToHeightPixels(y));
		return this;
	}

	public int clampX(int x) {
		return MathUtils.clamp(x, 0, this._width - 1);
	}

	public int clampY(int y) {
		return MathUtils.clamp(y, 0, this._height - 1);
	}

	public boolean canOffsetTile(float x, float y) {
		return this._offset.x >= x - _width && this._offset.x <= x + _width && this._offset.y >= y - _height
				&& this._offset.y <= y + _height;
	}

	public float getIsometricType(float px, float py) {
		float halfWidth = this.getDrawWidth() * 0.5f;
		if (px < 0 || px >= halfWidth || py < 0 || py >= this.getDrawHeight()) {
			return -1;
		}
		PointI point = this.pixelsIsometricToTiles(px, py);
		return this.getTileType(point.x, point.y);
	}

	public Field2D setIsometricType(float px, float py, int t) {
		float halfWidth = this.getDrawWidth() * 0.5f;
		if (px < 0 || px >= halfWidth || py < 0 || py >= this.getDrawHeight()) {
			return this;
		}
		PointI point = this.pixelsIsometricToTiles(px, py);
		return this.setTileType(point.x, point.y, t);
	}

	public int getNeighborType(float px, float py, int d) {
		PointI point = this.pixelsHexagonToTiles(px, py);
		int[] n = NEIGHBORS[point.x & 1][d];
		int nx = point.x + n[0];
		int ny = point.y + n[1];
		if (nx < 0 || nx >= getHexagonWidth() || ny < 0 || ny >= getHexagonHeight()) {
			return -1;
		}
		return getTileType(nx, ny);
	}

	public Field2D setNeighborType(float px, float py, int d, int t) {
		PointI point = this.pixelsHexagonToTiles(px, py);
		int[] n = NEIGHBORS[point.x & 1][d];
		int nx = point.x + n[0];
		int ny = point.y + n[1];
		if (nx < 0 || nx >= getHexagonWidth() || ny < 0 || ny >= getHexagonHeight()) {
			return this;
		}
		return setTileType(nx, ny, t);
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public Field2D setOffset(Vector2f offset) {
		this._offset = offset;
		return this;
	}

	public int offsetXPixel(int x) {
		return x - _offset.x();
	}

	public int offsetYPixel(int y) {
		return y - _offset.y();
	}

	public float freeX(float v) {
		return tilesToWidthPixels(v + 0.5f);
	}

	public float freeY(float v) {
		return tilesToHeightPixels(v + 0.5f);
	}

	public Field2D switchMap(MapSwitchMaker s) {
		if (s == null) {
			return this;
		}
		final int w = _width;
		final int h = _height;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (s != null) {
					final int v = _mapArrays[j][i];
					s.loop(v, tilesToWidthPixels(i), tilesToHeightPixels(j));
				}

			}
		}
		return this;
	}

	public int distanceFromEdgeTiles(int x, int y) {
		return MathUtils.min(MathUtils.min(x, _width - x), MathUtils.min(y, _height - y));
	}

	public boolean onTilesMap(int x, int y) {
		return (x >= 0 && y >= 0 && x < _width && y < _height);
	}

	public boolean onTilesEdge(int x, int y) {
		return !(x != 0 && y != 0 && x != _width - 1 && y != _height - 1);
	}

	public Field2D setTilesRect(int cx, int cy, int cwidth, int cheight, int type) {
		if (cx < 0) {
			cwidth += cx;
			cx = 0;
		}
		if (cy < 0) {
			cheight += cy;
			cy = 0;
		}
		if (cx + cwidth > _width) {
			cwidth = _width - cx;
		}
		if (cy + cheight > _height) {
			cheight = _height - cy;
		}
		for (int i = 0; i < cwidth; i++) {
			for (int j = 0; j < cheight; j++) {
				setTileType(cx + i, cy + j, type);
			}
		}
		return this;
	}

	public boolean checkTilesRect(int cx, int cy, int cwidth, int cheight, int type) {
		if (cx < 0) {
			cwidth += cx;
			cx = 0;
		}
		if (cy < 0) {
			cheight += cy;
			cy = 0;
		}
		if (cx + cwidth > _width) {
			cwidth = _width - cx;
		}
		if (cy + cheight > _height) {
			cheight = _height - cy;
		}
		for (int i = 0; i < cwidth; i++) {
			for (int j = 0; j < cheight; j++) {
				if (isTileType(cx + i, cy + j, type)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean inColType(int x, int type) {
		for (int i = 0; i < _height; i++) {
			if (isTileType(x, i, type)) {
				return true;
			}
		}
		return false;
	}

	public boolean inRowType(int y, int type) {
		for (int i = 0; i < _width; i++) {
			if (isTileType(i, y, type)) {
				return true;
			}
		}
		return false;
	}

	public float getColAt(float worldX, float x) {
		return getColAt(worldX, x, true);
	}

	public float getColAt(float worldX, float x, boolean b) {
		float result = MathUtils.floor((worldX - x) / _tileWidth);
		if (b) {
			return result < 0f ? 0f : (result >= _width ? _width - 1 : result);
		}
		return result;
	}

	public float getRowAt(float worldY, float y) {
		return getRowAt(worldY, y, true);
	}

	public float getRowAt(float worldY, float y, boolean b) {
		float result = MathUtils.floor((worldY - y) / _tileHeight);
		if (b) {
			return result < 0f ? 0f : (result >= _height ? _height - 1 : result);
		}
		return result;
	}

	public float getColToPos(float x) {
		return getColToPos(x, _width);
	}

	public float getColToPos(float x, float column) {
		return getColToPos(x, column, false);
	}

	public float getColToPos(float x, float column, boolean midpoint) {
		return x + column * _tileWidth + (midpoint ? _tileWidth * 0.5f : 0f);
	}

	public float getRowToPos(float y) {
		return getRowToPos(y, _height);
	}

	public float getRowToPos(float y, float row) {
		return getRowToPos(y, row, false);
	}

	public float getRowToPos(float y, float row, boolean midpoint) {
		return y + row * _tileHeight + (midpoint ? _tileHeight * 0.5f : 0f);
	}

	@Override
	public boolean isEmpty() {
		return _mapArrays == null || _mapArrays.length == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public Field2D setName(String n) {
		this._fieldName = n;
		return this;
	}

	public String getName() {
		return this._fieldName;
	}

	@Override
	public int size() {
		return _width * _height;
	}

	@Override
	public void clear() {
		set(new int[_height][_width], _width, _height);
	}

	@Override
	public String toString() {
		return toString(LSystem.COMMA);
	}

	public String toString(char split) {
		if (isEmpty()) {
			return "[]";
		}
		StrBuilder buffer = new StrBuilder(size() * 2 + _height + 2);
		buffer.append('[');
		buffer.append(LSystem.LS);
		for (int i = 0; i < _height; i++) {
			for (int j = 0; j < _width; j++) {
				buffer.append(_mapArrays[i][j]);
				if (j < _width - 1) {
					buffer.append(split);
				}
			}
			buffer.append(LSystem.LS);
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public void close() {
		if (_mapArrays != null) {
			_mapArrays = null;
		}
	}

}
