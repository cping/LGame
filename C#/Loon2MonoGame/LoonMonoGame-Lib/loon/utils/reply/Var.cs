namespace loon.utils.reply
{

    public class Var<T> : AbstractValue<T>
    {

        protected T _value;

        public static  Var<T1> Create<T1>(T1 value)
        {
            return new Var<T1>(value);
        }

        public Var(T value)
        {
            _value = value;
        }

        public T Update(T value)
        {
            return default;// UpdateAndNotifyIf(value);
        }

        public T UpdateForce(T value)
        {
            return default;// UpdateAndNotify(value);
        }

        public override T Get()
        {
            return _value;
        }

        protected virtual T UpdateLocal(T value)
        {
            T oldValue = _value;
            _value = value;
            return oldValue;
        }

    }

}
