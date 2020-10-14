using java.lang;

namespace java.util
{
    public abstract class AbstractList<V> : AbstractCollection<V>, List<V>
    {
        public abstract V Get(int index);
        public abstract V Set(int index, V element);
        public abstract void Add(int index, V element);
        public abstract V Remove(int index);

        public override bool Add(V e)
        {
            Add(Size(), e);
            return true;
        }

        public virtual bool AddAll(int index, Collection<V> c)
        {
            Iterator<V> i = c.Iterator();
            int pos = index;
            bool didappend = false;
            while (i.HasNext())
            {
                this.Add(pos++, i.Next());
                didappend = true;
            }
            return didappend;
        }

        public override void Clear()
        {
            for (int i = Size() - 1; i >= 0; i--)
            {
                Remove(i);
            }
        }

        public override bool Equals(object b)
        {
            if (b == null || !(b is List<V>)) { return false; }
            int s = this.Size();
            List<V> l = (List<V>)b;
            if (s != l.Size())
            {
                return false;
            }
            for (int i = 0; i < s; i++)
            {
                object e1 = this.Get(i);
                object e2 = l.Get(i);
                if (!(e1 == null ? e2 == null : e1.Equals(e2))) { return false; }
            }
            return true;
        }

        public override int GetHashCode()
        {
            int hashCode = 1;
            int s = Size();
            for (int i = 0; i < s; i++)
            {
                object e = Get(i);
                hashCode = (31 * hashCode + (e == null ? 0 : e.GetHashCode())) & (-1);
            }
            return hashCode;
        }

        public virtual int IndexOf(V o)
        {
            int s = Size();
            for (int i = 0; i < s; i++)
            {
                if (o == null ? (Get(i) == null) : o.Equals(Get(i))) { return i; }
            }
            return -1;
        }

        public override Iterator<V> Iterator()
        {
            return new AbstractListIterator<V>(this);
        }

        public virtual int LastIndexOf(V o)
        {
            for (int i = Size() - 1; i >= 0; i--)
            {
                if (o == null ? (Get(i) == null) : o.Equals(Get(i))) { return i; }
            }
            return -1;
        }

        public override bool Remove(V o)
        {
            int idx = IndexOf(o);
            if (idx >= 0)
            {
                Remove(idx);
                return true;
            }
            return false;
        }


        public virtual void ReplaceAll(java.util.function.UnaryOperator unaryoperator)
        {
            java.util.List_Java<V>.ReplaceAll(this, unaryoperator);
        }

        public virtual void Sort(java.util.Comparator<V> c)
        {
            java.util.List_Java<V>.Sort(this, c);
        }
    }

    class AbstractListIterator<V> : Iterator<V>, Enumeration<V>
    {
        private readonly AbstractList<V> list;
        private int n;

        public AbstractListIterator(AbstractList<V> list)
        {
            this.list = list;
            this.n = 0;
        }

        public bool HasNext()
        {
            return n < list.Size();
        }

        public V Next()
        {
            if (n >= list.Size()) { throw new NoSuchElementException(); }
            V v = list.Get(n);
            n++;
            return v;
        }
        public void Remove()
        {
            int before = n - 1;
            if (before < 0) { throw new IllegalStateException(); }
            list.Remove(before);
            n = before;
        }

        public bool HasMoreElements()
        {
            return HasNext();
        }

        public V NextElement()
        {
            return Next();
        }
    }
}
