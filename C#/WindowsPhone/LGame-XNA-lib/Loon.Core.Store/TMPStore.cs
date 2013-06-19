using System;
using System.IO;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Loon.Utils;
using Loon.Java;

namespace Loon.Core.Store
{
     internal class TMPStore
    {
        private const int DEFAULT_ID = 1;

        private const int COULD_NOT_SAVE = -1;

        private const int COULD_NOT_OPEN = -2;

        public static byte[] GetBytes(string resName)
        {
            return GetBytes(resName, DEFAULT_ID);
        }

        public static string GetString(string resName)
        {
            return BytesToString(GetBytes(resName));
        }

        public static byte[] GetBytes(string resName, int recordId)
        {
            byte[] result = new byte[0];
            TMPStore rs = null;
            try
            {
                rs = TMPStore.OpenRecordStore(resName, true);
                if (rs.Count >= recordId)
                {
                    result = rs.GetRecord(recordId);
                }
            }
            catch(Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex.Message);
            }
            finally
            {
                CloseRecordStore(rs);
            }
            return result;
        }

        private static string BytesToString(byte[] bytes)
        {
            if (bytes == null)
            {
                return null;
            }
            return StringUtils.GetString(bytes);
        }

        private static byte[] StringToBytes(string s)
        {
            if (s == null)
            {
                return null;
            }
            return StringUtils.GetBytes(s);
        }

        public static string GetString(string resName, int recordId)
        {
            return BytesToString(GetBytes(resName, recordId));
        }

        public static void SetBytes(string resName, string data)
        {
            SetBytes(resName, StringToBytes(data));
        }

        public static void SetBytes(string resName, byte[] data)
        {
            SetBytes(resName, DEFAULT_ID, data);
        }

        public static void SetBytes(string resName, int recordId, byte[] data)
        {
            TMPStore rs = null;
            try
            {
            
                rs = TMPStore.OpenRecordStore(resName, true);
                if (rs.GetNumRecords() == 0)
                {
                    rs.AddRecord(data, 0, data.Length);
                }
                else
                {
                    rs.SetRecord(recordId, data, 0, data.Length);
                }
            }
            catch (Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
            finally
            {
                CloseRecordStore(rs);
            }
        }

        public static int AddBytes(string resName, byte[] data)
        {
            TMPStore rs = null;
            bool opened = false;
            try
            {
                rs = TMPStore.OpenRecordStore(resName, true);
                opened = true;
                return rs.AddRecord(data, 0, data.Length);
            }
            catch (Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
            finally
            {
                CloseRecordStore(rs);
            }
            return opened ? COULD_NOT_SAVE : COULD_NOT_OPEN;
        }

        public static bool ExistRecordStoreAll(string resName)
        {
            string[] recordStores = TMPStore.ListRecordStores();
            if (recordStores == null)
            {
                return false;
            }
            for (int i = 0; i < recordStores.Length; i++)
            {
                if (recordStores[i].Equals(resName))
                {
                    return true;
                }
            }
            return false;
        }

        public static void RemoveRecord(string resName, int recordId)
        {
            TMPStore rs = null;
            try
            {
                rs = TMPStore.OpenRecordStore(resName, false);
                rs.DeleteRecord(recordId);
            }
            finally
            {
                CloseRecordStore(rs);
            }
        }

        public static void RemoveRecord(string resName)
        {
            RemoveRecord(resName, DEFAULT_ID);
        }

        public static bool ExistRecordStore(string resName)
        {
            TMPStore rs = null;
            try
            {
                rs = TMPStore.OpenRecordStore(resName, false);
                return (rs != null);
            }
            catch 
            {
                return false;
            }
            finally
            {
                CloseRecordStore(rs);
            }
        }

        public static void CloseRecordStore(TMPStore rs)
        {
            if (rs != null)
            {
                rs.CloseRecordStore();
                rs = null;
            }
        }

        private int nextRecordId = 1;

        public const string HEADER = "TMPStore:1";

        public const string STORE_FILENAME_PREFIX = "lgame-record-";

        public const string STORE_FILENAME_SUFFIX = ".store";

        private System.IO.IsolatedStorage.IsolatedStorageFile isoStorage = System.IO.IsolatedStorage.IsolatedStorageFile.GetUserStoreForApplication();

        private static Dictionary<string, TMPStore> stores = new Dictionary<string, TMPStore>(10);

        private List<RecordItem> records = new List<RecordItem>();

        private string name;

        private int openCount = 0;

        private string storeFile;

        public static TMPStore OpenRecordStore(string recordStoreName, bool createIfNecessary)
        {
            lock (stores)
            {
                TMPStore store = (TMPStore)CollectionUtils.Get(stores, recordStoreName);
                if (store == null)
                {
                    store = new TMPStore(recordStoreName);
                    stores.Add(recordStoreName, store);
                }
                store.OpenRecordStore(createIfNecessary);
                return store;
            }
        }

        private TMPStore(string name)
        {
            this.name = name;
        }

        public static void Deletes()
        {
            System.IO.IsolatedStorage.IsolatedStorageFile.GetUserStoreForApplication().Remove();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DeleteStores()
        {
            string[] stores = ListRecordStores();
            if (stores == null)
            {
                return;
            }
            for (int i = 0; i < stores.Length; i++)
            {
                string store = stores[i];
                try
                {
                    DeleteRecordStore(store);
                }
                catch (Exception ex)
                {
                    Loon.Utils.Debugging.Log.Exception(ex);
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool DeleteRecordStore(string recordStoreName)
        {
            try
            {
                List<string> list = FileUtils.GetIsolatedStorageFiles(STORE_FILENAME_SUFFIX.Substring(1));
                if (list != null)
                {
                    int size = list.Count;
                    string ret, name;
                    for (int i = 0; i < size; i++)
                    {
                        name = list[i];
                        ret = FileUtils.GetFileName(list[i]);

                        ret = StringUtils.ReplaceIgnoreCase(ret.Substring(0, ret.Length - STORE_FILENAME_SUFFIX.Length), STORE_FILENAME_PREFIX, "");

                        if (recordStoreName.Equals(ret))
                        {

                            stores.Remove(ret);
                            isoStorage.DeleteFile(list[i]);

                            return true;
                        }
                    }
                }
                else
                {
                    return false;
                }
            }
            catch (IOException e)
            {
                throw new IOException("Store " + recordStoreName + "deleteRecordStore IOException!" + e.Message);
            }
            catch (Exception e)
            {
                throw new Exception("Store " + recordStoreName + "deleteRecordStore Exception!" + e.Message);
            }
            return false;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public static string[] ListRecordStores()
        {
            string[] result = null;
            try
            {
                List<string> list = FileUtils.GetIsolatedStorageFiles(STORE_FILENAME_SUFFIX.Substring(1));
                if (list != null)
                {
                    int size = list.Count;
                    result = new string[size];
                    if (size == 0)
                    {
                        result = null;
                    }
                    else
                    {
                        string ret;
                        for (int i = 0; i < size; i++)
                        {
                            ret = FileUtils.GetFileName(list[i]);
                            result[i] = StringUtils.ReplaceIgnoreCase(ret.Substring(0, ret.Length - STORE_FILENAME_SUFFIX.Length), STORE_FILENAME_PREFIX, "");
                        }
                    }

                }
            }
            catch (IOException e)
            {
                throw new Exception("Store listRecordStores IOException!" + e.StackTrace);
            }
            return result;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void OpenRecordStore(bool createIfNecessary)
        {
            if (openCount > 0)
            {
                openCount++;
                return;
            }

            storeFile = STORE_FILENAME_PREFIX + name + STORE_FILENAME_SUFFIX;

            bool exists = isoStorage.FileExists(storeFile);

            bool readOk = false;
            if (exists)
            {
                try
                {
                    ReadFromDisk(isoStorage, storeFile);
                    readOk = true;
                }
                catch
                {
                    if (!createIfNecessary)
                    {
                        throw new Exception("Store " + name + " could not read/find backing file " + storeFile);
                    }
                }
            }
            if (!readOk)
            {
                Clear();
                WriteToDisk(isoStorage, storeFile);
            }
            openCount = 1;
        }

        public class RecordItem
        {

            internal int id;

            internal byte[] data;

            internal RecordItem()
            {
            }

            internal RecordItem(int id, byte[] data)
            {
                this.id = id;
                this.data = data;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void Clear()
        {
            nextRecordId = 1;
            records.Clear();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void ReadFromDisk(System.IO.IsolatedStorage.IsolatedStorageFile iso, string storeFile)
        {
            DataInputStream dis = FileUtils.ReadIsolatedStorageFileToDataInput(iso, storeFile);
            try
            {
                string header = dis.ReadUTF();
                if (!header.Equals(HEADER))
                {
                    throw new Exception("Store file header mismatch: " + header);
                }
                nextRecordId = dis.ReadInt();
                int size = dis.ReadInt();
                records.Clear();
                for (int i = 0; i < size; i++)
                {
                    long pSize = dis.ReadLong();
                    int pId = dis.ReadInt();
                    byte[] buffer = new byte[pSize];
                    dis.Read(buffer);
                    RecordItem ri = new RecordItem(pId, buffer);
                    records.Add(ri);
                }
            }
            catch (Exception e)
            {
                throw new Exception("ERROR reading store from disk (" + storeFile + "): " + e.StackTrace);
            }
            finally
            {
                if (dis != null)
                {
                    dis.Close();
                    dis = null;
                }
            }

        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void WriteToDisk(System.IO.IsolatedStorage.IsolatedStorageFile iso, string storeFile)
        {
            DataOutputStream dos = FileUtils.WriteIsolatedStorageFileToDataInput(iso, storeFile);
            try
            {
                dos.WriteUTF(HEADER);
                dos.WriteInt(nextRecordId);
                dos.WriteInt(records.Count);
                for (int i = 0; i < records.Count; i++)
                {
                    RecordItem ri = records[i];
                    long pSize = ri.data.Length;
                    int pId = ri.id;
                    dos.WriteLong(pSize);
                    dos.WriteInt(pId);
                    dos.Write(ri.data);
                }
            }
            catch (Exception e)
            {
                throw new Exception("Error writing store to disk: " + e.StackTrace);
            }
            finally
            {
                if (dos != null)

                    dos.Close();
                dos = null;
            }
        }


        public void CheckOpen(string message)
        {
            if (openCount <= 0)
            {
                throw new Exception(message);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int AddRecord(byte[] data, int offset, int numBytes)
        {
            CheckOpen("addRecord");
            byte[] buf = new byte[numBytes];
            if (numBytes != 0)
            {
                Array.Copy(data, offset, buf, 0, numBytes);
            }

            RecordItem ri = new RecordItem(nextRecordId++, buf);
            records.Add(ri);

            WriteToDisk(isoStorage, this.storeFile);
            return ri.id;
        }

        public int Count
        {
            get
            {
                return records.Count;
            }
        }

        public void CloseRecordStore()
        {
            CheckOpen("closeRecordStore");
            openCount--;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DeleteRecord(int recordId)
        {
            CheckOpen("deleteRecord");
            for (int i = 0; i < records.Count; i++)
            {
                RecordItem ri = records[i];
                if (ri.id == recordId)
                {
                    records.RemoveAt(i);
                    WriteToDisk(isoStorage, storeFile);
                    return;
                }
            }
            throw new Exception("deleteRecord " + recordId);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public RecordEnumerationImpl EnumerateRecords()
        {
            CheckOpen("enumerateRecords");
            return new RecordEnumerationImpl(records);
        }

        public string GetName()
        {
            CheckOpen("getName");
            return name;
        }

        public int GetNumRecords()
        {
            CheckOpen("getNumRecords");
            return records.Count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private RecordItem GetRecordItem(int id)
        {
            for (IEnumerator<RecordItem> rs = records.GetEnumerator(); rs.MoveNext(); )
            {
                RecordItem ri = rs.Current;
                if (ri.id == id)
                {
                    return ri;
                }
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public byte[] GetRecord(int recordId)
        {
            CheckOpen("getRecord");
            RecordItem ri = GetRecordItem(recordId);
            if (ri == null)
            {
                throw new Exception("GetRecord record " + recordId + " not found .");
            }
            return ri.data;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int GetRecord(int recordId, byte[] buffer, int offset)
        {
            CheckOpen("getRecord");
            RecordItem ri = GetRecordItem(recordId);
            if (ri == null)
            {
                throw new Exception("GetRecord record " + recordId + " not found .");
            }
            byte[] data = ri.data;
            int recordSize = data.Length;
            Array.Copy(data, 0, buffer, offset, recordSize);
            return recordSize;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int GetRecordSize(int recordId)
        {
            CheckOpen("getRecordSize");
            RecordItem ri = GetRecordItem(recordId);
            if (ri == null)
            {
                throw new Exception("record " + recordId + " not found .");
            }
            byte[] data = (byte[])ri.data;
            if (data == null)
            {
                throw new Exception();
            }
            return data.Length;
        }


        public int GetNextRecordID()
        {
            return nextRecordId;
        }


        public int GetSize()
        {
            try
            {
                return GetRecordSize(nextRecordId);
            }
            catch (Exception e)
            {
                throw new Exception(e.StackTrace);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetRecord(int recordId, byte[] newData, int offset, int numBytes)
        {
            CheckOpen("setRecord");
            RecordItem ri = GetRecordItem(recordId);
            if (ri == null)
            {
                throw new Exception("record " + recordId + " not found .");
            }
            byte[] buf = new byte[numBytes];
            if (numBytes != 0)
            {
                Array.Copy(newData, offset, buf, 0, numBytes);
            }
            ri.data = buf;

            WriteToDisk(isoStorage, this.storeFile);
        }

        internal class RecordEnumerationImpl
        {

            private List<RecordItem> items;

            internal RecordEnumerationImpl(List<RecordItem> r)
            {
                this.items = r;
                this.nextIndex = 0;
            }

            private int nextIndex;

            public bool HasNextElement()
            {
                lock (TMPStore.stores)
                {
                    return nextIndex < items.Count;
                }
            }

            public int NextRecordId()
            {
                lock (TMPStore.stores)
                {
                    if (nextIndex >= items.Count)
                    {
                        throw new Exception("nextRecordId at index " + nextIndex + "/" + items.Count);
                    }
                    RecordItem ri = items[nextIndex];
                    nextIndex++;
                    return ri.id;
                }
            }

        }

    }
}
