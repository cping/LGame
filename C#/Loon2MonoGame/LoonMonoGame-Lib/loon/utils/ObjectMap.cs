namespace loon.utils
{
   public class ObjectMap<K,V>
{

		private const uint MAP_BITS = 0xC0000000;

		private const uint MAP_EMPTY = 0;

		private const uint MAP_NEXT = 0x40000000;

		private const uint MAP_OVERFLOW = 0x80000000;

		private const uint MAP_END = 0xC0000000;

		private const uint AVAILABLE_BITS = 0x3FFFFFFF;

		private readonly object EMPTY_OBJECT = default;

		internal readonly static object FINAL_VALUE = default;

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

#pragma warning disable CS0693 // 类型参数与外部类型中的类型参数同名
        public class Entry<K, V>
#pragma warning restore CS0693 // 类型参数与外部类型中的类型参数同名
        {
			 int index;
			public  K key;
		public V value;
			ObjectMap<K, V> map;

			Entry(int index, ObjectMap<K, V> map)
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
				if (!(o is Entry<K,V>)) {
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


	}
}
