namespace Loon.Core.Graphics.Component
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Loon.Core.Geom;
    using Loon.Utils;
    using Loon.Java.Collections;

    public class BSPCollisionChecker : CollisionChecker
    {

        private const int MAX_SIZE = 2048;

        private static readonly BSPCollisionNode[] cache = new BSPCollisionNode[MAX_SIZE];

        private int tail = 0;

        private int size = 0;

        public static ActorNode GetNodeForActor(Actor o)
        {
            return (ActorNode)o.data;
        }

        public static void SetNodeForActor(Actor o, ActorNode node)
        {
            o.data = node;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private BSPCollisionNode GetBSPNode()
        {
            if (size == 0)
            {
                return new BSPCollisionNode(new RectBox(), 0, 0);
            }
            else
            {
                int ppos = tail - size;
                if (ppos < 0)
                {
                    ppos += MAX_SIZE;
                }
                BSPCollisionNode node = cache[ppos];
                node.SetParent((BSPCollisionNode)null);
                --size;
                return node;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void ReturnNode(BSPCollisionNode node)
        {
            cache[tail++] = node;
            if (tail == MAX_SIZE)
            {
                tail = 0;
            }
            size = MathUtils.Min(size + 1, MAX_SIZE);
            if (node.GetLeft() != null || node.GetRight() != null)
            {
                throw new Exception("Size Error !");
            }
        }

        private readonly CollisionBaseQuery actorQuery = new CollisionBaseQuery();

        private readonly CollisionNeighbourQuery neighbourQuery = new CollisionNeighbourQuery();

        private readonly CollisionPointQuery pointQuery = new CollisionPointQuery();

        private readonly CollisionInRangeQuery inRangeQuery = new CollisionInRangeQuery();

        private int cellSize;

        private BSPCollisionNode bspTree;

        private HashedSet cacheSet = new HashedSet();

        private LinkedList cacheNodeStack = new LinkedList();

        public virtual void Initialize(int cellSize_0)
        {
            this.cellSize = cellSize_0;
        }

        public virtual IIterator GetActorsIterator()
        {
            if (bspTree != null)
            {
                return bspTree.GetActorsIterator();
            }
            return null;
        }

        public virtual IList GetActorsList()
        {
            if (bspTree != null)
            {
                return bspTree.GetActorsList();
            }
            return null;
        }

        public void AddObject(Actor actor)
        {
            RectBox bounds = this.GetActorBounds(actor);
            float by;
            if (this.bspTree == null)
            {
                byte treeArea;
                if (bounds.width > bounds.height)
                {
                    treeArea = 0;
                    by = bounds.GetMiddleX();
                }
                else
                {
                    treeArea = 1;
                    by = bounds.GetMiddleY();
                }

                this.bspTree = GetBSPNode();
                this.bspTree.GetArea().Copy(bounds);
                this.bspTree.SetSplitAxis(treeArea);
                this.bspTree.SetSplitPos(by);
                this.bspTree.AddActor(actor);
            }
            else
            {
                int idx = 0;
                RectBox treeArea1 = this.bspTree.GetArea();
                RectBox result1 = new RectBox();
                RectBox result2 = new RectBox();
                for (; !treeArea1.Contains(bounds) && idx < MAX_SIZE; )
                {
                    RectBox newArea;
                    BSPCollisionNode newTop;
                    if (bounds.GetX() < treeArea1.GetX())
                    {
                        by = (treeArea1.GetX() - treeArea1.width);
                        newArea = new RectBox(by, treeArea1.GetY(),
                                treeArea1.GetRight() - by, treeArea1.height);
                        newTop = GetBSPNode();
                        newTop.GetArea().Copy(newArea);
                        newTop.SetSplitAxis(0);
                        newTop.SetSplitPos(treeArea1.GetX());
                        newTop.SetChild(1, this.bspTree);
                        this.bspTree = newTop;
                        treeArea1 = newArea;
                    }
                    if (bounds.GetRight() > treeArea1.GetRight())
                    {
                        by = (treeArea1.GetRight() + treeArea1.width);
                        newArea = new RectBox(treeArea1.GetX(), treeArea1.GetY(),
                                by - treeArea1.GetX(), treeArea1.height);
                        newTop = GetBSPNode();
                        newTop.GetArea().Copy(newArea);
                        newTop.SetSplitAxis(0);
                        newTop.SetSplitPos(treeArea1.GetRight());
                        newTop.SetChild(0, this.bspTree);
                        this.bspTree = newTop;
                        treeArea1 = newArea;
                    }
                    if (bounds.GetY() < treeArea1.GetY())
                    {
                        by = (treeArea1.GetY() - treeArea1.height);
                        newArea = new RectBox(treeArea1.GetX(), by,
                                treeArea1.width, treeArea1.GetBottom() - by);
                        newTop = GetBSPNode();
                        newTop.GetArea().Copy(newArea);
                        newTop.SetSplitAxis(1);
                        newTop.SetSplitPos(treeArea1.GetY());
                        newTop.SetChild(1, this.bspTree);
                        this.bspTree = newTop;
                        treeArea1 = newArea;
                    }
                    if (bounds.GetBottom() > treeArea1.GetBottom())
                    {
                        by = (treeArea1.GetBottom() + treeArea1.height);
                        newArea = new RectBox(treeArea1.GetX(), treeArea1.GetY(),
                                treeArea1.width, by - treeArea1.GetY());
                        newTop = GetBSPNode();
                        newTop.GetArea().Copy(newArea);
                        newTop.SetSplitAxis(1);
                        newTop.SetSplitPos(treeArea1.GetBottom());
                        newTop.SetChild(0, this.bspTree);
                        this.bspTree = newTop;
                        treeArea1 = newArea;
                    }
                    idx++;
                }

                this.InsertObject(actor, bounds, bounds, treeArea1, this.bspTree,
                        result1, result2);
            }

        }

        private void InsertObject(Actor actor, RectBox actorBounds, RectBox bounds,
                RectBox area, BSPCollisionNode node, RectBox result1,
                RectBox result2)
        {
            if (!node.ContainsActor(actor))
            {
                if (!node.IsEmpty()
                        && (area.width > actorBounds.width || area.height > actorBounds.height))
                {
                    RectBox leftArea = node.GetLeftArea();
                    RectBox rightArea = node.GetRightArea();
                    RectBox leftIntersects = RectBox.GetIntersection(leftArea,
                            bounds, result1);
                    RectBox rightIntersects = RectBox.GetIntersection(rightArea,
                            bounds, result2);
                    BSPCollisionNode newRight;
                    if (leftIntersects != null)
                    {
                        if (node.GetLeft() == null)
                        {
                            newRight = this.CreateNewNode(leftArea);
                            newRight.AddActor(actor);
                            node.SetChild(0, newRight);
                        }
                        else
                        {
                            this.InsertObject(actor, actorBounds, leftIntersects,
                                    leftArea, node.GetLeft(), result1, result2);
                        }
                    }
                    if (rightIntersects != null)
                    {
                        if (node.GetRight() == null)
                        {
                            newRight = this.CreateNewNode(rightArea);
                            newRight.AddActor(actor);
                            node.SetChild(1, newRight);
                        }
                        else
                        {
                            this.InsertObject(actor, actorBounds, rightIntersects,
                                    rightArea, node.GetRight(), result1, result2);
                        }
                    }

                }
                else
                {
                    node.AddActor(actor);
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void Clear()
        {
            if (bspTree != null)
            {
                bspTree.Clear();
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private BSPCollisionNode CreateNewNode(RectBox area)
        {
            byte splitAxis;
            float splitPos;
            if (area.width > area.height)
            {
                splitAxis = 0;
                splitPos = area.GetMiddleX();
            }
            else
            {
                splitAxis = 1;
                splitPos = area.GetMiddleY();
            }

            BSPCollisionNode newNode = GetBSPNode();
            newNode.SetArea(area);
            newNode.SetSplitAxis(splitAxis);
            newNode.SetSplitPos(splitPos);
            return newNode;
        }

        public RectBox GetActorBounds(Actor actor)
        {
            return actor.GetBoundingRect();
        }

        public void RemoveObject(Actor o) {
			for (ActorNode node = GetNodeForActor(o); node != null; node = GetNodeForActor(o)) {
				BSPCollisionNode bspNode = node.GetBSPNode();
				node.Remove();
				this.CheckRemoveNode(bspNode);
			}
		}

        [MethodImpl(MethodImplOptions.Synchronized)]
        private BSPCollisionNode CheckRemoveNode(BSPCollisionNode node)
        {
            int idx = 0;
            for (; idx < MAX_SIZE; )
            {
                if (node != null && node.IsEmpty())
                {
                    BSPCollisionNode parent = node.GetParent();
                    int side = (parent != null) ? parent.GetChildSide(node) : 3;
                    BSPCollisionNode left = node.GetLeft();
                    BSPCollisionNode right = node.GetRight();
                    if (left == null)
                    {
                        if (parent != null)
                        {
                            if (right != null)
                            {
                                right.SetArea(node.GetArea());
                            }
                            parent.SetChild(side, right);
                        }
                        else
                        {
                            this.bspTree = right;
                            if (right != null)
                            {
                                right.SetParent((BSPCollisionNode)null);
                            }
                        }
                        node.SetChild(1, (BSPCollisionNode)null);
                        ReturnNode(node);
                        node = parent;
                        continue;
                    }

                    if (right == null)
                    {
                        if (parent != null)
                        {
                            if (left != null)
                            {
                                left.SetArea(node.GetArea());
                            }

                            parent.SetChild(side, left);
                        }
                        else
                        {
                            this.bspTree = left;
                            if (left != null)
                            {
                                left.SetParent((BSPCollisionNode)null);
                            }
                        }

                        node.SetChild(0, (BSPCollisionNode)null);
                        ReturnNode(node);
                        node = parent;
                        continue;
                    }
                }
                idx++;
                return node;
            }
            return null;
        }

        private void UpdateObject(Actor o) {
			ActorNode node = GetNodeForActor(o);
			if (node != null) {
				RectBox newBounds = this.GetActorBounds(o);
				BSPCollisionNode bspNode;
				if (!this.bspTree.GetArea().Contains(newBounds)) {
					for (; node != null;) {
						bspNode = node.GetBSPNode();
						node.Remove();
						this.CheckRemoveNode(bspNode);
						node = node.GetNext();
					}
					this.AddObject(o);
				} else {
					RectBox bspArea;
					RectBox result1 = new RectBox();
					RectBox result2 = new RectBox();
					while (node != null) {
						bspNode = node.GetBSPNode();
						bspArea = bspNode.GetArea();
						if (bspArea.Contains(newBounds)) {
							for (ActorNode rNode2 = GetNodeForActor(o); rNode2 != null; rNode2 = rNode2
									.GetNext()) {
								if (rNode2 != node) {
									BSPCollisionNode rNode1 = rNode2.GetBSPNode();
									rNode2.Remove();
									this.CheckRemoveNode(rNode1);
								}
							}
							return;
						}
						if (!bspArea.Intersects(newBounds)) {
							BSPCollisionNode rNode = node.GetBSPNode();
							node.Remove();
							this.CheckRemoveNode(rNode);
						}
						node.ClearMark();
						node = node.GetNext();
					}
					node = GetNodeForActor(o);
					if (node != null) {
						for (bspNode = node.GetBSPNode(); bspNode != null
								&& !bspNode.GetArea().Contains(newBounds); bspNode = bspNode
								.GetParent()) {
							;
						}
						if (bspNode == null) {
							while (node != null) {
								bspNode = node.GetBSPNode();
								node.Remove();
								this.CheckRemoveNode(bspNode);
								node = node.GetNext();
							}
	
							this.AddObject(o);
							return;
						}
					} else {
						bspNode = this.bspTree;
					}
	
					bspArea = bspNode.GetArea();
					this.InsertObject(o, newBounds, newBounds, bspArea,
							bspNode, result1, result2);
					for (node = GetNodeForActor(o); node != null; node = node
							.GetNext()) {
						if (!node.CheckMark()) {
							bspNode = node.GetBSPNode();
							node.Remove();
							this.CheckRemoveNode(bspNode);
						}
					}
	
				}
			}
		}

        public void UpdateObjectLocation(Actor o, float oldX, float oldY) {
			this.UpdateObject(o);
		}

        public void UpdateObjectSize(Actor o) {
			this.UpdateObject(o);
		}

        private IList GetIntersectingObjects(float[] r,
                CollisionQuery query)
        {
            lock (cacheSet)
            {
                CollectionUtils.Clear(cacheSet);
                GetIntersectingObjects(r, query, cacheSet, this.bspTree);
                List<Actor> result = new List<Actor>(cacheSet.Count);
                for (IEnumerator it = cacheSet.GetEnumerator(); it.MoveNext(); )
                {
                    result.Add((Actor)it.Current);
                }
                return result;
            }
        }

        private void IntersectingObjects(float[] r, CollisionQuery query,
                ISet set)
        {
            this.GetIntersectingObjects(r, query, set, this.bspTree);
        }

        private void GetIntersectingObjects(float[] r,
                CollisionQuery query, ISet resultSet, BSPCollisionNode startNode)
        {
            lock (cacheNodeStack)
            {
                cacheNodeStack.Clear();
                try
                {
                    if (startNode != null)
                    {
                        CollectionUtils.Add(cacheNodeStack, startNode);
                    }
                    int idx = 0;
                    for (; !(cacheNodeStack.Count == 0) && idx < MAX_SIZE; )
                    {
                        BSPCollisionNode node = (BSPCollisionNode)cacheNodeStack
                                .RemoveLast();
                        lock (node)
                        {
                            if (node.GetArea().Intersects(r[0], r[1], r[2], r[3]))
                            {
                                IIterator i = node.GetActorsIterator();
                                for (; i.HasNext(); )
                                {
                                    Actor left = (Actor)i.Next();
                                    if (query.CheckCollision(left)
                                            && !CollectionUtils.Contains(left, resultSet))
                                    {
                                        CollectionUtils.Add(resultSet, left);
                                    }
                                }
                                BSPCollisionNode left1 = node.GetLeft();
                                BSPCollisionNode right = node.GetRight();
                                if (left1 != null)
                                {
                                    CollectionUtils.Add(cacheNodeStack, left1);
                                }
                                if (right != null)
                                {
                                    CollectionUtils.Add(cacheNodeStack, right);
                                }
                            }
                        }
                        idx++;
                    }
                }
                catch (Exception ex)
                {
                    Loon.Utils.Debug.Log.Exception(ex);
                }
            }
        }

        private Actor CheckForOnlyCollision(Actor ignore,
                BSPCollisionNode node, CollisionQuery query)
        {
            if (node == null)
            {
                return null;
            }
            IIterator i = node.GetActorsIterator();
            Actor candidate;
            do
            {
                if (!i.HasNext())
                {
                    return null;
                }
                candidate = (Actor)i.Next();
            } while (ignore == candidate || !query.CheckCollision(candidate));
            return candidate;
        }

        private Actor GetOnlyObjectDownTree(Actor ignore, RectBox r,
                CollisionQuery query, BSPCollisionNode startNode)
        {
            if (startNode == null)
            {
                return null;
            }
            else
            {
                lock (cacheNodeStack)
                {
                    cacheNodeStack.Clear();
                    if (startNode != null)
                    {
                        CollectionUtils.Add(cacheNodeStack, startNode);
                    }
                    while (!(cacheNodeStack.Count == 0))
                    {
                        BSPCollisionNode node = (BSPCollisionNode)cacheNodeStack
                                .RemoveLast();
                        if (node.GetArea().Intersects(r))
                        {
                            Actor res = this.CheckForOnlyCollision(ignore, node,
                                    query);
                            if (res != null)
                            {
                                return res;
                            }
                            BSPCollisionNode left = node.GetLeft();
                            BSPCollisionNode right = node.GetRight();
                            if (left != null)
                            {
                                CollectionUtils.Add(cacheNodeStack, left);
                            }
                            if (right != null)
                            {
                                CollectionUtils.Add(cacheNodeStack, right);
                            }
                        }
                    }
                }
                return null;
            }
        }

        private Actor GetOnlyIntersectingDown(RectBox r,
                CollisionQuery query, Actor actor)
        {
            if (this.bspTree == null)
            {
                return null;
            }
            else
            {
                lock (cacheNodeStack)
                {
                    cacheNodeStack.Clear();
                    CollectionUtils.Add(cacheNodeStack, this.bspTree);
                    int idx = 0;
                    for (; !(cacheNodeStack.Count == 0) && idx < MAX_SIZE; )
                    {
                        BSPCollisionNode node = (BSPCollisionNode)cacheNodeStack
                                .RemoveLast();
                        if (node.GetArea().Contains(r))
                        {
                            Actor res = this.CheckForOnlyCollision(actor, node,
                                    query);
                            if (res != null)
                            {
                                return res;
                            }

                            BSPCollisionNode left = node.GetLeft();
                            BSPCollisionNode right = node.GetRight();
                            if (left != null)
                            {
                                CollectionUtils.Add(cacheNodeStack, left);
                            }
                            if (right != null)
                            {
                                CollectionUtils.Add(cacheNodeStack, right);
                            }
                        }
                    }
                }
                return null;
            }
        }

        public Actor GetOnlyIntersectingUp(RectBox r,
                CollisionQuery query, Actor actor, BSPCollisionNode start)
        {
            for (; start != null && !start.GetArea().Contains(r); )
            {
                Actor res = this.CheckForOnlyCollision(actor, start, query);
                if (res != null)
                {
                    return res;
                }
                start = start.GetParent();
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual IList GetObjectsAt(float x, float y, Type cls)
        {
            lock (this.pointQuery)
            {
                float px = x * this.cellSize + this.cellSize / 2;
                float py = y * this.cellSize + this.cellSize / 2;
                this.pointQuery.Init(px, py, cls);
                float[] r = { px, py, 1, 1 };
                return this.GetIntersectingObjects(r, this.pointQuery);
            }
        }

        public IList GetIntersectingObjects(Actor actor, Type cls)
        {
            RectBox r = this.GetActorBounds(actor);
            lock (this.actorQuery)
            {
                this.actorQuery.Init(cls, actor);
                return this.GetIntersectingObjects(r.ToFloat(), this.actorQuery);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual IList GetObjectsInRange(float x, float y, float r,
                Type cls)
        {
            float halfCell = this.cellSize / 2;
            float size = 2 * r * this.cellSize;
            float[] rect = { (x - r) * this.cellSize + halfCell,
				(y - r) * this.cellSize + halfCell, size, size };
            cacheSet.Clear();
            lock (this.actorQuery)
            {
                this.actorQuery.Init(cls, (Actor)null);
                IntersectingObjects(rect, this.actorQuery, cacheSet);
            }
            lock (this.inRangeQuery)
            {
                this.inRangeQuery.Init(x * this.cellSize + halfCell, y
                        * this.cellSize + halfCell, r * this.cellSize);
                IList rangeResult = new List<Actor>();
                IEnumerator it = cacheSet.GetEnumerator();
                for (; it.MoveNext(); )
                {
                    Actor a = (Actor)it.Current;
                    if (this.inRangeQuery.CheckCollision(a))
                    {
                        rangeResult.Add(a);
                    }
                }
                return rangeResult;
            }
        }

        public IList GetNeighbours(Actor actor, float distance,
                bool diag, Type cls)
        {
            float x = actor.GetX();
            float y = actor.GetY();
            float xPixel = x * this.cellSize;
            float yPixel = y * this.cellSize;
            float dPixel = distance * this.cellSize;
            float[] r = { xPixel - dPixel, yPixel - dPixel, dPixel * 2 + 1,
					dPixel * 2 + 1 };
            lock (this.neighbourQuery)
            {
                this.neighbourQuery.Init(x, y, distance, diag, cls);
                IList res = this.GetIntersectingObjects(r, this.neighbourQuery);
                return res;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual IList GetObjects(Type cls)
        {
            lock (cacheSet)
            {
                CollectionUtils.Clear(cacheSet);
            }
            lock (cacheNodeStack)
            {
                if (this.bspTree != null)
                {
                    CollectionUtils.Add(cacheNodeStack, this.bspTree);
                }
                for (; !(cacheNodeStack.Count == 0); )
                {
                    BSPCollisionNode node = (BSPCollisionNode)cacheNodeStack
                            .RemoveLast();
                    IIterator i = node.GetActorsIterator();
                    while (i.HasNext())
                    {
                        Actor left = (Actor)i.Next();
                        if (cls == null || cls.IsInstanceOfType(left))
                        {
                            CollectionUtils.Add(cacheSet, left);
                        }
                    }
                    BSPCollisionNode left1 = node.GetLeft();
                    BSPCollisionNode right = node.GetRight();
                    if (left1 != null)
                    {
                        CollectionUtils.Add(cacheNodeStack, left1);
                    }
                    if (right != null)
                    {
                        CollectionUtils.Add(cacheNodeStack, right);
                    }
                }
                List<Actor> result = new List<Actor>(cacheSet.Count);
                for (IEnumerator it = cacheSet.GetEnumerator();it.MoveNext();)
                {
                    result.Add((Actor)it.Current);
                }
                    return result;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual IList GetObjectsList()
        {
            return this.GetObjects((Type)null);
        }

        public Actor GetOnlyObjectAt(Actor o, float dx, float dy,
                Type cls) {
			 lock (this.pointQuery) {
						float px = dx * this.cellSize + this.cellSize / 2;
						float py = dy * this.cellSize + this.cellSize / 2;
						this.pointQuery.Init(px, py, cls);
						object query = this.pointQuery;
						if (cls != null) {
							query = new CollisionClassQuery(cls, this.pointQuery);
						}
						return this.GetOnlyIntersectingDown(new RectBox(px, py, 1, 1),
								(CollisionQuery) query, o);
					}
		}

        public Actor GetOnlyIntersectingObject(Actor actor, Type cls)
        {
            RectBox rect = this.GetActorBounds(actor);
            lock (this.actorQuery)
            {
                this.actorQuery.Init(cls, actor);
                ActorNode node = GetNodeForActor(actor);
                if (node == null)
                {
                    return null;
                }
                do
                {
                    BSPCollisionNode bspNode = node.GetBSPNode();
                    Actor result = this.GetOnlyObjectDownTree(actor, rect,
                            this.actorQuery, bspNode);
                    if (result != null)
                    {
                        return result;
                    }
                    result = this.GetOnlyIntersectingUp(rect, this.actorQuery,
                            actor, bspNode.GetParent());
                    if (result != null)
                    {
                        return result;
                    }
                    node = node.GetNext();
                } while (node != null);
                return this.GetOnlyIntersectingDown(rect, this.actorQuery, actor);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void Dispose()
        {
            if (cacheSet != null)
            {
                CollectionUtils.Clear(cacheSet);
            }
            if (cacheNodeStack != null)
            {
                cacheNodeStack.Clear();
            }
            if (cache != null)
            {
                for (int i = 0; i < cache.Length; i++)
                {
                    if (cache[i] != null)
                    {
                        cache[i] = null;
                    }
                }
            }
        }

    }
}
