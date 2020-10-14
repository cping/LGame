using java.lang;
using System.Collections.Generic;
namespace java.util
{
    public class Vector<T> : ArrayListImpl<T>
    {
        public Vector() : base()
        {
        }

        public Vector(Collection<T> collection) : base(collection)
        {
        }

        public Vector(int capacity) : base()
        {
        }

        public override T Get(int index)
        {
            lock (this) { return base.Get(index); }
        }

        public override T Set(int index, T element)
        {
            lock (this) { return base.Set(index, element); }
        }

        public override void Add(int index, T element)
        {
            lock (this) { base.Add(index, element); }
        }

        public override T Remove(int index)
        {
            lock (this) { return base.Remove(index); }
        }

        public override int Size()
        {
            lock (this) { return base.Size(); }
        }

        public override void TrimToSize()
        {
            lock (this) { base.TrimToSize(); }
        }

        public override bool Add(T e)
        {
            lock (this) { return base.Add(e); }
        }

        public override void Clear()
        {
            lock (this) { base.Clear(); }
        }

        public override object[] ToArray()
        {
            lock (this) { return base.ToArray(); }
        }

        public override T[] ToArray(T[] a)
        {
            lock (this) { return base.ToArray(a); }
        }

        public virtual void AddElement(T o)
        {
            lock (this) { base.Add(o); }
        }

        public virtual object Clone()
        {
            lock (this) { return new Vector<T>(this); }
        }

        public virtual void CopyInto(T[] array)
        {
            lock (this)
            {
                for (int i = 0; i < base.Size(); i++)
                {
                    array[i] = base.Get(i);
                }
            }
        }

        public virtual T ElementAt(int i)
        {
            lock (this) { return base.Get(i); }
        }

        public virtual Enumeration<T> Elements()
        {
            lock (this) { return new AbstractListIterator<T>(this); }
        }

        public virtual T FirstElement()
        {
            lock (this) { return base.Get(0); }
        }

        public virtual int IndexOf(T o, int index)
        {
            lock (this)
            {
                for (int i = index; i < base.Size(); i++)
                {
                    if (o == null ? (base.Get(i) == null) : o.Equals(base.Get(i))) { return i; }
                }
            }
            return -1;
        }

        public virtual void InsertElementAt(T o, int index)
        {
            lock (this) { base.Add(index, o); }
        }

        public virtual T LastElement()
        {
            lock (this) { return base.Get(base.Size() - 1); }
        }

        public virtual int LastIndexOf(T o, int index)
        {
            lock (this)
            {
                for (int i = index; i >= 0; i--)
                {
                    if (o == null ? (base.Get(i) == null) : o.Equals(base.Get(i))) { return i; }
                }
            }
            return -1;
        }

        public virtual void RemoveAllElements()
        {
            lock (this) { base.Clear(); }
        }

        public virtual bool RemoveElement(T o)
        {
            lock (this)
            {
                int idx = IndexOf(o);
                if (idx >= 0)
                {
                    base.Remove(idx);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        public virtual void RemoveElementAt(int index)
        {
            lock (this) { base.Remove(index); }
        }

        public virtual void SetElementAt(T o, int index)
        {
            lock (this) { base.Set(index, o); }
        }

        public virtual void SetSize(int newsize)
        {
            lock (this)
            {
                if (newsize <= 0)
                {
                    if (newsize < 0) { throw new IndexOutOfBoundsException(); }
                    Clear();
                }
                else
                {
                    int need = newsize - base.Size();
                    if (need > 0)
                    {
                        for (int i = 0; i < need; i++) { base.Add(default); }
                    }
                    else if (need < 0)
                    {
                        for (int i = base.Size() - 1; i >= newsize; i--) { base.Remove(i); }
                    }
                }
            }
        }
    }
}
