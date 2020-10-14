namespace java.util
{
    public interface IListIterator<V> : IRemoveableIterator<V>, Iterator<V>
    {
        bool HasPrevious();
        int NextIndex();
        V Previous();
        int PreviousIndex();
        void Set(V x);
    }
}
