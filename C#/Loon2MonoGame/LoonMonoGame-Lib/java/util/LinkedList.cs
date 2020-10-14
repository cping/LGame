namespace java.util
{

    using System;
    using System.Collections.Generic;
    using System.Collections;
    using java.lang;

    public class LinkedList<V> : AbstractList<V>
    {
        private int currentIndex;

        private int len;

        private readonly Node<V> head;

        private Node<V> currentNode;

        public LinkedList()
        {
            Node<V> h = new Node<V>(default);
            h.next = h;
            h.prev = h;
            this.head = h;
            this.len = 0;
            this.currentNode = h;
            this.currentIndex = -1;
        }

        public LinkedList(Collection<V> collection) : this()
        {
            AddAll(collection);
        }

        public override V Get(int index)
        {
            return Seek(index).element;
        }

        public override V Set(int index, V element)
        {
            Node<V> n = Seek(index);
            V prev = n.element;
            n.element = element;
            return prev;
        }

        public override void Add(int index, V element)
        {
            Node<V> n = new Node<V>(element);
            Node<V> y;
            if (index == len)
            {
                y = head;
            }
            else
            {
                y = Seek(index);
                if (currentIndex >= index) { currentIndex++; }
            }
            Node<V> x = y.prev;
            n.prev = x;
            n.next = y;
            x.next = n;
            y.prev = n;
            len++;
            return;
        }

        public override V Remove(int index)
        {
            Node<V> n = Seek(index);
            Node<V> x = n.prev;
            Node<V> y = n.next;
            x.next = y;
            y.prev = x;
            len--;
            if (currentIndex >= index)
            {
                if (currentIndex == index)
                {
                    currentNode = x;
                }
                currentIndex--;
            }
            return n.element;
        }

        public override int Size()
        {
            return len;
        }

        private Node<V> Seek(int index)
        {
            if (index < 0 || index >= len) { throw new IndexOutOfBoundsException(); }
            if (index == 0) { return head.next; }
            if (index == len - 1) { return head.prev; }

            int ci = currentIndex;
            if (index == ci) { return currentNode; }

            Node<V> n = null;
            if (index < ci)
            {
                if (index <= ci - index)
                {
                    n = head.next;
                    for (int i = 0; i < index; i++)
                    {
                        n = n.next;
                    }
                }
                else
                {
                    n = currentNode;
                    for (int i = currentIndex; i > index; i--)
                    {
                        n = n.prev;
                    }
                }
            }
            else
            {
                if (index - ci <= len - index)
                {
                    n = currentNode;
                    for (int i = ci; i < index; i++)
                    {
                        n = n.next;
                    }
                }
                else
                {
                    n = head.prev;
                    for (int i = len - 1; i > index; i--)
                    {
                        n = n.prev;
                    }
                }
            }

            currentNode = n;
            currentIndex = index;
            return n;
        }

        class Node<T>
        {
            public T element;
            public Node<T> prev;
            public Node<T> next;

            public Node(T element)
            {
                this.element = element;
                prev = null;
                next = null;
            }

        }

        public virtual void AddFirst(V obj)
        {
            Add(0, obj);
        }

        public virtual void AddLast(V obj)
        {
            Add(Size(), obj);
        }

        public virtual V GetFirst()
        {
            return Get(0);
        }

        public virtual V GetLast()
        {
            return Get(Size() - 1);
        }

        public virtual V RemoveFirst()
        {
            return Remove(0);
        }

        public virtual V RemoveLast()
        {
            return Remove(Size() - 1);
        }

        public override void Clear()
        {
            Node<V> h = head;
            h.next = h;
            h.prev = h;
            len = 0;
            currentNode = h;
            currentIndex = -1;
        }
    }


}
