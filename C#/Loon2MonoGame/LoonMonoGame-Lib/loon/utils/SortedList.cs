using java.lang;
using java.util;
using java.util.function;

namespace loon.utils
{

    public class SortedList<E> : Iterable<E>, IArray
    {

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        internal class ListItr<E> : LIterator<E>
        {
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
            private SortedList<E>.Node<E> lastReturned;
            private SortedList<E>.Node<E> next;
            private int nextIndex;
            private int expectedModCount;
            private SortedList<E> _list;

            internal ListItr(SortedList<E> l, int idx)
            {
                this._list = l;
                this.next = (idx == _list.size) ? null : _list.LoadNode(idx);
                this.nextIndex = idx;
                expectedModCount = _list.modCount;
            }

            public bool HasNext()
            {
                return nextIndex < _list.size;
            }

            public E Next()
            {
                CheckForComodification();
                if (!HasNext())
                {
                    return default;
                }
                lastReturned = next;
                next = next.next;
                nextIndex++;
                return lastReturned.item;
            }

            public void Remove()
            {
                CheckForComodification();
                if (lastReturned == null)
                {
                    return;
                }

                loon.utils.SortedList<E>.Node<E> lastNext = lastReturned.next;
                _list.Unlink(lastReturned);
                if (next == lastReturned)
                    next = lastNext;
                else
                    nextIndex--;
                lastReturned = null;
                expectedModCount++;
            }

            internal void CheckForComodification()
            {
                if (_list.modCount != expectedModCount)
                    throw new LSysException("SortedList error!");
            }
        }

        public LIterator<E> ListIterator()
        {
            return ListIterator(0);
        }

        public LIterator<E> ListIterator(int index)
        {
            CheckPositionIndex(index);
            return new ListItr<E>(this, index);
        }


        public Iterator<E> Iterator()
        {
            return ListIterator();
        }

        public int modCount = 0;

        public int size = 0;

        Node<E> first;

        Node<E> last;

        public SortedList()
        {
        }

        public SortedList(SortedList<E> c) : this()
        {

            AddAll(c);
        }

        private void LinkFirst(E e)
        {
            Node<E> f = first;
            Node<E> newNode = new Node<E>(null, e, f);
            first = newNode;
            if (f == null)
                last = newNode;
            else
                f.prev = newNode;
            size++;
            modCount++;
        }

        internal void LinkLast(E e)
        {
            Node<E> l = last;
            Node<E> newNode = new Node<E>(l, e, null);
            last = newNode;
            if (l == null)
                first = newNode;
            else
                l.next = newNode;
            size++;
            modCount++;
        }

        internal void LinkBefore(E e, Node<E> succ)
        {
            Node<E> pred = succ.prev;
            Node<E> newNode = new Node<E>(pred, e, succ);
            succ.prev = newNode;
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            size++;
            modCount++;
        }

        private E UnlinkFirst(Node<E> f)
        {
            E element = f.item;
            Node<E> next = f.next;
            f.item = default;
            f.next = null;
            first = next;
            if (next == null)
                last = null;
            else
                next.prev = null;
            size--;
            modCount++;
            return element;
        }

        private E UnlinkLast(Node<E> l)
        {
            E element = l.item;
            Node<E> prev = l.prev;
            l.item = default;
            l.prev = null;
            last = prev;
            if (prev == null)
                first = null;
            else
                prev.next = null;
            size--;
            modCount++;
            return element;
        }

        internal E Unlink(Node<E> x)
        {
            E element = x.item;
            Node<E> next = x.next;
            Node<E> prev = x.prev;

            if (prev == null)
            {
                first = next;
            }
            else
            {
                prev.next = next;
                x.prev = null;
            }

            if (next == null)
            {
                last = prev;
            }
            else
            {
                next.prev = prev;
                x.next = null;
            }

            x.item = default;
            size--;
            modCount++;
            return element;
        }

        public E GetFirst()
        {
            Node<E> f = first;
            if (f == null)
                throw new LSysException("SortedList error!");
            return f.item;
        }

        public E GetLast()
        {
            Node<E> l = last;
            if (l == null)
                throw new LSysException("SortedList error!");
            return l.item;
        }

        public E RemoveFirst()
        {
            Node<E> f = first;
            if (f == null)
                throw new LSysException("SortedList error!");
            return UnlinkFirst(f);
        }

        public E RemoveLast()
        {
            Node<E> l = last;
            if (l == null)
                throw new LSysException("SortedList error!");
            return UnlinkLast(l);
        }

        public void AddFirst(E e)
        {
            LinkFirst(e);
        }

        public void AddLast(E e)
        {
            LinkLast(e);
        }

        public bool Contains(object o)
        {
            return IndexOf(o) != -1;
        }

        public int Size()
        {
            return size;
        }

        public bool Add(E e)
        {
            LinkLast(e);
            return true;
        }

        public bool RemoveAll(SortedList<E> c)
        {
            bool modified = false;
            LIterator<E> it = ListIterator();
            while (it.HasNext())
            {
                if (c.Contains(it.Next()))
                {
                    it.Remove();
                    modified = true;
                }
            }
            return modified;
        }

        public bool Remove(object o)
        {
            if (o == null)
            {
                for (Node<E> x = first; x != null; x = x.next)
                {
                    if (x.item == null)
                    {
                        Unlink(x);
                        return true;
                    }
                }
            }
            else
            {
                for (Node<E> x = first; x != null; x = x.next)
                {
                    if (o.Equals(x.item))
                    {
                        Unlink(x);
                        return true;
                    }
                }
            }
            return false;
        }

        public bool AddAll(SortedList<E> c)
        {
            return AddAll(size, c);
        }

        public bool AddAll(int index, SortedList<E> c)
        {
            CheckPositionIndex(index);

            object[] a = c.ToArray();
            int numNew = a.Length;
            if (numNew == 0)
                return false;

            Node<E> pred, succ;
            if (index == size)
            {
                succ = null;
                pred = last;
            }
            else
            {
                succ = LoadNode(index);
                pred = succ.prev;
            }

            foreach (object o in a)
            {
                E e = (E)o;
                Node<E> newNode = new Node<E>(pred, e, null);
                if (pred == null)
                    first = newNode;
                else
                    pred.next = newNode;
                pred = newNode;
            }

            if (succ == null)
            {
                last = pred;
            }
            else
            {
                pred.next = succ;
                succ.prev = pred;
            }

            size += numNew;
            modCount++;
            return true;
        }

        public void Clear()
        {
            for (Node<E> x = first; x != null;)
            {
                Node<E> next = x.next;
                x.item = default;
                x.next = null;
                x.prev = null;
                x = next;
            }
            first = last = null;
            size = 0;
            modCount++;
        }

        public E Get(int index)
        {
            CheckElementIndex(index);
            return LoadNode(index).item;
        }

        public E Set(int index, E element)
        {
            CheckElementIndex(index);
            Node<E> x = LoadNode(index);
            E oldVal = x.item;
            x.item = element;
            return oldVal;
        }

        public void Add(int index, E element)
        {
            CheckPositionIndex(index);

            if (index == size)
                LinkLast(element);
            else
                LinkBefore(element, LoadNode(index));
        }

        public E Remove(int index)
        {
            CheckElementIndex(index);
            return Unlink(LoadNode(index));
        }

        private bool IsElementIndex(int index)
        {
            return index >= 0 && index < size;
        }

        private bool IsPositionIndex(int index)
        {
            return index >= 0 && index <= size;
        }

        private string OutOfBoundsMsg(int index)
        {
            return "Index: " + index + ", Size: " + size;
        }

        private void CheckElementIndex(int index)
        {
            if (!IsElementIndex(index))
                throw new LSysException(OutOfBoundsMsg(index));
        }

        private void CheckPositionIndex(int index)
        {
            if (!IsPositionIndex(index))
                throw new LSysException(OutOfBoundsMsg(index));
        }

        public Node<E> LoadNode(int index)
        {
            if (index < (size >> 1))
            {
                Node<E> x = first;
                for (int i = 0; i < index; i++)
                    x = x.next;
                return x;
            }
            else
            {
                Node<E> x = last;
                for (int i = size - 1; i > index; i--)
                    x = x.prev;
                return x;
            }
        }

        public int IndexOf(object o)
        {
            int index = 0;
            if (o == null)
            {
                for (Node<E> x = first; x != null; x = x.next)
                {
                    if (x.item == null)
                        return index;
                    index++;
                }
            }
            else
            {
                for (Node<E> x = first; x != null; x = x.next)
                {
                    if (o.Equals(x.item))
                        return index;
                    index++;
                }
            }
            return -1;
        }

        public int LastIndexOf(object o)
        {
            int index = size;
            if (o == null)
            {
                for (Node<E> x = last; x != null; x = x.prev)
                {
                    index--;
                    if (x.item == null)
                        return index;
                }
            }
            else
            {
                for (Node<E> x = last; x != null; x = x.prev)
                {
                    index--;
                    if (o.Equals(x.item))
                        return index;
                }
            }
            return -1;
        }

        public E Peek()
        {
            Node<E> f = first;
            return (f == null) ? default : f.item;
        }

        public E Element()
        {
            return GetFirst();
        }

        public E Poll()
        {
            Node<E> f = first;
            return (f == null) ? default : UnlinkFirst(f);
        }

        public E Remove()
        {
            return RemoveFirst();
        }

        public bool Offer(E e)
        {
            return Add(e);
        }

        public bool OfferFirst(E e)
        {
            AddFirst(e);
            return true;
        }

        public bool OfferLast(E e)
        {
            AddLast(e);
            return true;
        }

        public E PeekFirst()
        {
            Node<E> f = first;
            return (f == null) ? default : f.item;
        }

        public E PeekLast()
        {
            Node<E> l = last;
            return (l == null) ? default : l.item;
        }

        public E PollFirst()
        {
            Node<E> f = first;
            return (f == null) ? default : UnlinkFirst(f);
        }

        public E PollLast()
        {
            Node<E> l = last;
            return (l == null) ? default : UnlinkLast(l);
        }

        public void Push(E e)
        {
            AddFirst(e);
        }

        public E Pop()
        {
            return RemoveFirst();
        }

        public bool RemoveFirstOccurrence(object o)
        {
            return Remove(o);
        }

        public bool RemoveLastOccurrence(object o)
        {
            if (o == null)
            {
                for (Node<E> x = last; x != null; x = x.prev)
                {
                    if (x.item == null)
                    {
                        Unlink(x);
                        return true;
                    }
                }
            }
            else
            {
                for (Node<E> x = last; x != null; x = x.prev)
                {
                    if (o.Equals(x.item))
                    {
                        Unlink(x);
                        return true;
                    }
                }
            }
            return false;
        }

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Node<E>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {
            internal E item;
            internal Node<E> next;
            internal Node<E> prev;

            public Node(Node<E> prev, E element, Node<E> next)
            {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }

        public bool IsEmpty()
        {
            return size == 0;
        }

        public object[] ToArray()
        {
            object[] result = new object[size];
            int i = 0;
            for (Node<E> x = first; x != null; x = x.next)
                result[i++] = x.item;
            return result;
        }

        public override int GetHashCode()
        {
            int hashCode = 1;
            for (Node<E> x = first; x != null; x = x.next)
            {
                hashCode = 31 * hashCode + (x.item == null ? 0 : x.item.GetHashCode());
            }
            return hashCode;
        }


        public override string ToString()
        {
            return ToString(',');
        }

        public string ToString(char separator)
        {
            if (size == 0)
            {
                return "[]";
            }
            StrBuilder buffer = new StrBuilder(32);
            buffer.Append('[');
            for (Node<E> x = first; x != null; x = x.next)
            {
                E o = x.item;
                if (o != null)
                {
                    buffer.Append(x.item);
                    if (x.next != null)
                    {
                        buffer.Append(separator);
                    }
                }
            }
            buffer.Append(']');
            return buffer.ToString();
        }

        public void ForEach(Consumer consumer)
        {
            throw new System.NotImplementedException();
        }
    }

}
