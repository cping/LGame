namespace loon
{
    public abstract class LazyLoading
    {
        public abstract class Data
        {

            public abstract Screen OnScreen();

        }

        public abstract void Register(LSetting setting, LazyLoading.Data lazy);
    }

}
