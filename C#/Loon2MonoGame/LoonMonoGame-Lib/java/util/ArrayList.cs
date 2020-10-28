namespace java.util
{
    public class ArrayList<V> : ArrayListImpl<V>
    {
        public ArrayList() : base()
        {
        }

        public ArrayList(Collection<V> collection) : base(collection)
        {
        }
        public ArrayList(params V[] array) : base(array)
        {
        }

        public ArrayList(int capacity) : base()
        {
        }

    }
}
