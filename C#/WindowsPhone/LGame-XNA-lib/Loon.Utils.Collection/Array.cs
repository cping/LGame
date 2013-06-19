namespace Loon.Utils.Collection
{
    using Loon.Core;

    public class ArrayNode<T>
    {

        public ArrayNode<T> next;
        public ArrayNode<T> previous;
        public T data;

        public ArrayNode()
        {
            this.next = null;
            this.previous = null;
            this.data = default(T);
        }
    }

    public class Array<T> : LRelease {

        private ArrayNode<T> _items = null ;

        private int _length = 0;

        private bool _close = false;

        private ArrayNode<T> _next_tmp = null, _previous_tmp = null;

        private int _next_count = 0, _previous_count = 0;

        public Array()
        {
            Clear();
        }

        public void InsertBetween(Array<T> previous, Array<T> next, Array<T> newNode)
        {
            InsertBetween(previous._items, next._items, newNode._items);
        }

        public void InsertBetween(ArrayNode<T> previous, ArrayNode<T> next,
                ArrayNode<T> newNode)
        {
            if (_close)
            {
                return;
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
        }

        public void Add(T data)
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
                for (; o != this._items; )
                {
                    o = o.next;
                }
                if (o == this._items)
                {
                    this.AddBack(newNode.data);
                }
            }
        }

        public void AddFront(T data)
        {
            if (_close)
            {
                return;
            }
            ArrayNode<T> newNode = new ArrayNode<T>();
            newNode.data = data;
            newNode.next = this._items.next;
            this._items.next.previous = newNode;
            this._items.next = newNode;
            newNode.previous = this._items;
            _length++;
        }

        public void AddBack(T data)
        {
            if (_close)
            {
                return;
            }
            ArrayNode<T> newNode = new ArrayNode<T>();
            newNode.data = data;
            newNode.previous = this._items.previous;
            this._items.previous.next = newNode;
            this._items.previous = newNode;
            newNode.next = this._items;
            _length++;
        }

        public T Get(int idx)
        {
            if (_close)
            {
                return default(T);
            }
            int size = _length - 1;
            if (0 <= idx && idx <= size)
            {
                ArrayNode<T> o = this._items.next;
                int count = 0;
                for (; count < idx; )
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
            return default(T);
        }

        public void Set(int idx, T v)
        {
            if (_close)
            {
                return;
            }
            int size = _length - 1;
            if (0 <= idx && idx <= size)
            {
                ArrayNode<T> o = this._items.next;
                int count = 0;
                for (; count < idx; )
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
            for (; o != this._items; )
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
            for (; o != this._items && count < _length; )
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
            for (; o != this._items && count > 0; )
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
            for (; o != this._items && !data.Equals(o.data); )
            {
                o = o.next;
            }
            if (o == this._items)
            {
                return null;
            }
            return o;
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
                for (; count < idx; )
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

        public T Pop()
        {
            T o = default(T);
            if (!IsEmpty())
            {
                o = this._items.previous.data;
                Remove(o);
            }
            return o;
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
                return default(T);
            }
            return Get(MathUtils.Random(0, _length - 1));
        }

        public override string ToString()
        {
            return ToString(',');
        }

        public T Next()
        {
            if (IsEmpty())
            {
                return default(T);
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
                return default(T);
            }
        }

        public int IdxNext()
        {
            return _next_count;
        }

        public void StopNext()
        {
            _next_tmp = null;
            _next_count = 0;
        }

        public T Previous()
        {
            if (IsEmpty())
            {
                return default(T);
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
                return default(T);
            }
        }

        public int IdxPrevious()
        {
            return _previous_count;
        }

        public void StopPrevious()
        {
            _previous_tmp = null;
            _previous_count = 0;
        }

        public string ToString(char split)
        {
            if (IsEmpty())
            {
                return "[]";
            }
            ArrayNode<T> o = this._items.next;
            System.Text.StringBuilder buffer = new System.Text.StringBuilder(
                    CollectionUtils.INITIAL_CAPACITY);
            buffer.Append('[');
            int count = 0;
            for (; o != this._items; )
            {
                buffer.Append(o.data);
                if (count != _length - 1)
                {
                    buffer.Append(split);
                }
                o = o.next;
                count++;
            }
            buffer.Append(']');
            return buffer.ToString();
        }

        public override bool Equals(object o) {
			if (o == (object) this) {
				return true;
			}
			Array<object> array = (Array<object>) o;
			int n = _length;
			if (n != array._length) {
				return false;
			}
			ArrayNode<T> items1 = this._items;
            ArrayNode<object> items2 = array._items;
			for (int i = 0; i < n; i++) {
				object o1 = items1.next;
                object o2 = items2.next;
				if (!((o1 == null) ? o2 == null : o1.Equals(o2))) {
					return false;
				}
			}
			return true;
		}

        public T First()
        {
            if (this.IsEmpty())
            {
                return default(T);
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
                return default(T);
            }
            else
            {
                return this._items.previous.data;
            }
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

        public bool IsClose()
        {
            return _close;
        }

        public Array<T> Copy()
        {
            Array<T> newlist = new Array<T>();
            newlist._items.next = this._items;
            newlist._items.previous = this._items;
            return newlist;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode() + _length;
        }

        public void Dispose()
        {
            _close = true;
            _length = 0;
            _items = null;
        }

        /*
        public static void main(String[] args) {
            Array<String> s = new Array<String>();
            s.add("A");
            s.add("B");
            s.add("C");
            s.add("X");
	
            System.out.println(s.first());
            System.out.println(s.last());
	
            s.set(0, "D");
	
            System.out.println(s.contains("A"));
            System.out.println(s.contains("D"));
            System.out.println(s.indexOf("B"));
            System.out.println(s.indexOf("Z"));
            System.out.println("last:" + s.lastIndexOf("C"));
            System.out.println(s.remove("X"));
	
            for (; s.idxNext() < s.size();) {
                String t = s.next();
                System.out.println("1:" + t);
            }
            s.stopNext();
            for (;;) {
                String t = s.previous();
                if (t != null) {
                    System.out.println("2:" + t);
                } else {
                    break;
                }
            }
            for (;;) {
                String t = s.next();
                if (t != null) {
                    System.out.println("3:" + t);
                } else {
                    break;
                }
            }
            for (;;) {
                String t = s.next();
                if (t != null) {
                    System.out.println("4:" + t);
                } else {
                    break;
                }
            }
	
            s.clear();
            for (;;) {
                String t = s.next();
                if (t != null) {
                    System.out.println("5:" + t);
                } else {
                    break;
                }
            }
        }*/
	
    }
}
