namespace Loon.Java.Generics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Collections;

    public class LinkedListNode<T>
    {
        public T Data;
        public LinkedListNode<T> Prev;
        public LinkedListNode<T> Next;

        public LinkedListNode(T Data)
        {
            this.Data = Data;
        }
    }

   public class LinkedList<T>
    {

        LinkedListNode<T> Head;
        LinkedListNode<T> Tail;
        int LinkedList_Size;

        public LinkedList()
        {
            Head = null;
            Tail = null;
            LinkedList_Size = 0;
        }

        public LinkedList(T[] collection)
        {
            Head = null;
            Tail = null;
            LinkedList_Size = 0;
            AddAll_Internal(Tail, collection);
        }

        private LinkedListNode<T> Add_Internal_Before(LinkedListNode<T> offset, T element)
        {
            if (offset == null)
                throw new Exception("Add_Internal: null is not a valid argument");

            LinkedListNode<T> Listelement = new LinkedListNode<T>(element);

            Listelement.Prev = offset.Prev;
            Listelement.Next = offset;

            if (offset.Prev != null) offset.Prev.Next = Listelement;
            offset.Prev = Listelement;

            if (offset == Head)
                Head = Listelement;

            LinkedList_Size++;

            return Listelement;
        }

        private LinkedListNode<T> Add_Internal_After(LinkedListNode<T> offset, T element)
        {
            if (offset == null)
                throw new Exception("Add_Internal: null is not a valid argument");

            LinkedListNode<T> Listelement = new LinkedListNode<T>(element);

            Listelement.Prev = offset;
            Listelement.Next = offset.Next;
            if (offset.Next != null) offset.Next.Prev = Listelement;
            offset.Next = Listelement;

            if (offset == Tail)
                Tail = Listelement;

            LinkedList_Size++;

            return Listelement;
        }

        public bool Add(int Index, T element)
        {
            if ((Index == 0) && (Head == null))
                AddFirst(element);
            else
                Add_Internal_Before(Get_Internal(Index), element);
            return true;
        }

        public void Add(T element)
        {
            if (Head == null)
                AddFirst(element);
            else
                Add_Internal_After(Tail, element);
        }

        public void AddFirst(T element)
        {
            LinkedListNode<T> ListElement = new LinkedListNode<T>(element);
            if (Tail == null)
            {
                Tail = ListElement;
            }
            else
            {
                ListElement.Next = Head;
                Head.Prev = ListElement;
            }
            Head = ListElement;
            LinkedList_Size++;
        }

        public void AddLast(T element)
        {
            LinkedListNode<T> Listelement = new LinkedListNode<T>(element);
            if (Head == null)
            {
                Head = Listelement;
            }
            else
            {
                Listelement.Prev = Tail;
                Tail.Next = Listelement;
            }
            Tail = Listelement;
            LinkedList_Size++;
        }

        private void AddAll_Internal(LinkedListNode<T> start, T[] collection)
        {
            bool First = true;
            foreach (T element in collection)
            {
                if (Head == null)
                {
                    AddFirst(element);
                    start = Head;
                    First = false;
                }
                else
                {
                    if (First)
                    {
                        start = Add_Internal_Before(start, element);
                        First = false;
                    }
                    else
                        start = Add_Internal_After(start, element);
                }
            }
        }

        public void AddAll(T[] collection)
        {
            AddAll_Internal(Tail, collection);
        }

        public void AddAll(int index, T[] collection)
        {
            AddAll_Internal(Get_Internal(index), collection);
        }

        public void Clear()
        {
            LinkedListNode<T> Temp;

            while (Head != null)
            {
                Temp = Head;
                Head = Head.Next;

                Temp.Prev = null;
                Temp.Next = null;
            }

            Tail = null;
            LinkedList_Size = 0;
        }

        public LinkedList<T> Clone()
        {
            LinkedList<T> ClonedCopy = new LinkedList<T>();

            LinkedListNode<T> Iterator = Head;
            while (Iterator != null)
            {
                ClonedCopy.AddLast(Iterator.Data);
                Iterator = Iterator.Next;
            }
            return ClonedCopy;
        }

        public bool Contains(T needle)
        {
            LinkedListNode<T> Iterator = Head;
            while (Iterator != null)
            {
                if ((needle == null) && (Iterator.Data == null))
                    return true;
                if (Iterator.Data != null)
                    if (Iterator.Data.Equals(needle))
                        return true;
                Iterator = Iterator.Next;
            }
            return false;
        }

        public int Size()
        {
            return LinkedList_Size;
        }

        private LinkedListNode<T> Get_Internal(int Index)
        {
            if ((LinkedList_Size == 0) || (Index < 0) || (Index >= LinkedList_Size))
                throw new IndexOutOfRangeException("LinkedList<T> Invalid Index");

            LinkedListNode<T> iterator;
            int DistanceFromHead = Index;
            int DistanceFromTail = LinkedList_Size - Index - 1;

            if (DistanceFromHead < DistanceFromTail)
            {
                iterator = Head;
                while (iterator != null && DistanceFromHead-- > 0)
                    iterator = iterator.Next;
            }
            else
            {
                iterator = Tail;
                while (iterator != null && DistanceFromTail-- > 0)
                    iterator = iterator.Prev;
            }

            return iterator;
        }

        public T Get(int index)
        {
            return Get_Internal(index).Data;
        }

        public T GetFirst()
        {
            if (LinkedList_Size == 0)
                throw new IndexOutOfRangeException("LinkedList<T> Invalid Index");
            return Head.Data;
        }

        public T GetLast()
        {
            if (LinkedList_Size == 0)
                throw new IndexOutOfRangeException("LinkedList<T> Invalid Index");
            return Tail.Data;
        }

        private LinkedListNode<T> IndexOf_Internal(T element)
        {
            int Index = 0;
            LinkedListNode<T> Iterator = Head;
            while (Iterator != null)
            {
                if ((element == null) && (Iterator.Data == null))
                    return Iterator;
                if (Iterator.Data != null)
                    if (Iterator.Data.Equals(element))
                        return Iterator;

                Index++;
                Iterator = Iterator.Next;
            }
            return null;
        }

        public int IndexOf(T element)
        {
            int Index = 0;
            LinkedListNode<T> Iterator = Head;
            while (Iterator != null)
            {
                if ((element == null) && (Iterator.Data == null))
                    return Index;
                if (Iterator.Data != null)
                    if (Iterator.Data.Equals(element))
                        return Index;

                Index++;
                Iterator = Iterator.Next;
            }
            return -1;
        }

        public int LastIndexOf(T element)
        {
            int Index = LinkedList_Size;
            LinkedListNode<T> Iterator = Tail;
            while (Iterator != null)
            {
                Index--;

                if ((element == null) && (Iterator.Data == null))
                    return Index;

                if (Iterator.Data != null)
                    if (Iterator.Data.Equals(element))
                        return Index;

                Iterator = Iterator.Prev;
            }
            return -1;
        }

        public void Remove_Internal(LinkedListNode<T> element)
        {
            if (element == null)
                throw new Exception("LinkedListNode<T> null is not a valid argument");

            if (element == Head)
                Head = element.Next;

            if (element == Tail)
                Tail = element.Prev;

            if (element.Prev != null)
                element.Prev.Next = element.Next;

            if (element.Next != null)
                element.Next.Prev = element.Prev;

            LinkedList_Size--;
        }

        public T Remove(int Index)
        {
            LinkedListNode<T> Element = Get_Internal(Index);

            T Data = Element.Data;

            Remove_Internal(Element);

            return Data;
        }

        public bool Remove(T element)
        {
            LinkedListNode<T> Node = IndexOf_Internal(element);

            if (Node != null)
            {
                Remove_Internal(Node);
                return true;
            }

            return false;
        }

        public T RemoveFirst()
        {
            if (LinkedList_Size == 0)
                throw new IndexOutOfRangeException("LinkedList<T> Empty List");

            T Data = Head.Data;

            Remove_Internal(Head);

            return Data;
        }

        public T RemoveLast()
        {
            if (LinkedList_Size == 0)
                throw new IndexOutOfRangeException("LinkedList<T> Empty List");

            T Data = Tail.Data;

            Remove_Internal(Tail);

            return Data;
        }

        public T Set(int Index, T element)
        {
            LinkedListNode<T> OldListEntry = Get_Internal(Index);

            T Temp = OldListEntry.Data;

            OldListEntry.Data = element;

            return Temp;
        }

        public T[] ToArray()
        {
            LinkedListNode<T> iterator = Head;
            T[] DataArray = new T[LinkedList_Size];
            int Index = 0;

            while (iterator != null)
            {
                DataArray[Index++] = iterator.Data;
                iterator = iterator.Next;
            }

            return DataArray;
        }

        public IEnumerator<T> GetEnumerator()
        {
            return new LinkedListEnumerator(this);
        }

        public class LinkedListEnumerator : IEnumerator<T>
        {
            LinkedListNode<T> start;
            LinkedListNode<T> current;

            public LinkedListEnumerator(LinkedList<T> list)
            {
                start = list.Head;
                current = null;
            }
            public bool MoveNext()
            {
                if (current == null) current = start;
                else current = current.Next;
                return (current != null);
            }

            public T Current
            {
                get
                {
                    if (current == null)
                        throw new InvalidOperationException();
                    return current.Data;
                }
            }

            object IEnumerator.Current
            {
                get
                {
                    if (current == null)
                        throw new InvalidOperationException();
                    return current.Data;
                }
            }

            public void Dispose() { }
            public void Reset() { current = null; }
        }

    }
}
