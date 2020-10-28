namespace java.util
{
    public interface List<V> : Collection<V>
    {

        void Add(int index, V element);

        bool AddAll(int index, Collection<V> c);

        V Get(int index);

        int IndexOf(V o);

        int LastIndexOf(V o);

        V Remove(int index);

        void ReplaceAll(java.util.function.UnaryOperator unaryoperator);

        V Set(int index, V element);
        void Sort(java.util.Comparator<V> c);

    }
    public static class List_Java<V>
    {
        public static void Sort(List<V> @this, Comparator<V> c)
        {
            V[] a = @this.ToArray(new V[0]);
            int l = a.Length;
            java.util.Arrays.Sort(a, 0, l, c);
            for (int i = 0; i < l; i++)
            {
                @this.Set(i, (V)a[i]);
            }
        }
        public static void ReplaceAll(List<V> @this, java.util.function.UnaryOperator unaryoperator)
        {
            int s = @this.Size();
            for (int i = 0; i < s; i++)
            {
                @this.Set(i, (V)unaryoperator.Apply(@this.Get(i)));
            }
        }
    }
}
