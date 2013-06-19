using System;
namespace Loon.Utils.Collection {
	

	public class IntHashMap {
	
		internal Entry[] valueTables;
	
		internal int _size;
	
		internal int _threshold;
	
		internal int _modCount;
	
		internal float _loadFactor;
	
		public IntHashMap(int initialCapacity, float l) {
			if (initialCapacity < 0) {
				throw new ArgumentException("Illegal initial capacity: "
						+ initialCapacity);
			}
			if (initialCapacity > 1 << 30) {
				initialCapacity = 1 << 30;
			}
			if (l <= 0 || Single.IsNaN(l)) {
				throw new ArgumentException("Illegal load factor: "
						+ l);
			}
			int capacity = 1;
			while (capacity < initialCapacity) {
				capacity <<= 1;
			}
	
			this._loadFactor = l;
			_threshold = (int) (capacity * l);
			valueTables = new Entry[capacity];
			Reset();
		}
	
		public IntHashMap() {
			_loadFactor = 0.75f;
			_threshold = (int) (CollectionUtils.INITIAL_CAPACITY * 0.75f);
			valueTables = new Entry[CollectionUtils.INITIAL_CAPACITY];
			Reset();
		}
	
		protected internal void Reset() {
		}
	
		static internal int IndexFor(int h, int length) {
			return h & (length - 1);
		}
	
		public int Size() {
			return _size;
		}
	
		public bool IsEmpty() {
			return _size == 0;
		}
	
		public object Get(int key) {
			int i = IndexFor(key, valueTables.Length);
			Entry e = valueTables[i];
			while (true) {
				if (e == null) {
					return null;
				}
				if (key == e.key) {
					return e.value_ren;
				}
				e = e.next;
			}
		}
	
		public bool ContainsKey(int key) {
			int i = IndexFor(key, valueTables.Length);
			Entry e = valueTables[i];
			while (e != null) {
				if (key == e.key) {
					return true;
				}
				e = e.next;
			}
			return false;
		}
	
		public Entry GetEntry(int key) {
			int i = IndexFor(key, valueTables.Length);
			Entry e = valueTables[i];
			while (e != null && !(key == e.key)) {
				e = e.next;
			}
			return e;
		}
	
		public object Put(int key, object value_ren) {
			int i = IndexFor(key, valueTables.Length);
			for (Entry e = valueTables[i]; e != null; e = e.next) {
				if (key == e.key) {
					object oldValue = e.value_ren;
					e.value_ren = value_ren;
					return oldValue;
				}
			}
			_modCount++;
			AddEntry(key, value_ren, i);
			return null;
		}
	
		private void PutForCreate(int key, object value_ren) {
			int i = IndexFor(key, valueTables.Length);
			for (Entry e = valueTables[i]; e != null; e = e.next) {
				if (key == e.key) {
					e.value_ren = value_ren;
					return;
				}
			}
	
			CreateEntry(key, value_ren, i);
		}
	
		internal void PutAllForCreate(IntHashMap m) {
			for (int i = 0; i < _size; i++) {
				Entry e = valueTables[i];
				PutForCreate(e.GetKey(), e.GetValue());
			}
		}
	
		internal void Resize(int newCapacity) {
			Entry[] oldTable = valueTables;
			int oldCapacity = oldTable.Length;
			if (oldCapacity == 1 << 30) {
				_threshold = Int32.MaxValue;
				return;
			}
			Entry[] newTable = new Entry[newCapacity];
			Transfer(newTable);
			valueTables = newTable;
			_threshold = (int) (newCapacity * _loadFactor);
		}
	
		internal void Transfer(Entry[] newTable) {
			Entry[] src = valueTables;
			int newCapacity = newTable.Length;
			for (int j = 0; j < src.Length; j++) {
				Entry e = src[j];
				if (e != null) {
					src[j] = null;
					do {
						Entry next = e.next;
						int i = IndexFor(e.key, newCapacity);
						e.next = newTable[i];
						newTable[i] = e;
						e = next;
					} while (e != null);
				}
			}
		}
	
		public void PutAll(IntHashMap m) {
			int numKeysToBeAdded = m._size;
			if (numKeysToBeAdded == 0) {
				return;
			}
			if (numKeysToBeAdded > _threshold) {
				int targetCapacity = (int) (numKeysToBeAdded / _loadFactor + 1);
				if (targetCapacity > 1 << 30) {
					targetCapacity = 1 << 30;
				}
				int newCapacity = valueTables.Length;
				while (newCapacity < targetCapacity) {
					newCapacity <<= 1;
				}
				if (newCapacity > valueTables.Length) {
					Resize(newCapacity);
				}
			}
			for (int i = 0; i < _size; i++) {
				Entry e = valueTables[i];
				Put(e.GetKey(), e.GetValue());
			}
		}
	
		public Entry[] ToEntrys() {
			Entry[] lists = (Entry[]) CollectionUtils.CopyOf(valueTables, _size);
			return lists;
		}
	
		public object Remove(int key) {
			Entry e = RemoveEntryForKey(key);
			return ((e == null) ? null : e.value_ren);
		}
	
		internal Entry RemoveEntryForKey(int key) {
			int i = IndexFor(key, valueTables.Length);
			Entry prev = valueTables[i];
			Entry e = prev;
	
			while (e != null) {
				Entry next = e.next;
				if (key == e.key) {
					_modCount++;
					_size--;
					if (prev == e) {
						valueTables[i] = next;
					} else {
						prev.next = next;
					}
					return e;
				}
				prev = e;
				e = next;
			}
	
			return e;
		}
	
		internal Entry RemoveMapping(object o) {
			if (!(o  is  Entry)) {
				return null;
			}
	
			Entry entry = (Entry) o;
			int key = entry.GetKey();
			int i = IndexFor(key, valueTables.Length);
			Entry prev = valueTables[i];
			Entry e = prev;
	
			while (e != null) {
				Entry next = e.next;
				if (e.key == key && e.Equals(entry)) {
					_modCount++;
					_size--;
					if (prev == e) {
						valueTables[i] = next;
					} else {
						prev.next = next;
					}
					return e;
				}
				prev = e;
				e = next;
			}
	
			return e;
		}
	
		public void Clear() {
			_modCount++;
			Entry[] tab = valueTables;
			for (int i = 0; i < tab.Length; i++) {
				tab[i] = null;
			}
			_size = 0;
		}
	
		public bool ContainsValue(object value_ren) {
			if (value_ren == null) {
				return ContainsNullValue();
			}
	
			Entry[] tab = valueTables;
			for (int i = 0; i < tab.Length; i++) {
				for (Entry e = tab[i]; e != null; e = e.next) {
					if (value_ren.Equals(e.value_ren)) {
						return true;
					}
				}
			}
			return false;
		}
	
		private bool ContainsNullValue() {
			Entry[] tab = valueTables;
			for (int i = 0; i < tab.Length; i++) {
				for (Entry e = tab[i]; e != null; e = e.next) {
					if (e.value_ren == null) {
						return true;
					}
				}
			}
			return false;
		}
	
		public virtual IntHashMap Clone() {
			IntHashMap result = null;
			try {
				result = (IntHashMap) base.MemberwiseClone();
				result.valueTables = new Entry[valueTables.Length];
				result._modCount = 0;
				result._size = 0;
				result.Reset();
				result.PutAllForCreate(this);
			} catch (Exception) {
			}
			return result;
		}
	
		public class Entry {
	
			internal readonly int key;
			internal object value_ren;
			internal Entry next;
	
			internal Entry(int k, object v, Entry n) {
				value_ren = v;
				next = n;
				key = k;
			}
	
			public int GetKey() {
				return key;
			}
	
			public object GetValue() {
				return value_ren;
			}
	
			public object SetValue(object newValue) {
				object oldValue = value_ren;
				value_ren = newValue;
				return oldValue;
			}
	
			public override bool Equals(object o) {
				if (!(o  is  Entry)) {
					return false;
				}
				Entry e = (Entry) o;
				int k1 = GetKey();
				int k2 = e.GetKey();
				if (k1 == k2) {
					object v1 = GetValue();
					object v2 = e.GetValue();
					if (v1 == v2 || (v1 != null && v1.Equals(v2))) {
						return true;
					}
				}
				return false;
			}
	
			public override int GetHashCode() {
				return key ^ ((value_ren == null) ? 0 : value_ren.GetHashCode());
			}
	
			public override String ToString() {
				return GetKey() + "=" + GetValue();
			}
		}
	
		internal void AddEntry(int key_0, object value_ren, int bucketIndex) {
			valueTables[bucketIndex] = new Entry(key_0, value_ren,
					valueTables[bucketIndex]);
			if (_size++ >= _threshold) {
				Resize(2 * valueTables.Length);
			}
		}
	
		internal void CreateEntry(int key_0, object value_ren, int bucketIndex) {
			valueTables[bucketIndex] = new Entry(key_0, value_ren,
					valueTables[bucketIndex]);
			_size++;
		}
	
		public int Capacity() {
			return valueTables.Length;
		}
	
		public float LoadFactor() {
			return _loadFactor;
		}

	}
}
