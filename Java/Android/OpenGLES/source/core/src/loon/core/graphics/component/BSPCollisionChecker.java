package loon.core.graphics.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import loon.core.geom.RectBox;
import loon.utils.MathUtils;


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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BSPCollisionChecker implements CollisionChecker {

	private final static int MAX_SIZE = 2048;

	private final static BSPCollisionNode[] cache = new BSPCollisionNode[MAX_SIZE];

	private int tail = 0;

	private int size = 0;

	public final static ActorNode getNodeForActor(Actor object) {
		return (ActorNode) object.data;
	}

	public final static void setNodeForActor(Actor object, ActorNode node) {
		object.data = node;
	}

	private synchronized final BSPCollisionNode getBSPNode() {
		if (size == 0) {
			return new BSPCollisionNode(new RectBox(), 0, 0);
		} else {
			int ppos = tail - size;
			if (ppos < 0) {
				ppos += MAX_SIZE;
			}
			BSPCollisionNode node = cache[ppos];
			node.setParent((BSPCollisionNode) null);
			--size;
			return node;
		}
	}

	private synchronized final void returnNode(BSPCollisionNode node) {
		cache[tail++] = node;
		if (tail == MAX_SIZE) {
			tail = 0;
		}
		size = MathUtils.min(size + 1, MAX_SIZE);
		if (node.getLeft() != null || node.getRight() != null) {
			throw new RuntimeException("Size Error !");
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

	private int cellSize;

	private BSPCollisionNode bspTree;

	private HashSet cacheSet = new HashSet();

	private LinkedList cacheNodeStack = new LinkedList();

	@Override
	public void initialize(int cellSize) {
		this.cellSize = cellSize;
	}

	@Override
	public Iterator getActorsIterator() {
		if (bspTree != null) {
			return bspTree.getActorsIterator();
		}
		return null;
	}

	@Override
	public List getActorsList() {
		if (bspTree != null) {
			return bspTree.getActorsList();
		}
		return null;
	}

	@Override
	public synchronized void addObject(Actor actor) {
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
			for (; !treeArea1.contains(bounds) && idx < MAX_SIZE;) {
				RectBox newArea;
				BSPCollisionNode newTop;
				if (bounds.getX() < treeArea1.getX()) {
					by = (treeArea1.getX() - treeArea1.width);
					newArea = new RectBox(by, treeArea1.getY(),
							treeArea1.getRight() - by, treeArea1.height);
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
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(),
							by - treeArea1.getX(), treeArea1.height);
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
					newArea = new RectBox(treeArea1.getX(), by,
							treeArea1.width, treeArea1.getBottom() - by);
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
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(),
							treeArea1.width, by - treeArea1.getY());
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

			this.insertObject(actor, bounds, bounds, treeArea1, this.bspTree,
					result1, result2);
		}

	}

	private void insertObject(Actor actor, RectBox actorBounds, RectBox bounds,
			RectBox area, BSPCollisionNode node, RectBox result1,
			RectBox result2) {
		if (!node.containsActor(actor)) {
			if (!node.isEmpty()
					&& (area.width > actorBounds.width || area.height > actorBounds.height)) {
				RectBox leftArea = node.getLeftArea();
				RectBox rightArea = node.getRightArea();
				RectBox leftIntersects = RectBox.getIntersection(leftArea,
						bounds, result1);
				RectBox rightIntersects = RectBox.getIntersection(rightArea,
						bounds, result2);
				BSPCollisionNode newRight;
				if (leftIntersects != null) {
					if (node.getLeft() == null) {
						newRight = this.createNewNode(leftArea);
						newRight.addActor(actor);
						node.setChild(0, newRight);
					} else {
						this.insertObject(actor, actorBounds, leftIntersects,
								leftArea, node.getLeft(), result1, result2);
					}
				}
				if (rightIntersects != null) {
					if (node.getRight() == null) {
						newRight = this.createNewNode(rightArea);
						newRight.addActor(actor);
						node.setChild(1, newRight);
					} else {
						this.insertObject(actor, actorBounds, rightIntersects,
								rightArea, node.getRight(), result1, result2);
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

	private synchronized BSPCollisionNode createNewNode(RectBox area) {
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

	public synchronized final RectBox getActorBounds(Actor actor) {
		return actor.getBoundingRect();
	}

	@Override
	public synchronized void removeObject(Actor object) {
		for (ActorNode node = getNodeForActor(object); node != null; node = getNodeForActor(object)) {
			BSPCollisionNode bspNode = node.getBSPNode();
			node.remove();
			this.checkRemoveNode(bspNode);
		}
	}

	private synchronized BSPCollisionNode checkRemoveNode(BSPCollisionNode node) {
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

	private synchronized void updateObject(Actor object) {
		ActorNode node = getNodeForActor(object);
		if (node != null) {
			RectBox newBounds = this.getActorBounds(object);
			BSPCollisionNode bspNode;
			if (!this.bspTree.getArea().contains(newBounds)) {
				for (; node != null;) {
					bspNode = node.getBSPNode();
					node.remove();
					this.checkRemoveNode(bspNode);
					node = node.getNext();
				}
				this.addObject(object);
			} else {
				RectBox bspArea;
				RectBox result1 = new RectBox();
				RectBox result2 = new RectBox();
				while (node != null) {
					bspNode = node.getBSPNode();
					bspArea = bspNode.getArea();
					if (bspArea.contains(newBounds)) {
						for (ActorNode rNode2 = getNodeForActor(object); rNode2 != null; rNode2 = rNode2
								.getNext()) {
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
				node = getNodeForActor(object);
				if (node != null) {
					for (bspNode = node.getBSPNode(); bspNode != null
							&& !bspNode.getArea().contains(newBounds); bspNode = bspNode
							.getParent()) {
				
					}
					if (bspNode == null) {
						while (node != null) {
							bspNode = node.getBSPNode();
							node.remove();
							this.checkRemoveNode(bspNode);
							node = node.getNext();
						}

						this.addObject(object);
						return;
					}
				} else {
					bspNode = this.bspTree;
				}

				bspArea = bspNode.getArea();
				this.insertObject(object, newBounds, newBounds, bspArea,
						bspNode, result1, result2);
				for (node = getNodeForActor(object); node != null; node = node
						.getNext()) {
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
	public void updateObjectLocation(Actor object, float oldX, float oldY) {
		this.updateObject(object);
	}

	@Override
	public void updateObjectSize(Actor object) {
		this.updateObject(object);
	}

	private ArrayList getIntersectingObjects(float[] r, CollisionQuery query) {
		synchronized (cacheSet) {
			cacheSet.clear();
			this.getIntersectingObjects(r, query, cacheSet, this.bspTree);
			ArrayList l = new ArrayList(cacheSet);
			return l;
		}
	}

	private void intersectingObjects(float[] r, CollisionQuery query,
			HashSet set) {
		this.getIntersectingObjects(r, query, set, this.bspTree);
	}

	private synchronized void getIntersectingObjects(float[] r,
			CollisionQuery query, Set resultSet, BSPCollisionNode startNode) {
		synchronized (cacheNodeStack) {
			cacheNodeStack.clear();
			try {
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				int idx = 0;
				for (; !cacheNodeStack.isEmpty() && idx < MAX_SIZE;) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack
							.removeLast();
					synchronized (node) {
						if (node.getArea().intersects(r[0], r[1], r[2], r[3])) {
							Iterator i = node.getActorsIterator();
							for (; i.hasNext();) {
								Actor left = (Actor) i.next();
								if (query.checkCollision(left)
										&& !resultSet.contains(left)) {
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
			} catch (Exception e) {
			}
		}
	}

	private synchronized Actor checkForOnlyCollision(Actor ignore,
			BSPCollisionNode node, CollisionQuery query) {
		if (node == null) {
			return null;
		}
		Iterator i = node.getActorsIterator();
		Actor candidate;
		do {
			if (!i.hasNext()) {
				return null;
			}
			candidate = (Actor) i.next();
		} while (ignore == candidate || !query.checkCollision(candidate));
		return candidate;
	}

	private synchronized Actor getOnlyObjectDownTree(Actor ignore, RectBox r,
			CollisionQuery query, BSPCollisionNode startNode) {
		if (startNode == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				if (startNode != null) {
					cacheNodeStack.add(startNode);
				}
				while (!cacheNodeStack.isEmpty()) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack
							.removeLast();
					if (node.getArea().intersects(r)) {
						Actor res = this.checkForOnlyCollision(ignore, node,
								query);
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

	private synchronized Actor getOnlyIntersectingDown(RectBox r,
			CollisionQuery query, Actor actor) {
		if (this.bspTree == null) {
			return null;
		} else {
			synchronized (cacheNodeStack) {
				cacheNodeStack.clear();
				cacheNodeStack.add(this.bspTree);
				int idx = 0;
				for (; !cacheNodeStack.isEmpty() && idx < MAX_SIZE;) {
					BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack
							.removeLast();
					if (node.getArea().contains(r)) {
						Actor res = this.checkForOnlyCollision(actor, node,
								query);
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

	public synchronized Actor getOnlyIntersectingUp(RectBox r,
			CollisionQuery query, Actor actor, BSPCollisionNode start) {
		for (; start != null && !start.getArea().contains(r);) {
			Actor res = this.checkForOnlyCollision(actor, start, query);
			if (res != null) {
				return res;
			}
			start = start.getParent();
		}
		return null;
	}

	@Override
	public synchronized List getObjectsAt(float x, float y, Class cls) {
		synchronized (this.pointQuery) {
			float px = x * this.cellSize + this.cellSize / 2f;
			float py = y * this.cellSize + this.cellSize / 2f;
			this.pointQuery.init(px, py, cls);
			float[] r = { px, py, 1, 1 };
			return this.getIntersectingObjects(r, this.pointQuery);
		}
	}

	@Override
	public synchronized List getIntersectingObjects(Actor actor, Class cls) {
		RectBox r = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(cls, actor);
			return this.getIntersectingObjects(r.toFloat(), this.actorQuery);
		}
	}

	@Override
	public synchronized List getObjectsInRange(float x, float y, float r,
			Class cls) {
		float halfCell = this.cellSize / 2;
		float size = 2 * r * this.cellSize;
		float[] rect = { (x - r) * this.cellSize + halfCell,
				(y - r) * this.cellSize + halfCell, size, size };
		cacheSet.clear();
		synchronized (this.actorQuery) {
			this.actorQuery.init(cls, (Actor) null);
			intersectingObjects(rect, this.actorQuery, cacheSet);
		}
		synchronized (this.inRangeQuery) {
			this.inRangeQuery.init(x * this.cellSize + halfCell, y
					* this.cellSize + halfCell, r * this.cellSize);
			ArrayList rangeResult = new ArrayList();
			Iterator it = cacheSet.iterator();
			for (; it.hasNext();) {
				Actor a = (Actor) it.next();
				if (this.inRangeQuery.checkCollision(a)) {
					rangeResult.add(a);
				}
			}
			return rangeResult;
		}
	}

	@Override
	public synchronized List getNeighbours(Actor actor, float distance,
			boolean diag, Class cls) {
		float x = actor.getX();
		float y = actor.getY();
		float xPixel = x * this.cellSize;
		float yPixel = y * this.cellSize;
		float dPixel = distance * this.cellSize;
		float[] r = { xPixel - dPixel, yPixel - dPixel, dPixel * 2 + 1,
				dPixel * 2 + 1 };
		synchronized (this.neighbourQuery) {
			this.neighbourQuery.init(x, y, distance, diag, cls);
			List res = this.getIntersectingObjects(r, this.neighbourQuery);
			return res;
		}
	}

	@Override
	public synchronized List getObjects(Class cls) {
		synchronized (cacheSet) {
			cacheSet.clear();
		}
		synchronized (cacheNodeStack) {
			if (this.bspTree != null) {
				cacheNodeStack.add(this.bspTree);
			}
			for (; !cacheNodeStack.isEmpty();) {
				BSPCollisionNode node = (BSPCollisionNode) cacheNodeStack
						.removeLast();
				Iterator i = node.getActorsIterator();
				while (i.hasNext()) {
					Actor left = (Actor) i.next();
					if (cls == null || cls.isInstance(left)) {
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
			ArrayList result = new ArrayList(cacheSet);
			return result;
		}
	}

	@Override
	public synchronized List getObjectsList() {
		return this.getObjects((Class) null);
	}

	@Override
	public synchronized Actor getOnlyObjectAt(Actor object, float dx, float dy,
			Class cls) {
		synchronized (this.pointQuery) {
			float px = dx * this.cellSize + this.cellSize / 2f;
			float py = dy * this.cellSize + this.cellSize / 2f;
			this.pointQuery.init(px, py, cls);
			Object query = this.pointQuery;
			if (cls != null) {
				query = new CollisionClassQuery(cls, this.pointQuery);
			}
			return this.getOnlyIntersectingDown(new RectBox(px, py, 1, 1),
					(CollisionQuery) query, object);
		}
	}

	@Override
	public synchronized Actor getOnlyIntersectingObject(Actor actor, Class cls) {
		RectBox rect = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(cls, actor);
			ActorNode node = getNodeForActor(actor);
			if (node == null) {
				return null;
			}
			do {
				BSPCollisionNode bspNode = node.getBSPNode();
				Actor result = this.getOnlyObjectDownTree(actor, rect,
						this.actorQuery, bspNode);
				if (result != null) {
					return result;
				}
				result = this.getOnlyIntersectingUp(rect, this.actorQuery,
						actor, bspNode.getParent());
				if (result != null) {
					return result;
				}
				node = node.getNext();
			} while (node != null);
			return this.getOnlyIntersectingDown(rect, this.actorQuery, actor);
		}
	}

	@Override
	public synchronized void dispose() {
		if (cacheSet != null) {
			cacheSet.clear();
		}
		if (cacheNodeStack != null) {
			cacheNodeStack.clear();
		}
		if (cache != null) {
			for (int i = 0; i < cache.length; i++) {
				if (cache[i] != null) {
					cache[i] = null;
				}
			}
		}
	}

}
