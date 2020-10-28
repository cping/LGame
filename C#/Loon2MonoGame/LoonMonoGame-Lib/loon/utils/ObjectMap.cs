using java.lang;
using java.util;
using java.util.function;
using System.Collections;
using System.Collections.Generic;

namespace loon.utils
{
    public class ObjectMap<K, V> : Iterable<ObjectMap<K, V>.Entry<K, V>>, IEnumerable<ObjectMap<K, V>.Entry<K, V>>, IArray
    {
#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Values<V> : Iterable<V>, LIterator<V>
        {
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名

            public bool _valid;
            internal bool _simpleOrder;
            internal int _nextIndex;
            internal int _lastIndex;
            internal int _expectedModCount;
            internal ObjectMap<K, V> _map;

            internal Values(ObjectMap<K, V> map)
            {
                this._map = map;
            }

            public void Reset()
            {
                this._simpleOrder = !(_map is OrderedMap<K, V>);
                this._nextIndex = _map.IterateFirst();
                this._lastIndex = NO_INDEX;
                this._expectedModCount = _map.modCount;
            }
            public bool HasNext()
            {
                if (!_valid)
                {
                    return false;
                }
                return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
            }

            public V Next()
            {
                if (!_valid)
                {
                    return default;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return default;
                }
                if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex)
                {
                    return default;
                }
                _lastIndex = _nextIndex;
                if (_simpleOrder)
                {
                    do
                    {
                        _nextIndex++;
                    } while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
                            && _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
                }
                else
                {
                    _nextIndex = _map.IterateNext(_nextIndex);
                }
                if (_lastIndex == NULL_INDEX)
                {
                    return default;
                }
                return ((V)(_map.keyValueTable[(_lastIndex << 1) + 2]));
            }

            public void Remove()
            {
                if (!_valid)
                {
                    return;
                }
                if (_lastIndex == NO_INDEX)
                {
                    return;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return;
                }
                _map.RemoveKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
                        _lastIndex);
                _lastIndex = NO_INDEX;
                _expectedModCount = _map.modCount;
            }

            public Values<V> Iterator()
            {
                return this;
            }

            Iterator<V> Iterable<V>.Iterator()
            {
                return this;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<V>.ForEach(this, consumer);
            }
        }

        private Values<V> values1, values2;

        public Values<V> VALUES()
        {
            return GetValues();
        }

        public Values<V> GetValues()
        {
            if (values1 == null)
            {
                values1 = new Values<V>(this);
                values2 = new Values<V>(this);
            }
            if (!values1._valid)
            {
                values1.Reset();
                values1._valid = true;
                values2._valid = false;
                return values1;
            }
            values2.Reset();
            values2._valid = true;
            values1._valid = false;
            return values2;
        }


#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Keys<K> : Iterable<K>, LIterator<K>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {


            public bool _valid;
            internal bool _simpleOrder;
            internal int _nextIndex;
            internal int _lastIndex;
            internal int _expectedModCount;
            internal ObjectMap<K, V> _map;

            internal Keys(ObjectMap<K, V> map)
            {
                this._map = map;
            }

            public void Reset()
            {
                this._simpleOrder = !(_map is OrderedMap<K, V>);
                this._nextIndex = _map.IterateFirst();
                this._lastIndex = NO_INDEX;
                this._expectedModCount = _map.modCount;
            }
            public bool HasNext()
            {
                if (!_valid)
                {
                    return false;
                }
                return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
            }

            public K Next()
            {
                if (!_valid)
                {
                    return default;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return default;
                }
                if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex)
                {
                    return default;
                }
                _lastIndex = _nextIndex;
                if (_simpleOrder)
                {
                    do
                    {
                        _nextIndex++;
                    } while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
                            && _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
                }
                else
                {
                    _nextIndex = _map.IterateNext(_nextIndex);
                }

                return _lastIndex == NULL_INDEX ? default : (K)_map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1];

            }
            public void Remove()
            {
                if (!_valid)
                {
                    return;
                }
                if (_lastIndex == NO_INDEX)
                {
                    return;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return;
                }
                _map.RemoveKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
                        _lastIndex);
                _lastIndex = NO_INDEX;
                _expectedModCount = _map.modCount;
            }

            public Keys<K> Iterator()
            {
                return this;
            }

            Iterator<K> Iterable<K>.Iterator()
            {
                return this;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<K>.ForEach(this, consumer);
            }
        }

        private Keys<K> keys1, keys2;

        public Keys<K> KEYS()
        {
            return GetKeys();
        }
            
        public Keys<K> GetKeys()
        {
            if (keys1 == null)
            {
                keys1 = new Keys<K>(this);
                keys2 = new Keys<K>(this);
            }
            if (!keys1._valid)
            {
                keys1.Reset();
                keys1._valid = true;
                keys2._valid = false;
                return keys1;
            }
            keys2.Reset();
            keys2._valid = true;
            keys1._valid = false;
            return keys2;
        }


#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Entries<K, V> : Iterable<ObjectMap<K, V>.Entry<K, V>>, LIterator<Entry<K, V>>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {


            public bool _valid;
            internal bool _simpleOrder;
            internal int _nextIndex;
            internal int _lastIndex;
            int _expectedModCount;
            internal ObjectMap<K, V> _map;

            public Entries(ObjectMap<K, V> map)
            {
                this._map = map;
            }

            public void Reset()
            {
                this._simpleOrder = !(_map is OrderedMap<K, V>);
                this._nextIndex = _map.IterateFirst();
                this._lastIndex = NO_INDEX;
                this._expectedModCount = _map.modCount;
            }

            public bool HasNext()
            {
                if (!_valid)
                {
                    return false;
                }
                return _nextIndex != NO_INDEX && _nextIndex < _map.firstUnusedIndex;
            }

            public Entry<K, V> Next()
            {
                if (!_valid)
                {
                    return null;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return null;
                }
                if (_nextIndex == NO_INDEX || _nextIndex >= _map.firstUnusedIndex)
                {
                    return null;
                }
                _lastIndex = _nextIndex;
                if (_simpleOrder)
                {
                    do
                    {
                        _nextIndex++;
                    } while (_map.firstDeletedIndex >= 0 && _nextIndex < _map.firstUnusedIndex
                            && _map.keyValueTable[(_nextIndex << _map.keyIndexShift) + 1] == null);
                }
                else
                {
                    _nextIndex = _map.IterateNext(_nextIndex);
                }
                return new Entry<K, V>(_lastIndex, _map);
            }

            public void Remove()
            {
                if (!_valid)
                {
                    return;
                }
                if (_lastIndex == NO_INDEX)
                {
                    return;
                }
                if (_map.modCount != _expectedModCount)
                {
                    return;
                }
                _map.RemoveKey(_lastIndex == NULL_INDEX ? null : _map.keyValueTable[(_lastIndex << _map.keyIndexShift) + 1],
                        _lastIndex);
                _lastIndex = NO_INDEX;
                _expectedModCount = _map.modCount;
            }

            public Entries<K, V> Iterator()
            {
                return this;
            }

            public void ForEach(Consumer consumer)
            {
                Iterable_Java<ObjectMap<K, V>.Entry<K, V>>.ForEach(this, consumer);
            }


            Iterator<ObjectMap<K, V>.Entry<K, V>> Iterable<ObjectMap<K, V>.Entry<K, V>>.Iterator()
            {
                return (Iterator<ObjectMap<K, V>.Entry<K, V>>)this;
            }

        }

        private Entries<K, V> entries1, entries2;

        public Entries<K, V> Iterator()
        {
            return GetEntries();
        }

        public Entries<K, V> GetEntries()
        {
            if (entries1 == null)
            {
                entries1 = new Entries<K, V>(this);
                entries2 = new Entries<K, V>(this);
            }
            if (!entries1._valid)
            {
                entries1.Reset();
                entries1._valid = true;
                entries2._valid = false;
                return entries1;
            }
            entries2.Reset();
            entries2._valid = true;
            entries1._valid = false;
            return entries2;
        }

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Entry<K, V>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {
            internal readonly int index;
            public K key;
            public V value;
            internal readonly ObjectMap<K, V> map;
            internal Entry(int index, ObjectMap<K, V> map)
            {
                this.map = map;
                this.index = index;
                this.key = index == NULL_INDEX ? default : (K)map.keyValueTable[(index << map.keyIndexShift) + 1];
                this.value = (V)(map.keyIndexShift == 0 ? FINAL_VALUE
                        : map.keyValueTable[(index << map.keyIndexShift) + 2]);
            }

            public K GetKey()
            {
                return key;
            }

            public V GetValue()
            {
                if (index == NULL_INDEX ? map.nullKeyPresent : (object)map.keyValueTable[(index << 1) + 1] == (object)key)
                {
                    value = (V)map.keyValueTable[(index << 1) + 2];
                }
                return value;
            }

            public V SetValue(V newValue)
            {
                V oldValue;
                if (index == NULL_INDEX ? map.nullKeyPresent : (object)map.keyValueTable[(index << 1) + 1] == (object)key)
                {
                    oldValue = (V)map.keyValueTable[(index << 1) + 2];
                    map.keyValueTable[(index << 1) + 2] = value = newValue;
                    return oldValue;
                }
                oldValue = value;
                value = newValue;
                return oldValue;
            }

            public override bool Equals(object o)
            {
                if (!(o is Entry<K, V>))
                {
                    return false;
                }
                Entry<K, V> that = (Entry<K, V>)o;
                K key2 = that.GetKey();
                if ((object)key == (object)key2 || (key != null && key.Equals(key2)))
                {
                    V value2 = that.GetValue();
                    return (object)GetValue() == (object)value2 || (value != null && value.Equals(value2));
                }
                return false;
            }


            public override int GetHashCode()
            {
                return (key == null ? 0 : key.GetHashCode()) ^ (GetValue() == null ? 0 : value.GetHashCode());
            }


            public override string ToString()
            {
                return key + "=" + GetValue();
            }
        }

        private const uint MAP_EMPTY = 0;

        private const uint MAP_BITS = 0xC0000000;

        private const uint MAP_NEXT = 0x40000000;

        private const uint MAP_OVERFLOW = 0x80000000;

        private const uint MAP_END = 0xC0000000;

        private const uint AVAILABLE_BITS = 0x3FFFFFFF;

        private readonly object EMPTY_OBJECT = new object();

        internal readonly static object FINAL_VALUE = new object();

        internal readonly static int NULL_INDEX = -1;

        internal readonly static int NO_INDEX = -2;

        private bool nullKeyPresent;

        public int size = 0;

        protected object[] keyValueTable;

        protected int keyIndexShift;

        protected int threshold;

        private int[] indexTable;

        private int firstUnusedIndex = 0;

        private int firstDeletedIndex = -1;

        private int capacity;

        private readonly float load_factor;

        protected int modCount;

        public ObjectMap() : this(true)
        {

        }

        internal ObjectMap(bool withValues) : this(CollectionUtils.INITIAL_CAPACITY, 0.85f, withValues)
        {

        }

        public ObjectMap(int initialCapacity, float factor) : this(initialCapacity, 0.85f, true)
        {

        }

        public ObjectMap(int initialCapacity) : this(initialCapacity, 0.85f, true)
        {

        }
        public ObjectMap(ObjectMap<K, V> map) : this(MathUtils.Max((int)(map.Size() / 0.85f) + 1, CollectionUtils.INITIAL_CAPACITY), 0.85f)
        {
            foreach (Entry<K, V> e in map)
            {
                Put(e.GetKey(), e.GetValue());
            }
        }

        internal ObjectMap(int initialCapacity, bool withValues) : this(initialCapacity, 0.85f, withValues)
        {

        }

        internal ObjectMap(int initialCapacity, float factor, bool withValues)
        {
            if (initialCapacity < 0)
            {
                throw new LSysException("initialCapacity must be >= 0: " + initialCapacity);
            }
            if (initialCapacity > 1 << 30)
            {
                throw new LSysException("initialCapacity is too large: " + initialCapacity);
            }
            this.capacity = MathUtils.NextPowerOfTwo(initialCapacity);
            if (factor <= 0)
            {
                throw new LSysException("loadFactor must be > 0: " + factor);
            }
            this.load_factor = MathUtils.Min(factor, 1f);
            this.threshold = (int)(capacity * load_factor);
            if (threshold < 1)
            {
                throw new LSysException("illegal load factor: " + load_factor);
            }
            this.keyIndexShift = withValues ? 1 : 0;
            Init();
        }

        internal virtual void Init()
        {
        }


        internal virtual void Resize(int newCapacity)
        {
            int newValueLen = (int)(newCapacity * load_factor);
            if (keyValueTable != null)
            {
                keyValueTable = CollectionUtils.CopyOf(keyValueTable, (newValueLen << keyIndexShift) + 1);
            }
            else
            {
                keyValueTable = new object[(newValueLen << keyIndexShift) + 1];
            }
            int[] newIndices = new int[newCapacity + newValueLen];
            if (indexTable != null)
            {
                int mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
                int newMask = (int)(AVAILABLE_BITS ^ (newCapacity - 1));
                for (int i = capacity - 1; i >= 0; i--)
                {
                    int j = indexTable[i];
                    if ((j & MAP_BITS) == MAP_EMPTY)
                    {
                        continue;
                    }
                    if ((j & MAP_BITS) == MAP_NEXT)
                    {
                        int i2 = (i + 1) & (capacity - 1);
                        int j2 = indexTable[i2];
                        int arrayIndex1 = j & (capacity - 1);
                        int arrayIndex2 = j2 & (capacity - 1);
                        int newHashIndex1 = i | (j & (newMask ^ mask));
                        int newHashIndex2 = i | (j2 & (newMask ^ mask));
                        if (newHashIndex1 == newHashIndex2)
                        {
                            newIndices[newHashIndex1] = (int)(arrayIndex1 | (j & newMask) | MAP_NEXT);
                            newIndices[(newHashIndex1 + 1) & (newCapacity - 1)] = arrayIndex2 | (j2 & newMask);
                        }
                        else
                        {
                            newIndices[newHashIndex1] = (int)(arrayIndex1 | (j & newMask) | MAP_END);
                            newIndices[newHashIndex2] = (int)(arrayIndex2 | (j2 & newMask) | MAP_END);
                        }
                    }
                    else
                    {
                        int next1i = -1, next1v = 0, next1n = 0;
                        int next2i = -1, next2v = 0, next2n = 0;
                        for (; ; )
                        {
                            int arrayIndex = j & (capacity - 1);
                            int newHashIndex = i | (j & (newMask ^ mask));
                            if (newHashIndex == i)
                            {
                                if (next1i >= 0)
                                {
                                    newIndices[next1i] = (int)(next1v | MAP_OVERFLOW);
                                    next1i = newCapacity + (next1v & (newCapacity - 1));
                                    next1n++;
                                }
                                else
                                {
                                    next1i = newHashIndex;
                                }
                                next1v = arrayIndex | (j & newMask);
                            }
                            else if (newHashIndex == i + capacity)
                            {
                                if (next2i >= 0)
                                {
                                    newIndices[next2i] = (int)(next2v | MAP_OVERFLOW);
                                    next2i = newCapacity + (next2v & (newCapacity - 1));
                                    next2n++;
                                }
                                else
                                {
                                    next2i = newHashIndex;
                                }
                                next2v = arrayIndex | (j & newMask);
                            }
                            else
                            {
                                int newIndex = arrayIndex | (j & newMask);
                                int oldIndex = newIndices[newHashIndex];
                                if ((oldIndex & MAP_BITS) != MAP_EMPTY)
                                {
                                    newIndices[newCapacity + arrayIndex] = oldIndex;
                                    newIndex = (int)(newIndex | MAP_OVERFLOW);
                                }
                                else
                                {
                                    newIndex = (int)(newIndex | MAP_END);
                                }
                                newIndices[newHashIndex] = newIndex;
                            }
                            if ((j & MAP_BITS) == MAP_END)
                            {
                                break;
                            }
                            j = indexTable[capacity + arrayIndex];
                        }
                        if (next1i >= 0)
                        {
                            if (next1n == 1 && i != capacity - 1 && (next1v & (capacity - 1)) != 0
                                    && newIndices[i + 1] == 0)
                            {
                                newIndices[i] = (int)(newIndices[i] ^ MAP_OVERFLOW ^ MAP_NEXT);
                                newIndices[i + 1] = next1v;
                            }
                            else
                            {
                                newIndices[next1i] = (int)(next1v | MAP_END);
                            }
                        }
                        if (next2i >= 0)
                        {
                            if (next2n == 1 && i != capacity - 1 && (next2v & (capacity - 1)) != 0
                                    && newIndices[i + capacity + 1] == 0)
                            {
                                newIndices[i + capacity] = (int)(newIndices[i + capacity] ^ MAP_OVERFLOW ^ MAP_NEXT);
                                newIndices[i + capacity + 1] = next2v;
                            }
                            else
                            {
                                newIndices[next2i] = (int)(next2v | MAP_END);
                            }
                        }
                    }
                }
                for (int i = firstDeletedIndex; i >= 0; i = (newIndices[newCapacity + i] = indexTable[capacity + i]))
                {
                    ;
                }
            }
            capacity = newCapacity;
            threshold = newValueLen;
            indexTable = newIndices;
        }

        internal int PositionOf(object key)
        {
            if (key == null)
            {
                return nullKeyPresent ? NULL_INDEX : NO_INDEX;
            }
            if (indexTable == null)
            {
                return NO_INDEX;
            }
            int hc = CollectionUtils.GetLimitHash(key.GetHashCode());
            int index = indexTable[hc & (capacity - 1)];
            uint MAP = (uint)index & MAP_BITS;
            if (MAP == MAP_EMPTY)
            {
                return NO_INDEX;
            }
            int mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
            for (; ; )
            {
                int position = index & (capacity - 1);
                if ((index & mask) == (hc & mask))
                {
                    object key1 = keyValueTable[(position << keyIndexShift) + 1];
                    if (key == key1 || key.Equals(key1))
                    {
                        return position;
                    }
                }
                if (MAP == MAP_END)
                {
                    return NO_INDEX;
                }
                else if (MAP == MAP_OVERFLOW)
                {
                    index = indexTable[capacity + position];
                }
                else if (MAP == MAP_NEXT)
                {
                    index = indexTable[(hc + 1) & (capacity - 1)];
                }
                else
                {
                    return NO_INDEX;
                }
                MAP = (uint)index & MAP_BITS;
            }
        }

        public virtual V Get(object key)
        {
            if (key == null)
            {
                return nullKeyPresent ? (V)keyValueTable[0] : default;
            }
            if (indexTable == null)
            {
                return default;
            }
            int hc = CollectionUtils.GetLimitHash(key.GetHashCode());
            int index = indexTable[hc & (capacity - 1)];
            uint MAP = (uint)index & MAP_BITS;
            if (MAP == MAP_EMPTY)
            {
                return default;

            }
            int mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
            for (; ; )
            {
                int position = index & (capacity - 1);
                if ((index & mask) == (hc & mask))
                {
                    object key1 = keyValueTable[(position << 1) + 1];
                    if (key == key1 || key.Equals(key1))
                    {
                        return (V)keyValueTable[(position << 1) + 2];
                    }
                }
                if (MAP == MAP_END)
                {
                    return default;
                }
                else if (MAP == MAP_OVERFLOW)
                {
                    index = indexTable[capacity + position];
                }
                else if (MAP == MAP_NEXT)
                {
                    index = indexTable[(hc + 1) & (capacity - 1)];
                }
                else
                {
                    return default;
                }
                MAP = (uint)index & MAP_BITS;
            }
        }

        internal bool IsEmpty(int i)
        {
            return i == NULL_INDEX ? !nullKeyPresent
                    : firstDeletedIndex >= 0 && keyValueTable[(i << keyIndexShift) + 1] == null;
        }

        public virtual V Put(K key, V value)
        {
            if (key == null)
            {
                return default;
            }
            return Put(key, value, true);
        }

        internal V Put(K key, V value, bool searchForExistingKey)
        {
            bool callback = this is OrderedMap<K, V>;
            if (key == null)
            {
                object oldValue;
                if (keyIndexShift > 0)
                {
                    if (keyValueTable == null)
                    {
                        keyValueTable = new object[(threshold << keyIndexShift) + 1];
                    }
                    oldValue = keyValueTable[0];
                    keyValueTable[0] = value;
                }
                else
                    oldValue = nullKeyPresent ? FINAL_VALUE : null;
                if (nullKeyPresent)
                {
                    if (callback)
                    {
                        UpdateBind(NULL_INDEX);
                    }
                }
                else
                {
                    nullKeyPresent = true;
                    size++;
                    if (callback)
                    {
                        AddBind(NULL_INDEX);
                    }
                }
                return (V)oldValue;
            }
            int hc = CollectionUtils.GetLimitHash(key.GetHashCode());
            int i = hc & (capacity - 1);
            int head;
            if (indexTable != null)
            {
                head = indexTable[i];
            }
            else
            {
                head = 0;
                indexTable = new int[capacity + threshold];
                if (keyValueTable == null)
                {
                    keyValueTable = new object[(threshold << keyIndexShift) + 1];
                }
            }
            int depth = 1;
            int mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
            uint MAP = (uint)head & MAP_BITS;
            if (MAP != MAP_EMPTY && searchForExistingKey)
            {
                int index = head;
                for (; ; )
                {
                    int cur = index & (capacity - 1);
                    if ((index & mask) == (hc & mask))
                    {
                        object key1 = keyValueTable[(cur << keyIndexShift) + 1];
                        if ((object)key == (object)key1 || key.Equals(key1))
                        {
                            object oldValue;
                            if (keyIndexShift > 0)
                            {
                                oldValue = keyValueTable[(cur << keyIndexShift) + 2];
                                keyValueTable[(cur << keyIndexShift) + 2] = value;
                            }
                            else
                            {
                                oldValue = FINAL_VALUE;
                            }
                            if (callback)
                            {
                                UpdateBind(cur);
                            }
                            return (V)oldValue;
                        }
                    }
                    depth++;
                    if ((index & MAP_BITS) == MAP_END)
                    {
                        break;
                    }
                    else if ((index & MAP_BITS) == MAP_OVERFLOW)
                    {
                        index = indexTable[capacity + cur];
                    }
                    else if ((index & MAP_BITS) == MAP_NEXT)
                    {
                        index = indexTable[(i + 1) & (capacity - 1)];
                    }
                    else
                    {
                        break;
                    }
                }
            }
            bool defragment = depth > 2 && firstUnusedIndex + depth <= threshold;
            if (size >= threshold)
            {
                Resize(capacity << 1);
                i = hc & (capacity - 1);
                mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
                head = indexTable[i];
                MAP = (uint)head & MAP_BITS;
                defragment = false;
            }
            if (MAP == MAP_EMPTY && head != 0)
            {
                int i2 = (hc - 1) & (capacity - 1);
                int head2 = indexTable[i2];
                int j2 = head2 & (capacity - 1);
                indexTable[i2] = (int)((head2 & AVAILABLE_BITS) | MAP_OVERFLOW);
                indexTable[capacity + j2] = (int)(head | MAP_END);
                head = 0;
            }
            int newIndex;
            if (firstDeletedIndex >= 0 && !defragment)
            {
                newIndex = firstDeletedIndex;
                firstDeletedIndex = indexTable[capacity + firstDeletedIndex];
                modCount++;
            }
            else
            {
                newIndex = firstUnusedIndex;
                firstUnusedIndex++;
            }
            if (defragment)
            {
                int j = head;
                head = (j & ~(capacity - 1)) | firstUnusedIndex;
                for (; ; )
                {
                    int k = j & (capacity - 1);
                    object tmp = keyValueTable[(k << keyIndexShift) + 1];
                    keyValueTable[(firstUnusedIndex << keyIndexShift) + 1] = tmp;
                    keyValueTable[(k << keyIndexShift) + 1] = null;
                    if (keyIndexShift > 0)
                    {
                        tmp = keyValueTable[(k << keyIndexShift) + 2];
                        keyValueTable[(firstUnusedIndex << keyIndexShift) + 2] = tmp;
                        keyValueTable[(k << keyIndexShift) + 2] = null;
                    }
                    int _nextIndex, n;
                    if ((j & MAP_BITS) == MAP_END)
                    {
                        _nextIndex = -1;
                        n = 0;
                    }
                    else if ((j & MAP_BITS) == MAP_OVERFLOW)
                    {
                        _nextIndex = capacity + k;
                        n = indexTable[_nextIndex];
                    }
                    else if ((j & MAP_BITS) == MAP_NEXT)
                    {
                        _nextIndex = (i + 1) & (capacity - 1);
                        n = (int)(indexTable[_nextIndex] | MAP_END);
                        indexTable[_nextIndex] = 0;
                        head = (int)((head & AVAILABLE_BITS) | MAP_OVERFLOW);
                        MAP = MAP_OVERFLOW;
                    }
                    else
                    {
                        _nextIndex = -1;
                        n = 0;
                    }
                    indexTable[capacity + k] = firstDeletedIndex;
                    firstDeletedIndex = k;
                    if (callback)
                    {
                        RelocateBind(firstUnusedIndex, k);
                    }
                    firstUnusedIndex++;
                    if (_nextIndex < 0)
                    {
                        break;
                    }
                    j = n;
                    indexTable[capacity + firstUnusedIndex - 1] = (j & ~(capacity - 1)) | firstUnusedIndex;
                }
            }
            keyValueTable[(newIndex << keyIndexShift) + 1] = key;
            if (keyIndexShift > 0)
            {
                keyValueTable[(newIndex << keyIndexShift) + 2] = value;
            }
            if (MAP == MAP_EMPTY)
            {
                indexTable[i] = (int)(newIndex | (hc & mask) | MAP_END);
            }
            else if (MAP == MAP_END && newIndex != 0 && indexTable[(i + 1) & (capacity - 1)] == 0)
            {
                indexTable[i] = (int)((head & AVAILABLE_BITS) | MAP_NEXT);
                indexTable[(i + 1) & (capacity - 1)] = newIndex | (hc & mask);
            }
            else if (MAP == MAP_NEXT)
            {
                int i2 = (i + 1) & (capacity - 1);
                int head2 = indexTable[i2];
                indexTable[i2] = 0;
                indexTable[capacity + (head & (capacity - 1))] = (int)(head2 | MAP_END);
                indexTable[capacity + newIndex] = (int)((head & AVAILABLE_BITS) | MAP_OVERFLOW);
                indexTable[i] = (int)(newIndex | (hc & mask) | MAP_OVERFLOW);
            }
            else
            {
                indexTable[capacity + newIndex] = head;
                indexTable[i] = (int)(newIndex | (hc & mask) | MAP_OVERFLOW);
            }
            size++;
            modCount++;
            if (callback)
            {
                AddBind(newIndex);
            }
            return default;
        }


        public virtual V Remove(object key)
        {
            if (key == null)
            {
                return default;
            }
            V result = RemoveKey(key, NO_INDEX);
            return (object)result == EMPTY_OBJECT ? default : result;
        }

        internal V RemoveKey(object key, int index)
        {
            if (key == null)
            {
                if (nullKeyPresent)
                {
                    nullKeyPresent = false;
                    size--;
                    if (this is OrderedMap<K, V>)
                    {
                        RemoveBind(NULL_INDEX);
                    }
                    if (keyIndexShift > 0)
                    {
                        V oldValue = (V)keyValueTable[0];
                        keyValueTable[0] = null;
                        return oldValue;
                    }
                    else
                    {
                        return (V)FINAL_VALUE;
                    }
                }
                else
                {
                    return (V)EMPTY_OBJECT;
                }
            }
            if (indexTable == null)
            {
                return (V)EMPTY_OBJECT;
            }
            int hc = CollectionUtils.GetLimitHash(key.GetHashCode());
            int prev = -1;
            int curr = hc & (capacity - 1);
            int i = indexTable[curr];
            if ((i & MAP_BITS) == MAP_EMPTY)
            {
                return (V)EMPTY_OBJECT;
            }
            int mask = (int)(AVAILABLE_BITS ^ (capacity - 1));
            for (; ; )
            {
                int j = i & (capacity - 1);
                int k = capacity + j;
                if ((hc & mask) == (i & mask))
                {
                    bool found;
                    if (index == NO_INDEX)
                    {
                        object o = keyValueTable[(j << keyIndexShift) + 1];
                        found = key == o || key.Equals(o);
                    }
                    else
                    {
                        found = j == index;
                    }
                    if (found)
                    {
                        size--;
                        if ((i & MAP_BITS) == MAP_END)
                        {
                            if (prev >= 0)
                                indexTable[prev] = (int)(indexTable[prev] | MAP_END);
                            else
                            {
                                indexTable[curr] = 0;
                            }
                        }
                        else if ((i & MAP_BITS) == MAP_OVERFLOW)
                        {
                            indexTable[curr] = indexTable[k];
                        }
                        else if ((i & MAP_BITS) == MAP_NEXT)
                        {
                            int c2 = (curr + 1) & (capacity - 1);
                            int i2 = indexTable[c2];
                            indexTable[curr] = (int)(i2 | MAP_END);
                            indexTable[c2] = 0;
                        }
                        else
                        {
                            indexTable[prev] = (int)(indexTable[prev] | MAP_END);
                            indexTable[curr] = 0;
                        }
                        if (size == 0)
                        {
                            firstUnusedIndex = 0;
                            firstDeletedIndex = -1;
                        }
                        else if (j == firstUnusedIndex - 1)
                        {
                            firstUnusedIndex = j;
                        }
                        else
                        {
                            indexTable[k] = firstDeletedIndex;
                            firstDeletedIndex = j;
                        }
                        object oldValue = index != NO_INDEX ? null
                                : keyIndexShift == 0 ? FINAL_VALUE : keyValueTable[(j << keyIndexShift) + 2];
                        keyValueTable[(j << keyIndexShift) + 1] = null;
                        if (keyIndexShift > 0)
                        {
                            keyValueTable[(j << keyIndexShift) + 2] = null;
                        }
                        modCount++;
                        if (this is OrderedMap<K, V>)
                        {
                            RemoveBind(j);
                        }
                        return (V)oldValue;
                    }
                }
                prev = curr;
                if ((i & MAP_BITS) == MAP_END)
                {
                    break;
                }
                else if ((i & MAP_BITS) == MAP_OVERFLOW)
                {
                    curr = k;
                }
                else if ((i & MAP_BITS) == MAP_NEXT)
                {
                    curr = (curr + 1) & (capacity - 1);
                }
                else
                {
                    break;
                }
                i = indexTable[curr];
            }
            return (V)EMPTY_OBJECT;
        }
        public virtual void Clear()
        {
            if (indexTable != null)
            {
                CollectionUtils.Fill(indexTable, 0, capacity + firstUnusedIndex, 0);
            }
            if (keyValueTable != null)
            {
                CollectionUtils.Fill(keyValueTable, 0, (firstUnusedIndex << keyIndexShift) + 1, default);
            }
            size = 0;
            firstUnusedIndex = 0;
            firstDeletedIndex = -1;
            modCount++;
            nullKeyPresent = false;
        }

        public virtual int Size()
        {
            return size;
        }


        public virtual bool IsEmpty()
        {
            return size == 0;
        }


        public virtual bool ContainsKey(object key)
        {
            return PositionOf(key) != NO_INDEX;
        }

        public void PutAll(ObjectMap<K, V> m)
        {
            int mSize = m.Size();
            if (mSize == 0)
            {
                return;
            }
            if (mSize > threshold)
            {
                int newCapacity = capacity;
                int newThreshold;
                do
                {
                    newCapacity <<= 1;
                    newThreshold = (int)(newCapacity * load_factor);
                } while (newThreshold < mSize);
                Resize(newCapacity);
            }
            if (m is ObjectMap<K, V> fm)
            {
                for (int i = fm.IterateFirst(); i != NO_INDEX; i = fm.IterateNext(i))
                {

                    K key = (K)fm.keyValueTable[(i << fm.keyIndexShift) + 1];


                    V value = (V)(fm.keyIndexShift > 0 ? fm.keyValueTable[(i << fm.keyIndexShift) + 2] : FINAL_VALUE);
                    Put(key, value);
                }
            }
            else
            {
                foreach (Entry<K, V> e in m)
                {
                    Put(e.GetKey(), e.GetValue());
                }
            }
        }

        public bool ContainsValue(object value)
        {
            if (keyValueTable == null || size == 0)
            {
                return false;
            }
            if (keyIndexShift == 0)
            {
                return size > 0 && value == FINAL_VALUE;
            }
            for (int i = NULL_INDEX; i < firstUnusedIndex; i++)
            {
                if (!IsEmpty(i))
                {
                    object o = keyValueTable[(i << keyIndexShift) + 2];
                    if (o == value || o != null && o.Equals(value))
                    {
                        return true;
                    }
                }
            }
            return false;
        }


        internal virtual int IterateFirst()
        {
            if (size == 0)
            {
                return NO_INDEX;
            }
            if (nullKeyPresent)
            {
                return NULL_INDEX;
            }
            int i = 0;
            while (IsEmpty(i))
            {
                i++;
            }
            return i;
        }

        internal virtual int IterateNext(int i)
        {
            do
            {
                i++;
            } while (i < firstUnusedIndex && IsEmpty(i));
            return i < firstUnusedIndex ? i : NO_INDEX;
        }

        internal int Capacity()
        {
            return capacity;
        }

        internal float Load_factor()
        {
            return load_factor;
        }

        internal virtual void AddBind(int i)
        {
        }

        internal virtual void UpdateBind(int i)
        {
        }

        internal virtual void RemoveBind(int i)
        {
        }

        internal virtual void RelocateBind(int newIndex, int oldIndex)
        {
        }

        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = NULL_INDEX; i < firstUnusedIndex; i++)
                if (!IsEmpty(i))
                {
                    int hc = i == NULL_INDEX ? 0 : keyValueTable[(i << keyIndexShift) + 1].GetHashCode();
                    object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
                    if (value != null)
                    {
                        hc ^= value.GetHashCode();
                    }
                    hashCode += hc;
                }
            return hashCode;
        }

        public override bool Equals(object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o is ObjectMap<K, V>))
            {
                return false;
            }

            ObjectMap<K, V> m = (ObjectMap<K, V>)o;
            if (m.Size() != size)
            {
                return false;
            }
            for (int i = NULL_INDEX; i < firstUnusedIndex; i++)
                if (!IsEmpty(i))
                {
                    object key = i == NULL_INDEX ? null : keyValueTable[(i << keyIndexShift) + 1];
                    object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
                    if (value == null)
                    {
                        if (!(m.Get(key) == null && m.ContainsKey(key)))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        object value2 = m.Get(key);
                        if (value != value2 && !value.Equals(value2))
                        {
                            return false;
                        }
                    }
                }
            return true;
        }


        public override string ToString()
        {
            if (size == 0)
            {
                return "[]";
            }
            StrBuilder sbr = new StrBuilder();
            sbr.Append('[');
            bool first = true;
            for (int i = IterateFirst(); i != NO_INDEX; i = IterateNext(i))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sbr.Append(", ");
                }
                object key = i == NULL_INDEX ? null : keyValueTable[(i << keyIndexShift) + 1];
                object value = keyIndexShift > 0 ? keyValueTable[(i << keyIndexShift) + 2] : FINAL_VALUE;
                sbr.Append(key == this ? "(this Map)" : key);
                sbr.Append('=');
                sbr.Append(value == this ? "(this Map)" : value);
            }
            return sbr.Append(']').ToString();
        }

        public void ForEach(Consumer consumer)
        {
            Iterable_Java<Entry<K, V>>.ForEach(this, consumer);
        }

        Iterator<Entry<K, V>> Iterable<Entry<K, V>>.Iterator()
        {
            return this.Iterator();
        }

        public IEnumerator<Entry<K, V>> GetEnumerator()
        {
            return new IEnumeratorAdapter<Entry<K, V>>(this.Iterator());
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }
    }
}
