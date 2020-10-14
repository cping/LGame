using java.lang;

namespace java.util 
{
    public interface  Iterator<V>
    {
         bool HasNext();
         V Next();
         void Remove();
    }

    public static class Iterator_Java<V>
    {
        public static void Remove(Iterator<V> @this)
        {   
            throw new UnsupportedOperationException();
        }
    }
}
