namespace loon.utils.reply
{

    public abstract class VarView<T>
    {
        public interface Listener<T> : Bypass.GoListener
        {
            void OnChange(T value, T oldValue);
        }

        public abstract T Get();

        public abstract VarView<M> Map<M>(Function<T, M> func);

        public abstract Connection Connect(Listener<T> listener);

        public abstract void Disconnect(Listener<T> listener);

        public abstract Connection Connect(ActView<T>.Listener<T> listener);

        public abstract Connection Connect(Port<T> listener);

    }

}
