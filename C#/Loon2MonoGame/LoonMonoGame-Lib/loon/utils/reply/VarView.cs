namespace loon.utils.reply
{
    public interface VarViewListener<T> : Bypass.GoListener
    {
        void OnChange(T value, T oldValue);
    }

    public interface VarView<T>
    {
        T Get();

        VarView<M> Map<M>(Function<T,M> func);

        Connection Connect(VarViewListener<T> listener);

        void Disconnect(VarViewListener<T> listener);

        Connection Connect(ActViewListener<T> listener);

        Connection Connect(Port<T> listener);

    }

}
