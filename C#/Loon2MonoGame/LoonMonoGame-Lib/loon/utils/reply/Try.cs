using java.lang;

namespace loon.utils.reply
{

    public abstract class Try<T>
    {

        public static Try<R> CreateSuccess<R>(R value)
        {
            return new Success<R>(value);
        }

        public static Try<R> CreateFailure<R>(System.Exception cause)
        {
            return new Failure<R>(cause);
        }


        public class Failure<T1> : Try<T1>
        {
            public readonly System.Exception cause;

            public Failure(System.Exception cause)
            {
                this.cause = cause;
            }


            public override T1 Get()
            {
                if (cause is RuntimeException exception)
                {
                    throw exception;
                }
                else if (cause is Throwable throwable)
                {
                    throw throwable;
                }
                else if (cause is System.Exception ex)
                {
                    throw ex;
                }
                else
                {
                    throw new System.Exception(cause.Message);
                }
            }

            public override System.Exception GetFailure()
            {
                return cause;
            }


            public override bool IsSuccess()
            {
                return false;
            }


            public override Try<R> Map<R>(Function<T1, R> func)
            {

                return CreateFailure<R>(cause);
            }


            public override string ToString()
            {
                return "Failure(" + cause + ")";
            }

        }


        public class Success<T1> : Try<T1>
        {
            public readonly T1 value;

            public Success(T1 value)
            {
                this.value = value;
            }

            public override T1 Get()
            {
                return value;
            }


            public override System.Exception GetFailure()
            {
                throw new LSysException("Failure");
            }

            public override bool IsSuccess()
            {
                return true;
            }


            public override Try<R> Map<R>(Function<T1, R> func)
            {
                try
                {
                    return CreateSuccess(func.Apply(value));
                }
                catch (System.Exception t)
                {
                    return CreateFailure<R>(t);
                }
            }

            public override string ToString()
            {
                return "Success(" + value + ")";
            }
        }


        public abstract T Get();

        public abstract System.Exception GetFailure();

        public abstract bool IsSuccess();

        public bool IsFailure()
        {
            return !IsSuccess();
        }

        public abstract Try<R> Map<R>(Function<T, R> func);

        internal Try()
        {
        }
    }

}
