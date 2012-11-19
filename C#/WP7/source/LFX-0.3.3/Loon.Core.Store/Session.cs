using System;
using System.Text;
using System.Collections.Generic;
using Loon.Java;
using Loon.Utils.Debug;
using Loon.Utils;

namespace Loon.Core.Store
{
    public class Session : LRelease
    {

        public static Session Load(string name)
        {
            return new Session(name);
        }

        public static Session LoadStringSession(string res)
        {
            Session session = new Session((Session)null);
            session.LoadEncodeSession(res);
            return session;
        }

        private const string flag = "&";

        private sealed class Record
        {

            internal string name;

            internal string[] values;

            internal bool active;

            public Record(string name)
            {
                this.values = new string[0];
                this.name = name;
            }

            public int Size()
            {
                if (values != null)
                {
                    return values.Length;
                }
                return 0;
            }

            public int Decode(string[] parts, int n)
            {
                if (n >= parts.Length)
                {
                    return n;
                }
                active = "1".Equals(parts[n++]);
                if (n >= parts.Length)
                {
                    return n;
                }
                int count = Convert.ToInt32(parts[n++]);
                values = new string[count];
                for (int i = 0; i < count; i++)
                {
                    if (n >= parts.Length)
                    {
                        return n;
                    }
                    values[i] = parts[n++];
                }
                return n;
            }

            public string Get(int index)
            {
                if (index < 0 || index >= values.Length)
                {
                    return null;
                }
                else
                {
                    return values[index];
                }
            }

            public void Set(int index, string v)
            {
                string value = StringUtils.Replace(v, flag, "+");
                if (index >= values.Length)
                {
                    string[] res = new string[index + 1];
                    for (int i = 0; i < values.Length; i++)
                    {
                        res[i] = values[i];
                    }
                    values = res;
                }
                this.values[index] = value;
            }

            public string Encode()
            {
                StringBuilder sbr = new StringBuilder(32);
                sbr.Append(this.name);
                sbr.Append(flag);
                sbr.Append(this.active ? "1" : "0");
                sbr.Append(flag);
                sbr.Append(this.values.Length);
                sbr.Append(flag);
                for (int i = 0; i < this.values.Length; i++)
                {
                    sbr.Append(this.values[i]);
                    sbr.Append(flag);
                }
                return sbr.ToString();
            }

        }

        private string name;

        private Dictionary<string, Record> records;

        private List<Record> recordsList;

        private Session(Session session)
        {
            if (session != null)
            {
                this.name = session.name;
                this.records = new Dictionary<string, Record>(session.records);
                this.recordsList = new List<Record>(session.recordsList);
            }
            else
            {
                this.records = new Dictionary<string, Record>(10);
                this.recordsList = new List<Record>(10);
            }
        }

        public Session(string name)
            : this(name, true)
        {
        }

        public Session(string name, bool gain)
        {
            if (name == null)
            {
                throw new Exception("session name can not exist !");
            }
            this.name = name;
            this.records = new Dictionary<string, Record>(10);
            this.recordsList = new List<Record>(10);
            if (gain)
            {
                Load();
            }
        }

        public int LoadEncodeSession(string encode)
        {
            if (encode != null && !"".Equals(encode))
            {
                string[] parts = StringUtils.Split(encode, flag);
                return Decode(parts, 0);
            }
            return -1;
        }

        public string GetActiveID()
        {
            lock (recordsList)
            {
                for (int i = 0; i < recordsList.Count; i++)
                {
                    Record record = recordsList[i];
                    if (record.active)
                    {
                        return record.name;
                    }
                }
                return null;
            }
        }

        public string Set(int index, string value)
        {
            string name = "session_name_" + JavaRuntime.CurrentTimeMillis();
            Set(name, index, value);
            return name;
        }

        public string Set(int index, int value)
        {
            return Set(index, Convert.ToString(value));
        }

        public string Set(int index, float value)
        {
            return Set(index, Convert.ToString(value));
        }

        public string Set(int index, bool value)
        {
            return Set(index, value ? "1" : "0");
        }

        public void Set(string name, string value)
        {
            Set(name, 0, value);
        }

        public void Set(string name, int index, string value)
        {
            lock (recordsList)
            {
                Record record = (Record)CollectionUtils.Get(records, name);
                if (record == null)
                {
                    record = new Record(name);
                    records.Add(name, record);
                    recordsList.Add(record);
                }
                record.Set(index, value);
            }
        }

        public void Set(string name, int value)
        {
            Set(name, 0, value);
        }

        public void Set(string name, int index, int value)
        {
            Set(name, index, Convert.ToString(value));
        }

        public void Set(string name, float value)
        {
            Set(name, 0, value);
        }

        public void Set(string name, int index, float value)
        {
            Set(name, index, Convert.ToString(value));
        }

        public void Set(string name, bool value)
        {
            Set(name, 0, value ? "1" : "0");
        }

        public void Set(string name, int index, bool value)
        {
            Set(name, index, value ? "1" : "0");
        }

        public void Add(string name, string value)
        {
            lock (recordsList)
            {
                Record record = (Record)CollectionUtils.Get(this.records, name);
                if (record == null)
                {
                    record = new Record(name);
                    records.Add(name, record);
                    recordsList.Add(record);
                }
                int id = record.Size();
                record.Set(id++, value);
            }
        }

        public void Add(string name, int value)
        {
            Add(name, Convert.ToString(value));
        }

        public void Add(string name, float value)
        {
            Add(name, Convert.ToString(value));
        }

        public void Add(string name, bool value)
        {
            Add(name, value ? "1" : "0");
        }

        public string Get(string name, int index)
        {
            lock (recordsList)
            {
                Record record = (Record)CollectionUtils.Get(this.records, name);
                if (record == null)
                {
                    return null;
                }
                else
                {
                    return record.Get(index);
                }
            }
        }

        public int GetInt(string name, int index)
        {
            string res = Get(name, index);
            return res != null ? Convert.ToInt32(res) : -1;
        }

        public float GetFloat(string name, int index)
        {
            string res = Get(name, index);
            return res != null ? Convert.ToSingle(res) : -1;
        }

        public bool GetBoolean(string name, int index)
        {
            string res = Get(name, index);
            return res != null ? ("1".Equals(res) ? true : false) : false;
        }

        public string Get(string name)
        {
            return Get(name, 0);
        }

        public int GetInt(string name)
        {
            return GetInt(name, 0);
        }

        public float GetFloat(string name)
        {
            return GetFloat(name, 0);
        }

        public bool GetBoolean(string name)
        {
            return GetBoolean(name, 0);
        }

        public void Delete(string name)
        {
            lock (recordsList)
            {
                records.Remove(name);
                for (int i = 0; i < recordsList.Count; i++)
                {
                    Record record = recordsList[i];
                    if (record.name.Equals(name))
                    {
                        recordsList.RemoveAt(i);
                        i--;
                    }
                }
            }
        }

        public int GetCount(string name)
        {
            lock (recordsList)
            {
                Record record = (Record)CollectionUtils.Get(this.records, name);
                if (record == null)
                {
                    return 0;
                }
                else
                {
                    return record.values.Length;
                }
            }
        }

        public int GetSize()
        {
            if (recordsList != null)
            {
                return recordsList.Count;
            }
            else
            {
                return 0;
            }
        }

        public int Decode(string[] parts, int n)
        {
            lock (recordsList)
            {
                records.Clear();
                recordsList.Clear();
                if (n >= parts.Length)
                {
                    return n;
                }

                int count = Convert.ToInt32(parts[n++]);
                for (int i = 0; i < count; i++)
                {
                    if (n >= parts.Length)
                    {
                        return n;
                    }
                    Record record = new Record(parts[n++]);
                    n = record.Decode(parts, n);
                    records.Add(record.name, record);
                    recordsList.Add(record);
                }
                return n;
            }
        }

        public string Encode()
        {
            lock (recordsList)
            {
                StringBuilder sbr = new StringBuilder();
                sbr.Append(recordsList.Count).Append(flag).ToString();
                for (int i = 0; i < recordsList.Count; i++)
                {
                    sbr.Append((recordsList[i]).Encode()).ToString();
                }
                return sbr.ToString();
            }
        }

        public bool HasData(string name)
        {
            lock (recordsList)
            {
                return records[name] != null;
            }
        }

        public void Activate(string name)
        {
            lock (recordsList)
            {
                Record record = new Record(name);
                record.active = true;
                records.Add(name, record);
                recordsList.Add(record);
            }
        }

        public void Clear(string name)
        {
            lock (recordsList)
            {

                Record record = (Record)CollectionUtils.Remove(records, name);
                if (record!=null)
                {
                        recordsList.Remove(record);
                }
                
            }
        }

        public bool IsActive(string name)
        {
            lock (recordsList)
            {
                Record record = (Record)CollectionUtils.Get(this.records, name);
                if (record != null)
                {
                    return record.active;
                }
                else
                {
                    return false;
                }
            }
        }

        public void Reject(string name)
        {
            lock (recordsList)
            {
                Clear(name);
                Record record = new Record(name);
                record.active = false;
                record.Set(0, "1");
                records.Add(name, record);
                recordsList.Add(record);
            }
        }

        public string getSessionName()
        {
            return name;
        }

        public void Save()
        {
            string result = Encode();
            if (result != null && !"".Equals(result))
            {
                RecordStoreUtils.SetBytes(name, result);
            }
        }

        public int Load()
        {
            return LoadEncodeSession(RecordStoreUtils.GetString(name));
        }

        public Dictionary<string, string> GetRecords(int index)
        {
            Dictionary<string, string> result = new Dictionary<string, string>(records.Count);
            for (IEnumerator<KeyValuePair<string, Record>> it = records.GetEnumerator(); it.MoveNext(); )
            {
                KeyValuePair<string, Record> entry = (KeyValuePair<string, Record>)it.Current;
                CollectionUtils.Put(result,entry.Key, entry.Value.Get(index));
            }
            return result;
        }

        public object Clone()
        {
            return new Session(this);
        }

        public void Dispose(string name)
        {
            lock (recordsList)
            {
                Clear(name);
                Record record = new Record(name);
                record.active = false;
                records.Add(name, record);
                recordsList.Add(record);
            }
        }

        public void Dispose()
        {
            try
            {
                if (records != null)
                {
                    records.Clear();
                }
                if (recordsList != null)
                {
                    recordsList.Clear();
                }
                RecordStoreUtils.RemoveRecord(name);
            }
            catch (Exception ex)
            {
                Log.Exception(ex);
            }
        }
    }
}
