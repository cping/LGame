namespace Loon.Utils.Debugging
{
    using System.Collections.Generic;

    public class LogFactory
    {

        static private readonly Dictionary<string, object> lazyMap = new Dictionary<string, object>(
            30);

        public static Log GetInstance(string app)
        {
            return GetInstance(app, 0);
        }

        public static Log GetInstance(string app,int type)
        {
            string key = app.ToLower();
            object obj = CollectionUtils.Get(lazyMap, key);
            if (obj == null)
            {
                CollectionUtils.Put(lazyMap, key, obj = new Log(app, type));
            }
            return (Log)obj;
        }

        public static Log GetInstance(System.Type clazz)
        {
            string key = clazz.FullName.ToLower();
            object obj = CollectionUtils.Get(lazyMap, key);
            if (obj == null)
            {
                CollectionUtils.Put(lazyMap, key, obj = new Log(clazz));
            }
            return (Log)obj;
        }

        public static void Clear()
        {
            lazyMap.Clear();
        }

    }
}
