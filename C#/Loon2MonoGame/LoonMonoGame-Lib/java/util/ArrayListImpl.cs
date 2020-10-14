using java.lang;
using System;
using System.Collections.Generic;
using System.Text;

namespace java.util
{
    public class ArrayListImpl<V> : AbstractList<V>
    {
        private V[] buffer;
        private int len;

        public ArrayListImpl() : base()
        {
            buffer = new V[10];
            len = 0;
        }

        public ArrayListImpl(Collection<V> collection) : this()
        {
            AddAll(collection);
        }
        public ArrayListImpl(params V[] array) : this()
        {
            foreach(V o in array)
            {
                Add(o);
            }
        }

        public override V Get(int index)
        {
            if (index < 0 || index >= len) { throw new IndexOutOfBoundsException(); }
            return buffer[index];
        }

        public override V Set(int index, V element)
        {
            if (index < 0 || index >= len) { throw new IndexOutOfBoundsException(); }
            V prev = buffer[index];
            buffer[index] = element;
            return prev;
        }

        public override void Add(int index, V element)
        {
            if (index < 0 || index > len) { throw new IndexOutOfBoundsException(); }
            if (len >= buffer.Length)
            {
                V[] newbuffer = new V[buffer.Length * 2];
                System.Array.Copy(buffer, 0, newbuffer, 0, buffer.Length);
                buffer = newbuffer;
            }
            if (index < len)
            {
                System.Array.Copy(buffer, index, buffer, index + 1, len - index);
            }
            buffer[index] = element;
            len++;
        }

        public override V Remove(int index)
        {
            if (index < 0 || index >= len) { throw new IndexOutOfBoundsException(); }
            V prev = buffer[index];
            if (index < len - 1)
            {
                System.Array.Copy(buffer, index + 1, buffer, index, len - 1 - index);
            }
            buffer[--len] = default;
            return prev;
        }

        public override int Size()
        {
            return len;
        }

        public virtual void TrimToSize()
        {
            if (len < buffer.Length)
            {
                V[] newbuffer = new V[len];
                System.Array.Copy(buffer, 0, newbuffer, 0, len);
                buffer = newbuffer;
            }
        }

        public override bool Add(V e)
        {
            if (len >= buffer.Length)
            {
                V[] newbuffer = new V[buffer.Length * 2];
                System.Array.Copy(buffer, 0, newbuffer, 0, buffer.Length);
                buffer = newbuffer;
            }
            buffer[len++] = e;
            return true;
        }

        public override void Clear()
        {
            for (int i = 0; i < len; i++)
            {  
                buffer[i] = default;
            }
            len = 0;
        }

        public override object[] ToArray()
        {
            object[] copy = new object[len];
            System.Array.Copy(buffer, 0, copy, 0, len);
            return copy;
        }

        public override V[] ToArray(V[] a)
        {
            V[] copy = (V[])System.Array.CreateInstance(a.GetType().GetElementType(), len);
            System.Array.Copy(buffer, 0, copy, 0, len);
            return copy;
        }

    }
}
