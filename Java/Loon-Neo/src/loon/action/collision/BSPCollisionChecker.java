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

import loon.LSysException;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;

public class BSPCollisionChecker implements CollisionChecker {

	private final static int MAX_SIZE = 2048;

	private final BSPCollisionNode[] _collisionCache = new BSPCollisionNode[MAX_SIZE];

	private int _tail = 0;

	private int _size = 0;

	private boolean _itlayer = false;

	private final Vector2f _offsetLocation = new Vector2f();

	public final static CollisionNode getNodeForActor(CollisionObject obj) {
		return (CollisionNode) obj.getCollisionData();
	}

	public final static void setNodeForActor(CollisionObject obj, CollisionNode node) {
		obj.setCollisionData(node);
	}

	private final BSPCollisionNode getBSPNode() {
		if (_size == 0) {
			return new BSPCollisionNode(new RectBox(), 0, 0);
		} else {
			int ppos = _tail - _size;
			if (ppos < 0) {
				ppos += MAX_SIZE;
			}
			BSPCollisionNode node = _collisionCache[ppos];
			node.setParent((BSPCollisionNode) null);
			--_size;
			return node;
		}
	}

	private final void returnNode(BSPCollisionNode node) {
		_collisionCache[_tail++] = node;
		if (_tail == MAX_SIZE) {
			_tail = 0;
		}
		_size = MathUtils.min(_size + 1, MAX_SIZE);
		if (node.getLeft() != null || node.getRight() != null) {
			throw new LSysException("Size Error !");
		}
	}

	public void startLoop() {

	}

	public void endLoop() {

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
	public void initialize(int _size) {
		this.initialize(_size, _size);
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
			RectBox result1 = new RectBox();
			RectBox result2 = new RectBox();
			for (; !treeArea1.intersects(bounds) && idx < MAX_SIZE;) {
				RectBox newArea;
				BSPCollisionNode newTop;
				if (bounds.getX() < treeArea1.getX()) {
					by = (treeArea1.getX() - treeArea1.width);
					newArea = new RectBox(by, treeArea1.getY(), treeArea1.getRight() - by, treeArea1.height);
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
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(), by - treeArea1.getX(), treeArea1.height);
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
					newArea = new RectBox(treeArea1.getX(), by, treeArea1.width, treeArea1.getBottom() - by);
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
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(), treeArea1.width, by - treeArea1.getY());
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
				RectBox leftArea = node.getLeftArea();
				RectBox rightArea = node.getRightArea();
				RectBox leftIntersects = RectBox.getIntersection(leftArea, bounds, result1);
				RectBox rightIntersects = RectBox.getIntersection(rightArea, bounds, result2);
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
		byte splitAxis;
		float splitPos;
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
				BSPCollisionNode parent = node.getParent();
				int side = parent != null ? parent.getChildSide(node) : 3;
				BSPCollisionNode left = node.getLeft();
				BSPCollisionNode right = node.getRight();
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
					returnNode(node);
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
					returnNode(node);
					node = parent;
					continue;
				}
			}
			idx++;
			return node;
		}
		return null;
	}

	private void updateObject(CollisionObject obj) {
		CollisionNode node = getNodeForActor(obj);
		if (node != null) {
			RectBox newBounds = this.getActorBounds(obj);
			BSPCollisionNode bspNode;
			if (!this.bspTree.getArea().intersects(newBounds)) {
				for (; node != null;) {
					bspNode = node.getBSPNode();
					node.remove();
					this.checkRemoveNode(bspNode);
					node = node.getNext();
				}
				this.addObject(obj);
			} else {
				RectBox bspArea;
				RectBox result1 = new RectBox();
				RectBox result2 = new RectBox();
				while (node != null) {
					bspNode = node.getBSPNode();
					bspArea = bspNode.getArea();
					if (bspArea.intersects(newBounds)) {
						for (CollisionNode rNode2 = getNodeForActor(obj); rNode2 != null; rNode2 = rNode2.getNext()) {
							if (rNode2 != node) {
								BSPCollisionNode rNode1 = rNode2.getBSPNode();
								rNode2.remove();
								this.checkRemoveNode(rNode1);
							}
						}
						return;
					}
					if (!bspArea.contains(newBounds)) {
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
							&& !bspNode.getArea().intersects(newBounds); bspNode = bspNode.getParent()) {

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

	private TArray<CollisionObject> getIntersectingObjects(float[] r, CollisionQuery query) {
		synchronized (cacheSet) {
			cacheSet.clear();
			this.getIntersectingObjects(r, query, cacheSet, this.bspTree);
			TArray<CollisionObject> l = new TArray<CollisionObject>(cacheSet.size());
			for (LIterator<CollisionObject> it = cacheSet.iterator(); it.hasNext();) {
				l.add(it.next());
			}
			return l;
		}
	}

	private void intersectingObjects(float[] r, CollisionQuery query, ObjectSet<CollisionObject> set) {
		this.getIntersectingObjects(r, query, set, this.bspTree);
	}

	private void getIntersectingObjects(float[] r, CollisionQuery query, ObjectSet<CollisionObject> resultSet,
			BSPCollisionNode startNode) {
		synchronized (cacheNodeStack) {
			cacheNodeStack.clear();
			try {
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				int idx = 0;
				for (; cacheNodeStack.size() != 0 && idx < MAX_SIZE;) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					synchronized (node) {
						if (node.getArea().contains(r[0], r[1], r[2], r[3])) {
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

	private CollisionObject checkForOnlyCollision(CollisionObject ignore, BSPCollisionNode node, CollisionQuery query) {
		if (node == null) {
			return null;
		}
		LIterator<CollisionObject> i = node.getActorsIterator();
		CollisionObject candidate;
		do {
			if (!i.hasNext()) {
				return null;
			}
			candidate = i.next();
		} while (ignore == candidate || !query.checkCollision(candidate));
		return candidate;
	}

	private CollisionObject getOnlyObjectDownTree(CollisionObject ignore, RectBox r, CollisionQuery query,
			BSPCollisionNode startNode) {
		if (startNode == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				while (cacheNodeStack.size() != 0) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					if (node.getArea().contains(r)) {
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

	private CollisionObject getOnlyIntersectingDown(RectBox r, CollisionQuery query, CollisionObject actor) {
		if (this.bspTree == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				cacheNodeStack.add(this.bspTree);
				int idx = 0;
				for (; cacheNodeStack.size() != 0 && idx < MAX_SIZE;) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
					if (node.getArea().intersects(r)) {
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

	private CollisionObject getOnlyIntersectingUp(RectBox r, CollisionQuery query, CollisionObject actor,
			BSPCollisionNode start) {
		for (; start != null && !start.getArea().intersects(r);) {
			CollisionObject res = this.checkForOnlyCollision(actor, start, query);
			if (res != null) {
				return res;
			}
			start = start.getParent();
		}
		return null;
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsAt(float x, float y, String flag) {
		synchronized (this.pointQuery) {
			float px = x * this.cellSizeX + this.cellSizeX / 2f;
			float py = y * this.cellSizeY + this.cellSizeY / 2f;
			this.pointQuery.init(px, py, flag, this._offsetLocation);
			float[] r = { px, py, 1, 1 };
			return this.getIntersectingObjects(r, this.pointQuery);
		}
	}

	@Override
	public synchronized TArray<CollisionObject> getIntersectingObjects(CollisionObject actor, String flag) {
		RectBox r = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, actor, _offsetLocation);
			return getInTheLayerObjects(actor.getLayer(), this.getIntersectingObjects(r.toFloat(), this.actorQuery));
		}
	}

	private final static float getRadius(float r1, float r2) {
		return MathUtils.max(r1, r2);
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String flag) {
		float halfCellX = this.cellSizeX / 2;
		float halfCellY = this.cellSizeY / 2;
		float sizeRX = 2 * r * this.cellSizeX;
		float sizeRY = 2 * r * this.cellSizeY;
		float[] rect = { (x - r) * this.cellSizeX + halfCellX, (y - r) * this.cellSizeY + halfCellY, sizeRX, sizeRY };
		cacheSet.clear();
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, null, this._offsetLocation);
			intersectingObjects(rect, this.actorQuery, cacheSet);
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
	public synchronized TArray<CollisionObject> getNeighbours(CollisionObject actor, float distance, boolean diag,
			String flag) {
		float x = actor.getX();
		float y = actor.getY();
		float xPixel = x * this.cellSizeX;
		float yPixel = y * this.cellSizeY;
		float dxPixel = distance * this.cellSizeX;
		float dyPixel = distance * this.cellSizeY;
		float[] r = { xPixel - dxPixel, yPixel - dyPixel, dxPixel * 2 + 1, dyPixel * 2 + 1 };
		synchronized (this.neighbourQuery) {
			this.neighbourQuery.init(x, y, distance, diag, flag, this._offsetLocation);
			return getInTheLayerObjects(actor.getLayer(), this.getIntersectingObjects(r, this.neighbourQuery));
		}
	}

	@Override
	public synchronized TArray<CollisionObject> getObjectsList() {
		return this.getObjects((String) null);
	}

	@Override
	public synchronized CollisionObject getOnlyObjectAt(CollisionObject obj, float dx, float dy, String flag) {
		synchronized (this.pointQuery) {
			float px = dx * this.cellSizeX + this.cellSizeX / 2f;
			float py = dy * this.cellSizeY + this.cellSizeY / 2f;
			this.pointQuery.init(px, py, flag, _offsetLocation);
			Object query = this.pointQuery;
			if (flag != null) {
				query = new CollisionClassQuery(flag, this.pointQuery, this._offsetLocation);
			}
			return getInTheLayerObject(obj.getLayer(),
					this.getOnlyIntersectingDown(new RectBox(px, py, 1, 1), (CollisionQuery) query, obj));
		}
	}

	@Override
	public synchronized CollisionObject getOnlyIntersectingObject(CollisionObject actor, String flag) {
		int layer = actor.getLayer();
		RectBox rect = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(flag, actor, this._offsetLocation);
			CollisionNode node = getNodeForActor(actor);
			if (node == null) {
				return null;
			}
			do {
				BSPCollisionNode bspNode = node.getBSPNode();
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

	private CollisionObject getInTheLayerObject(int layer, CollisionObject obj) {
		if (!_itlayer) {
			return obj;
		}
		if (obj != null && obj.getLayer() == layer) {
			return obj;
		}
		return null;
	}

	private TArray<CollisionObject> getInTheLayerObjects(int layer, TArray<CollisionObject> lists) {
		if (!_itlayer) {
			return lists;
		}
		TArray<CollisionObject> tmp = new TArray<CollisionObject>(lists.size);
		for (int i = 0; i < lists.size; i++) {
			CollisionObject obj = lists.get(i);
			if (obj != null && obj.getLayer() == layer) {
				tmp.add(obj);
			}
		}
		return tmp;
	}

	@Override
	public synchronized TArray<CollisionObject> getObjects(String flag) {
		synchronized (cacheSet) {
			cacheSet.clear();
		}
		synchronized (cacheNodeStack) {
			if (this.bspTree != null) {
				cacheNodeStack.add(this.bspTree);
			}
			for (; cacheNodeStack.size() != 0;) {
				BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack.removeLast();
				LIterator<CollisionObject> i = node.getActorsIterator();
				while (i.hasNext()) {
					CollisionObject left = i.next();
					if (flag == null || flag.equals(left.getObjectFlag())) {
						cacheSet.add(left);
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
			TArray<CollisionObject> result = new TArray<CollisionObject>(cacheSet.size());
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
		if (_collisionCache != null) {
			for (int i = 0; i < _collisionCache.length; i++) {
				if (_collisionCache[i] != null) {
					_collisionCache[i] = null;
				}
			}
		}
	}

}
