using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.CompilerServices;
using Loon.Utils;
using Loon.Core.Graphics.Opengl;

namespace Loon.Core.Graphics
{

    public abstract class LContainer : LComponent
    {

        public sealed class InnerComponent : IComparer<object>
        {
            public int Compare(object o1, object o2)
            {
                return ((LComponent)o2).GetLayer() - ((LComponent)o1).GetLayer();
            }
        }

        private static readonly IComparer<object> DEFAULT_COMPARATOR = new InnerComponent();

        protected internal bool locked;

        private IComparer<object> comparator = LContainer.DEFAULT_COMPARATOR;

        private LComponent[] childs = new LComponent[0];

        private int childCount = 0;

        private LComponent latestInserted = null;

        public LContainer(int x, int y, int w, int h): base(x, y, w, h)
        {
            this.SetFocusable(false);
        }

        public override bool IsContainer()
        {
            return true;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Add(LComponent comp)
        {
            if (this.Contains(comp))
            {
                return;
            }
            if (comp.GetContainer() != null)
            {
                comp.SetContainer(null);
            }
            comp.SetContainer(this);
            this.childs = (LComponent[])CollectionUtils.Expand(this.childs, 1,
                    false);
            this.childs[0] = comp;
            this.childCount++;
            this.desktop.SetDesktop(comp);
            this.SortComponents();
            this.latestInserted = comp;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Add(LComponent comp, int index)
        {
            if (comp.GetContainer() != null)
            {
                throw new InvalidOperationException(comp
                        + " already reside in another container!!!");
            }
            comp.SetContainer(this);
            LComponent[] newChilds = new LComponent[this.childs.Length + 1];
            this.childCount++;
            int ctr = 0;
            for (int i = 0; i < this.childCount; i++)
            {
                if (i != index)
                {
                    newChilds[i] = this.childs[ctr];
                    ctr++;
                }
            }
            this.childs = newChilds;
            this.childs[index] = comp;
            this.desktop.SetDesktop(comp);
            this.SortComponents();
            this.latestInserted = comp;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool Contains(LComponent comp)
        {
            if (comp == null)
            {
                return false;
            }
            if (childs == null)
            {
                return false;
            }
            for (int i = 0; i < this.childCount; i++)
            {
                if (childs[i] != null && comp.Equals(childs[i]))
                {
                    return true;
                }
            }
            return false;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int Remove(LComponent comp)
        {
            for (int i = 0; i < this.childCount; i++)
            {
                if (this.childs[i] == comp)
                {
                    this.Remove(i);
                    return i;
                }
            }
            return -1;
        }

        public int Remove(Type clazz)
        {
            if (clazz == null)
            {
                return -1;
            }
            int count = 0;
            for (int i = childCount; i > 0; i--)
            {
                int index = i - 1;
                LComponent comp = (LComponent)this.childs[index];
                Type cls = comp.GetType();
                if (clazz == null || (object)clazz == (object)cls || clazz.IsInstanceOfType(comp)
                        || clazz.Equals(cls))
                {
                    this.Remove(index);
                    count++;
                }
            }
            return count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public LComponent Remove(int index)
        {
            LComponent comp = this.childs[index];

            this.desktop.SetComponentStat(comp, false);
            comp.SetContainer(null);
            // comp.dispose();
            this.childs = (LComponent[])CollectionUtils.Cut(this.childs, index);
            this.childCount--;

            return comp;
        }

        public void Clear()
        {
            this.desktop.ClearComponentsStat(this.childs);
            for (int i = 0; i < this.childCount; i++)
            {
                this.childs[i].SetContainer(null);
                // this.childs[i].dispose();
            }
            this.childs = new LComponent[0];
            this.childCount = 0;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Replace(LComponent oldComp, LComponent newComp)
        {
            int index = this.Remove(oldComp);
            this.Add(newComp, index);
        }

        public override void Update(long timer)
        {
            if (isClose)
            {
                return;
            }
            if (!this.IsVisible())
            {
                return;
            }
            lock (childs)
            {
                base.Update(timer);
                LComponent component;
                for (int i = 0; i < this.childCount; i++)
                {
                    component = childs[i];
                    if (component != null)
                    {
                        component.Update(timer);
                    }
                }
            }
        }

        public override void ValidatePosition()
        {
            if (isClose)
            {
                return;
            }
            base.ValidatePosition();

            for (int i = 0; i < this.childCount; i++)
            {
                this.childs[i].ValidatePosition();
            }

            if (!this.elastic)
            {
                for (int i_0 = 0; i_0 < this.childCount; i_0++)
                {
                    if (this.childs[i_0].GetX() > this.GetWidth()
                            || this.childs[i_0].GetY() > this.GetHeight()
                            || this.childs[i_0].GetX() + this.childs[i_0].GetWidth() < 0
                            || this.childs[i_0].GetY() + this.childs[i_0].GetHeight() < 0)
                    {
                        SetElastic(true);
                        break;
                    }
                }
            }
        }

        protected internal override void ValidateSize()
        {
            base.ValidateSize();

            for (int i = 0; i < this.childCount; i++)
            {
                this.childs[i].ValidateSize();
            }
        }

        public override void CreateUI(GLEx g)
        {
            if (isClose)
            {
                return;
            }
            if (!this.IsVisible())
            {
                return;
            }
            lock (childs)
            {
                base.CreateUI(g);
                if (this.elastic)
                {
                    g.SetClip(this.GetScreenX(), this.GetScreenY(),
                            this.GetWidth(), this.GetHeight());
                }
                this.RenderComponents(g);
                if (this.elastic)
                {
                    g.ClearClip();
                }
            }
        }

        protected internal void RenderComponents(GLEx g)
        {
            for (int i = this.childCount - 1; i >= 0; i--)
            {
                this.childs[i].CreateUI(g);
            }
        }

        public void SendToFront(LComponent comp)
        {

            if (this.childCount <= 1 || this.childs[0] == comp)
            {
                return;
            }
            if (childs[0] == comp)
            {
                return;
            }
            for (int i = 0; i < this.childCount; i++)
            {
                if (this.childs[i] == comp)
                {
                    this.childs = (LComponent[])CollectionUtils
                            .Cut(this.childs, i);
                    this.childs = (LComponent[])CollectionUtils.Expand(
                            this.childs, 1, false);
                    this.childs[0] = comp;
                    this.SortComponents();
                    break;
                }
            }
        }

        public void SendToBack(LComponent comp)
        {
            if (this.childCount <= 1 || this.childs[this.childCount - 1] == comp)
            {
                return;
            }
            if (childs[this.childCount - 1] == comp)
            {
                return;
            }
            for (int i = 0; i < this.childCount; i++)
            {
                if (this.childs[i] == comp)
                {
                    this.childs = (LComponent[])CollectionUtils
                            .Cut(this.childs, i);
                    this.childs = (LComponent[])CollectionUtils.Expand(
                            this.childs, 1, true);
                    this.childs[this.childCount - 1] = comp;
                    this.SortComponents();
                    break;
                }
            }
        }

        public void SortComponents()
        {
            Array.Sort(this.childs, this.comparator);
        }

        protected internal void TransferFocus(LComponent component)
        {
            for (int i = 0; i < this.childCount; i++)
            {
                if (component == this.childs[i])
                {
                    int j = i;
                    do
                    {
                        if (--i < 0)
                        {
                            i = this.childCount - 1;
                        }
                        if (i == j)
                        {
                            return;
                        }
                    } while (!this.childs[i].RequestFocus());

                    break;
                }
            }
        }

        protected internal void TransferFocusBackward(LComponent component)
        {
            for (int i = 0; i < this.childCount; i++)
            {
                if (component == this.childs[i])
                {
                    int j = i;
                    do
                    {
                        if (++i >= this.childCount)
                        {
                            i = 0;
                        }
                        if (i == j)
                        {
                            return;
                        }
                    } while (!this.childs[i].RequestFocus());

                    break;
                }
            }
        }

        public override bool IsSelected()
        {
            if (!base.IsSelected())
            {
                for (int i = 0; i < this.childCount; i++)
                {
                    if (this.childs[i].IsSelected())
                    {
                        return true;
                    }
                }
                return false;

            }
            else
            {
                return true;
            }
        }

        public bool IsElastic()
        {
            return this.elastic;
        }

        public void SetElastic(bool b)
        {
            if (GetWidth() > 128 || GetHeight() > 128)
            {
                this.elastic = b;
            }
            else
            {
                this.elastic = false;
            }
        }

        public IComparer<object> GetComparator()
        {
            return this.comparator;
        }

        public void SetComparator(IComparer<object> c)
        {
            if (c == null)
            {
                throw new NullReferenceException("Comparator can not null !");
            }

            this.comparator = c;
            this.SortComponents();
        }

        public LComponent FindComponent(int x1, int y1)
        {
            if (!this.Intersects(x1, y1))
            {
                return null;
            }
            for (int i = 0; i < this.childCount; i++)
            {
                if (this.childs[i].Intersects(x1, y1))
                {
                    LComponent comp = (this.childs[i].IsContainer() == false) ? this.childs[i]
                            : ((LContainer)this.childs[i]).FindComponent(x1, y1);
                    return comp;
                }
            }
            return this;
        }

        public int GetComponentCount()
        {
            return this.childCount;
        }

        public LComponent[] GetComponents()
        {
            return this.childs;
        }

        public LComponent Get()
        {
            return this.latestInserted;
        }

        public override void Dispose()
        {
            base.Dispose();
            if (autoDestroy)
            {
                if (childs != null)
                {
                    foreach (LComponent c in childs)
                    {
                        if (c != null)
                        {
                            c.Dispose();
                        }
                    }
                }
            }
        }

    }
}
