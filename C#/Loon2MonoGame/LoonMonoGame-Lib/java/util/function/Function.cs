using java.lang;

namespace java.util.function
{
    public interface Function
    {
        object Apply(object s);
        Function AndThen(Function other);
        Function Compose(Function other);
    }

    public static class Function_Java
    {
        public static Function AndThen(Function @this, Function other)
        {
            if (@this == null || other == null) { throw new NullPointerException(); }
            return new FunctionAndThen(@this, other);
        }

        public static Function Compose(Function @this, Function other)
        {
            if (@this == null || other == null) { throw new NullPointerException(); }
            return new FunctionAndThen(other, @this);
        }

        public static Function Identity()
        {
            return new FunctionIdentity();
        }
    }

    public class FunctionIdentity : Function
    {
        public FunctionIdentity()
        {
        }
        public virtual object Apply(object o)
        {
            return o;
        }
        public virtual Function AndThen(Function other)
        {
            return Function_Java.AndThen(this, other);
        }
        public virtual Function Compose(Function other)
        {
            return Function_Java.Compose(this, other);
        }
    }

    public class FunctionAndThen : FunctionIdentity
    {
        private readonly Function a;
        private readonly Function b;

        public FunctionAndThen(Function a, Function b) : base()
        {
            this.a = a;
            this.b = b;
        }
        public override object Apply(object o)
        {
            return b.Apply(a.Apply(o));
        }
    }
}

