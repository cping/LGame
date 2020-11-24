namespace loon.utils.reply
{

    public abstract class Port<T> : VarViewListener<T>, ActViewListener<T>
    {

        internal class ComposePort<P> : Port<P>
        {
            private readonly Port<T> _outer;
            private readonly Function<P, T> _fn;

            internal ComposePort(Port<T> outer, Function<P, T> fn)
            {
                this._outer = outer;
                this._fn = fn;
            }

            public override void OnEmit(P value)
            {
                _outer.OnEmit(_fn.Apply(value));
            }
        }
        internal class FilterPort<P> : Port<P> where P : T
        {
            private readonly Port<T> _outer;
            private readonly Function<P, bool> _fn;

            internal FilterPort(Port<T> outer, Function<P, bool> fn)
            {
                this._outer = outer;
                this._fn = fn;
            }

            public override void OnEmit(P value)
            {
                if (_fn.Apply(value))
                {
                    _outer.OnEmit(value);
                }
            }
        }

        internal class ThenPort<P> : Port<P> where P : T
        {
            private readonly Port<T> _before;
            private readonly Port<P> _after;

            internal ThenPort(Port<T> before, Port<P> after)
            {
                this._before = before;
                this._after = after;
            }

            public override void OnEmit(P value)
            {
                _before.OnEmit(value);
                _after.OnEmit(value);
            }
        }
        public Port<P> Compose<P>(Function<P, T> fn)
        {
            return new ComposePort<P>(this, fn);
        }

        public Port<P> Filtered<P>(Function<P, bool> pred) where P : T
        {
            return new FilterPort<P>(this, pred);
        }

        public Port<P> AndThen<P>(Port<P> after) where P : T
        {
            return new ThenPort<P>(this, after);
        }

        public virtual void OnChange(T value, T oldValue)
        {
            OnEmit(value);
        }

        public abstract void OnEmit(T e);
    }
}
