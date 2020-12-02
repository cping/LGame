using loon.utils.reply;

namespace loon.utils
{
    public class RefObject<T> : LRelease
    {

        public static RefObject<T1> GetValue<T1>(T1 v)
        {
            return new RefObject<T1>(v);
        }


        private Callback<T> closed;

        public T argvalue;

        public RefObject(T refarg)
        {
            argvalue = refarg;
        }

        public void Set(T value)
        {
            this.argvalue = value;
        }

        public bool HasValue()
        {
            return argvalue != null;
        }

        public T Get()
        {
            return argvalue;
        }

        public T Result()
        {
            return argvalue;
        }

        public Callback<T> GetClosed()
        {
            return closed;
        }

        public void SetClosed(Callback<T> closed)
        {
            this.closed = closed;
        }

        public override string ToString()
        {
            return StringUtils.ToString(argvalue, LSystem.NULL);
        }

        public void Close()
        {
            if (argvalue != null && closed != null)
            {
                closed.OnSuccess(argvalue);
            }
        }

    }
}
