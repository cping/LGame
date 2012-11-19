using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Geom;
using Loon.Utils;
using Loon.Java.Collections;
using System.Collections;

namespace Loon.Core.Graphics.Component
{
    public sealed class BSPCollisionNode
    {

        private Dictionary<Actor, ActorNode> actors;

        private BSPCollisionNode parent;

        private RectBox area;

        private float splitAxis;

        private float splitPos;

        private BSPCollisionNode left;

        private BSPCollisionNode right;

        private bool areaRipple;

        public BSPCollisionNode(RectBox area_0, int splitAxis_1, int splitPos_2)
        {
            
            this.area = area_0;
            this.splitAxis = splitAxis_1;
            this.splitPos = splitPos_2;
            this.actors = new Dictionary<Actor, ActorNode>();
        }

        public void SetChild(int side, BSPCollisionNode child)
        {
            if (side == 0)
            {
                this.left = child;
                if (child != null)
                {
                    child.parent = this;
                }
            }
            else
            {
                this.right = child;
                if (child != null)
                {
                    child.parent = this;
                }
            }
        }

        public void Clear()
        {
            foreach (ActorNode node in actors.Values)
            {
                if (node != null)
                {
                    node.Dispose();
                }
            }
            actors.Clear();
        }

        public void SetArea(RectBox area_0)
        {
            this.area = area_0;
            this.areaRipple = true;
        }

        public void SetSplitAxis(float axis)
        {
            if (axis != this.splitAxis)
            {
                this.splitAxis = axis;
                this.areaRipple = true;
            }
        }

        public void SetSplitPos(float pos)
        {
            if (pos != this.splitPos)
            {
                this.splitPos = pos;
                this.areaRipple = true;
            }

        }

        public float GetSplitAxis()
        {
            return this.splitAxis;
        }

        public float GetSplitPos()
        {
            return this.splitPos;
        }

        public RectBox GetLeftArea()
        {
            return (this.splitAxis == 0) ? new RectBox(this.area.GetX(),
                    this.area.GetY(), this.splitPos - this.area.GetX(),
                    this.area.GetHeight()) : new RectBox(this.area.GetX(),
                    this.area.GetY(), this.area.GetWidth(), this.splitPos
                            - this.area.GetY());
        }

        public RectBox GetRightArea()
        {
            return (this.splitAxis == 0) ? new RectBox(this.splitPos,
                    this.area.GetY(), this.area.GetRight() - this.splitPos,
                    this.area.GetHeight()) : new RectBox(this.area.GetX(),
                    this.splitPos, this.area.GetWidth(), this.area.GetBottom()
                            - this.splitPos);
        }

        public RectBox GetArea()
        {
            return this.area;
        }

        private void ResizeChildren()
        {
            if (this.left != null)
            {
                this.left.SetArea(this.GetLeftArea());
            }

            if (this.right != null)
            {
                this.right.SetArea(this.GetRightArea());
            }

        }

        public BSPCollisionNode GetLeft()
        {
            if (this.areaRipple)
            {
                this.ResizeChildren();
                this.areaRipple = false;
            }
            return this.left;
        }

        public BSPCollisionNode GetRight()
        {
            if (this.areaRipple)
            {
                this.ResizeChildren();
                this.areaRipple = false;
            }
            return this.right;
        }

        public BSPCollisionNode GetParent()
        {
            return this.parent;
        }

        public void SetParent(BSPCollisionNode parent_0)
        {
            this.parent = parent_0;
        }

        public int GetChildSide(BSPCollisionNode child)
        {
            return (this.left == child) ? 0 : 1;
        }

        public void AddActor(Actor actor)
        {
            CollectionUtils.Put(actors,actor, new ActorNode(actor, this));
        }

        public bool ContainsActor(Actor actor)
        {
            ActorNode anode = (ActorNode)CollectionUtils.Get(actors,actor);
            if (anode != null)
            {
                anode.Mark();
                return true;
            }
            else
            {
                return false;
            }
        }

        public void ActorRemoved(Actor actor)
        {
            CollectionUtils.Remove(actors,actor);
        }

        public int NumberActors()
        {
            return this.actors.Count;
        }

        public bool IsEmpty()
        {
            return actors==null||this.actors.Count == 0;
        }

        public IIterator GetEntriesIterator()
        {
            return new IteratorAdapter(this.actors.Values.GetEnumerator());
        }

        public IIterator GetActorsIterator()
        {
            return new IteratorAdapter(this.actors.Keys.GetEnumerator());
        }

        public IList GetActorsList()
        {
            List<Actor> result = new List<Actor>();
            foreach (Actor a in actors.Keys)
            {
                result.Add(a);
            }
            return result;
        }

    }
}
