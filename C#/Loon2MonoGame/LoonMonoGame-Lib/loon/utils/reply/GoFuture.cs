namespace loon.utils.reply
{
    public class GoFuture<T>
    {

        protected internal readonly VarView<Try<T>> _result;
        protected internal VarView<bool> _isComplete;

        public static GoFuture<T1> Success<T1>(T1 value)
        {
            return Result(Try<T1>.CreateSuccess(value));
        }

        public static GoFuture<object> Success()
        {
            return Success<object>(null);
        }

        public static GoFuture<T1> Failure<T1>(System.Exception cause)
        {
            return Result(Try<T1>.CreateFailure<T1>(cause));
        }

        public static GoFuture<T1> Result<T1>(Try<T1> result)
        {
            return new GoFuture<T1>(Var<T1>.Create(result));
        }

        public virtual GoFuture<T> OnSuccess(ActViewListener<T> slot)
        {
            Try<T> result = _result.Get();
            if (result == null)
            {
                _result.Connect(new OnSuccessImpl<T>(slot));
            }
            else if (result.IsSuccess())
            {
                slot.OnEmit(result.Get());
            }
            return this;
        }

        private class OnSuccessImpl<T1> : ActViewListener<Try<T1>>
        {
            private readonly ActViewListener<T1> slot;

            public OnSuccessImpl(ActViewListener<T1> s)
            {
                this.slot = s;
            }

            public void OnEmit(Try<T1> result)
            {
                if (result.IsSuccess())
                {
                    slot.OnEmit(result.Get());
                }
            }
        }

        public virtual GoFuture<T> OnFailur(ActViewListener<System.Exception> slot)
        {
            Try<T> result = _result.Get();
            if (result == null)
            {
                _result.Connect(new GoFutureImpl<T>(slot));
            }
            else if (result.IsFailure())
            {
                slot.OnEmit(result.GetFailure());
            }
            return this;
        }

        private class GoFutureImpl<T1> : ActViewListener<Try<T1>>
        {
            private readonly ActViewListener<System.Exception> slot;

            public GoFutureImpl(ActViewListener<System.Exception> s)
            {
                this.slot = s;
            }

            public void OnEmit(Try<T1> result)
            {
                if (result.IsFailure())
                {
                    slot.OnEmit(result.GetFailure());
                }
            }
        }

        public virtual GoFuture<R> Map<R>(Function<T, R> func)
        {
            return new GoFuture<R>(_result.Map(new FunctionImpl<T, R>(func)));
        }

        private class FunctionImpl<T1, R> : Function<Try<T1>, Try<R>>
        {

            private readonly Function<T1, R> func;

            public FunctionImpl(Function<T1, R> func)
            {
                this.func = func;
            }

            public Try<R> Apply(Try<T1> result)
            {
                return result?.Map(func);
            }
        }
        public GoFuture<T> OnComplete(ActViewListener<Try<T>> slot)
        {
            Try<T> result = _result.Get();
            if (result == null)
            {
                _result.Connect(slot);
            }
            else
            {
                slot.OnEmit(result);
            }
            return this;
        }

        public bool IsCompleteNow()
        {
            return _result.Get() != null;
        }

        public virtual Try<T> Result()
        {
            return _result.Get();
        }

        protected internal GoFuture(VarView<Try<T>> result)
        {
            _result = result;
        }

    }
}
