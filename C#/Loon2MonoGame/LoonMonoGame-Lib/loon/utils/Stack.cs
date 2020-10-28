namespace loon.utils
{
    public class Stack<T> : Array<T>
    {

        public bool Empty()
        {
            return base.IsEmpty();
        }

        public int Serach(T o)
        {
            int i = base.LastIndexOf(o);
            if (i >= 0)
            {
                return base.Size() - i;
            }
            return -1;
        }

    }

}
