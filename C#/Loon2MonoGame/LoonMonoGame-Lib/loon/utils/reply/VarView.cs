namespace loon.utils.reply
{
    public interface Listener<T> : Bypass.GoListener
    {
        void OnChange(T value, T oldValue);
    }

    public interface VarView<T>
    {


        T Get();

        VarView<M> Map<M>(Function<T, M> func);

        Connection Connect(Listener<T> listener);

        void Disconnect(Listener<T> listener);

        Connection Connect(ActView<T>.Listener<T> listener);

        Connection Connect(Port<T> listener);

    }

}
