namespace loon.utils
{

    public class MapBundle<T> : Bundle<T>
    {

        protected readonly ObjectMap<string, T> _mapBundle;

        public MapBundle()
        {
            this._mapBundle = new ObjectMap<string, T>(20);
        }


        public void Put(string key, T value)
        {
            _mapBundle.Put(key, value);
        }


        public T Get(string key)
        {
            return Get(key, default);
        }


        public T Get(string key, T defaultValue)
        {
            T value = _mapBundle.Get(key);
            if (value != null)
            {
                return value;
            }
            else
            {
                return defaultValue;
            }
        }


        public T Remove(string key)
        {
            return Remove(key, default);
        }


        public T Remove(string key, T defaultValue)
        {
            T value = _mapBundle.Remove(key);
            if (value != null)
            {
                return value;
            }
            else
            {
                return defaultValue;
            }
        }


        public int Size()
        {
            return _mapBundle.size;
        }


        public void Clear()
        {
            _mapBundle.Clear();
        }


        public bool IsEmpty()
        {
            return _mapBundle.IsEmpty();
        }
    }

}
