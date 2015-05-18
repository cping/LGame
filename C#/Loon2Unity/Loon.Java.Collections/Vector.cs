using System.Collections.Generic;
namespace Loon.Java.Collections
{
    public class Vector
    {
        private List<object> vector = new List<object>();

        public void AddElement(object obj)
        {
            this.vector.Add(obj);
        }

        public bool Contains(object obj)
        {
            return this.vector.Contains(obj);
        }

        public void CopyInto(object[] objs)
        {
            for (int i = 0; (i < objs.Length) && (i < this.vector.Count); i++)
            {
                objs[i] = this.vector[i];
            }
        }

        public object ElementAt(int index)
        {
            return this.vector[index];
        }

        public object FirstElement()
        {
            if (this.vector.Count > 0)
            {
                return this.vector[0];
            }
            return null;
        }

        public void InsertElementAt(object obj, int index)
        {
            this.vector.Insert(index, obj);
        }

        public object LastElement()
        {
            if (this.vector.Count > 0)
            {
                return this.vector[this.vector.Count - 1];
            }
            return null;
        }

        public void RemoveAllElements()
        {
            this.vector.Clear();
        }

        public void RemoveElement(object obj)
        {
            this.vector.Remove(obj);
        }

        public void RemoveElementAt(int index)
        {
            this.vector.RemoveAt(index);
        }

        public void SetElementAt(object obj, int index)
        {
            this.vector[index] = obj;
        }

        public void SetSize(int size)
        {
            int num;
            if (this.vector.Count > size)
            {
                for (num = this.vector.Count - size; num > 0; num--)
                {
                    this.vector.RemoveAt(this.vector.Count - 1);
                }
            }
            else
            {
                for (num = size - this.vector.Count; num > 0; num--)
                {
                    this.vector.Add(null);
                }
            }
        }

        public int Size()
        {
            return this.vector.Count;
        }
    }
}
