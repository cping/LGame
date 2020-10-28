using java.lang;

namespace java.util.function
{
    public interface Consumer
    {
        void Accept(object s);
        Consumer AndThen(Consumer other);
    }

    public static class Consumer_Java
    {
        public static Consumer AndThen(Consumer @this, Consumer other)
        {
            if (@this == null || other == null) { throw new NullPointerException(); }
            return new ConsumerAndThen(@this, other);
        }
    }

    public class ConsumerAndThen : Consumer
    {
        private readonly Consumer a;
        private readonly Consumer b;

        public ConsumerAndThen(Consumer a, Consumer b)
        {
            this.a = a;
            this.b = b;
        }
        public void Accept(object o)
        {
            a.Accept(o);
            b.Accept(o);
        }
        public virtual Consumer AndThen(Consumer other)
        {
            return Consumer_Java.AndThen(this, other);
        }
    }
}

