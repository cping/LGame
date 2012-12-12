using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Loon.Core.Graphics.Component
{
    public sealed class ActorNode : LRelease
    {

        private Actor actor;

        private BSPCollisionNode node;

        private ActorNode next;

        private ActorNode prev;

        private bool mark;

        public ActorNode(Actor actor, BSPCollisionNode node)
        {
            this.actor = actor;
            this.node = node;
            ActorNode first = BSPCollisionChecker.GetNodeForActor(actor);
            this.next = first;
            BSPCollisionChecker.SetNodeForActor(actor, this);
            if (this.next != null)
            {
                this.next.prev = this;
            }

            this.mark = true;
        }

        public void ClearMark()
        {
            this.mark = false;
        }

        public void Mark()
        {
            this.mark = true;
        }

        public bool CheckMark()
        {
            bool markVal = this.mark;
            this.mark = false;
            return markVal;
        }

        public Actor GetActor()
        {
            return this.actor;
        }

        public BSPCollisionNode GetBSPNode()
        {
            return this.node;
        }

        public ActorNode GetNext()
        {
            return this.next;
        }

        public void Remove()
        {
            this.Removed();
            this.node.ActorRemoved(this.actor);
        }

        public void Removed()
        {
            if (this.prev == null)
            {
                BSPCollisionChecker.SetNodeForActor(this.actor, this.next);
            }
            else
            {
                this.prev.next = this.next;
            }
            if (this.next != null)
            {
                this.next.prev = this.prev;
            }
        }

        public void Dispose()
        {
            if (node != null)
            {
                node = null;
            }
            if (next != null)
            {
                next.Dispose();
                next = null;
            }
            if (prev != null)
            {
                prev.Dispose();
                prev = null;
            }

        }
    }
}
