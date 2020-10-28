namespace loon.events
{
    public interface QueryEvent<T>
    {
        bool Hit(T t);
    }

}
