namespace Loon.Core.Resource
{
    using System;
    using System.IO;
    using System.Reflection;
    using System.Collections.Generic;
    using Loon.Utils.Collection;
    using Loon.Java.Collections;
    using Loon.Utils;
    using Loon.Java;

    public class CSVTable
    {

        public class CSVItem : ArrayMap
        {

            internal int index;

            public int GetIndex()
            {
                return index;
            }

        }

        static public object[] Load(string fileName,
                Type clazz)
        {
            object[] obj = null;
            try
            {
                CSVTable.CSVItem[] properts = Load(fileName);
                if (properts != null)
                {
                    int size = properts.Length - 1;
                    obj = (object[])Arrays.NewInstance(clazz, size);
                    for (int i = 0; i < size; i++)
                    {
                        CSVTable.CSVItem property = properts[i];
                        if (property != null)
                        {
                            ArrayMap.Entry[] entry = property.ToEntrys();
                            obj[i] = Activator.CreateInstance(clazz);
                            for (int j = 0; j < entry.Length; j++)
                            {
                                ArrayMap.Entry e = entry[j];
                                Register(obj[i], (string)e.GetKey(),
                                        (string)e.GetValue());
                            }
                        }
                    }
                    properts = null;
                }
            }
            catch (Exception ex)
            {
                throw new Exception(ex + " " + fileName);
            }
            return obj;
        }

        static public CSVTable.CSVItem[] Load(string fileName)
        {
            CSVTable.CSVItem[] items = null;
            try
            {
                CSVReader csv = new CSVReader(fileName);
                List<string> tables = csv.ReadLineAsList();
                int length = tables.Count;
                if (length > 0)
                {
                    items = new CSVTable.CSVItem[length];
                    int count = 0;
                    for (; csv.Ready(); )
                    {
                        items[count] = new CSVTable.CSVItem();
                        string[] value_ren = csv.ReadLineAsArray();
                        for (int i = 0; i < length; i++)
                        {
                            items[count].Put(tables[i], value_ren[i]);
                            items[count].index = i;
                        }
                        count++;
                    }
                }
                if (csv != null)
                {
                    csv.Close();
                    csv = null;
                }
            }
            catch (IOException ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
            return items;
        }

        /// <summary>
        /// 注册指定对象中指定名称函数为指定数值
        /// </summary>
        ///
        /// <param name="object"></param>
        /// <param name="beanProperty"></param>
        /// <param name="value"></param>
        private static void Register(object obj0,
                string beanProperty, string value_ren)
        {
            object[] beanObject = Bind(obj0.GetType(), beanProperty);
            object[] cache = new object[1];
            MethodInfo getter = (MethodInfo)beanObject[0];
            MethodInfo setter = (MethodInfo)beanObject[1];
            try
            {
                string methodType = getter.ReturnType.FullName;
                if (methodType.Equals("long", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = Int64.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("int", StringComparison.InvariantCultureIgnoreCase)
                      || methodType.Equals("integer", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = Int32.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("short", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = Int16.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("float", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = Single.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("double", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = Double.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("bool", StringComparison.InvariantCultureIgnoreCase) || methodType.Equals("boolean", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] =  Boolean.Parse(value_ren);
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("string", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = value_ren;
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
                else if (methodType.Equals("stream", StringComparison.InvariantCultureIgnoreCase))
                {
                }
                else if (methodType.Equals("char", StringComparison.InvariantCultureIgnoreCase))
                {
                    cache[0] = (((char)value_ren[0]));
                    JavaRuntime.Invoke(setter, obj0, cache);
                }
            }
            catch (Exception ex)
            {
                throw new Exception(beanProperty + " is " + ex.Message);
            }
        }

        /// <summary>
        /// 绑定指定类与方法
        /// </summary>
        ///
        /// <param name="clazz"></param>
        /// <param name="beanProperty"></param>
        /// <returns></returns>
        static private object[] Bind(Type clazz,
                string beanProperty)
        {
            object[] result = new object[2];
            byte[] array = StringUtils.GetBytes(beanProperty.ToLower());
            array[0] = (byte)Char.ToUpper((char)array[0]);
            string nowPropertyName = StringUtils.NewString(array);
            string[] names = { string.Intern(("Set" + nowPropertyName)),
					string.Intern(("Get" + nowPropertyName)),
					string.Intern(("Is" + nowPropertyName)),
					string.Intern(("Write" + nowPropertyName)),
					string.Intern(("Read" + nowPropertyName)) };
            MethodInfo getter = null;
            MethodInfo setter = null;
            MethodInfo[] methods = clazz.GetMethods();
            for (int i = 0; i < methods.Length; i++)
            {
                MethodInfo method = methods[i];
                JavaModifier m = new JavaModifier(method);
                if (!JavaModifier.IsPublic(m.GetModifiers()))
                {
                    continue;
                }
                string methodName = string.Intern(method.Name);
                for (int j = 0; j < names.Length; j++)
                {
                    string name = names[j];
                    if (!name.Equals(methodName))
                    {
                        continue;
                    }
                    if (methodName.StartsWith("Set")
                            || methodName.StartsWith("Read"))
                    {
                        setter = method;
                    }
                    else
                    {
                        getter = method;
                    }
                }
            }
            result[0] = getter;
            result[1] = setter;
            return result;
        }
    }
}
