namespace loon.utils.reply
{
    public interface ActViewListener<T> : Bypass.GoListener
    {

        void OnEmit(T e);
    }

    public interface ActView<T>
    {

        ActView<M> Map<M>(Function<T, M> func);

        ActView<T> Filter(Function<T, bool> pred);

        Connection Connect(ActViewListener<T> slot);

        void Disconnect(ActViewListener<T> slot);
    }

}
