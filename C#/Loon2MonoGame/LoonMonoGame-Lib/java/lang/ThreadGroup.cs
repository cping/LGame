using System.Collections.Generic;

namespace java.lang
{
    public class ThreadGroup
    {
        private List<Thread> threads = new List<Thread>();

        public ThreadGroup()
        {
        }

        public ThreadGroup(string name)
        {
        }

        internal void Add(Thread t)
        {
            lock (threads)
            {
                threads.Add(t);
            }
        }

        internal void Remove(Thread t)
        {
            lock (threads)
            {
                threads.Remove(t);
            }
        }

        public int Enumerate(Thread[] array)
        {
            lock (threads)
            {
                int count = Math.Min(array.Length, threads.Count);
                threads.CopyTo(0, array, 0, count);
                return count;
            }
        }
    }
}
