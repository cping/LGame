using System;
using System.IO;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Action.Map;
using Loon.Java;
using Loon.Utils.Debug;
using Loon.Action.Avg.Drama;
using System.Text;

namespace Loon.Core.Resource
{
    public class ConfigReader : Expression, LRelease
    {

        private static readonly Dictionary<string, ConfigReader> pConfigReaders = new Dictionary<string, ConfigReader>(
                CollectionUtils.INITIAL_CAPACITY);

        public static ConfigReader GetInstance(string resName)
        {
            lock (pConfigReaders)
            {
                ConfigReader reader = (ConfigReader)CollectionUtils.Get(pConfigReaders, resName);
                if (reader == null || reader.isClose)
                {
                    try
                    {
                        reader = new ConfigReader(resName);
                    }
                    catch (IOException ex)
                    {
                        throw new Exception(ex.Message);
                    }
                    CollectionUtils.Put(pConfigReaders, resName, reader);
                }
                return reader;
            }
        }

        public static ConfigReader GetInstance(Stream ins0)
        {
            try
            {
                return new ConfigReader(ins0);
            }
            catch (IOException ex)
            {
                throw new Exception(ex.Message);
            }
        }

        private readonly Dictionary<string, string> pConfigItems = new Dictionary<string, string>(
                CollectionUtils.INITIAL_CAPACITY);

        private System.Text.StringBuilder values = new System.Text.StringBuilder();

        private bool isClose;

        public Dictionary<String, String> GetContent()
        {
            return new Dictionary<String, String>(pConfigItems);
        }

        public ConfigReader(string resName)
            : this(Resources.OpenStream(resName))
        {
        }

        public ConfigReader(Stream ins0)
        {
            TextReader reader = null;
            try
            {
                reader = new StreamReader(ins0, System.Text.Encoding.UTF8);
                string record = null;
                StringBuilder mapBuffer = new StringBuilder();
                bool mapFlag = false;
                string mapName = null;
                for (; (record = reader.ReadLine()) != null; )
                {
                    record = record.Trim();
                    if (record.Length > 0 && !record.StartsWith(FLAG_L_TAG)
                            && !record.StartsWith(FLAG_C_TAG)
                            && !record.StartsWith(FLAG_I_TAG))
                    {
                        if (record.StartsWith("begin"))
                        {
                            mapBuffer.Remove(0, mapBuffer.Length - (0));
                            string mes = record.Substring(5, (record.Length) - (5))
                                    .Trim();
                            if (mes.StartsWith("name"))
                            {
                                mapName = LoadItem(mes, false);
                            }
                            mapFlag = true;
                        }
                        else if (record.StartsWith("end"))
                        {
                            mapFlag = false;
                            if (mapName != null)
                            {
                                CollectionUtils.Put(pConfigItems, mapName, mapBuffer.ToString());
                            }
                        }
                        else if (mapFlag)
                        {
                            mapBuffer.Append(record);
                        }
                        else
                        {
                            LoadItem(record, true);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                throw new IOException(ex.Message);
            }
            finally
            {
                LSystem.Close(ins0);
                if (reader != null)
                {
                    try
                    {
                        reader.Close();
                        reader = null;
                    }
                    catch (IOException e)
                    {
                        Log.Exception(e);
                    }
                }
            }
        }

        private string LoadItem(string mes, bool save)
        {
            char[] chars = mes.ToCharArray();
            int size = chars.Length;
            StringBuilder sbr = values.Remove(0, values.Length - (0));
            string key = null;
            string value_ren = null;
            int idx = 0;
            for (int i = 0; i < size; i++)
            {
                char flag = chars[i];
                switch ((int)flag)
                {
                    case '=':
                        if (idx == 0)
                        {
                            key = sbr.ToString();
                            sbr.Remove(0, sbr.Length - (0));
                        }
                        idx++;
                        break;
                    case '\'':
                        break;
                    case ' ':
                        break;
                    case '\"':
                        break;
                    default:
                        sbr.Append(flag);
                        break;
                }
            }
            if (key != null)
            {
                value_ren = sbr.ToString();
                if (save)
                {
                    CollectionUtils.Put(pConfigItems, key, value_ren);
                }
            }
            return value_ren;
        }

        public void PutItem(string key, string value_ren)
        {
            lock (pConfigItems)
            {
                CollectionUtils.Put(pConfigItems, key, value_ren);
            }
        }

        public void RemoveItem(string key)
        {
            lock (pConfigItems)
            {
                CollectionUtils.Remove(pConfigItems, key);
            }
        }

        public Field2D GetField2D(string name, int width, int height)
        {
            return GetField2D(name, width, height, null);
        }

        public Field2D GetField2D(string name, int width, int height,
                Field2D fallback)
        {
            int[][] arrays = GetArray2D(name,
                    (fallback == null) ? null : fallback.GetMap());
            if (arrays != null)
            {
                return new Field2D(arrays, width, height);
            }
            return null;
        }

        public int[][] GetArray2D(string name)
        {
            return GetArray2D(name, null);
        }

        public int[][] GetArray2D(string name, int[][] fallback)
        {
            string v = null;
            lock (pConfigItems)
            {
                v = (string)CollectionUtils.Get(pConfigItems, name);
            }
            if (v != null)
            {
                bool pFlag = false;
                char[] chars = v.ToCharArray();
                int size = chars.Length;
                StringBuilder sbr = new StringBuilder(128);
                List<int[]> records = new List<int[]>(
                        CollectionUtils.INITIAL_CAPACITY);
                for (int i = 0; i < size; i++)
                {
                    char pValue = chars[i];
                    switch ((int)pValue)
                    {
                        case '{':
                            pFlag = true;
                            break;
                        case '}':
                            pFlag = false;
                            string row = sbr.ToString();
                            string[] strings = StringUtils.Split(row, ",");
                            int length = strings.Length;
                            int[] arrays = new int[length];
                            for (int j = 0; j < length; j++)
                            {
                                arrays[j] = Int32.Parse(strings[j]);
                            }
                            CollectionUtils.Add(records, arrays);
                            sbr.Remove(0, sbr.Length - (0));
                            break;
                        case ' ':
                            break;
                        default:
                            if (pFlag)
                            {
                                sbr.Append(pValue);
                            }
                            break;
                    }
                }
                int col = records.Count;
                int[][] result = new int[col][];
                for (int i_0 = 0; i_0 < col; i_0++)
                {
                    result[i_0] = (int[])records[i_0];
                }
                return result;
            }
            return fallback;
        }

        public bool GetBoolValue(string name)
        {
            return GetBoolValue(name, false);
        }

        public bool GetBoolValue(string name, bool fallback)
        {
            string v = null;
            lock (pConfigItems)
            {
                v = (string)CollectionUtils.Get(pConfigItems, name);
            }
            if (v == null)
            {
                return fallback;
            }
            return "true".Equals(v, StringComparison.InvariantCultureIgnoreCase) || "yes".Equals(v, StringComparison.InvariantCultureIgnoreCase) || "ok".Equals(v, StringComparison.InvariantCultureIgnoreCase);
        }

        public int GetIntValue(string name)
        {
            return GetIntValue(name, 0);
        }

        public int GetIntValue(string name, int fallback)
        {
            string v = null;
            lock (pConfigItems)
            {
                v = (string)CollectionUtils.Get(pConfigItems, name);
            }
            if (v == null)
            {
                return fallback;
            }
            return Int32.Parse(v);
        }

        public float GetFloatValue(string name)
        {
            return GetFloatValue(name, 0f);
        }

        public float GetFloatValue(string name, float fallback)
        {
            string v = null;
            lock (pConfigItems)
            {
                v = (string)CollectionUtils.Get(pConfigItems, name);
            }
            if (v == null)
            {
                return fallback;
            }
            return Single.Parse(v, JavaRuntime.NumberFormat);
        }

        public string GetValue(string name)
        {
            return GetValue(name, null);
        }

        public string GetValue(string name, string fallback)
        {
            string v = null;
            lock (pConfigItems)
            {
                v = (string)CollectionUtils.Get(pConfigItems, name);
            }
            if (v == null)
            {
                return fallback;
            }
            return v;
        }

        public string Get(string name)
        {
            return GetValue(name, null);
        }

        public bool IsClose()
        {
            return isClose;
        }

        public virtual void Dispose()
        {
            isClose = true;
            if (pConfigItems != null)
            {
                pConfigItems.Clear();
            }
        }

    }
}
