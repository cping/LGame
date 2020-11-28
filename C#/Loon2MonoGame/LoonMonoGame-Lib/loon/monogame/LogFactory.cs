using System;
using System.Collections.Generic;

namespace loon.monogame
{
	public class LogFactory
	{

		private static readonly Dictionary<string, object> lazyMap = new Dictionary<string, object>(LSystem.DEFAULT_MAX_CACHE_SIZE);

		public static LogImpl GetInstance(string app)
		{
			return GetInstance(app, 0);
		}

		public static LogImpl GetInstance(string app, int type)
		{
			string key = app.ToLower();
			object obj;
			if (lazyMap.ContainsKey(key))
            {
				 obj = lazyMap[key];
			}else
			{
				lazyMap.Add(key, obj = new LogImpl(app, type));
			}
			return (LogImpl)obj;
		}

		public static LogImpl GetInstance(Type clazz)
		{
			string key = clazz.FullName.ToLower();
			object obj = lazyMap[key];
			if (obj == null)
			{
				lazyMap.Add(key, obj = new LogImpl(clazz));
			}
			return (LogImpl)obj;
		}

		public static void Clear()
		{
			lazyMap.Clear();
		}

	}
}
