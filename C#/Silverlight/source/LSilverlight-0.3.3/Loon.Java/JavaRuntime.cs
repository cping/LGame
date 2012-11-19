#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Java
{

    using System;
    using System.IO;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Net;
    using System.Reflection;
    using System.Globalization;
    using System.Threading;
    using Loon.Core;
    using Loon.Utils.Debug;

    /// <summary>
    /// 本类用以充当转换接口，作用是将C#函数仿造为常用Java函数
    /// </summary>
    public class JavaRuntime
    {

        private List<ShutdownHook> shutdownHooks = new List<ShutdownHook>();

        private static NumberFormatInfo formatProvider;

        public static NumberFormatInfo NumberFormat
        {
            get
            {
                if (formatProvider == null)
                {
                    formatProvider = new NumberFormatInfo();
                    formatProvider.NumberDecimalSeparator = ".";
                }
                return formatProvider;
            }
        }

        public static object NewInstance(string className)
        {
            Type type = JavaRuntime.ClassforName(className);
            return Activator.CreateInstance(type);
        }

        public static object NewInstance(Type clazz)
        {
            return Activator.CreateInstance(clazz);
        }

        private static string assemblyName;

        public static string GetAssemblyName()
        {
            if (assemblyName == null)
            {
                JavaRuntime.assemblyName = new AssemblyName(Assembly.GetExecutingAssembly().FullName).Name;
            }
            return assemblyName;
        }

        private static JavaRuntime instance;

        public static Uri GetResource(Assembly ass, string resourceName)
        {
            string str = @"Resources\";
            string name = (ass.GetName().Name + "." + str + resourceName).Replace(@"\", ".").Replace("/", ".");
            if (ass.GetManifestResourceStream(name) != null)
            {
                return new Uri(name);
            }
            return null;
        }

        public static Stream GetResourceAsStream(Assembly ass, string file)
        {
            string str = @"src\resources\";
            string name = (ass.GetName().Name + "." + str + file).Replace(@"\", ".").Replace("/", ".");
            return ass.GetManifestResourceStream(name);
        }

        public static Stream GetResourceAsStream(Assembly ass, string path, string file)
        {
            string name = (ass.GetName().Name + "." + path + file).Replace(@"\", ".").Replace("/", ".");
            return ass.GetManifestResourceStream(name);
        }

        public static StreamWriter NewStreamWriter(Stream stream)
        {
            return new StreamWriter(stream);
        }

        public static StreamWriter NewStreamWriter(StreamWriter writer)
        {
            return writer;
        }

        public static StreamWriter NewStreamWriter(TextWriter writer)
        {
            if (writer is StreamWriter)
            {
                return (StreamWriter)writer;
            }
            if (writer is StringWriter)
            {
                throw new Exception("In .NET a StringWriter can'b be a StreamWriter");
            }
            throw new Exception("Text writer is not a stream !");
        }

        public static StreamWriter NewStreamWriter(Stream stream, bool autoflush)
        {
            StreamWriter writer = new StreamWriter(stream);
            writer.AutoFlush = autoflush;
            return writer;
        }

        public static StreamWriter NewStreamWriter(StreamWriter writer, bool autoflush)
        {
            writer.AutoFlush = autoflush;
            return writer;
        }

        public static StreamWriter NewStreamWriter(TextWriter writer, bool autoflush)
        {
            if (writer is StreamWriter)
            {
                StreamWriter writer2 = (StreamWriter)writer;
                writer2.AutoFlush = autoflush;
                return writer2;
            }
            if (writer is StringWriter)
            {
                throw new Exception("In .NET a StringWriter can'b be a StreamWriter");
            }
            throw new Exception("Text writer is not a stream !");
        }


        public static JavaRuntime GetJavaRuntime()
        {
            if (instance == null)
            {
                instance = new JavaRuntime();
            }
            return instance;
        }

        public static ConstructorInfo GetConstructor(Type type, params Type[] paramsType)
        {
            if (paramsType.Length == 0)
            {
                return type.GetConstructor(new Type[0]);
            }
            return type.GetConstructor(paramsType);
        }

        public static MethodInfo GetMethod(Type type, string name)
        {
            char ch = name.Substring(0, 1).ToUpper()[0];
            string str = ch + name.Remove(0, 1);
            return type.GetMethod(str, BindingFlags.NonPublic | BindingFlags.Public | BindingFlags.Static | BindingFlags.Instance);
        }

        public static MethodInfo GetMethod(Type type, string name, params Type[] paramsType)
        {
            char ch = name.Substring(0, 1).ToUpper()[0];
            string str = ch + name.Remove(0, 1);
            if (paramsType.Length == 0)
            {
                return type.GetMethod(str, BindingFlags.NonPublic | BindingFlags.Public | BindingFlags.Static | BindingFlags.Instance);
            }
            return type.GetMethod(str, paramsType);
        }

        public static Type ClassforName(string className)
        {
            return ClassforName(Assembly.GetExecutingAssembly(), className);
        }

        public static Type ClassforName(Assembly a, string className)
        {
            Type type = a.GetType(className);
            if (type != null)
            {
                return type;
            }
            int length = className.LastIndexOf(".");
            if (length > 0)
            {
                string str = className.Substring(length + 1);
                string name = className.Substring(0, length) + "+" + str;
                Type type2 = a.GetType(name);
                if (type2 != null)
                {
                    return type2;
                }
            }
            foreach (Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
            {
                type = assembly.GetType(className);
                if (type == null)
                {
                    length = className.LastIndexOf(".");
                    if (length > 0)
                    {
                        string str3 = className.Substring(length + 1);
                        string str4 = className.Substring(0, length) + "+" + str3;
                        Type type3 = assembly.GetType(str4);
                        if (type3 != null)
                        {
                            return type3;
                        }
                    }
                }
                if (type != null)
                {
                    return type;
                }
            }
            return Type.GetType(className);
        }

        public static object Invoke(ConstructorInfo cInfo, params object[] parameters)
        {
            return cInfo.Invoke(parameters);
        }

        public static T Invoke<T>(ConstructorInfo cInfo, params object[] parameters)
        {
            return (T)cInfo.Invoke(parameters);
        }

        public static object Invoke(MethodInfo cInfo, object obj, params object[] parameters)
        {
            return cInfo.Invoke(obj, parameters);
        }

        public static T Invoke<T>(MethodInfo cInfo, object obj, params object[] parameters)
        {
            return (T)cInfo.Invoke(obj, parameters);
        }

        public static int IdentityHashCode(object o)
        {
            return System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(o);
        }

        public void AddShutdownHook(Runnable r)
        {
            ShutdownHook item = new ShutdownHook();
            item.Runnable = r;
            this.shutdownHooks.Add(item);
        }

        public int AvailableProcessors()
        {
            return Environment.ProcessorCount;
        }

        public static long CurrentTimeMillis()
        {
            return Environment.TickCount;
        }

        public long MaxMemory()
        {
            return int.MaxValue;
        }

        private class ShutdownHook
        {
            public Runnable Runnable;

            ~ShutdownHook()
            {
                this.Runnable.Run();
            }
        }

        public static byte[] GetBytesForString(string str)
        {
            return Encoding.UTF8.GetBytes(str);
        }

        public static byte[] GetBytesForString(string str, string encoding)
        {
            return Encoding.GetEncoding(encoding).GetBytes(str);
        }

        public static FieldInfo[] GetDeclaredFields(Type t)
        {
            return t.GetFields(BindingFlags.NonPublic | BindingFlags.Public | BindingFlags.Instance);
        }

        public static void Wait(object o)
        {
            Monitor.Wait(o);
        }

        public static void Wait(object o, long milis)
        {
            Monitor.Wait(o, (int)milis);
        }

        public static void Wait(object o, TimeSpan t)
        {
            Monitor.Wait(o, t);
        }

        public static void NotifyAll(object o)
        {
            Monitor.PulseAll(o);
        }

        public static void Notify(object o)
        {
            Monitor.Pulse(o);
        }

        public static void PrintStackTrace(Exception ex)
        {
            Log.Exception(ex);
        }

        public static string Substring(string str, int index)
        {
            return str.Substring(index);
        }

        public static string Substring(string str, int index, int endIndex)
        {
            return str.Substring(index, endIndex - index);
        }

        public static Type GetType(string name)
        {
            foreach (Assembly a in AppDomain.CurrentDomain.GetAssemblies())
            {
                Type t = a.GetType(name);
                if (t != null)
                {
                    return t;
                }
            }
            throw new InvalidOperationException("Type not found: " + name);
        }

        public static void SetCharAt(StringBuilder sb, int index, char c)
        {
            sb[index] = c;
        }

        public static bool EqualsIgnoreCase(string s1, string s2)
        {
            return s1.Equals(s2, StringComparison.CurrentCultureIgnoreCase);
        }

        public static long NanoTime()
        {
            return Environment.TickCount * 1000 * 1000;
        }

        public static int CompareOrdinal(string s1, string s2)
        {
            return string.CompareOrdinal(s1, s2);
        }

        public static string GetStringForBytes(byte[] chars)
        {
            return Encoding.UTF8.GetString(chars, 0, chars.Length);
        }

        public static string GetStringForBytes(byte[] chars, string encoding)
        {
            return GetEncoding(encoding).GetString(chars, 0, chars.Length);
        }

        public static string GetStringForBytes(byte[] chars, int start, int len)
        {
            return Encoding.UTF8.GetString(chars, start, len);
        }

        public static string GetStringForBytes(byte[] chars, int start, int len, string encoding)
        {
            return GetEncoding(encoding).GetChars(chars, start, len).ToString();
        }

        public static Encoding GetEncoding(string name)
        {
            Encoding e = Encoding.GetEncoding(name.Replace('_', '-'));
            if (e is UTF8Encoding)
            {
                return new UTF8Encoding(false, true);
            }
            return e;
        }
    }
}
