using System;
using System.IO;
using Loon.Core.Store;
using Loon.Java;
using Loon.Core;

namespace Loon.Utils
{
    public class RecordStoreUtils
    {

        private const int DEFAULT_ID = 1;

        private const int COULD_NOT_SAVE = -1;

        private const int COULD_NOT_OPEN = -2;

        private RecordStoreUtils()
        {
        }

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
            RecordStore rs = null;
            try
            {
                rs = RecordStore.OpenRecordStore(resName, true);
                if (rs.Count >= recordId)
                {
                    result = rs.GetRecord(recordId);
                }
            }
            catch(Exception ex)
            {
                Loon.Utils.Debug.Log.Exception(ex.Message);
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
            RecordStore rs = null;
            try
            {
            
                rs = RecordStore.OpenRecordStore(resName, true);
                if (rs.GetNumRecords() == 0)
                {
                    rs.AddRecord(data, 0, data.Length);
                }
                else
                {
                    rs.SetRecord(recordId, data, 0, data.Length);
                }
            }
            catch (RecordStoreException ex)
            {
                Loon.Utils.Debug.Log.Exception(ex);
            }
            finally
            {
                CloseRecordStore(rs);
            }
        }

        public static int AddBytes(string resName, byte[] data)
        {
            RecordStore rs = null;
            bool opened = false;
            try
            {
                rs = RecordStore.OpenRecordStore(resName, true);
                opened = true;
                return rs.AddRecord(data, 0, data.Length);
            }
            catch (RecordStoreException ex)
            {
                Loon.Utils.Debug.Log.Exception(ex);
            }
            finally
            {
                CloseRecordStore(rs);
            }
            return opened ? COULD_NOT_SAVE : COULD_NOT_OPEN;
        }

        public static bool ExistRecordStoreAll(string resName)
        {
            string[] recordStores = RecordStore.ListRecordStores();
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
            RecordStore rs = null;
            try
            {
                rs = RecordStore.OpenRecordStore(resName, false);
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
            RecordStore rs = null;
            try
            {
                rs = RecordStore.OpenRecordStore(resName, false);
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

        public static void CloseRecordStore(RecordStore rs)
        {
            if (rs != null)
            {
                rs.CloseRecordStore();
                rs = null;
            }
        }

    }
}
