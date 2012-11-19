using System;
namespace Loon.Utils.Collection
{
    
    public class IntArray 
    {

        public int[] items;
        public int size;
        public bool ordered;

        public IntArray(): this(true, CollectionUtils.INITIAL_CAPACITY)
        {
           
        }

        public IntArray(int capacity):this(true, capacity)
        {
            
        }

        public IntArray(bool ordered, int capacity)
        {
            this.ordered = ordered;
            items = new int[capacity];
        }

        public IntArray(IntArray array)
        {
            this.ordered = array.ordered;
            size = array.size;
            items = new int[size];
            System.Array.Copy(array.items, 0, items, 0, size);
        }

        public IntArray(int[] array):this(true, array)
        {
            
        }

        public IntArray(bool ordered, int[] array):this(ordered, array.Length)
        {
            size = array.Length;
            System.Array.Copy(array, 0, items, 0, size);
        }

        public void Add(int value)
        {
            int[] items = this.items;
            if (size == items.Length)
            {
                items = Resize(MathUtils.Max(8, (int)(size * 1.75f)));
            }
            items[size++] = value;
        }

        public void AddAll(IntArray array)
        {
            AddAll(array, 0, array.size);
        }

        public void AddAll(IntArray array, int offset, int Length)
        {
            if (offset + Length > array.size)
            {
                throw new Exception(
                        "offset + Length must be <= size: " + offset + " + "
                                + Length + " <= " + array.size);
            }
            AddAll(array.items, offset, Length);
        }

        public void AddAll(int[] array)
        {
            AddAll(array, 0, array.Length);
        }

        public void AddAll(int[] array, int offset, int Length)
        {
            int[] items = this.items;
            int sizeNeeded = size + Length - offset;
            if (sizeNeeded >= items.Length)
            {
                items = Resize(MathUtils.Max(8, (int)(sizeNeeded * 1.75f)));
            }
            System.Array.Copy(array, offset, items, size, Length);
            size += Length;
        }

        public int Get(int index)
        {
            if (index >= size)
            {
                throw new Exception(Convert.ToString(index));
            }
            return items[index];
        }

        public void Set(int index, int value)
        {
            if (index >= size)
            {
                throw new Exception(Convert.ToString(index));
            }
            items[index] = value;
        }

        public void Insert(int index, int value)
        {
            int[] items = this.items;
            if (size == items.Length)
                items = Resize(Math.Max(8, (int)(size * 1.75f)));
            if (ordered)
            {
                System.Array.Copy(items, index, items, index + 1, size - index);
            }
            else
            {
                items[size] = items[index];
            }
            size++;
            items[index] = value;
        }

        public bool Contains(int value)
        {
            int i = size - 1;
            int[] items = this.items;
            while (i >= 0)
            {
                if (items[i--] == value)
                    return true;
            }
            return false;
        }

        public int IndexOf(int value)
        {
            int[] items = this.items;
            for (int i = 0, n = size; i < n; i++)
            {
                if (items[i] == value)
                    return i;
            }
            return -1;
        }

        public bool RemoveValue(int value)
        {
            int[] items = this.items;
            for (int i = 0, n = size; i < n; i++)
            {
                if (items[i] == value)
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
                throw new Exception(Convert.ToString(index));
            }
            int[] items = this.items;
            int value = items[index];
            size--;
            if (ordered)
            {
                System.Array.Copy(items, index + 1, items, index, size - index);
            }
            else
            {
                items[index] = items[size];
            }
            return value;
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
                Resize(MathUtils.Max(8, sizeNeeded));
            }
            return items;
        }

        protected int[] Resize(int newSize)
        {
            int[] newItems = new int[newSize];
            int[] items = this.items;
            System.Array.Copy(items, 0, newItems, 0,
                    MathUtils.Min(items.Length, newItems.Length));
            this.items = newItems;
            return newItems;
        }

        public void Sort()
        {
            Array.Sort(items, 0, size);
        }

        public int[] ToArray()
        {
            int[] array = new int[size];
            System.Array.Copy(items, 0, array, 0, size);
            return array;
        }
    }
}
