using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Java.Generics;
using Loon.Java.Collections;
using Loon.Utils;

namespace Loon.Core.Graphics.Component
{
    public class ActorSet
    {

        ActorSet.ListNode listHeadTail = new ActorSet.ListNode();

        ActorSet.ListNode[] hashMap = new ActorSet.ListNode[0];

        private int numActors = 0;

        private int code = 0;

        public override int GetHashCode()
        {
            return this.code;
        }

        public void Clear()
        {
            IIterator e = Iterator();
            while (e.HasNext())
            {
                e.Next();
                e.Remove();
            }
        }

        public Actor[] ToArray()
        {
            Actor[] r = new Actor[numActors];
            IIterator it = Iterator();
            for (int i = 0; i < r.Length; i++)
            {
                if (!it.HasNext())
                {
                    return (Actor[])CollectionUtils.CopyOf(r, i);
                }
                r[i] = (Actor)it.Next();
            }
            return it.HasNext() ? FinishToArray(r, it) : r;
        }

        private static Actor[] FinishToArray(Actor[] r, IIterator it)
        {
            int i = r.Length;
            while (it.HasNext())
            {
                int cap = r.Length;
                if (i == cap)
                {
                    int newCap = ((cap / 2) + 1) * 3;
                    if (newCap <= cap)
                    {
                        if (cap == int.MaxValue)
                            newCap = int.MaxValue;
                    }
                    r = (Actor[])CollectionUtils.CopyOf(r, newCap);
                }
                r[i++] = (Actor)it.Next();
            }
            return (i == r.Length) ? r : (Actor[])CollectionUtils.CopyOf(r, i);
        }

        public bool Add(Actor actor)
        {
            if (this.Contains(actor))
            {
                return false;
            }
            else
            {
                ++this.numActors;
                ActorSet.ListNode newNode = new ActorSet.ListNode(actor,
                        this.listHeadTail.prev);
                int seq = actor.GetSequenceNumber();
                if (this.numActors >= 2 * this.hashMap.Length)
                {
                    this.Resize();
                }
                else
                {
                    int hash = seq % this.hashMap.Length;
                    ActorSet.ListNode hashHead = this.hashMap[hash];
                    this.hashMap[hash] = newNode;
                    newNode.SetHashListHead(hashHead);
                }
                this.code += seq;
                return true;
            }
        }

        public void AddAll(Actor[] o)
        {
            int size = o.Length;
            this.numActors = size;
            this.Resize();
            for (int i = 0; i < size; i++)
            {
                Actor actor = o[i];
                ActorSet.ListNode newNode = new ActorSet.ListNode(actor,
                        this.listHeadTail.prev);
                int seq = actor.GetSequenceNumber();
                int hash = seq % this.hashMap.Length;
                ActorSet.ListNode hashHead = this.hashMap[hash];
                this.hashMap[hash] = newNode;
                newNode.SetHashListHead(hashHead);
                this.code += seq;
            }
        }

        private void Resize(int size)
        {
            this.hashMap = new ActorSet.ListNode[size];
            for (ActorSet.ListNode currentActor = this.listHeadTail.next; currentActor != this.listHeadTail; currentActor = currentActor.next)
            {
                int seq = currentActor.actor.GetSequenceNumber();
                int hash = seq % size;
                ActorSet.ListNode hashHead = this.hashMap[hash];
                this.hashMap[hash] = currentActor;
                currentActor.SetHashListHead(hashHead);
            }
        }

        private void Resize()
        {
            Resize(this.numActors);
        }

        public bool Contains(Actor actor)
        {
            return this.GetActorNode(actor) != null;
        }

        private ActorSet.ListNode GetActorNode(Actor actor)
        {
            if (this.hashMap.Length == 0)
            {
                return null;
            }
            else
            {
                int seq = actor.GetSequenceNumber();
                int hash = seq % this.hashMap.Length;
                ActorSet.ListNode hashHead = this.hashMap[hash];
                if (hashHead == null)
                {
                    return null;
                }
                else if (hashHead.actor == actor)
                {
                    return hashHead;
                }
                else
                {
                    for (ActorSet.ListNode curNode = hashHead.nextHash; curNode != hashHead; curNode = curNode.nextHash)
                    {
                        if (curNode.actor == actor)
                        {
                            return curNode;
                        }
                    }

                    return null;
                }
            }
        }

        public bool IsEmpty()
        {
            return this.numActors == 0;
        }

        public bool Remove(Actor actor)
        {
            ActorSet.ListNode actorNode = this.GetActorNode(actor);
            if (actorNode != null)
            {
                this.Remove(actorNode);
                this.code -= actor.GetSequenceNumber();
                return true;
            }
            else
            {
                return false;
            }
        }

        private void Remove(ActorSet.ListNode actorNode)
        {
            int seq = actorNode.actor.GetSequenceNumber();
            int hash = seq % this.hashMap.Length;
            if (this.hashMap[hash] == actorNode)
            {
                this.hashMap[hash] = actorNode.nextHash;
                if (this.hashMap[hash] == actorNode)
                {
                    this.hashMap[hash] = null;
                }
            }

            actorNode.Remove();
            --this.numActors;
            if (this.numActors <= this.hashMap.Length / 2)
            {
                this.Resize();
            }

        }

        public int Size()
        {
            return this.numActors;
        }

        public IIterator Iterator()
        {
            return new ActorSet.ActorSetIterator(this);
        }

        private class ListNode
        {

            internal Actor actor;

            internal ActorSet.ListNode next;

            internal ActorSet.ListNode prev;

            internal ActorSet.ListNode nextHash;

            internal ActorSet.ListNode prevHash;

            public ListNode()
            {
                this.next = this;
                this.prev = this;
            }

            public ListNode(Actor actor, ActorSet.ListNode listTail)
            {
                this.actor = actor;
                this.next = listTail.next;
                this.prev = listTail;
                listTail.next = this;
                this.next.prev = this;
            }

            public void SetHashListHead(ActorSet.ListNode oldHead)
            {
                if (oldHead == null)
                {
                    this.nextHash = this;
                    this.prevHash = this;
                }
                else
                {
                    this.nextHash = oldHead;
                    this.prevHash = oldHead.prevHash;
                    oldHead.prevHash = this;
                    this.prevHash.nextHash = this;
                }

            }

            public void Remove()
            {
                this.next.prev = this.prev;
                this.prev.next = this.next;
                this.nextHash.prevHash = this.prevHash;
                this.prevHash.nextHash = this.nextHash;
            }
        }

        private class ActorSetIterator : IIterator
        {

            ActorSet.ListNode currentNode;

            ActorSet actorSet;

            public ActorSetIterator(ActorSet set)
            {
                this.actorSet = set;
                this.currentNode = actorSet.listHeadTail;
            }

            public bool HasNext()
            {
                return this.currentNode.next != actorSet.listHeadTail;
            }

            public object Next()
            {
                this.currentNode = this.currentNode.next;
                return this.currentNode.actor;
            }

            public void Remove()
            {
                actorSet.Remove(this.currentNode);
            }
        }
    }
}
