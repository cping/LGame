using System;
namespace Loon.Utils.Collection
{
    
    public class IntArray 
    {

        public int[] items;
        public int size;
        public bool ordered;

        public IntArray():this(true, CollectionUtils.INITIAL_CAPACITY)
        {
            
        }

        public IntArray(string ctx)
        {
            string intString = ctx;
            if (intString != null)
            {
                if (intString.IndexOf(',') != -1)
                {
                    intString = StringUtils.Replace(intString, ",", "")
                            .Replace(" ", "").Trim();
                }
                if (!MathUtils.IsNan(intString))
                {
                    throw new Exception('[' + intString + ']'
                            + " not number ! ");
                }
            }
            else
            {
                throw new Exception('[' + intString + ']' + " is NULL ! ");
            }
            char[] tmp = intString.ToCharArray();
            int len = tmp.Length;
            items = new int[len];
            for (int i = 0; i < len; i++)
            {
                switch ((int)tmp[i])
                {
                    case '0':
                        items[i] = 0;
                        break;
                    case '1':
                        items[i] = 1;
                        break;
                    case '2':
                        items[i] = 2;
                        break;
                    case '3':
                        items[i] = 3;
                        break;
                    case '4':
                        items[i] = 4;
                        break;
                    case '5':
                        items[i] = 5;
                        break;
                    case '6':
                        items[i] = 6;
                        break;
                    case '7':
                        items[i] = 7;
                        break;
                    case '8':
                        items[i] = 8;
                        break;
                    case '9':
                        items[i] = 9;
                        break;
                    default:
                        items[i] = -1;
                        break;
                }
            }
            size = len;
            tmp = null;
        }

        public IntArray(int capacity):this(true, capacity)
        {
            
        }

        public IntArray(bool o, int capacity)
        {
            this.ordered = o;
            items = new int[capacity];
        }

        public IntArray(IntArray array)
        {
            this.ordered = array.ordered;
            size = array.size;
            items = new int[size];
            System.Array.Copy(array.items, 0, items, 0, size);
        }

        public IntArray(params int[] array): this(true, array)
        {
           
        }

        public IntArray(bool o, int[] array): this(o, array.Length)
        {
            size = array.Length;
            System.Array.Copy(array, 0, items, 0, size);
        }

        public void Add(int value_ren)
        {
            int[] item = this.items;
            if (size == item.Length)
            {
                item = Resize(MathUtils.Max(8, (int)(size * 1.75f)));
            }
            item[size++] = value_ren;
        }

        public void AddAll(IntArray array)
        {
            AddAll(array, 0, array.size);
        }

        public void AddAll(IntArray array, int offset, int length)
        {
            if (offset + length > array.size)
            {
                throw new ArgumentException(
                        "offset + length must be <= size: " + offset + " + "
                                + length + " <= " + array.size);
            }
            AddAll(array.items, offset, length);
        }

        public void AddAll(int[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(int[] array, int offset, int length)
        {
            int[] item = this.items;
            int sizeNeeded = size + length - offset;
            if (sizeNeeded >= item.Length)
            {
                item = Resize(MathUtils.Max(8, (int)(sizeNeeded * 1.75f)));
            }
            System.Array.Copy((Array)(array), offset, (Array)(item), size, length);
            size += length;
        }

        public int Get(int index)
        {
            if (index >= size)
            {
                throw new IndexOutOfRangeException(index.ToString().ToString());
            }
            return items[index];
        }

        public void Set(int index, int value_ren)
        {
            if (index >= size)
            {
                throw new IndexOutOfRangeException(index.ToString().ToString());
            }
            items[index] = value_ren;
        }

        public void Insert(int index, int value_ren)
        {
            int[] item = this.items;
            if (size == item.Length)
                item = Resize(Math.Max(8, (int)(size * 1.75f)));
            if (ordered)
            {
                System.Array.Copy((Array)(item), index, (Array)(item), index + 1, size - index);
            }
            else
            {
                item[size] = item[index];
            }
            size++;
            item[index] = value_ren;
        }

        public bool Contains(int value_ren)
        {
            int i = size - 1;
            int[] item = this.items;
            while (i >= 0)
            {
                if (item[i--] == value_ren)
                    return true;
            }
            return false;
        }

        public bool ContainsAll(IntArray c)
        {
            for (int i = 0; i < c.size; i++)
            {
                if (!Contains(c.items[i]))
                {
                    return false;
                }
            }
            return true;
        }

        public bool Part(IntArray c)
        {
            int cc = 0;
            for (int i = 0; i < c.size; i++)
            {
                if (Contains(c.items[i]))
                {
                    cc++;
                }
            }
            return cc != 0;
        }

        public int IndexOf(int value_ren)
        {
            int[] item = this.items;
            for (int i = 0, n = size; i < n; i++)
            {
                if (item[i] == value_ren)
                {
                    return i;
                }
            }
            return -1;
        }

        public int Count(int value_ren)
        {
            int cc = 0;
            int[] item = this.items;
            for (int i = 0; i < size; i++)
            {
                if (item[i] == value_ren)
                {
                    cc++;
                }
            }
            return cc;
        }

        public IntArray Copy()
        {
            return new IntArray(this);
        }

        public bool RetainAll(IntArray c)
        {
            return BatchRemove(c, true);
        }

        public bool RemoveAll(IntArray c)
        {
            return BatchRemove(c, false);
        }

        private bool BatchRemove(IntArray c, bool complement)
        {
            int[] data = this.items;
            int r = 0, w = 0;
            bool modified = false;
            try
            {
                for (; r < size; r++)
                {
                    if (c.Contains(data[r]) == complement)
                    {
                        data[w++] = data[r];
                    }
                }
            }
            finally
            {
                if (r != size)
                {
                    System.Array.Copy((Array)(data), r, (Array)(data), w, size - r);
                    w += size - r;
                }
                if (w != size)
                {
                    for (int i = w; i < size; i++)
                    {
                        data[i] = -1;
                    }
                    size = w;
                    modified = true;
                }
            }
            return modified;
        }

        public bool RemoveValue(int value_ren)
        {
            int[] item = this.items;
            for (int i = 0, n = size; i < n; i++)
            {
                if (item[i] == value_ren)
                {
                    RemoveIndex(i);
                    return true;
                }
            }
            return false;
        }

        public int RemoveIndex(int index)
        {
            if (index >= size)
            {
                throw new IndexOutOfRangeException(index.ToString().ToString());
            }
            int[] item = this.items;
            int value_ren = item[index];
            size--;
            if (ordered)
            {
                System.Array.Copy(item, index + 1,item, index, size - index);
            }
            else
            {
                item[index] = item[size];
            }
            return value_ren;
        }

        public int Pop()
        {
            return items[--size];
        }

        public int Peek()
        {
            return items[size - 1];
        }

        public void Clear()
        {
            size = 0;
        }

        public int[] EnsureCapacity(int additionalCapacity)
        {
            int sizeNeeded = size + additionalCapacity;
            if (sizeNeeded >= items.Length)
            {
                Resize(Math.Max(8, sizeNeeded));
            }
            return items;
        }

        protected internal int[] Resize(int newSize)
        {
            int[] newItems = new int[newSize];
            int[] item = this.items;
            System.Array.Copy(item, 0,newItems, 0, Math.Min(item.Length, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public void Sort()
        {
            System.Array.Sort(items, 0, size);
        }

        public int[] ToArray()
        {
            int[] array = new int[size];
            System.Array.Copy((Array)(items), 0, (Array)(array), 0, size);
            return array;
        }

        public string ToString(char split)
        {
            if (size == 0)
            {
                return "[]";
            }
            int[] item = this.items;
            System.Text.StringBuilder buffer = new System.Text.StringBuilder(
                    CollectionUtils.INITIAL_CAPACITY);
            buffer.Append('[');
            buffer.Append(item[0]);
            for (int i = 1; i < size; i++)
            {
                buffer.Append(split);
                buffer.Append(item[i]);
            }
            buffer.Append(']');
            return buffer.ToString();
        }

        public override string ToString()
        {
            return ToString(',');
        }
    }
}
