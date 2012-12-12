using System;
using System.IO;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Loon.Utils;
using Loon.Java;

namespace Loon.Core.Store
{
    public class RecordStore
    {

        private int nextRecordId = 1;

        public const string HEADER = "RecordStore:1";

        public const string STORE_FILENAME_PREFIX = "lgame-record-";

        public const string STORE_FILENAME_SUFFIX = ".store";

        private System.IO.IsolatedStorage.IsolatedStorageFile isoStorage = System.IO.IsolatedStorage.IsolatedStorageFile.GetUserStoreForApplication();

        private static Dictionary<string, RecordStore> stores = new Dictionary<string, RecordStore>(10);

        private List<RecordItem> records = new List<RecordItem>();

        private string name;

        private int openCount = 0;

        private string storeFile;

        public static RecordStore OpenRecordStore(string recordStoreName, bool createIfNecessary)
        {
            lock (stores)
            {
                RecordStore store = (RecordStore)CollectionUtils.Get(stores, recordStoreName);
                if (store == null)
                {
                    store = new RecordStore(recordStoreName);
                    stores.Add(recordStoreName, store);
                }
                store.OpenRecordStore(createIfNecessary);
                return store;
            }
        }

        private RecordStore(string name)
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
                    Loon.Utils.Debug.Log.Exception(ex);
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
                        throw new RecordStoreException("Store " + name + " could not read/find backing file " + storeFile);
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
                    throw new RecordStoreException("Store file header mismatch: " + header);
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
                throw new RecordStoreException("ERROR reading store from disk (" + storeFile + "): " + e.StackTrace);
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
                throw new RecordStoreException("Error writing store to disk: " + e.StackTrace);
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
                throw new RecordStoreNotOpenException(message);
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
            throw new InvalidRecordIDException("deleteRecord " + recordId);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public RecordEnumeration EnumerateRecords(RecordFilter filter, RecordComparator comparator, bool keepUpdated)
        {
            CheckOpen("enumerateRecords");
            if (filter != null)
            {
                throw new Exception("enumerateRecords with RecordFilter Unimplemented");
            }
            if (comparator != null)
            {
                throw new Exception("enumerateRecords with RecordComparator Unimplemented");
            }
            if (keepUpdated)
            {
                throw new Exception("enumerateRecords with keepUpdated Unimplemented");
            }

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
                throw new InvalidRecordIDException("GetRecord record " + recordId + " not found .");
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
                throw new InvalidRecordIDException("GetRecord record " + recordId + " not found .");
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
                throw new InvalidRecordIDException("record " + recordId + " not found .");
            }
            byte[] data = (byte[])ri.data;
            if (data == null)
            {
                throw new InvalidRecordIDException();
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
                throw new RecordStoreNotOpenException(e.StackTrace);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetRecord(int recordId, byte[] newData, int offset, int numBytes)
        {
            CheckOpen("setRecord");
            RecordItem ri = GetRecordItem(recordId);
            if (ri == null)
            {
                throw new InvalidRecordIDException("record " + recordId + " not found .");
            }
            byte[] buf = new byte[numBytes];
            if (numBytes != 0)
            {
                Array.Copy(newData, offset, buf, 0, numBytes);
            }
            ri.data = buf;

            WriteToDisk(isoStorage, this.storeFile);
        }

        internal class RecordEnumerationImpl : RecordEnumeration
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
                lock (RecordStore.stores)
                {
                    return nextIndex < items.Count;
                }
            }

            public int NextRecordId()
            {
                lock (RecordStore.stores)
                {
                    if (nextIndex >= items.Count)
                    {
                        throw new InvalidRecordIDException("nextRecordId at index " + nextIndex + "/" + items.Count);
                    }
                    RecordItem ri = items[nextIndex];
                    nextIndex++;
                    return ri.id;
                }
            }

            public void Destroy()
            {

            }

            public bool HasPreviousElement()
            {
                return false;
            }

            public bool IsKeptUpdated()
            {
                return false;
            }

            public void KeepUpdated(bool keepUpdated)
            {

            }

            public byte[] NextRecord()
            {
                return null;
            }

            public int NumRecords()
            {
                return 0;
            }

            public byte[] PreviousRecord()
            {
                return null;
            }

            public int PreviousRecordId()
            {
                return 0;
            }

            public void Rebuild()
            {

            }

            public void Reset()
            {

            }
        }

    }
}
