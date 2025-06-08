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
package loon.action.collision;

import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.collision.CollisionGrid.TraverseCallback;
import loon.action.map.Side;
import loon.geom.PointF;
import loon.geom.RectF;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.ObjectMap.Keys;

/**
 * 一个碰撞物体自动管理用类,和CollisionManager不同,它会自动获得碰撞后新的物体坐标
 */
public class CollisionWorld implements LRelease {

	private static class WorldCollisionFilter extends CollisionFilter {

		private final CollisionWorld _world;
		private final TArray<ActionBind> _visited;
		private final CollisionFilter _filter;

		public WorldCollisionFilter(CollisionWorld world, TArray<ActionBind> v, CollisionFilter f) {
			this._world = world;
			this._visited = v;
			this._filter = f;
		}

		@Override
		public CollisionResult filter(ActionBind obj, ActionBind other) {
			if (_visited.contains(other)) {
				return null;
			}
			CollisionResult result = null;
			if (_filter == null) {
				result = _world._worldCollisionFilter.filter(obj, other);
			} else {
				result = _filter.filter(obj, other);
			}
			final CollisionActionQuery<ActionBind> actionQuery = _world._collisionActionQuery;
			if (actionQuery != null) {
				final int dir = Side.getCollisionSide(obj.getRectBox(), other.getRectBox());
				final boolean query = actionQuery.checkQuery(obj, other, dir);
				if (query) {
					actionQuery.onCollisionResult(obj, other, dir);
					return result;
				} else {
					return null;
				}
			}
			return result;
		}
	}

	public class Cell {
		public int itemCount = 0;
		public float x;
		public float y;
		public ObjectMap<ActionBind, Boolean> items = new ObjectMap<ActionBind, Boolean>();
	}

	private final static float DELTA = 1e-5f;

	private CollisionFilter _worldCollisionFilter;

	private final Screen _gameScreen;

	private final RectF detectCollisionDiff = new RectF();
	private final PointF nearestCorner = new PointF();
	private final PointF segmentIntersectionIndices_ti = new PointF();
	private final PointF segmentIntersectionIndicesn1 = new PointF();
	private final PointF segmentIntersectionIndicesn2 = new PointF();
	private final CollisionData segmentIntersectionIndicescol = new CollisionData();

	private ObjectMap<ActionBind, RectF> rects = new ObjectMap<ActionBind, RectF>();
	private ObjectMap<Float, ObjectMap<Float, Cell>> rows = new ObjectMap<Float, ObjectMap<Float, Cell>>();
	private ObjectMap<Cell, Boolean> nonEmptyCells = new ObjectMap<Cell, Boolean>();
	private CollisionGrid grid = new CollisionGrid();

	private boolean _tileMode = false;
	private boolean _closed = false;

	private boolean _autoRemoveItem = false;
	private boolean _autoAddItem = true;

	private CollisionManager collisionManager;

	private final float cellSizeX;
	private final float cellSizeY;

	private final TArray<Cell> getCellsTouchedBySegment_visited = new TArray<Cell>();

	private final RectF remove_c = new RectF();
	private final TArray<ActionBind> project_visited = new TArray<ActionBind>();
	private final RectF project_c = new RectF();
	private final ObjectMap<ActionBind, Boolean> project_dictItemsInCellRect = new ObjectMap<ActionBind, Boolean>();

	private final RectF add_c = new RectF();
	private final RectF update_c1 = new RectF();
	private final RectF update_c2 = new RectF();

	private final TArray<ActionBind> check_visited = new TArray<ActionBind>();

	private final Collisions check_cols = new Collisions();
	private final Collisions check_projectedCols = new Collisions();
	private final CollisionResult.Result check_result = new CollisionResult.Result();

	private CollisionActionQuery<ActionBind> _collisionActionQuery;

	public CollisionWorld() {
		this(null);
	}

	public CollisionWorld(Screen s) {
		this(s, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, true);
	}

	public CollisionWorld(Screen s, boolean mode) {
		this(s, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, mode);
	}

	public CollisionWorld(Screen s, float cellx, float celly, boolean mode) {
		this._gameScreen = s;
		this.cellSizeX = cellx;
		this.cellSizeY = celly;
		this._tileMode = mode;
		this._worldCollisionFilter = CollisionFilter.getDefault();
		this._autoRemoveItem = false;
		this._autoAddItem = true;
	}

	public CollisionManager getCollisionManager() {
		if (_closed) {
			return collisionManager;
		}
		if (collisionManager == null) {
			collisionManager = new CollisionManager();
		}
		collisionManager.initialize(MathUtils.iceil(cellSizeX), MathUtils.iceil(cellSizeY));
		return collisionManager;
	}

	public CollisionData detectCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2,
			float goalX, float goalY) {
		if (_closed) {
			return null;
		}
		CollisionData col = segmentIntersectionIndicescol;
		float dx = goalX - x1;
		float dy = goalY - y1;

		CollisionHelper.getDiff(x1, y1, w1, h1, x2, y2, w2, h2, detectCollisionDiff);
		float x = detectCollisionDiff.x;
		float y = detectCollisionDiff.y;
		float w = detectCollisionDiff.width;
		float h = detectCollisionDiff.height;

		boolean overlaps = false;
		float ti = -1f;
		float nx = 0, ny = 0;

		if (CollisionHelper.containsPoint(x, y, w, h, 0, 0, DELTA)) {
			CollisionHelper.getNearestCorner(x, y, w, h, 0, 0, nearestCorner);
			float px = nearestCorner.x;
			float py = nearestCorner.y;
			float wi = MathUtils.min(w1, MathUtils.abs(px));
			float hi = MathUtils.min(h1, MathUtils.abs(py));
			ti = -wi * hi;
			// ti = -wi * h1;
			overlaps = true;
		} else {
			boolean intersect = CollisionHelper.getSegmentIntersectionIndices(x, y, w, h, 0, 0, dx, dy,
					-Float.MAX_VALUE, Float.MAX_VALUE, segmentIntersectionIndices_ti, segmentIntersectionIndicesn1,
					segmentIntersectionIndicesn2);
			float ti1 = segmentIntersectionIndices_ti.x;
			float ti2 = segmentIntersectionIndices_ti.y;
			float nx1 = segmentIntersectionIndicesn1.x;
			float ny1 = segmentIntersectionIndicesn1.y;

			if (intersect && ti1 < 1 && MathUtils.abs(ti1 - ti2) >= DELTA && (0 < ti1 + DELTA || 0 == ti1 && ti2 > 0)) {
				ti = ti1;
				nx = nx1;
				ny = ny1;
				overlaps = false;
			}
		}
		if (ti == -1f) {
			return null;
		}
		float tx, ty;

		if (overlaps) {
			if (dx == 0 && dy == 0) {
				CollisionHelper.getNearestCorner(x, y, w, h, 0, 0, nearestCorner);
				float px = nearestCorner.x;
				float py = nearestCorner.y;
				if (MathUtils.abs(px) < MathUtils.abs(py)) {
					py = 0;
				} else {
					px = 0;
				}
				nx = MathUtils.sign(px);
				ny = MathUtils.sign(py);
				tx = x1 + px;
				ty = y1 + py;
			} else {
				boolean intersect = CollisionHelper.getSegmentIntersectionIndices(x, y, w, h, 0, 0, dx, dy,
						-Float.MAX_VALUE, 1, segmentIntersectionIndices_ti, segmentIntersectionIndicesn1,
						segmentIntersectionIndicesn2);
				float ti1 = segmentIntersectionIndices_ti.x;
				nx = segmentIntersectionIndicesn1.x;
				ny = segmentIntersectionIndicesn1.y;
				if (!intersect) {
					return null;
				}
				tx = x1 + dx * ti1;
				ty = y1 + dy * ti1;
			}
		} else {
			tx = x1;
			ty = y1;
		}
		col.set(overlaps, ti, dx, dy, nx, ny, tx, ty, x1, y1, w1, h1, x2, y2, w2, h2);
		return col;
	}

	public void setTileMode(boolean tileMode) {
		this._tileMode = tileMode;
	}

	public boolean isTileMode() {
		return _tileMode;
	}

	private void addItemToCell(ActionBind bind, float cx, float cy) {
		if (_closed) {
			return;
		}
		if (!rows.containsKey(cy)) {
			rows.put(cy, new ObjectMap<Float, Cell>());
		}
		ObjectMap<Float, Cell> row = rows.get(cy);
		if (!row.containsKey(cx)) {
			row.put(cx, new Cell());
		}
		Cell cell = row.get(cx);

		nonEmptyCells.put(cell, true);
		if (!cell.items.containsKey(bind)) {
			cell.items.put(bind, true);
			cell.itemCount = cell.itemCount + 1;
		}
	}

	private boolean removeItemFromCell(ActionBind bind, float cx, float cy) {
		if (_closed) {
			return false;
		}
		if (!rows.containsKey(cy)) {
			return false;
		}
		ObjectMap<Float, Cell> row = rows.get(cy);
		if (!row.containsKey(cx)) {
			return false;
		}
		Cell cell = row.get(cx);
		if (!cell.items.containsKey(bind)) {
			return false;
		}
		cell.items.remove(bind);
		cell.itemCount = cell.itemCount - 1;
		if (cell.itemCount == 0) {
			nonEmptyCells.remove(cell);
		}
		return true;
	}

	private ObjectMap<ActionBind, Boolean> getDictItemsInCellRect(float cl, float ct, float cw, float ch,
			ObjectMap<ActionBind, Boolean> result) {
		if (_closed) {
			return null;
		}
		result.clear();
		for (float cy = ct; cy < ct + ch; cy++) {
			if (rows.containsKey(cy)) {
				ObjectMap<Float, Cell> row = rows.get(cy);
				for (float cx = cl; cx < cl + cw; cx++) {
					if (row.containsKey(cx)) {
						Cell cell = row.get(cx);
						if (cell.itemCount > 0) {
							Keys<ActionBind> keys = cell.items.keys();
							for (ActionBind bind : keys) {
								result.put(bind, true);
							}
						}
					}
				}
			}
		}
		return result;
	}

	public TArray<Cell> getCellsTouchedBySegment(float x1, float y1, float x2, float y2, final TArray<Cell> result) {
		if (_closed) {
			return null;
		}
		result.clear();
		getCellsTouchedBySegment_visited.clear();
		final TArray<Cell> visited = getCellsTouchedBySegment_visited;

		grid.traverse(cellSizeX, cellSizeY, x1, y1, x2, y2, new TraverseCallback() {
			@Override
			public void onTraverse(float cx, float cy) {
				if (!rows.containsKey(cy)) {
					return;
				}
				ObjectMap<Float, Cell> row = rows.get(cy);
				if (!row.containsKey(cx)) {
					return;
				}
				Cell cell = row.get(cx);
				if (visited.contains(cell)) {
					return;
				}
				visited.add(cell);
				result.add(cell);
			}
		});

		return result;
	}

	public Collisions project(ActionBind bind, float x, float y, float w, float h, float goalX, float goalY,
			Collisions collisions) {
		return project(bind, x, y, w, h, goalX, goalY, _worldCollisionFilter, collisions);
	}

	public Collisions project(ActionBind bind, float x, float y, float w, float h, float goalX, float goalY,
			CollisionFilter filter, Collisions collisions) {
		if (_closed) {
			return null;
		}
		collisions.clear();
		TArray<ActionBind> visited = project_visited;
		visited.clear();
		if (bind != null) {
			visited.add(bind);
		}
		float tl = MathUtils.min(goalX, x);
		float tt = MathUtils.min(goalY, y);
		float tr = MathUtils.max(goalX + w, x + w);
		float tb = MathUtils.max(goalY + h, y + h);

		float tw = tr - tl;
		float th = tb - tt;

		grid.toCellRect(cellSizeX, cellSizeY, tl, tt, tw, th, project_c);
		float cl = project_c.x, ct = project_c.y, cw = project_c.width, ch = project_c.height;
		ObjectMap<ActionBind, Boolean> dictItemsInCellRect = getDictItemsInCellRect(cl, ct, cw, ch,
				project_dictItemsInCellRect);
		for (ActionBind other : dictItemsInCellRect.keys()) {
			if (!visited.contains(other)) {
				visited.add(other);
				CollisionResult response = filter.filter(bind, other);
				if (response != null) {
					RectF rect = getRect(other);
					if (rect != null) {
						float ox = rect.x, oy = rect.y, ow = rect.width, oh = rect.height;
						CollisionData col = detectCollision(x, y, w, h, ox, oy, ow, oh, goalX, goalY);

						if (col != null) {
							collisions.add(col.overlaps, col.ti, col.move.x, col.move.y, col.normal.x, col.normal.y,
									col.touch.x, col.touch.y, col.itemRect.x, col.itemRect.y, col.itemRect.width,
									col.itemRect.height, col.otherRect.x, col.otherRect.y, col.otherRect.width,
									col.otherRect.height, bind, other, response);
						}
					}
				}
			}
		}
		if (_tileMode) {
			collisions.sort();
		}
		return collisions;
	}

	public CollisionWorld syncBindToObject() {
		if (_closed) {
			return this;
		}
		for (Entries<ActionBind, RectF> it = rects.entries(); it.hasNext();) {
			Entry<ActionBind, RectF> obj = it.next();
			if (obj != null) {
				RectF rect = obj.value;
				ActionBind act = obj.key;
				if (rect != null && act != null) {
					act.setLocation(rect.x, rect.y);
					act.setSize(rect.width, rect.height);
				}
			}
		}
		return this;
	}

	public CollisionWorld syncObjectToBind() {
		if (_closed) {
			return this;
		}
		for (Entries<ActionBind, RectF> it = rects.entries(); it.hasNext();) {
			Entry<ActionBind, RectF> obj = it.next();
			if (obj != null) {
				RectF rect = obj.value;
				ActionBind act = obj.key;
				if (rect != null && act != null) {
					rect.set(act.getX(), act.getY(), act.getWidth(), act.getHeight());
				}
			}
		}
		return this;
	}

	public RectF getRect(ActionBind bind) {
		if (_closed) {
			return null;
		}
		return rects.get(bind);
	}

	public int countCells() {
		if (_closed) {
			return 0;
		}
		int count = 0;
		for (ObjectMap<Float, Cell> row : rows.values()) {
			for (Float x : row.keys()) {
				if (x != null) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean hasItem(ActionBind bind) {
		if (_closed) {
			return false;
		}
		return rects.containsKey(bind);
	}

	public int countItems() {
		if (_closed) {
			return 0;
		}
		return rects.size();
	}

	public PointF toWorld(float cx, float cy, PointF result) {
		CollisionGrid.toWorld(cellSizeX, cellSizeY, cx, cy, result);
		return result;
	}

	public PointF toCell(float x, float y, PointF result) {
		CollisionGrid.toCell(cellSizeX, cellSizeY, x, y, result);
		return result;
	}

	public CollisionWorld add(ActionBind... binds) {
		for (ActionBind act : binds) {
			if (act != null) {
				add(act);
			}
		}
		return this;
	}

	public ActionBind add(ActionBind bind) {
		if (_closed) {
			return null;
		}
		return add(bind, bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight());
	}

	public ActionBind add(ActionBind bind, float x, float y, float w, float h) {
		if (_closed) {
			return null;
		}
		if (rects.containsKey(bind)) {
			return bind;
		}
		if (_gameScreen != null) {
			_gameScreen.add(bind);
		}
		rects.put(bind, new RectF(x, y, w, h));
		grid.toCellRect(cellSizeX, cellSizeY, x, y, w, h, add_c);
		float cl = add_c.x, ct = add_c.y, cw = add_c.width, ch = add_c.height;
		for (float cy = ct; cy < ct + ch; cy++) {
			for (float cx = cl; cx < cl + cw; cx++) {
				addItemToCell(bind, cx, cy);
			}
		}
		return bind;
	}

	public void remove(ActionBind bind) {
		if (_closed) {
			return;
		}
		RectF rect = getRect(bind);
		if (rect != null) {
			float x = rect.x, y = rect.y, w = rect.width, h = rect.height;
			if (_gameScreen != null) {
				_gameScreen.remove(bind);
			}
			rects.remove(bind);
			grid.toCellRect(cellSizeX, cellSizeY, x, y, w, h, remove_c);
			float cl = remove_c.x, ct = remove_c.y, cw = remove_c.width, ch = remove_c.height;

			for (float cy = ct; cy < ct + ch; cy++) {
				for (float cx = cl; cx < cl + cw; cx++) {
					removeItemFromCell(bind, cx, cy);
				}
			}
		}
	}

	public void update(ActionBind bind, float x2, float y2) {
		if (_closed) {
			return;
		}
		RectF rect = getRect(bind);
		if (rect != null) {
			float w = rect.width, h = rect.height;
			update(bind, x2, y2, w, h);
		}
	}

	public void update(ActionBind bind, float x2, float y2, float w2, float h2) {
		if (_closed) {
			return;
		}
		RectF rect = getRect(bind);
		if (rect != null) {
			float x1 = rect.x, y1 = rect.y, w1 = rect.width, h1 = rect.height;
			if (!MathUtils.equal(x1, x2) || !MathUtils.equal(y1, y2) || !MathUtils.equal(w1, w2)
					|| !MathUtils.equal(h1, h2)) {

				// size limit
				RectF c1 = grid.toCellRect(cellSizeX, cellSizeY, x1, y1, w1, h1, update_c1);
				RectF c2 = grid.toCellRect(cellSizeX, cellSizeY, x2, y2, w2, h2, update_c2);

				float cl1 = MathUtils.ceil(c1.x), ct1 = MathUtils.ceil(c1.y), cw1 = MathUtils.ceil(c1.width),
						ch1 = MathUtils.ceil(c1.height);
				float cl2 = MathUtils.ceil(c2.x), ct2 = MathUtils.ceil(c2.y), cw2 = MathUtils.ceil(c2.width),
						ch2 = MathUtils.ceil(c2.height);

				if (!MathUtils.equal(cl1, cl2) || !MathUtils.equal(ct1, ct2) || !MathUtils.equal(cw1, cw2)
						|| !MathUtils.equal(ch1, ch2)) {
					float cr1 = cl1 + cw1 - 1, cb1 = ct1 + ch1 - 1;
					float cr2 = cl2 + cw2 - 1, cb2 = ct2 + ch2 - 1;
					boolean cyOut;

					if (_autoAddItem) {
						for (float cy = ct2; cy <= cb2; cy++) {
							cyOut = cy < ct1 || cy > cb1;
							for (float cx = cl2; cx <= cr2; cx++) {
								if (cyOut || cx < cl1 || cy > cr1) {
									addItemToCell(bind, cx, cy);
								}
							}
						}
					}
					if (_autoRemoveItem) {
						for (float cy = ct1; cy <= cb1; cy++) {
							cyOut = cy < ct2 || cy > cb2;
							for (float cx = cl1; cx <= cr1; cx++) {
								if (cyOut || cx < cl2 || cx > cr2) {
									removeItemFromCell(bind, cx, cy);
								}
							}
						}
					}

				}
				rect.set(x2, y2, w2, h2);
			}
		}
	}

	public CollisionResult.Result check(ActionBind bind, float goalX, float goalY) {
		return check(bind, goalX, goalY, _worldCollisionFilter);
	}

	public CollisionResult.Result check(ActionBind bind, float goalX, float goalY, final CollisionFilter filter) {
		if (_closed || rects.size == 0) {
			check_result.goalX = goalX;
			check_result.goalY = goalY;
			return check_result;
		}
		final TArray<ActionBind> visited = check_visited;
		visited.clear();
		visited.add(bind);

		final CollisionFilter visitedFilter = new WorldCollisionFilter(this, visited, filter);

		RectF rect = getRect(bind);
		CollisionResult.Result result = null;
		if (rect != null) {
			float x = rect.x, y = rect.y, w = rect.width, h = rect.height;
			Collisions cols = check_cols;
			cols.clear();
			Collisions projectedCols = project(bind, x, y, w, h, goalX, goalY, filter, check_projectedCols);
			result = check_result;
			while (projectedCols != null && !projectedCols.isEmpty()) {
				CollisionData col = projectedCols.get(0);
				cols.add(col.overlaps, col.ti, col.move.x, col.move.y, col.normal.x, col.normal.y, col.touch.x,
						col.touch.y, col.itemRect.x, col.itemRect.y, col.itemRect.width, col.itemRect.height,
						col.otherRect.x, col.otherRect.y, col.otherRect.width, col.otherRect.height, col.item,
						col.other, col.type);

				visited.add(col.other);
				CollisionResult response = col.type;
				response.response(this, col, x, y, w, h, goalX, goalY, visitedFilter, result);
				goalX = result.goalX;
				goalY = result.goalY;
				projectedCols = result.collisions;
			}

			result.set(goalX, goalY);
			result.collisions.clear();
			for (int i = 0; i < cols.size(); i++) {
				result.collisions.add(cols.get(i));
			}
		}
		return result;
	}

	public CollisionResult.Result move(ActionBind bind, float goalX, float goalY) {
		return move(bind, goalX, goalY, _worldCollisionFilter);
	}

	public CollisionResult.Result move(ActionBind bind, float goalX, float goalY, CollisionFilter filter) {
		if (_closed) {
			check_result.goalX = goalX;
			check_result.goalY = goalY;
			return check_result;
		}
		CollisionResult.Result result = check(bind, goalX, goalY, filter);
		if (result != null) {
			update(bind, result.goalX, result.goalY);
		}
		return result;
	}

	public CollisionFilter getWorldCollisionFilter() {
		return _worldCollisionFilter;
	}

	public CollisionWorld setWorldCollisionFilter(CollisionFilter c) {
		this._worldCollisionFilter = c;
		return this;
	}

	public CollisionActionQuery<ActionBind> getCollisionActionQuery() {
		return _collisionActionQuery;
	}

	public CollisionWorld setCollisionActionQuery(CollisionActionQuery<ActionBind> c) {
		this._collisionActionQuery = c;
		return this;
	}

	public Screen getGameScreen() {
		return _gameScreen;
	}

	public boolean isClose() {
		return _closed;
	}

	public CollisionWorld setAutoRemoveItem(boolean a) {
		this._autoRemoveItem = a;
		return this;
	}

	public boolean isAutoRemoveItem() {
		return _autoRemoveItem;
	}

	public CollisionWorld setAutoAddItem(boolean a) {
		this._autoAddItem = a;
		return this;
	}

	public boolean isAutoAddItem() {
		return _autoAddItem;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		_closed = true;
		_autoAddItem = false;
		_autoRemoveItem = false;
		if (rects != null) {
			rects.clear();
		}
		if (rows != null) {
			rows.clear();
			rows = null;
		}
		if (nonEmptyCells != null) {
			nonEmptyCells.clear();
			nonEmptyCells = null;
		}
		if (collisionManager != null) {
			collisionManager.clear();
			collisionManager = null;
		}
		getCellsTouchedBySegment_visited.clear();
		project_visited.clear();
		project_dictItemsInCellRect.clear();
		check_visited.clear();
		check_cols.clear();
		check_projectedCols.clear();
	}

}
