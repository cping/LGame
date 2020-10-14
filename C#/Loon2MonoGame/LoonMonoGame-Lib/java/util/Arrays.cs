namespace java.util
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Collections;
    using java.lang;

    public class Arrays
    {

        public static void Sort<T>(T[] a, Comparator<T> comparator)
        {
            Sort(a, 0, a.Length, comparator);
        }

        public static void Sort<T>(T[] a, int fromIndex, int toIndex, Comparator<T> comparator)
        {
            if (fromIndex < 0 || toIndex > a.Length) { throw new IndexOutOfBoundsException(); }
            int len = toIndex - fromIndex;
            if (len < 0 || comparator == null) { throw new IllegalArgumentException(); }
            Mergesort<T>(a, fromIndex, len, comparator, (len >= 4) ? new T[len / 2] : null);
        }

        private static void Mergesort<T>(T[] a, int start, int len, Comparator<T> c, T[] tmp)
        {
            if (len == 2)
            {
                if (c.Compare(a[start], a[start + 1]) > 0)
                {
                    T x = a[start];
                    a[start] = a[start + 1];
                    a[start + 1] = x;
                }
            }
            else if (len == 3)
            {
                if (c.Compare(a[start], a[start + 1]) > 0)
                {
                    T x = a[start];
                    a[start] = a[start + 1];
                    a[start + 1] = x;
                }
                if (c.Compare(a[start + 1], a[start + 2]) > 0)
                {
                    if (c.Compare(a[start], a[start + 2]) > 0)
                    {
                        T x = a[start + 2];
                        a[start + 2] = a[start + 1];
                        a[start + 1] = a[start];
                        a[start] = x;
                    }
                    else
                    {
                        T x = a[start + 1];
                        a[start + 1] = a[start + 2];
                        a[start + 2] = x;
                    }
                }
            }

            else if (len >= 4)
            {
                int halve = len / 2;
                Mergesort(a, start, halve, c, tmp);
                Mergesort(a, start + halve, len - halve, c, tmp);

                if (c.Compare(a[start + halve - 1], a[start + halve]) <= 0)
                {
                    return;
                }

                System.Array.Copy(a, start, tmp, 0, halve);
                int readtmppos = 0;
                int readpart2 = start + halve;
                int writepos = start;
 
                while (readtmppos < halve)
                {
                    if (readpart2 >= start + len)
                    {
                        System.Array.Copy(tmp, readtmppos, a, writepos, halve - readtmppos);
                        return;
                    }
                    if (c.Compare(tmp[readtmppos], a[readpart2]) > 0)
                    {
                        a[writepos] = a[readpart2];
                        readpart2++;
                    }
                    else
                    {
                        a[writepos] = tmp[readtmppos];
                        readtmppos++;
                    }
                    writepos++;
                }
            }
        }

        public static List<T> AsList<T>(params T[] array)
        {
            return new ArrayList<T>(array);
        }

        public static bool Equals<T>(T[] a1, T[] a2)
        {
            if (a1.Length != a2.Length)
            {
                return false;
            }
            for (int i = 0; i < a1.Length; i++)
            {
                if (!a1[i].Equals(a2[i]))
                {
                    return false;
                }
            }
            return true;
        }

        public static void Fill<T>(T[] array, T val)
        {
            Fill<T>(array, 0, array.Length, val);
        }

        public static void Fill<T>(T[] array, int start, int end, T val)
        {
            for (int i = start; i < end; i++)
            {
                array[i] = val;
            }
        }

        public static void Sort<T>(T[] array)
        {
            Array.Sort<T>(array);
        }

        public static void Sort<T>(T[] array, IComparer<T> c)
        {
            Array.Sort<T>(array, c);
        }

        public static void Sort<T>(T[] array, int start, int count)
        {
            Array.Sort<T>(array, start, count);
        }

        public static void Sort<T>(T[] array, int start, int count, IComparer<T> c)
        {
            Array.Sort<T>(array, start, count, c);
        }

        public static void Sort<T>(List<T> list, IComparer<T> c)
        {
            T[] a = list.ToArray(new T[0]);
            Sort(a, c);
            for (int j = 0; j < list.Size(); j++)
            {
                list.Set(j, a[j]);
            }
        }

        public static List<object> AsList(params object[] array)
        {
            return new ArrayList<object>(array);
        }

        public static T[] CopyOf<T>(T[] original, int newLength)
        {
            T[] destinationArray = new T[newLength];
            Array.Copy(original, destinationArray, newLength);
            return destinationArray;
        }

        public static T[] CopyOf<T, U>(U[] original, int newLength, Type newType)
        {
            throw new NotImplementedException();
        }

        public static Array CreateArray(Type type, params int[] dims)
        {
            return Array.CreateInstance(type, dims);
        }

        public static Array CreateArray(Type type, int dim)
        {
            return Array.CreateInstance(type, dim);
        }

        public static Array CreateJaggedArray(Type type, int dim1, int dim2)
        {
            Array array = Array.CreateInstance(Array.CreateInstance(type, 1).GetType(), dim1);
            for (int i = 0; i < dim1; i++)
            {
                array.SetValue(Array.CreateInstance(type, dim2), i);
            }
            return array;
        }

        public static bool Equal(byte[] array1, byte[] array2)
        {
            return Equals(array1, array2);
        }

        public static bool Equal(sbyte[] array1, sbyte[] array2)
        {
            return Equals(array1, array2);
        }

        public static bool Equals(bool[] array1, bool[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(byte[] array1, byte[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(char[] array1, char[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(double[] array1, double[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(short[] array1, short[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(int[] array1, int[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(long[] array1, long[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(object[] array1, object[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    object obj2 = array1[i];
                    object obj3 = array2[i];
                    if (!((obj2 == null) ? (obj3 == null) : obj2.Equals(obj3)))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(sbyte[] array1, sbyte[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static bool Equals(float[] array1, float[] array2)
        {
            if (array1 != array2)
            {
                if (((array1 == null) || (array2 == null)) || (array1.Length != array2.Length))
                {
                    return false;
                }
                for (int i = 0; i < array1.Length; i++)
                {
                    if (array1[i] != array2[i])
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static void Fill(Array array, object val)
        {
            int length = array.Length;
            for (int i = 0; i < length; i++)
            {
                array.SetValue(val, i);
            }
        }

        public static void Fill(long[] array, long val)
        {
            int length = array.Length;
            for (int i = 0; i < length; i++)
            {
                array.SetValue(val, i);
            }
        }

        public static void Fill(int[] array, int fromIndex, int toIndex, int val)
        {
            RangeCheck(array.Length, fromIndex, toIndex);
            for (int i = fromIndex; i < toIndex; i++)
            {
                array[i] = val;
            }
        }

        public static void Fill(Array array, int start, int end, object val)
        {
            for (int i = start; i < end; i++)
            {
                array.SetValue(val, i);
            }
        }

        public static object Get(object array, int indice)
        {
            return ((Array)array).GetValue(indice);
        }

        public static object Get(object array, int? indice)
        {
            return ((Array)array).GetValue(indice.Value);
        }

        private static Array InternalCreateArray(Type type, Type arraytype, int[] dims)
        {
            int length = dims.Length;
            if (length == 1)
            {
                return Array.CreateInstance(type, dims[0]);
            }
            Array array = Array.CreateInstance(arraytype, dims[0]);
            int[] numArray = new int[length - 1];
            for (int i = 0; i < (length - 1); i++)
            {
                numArray[i] = dims[i + 1];
            }
            for (int j = 0; j < dims[0]; j++)
            {
                array.SetValue(CreateArray(type, numArray), j);
            }
            return array;
        }

        public static Array NewInstance(Type type, params int?[] dims)
        {
            throw new NotImplementedException("NewInstance(Type type, params int?[] dims)");
        }

        public static Array NewInstance(Type type, params int[] dims)
        {
            return Array.CreateInstance(type, dims);
        }

        private static void RangeCheck(int length, int fromIndex, int toIndex)
        {
            if (fromIndex > toIndex)
            {
                throw new ArgumentException(string.Concat(new object[] { "fromIndex(", fromIndex, ") > toIndex(", toIndex, ")" }));
            }
            if (fromIndex < 0)
            {
                throw new IndexOutOfRangeException("fromIndex(" + fromIndex + ")");
            }
            if (toIndex > length)
            {
                throw new IndexOutOfRangeException("toIndex(" + toIndex + ")");
            }
        }

        public static void Set(object array, int? indice, object newValue)
        {
            ((Array)array).SetValue(newValue, indice.Value);
        }

        public static void Set(object array, int indice, object newValue)
        {
            ((Array)array).SetValue(newValue, indice);
        }
    }


}
