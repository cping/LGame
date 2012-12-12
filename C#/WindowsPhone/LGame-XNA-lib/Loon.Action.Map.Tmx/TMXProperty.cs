using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Loon.Utils;

namespace Loon.Action.Map.Tmx
{
    public class TMXProperty : Dictionary<string, string>
    {
        [MethodImpl(MethodImplOptions.Synchronized)]
        public object SetProperty(string key, string value_ren)
        {
            return CollectionUtils.Put(this, key, value_ren);
        }

        public string GetProperty(string key, string defaultValue)
        {
            string val = GetProperty(key);
            return (val == null) ? defaultValue : val;
        }

        public string GetProperty(string key)
        {
            object oval = base[key];
            string sval = (oval is string) ? (string)oval : null;
            return sval;
        }

    }
}
