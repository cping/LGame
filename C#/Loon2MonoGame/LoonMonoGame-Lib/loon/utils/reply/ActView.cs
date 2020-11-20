namespace loon.utils.reply
{
    public abstract class ActView<T>
    {
        public interface Listener<T1> : Bypass.GoListener
        {

            void OnEmit(T1 e);
        }

        public abstract ActView<T> Map<M>(Function<T, M> func);

        public abstract ActView<T> Filter(Function<T, bool> pred);

        public abstract Connection Connect(Listener<T> slot);

        public abstract void Disconnect(Listener<T> slot);
    }

}
