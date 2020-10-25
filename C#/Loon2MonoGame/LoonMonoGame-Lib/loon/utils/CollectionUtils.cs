using java.lang;

namespace loon.utils
{
  public class CollectionUtils
  {

        public const int INITIAL_CAPACITY = 16;

		public static int IndexOf(object[] arrays, object data)
		{
			int len = arrays.Length - 1;
			int count = 0;
			for (int i = len; i >= 0; i--)
			{
				object o = arrays[i];
				if (o == data || (o != null && o.Equals(data)))
				{
					return len - count;
				}
				count++;
			}
			return -1;
		}

		public static int HashCode(int[][] arrays)
		{
			if (arrays == null)
			{
				return 0;
			}
			int result = 1;
			int h = arrays.Length;
			int w = arrays[0].Length;
			int value = 0;
			for (int i = 0; i < h; i++)
			{
				for (int j = 0; j < w; j++)
				{
					value = arrays[i][j];
					int elementHash = (value ^ MathUtils.Abs(value >> 32));
					result = 31 * result + elementHash;
				}
			}
			return result;
		}
		public static int[][] CopyOf(int[][] data)
		{
			int size = data.Length;
			int[][] copy = new int[size][];
			for (int i = 0; i < size; i++)
			{
				int len = data[i].Length;
				int[] res = new int[len];
				JavaSystem.Arraycopy(data[i], 0, res, 0, len);
				copy[i] = res;
			}
			return copy;
		}
		public static int GetLimitHash(int hashCode)
		{
			hashCode ^= MathUtils.Abs(hashCode >> 20) ^ MathUtils.Abs(hashCode >> 12);
			return hashCode ^ MathUtils.Abs(hashCode >> 7) ^ MathUtils.Abs(hashCode >> 4);
		}

		public static long GetHashKey(int key)
		{
			int hash = GetLimitHash(key);
			if (hash == 0)
			{
				hash = 1;
			}
			return ((long)key << 32) | (hash & 0xFFFFFFFFL);
		}

	}
}
