namespace Loon.Core.Store
{
    public interface IRecordStoreManager
    {
      RecordStore OpenRecordStore(string recordStoreName,bool createIfNecessary);
    }
}
