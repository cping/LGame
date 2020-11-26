namespace loon.utils.reply
{
    public class Act<T> : AbstractAct<T>
    {

        public static Act<T1> Create<T1>()
        {
            return new Act<T1>();
        }

        public virtual void Emit(T e)
        {
            NotifyEmit(e);
        }

        public virtual Port<T> Port()
        {
            return new PortImpl<T>(this);
        }

        private class PortImpl<T1> : Port<T1>
        {
            private readonly Act<T1> outer;

            public PortImpl(Act<T1> outer)
            {
                this.outer = outer;
            }

            public override void OnEmit(T1 value)
            {
                outer.Emit(value);
            }
        }
    }
}
