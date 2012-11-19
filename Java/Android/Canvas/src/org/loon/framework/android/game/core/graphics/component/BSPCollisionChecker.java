package org.loon.framework.android.game.core.graphics.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.loon.framework.android.game.core.geom.RectBox;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

@SuppressWarnings({"unchecked","rawtypes"})
public class BSPCollisionChecker implements CollisionChecker {

	private static BSPCollisionNode[] cache = new BSPCollisionNode[1000];

	private static int tail = 0;

	private static int size = 0;

	public static BSPCollisionNode getBSPNode() {
		if (size == 0) {
			return new BSPCollisionNode(new RectBox(), 0, 0);
		} else {
			int ppos = tail - size;
			if (ppos < 0) {
				ppos += 1000;
			}
			BSPCollisionNode node = cache[ppos];
			node.setParent((BSPCollisionNode) null);
			--size;
			return node;
		}
	}

	public static void returnNode(BSPCollisionNode node) {
		cache[tail++] = node;
		if (tail == 1000) {
			tail = 0;
		}
		size = Math.min(size + 1, 1000);
		if (node.getLeft() != null || node.getRight() != null) {
			throw new RuntimeException("Size Error !");
		}
	}

	public static final int X_AXIS = 0;

	public static final int Y_AXIS = 1;

	public static final int PARENT_LEFT = 0;

	public static final int PARENT_RIGHT = 1;

	public static final int PARENT_NONE = 3;

	public static final int REBALANCE_THRESHOLD = 20;

	private CollisionBaseQuery actorQuery = new CollisionBaseQuery();

	private CollisionNeighbourQuery neighbourQuery = new CollisionNeighbourQuery();

	private CollisionPointQuery pointQuery = new CollisionPointQuery();

	private CollisionInRangeQuery inRangeQuery = new CollisionInRangeQuery();

	private int cellSize;

	private BSPCollisionNode bspTree;

	public void initialize(int cellSize) {
		this.cellSize = cellSize;
	}

	public void addObject(Actor actor) {
		RectBox bounds = this.getActorBounds(actor);
		int by;
		if (this.bspTree == null) {
			byte treeArea;
			if (bounds.getWidth() > bounds.getHeight()) {
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
			RectBox treeArea1 = this.bspTree.getArea();

			while (!treeArea1.contains(bounds)) {
				RectBox newArea;
				BSPCollisionNode newTop;
				if (bounds.getX() < treeArea1.getX()) {
					by = treeArea1.getX() - treeArea1.getWidth();
					newArea = new RectBox(by, treeArea1.getY(), treeArea1
							.getRight()
							- by, treeArea1.getHeight());
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(0);
					newTop.setSplitPos(treeArea1.getX());
					newTop.setChild(1, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}

				if (bounds.getRight() > treeArea1.getRight()) {
					by = treeArea1.getRight() + treeArea1.getWidth();
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(),
							by - treeArea1.getX(), treeArea1.getHeight());
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(0);
					newTop.setSplitPos(treeArea1.getRight());
					newTop.setChild(0, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}

				if (bounds.getY() < treeArea1.getY()) {
					by = treeArea1.getY() - treeArea1.getHeight();
					newArea = new RectBox(treeArea1.getX(), by, treeArea1
							.getWidth(), treeArea1.getTop() - by);
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(1);
					newTop.setSplitPos(treeArea1.getY());
					newTop.setChild(1, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}

				if (bounds.getTop() > treeArea1.getTop()) {
					by = treeArea1.getTop() + treeArea1.getHeight();
					newArea = new RectBox(treeArea1.getX(), treeArea1.getY(),
							treeArea1.getWidth(), by - treeArea1.getY());
					newTop = getBSPNode();
					newTop.getArea().copy(newArea);
					newTop.setSplitAxis(1);
					newTop.setSplitPos(treeArea1.getTop());
					newTop.setChild(0, this.bspTree);
					this.bspTree = newTop;
					treeArea1 = newArea;
				}
			}

			this.insertObject(actor, bounds, bounds, treeArea1, this.bspTree);
		}

	}

	private void insertObject(Actor actor, RectBox actorBounds, RectBox bounds,
			RectBox area, BSPCollisionNode node) {
		if (!node.containsActor(actor)) {
			if (!node.isEmpty()
					&& (area.getWidth() > actorBounds.getWidth() || area
							.getHeight() > actorBounds.getHeight())) {
				RectBox leftArea = node.getLeftArea();
				RectBox rightArea = node.getRightArea();
				RectBox leftIntersects = RectBox.getIntersection(leftArea,
						bounds);
				RectBox rightIntersects = RectBox.getIntersection(rightArea,
						bounds);
				BSPCollisionNode newRight;
				if (leftIntersects != null) {
					if (node.getLeft() == null) {
						newRight = this.createNewNode(leftArea);
						newRight.addActor(actor);
						node.setChild(0, newRight);
					} else {
						this.insertObject(actor, actorBounds, leftIntersects,
								leftArea, node.getLeft());
					}
				}

				if (rightIntersects != null) {
					if (node.getRight() == null) {
						newRight = this.createNewNode(rightArea);
						newRight.addActor(actor);
						node.setChild(1, newRight);
					} else {
						this.insertObject(actor, actorBounds, rightIntersects,
								rightArea, node.getRight());
					}
				}

			} else {
				node.addActor(actor);
			}
		}
	}

	public void clear() {
		if (bspTree != null) {
			synchronized (bspTree) {
				bspTree.clear();
			}
		}
	}

	private BSPCollisionNode createNewNode(RectBox area) {
		byte splitAxis;
		int splitPos;
		if (area.getWidth() > area.getHeight()) {
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

	public final RectBox getActorBounds(Actor actor) {
		return actor.getBoundingRect();
	}

	public void removeObject(Actor object) {
		for (ActorNode node = getNodeForActor(object); node != null; node = getNodeForActor(object)) {
			BSPCollisionNode bspNode = node.getBSPNode();
			node.remove();
			this.checkRemoveNode(bspNode);
		}
	}

	private BSPCollisionNode checkRemoveNode(BSPCollisionNode node) {
		while (true) {
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

			return node;
		}
	}

	public static ActorNode getNodeForActor(Actor object) {
		return (ActorNode) object.getData();
	}

	public static void setNodeForActor(Actor object, ActorNode node) {
		object.setData(node);
	}

	private void updateObject(Actor object) {
		ActorNode node = getNodeForActor(object);
		if (node != null) {
			RectBox newBounds = this.getActorBounds(object);
			BSPCollisionNode bspNode;
			if (!this.bspTree.getArea().contains(newBounds)) {
				while (node != null) {
					bspNode = node.getBSPNode();
					node.remove();
					this.checkRemoveNode(bspNode);
					node = node.getNext();
				}

				this.addObject(object);
			} else {
				RectBox bspArea;
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
						;
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
						bspNode);

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

	public void updateObjectLocation(Actor object, int oldX, int oldY) {
		this.updateObject(object);
	}

	public void updateObjectSize(Actor object) {
		this.updateObject(object);
	}

	private List getIntersectingObjects(RectBox r, CollisionQuery query) {
		HashSet set = new HashSet();
		this.getIntersectingObjects(r, query, set, this.bspTree);
		ArrayList l = new ArrayList(set);
		return l;
	}

	private void getIntersectingObjects(RectBox r, CollisionQuery query,
			Set resultSet, BSPCollisionNode startNode) {
		LinkedList nodeStack = new LinkedList();
		if (startNode != null) {
			nodeStack.add(startNode);
		}
		while (!nodeStack.isEmpty()) {
			BSPCollisionNode node = (BSPCollisionNode) nodeStack.removeLast();
			if (node.getArea().intersects(r)) {
				Iterator i = node.getActorsIterator();
				while (i.hasNext()) {
					Actor left = (Actor) i.next();
					if (query.checkCollision(left) && !resultSet.contains(left)) {
						resultSet.add(left);
					}
				}

				BSPCollisionNode left1 = node.getLeft();
				BSPCollisionNode right = node.getRight();
				if (left1 != null) {
					nodeStack.add(left1);
				}

				if (right != null) {
					nodeStack.add(right);
				}
			}
		}

	}

	private Actor checkForOnlyCollision(Actor ignore, BSPCollisionNode node,
			CollisionQuery query) {
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

	private Actor getOnlyObjectDownTree(Actor ignore, RectBox r,
			CollisionQuery query, BSPCollisionNode startNode) {
		if (startNode == null) {
			return null;
		} else {
			LinkedList nodeStack = new LinkedList();
			nodeStack.add(startNode);
			while (!nodeStack.isEmpty()) {
				BSPCollisionNode node = (BSPCollisionNode) nodeStack
						.removeLast();
				if (node.getArea().intersects(r)) {
					Actor res = this.checkForOnlyCollision(ignore, node, query);
					if (res != null) {
						return res;
					}
					BSPCollisionNode left = node.getLeft();
					BSPCollisionNode right = node.getRight();
					if (left != null) {
						nodeStack.add(left);
					}

					if (right != null) {
						nodeStack.add(right);
					}
				}
			}

			return null;
		}
	}

	private Actor getOnlyIntersectingDown(RectBox r, CollisionQuery query,
			Actor actor) {
		if (this.bspTree == null) {
			return null;
		} else {
			LinkedList nodeStack = new LinkedList();
			nodeStack.add(this.bspTree);

			while (!nodeStack.isEmpty()) {
				BSPCollisionNode node = (BSPCollisionNode) nodeStack
						.removeLast();
				if (node.getArea().contains(r)) {
					Actor res = this.checkForOnlyCollision(actor, node, query);
					if (res != null) {
						return res;
					}

					BSPCollisionNode left = node.getLeft();
					BSPCollisionNode right = node.getRight();
					if (left != null) {
						nodeStack.add(left);
					}
					if (right != null) {
						nodeStack.add(right);
					}
				}
			}
			return null;
		}
	}

	public Actor getOnlyIntersectingUp(RectBox r, CollisionQuery query,
			Actor actor, BSPCollisionNode start) {
		while (start != null && !start.getArea().contains(r)) {
			Actor res = this.checkForOnlyCollision(actor, start, query);
			if (res != null) {
				return res;
			}
			start = start.getParent();
		}
		return null;
	}

	public List getObjectsAt(int x, int y, Class cls) {
		synchronized (this.pointQuery) {
			int px = x * this.cellSize + this.cellSize / 2;
			int py = y * this.cellSize + this.cellSize / 2;
			this.pointQuery.init(px, py, cls);
			return this.getIntersectingObjects(new RectBox(px, py, 1, 1),
					this.pointQuery);
		}
	}

	public List getIntersectingObjects(Actor actor, Class cls) {
		RectBox r = this.getActorBounds(actor);
		synchronized (this.actorQuery) {
			this.actorQuery.init(cls, actor);
			return this.getIntersectingObjects(r, this.actorQuery);
		}
	}

	public List getObjectsInRange(int x, int y, int r, Class cls) {
		int halfCell = this.cellSize / 2;
		int size = 2 * r * this.cellSize;
		RectBox rect = new RectBox((x - r) * this.cellSize + halfCell, (y - r)
				* this.cellSize + halfCell, size, size);
		List result;
		synchronized (this.actorQuery) {
			this.actorQuery.init(cls, (Actor) null);
			result = this.getIntersectingObjects(rect, this.actorQuery);
		}
		Iterator it = result.iterator();
		synchronized (this.inRangeQuery) {
			this.inRangeQuery.init(x * this.cellSize + halfCell, y
					* this.cellSize + halfCell, r * this.cellSize);
			while (it.hasNext()) {
				if (!this.inRangeQuery.checkCollision((Actor) it.next())) {
					it.remove();
				}
			}
			return result;
		}
	}

	public List getNeighbours(Actor actor, int distance, boolean diag, Class cls) {
		int x = actor.getX();
		int y = actor.getY();
		int xPixel = x * this.cellSize;
		int yPixel = y * this.cellSize;
		int dPixel = distance * this.cellSize;
		RectBox r = new RectBox(xPixel - dPixel, yPixel - dPixel,
				dPixel * 2 + 1, dPixel * 2 + 1);

		synchronized (this.neighbourQuery) {
			this.neighbourQuery.init(x, y, distance, diag, cls);
			List res = this.getIntersectingObjects(r, this.neighbourQuery);
			return res;
		}
	}

	public List getObjects(Class cls) {
		HashSet set = new HashSet();
		LinkedList nodeStack = new LinkedList();
		if (this.bspTree != null) {
			nodeStack.add(this.bspTree);
		}
		while (!nodeStack.isEmpty()) {
			BSPCollisionNode list = (BSPCollisionNode) nodeStack.removeLast();
			Iterator i = list.getActorsIterator();
			while (i.hasNext()) {
				Actor left = (Actor) i.next();
				if (cls == null || cls.isInstance(left)) {
					set.add(left);
				}
			}
			BSPCollisionNode left1 = list.getLeft();
			BSPCollisionNode right = list.getRight();
			if (left1 != null) {
				nodeStack.add(left1);
			}
			if (right != null) {
				nodeStack.add(right);
			}
		}

		ArrayList list1 = new ArrayList(set);
		return list1;
	}

	public List getObjectsList() {
		return this.getObjects((Class) null);
	}

	public Actor getOnlyObjectAt(Actor object, int dx, int dy, Class cls) {
		synchronized (this.pointQuery) {
			int px = dx * this.cellSize + this.cellSize / 2;
			int py = dy * this.cellSize + this.cellSize / 2;
			this.pointQuery.init(px, py, cls);
			Object query = this.pointQuery;
			if (cls != null) {
				query = new CollisionClassQuery(cls, this.pointQuery);
			}

			return this.getOnlyIntersectingDown(new RectBox(px, py, 1, 1),
					(CollisionQuery) query, object);
		}
	}

	public Actor getOnlyIntersectingObject(Actor actor, Class cls) {
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

}
