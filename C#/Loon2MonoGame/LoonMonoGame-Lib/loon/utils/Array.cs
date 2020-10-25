using loon.events;

namespace loon.utils
{
    public class ArrayNode<T>
    {

        public ArrayNode<T> next;
        public ArrayNode<T> previous;
        public T data;

        public ArrayNode()
        {
            this.next = null;
            this.previous = null;
            this.data = default;
        }
    }

    public class Array<T> : IArray, LRelease
    {

        public static Array<T> At() {
            return new Array<T>();
        }
        public static Array<T> At(Array<T> data) {
            return new Array<T>(data);
        }

        private ArrayNode<T> _items = null;

        private int _length;

        private bool _close;

        private ArrayNode<T> _next_tmp = null, _previous_tmp = null;

        private int _next_count = 0, _previous_count = 0;
        public Array()
        {
            Clear();
        }

        public Array(Array<T> data)
        {
            Clear();
            AddAll(data);
        }

        public Array<T> InsertBetween(Array<T> previous, Array<T> next, Array<T> newNode)
        {
            return InsertBetween(previous._items, next._items, newNode._items);
        }

        public Array<T> InsertBetween(ArrayNode<T> previous, ArrayNode<T> next, ArrayNode<T> newNode)
        {
            if (_close)
            {
                return this;
            }
            if (previous == this._items && next != this._items)
            {
                this.AddFront(newNode.data);
            }
            else if (previous != this._items && next == this._items)
            {
                this.AddBack(newNode.data);
            }
            else
            {
                newNode.next = next;
                newNode.previous = previous;
                previous.next = newNode;
                next.previous = newNode;
            }
            return this;
        }

        public Array<T> Reverse()
        {
            Array<T> tmp = new Array<T>();
            for (int i = Size() - 1; i > -1; --i)
            {
                tmp.Add(Get(i));
            }
            return tmp;
        }

        public Array<T> AddAll(Array<T> data)
        {
            for (; data.HashNext();)
            {
                Add(data.Next());
            }
            return data.StopNext();
        }

        public Array<T> Concat(Array<T> data)
        {
            Array<T> list = new Array<T>();
            list.AddAll(this);
            list.AddAll(data);
            return list;
        }

        public Array<T> Slice(int start)
        {
            return Slice(start, this.Size());
        }

        public Array<T> Slice(int start, int end)
        {
            Array<T> list = new Array<T>();
            for (int i = start; i < end; i++)
            {
                list.Add(Get(i));
            }
            return list;
        }

        public Array<T> Push(T data)
        {
            return Add(data);
        }

        public Array<T> Add(T data)
        {
            ArrayNode<T> newNode = new ArrayNode<T>();
            ArrayNode<T> o = this._items.next;
            newNode.data = data;
            if (o == this._items)
            {
                this.AddFront(data);
            }
            else
            {
                for (; o != this._items;)
                {
                    o = o.next;
                }
                if (o == this._items)
                {
                    this.AddBack(newNode.data);
                }
            }
            return this;
        }

        public Array<T> AddFront(T data)
        {
            if (_close)
            {
                return this;
            }
            ArrayNode<T> newNode = new ArrayNode<T>();
            newNode.data = data;
            newNode.next = this._items.next;
            this._items.next.previous = newNode;
            this._items.next = newNode;
            newNode.previous = this._items;
            _length++;
            return this;
        }

        public Array<T> AddBack(T data)
        {
            if (_close)
            {
                return this;
            }
            ArrayNode<T> newNode = new ArrayNode<T>();
            newNode.data = data;
            newNode.previous = this._items.previous;
            this._items.previous.next = newNode;
            this._items.previous = newNode;
            newNode.next = this._items;
            _length++;
            return this;
        }

        public T Get(int idx)
        {
            if (_close)
            {
                return default;
            }
            int size = _length - 1;
            if (0 <= idx && idx <= size)
            {
                ArrayNode<T> o = this._items.next;
                int count = 0;
                for (; count < idx;)
                {
                    o = o.next;
                    count++;
                }
                return o.data;
            }
            else if (idx == size)
            {
                return _items.data;
            }
            return default;
        }

        public Array<T> Set(int idx, T v)
        {
            if (_close)
            {
                return this;
            }
            int size = _length - 1;

            if (0 <= idx && idx <= size)
            {
                ArrayNode<T> o = this._items.next;
                int count = 0;
                for (; count < idx;)
                {
                    o = o.next;
                    count++;
                }
                o.data = v;
            }
            else if (idx == size)
            {
                _items.data = v;
            }
            else if (idx > size)
            {
                for (int i = size; i < idx; i++)
                {
                    Add(default);
                }
                Set(idx, v);
            }
            return this;
        }

        public ArrayNode<T> Node()
        {
            return _items;
        }

        public bool Contains(T data)
        {
            return Contains(data, false);
        }

        public bool Contains(T data, bool identity)
        {
            if (_close)
            {
                return false;
            }
            ArrayNode<T> o = this._items.next;
            for (; o != this._items;)
            {
                if ((identity || data == null) && (object)o.data == (object)data)
                {
                    return true;
                }
                else if (data.Equals(o.data))
                {
                    return true;
                }
                o = o.next;
            }
            return false;
        }

        public int IndexOf(T data)
        {
            return IndexOf(data, false);
        }

        public int IndexOf(T data, bool identity)
        {
            if (_close)
            {
                return -1;
            }
            int count = 0;
            ArrayNode<T> o = this._items.next;
            for (; o != this._items && count < _length;)
            {
                if ((identity || data == null) && (object)o.data == (object)data)
                {
                    return count;
                }
                else if (data.Equals(o.data))
                {
                    return count;
                }
                o = o.next;
                count++;
            }
            return -1;
        }

        public int LastIndexOf(T data)
        {
            return LastIndexOf(data, false);
        }

        public int LastIndexOf(T data, bool identity)
        {
            if (_close)
            {
                return -1;
            }
            int count = _length - 1;
            ArrayNode<T> o = this._items.previous;
            for (; o != this._items && count > 0;)
            {
                if ((identity || data == null) && (object)o.data == (object)data)
                {
                    return count;
                }
                else if (data.Equals(o.data))
                {
                    return count;
                }
                o = o.previous;
                count--;
            }
            return -1;
        }

        public ArrayNode<T> Find(T data)
        {
            if (_close)
            {
                return null;
            }
            ArrayNode<T> o = this._items.next;
            for (; o != this._items && !data.Equals(o.data);)
            {
                o = o.next;
            }
            if (o == this._items)
            {
                return null;
            }
            return o;
        }

        public T RemoveFirst()
        {
            if (_close)
            {
                return default;
            }
            T result = First();
            Remove(0);
            return result;
        }

        public T RemoveLast()
        {
            if (_close)
            {
                return default;
            }
            T result = Last();
            Remove(_length < 1 ? 0 : _length - 1);
            return result;
        }

        public bool Remove(int idx)
        {
            if (_close)
            {
                return false;
            }
            int size = _length - 1;
            if (0 <= idx && idx <= size)
            {
                ArrayNode<T> o = this._items.next;
                int count = 0;
                for (; count < idx;)
                {
                    o = o.next;
                    count++;
                }
                return Remove(o.data);
            }
            else if (idx == size)
            {
                return Remove(_items.data);
            }
            return false;
        }

        public bool Remove(T data)
        {
            if (_close)
            {
                return false;
            }
            ArrayNode<T> toDelete = this.Find(data);
            if (toDelete != this._items && toDelete != null)
            {
                toDelete.previous.next = toDelete.next;
                toDelete.next.previous = toDelete.previous;
                _length--;
                return true;
            }
            return false;
        }

        public bool Remove()
        {
            int tsSize = _length;
            return Remove(--tsSize);
        }

        public T Pop()
        {
            T o = default;
            if (!IsEmpty())
            {
                o = this._items.previous.data;
                Remove(o);
            }
            return o;
        }

        public T PreviousPop()
        {
            T o = default;
            o = this._items.previous.data;
            Remove(o);
            return this._items.previous.data;
        }

        public bool IsFirst(Array<T> o)
        {
            if (o._items.previous == this._items)
            {
                return true;
            }
            return false;
        }

        public bool IsLast(Array<T> o)
        {
            if (o._items.next == this._items)
            {
                return true;
            }
            return false;
        }

        public T Random()
        {
            if (_length == 0)
            {
                return default;
            }
            return Get(MathUtils.Random(0, _length - 1));
        }

        public Array<T> RandomArrays()
        {
            if (_length == 0)
            {
                return new Array<T>();
            }
            T v = default;
            Array<T> newArrays = new Array<T>();
            for (; HashNext();)
            {
                newArrays.Add(Next());
            }
            StopNext();
            for (int i = 0; i < _length; i++)
            {
                v = Random();
                for (int j = 0; j < i; j++)
                {
                    if ((object)newArrays.Get(j) == (object)v)
                    {
                        v = Random();
                        j = -1;
                    }

                }
                newArrays.Set(i, v);
            }
            return newArrays;
        }


        public override string ToString()
        {
            return ToString(',');
        }

        public T Next()
        {
            if (IsEmpty())
            {
                return default;
            }
            if (_next_count == 0)
            {
                _next_tmp = this._items.next;
                _next_count++;
                return _next_tmp.data;
            }
            if (_next_tmp != this._items && _next_count < _length)
            {
                _next_tmp = _next_tmp.next;
                _next_count++;
                return _next_tmp.data;
            }
            else
            {
                StopNext();
                return default;
            }
        }

        public bool HashNext()
        {
            return _next_count < _length;
        }

        public int IdxNext()
        {
            return _next_count;
        }

        public Array<T> StopNext()
        {
            _next_tmp = null;
            _next_count = 0;
            return this;
        }

        public T Previous()
        {
            if (IsEmpty())
            {
                return default;
            }
            if (_previous_count == 0)
            {
                _previous_tmp = this._items.previous;
                _previous_count++;
                return _previous_tmp.data;
            }
            if (_previous_tmp != this._items && _previous_count < _length)
            {
                _previous_tmp = _previous_tmp.previous;
                _previous_count++;
                return _previous_tmp.data;
            }
            else
            {
                StopPrevious();
                return default;
            }
        }

        public int IdxPrevious()
        {
            return _previous_count;
        }

        public Array<T> StopPrevious()
        {
            _previous_tmp = null;
            _previous_count = 0;
            return this;
        }

        public string ToString(char split)
        {

            return "[]";

        }



        public override bool Equals(object o)
        {
            if (o == (object)this)
            {
                return true;
            }
            Array<object> array = (Array<object>)o;
            int n = _length;
            if (n != array._length)
            {
                return false;
            }
            ArrayNode<T> items1 = this._items;
            ArrayNode<object> items2 = array._items;
            for (int i = 0; i < n; i++)
            {
                object o1 = items1.next;
                object o2 = items2.next;
                if (!((o1 == null) ? o2 == null : o1.Equals(o2)))
                {
                    return false;
                }
            }
            return true;
        }

        public T First()
        {
            if (this.IsEmpty())
            {
                return default;
            }
            else
            {
                return this._items.next.data;
            }
        }

        public T Last()
        {
            if (this.IsEmpty())
            {
                return default;
            }
            else
            {
                return this._items.previous.data;
            }
        }

        public T Peek()
        {
            return Last();
        }


        public void Clear()
        {
            this._close = false;
            this._length = 0;
            this.StopNext();
            this.StopPrevious();
            this._items = null;
            this._items = new ArrayNode<T>();
            this._items.next = this._items;
            this._items.previous = this._items;
        }


        public int Size()
        {
            return _length;
        }


        public bool IsEmpty()
        {
            return _close || _length == 0 || this._items.next == this._items;
        }

        public bool IsClosed()
        {
            return _close;
        }

        public Array<T> Cpy()
        {
            Array<T> newlist = new Array<T>();
            newlist.AddAll(this);
            return newlist;
        }

        public Array<T> Where(QueryEvent<T> test)
        {
            Array<T> list = new Array<T>();
            for (; HashNext();)
            {
                T t = Next();
                if (test.Hit(t))
                {
                    list.Add(t);
                }
            }
            StopNext();
            return list;
        }

        public T Find(QueryEvent<T> test)
        {
            for (; HashNext();)
            {
                T t = Next();
                if (test.Hit(t))
                {
                    StopNext();
                    return t;
                }
            }
            StopNext();
            return default;
        }

        public bool Remove(QueryEvent<T> test)
        {
            for (; HashNext();)
            {
                T t = Next();
                if (test.Hit(t))
                {
                    StopNext();
                    return Remove(t);
                }
            }
            StopNext();
            return false;
        }

        public void Dispose()
        {
            _close = true;
            _length = 0;
            _items = null;
        }


        public override int GetHashCode()
        {
            int hashCode = 1;
            for (; HashNext();)
            {
                object obj = Next();
                hashCode = 31 * hashCode + (obj == null ? 0 : obj.GetHashCode());
            }
            StopNext();
            return hashCode;
        }


        public void Close()
        {
            Dispose();
        }

    }

}
