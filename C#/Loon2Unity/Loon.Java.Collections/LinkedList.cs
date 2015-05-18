namespace Loon.Java.Collections
{

    using System;
    using System.Collections.Generic;
    using Loon.Java.Collections;
    using System.Collections;

    public class LinkedList : ExtendedCollectionBase, IList, IExtendedCollection, ICollection, IEnumerable, ICloneable
    {

        private LinkedList<object> list;

        public LinkedList()
        {
            this.list = new LinkedList<object>();
        }

        public LinkedList(ICollection c)
            : this()
        {
            foreach (object obj2 in c)
            {
                this.list.AddLast(obj2);
            }
        }

        public override bool Add(object e)
        {
            this.list.AddLast(e);
            return true;
        }

        public void AddFirst(object e)
        {
            this.list.AddFirst(e);
        }

        public void AddLast(object e)
        {
            this.list.AddLast(e);
        }

        public void Clear()
        {
            this.list.Clear();
        }

        public object Clone()
        {
            return new LinkedList(this);
        }

        private static bool CollectionContains(ICollection c, object v)
        {
            foreach (object obj2 in c)
            {
                if (object.Equals(obj2, v))
                {
                    return true;
                }
            }
            return false;
        }

        public override bool Contains(object e)
        {
            return this.list.Contains(e);
        }

        private LinkedListNode<object> GetNodeAt(int index)
        {
            LinkedListNode<object> last;
            if ((index < 0) || (index >= this.list.Count))
            {
                throw new ArgumentOutOfRangeException();
            }
            if (index < (this.list.Count / 2))
            {
                last = this.list.First;
                for (int j = 0; j < index; j++)
                {
                    last = last.Next;
                }
                return last;
            }
            last = this.list.Last;
            for (int i = this.list.Count - 1; i > index; i--)
            {
                last = last.Previous;
            }
            return last;
        }

        public int IndexOf(object value)
        {
            int num = 0;
            foreach (object obj2 in this.list)
            {
                if (object.Equals(obj2, value))
                {
                    return num;
                }
                num++;
            }
            return -1;
        }

        public void Insert(int index, object value)
        {
            if (index == this.list.Count)
            {
                this.list.AddLast(value);
            }
            LinkedListNode<object> nodeAt = this.GetNodeAt(index);
            this.list.AddBefore(nodeAt, value);
        }

        public override bool Remove(object e)
        {
            return this.list.Remove(e);
        }

        public object Poll()
        {
            return RemoveFirst();
        }

        public bool IsEmpty()
        {
            return list.Count == 0;
        }

        public void RemoveAt(int index)
        {
            LinkedListNode<object> nodeAt = this.GetNodeAt(index);
            this.list.Remove(nodeAt);
        }

        public object RemoveFirst()
        {
            object obj2 = this.list.First.Value;
            this.list.RemoveFirst();
            return obj2;
        }

        public object RemoveLast()
        {
            object obj2 = this.list.Last.Value;
            this.list.RemoveLast();
            return obj2;
        }

        public override bool RetainAll(ICollection c)
        {
            LinkedListNode<object> first = this.list.First;
            int count = this.list.Count;
            while (first != null)
            {
                if (!CollectionContains(c, first.Value))
                {
                    LinkedListNode<object> next = first.Next;
                    this.list.Remove(first);
                    first = next;
                }
                else
                {
                    first = first.Next;
                }
            }
            return (this.list.Count != count);
        }

        int IList.Add(object value)
        {
            this.list.AddLast(value);
            return (this.list.Count - 1);
        }

        void IList.Remove(object value)
        {
            this.list.Remove(value);
        }

        public LinkedListNode<object> First
        {
            get
            {
                return this.list.First;
            }
        }

        protected override ICollection InnerCollection
        {
            get
            {
                return this.list;
            }
        }

        public bool IsFixedSize
        {
            get
            {
                return false;
            }
        }

        public bool IsReadOnly
        {
            get
            {
                return false;
            }
        }

        public object this[int index]
        {
            get
            {
                return this.GetNodeAt(index).Value;
            }
            set
            {
                LinkedListNode<object> nodeAt = this.GetNodeAt(index);
                this.list.AddAfter(nodeAt, value);
                this.list.Remove(nodeAt);
            }
        }

        public LinkedListNode<object> Last
        {
            get
            {
                return this.list.Last;
            }
        }
    }


}
