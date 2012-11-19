using System;
using System.Collections.Generic;
using System.Collections;
using Loon.Java.Collections;

namespace Loon.Utils
{
    public sealed class CollectionUtils
    {

        public static object Poll(LinkedList coll)
        {
            return coll.RemoveFirst();
        }

        public static object Poll(IList coll)
        {

            int idx = coll.Count - 1;
            object result = coll[idx];
            coll.Remove(idx);
            return result;
        }

        public static bool Add(IExtendedCollection c, object e)
        {
            return c.Add(e);
        }

        public static bool Add(ICollection coll, object obj)
        {
            if (coll is IList)
            {
                ((IList)coll).Add(obj);
                return true;
            }
            if (!(coll is ISet))
            {
                throw new NotImplementedException("Add(ICollection coll, object obj)");
            }
            return ((ISet)coll).Add(obj);
        }

        public static bool Add(LinkedList<object> coll, object obj)
        {
            if (obj == null)
            {
                return false;
            }
            return coll.AddLast(obj) != null;
        }

        public static bool AddAll(ICollection source, HashedSet dest)
        {
            return dest.AddAll(source);
        }

        public static bool AddAll(ICollection c1, IExtendedCollection c2)
        {
            return c2.AddAll(c1);
        }

        public static bool AddAll(ICollection source, LinkedList dest)
        {
            return dest.AddAll(source);
        }

        public static bool AddAll(ICollection source, ICollection dest)
        {
            if (!(dest is IList))
            {
                throw new NotImplementedException("AddAll(ICollection source, ICollection dest)");
            }
            return AddAll(source, (IList)dest);
        }

        public static bool AddAll(ICollection source, IList dest)
        {
            if (source == null)
            {
                return false;
            }
            if (source.Count == 0)
            {
                return false;
            }
            foreach (object obj2 in source)
            {
                dest.Add(obj2);
            }
            return true;
        }

        public static BitArray And(BitArray b1, BitArray b2)
        {
            if (b1.Length > b2.Length)
            {
                b2.Length = b1.Length;
            }
            else if (b2.Length > b1.Length)
            {
                b1.Length = b2.Length;
            }
            b1.And(b2);
            return b1;
        }

        public static BitArray AndNot(BitArray b1, BitArray b2)
        {
            if (b1.Length > b2.Length)
            {
                b2.Length = b1.Length;
            }
            else if (b2.Length > b1.Length)
            {
                b1.Length = b2.Length;
            }
            b1.And(b2.Not());
            return b1;
        }

        public static void Clear(IExtendedCollection c)
        {
            c.Clear();
        }

        public static void Clear(IList c)
        {
            c.Clear();
        }

        public static bool Contains(object e, IExtendedCollection c)
        {
            return c.Contains(e);
        }

        public static bool Contains(object o, ICollection coll)
        {
            if (coll is Array)
            {
                foreach (object obj2 in coll)
                {
                    if (obj2 != null)
                    {
                        if ((!(obj2 is ValueType) || !(o is ValueType)) && obj2.Equals(o))
                        {
                            return true;
                        }
                        continue;
                    }
                    if (o == null)
                    {
                        return true;
                    }
                }
                return false;
            }
            IList list = coll as IList;
            if (list != null)
            {
                return list.Contains(o);
            }
            IDictionary dictionary = coll as IDictionary;
            if (dictionary != null)
            {
                return dictionary.Contains(o);
            }
            Console.WriteLine(coll);
            throw new NotImplementedException("Contains");
        }

        public static bool ContainsAll(IExtendedCollection c1, ICollection c2)
        {
            return c1.ContainsAll(c2);
        }

        public static bool ContainsAll(IList source, ICollection dest)
        {
            foreach (object obj2 in dest)
            {
                if (!source.Contains(obj2))
                {
                    return false;
                }
            }
            return true;
        }

        public static bool ContainsAll(ICollection a, ICollection b)
        {
            IEnumerator enumerator = b.GetEnumerator();
            while (enumerator.MoveNext())
            {
                if (!Contains(enumerator.Current, a))
                {
                    return false;
                }
            }
            return true;
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

        public static ISet EmptySet()
        {
            throw new Exception("EmptySet()");
        }

        internal static IIterator Enumeration(ICollection bindings)
        {
            return new IteratorAdapter(bindings.GetEnumerator());
        }

        public static bool Equals(IList list1, IList list2)
        {
            if (list2 == null)
            {
                return false;
            }
            if (list1 == list2)
            {
                return true;
            }
            IEnumerator enumerator = list1.GetEnumerator();
            IEnumerator enumerator2 = list2.GetEnumerator();
            while (enumerator.MoveNext() && enumerator2.MoveNext())
            {
                if (!object.Equals(enumerator.Current, enumerator2.Current))
                {
                    return false;
                }
            }
            return (list1.Count == list2.Count);
        }

        public static ICollection List(object p)
        {
            throw new Exception("List(object p)");
        }

        public static void Or(BitArray b1, BitArray b2)
        {
            if (b1.Length > b2.Length)
            {
                b2.Length = b1.Length;
            }
            else if (b2.Length > b1.Length)
            {
                b1.Length = b2.Length;
            }
            b1.Or(b2);
        }

        public static object Put(IDictionary table, object key, object newvalue)
        {
            object obj2 = table[key];
            table[key] = newvalue;
            return obj2;
        }

        public static void PutAll(IDictionary dest, IDictionary source)
        {
            foreach (object obj2 in source.Keys)
            {
                dest[obj2] = source[obj2];
            }
        }

        public static bool Remove(IExtendedCollection c, object e)
        {
            return (((e != null) && c.Contains(e)) && c.Remove(e));
        }

        public static bool Remove(ISet coll, object obj)
        {
            if ((obj == null) || !coll.Contains(obj))
            {
                return false;
            }
            coll.Remove(obj);
            return true;
        }

        public static bool Remove(ICollection coll, object obj)
        {
            IList list = coll as IList;
            if (list == null)
            {
                throw new NotImplementedException("Remove(ICollection coll, object obj)");
            }
            return Remove(list, obj);
        }

        public static object Remove(IDictionary coll, object obj)
        {
            object obj2 = coll[obj];
            coll.Remove(obj);
            return obj2;
        }

        public static bool Remove(IList coll, object obj)
        {
            if (!coll.Contains(obj))
            {
                return false;
            }
            coll.Remove(obj);
            return true;
        }

        public static bool RemoveAll(IExtendedCollection c1, ICollection c2)
        {
            return c1.RemoveAll(c2);
        }

        public static bool RemoveAll(IList source, ICollection dest)
        {
            foreach (object obj2 in dest)
            {
                source.Remove(obj2);
            }
            return true;
        }

        public static object RemoveAt(IList coll, int index)
        {
            object obj2 = coll[index];
            coll.RemoveAt(index);
            return obj2;
        }

        public static object RemoveAt(LinkedList coll, int index)
        {
            object obj2 = coll[index];
            coll.RemoveAt(index);
            return obj2;
        }

        public static object Get(IDictionary table, object key)
        {
            if (key != null)
            {
                return table[key];
            }
            return null;
        }

        public static bool RetainAll(IExtendedCollection c1, ICollection c2)
        {
            return c1.RetainAll(c2);
        }

        public static void Reverse(IList l)
        {
            int num = 0;
            for (int i = l.Count - 1; num < i; i--)
            {
                object obj2 = l[num];
                l[num] = l[i];
                l[i] = obj2;
                num++;
            }
        }

        public static ISet Singleton(object item)
        {
            ISet set = new HashedSet();
            set.Add(item);
            return set;
        }

        public static object[] ToArray(IExtendedCollection c)
        {
            return c.ToArray();
        }

        public static object[] ToArray(ICollection coll)
        {
            IList list = coll as IList;
            if (list == null)
            {
                throw new NotImplementedException("ToArray(ICollection coll)");
            }
            object[] array = new object[list.Count];
            list.CopyTo(array, 0);
            return array;
        }

        public static object[] ToArray(IExtendedCollection c, object[] arr)
        {
            return c.ToArray(arr);
        }

        public static object[] ToArray(ICollection coll, object[] array)
        {
            IList list = coll as IList;
            if (array.Length < coll.Count)
            {
                array = (object[])Array.CreateInstance(array.GetType().GetElementType(), coll.Count);
            }
            try
            {
                if (list != null)
                {
                    list.CopyTo(array, 0);
                    return array;
                }
            }
            catch (Exception exception)
            {
                throw exception;
            }
            ISet set = coll as ISet;
            try
            {
                if (set != null)
                {
                    set.CopyTo(array, 0);
                    return array;
                }
            }
            catch (Exception exception2)
            {
                throw exception2;
            }
            ICollection is2 = coll;
            if (is2 == null)
            {
                throw new NotImplementedException("ToArray(ICollection coll, object[] array)");
            }
            is2.CopyTo(array, 0);
            return array;
        }


        public static ISet UnmodifiableSet(ISet set)
        {
            return ExtendedCollectionBase.ReadOnly(set);
        }

 

        /// <summary>
        /// 仅为XNA版存在的函数，用来构建一个指定维度的数组
        /// 
        /// </summary>
        /// <param name="type"></param>
        /// <param name="size1"></param>
        /// <param name="size2"></param>
        /// <returns></returns>
        public static Array XNA_CreateJaggedArray(Type type, int size1, int size2)
        {
            Array array = Array.CreateInstance(Array.CreateInstance(type, 1).GetType(), size1);
            for (int i = 0; i < size1; i++)
            {
                array.SetValue(Array.CreateInstance(type, size2), i);
            }
            return array;
        }

        /// <summary>
        /// 仅为XNA版存在的函数，用来构建一个二维数组
        /// </summary>
        /// <param name="size1"></param>
        /// <param name="size2"></param>
        /// <returns></returns>
        public static int[][] XNA_2DIntArray(int size1, int size2)
        {
            int[][] arrays = new int[size1][];
            for (int i = 0; i < size1; i++)
            {
                arrays[i] = new int[size2];
            }
            return arrays;
        }

        /// <summary>
        /// 仅为XNA版存在的函数，用来注入指定长度数据到数组当中
        /// </summary>
        /// <param name="array"></param>
        /// <param name="indice"></param>
        /// <param name="newValue"></param>
        public static void XNA_Set(object array, int indice, object newValue)
        {
            ((Array)array).SetValue(newValue, indice);
        }

        /// <summary>
        /// 仅为XNA版存在的函数，用来获得指定长度的数组数据
        /// </summary>
        /// <param name="array"></param>
        /// <param name="indice"></param>
        /// <returns></returns>
        public static object XNA_Get(object array, int indice)
        {
            return ((Array)array).GetValue(indice);
        }

        public const int INITIAL_CAPACITY = 10;

        /// <summary>
        /// 判定指定对象是否存在于指定对象数组中
        /// </summary>
        ///
        /// <param name="array"></param>
        /// <param name="obj"></param>
        /// <returns></returns>
        public static int IndexOf(Object[] array, Object obj)
        {
            return Array.IndexOf(array, obj);
        }

        /// <summary>
        /// 获得指定2维数组的HashCode
        /// </summary>
        ///
        /// <param name="arrays"></param>
        /// <returns></returns>
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
                    int elementHash = (value ^ ((int)((uint)value >> 32)));
                    result = 31 * result + elementHash;
                }
            }
            return result;
        }

        /// <summary>
        /// 扩充指定数组
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="i"></param>
        /// <param name="flag"></param>
        /// <returns></returns>
        public static Object Expand(Object obj, int i, bool flag)
        {
            int j = ((Array)obj).Length;
            Object obj1 = Array.CreateInstance(obj.GetType().GetElementType(), j + i);
            System.Array.Copy((Array)(obj), 0, (Array)(obj1), (flag) ? 0 : i, j);
            return obj1;
        }

        /// <summary>
        /// 扩充指定数组
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="size"></param>
        /// <returns></returns>
        public static Object Expand(Object obj, int size)
        {
            return Expand(obj, size, true);
        }

        /// <summary>
        /// 扩充指定数组
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="size"></param>
        /// <param name="flag"></param>
        /// <param name="class1"></param>
        /// <returns></returns>
        public static Object Expand(Object obj, int size, bool flag,
                Type clazz)
        {
            if (obj == null)
            {
                return Array.CreateInstance(clazz, 1);
            }
            else
            {
                return Expand(obj, size, flag);
            }
        }

        /// <summary>
        /// 剪切出指定长度的数组
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="size"></param>
        /// <returns></returns>
        public static Object Cut(Object obj, int size)
        {
            int j;
            if ((j = ((Array)obj).Length) == 1)
            {
                return Array.CreateInstance(obj.GetType().GetElementType(), 0);
            }
            int k;
            if ((k = j - size - 1) > 0)
            {
                System.Array.Copy((Array)(obj), size + 1, (Array)(obj), size, k);
            }
            j--;
            Object obj1 = Array.CreateInstance(obj.GetType().GetElementType(), j);
            System.Array.Copy((Array)(obj), 0, (Array)(obj1), 0, j);
            return obj1;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="src"></param>
        /// <returns></returns>
        public static Object CopyOf(Object src)
        {
            int srcLength = ((Array)src).Length;
            Type srcComponentType = src.GetType().GetElementType();
            Object dest = Array.CreateInstance(srcComponentType, srcLength);
            if (srcComponentType.IsArray)
            {
                for (int i = 0; i < ((Array)src).Length; i++)
                {
                  XNA_Set(dest, i, CopyOf(XNA_Get(src, i)));
                }
            }
            else
            {
                System.Array.Copy((Array)(src), 0, (Array)(dest), 0, srcLength);
            }
            return dest;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static int[][] CopyOf(int[][] obj)
        {
            int size = obj.Length;
            int[][] copy = new int[size][];
            for (int i = 0; i < size; i++)
            {
                int len = obj[i].Length;
                int[] res = new int[len];
                System.Array.Copy((Array)(obj[i]), 0, (Array)(res), 0, len);
                copy[i] = res;
            }
            return copy;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static String[] CopyOf(String[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static String[] CopyOf(String[] obj, int newSize)
        {
            String[] tempArr = new String[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="original"></param>
        /// <param name="newLength"></param>
        /// <returns></returns>
        public static Object[] CopyOf(Object[] original, int newLength)
        {
            return CopyOf(original, newLength, original.GetType());
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="original"></param>
        /// <param name="newLength"></param>
        /// <param name="newType"></param>
        /// <returns></returns>
        public static Object[] CopyOf(Object[] original, int newLength,
                Type newType)
        {
            Object[] copy = ((Object)newType == (Object)typeof(Object[])) ? (Object[])new Object[newLength]
                    : (Object[])Array.CreateInstance(newType.GetElementType(), newLength);
            System.Array.Copy((Array)(original), 0, (Array)(copy), 0, Math.Min(original.Length, newLength));
            return copy;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static int[] CopyOf(int[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static int[] CopyOf(int[] obj, int newSize)
        {
            int[] tempArr = new int[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static double[] CopyOf(double[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static double[] CopyOf(double[] obj, int newSize)
        {
            double[] tempArr = new double[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static float[] CopyOf(float[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static float[] CopyOf(float[] obj, int newSize)
        {
            float[] tempArr = new float[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static byte[] CopyOf(byte[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static byte[] CopyOf(byte[] obj, int newSize)
        {
            byte[] tempArr = new byte[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static char[] CopyOf(char[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static char[] CopyOf(char[] obj, int newSize)
        {
            char[] tempArr = new char[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static long[] CopyOf(long[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static long[] CopyOf(long[] obj, int newSize)
        {
            long[] tempArr = new long[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <returns></returns>
        public static bool[] CopyOf(bool[] obj)
        {
            return CopyOf(obj, obj.Length);
        }

        /// <summary>
        /// copy指定长度的数组数据
        /// </summary>
        ///
        /// <param name="obj"></param>
        /// <param name="newSize"></param>
        /// <returns></returns>
        public static bool[] CopyOf(bool[] obj, int newSize)
        {
            bool[] tempArr = new bool[newSize];
            System.Array.Copy((Array)(obj), 0, (Array)(tempArr), 0, Math.Min(obj.Length, newSize));
            return tempArr;
        }
    }
}
