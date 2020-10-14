namespace java.io
{
    public abstract class Singleton<T> where T : class, new()
    {
        private static T instance;
        public static T Instance
        {
            get { return instance ?? (instance = new T()); }
            set { instance = value; }
        }

        protected Singleton()
        {

        }
    }
}
