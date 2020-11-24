namespace loon.utils.reply
{

    public class Var<T> : AbstractValue<T>
    {

        protected internal T _value;

        public static Var<T1> Create<T1>(T1 value)
        {
            return new Var<T1>(value);
        }

        public Var(T value)
        {
            _value = value;
        }

        public virtual T Update(T value)
        {
            return UpdateAndNotifyIf(value);
        }

        public virtual T UpdateForce(T value)
        {
            return UpdateAndNotify(value);
        }

        public virtual Port<T> Port()
        {
            return new PortImpl<T>(this);
        }

        private class PortImpl<T1> : Port<T1>
        {
            private readonly Var<T1> outerInstance;

            public PortImpl(Var<T1> outerInstance)
            {
                this.outerInstance = outerInstance;
            }

            public override void OnEmit(T1 value)
            {
                outerInstance.Update(value);
            }
        }

        public override T Get()
        {
            return _value;
        }

        protected internal override T UpdateLocal(T value)
        {
            T oldValue = _value;
            _value = value;
            return oldValue;
        }

    }

}
