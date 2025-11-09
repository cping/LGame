/**
 * 
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
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.collision;

import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;
import loon.utils.cache.Pool;

public final class BSPCollisionChecker implements CollisionChecker {

	private final static int MAX_SIZE = 1024;

	private class BSPNodePool extends Pool<BSPCollisionNode> {

		private BSPCollisionChecker _checker;

		public BSPNodePool(BSPCollisionChecker c, int max) {
			super(max);
			this._checker = c;
		}

		@Override
		protected BSPCollisionNode newObject() {
			return new BSPCollisionNode(_checker, _checker.createRect(), 0, 0);
		}

		@Override
		public boolean isLimit(BSPCollisionNode src, BSPCollisionNode dst) {
			return false;
		}

		@Override
		protected BSPCollisionNode filterObtain(BSPCollisionNode o) {
			o.free();
			o.setArea(_checker.createRect(), false);
			return o;
		}
	}

	private final Pool<BSPCollisionNode> _nodes = new BSPNodePool(this, MAX_SIZE);

	private boolean _itlayer = false;

	private Vector2f _offsetLocation = new Vector2f();

	public final static CollisionNode getNodeForActor(CollisionObject obj) {
		return (CollisionNode) obj.getCollisionData();
	}

	public final static void setNodeForActor(CollisionObject obj, CollisionNode node) {
		obj.setCollisionData(node);
	}

	private final BSPCollisionNode getBSPNode() {
		return _nodes.obtain();
	}

	private final void freeNode(BSPCollisionNode node) {
		if (node != null) {
			_nodes.free(node);
		}
	}

	protected final RectBox createRect() {
		return createRect(0f, 0f, 0f, 0f);
	}

	protected final RectBox createRect(float x, float y, float w, float h) {
		return new RectBox(x, y, w, h);
	}

	private final CollisionBaseQuery actorQuery = new CollisionBaseQuery();

	private final CollisionNeighbourQuery neighbourQuery = new CollisionNeighbourQuery();

	private final CollisionPointQuery pointQuery = new CollisionPointQuery();

	private final CollisionInRangeQuery inRangeQuery = new CollisionInRangeQuery();

	private int cellSizeX, cellSizeY;

	private BSPCollisionNode bspTree;

	private ObjectSet<CollisionObject> cacheSet = new ObjectSet<CollisionObject>();

	private SortedList<BSPCollisionNode> cacheNodeStack = new SortedList<BSPCollisionNode>();

	@Override
	public void initialize(int size) {
		this.initialize(size, size);
	}

	@Override
	public void initialize(int tsx, int tsy) {
		this.cellSizeX = MathUtils.max(1, tsx);
		this.cellSizeY = MathUtils.max(1, tsy);
	}

	@Override
	public int numberActors() {
		if (bspTree == null) {
			return 0;
		}
		return bspTree.numberActors();
	}

	@Override
	public LIterator<CollisionObject> getActorsIterator() {
		if (bspTree != null) {
			return bspTree.getActorsIterator();
		}
		return null;
	}

	@Override
	public TArray<CollisionObject> getActorsList() {
		if (bspTree != null) {
			return bspTree.getActorsList();
		}
		return null;
	}

	@Override
	public synchronized void addObject(CollisionObject actor) {
		RectBox bounds = this.getActorBounds(actor);
		float by;
		if (this.bspTree == null) {
			byte treeArea;
			if (bounds.width > bounds.height) {
				treeArea = 0;
				by = bounds.getMiddleX();
			} else {
				treeArea = 1;
				by = bounds.getMiddleY();
			}

			this.bspTree = getBSPNode();
			this.bspTree.getArea().copy(bounds);
			this.bspTree.setSplitAxis(treeArea);
			this.bspTree.setSplitPos(by);
			this.bspTree.addActor(actor);
		} else {
			int idx = 0;
			RectBox treeArea1 = this.bspTree.getArea();
			final RectBox result1 = createRect();
			final RectBox result2 = createRect();
			for (; !treeArea1.contains(bounds) && idx < MAX_SIZE;) {
				RectBox newArea;
				BSPCollisionNode newTop;
				if (bounds.getX() < treeArea1.getX()) {
					by = (treeArea1.getX() - treeArea1.width);
					newArea = createRect(by, treeArea1.getY(), treeArea1.getRight() - by, treeArea1.height);
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(0);
					newTop.setSplitPos(treeArea1.getX());
					newTop.setChild(1, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}
				if (bounds.getRight() > treeArea1.getRight()) {
					by = (treeArea1.getRight() + treeArea1.width);
					newArea = createRect(treeArea1.getX(), treeArea1.getY(), by - treeArea1.getX(), treeArea1.height);
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(0);
					newTop.setSplitPos(treeArea1.getRight());
					newTop.setChild(0, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}
				if (bounds.getY() < treeArea1.getY()) {
					by = (treeArea1.getY() - treeArea1.height);
					newArea = createRect(treeArea1.getX(), by, treeArea1.width, treeArea1.getBottom() - by);
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(1);
					newTop.setSplitPos(treeArea1.getY());
					newTop.setChild(1, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}
				if (bounds.getBottom() > treeArea1.getBottom()) {
					by = (treeArea1.getBottom() + treeArea1.height);
					newArea = createRect(treeArea1.getX(), treeArea1.getY(), treeArea1.width, by - treeArea1.getY());
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(1);
					newTop.setSplitPos(treeArea1.getBottom());
					newTop.setChild(0, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}
				idx++;
			}

			this.insertObject(actor, bounds, bounds, treeArea1, this.bspTree, result1, result2);
		}

	}

	private void insertObject(CollisionObject actor, RectBox actorBounds, RectBox bounds, RectBox area,
			BSPCollisionNode node, RectBox result1, RectBox result2) {
		if (!node.containsActor(actor)) {
			if (!node.isEmpty() && (area.width > actorBounds.width || area.height > actorBounds.height)) {
				final RectBox leftArea = node.getLeftArea();
				final RectBox rightArea = node.getRightArea();
				final RectBox leftIntersects = RectBox.getIntersection(leftArea, bounds, result1);
				final RectBox rightIntersects = RectBox.getIntersection(rightArea, bounds, result2);
				BSPCollisionNode newRight;
				if (leftIntersects != null) {
					if (node.getLeft() == null) {
						newRight = this.createNewNode(leftArea);
						newRight.addActor(actor);
						node.setChild(0, newRight);
					} else {
						this.insertObject(actor, actorBounds, leftIntersects, leftArea, node.getLeft(), result1,
								result2);
					}
				}
				if (rightIntersects != null) {
					if (node.getRight() == null) {
						newRight = this.createNewNode(rightArea);
						newRight.addActor(actor);
						node.setChild(1, newRight);
					} else {
						this.insertObject(actor, actorBounds, rightIntersects, rightArea, node.getRight(), result1,
								result2);
					}
				}

			} else {
				node.addActor(actor);
			}
		}
	}

	@Override
	public synchronized void clear() {
		if (bspTree != null) {
			bspTree.clear();
		}
	}

	private BSPCollisionNode createNewNode(RectBox area) {
		final float splitAxis;
		final float splitPos;
		if (area.width > area.height) {
			splitAxis = 0;
			splitPos = area.getMiddleX();
		} else {
			splitAxis = 1;
			splitPos = area.getMiddleY();
		}
		BSPCollisionNode newNode = getBSPNode();
		newNode.setArea(area);
		newNode.setSplitAxis(splitAxis);
		newNode.setSplitPos(splitPos);
		return newNode;
	}

	public final RectBox getActorBounds(CollisionObject actor) {
		return actor.getBoundingRect();
	}

	@Override
	public synchronized void removeObject(CollisionObject obj) {
		for (CollisionNode node = getNodeForActor(obj); node != null; node = getNodeForActor(obj)) {
			BSPCollisionNode bspNode = node.getBSPNode();
			node.remove();
			this.checkRemoveNode(bspNode);
		}
	}

	private BSPCollisionNode checkRemoveNode(BSPCollisionNode node) {
		int idx = 0;
		for (; idx < MAX_SIZE;) {
			if (node != null && node.isEmpty()) {
				final BSPCollisionNode parent = node.getParent();
				int side = parent != null ? parent.getChildSide(node) : 3;
				final BSPCollisionNode left = node.getLeft();
				final BSPCollisionNode right = node.getRight();
				if (left == null) {
					if (parent != null) {
						if (right != null) {
							right.setArea(node.getArea());
						}
						parent.setChild(side, right);
					} else {
						this.bspTree = right;
						if (right != null) {
							right.setParent((BSPCollisionNode) null);
						}
					}
					node.setChild(1, (BSPCollisionNode) null);
					freeNode(node);
					node = parent;
					continue;
				}

				if (right == null) {
					if (parent != null) {
						if (left != null) {
							left.setArea(node.getArea());
						}

						parent.setChild(side, left);
					} else {
						this.bspTree = left;
						if (left != null) {
							left.setParent((BSPCollisionNode) null);
						}
					}

					node.setChild(0, (BSPCollisionNode) null);
					freeNode(node);
					node = parent;
					continue;
				}
			}
			idx++;
			return node;
		}
		return null;
	}

	private void updateObject(final CollisionObject obj) {
		CollisionNode node = getNodeForActor(obj);
		if (node != null) {
			final RectBox newBounds = this.getActorBounds(obj);
			BSPCollisionNode bspNode;
			if (!this.bspTree.getArea().contains(newBounds)) {
				for (; node != null;) {
					bspNode = node.getBSPNode();
					node.remove();
					this.checkRemoveNode(bspNode);
					node = node.getNext();
				}
				this.addObject(obj);
			} else {
				RectBox bspArea;
				final RectBox result1 = createRect();
				final RectBox result2 = createRect();
				while (node != null) {
					bspNode = node.getBSPNode();
					bspArea = bspNode.getArea();
					if (bspArea.contains(newBounds)) {
						for (CollisionNode rNode2 = getNodeForActor(obj); rNode2 != null; rNode2 = rNode2.getNext()) {
							if (rNode2 != node) {
								BSPCollisionNode rNode1 = rNode2.getBSPNode();
								rNode2.remove();
								this.checkRemoveNode(rNode1);
							}
						}
						return;
					}
					if (!bspArea.intersects(newBounds)) {
						BSPCollisionNode rNode = node.getBSPNode();
						node.remove();
						this.checkRemoveNode(rNode);
					}
					node.clearMark();
					node = node.getNext();
				}
				node = getNodeForActor(obj);
				if (node != null) {
					for (bspNode = node.getBSPNode(); bspNode != null
							&& !bspNode.getArea().contains(newBounds); bspNode = bspNode.getParent()) {

					}
					if (bspNode == null) {
						while (node != null) {
							bspNode = node.getBSPNode();
							node.remove();
							this.checkRemoveNode(bspNode);
							node = node.getNext();
						}

						this.addObject(obj);
						return;
					}
				} else {
					bspNode = this.bspTree;
				}

				bspArea = bspNode.getArea();
				this.insertObject(obj, newBounds, newBounds, bspArea, bspNode, result1, result2);
				for (node = getNodeForActor(obj); node != null; node = node.getNext()) {
					if (!node.checkMark()) {
						bspNode = node.getBSPNode();
						node.remove();
						this.checkRemoveNode(bspNode);
					}
				}

			}
		}
	}

	@Override
	public void updateObjectLocation(CollisionObject obj, float oldX, float oldY) {
		this.updateObject(obj);
	}

	@Override
	public void updateObjectSize(CollisionObject obj) {
		this.updateObject(obj);
	}

	private TArray<CollisionObject> getIntersectingObjects(final float x, final float y, final float w, final float h,
			final CollisionQuery query) {
		synchronized (cacheSet) {
			cacheSet.clear();
			this.getIntersectingObjects(x, y, w, h, query, cacheSet, this.bspTree);
			final TArray<CollisionObject> l = new TArray<CollisionObject>(cacheSet.size());
			for (LIterator<CollisionObject> it = cacheSet.iterator(); it.hasNext();) {
				l.add(it.next());
			}
			return l;
		}
	}

	private void intersectingObjects(final float x, final float y, final float w, final float h, CollisionQuery query,
			ObjectSet<CollisionObject> set) {
		this.getIntersectingObjects(x, y, w, h, query, set, this.bspTree);
	}

	private void getIntersectingObjects(final float x, final float y, final float w, final float h,
			final CollisionQuery query, final ObjectSet<CollisionObject> resultSet, final BSPCollisionNode startNode) {
		synchronized (cacheNodeStack) {
			cacheNodeStack.clear();
			try {
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				int idx = 0;
				for (; cacheNodeStack.size() != 0 && idx < MAX_SIZE;) {
					final BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					synchronized (node) {
						if (node.getArea().intersects(x, y, w, h)) {
							LIterator<CollisionObject> i = node.getActorsIterator();
							for (; i.hasNext();) {
								CollisionObject left = i.next();
								if (query.checkCollision(left) && !resultSet.contains(left)) {
									resultSet.add(left);
								}
							}
							BSPCollisionNode left1 = node.getLeft();
							BSPCollisionNode right = node.getRight();
							if (left1 != null) {
								cacheNodeStack.add(left1);
							}
							if (right != null) {
								cacheNodeStack.add(right);
							}
						}
					}
					idx++;
				}
			} catch (Throwable e) {
			}
		}
	}

	private CollisionObject checkForOnlyCollision(final CollisionObject ignore, final BSPCollisionNode node,
			final CollisionQuery query) {
		if (node == null) {
			return null;
		}
		final LIterator<CollisionObject> i = node.getActorsIterator();
		CollisionObject candidate;
		do {
			if (!i.hasNext()) {
				return null;
			}
			candidate = i.next();
		} while (ignore == candidate || !query.checkCollision(candidate));
		return candidate;
	}

	private CollisionObject getOnlyObjectDownTree(final CollisionObject ignore, final RectBox r,
			final CollisionQuery query, final BSPCollisionNode startNode) {
		if (startNode == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				while (cacheNodeStack.size() != 0) {
					final BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					if (node.getArea().intersects(r)) {
						CollisionObject res = this.checkForOnlyCollision(ignore, node, query);
						if (res != null) {
							return res;
						}

						BSPCollisionNode left = node.getLeft();
						BSPCollisionNode right = node.getRight();
						if (left != null) {
							cacheNodeStack.add(left);
						}
						if (right != null) {
							cacheNodeStack.add(right);
						}
					}
				}
			}
			return null;
		}
	}

	private CollisionObject getOnlyIntersectingDown(RectBox rect, CollisionQuery query, CollisionObject actor) {
		return getOnlyIntersectingDown(rect.x, rect.y, rect.width, rect.height, query, actor);
	}

	private CollisionObject getOnlyIntersectingDown(final float x, final float y, final float w, final float h,
			final CollisionQuery query, final CollisionObject actor) {
		if (this.bspTree == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				cacheNodeStack.add(this.bspTree);
				int idx = 0;
				for (; cacheNodeStack.size() != 0 && idx < MAX_SIZE;) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					if (node.getArea().contains(x, y, w, h)) {
						CollisionObject res = this.checkForOnlyCollision(actor, node, query);
						if (res != null) {
							return res;
						}

						BSPCollisionNode left = node.getLeft();
						BSPCollisionNode right = node.getRight();
						if (left != null) {
							cacheNodeStack.add(left);
						}
						if (right != null) {
							cacheNodeStack.add(right);
						}
					}
				}
			}
			return null;
		}
	}

	private CollisionObject getOnlyIntersectingUp(final RectBox rect, final CollisionQuery query,
			final CollisionObject actor, final BSPCollisionNode start) {
		return getOnlyIntersectingUp(rect.x, rect.y, rect.width, rect.height, query, actor, start);
	}

	private CollisionObject getOnlyIntersectingUp(final float x, final float y, final float w, final float h,
			final CollisionQuery query, final CollisionObject actor, BSPCollisionNode start) {
		for (; start != null && !start.getArea().contains(x, y, w, h);) {
			final CollisionObject res = this.checkForOnlyCollision(actor, start, query);
			if (res != null) {
				return res;
			}
			start = start.getParent();
		}
		return null;
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsAt(final float x, final float y, final String flag) {
		synchronized (this.pointQuery) {
			final float px = x * this.cellSizeX + this.cellSizeX / 2f;
			final float py = y * this.cellSizeY + this.cellSizeY / 2f;
			this.pointQuery.init(px, py, flag, this._offsetLocation);
			return this.getIntersectingObjects(px, py, 1, 1, this.pointQuery);
		}
	}

	@Override
	public synchronized TArray<CollisionObject> getIntersectingObjects(final CollisionObject actor, final String flag) {
		final RectBox r = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, actor, _offsetLocation);
			return getInTheLayerObjects(actor.getLayer(),
					this.getIntersectingObjects(r.x, r.y, r.width, r.height, this.actorQuery));
		}
	}

	private final static float getRadius(float r1, float r2) {
		return MathUtils.max(r1, r2);
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsInRange(final float x, final float y, final float r,
			final String flag) {
		final float halfCellX = this.cellSizeX / 2;
		final float halfCellY = this.cellSizeY / 2;
		final float sizeRX = 2 * r * this.cellSizeX;
		final float sizeRY = 2 * r * this.cellSizeY;
		cacheSet.clear();
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, null, this._offsetLocation);
			intersectingObjects((x - r) * this.cellSizeX + halfCellX, (y - r) * this.cellSizeY + halfCellY, sizeRX,
					sizeRY, this.actorQuery, cacheSet);
		}
		synchronized (this.inRangeQuery) {
			this.inRangeQuery.init(x * this.cellSizeX + halfCellX, y * this.cellSizeY + halfCellY,
					r * getRadius(this.cellSizeX, this.cellSizeY), this._offsetLocation);
			TArray<CollisionObject> rangeResult = new TArray<CollisionObject>();
			LIterator<CollisionObject> it = cacheSet.iterator();
			for (; it.hasNext();) {
				CollisionObject a = it.next();
				if (a != null && this.inRangeQuery.checkCollision(a)) {
					rangeResult.add(a);
				}
			}
			return rangeResult;
		}
	}

	@Override
	public synchronized TArray<CollisionObject> getNeighbours(final CollisionObject actor, final float distance,
			final boolean diag, final String flag) {
		final float x = actor.getX();
		final float y = actor.getY();
		final float xPixel = x * this.cellSizeX;
		final float yPixel = y * this.cellSizeY;
		final float dxPixel = distance * this.cellSizeX;
		final float dyPixel = distance * this.cellSizeY;
		synchronized (this.neighbourQuery) {
			this.neighbourQuery.init(x, y, distance, diag, flag, this._offsetLocation);
			return getInTheLayerObjects(actor.getLayer(), this.getIntersectingObjects(xPixel - dxPixel,
					yPixel - dyPixel, dxPixel * 2 + 1, dyPixel * 2 + 1, this.neighbourQuery));
		}
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsList() {
		return this.getObjects((String) null);
	}

	@Override
	public synchronized CollisionObject getOnlyObjectAt(final CollisionObject obj, final float dx, final float dy,
			final String flag) {
		synchronized (this.pointQuery) {
			final float px = dx * this.cellSizeX + this.cellSizeX / 2f;
			final float py = dy * this.cellSizeY + this.cellSizeY / 2f;
			this.pointQuery.init(px, py, flag, _offsetLocation);
			CollisionQuery query = this.pointQuery;
			if (flag != null) {
				query = new CollisionClassQuery(flag, this.pointQuery, this._offsetLocation);
			}
			return getInTheLayerObject(obj.getLayer(),
					this.getOnlyIntersectingDown(px, py, 1, 1, (CollisionQuery) query, obj));
		}
	}

	@Override
	public synchronized CollisionObject getOnlyIntersectingObject(final CollisionObject actor, final String flag) {
		final int layer = actor.getLayer();
		RectBox rect = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, actor, this._offsetLocation);
			CollisionNode node = getNodeForActor(actor);
			if (node == null) {
				return null;
			}
			do {
				final BSPCollisionNode bspNode = node.getBSPNode();
				CollisionObject result = this.getOnlyObjectDownTree(actor, rect, this.actorQuery, bspNode);
				if (result != null) {
					return getInTheLayerObject(layer, result);
				}
				result = this.getOnlyIntersectingUp(rect, this.actorQuery, actor, bspNode.getParent());
				if (result != null) {
					return getInTheLayerObject(layer, result);
				}
				node = node.getNext();
			} while (node != null);
			return getInTheLayerObject(layer, this.getOnlyIntersectingDown(rect, this.actorQuery, actor));
		}
	}

	private CollisionObject getInTheLayerObject(final int layer, final CollisionObject obj) {
		if (!_itlayer) {
			return obj;
		}
		if (obj != null && obj.getLayer() == layer) {
			return obj;
		}
		return null;
	}

	private TArray<CollisionObject> getInTheLayerObjects(final int layer, final TArray<CollisionObject> lists) {
		if (!_itlayer) {
			return lists;
		}
		final TArray<CollisionObject> tmp = new TArray<CollisionObject>(lists.size);
		for (int i = 0; i < lists.size; i++) {
			CollisionObject obj = lists.get(i);
			if (obj != null && obj.getLayer() == layer) {
				tmp.add(obj);
			}
		}
		return tmp;
	}

	@Override
	public synchronized TArray<CollisionObject> getObjects(final String flag) {
		synchronized (cacheSet) {
			cacheSet.clear();
		}
		synchronized (cacheNodeStack) {
			if (this.bspTree != null) {
				cacheNodeStack.add(this.bspTree);
			}
			for (; cacheNodeStack.size() != 0;) {
				final BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
				final LIterator<CollisionObject> i = node.getActorsIterator();
				while (i.hasNext()) {
					CollisionObject left = i.next();
					if (flag == null || flag.equals(left.getObjectFlag())) {
						cacheSet.add(left);
					}
				}
				final BSPCollisionNode left1 = node.getLeft();
				final BSPCollisionNode right = node.getRight();
				if (left1 != null) {
					cacheNodeStack.add(left1);
				}
				if (right != null) {
					cacheNodeStack.add(right);
				}
			}
			final TArray<CollisionObject> result = new TArray<CollisionObject>(cacheSet.size());
			for (LIterator<CollisionObject> it = cacheSet.iterator(); it.hasNext();) {
				result.add(it.next());
			}
			return result;
		}
	}

	@Override
	public void setInTheLayer(boolean yes) {
		this._itlayer = yes;
	}

	@Override
	public boolean getInTheLayer() {
		return _itlayer;
	}

	@Override
	public void setOffsetPos(Vector2f offset) {
		if (offset == null) {
			return;
		}
		_offsetLocation = offset;
	}

	@Override
	public void setOffsetPos(float x, float y) {
		_offsetLocation.set(x, y);
	}

	@Override
	public void setOffsetX(float x) {
		_offsetLocation.setX(x);
	}

	@Override
	public void setOffsetY(float y) {
		_offsetLocation.setY(y);
	}

	@Override
	public Vector2f getOffsetPos() {
		return _offsetLocation;
	}

	@Override
	public void dispose() {
		if (cacheSet != null) {
			cacheSet.clear();
		}
		if (cacheNodeStack != null) {
			cacheNodeStack.clear();
		}
		_nodes.clear();
	}

}
