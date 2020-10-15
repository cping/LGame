using System;
using System.Collections.Generic;
using System.Text;

namespace loon.utils
{
    public interface Bundle<T> : IArray
    {
	
        void Put(string key, T value);

        T Get(string key);

        T Get(string key, T defaultValue);

        T Remove(string key);

        T Remove(string key, T defaultValue);
        }
    }
