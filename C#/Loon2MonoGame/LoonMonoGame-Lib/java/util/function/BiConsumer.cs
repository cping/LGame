using java.lang;

namespace java.util.function 
{
    public interface BiConsumer
    {
        void Accept(object t, object u);
        BiConsumer AndThen(BiConsumer other);
    }

    public static class BiConsumer_Java
    {
        public static BiConsumer AndThen(BiConsumer @this, BiConsumer other)
        {   
            if (@this==null || other==null) { throw new NullPointerException(); }
            return new BiConsumerAndThen(@this,other);
        }
    }

    public class BiConsumerAndThen : BiConsumer
    {
        private readonly BiConsumer a;
        private readonly BiConsumer b;
        
        public BiConsumerAndThen(BiConsumer a, BiConsumer b) 
        {   
            this.a = a;
            this.b = b;
        }
        public void Accept(object t, object u)
        {   
            a.Accept(t,u);
            b.Accept(t,u);
        }
        public virtual BiConsumer AndThen(BiConsumer other)
        {   
            return BiConsumer_Java.AndThen(this,other);
        }
    }
}        
