/// <summary>
/// Copyright 2008 - 2012
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
/// @project loon
/// @email javachenpeng@yahoo.com
namespace Loon.Utils.Collection {

	
	public class ArrayList {
	
		private object[] _items;
		private bool _full;
		private int _size;
	
		public ArrayList():this(CollectionUtils.INITIAL_CAPACITY) {
			
		}
	
		public ArrayList(int length) {
            this._items = new object[length + (length / 2)];
			this._full = false;
            this._size = length;
		}
	
		public void AddAll(ArrayList array) {
			AddAll(array, 0, array._size);
		}
	
		public void AddAll(ArrayList array, int offset, int length) {
			if (offset + length > array._size) {
				throw new System.ArgumentException(
						"offset + length must be <= size: " + offset + " + "
								+ length + " <= " + array._size);
			}
			AddAll(array._items, offset, length);
		}
	
		public void AddAll(object[] array, int offset, int length) {
			object[] items = this._items;
			int sizeNeeded = _size + length;
			if (sizeNeeded > items.Length) {
				items = ExpandCapacity(MathUtils.Max(8, (_size + 1) * 2));
			}
			System.Array.Copy(array,offset,items,_size,length);
			_size += length;
		}
	
		public void Add(int index, object element) {
			if (index >= this._items.Length) {
				object[] items = this._items;
				if (_size == items.Length) {
					items = ExpandCapacity(MathUtils.Max(8, (_size + 1) * 2));
				}
			} else {
				this._items[index] = element;
			}
			this._size++;
		}
	
		public void Add(object element) {
			if (this._full) {
				object[] items = this._items;
				if (_size == items.Length) {
					items = ExpandCapacity(MathUtils.Max(8, (_size + 1) * 2));
				}
				items[_size] = element;
			} else {
				int size = this._items.Length;
				for (int i = 0; i < size; i++) {
					if (this._items[i] == null) {
						this._items[i] = element;
						if (i == size - 1) {
							this._full = true;
						}
						break;
					}
	
				}
			}
			this._size++;
		}
	
		private object[] ExpandCapacity(int newSize) {
			object[] items = this._items;
			object[] obj = (object[]) System.Array.CreateInstance(items
							.GetType().GetElementType(),newSize);
			System.Array.Copy((items),0,(obj),0,MathUtils.Min(_size, obj.Length));
			this._items = obj;
			return obj;
		}
	
		public virtual object Clone() {
			return this;
		}
	
		public bool Contains(object elem) {
			for (int i = 0; i < this._items.Length; i++) {
				if (this._items[i].Equals(elem)) {
					return true;
				}
			}
			return false;
		}
	
		public object Set(int index, object value_ren) {
			if (index >= _size) {
				throw new System.IndexOutOfRangeException(index.ToString());
			}
			object old = this._items[index];
			_items[index] = value_ren;
			return old;
		}
	
		public object Get(int index) {
			if (index >= _size) {
				throw new System.IndexOutOfRangeException(index.ToString());
			}
			return this._items[index];
		}

        public override int GetHashCode()
        {
            return base.GetHashCode() + _size;
        }
	
		public void Swap(int first, int second) {
			if (first >= _size) {
				throw new System.IndexOutOfRangeException(first.ToString().ToString());
			}
			if (second >= _size) {
				throw new System.IndexOutOfRangeException(second.ToString().ToString());
			}
			object[] items = this._items;
			object firstValue = items[first];
			items[first] = items[second];
			items[second] = firstValue;
		}
	
		public int IndexOf(object elem) {
			for (int i = 0; i < this._items.Length; i++) {
				if (this._items[i].Equals(elem)) {
					return i;
				}
			}
			return -1;
		}

        public int IndexOfIdenticalObject(object elem)
        {
            for (int i = 0; i < this._items.Length; i++)
            {
                if (this._items[i] == elem)
                {
                    return i;
                }
            }
            return -1;
        }

		public bool IsEmpty() {
			if (this._size == 0) {
				return true;
			} else {
				return false;
			}
		}
	
		public int LastIndexOf(object elem) {
			for (int i = this._items.Length - 1; i >= 0; i--) {
				if (this._items[i].Equals(elem)) {
					return i;
				}
			}
			return -1;
		}

        public bool Remove(object value)
        {
            return Remove(value, false);
        }

        public bool Remove(object value, bool identity)
        {
            object[] items = this._items;
            if (identity || value == null)
            {
                for (int i = 0; i < _size; i++)
                {
                    if (items[i] == value)
                    {
                        Remove(i);
                        return true;
                    }
                }
            }
            else
            {
                for (int i = 0; i < _size; i++)
                {
                    if (value.Equals(items[i]))
                    {
                        Remove(i);
                        return true;
                    }
                }
            }
            return false;
        }

		public object Remove(int index) {
			if (index >= _size) {
				throw new System.IndexOutOfRangeException(index.ToString());
			}
			object[] items = this._items;
			object elem = items[index];
			_size--;
			System.Array.Copy((_items),index + 1,(_items),index,_size - index);
			items[_size] = null;
			return elem;
		}
	
		public void RemoveRange(int fromIndex, int toIndex) {
			for (int i = fromIndex; i <= toIndex; i++) {
				this.Remove(fromIndex);
			}
		}
	
		public override bool Equals(object obj0) {
			if (obj0 == (object) this) {
				return true;
			}
			if (!(obj0   is  ArrayList)) {
				return false;
			}
			ArrayList array = (ArrayList) obj0;
			int n = _size;
			if (n != array._size) {
				return false;
			}
			object[] items1 = this._items;
			object[] items2 = array._items;
			for (int i = 0; i < n; i++) {
				object o1 = items1[i];
				object o2 = items2[i];
				if (!((o1 == null) ? o2 == null : o1.Equals(o2))) {
					return false;
				}
			}
			return true;
		}
	
		public object Last() {
			return _items[(_size < 1) ? 0 : _size - 1];
		}
	
		public object First() {
			return _items[0];
		}
	
		public object Pop() {
			--_size;
			object item = _items[_size];
			_items[_size] = null;
			return item;
		}
	
		public object Random() {
			if (_size == 0) {
				return null;
			}
			return _items[MathUtils.Random(0, _size - 1)];
		}
	
		public void Clear() {
			object[] items = this._items;
            for (int i = 0; i < items.Length; i++)
            {
                items[i] = null;
            }
			_size = 0;
		}
	
		public int Size() {
			return this._size;
		}
	
		public object[] ToArray() {
			object[] result = (object[]) System.Array.CreateInstance(_items
							.GetType().GetElementType(),_size);
			System.Array.Copy((_items),0,(result),0,_size);
			return result;
		}
	
		public override string ToString() {
			return ToString(',');
		}

        public string ToString(char split)
        {
			if (_size == 0) {
				return "[]";
			}
			object[] items = this._items;
			System.Text.StringBuilder buffer = new System.Text.StringBuilder(
					CollectionUtils.INITIAL_CAPACITY);
			buffer.Append('[');
			buffer.Append(items[0]);
			for (int i = 1; i < _size; i++) {
				buffer.Append(split);
				buffer.Append(items[i]);
			}
			buffer.Append(']');
			return buffer.ToString();
		}
	
		/*
		 * public static void main(String[] args) { ArrayList list = new
		 * ArrayList(); list.add("A"); list.add("B"); list.add("C"); list.add("D");
		 * list.add("E"); list.add("F"); list.set(0, "Z"); list.pop();
		 * list.remove(0); System.out.println(list); }
		 */
	
	}
}
