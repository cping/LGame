using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;

namespace java.util
{
    public class Pool<T> : IEnumerable<T> where T : class
    {
        private readonly T[] items;

        private readonly System.Collections.Generic.List<T> enumerableItemList = new System.Collections.Generic.List<T>();

        private readonly Predicate<T> validate;

        private readonly Allocate allocate;

        private readonly ConstructorInfo constructor;

        public Action<T> Initialize { get; set; }

        public Action<T> Uninitialize { get; set; }

        public int MaximumSize { get { return items.Length; } }

        public int AllocationSize { get; private set; }

        public int ValidCount { get { return items.Length - InvalidCount; } }

        public int InvalidCount { get; private set; }

        public T this[int index]
        {
            get
            {
                index += InvalidCount;

                if (index < InvalidCount || index >= MaximumSize)
                    throw new IndexOutOfRangeException("The index must be less than or equal to ValidCount");

                return items[index];
            }
        }

        public Pool(int size, Predicate<T> validateFunc) : this(size, size, validateFunc) { }

        public Pool(int size, int initialAllocation, Predicate<T> validateFunc) : this(size, initialAllocation, validateFunc, null) { }

        public Pool(int size, int initialAllocation, Predicate<T> validateFunc, Allocate allocateFunc)
        {
            if (size < 1)
                throw new ArgumentException("size must be greater than zero");
            if (initialAllocation < 0 || initialAllocation > size)
                throw new ArgumentException("initialAllocation must be non-negative and cannot be larger than size");
            items = new T[size];
            validate = validateFunc ?? throw new ArgumentNullException("validateFunc");
            InvalidCount = size;

            allocate = allocateFunc ?? ConstructorAllocate;

            if (allocate == ConstructorAllocate)
            {
                constructor = typeof(T).GetConstructor(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance, null, new Type[] { }, null);

                if (constructor == null)
                    throw new InvalidOperationException("No allocateFunc was provided and " + typeof(T) + " does not have a parameterless constructor.");
            }

            AllocationSize = initialAllocation;
            for (int i = 0; i < initialAllocation; i++)
            {
                T obj = allocate();
                items[i] = obj ?? throw new InvalidOperationException("The pool's allocate method returned a null object reference.");
            }
        }

        public void Sort(Comparison<T> comparer)
        {
            enumerableItemList.Sort(comparer);
        }

        public void CleanUp()
        {
            for (int i = InvalidCount; i < items.Length; i++)
            {
                T obj = items[i];

                if (validate(obj))
                    continue;

                if (i != InvalidCount)
                {
                    items[i] = items[InvalidCount];
                    items[InvalidCount] = obj;
                }

                enumerableItemList.Remove(obj);

                if (Uninitialize != null)
                    Uninitialize(obj);

                InvalidCount++;
            }
        }


        public T New()
        {
            if (InvalidCount > 0)
            {
                InvalidCount--;

                T obj = items[InvalidCount];

                if (obj == null)
                {
                    obj = allocate();

                    if (obj == null)
                        throw new InvalidOperationException("The pool's allocate method returned a null object reference.");

                    items[InvalidCount] = obj;
                    AllocationSize++;
                }

                if (Initialize != null)
                    Initialize(obj);

                enumerableItemList.Add(obj);

                return obj;
            }

            return null;
        }

        public IEnumerator<T> GetEnumerator()
        {
            return enumerableItemList.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        private T ConstructorAllocate()
        {
            return constructor.Invoke(null) as T;
        }

        public delegate T Allocate();
    }
}
