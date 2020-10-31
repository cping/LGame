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
                    int elementHash = (value ^ (int)((uint)value >> 32));
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
            hashCode ^= (int)((uint)hashCode >> 20) ^ (int)((uint)hashCode >> 12);
            return hashCode ^ (int)((uint)hashCode >> 7) ^ (int)((uint)hashCode >> 4);
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

        public static void RangeCheck(int arrayLength, int fromIndex, int toIndex)
        {
            if (fromIndex > toIndex)
            {
                throw new LSysException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            if (fromIndex < 0)
            {
                throw new LSysException("fromIndex < 0");
            }
            if (toIndex > arrayLength)
            {
                throw new LSysException("toIndex > arrayLength");
            }
        }

        public static void Fill(int[] arrays, int fromIndex, int toIndex, int val)
        {
            RangeCheck(arrays.Length, fromIndex, toIndex);
            for (int i = fromIndex; i < toIndex; i++)
            {
                arrays[i] = val;
            }
        }

        public static void Fill(char[] arrays, int fromIndex, int toIndex, char val)
        {
            RangeCheck(arrays.Length, fromIndex, toIndex);
            for (int i = fromIndex; i < toIndex; i++)
            {
                arrays[i] = val;
            }
        }

        public static void Fill(long[] arrays, int fromIndex, int toIndex, int val)
        {
            RangeCheck(arrays.Length, fromIndex, toIndex);
            for (int i = fromIndex; i < toIndex; i++)
            {
                arrays[i] = val;
            }
        }

        public static void Fill(long[] arrays, long val)
        {
            for (int i = 0, len = arrays.Length; i < len; i++)
            {
                arrays[i] = val;
            }
        }

        public static void Fill(int[] arrays, int val)
        {
            for (int i = 0, len = arrays.Length; i < len; i++)
            {
                arrays[i] = val;
            }
        }
        public static void Fill(object[] arrays, int fromIndex, int toIndex, object data)
        {
            RangeCheck(arrays.Length, fromIndex, toIndex);
            for (int i = fromIndex; i < toIndex; i++)
            {
                arrays[i] = data;
            }
        }

        public static int IndexOf(int[] arr, int v)
        {
            return IndexOf(arr, v, 0);
        }

        public static int IndexOf(int[] arr, int v, int off)
        {
            if (null != arr)
                for (int i = off; i < arr.Length; i++)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int LastIndexOf(int[] arr, int v)
        {
            if (null != arr)
                for (int i = arr.Length - 1; i >= 0; i--)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int IndexOf(char[] arr, char v)
        {
            if (null != arr)
                for (int i = 0; i < arr.Length; i++)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int IndexOf(char[] arr, char v, int off)
        {
            if (null != arr)
                for (int i = off; i < arr.Length; i++)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int LastIndexOf(char[] arr, char v)
        {
            if (null != arr)
                for (int i = arr.Length - 1; i >= 0; i--)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int IndexOf(long[] arr, long v)
        {
            return IndexOf(arr, v, 0);
        }

        public static int IndexOf(long[] arr, long v, int off)
        {
            if (null != arr)
                for (int i = off; i < arr.Length; i++)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int LastIndexOf(long[] arr, long v)
        {
            if (null != arr)
                for (int i = arr.Length - 1; i >= 0; i--)
                {
                    if (arr[i] == v)
                        return i;
                }
            return -1;
        }

        public static int[] CopyOf(int[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static int[] CopyOf(int[] obj, int newSize)
        {
            int[] tempArr = new int[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }

        public static byte[] CopyOf(byte[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static byte[] CopyOf(byte[] obj, int newSize)
        {
            byte[] tempArr = new byte[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }

        public static sbyte[] CopyOf(sbyte[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static sbyte[] CopyOf(sbyte[] obj, int newSize)
        {
            sbyte[] tempArr = new sbyte[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }

        public static char[] CopyOf(char[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static char[] CopyOf(char[] obj, int newSize)
        {
            char[] tempArr = new char[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }
        public static float[] CopyOf(float[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static float[] CopyOf(float[] obj, int newSize)
        {
            float[] tempArr = new float[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }
        public static bool[] CopyOf(bool[] data)
        {
            return CopyOf(data, data.Length);
        }

        public static bool[] CopyOf(bool[] data, int newSize)
        {
            bool[] tempArr = new bool[newSize];
            JavaSystem.Arraycopy(data, 0, tempArr, 0, MathUtils.Min(data.Length, newSize));
            return tempArr;
        }
        public static long[] CopyOf(long[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static long[] CopyOf(long[] obj, int newSize)
        {
            long[] tempArr = new long[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }

        public static object[] CopyOf(object[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        public static object[] CopyOf(object[] obj, int newSize)
        {
            object[] tempArr = new object[newSize];
            JavaSystem.Arraycopy(obj, 0, tempArr, 0, MathUtils.Min(obj.Length, newSize));
            return tempArr;
        }

        public static void Set(object array, int indice, object newValue)
        {
            ((System.Array)array).SetValue(newValue, indice);
        }
        public static object XNA_Get(object array, int indice)
        {
            return ((System.Array)array).GetValue(indice);
        }

        public static object CopyOf(object src)
        {
            int srcLength = ((System.Array)src).Length;
            System.Type srcComponentType = src.GetType().GetElementType();
            object dest = System.Array.CreateInstance(srcComponentType, srcLength);
            if (srcComponentType.IsArray)
            {
                for (int i = 0; i < ((System.Array)src).Length; i++)
                {
                    Set(dest, i, CopyOf(XNA_Get(src, i)));
                }
            }
            else
            {
                System.Array.Copy((System.Array)(src), 0, (System.Array)(dest), 0, srcLength);
            }
            return dest;
        }
        public static ArrayMap.Entry[] CopyOf(ArrayMap.Entry[] data, int newSize)
        {
            ArrayMap.Entry[] tempArr = new ArrayMap.Entry[newSize];
            JavaSystem.Arraycopy(data, 0, tempArr, 0, MathUtils.Min(data.Length, newSize));
            return tempArr;
        }

        public static ArrayMap.Entry[] CopyOf(ArrayMap.Entry[] data)
        {
            return CopyOf(data, data.Length);
        }
        public static T[] CopyOf<T>(T[] data, int start, int end)
        {
            T[] tempArr = new T[end - start];
            for (int i = start, j = 0; i < end; i++, j++)
            {
                tempArr[j] = data[i];
            }
            return tempArr;
        }

        public static void Reverse<T>(T[] arrays)
        {
            for (int i = 0, size = arrays.Length; i < size; i++)
            {
                int idx = i;
                int last = size - 1 - i;
                if (idx == last || idx > last)
                {
                    break;
                }
                T data = arrays[idx];
                T swap = arrays[last];
                arrays[idx] = swap;
                arrays[last] = data;
            }
        }

        public static bool IsEmpty(int[] array)
        {
            return array == null || array.Length == 0;
        }
        public static bool IsEmpty(char[] array)
        {
            return array == null || array.Length == 0;
        }
        public static bool IsEmpty<T>(T[] array)
        {
            return array == null || array.Length == 0;
        }
    }
}
