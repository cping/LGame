namespace java.lang
{
    public interface Iterable<V>
    {
        java.util.Iterator<V> Iterator();
        void ForEach(java.util.function.Consumer consumer);
    }

    public static class Iterable_Java<V>
    {
        public static void ForEach(Iterable<V> @this, java.util.function.Consumer consumer)
        {
            java.util.Iterator<V> i = @this.Iterator();
            while (i.HasNext()) { consumer.Accept(i.Next()); }
        }
    }
}
