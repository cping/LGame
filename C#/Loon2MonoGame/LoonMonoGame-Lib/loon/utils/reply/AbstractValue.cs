namespace loon.utils.reply
{
    public abstract class AbstractValue<T> : Bypass, VarView<T>
    {
        public virtual Connection Connect(Listener<T> listener)
        {
            throw new System.NotImplementedException();
        }

        public virtual Connection Connect(ActView<T>.Listener<T> listener)
        {
            throw new System.NotImplementedException();
        }

        public virtual Connection Connect(Port<T> listener)
        {
            throw new System.NotImplementedException();
        }

        public virtual void Disconnect(Listener<T> listener)
        {
            throw new System.NotImplementedException();
        }

        public virtual T Get()
        {
            throw new System.NotImplementedException();
        }

        public virtual VarView<M> Map<M>(Function<T, M> func)
        {
            throw new System.NotImplementedException();
        }
    }
}
