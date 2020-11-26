namespace loon
{
    public interface Data
    {
        Screen OnScreen();

    }

    public interface LazyLoading
    {

        void Register(LSetting setting, Data lazy);
    }

}
