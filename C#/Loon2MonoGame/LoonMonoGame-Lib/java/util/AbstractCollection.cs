using java.lang;

namespace java.util
{
    public abstract class AbstractCollection<V> : Collection<V>
    {
        public virtual bool Add(V obj)
        {
            throw new UnsupportedOperationException();
        }

        public virtual bool AddAll(Collection<V> c)
        {
            Iterator<V> i = c.Iterator();
            bool didappend = false;
            while (i.HasNext())
            {
                if (this.Add(i.Next()))
                {
                    didappend = true;
                }
            }
            return didappend;
        }

        public virtual void Clear()
        {
            java.util.Iterator<V> i = Iterator();
            while (i.HasNext())
            {
                i.Next();
                i.Remove();
            }
        }

        public virtual bool Contains(V obj)
        {
            Iterator<V> i = this.Iterator();
            while (i.HasNext())
            {
                V o = i.Next();
                if (obj == null ? o == null : obj.Equals(o)) { return true; }
            }
            return false;
        }

        public virtual bool ContainsAll(Collection<V> c)
        {
            Iterator<V> i = c.Iterator();
            while (i.HasNext())
            {
                if (!this.Contains(i.Next())) { return false; }
            }
            return true;
        }


        public virtual bool Remove(V o)
        {
            java.util.Iterator<V> i = Iterator();
            while (i.HasNext())
            {
                object e = i.Next();
                if (o == null ? e == null : o.Equals(e))
                {
                    i.Remove();
                    return true;
                }
            }
            return false;
        }

        public virtual bool RemoveIf(java.util.function.Predicate predicate)
        {
            return java.util.Collection_Java<V>.RemoveIf(this, predicate);
        }

        public virtual bool RemoveAll(Collection<V> c)
        {
            java.util.Iterator<V> i = Iterator();
            bool didremove = false;
            while (i.HasNext())
            {
                V o = i.Next();
                if (c.Contains(o))
                {
                    didremove = true;
                    i.Remove();
                }
            }
            return didremove;
        }

        public virtual bool RetainAll(Collection<V> c)
        {
            java.util.Iterator<V> i = Iterator();
            bool didremove = false;
            while (i.HasNext())
            {
                V o = i.Next();
                if (!c.Contains(o))
                {
                    didremove = true;
                    i.Remove();
                }
            }
            return didremove;
        }

        public virtual bool IsEmpty()
        {
            return Size() <= 0;
        }

        public abstract Iterator<V> Iterator();
        public abstract int Size();

        public virtual object[] ToArray()
        {
            object[] a = new object[Size()];
            int cursor = 0;
            for (Iterator<V> i = this.Iterator(); i.HasNext();)
            {
                a[cursor++] = i.Next();
            }
            return a;
        }
        public virtual V[] ToArray(V[] ta)
        {
            V[] a = (V[])
            System.Array.CreateInstance(ta.GetType().GetElementType(), Size());
            int cursor = 0;
            for (Iterator<V> i = this.Iterator(); i.HasNext();)
            {
                a[cursor++] = i.Next();
            }
            return a;
        }

        public override string ToString()
        {
            System.Text.StringBuilder b = new System.Text.StringBuilder("[");
            bool first = true;
            for (Iterator<V> i = this.Iterator(); i.HasNext();)
            {
                if (!first) { b.Append(", "); }
                first = false;
                object o = i.Next();
                b.Append((o == null) ? "null" : o.ToString());
            }
            b.Append("]");
            return b.ToString();
        }

        public virtual void ForEach(java.util.function.Consumer consumer)
        {
            Iterable_Java<V>.ForEach(this, consumer);
        }
    }
}
