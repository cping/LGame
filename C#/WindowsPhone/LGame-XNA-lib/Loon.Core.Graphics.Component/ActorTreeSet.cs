using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Java.Generics;
using Loon.Java;
using Loon.Utils;
using Loon.Java.Collections;

namespace Loon.Core.Graphics.Component
{
    public class ActorTreeSet 
    {

        private readonly static IComparer<object> DEFAULT_COMPARATOR = new _Comparer();

        internal sealed class _Comparer : IComparer<object>
        {
            public int Compare(object o1, object o2)
            {
                return 0;
            }
        }

        private LinkedList subSets = new LinkedList();

        private ActorSet generalSet = new ActorSet();

        private ActorTreeSet.TasIterator iterator;

        public ActorTreeSet()
        {
            this.subSets.Add(this.generalSet);
            this.iterator = new ActorTreeSet.TasIterator(this);
        }

        public void Clear()
        {
            if (subSets != null)
            {
                subSets.Clear();
            }
            if (generalSet != null)
            {
                generalSet.Clear();
            }
        }

        public IIterator Iterator()
        {
            iterator.Reset();
            return iterator;
        }

        public IIterator NewIterator()
        {
            return new ActorTreeSet.TasIterator(this);
        }

        public Actor GetOnlyCollisionObjectsAt(float x, float y)
        {
            for (IIterator it = NewIterator(); it.HasNext(); )
            {
                Actor a = (Actor)it.Next();
                if (a.GetRectBox().Contains(x, y))
                {
                    return a;
                }
            }
            return null;
        }

        public Actor GetOnlyCollisionObjectsAt(float x, float y, object tag)
        {
            for (IIterator it = NewIterator(); it.HasNext(); )
            {
                Actor a = (Actor)it.Next();
                if (a.GetRectBox().Contains(x, y) && a.GetTag() == tag)
                {
                    return a;
                }
            }
            return null;
        }

        public Actor GetSynchronizedObject(float x, float y)
        {
            IIterator iter = NewIterator();
            Actor tmp = (Actor)iter.Next();
            if (tmp == null)
            {
                return null;
            }
            int seq = tmp.GetLastPaintSeqNum();
            int idx = 0;
            for (; iter.HasNext(); )
            {
                Actor actor = (Actor)iter.Next();
                if (actor.GetRectBox().Contains(x, y))
                {
                    int actorSeq = actor.GetLastPaintSeqNum();
                    if (actorSeq > seq)
                    {
                        tmp = actor;
                        seq = actorSeq;
                    }
                    idx++;
                }
            }
            if (idx == 0)
            {
                if (tmp.GetRectBox().Contains(x, y))
                {
                    return tmp;
                }
                else
                {
                    return null;
                }
            }
            return tmp;
        }

        public int Size()
        {
            int size = 0;
            for (IIterator i = new IteratorAdapter(this.subSets.GetEnumerator()); i.HasNext(); size += ((ActorSet)i
                    .Next()).Size())
            {
            }
            return size;
        }

        public bool Add(Actor o)
        {
            if (o == null)
            {
                throw new RuntimeException("Null actor !");
            }
            else
            {
                return this.generalSet.Add(o);
            }
        }

        public bool Remove(Actor o)
        {
            return this.generalSet.Remove(o);
        }

        public bool Contains(Actor o)
        {
            return this.generalSet.Contains(o);
        }

        public Actor[] ToActors()
        {
            return generalSet.ToArray();
        }

        public bool IsEmpty()
        {
            return this.Size() == 0;
        }

        public void SendToFront(Actor actor)
        {
            if (generalSet != null)
            {
                lock (generalSet)
                {
                    Actor[] o = generalSet.ToArray();
                    int size = o.Length;
                    if (o == null || size <= 0)
                    {
                        return;
                    }
                    if (o[size - 1] == actor)
                    {
                        return;
                    }
                    for (int i = 0; i < size; i++)
                    {
                        if (o[i] == actor)
                        {
                            o = (Actor[])CollectionUtils.Cut(o, i);
                            o = (Actor[])CollectionUtils.Expand(o, 1, true);
                            o[size - 1] = actor;
                            Arrays.Sort(o, DEFAULT_COMPARATOR);
                            break;
                        }
                    }
                    generalSet.Clear();
                    generalSet.AddAll(o);
                }
            }
        }

        public void SendToBack(Actor actor)
        {
            if (generalSet != null)
            {
                lock (generalSet)
                {
                    Actor[] o = generalSet.ToArray();
                    int size = o.Length;
                    if (o == null || size <= 0)
                    {
                        return;
                    }
                    if (o[0] == actor)
                    {
                        return;
                    }
                    for (int i = 0; i < size; i++)
                    {
                        if (o[i] == actor)
                        {
                            o = (Actor[])CollectionUtils.Cut(o, i);
                            o = (Actor[])CollectionUtils.Expand(o, 1, false);
                            o[0] = actor;
                            Arrays.Sort(o, DEFAULT_COMPARATOR);
                            break;
                        }
                    }
                    generalSet.Clear();
                    generalSet.AddAll(o);
                }
            }



        }

        class TasIterator : IIterator
        {

            private IIterator setIterator;

            private ActorSet currentSet;

            private IIterator actorIterator;

            private ActorTreeSet actorTreeSet;

            public TasIterator(ActorTreeSet ats)
            {
                this.actorTreeSet = ats;
                this.Reset();
            }

            public void Reset()
            {
                this.setIterator = new IteratorAdapter(actorTreeSet.subSets.GetEnumerator());
                for (this.currentSet = (ActorSet)this.setIterator.Next(); this.currentSet
                        .IsEmpty() && this.setIterator.HasNext(); this.currentSet = (ActorSet)this.setIterator
                        .Next())
                {
                }
                this.actorIterator = this.currentSet.Iterator();
            }

            public void Remove()
            {
                this.actorIterator.Remove();
            }

            public object Next()
            {
                return (Actor)this.actorIterator.Next();
            }

            public bool HasNext()
            {
                if (this.actorIterator.HasNext())
                {
                    return true;
                }
                else if (!this.setIterator.HasNext())
                {
                    return false;
                }
                else
                {
                    while (this.setIterator.HasNext())
                    {
                        this.currentSet = (ActorSet)this.setIterator.Next();
                        if (!this.currentSet.IsEmpty())
                        {
                            break;
                        }
                    }
                    this.actorIterator = this.currentSet.Iterator();
                    return this.actorIterator.HasNext();
                }
            }
        }
    }
}
